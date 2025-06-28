<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
    <head>
        <title>Lịch nghỉ phép cá nhân</title>
        <script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/index.global.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/locales-all.global.min.js"></script>

        <style>
            body {
                font-family: sans-serif;
                background: #f4f4f4;
                padding: 20px;
            }

            h2, h3 {
                color: #333;
            }

            #calendar {
                background: white;
                padding: 20px;
                border-radius: 8px;
                max-width: 1000px;
                margin: 30px auto;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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

            .status-approved {
                color: green;
            }
            .status-pending {
                color: orange;
            }
            .status-denied  {
                color: red;
            }

            .top-bar {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin: 20px 0 10px;
            }

            .top-bar input {
                padding: 6px;
                width: 250px;
                border: 1px solid #ccc;
                border-radius: 4px;
            }

            .top-bar a {
                padding: 8px 12px;
                background: #007bff;
                color: white;
                text-decoration: none;
                border-radius: 4px;
            }

            .top-bar a:hover {
                background: #0056b3;
            }

            /* FullCalendar custom event styles */
            .fc-event-approved {
                background-color: #28a745 !important; /* xanh lá */
                border-color: #28a745 !important;
                color: black !important;
            }

            .fc-event-pending {
                background-color: #fff3cd;
                border-color: #856404;
                color: black !important;
            }

            .fc-event-denied {
                background-color: #dc3545 !important; /* đỏ */
                border-color: #dc3545 !important;
                color: black !important;
            }

            .fc-event .fc-event-title {
                white-space: normal !important;
                font-size: 0.85rem;
            }

            .fc-daygrid-day {
                background-color: inherit !important;
            }

            .fc .fc-event-title,
            .fc .fc-event-main {
                color: black !important;
                white-space: normal !important;
                font-size: 0.9rem;
            }
        </style>
    </head>

    <body>
        <h2>Agenda của tôi</h2>
        <p>Xem lịch nghỉ và trạng thái đơn xin nghỉ phép của bạn.</p>

        <!-- LỊCH -->
        <div id="calendar"></div>

        <!-- DỮ LIỆU ẨN -->
        <div id="requestData" style="display:none;">
            <c:forEach var="r" items="${myRequests}">
                <div 
                    data-start="${r.fromDate}" 
                    data-end="${r.toDate}" 
                    data-type="${r.leaveType.typeName}" 
                    data-status="${r.status}" 
                    data-reason="${fn:escapeXml(r.reason)}">
                </div>
            </c:forEach>
        </div>

        <script>
            function getNextDate(dateStr) {
            const date = new Date(dateStr);
            date.setDate(date.getDate() + 1);
            return date.toISOString().split('T')[0];
            }

            document.addEventListener('DOMContentLoaded', function () {
            const calendarEl = document.getElementById('calendar');
            const calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
                    locale: 'vi',
                    firstDay: 1,
                    headerToolbar: {
                    left: 'prev',
                            center: 'title',
                            right: 'next'
                    },
                    events: [
            <c:forEach var="r" items="${myRequests}" varStatus="loop">
                    {
                    title: '${r.leaveType.typeName}: ${fn:escapeXml(r.reason)}',
                                        start: '${r.fromDate}',
                                        end: getNextDate('${r.toDate}'),
                                        className: 'fc-event-${fn:toLowerCase(r.status)}',
                                        extendedProps: {
                                        status: '${r.status}',
                                                reason: '${fn:escapeXml(r.reason)}'
                                        }
                                }<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
                                ]
                        });
                        calendar.render();
                        });
                        function filterTable(keyword) {
                        const input = keyword.toLowerCase();
                        const rows = document.querySelectorAll("#requestTable tbody tr");
                        rows.forEach(row => {
                        const text = row.innerText.toLowerCase();
                        row.style.display = text.includes(input) ? "" : "none";
                        });
                        }
        </script>

        <!-- DANH SÁCH ĐƠN -->
        <h3>Danh sách đơn xin nghỉ phép</h3>
        <c:if test="${not empty roles}">
            <c:set var="role" value="${roles[0].roleName}" />
            <c:set var="rolePath" value="${fn:toLowerCase(fn:replace(role, ' ', '_'))}" />
            <div class="top-bar">
                <input type="text" placeholder="Tìm theo loại nghỉ hoặc ngày..." onkeyup="filterTable(this.value)">
                <a href="${pageContext.request.contextPath}/${rolePath}/create_leave_request">+ Tạo đơn mới</a>
            </div>
        </c:if>

        <table id="requestTable">
            <thead>
                <tr>
                    <th>STT</th>
                    <th>Từ ngày</th>
                    <th>Đến ngày</th>
                    <th>Loại nghỉ</th>
                    <th>Lý do</th>
                    <th>Trạng thái</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="r" items="${myRequests}" varStatus="loop">
                    <tr>
                        <td>${loop.index + 1}</td>
                        <td><fmt:formatDate value="${r.fromDate}" pattern="dd/MM/yyyy"/></td>
                        <td><fmt:formatDate value="${r.toDate}" pattern="dd/MM/yyyy"/></td>
                        <td>${r.leaveType.typeName}</td>
                        <td>${r.reason}</td>
                        <td class="status-${fn:toLowerCase(r.status)}">${r.status}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <c:if test="${not empty roles}">
            <c:set var="role" value="${roles[0].roleName}" />        
            <c:set var="rolePath" value="${fn:toLowerCase(fn:replace(role, ' ', '_'))}" />
            <a class="back-link" href="${pageContext.request.contextPath}/${rolePath}/menu">⬅ Quay lại Menu</a>
        </c:if>
    </body>
</html>
