<%--
    Document   : stockMonitoring
    Created on : Nov 16, 2010, 2:06:33 PM
    Author     : Yahor But-Husaim
--%>

<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="fr_FR"/>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Suivi des flux C&C</title>
    <link rel="stylesheet" type="text/css" href="css/stock.css"/>
</head>
<body class="stockMonitoringBody">
<c:choose>
    <c:when test="${supervisionManager.monthData}">
        <form action="" method="post" name="selectForm">
            <input type="hidden" name="chosenMonth" value=""/>

            <div class="stockMonitoringHeader">
                Suivi des flux C&C <fmt:formatDate type="date" pattern="MMMMM yyyy" value="${monthToView}"/> :
                <select class="stockMonitoringMonthSelect"
                        onchange="javascript: document.selectForm.elements['chosenMonth'].value=this.options[this.selectedIndex].title;document.selectForm.submit();">
                    <option selected="selected">Mois</option>
                    <c:forEach var="month" items="${monthes}">
                        <option title="<fmt:formatDate type="date" pattern="MM/yyyy" value="${month}" />">
                            <fmt:formatDate type="date" pattern="MM/yyyy" value="${month}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
        </form>
        <div class="stockMonitoringContent">
            <table class="stockMonitoringTable">
                <tr class="stockMonitoringTableHeaderCategories">
                    <td></td>
                    <td colspan="2" class="stockMonitoringTableHeaderCell">Stock Flow</td>
                </tr>
                <tr class="stockMonitoringTableHeaderRow">
                    <td class="date">Date</td>
                    <td>Store OK (nbr)</td>
                    <td>Store KO (nbr)</td>
                </tr>
                <c:forEach var="record" items="${supervisionRecords}" varStatus="st">
                    <c:choose>
                        <c:when test="${(st.index)%2 eq 0}">
                            <c:set var="trclass" value="stockMonitoringTableRow"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="trclass" value="stockMonitoringTableOddRow"/>
                        </c:otherwise>
                    </c:choose>
                    <tr class="<c:out value="${trclass}"/>">
                        <td>
                            <fmt:formatDate var="chosenDayString" value="${record.date}" type="date"
                                            pattern="dd/MM/yyyy"/>
                            <a href="storeStockSupervision.jsp?chosenDay=${chosenDayString}">
                                <c:out value="${chosenDayString}"/>
                            </a>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${record.stockOkCount < 0}">
                                    -
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${record.stockOkCount}"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${record.stockKoCount < 0}">
                                    -
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${record.stockKoCount}"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                <tr class="stockMonitoringTableEmptyRow">
                    <td><br/></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr class="stockMonitoringTableHeaderRow">
                    <td>Moyenne sur le Mois</td>
                    <td>
                        <c:choose>
                            <c:when test="${averageStockOK < 0}">
                                -
                            </c:when>
                            <c:otherwise>
                                <fmt:formatNumber type="percent" value="${averageStockOK}" minIntegerDigits="2"
                                                  minFractionDigits="2" maxFractionDigits="2"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${averageStockKO < 0}">
                                -
                            </c:when>
                            <c:otherwise>
                                <fmt:formatNumber type="percent" value="${averageStockKO}" minIntegerDigits="2"
                                                  minFractionDigits="2" maxFractionDigits="2"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>
        </div>
    </c:when>
    <c:otherwise>
        Sorry. Error occured.
    </c:otherwise>
</c:choose>
</body>
</html>
