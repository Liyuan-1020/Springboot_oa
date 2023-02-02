package com.web.oa.shiro;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 自定义的过滤器，专门给验证码进行处理的
 * @author gec
 *
 */
public class CustomFormAuthenticationFilterr extends FormAuthenticationFilter {

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		System.out.println("进入验证码监听器++++++++++++++++++++++++++++++++++++");
		HttpServletRequest req = (HttpServletRequest) request;
		//获取session,从session中取出验证码
		HttpSession session = req.getSession();
		//获取表单中的验证码
		String validateCode = req.getParameter("yzm");
		String randNum = (String) session.getAttribute("randomCode");

		if (validateCode != null && randNum != null && !validateCode.equalsIgnoreCase(randNum)) {
			//前一个参数不能改变
			request.setAttribute("shiroLoginFailure", "randomcodeError");
			return true;
		}
		return super.onAccessDenied(request, response);
	}

	
	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
                                     ServletResponse response) throws Exception {
		WebUtils.getAndClearSavedRequest(request);
		WebUtils.redirectToSavedRequest(request, response, "/main");
		return false;
	}
}
