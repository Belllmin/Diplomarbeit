package com.htlleonding.ac.at.backend.service;

import com.htlleonding.ac.at.backend.dto.JwtResponse;
import com.htlleonding.ac.at.backend.dto.LoginUserDto;
import com.htlleonding.ac.at.backend.dto.RegisterUserDto;
import com.htlleonding.ac.at.backend.entity.EnumRole;
import com.htlleonding.ac.at.backend.entity.Role;
import com.htlleonding.ac.at.backend.entity.User;
import com.htlleonding.ac.at.backend.repository.RoleRepository;
import com.htlleonding.ac.at.backend.repository.UserRepository;
import com.htlleonding.ac.at.backend.security_service.JwtUser;
import com.htlleonding.ac.at.backend.util.JwtUtils;
import net.bytebuddy.utility.RandomString;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService implements UserDetailsService {

    //region Fields
    @Autowired
    private JwtUtils jwtUtil;

    @Value("${backend.app.jwtSecret}")
    private String secretKey;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;
    //endregion

    //region Main methods
    public JwtResponse singInUser(LoginUserDto loginUserDto) throws Exception {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUserDto.getEmail(), loginUserDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken(authentication);
            JwtUser userDetails = (JwtUser) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            return new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles);
        }
        catch (DisabledException ex) {
            throw new Exception("USER_DISABLED", ex);
        }
        catch (BadCredentialsException ex) {
            throw new Exception("INVALID_CREDENTIALS", ex);
        }
    }

    public String singUpNewUser(RegisterUserDto registerUserDto, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
        if (userRepository.existsByUserName(registerUserDto.getUserName())) {
            throw new BadCredentialsException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(registerUserDto.getEmail())) {
            throw new BadCredentialsException("Error: Email is already in use!");
        }
        // Create new user's account
        User user = new User(registerUserDto.getUserName(),
                registerUserDto.getEmail(),
                encoder.encode(registerUserDto.getPassword()));
        Set<String> strRoles = registerUserDto.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(EnumRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(EnumRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(EnumRole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(EnumRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        String randomCode = RandomString.make(64);
        user.setVerificationCode(randomCode);
        user.setEnabled(false);
        user.setRoles(roles);
        userRepository.save(user);
        String siteURL = getSiteURL(request);
        sendVerificationEmail(user, siteURL);
        return "Confirmation email has been sent!";
    }

    public String getRefreshToken(HttpServletRequest request) {
        String oldToken = request.getHeader("Authorization").replace("Bearer ", "");
        String[] chunks = oldToken.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        if(verifJwtToken(chunks, header)){
            Pair<Map<String, Object>, String> claimsAndUserName = getClaimsAndUserNameFromJwt(payload);
            String newToken = jwtUtil.doGenerateRefreshJwtToken(claimsAndUserName.getFirst(),claimsAndUserName.getSecond());
            return newToken;
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       User user = userRepository.findByEmail(email).orElse(null); // userDetailsImp same machn
       return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
    //endregion

    //region Helper methods
    private boolean verifJwtToken(String[] chunks, String header) {
        boolean isVerified = false;
        JSONObject jsonHeaderObject = new JSONObject(header);
        String alg = jsonHeaderObject.getString("alg");
        if(!alg.equals("HS256")){
            return isVerified;
        }
        isVerified = true;
        return isVerified;
    }

    private Pair<Map<String, Object>, String> getClaimsAndUserNameFromJwt(String payload) {
        JSONObject jsonPayLoadObject = new JSONObject(payload);
        String userName = jsonPayLoadObject.getString("sub");
        boolean isUser = jsonPayLoadObject.getBoolean("isUser");
        boolean isModerator = jsonPayLoadObject.getBoolean("isModerator");
        boolean isAdmin = jsonPayLoadObject.getBoolean("isAdmin");
        Map<String, Object> claims = new HashMap<>();
        claims.put("isUser", isUser);
        claims.put("isModerator", isModerator);
        claims.put("isAdmin", isAdmin);
        return Pair.of(claims, userName);
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    private void sendVerificationEmail(User user, String siteURL) throws UnsupportedEncodingException, MessagingException {
        String toAddress = user.getEmail();
        String fromAddress = "bellmin.coralic@gmail.com";
        String senderName = "Traggie";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Traggie.";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", user.getUserName());
        String verifyURL = siteURL + "/api/auth/verify?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);
        helper.setText(content, true);
        mailSender.send(message);
    }
    //endregion
}