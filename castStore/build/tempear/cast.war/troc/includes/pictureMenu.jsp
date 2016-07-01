<dsp:page>

<dsp:importbean bean="/com/castorama/integration/troc/ThematiqueListDroplet"/>
<dsp:importbean bean="/com/castorama/integration/troc/ForCustom"/>
<dsp:importbean bean="/atg/dynamo/droplet/Cache"/>

<dsp:getvalueof var="counter" value="2"></dsp:getvalueof>
<dsp:getvalueof var="amount" value="8"></dsp:getvalueof>
<dsp:getvalueof var="currentIndex" param="currentIndex"/>
<c:if test="${empty currentIndex}">
  <dsp:getvalueof var="currentIndex" value="0"/>
</c:if>

<div class="themesPictureMenuContainer" id="themesPictureMenuContainer">
  <dsp:droplet name="ThematiqueListDroplet">
     <dsp:param name="amount" value="${amount}"/>
     <dsp:param name="currentIndex" value="${currentIndex}"/>
     <dsp:oparam name="output">
     <dsp:getvalueof param="currentIndex" var="currentIndex"/>
     <dsp:getvalueof value="${currentIndex-amount}" var="backIndex"/>
     <dsp:getvalueof value="${currentIndex+amount}" var="forwIndex"/>
     <dsp:getvalueof param="canBack" var="canBack"/>
     <dsp:getvalueof param="canForw" var="canForw"/>
     <div class="themesBackButton">
       <c:if test="${canBack}">
         <a href="#" onclick="showAnotherThematiques(${backIndex})">
           <img src="/images/troc/lancez-vous/backButton.png" />
         </a>
       </c:if>
     </div>
     <div class="themesPictureMenuBlock">
     <dsp:getvalueof param="thematiqueList" var="thematiqueList"/>
     <dsp:getvalueof param="amount" var="amount"/>
       <dsp:droplet name="ForCustom">
         <dsp:param name="howMany" value="${amount}"/>
         <dsp:param name="counter" value="${counter}"/>
         <dsp:oparam name="output">
         <dsp:getvalueof param="index" var="index"/>
             <div class="themesPictureVerBlock">
               <dsp:include page="tematiquePicture.jsp">
                 <dsp:param name="thematique" param="thematiqueList[${index}]"/>
               </dsp:include>
               <dsp:include page="tematiquePicture.jsp">
                 <dsp:param name="thematique" param="thematiqueList[${index+1}]"/>
               </dsp:include>
              </div>
         </dsp:oparam>
       </dsp:droplet>
     </div>
     <div class="themesForwButton">
       <c:if test="${canForw}">
         <a href="#" onclick="showAnotherThematiques(${forwIndex})">
           <img src="/images/troc/lancez-vous/forwButton.png" />
         </a>
       </c:if>
     </div>
   </dsp:oparam>
   </dsp:droplet>
</div>
</dsp:page>