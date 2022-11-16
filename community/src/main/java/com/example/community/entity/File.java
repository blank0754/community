package com.example.community.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class File implements Serializable {
    private String id;
    private String postsId;
    private String text;
}
