<dsp:page>
	<dsp:importbean bean="/com/castorama/util/UtilFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	
	<dsp:getvalueof var="currentPage" value="${pageContext.request.contextPath}/user/readOrders.jsp" />

	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param bean="Profile.transient" name="value" />
		<dsp:oparam name="true">
			<dsp:setvalue bean="/atg/userprofiling/SessionBean.values.loginSuccessURL" value="${currentPage}" />
			<dsp:droplet name="/atg/dynamo/droplet/Redirect">
				<dsp:param name="url" value="login.jsp" />
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>

	<cast:pageContainer>
		<jsp:attribute name="bodyContent">
			<dsp:form method="post">
				<dsp:input type="hidden" bean="UtilFormHandler.successURL" value="${currentPage}" />
				<dsp:input type="hidden" bean="UtilFormHandler.failureURL" value="${currentPage}" />
				<dsp:input type="hidden" bean="UtilFormHandler.userId" beanvalue="Profile.repositoryId" />
				<dsp:input type="submit" bean="UtilFormHandler.readOrders" value="SCD Commandes"/>
			</dsp:form>
		</jsp:attribute>
	</cast:pageContainer>
</dsp:page>