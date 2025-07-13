<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
    <head>
        <title>Gán Quyền Cho Người Dùng</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f7f9fc;
                margin: 0;
                padding: 20px;
            }

            h2 {
                text-align: center;
                color: #2c3e50;
                margin-bottom: 30px;
            }

            form {
                max-width: 700px;
                margin: 0 auto;
                background-color: #fff;
                padding: 25px 30px;
                border-radius: 10px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            }

            label {
                font-weight: bold;
                display: block;
                margin: 15px 0 5px;
            }

            select, input[type="text"], button {
                width: 100%;
                padding: 10px;
                border: 1px solid #ccc;
                border-radius: 6px;
                box-sizing: border-box;
                margin-bottom: 15px;
                font-size: 14px;
            }

            select:focus, input:focus {
                outline: none;
                border-color: #3498db;
            }

            .checkbox-group {
                margin: 10px 0;
                padding: 10px;
                background: #f0f4f8;
                border-radius: 6px;
                max-height: 200px;
                overflow-y: auto;
            }

            .checkbox-group label {
                display: block;
                font-weight: normal;
                margin-bottom: 5px;
            }

            .button-group {
                margin-top: 20px;
                display: flex;
                gap: 10px;
            }

            .btn {
                padding: 10px 20px;
                border: none;
                font-weight: bold;
                border-radius: 6px;
                text-decoration: none;
                text-align: center;
                cursor: pointer;
                transition: background-color 0.3s ease, transform 0.1s ease;
            }

            .save-btn {
                background-color: #4CAF50; /* xanh lá */
                color: white;
            }

            .save-btn:hover {
                background-color: #45a049;
                transform: scale(1.02);
            }

            .back-btn {
                background-color: #f44336; /* đỏ */
                color: white;
            }

            .back-btn:hover {
                background-color: #d32f2f;
                transform: scale(1.02);
            }
        </style>

    </head>
    <body>
        <h2>Gán Quyền Cho Người Dùng</h2>

        <form action="${pageContext.request.contextPath}/admin/assign_permissions" method="post">
            <input type="hidden" name="userId" value="${selectedUser.userId}" />

            <label>Người dùng:</label>
            <input type="text" value="${selectedUser.fullName}" readonly />

            <label for="departmentId">Phòng ban:</label>
            <select name="departmentId" id="departmentId" required>
                <c:forEach var="d" items="${departments}">
                    <option value="${d.departmentId}" ${selectedUser.department != null && selectedUser.department.departmentId == d.departmentId ? 'selected' : ''}>
                        ${d.departmentName}
                    </option>
                </c:forEach>
            </select>

            <label for="managerId">Quản lý:</label>
            <select name="managerId" id="managerId">
                <option value="">-- Không có --</option>

                <c:forEach var="m" items="${managers}">
                    <c:set var="selected" value="" />
                    <c:if test="${selectedUser.managerId != null and selectedUser.managerId == m.userId}">
                        <c:set var="selected" value="selected" />
                    </c:if>
                    <option value="${m.userId}" ${selected}>${m.fullName}</option>
                </c:forEach>
            </select>

            <label for="roleId">Vai trò:</label>
            <select name="roleId" id="roleId" required>
                <c:forEach var="r" items="${roles}">
                    <option value="${r.roleId}" ${selectedRoleId == r.roleId ? 'selected' : ''}>${r.roleName}</option>
                </c:forEach>
            </select>

            <label>Tính năng được cấp:</label>
            <div class="checkbox-group">
                <c:forEach var="f" items="${features}">
                    <c:set var="checkStr" value=",${f.featureId}," />
                    <label>
                        <input type="checkbox" name="featureIds" value="${f.featureId}"
                               <c:if test="${fn:contains(assignedFeatureString, checkStr)}">checked</c:if> />
                        ${f.featureName}
                    </label>
                </c:forEach>
            </div>

            <div class="button-group">
                <button type="submit" class="btn save-btn">Lưu thay đổi</button>
                <a href="${pageContext.request.contextPath}/${rolePath}/menu" class="btn back-btn">Về menu</a>
                <a href="${pageContext.request.contextPath}/${rolePath}/assign_permissions" class="btn back-btn">Danh sách người dùng</a>
            </div>


        </form>

        <c:if test="${not empty message}">
            <p style="color: green; text-align: center;">${message}</p>
        </c:if>
        <c:if test="${not empty error}">
            <p style="color: red; text-align: center;">${error}</p>
        </c:if>

        <!-- JavaScript để cập nhật danh sách quản lý khi chọn phòng ban -->
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const deptSelect = document.getElementById("departmentId");
                const managerSelect = document.getElementById("managerId");
                const baseUrl = "${pageContext.request.contextPath}/${rolePath}/assign_permissions";

                if (!deptSelect || !managerSelect) {
                    console.error("Không tìm thấy phần tử departmentId hoặc managerId");
                    return;
                }

                deptSelect.addEventListener("change", function () {
                    const selectedDeptId = this.value;
                    console.log("Selected departmentId:", selectedDeptId);

                    fetch(baseUrl, {
                        method: "POST",
                        headers: {"Content-Type": "application/x-www-form-urlencoded"},
                        body: "ajax=true&departmentId=" + encodeURIComponent(selectedDeptId)
                    })
                            .then(response => response.text())
                            .then(text => {
                                console.log("Response text from server:", text);
                                managerSelect.innerHTML = '<option value="">-- Không có --</option>';
                                const lines = text.trim().split("\n");
                                lines.forEach(line => {
                                    const parts = line.split("|");
                                    if (parts.length === 2) {
                                        const option = document.createElement("option");
                                        option.value = parts[0];
                                        option.textContent = parts[1];
                                        managerSelect.appendChild(option);
                                    }
                                });
                            })
                            .catch(error => {
                                console.error("Lỗi khi lấy quản lý:", error);
                            });
                });
            });
        </script>
    </body>
</html>
