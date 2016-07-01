<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"><HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8" >
<TITLE>CREALFI</TITLE>
<LINK HREF="https://financement.transcred.com/Partenaires/CREALFI/epaiement/css/style.css" TYPE="text/css" REL="stylesheet">
	<SCRIPT LANGUAGE="javascript" type="text/javascript"> 
		<!--
		function updateLibelle(obj)
			{
				document.f.lb.value=obj.title;
			}	

		//-->
	</SCRIPT></HEAD>
<BODY>
<div id="container">
<dsp:getvalueof var="host" bean="/com/castorama/util/ServerSetting.host" />
<form name="f" action="${host}/store/html/commande/paiement/retour-sofinco.jsp" method="POST">
	<input type="hidden" name="rc" value="${param.rc}">
	<input type="hidden" name="mc" value="${param.mc}">
	<input type="hidden" name="lb" value="">
	<div id="top"></div>
	<div class="divTitre"><span class="labelTitre">Choix du mode de règlement (test)</span></div>
	<div class="divChoix">
		<div class="divOption">
			<input type="radio" name="op" value="10" title="PAIEMENT        COMPTANT" onClick="updateLibelle(this);"><span class="labelOption">PAIEMENT COMPTANT</span>
		</div>
		<div class="divTxtCommercial"><span class="labelTxtCommercial">Bareme COMPTANT plan principal</span></div>
		<div class="divExemple"><span class="labelExemple">
			Par exemple, pour financer un montant de ___ €, vous remboursez 1 mensualités de ___ € hors assurances facultatives, au TEG annuel fixe de 0,00 %.
			Le coût total de votre crédit est de 0,00 € hors assurances facultatives. 
		</span></div>
	</div>
	<div class="divChoix">
	<div class="divOption">
		<input type="radio" name="op" value="4" title="PETITES         MENSUALITES" onClick="updateLibelle(this);"><span class="labelOption">PETITES MENSUALITES</span></div>
		<div class="divTxtCommercial"><span class="labelTxtCommercial">
			Remboursement de 30 Euros par tranche de 750 Euros d'encours.TEG annuel révisable de 13,72% à 18,90%
		</span></div>
		<div class="divExemple"></div>
	</div>
<div class="divChoix">
<div class="divOption"><input type="radio" name="op" value="34" title="3 MOIS          CLASSIQUE" onClick="updateLibelle(this);"><span class="labelOption">3 MOIS          CLASSIQUE</span></div>

<div class="divTxtCommercial"><span class="labelTxtCommercial">.</span></div>
<div class="divExemple"><span class="labelExemple">
						Par exemple, pour financer un montant de _, vous remboursez 3 mensualit�s de _ hors assurances facultatives, au TEG annuel fixe de 18,90 %.<br>
						Le co�t total de votre cr�dit est de _ hors assurances facultatives.
					</span></div>
</div>
<div class="divChoix">
<div class="divOption"><input type="radio" name="op" value="37" title="5 MOIS          PROMO" onClick="updateLibelle(this);"><span class="labelOption">5 MOIS          PROMO</span></div>
<div class="divTxtCommercial"><span class="labelTxtCommercial">Paiement 5 mois promo</span></div>
<div class="divExemple"><span class="labelExemple">
						Par exemple, pour financer un montant de _, vous remboursez 5 mensualit�s de _ hors assurances facultatives, au TEG annuel fixe de 6,988 %.<br>

						Le co�t total de votre cr�dit est de _ hors assurances facultatives.
					</span></div>
</div>
<div class="divChoix">
<div class="divOption"><input type="radio" name="op" value="12" title="10 MOIS         CLASSIQUE" onClick="updateLibelle(this);"><span class="labelOption">10 MOIS         CLASSIQUE</span></div>
<div class="divTxtCommercial"><span class="labelTxtCommercial">.</span></div>
<div class="divExemple"><span class="labelExemple">
						Par exemple, pour financer un montant de _, vous remboursez 10 mensualit�s de _ hors assurances facultatives, au TEG annuel fixe de 18,90 %.<br>
						Le co�t total de votre cr�dit est de _ hors assurances facultatives.
					</span></div>
</div>

<div class="divChoix">
<div class="divOption"><input type="radio" name="op" value="13" title="20 MOIS         CLASSIQUE" onClick="updateLibelle(this);"><span class="labelOption">20 MOIS         CLASSIQUE</span></div>
<div class="divTxtCommercial"><span class="labelTxtCommercial">.</span></div>
<div class="divExemple"><span class="labelExemple">
						Par exemple, pour financer un montant de _, vous remboursez 20 mensualit�s de _ hors assurances facultatives, au TEG annuel fixe de 18,90 %.<br>
						Le co�t total de votre cr�dit est de _ hors assurances facultatives.
					</span></div>
</div>
	<div class="divBouton">
		<input type="submit" value="valider">
	</div>
	<div class="labelMention">
		La carte l'Atout Castorama est une carte d'avantage et de paiement, utilisable au comptant (sans frais) et/ou à crédit, et associée à un compte de crédit renouvelable annuellement et reconstituable, sous réserve d'acceptation par Créalfi - SAS au capital de 15 641 550 € - Siren : 437 604 770 00036 - RCS Paris - 128/130 Boulevard Raspail 75006 Paris. TEG annuel révisable de 13,72% à 18.90%. Remboursement par mensualités de 30€ par tranche de 750€ d'encours. Le coût total varie selon la durée et le montant du découvert effectif du compte. Taux et montant hors assurances facultatives. Cotisation annuelle d'accès aux services non financiers 9,90€. Barèmes et conditions en vigueur au 01/01/2008, susceptibles de variations.
	</div>

</form>
</div>
</BODY>
</HTML>
