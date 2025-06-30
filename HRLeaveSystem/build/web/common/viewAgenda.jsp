<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
    <head>
        <title>Agenda của: ${selectedUser.fullName}</title>
        <script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/index.global.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/locales-all.global.min.js"></script>
        <link rel="stylesheet" href="your-calendar-style.css"> <!-- Gắn file CSS nếu cần -->
        <style>
            /* KHỐI BỌC TOÀN BỘ PHẦN NGOÀI LỊCH */
            .leave-section {
                padding: 30px 20px;
                max-width: 1100px;
                margin: 30px auto;
                background-color: #fff;
                border-radius: 16px;
                box-shadow: 0 6px 16px rgba(0,0,0,0.05);
                box-sizing: border-box;
            }

            /* Tiêu đề & mô tả */
            .leave-section h3 {
                text-align: center;
                color: #4a4a4a;
                margin-bottom: 20px;
                font-size: 20px;
            }

            .leave-section p {
                text-align: center;
                margin-bottom: 30px;
                color: #666;
            }

            /* Thanh tìm kiếm + nút tạo đơn */
            .leave-section .top-bar {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin: 20px auto;
                max-width: 100%;
                gap: 12px;
                flex-wrap: wrap;
            }

            .leave-section #searchInput {
                padding: 10px 14px;
                flex-grow: 1;
                border: 1px solid #ccc;
                border-radius: 10px;
                font-size: 14px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.05);
                transition: border-color 0.3s;
            }

            .leave-section #searchInput:focus {
                outline: none;
                border-color: #6b46c1;
            }

            .leave-section .btn-create {
                background-color: #6b46c1;
                color: white;
                padding: 10px 20px;
                border-radius: 10px;
                text-decoration: none;
                font-weight: bold;
                transition: background-color 0.3s;
                white-space: nowrap;
            }

            .leave-section .btn-create:hover {
                background-color: #5a2aa5;
            }

            /* Bảng đơn nghỉ phép */
            .leave-section table {
                width: 100%;
                margin: 0 auto 30px;
                background: white;
                border-collapse: collapse;
                border-radius: 12px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.06);
                overflow: hidden;
            }

            .leave-section th {
                background-color: #6b46c1;
                color: white;
                font-weight: bold;
                padding: 14px;
                text-align: center;
            }

            .leave-section td {
                padding: 12px 10px;
                border-bottom: 1px solid #eee;
                text-align: center;
                vertical-align: middle;
                word-break: break-word;
            }

            .leave-section tr:nth-child(even) {
                background-color: #fafafa;
            }

            .leave-section tr:hover {
                background-color: #f3f0ff;
            }

            /* Trạng thái */
            .leave-section .status-approved {
                color: #28a745;
                font-weight: bold;
            }

            .leave-section .status-pending {
                color: #ffc107;
                font-weight: bold;
            }

            .leave-section .status-denied {
                color: #dc3545;
                font-weight: bold;
            }

            /* Nút duyệt / từ chối */
            .leave-section button {
                padding: 6px 12px;
                margin: 2px;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-weight: bold;
                font-size: 13px;
                transition: background-color 0.3s;
            }

            .leave-section button[name="action"][value="Approved"] {
                background-color: #28a745;
                color: white;
            }

            .leave-section button[name="action"][value="Approved"]:hover {
                background-color: #218838;
            }

            .leave-section button[name="action"][value="Rejected"] {
                background-color: #dc3545;
                color: white;
            }

            .leave-section button[name="action"][value="Rejected"]:hover {
                background-color: #c82333;
            }

            /* Ghi chú phê duyệt */
            .leave-section input[name="comment"] {
                padding: 6px 10px;
                width: 90%;
                border: 1px solid #ccc;
                border-radius: 8px;
                font-size: 13px;
            }

            /* Nút quay lại */
            .leave-section a.back-link {
                display: inline-block;
                margin: 10px;
                padding: 10px 18px;
                background-color: #f0f0f0;
                color: #333;
                text-decoration: none;
                border-radius: 8px;
                transition: background 0.3s ease;
                font-weight: 500;
            }

            .leave-section a.back-link:hover {
                background-color: #ddd;
            }

            .leave-section .back-wrapper {
                text-align: center;
                margin-top: 40px;
            }

            #calendar {
                background: #fff;
                padding: 20px;
                border-radius: 16px;
                max-width: 1100px;
                margin: 40px auto;
                box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
                overflow: hidden;
            }

            #calendar .fc-col-header thead tr th:first-child {
                border-top-left-radius: 12px;
            }
            #calendar .fc-col-header thead tr th:last-child {
                border-top-right-radius: 12px;
            }

            /* Bo tròn góc dưới trái/phải của dòng cuối cùng */
            #calendar .fc-daygrid-body .fc-daygrid-day:last-child {
                border-bottom-right-radius: 12px;
            }
            #calendar .fc-daygrid-body tr:last-child .fc-daygrid-day:first-child {
                border-bottom-left-radius: 12px;
            }


            /* Tiêu đề tháng + nút điều hướng */
            #calendar .fc-toolbar-title {
                font-size: 1.8rem;
                font-weight: bold;
                color: #333;
            }

            #calendar .fc-button {
                background-color: #fff;
                border: 1px solid #ccc;
                color: #333;
                font-weight: 600;
                border-radius: 6px;
                padding: 6px 12px;
                transition: background-color 0.3s;
            }

            #calendar .fc-button:hover {
                background-color: #eee;
            }

            /* Header ngày trong tuần (T2 -> CN) */
            #calendar .fc-col-header-cell {
                background-color: #6b46c1;
                color: white;
                font-weight: 500;
                border: 1px solid #ddd;
                text-align: center;
                padding: 10px 0;
            }

            #calendar .fc-col-header-cell-cushion {
                font-size: 14px;
                display: block;
                width: 100%;
            }

            /* Ô ngày */
            #calendar .fc-daygrid-day-frame {
                padding: 6px;
                height: 100px;
                border: 1px solid #eee;
                box-sizing: border-box;
            }

            #calendar .fc-daygrid-day {
                vertical-align: top;
            }

            /* Nền ngày hôm nay */
            #calendar .fc-day-today {
                background: #fdfdfd !important;
            }

            /* Sự kiện */
            #calendar .fc-event {
                background-color: #28a745;
                color: white;
                font-size: 13px;
                padding: 4px 6px;
                border-radius: 6px;
                white-space: normal;
                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                margin-top: 4px;
                text-align: left;
                font-weight: 500;
            }

            /* Màu sự kiện theo trạng thái */
            #calendar .fc-event-approved {
                background-color: #28a745 !important;
            }
            #calendar .fc-event-pending {
                background-color: #ffc107 !important;
                color: black !important;
            }
            #calendar .fc-event-denied {
                background-color: #dc3545 !important;
            }

            /* Fix lỗi dư khoảng trắng ở cuối lịch */
            #calendar .fc-daygrid-body,
            #calendar .fc-daygrid-body-unbalanced,
            #calendar .fc-daygrid-body-natural {
                border-bottom-left-radius: 16px;
                border-bottom-right-radius: 16px;
                overflow: hidden !important;
                margin-bottom: -4px !important;
                padding-bottom: 0 !important;
                height: auto !important;
                min-height: unset !important;
            }

            /* Tắt viền + overflow thừa */
            #calendar .fc-scrollgrid,
            #calendar .fc-scrollgrid-section,
            #calendar .fc-scrollgrid-sync-table,
            #calendar .fc-scrollgrid-liquid,
            #calendar .fc-scrollgrid-section-body {
                border: none !important;
            }

            #calendar .fc-scrollgrid {
                width: 100%;
                table-layout: fixed;
                border-collapse: collapse;
            }

            #calendar .fc-scroller-harness {
                overflow: visible !important;
                height: auto !important;
            }

            .status-rejected {
                color: #dc3545;
                font-weight: bold;
            }

            .fc-event-approved {
                background-color: #28a745 !important;
                color: #fff !important;
            }
            .fc-event-pending {
                background-color: #ffc107 !important;
                color: #000 !important;
            }
        </style>
    </head>
    <body>
        <h2 style="font-size: 28px; font-weight: bold; text-align: center; color: #4a4a4a; margin-top: 30px; margin-bottom: 10px;">
            Agenda của: ${selectedUser.fullName}
        </h2>

        <p style="font-size: 16px; text-align: center; color: #666; margin-bottom: 30px;">
            Xem lịch nghỉ và trạng thái đơn xin nghỉ phép của nhân viên này.
        </p>


        <div id="calendar"></div>

        <div class="leave-section">

            <h3>Danh sách đơn xin nghỉ phép</h3>

            <c:if test="${not empty roles}">
                <c:set var="role" value="${roles[0].roleName}" />
                <c:set var="rolePath" value="${fn:toLowerCase(fn:replace(role, ' ', '_'))}" />
                <div class="top-bar">
                    <input type="text" id="searchInput" placeholder="Tìm theo loại nghỉ hoặc ngày..." onkeyup="filterTable()">
                    <a href="${pageContext.request.contextPath}/${rolePath}/create_leave_request" class="btn-create">+ Tạo đơn mới</a>
                </div>
            </c:if>

            <c:if test="${not empty selectedUserRequests}">
                <table id="requestTable">
                    <thead>
                        <tr>
                            <th>STT</th>
                            <th>Từ ngày</th>
                            <th>Đến ngày</th>
                            <th>Loại nghỉ</th>
                            <th>Lý do</th>
                            <th>Trạng thái</th>
                            <th>Ghi chú phê duyệt</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="r" items="${selectedUserRequests}" varStatus="loop">
                            <c:choose>
                                <c:when test="${canApprove && r.status == 'Pending'}">
                                <form method="post" action="${pageContext.request.contextPath}/${currentFeatureLink}">
                                    <input type="hidden" name="requestId" value="${r.requestId}" />
                                    <input type="hidden" name="userId" value="${selectedUser.userId}" />
                                    <tr>
                                        <td>${loop.index + 1}</td>
                                        <td><fmt:formatDate value="${r.fromDate}" pattern="dd/MM/yyyy" /></td>
                                        <td><fmt:formatDate value="${r.toDate}" pattern="dd/MM/yyyy" /></td>
                                        <td>${r.leaveType.typeName}</td>
                                        <td>${r.reason}</td>
                                        <td class="status-${fn:toLowerCase(r.status)}">
                                            <button type="submit" name="action" value="Approved">✔ Duyệt</button>
                                            <button type="submit" name="action" value="Rejected">✖ Từ chối</button>
                                        </td>
                                        <td><input type="text" name="comment" placeholder="Ghi chú" /></td>
                                    </tr>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td>${loop.index + 1}</td>
                                    <td><fmt:formatDate value="${r.fromDate}" pattern="dd/MM/yyyy" /></td>
                                    <td><fmt:formatDate value="${r.toDate}" pattern="dd/MM/yyyy" /></td>
                                    <td>${r.leaveType.typeName}</td>
                                    <td>${r.reason}</td>
                                    <td class="status-${fn:toLowerCase(r.status)}">${r.status}</td>
                                    <td>${requestIdToComment[r.requestId]}</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>

            <c:if test="${empty selectedUserRequests}">
                <p>Người này chưa có đơn xin nghỉ nào.</p>
            </c:if>

            <c:choose>
                <c:when test="${fn:endsWith(currentFeatureLink, 'view_own_agenda')}">
                    <a class="back-link" href="${pageContext.request.contextPath}/${rolePath}/menu">
                        ⬅ Quay lại Menu
                    </a>
                </c:when>
                <c:otherwise>
                    <div class="back-wrapper">
                        <a class="back-link" href="${pageContext.request.contextPath}/${currentFeatureLink}">
                            ⬅ Quay lại danh sách cấp dưới
                        </a>
                        <a class="back-link" href="${pageContext.request.contextPath}/${rolePath}/menu">
                            ⬅ Quay lại Menu
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>

            <script>
                function getNextDate(dateStr) {
                if (!dateStr) return null;
                const date = new Date(dateStr);
                if (isNaN(date)) return null;
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
                        height: '800px',
                        contentHeight: 'auto',
                        eventDisplay: 'block',
                        events: [
                <c:forEach var="r" items="${selectedUserRequests}" varStatus="loop">
                    <c:set var="safeEnd" value="${empty r.toDate ? '' : r.toDate}" />
                    <c:if test="${fn:toLowerCase(r.status) != 'rejected'}">
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
                    </c:if>
                </c:forEach>
                                    ]
                            });
                            calendar.render();
                            });
            </script>
    </body>
</html>
