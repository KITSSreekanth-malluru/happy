<dsp:page xml="true">


	<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/atg/commerce/order/FindOrdersFormHandler"/> 
<dsp:importbean bean="/castorama/CastoOrderEditor"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<html>
<TITLE>Castorama : Call center - Order no load</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<LINK REL="stylesheet" HREF="../../../css/hp.css">
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN="0" TOPMARGIN="0" MARGINWIDTH="0" MARGINHEIGHT="0">

<dsp:setvalue param="order" beanvalue="FindOrdersFormHandler.order"/>
		  
		  <dsp:droplet name="/atg/targeting/RepositoryLookup">
			<dsp:param name="repository" bean="/atg/userprofiling/ProfileAdapterRepository" />
			<dsp:param name="itemDescriptor" value="user" />
			<dsp:param name="id" param="order.profileId" />
			<dsp:param name="elementName" value="profile" />
			<dsp:oparam name="output">
		  
	
    		   <dsp:droplet name="/atg/dynamo/droplet/IsNull">
                  <dsp:param name="value" param="profile"/>
                  <dsp:oparam name="true">
                   <dsp:include src="../../common/header.jsp"/>
                  </dsp:oparam> 
                  <dsp:oparam name="false">
					<dsp:include page="../../common/header.jsp">
                      <param name="id" param="order.profileId"/>
                      <param name="user" param="profile.login">
                    </dsp:include>
                  </dsp:oparam> 
               </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>
		 <table align=center border=0 align=center>
		 <tr>
		 <td align=center>
		 	<span class="prix" align=center>La commande ne s'est pas charg&eacute; correctement.</span>
			<br><a href="javascript:history.back();"><img src="<%=request.getContextPath()%>/html/img/bt_retour.gif" border=0></a>
		 </td></tr></table>

</body>
</html>

</dsp:page>
