package com.example.community.dto;

import com.example.community.entity.Role;
import com.example.community.entity.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RoleDto extends Role {
    private List<User> user = new ArrayList<>();

}
