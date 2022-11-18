package com.example.community.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.community.common.R;
import com.example.community.entity.Comment;
import com.example.community.entity.Posts;
import com.example.community.entity.User;
import com.example.community.mapper.CommentMapper;
import com.example.community.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CommentImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private CommentService commentService;

    @Override
    public R<Page> pageUserId(int page, int pageSize, String userid) {
        log.info("page={},pageSize={},name={}",page,pageSize,userid);

        //1.构造分页构造器
        Page<Comment> pageinfo = new Page(page,pageSize);

        //2.构造条件构造器
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(userid),Comment::getUser_id,userid);//name不等于空时执行查询

        //排序条件
        queryWrapper.orderByDesc(Comment::getCreateTime);//排序条件是创建时间降序

        //3.执行查询
        Page<Comment> page1 = commentService.page(pageinfo, queryWrapper);//传入分页构造器和条件构造器

        //执行查询
        return R.success(page1);
    }

    @Override
    public R<Page> pagePostsId(int page, int pageSize, String postsId) {
        log.info("page={},pageSize={},name={}",page,pageSize,postsId);

        //1.构造分页构造器
        Page<Comment> pageinfo = new Page(page,pageSize);

        //2.构造条件构造器
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(postsId),Comment::getPosts_id,postsId);//name不等于空时执行查询

        //排序条件
        queryWrapper.orderByDesc(Comment::getCreateTime);//排序条件是创建时间降序

        //3.执行查询
        Page<Comment> page1 = commentService.page(pageinfo, queryWrapper);//传入分页构造器和条件构造器

        //执行查询
        return R.success(page1);
    }

    @Override
    public R<String> delete(String id) {
        log.info("开始删除评论");

        commentService.removeById(id);
        return R.success("删除评论成功");
    }

    @Override
    public R<String> add(Comment comment) {
        commentService.save(comment);
        return R.success("新增评论成功");
    }
}