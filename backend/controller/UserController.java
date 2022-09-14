package com.htlleonding.ac.at.backend.controller;

import com.htlleonding.ac.at.backend.entity.User;
import com.htlleonding.ac.at.backend.security_service.JwtUser;
import com.htlleonding.ac.at.backend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Api(tags = "Users")
public class UserController {

    //region Fields
    @Autowired
    private UserService userService;
    //endregion

    //region Main methods
    @GetMapping("/userById")
    @ApiOperation(value = "Finds Users by id.", response = User.class)
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public User findUserById() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        String userId = jwtUser.getId();
        return userService.getUserById(userId);
    }

    @DeleteMapping("/deleteUser/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String deleteUserById(@PathVariable String id) {
        return userService.deleteUser(id);
    }

    @PutMapping("/updateUser")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }
    //endregion
}