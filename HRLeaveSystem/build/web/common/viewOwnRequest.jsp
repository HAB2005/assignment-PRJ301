<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
    <head>
        <title>Danh Sách Đơn Nghỉ Phép</title>
        <style>
            .back-link {
                margin-top: 20px;
                display: inline-block;
            }
            table, th, td {
                border: 1px solid black;
                border-collapse: collapse;
                padding: 8px;
            }
        </style>
    </head>
    <body>
        <h2>Danh Sách Đơn Nghỉ Phép</h2>

        <!-- Thông tin người dùng -->
        <p><strong>Họ tên:</strong> <c:out value="${sessionScope.user.fullName}" /></p>
        <p><strong>Phòng ban:</strong> <c:out value="${sessionScope.department.departmentName}" /></p>
        <p><strong>Vai trò:</strong> ${roles[0].roleName}</p>

        <hr>

        <!-- Bảng đơn nghỉ phép -->
        <c:if test="${not empty leaveRequests}">
            <table>
                <thead>
                    <tr>
                        <th>STT</th>
                        <th>Từ ngày</th>
                        <th>Đến ngày</th>
                        <th>Loại nghỉ phép</th>
                        <th>Lý do</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="req" items="${leaveRequests}" varStatus="loop">
                        <tr>
                            <td>${loop.index + 1}</td>
                            <td>${req.fromDate}</td>
                            <td>${req.toDate}</td>
                            <td>${req.leaveTypeName}</td>
                            <td>${req.reason}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
        <c:if test="${empty leaveRequests}">
            <p>Không có đơn nghỉ phép nào.</p>
        </c:if>

        <br>

        <!-- Nút Back to Menu -->
        <c:if test="${not empty roles}">
            <c:set var="role" value="${roles[0].roleName}" />
            <c:set var="rolePath" value="${fn:toLowerCase(fn:replace(role, ' ', '_'))}" />
            <a class="back-link" href="${pageContext.request.contextPath}/${rolePath}/menu">⬅ Quay lại Menu</a>
        </c:if>

    </body>
</html>
