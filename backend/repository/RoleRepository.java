package com.htlleonding.ac.at.backend.repository;

import com.htlleonding.ac.at.backend.entity.EnumRole;
import com.htlleonding.ac.at.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(EnumRole name);
}