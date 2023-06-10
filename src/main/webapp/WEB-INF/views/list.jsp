<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="${mvc.basePath}/../app.css" rel="stylesheet">
<title>メッセージの累積</title>
</head>
<body>
	${loginUserModel.name}さん、こんにちは！<br>
	<form action="${mvc.basePath}/fileupload" method="POST" enctype="multipart/form-data">
		メッセージ：<input type="text" name="message">
		<input type="file" name="uploadfile">
		<button>送信</button>
	</form>	
	<form action="${mvc.basePath}/clear" method="GET">
		<button>Clear</button>
	</form>
	<hr>
	<h1>メッセージ一覧</h1>
	<c:forEach var="mes" items="${messagesModel}">
		<div>${mes.name}:${mes.message}
		<c:if test = "${ mes.fileName != null }"><br>[<a href="../${ uploaderDirName }/${ mes.fileName }">${ mes.fileName }</a>]</c:if></div>
	</c:forEach>
	<p>
		<a href="${mvc.basePath}/login">ログアウト</a>
	</p>
	<p>
		<a href="${mvc.basePath}/">ホームへ戻る</a>
	</p>
</body>
</html>
