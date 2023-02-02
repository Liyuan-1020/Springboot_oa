package com.web.oa.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.BaoxiaoBill;
import com.web.oa.service.BaoxiaoBillService;
import com.web.oa.service.WorkFlowService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BaoxiaoBillController {
   @Autowired
    private BaoxiaoBillService baoxiaoBillService;
    @Autowired
    private WorkFlowService workFlowService;
    //提交报销申请的方法
    @RequestMapping("/saveStartBaoxiao")
    public String saveStartBaoxiao(BaoxiaoBill baoxiaoBill){

        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        baoxiaoBill.setCreatdate(new Date());
        baoxiaoBill.setState(1);
        baoxiaoBill.setUserId(activeUser.getId());

        baoxiaoBillService.saveBaoxiao(baoxiaoBill);
        //调用启动流程的方法
        workFlowService.saveStartProcess(baoxiaoBill.getId(),activeUser.getUsername());
        //调用查看流程的方法
        return "redirect:/myTaskList";
    }
    //查看我的报销单报销的方法,对报销单分页的方法
    @RequestMapping("/aaa")
    public void home(ModelMap model, HttpServletRequest request,HttpServletResponse response) throws Exception {

        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();

        //从前端获取pagenow,和pagesize
        int pageNow = Integer.parseInt(request.getParameter("page"));
        int pageSize = Integer.parseInt(request.getParameter("limit"));
        //查到报销集合
        List<BaoxiaoBill> list = baoxiaoBillService.findBaoxiaoBillListByUser(activeUser.getId(),pageNow,pageSize);
        model.addAttribute("baoxiaoList",list);
        System.out.println("当前页"+pageNow+"分页尺寸"+pageSize);
        //获取分页总数
        Long count = baoxiaoBillService.getCount();
        System.out.println(count);
        Map<String,Object> map = new HashMap<>();
        map.put("msg","");
        map.put("code","");
        map.put("count",count);
        map.put("data",list);
        ObjectMapper m = new ObjectMapper();
        String json = m.writeValueAsString(map);

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json);

        /*return "baoxiaobilll";*/
    }
    @RequestMapping("/myBaoxiaoBill")
    public String aa(){
        return "baoxiaobill";
    }
    //删除报销单的方法
    @RequestMapping("/leaveBillAction_delete")
    public String leaveBillAction_delete(long id){
        baoxiaoBillService.deleteBaoxiaoBill(id);
        return "redirect:/myBaoxiaoBill";
    }
}
