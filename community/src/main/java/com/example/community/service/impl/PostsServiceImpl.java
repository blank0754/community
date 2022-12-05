package com.example.community.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.community.common.BaseContext;
import com.example.community.common.R;
import com.example.community.dto.PostDto;
import com.example.community.dto.RoleDto;
import com.example.community.entity.*;
import com.example.community.mapper.PostsMapper;
import com.example.community.service.FileService;
import com.example.community.service.PlateService;
import com.example.community.service.PostsService;
import com.example.community.service.UserService;
import com.example.community.utils.RequestDemo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostsServiceImpl extends ServiceImpl<PostsMapper, Posts> implements PostsService {



    @Autowired
    private PostsService postsService;

    @Autowired
    private FileService fileService;

    @Autowired
    private PlateService plateService;

    @Autowired
    private UserService userService;

    @Override
    public R<String> addPosts(Posts posts,String id) {


        //接收传入的参数
        //通过工具类调用百度api判断是否合格，合格存入数据库中，不合格返回原因
        posts.setCreateTime(new Date());//设置时间
        posts.setUserId(id);
        posts.setReview(0);
        String s1 = "text=" + posts.getText();
        System.out.println(s1);

        RequestDemo requestDemo = new RequestDemo();
        String s = requestDemo.RequestDemo1(s1);
        int conclusionType = s.lastIndexOf("Type");
        String substring = s.substring(conclusionType + 6, conclusionType + 7);
        System.out.println(substring);

        String substring1 =null;
            if (substring.equals("1")){
                String t = "text=" + posts.getTitle();
                String s2 = requestDemo.RequestDemo1(t);
                int conclusionType1 = s2.lastIndexOf("Type");
                substring1 = s2.substring(conclusionType1 + 6, conclusionType1 + 7);
                System.out.println(substring1);
                if (substring1.equals("1")) {
                    //合格存入数据库中
                    postsService.save(posts);
                    return R.success("帖子插入成功");
                }
                if(substring1.equals("2")){
                    //转化请求的 json 数据
                    JSONObject jsonObject = JSONObject.parseObject(s2);
                    //获取 data 数组
                    JSONArray data = jsonObject.getJSONArray("data");
                    String msg = null;
                    for (int i = 0; i < data.size(); i++) {
                        //获取 msg 返回信息
                        msg = data.getJSONObject(i).getString("msg");

                        System.out.println(msg);
                    }
                    return R.error(msg);
                }
            }

            if(substring.equals("2")){
                //转化请求的 json 数据
                JSONObject jsonObject = JSONObject.parseObject(s);
                //获取 data 数组
                JSONArray data = jsonObject.getJSONArray("data");
                String msg = null;
                for (int i = 0; i < data.size(); i++) {
                    //获取 msg 返回信息
                    msg = data.getJSONObject(i).getString("msg");

                    System.out.println(msg);
                }
                return R.error(msg);
            }


            return R.error("失败");

    }

    @Override
    public R<Page> pageposts(int page, int pageSize, String name,String plateName) {
        log.info("page={},pageSize={},name={}",page,pageSize,name,plateName);

        //1.构造分页构造器
        Page<Posts> pageinfo = new Page(page,pageSize);
        Page<PostDto> postDtoPage = new Page<>();

        //添加一个过滤条件
        if (StringUtils.isNotEmpty(plateName)) {
            LambdaQueryWrapper<Plate> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(Plate::getPlateName,plateName);
            Plate one = plateService.getOne(lambdaQueryWrapper);

//            Plate byId = plateService.getById(plateName);
            LambdaQueryWrapper<Posts> queryWrapper3 = new LambdaQueryWrapper();
            //添加一个过滤条件
            queryWrapper3
                    .eq(Posts::getPlateId,one.getId())
                    .like(StringUtils.isNotEmpty(name),Posts::getTitle,name);//name不等于
            postsService.page(pageinfo, queryWrapper3);

        }else {
            //2.构造条件构造器
            LambdaQueryWrapper<Posts> queryWrapper = new LambdaQueryWrapper();
            //添加一个过滤条件
            queryWrapper.like(StringUtils.isNotEmpty(name), Posts::getTitle, name);//name不等于空时执行查询

            //排序条件
            queryWrapper.orderByDesc(Posts::getCreateTime);//排序条件是创建时间降序

            //3.执行查询
            postsService.page(pageinfo, queryWrapper);//传入分页构造器和条件构造器
        }

        //对象拷贝
        BeanUtils.copyProperties(pageinfo, postDtoPage, "records");
        List<Posts> records = pageinfo.getRecords();

        List<PostDto> list = records.stream().map((item) -> {//遍历records对象
            PostDto dishDto = new PostDto();
            //根据页面提交的用户名username查询数据库
            LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(User::getId,item.getUserId());
            List<User> list1 = userService.list(queryWrapper1);
            dishDto.setUser(list1);


            LambdaQueryWrapper<Plate> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(Plate::getId,item.getPlateId());
            List<Plate> list2 = plateService.list(queryWrapper2);
            dishDto.setPlate(list2);

            BeanUtils.copyProperties(item, dishDto);
            return dishDto;

        }).collect(Collectors.toList());

        Page<PostDto> postDtoPage1 = postDtoPage.setRecords(list);

        //执行查询
        return R.success(postDtoPage1);



    }

    @Override
    public R<Page> pageId(int page, int pageSize, String id) {
        log.info("page={},pageSize={},name={}",page,pageSize,id);

        //1.构造分页构造器
        Page<Posts> pageinfo = new Page(page,pageSize);

        //2.构造条件构造器
        LambdaQueryWrapper<Posts> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(id),Posts::getUserId,id);//name不等于空时执行查询

        //排序条件
        queryWrapper.orderByDesc(Posts::getCreateTime);//排序条件是创建时间降序

        //3.执行查询
        Page<Posts> page1 = postsService.page(pageinfo, queryWrapper);//传入分页构造器和条件构造器

        //执行查询
        return R.success(page1);
    }

    @Override
    public R<String> delete(Long[] ids) {
        log.info("删除分类id为{}", ids);
        for (Long id : ids) {
        //清理当前帖子对应的文件数据
        LambdaQueryWrapper<File> LambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper.eq(File::getPostsId,id);

            int count = fileService.count(LambdaQueryWrapper);
            if (count>0) {
                fileService.remove(LambdaQueryWrapper);
            }
        }
        postsService.removeByIds(Arrays.asList(ids));
        return R.success("成功");
    }

    @Override
    public R<String> update(Posts posts) {
        postsService.update(posts);
        return R.success("修改成功");

    }

}
