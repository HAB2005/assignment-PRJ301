<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
    <head>
        <title>Tạo người dùng</title>
        <style>
            body {
                font-family: 'Segoe UI', sans-serif;
                background: #f4f4f9;
                margin: 0;
                padding: 0;
            }

            h2 {
                text-align: center;
                margin-top: 30px;
                color: #333;
            }

            form {
                max-width: 450px;
                margin: 30px auto;
                background: #fff;
                padding: 30px;
                border-radius: 16px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.06);
            }

            label {
                display: block;
                margin-top: 16px;
                margin-bottom: 6px;
                font-weight: bold;
                color: #5a2aa5;
            }

            input[type="text"],
            input[type="password"],
            input[type="email"] {
                width: 100%;
                padding: 12px;
                border: 1px solid #ccc;
                border-radius: 8px;
                box-sizing: border-box;
                font-size: 14px;
            }

            .button-group {
                display: flex;
                justify-content: space-between;
                margin-top: 30px;
            }

            .button-group input,
            .button-group a {
                width: 48%;
                padding: 12px;
                text-align: center;
                font-weight: bold;
                border-radius: 999px;
                text-decoration: none;
                display: inline-block;
                box-sizing: border-box;
            }

            input[type="submit"] {
                background-color: #6b46c1;
                color: white;
                border: none;
                cursor: pointer;
            }

            input[type="submit"]:hover {
                background-color: #5a2aa5;
            }

            a.back-button {
                background-color: #eee;
                color: #555;
                border: 1px solid #ccc;
            }

            a.back-button:hover {
                background-color: #ddd;
            }
        </style>
    </head>
    <body>

        <h2>Tạo người dùng mới</h2>

        <form action="create_user" method="post">
            <label for="username">Tên đăng nhập:</label>
            <input type="text" id="username" name="username" required />

            <label for="password">Mật khẩu:</label>
            <input type="password" id="password" name="password" required />

            <label for="fullname">Họ và tên:</label>
            <input type="text" id="fullname" name="fullname" required />

            <label for="email">Email:</label>
            <input type="email" id="email" name="email" required />

            <c:if test="${not empty message}">
                <div style="color: green; font-weight: bold; margin-bottom: 12px;">
                    ${message}
                </div>
            </c:if>

            <c:if test="${not empty error}">
                <div style="color: red; font-weight: bold; margin-bottom: 12px;">
                    ${error}
                </div>
            </c:if>

            <div class="button-group">
                <input type="submit" value="Tạo người dùng" />
                <a href="${pageContext.request.contextPath}/${rolePath}/menu" class="back-button">Quay lại</a>
            </div>
        </form>

    </body>
</html>
