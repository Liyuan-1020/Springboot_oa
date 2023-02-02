package com.web.oa.service;



import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.SysRole;

import java.util.List;

public interface SysService {
    //获取菜单的方法
    List<MenuTree> loadMenuTree();
    //获取权限的方法
    List<SysPermission> findPermissionListByUserId(String username) throws Exception;
    //查找所有角色
    List<SysRole> findAllRoles();
    //获取角色菜单
    List<MenuTree> getAllMenuAndPermision();
    //查看所有菜单
    List<SysPermission> findAllMenus();
    //查看所有用户和权限
    List<SysRole> findRolesAndPermissions();
    //通过用户名字查找角色的方法
    SysRole findRolesAndPermissionsByUserId(String userName);
    //用户添加的方法
    void addRoleAndPermissions(SysRole role, int[] permissionIds);
    //添加权限的方法
    void addSysPermission(SysPermission permission);
    //编辑权限的方法
    void updateRoleAndPermissions(String roleId, int[] permissionIds);
    //编辑查看权限菜单的方法
    List<SysPermission> findPermissionsByRoleId(String roleId);
    //通过id删除角色的方法
    void deleteRoleById(String roleId);
}
