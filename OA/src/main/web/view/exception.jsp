<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2020/12/12
  Time: 9:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>异常页面</title>
</head>
<body>
<table>
    <tr>
        <td>
            <img src="${pageContext.request.contextPath}/img/404.jpeg">
        </td>
    </tr>
    <tr>
        <td>
            <p>${error}</p>
        </td>
    </tr>
</table>
</body>
</html>
