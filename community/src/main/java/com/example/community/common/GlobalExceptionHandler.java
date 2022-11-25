package com.example.community.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.ConstraintViolationException;


import javax.validation.ConstraintViolation;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2]+"已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }


    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){

        return R.error(ex.getMessage());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public R<String> handler(RuntimeException ex){
        log.error("运行时异常-----------{}"+ex.getMessage());
        return R.error(ex.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R<String> handleValidException(MethodArgumentNotValidException e){
        return getCommonResult(e);
//        return null;

    }
    @ExceptionHandler(value = BindException.class)
    public R<String> handleValidException(BindException e){
        return getCommonResult(e);

    }
//    @ExceptionHandler(value = ConstraintViolationException.class)
//    public  R<String> handleValidException(ConstraintViolationException e) {
//        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
//        String message = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList()).get(SystemConstant.NUM_ZERO);
//        return R.error(message);
//    }

    private R<String> getCommonResult(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getDefaultMessage();
            }
        }
        return R.error(message);
    }

    private R<String> getCommonResult(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getDefaultMessage();
            }
        }
        return R.error(message);
    }


}
