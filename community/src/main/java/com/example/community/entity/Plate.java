package com.example.community.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Plate implements Serializable {
    private String id;
    private String plateName;
    private int status;
    private String image;
    private LocalDateTime createTime;
}
