package com.example.community.entity;

import lombok.Data;

@Data
public class Password {
    private String id;
    private String oldpassword;
    private String newpassword;
}
