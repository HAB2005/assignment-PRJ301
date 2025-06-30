<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
    <head>
        <title>Create Leave Request</title>
        <style>
            body {
                font-family: 'Segoe UI', sans-serif;
                background-color: #f4f4f9;
                padding: 40px;
            }

            h2 {
                text-align: center;
                color: #333;
            }

            form {
                max-width: 500px;
                margin: 0 auto;
                background: white;
                padding: 30px;
                border-radius: 12px;
                box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            }

            label {
                font-weight: bold;
                color: #444;
            }

            input[type="date"],
            select,
            textarea {
                width: 100%;
                padding: 10px 12px;
                margin-top: 6px;
                margin-bottom: 12px;
                border: 1px solid #ccc;
                border-radius: 8px;
                box-sizing: border-box;
                transition: border-color 0.3s;
            }

            input:focus,
            select:focus,
            textarea:focus {
                border-color: #6b46c1;
                outline: none;
            }

            textarea {
                resize: vertical;
            }

            button {
                background-color: #6b46c1;
                color: white;
                padding: 12px 20px;
                border: none;
                border-radius: 999px;
                cursor: pointer;
                font-weight: bold;
                width: 100%;
                margin-top: 10px;
                transition: background-color 0.3s;
            }

            button:hover {
                background-color: #5a2aa5;
            }

            .info {
                max-width: 500px;
                margin: 0 auto 20px;
                background: white;
                padding: 20px;
                border-radius: 12px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
            }

            .info p {
                margin: 6px 0;
                color: #333;
            }

            small {
                display: block;
                margin-top: -8px;
                margin-bottom: 8px;
            }

            .success {
                color: green;
                text-align: center;
                font-weight: bold;
            }

            .back-link {
                display: block;
                text-align: center;
                margin-top: 30px;
                text-decoration: none;
                font-weight: bold;
                color: #6b46c1;
            }

            .back-link:hover {
                text-decoration: underline;
            }

            input[type="text"].date-picker {
                width: 100%;
                padding: 12px 14px;
                border: 1px solid #ccc;
                border-radius: 10px;
                font-size: 14px;
                font-family: 'Segoe UI', sans-serif;
                color: #333;
                box-sizing: border-box;
                transition: border-color 0.3s, box-shadow 0.3s;
                background-color: #fff;
                background-image: url('data:image/svg+xml;utf8,<svg fill="gray" height="20" viewBox="0 0 24 24" width="20" xmlns="http://www.w3.org/2000/svg"><path d="M19 4h-1V2h-2v2H8V2H6v2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V9h14v11z"/></svg>');
                background-repeat: no-repeat;
                background-position: right 12px center;
                background-size: 18px 18px;
                cursor: pointer;
            }

            input[type="text"].date-picker:focus {
                border-color: #6b46c1;
                box-shadow: 0 0 0 3px rgba(107, 70, 193, 0.2);
                outline: none;
            }

            small {
                display: block;
                margin-top: 4px;
                color: red;
                font-size: 12px;
            }

            input.date-picker:invalid {
                border-color: red;
                box-shadow: 0 0 0 2px rgba(255, 0, 0, 0.2);
            }

            input.date-picker:valid {
                border-color: #ccc;
            }

        </style>
        <!-- CSS theme đẹp -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/themes/material_blue.css">

        <!-- Flatpickr core -->
        <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>

    </head>
    <body>

        <h2>📝 Tạo Yêu Cầu Nghỉ Phép</h2>

        <div class="info">
            <p><strong>Tên:</strong> <c:out value="${sessionScope.user.fullName}" /></p>
            <p><strong>Phòng ban:</strong> <c:out value="${sessionScope.department.departmentName}" /></p>
            <p><strong>Vai trò:</strong> ${roles[0].roleName}</p>
        </div>

        <form action="create_leave_request" method="post">
            <label for="fromDate">Từ ngày:</label>
            <input type="text" id="fromDate" name="fromDate" required placeholder="Chọn ngày bắt đầu" class="date-picker">
            <small id="fromDateError" style="color:red;"></small>

            <label for="toDate">Đến ngày:</label>
            <input type="text" id="toDate" name="toDate" required placeholder="Chọn ngày kết thúc" class="date-picker">
            <small id="toDateError" style="color:red;"></small>

            <label for="leaveType">Loại nghỉ phép:</label>
            <select name="leaveTypeId" id="leaveType" required>
                <c:forEach var="type" items="${leaveTypes}">
                    <option value="${type.leaveTypeId}">${type.typeName}</option>
                </c:forEach>
            </select>

            <label for="reason">Lý do:</label>
            <textarea id="reason" name="reason" rows="4" placeholder="Nhập lý do nghỉ..."></textarea>

            <c:if test="${not empty sessionScope.successMessage}">
                <p class="success">${sessionScope.successMessage}</p>
                <c:remove var="successMessage" scope="session"/>
            </c:if>

            <button type="submit">📤 Gửi yêu cầu</button>
        </form>


        <!-- 🔙 Back to Menu -->
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
                    fromInput.value = "";
                }
                validateToDate();
            }

            function validateToDate() {
                toError.textContent = "";
                const fromDate = new Date(fromInput.value);
                const toDate = new Date(toInput.value);

                if (fromInput.value && toInput.value && toDate < fromDate) {
                    toError.textContent = "❌ Ngày kết thúc không được trước ngày bắt đầu.";
                    toInput.value = "";
                }
            }

            fromInput.addEventListener("change", validateFromDate);
            toInput.addEventListener("change", validateToDate);
        </script>

        <script>
            flatpickr("#fromDate", {
                minDate: "today",
                dateFormat: "Y-m-d",
                onChange: function (selectedDates, dateStr, instance) {
                    // Reset ngày đến nếu nhỏ hơn
                    const toPicker = flatpickr("#toDate");
                    toPicker.set('minDate', dateStr);
                }
            });

            flatpickr("#toDate", {
                minDate: "today",
                dateFormat: "Y-m-d"
            });
        </script>

        <script>
            document.querySelector("form").addEventListener("submit", function (e) {
                const fromDate = document.getElementById("fromDate");
                const toDate = document.getElementById("toDate");
                let valid = true;

                if (!fromDate.value) {
                    document.getElementById("fromDateError").textContent = "❌ Vui lòng chọn ngày bắt đầu.";
                    valid = false;
                }

                if (!toDate.value) {
                    document.getElementById("toDateError").textContent = "❌ Vui lòng chọn ngày kết thúc.";
                    valid = false;
                }

                if (!valid) {
                    e.preventDefault(); // Ngăn submit nếu thiếu
                }
            });

            document.getElementById("fromDate").addEventListener("input", () => {
                document.getElementById("fromDateError").textContent = "";
            });

            document.getElementById("toDate").addEventListener("input", () => {
                document.getElementById("toDateError").textContent = "";
            });
        </script>


    </body>
</html>
