package com.htlleonding.ac.at.backend.dto;

import com.htlleonding.ac.at.backend.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EditProductDto {

    //region Fields
    private int amount;
    private String name;
    private String date;
    private String description;
    private String city;
    private String address;
    private double price;
    private int plz;
    private byte[] image;
    //private List<ProductImage> productImages = new ArrayList<>();
    //endregion
}
