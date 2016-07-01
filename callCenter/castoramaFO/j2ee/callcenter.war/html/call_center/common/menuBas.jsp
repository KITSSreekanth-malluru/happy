
<dsp:page>

 	
<dsp:droplet name="/atg/targeting/RepositoryLookup">
					<dsp:param name="repository" bean="/atg/userprofiling/ProfileAdapterRepository" />
					<dsp:param name="itemDescriptor" value="user" />
					<dsp:param name="id" param="id" />
					<dsp:param name="elementName" value="profile" />
					<dsp:oparam name="output"> 	 	
						<dsp:droplet name="/atg/dynamo/droplet/Switch">
							<dsp:param name="value" param="profile.estUtilisateurFo"/>
							<dsp:oparam name="false">
								<dsp:include page="../common/footer.jsp">
		       						<dsp:param name="id" param="profile.repositoryId"/>
		       						<dsp:param name="user" param="profile.login"/>
		     					</dsp:include>							
							</dsp:oparam>
							<dsp:oparam name="true">
								<dsp:include page="../common/footer.jsp"/>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:oparam>
	
					<dsp:oparam name="empty">
						<dsp:include page="../common/footer.jsp"/>
					</dsp:oparam>
</dsp:droplet>	


</dsp:page>