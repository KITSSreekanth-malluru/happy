<dsp:page>

<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/atg/userprofiling/ProfileAdminFormHandler" />
<dsp:importbean bean="/castorama/CastoProfileAdminFormHandler" />
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>


<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
	<dsp:include page="../common/protocolChange.jsp">
		<dsp:param name="protocol" value="secure" />
	</dsp:include>
<script type="text/javascript" src="<c:out value="${beanImg.jsPath}"/>/fonctions.js"></script>
<script type="text/javascript">
<!--
var monthLength = new Array(31,28,31,30,31,30,31,31,30,31,30,31);

function checkDate()
{
	var dateNaissance = document.getElementById('dateOfBirth').value;

	if (dateNaissance.length > 10)
	{
		document.getElementById('dateOfBirth').value=dateNaissance.substring(0,9);
		alert('Date de naissance non valide');
		return false;
	}


	if (dateNaissance!= null && dateNaissance.length == 10) 
	{
			var day = dateNaissance.substring(0,2);
			var month = dateNaissance.substring(3,5); 
			var year = dateNaissance.substring(6);

			if(isNaN(day) || isNaN(month) || isNaN(year))	
			{
				document.getElementById('dateOfBirth').value=dateNaissance.substring(0,9);
				alert('Date de naissance non valide');
				return false;
			}
			else
			{
			
				if (!day || !month || !year)
				{
					
					document.getElementById('dateOfBirth').value=dateNaissance.substring(0,9);
					alert('Date de naissance non valide');
					return false;
				}
			
				if (year/4 == parseInt(year/4))
					monthLength[1] = 29;
			
				if (month > 12 || month < 1)
				{
					document.getElementById('dateOfBirth').value=dateNaissance.substring(0,9);
					alert('Date de naissance non valide');
					return false;
				}

				if (day > monthLength[month-1] || day < 1)
				{
					document.getElementById('dateOfBirth').value=dateNaissance.substring(0,9);
					alert('Date de naissance non valide');
					return false;
				}
			
				monthLength[1] = 28;
			
				var now = new Date();
				now = now.getTime(); //NN3
			
				var dateToCheck = new Date();
				dateToCheck.setYear(year);
				dateToCheck.setMonth(month-1);
				dateToCheck.setDate(day);
				var checkDate = dateToCheck.getTime();
			
				
			
				return true;
			}
		}
}
				
				
				
				//-->
			</script>

	<HTML>
	<TITLE>Castorama : Call center - Edit Basics</TITLE>
	<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<LINK rel="stylesheet" href="../../css/hp.css">
	<BODY bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
		marginheight="0">


			<dsp:droplet name="CastoGetPrivileges">
			    <dsp:param name="profile" bean="Profile"/>
			   	<dsp:param name="requis" value="commerce-csr-profiles-privilege"/>	
			   		   		
		   		<dsp:oparam name="accesAutorise">


	<dsp:droplet name="/atg/targeting/RepositoryLookup">
		<dsp:param name="repository" bean="/atg/userprofiling/ProfileAdapterRepository" />
		<dsp:param name="itemDescriptor" value="user" />
		<dsp:param name="id" param="id" />		
		<dsp:param name="elementName" value="profile"/>							
		<dsp:oparam name="output">
		
		
			<dsp:include page="../common/header.jsp">
				<dsp:param name="id" param="profile.repositoryId" />
				<dsp:param name="user" param="profile.login" />
			</dsp:include>
			<TABLE width="760" border="0" cellpadding="0" cellspacing="0"
				align="center">
				<TR>
					<TD align=center>
					<P><SPAN class=marques>&nbsp;Editer le profile de
					l'internaute : </SPAN><SPAN class=prix><dsp:valueof
						param="profile.login" /></SPAN>
					<P>
					</TD>
				</TR>
				<TR>
					<TD align=center>
					<P>&nbsp;<SPAN class=texteg><B>Bloc Profil :</B></SPAN>
					<P>
					</TD>
				</TR>
				<TR>
					<TD width="760" align="center">
					<%
				String l_strCommentaire = "Le bloc profil de l'internaute "+request.getParameter("profile.id")+ " a ete modifie";
			%> <dsp:form action="${pageContext.request.requestURI}" method="POST"
						name="formAdmin">
						<dsp:input type="hidden" bean="CastoProfileAdminFormHandler.type"
							value="Contact CallCenter" />
						<dsp:input type="hidden"
							bean="CastoProfileAdminFormHandler.action" value="Modification" />
						<dsp:input type="hidden"
							bean="CastoProfileAdminFormHandler.commentaire"
							value="l_strCommentaire" />
						<dsp:input type="hidden" bean="CastoProfileAdminFormHandler.ip"
							beanvalue="/atg/dynamo/servlet/pipeline/BrowserAttributes.remoteAddr" />
						<dsp:droplet name="/atg/dynamo/security/CurrentUser">
							<dsp:oparam name="output">
								<dsp:input type="hidden"
									bean="CastoProfileAdminFormHandler.nomLogin"
									paramvalue="username" />
							</dsp:oparam>
							<dsp:oparam name="empty"></dsp:oparam>
						</dsp:droplet>
						<dsp:input type="hidden" name="profilLastName"
							bean="CastoProfileAdminFormHandler.value.LastName"
							paramvalue="profile.LastName" />
						<dsp:input type="hidden" name="profilFirstName"
							bean="CastoProfileAdminFormHandler.value.FirstName"
							paramvalue="profile.FirstName" />
						<dsp:input type="hidden" name="civilite"
							bean="CastoProfileAdminFormHandler.value.civilite"
							paramvalue="profile.civilite" />
						
						
						
						
						<dsp:droplet name="/atg/dynamo/droplet/URLArgument">
							<dsp:param name="url" value="editprofileBasics.jsp" />
							<dsp:param name="argName" value="id" />
							<dsp:param name="argValue" param="profile.repositoryId" />
							<dsp:oparam name="output">
								<dsp:input type="hidden"
									bean="CastoProfileAdminFormHandler.updateErrorURL"
									beanvalue="/atg/dynamo/droplet/URLArgument.url" />
							</dsp:oparam>
						</dsp:droplet>
						
						<dsp:input type="hidden" bean="CastoProfileAdminFormHandler.updateErrorURL" beanvalue="/atg/dynamo/droplet/URLArgument.url" />
						
						
						
						<dsp:input type="hidden"
							bean="CastoProfileAdminFormHandler.deleteSuccessURL"
							value="user_deleted.jsp" />
						
						
						
						<dsp:droplet name="/atg/dynamo/droplet/URLArgument">
							<dsp:param name="url" value="editprofilePage.jsp" />
							<dsp:param name="argName" value="id" />
							<dsp:param name="argValue" param="profile.repositoryId" />
							<dsp:oparam name="output">
								<dsp:input type="hidden"
									bean="CastoProfileAdminFormHandler.updateSuccessURL"
									beanvalue="/atg/dynamo/droplet/URLArgument.url" />
							</dsp:oparam>
						</dsp:droplet>
						
						
						
						
						
						<dsp:input type="hidden"
							bean="CastoProfileAdminFormHandler.repositoryId" paramvalue="id" />
						<TABLE width="600" border="0" cellspacing="0" cellpadding="0"
							align="center">
							<TR>
								<TD><dsp:include page="./erreurs.jsp"/></TD>
							</TR>
						</TABLE>
						<BR>
						<TABLE width="600" border="0" cellspacing="0" cellpadding="0"
							align="center">
							<TR bgcolor="#FFDE63">
								<TD width="1"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="10"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="289"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="289"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="10"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="1"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
							</TR>
							<TR>
								<TD width="1" bgcolor="#FFDE63"><IMG
									src="../../img/1pixel.gif" width="1" height="1"></TD>
								<TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
								<dsp:droplet name="/atg/dynamo/droplet/Switch">
									<dsp:param name="value" param="profile.Civilite" />
									<dsp:oparam name="mr">
										<TD width="289" valign="MIDDLE" class="texte" align="center"
											height="30">Mr <dsp:input type="radio" checked="true"
											bean="ProfileFormHandler.value.civilite" required="true"
											value="Monsieur"
											onchange="window.document.formAdmin.civilite.value=this.value;" />
										</TD>
										<TD width="289" valign="MIDDLE" class="texte" align="center"
											height="30">Mme <dsp:input type="radio"
											bean="ProfileFormHandler.value.civilite" required="true"
											value="Madame"
											onchange="window.document.formAdmin.civilite.value=this.value;" />
										</TD>
										<TD width="289" valign="MIDDLE" class="texte" align="center"
											height="30">Mlle <dsp:input type="radio"
											bean="ProfileFormHandler.value.civilite" required="true"
											value="Mlle"
											onchange="window.document.formAdmin.civilite.value=this.value;" />
										</TD>
									</dsp:oparam>
									<dsp:oparam name="miss">
										<TD width="289" valign="MIDDLE" class="texte" align="center"
											height="30">Mr <dsp:input type="radio"
											bean="ProfileFormHandler.value.civilite" required="true"
											value="Monsieur"
											onchange="window.document.formAdmin.civilite.value=this.value;" />
										</TD>
										<TD width="289" valign="MIDDLE" class="texte" align="center"
											height="30">Mme <dsp:input type="radio"
											bean="ProfileFormHandler.value.civilite" required="true"
											value="Madame"
											onchange="window.document.formAdmin.civilite.value=this.value;" />
										</TD>
										<TD width="289" valign="MIDDLE" class="texte" align="center"
											height="30">Mlle <dsp:input type="radio" checked="true"
											bean="ProfileFormHandler.value.civilite" required="true"
											value="Mlle"
											onchange="window.document.formAdmin.civilite.value=this.value;" />
										</TD>
									</dsp:oparam>
									<dsp:oparam name="mrs">
										<TD width="289" valign="MIDDLE" class="texte" align="center"
											height="30">Mr <dsp:input type="radio"
											bean="ProfileFormHandler.value.civilite" required="true"
											value="Monsieur"
											onchange="window.document.formAdmin.civilite.value=this.value;" />
										</TD>
										<TD width="289" valign="MIDDLE" class="texte" align="center"
											height="30">Mme <dsp:input type="radio" checked="true"
											bean="ProfileFormHandler.value.civilite" required="true"
											value="Madame"
											onchange="window.document.formAdmin.civilite.value=this.value;" />
										</TD>
										<TD width="289" valign="MIDDLE" class="texte" align="center"
											height="30">Mlle <dsp:input type="radio"
											bean="ProfileFormHandler.value.civilite" required="true"
											value="Mlle"
											onchange="window.document.formAdmin.civilite.value=this.value;" />
										</TD>
									</dsp:oparam>
								</dsp:droplet>
								<TD width="289" valign="MIDDLE" class="texte" align="center">&nbsp;</TD>
								<TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
								<TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG
									src="../../img/1pixel.gif" width="1" height="1"></TD>
							</TR>
							<TR bgcolor="#FFDE63">
								<TD width="1"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="10"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="289"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="289"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="10"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="1"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
							</TR>
						</TABLE>
						<BR>
						<TABLE width="600" border="0" cellspacing="0" cellpadding="0"
							align="center">
							<TR bgcolor="#FFDE63">
								<TD width="1"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="10"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="289"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="289"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="10"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="1"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
							</TR>
							<TR>
								<TD width="1" bgcolor="#FFDE63"><IMG
									src="../../img/1pixel.gif" width="1" height="1"></TD>
								<TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
								<TD width="289" valign="MIDDLE" class="texte" align="center"
									height="30">Nom :&nbsp; <dsp:input iclass="moncasto"
									type="TEXT" size="30" maxsize="150"
									bean="CastoProfileAdminFormHandler.value.lastName"
									paramvalue="profile.LastName"
									onchange="window.document.formAdmin.profilLastName.value=this.value;" />
								</TD>
								<TD width="289" valign="MIDDLE" class="texte" align="center">Pr&eacute;nom
								:&nbsp; <dsp:input iclass="moncasto" type="TEXT" size="30"
									maxsize="100"
									bean="CastoProfileAdminFormHandler.value.firstName"
									paramvalue="profile.FirstName"
									onchange="window.document.formAdmin.profilFirstName.value=this.value;" />
								</TD>
								<TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
								<TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG
									src="../../img/1pixel.gif" width="1" height="1"></TD>
							</TR>
							<TR>
								<TD width="1" bgcolor="#FFDE63"><IMG
									src="../../img/1pixel.gif" width="1" height="1"></TD>
								<TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
								
								
								<TD width="289" valign="MIDDLE" class="texte" align="center" height="30">Date de naissance (jj/mm/aaaa) :&nbsp; 
									<dsp:input iclass="moncasto" type="TEXT" size="10" maxsize="10" id="dateOfBirth" name="dateOfBirth"
									 bean="CastoProfileAdminFormHandler.value.dateOfBirth" paramvalue="profile.dateOfBirth"
									  date="dd/MM/yyyy" onkeyup="checkDate();">
									  </dsp:input>
								</TD>
								<TD width="289" valign="MIDDLE" class="texte" align="center">&nbsp;
								</TD>
								<TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
								<TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG
									src="../../img/1pixel.gif" width="1" height="1"></TD>
							</TR>
							<TR bgcolor="#FFDE63">
								<TD width="1"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="10"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="289"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="289"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="10"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="1"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
							</TR>
						</TABLE>
						<BR>
						<TABLE width="600" border="0" cellspacing="0" cellpadding="0"
							align="center">
							<TR bgcolor="#FFDE63">
								<TD width="1"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="10"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="289"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="289"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="10"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="1"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
							</TR>
							<TR>
								<TD width="1" bgcolor="#FFDE63"><IMG
									src="../../img/1pixel.gif" width="1" height="1"></TD>
								<TD width="10" valign="MIDDLE" class="texte" align="center"></TD>
								<TD width="289" align="right" class="texte"><IMG
									src="../../img/1pixel.gif" width="1" height="1">Adresse
								:&nbsp;</TD>
								<TD width="289"><IMG src="../../img/1pixel.gif" width="1"
									height="1"> 
										
										
									<dsp:tomap var="adresse" param="profile.billingAddress"/>						
									<dsp:textarea cols="25" rows="3" bean="CastoProfileAdminFormHandler.value.billingAddress.address1"><c:out value="${adresse.address1}"/></dsp:textarea>

								</TD>
								<TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
								<TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG
									src="../../img/1pixel.gif" width="1" height="1"></TD>
							</TR>
							<TR>
								<TD width="1" bgcolor="#FFDE63"><IMG
									src="../../img/1pixel.gif" width="1" height="1"></TD>
								<TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
								<TD width="289" valign="MIDDLE" class="texte" align="center"
									height="30">Compl&eacute;ment d'adresse
								(&eacute;tage,entr&eacute;e,porte ...) :&nbsp; <dsp:input
									iclass="moncasto" type="TEXT" size="30" maxsize="150"
									bean="CastoProfileAdminFormHandler.value.billingAddress.address2"
									paramvalue="profile.billingAddress.address2" /></TD>
								<TD width="289" valign="MIDDLE" class="texte" align="center">Code
								postal :&nbsp; 
								
									<dsp:input iclass="moncasto" type="text" size="7" maxsize="10" bean="CastoProfileAdminFormHandler.value.billingAddress.postalCode" paramvalue="profile.billingAddress.postalCode" />

								</TD>
								<TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
								<TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG
									src="../../img/1pixel.gif" width="1" height="1"></TD>
							</TR>
							<TR>
								<TD width="1" bgcolor="#FFDE63"><IMG
									src="../../img/1pixel.gif" width="1" height="1"></TD>
								<TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
								<TD width="289" valign="MIDDLE" class="texte" align="center"
									height="30">Ville :&nbsp; 
									
									<dsp:input iclass="moncasto" type="TEXT" size="30" maxsize="100" bean="CastoProfileAdminFormHandler.value.billingAddress.city" paramvalue="profile.billingAddress.city" />

								</TD>
								<TD width="289" valign="MIDDLE" class="texte" align="center">T&eacute;l&eacute;phone
								:&nbsp; <dsp:input iclass="moncasto" type="TEXT" size="15"
									maxsize="30"
									bean="CastoProfileAdminFormHandler.value.billingAddress.phoneNumber"
									paramvalue="profile.billingAddress.phoneNumber"/></TD>
								<TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
								<TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG
									src="../../img/1pixel.gif" width="1" height="1"></TD>
							</TR>
							<TR bgcolor="#FFDE63">
								<TD width="1"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="10"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="289"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="289"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="10"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
								<TD width="1"><IMG src="../../img/1pixel.gif" width="1"
									height="1"></TD>
							</TR>
						</TABLE>
						<BR>
						<TABLE width="760" border="0" cellspacing="0" cellpadding="0">
							<TR align="center">
								<TD valign=top><A href="javascript:history.back();"><IMG
									src="../../img/annuler.gif" border="0"></A></TD>
								<TD>
									<input type="hidden" name="provenance" value="basic"/>
									<dsp:input type="image" src="../../img/valider.gif" bean="CastoProfileAdminFormHandler.updateProfile" />
								</TD>
							</TR>
						</TABLE>
					</dsp:form></TD>
				</TR>
			</TABLE>
		</dsp:oparam>
	</dsp:droplet>
	
	</dsp:oparam>
	<dsp:oparam name="accesRefuse"></dsp:oparam>
	</dsp:droplet>
	<dsp:include page="../common/menuBas.jsp" />
	</BODY>
	</HTML>


</dsp:page>
