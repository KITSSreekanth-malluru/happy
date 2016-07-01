var ajaxCard = createXMLHttpRequest();

function lanceRequeteAjaxAtout() 
{
	if (ajaxCard.readyState == 0 || ajaxCard.readyState == 4 ) 
	{
		var cardPrice = document.getElementById('cardPrice').innerHTML;
		var cardMode = document.getElementById('cardMode').innerHTML;
		var urlLegalNotice = document.getElementById('urlLegalNotice').innerHTML;
		if( cardPrice!=null && cardPrice!="" && urlLegalNotice!=null && urlLegalNotice!="") 
		{	
			var request = urlLegalNotice+cardPrice;
			ajaxCard.open("GET", request , true);
			ajaxCard.onreadystatechange = reactAjaxCard;
			ajaxCard.send(null);
		} 
	}
}

var teg 	=   "";
var mens 	=	"";
var dur 	=	"";
var cout	=	"";
var ml		=	"";
var bar		=	"";
var content =   "";

var openContWindow = function(content) {
 var newWindow = window.open(this.location.href,'','location=no,menubar=no,width=620,height=500');
 var writeContent = function() {
	 try {
	  newWindow.document.write(content);
	 }
	 catch (e){
	  setTimeout(writeContent, 100);
	 }
 }
 writeContent();
}

var expr = new RegExp('\\w', 'ig');

function reactAjaxCard() 
{
	if (ajaxCard.readyState == 4 && expr.test(ajaxCard.responseText)) 
	{
		var div_cast_card = document.getElementById('div_cast_card');
		var cast_card = document.getElementById('castaCard');
		cast_card.style.borderBottomWidth= "1px";
		var cardAtout = document.getElementById('cardAtout');
		
		div_cast_card.innerHTML = '';
		
		if( ajaxCard.status < 300 ) 
		{
			var repDOMAtout = ajaxCard.responseXML;

			var codret = null;
			if (
					(repDOMAtout!=null )
					&& ((repDOMAtout.getElementsByTagName( 'codret' )) 							 !=null)
					&& ((repDOMAtout.getElementsByTagName( 'codret' )[0]) 						 !=null)
					&& ((repDOMAtout.getElementsByTagName( 'codret' )[0].firstChild) 			 !=null)
					&& ((repDOMAtout.getElementsByTagName( 'codret' )[0].firstChild.nodeValue) 	 !=null)
				)
			{
				codret = repDOMAtout.getElementsByTagName( 'codret' )[0].firstChild.nodeValue;
				if (codret=='00')
				{
					cardAtout.style.display='block';
					
					teg 	=	repDOMAtout.getElementsByTagName( 'TEG' )[0].firstChild.nodeValue;
					mens 	=	repDOMAtout.getElementsByTagName( 'MENS' )[0].firstChild.nodeValue;
					dur 	=	repDOMAtout.getElementsByTagName( 'DUR' )[0].firstChild.nodeValue;
					total_du =  repDOMAtout.getElementsByTagName( 'MT_TOTAL_DU' )[0].firstChild.nodeValue;
					mention	=	repDOMAtout.getElementsByTagName( 'MENTION_SANITAIRE' )[0].firstChild.nodeValue;
					ml		=	repDOMAtout.getElementsByTagName( 'ML' )[0].firstChild.nodeValue;
					bar		=	repDOMAtout.getElementsByTagName( 'BAR' )[0].firstChild.nodeValue;
					
					if (-1 != ml.indexOf('\375')) 
          			{
            			eval("ml = ml.replace(/" + '\375' + "/g, '&eacute;')");
            			ml = ml.replace(/acc&eacute;s/, 'acc&egrave;s');
            			ml = ml.replace(/Bar&eacute;me/, 'Bar&ecirc;me');
            			ml = ml.replace(/co&eacute;t/, 'co&ucirc;t');
            			ml = ml.replace(/ &eacute; /g, ' &agrave; ');
          			}
          			
          			content = "<!DOCTYPE html PUBLIC \'-//W3C//DTD XHTML 1.0 Transitional//EN\' \'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\'>"
          					+ "<html xmlns=\'http://www.w3.org/1999/xhtml\'>"
          					+ "<head><TITLE>Mentions l&eacute;gales</TITLE></head><body>" 
          					+ "<BR/>TAEG : " + teg
							+ " %<BR/>Mensualit&eacute; : " + mens
							+ " &euro;<BR/>Dur&eacute;e : " + dur
							+ " mois<BR/>Montant total d&ucirc; : " + total_du 
							+ " &euro;<BR/><BR/>" + mention
							+ "<BR/><BR/>Mentions l&eacute;gales : " + ml
							+ " </body></html>";
							
          			var myUrl = "https://www.transcred.com/flux/castosimul.asp?ws=C&mtt=" + document.getElementById('cardPrice').innerHTML + "&id=114I01&bar=" + bar;											

					var myhtmlAtout = '<strong><span class="blueTxtV2">Ce produit pour</span> <span class="red">'
					+ mens + ' &euro; </span> par mois pendant ' 
					+ dur + ' mois.</strong><div class="clear"></div><ul><li> TAEG Fixe : '
					+ teg + '%.</li><li> Montant total d&ucirc; : '
					+ total_du +' &euro;.</li></ul><ul><li><a href="javascript:void(0);" onclick="openContWindow(content);">Mentions l&eacute;gales</a></li><li><a href="javascript:void(1);" onclick="window.open(\'' 
					+ myUrl + '\', \'\', \'location=no,menubar=no,resizable=yes,scrollbars=1,width=630,height=780\')">Autres financements</a></li></ul><div class="clear"></div>';

					div_cast_card.innerHTML += myhtmlAtout;
					
					div_cast_card.style.display='block';
				}
			}
		}
	}
}