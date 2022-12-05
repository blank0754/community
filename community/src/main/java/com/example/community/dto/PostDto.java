package com.example.community.dto;


import com.example.community.entity.Plate;
import com.example.community.entity.Posts;
import com.example.community.entity.User;
import lombok.Data;


import java.util.ArrayList;
import java.util.List;

@Data
public class PostDto extends Posts {
    private List<User> user = new ArrayList<>();
    private List<Plate> plate = new ArrayList<>();
}
