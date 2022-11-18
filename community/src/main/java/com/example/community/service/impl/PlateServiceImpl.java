package com.example.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.community.common.R;
import com.example.community.entity.Comment;
import com.example.community.entity.Plate;
import com.example.community.entity.Posts;
import com.example.community.entity.User;
import com.example.community.mapper.PlateMapper;
import com.example.community.service.PlateService;
import com.example.community.service.PostsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PlateServiceImpl extends ServiceImpl<PlateMapper, Plate> implements PlateService {

    @Autowired
    private PlateService plateservice;

    @Autowired
    private PostsService postsService;

    @Override
    public R<String> add(Plate plate) {
        plateservice.save(plate);
        return R.success("新增板块成功");
    }

    @Override
    public R<String> delete(String id) {
        log.info("开始删除板块");
        LambdaQueryWrapper<Posts> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Posts::getPlateId,id);
        List<Posts> list = postsService.list(queryWrapper1);
        if (list.size()!=0){
            return R.error("还有帖子在该板块,不能删除");
        }
        plateservice.removeById(id);
        return R.success("删除板块成功");
    }

    @Override
    public R<Page> page1(int page, int pageSize) {
        log.info("page={},pageSize={},name={}",page,pageSize);

        //1.构造分页构造器
        Page<Plate> pageinfo = new Page(page,pageSize);

        //2.构造条件构造器
        LambdaQueryWrapper<Plate> queryWrapper = new LambdaQueryWrapper();
        //排序条件
        queryWrapper.orderByDesc(Plate::getCreateTime);//排序条件是创建时间降序

        //3.执行查询
        Page<Plate> page1 = plateservice.page(pageinfo, queryWrapper);//传入分页构造器和条件构造器

        //执行查询
        return R.success(page1);
    }
}
