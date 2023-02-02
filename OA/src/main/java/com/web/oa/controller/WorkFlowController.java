package com.web.oa.controller;

import com.sun.org.apache.xpath.internal.objects.XObject;
import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.BaoxiaoBill;
import com.web.oa.service.BaoxiaoBillService;
import com.web.oa.service.WorkFlowService;
import com.web.oa.utils.Constants;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Controller
public class WorkFlowController {
    @Autowired
    private WorkFlowService workFlowService;
    @Autowired
    private BaoxiaoBillService baoxiaoBillService;
    //部署的方法
    @RequestMapping("/deployProcess")
    public String deployProcess(String processName, MultipartFile fileName) {
        System.out.println("这里是部署的方法+++++++++++++++++++++++++++++++++++++++++++++");
        try {
            workFlowService.saveNewDeploye(fileName.getInputStream(), processName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/processDefinitionList";
    }

    //查看部署信息以及流程信息的方法
    @RequestMapping("/processDefinitionList")
    public ModelAndView processDefinitionList() {
        ModelAndView mv = new ModelAndView();
        //1:查询部署对象信息，对应表（act_re_deployment）
        List<Deployment> depList = workFlowService.findDeploymentList();
        //2:查询流程定义的信息，对应表（act_re_procdef）
        List<ProcessDefinition> pdList = workFlowService.findProcessDefinitionList();
        //放置到上下文对象中
        mv.addObject("depList", depList);
        mv.addObject("pdList", pdList);
        mv.setViewName("workflow_list");
        return mv;
    }

    //查看流程任务的方法
    @RequestMapping("/myTaskList")
    public ModelAndView getTaskList() {
        ModelAndView mv=new ModelAndView();
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Task> list = workFlowService.findTaskListByName(activeUser.getUsername());
        mv.addObject("taskList", list);
        mv.setViewName("workflow_task");
        return mv;
    }
    //删除流程的方法
    @RequestMapping("/delDeployment")
    public String delDeployment(String deploymentId){
        //使用部署对象ID，删除流程定义
        workFlowService.deleteProcessDefinitionByDeploymentId(deploymentId);
        return "redirect:/processDefinitionList";
    }
    //查看部署流程图的方法
    @RequestMapping("/viewImage")
    public String viewImage(String deploymentId, String imageName, HttpServletResponse response) throws IOException {
        //
        InputStream in = workFlowService.findImageInputStream(deploymentId,imageName);
        //将他读出到页面上
        OutputStream out=response.getOutputStream();
        //
        byte [] b=new byte[1024];
        int a=0;
        while((a=in.read(b))>0){
            out.write(b,0,b.length);
        }
        out.close();
        in.close();
        return null;
    }
    //显示办理流程的方法
    @RequestMapping("/viewTaskForm")
    public ModelAndView viewTaskForm(String taskId) {
        ModelAndView mv = new ModelAndView();
        //查看流程信息
        BaoxiaoBill bill = this.workFlowService.findBaoxiaoBillByTaskId(taskId);
        //获取按钮的方法
        List<String> outcomeList = this.workFlowService.findOutComeListByTaskId(taskId);
        //查看批注信息的方法
        List<Comment> list = this.workFlowService.findCommentByTaskId(taskId);


        mv.addObject("baoxiaoBill",bill);
        mv.addObject("commentList", list);
        mv.addObject("outcomeList", outcomeList);
        mv.addObject("taskId", taskId);
        mv.setViewName("approve_baoxiao");
        return mv;
    }
    //完成流程的方法
    @RequestMapping("/submitTask")
    public String submitTask(long id, String taskId, String comment, String outcome){
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        String username = activeUser.getUsername();
        this.workFlowService.saveSubmitTask(id, taskId, comment, outcome, username);
        return "redirect:/myTaskList";
    }
    //查看审核记录的方法
    @RequestMapping("/viewHisComment")
    public String viewHisComment(long id, ModelMap model){
        //使用报销单ID，查询报销单对象
        BaoxiaoBill bill = baoxiaoBillService.findBaoxiaoBillById(id);
        model.addAttribute("baoxiaoBill", bill);
        //使用请假单ID，查询历史的批注信息
        List<Comment> commentList = workFlowService.findCommentByBaoxiaoBillId(id);
        model.addAttribute("commentList", commentList);
        return "workflow_commentlist";
    }
    //查看当前流程图的方法
    @RequestMapping("/viewCurrentImage")
    public String viewCurrentImageByBill(String taskId,ModelMap model){
        System.out.println("/这里是taskId"+taskId+"==============================");
        /**一：查看当前流程图*/
        //1：获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
        ProcessDefinition pd = workFlowService
                .findProcessDefinitionByTaskId(taskId);
        model.addAttribute("deploymentId", pd.getDeploymentId());
        model.addAttribute("imageName", pd.getDiagramResourceName());
        /**二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中*/
        Map<String, Object> map = workFlowService.findCoordingByTask(taskId);
        model.addAttribute("acs", map);
        return "viewimage";
    }
    //查看报销单流程图
    @RequestMapping("/viewCurrentImageByBill")
    public String viewCurrentImageByBill(long billId,ModelMap model) {
        String BUSSINESS_KEY = Constants.BAOXIAO_KEY + "." + billId;
        System.out.println("BUSSINESS_KEY类型为"+getType(BUSSINESS_KEY));
        System.out.println("这里是bussiness_key========="+BUSSINESS_KEY);
        Task task = this
                .workFlowService
                .findTaskByBussinessKey(BUSSINESS_KEY);
        System.out.println(task+"===");
        //查看流程图
        //获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
        ProcessDefinition pd = workFlowService
                .findProcessDefinitionByTaskId(task.getId());
        model.addAttribute("deploymentId", pd.getDeploymentId());
        model.addAttribute("imageName", pd.getDiagramResourceName());
        //查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中
        Map<String, Object> map = workFlowService
                .findCoordingByTask(task.getId());
        model.addAttribute("acs", map);
        return "viewimage";
    }

    public static String getType(Object o){ //获取变量类型方法
        return o.getClass().toString(); //使用int类型的getClass()方法
    }

}
