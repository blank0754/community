package com.example.community.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MenuRole implements Serializable {
    private String id;
    private String menuId;
    private String roleId;
}
