package com.example.community.controller;

import com.example.community.common.R;
import com.example.community.entity.File;
import com.example.community.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/file")
public class FileController {



    @Autowired
    private FileService fileService;

    /**
     * 文件添加
     * @param file
     * @return
     */
    @PostMapping("/add")
    public R<String> add(@RequestBody File file) {
        boolean save = fileService.save(file);
        if (save){
            return R.success("成功");
        }else {
            return R.error("失败");
        }

    }
}
