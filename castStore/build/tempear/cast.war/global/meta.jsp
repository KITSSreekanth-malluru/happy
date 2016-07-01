<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

    <dsp:getvalueof var="id" param="id"/>
    <dsp:getvalueof var="item" param="item"/>
    <dsp:getvalueof var="pageNum" param="pageNum"/>
    <c:if test="${empty pageNum}">
       <dsp:getvalueof var="pageNum" value="1"/>
    </c:if>

  <c:if test="${empty id}">
      <dsp:getvalueof var="set" bean="Profile.catalog.rootCategories.updatedValue"/>
	  <dsp:param name="list" value="${castCollection:list(set)}"/>
	  <dsp:getvalueof var="id" param="list[0].repositoryId"/>
  </c:if>

	<dsp:droplet name="/atg/dynamo/droplet/Cache">
		<dsp:param name="key" value="meta_${item}_${id}_${pageNum}" />
    	<dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSecondsLong"/>
    	<dsp:oparam name="output">
    		<dsp:droplet name="/com/castorama/seo/MetaInfoEvaluationDroplet">
				<dsp:param name="id" value="${id}"/>
				<dsp:param name="item" param="item"/>
				<dsp:param name="tag" value="TITLE"/>
				<dsp:oparam name="output">
					<TITLE><dsp:valueof param="value"/><dsp:valueof param="numText"/></TITLE>
				</dsp:oparam>
			</dsp:droplet>
			<dsp:droplet name="/com/castorama/seo/MetaInfoEvaluationDroplet">
				<dsp:param name="id" value="${id}"/>
				<dsp:param name="item" param="item"/>
				<dsp:param name="tag" value="KEYWORDS"/>
				<dsp:oparam name="output">
					<META name="keywords" content="<dsp:valueof param="value"/>"/>
				</dsp:oparam>
			</dsp:droplet>
			<dsp:droplet name="/com/castorama/seo/MetaInfoEvaluationDroplet">
				<dsp:param name="id" value="${id}"/>
				<dsp:param name="item" param="item"/>
				<dsp:param name="tag" value="DESCRIPTION"/>
				<dsp:oparam name="output">
					<META name="description" content="<dsp:valueof param="value"/>"/>
				</dsp:oparam>
			</dsp:droplet>
    	</dsp:oparam>
	</dsp:droplet>	
</dsp:page>