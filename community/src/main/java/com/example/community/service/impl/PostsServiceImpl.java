package com.example.community.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.community.common.BaseContext;
import com.example.community.common.R;
import com.example.community.entity.Posts;
import com.example.community.mapper.PostsMapper;
import com.example.community.service.PostsService;
import com.example.community.utils.RequestDemo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PostsServiceImpl extends ServiceImpl<PostsMapper, Posts> implements PostsService {



    @Autowired
    private PostsService postsService;

    @Override
    public R<String> addPosts(Posts posts) {


        //接收传入的参数
        //通过工具类调用百度api判断是否合格，合格存入数据库中，不合格返回原因
        //从线程空间获取id
        String currentId = String.valueOf(BaseContext.getCurrentId());
        posts.setCreateTime(LocalDateTime.now());//设置时间
        posts.setUser_id(currentId);
        String s1 = "text=" + posts.getText();
        System.out.println(s1);

        RequestDemo requestDemo = new RequestDemo();
        String s = requestDemo.RequestDemo1(s1);
        int conclusionType = s.lastIndexOf("Type");
        String substring = s.substring(conclusionType + 6, conclusionType + 7);

            if (substring.equals("1")){
                //合格存入数据库中
                postsService.save(posts);
                return R.success("帖子插入成功");
            }

            if(substring.equals("2")){
                //转化请求的 json 数据
                JSONObject jsonObject = JSONObject.parseObject(s);
                //获取 data 数组
                JSONArray data = jsonObject.getJSONArray("data");
                String msg = null;
                for (int i = 0; i < data.size(); i++) {
                    //获取 msg 返回信息
                    //获取青岛
                    msg = data.getJSONObject(i).getString("msg");

                    System.out.println(msg);
                }
                return R.error(msg);
            }

            return R.error("失败");

    }

    @Override
    public R<Page> pageposts(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={}",page,pageSize,name);

        //1.构造分页构造器
        Page<Posts> pageinfo = new Page(page,pageSize);

        //2.构造条件构造器
        LambdaQueryWrapper<Posts> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Posts::getTitle,name);//name不等于空时执行查询

        //排序条件
        queryWrapper.orderByDesc(Posts::getCreateTime);//排序条件是创建时间降序

        //3.执行查询
        Page<Posts> page1 = postsService.page(pageinfo, queryWrapper);//传入分页构造器和条件构造器

        //执行查询
        return R.success(page1);
    }

    @Override
    public R<Page> pageId(int page, int pageSize, String id) {
        log.info("page={},pageSize={},name={}",page,pageSize,id);

        //1.构造分页构造器
        Page<Posts> pageinfo = new Page(page,pageSize);

        //2.构造条件构造器
        LambdaQueryWrapper<Posts> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(id),Posts::getUser_id,id);//name不等于空时执行查询

        //排序条件
        queryWrapper.orderByDesc(Posts::getCreateTime);//排序条件是创建时间降序

        //3.执行查询
        Page<Posts> page1 = postsService.page(pageinfo, queryWrapper);//传入分页构造器和条件构造器

        //执行查询
        return R.success(page1);
    }
}
