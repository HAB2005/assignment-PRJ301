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

            .fc-daygrid-day {
                background-color: inherit !important;
            }

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
                                ]
                        });
                        calendar.render();
                        });
        </script>

        <h3>Danh sách đơn xin nghỉ phép</h3>
        <c:set var="role" value="${roles[0].roleName}" />
        <c:set var="rolePath" value="${fn:toLowerCase(fn:replace(role, ' ', '_'))}" />
        <c:if test="${not empty selectedUserRequests}">
            <c:if test="${empty rolePath && not empty roles}">
                <c:set var="role" value="${roles[0].roleName}" />
                <c:set var="rolePath" value="${fn:toLowerCase(fn:replace(role, ' ', '_'))}" />
            </c:if>

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
                                <c:choose>
                                    <c:when test="${canApprove && r.status == 'Pending'}">
                                        <form method="post" action="${pageContext.request.contextPath}/${currentFeatureLink}" style="display:inline;">
                                            <input type="hidden" name="requestId" value="${r.requestId}" />
                                            <input type="hidden" name="userId" value="${selectedUser.userId}" />
                                            <button type="submit" name="action" value="Approved">✔ Duyệt</button>
                                            <button type="submit" name="action" value="Rejected">✖ Từ chối</button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        ${r.status}
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

        <c:if test="${empty selectedUserRequests}">
            <p>Người này chưa có đơn xin nghỉ nào.</p>
        </c:if>

        <a href="${pageContext.request.contextPath}/${currentFeatureLink}">⬅ Quay lại danh sách cấp dưới</a>
    </body>
</html>
