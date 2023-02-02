package com.web.oa.exceptions;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class QuanJuException implements HandlerExceptionResolver {
/*全局异常*/
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        System.out.println("自定义异常被执行了");
        String error="出现异常了,请联系管理员来修改";
        ModelAndView mav=new ModelAndView();
        mav.addObject("error", error);
        mav.setViewName("exception");

        return mav;
    }
}
