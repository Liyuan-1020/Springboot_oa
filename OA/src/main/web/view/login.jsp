<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>

<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>myoa登录</title>
<link rel="stylesheet" href="static/css/bootstrap.min.css">
<link rel="stylesheet" href="static/css/style1.css">
<link rel="stylesheet" href="static/css/bootstrapValidator.min.css">
<script src="static/js/jquery-1.11.0.min.js"></script>
<script src="static/js/bootstrap.min.js"></script>
<script src="static/js/bootstrapValidator.min.js"></script>

	<script>
		window.onload =function () {
			document.getElementById("img1").onclick = function () {
				this.src = "${pageContext.request.contextPath}/yzms?random="+Math.random();
			}
		}
	</script>
</head>
<body>

	<div class="container">
		<div class="row">
			<div class="col-md-offset-3 col-md-6">
				
				<form class="form-horizontal" action="login" method="post" id="loginForm">
					<span class="heading">欢迎登录办公系统</span>
					<div class="form-group">
						<div class="col-sm-10">
						<input type="text" name="username" class="form-control" id="username"
							placeholder="请输入用户名" > <i class="fa fa-user"></i>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-10">
						<input type="password" name="password" class="form-control" id="password"
							placeholder="请输入密码" > <i class="fa fa-lock"></i> <a href="#"
							class="fa fa-question-circle"></a>
						</div>
					</div>

					<div class="form-group">
						<div class="col-sm-10">
							<input type="text" name="yzm" class="form-control" id="yzm"
								   placeholder="请输入验证码" >
							<img src="${pageContext.request.contextPath}/yzms" alt="" >
							<a href="" id="img1">看不清楚</a>
						</div>
					</div>
					<div class="form-group">
						 
						<div class="main-checkbox">
							<input type="checkbox" name="remember" value="true" id="checkbox1" />
							<label for="checkbox1"></label>
						</div>
						
						<span class="text">Remember me</span>
						<div class="error">${errorMsg}</div>
						<div class="col-sm-10">
							<button type="submit" id="sub" name="sub" class="btn btn-primary">登录</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>

</body>
</html>