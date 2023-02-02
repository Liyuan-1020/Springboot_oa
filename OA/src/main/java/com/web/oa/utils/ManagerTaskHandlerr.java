package com.web.oa.utils;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Employee;
import com.web.oa.service.EmployeeService;

public class ManagerTaskHandlerr implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		System.out.println("这里是代办人的监听器++++++++++++++++++++++++++++++++++++");
		//spring容器
		WebApplicationContext context =ContextLoader.getCurrentWebApplicationContext();
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		
		/**从新查询当前用户，再获取当前用户对应的领导*/
		ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
		
		EmployeeService employeeService = (EmployeeService) context.getBean("employeeService");

		Employee manager = employeeService.findEmployeeManager(activeUser.getManagerId());
		//设置个人任务的办理人
		System.out.println(manager.getName()+"这里是代办人的监听器=====================代办人名");
		delegateTask.setAssignee(manager.getName());
		
	}

}
