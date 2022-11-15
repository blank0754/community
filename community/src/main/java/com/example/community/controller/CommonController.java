package com.example.community.controller;


import com.example.community.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


/**
 * 文件的上传和下载
 */
@Slf4j
@RestController//返回集合转为json格式
@RequestMapping("/common")
public class CommonController {

    @Value("${community.imgpath}")
    private String imgPath;

    @Value("${community.filepath}")
    private String filePath;


    /**
     * 实现文件上传
     * @param file 上传的文件对象
     */
    @RequestMapping("/fileupload")
    @ResponseBody
    public R<String> fileUpload(MultipartFile file){
        try {
            //path:目标目录
            File path=new File(filePath);
            if(!path.exists()){
                path.mkdirs();
            }
            String originalFilename=file.getOriginalFilename();
            String substring = originalFilename.substring(originalFilename.lastIndexOf("."));

            //使用UUID重新生成文件名，防止图片文件覆盖
            String fileName = UUID.randomUUID().toString() + substring;

            //descFile:目标目录=文件夹名+文件名
            File descFile=new File(path.getAbsolutePath()+"/"+fileName);
            //将上传的文件保存到指定的目标目录下
            file.transferTo(descFile);
            return R.success(originalFilename);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error("文件上传失败");
        }
    }


    /**
     * 图片上传
     * @param file
     * @return
     */
    @PostMapping("/imgupload")
    public R<String> upload(MultipartFile file){//这里的file必须跟前端的name=“file”保持一致
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());

        //获得上传时的原始文件名
        String originalFilename = file.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));


        //使用UUID重新生成文件名，防止图片文件覆盖
        String fileName = UUID.randomUUID().toString() + substring;

        //创建一个目录对象
        File dir = new File(imgPath);
        //判断目录是否存在
        if(!dir.exists()){
            //目录不存在，需要创建
            dir.mkdir();
        }

        try{
            file.transferTo(new File(imgPath+originalFilename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(originalFilename);
    }

    /**
     * 图片下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(imgPath+name));

            //输出流，通过输出流文件写回浏览器，在浏览器展示图片了
            ServletOutputStream outputStream = response.getOutputStream();

            //设置response响应的文件类型为imge
            response.setContentType("image/jpeg");


            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
