package com.web.oa.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.web.oa.mapper.BaoxiaoBillMapper;
import com.web.oa.pojo.BaoxiaoBill;
import com.web.oa.pojo.BaoxiaoBillExample;
import com.web.oa.service.BaoxiaoBillService;
//import com.web.oa.service.WorkFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class BaoxiaoBillServiceImpl implements BaoxiaoBillService {
    @Autowired
    private BaoxiaoBillMapper baoxiaoBillMapper;
    //@Autowired
    //private WorkFlowService workFlowService;
    //直接调用插入方法,发起报销
    Page page=null;
    @Override
    public void saveBaoxiao(BaoxiaoBill baoxiaoBill) {
        baoxiaoBillMapper.insert(baoxiaoBill);
    }
    //查看报销信息的方法
    @Override
    public List<BaoxiaoBill> findBaoxiaoBillListByUser(Long userid,int pageNum,int pageSize) {
        //通过userid来查找报销表
        BaoxiaoBillExample example=new BaoxiaoBillExample();
        BaoxiaoBillExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userid);
        page= PageHelper.startPage(pageNum,pageSize);
        List<BaoxiaoBill> baoxiaoBills = baoxiaoBillMapper.selectByExample(example);
        return baoxiaoBills;
    }
    //通过id查找报销对象
    @Override
    public BaoxiaoBill findBaoxiaoBillById(long id) {
        return baoxiaoBillMapper.selectByPrimaryKey(id);
    }
    //删除报销的方法
    @Override
    public void deleteBaoxiaoBill(long id) {
        baoxiaoBillMapper.deleteByPrimaryKey(id);
    }
    //获取分页总数的方法
    @Override
    public Long getCount() {
        return  page.getTotal();
    }
}
