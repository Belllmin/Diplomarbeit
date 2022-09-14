package com.htlleonding.ac.at.backend.repository;

import com.htlleonding.ac.at.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<User, String> {

}