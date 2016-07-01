<dsp:page>
<dsp:importbean bean="/atg/userprofiling/Profile"/>


<dsp:setvalue bean="Profile.actionUtilisateur"
				value="${pageContext.request.requestURI}?${pageContext.request.queryString}" />
<!-- MENU -->
<TABLE WIDTH="600" BORDER="0" CELLSPACING="0" CELLPADDING="0" align="center">
    <tr bgcolor="#FFDE63"> 
      <td width="1"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="10"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="192"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="192"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	  <td width="192"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="10"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="1"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
    </tr>
	<%--<TR> 
      <TD WIDTH="1" bgcolor="#FFDE63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER"></TD>
      <TD WIDTH="192" VALIGN="TOP" class="texte" ALIGN="center" height="30">
	  	<dsp:include page="/../user/menu_profile.jsp"/>
      </TD>      
      <td WIDTH="192" VALIGN="TOP" class="texte" ALIGN="center" height="30">
	  	<dsp:include page="../order/menu_order.jsp"/>
	  </td>      
      <td WIDTH="192" VALIGN="TOP" class="texte" ALIGN="center" height="30">
	  				<table border="0" cellspacing="0">
	  					<TR>
							<TD class="marques">
								PERSONNEL :
							</TD>
						</TR>
	  					<tr>
	  						<td>
	  							<IMG src="<%=request.getContextPath()%>/html/img/flecheb_retrait.gif" border=0>&nbsp;
					             <A class=moncasto href="<%=request.getContextPath()%>/html/gestionUsers/common/changePwdAction.jsp?who=<dsp:valueof bean="Profile.repositoryId"/>&externe=true">              
					               Modifier votre mot de passe
					             </A>
					        </td>
					    </tr>
					</table>
	  </td>	  
      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER">&nbsp;</TD>
      <TD WIDTH="1" ALIGN="CENTER" bgcolor="#FFDE63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>--%>
    <TR> 
      <TD WIDTH="1" bgcolor="#FFDE63">
      	<img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1">
      </TD>
      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER">
      	&nbsp;
      </TD>
      <TD COLSPAN="3" WIDTH="576" VALIGN="TOP" class="texte" ALIGN="center" height="30">
	  	<dsp:include page="./../includes/inc-menu-choix-dates.jsp"/>
      </TD> 
      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER">
      	&nbsp;
      </TD>
      <TD WIDTH="1" ALIGN="CENTER" bgcolor="#FFDE63">
      	<img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1">
      </TD>
    </TR>
	 <tr bgcolor="#FFDE63"> 
      <td width="1"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="10"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="192"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="192"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	  <td width="192"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="10"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
      <td width="1"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
    </tr>
  </TABLE>  
<!-- END -->

</dsp:page>