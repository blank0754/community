package com.example.community.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.community.entity.Comment;
import com.example.community.mapper.CommentMapper;
import com.example.community.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommentImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {


}