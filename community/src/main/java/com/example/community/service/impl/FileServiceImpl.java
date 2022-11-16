package com.example.community.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.community.entity.File;
import com.example.community.mapper.FileMapper;
import com.example.community.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {


}
