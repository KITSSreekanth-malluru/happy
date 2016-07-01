<dsp:page xml="true">


	<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>


<dsp:importbean bean="/castorama/CastoOrderEditor"/>
<dsp:importbean bean="/atg/commerce/csr/PaymentGroup"/>
<dsp:importbean bean="/atg/commerce/csr/ShippingGroup"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>


	<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
	<dsp:include page="../../common/protocolChange.jsp"> <dsp:param name="protocol" value="secure" /> </dsp:include>




<html>
<head>
<TITLE>Castorama : Call center - Edite Commande</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<LINK REL="stylesheet" HREF="../../../css/hp.css">
</head>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN="0" TOPMARGIN="0" MARGINWIDTH="0" MARGINHEIGHT="0">



<dsp:droplet name="/atg/dynamo/droplet/IsNull">
   <dsp:param name="value" bean="CastoOrderEditor.order"/>
   <dsp:oparam name="true">
   	<span class="prix">La commande n'a pas &eacute;t&eacute; charg&eacute;e correctement !</span>
   </dsp:oparam>
   <dsp:oparam name="false">
      <dsp:droplet name="/atg/targeting/RepositoryLookup">
		<dsp:param name="repository" bean="/atg/userprofiling/ProfileAdapterRepository" />
		<dsp:param name="itemDescriptor" value="user" />
		<dsp:param name="id" bean="CastoOrderEditor.order.profileId" />
		<dsp:param name="elementName" value="profile" />
		<dsp:oparam name="output">
           <dsp:droplet name="Switch">
              <dsp:param name="value" param="item"/>
              <dsp:oparam name="unset">
                    <dsp:include page="../../common/header.jsp"/>
              </dsp:oparam>
              <dsp:oparam name="default">                 
                    <dsp:include page="../../common/header.jsp">
                      <param name="id" bean="CastoOrderEditor.order.profileId"/>
                      <param name="user" param="profile.login"/>
                  </dsp:include>
              </dsp:oparam>
          </dsp:droplet> <!--- /Switch --->
        </dsp:oparam>
     </dsp:droplet> <!--- /ProfileRepositoryItemServlet --->
     <p>

  <table border=0 cellpadding=0 cellspacing=0 align=center width=600>
    <tr>
     <td>
	 <span align=right>
	 	<img src="<%=request.getContextPath()%>/html/img/flecheb_retrait.gif" border=0>&nbsp;
	 	<a class=moncasto href="javascript:history.back();">retour</a></span>
	 </td>
	  <dsp:droplet name="Switch">
       <dsp:param name="value" param="group_type"/>
       <dsp:oparam name="unset"> Erreur </dsp:oparam>
       <dsp:oparam name="shipping">
      
       
       <table align=center border=0 borderColor="#FFFFFF" cellpadding="0" cellspacing="0">
	   <tr><td align=center>
	   		<span class=marques>Modification de la commande</span>
	   </td></tr>
	   </table>
	   <p align=center>
       <table border=1 borderColor="#CC0033" cellpadding="5" cellspacing="0">
	   <tr><td>
	   		<span class="prix">Indiquer la quantit&eacute; que vous d&eacute;sirez.</span>
	   </td></tr>
	   </table>
	   <br>
         <dsp:droplet name="ShippingGroup">
           <dsp:param name="id" param="id"/>
           <dsp:oparam name="empty"><span class=prix>Aucun Shipping Groups trouv&eacute;</dsp:oparam>
           <dsp:oparam name="shippingGroup">
             <dsp:include page="./edit_items_per_group.jsp">
               <dsp:param name="group" bean="ShippingGroup.shippingGroup"/>
               <dsp:param name="group_type" value="shipping"/>
             </dsp:include>
           </dsp:oparam>
           <dsp:oparam name="hardgoodShippingGroup">
             <dsp:include page="./edit_items_per_group.jsp">
               <dsp:param name="group" bean="ShippingGroup.hardgoodShippingGroup"/>
               <dsp:param name="group_type" value="shipping"/>
             </dsp:include>
           </dsp:oparam>
         </dsp:droplet> <!---- /ShippingGroup ---->
       </dsp:oparam>       
      </dsp:droplet>      <!---- /Switch --->
		  <p>
    </td></tr></table>
  </dsp:oparam>
</dsp:droplet>   <!--- /Switch --->
</body>
</HTML>

</dsp:page>