package com.example.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.community.common.BaseContext;
import com.example.community.common.R;
import com.example.community.entity.Comment;
import com.example.community.entity.Plate;
import com.example.community.service.CommentService;
import com.example.community.service.PlateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

/**
 * 板块管理
 */
@Slf4j
@RestController//返回集合转为json格式
@RequestMapping("/plate")
public class PlateController {

    @Autowired
    private PlateService plateservice;

    /**
     * 板块分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    @Cacheable(value = "pageplate",key = "#p1.toString() + (#p0 != null ? # p0.toString() : '')")
    public R<Page> pageUserId(int page, int pageSize){
        return plateservice.page1(page,pageSize);
    }


    /**
     * 板块删除
     */
    @PostMapping("/delete")
    @CacheEvict(value = "pageplate")
    public R<String> Delete(@RequestBody Plate plate) {
        return plateservice.delete(plate.getId());
    }


    /**
     * 新增板块
     */
    @PostMapping("/add")
    @CacheEvict(value = "pageplate")
    public R<String> add(@RequestBody Plate plate){
        return plateservice.add(plate);
    }
}
