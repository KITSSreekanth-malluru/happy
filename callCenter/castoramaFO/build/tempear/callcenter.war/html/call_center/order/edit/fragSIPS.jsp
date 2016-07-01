<dsp:page xml="true">


	<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>

<dsp:importbean bean="/castorama/BeanSessionOrder"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/castorama/config/Configuration"/>
<dsp:importbean bean="/castorama/droplet/commande/paiement/CastoMercanetFo" />


<%-- 
	GY stabilisation : journalisation
--%>
<script type="text/javascript" src="/jsV3/callcenter/ajax.js"/>
<script></script>

<div id="orderId" style="display:none;"><dsp:valueof param="orderId"/></div>
<div id="userId" style="display:none;"><dsp:valueof bean="Profile.repositoryId"/></div>
<div id="userLogin" style="display:none;"><dsp:valueof bean="Profile.login"/></div>



<table width="400" border="0" cellspacing="0" cellpadding="0" align="center">
<tr bgcolor="#003399"> 
	<td><img src="./../../img/1pixel.gif" width="1" height="1"></td>
</tr>
<tr>
	<td align="center">
		<br><span class="marques">Paiement Call Center :</span><br><br>
		
		<span class="texte" onclick="javascript:lanceRequeteAjaxAtout();">
		
		<dsp:droplet name="CastoMercanetFo">
			<dsp:param name="amount" param="amount" />
			<dsp:param name="orderId" param="orderId" />
			<dsp:param name="ProfileId" param="profileId" />
			
			<dsp:oparam name="output">
				<dsp:valueof param="sipsRequest" valueishtml="true" />
			</dsp:oparam>
			<dsp:oparam name="error">
				Une erreur est survenue en contactant le serveur de paiement.
			</dsp:oparam>
		</dsp:droplet>
		</span>
	</td>
</tr>
<tr>
	<td align="center">
	</td>
</tr>
</table>

</dsp:page>