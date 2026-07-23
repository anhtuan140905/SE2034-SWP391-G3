package vn.edu.fpt.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import vn.edu.fpt.exception.ResourceNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Tự động chuyển hướng về trang 404 giao diện đồng bộ khi gõ sai URL / sai kiểu dữ liệu
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, NoResourceFoundException.class, ResourceNotFoundException.class})
    public String handleNotFoundAndMismatch() {
        return "homepage/404";
    }

    // Tự động chuyển hướng về trang 500 khi có lỗi hệ thống không mong muốn
    @ExceptionHandler(Exception.class)
    public String handleGeneralException() {
        return "homepage/500";
    }
}