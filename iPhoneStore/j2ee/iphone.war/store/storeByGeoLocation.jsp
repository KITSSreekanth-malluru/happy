<dsp:page>
  <m:jsonObject>
    <dsp:droplet name="/com/castorama/mobile/droplet/ClothestStoreDroplet">
       <dsp:param name="longitude" param="longitude" />
       <dsp:param name="latitude" param="latitude" />
       <dsp:param name="numberOfStoresInList" param="numberOfStoresInList" />
       <dsp:oparam name="output">
         <dsp:include page="/store/storeList.jsp" flush="true">
           <dsp:param name="storeList" param="storeList" />
         </dsp:include>
         <json:property name="errorCode" value="${0}"/>
       </dsp:oparam>
       <dsp:oparam name="empty">
           <json:property name="errorMessage">
              <fmt:message key="er_407"/>
            </json:property>
           <json:property name="errorCode" value="${1}"/>
       </dsp:oparam>
       <dsp:oparam name="error">
           <json:property name="errorMessage">
             <fmt:message key="er_407"/>
           </json:property>            
           <json:property name="errorCode" value="${1}"/>
       </dsp:oparam>
    </dsp:droplet>
  </m:jsonObject>  
</dsp:page>