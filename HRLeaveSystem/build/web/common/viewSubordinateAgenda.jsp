<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
    <head>
        <title>Agenda của: ${selectedUser.fullName}</title>
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
            .status-denied {
                color: red;
            }
            button {
                margin: 0 2px;
                padding: 4px 8px;
            }

            /* Ngăn không cho tô màu cả ô ngày */
            .fc-daygrid-day {
                background-color: inherit !important;
            }

            /* Màu cho sự kiện */
            .fc-event-approved {
                background-color: #d4edda;
                border-color: #155724;
                color: #155724 !important;
            }

            .fc-event-pending {
                background-color: #fff3cd;
                border-color: #856404;
                color: #333 !important;
            }

            .fc-event-denied {
                background-color: #f8d7da;
                border-color: #721c24;
                color: #721c24 !important;
            }

            .fc-event .fc-event-title {
                white-space: normal !important;
                font-size: 0.85rem;
            }

        </style>
    </head>
    <body>

        <h2>Agenda của: ${selectedUser.fullName}</h2>
        <p>Xem lịch nghỉ và trạng thái đơn xin nghỉ phép của nhân viên này.</p>

        <div id="calendar"></div>

        <script>
            function getNextDate(dateStr) {
                if (!dateStr)
                    return null;
                const date = new Date(dateStr);
                if (isNaN(date))
                    return null;
                date.setDate(date.getDate() + 1);
                return date.toISOString().split('T')[0];
            }

            document.addEventListener('DOMContentLoaded', function () {
                const calendar = new FullCalendar.Calendar(document.getElementById('calendar'), {
                initialView: 'dayGridMonth',
                        locale: 'vi',
                        firstDay: 1,
                        headerToolbar: {
                        left: 'prev',
                                center: 'title',
                                right: 'next'
                        },
                        events: [
            <c:forEach var="r" items="${selectedUserRequests}" varStatus="loop">
                <c:set var="safeEnd" value="${empty r.toDate ? '' : r.toDate}" />
                        {
                        title: '${r.leaveType.typeName}: ${fn:escapeXml(r.reason)}',
                                                start: '${r.fromDate}',
                                                end: '${safeEnd}' !== '' ? getNextDate('${safeEnd}') : null,
                                                className: 'fc-event-${fn:toLowerCase(r.status)}',
                                        extendedProps: {
                                            status: '${r.status}',
                                                    reason: '${fn:escapeXml(r.reason)}'
                                            }
                                        }<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
                                        ],
                                eventClick: function (info) {
                                    alert(
                                            'Loại nghỉ: ' + info.event.title + '\n' +
                                            'Trạng thái: ' + info.event.extendedProps.status + '\n' +
                                            'Từ: ' + info.event.start.toLocaleDateString('vi-VN') + '\n' +
                                            'Đến: ' + (info.event.end ? info.event.end.toLocaleDateString('vi-VN') : '') + '\n' +
                                            'Lý do: ' + info.event.extendedProps.reason
                                            );
                                    }
                                }
                                );
                                calendar.render();
                            });
        </script>

        <h3>Danh sách đơn xin nghỉ phép</h3>
        <table>
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
                <c:forEach var="r" items="${selectedUserRequests}" varStatus="loop">
                    <tr>
                        <td>${loop.index + 1}</td>
                        <td><fmt:formatDate value="${r.fromDate}" pattern="dd/MM/yyyy"/></td>
                        <td><fmt:formatDate value="${r.toDate}" pattern="dd/MM/yyyy"/></td>
                        <td>${r.leaveType.typeName}</td>
                        <td>${r.reason}</td>
                        <td class="status-${fn:toLowerCase(r.status)}">
                            ${r.status}
                            <c:if test="${canApprove && r.status == 'Pending'}">
                                <form method="post" action="${pageContext.request.contextPath}/agenda/approve" style="display:inline;">
                                    <input type="hidden" name="requestId" value="${r.requestId}" />
                                    <input type="hidden" name="userId" value="${selectedUser.userId}" />
                                    <button type="submit" name="action" value="Approved">✔ Duyệt</button>
                                    <button type="submit" name="action" value="Rejected">✖ Từ chối</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <a href="${pageContext.request.contextPath}/subordinates">⬅ Quay lại danh sách cấp dưới</a>
    </body>
</html>
