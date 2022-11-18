package com.example.community.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Role implements Serializable {
    private String id;
    private String roleName;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
