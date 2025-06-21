<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
        <title>Xem Đơn Xin Nghỉ Phép</title>
    </head>
    <body>
        <h2>Thông Tin Đơn Xin Nghỉ Phép</h2>

        <p><strong>Họ tên:</strong> ${user.fullName}</p>

        <p><strong>Vai trò:</strong>
            <c:forEach var="role" items="${roles}" varStatus="loop">
                ${role.roleName}<c:if test="${!loop.last}">, </c:if>
            </c:forEach>
        </p>

        <p><strong>Tên phòng ban:</strong> ${departmentName}</p>

        <hr>

        <p><strong>Từ ngày:</strong> ${request.fromDate}</p>
        <p><strong>Đến ngày:</strong> ${request.toDate}</p>
        <p><strong>Loại nghỉ phép:</strong> ${leaveTypeName}</p>
        <p><strong>Lý do:</strong> ${request.reason}</p>

        <br>

        <!-- Nút quay lại Menu dựa trên vai trò đầu tiên -->
        <c:if test="${not empty roles}">
            <c:choose>
                <c:when test="${roles[0].roleName eq 'General Manager'}">
                    <a href="${pageContext.request.contextPath}/general_manager/menu">⬅ Quay lại Menu</a>
                </c:when>
                <c:when test="${roles[0].roleName eq 'Department Head'}">
                    <a href="${pageContext.request.contextPath}/department_head/menu">⬅ Quay lại Menu</a>
                </c:when>
                <c:when test="${roles[0].roleName eq 'Direct Manager'}">
                    <a href="${pageContext.request.contextPath}/direct_manager/menu">⬅ Quay lại Menu</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/employee/menu">⬅ Quay lại Menu</a>
                </c:otherwise>
            </c:choose>
        </c:if>
    </body>
</html>
