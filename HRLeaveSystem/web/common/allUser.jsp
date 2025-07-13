<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
    <head>
        <title>Danh sách người dùng</title>
        <style>
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

            a.btn {
                padding: 8px 14px;
                background: #6b46c1;
                color: white;
                text-decoration: none;
                border-radius: 8px;
                font-weight: 500;
                font-size: 13px;
                transition: background-color 0.3s;
                display: inline-block;
            }

            a.btn:hover {
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

            button.btn {
                padding: 8px 14px;
                background: #6b46c1;
                color: white;
                border: none;
                border-radius: 8px;
                font-weight: 500;
                font-size: 13px;
                cursor: pointer;
                transition: background-color 0.3s;
            }

            button.btn:hover {
                background-color: #5a2aa5;
            }

        </style>
    </head>
    <body>
        <h2>Danh sách người dùng</h2>

        <input type="text" id="searchInput" placeholder="🔍 Tìm theo tên..." onkeyup="filterUsers()" />

        <table>
            <thead>
                <tr>
                    <th>User ID</th>
                    <th>Username</th>
                    <th>Password</th>
                    <th>Email</th>
                    <th>Full Name</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody id="userTable">
                <c:forEach var="u" items="${userList}">
                    <tr>
                        <td>${u.userId}</td>
                        <td>${u.username}</td>
                        <td>********</td>
                        <td>${u.email}</td>
                        <td>${u.fullName}</td>
                        <td>
                            <form method="post" action="${formAction}">
                                <input type="hidden" name="userId" value="${u.userId}" />
                                <button class="btn" type="submit">Edit</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>

            </tbody>
        </table>

        <a href="${pageContext.request.contextPath}/${rolePath}/menu" class="back-link">Quay lại</a>

        <script>
            function filterUsers() {
                let input = document.getElementById("searchInput");
                let filter = input.value.toLowerCase();
                let rows = document.querySelectorAll("#userTable tr");

                rows.forEach(row => {
                    let name = row.cells[1].textContent.toLowerCase();
                    row.style.display = name.includes(filter) ? "" : "none";
                });
            }
        </script>
    </body>
</html>
