<%@page import="atg.servlet.DynamoHttpServletRequest"%>
<%@page import="java.lang.String"%>

<dsp:page>


<dsp:importbean bean="/atg/dynamo/servlet/pipeline/BrowserAttributes"/>
<dsp:importbean bean="/castorama/utils/CastoConvPrix"/>


<dsp:droplet name="/atg/dynamo/droplet/Switch">
<dsp:param name="value" param="formatEntreePrix"/>
<dsp:oparam name="F">
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
	<dsp:param name="value" param="formatSortiePrix"/>
	<dsp:oparam name="F">
		<dsp:valueof param="prix" number="0.00"/> Frs
	</dsp:oparam>
	<dsp:oparam name="E">
		<%--<%convFrancsEuros(request);%>--%>
		<%--<dsp:droplet name="/atg/dynamo/droplet/Switch">
			<dsp:param name="value" value="m_SymboleEuro"/>
			<dsp:oparam name="OUI">
				<dsp:valueof param="CONVERTI" euro="true" symbol="<font face=arial>&euro;</font>" locale="fr_FR"/>
			</dsp:oparam>
			<dsp:oparam name="NON">
				<dsp:valueof param="CONVERTI" euro="true" symbol="EUR" locale="fr_FR"/>
			</dsp:oparam>			
			<dsp:oparam name="AUCUN">
				<dsp:valueof param="CONVERTI" euro="true" symbol=" " locale="fr_FR"/>
			</dsp:oparam>
		</dsp:droplet>--%>
		<dsp:droplet name="CastoConvPrix">
			<dsp:param name="montant" param="prix"/>
			<dsp:param name="sensConv" value="Fr-Eur"/>
			<dsp:oparam name="output">
				<dsp:valueof param="montantConverti" number="0.00"/> <dsp:valueof param="devise" valueishtml="true"/>
			</dsp:oparam>
		</dsp:droplet>
		
	</dsp:oparam>
	<dsp:oparam name="default">
		<B>NC</B>
	</dsp:oparam>
	</dsp:droplet>
</dsp:oparam>
<dsp:oparam name="E">

	<dsp:droplet name="/atg/dynamo/droplet/Switch">
	<dsp:param name="value" param="formatSortiePrix"/>
	<dsp:oparam name="F">
		<%--<%convEurosFrancs(request);%>--%>
		
		<dsp:droplet name="CastoConvPrix">
			<dsp:param name="montant" param="prix"/>
			<dsp:param name="sensConv" value="Eur-Fr"/>
			<dsp:oparam name="output">
				<dsp:valueof param="montantConverti" number="0.00"/> <dsp:valueof param="devise" valueishtml="true;"/>
			</dsp:oparam>
		</dsp:droplet>
		
		
	</dsp:oparam>
	<dsp:oparam name="E">
			
		<%--<dsp:droplet name="/atg/dynamo/droplet/Switch">
			<dsp:param name="value" value="m_SymboleEuro"/>
			<dsp:oparam name="OUI">
				<dsp:valueof param="prix" euro="true" symbol="<font face=arial>&euro;</font>" locale="fr_FR"/>
			</dsp:oparam>
			<dsp:oparam name="NON">
				<dsp:valueof param="prix" euro="true" symbol="EUR" locale="fr_FR"/>
			</dsp:oparam>			
			<dsp:oparam name="AUCUN">
				<dsp:valueof param="prix" euro="true" symbol=" " locale="fr_FR"/>
			</dsp:oparam>
		</dsp:droplet>--%>
			<dsp:valueof param="prix" number="0.00"/> &euro;
	</dsp:oparam>
	
	<dsp:oparam name="default">
		<B>NC</B>
	</dsp:oparam>
	</dsp:droplet>
</dsp:oparam>
<dsp:oparam name="default">
	<B>NC</B>
</dsp:oparam>
</dsp:droplet>


</dsp:page>