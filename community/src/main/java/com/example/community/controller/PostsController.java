package com.example.community.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.community.common.BaseContext;
import com.example.community.common.R;
import com.example.community.dto.RoleDto;
import com.example.community.entity.Plate;
import com.example.community.entity.Posts;
import com.example.community.entity.Role;
import com.example.community.entity.User;
import com.example.community.service.PlateService;
import com.example.community.service.PostsService;
import com.example.community.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子模块
 */
@RestController
@Slf4j
@RequestMapping("/posts")
public class PostsController {

    @Autowired
    private PostsService postsService;

    @Autowired
    private PlateService plateService;


    /**
     * 新增帖子
     * @param posts
     * @return
     */
    @PostMapping("/add")
    @CacheEvict(value = {"posts", "postid"},allEntries=true)
    public R<String> addPosts(@RequestBody Posts posts,@RequestHeader String token){

        Claims claims1 = JwtUtils.parseJWT(token);
        //获取id
        String jti = (String) claims1.get("jti");
        return postsService.addPosts(posts,jti);
    }

    /**
     * 帖子分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @Cacheable(value = "posts",key = "#p1.toString() + (#p0 != null ? # p0.toString() : '') + (#p2 != null ? # p2.toString() : '') + (#p3 != null ? # p3.toString() : '')")
    public R<Page> page(int page, int pageSize, String name,String plateName){
       return postsService.pageposts(page,pageSize,name,plateName);
    }


    /**
     * 根据id分页查询帖子
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/pageid")
    @Cacheable(value = "postsid",key = "#p1.toString() + (#p0 != null ? # p0.toString() : '')")
    public R<Page> pageId(int page, int pageSize){
        //从线程空间获取id
        String currentId = String.valueOf(BaseContext.getCurrentId());
        return postsService.pageId(page,pageSize,currentId);
    }


    /**
     * 根据id单个或批量删除帖子
     *
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @Transactional
    @CacheEvict(value = {"posts", "postid"},allEntries=true)
    public R<String> delete(Long[] ids) {
        return postsService.delete(ids);

    }

    /**
     * 根据id修改帖子
     *
     * @param posts
     * @return
     */
    @PostMapping("/update")
    @Transactional
    @CacheEvict(value = {"posts", "postid"},allEntries=true)
    public R<String> update(@RequestBody Posts posts) {
       postsService.updateById(posts);
        return R.success("修改成功");

    }


    /**
     * 根据id查询帖子
     * @param
     * @return
     */
    @PostMapping("/getid")
    public R<Posts> getById(@RequestBody Posts post){
        log.info("根据id查询帖子信息");
        Posts byId = postsService.getById(post.getId());
        String plateId = byId.getPlateId();
        Plate PlatebyId = plateService.getById(plateId);
        return R.success(byId).add("PlatebyId",PlatebyId);
    }


}
