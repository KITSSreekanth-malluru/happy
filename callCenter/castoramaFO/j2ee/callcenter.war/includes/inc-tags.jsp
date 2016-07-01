<%--
 * ============================================================================
 * Description : Lance tous les tags necessaires
 * ============================================================================
--%>


<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ page import="atg.servlet.DynamoHttpServletRequest" %>
<dsp:page xml="true">

<!-- Castorama2/EDATIS TAG V2 - Tag de commande -->
<!-- EDATIS / Castorama_DW2 : TAG NL V2 -->
<SCRIPT LANGUAGE="JavaScript">
// Attention ! Faire des escape(s) si nécessaire !
var v0 = escape("<dsp:valueof param='id_client'/>"); // Id_client
var v1 = escape("<dsp:valueof param='email'/>"); // Email

//Date de desinscription: 
// = "" si le client est abonné
// = dateDuJour sinon
var abo_valide = escape("<dsp:valueof param='abonnement_valide'/>");
var v2 = escape("");
if (abo_valide==escape("false") || abo_valide == escape(""))
{
	var datedujour = new Date();
	var date = datedujour.getDate();
	date = escape(date); 
	var mois = datedujour.getMonth();
	mois = mois + 1;
	mois = escape(mois);
	var an = datedujour.getFullYear();
	an=escape(an);
	v2 = escape(date+"/"+mois+"/"+an);
}

// Civilite
var civ = escape("<dsp:valueof param='civilite'/>");
var v3 = "0";
if (civ == escape("Madame"))
	v3 = "1";
else
{
	if (civ == escape("Mlle"))
		v3="2";
}
var v4 = escape("<dsp:valueof param='nom'/>"); // Nom
var v5 = escape("<dsp:valueof param='prenom'/>"); // Prenom
var v6 = escape("<dsp:valueof param='code_postal'/>"); // code_postal
var v7 = escape("<dsp:valueof param='ville'/>"); // Ville
var v8 = escape("<dsp:valueof param='pays'/>"); // Pays

var date_of_birth = escape("<dsp:valueof param='date_de_naissance'/>"); // Date naissance
var day = date_of_birth.substring(8,10);
var month = date_of_birth.substring(5,7)
var year = date_of_birth.substring(0,4);
var v9 = escape(day+"/"+month+"/"+ year);



var v10 = "<dsp:valueof param='nb_pers_foyer'/>"; // Nb_personnes_foyer
if (v10 == "4 et +")
	v10 = "4";

// Client_casto
var v11 = escape("");
var mag = "<dsp:valueof param='magasin'/>"; 
if (mag == "")
	v11 = escape("0");
else
	v11 = escape("1");

// Carte_atout
var v12 = "<dsp:valueof param='carte_atout'/>";
if (v12 == "" || v12 == "false")
	v12 = escape("0");
else
	v12 = escape("1");

// Proprietaire_locataire
var v13 = "<dsp:valueof param='proprietaire_locataire'/>";
if (v13 == "proprietaire")
	v13 = escape("0");
else
	v13 = escape("1");

// Maison
var v14 = "<dsp:valueof param='maison'/>";
if (v14 == "false")
	v14 = escape("0");
else
	v14 = escape("1");

// Jardin
var v15 = "<dsp:valueof param='jardin'/>";
if (v15 == "false")
	v15 = escape("0");
else
	v15 = escape("1");

// Residence_secondaire
var v16 = "<dsp:valueof param='residence_secondaire'/>";
if (v16 == "false")
	v16 = escape("0");
else
	v16 = escape("1");

// Esprit_contemporain
var v17 = "<dsp:valueof param='esprit_contemporain'/>";
if (v17 == "false")
	v17 = escape("0");
else
	v17 = escape("1");

// Esprit_technicolor
var v18 = "<dsp:valueof param='esprit_technicolor'/>";
if (v18 == "false")
	v18 = escape("0");
else
	v18 = escape("1");

// Esprit_charme
var v19 = "<dsp:valueof param='esprit_charme'/>";
if (v19 == "false")
	v19 = escape("0");
else
	v19 = escape("1");
	
// Esprit_authentique
var v20 = escape("<dsp:valueof param='esprit_authentique'/>");
if (v20 == "false")
	v20 = escape("0");
else
	v20 = escape("1");

var edt_randomize = Math.round(Math.random()*10000000000);
var edt_url = 'http://home.edt02.net/emc/Nl_tag_dw2/?s=56600&t=1';
	edt_url += '&r=' + edt_randomize ;
	edt_url += '&v0=' + v0 + '&v1=' + v1 + '&v2=' + v2 + '&v3=' + v3;
	edt_url += '&v4=' + v4 + '&v5=' + v5 + '&v6=' + v6 + '&v7=' + v7;
	edt_url += '&v8=' + v8 + '&v9=' + v9 + '&v10=' + v10 + '&v11=' + v11;
	edt_url += '&v12=' + v12 + '&v13=' + v13 + '&v14=' + v14 + '&v15=' + v15;
	edt_url += '&v16=' + v16 + '&v17=' + v17 + '&v18=' + v18 + '&v19=' + v19;
	edt_url += '&v20=' + v20;

document.write ('<img src="' + edt_url + '" border="0" height="0" width="0">');
document.write ('<p>'+ edt_url);

</SCRIPT>

</dsp:page>
	