package com.example.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.community.common.R;
import com.example.community.entity.Comment;
import com.example.community.entity.File;


public interface CommentService extends IService<Comment> {


    R<Page> pageUserId(int page, int pageSize, String userid);

    R<Page> pagePostsId(int page, int pageSize, String postsId);

    R<String> delete(String id);

    R<String> add(Comment comment);
}
