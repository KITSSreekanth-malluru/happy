<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
  <dsp:importbean bean="/com/castorama/util/UtilFormHandler" />
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  
  <dsp:getvalueof var="currentPage" value="${pageContext.request.requestURL}?${pageContext.request.queryString}" />

  <dsp:droplet name="/atg/dynamo/droplet/Switch">
    <dsp:param bean="Profile.transient" name="value" />
    <dsp:oparam name="true">
      <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.loginSuccessURL" value="${currentPage}" />
      <dsp:droplet name="/atg/dynamo/droplet/Redirect">
        <dsp:param name="url" value="../user/login.jsp" />
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>

  <cast:pageContainer>
    <jsp:attribute name="bottomBanners">true</jsp:attribute>
    <jsp:attribute name="bvInclude">submit</jsp:attribute>
    <jsp:attribute name="bodyContent">

      <dsp:importbean bean="/com/castorama/bazaarvoice/utils/BVConfiguration"/>
      <dsp:getvalueof var="documentDomain" bean="BVConfiguration.documentDomain"/>
      <dsp:getvalueof var="bvRoot" bean="BVConfiguration.bvRoot"/>
      <dsp:getvalueof var="bvDisplayCode" bean="BVConfiguration.bvDisplayCode"/>

      <dsp:importbean bean="/com/castorama/bazaarvoice/utils/BVKeyGenerator"/>
      <dsp:getvalueof var="key" bean="BVKeyGenerator.key"/>
      
      <div id="BVSubmissionContainer">
        <img src="${bvRoot}/static/${bvDisplayCode}/bv_sub_loading_60.gif" id="BVLoaderImage" width="60" height="60" alt="Loading..."/>
      </div>

      <div id="BVSessionParams"></div>
      <iframe id="BVSubmissionFrame" name="BVSubmissionFrame" src="" style="visibility: hidden; position: absolute; left: -999px; top: -999px; width: 1px; height: 1px;"></iframe>

      <script type="text/javascript" language="javascript">
      <!--
          if(typeof(BVisLoaded) == 'undefined') {
              var BVisLoaded = false;
          }
          function BVsubmissionCheckLoadState(bvErrorMsg){
              if(!BVisLoaded){
                  document.getElementById('BVSubmissionFrame').src="";
                  // If the content was not pushed, show a friendly message
                  document.getElementById('BVSubmissionContainer').innerHTML = bvErrorMsg;
              }
          }
          
          function bvLoadRRSubmission() {
              var bvoice_user = "${key}";
              var bvErrorMsg = 'Review Submission Currently Unavailable';
      
              var bvPageMatch = /[?&]bvpage=([^&#]+)/.exec(window.location.search);
              var bvPage = bvPageMatch ? decodeURIComponent(bvPageMatch[1]) : null;
              var bvRegex = new RegExp('^' + window.location.protocol + '\/\/([A-Za-z0-9-.]+[.])?' + document.domain + '\/');
              
              if(bvPage && bvRegex.test(bvPage)) {
                  BVisLoaded = false;
                  document.getElementById('BVSubmissionFrame').src = bvPage.replace(/__USERID__/, bvoice_user);
                  setTimeout("BVsubmissionCheckLoadState(\"" + bvErrorMsg + "\")", 15000);
              } else {
                  document.getElementById('BVSubmissionContainer').innerHTML = bvErrorMsg;
              }
          }

          $(document).ready(function(){
            bvLoadRRSubmission();
          });
      //-->
      </script>

    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>