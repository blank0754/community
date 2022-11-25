package com.example.community.dto;

import com.example.community.entity.Role;
import com.example.community.entity.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class UserDto extends User {
    private Set<Role> role = new HashSet<>();
}
