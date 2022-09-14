package com.htlleonding.ac.at.backend.repository;

import com.htlleonding.ac.at.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, String> {

    Product findByName(String name);

    @Query("DELETE FROM Product WHERE userId = ?1")
    Boolean deleteImageByUserId(String userId);

    @Query("SELECT p.image FROM Product p WHERE p.userId = ?1")
    byte[] findImageByUserId(String userId);
}