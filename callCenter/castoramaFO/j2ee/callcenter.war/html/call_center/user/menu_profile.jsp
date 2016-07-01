<dsp:page>
<%-- 
------------------------------------------------------------------------------------------ 
imports-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>


<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:include page="../common/protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include>

<table>
	<tr>
		<td class="marques">
			COMPTES INTERNAUTES : 
			
			
		</td>
	</tr>
	<tr>
		<td class="texteg">
		
		
			<dsp:droplet name="CastoGetPrivileges">
			    <dsp:param name="profile" bean="Profile"/>
			   	<dsp:param name="requis" value="commerce-csr-profiles-privilege"/>	
			   		   		
		   		<dsp:oparam name="accesAutorise">
	    			<img src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" border=0>&nbsp;<a class=moncasto href="${pageContext.request.contextPath}/html/call_center/user/find_users_page.jsp">Rechercher un internaute</a>
					<dsp:droplet name="/atg/dynamo/droplet/IsNull">
	           			<dsp:param name="value" param="user"/>
	           			<dsp:oparam name="false"><br>
	             		<img src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" border=0>&nbsp;
						 <a class="moncasto" href="${pageContext.request.contextPath}/html/call_center/user/editprofilePage.jsp?id=<dsp:valueof param="id"/>"><dsp:param name="id" param="id"/>Editer l'internaute <dsp:valueof param="user"><dsp:valueof param="id"></dsp:valueof></dsp:valueof></a>
	          			</dsp:oparam>
	         			<dsp:oparam name="true"></dsp:oparam>
			        </dsp:droplet>
				</dsp:oparam>
				
	   			<dsp:oparam name="accesRefuse"></dsp:oparam>
	   			
			</dsp:droplet>
		</td>
	</tr>
</table>
</dsp:page>
