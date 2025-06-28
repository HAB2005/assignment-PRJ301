<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
    <head>
        <title>Create Leave Request</title>
        <style>
            .back-link {
                margin-top: 20px;
                display: inline-block;
            }
        </style>
    </head>
    <body>
        <h2>Create Leave Request</h2>

        <!-- Thông tin người dùng -->
        <p><strong>Tên:</strong> <c:out value="${sessionScope.user.fullName}" /></p>
        <p><strong>Phòng ban:</strong> <c:out value="${sessionScope.department.departmentName}" /></p>
        <p><strong>Vai trò:</strong> ${roles[0].roleName}</p>

        <form action="create_leave_request" method="post">
            <label for="fromDate">Từ ngày:</label><br>
            <input type="date" id="fromDate" name="fromDate" required><br>
            <small id="fromDateError" style="color:red;"></small><br><br>

            <label for="toDate">Đến ngày:</label><br>
            <input type="date" id="toDate" name="toDate" required><br>
            <small id="toDateError" style="color:red;"></small><br><br>

            <label for="leaveType">Loại nghỉ phép:</label><br>
            <select name="leaveTypeId" id="leaveType" required>
                <c:forEach var="type" items="${leaveTypes}">
                    <option value="${type.leaveTypeId}">${type.typeName}</option>
                </c:forEach>
            </select><br><br>


            <label for="reason">Lý do:</label><br>
            <textarea id="reason" name="reason" rows="4" cols="50"></textarea><br><br>

            <c:if test="${not empty sessionScope.successMessage}">
                <p style="color: green;"><strong>${sessionScope.successMessage}</strong></p>
                        <c:remove var="successMessage" scope="session"/>
                    </c:if>


            <button type="submit">Gửi yêu cầu</button>
        </form>

        <br>

        <!-- Nút Back to Menu -->
        <c:if test="${not empty roles}">
            <c:set var="role" value="${roles[0].roleName}" />        
            <c:set var="rolePath" value="${fn:toLowerCase(fn:replace(role, ' ', '_'))}" />
            <a class="back-link" href="${pageContext.request.contextPath}/${rolePath}/menu">⬅ Quay lại Menu</a>
        </c:if>

        <script>
            const fromInput = document.getElementById("fromDate");
            const toInput = document.getElementById("toDate");
            const fromError = document.getElementById("fromDateError");
            const toError = document.getElementById("toDateError");

            function validateFromDate() {
                fromError.textContent = "";
                const fromDate = new Date(fromInput.value);
                const today = new Date();
                today.setHours(0, 0, 0, 0);

                if (fromInput.value && fromDate < today) {
                    fromError.textContent = "❌ Ngày bắt đầu phải từ hôm nay trở đi.";
                    fromInput.value = ""; // Xoá lựa chọn sai
                }

                // Gọi lại validateToDate vì toDate có thể phụ thuộc vào fromDate
                validateToDate();
            }

            function validateToDate() {
                toError.textContent = "";
                const fromDate = new Date(fromInput.value);
                const toDate = new Date(toInput.value);

                if (fromInput.value && toInput.value && toDate < fromDate) {
                    toError.textContent = "❌ Ngày kết thúc không được trước ngày bắt đầu.";
                    toInput.value = ""; // Xoá lựa chọn sai
                }
            }

            fromInput.addEventListener("change", validateFromDate);
            toInput.addEventListener("change", validateToDate);
        </script>


    </body>
</html>