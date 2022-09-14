package com.htlleonding.ac.at.backend.service;

import com.htlleonding.ac.at.backend.entity.User;
import com.htlleonding.ac.at.backend.repository.AdminRepository;
import com.htlleonding.ac.at.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    //region Fields
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ProductRepository productRepository;
    //endregion

    //region Main methods
    public byte[] findImagesByUserId(String userId){
        return productRepository.findImageByUserId(userId);
    }

    public String deleteImageByUserId(String userId){
        if(productRepository.deleteImageByUserId(userId)){
            return "Image with an user id: " + userId + " has been deleted.";
        }
        return "Could not delete image with an user id: " + userId;
    }

    public String enableUserById(String id){
        User user = adminRepository.findById(id).orElse(null);
        if(user != null){
            user.setEnabled(true);
            if(user.getVerificationCode() != null || user.getVerificationCode() != "")
                user.setVerificationCode(null);
            adminRepository.save(user);
            return "User with ID: " + id + " enabled!";
        }
        return "No user with ID: " + id + " found.";
    }

    public String deactivateUserById(String id){
        User user = adminRepository.findById(id).orElse(null);
        if(user != null){
            user.setActivated(false);
            adminRepository.save(user);
            return "User with ID: " + id + " deactivated!";
        }
        return "No user with ID: " + id + " found.";
    }

    public String activateUserById(String id){
        User user = adminRepository.findById(id).orElse(null);
        if(user != null){
            user.setActivated(true);
            adminRepository.save(user);
            return "User with ID: " + id + " activated!";
        }
        return "No user with ID: " + id + " found.";
    }

    public String unBlockUserById(String id){
        User user = adminRepository.findById(id).orElse(null);
        if(user != null){
            user.setBlocked(false);
            adminRepository.save(user);
            return "User with ID: " + id + " unblocked!";
        }
        return "No user with ID: " + id + " found.";
    }

    public String blockUserById(String id){
        User user = adminRepository.findById(id).orElse(null);
        if(user != null){
            user.setBlocked(true);
            adminRepository.save(user);
            return "User with ID: " + id + " blocked!";
        }
        return "No user with ID: " + id + " found.";
    }
    //endregion
}
