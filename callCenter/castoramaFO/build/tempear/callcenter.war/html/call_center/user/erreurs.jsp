<dsp:page>




<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:droplet name="ForEach">
  <dsp:param name="array" bean="ProfileFormHandler.formExceptions"/>
	<dsp:oparam name="outputStart">
		<TABLE border=0 cellpadding="10"><TR><TD align="left">
			<SPAN class="texterou">D&eacute;sol&eacute;, des anomalies sont survenues.<BR>Merci de v&eacute;rifier si les champs ci-dessous sont correctement renseign&eacute;s.</SPAN><BR></TD></TR>
			<TR><TD align="left">
	</dsp:oparam>
	<dsp:oparam name="outputEnd">
		</TD></TR></TABLE>
	</dsp:oparam>
  <dsp:oparam name="output">
		<dsp:droplet name="Switch">
 			<dsp:param name="value" param="element.errorCode"/>
			<dsp:oparam name="missingRequiredValue">
				<dsp:droplet name="Switch">
 					<dsp:param name="value" param="element.propertyName"/>
					<dsp:oparam name="lastName">
						<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Le champ "nom"<BR></SPAN>
					</dsp:oparam>
					<dsp:oparam name="firstName">
						<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Le champ "pr&eacute;nom"<BR></SPAN>
					</dsp:oparam>
					<dsp:oparam name="login">
						<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Le champ "e-mail"<BR></SPAN>
					</dsp:oparam>
					<dsp:oparam name="password">
						<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Le champ "mot de passe"<BR></SPAN>
					</dsp:oparam>
					<dsp:oparam name="confirmPassword">
						<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Le champ "confirmation de votre mot de passe"<BR></SPAN>
					</dsp:oparam>
					<dsp:oparam name="reminderPassword">
						<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Le champ "saisie d'une question aide-m&eacute;moire"<BR></SPAN>
					</dsp:oparam>
					<dsp:oparam name="reponseReminder">
						<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Le champ "saisie de votre r&eacute;ponse &agrave; la question aide-m&eacute;moire"<BR></SPAN>
					</dsp:oparam>
					<dsp:oparam name="oldPassword">
						<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Le champ "saisie de votre ancien mot de passe"<BR></SPAN>
					</dsp:oparam>
					<dsp:oparam name="address1">
						<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Le champ "adresse"<BR></SPAN>
					</dsp:oparam>
					<dsp:oparam name="city">
						<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Le champ "ville"<BR></SPAN>
					</dsp:oparam>
					<dsp:oparam name="postalCode">
						<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Le champ "code postal"<BR></SPAN>
					</dsp:oparam>
					<dsp:oparam name="default">
						<SPAN class="texterou">Un champ obligatoire n'a pas &eacute;t&eacute; rempli :<dsp:valueof param="element.propertyName"/><BR></SPAN>
					</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
			<dsp:oparam name="missingLogin">
				<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Le champ "e-mail"<BR></SPAN>
			</dsp:oparam>
			<dsp:oparam name="loginSyntaxError">
				<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Erreur de syntaxe, veuillez ressaisir votre e-mail<BR></SPAN>
			</dsp:oparam>
			<dsp:oparam name="missingPassword">
				<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Le champ "mot de passe"<BR></SPAN>
			</dsp:oparam>
			<dsp:oparam name="invalidLogin">
				<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Votre e-mail n'est pas valide<BR></SPAN>
			</dsp:oparam>
			<dsp:oparam name="invalidPassword">
				<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Votre mot de passe n'est pas valide<BR></SPAN>
			</dsp:oparam>
			<dsp:oparam name="errorUpdatingProfile">
				<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Une erreur s'est produite lors de la mise &agrave; jour de votre profil<BR></SPAN>
			</dsp:oparam>
			<dsp:oparam name="userAlreadyExists">
				<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Un utilisateur existe d&eacute;j&agrave; avec cet e-mail<BR></SPAN>
			</dsp:oparam>
			<dsp:oparam name="illegalArgumentError">
				<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Vous avez saisi une valeur non valide pour le champ <dsp:valueof param="element.propertyName"/><BR></SPAN>
			</dsp:oparam>
			<dsp:oparam name="passwordsDoNotMatch">
				<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;La confirmation de votre mot de passe est erron&eacute;e<BR></SPAN>
			</dsp:oparam>
			<dsp:oparam name="errorCreatingProfile">
				<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Une erreur s'est produite lors de la cr&eacute;ation de votre compte<BR></SPAN>
			</dsp:oparam>
			<dsp:oparam name="permissionDefinedPasswordChange">
				<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Le mot de passe n'a pas &eacute;t&eacute; mis &agrave; jour. l'ancien mot de passe fournit est erronn&eacute;<BR></SPAN>
			</dsp:oparam>
			<dsp:oparam name="missingOldPassword">
				<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Le champ "Ancien mot de passe" est vide<BR></SPAN>
			</dsp:oparam>
			<dsp:oparam name="invalidDate">
				<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Le format de la date saisie 'param:propertyName' est invalide<BR></SPAN>
			</dsp:oparam>
			<dsp:oparam name="oldPasswordError">
				<SPAN class="texterou"><IMG src="/img/puce.gif" border="0">&nbsp;Votre ancien mot de passe saisi est incorrect<BR></SPAN>
			</dsp:oparam>
			<dsp:oparam name="codePostalSyntaxError">
				<SPAN class="texterou">&nbsp;Le champ "code postal" n'est pas valide<BR></SPAN>
			</dsp:oparam>
			<dsp:oparam name="default">
				<SPAN class="texterou"><dsp:valueof param="element.errorCode"/><BR></SPAN>
			</dsp:oparam>
		</dsp:droplet>
  </dsp:oparam>
</dsp:droplet>

</dsp:page>