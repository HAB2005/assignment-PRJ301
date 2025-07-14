<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
    <head>
        <title>Menu</title>
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

            ul {
                list-style-type: none;
                padding: 0;
                max-width: 400px;
                margin: 30px auto;
            }

            ul li {
                background: #fff;
                margin: 10px 0;
                padding: 15px 20px;
                border-radius: 12px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.05);
                transition: background 0.3s;
            }

            ul li:hover {
                background: #f0eaff;
            }

            ul li a {
                text-decoration: none;
                color: #5a2aa5;
                font-weight: 600;
            }

            a[href$="login"] {
                display: block;
                text-align: center;
                margin-top: 30px;
                color: #c0392b;
                font-weight: bold;
                text-decoration: none;
            }

            /* 🔔 Notification icon */
            #notification-icon {
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 999;
            }

            #notification-icon button {
                position: relative;
                background-color: #6b46c1;
                color: white;
                padding: 10px 20px;
                border-radius: 999px;
                border: none;
                font-weight: bold;
                cursor: pointer;
                transition: background 0.3s;
            }

            #notification-icon button:hover {
                background-color: #5a2aa5;
            }

            #notification-icon span {
                position: absolute;
                top: -5px;
                right: -5px;
                background-color: red;
                color: white;
                font-size: 12px;
                font-weight: bold;
                padding: 2px 6px;
                border-radius: 999px;
            }

            /* 🪟 Notification panel */
            #notification-panel {
                position: fixed;
                top: 70px;
                right: 20px;
                width: 380px;
                max-height: 500px;
                background: white;
                border: 1px solid #ccc;
                border-radius: 12px;
                box-shadow: 0 8px 20px rgba(0,0,0,0.15);
                overflow-y: auto;
                opacity: 0;
                transform: translateY(-20px);
                transition: all 0.4s ease;
                visibility: hidden;
                z-index: 998;
            }

            #notification-popup::-webkit-scrollbar {
                width: 6px;
            }

            #notification-popup::-webkit-scrollbar-thumb {
                background-color: #ccc;
                border-radius: 10px;
            }

            #notification-popup::-webkit-scrollbar-track {
                background: transparent;
            }

            #notification-content div {
                padding: 16px 20px;
                border-bottom: 1px solid #eee;
                cursor: pointer;
                transition: background 0.3s;
            }

            #notification-content div:hover {
                background: #f9f7fd;
            }

            #notification-content .title {
                font-weight: bold;
                color: #5a2aa5;
            }

            #notification-content .message {
                font-size: 14px;
                color: #555;
            }

            #notification-content .timestamp {
                font-size: 12px;
                color: #999;
                margin-top: 5px;
            }

            ul {
                list-style-type: none;
                padding: 0;
                max-width: 400px;
                margin: 30px auto;
            }

            ul li {
                margin: 10px 0;
                border-radius: 12px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.05);
                overflow: hidden;
                background: #fff;
                transition: transform 0.2s, background 0.3s;
            }

            ul li:hover {
                background: #f0eaff;
                transform: translateY(-1px);
            }

            ul li a {
                display: flex;                /* dùng flex để kiểm soát chiều cao */
                align-items: center;          /* canh giữa nội dung */
                padding: 14px 20px;
                text-decoration: none;
                color: #5a2aa5;
                font-weight: 600;
                font-size: 15px;
            }

            ul li {
                border: 1px solid #eee;
            }

            #notification-panel.show {
                opacity: 1;
                transform: translateY(0);
                visibility: visible;
            }
        </style>
    </head>
    <body>
        <h2>Welcome, <c:out value="${sessionScope.user.fullName}" /></h2>

        <!-- 🔔 Notification button -->
        <div id="notification-icon">
            <button onclick="toggleNotifications()">
                🔔 Notification
                <c:if test="${unreadCount > 0}">
                    <span id="notification-count">${unreadCount}</span>
                </c:if>
            </button>
        </div>

        <!-- 🪟 Notification Panel -->
        <div id="notification-panel">
            <div id="notification-content">
                <c:choose>
                    <c:when test="${empty notifications}">
                        <div style="padding: 20px; text-align: center; color: #777;">Không có thông báo mới</div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="n" items="${notifications}">
                            <div class="notification-item ${n.read ? '' : 'unread'}"
                                 style="padding: 10px; border-bottom: 1px solid #ccc; cursor: pointer;"
                                 onclick="location.href = '#'"> <!-- TODO: cập nhật action nếu có -->
                                <div class="message" style="font-weight: bold;">${n.message}</div>
                                <div class="timestamp" style="font-size: 12px; color: gray;">
                                    <fmt:formatDate value="${n.createdAt}" pattern="HH:mm dd/MM/yyyy" />
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- 📋 Menu -->
        <ul>
            <c:if test="${not empty features && not empty featureLinks}">
                <c:forEach var="feature" items="${features}">
                    <c:set var="link" value="${featureLinks[feature.featureName]}" />
                    <li>
                        <a href="${pageContext.request.contextPath}/${link}">
                            <c:out value="${feature.featureName}" />
                        </a>
                    </li>
                </c:forEach>
            </c:if>
        </ul>

        <!-- 🚪 Logout -->
        <a href="${pageContext.request.contextPath}/login">Đăng xuất</a>

        <script>
            let panelVisible = false;

            function toggleNotifications() {
                const panel = document.getElementById("notification-panel");
                const countSpan = document.getElementById("notification-count");

                const isVisible = panel.style.visibility === "visible";

                // Toggle panel
                if (isVisible) {
                    panel.style.opacity = "0";
                    panel.style.transform = "translateY(-20px)";
                    panel.style.visibility = "hidden";
                } else {
                    panel.style.opacity = "1";
                    panel.style.transform = "translateY(0)";
                    panel.style.visibility = "visible";

                    // Gửi yêu cầu đánh dấu là đã đọc
                    fetch('<c:url value="/api/mark-read" />', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    })
                            .then(response => {
                                if (response.ok) {
                                    // ✅ Ẩn số thông báo sau khi đánh dấu
                                    if (countSpan) {
                                        countSpan.style.display = "none";
                                    }
                                } else {
                                    console.error("Không đánh dấu được thông báo");
                                }
                            })
                            .catch(err => console.error("Lỗi fetch:", err));
                }
            }

            // Đóng panel nếu click ra ngoài
            document.addEventListener("click", function (event) {
                const panel = document.getElementById("notification-panel");
                const icon = document.getElementById("notification-icon");
                if (!panel.contains(event.target) && !icon.contains(event.target)) {
                    panel.style.opacity = "0";
                    panel.style.transform = "translateY(-20px)";
                    panel.style.visibility = "hidden";
                }
            });

            // Đóng panel nếu click bên ngoài
            document.addEventListener("click", function (event) {
                const panel = document.getElementById("notification-panel");
                const icon = document.getElementById("notification-icon");

                if (!panel.contains(event.target) && !icon.contains(event.target)) {
                    panel.classList.remove("show");
                }
            });

            document.addEventListener("click", function (event) {
                const panel = document.getElementById("notification-panel");
                const icon = document.getElementById("notification-icon");

                if (!panel.contains(event.target) && !icon.contains(event.target)) {
                    panel.style.opacity = "0";
                    panel.style.transform = "translateY(-20px)";
                    panel.style.visibility = "hidden";
                    panelVisible = false;
                }
            });
        </script>
    </body>
</html>
