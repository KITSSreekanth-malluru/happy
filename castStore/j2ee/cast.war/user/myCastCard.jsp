<%@ taglib prefix="dsp"
	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<dsp:page>
	<dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />

	<table>
		<tr>
			<td><br>
			<b>Vous avez une Carte Castorama?</b> <br>
			<br>
			</td>
		</tr>
		<tr>
			<table>
				<tr>
					<td align="right">merci de saisir son numéro:&nbsp;</td>
					<td><dsp:input
						bean="CastProfileFormHandler.value.numeroCarteAtout" maxlength="30"
						size="30" type="text" />
					<td>
				</tr>
			</table>
		</tr>
	</table>
</dsp:page>