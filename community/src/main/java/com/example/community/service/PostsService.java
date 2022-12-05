package com.example.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.community.common.R;
import com.example.community.entity.Posts;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;


public interface PostsService extends IService<Posts> {

    //新增帖子
    R<String> addPosts(Posts posts,String id);

    //分页查询帖子
    R<Page> pageposts(int page, int pageSize, String name,String plateName);

    //根据用户id分页查询帖子
    R<Page> pageId(int page, int pageSize, String id);

    R<String> delete(Long[] ids);

    R<String> update(Posts posts);

    //根据板块id查询帖子

    //

}
