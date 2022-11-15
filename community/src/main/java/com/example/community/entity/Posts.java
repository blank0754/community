package com.example.community.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Posts implements Serializable {
    private String id;

    private String text;

    private String title;//标题

    private String user_id;

    private String role_id;

    private LocalDateTime createTime;
}
