<%--
  User: dmulderink
  Date: 8/30/12
  Time: 6:04 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <link href="../../../css/ksaHeader.css" rel="stylesheet" type="text/css" />
</head>
<body>
    <img src="<%= request.getContextPath() %>/images/logo.png" width="200" height="53"  style="margin:10px;"/>
    <table width="100%" border="0" cellspacing="0" cellpadding="0" style="height:100%; width:100%;">
        <tr>
            <td style="height:47px;"><img src="<%= request.getContextPath() %>/images/topLeft.png" width="74" height="47" /></td>
            <td style="height:47px; width:100%; background-image:url(<%= request.getContextPath() %>/images/topMiddle.png); background-repeat:repeat-x;"></td>
            <td style="height:47px;"><img src="<%= request.getContextPath() %>/images/topRight.png" width="68" height="47" /></td>
        </tr>
        <tr>
            <td style="background-image:url(<%= request.getContextPath() %>/images/middleLeft.png); background-repeat:repeat-y;"></td>
            <td align="left" valign="top" style="background-color:#fff;" >This is the KSA VIEW area.</td>
            <td style="background-image:url(<%= request.getContextPath() %>/images/middleRight.png); background-repeat:repeat-y;"></td>
        </tr>
    </table>
</body>
</html>