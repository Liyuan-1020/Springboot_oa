package com.web.oa.service.impl;

import com.web.oa.mapper.BaoxiaoBillMapper;
import com.web.oa.pojo.BaoxiaoBill;
import com.web.oa.service.WorkFlowService;
import com.web.oa.utils.Constants;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Service
@Transactional
public class WorkFlowServiceImpl implements WorkFlowService {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private FormService formService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private BaoxiaoBillMapper baoxiaoBillMapper;

    //部署的方法
    @Override
    public void saveNewDeploye(InputStream in, String filename) {
        ZipInputStream zipInputStream=new ZipInputStream(in);
        this.repositoryService
                .createDeployment()
                .addZipInputStream(zipInputStream)
                .name(filename)
                .deploy();

    }
    //查看部署信息的方法
    @Override
    public List<Deployment> findDeploymentList() {
        List<Deployment> list = this.repositoryService
                .createDeploymentQuery()
                .list();
        return list;
    }
    //查看流程定义信息的方法
    @Override
    public List<ProcessDefinition> findProcessDefinitionList() {
        List<ProcessDefinition> list = this.repositoryService
                .createProcessDefinitionQuery()
                .list();
        return list;
    }
    //查看代办流程的方法
    @Override
    public List<Task> findTaskListByName(String name) {
        List<Task> list = this.taskService
                .createTaskQuery()
                .taskAssignee(name)
                .list();
        return list;
    }
    //启动流程的方法
    @Override
    public void saveStartProcess(long id,String username) {
        String baoxiaoKey = Constants.BAOXIAO_KEY;
        //设置BUSSINES_KEY的规则
        String BUSSINES_KEY=baoxiaoKey+"."+id;

        Map<String,Object> map=new HashMap();
        map.put("inputUser",username);
        map.put("objId", BUSSINES_KEY);
        this.runtimeService.startProcessInstanceByKey(baoxiaoKey,BUSSINES_KEY,map);
    }
    //删除部署的方法
    @Override
    public void deleteProcessDefinitionByDeploymentId(String deploymentId) {
        //根据部署id删除的方法
        this.repositoryService
                .deleteDeployment(deploymentId,true);
    }
    //获取报销信息的方法
    @Override
    public BaoxiaoBill findBaoxiaoBillByTaskId(String taskId) {
        //获取任务对象
        Task task = this.taskService
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
        //获取流程实例id
        String processInstanceId = task.getProcessInstanceId();
        //获取流程实例对象
        ProcessInstance processInstance = this.runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        //获取business_key
        String businessKey = processInstance.getBusinessKey();
        System.out.println(businessKey+"======================================这里是businessKey");
        //获取报销id
        String baoxiaoId ="";
        if(businessKey!=null&&!"".equals(businessKey)){
            baoxiaoId = businessKey.split("\\.")[1];
        }
        System.out.println(baoxiaoId+"======================================这里是baoxiaoId");
        //通过baoxiaoId来获取baoxiao对象
        BaoxiaoBill baoxiaoBill = this.baoxiaoBillMapper.selectByPrimaryKey(Long.parseLong(baoxiaoId));
        return baoxiaoBill;
    }
        //获取部署流程图
    @Override
    public InputStream findImageInputStream(String deploymentId, String imageName) {
        //通过部署id和图片名来获取
        InputStream resourceAsStream = this.repositoryService
                .getResourceAsStream(deploymentId, imageName);
        return resourceAsStream;
    }
    //查看按钮的方法
    @Override
    public List<String> findOutComeListByTaskId(String taskId) {
        //返回存放连线的名称集合
        List<String> list = new ArrayList<String>();
        //1:使用任务ID，查询任务对象
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        //2：获取流程定义ID
        String processDefinitionId = task.getProcessDefinitionId();
        //3：查询ProcessDefinitionEntiy对象
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        //使用任务对象Task获取流程实例ID
        String processInstanceId = task.getProcessInstanceId();
        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
                .processInstanceId(processInstanceId)//使用流程实例ID查询
                .singleResult();
        //获取当前活动的id
        String activityId = pi.getActivityId();
        //4：获取当前的活动
        ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
        //5：获取当前活动完成之后连线的名称
        List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
        if(pvmList!=null && pvmList.size()>0){
            for(PvmTransition pvm:pvmList){
                String name = (String) pvm.getProperty("name");
                if(StringUtils.isNotBlank(name)){
                    list.add(name);
                } else{
                    list.add("默认提交");
                }
            }
        }
        return list;
    }
    //完成任务的方法
    @Override
    public void saveSubmitTask(long id, String taskId, String comment, String outcome, String username) {
        //获取到任务对象
        Task task = this.taskService
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
        //获取到实例id
        String processInstanceId = task.getProcessInstanceId();
        //加当前任务的审核人
        Authentication.setAuthenticatedUserId(username);
        //添加批注信息
        taskService.addComment(taskId, processInstanceId, comment);
        Map<String, Object> map = new HashMap<String,Object>();
        if(outcome!=null && !outcome.equals("默认提交")){
            map.put("message", outcome);
            //3：使用任务ID，完成当前人的个人任务，同时流程变量
            taskService.complete(taskId, map);
        } else {
            taskService.complete(taskId);
        }
        //获取到实例对象
        ProcessInstance processInstance = this.runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        //如果流程对象为空，表示流程结束了
        if(processInstance==null){
            //更新请假单表的状态从1变成2（审核中-->审核完成）
            BaoxiaoBill bill = baoxiaoBillMapper.selectByPrimaryKey(id);
            bill.setState(2);
            baoxiaoBillMapper.updateByPrimaryKey(bill);
        }
    }
    //查看历史批注
    @Override
    public List<Comment> findCommentByBaoxiaoBillId(long id) {
        //获取bussiness_key
        String bussiness_key = Constants.BAOXIAO_KEY +"."+id;
        //获取历史实例对象
        HistoricProcessInstance historicProcessInstance = this.historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(bussiness_key)
                .singleResult();
        System.out.println("这里是历史实例对象id"+historicProcessInstance.getId());
        //获取历史实例对象id
        String historicProcessInstanceId = historicProcessInstance.getId();
        //获取批注信息
        List<Comment> commentList = this.taskService
                .getProcessInstanceComments(historicProcessInstanceId);
        //返回
        return commentList;
    }
    //通过任务id来查找批注信息
    @Override
    public List<Comment> findCommentByTaskId(String taskId) {
        Task task = this.taskService
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
        String processId = task.getProcessInstanceId();
        List<Comment> list = this.taskService
                .getProcessInstanceComments(processId);
        return list;
    }
    //查看流程定义对象
    @Override
    public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
        //获取到任务对象
        Task task = this.taskService
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
        //获取到流程定义id
        String processDefinitionId = task.getProcessDefinitionId();
        //获取到流程定义对象
        ProcessDefinition processDefinition = this.repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        return processDefinition;
    }
    //查看报销单流程图
    @Override
    public Map<String, Object> findCoordingByTask(String taskId) {
        //存放坐标
        Map<String, Object> map = new HashMap<String,Object>();
        //获取到任务对象
        Task task = this.taskService
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
        //获取流程定义的ID
        String processDefinitionId = task.getProcessDefinitionId();
        //获取流程定义的实体对象（对应.bpmn文件中的数据）
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)repositoryService
                .getProcessDefinition(processDefinitionId);
        //流程实例ID
        String processInstanceId = task
                .getProcessInstanceId();
        //使用流程实例ID，查询正在执行的执行对象表，获取当前活动对应的流程实例对象
        ProcessInstance processInstance = this.runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        //获取当前活动的ID
        String activityId = processInstance
                .getActivityId();
        //获取当前活动对象
        ActivityImpl activityImpl = processDefinitionEntity
                .findActivity(activityId);//活动ID
        //获取坐标
        map.put("x", activityImpl.getX());
        map.put("y", activityImpl.getY());
        map.put("width", activityImpl.getWidth());
        map.put("height", activityImpl.getHeight());
        return map;
    }
    //根据bussinesskey来查找任务task对象
    @Override
    public Task findTaskByBussinessKey(String bussiness_key) {
        Task task = this.taskService
                .createTaskQuery()
                .processInstanceBusinessKey(bussiness_key)
                .singleResult();
        return task;
    }


}
