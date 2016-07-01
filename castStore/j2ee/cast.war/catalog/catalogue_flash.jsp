<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.flash.js"></script>

<script type="text/javascript">var contextPath = "${pageContext.request.contextPath}";</script> 
</head>


<body>
	<dsp:getvalueof var="browse_url" param="browse_url"/>
						
	<div id="catalogue_flash" class="homeBannersTop">
	</div>
   <script language="javascript">
   $(document).ready(function(){
   
     $('#catalogue_flash').flash({
       src: '${browse_url}',
        width: 1000,
        height: 670,
        wmode: 'opaque',
     	expressInstall: true }
     );
   });
   
   </script>
  </body>
</dsp:page>