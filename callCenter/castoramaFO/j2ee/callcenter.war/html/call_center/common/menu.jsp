<dsp:page>
<dsp:importbean bean="/atg/userprofiling/Profile"/>


<dsp:setvalue bean="Profile.actionUtilisateur"
				value="${pageContext.request.requestURI}?${pageContext.request.queryString}" />
<!-- MENU -->
<TABLE WIDTH="600" BORDER="0" CELLSPACING="0" CELLPADDING="0" align="center">
    <tr bgcolor="#FFDE63"> 
      <td width="1"><img src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="10"><img src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="288"><img src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="288"><img src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="10"><img src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="1"><img src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></td>
    </tr>
	<TR> 
      <TD WIDTH="1" bgcolor="#FFDE63"><img src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER"></TD>
      <TD WIDTH="288" VALIGN="TOP" class="texte" ALIGN="center" height="30">
	  	<dsp:include page="../user/menu_profile.jsp"/>
      </TD>      
      <td WIDTH="288" VALIGN="TOP" class="texte" ALIGN="center" height="30">
	  	<dsp:include page="../order/menu_order.jsp"/>
	  </td>      
      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER">&nbsp;</TD>
      <TD WIDTH="1" ALIGN="CENTER" bgcolor="#FFDE63"><img src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
	 <tr bgcolor="#FFDE63"> 
      <td width="1"><img src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="10"><img src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="288"><img src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="288"><img src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="10"><img src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="1"><img src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></td>
    </tr>
  </TABLE>  
<!-- END -->

</dsp:page>