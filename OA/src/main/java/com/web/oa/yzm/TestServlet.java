package com.web.oa.yzm;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/yzms")
public class TestServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(250, 100);
        // 取出 验证码中 字符
        // 存在session 域中
        //lineCaptcha.getCode();
        HttpSession session = request.getSession();

        session.setAttribute("randomCode",lineCaptcha.getCode());

        System.out.println("session存进去了================="+session.getAttribute("randomCode"));
        lineCaptcha.write(response.getOutputStream());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
