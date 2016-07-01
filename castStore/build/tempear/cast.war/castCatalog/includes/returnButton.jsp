<dsp:page>
<%--<dsp:getvalueof var="prevURI" bean="com/castorama/util/LastViewedPage.lastVisitedUrl"/>--%>

<c:if test="${not empty lastViewedPage}">
<div class="breadcrumbs" id="returnButton">
    <script type="text/javascript">
    function returnToPrev(){
        if('${lastViewedPage}'){
            //window.history.back();
            location.href='${lastViewedPage}';
        }
    };
    </script>
    <div class="clickable">
        <a onclick="javascript:returnToPrev();">
        <img src="../images/icoReturnButton.png"/>
        </a>
    </div>

</div>
</c:if>
</dsp:page>