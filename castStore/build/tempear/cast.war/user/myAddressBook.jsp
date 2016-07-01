<%@ taglib prefix="dsp"
  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>

<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile" />
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>

  <dsp:importbean bean="/atg/userprofiling/PropertyManager" />
  <dsp:droplet name="/atg/dynamo/droplet/Compare">
     <dsp:param bean="Profile.securityStatus" name="obj1"/>
     <dsp:param bean="PropertyManager.securityStatusCookie" name="obj2"/>
      <dsp:oparam name="lessthan">
        <!-- send the user to the login form -->
        <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.loginSuccessURL" value="${pageContext.request.contextPath}/user/myAddressBook.jsp" />
         <dsp:droplet name="Redirect">
           <dsp:param name="url" value="login.jsp" />
         </dsp:droplet>
      </dsp:oparam>
      <dsp:oparam name="equal">
        <!-- send the user to the login form -->
        <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.loginSuccessURL" value="${pageContext.request.contextPath}/user/myAddressBook.jsp" />
         <dsp:droplet name="Redirect">
           <dsp:param name="url" value="login.jsp" />
         </dsp:droplet>
      </dsp:oparam>
  </dsp:droplet>    
  
  <cast:pageContainer>
    <jsp:attribute name="bodyContent">    
      <div class="content">
        <c:set var="brElement" value="client.breadcrumb.address.book" scope="request"/>
        <dsp:include page="includes/breadcrumbsClientHeader.jsp">
          <dsp:param name="element" value="${brElement}"/>
        </dsp:include>
        <c:set var="footerBreadcrumb" value="client" scope="request"/>
		    <dsp:include page="clientMenu.jsp">
		      <dsp:param name="currPage" value="addressBook"/>
		    </dsp:include>
        <div class="contactBlock">              
          <dsp:getvalueof var="addressForm" bean="/OriginatingRequest.requestURI" idtype="java.lang.String"/>  
          <div class="msg"><fmt:message key="msg.address.instruction" /></div>                                                
          <div class="splitter"><!--~--></div>
          <dsp:getvalueof var="addresses" bean="Profile.secondaryAddresses" />
          <dsp:test var="objectSize" value="${addresses}" />
          <c:if test="${objectSize.size<3}">
            <div class="lightBg"><a href="javascript:void(0)" class="arrowedLink darkBlue_whiteArrow" id="addAddressLink"><fmt:message key="msg.address.add" /></a></div>  
          </c:if>

          <div class="contactBlockLayout" >
            <div class="contactContentBlock outerButtons">           
  
              <h3><fmt:message key="msg.delivery.primary" /></h3>
              <div class="splitter"><!--~--></div>
              <div class="contactContent">
                <div class="contactContentText">
                  <dsp:include page="includes/contentAddressInfo.jsp">
                    <dsp:param name="address" bean="Profile.billingAddress" />
                  </dsp:include>
                </div>
                <a href="javascript:void(0)" id="modifierPrAdresseLink"><fmt:message key="msg.address.modify.primary.address" /></a>
              </div>
            </div> 
                        
                    
            <dsp:droplet name="IsEmpty">
              <dsp:param bean="Profile.secondaryAddresses" name="value" />
              <dsp:oparam name="false">  
	              <dsp:getvalueof var="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI" />            
	              <dsp:droplet name="ForEach">
	                <dsp:param name="sortProperties" value="+_key"/>
	                <dsp:param bean="Profile.secondaryAddresses" name="array" />
	                <dsp:oparam name="output">
	                 <div class="contactContentBlock outerButtons"> 
	                   <h3><fmt:message key="msg.delivery.secondary" /></h3>
	                   <dsp:getvalueof var="key" param="key"></dsp:getvalueof>
	                   <div class="subtitle"><fmt:message key="msg.address.nom" />&nbsp;<dsp:valueof param="key" /></div>
	                   <div class="contactContent">
	                     <dsp:form method="post" action="${addressForm}">
	                       <div class="contactContentText">
	                         <dsp:include page="includes/contentAddressInfo.jsp">
	                           <dsp:param name="address" param="element" />
	                         </dsp:include>
	                       </div>	                       
	                                                           
	                       <a href="javascript:void(0)" id="${key}" name="modifierAdresse">
	                         <fmt:message key="msg.address.modifier" />
	                       </a>                        
	                       <dsp:a bean="CastProfileFormHandler.removeAddress" href="${requestURL}" paramvalue="key" name="removeAddress"><fmt:message key="msg.address.supprimer" /></dsp:a><br>
	                     </dsp:form>
	                   </div>
	                 </div>
	                </dsp:oparam>
	              </dsp:droplet>               
                    
              </dsp:oparam>
            </dsp:droplet>
          </div>
        
          <dsp:include page="includes/adviceBlock.jsp" />
        </div>      
       </div>
      </jsp:attribute>
    </cast:pageContainer>
    <div id="popupDiv"> </div>

		 <fmt:message key="msg.address.create.header" var="titleNewAddress"/>
		 <dsp:getvalueof var="titleEditAddress" value="Modifier une adresse"/>
		 
		 <script>
		 <!--  
       $(document).ready(function(){  
         $("#addAddressLink").click(function () {         
              var html = $.ajax({
							  url: "/store/user/includes/addressPopup.jsp",
							  data: "popupContainerID=uneAdresse&title=${titleNewAddress}",
							  async: false
							 }).responseText;
							 $('#popupDiv').html(html);
							 showPopup('uneAdresse');  
         });
         $("#modifierPrAdresseLink").click(function () {         
              var html = $.ajax({
                url: "/store/user/includes/addressPopup.jsp",
                data: "popupContainerID=modifierPrAdresse&title=${titleEditAddress}&content=profile",
                async: false
               }).responseText;
               $('#popupDiv').html(html);
               showPopup('modifierPrAdresse');  
         });
         $("a[name='modifierAdresse']").click(function () {
              var addressName = $(this).attr("id");          
              var html = $.ajax({
                url: "/store/user/includes/addressPopup.jsp",
                data: "popupContainerID=modifierAdresse&title=${titleEditAddress}&type=edit&addressName=" + addressName,
                async: false
               }).responseText;
               $('#popupDiv').html(html);
               showPopup('modifierAdresse');  
         });
       });  
       //-->  
		 </script>
</dsp:page>
