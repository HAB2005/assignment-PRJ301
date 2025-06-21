<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
    <head>
        <title>Create Leave Request</title>
        <style>
            .back-link {
                margin-top: 20px;
                display: inline-block;
            }
        </style>
    </head>
    <body>
        <h2>Create Leave Request</h2>

        <!-- Thông tin người dùng -->
        <p><strong>Tên:</strong> <c:out value="${sessionScope.user.fullName}" /></p>
        <p><strong>Vai trò:</strong>
            <c:forEach var="role" items="${roles}" varStatus="loop">
                ${role.roleName}<c:if test="${!loop.last}">, </c:if>
            </c:forEach>
        </p>

        <form action="createRequest" method="post">
            <label for="fromDate">Từ ngày:</label><br>
            <input type="date" id="fromDate" name="fromDate" required><br><br>

            <label for="toDate">Đến ngày:</label><br>
            <input type="date" id="toDate" name="toDate" required><br><br>

            <label for="leaveType">Loại nghỉ phép:</label><br>
            <!-- <select name="leaveTypeId" id="leaveType" required>
                TODO: Thêm options cho loại nghỉ phép từ bảng leave_types
            </select><br><br> -->

            <label for="reason">Lý do:</label><br>
            <textarea id="reason" name="reason" rows="4" cols="50" required></textarea><br><br>

            <button type="submit">Gửi yêu cầu</button>
        </form>

        <br>

        <!-- Nút Back to Menu -->
        <c:if test="${not empty roles}">
            <c:set var="role" value="${roles[0].roleName}" />        
            <c:set var="rolePath" value="${fn:toLowerCase(fn:replace(role, ' ', '_'))}" />
            <a class="back-link" href="${pageContext.request.contextPath}/${rolePath}/menu">⬅ Quay lại Menu</a>
        </c:if>

    </body>
</html>