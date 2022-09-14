package com.htlleonding.ac.at.backend.service;

import com.htlleonding.ac.at.backend.entity.User;
import com.htlleonding.ac.at.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    //region Fields
    @Autowired
    private UserRepository userRepository;
    //endregion

    //region Main methods
    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);
        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        }
    }

    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public String deleteUser(String id) {
        userRepository.deleteById(id);
        return "User with ID: " + id + " removed!";
    }

    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        existingUser.setUserName(user.getUserName());
        existingUser.setProducts(user.getProducts());
        existingUser.setImage(user.getImage());
        return userRepository.save(existingUser);
    }
    //endregion
}