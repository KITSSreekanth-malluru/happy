<dsp:page>
<%-- 
------------------------------------------------------------------------------------------ 
imports-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>
<dsp:importbean bean="/castorama/order/CastoRattrapageCdeFormHandler" />
<%--AP dsp:importbean bean="/castorama/settings/CastoImgPath"/>
<dsp:tomap var="beanImg" bean="CastoImgPath"/> --%>
<%-- 
------------------------------------------------------------------------------------------ 
Securisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:include page="../common/protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include>
<dsp:droplet name="/castorama/droplet/CastoCheckSecurityStatusStats">
    <dsp:param name="profilUtilisateur" bean="Profile" />
</dsp:droplet> 

<dsp:form method="post" action="/adminFO/html/rattrapageCde/login_success.jsp" name="formUser">
			<dsp:input type="hidden" bean="CastoRattrapageCdeFormHandler.successURL" value="/adminFO/html/rattrapageCde/login_success.jsp" />
			<dsp:input type="hidden" bean="CastoRattrapageCdeFormHandler.errorURL" value="/adminFO/html/rattrapageCde/login_success.jsp" />
			
			<h1>S&eacute;l&eacute;ction par intervalle de dates</h1>
			
			<table width="500" border="0" cellspacing="5">
				<tr>
					<td align="right">
						Date de d&eacute;but : 
					</td>
					<td align="left">
						<dsp:input bean="CastoRattrapageCdeFormHandler.dateDebut" type="text"/><i>* format des dates : jj/mm/aa</i>				
					</td>			
					<td rowspan="2" align="center">
						<dsp:input bean="CastoRattrapageCdeFormHandler.update" src="${pageContext.request.contextPath}/html/img/valider.gif" type="image" />
					</td>		
				</tr>
				<tr>
					<td align="right">
						Date de fin : 
					</td>
					<td align="left">
						<dsp:input bean="CastoRattrapageCdeFormHandler.dateFin" type="text"/><i>* format des dates : jj/mm/aa</i>
					</td>
				</tr>
				<tr>
					<td align="right">
						Montant (Facultatif) : 
					</td>
					<td align="left">
						<dsp:input bean="CastoRattrapageCdeFormHandler.montant" type="text"/><i>* format du montant : XXXX.XX</i>
					</td>
				</tr>
			</table>	
			<br/>
			
			
			<dsp:droplet name="IsNull">
				<dsp:param name="value" param="success"/>
				<dsp:oparam name="false">
					
					<% int cpt = 0; %>
					
					<dsp:droplet name="ForEach">
						<dsp:param name="array" bean="CastoRattrapageCdeFormHandler.resultat"/>
						<dsp:param name="sortProperties" value="-dateCommande,-order_id"/>
						<dsp:oparam name="outputStart">
							<table border="1">
								<tr style="background-color: #C0C0C0;">
									<td>
										Identifiant de la commande
									</td>
									<td>
										Date de la commande
									</td>
									<td>
										D&eacute;tail de la commande
									</td>
									<td>
										Sous Total de la comande
									</td>
									<td>
										Frais de livraison
									</td>
									<td>
										Frais de traitement
									</td>
									<td>
										Total de la comande
									</td>
									<td>
										Magasin d'origine
									</td>
									<%-- <td>
										Moyen de Paiement
									</td> --%>
									<td>
										Login
									</td>
									<td>
										Nom
									</td>
									<td>
										Prenom
									</td>
									<td>
										T&eacute;l&eacute;phone portable
									</td>
									<td>
										T&eacute;l&eacute;phone fixe
									</td>
									<td>
										Adresse Factu
									</td>
									<td>
										Adresse Liv
									</td>
								</tr>
								
						</dsp:oparam>
						<dsp:oparam name="outputEnd">
							</table>
						</dsp:oparam>
						<dsp:oparam name="output">
							
							<%
								if (cpt % 2 == 0)
								{
							%>
									<tr>
							<%
								}
								else
								{
							%>
									<tr style="background-color: #C0C0C0;">
							<%
								}
								cpt = cpt + 1;
							%>
								<td>
									<dsp:valueof param="element.order_id"/>
								</td>
								<td>
									<dsp:valueof param="element.dateCommande"/>
								</td>
								<td>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="element.listCceItems"/>
										<dsp:param name="elementName" value="cce"/>
										<dsp:oparam name="output">	
											-><dsp:valueof param="cce.codeArticle"/>*<dsp:valueof param="cce.quantite"/>=<dsp:valueof param="cce.prixLigneArticle"/><br/>
										</dsp:oparam>
									</dsp:droplet>
								</td>
								<td>
									<dsp:valueof param="element.totalArticles"/>
								</td>
								<td>
									<dsp:valueof param="element.FraisLivraison"/>
								</td>
								<td>
									<dsp:valueof param="element.FraisTraitement"/>
								</td>
								<td>
									<dsp:valueof param="element.totalCommande"/>
								</td>
								<td>
									<dsp:valueof param="element.origineMagasin"/>
								</td>
								<%-- <td>
									<dsp:valueof param="element.moyenPaiement"/>
								</td> --%>
								<td>
									<dsp:valueof param="element.login"/>
								</td>
								<td>
									<dsp:valueof param="element.nom"/>
								</td>
								<td>
									<dsp:valueof param="element.prenom"/>
								</td>
								<td>
									<dsp:valueof param="element.telPortable"/>
								</td>
								<td>
									<dsp:valueof param="element.telFixe"/>
								</td>
								<td>
									<dsp:valueof param="element.adresseLibelleFactu"/>
								</td>
								<td>
									<dsp:valueof param="element.adresseLibelleLiv"/>
								</td>
							</tr>
						</dsp:oparam>
					</dsp:droplet>
							
				</dsp:oparam>
			</dsp:droplet>
			
			
			
			
			<dsp:droplet name="ErrorMessageForEach">
				<dsp:param bean="CastoRattrapageCdeFormHandler.formExceptions" name="exceptions"/>
				<dsp:oparam name="output">
					<div class="erreur">
						<SPAN><img src="<%=request.getContextPath()%>/html/img/flecheb_retrait.gif" border=0><dsp:valueof param="message" /></span><br />		
					</div>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:form>
</dsp:page>
