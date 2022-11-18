package com.example.community.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Comment implements Serializable{
    private String id;
    private String user_id;
    private String posts_id;
    //文本
    private String text;
    //创建时间
    private LocalDateTime createTime;
}
