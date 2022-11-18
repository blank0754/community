package com.example.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.community.common.BaseContext;
import com.example.community.common.R;
import com.example.community.entity.Comment;
import com.example.community.entity.Posts;
import com.example.community.entity.Role;
import com.example.community.entity.User;
import com.example.community.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论管理
 */
@Slf4j
@RestController//返回集合转为json格式
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 根据用户id评论分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/pageuserid")
    @Cacheable(value = "pageCommentUserId",key = "#p1.toString() + (#p0 != null ? # p0.toString() : '')")
    public R<Page> pageUserId(int page, int pageSize){
        String currentId = String.valueOf(BaseContext.getCurrentId());
        return commentService.pageUserId(page,pageSize,currentId);
    }

    /**
     * 根据帖子id评论分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/pagepostsid")
    @Cacheable(value = "pageCommentPostsId",key = "#p1.toString() + (#p0 != null ? # p0.toString() : '')")
    public R<Page> pagePostsId(int page, int pageSize,String postsId){
        return commentService.pagePostsId(page,pageSize,postsId);
    }


    /**
     * 评论删除
     */
    @PostMapping("/delete")
    @CacheEvict(value = {"pageCommentPostsId","pageCommentUserId"})
    public R<String> Delete(@RequestBody Comment comment) {
        return commentService.delete(comment.getId());
    }


    /**
     * 新增帖子
     */
    @PostMapping("/add")
    @CacheEvict(value = {"pageCommentPostsId","pageCommentUserId"})
    public R<String> add(@RequestBody Comment comment){
        return commentService.add(comment);
    }






}
