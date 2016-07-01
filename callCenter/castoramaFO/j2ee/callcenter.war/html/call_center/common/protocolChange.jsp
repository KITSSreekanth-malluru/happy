
<%@page import="atg.servlet.DynamoHttpServletResponse"%>
<%@page import="atg.servlet.DynamoHttpServletRequest"%>

<dsp:page>

<dsp:importbean bean="/castorama/ProtocolChangeAdmin"/>

<dsp:getvalueof var="enable" bean="ProtocolChangeAdmin.enable"/>

<dsp:droplet name="/atg/dynamo/droplet/IsNull">
  <dsp:param name="value" param="protocol"/>
  <dsp:oparam name="true">
    <dsp:setvalue param="protocol" value="nonSecure"/>
  </dsp:oparam>
  <dsp:oparam name="false">
  </dsp:oparam>
</dsp:droplet>


<dsp:getvalueof var="URIWithQueryString" bean="/OriginatingRequest.requestURIWithQueryString" />
<dsp:getvalueof var="ServerPort" bean="/OriginatingRequest.serverPort" />


<dsp:droplet name="/atg/dynamo/droplet/Switch">
	<dsp:param name="value" value="${ServerPort}"/>
	<dsp:oparam beanname="ProtocolChangeAdmin.securePort">
		<dsp:droplet name="/atg/dynamo/droplet/Switch">
  			<dsp:param name="value" param="protocol"/>
  			<dsp:oparam name="nonSecure">
		  		<dsp:droplet name="/castorama/ProtocolChangeAdmin">
		  			<dsp:param name="inUrl" value="${URIWithQueryString}"/>
					<dsp:oparam name="output">
						<dsp:droplet name="/atg/dynamo/droplet/Redirect">
							<dsp:param name="url" param="nonSecureUrl"/>
						</dsp:droplet>
		  			</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>
	</dsp:oparam>
  
	<dsp:oparam beanname="ProtocolChangeAdmin.nonSecurePort">
		<dsp:droplet name="/atg/dynamo/droplet/Switch">
  			<dsp:param name="value" param="protocol"/>
  			<dsp:oparam name="secure">
		 	 	<dsp:droplet name="/castorama/ProtocolChangeAdmin">
		  			<dsp:param name="inUrl" value="${URIWithQueryString}"/>
					<dsp:oparam name="output">
						<dsp:droplet name="/atg/dynamo/droplet/Redirect">
							<dsp:param name="url" param="secureUrl"/>
						</dsp:droplet>
		  			</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>
	</dsp:oparam>
</dsp:droplet>

<%
	String l_strRequestedSessionId = request.getRequestedSessionId();
	String l_strCurrentSessionId = request.getSession().getId();
	if(l_strRequestedSessionId!=null){
		if(!l_strRequestedSessionId.equals(l_strCurrentSessionId)){
			((DynamoHttpServletResponse)response).sendLocalRedirect("./sessionExpire.jsp",(DynamoHttpServletRequest)request);
		}
	}
%>


</dsp:page>
