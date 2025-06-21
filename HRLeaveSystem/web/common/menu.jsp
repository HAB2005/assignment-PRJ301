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
        </style>
    </head>
    <body>
        <h2>Welcome, <c:out value="${sessionScope.user.fullName}" /></h2>
        <ul>
            <c:if test="${not empty roles}">
                <c:set var="role" value="${roles[0].roleName}" />
                <c:set var="rolePath" value="${fn:toLowerCase(fn:replace(role, ' ', '_'))}" />
                <c:forEach var="feature" items="${features}">
                    <c:set var="featurePath" value="${fn:toLowerCase(fn:replace(feature.featureName, ' ', '_'))}" />
                    <li><a href="${pageContext.request.contextPath}/${rolePath}/${featurePath}">${feature.featureName}</a></li>
                    </c:forEach>
                </c:if>
        </ul>
        <a href="${pageContext.request.contextPath}/login">Log out</a>
    </body>
</html>