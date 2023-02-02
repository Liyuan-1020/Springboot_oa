package com.web.oa.service;



import com.web.oa.pojo.BaoxiaoBill;

import java.util.List;

public interface BaoxiaoBillService {
    //保存报销的方法
    public void saveBaoxiao(BaoxiaoBill baoxiaoBill);
    //查看报销的方法
    public List<BaoxiaoBill> findBaoxiaoBillListByUser(Long userid, int pageNum, int pageSize);
    //通过id获取到报销对象
    BaoxiaoBill findBaoxiaoBillById(long id);
    //根据id删除报销信息
    void deleteBaoxiaoBill(long id);
    //获取分页总数的方法
    Long getCount();
}
