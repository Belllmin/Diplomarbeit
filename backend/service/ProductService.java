package com.htlleonding.ac.at.backend.service;

import com.htlleonding.ac.at.backend.dto.EditProductDto;
import com.htlleonding.ac.at.backend.entity.Product;
import com.htlleonding.ac.at.backend.repository.ProductRepository;
import com.htlleonding.ac.at.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    //region Fields
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    //endregion

    //region Main methods
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getProducts() { return productRepository.findAll(); }

    public List<Product> getProductsByName(String name) {
        return productRepository.findAll()
                .stream()
                .filter(p -> p.getName().toUpperCase().contains(name.toUpperCase()))
                .collect(Collectors.toList());
    }

    public List<Product> getProductsByUserId(String id) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getId().equals(id))
                .flatMap(user -> user.getProducts().stream())
                .sorted(Comparator.comparing(Product::getName))
                .collect(Collectors.toList());
    }

    public Product getProductById(String id) { return productRepository.findById(id).orElse(null); }

    public String deleteProduct(String productId, String userId) {
        Product existingProduct = productRepository.findById(productId).orElse(null);
        if(existingProduct.getUserId().equals(userId)){
            productRepository.deleteById(productId);
            return "Product with ID: " + productId + "removed!";
        }
        return null;
    }

    public Product updateProduct(EditProductDto editProductDto, String productId, String userId){
        Product existingProduct = productRepository.findById(productId).orElse(null);
        if(existingProduct.getUserId().equals(userId)){
            existingProduct.setName(editProductDto.getName());
            existingProduct.setPrice(editProductDto.getPrice());
            existingProduct.setDescription(editProductDto.getDescription());
            existingProduct.setCity(editProductDto.getCity());
            existingProduct.setAddress(editProductDto.getAddress());
            existingProduct.setDate(editProductDto.getDate());
            existingProduct.setAmount(editProductDto.getAmount());
            //existingProduct.setProductImages(editProductDto.getProductImages());
            existingProduct.setImage(editProductDto.getImage());
            existingProduct.setPlz(editProductDto.getPlz());
            return productRepository.save(existingProduct);
        }
        return null;
    }
    //endregion
}