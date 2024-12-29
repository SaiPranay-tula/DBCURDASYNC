package com.project.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ApiLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", LocalDateTime.now());
        logger.info("Request [{}] [{}] from IP: {}", request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        return true; }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           org.springframework.web.servlet.ModelAndView modelAndView) {

    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LocalDateTime startTime = (LocalDateTime) request.getAttribute("startTime");
        long duration = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());

        logger.info("Response [{}] [{}] Status: {} Time Taken: {} ms",
                request.getMethod(), request.getRequestURI(), response.getStatus(), duration);

        if (ex != null) {
            logger.error("Exception occurred: ", ex);
        }
    }
}
