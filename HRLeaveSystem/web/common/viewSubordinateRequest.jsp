<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
        <title>Yêu cầu nghỉ phép của cấp dưới</title>
    </head>
    <body>
        <h2>Yêu cầu nghỉ phép của cấp dưới</h2>

        <table border="1" cellpadding="10">
            <thead>
                <tr>
                    <th>Họ tên</th>
                    <th>Phòng ban</th>
                    <th>Từ ngày</th>
                    <th>Đến ngày</th>
                    <th>Tổng số ngày</th>
                    <th>Loại nghỉ</th>
                    <th>Lý do</th>
                    <th>Trạng thái</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="req" items="${requests}">
                    <tr>
                        <td>${req.employeeName}</td>
                        <td>${req.departmentName}</td>
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
                        <td>
                            <c:if test="${req.approved == null}">
                                <form action="approveRequest" method="post" style="display:inline;">
                                    <input type="hidden" name="requestId" value="${req.requestId}" />
                                    <input type="hidden" name="decision" value="approved" />
                                    <button type="submit">Duyệt</button>
                                </form>
                                <form action="approveRequest" method="post" style="display:inline;">
                                    <input type="hidden" name="requestId" value="${req.requestId}" />
                                    <input type="hidden" name="decision" value="rejected" />
                                    <button type="submit">Từ chối</button>
                                </form>
                            </c:if>
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
