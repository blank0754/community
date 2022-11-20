package com.example.community.common;

import org.springframework.security.core.AuthenticationException;


/**
 * 自定义异常事件
 */
public class UserCountLockException extends AuthenticationException {

    public UserCountLockException(String msg, Throwable t) {
        super(msg, t);
    }

    public UserCountLockException(String msg) {
        super(msg);
    }
}
