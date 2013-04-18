<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="../js/jquery-1.6.4.js"></script>
<title>开奖抓取</title>
<style>
.boxCss{ width:600px; height:400px; overflow:hidden; margin:0 auto; padding:0 auto;}
.boxCss a{ width:290px; height:140px; float:left; margin-right:10px; background:#9F3; margin-bottom:10px;text-decoration:none;text-align:center; line-height:140px;margin-top:120px;}
.boxCss a:hover{ width:290px; height:140px; float:left; margin-right:10px;   background:#33C; }
</style>
</head>
<body>
<div class="boxCss">
<a href="<%=path%>/system/goMissvalue"><font size="30" color="red">开奖抓取</font></a>
</div>
</body>
</html>