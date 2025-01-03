package com.villysiu.yumtea.models.tea;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private long id;

    @Column(unique = true, nullable = false)
    String ounce;

    @Column(columnDefinition = "Double default 0.0")
    Double price;
}