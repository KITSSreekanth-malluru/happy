<dsp:page>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
	<dsp:param name="value" param="state"/>
	<dsp:oparam name="A_CONTROLER">La commande est &agrave; contr&ocirc;ler.</dsp:oparam>
	<dsp:oparam name="EN_ANOMALIE_FACTURE">La commande est en cours de pr&eacute;paration.</dsp:oparam>
	<dsp:oparam name="EN_ANOMALIE_STOCK">Pb de stock sur la commande.</dsp:oparam>
	<dsp:oparam name="EN_PREPARATION">La commande est en cours de pr&eacute;paration.</dsp:oparam>
	<dsp:oparam name="EN_SUSPENS">La commande est en suspens.</dsp:oparam>
	<dsp:oparam name="ERROR_COMMIT_SIPS">ERROR_COMMIT_SIPS<br><dsp:valueof param="stateDetail"/></dsp:oparam>
	<dsp:oparam name="EXPEDIEE">La commande a &eacute;t&eacute; exp&eacute;di&eacute;e.</dsp:oparam>
	<dsp:oparam name="PENDING_CALL_CENTER">En attente de paiement par t&eacute;l&eacute;phone.</dsp:oparam>
	<dsp:oparam name="PENDING_CHEQUE">En attente de paiement par ch&egrave;que.</dsp:oparam>
	<dsp:oparam name="PENDING_REMOVE">La commande est en attente de suppression.</dsp:oparam>
	<dsp:oparam name="PENDING_VIREMENT">D&eacute;signe une commande avec un statut en attente de virement.</dsp:oparam>
    <dsp:oparam name="PENDING_PAYBOX">En attente de r&eacute;ponse de Paybox.</dsp:oparam>
	<dsp:oparam name="VALIDE">La commande est valid&eacute;e.</dsp:oparam>
	<dsp:oparam name="TERMINEE">La commande a &eacute;t&eacute; exp&eacute;di&eacute;e.</dsp:oparam>
	<dsp:oparam name="INCOMPLETE">Panier en cours.</dsp:oparam>
	<dsp:oparam name="PENDING_ATOUT">En attente de v&eacute;rification carte l'Atout.</dsp:oparam>
	<dsp:oparam name="PENDING_FAX">En attente de validation du fax.</dsp:oparam>
	<dsp:oparam name="RELANCE_APRES_EXPEDITION">Relance apr&egrave;s exp&eacute;dition.</dsp:oparam>
    <dsp:oparam name="FAILED">PAYBOX_FAILED</dsp:oparam>
	<dsp:oparam name="default"><dsp:valueof param="state"/></dsp:oparam>
</dsp:droplet>
</dsp:page>