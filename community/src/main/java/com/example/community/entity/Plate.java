package com.example.community.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Plate implements Serializable {
    private String id;
    private String plateName;
    private int status;
    private String image;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
