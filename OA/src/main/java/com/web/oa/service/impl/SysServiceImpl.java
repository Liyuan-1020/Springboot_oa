package com.web.oa.service.impl;

import com.web.oa.mapper.SysPermissionMapper;
import com.web.oa.mapper.SysPermissionMapperCustom;
import com.web.oa.mapper.SysRoleMapper;
import com.web.oa.mapper.SysRolePermissionMapper;
import com.web.oa.pojo.*;
import com.web.oa.service.SysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SysServiceImpl implements SysService {
    @Autowired
    private SysPermissionMapperCustom sysPermissionMapperCustom;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysPermissionMapper sysPermissionMapper;
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;
    //获取菜单
    @Override
    public List<MenuTree> loadMenuTree() {
        return sysPermissionMapperCustom.getMenuTree();
    }
    //获取权限
    @Override
    public List<SysPermission> findPermissionListByUserId(String username) throws Exception {
        return sysPermissionMapperCustom.findPermissionListByUserId(username);
    }
    //查看所有角色
    @Override
    public List<SysRole> findAllRoles() {
        return sysRoleMapper.selectByExample(null);
    }
    //获取所有的菜单和权限
    @Override
    public List<MenuTree> getAllMenuAndPermision() {
        return  sysPermissionMapperCustom.getAllMenuAndPermision();
    }
    //查看所有菜单
    @Override
    public List<SysPermission> findAllMenus() {
        return sysPermissionMapper.selectByExample(null);
    }
    //查看所有角色
    @Override
    public List<SysRole> findRolesAndPermissions() {
        return sysRoleMapper.selectByExample(null);
    }
    //通过userid来查找权限
    @Override
    public SysRole findRolesAndPermissionsByUserId(String userName) {
        return sysPermissionMapperCustom.findRoleAndPermissionListByUserId(userName);
    }
    //添加角色和权限的方法
    @Override
    public void addRoleAndPermissions(SysRole role, int[] permissionIds) {
        //添加角色
        roleMapper.insert(role);
        //添加角色和权限关系表
        for (int i = 0; i < permissionIds.length; i++) {
            SysRolePermission rolePermission = new SysRolePermission();
            //16进制随机码
            String uuid = UUID.randomUUID().toString();
            rolePermission.setId(uuid);
            rolePermission.setSysRoleId(role.getId());
            rolePermission.setSysPermissionId(permissionIds[i] + "");
            sysRolePermissionMapper.insert(rolePermission);
        }
    }
    //添加权限的方法
    @Override
    public void addSysPermission(SysPermission permission) {
        sysPermissionMapper.insert(permission);
    }
    //编辑角色的方法
    @Override
    public void updateRoleAndPermissions(String roleId, int[] permissionIds) {
        //先删除角色权限关系表中角色的权限关系
        SysRolePermissionExample example = new SysRolePermissionExample();
        SysRolePermissionExample.Criteria criteria = example.createCriteria();
        criteria.andSysRoleIdEqualTo(roleId);
        sysRolePermissionMapper.deleteByExample(example);
        //重新创建角色权限关系
        for (Integer pid : permissionIds) {
            SysRolePermission rolePermission = new SysRolePermission();
            String uuid = UUID.randomUUID().toString();
            rolePermission.setId(uuid);
            rolePermission.setSysRoleId(roleId);
            rolePermission.setSysPermissionId(pid.toString());

            sysRolePermissionMapper.insert(rolePermission);
        }
    }
    //查看编辑角色菜单的方法
    @Override
    public List<SysPermission> findPermissionsByRoleId(String roleId) {
        return sysPermissionMapperCustom.findPermissionsByRoleId(roleId);
    }
    //通过id删除角色的方法
    @Override
    public void deleteRoleById(String roleId) {
        sysRoleMapper.deleteByPrimaryKey(roleId);
    }
}
