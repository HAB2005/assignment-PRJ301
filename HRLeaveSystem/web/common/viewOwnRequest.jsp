<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
    <head>
        <title>Danh sách đơn nghỉ phép của tôi</title>
    </head>
    <body>
        <h2>Đơn nghỉ phép của bạn</h2>

        <c:choose>
            <c:when test="${empty myRequests}">
                <p>Không có đơn nghỉ phép nào.</p>
            </c:when>
            <c:otherwise>
                <table border="1">
                    <tr>
                        <th>STT</th>
                        <th>Từ ngày</th>
                        <th>Đến ngày</th>
                        <th>Loại nghỉ</th>
                        <th>Lý do</th>
                        <th>Trạng thái</th>
                    </tr>
                    <c:forEach var="r" items="${myRequests}" varStatus="loop">
                        <tr>
                            <td>${loop.index + 1}</td>
                            <td><fmt:formatDate value="${r.fromDate}" pattern="dd/MM/yyyy"/></td>
                            <td><fmt:formatDate value="${r.toDate}" pattern="dd/MM/yyyy"/></td>
                            <td>${r.leaveType.typeName}</td>
                            <td>${r.reason}</td>
                            <td>${r.status}</td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
                
        <c:if test="${not empty roles}">
            <c:set var="role" value="${roles[0].roleName}" />        
            <c:set var="rolePath" value="${fn:toLowerCase(fn:replace(role, ' ', '_'))}" />
            <a class="back-link" href="${pageContext.request.contextPath}/${rolePath}/menu">⬅ Quay lại Menu</a>
        </c:if>

    </body>
</html>
