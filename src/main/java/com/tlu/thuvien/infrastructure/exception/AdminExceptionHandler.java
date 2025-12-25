package com.tlu.thuvien.infrastructure.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(basePackages = "com.tlu.thuvien.api.controller.mvc")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AdminExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc,
                                         HttpServletRequest request,
                                         RedirectAttributes redirectAttributes) {
        return handleExceptionAndRedirect("File quá lớn! Vui lòng chọn ảnh nhỏ hơn 10MB.", request, redirectAttributes);
    }

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception exc,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) {
        exc.printStackTrace(); // In lỗi ra console server để debug

        String message = "Đã xảy ra lỗi hệ thống: " + exc.getMessage();
        return handleExceptionAndRedirect(message, request, redirectAttributes);
    }

    private String handleExceptionAndRedirect(String message,
                                              HttpServletRequest request,
                                              RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", message);

        String referer = request.getHeader("Referer");
        String redirectUrl = (referer != null && !referer.isEmpty()) ? referer : "/admin/dashboard";

        return "redirect:" + redirectUrl;
    }
}