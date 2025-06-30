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

            body {
                font-family: 'Segoe UI', sans-serif;
                background: #f4f6fa;
                padding: 30px;
                margin: 0;
                color: #333;
            }

            h2 {
                text-align: center;
                color: #4a4a4a;
                margin-bottom: 20px;
            }

            #searchInput {
                display: block;
                margin: 0 auto 20px;
                padding: 10px 16px;
                width: 320px;
                border: 1px solid #ccc;
                border-radius: 10px;
                font-size: 14px;
                box-shadow: 0 1px 4px rgba(0,0,0,0.05);
                transition: border-color 0.3s;
            }

            #searchInput:focus {
                border-color: #6b46c1;
                outline: none;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                background: white;
                border-radius: 12px;
                box-shadow: 0 4px 20px rgba(0,0,0,0.05);
                overflow: hidden;
                table-layout: fixed;
            }

            th {
                background-color: #6b46c1;
                color: white;
                padding: 14px 10px;
                text-align: center;
                font-weight: 600;
                font-size: 15px;
            }

            td {
                padding: 12px 10px;
                border-bottom: 1px solid #eee;
                text-align: center;
                word-wrap: break-word;
            }

            tr:nth-child(even) td {
                background-color: #fafafa;
            }

            tr:hover td {
                background-color: #f0f0ff;
            }

            tr[style*="background-color: #fff3cd;"] td {
                background-color: #fff8e1 !important;
            }

            a.btn, button.btn {
                padding: 8px 14px;
                background: #6b46c1;
                color: white;
                text-decoration: none;
                border-radius: 8px;
                font-weight: 500;
                font-size: 13px;
                border: none;
                cursor: pointer;
                transition: background-color 0.3s;
            }

            a.btn:hover, button.btn:hover {
                background-color: #5a2aa5;
            }

            .back-link {
                display: inline-block;
                margin-top: 25px;
                color: #6b46c1;
                text-decoration: none;
                font-weight: bold;
                padding: 8px 12px;
                border: 1px solid #6b46c1;
                border-radius: 8px;
                transition: background-color 0.3s, color 0.3s;
            }

            .back-link:hover {
                background-color: #6b46c1;
                color: white;
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
