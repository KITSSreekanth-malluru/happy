<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/castorama/droplet/CastoStageDroplet"/>

    <cast:pageContainer>
    <jsp:attribute name="bodyContent">   
		<div class="breadcrumbs bluePage">
    		<div class="homeBreadIco">
      			<a href="/store/home.jsp"><img src="/store/images/icoHomeGray.gif" alt="" title=""/></a>
    		</div>
    		<div class="splitter">&gt;</div>
    		<div class="active">Les castostages</div>
    	</div>
    	<c:set var="footerBreadcrumb" value="clientSpaceHome" scope="request"/>
		<div class="content">
       		<div class="trainingsTable">
       			<div class="pageTitle"><h1>Participez aux castostages, ouverts à tous et gratuits dans les magasins Castorama !</h1></div>
       			<p>
       				Poser un parquet dans une chambre d'enfants, perplexe devant une chasse d'eau qui fuit...<br />
					Si vous ne savez pas comment faire, les castostages sont ouverts à tous, gratuits, tous les samedis matins de 9h30 à 11h30, pour découvrir un thème de bricolage.
       			</p>
       			<table cellspacing="0" cellpadding="0" class="productsTable darkTable">
       				<thead>
       					<tr>
       						<th class="alignLeft">Castostage</th>
       						<th>Date</th>
       					</tr>
       				</thead>
       				<tbody>
       					<dsp:droplet name="CastoStageDroplet">
							<dsp:oparam name="output">
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="trainings"/>
									<dsp:oparam name="output">
										<tr>
				       						<td>
				       							<h2><dsp:valueof param="count"/> &bull; <dsp:valueof param="element.nom"/></h2>
				       							<dsp:valueof param="element.descriptif"/>
				       						</td>
				       						<td class="center">
				       							<span>le <dsp:valueof param="element.dateStage" date="dd/MM/yyyy"/></span><br/>
				       							dans votre magasin Castorama*
				       						</td>
				       					</tr>										
									</dsp:oparam>
								</dsp:droplet>
								<script type="text/javascript">var hideFooterNote = false;</script>
							</dsp:oparam>
			        		<dsp:oparam name="empty">
								<tr>
		       						<td colspan="2"><div style="text-align: center" class="darkRed" ><strong>Nous n'avons pas de Castostage &agrave; vous proposer actuellement.</strong></div></td>
				       			</tr>
				       			<script type="text/javascript">var hideFooterNote = true;</script>	
							</dsp:oparam>
						</dsp:droplet>
       				</tbody>
       			</table>
       			<div class="footer-note">*Certains magasins ne proposent pas ces stages. Renseignements et inscription à l'accueil de votre magasin Castorama.</div>
       		</div>
		</div>
		<script type="text/javascript">
			if(hideFooterNote){
				$("div.footer-note").hide();
			}
		</script>
	  </jsp:attribute>
    </cast:pageContainer>
</dsp:page>







