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
        <input type="text" id="searchInput" placeholder="🔍 Tìm theo tên..." onkeyup="filterSubordinates()" style="margin-bottom: 10px; padding: 5px; width: 300px;" />

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
                    <c:set var="highlight" value="false" />
                    <c:forEach var="id" items="${pendingUserIds}">
                        <c:if test="${id == u.userId}">
                            <c:set var="highlight" value="true" />
                        </c:if>
                    </c:forEach>

                    <tr style="<c:if test='${highlight}'>background-color: #fff3cd;</c:if>">
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

        <script>
            function filterSubordinates() {
                const input = document.getElementById("searchInput");
                const filter = input.value.toLowerCase();
                const table = document.querySelector("table");
                const rows = table.getElementsByTagName("tr");

                // Bắt đầu từ hàng thứ 1 vì hàng 0 là header
                for (let i = 1; i < rows.length; i++) {
                    const nameCell = rows[i].getElementsByTagName("td")[1]; // Cột họ tên
                    if (nameCell) {
                        const name = nameCell.textContent.toLowerCase();
                        rows[i].style.display = name.includes(filter) ? "" : "none";
                    }
                }
            }
        </script>
    </body>
</html>
