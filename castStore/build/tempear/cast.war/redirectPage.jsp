<dsp:page>

    <!-- 
    qs=( <dsp:valueof param="qs"/> )</br>
    <dsp:getvalueof var="qs" param="qs"/>
    uri=( <dsp:valueof param="uri"/> )
     -->
    <dsp:getvalueof var="uri" param="uri"/>
    <form name="prepare" action="${contextPath }${uri}" method="post" id="prepare">
        <input type="hidden" value="${qs}" id="qs" name="qs"/>
    </form>
    
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.form.js"></script>
    <script type="text/javascript">
    $(document).ready(function(){   
        $("#prepare").submit();
    })
    
    </script>
</dsp:page>