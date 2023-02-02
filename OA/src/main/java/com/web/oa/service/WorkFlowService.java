package com.web.oa.service;

import com.web.oa.pojo.BaoxiaoBill;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface WorkFlowService {
	//部署的方法
	void saveNewDeploye(InputStream in, String filename);
	//查看部署信息的方法
	List<Deployment> findDeploymentList();
	//查看流程定义的方法
	List<ProcessDefinition> findProcessDefinitionList();
	//查看流程任务的方法,根据代办人查询
	public List<Task> findTaskListByName(String name);
	//启动流程的方法,根据key来启动
	public void saveStartProcess(long id, String username);
	//删除部署流程的方法
    public void deleteProcessDefinitionByDeploymentId(String deploymentId);
	//通过taskId来查找报销单
	public BaoxiaoBill findBaoxiaoBillByTaskId(String taskId);
	//查看流程图的方法
    InputStream findImageInputStream(String deploymentId, String imageName);
	//查看按钮的方法
	List<String> findOutComeListByTaskId(String taskId);
	//完成流程的方法
	void saveSubmitTask(long id, String taskId, String comment, String outcome, String username);
	//通过报销id来查找批注信息
	List<Comment> findCommentByBaoxiaoBillId(long id);
	//通过taskId来获取批注信息
	List<Comment> findCommentByTaskId(String taskId);
	//通过taskId来获取流程定义对象
	ProcessDefinition findProcessDefinitionByTaskId(String taskId);
	//查询到项目的x,y轴信息,以及宽度和长度
	Map<String, Object> findCoordingByTask(String taskId);
	//通过bussinesskey查找到任务对象
	Task findTaskByBussinessKey(String bussiness_key);
}
