<%@page import="atg.servlet.DynamoHttpServletRequest"%>
<%@page import="java.lang.String"%>

<dsp:page>


<dsp:importbean bean="/atg/dynamo/servlet/pipeline/BrowserAttributes"/>
<dsp:importbean bean="/castorama/utils/CastoConvPrix"/>
<%--
<%
String m_SymboleEuro = "AUCUN";


private static Double arrondi(double pNumber)
{
    return new Double ((new Long(Math.round(pNumber*100.0)).doubleValue())/100.0);
}

private static void convEurosFrancs(atg.servlet.DynamoHttpServletRequest a_request)
{
	try
	{
		Double l_PrixEuros = (Double)a_request.getObjectParameter("prix");
		Double l_PrixFrancs = arrondi(l_PrixEuros.doubleValue()*6.55957);
		a_request.setParameter("CONVERTI",l_PrixFrancs);
	}
	catch(Exception e)
	{
		a_request.setParameter("CONVERTI",Double.valueOf("9999999.99"));
	}
}
private static void convFrancsEuros(atg.servlet.DynamoHttpServletRequest a_request)
{
	try
	{
		Double l_PrixFrancs = (Double)a_request.getObjectParameter("prix");
		Double l_PrixEuros = arrondi(l_PrixFrancs.doubleValue()/6.55957);
		a_request.setParameter("CONVERTI",l_PrixEuros);
	}
	catch(Exception e)
	{
		a_request.setParameter("CONVERTI",Double.valueOf("9999999.99"));
	}	
}
%>
--%>


<%--<dsp:droplet name="/atg/dynamo/droplet/Switch">
<dsp:param name="value" param="pasAffichageSymboleEuro"/>
<dsp:oparam name="true">
<%m_SymboleEuro="AUCUN";%>
</dsp:oparam>
<dsp:oparam name="default">
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
	<dsp:param name="value" param="formatSortiePrix"/>
	<dsp:oparam name="E">
		<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param name="value" bean="BrowserAttributes.NonEuroCompatible"/>
		<dsp:oparam name="false">
		<%m_SymboleEuro="OUI";%>
		</dsp:oparam>
		<dsp:oparam name="true">
		<%m_SymboleEuro="NON";%>
		</dsp:oparam>  
		</dsp:droplet>
	</dsp:oparam>
	</dsp:droplet>
</dsp:oparam>
</dsp:droplet>--%>
<%--
<%
try
{
	java.lang.Object l_oPrix = request.getObjectParameter("prix");
	if (l_oPrix instanceof String)
	{
		String l_Result1;
		String l_Result2;
		l_Result1 = ((String)l_oPrix).replace(',','.');
		gnu.regexp.RE l_RE = new gnu.regexp.RE("[^0-9\\.]");
		l_Result2=l_RE.substituteAll(l_Result1,"");
		request.setParameter("prix",Double.valueOf(l_Result2));
	}	
}
catch(Exception e)
{
	request.setParameter("formatSortiePrix","DUMMY");
}
%>
--%>

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