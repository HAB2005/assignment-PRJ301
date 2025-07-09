<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title>Gán Quyền</title>
        <style>
            body {
                font-family: 'Segoe UI', sans-serif;
                background: #f4f6f9;
                padding: 30px;
            }

            h2 {
                text-align: center;
                color: #333;
            }

            form {
                max-width: 600px;
                margin: 0 auto;
                background: white;
                padding: 30px;
                border-radius: 12px;
                box-shadow: 0 4px 10px rgba(0,0,0,0.05);
            }

            label {
                display: block;
                margin-top: 20px;
                font-weight: bold;
            }

            select, input[type="submit"] {
                width: 100%;
                padding: 10px;
                border-radius: 8px;
                border: 1px solid #ccc;
                margin-top: 8px;
            }

            input[type="submit"] {
                background: #6b46c1;
                color: white;
                font-weight: bold;
                cursor: pointer;
            }

            input[type="submit"]:hover {
                background: #5a2aa5;
            }

            .checkbox-group {
                margin-top: 10px;
            }

            .checkbox-group label {
                font-weight: normal;
            }
        </style>
    </head>
    <body>
        <h2>Gán Quyền Cho Người Dùng</h2>

        <form action="assign_permission" method="post">
            <label for="departmentId">Phòng ban:</label>
            <select name="departmentId" id="departmentId" required>
                <c:forEach var="d" items="${departments}">
                    <option value="${d.departmentId}">${d.departmentName}</option>
                </c:forEach>
            </select>

            <label for="managerId">Người quản lý:</label>
            <select name="managerId" id="managerId">
                <option value="">-- Không có --</option>
                <c:forEach var="m" items="${managers}">
                    <option value="${m.userId}">${m.fullName}</option>
                </c:forEach>
            </select>

            <label for="roleId">Vai trò:</label>
            <select name="roleId" id="roleId" required>
                <c:forEach var="r" items="${roles}">
                    <option value="${r.roleId}">${r.roleName}</option>
                </c:forEach>
            </select>

            <label>Tính năng được cấp:</label>
            <div class="checkbox-group">
                <c:forEach var="f" items="${features}">
                    <label><input type="checkbox" name="featureIds" value="${f.featureId}" /> ${f.featureName}</label><br/>
                </c:forEach>
            </div>

            <input type="submit" value="Lưu thay đổi" />

        </form>
    </body>
</html>
