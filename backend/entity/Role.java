package com.htlleonding.ac.at.backend.entity;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import lombok.ToString;
import javax.persistence.*;

@Data
@Entity
@ToString
@Table(name = "role")
@ApiModel(description = "Details about role.")
public class Role {

    //region Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EnumRole name;
    //endregion
}