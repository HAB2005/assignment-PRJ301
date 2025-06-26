<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
    <head>
        <title>Danh sách cấp dưới</title>
        <style>
            body {
                font-family: sans-serif;
                background: #f4f4f4;
                padding: 20px;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                background: white;
                margin-top: 20px;
            }
            th, td {
                padding: 10px;
                border: 1px solid #ccc;
                text-align: center;
            }
            a.btn {
                padding: 6px 10px;
                background: #007bff;
                color: white;
                text-decoration: none;
                border-radius: 4px;
            }
            a.btn:hover {
                background: #0056b3;
            }
        </style>
    </head>
    <body>
        <h2>Danh sách cấp dưới</h2>
        <table>
            <thead>
                <tr>
                    <th>STT</th>
                    <th>Họ tên</th>
                    <th>Email</th>
                    <th>Phòng ban</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="u" items="${subordinates}" varStatus="loop">
                    <tr>
                        <td>${loop.index + 1}</td>
                        <td>${u.fullName}</td>
                        <td>${u.email}</td>
                        <td>${u.department.departmentName}</td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/${currentFeatureLink}">
                                <input type="hidden" name="userId" value="${u.userId}" />
                                <button class="btn" type="submit">Xem agenda</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <c:if test="${not empty roles}">
            <c:set var="role" value="${roles[0].roleName}" />        
            <c:set var="rolePath" value="${fn:toLowerCase(fn:replace(role, ' ', '_'))}" />
            <a class="back-link" href="${pageContext.request.contextPath}/${rolePath}/menu">⬅ Quay lại Menu</a>
        </c:if>
    </body>
</html>
