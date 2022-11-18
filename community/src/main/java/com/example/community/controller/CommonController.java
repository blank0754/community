package com.example.community.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidubce.model.ApiExplorerResponse;
import com.example.community.common.R;
import com.example.community.utils.ImageDemo;
import com.example.community.utils.OssTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


/**
 * 文件的上传和下载
 */
@Slf4j
@RestController//返回集合转为json格式
@RequestMapping("/common")
public class CommonController {

    @Value("${community.filepath}")
    private String filePath;

    @Autowired
    private OssTemplate ossTemplate;

    /**
     * 单个文件上传
     * @param file 上传的文件对象
     */
    @RequestMapping("/upload/file")
    public R<String> fileUpload(MultipartFile file) throws IOException {
        log.info(file.toString());
        String imgPath = ossTemplate.uploadfile(file.getOriginalFilename(), file.getInputStream());
        return R.success(imgPath);
    }
    /**
     * 多个文件上传
     * @param files
     * @return
     */
    @RequestMapping("/upload/files")
    public R<List> filesUpload(MultipartFile[] files) throws IOException {
        List<String> usrList = new ArrayList<>(files.length);
        for (MultipartFile file : files) {
            String uploadfile = ossTemplate.uploadfile(file.getOriginalFilename(), file.getInputStream());
            usrList.add(uploadfile);
        }
        return R.success(usrList);
    }




    /**
     * 图片上传
     * @param file
     * @return
     */
    @PostMapping("/upload/image")
    public R<String> upload(MultipartFile file) throws IOException {//这里的file必须跟前端的name=“file”保持一致
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());
        String imgPath = ossTemplate.upload(file.getOriginalFilename(), file.getInputStream());

        ImageDemo imageDemo = new ImageDemo();
        String s = imageDemo.ImageDemo1(imgPath);
        System.out.println(s);
        //转化请求的 json 数据
        JSONObject jsonObject = JSONObject.parseObject(s);
        //获取 conclustionType 数组
        String conclusionType = jsonObject.getString("conclusionType");
        if (conclusionType.equals("1")){
            return R.success(imgPath);
        }

        if (conclusionType.equals("2")){
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
        return R.success(imgPath);
    }

    /**
     * 单个删除
     * @param url 文件url
     */
    @PostMapping("/upload/delete")
    public R<String> delete(String url) {
        // 处理 url
        log.info("============入参：{}", url);
        int file = url.indexOf("file");
        String path = url.substring(file);
        log.info("============path：{}", path);
        /**
         * 填写文件名。文件名包含路径，不包含Bucket名称。
         * 例如2021/09/14/52c6a3114e634979a2934f1ea12deaadfile.png。
         */
        return ossTemplate.ossDelete(path);

    }


//    /**
//     * 图片下载
//     * @param name
//     * @param response
//     */
//    @GetMapping("/download")
//    public void download(String name, HttpServletResponse response){
//
//        try {
//            //输入流，通过输入流读取文件内容
//            FileInputStream fileInputStream = new FileInputStream(new File(imgPath+name));
//
//            //输出流，通过输出流文件写回浏览器，在浏览器展示图片了
//            ServletOutputStream outputStream = response.getOutputStream();
//
//            //设置response响应的文件类型为imge
//            response.setContentType("image/jpeg");
//
//
//            int len = 0;
//            byte[] bytes = new byte[1024];
//            while ((len = fileInputStream.read(bytes)) != -1) {
//                outputStream.write(bytes,0,len);
//                outputStream.flush();
//            }
//
//            //关闭资源
//            outputStream.close();
//            fileInputStream.close();
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }


}
