package com.htlleonding.ac.at.backend.security_service;

import com.htlleonding.ac.at.backend.entity.User;
import com.htlleonding.ac.at.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    //region Fields
    @Autowired
    UserRepository userRepository;
    //endregion

    //region Main methods
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Error: User Not Found with username: " + username));
        return JwtUser.build(user);
    }
    //endregion
}