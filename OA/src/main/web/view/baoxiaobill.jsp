<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>流程管理</title>

    <!-- Bootstrap -->
    <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="css/content.css" rel="stylesheet">

    <link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/layui/css/layui.css" rel="stylesheet">
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <script src="js/jquery.min.js"></script>
    <script src="bootstrap/js/bootstrap.min.js"></script>
</head>
<body>
<script src="${pageContext.request.contextPath}/layui/layui.all.js"></script>
<!--路径导航-->
<ol class="breadcrumb breadcrumb_nav">
    <li>首页</li>
    <li>报销管理</li>
    <li class="active">我的报销单</li>
</ol>
<!--路径导航-->

<div class="page-content">
    <form class="form-inline">
        <div class="panel panel-default">
            <div class="panel-heading">报销单列表</div>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <table class="layui-hide" id="test" lay-filter="test"></table>
            <script>
                layui.use('table', function(){
                    var table = layui.table;

                    table.render({
                        elem: '#test'
                        ,url:'${pageContext.request.contextPath}/aaa'
                        ,cols: [[
                            {field:'id', width:100, title: '报销编号', sort: true}
                            ,{field:'money', width:120, title: '报销金额'}
                            ,{field:'title', width:120, title: '报销标题', sort: true}
                            ,{field:'remark', width:150, title: '报销描述'}
                            , {field:'creatdate', width:"15%", title: '时间',templet:"<div>{{layui.util.toDateString(d.sbj_start, 'yyyy年-MM月-dd日')}}</div>"}
                            ,{field:'state', width:100, title: '申请状态',templet: function (data) {
                                    if(data.state==1){
                                        return "审核中";
                                    }else {
                                        return "审核完成";
                                    }
                                }}
                            ,{field:'right',height:80, width:300, title: '操作', toolbar:'#lineBtns'}//操作栏
                        ]]
                        ,page: true
                        ,limit:5
                        ,limits:[5,10,15]
                        , id: 'testReload'
                    });

                    //监听工具条
                    table.on('tool(test)', function(obj){ //注：tool 是工具条事件名，test 是 table 原始容器的属性 lay-filter="对应的值"
                        var data = obj.data; //获得当前行数据
                        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
                        var tr = obj.tr; //获得当前行 tr 的 DOM 对象（如果有的话）

                        if(layEvent === 'del'){ //删除
                            layer.confirm('真的删除行么', function(index){
                                obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
                                layer.close(index);
                                //向服务端发送删除指令
                                $.get('${pageContext.request.contextPath}/leaveBillAction_delete?id='+data.id, function(ret){
                                    layer.alert('删除成功',{icon:1,title:'提示'},function(i){
                                        layer.close(i);
                                        layer.close(index);//关闭弹出层
                                        $("#booktype")[0].reset()//重置form
                                    })
                                    table.reload('testReload',{//重载表格
                                        page:{
                                            curr:1
                                        }
                                    })
                                })
                            });
                        } else if(layEvent === 'selectImg'){ //查看流程图
                            /*window.location.href = "${pageContext.request.contextPath}/viewCurrentImageByBill?billId="+data.id;*/
                            window.open("${pageContext.request.contextPath}/viewCurrentImageByBill?billId="+data.id);
                        } else if(layEvent === 'selectRecode'){//查看审核记录
                            /*window.location.href = "${pageContext.request.contextPath }/viewHisComment?id="+data.id;*/
                            window.open("${pageContext.request.contextPath }/viewHisComment?id="+data.id);
                        }
                        else if(layEvent === 'LAYTABLE_TIPS'){
                            layer.alert('Hi，头部工具栏扩展的右侧图标。');
                        }
                    });

                });
            </script>
        </div>
    </div>
<!--行内样式按钮   -->
<script type="text/html" id="lineBtns">
    {{#  if(d.state =="2"){ }}
    <a class="layui-btn layui-btn-xs layui-btn-warm" lay-event="selectRecode">查看审核记录</a>
    <a class="layui-btn layui-btn-danger layui-btn-xs layui-btn-warm" lay-event="del">删除</a>
    {{#  } }}

    {{#  if(d.state =="1"){ }}

    <a class="layui-btn layui-btn-xs layui-btn-warm" lay-event="selectRecode">查看审核记录</a>
    <a class="layui-btn layui-btn-xs layui-btn-warm" lay-event="selectImg">查看当前流程图</a>

    {{#  } }}

</script>

<!-- jQuery (Bootstrap 的所有 JavaScript 插件都依赖 jQuery，所以必须放在前边) -->
<script src="${pageContext.request.contextPath}/bootstrap/js/jquery-1.11.2.js"></script>
<!-- 加载 Bootstrap 的所有 JavaScript 插件。你也可以根据需要只加载单个插件。 -->
<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.js"></script>
</body>
</html>
