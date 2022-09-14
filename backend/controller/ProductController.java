package com.htlleonding.ac.at.backend.controller;

import com.htlleonding.ac.at.backend.dto.EditProductDto;
import com.htlleonding.ac.at.backend.entity.Product;
import com.htlleonding.ac.at.backend.security_service.JwtUser;
import com.htlleonding.ac.at.backend.service.ProductService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@Api(tags = "Products")
public class ProductController {

    //region Fields
    @Autowired
    private ProductService productService;
    //endregion

    //region Main methods
    @PostMapping("/addProduct")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public Product addProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Product> findAllProducts() {
        return productService.getProducts();
    }

    @GetMapping("/productsByName/{name}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Product> findAllProductsByName(@PathVariable String name) {
        return productService.getProductsByName(name);
    }

    @GetMapping("/productsByUserId")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Product> findAllProductsByUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        String userId = jwtUser.getId();
        return productService.getProductsByUserId(userId);
    }

    @GetMapping("/productById/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public Product findProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @DeleteMapping("/deleteProduct/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String deleteProduct(@PathVariable String productId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        String userId = jwtUser.getId();
        return productService.deleteProduct(productId, userId);
    }

    @PutMapping("/updateProduct")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public Product updateProduct(@RequestBody EditProductDto editProductDto, String productId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        String userId = jwtUser.getId();
        return productService.updateProduct(editProductDto, productId, userId);
    }
    //endregion
}