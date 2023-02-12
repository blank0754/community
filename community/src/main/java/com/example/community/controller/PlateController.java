package com.example.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.community.common.BaseContext;
import com.example.community.common.R;
import com.example.community.entity.*;
import com.example.community.service.CommentService;
import com.example.community.service.PlateService;
import com.example.community.service.PostsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 板块管理
 */
@Slf4j
@RestController//返回集合转为json格式
@RequestMapping("/plate")
public class PlateController {

    @Autowired
    private PlateService plateservice;

    @Autowired
    private PostsService postsService;

    /**
     * 板块分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    @Cacheable(value = "pageplate",key = "#p1.toString() + (#p0 != null ? # p0.toString() : '') + (#p2 != null ? # p2.toString() : '')")
    public R<Page> pageUserId(int page, int pageSize,String name){
        return plateservice.page1(page,pageSize,name);
    }
    /**
     * 根据id查询板块
     * @param
     * @return
     */
    @PostMapping("/getid")
    public R<Plate> getById(@RequestBody Plate plate){
        log.info("根据id查询板块信息");
        Plate byId = plateservice.getById(plate);
        return R.success(byId);
    }

    /**
     * 根据id单个或批量板块删除
     *
     * @param ids
     * @return
     */
    @CacheEvict(value = {"pageplate","posts"},allEntries=true)
    @PostMapping("/delete")
    @Transactional
    public R<String> Delete(String[] ids) {
        LambdaQueryWrapper<Posts> queryWrapper = new LambdaQueryWrapper();
        log.info("删除用户id为{}", ids);
        for (String id : ids) {
            Plate byId = plateservice.getById(id);
            if (byId.getStatus() == 0){
                return R.error("板块未禁用");
            }
            queryWrapper.like(Posts::getPlateId,id);
//            redisTemplate.delete(String.valueOf(id));
            postsService.remove(queryWrapper);
        }

        plateservice.removeByIds(Arrays.asList(ids));
        return R.success("成功");
    }



    /**
     * 新增板块
     */
    @PostMapping("/add")
    @CacheEvict(value = {"pageplate","posts"},allEntries=true)
    public R<String> add(@RequestBody Plate plate){
        return plateservice.add(plate);
    }


    /**
     * 修改板块
     */
    @PostMapping("/update")
    @Transactional
    @CacheEvict(value = {"pageplate","posts"},allEntries=true)
    public R<String> update(@RequestBody Plate plate){
        plateservice.updateById(plate);
        return R.success("修改成功");
    }

    /**
     * 单个或批量禁用启用
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    @Transactional
    @CacheEvict(value = {"pageplate","posts"},allEntries=true)
    public R<String> updateStatus(
            @PathVariable("status") int status,
            @RequestParam List<Long> ids
    ){
        //批量替换
        List<Plate> plates = new ArrayList<>();
        for (Long id : ids) {
            Plate plate = new Plate();
            plate.setStatus(status);
            plate.setId(String.valueOf(id));
            plates.add(plate);
        }

         plateservice.updateBatchById(plates, plates.size());
        return R.success("状态修改成功");
    }

    /**
     * 查询所有板块（不分页）
     */
    @GetMapping("/listAll")
    public R listAll(){
        HashMap<String,Object> roleHashMap = new HashMap<>();
        List<Plate> platelist = plateservice.list();
        roleHashMap.put("platelist",platelist);
        return R.success(roleHashMap);
    }

}