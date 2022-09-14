package com.htlleonding.ac.at.backend.controller;

import com.htlleonding.ac.at.backend.entity.User;
import com.htlleonding.ac.at.backend.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin-page")
@Api(tags = "Admin-Page")
public class AdminController {

    //region Fields
    @Autowired
    private AdminService service;
    //endregion

    //region Main methods
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/enable-user-by-id/{id}")
    @ApiOperation(value = "Enables User by id.",
            notes = "Provide an id to enable specific user from the list of disabled users.",
            response = User.class)
    public String enableUserById(@ApiParam(value = "ID value for the user your want to enable.", required = true) @PathVariable String id) {
        return service.enableUserById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/deactivate-user-by-id/{id}")
    @ApiOperation(value = "Deactivates User by id.",
            notes = "Provide an id to deactivate specific user from the list of activated users.",
            response = User.class)
    public String deactivateUserById(@ApiParam(value = "ID value for the user your want to deactivate.", required = true) @PathVariable String id) {
        return service.deactivateUserById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/activate-user-by-id/{id}")
    @ApiOperation(value = "Activates User by id.",
            notes = "Provide an id to activate specific user from the list of deactivated users.",
            response = User.class)
    public String activateUserById(@ApiParam(value = "ID value for the user your want to activate.", required = true) @PathVariable String id) {
        return service.activateUserById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/unblock-user-by-id/{id}")
    @ApiOperation(value = "Unblocks User by id.",
            notes = "Provide an id to unblock specific user from the list of blocked users.",
            response = User.class)
    public String unBlockUserById(@ApiParam(value = "ID value for the user your want to unblock.", required = true) @PathVariable String id) {
        return service.unBlockUserById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/block-user-by-id/{id}")
    @ApiOperation(value = "Blocks User by id.",
            notes = "Provide an id to block specific user from the list of unblocked users.",
            response = User.class)
    public String blockUserById(@ApiParam(value = "ID value for the user your want to block.", required = true) @PathVariable String id) {
        return service.blockUserById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/images-by-user-id/{id}")
    @ApiOperation(value = "Gets all user images by id.",
            notes = "Provide an user id to get the list of images.",
            response = User.class)
    public byte[] getImagesByUserId(@ApiParam(value = "ID value of the user with who you want to get his images.", required = true) @PathVariable String userId){
        return service.findImagesByUserId(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete-image-by-user-id/{id}")
    @ApiOperation(value = "Delets user image by id.",
            notes = "Provide an user id to delete the wanted image.",
            response = User.class)
    public String deleteImageByUserId(@ApiParam(value = "ID value of the user with who you want to delete his images.", required = true) @PathVariable String userId){
        return service.deleteImageByUserId(userId);
    }
    //endregion
}