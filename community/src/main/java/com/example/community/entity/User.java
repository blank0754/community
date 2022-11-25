package com.example.community.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

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
    private Date createTime;//注册时间
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date loginTime;//登录时间

    @NotBlank(message = "手机号码不能为空")
    @NotNull(message = "手机号不能为空")
    @Length(min = 11, max = 11, message = "手机号只能为11位")
    @Pattern(regexp = "^[1][3,4,5,6,7,8,9][0-9]{9}$", message = "手机号格式有误")
    private String mobile;
    @NotBlank(message = "邮箱不能为空")
    @NotNull(message = "邮箱不能为空")
    @Email(message = "邮箱不正确")
    private String email;
    private int count;//登录次数
    private int status;
    private int roleId;
}
