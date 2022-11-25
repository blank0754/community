package com.example.community.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 自定义元数据对象处理器
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入操作，自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充insert");
        log.info(metaObject.toString());
        metaObject.setValue("createTime",new Date());
//        metaObject.setValue("loginTime",LocalDateTime.now());
    }

    /**
     * 更新操作自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充update");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime",new Date());
//        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
