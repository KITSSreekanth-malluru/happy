<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/Range" />
  <dsp:importbean bean="/atg/commerce/order/OrderLookup" />
  <dsp:importbean bean="/com/castorama/droplet/WebOrdersDroplet"/>
  <dsp:importbean bean="/atg/registry/RepositoryTargeters/Orders/WebOrders"/>
  <dsp:importbean bean="/com/castorama/droplet/MessageStateDroplet"/>
  <dsp:importbean bean="/com/castorama/commerce/order/CastWebOrdersFormHandler"/>
  <dsp:importbean bean="/com/castorama/droplet/MessageDetailsDroplet" />
  
  <dsp:droplet name="/atg/dynamo/droplet/Compare">
     <dsp:param bean="/atg/userprofiling/Profile.securityStatus" name="obj1"/>
     <dsp:param bean="/atg/userprofiling/PropertyManager.securityStatusLogin" name="obj2"/>
      <dsp:oparam name="lessthan">
        <!-- send the user to the login form -->
        <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.loginSuccessURL" value="${pageContext.request.contextPath}/user/ordersHistory.jsp" />
         <dsp:droplet name="/atg/dynamo/droplet/Redirect">
           <dsp:param name="url" value="login.jsp" />
         </dsp:droplet>
      </dsp:oparam>
  </dsp:droplet>   

  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
    
      <dsp:getvalueof var="orderView" param="orderView" />
      
      <c:choose>
        <c:when test="${'new' == orderView}" >
          <script type="text/javascript">
            $(window).load( function(){ showPopup('newMessage'); });
          </script>
        </c:when>
        <c:when test="${'view' == orderView}" >
          <script type="text/javascript">
            $(window).load( function(){ showPopup('readMessage'); });
          </script>
        </c:when>
        <c:when test="${'replay' == orderView}" >
          <script type="text/javascript">
            $(window).load( function(){ showPopup('replayMessage'); });
          </script>
        </c:when>
        <c:when test="${'sent' == orderView}" >
          <script type="text/javascript">
            $(window).load( function(){ showPopup('sentMessage'); });
          </script>
        </c:when>
        <c:otherwise>
        </c:otherwise>
      </c:choose>
    
      <c:set var="brElement" value="header.my.profil" scope="request"/>
      <dsp:include page="includes/breadcrumbsClientHeader.jsp">
        <dsp:param name="element" value="${brElement}"/>
        <dsp:param name="pageType" value="orders"/>
      </dsp:include>
      <c:set var="footerBreadcrumb" value="client" scope="request"/>
      <dsp:getvalueof var="pageType" value="orders" scope="request"/>
      
	    <dsp:include page="clientMenu.jsp">
	      <dsp:param name="currPage" value="order"/>
	    </dsp:include>
      <div class="clientSpace">
      <div class="formBlock">
        <div class="orderHistoryMsg">
          <fmt:message key="client.orders.description" />
        </div>
        <dsp:getvalueof var="all_orders" param="all_orders" />
        <dsp:getvalueof var="all_messages" param="all_messages" />
            
            <dsp:droplet name="WebOrdersDroplet">
              <dsp:param name="targeter" bean="WebOrders" />
              <dsp:oparam name="empty">
                <div class="darkRed rdAlert"><strong><fmt:message key="client.orders.empty" /></strong></div>
              </dsp:oparam>
              <dsp:oparam name="output">
                <dsp:getvalueof var="orders" param="orders" />
                <c:choose>
                  <c:when test="${'true' == all_orders || 5 > fn:length(orders)}">
                  
                <dsp:droplet name="ForEach">
                  <dsp:param name="array" param="orders" />
                  <dsp:oparam name="outputStart">
                    <table class="productsTable blueTable" cellspacing="0" cellpadding="0" >
                      <colgroup>
                        <col width="89px" />
                        <col width="189px" />
                        <col width="160px"/>
                        <col width="258px" />
                        <col />
                      </colgroup>
                      <tr valign=top>
                        <th class=box-top-profile><fmt:message key="client.orders.order.date" /></th>
                        <th class=box-top-profile><fmt:message key="client.orders.order.id" /></th>
                        <th class=box-top-profile><fmt:message key="client.orders.order.type" /></th>
                        <th class=box-top-profile><fmt:message key="client.orders.order.status" /></th>
                        <th class=box-top-profile><fmt:message key="client.orders.order.message" /></th>
                      </tr>
                  </dsp:oparam>
                  <dsp:oparam name="outputEnd">
                    </table>
                  </dsp:oparam>
                  <dsp:oparam name="output">
                    <%@include file="orderInHistory.jspf" %>
                  </dsp:oparam>
                </dsp:droplet>
                  
                  </c:when>
                  <c:otherwise>

                <dsp:droplet name="Range">
                  <dsp:param name="array" param="orders" />
                  <dsp:param name="howMany" value="4" />
                  <dsp:oparam name="outputStart">
                    <table class="productsTable blueTable" cellspacing="0" cellpadding="0" >
                      <colgroup>
                        <col width="89px" />
                        <col width="189px" />
                        <col width="160px" />
                        <col width="258px" />
                        <col />
                      </colgroup>
                      <tr valign=top>
                        <th class=box-top-profile><fmt:message key="client.orders.order.date" /></th>
                        <th class=box-top-profile><fmt:message key="client.orders.order.id" /></th>
                        <th class=box-top-profile><fmt:message key="client.orders.order.type" /></th>
                        <th class=box-top-profile><fmt:message key="client.orders.order.status" /></th>
                        <th class=box-top-profile><fmt:message key="client.orders.order.message" /></th>
                      </tr>
                  </dsp:oparam>
                  <dsp:oparam name="outputEnd">
                    </table>
                    <div class="orderBlockLinkSmr">
                      <dsp:a href="ordersHistory.jsp" iclass="darkBlueLink">
                        <dsp:param name="all_orders" value="true" />
                        <c:if test="${'true' == all_messages}">
                          <dsp:param name="all_messages" value="true" />
                        </c:if>
                        <fmt:message key="client.orders.view.all" />
                      </dsp:a>
                    </div>
                  </dsp:oparam>
                  <dsp:oparam name="output">
                    <%@include file="orderInHistory.jspf" %>
                  </dsp:oparam>
                </dsp:droplet>

                  
                  </c:otherwise>
                </c:choose>
                
                
                <br />
              
                <dsp:getvalueof var="messages" param="messages" />
                <c:choose>
                  <c:when test="${'true' == all_messages || 5 > fn:length(messages)}">


                <dsp:droplet name="ForEach">
                  <dsp:param name="array" param="messages" />
                  <dsp:oparam name="outputStart">
                    <table class="productsTable blueTable" id="orderMessages" cellspacing="0" cellpadding="0" >
                    	<colgroup>
                    		<col width="80px" />
                    		<col width="89px" />
                    		<col />
                    		<col width="127px" />
                    	</colgroup>
                      <tr valign=top>
                        <th class=box-top-profile>&nbsp;</td>
                        <th class=box-top-profile><fmt:message key="client.orders.message.date" /></th>
                        <th class=box-top-profile><fmt:message key="client.orders.message.subject" /></th>
                        <th class=box-top-profile><fmt:message key="client.orders.message.read" /></th>
                      </tr>
                  </dsp:oparam>
                  <dsp:oparam name="outputEnd">
                    </table>
                  </dsp:oparam>
                  <dsp:oparam name="output">
                    <%@include file="messageInHistory.jspf" %>
                  </dsp:oparam>
                </dsp:droplet>

                  </c:when>
                  <c:otherwise>

                <dsp:droplet name="Range">
                  <dsp:param name="array" param="messages" />
                  <dsp:param name="howMany" value="4" />
                  <dsp:oparam name="outputStart">
                    <table class="productsTable blueTable" id="orderMessages" cellspacing="0" cellpadding="0" >
                    	<colgroup>
                    		<col width="80px" />
                    		<col width="89px" />
                    		<col />
                    		<col width="127px" />
                    	</colgroup>
                      <tr valign=top>
                        <th class=box-top-profile>&nbsp;</td>
                        <th class=box-top-profile><fmt:message key="client.orders.message.date" /></th>
                        <th class=box-top-profile><fmt:message key="client.orders.message.subject" /></th>
                        <th class=box-top-profile><fmt:message key="client.orders.message.read" /></th>
                      </tr>
                  </dsp:oparam>
                  <dsp:oparam name="outputEnd">
                    </table>
                    <div class="orderBlockLinkSmr">
                      <dsp:a href="ordersHistory.jsp" iclass="darkBlueLink">
                        <dsp:param name="all_messages" value="true" />
                        <c:if test="${'true' == all_orders}">
                          <dsp:param name="all_orders" value="true" />
                        </c:if>
                        <fmt:message key="client.orders.view.messages.all" />
                      </dsp:a>
                    </div>
                    
                  </dsp:oparam>
                  <dsp:oparam name="output">
                    <%@include file="messageInHistory.jspf" %>
                  </dsp:oparam>
                </dsp:droplet>

                  </c:otherwise>
                </c:choose>
              
              
              </dsp:oparam>
            </dsp:droplet>
            <dsp:include page="../user/includes/adviceBlock.jsp" />
          </div>
          </div>

             
      
            <%@include file="orderStatesHelp.jspf" %>
        
            <%@include file="readMessage.jspf" %>
            <%@include file="replayMessage.jspf" %>
            <%@include file="newMessage.jspf" %>
            <%@include file="sentMessage.jspf" %>
        
              
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>

