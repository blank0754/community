package com.example.community.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoleUser implements Serializable {
    private String id;
    private String roleId;
    private String userId;
}
