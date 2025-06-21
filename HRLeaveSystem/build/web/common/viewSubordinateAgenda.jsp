<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
        <title>Lịch nghỉ của cấp dưới</title>
    </head>
    <body>
        <h2>Lịch nghỉ của cấp dưới</h2>

        <table border="1" cellpadding="10">
            <thead>
                <tr>
                    <th>Họ tên</th>
                    <th>Phòng ban</th>
                    <th>Tiêu đề</th>
                    <th>Từ ngày</th>
                    <th>Đến ngày</th>
                    <th>Số ngày</th>
                    <th>Loại nghỉ</th>
                    <th>Lý do</th>
                    <th>Trạng thái</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="req" items="${subordinateAgenda}">
                    <tr>
                        <td>${req.employeeName}</td>
                        <td>${req.departmentName}</td>
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
            </c:choose>
        </c:if>
    </body>
</html>
