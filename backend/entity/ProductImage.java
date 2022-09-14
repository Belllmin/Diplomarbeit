package com.htlleonding.ac.at.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "productImage")
public class ProductImage {

    //region Fields
    @Id
    @GeneratedValue
    private long id;
    private byte[] image;

    @ManyToOne
    private Product product;
    //endregion
}