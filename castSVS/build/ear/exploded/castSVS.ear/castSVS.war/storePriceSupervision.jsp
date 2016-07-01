<%-- 
    Document   : stockAvailabilitySupervision
    Created on : Nov 11, 2010, 8:06:33 PM
    Author     : Yahor But-Husaim
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="currentTime" class="java.util.Date"/>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Supervision du Flux Visu de Stock </title>
    <link rel="stylesheet" type="text/css" href="css/stock.css"/>
</head>
<body>
<div class="stockAvailabilityBody">
    <div class="stockAvailabilityHeader">
        Supervision du flux Prix
    </div>
    <div class="stockAvailabilityContent">
        <c:choose>
            <c:when test="${supervisionManager.dayDataPrice}">
                <div class="stockAvailabilityContentBlock">
                    <div class="stockAvailabilitySubheader"></div>
                    <table class="stockAvailabilityTable">
                        <tr class="stockAvailabilityTableHeaderRow">
                            <td>Store ID</td>
                            <td>Date derniere int&eacute;gration</td>
                            <td>Status</td>
                            <td>Result</td>
                        </tr>

                        <c:forEach var="record" items="${syncJournalRecords}" varStatus="st">
                            <c:choose>
                                <c:when test="${(st.index)%2 eq 0}">
                                    <c:set var="trclass" value="stockAvailabilityTableRow"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="trclass" value="stockAvailabilityTableOddRow"/>
                                </c:otherwise>
                            </c:choose>
                            <tr class="<c:out value="${trclass}"/>">
                                <td><fmt:formatNumber minIntegerDigits="4" groupingUsed="false"
                                                      value="${record.storeId}"/></td>
                                <td><fmt:formatDate value="${record.lastUpdate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                <td>
                                    <c:if test="${record.priceStatus != 3}">
                                        <c:out value="${record.priceStatus}"/>
                                    </c:if>
                                </td>
                                <td>
                                    <c:if test="${record.priceStatus == 0}">
                                        OK
                                        <c:if test="${st.index == 0}">
                                            <c:set var="totalResultPrice" scope="page" value="OK"/>
                                        </c:if>
                                    </c:if>
                                    <c:if test="${record.priceStatus == 1}">
                                        KO
                                        <c:set var="totalResultPrice" scope="page" value="KO"/>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                    <div class="supervisionFooter">
                        <div class="date">
                            DATE : <fmt:formatDate value="${currentTime}" pattern="dd/MM/yyyy HH:mm:ss"/>
                        </div>
                        <div class="result">
                            RESULTAT : <c:out value="${totalResultPrice}"/>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                Sorry. Error occured.
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
