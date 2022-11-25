package com.example.community.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Role implements Serializable {
    private String id;
    private String roleName;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    private String code;

    //更新时间
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    private String remark;
}
