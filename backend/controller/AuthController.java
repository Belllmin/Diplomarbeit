package com.htlleonding.ac.at.backend.controller;

import com.htlleonding.ac.at.backend.dto.LoginUserDto;
import com.htlleonding.ac.at.backend.dto.RegisterUserDto;
import com.htlleonding.ac.at.backend.entity.User;
import com.htlleonding.ac.at.backend.repository.UserRepository;

import com.htlleonding.ac.at.backend.service.AuthService;
import com.htlleonding.ac.at.backend.service.UserService;
import com.htlleonding.ac.at.backend.util.JwtUtils;
import io.swagger.annotations.Api;

import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import org.mapstruct.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Api(tags = "Authentication")
public class AuthController {

    //region Fields
    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthService authService;

    @Autowired
    private UserService userService;

    @Value("${backend.app.jwtSecret}")
    private String secretKey;
    //endregion

    //region Main methods
    @PutMapping("/change-password/{id}")
    @PreAuthorize("!hasAuthority('USER') || (#oldPassword != null && !#oldPassword.isEmpty() && authentication.principal == @userRepository.findById(#id).orElse(new net.reliqs.gleeometer.users.User()).email)")
    public ResponseEntity<?> changePassword(@PathVariable String id, @RequestParam(required = false) String oldPassword, @Valid @Size(min = 3) @RequestParam String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id: " + id + " not found."));
        if (oldPassword == null || oldPassword.isEmpty() || encoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(encoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new ConstraintViolationException("Old password doesn't match!", new HashSet<>());
        }
        return null;
    }

    @PutMapping("/change-email/{id}")
    @PreAuthorize("!hasAuthority('USER') || (#oldEmail != null && !#oldEmail.isEmpty() && authentication.principal == @userRepository.findById(#id).orElse(new net.reliqs.gleeometer.users.User()).email)")
    public ResponseEntity<?> changeEmail(@PathVariable String id, @Email @RequestParam(required = false) String oldEmail, @Email @RequestParam String newEmail) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id: " + id + " not found."));
        if (oldEmail == null || oldEmail.isEmpty() || oldEmail.equals(user.getEmail())) {
            user.setEmail(newEmail);
            userRepository.save(user);
        } else {
            throw new ConstraintViolationException("Old email doesn't match!", new HashSet<>());
        }
        return null;
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<?> refreshtoken(@Context HttpServletRequest request) {
        String newToken = authService.getRefreshToken(request);
        if(newToken != null)
            return new ResponseEntity<>(newToken, HttpStatus.CREATED);
        return new ResponseEntity<>("Could not verify or generate jwt token.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/sign-out")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> logoutUser(@Context HttpServletRequest request, HttpServletResponse response){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return new ResponseEntity<>("Successfully logged out.", HttpStatus.OK);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> loginUser(@RequestBody LoginUserDto loginUserDto) throws Exception {
        try{
            return ResponseEntity.ok(authService.singInUser(loginUserDto));
        }
        catch (DisabledException ex) { throw new Exception("USER_DISABLED", ex); }
        catch (BadCredentialsException ex) { throw new Exception("INVALID_CREDENTIALS", ex); }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterUserDto registerUserDto, HttpServletRequest request) throws Exception {
        try {
            return ResponseEntity.ok(authService.singUpNewUser(registerUserDto, request));
        }
        catch (UnsupportedEncodingException ex) { throw new Exception("Encoding Exception", ex); }
        catch (MessagingException ex) { throw new Exception("Messaging exception", ex); }
        catch (BadCredentialsException ex) { throw new Exception("Error: Username or Email is already taken", ex); }
    }

    @GetMapping("/verify")
    public ModelAndView verifyUser(@Param("code") String code) {
        ModelAndView modelAndView = new ModelAndView();
        if (userService.verify(code)) {
            modelAndView.setViewName("verify_success");
            return  modelAndView;
        } else {
            modelAndView.setViewName("verify_fail");
            return  modelAndView;
        }
    }
    //endregion

    //region Test methods
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user_test")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR')")
    public String moderatorAccess() {
        return "Moderator Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
    //endregion
}