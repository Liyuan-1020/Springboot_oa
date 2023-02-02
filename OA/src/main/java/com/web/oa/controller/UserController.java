package com.web.oa.controller;

import com.web.oa.pojo.*;
import com.web.oa.service.EmployeeService;
import com.web.oa.service.SysService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class UserController {

	@Autowired
	SysService sysService;
	@Autowired
	EmployeeService employeeService;
//去主页面
	@RequestMapping("/main")
	public String main(ModelMap model) {
		ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
		model.addAttribute("activeUser", activeUser);
		return "index";

	}
	//登录方法，认证
	@RequestMapping("/login")
	public String login(HttpServletRequest request, Model model){
		String exceptionName = (String) request.getAttribute("shiroLoginFailure");
		if (exceptionName != null) {
			if (UnknownAccountException.class.getName().equals(exceptionName)) {
				model.addAttribute("errorMsg", "用户账号不存在");
			} else if (IncorrectCredentialsException.class.getName().equals(exceptionName)) {
				model.addAttribute("errorMsg", "密码不正确");
			} else if("randomcodeError".equals(exceptionName)) {
				model.addAttribute("errorMsg", "验证码不正确");
			}else {
				model.addAttribute("errorMsg", "未知错误");
			}
		}
		return "login";
	}
	//查看角色的方法
	@RequestMapping("/findRoles")
	public ModelAndView findRoles(){

		ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
		List<SysRole> roles = sysService.findAllRoles();
		List<MenuTree> allMenuAndPermissions = sysService.getAllMenuAndPermision();

		ModelAndView mv = new ModelAndView();
		mv.addObject("allRoles", roles);
		mv.addObject("activeUser",activeUser);
		mv.addObject("allMenuAndPermissions", allMenuAndPermissions);

		mv.setViewName("permissionlist");
		return mv;
	}
	//查看用户的方法
	@RequestMapping("/findUserList")
	public ModelAndView findUserList(){
		ModelAndView mv = new ModelAndView();
		List<SysRole> allRoles = sysService.findAllRoles();
		List<EmployeeCustom> list = employeeService.findUserAndRoleList();

		mv.addObject("userList", list);

		mv.addObject("allRoles", allRoles);
		mv.setViewName("userlist");
		return mv;
	}
	//查看用户权限的方法,通过ajax的,所以要把他转换为json方式
	@RequestMapping("/viewPermissionByUser")
    @ResponseBody
	public SysRole viewPermissionByUser(String userName){
        //获取角色对象
        SysRole sysRole = sysService.findRolesAndPermissionsByUserId(userName);
        return sysRole;
	}


	//添加用户的方法
	@RequestMapping("/saveUser")
	public String saveUser(Employee employee){
		String salt="eteokues";
		Md5Hash md5Hash=new Md5Hash(employee.getPassword(),salt,2);
		employee.setPassword(md5Hash.toString());
		employee.setSalt(salt);
		System.out.println(employee);
		employeeService.addEmployee(employee);

		SysUserRole sysUserRole=new SysUserRole();
		sysUserRole.setSysUserId(employee.getName());
		sysUserRole.setSysRoleId(employee.getRole().toString());
		employeeService.addSysUserRole(sysUserRole);
		return "redirect:/findUserList";
	}
	

	//查看用户上级的方法
	@RequestMapping("/findNextManager")
	@ResponseBody
	public List<Employee> findNextManager(Integer level) {

		System.out.println("findNextManager level="+level);
		level++; //加一，表示下一个级别
		List<Employee> list = employeeService.findEmployeeByLevel(level);
		System.out.println("======================="+list.toString());
		return list;
	}

	//分配用户角色的方法
	@RequestMapping("/assignRole")
	@ResponseBody
	public Map<String, String> assignRole(String roleId,String userId) {
		Map<String, String> map = new HashMap<>();
		try {
			employeeService.updateEmployeeRole(roleId, userId);
			map.put("msg", "分配权限成功");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("msg", "分配权限失败");
		}
		return map;
	}
	//跳转角色添加的方法
	@RequestMapping("/toAddRole")
	public ModelAndView toAddRole(){
		List<MenuTree> allPermissions = sysService.loadMenuTree();
		System.out.println("============"+allPermissions);
		List<SysPermission> menus = sysService.findAllMenus();
		System.out.println("============"+menus);
		List<SysRole> permissionList = sysService.findRolesAndPermissions();
		System.out.println("============"+permissionList);
		ModelAndView mv = new ModelAndView();
		mv.addObject("allPermissions", allPermissions);
		mv.addObject("menuTypes", menus);
		mv.addObject("roleAndPermissionsList", permissionList);
		mv.setViewName("rolelist");
		return mv;
	}


	//角色权限添加的方法
	@RequestMapping("/saveRoleAndPermissions")
	public String saveRoleAndPermissions(SysRole role,int[] permissionIds){
		//设置role主键，使用uuid
		String uuid = UUID.randomUUID().toString();
		role.setId(uuid);
		//默认可用
		role.setAvailable("1");
		sysService.addRoleAndPermissions(role, permissionIds);
		System.out.println("这是角色添加方法=========="+role);
		return "redirect:/toAddRole";
	}


	//新建权限的方法
	@RequestMapping("/saveSubmitPermission")
	public String saveSubmitPermission(SysPermission permission){
		if (permission.getAvailable() == null) {
			permission.setAvailable("0");
		}
		sysService.addSysPermission(permission);
		return "redirect:/toAddRole";
	}


	//查看编辑角色菜单的方法
	@RequestMapping("/loadMyPermissions")
	@ResponseBody
	public List<SysPermission> loadMyPermissions(String roleId){
		List<SysPermission> list = sysService.findPermissionsByRoleId(roleId);

		for (SysPermission sysPermission : list) {
			System.out.println(sysPermission.getId()+","+sysPermission.getType()+"\n"+sysPermission.getName() + "," + sysPermission.getUrl()+","+sysPermission.getPercode());
		}
		return list;
	}
	//编辑角色的方法
	@RequestMapping("/updateRoleAndPermission")
	public String updateRoleAndPermission(String roleId,int[] permissionIds){
		sysService.updateRoleAndPermissions(roleId, permissionIds);
		return "redirect:/findRoles";
	}
	//删除角色的方法
	@RequestMapping("/deleteRole")
	public String deleteRole(String roleId){
		sysService.deleteRoleById(roleId);
		return "redirect:/findRoles";
	}
}
