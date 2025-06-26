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
    </body>

</html>