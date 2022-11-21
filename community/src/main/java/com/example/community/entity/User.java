package com.example.community.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息
 */
@Data
public class User implements Serializable {//反序列化
    private String id;
    private String username;
    private String password;
    private String image;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;//注册时间
    private LocalDateTime loginTime;//登录时间
    private String mobile;
    private String email;
    private int count;//登录次数
    private int status;
    private int roleId;
}
