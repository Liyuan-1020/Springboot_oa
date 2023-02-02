package com.web.oa.service;



import com.web.oa.pojo.Employee;
import com.web.oa.pojo.EmployeeCustom;
import com.web.oa.pojo.SysUserRole;

import java.util.List;

public interface EmployeeService {
    //通过用户名查找对象信息（登录）
    Employee findEmployeeByName(String username);
    //通过id查找对象信息
    Employee findEmployeeManager(Long managerId);
    //查看所有用户角色信息
    List<EmployeeCustom> findUserAndRoleList();
    //创建角色的方法
    void addEmployee(Employee employee);
    //分配用户角色的方法
    void updateEmployeeRole(String roleId, String userId);
    //查看根据上级查看用户的方法
    List<Employee> findEmployeeByLevel(int level);
    //添加用户角色表的方法
    void addSysUserRole(SysUserRole sysUserRole);
}
