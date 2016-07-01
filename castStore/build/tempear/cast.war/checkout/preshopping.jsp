<dsp:page>
  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
    
      <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler" />

      <div class="content">
        <%--
             <dsp:include page="includes/errors.jsp">
              <dsp:param name="titlekey" value="msg.cart.error" />
              <dsp:param name="handler" bean="CastShoppingCartFormHandler" />
            </dsp:include>
        --%>    
        <div class="ajoutpanierContainer">  
          <div>
            <dsp:include page="recommended_row.jsp" >
              <dsp:param name="titlekey" value="msg.cart.recomended" />
            </dsp:include>
          
            <dsp:include page="recentlyviewed_row.jsp" >
              <dsp:param name="titlekey" value="msg.cart.recentlyviewed" />
            </dsp:include>
          </div>
        </div>
        <div class="rightPanel">
          <dsp:include page="precart.jsp" >
            <dsp:param name="url" value="preshopping.jsp" />
          </dsp:include>
        </div>
      </div>
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>
