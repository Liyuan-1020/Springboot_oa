package com.web.oa.pojo;

import java.io.Serializable;

public class EmployeeCustom extends Employee implements Serializable {

	private static final long serialVersionUID = 3847351417655434275L;
	private String roleId;
	private String rolename;
	private String manager;
	
	
	public String getRolename() {
		return rolename;
	}
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	
}
