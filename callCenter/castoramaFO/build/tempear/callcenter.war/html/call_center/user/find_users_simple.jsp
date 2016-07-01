<dsp:page>


<dsp:importbean bean="/castorama/RechercheUserFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>



<dsp:droplet name="CastoGetPrivileges">
 <dsp:param name="requis" value="commerce-csr-profiles-privilege"/>
 <dsp:param name="profile" bean="Profile"/>
 <dsp:oparam name="accesAutorise">
 
  <TABLE WIDTH="760" BORDER="0" CELLSPACING="0" CELLPADDING="0" align="center">
  	<dsp:droplet name="/atg/dynamo/droplet/Switch">
	<dsp:param name="value" bean="RechercheUserFormHandler.formError"/>
	<dsp:oparam name="true">
	 <TR VALIGN="top" align=center>
	 <TD></TD>
		<TD WIDTH="760" ALIGN="center"><dsp:include page="../common/erreur.jsp"/><br><br></TD>
	</TR>
	
	</dsp:oparam>
	</dsp:droplet>
  <TR VALIGN="top">
		<TD></TD>
		<TD WIDTH="760" ALIGN="center">
 	 		<span class=prix>Rechercher un compte internaute par :</span>
	 		<p>
	  		<dsp:form action="find_users_page.jsp" method="POST">
				<dsp:input type="hidden" bean="RechercheUserFormHandler.submitSuccessURL" value="searchresults.jsp"/>
							
			<!-- debut -->
			<TABLE WIDTH="600" BORDER="0" CELLSPACING="0" CELLPADDING="0" align="center">
		    <tr bgcolor="#FFDE63"> 
		      <td width="600"><img src="../../img/1pixel.gif" width="1" height="1"></td>      
		    </tr>
			</table>
			<TABLE WIDTH="600" BORDER="0" CELLSPACING="0" CELLPADDING="0" align="center">
		    <TR> 
		      <TD WIDTH="1" bgcolor="#FFDE63"><img src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER"></TD>
		      <TD WIDTH="289" VALIGN="MIDDLE" class="texte" ALIGN="center" height="30">Email Login (v&eacute;rifier majuscule/minuscule) 
		        :&nbsp; 
		      </TD>
			  <td WIDTH="289" VALIGN="MIDDLE" class="texte" ALIGN="center" height="30">
			  
			  
			  
			  <dsp:input type="text" size="25" maxlength="150" iclass="moncasto" bean="RechercheUserFormHandler.login"/>&nbsp;
			  <dsp:input type="image" align="absmiddle" src="../../img/valider.gif" border="0" value="1" bean="RechercheUserFormHandler.submit1"/></td>
		      
		      
		      
		      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER">&nbsp;</TD>
		      <TD WIDTH="1" ALIGN="CENTER" bgcolor="#FFDE63"><img src="../../img/1pixel.gif" width="1" height="1"></TD>
		    </TR>
			</table>
			<TABLE WIDTH="600" BORDER="0" CELLSPACING="0" CELLPADDING="0" align="center">
		    <tr bgcolor="#FFDE63"> 
		      <td width="600"><img src="../../img/1pixel.gif" width="1" height="1"></td>      
		    </tr>
			</table>
			<br>
			<TABLE WIDTH="600" BORDER="0" CELLSPACING="0" CELLPADDING="0" align="center">
		    <tr bgcolor="#FFDE63"> 
		      <td width="600"><img src="../../img/1pixel.gif" width="1" height="1"></td>      
		    </tr>
			</table>
			<TABLE WIDTH="600" BORDER="0" CELLSPACING="0" CELLPADDING="0" align="center">
			<TR> 
		      <TD WIDTH="1" bgcolor="#FFDE63"><img src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER"></TD>
		      <TD WIDTH="289" VALIGN="MIDDLE" class="texte" ALIGN="center">Num&eacute;ro Client 
		        :&nbsp; 
		      </TD>
			   <td WIDTH="289" VALIGN="MIDDLE" class="texte" ALIGN="center" height="30">
			   
			   
			   
			   <dsp:input type="text" size="25" maxlength="150" iclass="moncasto" bean="RechercheUserFormHandler.id"/>&nbsp;
			   <dsp:input type="image" align="absmiddle" src="../../img/valider.gif" border="0" value="2" bean="RechercheUserFormHandler.submit2"/></td>
		      
		      
		      
		      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER">&nbsp;</TD>
		      <TD WIDTH="1" ALIGN="CENTER" bgcolor="#FFDE63"><img src="../../img/1pixel.gif" width="1" height="1"></TD>
		    </TR>
			</table>
			
			<TABLE WIDTH="600" BORDER="0" CELLSPACING="0" CELLPADDING="0" align="center">
		    <tr bgcolor="#FFDE63"> 
		      <td width="600"><img src="../../img/1pixel.gif" width="1" height="1"></td>      
		    </tr>
			</table>
			<br>
			<TABLE WIDTH="600" BORDER="0" CELLSPACING="0" CELLPADDING="0" align="center">
		    <tr bgcolor="#FFDE63"> 
		      <td width="600"><img src="../../img/1pixel.gif" width="1" height="1"></td>      
		    </tr>
			</table>
			<TABLE WIDTH="600" BORDER="0" CELLSPACING="0" CELLPADDING="0" align="center">
			<TR> 
		      <TD WIDTH="1" bgcolor="#FFDE63"><img src="../img/1pixel.gif" width="1" height="1"></TD>
		      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER"></TD>
		      <TD WIDTH="289" VALIGN="MIDDLE" class="texte" ALIGN="center" height="30">Nom 
		        :&nbsp;   
		      </TD>
		      <td WIDTH="289" VALIGN="MIDDLE" class="texte" ALIGN="center" height="30">
		      
		      
		      <dsp:input type="text" size="25" maxlength="150" iclass="moncasto" bean="RechercheUserFormHandler.nom"/></td>
		      
		      
		      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER">&nbsp;</TD>
		      <TD WIDTH="1" ALIGN="CENTER" bgcolor="#FFDE63"><img src="../../img/1pixel.gif" width="1" height="1"></TD>
		    </TR>
			<TR> 
		      <TD WIDTH="1" bgcolor="#FFDE63"><img src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER"></TD>
		      
		      <TD WIDTH="289" VALIGN="MIDDLE" class="texte" ALIGN="center">Pr&eacute;nom 
			  	:&nbsp;
			  </TD>
			  <td WIDTH="289" VALIGN="MIDDLE" class="texte" ALIGN="center" height="30">
			  
			  
			  <dsp:input type="text" size="25" maxlength="150" iclass="moncasto" bean="RechercheUserFormHandler.prenom"/></td>
		      
		      
		      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER">&nbsp;</TD>
		      <TD WIDTH="1" ALIGN="CENTER" bgcolor="#FFDE63"><img src="../../img/1pixel.gif" width="1" height="1"></TD>
		    </TR>
			<TR> 
		      <TD WIDTH="1" bgcolor="#FFDE63"><img src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER"></TD>
		      <TD WIDTH="289" VALIGN="MIDDLE" class="texte" ALIGN="center" height="30">Code postal 
		        :&nbsp; 
		      </TD>
		      <td WIDTH="289" VALIGN="MIDDLE" class="texte" ALIGN="center" height="30">
		      
		      
		      <dsp:input type="text" size="25" maxlength="150" iclass="moncasto" bean="RechercheUserFormHandler.codePostal"/></td>
		      
		      
		      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER">&nbsp;</TD>
		      <TD WIDTH="1" ALIGN="CENTER" bgcolor="#FFDE63"><img src="../../img/1pixel.gif" width="1" height="1"></TD>
		    </TR>
			<TR> 
		      <TD WIDTH="1" bgcolor="#FFDE63"><img src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER"></TD>
		      
		      <TD WIDTH="289" VALIGN="MIDDLE" class="texte" ALIGN="center">Ville 
			  	:&nbsp;
			  </TD>
			  <td WIDTH="289" VALIGN="MIDDLE" class="texte" ALIGN="center" height="30">
			  
			  
			  <dsp:input type="text" size="25" maxlength="150" iclass="moncasto" bean="RechercheUserFormHandler.ville"/></td>
		      
		      
		      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER">&nbsp;</TD>
		      <TD WIDTH="1" ALIGN="CENTER" bgcolor="#FFDE63"><img src="../../img/1pixel.gif" width="1" height="1"></TD>
		    </TR>
			<TR> 
		      <TD WIDTH="1" bgcolor="#FFDE63"><img src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER"></TD>
		      
		      <TD WIDTH="289" VALIGN="MIDDLE" class="texte" ALIGN="center">&nbsp;</TD>
			  <td WIDTH="289" VALIGN="MIDDLE" class="texte" ALIGN="center" height="30">
			  
			  
			  <dsp:input type="image" align="absmiddle"  src="../../img/valider.gif" border="0" value="3" bean="RechercheUserFormHandler.submit"/></td>
		      
		      
		      <TD WIDTH="10" VALIGN="MIDDLE" class="texte" ALIGN="CENTER">&nbsp;</TD>
		      <TD WIDTH="1" ALIGN="CENTER" bgcolor="#FFDE63"><img src="../../img/1pixel.gif" width="1" height="1"></TD>
		    </TR>
			
			</table>
			<TABLE WIDTH="600" BORDER="0" CELLSPACING="0" CELLPADDING="0" align="center">
		    <tr bgcolor="#FFDE63"> 
		      <td width="600"><img src="../../img/1pixel.gif" width="1" height="1"></td>      
		    </tr>
			</table>
		  </TABLE>  
  <br>
  <!-- fin -->
	</dsp:form>

</dsp:oparam>
<dsp:oparam name="accesRefuse"></dsp:oparam>
</dsp:droplet>
<dsp:include page="../common/menuBas.jsp"/>


</dsp:page>
