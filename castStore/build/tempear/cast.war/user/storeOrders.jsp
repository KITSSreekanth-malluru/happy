<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Range"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/userprofiling/PropertyManager"/>
  <dsp:importbean bean="/atg/userprofiling/SessionBean"/>
  <dsp:importbean bean="/com/castorama/droplet/HasCastoramaCardDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/TicketPDFSecureTokenDroplet"/>
  <dsp:importbean bean="/com/castorama/purchaseHistory/TicketFilterFormHandler"/>
  <dsp:importbean bean="/com/castorama/purchaseHistory/PurchaseHistoryConfiguration"/>

  <dsp:droplet name="Switch">
    <dsp:param bean="PurchaseHistoryConfiguration.enabled" name="value" />
    <dsp:oparam name="false">
     <dsp:droplet name="Redirect">
       <dsp:param name="url" value="purchaseHistoryDisabled.jsp" />
     </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
  
  <dsp:droplet name="Compare">
     <dsp:param bean="Profile.securityStatus" name="obj1"/>
     <dsp:param bean="PropertyManager.securityStatusLogin" name="obj2"/>
      <dsp:oparam name="lessthan">
        <!-- send the user to the login form -->
        <dsp:setvalue bean="SessionBean.values.loginSuccessURL" value="${pageContext.request.contextPath}/user/storeOrders.jsp" />
         <dsp:droplet name="Redirect">
           <dsp:param name="url" value="login.jsp" />
         </dsp:droplet>
      </dsp:oparam>
      <dsp:oparam name="default">

        <dsp:droplet name="HasCastoramaCardDroplet">
          <dsp:param name="profile" bean="Profile"/>
          <dsp:oparam name="false">
          
            <!-- send the user to the page with information message  -->
             <dsp:droplet name="Redirect">
               <dsp:param name="url" value="noCastoramaCard.jsp" />
             </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>

      </dsp:oparam>
  </dsp:droplet>   



  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
    
    
      <c:set var="brElement" value="header.my.profil" scope="request"/>
      <dsp:include page="includes/breadcrumbsClientHeader.jsp">
        <dsp:param name="element" value="${brElement}"/>
        <dsp:param name="pageType" value="store.orders"/>
      </dsp:include>
      <c:set var="footerBreadcrumb" value="client" scope="request"/>
      <dsp:getvalueof var="pageType" value="store.orders" scope="request"/>
      
	    <dsp:include page="clientMenu.jsp">
	      <dsp:param name="currPage" value="storeOrders"/>
	    </dsp:include>
      <div class="clientSpace">
      <div class="formBlock">
      
      <dsp:form action="storeOrders.jsp" method="post" formid="storeOrders">
        
        <div class="storeOrdersFilterMsg">
          <fmt:message key="client.store.orders.filter.message"/>
        </div>

        <dsp:droplet name="Switch">
           <dsp:param name="value" bean="TicketFilterFormHandler.validerAction" />
           <dsp:oparam name="false">
             <%-- Mantis 0001928: [Purchase History] Show result list as soon as we click on 'Mon memo web'--%>
             <dsp:setvalue bean="TicketFilterFormHandler.filter"/>
           </dsp:oparam>
        </dsp:droplet>

        <%@include file="ticketsFilter.jspf" %>

        <dsp:droplet name="Switch">
           <dsp:param bean="TicketFilterFormHandler.formError" name="value" />
           <dsp:oparam name="true">
            <div class="errorList">
               <UL>
                 <dsp:droplet name="ErrorMessageForEach">
                   <dsp:param bean="TicketFilterFormHandler.formExceptions" name="exceptions" />
                   <dsp:oparam name="output">
                    <LI> <dsp:valueof param="message" /> 
                    </dsp:oparam>
                 </dsp:droplet>
               </UL>
             </div>
           </dsp:oparam>
         </dsp:droplet>

        <dsp:droplet name="ForEach">
          <dsp:param name="array" bean="TicketFilterFormHandler.tickets" />
          <dsp:param name="elementName" value="ticket" />
          <dsp:oparam name="empty">
            <dsp:droplet name="IsEmpty">
              <dsp:param name="value" bean="TicketFilterFormHandler.informationText"/>
              <dsp:oparam name="false">
                <div class="darkRed rdAlert"><strong><dsp:valueof bean="TicketFilterFormHandler.informationText" valueishtml="true"/></strong></div>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
          <dsp:oparam name="outputStart">
            <dsp:droplet name="IsEmpty">
              <dsp:param name="value" bean="TicketFilterFormHandler.informationText"/>
              <dsp:oparam name="false">
                <div class="informationText"><strong><dsp:valueof bean="TicketFilterFormHandler.informationText" valueishtml="true"/></strong></div>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
          <dsp:oparam name="outputEnd">
          </dsp:oparam>
          <dsp:oparam name="output">
             
             <input type="hidden" id="<dsp:valueof param="ticket.repositoryId"/>_index" value="<dsp:valueof param="index"/>"/>
             
             <dsp:getvalueof var="image_src" value="/images/icoExpand.gif" scope="request"/>
             <dsp:getvalueof var="display_style" value="display:none" scope="request"/>
             
             <%--  By defaults the 1st ticket in the search results table shall be expanded. All the rest shall be collapsed --%>
                <dsp:droplet name="/atg/dynamo/droplet/Switch">
                  <dsp:param name="value" param="index"/>
                  <dsp:oparam name="0">
                     <dsp:getvalueof var="image_src" value="/images/icoCollapse.gif" scope="request"/>
                     <dsp:getvalueof var="display_style" value="" scope="request"/>
                     <script type="text/javascript">
                       currentlySelected = '<dsp:valueof param="ticket.repositoryId"/>';
                     </script>
                  </dsp:oparam>
                </dsp:droplet>
            
            
              <dsp:droplet name="TicketPDFSecureTokenDroplet">
                <dsp:param name="mode" value="encode"/>
                <dsp:param name="userId" bean="Profile.repositoryId" />
                <dsp:param name="ticketId" param="ticket.repositoryId" />
                
                <dsp:oparam name="output">
                  <dsp:getvalueof var="secureTocken" param="encodedParam"/>
                </dsp:oparam>
              </dsp:droplet>
            
            <table class="ticketsTable blueTicketsTable" cellspacing="0" cellpadding="0" >
              <colgroup>
                <col width="7px" />
                <col width="340px" />
                <col width="170px" />
                <col />
                <col width="7px" />
              </colgroup>
              <tr valign=top>
                <th colspan="2"><img style="float:left" id="<dsp:valueof param="ticket.repositoryId"/>_image" src="${image_src}" onclick="showHideOrderContent('<dsp:valueof param="ticket.repositoryId"/>')"/> <span style="float:left;padding-left:10px;margin-top:2px" ><dsp:valueof date="dd/MM/yyyy" param="ticket.ticketDate"/> </span>
                <span style="float:left;padding-left:30px;margin-top:2px" ><fmt:message key="client.store.orders.castorama" />&nbsp;<%@include file="storeName.jspf" %></span></th>
                <th style="padding-top: 12px"><span><fmt:message key="client.store.orders.ticket.total"/>&nbsp;<dsp:valueof param="ticket.totalPriceTTC" converter="number" format="#.00"/>&euro;&nbsp;<fmt:message key="client.store.orders.TTC" /></span></th>
                <th colspan="2">
                   <div class="printTicketButtonArea">
                   <span class="printTicketButton">
                      <a href="${pageContext.request.contextPath}/generateTicketPdf?t=${secureTocken}" target="_blank"
                         onclick="printTicketPDF('<dsp:valueof param="ticket.repositoryId"/>');">
                        <fmt:message key="client.store.orders.print.ticket"/>
                      </a>
                  </span>
                  </div>
                </th>
              </tr>
              
              
              <dsp:droplet name="ForEach">
                <dsp:param name="array" param="ticket.lines"/>
                <dsp:param name="elementName" value="line"/>
                <dsp:param name="sortProperties" value="+lineNumber"/>
                <dsp:oparam name="empty">
                  <%-- Nothing to show --%>
                </dsp:oparam>

                <dsp:oparam name="output">
                  <%@include file="ticketLine.jspf" %>
                </dsp:oparam>
              </dsp:droplet>
            </table>
          </dsp:oparam>
        </dsp:droplet>
          
        
        <br />

        <dsp:include page="../user/includes/adviceBlock.jsp" />
      </dsp:form>
      </div>
      </div>

    <%-- Omniture params Section begins--%>
    <fmt:message var="omniturePageName" key="omniture.pageName.tickets"/>
    <c:set var="omniturePageName" value="${omniturePageName}" scope="request"/>
    <%-- Omniture params Section ends--%>

    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>

