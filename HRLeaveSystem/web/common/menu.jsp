<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
    <head>
        <title>Menu</title>
        <style>
            ul {
                list-style-type: none;
                padding: 0;
            }
            ul li {
                margin: 10px 0;
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
        </style>
    </head>
    <body>
        <h2>Welcome, <c:out value="${sessionScope.user.fullName}" /></h2>
        <!-- 🔔 Nút chuông thông báo -->
        <div id="notification-icon" style="position: fixed; top: 20px; right: 20px; z-index: 999;">
            <button onclick="toggleNotifications()"
                    style="position: relative; background-color: #6b46c1; color: white; padding: 10px 20px; border-radius: 999px; border: none; font-weight: bold; cursor: pointer;">
                🔔 Notification
                <c:if test="${not empty notifications}">
                    <span style="position: absolute; top: -5px; right: -5px; background-color: red; color: white;
                          font-size: 12px; font-weight: bold; padding: 2px 6px; border-radius: 999px;">
                        ${notifications.size()}
                    </span>
                </c:if>
            </button>
        </div>

        <!-- 🪟 Panel thông báo -->
        <div id="notification-panel"
             style="position: fixed; top: 70px; right: 20px; width: 380px; max-height: 500px; background: white; border: 1px solid #ccc;
             border-radius: 12px; box-shadow: 0 8px 20px rgba(0,0,0,0.15); overflow-y: auto;
             opacity: 0; transform: translateY(-20px); transition: all 0.4s ease; visibility: hidden; z-index: 998;">
            <div id="notification-content">
                <c:choose>
                    <c:when test="${empty notifications}">
                        <div style="padding: 20px; text-align: center; color: #777;">Không có thông báo mới</div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="n" items="${notifications}">
                            <div onclick="location.href = '${pageContext.request.contextPath}/${n.actionLink}'"
                                 style="padding: 16px 20px; border-bottom: 1px solid #eee; cursor: pointer;
                                 transition: background 0.3s;">
                                <div style="font-weight: bold; color: #5a2aa5;">${n.title}</div>
                                <div style="font-size: 14px; color: #555;">${n.message}</div>
                                <div style="font-size: 12px; color: #999; margin-top: 5px;">${n.timestamp}</div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

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
        <a href="${pageContext.request.contextPath}/login">Log out</a>

        <script>
            let panelVisible = false;

            function toggleNotifications() {
                const panel = document.getElementById("notification-panel");

                if (panelVisible) {
                    // Ẩn
                    panel.style.opacity = "0";
                    panel.style.transform = "translateY(-20px)";
                    panel.style.visibility = "hidden";
                } else {
                    // Hiện
                    panel.style.opacity = "1";
                    panel.style.transform = "translateY(0)";
                    panel.style.visibility = "visible";
                }

                panelVisible = !panelVisible;
            }

            // Ẩn khi click ra ngoài
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