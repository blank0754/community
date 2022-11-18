package com.example.community.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.example.community.common.R;
import com.example.community.config.OssConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//阿里存储工具类
@Component
public class OssTemplate {

    @Autowired
    private OssConfig ossConfig;

    //图片上传
    public String upload(String fileName, InputStream inputStream) {
        // 创建OSSClient
        OSS ossClient = new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret());

        // 上传文件流
        // <yourObjectName>表示上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如 images/2020/11/11/asdf.jpg。
        String objectName = "images/" + new SimpleDateFormat("yyyy/MM/dd").format(new Date())
                + "/" + System.currentTimeMillis() + fileName.substring(fileName.lastIndexOf("."));

        // meta设置请求头,解决访问图片地址直接下载
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType(getContentType(fileName.substring(fileName.lastIndexOf("."))));
        ossClient.putObject(ossConfig.getBucketName(), objectName, inputStream, meta);

        // 关闭OSSClient。
        ossClient.shutdown();

        return ossConfig.getUrl() + "/" + objectName;
    }

    //文件上传
    public String uploadfile(String fileName, InputStream inputStream) {
        // 创建OSSClient
        OSS ossClient = new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret());

        // 上传文件流
        // <yourObjectName>表示上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如 images/2020/11/11/asdf.jpg。
        String objectName = "file/" + new SimpleDateFormat("yyyy/MM/dd").format(new Date())
                + "/" + System.currentTimeMillis() + fileName.substring(fileName.lastIndexOf("."));
        // 上传文件
        ossClient.putObject(ossConfig.getBucketName(), objectName, inputStream);
        // 关闭OSSClient。
        ossClient.shutdown();

        return ossConfig.getUrl() + "/" + objectName;
    }




    //获取文件类型
    public String getContentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (FilenameExtension.equalsIgnoreCase(".jpeg") ||
                FilenameExtension.equalsIgnoreCase(".jpg") ||
                FilenameExtension.equalsIgnoreCase(".png")) {
            return "image/jpg";
        }
        return "image/jpg";
    }


    /**
     * 具体删除代码
     *
     * @return
     */
    public R<String> ossDelete(String path) {
        String bucketName = ossConfig.getBucketName();
            OSS ossClient = new OSSClientBuilder().build(
                    ossConfig.getEndpoint(),
                    ossConfig.getAccessKeyId(),
                    ossConfig.getAccessKeySecret());
            // ossClient.deleteObject(bucket, path);
        String objectName = path;
        ossClient.deleteObject(bucketName, objectName);
        ossClient.shutdown();
        return R.success("成功");
    }

}
