//package com.hms.meenakshi.config;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//@Component
//public class SessionInterceptor implements HandlerInterceptor {
//        @Override
//        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//            HttpSession session = request.getSession(false);
//            if (session == null || session.getAttribute("user") == null) {
//                // Redirect to login if session is null or user attribute is missing
//                response.sendRedirect("/sign-in?timeout=true");
//                return false;
//            }
//            return true;
//        }
//    }
