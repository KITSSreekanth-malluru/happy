<%@ taglib prefix="c"   uri="/core" %>
<%@ taglib prefix="fmt" uri="/fmt" %>
<%@ taglib prefix="dsp" uri="/dspTaglib" %>
<%@ taglib prefix="cordsp" uri="/coreDSP" %>

<dsp:page>

<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:include page="../common/protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include>

<table>
	<tr>
		<td class="marques">
			CONTACTS :
		</td>
	</tr>
	<tr>
		<td class="texteg"> 
			<%--<dsp:droplet name="/atg/dynamo/security/SecurityAccessor">
		   		<param name="allowGroups" value="commerce-csr-profiles-privilege">
		   		<param name="denyGroups" value="">
		   		<dsp:oparam name="accessAllowed">--%>
    				<img src="../img/flecheb_retrait.gif" border=0>&nbsp;<a class=moncasto href="find_contact_page.jhtml">Rechercher un contact</a>
    				<br><img src="../img/flecheb_retrait.gif" border=0>&nbsp;<a class=moncasto href="createcontact.jhtml">Cr&eacute;er un contact</a>
					<dsp:droplet name="/atg/dynamo/droplet/IsNull">
           				<dsp:param name="value" param="user"/>
           				<dsp:oparam name="false"><br>
             				<img src="../img/flecheb_retrait.gif" border=0>&nbsp;
			 				<a class="moncasto" href="createcontact_by_user.jsp"><dsp:param name="id" param="id"/>Cr&eacute;er un contact pour <dsp:valueof param="user"><dsp:valueof param="id"></dsp:valueof></dsp:valueof>
             				</a>
           				</dsp:oparam>
           				<dsp:oparam name="true"></dsp:oparam>
          			</dsp:droplet>
 			<%--</dsp:oparam>
  			<dsp:oparam name="accessDenied"></dsp:oparam>
			</dsp:droplet>--%>
		</td>
	</tr>
</table>

</dsp:page>
