<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
        <title>Lịch nghỉ phép của tôi</title>
    </head>
    <body>
        <h2>Lịch nghỉ phép của tôi</h2>

        <p><strong>Họ tên:</strong> ${user.fullName}</p>

        <p><strong>Vai trò:</strong>
            <c:forEach var="role" items="${roles}" varStatus="loop">
                ${role.roleName}<c:if test="${!loop.last}">, </c:if>
            </c:forEach>
        </p>

        <p><strong>Phòng ban:</strong> ${departmentName}</p>

        <br>

        <table border="1" cellpadding="10">
            <thead>
                <tr>
                    <th>Tiêu đề</th>
                    <th>Từ ngày</th>
                    <th>Đến ngày</th>
                    <th>Tổng số ngày</th>
                    <th>Loại nghỉ</th>
                    <th>Lý do</th>
                    <th>Trạng thái</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="req" items="${myRequests}">
                    <tr>
                        <td>${req.title}</td>
                        <td>${req.fromDate}</td>
                        <td>${req.toDate}</td>
                        <td>${req.totalDays}</td>
                        <td>${req.leaveTypeName}</td>
                        <td>${req.reason}</td>
                        <td>
                            <c:choose>
                                <c:when test="${req.approved eq true}">
                                    ✅ Đã duyệt
                                </c:when>
                                <c:when test="${req.approved eq false}">
                                    ❌ Từ chối
                                </c:when>
                                <c:otherwise>
                                    ⏳ Đang chờ
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <br>

        <!-- Nút quay lại Menu -->
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
