package com.htlleonding.ac.at.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {

    //region Field(s)
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;
    private int amount;
    private String name;
    private String date;
    private String description;
    private String city;
    private String address;
    private String federalState;

    //@GeneratedValue
    @ManyToOne
    private User userId; // Fremd key
    private double price;
    private int plz;
    private byte[] image;

    //@OneToMany(targetEntity = ProductImage.class, cascade = CascadeType.ALL)
    //@JoinColumn(name = "product_fk", referencedColumnName = "id")
    //private List<ProductImage> productImages = new ArrayList<>();
    //endregion
}