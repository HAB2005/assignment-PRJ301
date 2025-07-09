<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
        <title>
            <c:choose>
                <c:when test="${mode == 'edit'}">Chỉnh sửa người dùng</c:when>
                <c:otherwise>Tạo người dùng mới</c:otherwise>
            </c:choose>
        </title>
        <style>
            body {
                font-family: 'Segoe UI', sans-serif;
                background: #f4f4f9;
                margin: 0;
                padding: 0;
            }

            h2 {
                text-align: center;
                margin-top: 40px;
                color: #333;
            }

            form {
                max-width: 460px;
                background: white;
                margin: 40px auto;
                padding: 30px 35px;
                border-radius: 16px;
                box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
            }

            label {
                display: block;
                margin-bottom: 6px;
                margin-top: 18px;
                font-weight: 600;
                color: #444;
            }

            input[type="text"],
            input[type="password"],
            input[type="email"] {
                width: 100%;
                padding: 10px 14px;
                border: 1px solid #ccc;
                border-radius: 10px;
                font-size: 14px;
                transition: border-color 0.3s ease;
                background: #fafafa;
            }

            input[type="text"]:focus,
            input[type="password"]:focus,
            input[type="email"]:focus {
                border-color: #6b46c1;
                outline: none;
                background: white;
            }

            .button-group {
                display: flex;
                justify-content: center;
                gap: 20px;
                margin-top: 30px;
            }

            .submit-btn,
            .back-btn {
                min-width: 160px;
                padding: 12px 24px;
                border-radius: 10px;
                font-size: 15px;
                font-weight: 600;
                text-align: center;
                transition: all 0.3s ease;
            }

            /* Nút submit */
            .submit-btn {
                background-color: #6b46c1;
                color: white;
                border: none;
                cursor: pointer;
            }

            .submit-btn:hover {
                background-color: #5a2aa5;
            }

            /* Nút quay lại */
            .back-btn {
                display: inline-block;
                background-color: transparent;
                color: #6b46c1;
                border: 2px solid #6b46c1;
                text-decoration: none;
                line-height: 42px; /* Giúp căn giữa văn bản nếu cần */
                cursor: pointer;
            }

            .back-btn:hover {
                background-color: #6b46c1;
                color: white;
            }

            .message {
                margin-top: 20px;
                font-weight: bold;
                text-align: center;
                padding: 10px;
                border-radius: 8px;
            }

            .message.success {
                background-color: #e6ffed;
                color: #2e7d32;
            }

            .message.error {
                background-color: #ffeaea;
                color: #d32f2f;
            }
        </style>
    </head>
    <body>

        <h2>
            <c:choose>
                <c:when test="${mode == 'edit'}">Chỉnh sửa người dùng</c:when>
                <c:otherwise>Tạo người dùng mới</c:otherwise>
            </c:choose>
        </h2>
        
        <form action="<c:choose>
                  <c:when test='${mode == "edit"}'>edit_user</c:when>
                  <c:otherwise>create_user</c:otherwise>
              </c:choose>" method="post">

            <c:if test="${mode == 'edit'}">
                <input type="hidden" name="userId" value="${userId}" />
                <input type="hidden" name="action" value="updateUser" />
            </c:if>

            <label for="username">Tên đăng nhập:</label>
            <input type="text" id="username" name="username" required value="${username}" />

            <label for="password">Mật khẩu:</label>
            <input type="password" id="password" name="password" required value="${password}" />

            <label for="fullname">Họ và tên:</label>
            <input type="text" id="fullname" name="fullname" required value="${fullname}" />

            <label for="email">Email:</label>
            <input type="email" id="email" name="email" required value="${email}" />

            <c:if test="${not empty message}">
                <div class="message success">${message}</div>
            </c:if>

            <c:if test="${not empty error}">
                <div class="message error">${error}</div>
            </c:if>

            <c:choose>
                <c:when test="${mode == 'edit'}">
                    <c:set var="submitLabel" value="Cập nhật" />
                </c:when>
                <c:otherwise>
                    <c:set var="submitLabel" value="Tạo người dùng" />
                </c:otherwise>
            </c:choose>

            <div class="button-group">
                <input type="submit" class="submit-btn" value="${submitLabel}" />
                <input type="hidden" name="action" value="updateUser" />
                <a href="${pageContext.request.contextPath}/${rolePath}/menu" class="back-btn">Quay lại</a>
            </div>

        </form>
    </body>
</html>
