package com.example.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.community.common.R;
import com.example.community.entity.Plate;

public interface PlateService extends IService<Plate> {
    R<String> add(Plate plate);

    R<String> delete(String id);

    R<Page> page1(int page, int pageSize);
}
