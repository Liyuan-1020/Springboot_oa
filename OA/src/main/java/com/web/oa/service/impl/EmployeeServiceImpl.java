package com.web.oa.service.impl;

import com.web.oa.mapper.EmployeeMapper;
import com.web.oa.mapper.SysPermissionMapperCustom;
import com.web.oa.mapper.SysUserRoleMapper;
import com.web.oa.pojo.*;
import com.web.oa.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("employeeService")
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private SysPermissionMapperCustom sysPermissionMapperCustom;
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    //通过用户名查找对象信息
    @Override
    public Employee findEmployeeByName(String username) {
        EmployeeExample employeeExample=new EmployeeExample();
        EmployeeExample.Criteria criteria = employeeExample.createCriteria();
        criteria.andNameEqualTo(username);
        List<Employee> employees = employeeMapper.selectByExample(employeeExample);
        if(employees!=null&&employees.size()>0){
            Employee employee=employees.get(0);
            return employee;
        }
        return null;
    }


    //通过id查找对象信息
    @Override
    public Employee findEmployeeManager(Long managerId) {
        System.out.println("这里是findEmployeeManager");
        //通过主键查找
        return employeeMapper.selectByPrimaryKey(managerId);
    }


    //查看所有用户和角色集合信息
    @Override

    public List<EmployeeCustom> findUserAndRoleList() {
        return sysPermissionMapperCustom.findUserAndRoleList();
    }
    //添加角色的方法
    @Override
    public void addEmployee(Employee employee) {
        employeeMapper.insert(employee);
    }


    //给用户改变角色的方法
    @Override
    public void updateEmployeeRole(String roleId, String userId) {
        SysUserRoleExample example = new SysUserRoleExample();
        //查询到用户角色表
        example.createCriteria().andSysUserIdEqualTo(userId);
        //要通过用户角色表来进行查询
        List<SysUserRole> sysUserRoles = userRoleMapper.selectByExample(example);
        SysUserRole userRole=null;
        if(sysUserRoles!=null&&sysUserRoles.size()>0){
            userRole=sysUserRoles.get(0);
        }
        //设置他的角色为角色id
        userRole.setSysRoleId(roleId);
        //最后进行删除
        userRoleMapper.updateByPrimaryKey(userRole);
    }

    //根据上级来查看的方法
    @Override
    public List<Employee> findEmployeeByLevel(int level) {
        EmployeeExample employeeExample=new EmployeeExample();
        //根据上级id来查找用户
        employeeExample.createCriteria().andRoleEqualTo(level);
        List<Employee>list=employeeMapper.selectByExample(employeeExample);
        return list;
    }

    //添加用户角色表的方法
    @Override
    public void addSysUserRole(SysUserRole sysUserRole) {
        sysUserRoleMapper.insert(sysUserRole);
    }

}
