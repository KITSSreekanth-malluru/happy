<dsp:page>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
<dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
<dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>

<dsp:getvalueof var="popupContainerID" param="popupContainerID"/>
<dsp:getvalueof var="title" param="title"/>
<dsp:getvalueof var="content" param="content"/>
<dsp:getvalueof var="addressName" param="addressName"/>

<c:if test="${empty content }">
  <dsp:getvalueof var="content" value="address"/>
</c:if>

<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
<dsp:getvalueof var="backURL" param="backURL" />

<c:if test="${empty backURL }">
  <dsp:getvalueof var="backURL" value="${contextPath}/user/myAddressBook.jsp"/>
</c:if>

<dsp:droplet name="/com/castorama/droplet/AddressDroplet">
 <dsp:param name="type" param="type"/>
 <dsp:param name="addressName" param="addressName"/>
 <dsp:oparam name="output">
 
	 <dsp:include page="/user/includes/addressPopupTemplate.jsp">
	  <dsp:param name="title" value="${title}"/>
	  <dsp:param name="popupContainerID" value="${popupContainerID}"/>
	  <dsp:param name="content" value="${content}"/>	  
	  <dsp:param name="cancelURL" value="${backURL}"/>
	  <dsp:param name="formExceptions" param="formExceptions"/>
    <dsp:param name="formError" param="formError"/>
    <dsp:param name="flagErrorCp" param="flagErrorCp"/>
    <dsp:param name="editValue" param="editValue"/>
	</dsp:include> 

	<script type="text/javascript">
	<!--
	  $("#submit").click(function() {
	      $("#submit").attr("disabled","disabled");
	      var datastr = "type=${popupContainerID}&content=${content}&popupContainerID=${popupContainerID}&title=${title}&backURL=${backURL}&addressName=${addressName}&"+$("#${popupContainerID}_form").serialize();     
	          
	      var html = $.ajax({
	        url: "/store/user/includes/addressPopup.jsp",
	        data: datastr,
	        async: false
	       }).responseText;

		  var reg= /\S+/;
  		  var m = reg.exec(html);
  
	       if (m != null && m.length > 0 && m[0] != 'ok') {  
	         $('#popupDiv').html(html);
	         showPopup('${popupContainerID }');
	       } else {
	         window.location.replace("${backURL}");
	       }
	  });
	//-->
	</script>

 </dsp:oparam>
 <dsp:oparam name="empty">
 	ok
 </dsp:oparam>
</dsp:droplet>
</dsp:page>

