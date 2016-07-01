<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
  <head>
    <title>
      Test du webservice Visu Stock.
    </title>
    <link rel="stylesheet" href="css/stock.css" type="text/css" media="screen" />
    <c:set var="js" value="${pageContext.request.contextPath}/javascripts" />
    <script src="${js}/jquery.js" type="text/javascript" >
    </script>
    <script src="${js}/stock.js" type="text/javascript" >
    </script>
  </head>
  <body>
    <h1>Test du webservice Visu Stock.</h1>
    <div id="sv_wstest_data">
      <form id="sv_wstest_form" action="" method="post">
        <table class="sv_wstest_table">
          <tr>
          <td class="sv_wstest_label"><label for="sv_wstest_storeId">Magasin:&nbsp;</label></td>
          <td><input id="sv_wstest_storeId" type="text" size="35" /></td>
          </tr><tr>
          <td class="sv_wstest_label"><label for="sv_wstest_productId">Produit:&nbsp;</label></td>
          <td><input id="sv_wstest_productId" type="text" size="35" /></td>
          </tr><tr>
          <td class="sv_wstest_label"><label for="sv_wstest_quantity">Quantit&eacute;e:&nbsp;</label></td>
          <td><input id="sv_wstest_quantity" type="text" size="35" /></td>
          </tr><tr>
		  <td colspan="2"><hr/></td>
		  </tr><tr>
          <td class="sv_wstest_label">&nbsp;</td>
          <td>
            <input type="radio" id="sv_wstest_channel_web" name="sv_wstest_channel" value="web" checked="checked">Web</input><br />
            <input type="radio" id="sv_wstest_channel_svi" name="sv_wstest_channel" value="svi">SVI</input>
          </td>
          </tr><tr>
		  <td colspan="2"><hr/></td>
		  </tr><tr>
          <td>&nbsp;</td><td><input id="sv_wstest_getstatus" type="button" value="Consulter disponibilit&eacute;" onclick="javascript:getStoreStatus('${pageContext.request.contextPath}');"/></td>
          </tr>
        </table>
      </form>
	  <div id="sv_wstest_url_text"></div>
	  <div id="sv_wstest_response_text"></div>
      <br />
      <a href="${pageContext.request.contextPath}/svTestByPostalCode.jsp">Test du webservice Code Postal</a>
    </div>
  </body>
</html>