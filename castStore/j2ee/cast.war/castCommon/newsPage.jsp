<dsp:page>
    <cast:pageContainer>
        <jsp:attribute name="bodyContent">
			<div class="content">
	      		<div class="breadcrumbs bluePage">
	            	<div class="homeBreadIco"><a href="${pageContext.request.contextPath}/home.jsp"><img src="${pageContext.request.contextPath}/images/icoHomeGray.gif" alt="" title="" /></a></div>
	                <div class="splitter">&gt;</div><div class="active"><fmt:message key="castCommon.news.active"/></div>					
	            </div>
                <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
	            <c:import charEncoding="utf-8" url="${staticContentPath}/static-pages/news-page-content.html"/>			
        	</div>    
        </jsp:attribute>
    </cast:pageContainer>
</dsp:page>