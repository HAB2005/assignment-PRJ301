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

            .toggle-container {
                display: flex;
                flex-direction: column;
                gap: 10px;
            }

            .toggle-button {
                display: block;               /* Đảm bảo nó chiếm nguyên dòng để căn giữa được */
                margin: 10px auto;            /* ✅ Căn giữa ngang */
                width: 400px;             /* ✅ Giữ độ rộng hợp lý */
                background-color: #6b46c1;
                color: white;
                padding: 6px 14px;
                font-size: 15px;
                border: none;
                border-radius: 8px;
                cursor: pointer;
                text-align: left;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
                transition: background-color 0.2s ease;
            }

            .toggle-button:hover {
                background-color: #553c9a;
            }

            .toggle-content {
                overflow: hidden;
                max-height: 0;
                opacity: 0;
                display: block;
                transition: max-height 0.4s ease, opacity 0.4s ease;
            }

            .toggle-content.show {
                /*                max-height: 9999px;*/
                opacity: 1;
            }

            .sub-table {
                width: 100%;
                border-collapse: collapse;
                margin: 10px 0 20px;
            }

            .sub-table th, .sub-table td {
                border: 1px solid #ddd;
                padding: 8px;
                text-align: left;
            }

            /* ✅ màu cho dòng tiêu đề bảng */
            .sub-table th {
                background-color: #6b46c1;
                color: white;
                font-weight: bold;
            }
        </style>
    </head>
    <body>
        <h2>Danh sách cấp dưới</h2>
        <input type="text" id="searchInput" placeholder="🔍 Tìm theo tên..." onkeyup="filterSubordinates()" style="margin-bottom: 10px; padding: 5px; width: 300px;" />

        <c:if test="${fn:trim(roles[0].roleName) eq 'General Manager'}">
            <div class="toggle-container">
                <c:forEach var="dept" items="${departments}" varStatus="status">
                    <button class="toggle-button" type="button" onclick="toggleVisibility('dept${status.index}')">
                        ▶ Phòng: ${dept.departmentName}
                    </button>
                    <div id="dept${status.index}" class="toggle-content">
                        <table class="sub-table">
                            <thead>
                                <tr>
                                    <th>STT</th>
                                    <th>Họ tên</th>
                                    <th>Email</th>
                                    <th>Vai trò</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:set var="count" value="1" />
                                <c:forEach var="u" items="${subordinates}">
                                    <c:if test="${u.department.departmentId == dept.departmentId}">
                                        <tr>
                                            <td>${count}</td>
                                            <td>${u.fullName}</td>
                                            <td>${u.email}</td>
                                            <td>${u.roles[0].roleName}</td>
                                            <td>
                                                <form method="post" action="${pageContext.request.contextPath}/${currentFeatureLink}">
                                                    <input type="hidden" name="userId" value="${u.userId}" />
                                                    <button class="btn" type="submit">Xem agenda</button>
                                                </form>
                                            </td>
                                        </tr>
                                        <c:set var="count" value="${count + 1}" />
                                    </c:if>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:forEach>
            </div>
        </c:if>


        <c:if test="${(fn:trim(roles[0].roleName) eq 'Department Head' or not empty subordinates) 
                      and fn:trim(roles[0].roleName) ne 'General Manager'}">

              <c:forEach var="group" items="${roleGroups}">
                  <button class="toggle-button" type="button" onclick="toggleVisibility('${group}')">
                      ▶ Nhóm: ${group}
                  </button>
                  <div id="${group}" class="toggle-content">
                      <table class="sub-table">
                          <thead>
                              <tr>
                                  <th>STT</th>
                                  <th>Họ tên</th>
                                  <th>Email</th>
                                  <th>Vai trò</th>
                                  <th>Hành động</th>
                              </tr>
                          </thead>
                          <tbody>
                              <c:set var="count" value="1" />
                              <c:forEach var="u" items="${subordinates}">
                                  <c:if test="${u.roles[0].roleName eq group}">
                                      <tr>
                                          <td>${count}</td>
                                          <td>${u.fullName}</td>
                                          <td>${u.email}</td>
                                          <td>${u.roles[0].roleName}</td>
                                          <td>
                                              <form method="post" action="${pageContext.request.contextPath}/${currentFeatureLink}">
                                                  <input type="hidden" name="userId" value="${u.userId}" />
                                                  <button class="btn" type="submit">Xem agenda</button>
                                              </form>
                                          </td>
                                      </tr>
                                      <c:set var="count" value="${count + 1}" />
                                  </c:if>
                              </c:forEach>
                          </tbody>
                      </table>
                  </div>
              </c:forEach>
        </c:if>


        <c:if test="${not empty roles}">
            <c:set var="role" value="${roles[0].roleName}" />        
            <c:set var="rolePath" value="${fn:toLowerCase(fn:replace(role, ' ', '_'))}" />
            <a class="back-link" href="${pageContext.request.contextPath}/${rolePath}/menu">⬅ Quay lại Menu</a>
        </c:if>

        <script>
            function filterSubordinates() {
                const input = document.getElementById("searchInput").value.toLowerCase();
                const sections = document.querySelectorAll(".toggle-content");

                // ✅ Nếu input trống: đóng toàn bộ + hiện tất cả dòng
                if (!input) {
                    sections.forEach(section => {
                        const rows = section.querySelectorAll("tbody tr");

                        rows.forEach(row => {
                            row.style.display = "";
                        });

                        section.classList.remove("show");
                        section.style.maxHeight = "0px";
                        section.style.opacity = "0";

                        // Đổi icon ▼ thành ▶
                        const btn = section.previousElementSibling;
                        if (btn && btn.innerHTML.includes("▼")) {
                            btn.innerHTML = btn.innerHTML.replace("▼", "▶");
                        }
                    });
                    return;
                }

                // ✅ Ngược lại: xử lý tìm kiếm như cũ
                sections.forEach(section => {
                    const rows = section.querySelectorAll("tbody tr");
                    let matchFound = false;

                    rows.forEach(row => {
                        const nameCell = row.querySelector("td:nth-child(2)");
                        const name = nameCell ? nameCell.textContent.toLowerCase() : "";
                        const match = name.includes(input);
                        row.style.display = match ? "" : "none";
                        if (match)
                            matchFound = true;
                    });

                    if (matchFound) {
                        section.classList.add("show");
                        section.style.maxHeight = section.scrollHeight + "px";
                        section.style.opacity = "1";

                        const btn = section.previousElementSibling;
                        if (btn && btn.innerHTML.includes("▶")) {
                            btn.innerHTML = btn.innerHTML.replace("▶", "▼");
                        }
                    } else {
                        section.classList.remove("show");
                        section.style.maxHeight = "0px";
                        section.style.opacity = "0";

                        const btn = section.previousElementSibling;
                        if (btn && btn.innerHTML.includes("▼")) {
                            btn.innerHTML = btn.innerHTML.replace("▼", "▶");
                        }
                    }
                });
            }
        </script>

        <script>
            function toggleVisibility(id) {
                const el = document.getElementById(id);
                const btn = el.previousElementSibling;

                if (el.classList.contains("show")) {
                    // Đóng: thu gọn từ từ
                    el.style.maxHeight = el.scrollHeight + "px"; // set về chiều cao hiện tại
                    requestAnimationFrame(() => {
                        el.style.maxHeight = "0px";
                        el.classList.remove("show");
                        el.style.opacity = "0";
                    });
                    btn.innerHTML = btn.innerHTML.replace("▼", "▶");
                } else {
                    // Mở: mở từ từ theo đúng chiều cao thật
                    el.classList.add("show");
                    el.style.opacity = "1";
                    el.style.maxHeight = el.scrollHeight + "px";
                    btn.innerHTML = btn.innerHTML.replace("▶", "▼");
                }
            }
        </script>

    </body>
</html>
