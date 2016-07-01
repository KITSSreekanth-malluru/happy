/* SiteCatalyst code version: H.25.
Copyright 1996-2012 Adobe, Inc. All Rights Reserved
More info available at http://www.omniture.com */

var s_account="advcastov4dev"
var s=s_gi(s_account)
var sendOmnitureIfPageLoad = false;
s.prop20="s_code v4 2013-04-12"
/************************** CONFIG SECTION **************************/
/* You may add or alter any code config here. */
s.charSet="ISO-8859-1"
/* Conversion Config */
s.currencyCode="EUR"
/* Link Tracking Config */
s.trackDownloadLinks=true
s.trackExternalLinks=true
s.trackInlineStats=true
s.linkDownloadFileTypes="exe,zip,wav,mp3,mov,mpg,avi,wmv,doc,pdf,xls"
s.linkInternalFilters="javascript:,castorama.fr,castorama.epam.com"
s.linkLeaveQueryString=false
s.linkTrackVars="prop12"
s.linkTrackEvents="None"
s.usePlugins=true;
s.dynamicAccountSelection=true
s.dynamicAccountMatch=window.location.host
s.dynamicAccountList="advcastov4dev=ppr.castorama.fr,prod.castorama.epam.com,castorama.epam.com,atg-rec3-fo.homolo-ecommerce.castorama.fr,atg-rec1-fo.homolo-ecommerce.castorama.fr;advcastov4prod=castorama.fr"
s.trackingServer="wa.castorama.fr"
s.trackingServerSecure="swa.castorama.fr"

s_expectedOmniCallFlag = true;
s_defaultPNameShopSelection = "Overlay:Choix Du Magasin";

function isBrandedKeyword(strKeyword){
	return (strKeyword.match(/casto/)?true:false);
}

var s_channelArray = new Array(	"UN;UNKNOWN",
								"RE;SITES REFERENTS",
								"DR;ACCES DIRECT",
								"IN;INTERNE SITE",
								"SM;RESEAUX SOCIAUX",
								"RT;RETARGETING",
								"CL;CALL CENTER",
								"CMg;COMPARATEURS (Googleprod)",
								"CMo;COMPARATEURS (Autres)",
								"EM;EMAILS",
								"DS;DISPLAY",
								"AF;AFFILIATION",
								"SEb;SEM (marque)",
								"SEn;SEM (hors marque)",
								"SOb;SEO (marque)",
								"SOn;SEO (hors marque)",
								"PA;PARTENAIRE",
								"RB;REAL-TIME BIDDING");

function s_getChannelName(channelID){
	for(i=0;i<s_channelArray.length;i++){
		s_tmpChannelVar = s_channelArray[i].split(";")
		if(s_tmpChannelVar[0]== channelID)
			return s_tmpChannelVar[1];
	}
	return channelID;
}

function s_trackECMP(){
	s.campaign=s.getValOnce(s.getQueryParam("ecmp,pid"), 's_campaign', 0) 
	s.eVar38=s.getValOnce(s.getQueryParam("o2mc"), 's_o2mc', 0) 
	if (!s.campaign) s.campaign=s.getValOnce(s.getQueryParam("zanpid"), 's_campaign', 0) 
	if (!s.campaign) s.campaign=s.getValOnce(s.getQueryParam("zanpid","",document.referrer), 's_campaign', 0)

	// Internal promotions
	if(typeof s_promotionID != "undefined" && s_promotionID!="")
		s.eVar10 = s_promotionID;
	else
		s.eVar10=s.getQueryParamA('promo');

	if (s.eVar10) {	
		if (s.eVar10.indexOf('#')>-1) s.eVar10=s.eVar10.substring(0,s.eVar10.indexOf('#')); //erase "#" and all text after it
		s.eVar10=s.getValOnce(s.eVar10, 's_v10', 0) // register internal promotions once per visit
	}		

	// Campaign Bounce Rate Calculation
	s.ctru1 = "2"
	if (s.campaign){
		s.events=s.addToList(s.events,"event20");	// Campaign Click Through
		s.ctru1 = "1"
		s.prop13='D="EC|"+v0+"|"+pageName';
		s.eVar19=s.eVar20="D=v0";	//Other Campaign allocation methodologies
	}
	else if(s.eVar10){
			s.prop13='D="IC|"+v10+"|"+pageName';
	}
	else if(s.prop1){
		s.prop13='D="IS|"+c1+"|"+pageName';
	}

	// Warning: this code should always be outside the doplugin call as it could be called
	// at every clic onthe site and therefore disrupt the cookie values
	if(s.pageName != s_defaultPNameShopSelection){
		s.ctru2 = s.getPreviousValue(s.ctru1, "s_ctru2", "");
		if (!s.campaign && s.ctru2 == "1") {
			s.events=s.addToList(s.events,"event32");		// Campaign 2nd click (used to be event21)
		}
	}

	s.eVar23 = s.crossVisitParticipation(s.campaign,'s_cvp','30','3','>','purchase'); // Campaign Stacking - 30 days, 3 values max

	// All landings
	s.ctru_incSEO_1 = "2" ;
	if (s.getVisitStart("s_vStart")=="1"){
		
		if (s.eVar33 != "" && s.eVar33.toLowerCase() != "web"){
			s.eVar34= "Nouvelle visite avec magasin déjà connu";
			s.events=s.addToList(s.events,"event52"); // Shop Location Automatically Selected
		}

		s.events=s.addToList(s.events,"event49");		// New Visit Started

		t_ref = (typeof s_forceRef != "undefined"?s_forceRef:document.referrer);
		t_refDomain = t_ref.substring(t_ref.indexOf("//")+2);
		t_refDomain = t_refDomain.substring(0,t_refDomain.indexOf("/")!=-1?t_refDomain.indexOf("/"):t_refDomain.length).toLowerCase();
		t_source="";

		if(t_refDomain.indexOf("google.")!= -1){
			t_source=(s.getQueryParam("pid")!=""?"SE":"SO");
			t_refSEng = "GOOGLE";
			t_refKwd = s.getQueryParam('q','',t_ref);
			t_refKwd = (t_refKwd!=""?t_refKwd:"Keyword Encrypted by Google");
			t_refKwd = t_refKwd.replace(/\+/g," ").toLowerCase();
			t_refKwdType = (isBrandedKeyword(t_refKwd)?"b":"n");
			t_source += t_refKwdType
		} 
		else if (t_refDomain.indexOf("yahoo.")!= -1){
			t_refSEng = "YAHOO";
			t_source=(s.getQueryParam("pid")!=""?"SE":"SO");
			t_refKwd = s.getQueryParam('p','',t_ref);
			t_refKwd = t_refKwd.replace(/\+/g," ").toLowerCase();
			t_refKwdType = (isBrandedKeyword(t_refKwd)?"b":"n");
			t_source += t_refKwdType
		}
		else if (t_refDomain.indexOf("bing.")!= -1){
			t_refSEng = "BING";
			t_source=(s.getQueryParam("pid")!=""?"SE":"SO");
			t_refKwd = s.getQueryParam('q','',t_ref);
			t_refKwd = t_refKwd.replace(/\+/g," ").toLowerCase();
			t_refKwdType = (isBrandedKeyword(t_refKwd)?"b":"n");
			t_source += t_refKwdType
		}
		else if(s.getQueryParam("zanpid")!=""){
			t_source="AF";
		}
		else if(s.campaign){
			t_campaignStart = s.campaign.substring(0,s.campaign.indexOf("_")).toLowerCase();
			switch(t_campaignStart){
				case "dis":
					t_source="DS";
				break;
				case "nl":
					t_source="EM";
				break;
				case "rtb":
					t_source="RB";
				break;
			}
			if(t_source==""){
				switch(s.campaign){
					case "kelkoo":
					case "leguide":
					case "idealo":
					case "achetezfacile":
						t_source="CMo";
					break;
					case "googleprod":
						t_source="CMg";
					break;
					case "cc":
					case "cc_cde":
						t_source="CL";
					break;
					case "RET_criteo":
					case "RET_mythings":
					case "RET_nextperformance":
						t_source="RT";
					break;
				}
			}
		}
		else{
			if (t_refDomain.indexOf("facebook.com")!= -1 || t_refDomain.indexOf("twitter.com")!= -1 || t_refDomain.indexOf("youtube.com")!= -1 || t_refDomain.indexOf("dailymotion.com")!= -1 || t_refDomain.indexOf("flickr.com")!= -1){
				t_source="SM";
			}
			else if (t_refDomain.indexOf("castorama.fr")!= -1){
				t_source="IN";
			}
			else if(t_refDomain==""){
				t_source="DR";
			}
			else if(t_refDomain!=""){
				t_source="RE";
			}
			else {
				t_source="UN";
			}
		}

		t_sourceFullName = s_getChannelName(t_source)
		s.eVar28 = t_sourceFullName;
		s.eVar29 = s.crossVisitParticipationWithoutLastValOnce(t_source,'s_cvpseo','30','15','>',''); // Campaign Channel MultiTouch (inc. SEO) - 30 days, 5 values max
		if(t_source.indexOf("SO") != -1 || t_source.indexOf("SE") != -1)
			s.eVar30 = s.crossVisitParticipationWithoutLastValOnce(t_source,'s_cvpseonly','30','15','>',''); // Campaign Channel MultiTouch (inc. SEO) - 30 days, 5 values max

		if(s.campaign){
			s.eVar27 = "D=v0";
			s.eVar31 = "D=v0";
			s.ctru_incSEO_1 = "1"
			s.events=s.addToList(s.events,"event38");		// Campaign 1st click (inc. SEO)
			if(t_source.indexOf("SE") != -1){ // SEM
				s.eVar31 = "SEM_" + t_refSEng + "_" + t_refKwd;
			}
		}
		else if(t_source.indexOf("SO") != -1){ // SEO
			s.eVar27 = "SEO_" + t_refSEng + "_" + t_refKwd;
			s.eVar31 = "D=v27";
			s.prop13='D="EC|"+v27+"|"+pageName';
			s.ctru_incSEO_1 = "1"
			s.events=s.addToList(s.events,"event38");		// Campaign 1st click (inc. SEO)
		}
	}

	if(s.pageName != s_defaultPNameShopSelection){
		s.ctru_incSEO_2 = s.getPreviousValue(s.ctru_incSEO_1, "s_ctruSEO2", "");
		if (!s.eVar27 && s.ctru_incSEO_2 == "1") {
			s.events=s.addToList(s.events,"event39");		// Campaign 2nd click (inc. SEO)
		}
	}
}

function s_makeCampaignAssitRequest(listOfChannels){
	t_listOfChannels = listOfChannels.split(">")
	if(t_listOfChannels.length>1){
		s.eVar28=s_getChannelName(t_listOfChannels[0]);
		void(s.tl(true,"o","Campaign Channel Assist"));
		if(t_listOfChannels.length>2){
			s_makeCampaignAssitRequest(listOfChannels.replace(t_listOfChannels[0]+">",""));
		}
		else{
			s.eVar28=s_getChannelName(t_listOfChannels[1]);
			s.linkTrackVars="eVar28";
			s.events="";
			void(s.tl(true,"o","Campaign Channel Assist - Back to Regular Channel"));
			s.linkTrackVars="prop12"
			s.linkTrackEvents="None"
		}
	}
	else{
		s.events="";
		s.linkTrackVars="prop12"
		s.linkTrackEvents="None"
	}
}

function s_CampaignAssistTrigger() {
	s.linkTrackVars="eVar28,events,products";
	s.linkTrackEvents="event40";
	s.products=";"+s_prodList.replace(/,/g,",;");
	s.events="event40";

	var arry=new Array()
	c=s.c_r('s_cvpseo')
	if(c&&c!=''){arry=s.split(c,'],[');
		for(q=0;q<arry.length;q++){
			z=arry[q];z=s.repl(z,'[','');
			z=s.repl(z,']','');
			z=s.repl(z,"'",'')
			arry[q]=s.split(z,',')
		}
	}
	g=0
	var h=new Array()
	var td=new Date()
	var start=arry.length-15<0?0:arry.length-15
	for(var x=start;x<arry.length;x++){
		var diff=Math.round((td.getTime()-arry[x][1])/86400000);
		if(diff<30){
			h[g]=unescape(arry[x][0]);
			g++;
		}
	}
	var r=s.join(h,{delim:">"});

	s.usePlugins=false;
	s_makeCampaignAssitRequest(r);
}

function s_doPlugins(s) {
	s_account=s.un;

	// 2010-05-11: Code fix for promoid & documentid page with no pagename
	if(typeof s_pName !="undefined" && s_pName==""){
		if(s.getQueryParam("promoId")){
			s_channel = "Pages operations";
			s_pName =s_channel +":"+ s.getQueryParam("promoId");
		}
		else if(s.getQueryParam("documentId")){
			s_channel = "Pages pedagogie";
			s_pName =s_channel +":"+ s.getQueryParam("documentId");
		}
	}
	// 2010-05-11: END of Code Fix

	s.pageName=(typeof s_pNameOverwrite!="undefined" && s_pNameOverwrite!="")?s_pNameOverwrite:((typeof s_pName !="undefined")?s_pName:"Not Set");
	if(s.pageName==""){
		tmp_loc = location.pathname;
		if(tmp_loc.indexOf("/store/")!=-1)
			tmp_loc = tmp_loc.substring(tmp_loc.indexOf("/store/")+7);
		tmp_loc = tmp_loc.toLowerCase().replace(/\//g,":").replace(".jsp","");
		//Removes the jsessionid if found
		if(tmp_loc.indexOf(";jsessionid=")!=-1)
			tmp_loc = tmp_loc.substring(0,tmp_loc.indexOf(";jsessionid="));
		s.pageName = tmp_loc;
		s_channel = (typeof s_channel !="undefined" && s_channel!=""?"Section set with no pagename"+s_channel:"No section set");
	}
		
	var urlCiblee = window.location.href;
	if(urlCiblee.search("cat_id_4075.htm") != -1 && s.pageName.search("FenetresSurMesure") == -1)
			{
			 s.pageName =  "FenetresSurMesure:Accueil";
			}

	
    if(typeof pTestAB!="undefined" && pTestAB !="")
    {
      s.pageName =  "Home Page test";
    }

	s.channel=(typeof s_channelOverwrite!="undefined" && s_channelOverwrite!="")?s_channelOverwrite:((typeof s_channel !="undefined")?s_channel:"Not Set");

	s.prop17=(typeof s_loginStatus == "undefined"?"Not Set": s_loginStatus); 
	s.prop18="D=c17+'|'+pageName";
	s.prop19="Non-Catégorisé";
	s.eVar32= (typeof s_userId !="undefined"?s_userId :"");

	s.eVar33=(typeof s_curLoc == "undefined"?"": s_curLoc); 
	tmp_location = s.c_r("s_shopselect");
	if(typeof tmp_location != undefined && tmp_location != ""){
		locArray = tmp_location.split("||");
		switch(locArray[0]){
			case "close":
				s.eVar34= "Choix par défaut (fermeture overlay)"
			break;
			case "default":
				s.eVar34= "Choix par défaut (sélection utilisateur)"
			break;
			case "other":
				s.eVar34= "Choix différent du magasin par défaut"
			break;
			default:
				s.eVar34= "Bug : cookie trouvé avec type inconnu ("+ locArray[0] +")"
			break;
		}
		s.eVar33= locArray[1];
		s.events=s.addToList(s.events,"event51"); // Shop Location Selected
		s.c_w("s_shopselect","",0)
	}

	s.prop9 = s_getSections(s.pageName,s.channel,typeof s_pState != "undefined"?s_pState:""); // Current Section & Subsection
	s.prop10=s.getPreviousValue(s.prop9,'gpv_p2',''); // Last Section & Subsection

	// Copy variables
	s.hier2="D=pageName"; // Hierarchy
	s.prop11="D=g"; // Page URL

	//Removes the jsessionid if found
	if(location.href.indexOf(";jsessionid=")!=-1){
		tmp_loc = location.href;
		s.prop11 = tmp_loc.substring(0,tmp_loc.indexOf(";jsessionid="))+(tmp_loc.indexOf("?")!=-1?tmp_loc.substring(tmp_loc.indexOf("?")):"");
	}

	s.prop12="D=pageName"; // Page Name Copy
	s.prop13="D=pageName"	// Pathing for campaigns & int. search
	s.prop15="D=pageName"	// Pathing for filter type
	s.prop16="D=pageName"	// Pathing for filter type & value
	s.events=s.addToList(s.events,"event9"); // Page View event
	
	s.pPName=s.getPreviousValue(s.pageName, "s_ppn", ""); 	// Temporary Value used for cart addition classification
	
	if(typeof s_pState != "undefined"){
		s.pPState=s.getPreviousValue(s_pState, "s_pps", ""); 	// Temporary Value used for cart addition classification
		switch(s_pState){
			case "error:404":
				s.pageName="Error 404 - Page Not Found";
				s.channel="Error";
				s.pageType="errorPage";
				s.events=s.addToList(s.events,"event12"); // Error 404
				s.events=s.addToList(s.events,"event14"); // Any error
				s.eVar4="+1";
				s.prop19="Erreur";
			break;
			case "error":
				s.pageName="Error - " + s_errorMessage;
				s.channel="Error";
				s.events=s.addToList(s.events,"event13"); // Non 404 Error
				s.events=s.addToList(s.events,"event14"); // Any error
				s.eVar4="+1";
				s.prop19="Erreur";
			break;
			case "search:result":
				s.prop1=s_searchString;
				s.prop3=s_searchResultCount;
				//// Intervals and translations
				s.prop3=s.createInterval(s.prop3,[0,5,10,20,50,100,200],'resultat','resultats');
				if ((s.pageName)&&(s.prop3=="0 resultats")&&(s.pageName.indexOf("0 resultats")==-1)) s.pageName=s.pageName+" (0 resultats)";

				s.events=s.addToList(s.events,"event1"); // Product Search
				s.events=s.addToList(s.events,"event"+(s_searchResultCount=="0"?"5":"4")); // Search with/without result
				s.eVar1="D=c1";
				s.eVar2="D=c3";

				s.prop2= "Other Search";

				if(typeof s_searchType != "undefined" && s_searchType != ""){
					switch(s_searchType){
						case "tagcloud":
							s.prop2= "Tag Cloud";
							s.events=s.addToList(s.events,"event18"); // Tag Cloud Search
						break;
						case "reg":
							s.prop2= "Normal Search";
							s.events=s.addToList(s.events,"event19"); // Regular Search
						break;
					}
				}

				s.prop4="D=c2+'|'+c10";	
				s.prop19="Résultat de recherche";

				if(typeof s_prodList !="undefined" && s_prodList!="" && s_FullFilterTypes!="None" && s_FullFilterTypes!=""){
					s.products=";"+s_prodList.replace(/,/g,",;");
					s.events=s.addToList(s.events,"event7"); // Filter Applied
					s.eVar21=s_FullFilterTypes.replace(/,/g,"/");	// List of All Filters Applied
					s.prop6=s_LastFilterType; // Last Filter Type
					s.prop7=s_LastFilterValue; // Last Filter Value
					s.prop8=s_FullFilterTypes; // All Filter Types

					s.prop15='D=pageName+":"+c6'; 	// Pathing for pagename including filter type granularity
					s.prop16='D=pageName+":"+c6+":"+c7';	// Pathing for pagename including filter type & value granularities

					s.eVar9="D=c6";
				}

			break;
			case "productsList":
				s.events=s.addToList(s.events,"event2"); // Product List View
				s.products=";"+s_prodList.replace(/,/g,",;");

				if(typeof s_highlitedProduct!= "undefined"&& s_highlitedProduct!=""){
					// Remove the product from the string, if found
					if(s.products.indexOf(";"+s_highlitedProduct)!=-1){
						if(s.products.indexOf(";"+s_highlitedProduct+",")!=-1){
							s.products = s.products.replace(";"+s_highlitedProduct+",","");
						}
						else{
							s.products = s.products.replace(";"+s_highlitedProduct,"");
						}
					}
					// Add the product with its event
					s.events=s.addToList(s.events,"event6"); // Product Highlighted Count
					s.products+=",;"+s_highlitedProduct+";;;event6=1;evar8="+s.prop9.replace(/(,|;)/g," ");
				}
/*
				if(typeof s_stockVisuPageType != "undefined" && s_stockVisuPageType == "stockCheck:productList" && typeof s_stockVisuProdID!= "undefined" && typeof s_stockVisuStatus!= "undefined"){
					s.events=s.addToList(s.events,"event30"); // Stock visualized
					s.eVar17="Liste Produits";
					if(typeof s_highlitedProduct!= "undefined"&& s_highlitedProduct!="" && s_stockVisuProdID==s_highlitedProduct){
						s.products += "|evar22="+s_stockVisuStatus
					}
					else{
						s.products=s.products.replace(";"+s_stockVisuProdID,";"+s_stockVisuProdID+";;;;evar22="+s_stockVisuStatus)
					}
				}
*/
				if(s_FullFilterTypes!="None" && s_FullFilterTypes!=""){
					s.pageName += (s.pageName.toLowerCase().indexOf("filtre")==-1)?":Filtre":"";
					s.events=s.addToList(s.events,"event7"); // Filter Applied
					s.eVar21=s_FullFilterTypes.replace(/,/g,"/");	// List of All Filters Applied
					s.prop6=s_LastFilterType; // Last Filter Type
					s.prop7=s_LastFilterValue; // Last Filter Value
					s.prop8=s_FullFilterTypes; // All Filter Types

					s.prop15='D=pageName+":"+c6'; 	// Pathing for pagename including filter type granularity
					s.prop16='D=pageName+":"+c6+":"+c7';	// Pathing for pagename including filter type & value granularities

					s.eVar9="D=c6";
				}
				s.prop19="Liste de Produits";
			break;
			case "productView":
				s.events=s.addToList(s.events,"event3"); // Product Item View
				s.events=s.addToList(s.events,"prodView"); // Default Product View
				s.products=";"+s_prodList.replace(/,/g,",;");
				if(s_prodList.indexOf(",") != -1 && s.getQueryParam("skuId").toLowerCase().indexOf("casto") !=-1){
					tmp_skuID = s.getQueryParam("skuId").toLowerCase();	tmp_skuID = tmp_skuID.substring(tmp_skuID.indexOf("casto")+5)
					s.products=";" + tmp_skuID
				}

				if(typeof s_prodPrice != "undefined")
					s.events=s.addToList(s.events,"event53"); // Local Product Price
				s.products += ";;;"+(typeof s_prodPrice != "undefined"?"event53=" + s_prodPrice:"")+";evar6="+s_productDeliveryTime; // Product Delivery Time

				if(typeof s_stockVisuPageType != "undefined" && s_stockVisuPageType == "stockCheck:productView" && typeof s_stockVisuProdID!= "undefined" && typeof s_stockVisuStatus!= "undefined"){

					if(s_stockVisuProdID==s_prodList){
						s.events=s.addToList(s.events,"event30"); // Stock visualized
						s.eVar17="Fiche Produit";
						s.products += "|evar22="+s_stockVisuStatus.replace(/,/g,"_");
					}
				}
				s.prop19="Fiche Produit";
				s.channel="Fiche Produit";
			break;
			case "stockCheck:productView:VisuTab":
				if(typeof s_stockVisuPageType != "undefined" && typeof s_stockVisuProdID!= "undefined" && typeof s_stockVisuStatus!= "undefined"){
					s.events=s.addToList(s.events,"event30"); // Stock visualized
					s.eVar17=s_stockVisuPageType;
					s.products=";"+s_stockVisuProdID+";;;;evar22="+s_stockVisuStatus.replace(/,/g,"_")
				} 
			break;
			case "productView:print":
				s.events=s.addToList(s.events,"event28"); // Product Item View - Print Page
				s.pageName+="[PrintView]";
				s.products=";"+s_prodList;
				s.prop19="Fiche Produit";
			break;			
			case "productView:taf:confirm":
				s.events=s.addToList(s.events,"event29"); // Product Sent to a Friend
				s.pageName+="[Envoi à un ami]";
				s.products=";"+s_prodList;
			break;
			case "productComparison":
				s.events=s.addToList(s.events,"event8"); // Product Compared
				s.products=";"+s_prodList.replace(/,/g,",;");
				s.eVar11="+1"; // Product Comparison Counter

				if(typeof s_stockVisuPageType != "undefined" && s_stockVisuPageType == "Comparaison de Produits" && typeof s_stockVisuProdID!= "undefined" && typeof s_stockVisuStatus!= "undefined"){
					s_tmpProdList = s_stockVisuProdID.split(",")
					s_tmpStatusList = s_stockVisuStatus.split(",")


					if(s_tmpStatusList.length == s_tmpProdList.length){
						s.events=s.addToList(s.events,"event30"); // Stock visualized
						s.eVar17= s_stockVisuPageType;
						for(i=0;i<s_tmpProdList.length;i++){
							s.products=s.products.replace(";"+s_tmpProdList[i],";"+s_tmpProdList[i]+";;;;evar22="+s_tmpStatusList[i])
						}
					} 
				}
			break;			
			case "cart:add:1st":
				s.events=s.addToList(s.events,"scAdd"); // Product Added
				s.events=s.addToList(s.events,"scOpen"); // Cart Opened
				s.products=";"+s_prodList+";;;;evar12="+getPreviousPageType();
			break;			
			case "cart:add:more":
				s.events=s.addToList(s.events,"scAdd"); // Product Added
				s.products=";"+s_prodList+";;;;evar12="+getPreviousPageType();	
			break;			
			case "cart:fullview":
				s.events=s.addToList(s.events,"scView"); // Cart Viewed
				s.products=";"+s_prodList.replace(/,/g,",;");
				
				if(typeof s_promoCodeAdded != "undefined" && s_promoCodeAdded!=""){
					s.events=s.addToList(s.events,"event22"); // Promo Code Added
					s.eVar14=s_promoCodeAdded;
				}
			break;			
			case "order:delivery":
				s.prop22=(typeof s_selectedDeliveryMethod != "undefined" && s_selectedDeliveryMethod !=""?s_selectedDeliveryMethod:"Not Set")
				s.events=s.addToList(s.events,"scCheckout"); // Cart Checkout
				s.events=s.addToList(s.events,"event15"); // Cart :Step 1: delivery
				s.products=";"+s_prodList.replace(/,/g,",;");
				if(s_accountStatus=="new account"){
					s.events=s.addToList(s.events,"event25"); // Account Created
					if(s_newsletterRegistration!="None")
						s.events=s.addToList(s.events,"event26"); // Newsletter Subscribed
					s.eVar15 = "New Account - With order: " + s_newsletterRegistration;
				}
			break;
			case "order:payment":
				s.prop22=(typeof s_selectedDeliveryMethod != "undefined" && s_selectedDeliveryMethod !=""?s_selectedDeliveryMethod:"Not Set")
				s.events=s.addToList(s.events,"event16"); // Cart :Step 2: payment
				s.products=";"+s_prodList.replace(/,/g,",;");
			break;
			case "order:stockValidation:inprogress":
				s.prop22=(typeof s_selectedDeliveryMethod != "undefined" && s_selectedDeliveryMethod !=""?s_selectedDeliveryMethod:"Not Set")
				s.events=s.addToList(s.events,"event54"); // Cart :Step 2.1 validation
				s.products=";"+s_prodList.replace(/,/g,",;");
			break;
			case "order:stockValidation:ko":
				s.prop22=(typeof s_selectedDeliveryMethod != "undefined" && s_selectedDeliveryMethod !=""?s_selectedDeliveryMethod:"Not Set")
				s.events=s.addToList(s.events,"event55"); // Cart :Step 2.2 validation KO
				s.products=";"+s_prodList.replace(/,/g,",;");
			break;
			case "order:confirmation":
				s.prop22=(typeof s_selectedDeliveryMethod != "undefined" && s_selectedDeliveryMethod !=""?s_selectedDeliveryMethod:"Not Set")
				if(s.prop22=="Not Set" || s.prop22 == "home delivery"){
					s.events=s.addToList(s.events,"event56"); // Purchase : Home delivery
				}
				else{
					s.events=s.addToList(s.events,"event57"); // Purchase : Store pick-up
				}
				s.events=s.addToList(s.events,"purchase"); // Purchase
				s.events=s.addToList(s.events,"event17"); // Cart :Step 3: confirmation
				s.products=";"+s_prodList.replace(/,/g,",;");
				s.eVar18 = s.transactionID = s.purchaseID= s_orderID;

				var discountList = s_discountInformation.split(",");
				var productList = s.products.split(",");
				var deliveryList = s_productDeliveryTime.split(",");
				var stdShpFees = s_productStdShippingFee.split(",");

				s.products="";
				discountAppliedFlag = false;
				for(i=0;i<productList.length;i++){
					discountAppliedFlagI = (discountList[i]==";"||discountList[i]=="")?false:true; 	// Is there a discount on current product
					discountAppliedFlag = discountAppliedFlagI?true:discountAppliedFlag; // Is there a discount on any product
					if(typeof discountList[i] != "undefined")
						discountListI = discountList[i].split(";");
					
					s.products+=productList[i]+(discountAppliedFlagI?";event11="+discountListI[1]+";evar3="+discountListI[0]+"|":";;")+"evar6="+deliveryList[i]+"|evar13="+stdShpFees[i]+",";
				}
				s.products+=";Frais de Traitement;1;"+s_processingFee;
				s.products+=",;Frais de Livraison;1;"+s_shippingFee;
				if (discountAppliedFlag)
					s.events=s.addToList(s.events,"event11"); // Discount Applied on Order

				if(typeof s_promoCodeUsed != "undefined" && s_promoCodeUsed!=""){
					s.events=s.addToList(s.events,"event23"); // Promo Code Added
					s.eVar14=s_promoCodeUsed;
					s.product+="; Promo Code " + s_promoCodeUsed +";1;0;event24="+s_promoCodeAmount;
				}

			break;
			case "newsletter:confirmation":
				if(s_newsletterRegistration!="None")
					s.events=s.addToList(s.events,"event26"); // Newsletter Subscription
				s.eVar15 = "Standalone : " + s_newsletterRegistration;
			break;			
			case "account:confirmation":
				s.events=s.addToList(s.events,"event25"); // Account Created
				if(s_newsletterRegistration!="None")
					s.events=s.addToList(s.events,"event26"); // Newsletter Subscribed
				s.eVar15 = "New Account - No order: " + s_newsletterRegistration;
			break;
			case "orderreceipt:print":
				s.events=s.addToList(s.events,"event31"); // Order History Page
				s.pageName="Espace client:Historique des achats:Impression";
				s.channel="Espace client";
			break;
			case "":
				if(s_channel.toLowerCase()=="catalogue produit")
					s.prop19="Catégorie de Produits";
			break;
		}
	}
	else{
		s.pPState=s.getPreviousValue("", "s_pps", ""); 	// Temporary Value used for cart addition classification
		if(s_channel.toLowerCase()=="catalogue produit")
			s.prop19="Catégorie de Produits";

		if(typeof s_stockVisuPageType != "undefined" && typeof s_stockVisuProdID!= "undefined" && typeof s_stockVisuStatus!= "undefined"){
			if(s_stockVisuPageType == "stockCheck:advice"){
				s.events=s.addToList(s.events,"event30"); // Stock visualized
				s.eVar17="Page Conseil";
				s.products=";"+s_stockVisuProdID+";;;;evar22="+s_stockVisuStatus
			}
		} 
	}
	if(typeof s_savedCartContent != "undefined" && s_savedCartContent!=""){
		s.eVar16 = s_savedCartContent;
		s.events=s.addToList(s.events,"event27"); // Saved Cart reopened
	}

	// Populate s.prop8 for pathing if no value set
	s.prop8=s.prop8!=""?s.prop8:"D=pageName";

	// Campaigns
	if(s_expectedOmniCallFlag){
		s_trackECMP();
		s_expectedOmniCallFlag = false;
	}

	s.previousSearchedKeyword = s.getPreviousValue(s.prop1, "s_psk", "");
	if (s.getQueryParamA('isSearchResult')=="true" && s.previousSearchedKeyword !="no value"){
		s.eVar7=s.previousSearchedKeyword;
		s.events=s.addToList(s.events,"event10");		// Search Keyword that was clicked
	}

	// Timeparting
	var tmp_curYear=(new Date()).getFullYear();
	s.prop14=s.getTimeParting("d","+1",tmp_curYear)+"-"+s.getTimeParting("h","+1",tmp_curYear);	// TimeParting (prop)
	s.eVar5="D=c14";
	
	//Replaces accentuated characters with regular ones.
	if(s.pageName)
		s.pageName = s_strReplacement(s.pageName);
	if(s.channel)
		s.channel = s_strReplacement(s.channel);
	if(s.products)
		s.products = s_strReplacement(s.products);


	// Campaign Asssist Code
	if(typeof(s_pState)!="undefined" && s_pState == "order:confirmation" && s.c_r('s_cvpseo') != ""){
		setTimeout("s_CampaignAssistTrigger()",1000);
	}
}

s.doPlugins=s_doPlugins;

s.loadModule("Media")
s.Media.autoTrack=false
s.Media.trackWhilePlaying=true
s.Media.trackVars="None"
s.Media.trackEvents="None"

s.Media.trackUsingContextData = true
s.Media.contextDataMapping = {
  "a.media.name":"eVar35,prop23",
  "a.media.segment":"eVar36",
  "a.contentType":"eVar37",
  "a.media.timePlayed":"event61",
  "a.media.view":"event58",
  "a.media.segmentView":"event59",
  "a.media.complete":"event60",
  "a.media.milestones":{
    25: "event62",
    50: "event63",
    75: "event64"
   }
};
s.Media.trackSeconds = 30;
s.Media.trackMilestones = "25,50,75";
s.Media.SegmentByMilestones = true;
s.Media.SegmentByMilestones = false;
s.Media.completeByCloseOffset = true;
s.Media.completeCloseOffsetThreshold = 5;

function s_videoStart(mediaName,mediaLength,mediaPlayerName){
	s.Media.open(mediaName,mediaLength,mediaPlayerName);
	s_videoPlay(mediaName,0);
}

function s_videoEnd(mediaName,mediaLength){
	s_videoStop(mediaName,mediaLength);
	s.Media.close(mediaName);
}

function s_videoPlay(mediaName,mediaOffset, segmentNum, segment, segmentLength){
	if(typeof segmentNum != "undefined" && typeof segment != "undefined" && typeof segmentLength != "undefined")
		s.Media.play(mediaName,mediaOffset, segmentNum, segment, segmentLength);
	else
		s.Media.play(mediaName,mediaOffset);
}

function s_videoStop(mediaName,mediaOffset){
	s.Media.stop(mediaName,mediaOffset);
}

function s_videoSkip(mediaName,mediaStopTime,mediaStartTime, segmentNum, segment, segmentLength){
	s.Media.stop(mediaName,mediaStopTime);
	if(typeof segmentNum != "undefined" && typeof segment != "undefined" && typeof segmentLength != "undefined")
		s.Media.play(mediaName,mediaStartTime, segmentNum, segment, segmentLength);
	else
		s.Media.play(mediaName,mediaStartTime);
}


function s_sendOmnitureInfo(pName,channel,pState,val1,val2){
	//var s=s_gi(s_account);
	s_doNotTrackFlag = false

	s_expectedOmniCallFlag = true;
	s.trackingServer="wa.castorama.fr";
	s.trackingServerSecure="swa.castorama.fr";
	s.events="";
	s.products="";
	s_pState = "";
	s.campaign="";
	for(i=1;i<=50;i++){eval("s.eVar"+i+"=''");eval("s.prop"+i+"=''")}

	if(typeof(pState) != "undefined"){
		s_pState=pState;
		switch(s_pState){
			case "productComparison":
				s_prodList=val1;
				if(val2!=null){
					s_stockVisuPageType = "Comparaison de Produits"; 
					s_stockVisuProdID = val1;
					s_stockVisuStatus = val2;
				}
			break;
			case "stockCheck:productView:VisuTab":
				pName = s_pName+ "[Tab Visu.]";
				channel = s_channel;
				s_stockVisuPageType = "Fiche Produit : Tab Visualisation des Stocks"; 
				s_stockVisuProdID = val1;
				s_stockVisuStatus = val2;
			break;
			case "location:select":
				pName = s_defaultPNameShopSelection;
				channel = "Choix du magasin";
			break;
			case "location:close":
				s_doNotTrackFlag = true;
				s.c_w("s_shopselect","close||" + val1,0)
			break;
			case "location:select:default":
				s_doNotTrackFlag = true;
				s.c_w("s_shopselect","default||" + val1,0)
			break;
			case "location:select:other":
				s_doNotTrackFlag = true;
				s.c_w("s_shopselect","other||" + val1,0)
			break;
			case "contextChange:promo":
				pName = "Overlay:Avertissement Changement de Contexte[Bonnes Affaires]";
				channel = "Choix du magasin";
			break;
			case "contextChange:promo:ok":
				pName = "Overlay:Avertissement Changement de Contexte[Bonnes Affaires]:Validation";
				channel = "Choix du magasin";
			break;
		}
	}
	s_pName = pName;
	s_channel = channel;

	if(!s_doNotTrackFlag)
		s.t(); 	
}

// Legacy function updated to use new function
function s_sendVirtualPageName(newPageName,newChannel){
	defaultChannelName = "" + newPageName.split(":")[0];
	s_sendOmnitureInfo(newPageName,(typeof newChannel!= "undefined")?newChannel:defaultChannelName);
}


if(typeof(Adversitement) == 'undefined') {
  var Adversitement = {};
}

function s_strReplacement(str){
	newstr = str.replace(/[àâ]/g,"a")
	newstr = newstr.replace(/[ÀÂ]/g,"A")
	newstr = newstr.replace(/[éèêë]/g,"e")
	newstr = newstr.replace(/[ÉÈÊË]/g,"E")
	newstr = newstr.replace(/[ùüû]/g,"u")
	newstr = newstr.replace(/[ÙÛÜ]/g,"U")
	newstr = newstr.replace(/[îï]/g,"i")
	newstr = newstr.replace(/[ÎÏ]/g,"I")
	return newstr;
}


function s_getSections(str, defaultVal,pState){
	newVal = defaultVal;
	var pNList=str.split(":");
	if(pNList.length>=2 && pNList[0]=="Catalogue"){
		newVal ="";
		for(i=1;i<pNList.length-(pState=="productView"||(pState=="productsList" && str.toLowerCase().indexOf("filtre")!=-1)?1:0);i++)
			newVal += pNList[i] + ":";
		newVal = newVal.substring(0,newVal.length-1);
	}
	return newVal
}

function getPreviousPageType(){
	if(s.pPState=="search:result")
		return "Search Result List"
	else if(s.pPState=="productsList")
		return "Product List"
	else if(s.pPState=="productView")
		return "Product View"
	else if(s.pPName.indexOf("home page")!=-1)
		return "HomePage"
	else
		return (s.pPState==""?"Page Name "+s.pPName:"Page State : "+s.pPState)
}

Adversitement.extra = function() {
  //Private variables
  /**
   * Handle javascript errors
   * @private   
   */     
  var onError = function(s_account, prop, msg, url, l) {
    var p,ltv,lte;
    var err = url+':'+msg+':'+l;
    var s = s_gi(s_account);
    ltv = s.linkTrackVars;
    lte = s.linkTrackEvents;  
    if(prop) {
      p = s[prop];
      s[prop] = err;
      s.linkTrackVars = prop;
    }
    s.linkTrackEvents = 'None';
    s.tl(this,'o','Javascript Error');
    if(p) {
      s[prop] = p;
    }
    s.linkTrackVars = ltv;
    s.linkTrackEvents = lte;
  };
 
  //Public variables
  return /** @scope Adversitement.extra */ {
 
    
    /**
     * On Javascript error send error url, message and line number as link to SiteCatalyst
     * @param s_account accountId
     * @param prop CustomInsight variable name or eVar variable name          
     */         
    addOnErrorHandler: function(s_account, prop) {
      if(window.onerror && typeof(window.onerror)=='function') {
        var tmp = window.onerror;
        window.onerror = function(msg, url, l) {
          tmp(msg, url, l);
          onError(s_account, prop, msg, url, l);
        };
      } else {
        window.onerror = function(msg, url, l) {
          onError(s_account, prop, msg, url, l);
        };
      }
    }
  }
}(); // create the actual object

//Adversitement.extra.addOnErrorHandler(s_account, 'prop5');
/*
 * Function to transform specific integer to an interval
 */
s.createInterval = function(val, bounds, unit, units, dutch) {
    if (val == parseFloat(val)) {        // numeric input
        var s_i = 0;
        var s_len = bounds.length;
        while (s_i < s_len && val > bounds[s_i]) {       
            s_i++;
        }
       
        if (dutch) {
            var smaller = "Minder dan ";
            var larger = "Meer dan ";
            var separator = " tot ";
            var plural = (units !== undefined ? units : unit + "en");
        }
        else {
            var smaller = "Less than ";
            var larger = "More than ";
            var separator = " to ";
            var plural = (units !== undefined ? units : unit + "s");
        }
       
       
        if (val == 0) {   
            val = "0 " + plural;
        }
        else if (s_i == 0) {        // val <= bound[0]
            val = smaller + bounds[0] + " " + plural;
        }
        else if (s_i == s_len) {    // val > bounds[s_len-1]
            val = larger + bounds[s_len - 1] + " " + plural;
        }
        else {                        // val <= bounds[s_i]
            if (bounds[s_i] - bounds[s_i-1] == 1) {    // unit interval
                val = val + " " + (val != 1 ? plural : unit);
            }
            else {
                val = bounds[s_i - 1] + separator + bounds[s_i] + " " + plural;
            }
        }
    }   
    return val;   
};


s.addToList=new Function("l","s","if(l){if(s){return l+','+s}else{retu"
+"rn l}}else{return s}");

/*
 * Plugin: getValOnce 0.2 - get a value once per session or number of days
 */
s.getValOnce=new Function("v","c","e",""
+"var s=this,k=s.c_r(c),a=new Date;e=e?e:0;if(v){a.setTime(a.getTime("
+")+e*86400000);s.c_w(c,v,e?a:0);}return v==k?'':v");

/*
 * Plugin: getVisitStart v2.0 - returns 1 on first page of visit
 * otherwise 0
 */
s.getVisitStart=new Function("c",""
+"var s=this,v=1,t=new Date;t.setTime(t.getTime()+1800000);if(s.c_r(c"
+")){v=0}if(!s.c_w(c,1,t)){s.c_w(c,1,0)}if(!s.c_r(c)){v=0}return v;");


/*
 * Plugin: getQueryParamA 1.0
 * Based on getQueryParam 2.3
 * 
 * http://www.adversitement.nl
 */
s.getQueryParamA=function(p,d,u){var s=this,v="",i,t,a;d=d?d:"";u=u?u:(s.pageURL?s.pageURL:s.wd.location);
if(u=="f"){u=s.gtfs().location}while(p){i=p.indexOf(",");i=i<0?p.length:i;
t=s.p_gpvA(p.substring(0,i),u+"");if(t){t=t.indexOf("#")>-1?t.substring(0,t.indexOf("#")):t
}if(t){v+=v?d+t:t}a=s.p_gpvA(p.substring(0,i),u+"","#");if(a){v+=v?d+a:a
}p=p.substring(i==p.length?i:i+1)}return v};s.p_gpvA=function(k,u,a){if(!a){a="?"
}var s=this,v="",i=u.indexOf(a),q;if(k&&i>-1){q=u.substring(i+1);v=s.pt(q,"&","p_gvfA",k)
}return v};s.p_gvfA=function(t,k){if(t){var s=this,i=t.indexOf("="),p=i<0?t:t.substring(0,i),v=i<0?"True":t.substring(i+1);
if(p.toLowerCase()==k.toLowerCase()){return s.epa(v)}}return""};

/*
 * Plugin: getQueryParam 2.3
 */
s.getQueryParam=new Function("p","d","u",""
+"var s=this,v='',i,t;d=d?d:'';u=u?u:(s.pageURL?s.pageURL:s.wd.locati"
+"on);if(u=='f')u=s.gtfs().location;while(p){i=p.indexOf(',');i=i<0?p"
+".length:i;t=s.p_gpv(p.substring(0,i),u+'');if(t){t=t.indexOf('#')>-"
+"1?t.substring(0,t.indexOf('#')):t;}if(t)v+=v?d+t:t;p=p.substring(i="
+"=p.length?i:i+1)}return v");
s.p_gpv=new Function("k","u",""
+"var s=this,v='',i=u.indexOf('?'),q;if(k&&i>-1){q=u.substring(i+1);v"
+"=s.pt(q,'&','p_gvf',k)}return v");
s.p_gvf=new Function("t","k",""
+"if(t){var s=this,i=t.indexOf('='),p=i<0?t:t.substring(0,i),v=i<0?'T"
+"rue':t.substring(i+1);if(p.toLowerCase()==k.toLowerCase())return s."
+"epa(v)}return ''");
/*
 * Utility Function: split v1.5 - split a string (JS 1.0 compatible)
 */
s.split=new Function("l","d",""
+"var i,x=0,a=new Array;while(l){i=l.indexOf(d);i=i>-1?i:l.length;a[x"
+"++]=l.substring(0,i);l=l.substring(i+d.length);}return a");
/*
 * Plugin: Get Previous Value (requires s.split)
 */
s.getPreviousValue=new Function("v","c","el",""
+"var s=this,t=new Date,i,j,r='';t.setTime(t.getTime()+1800000);if(el"
+"){if(s.events){i=s.split(el,',');j=s.split(s.events,',');for(x in i"
+"){for(y in j){if(i[x]==j[y]){if(s.c_r(c)) r=s.c_r(c);v?s.c_w(c,v,t)"
+":s.c_w(c,'no value',t);return r}}}}}else{if(s.c_r(c)) r=s.c_r(c);v?"
+"s.c_w(c,v,t):s.c_w(c,'no value',t);return r}");

/*
 * Plugin: TimeParting
 */
s.getTimeParting = new Function("t", "z", "y", "" + "dc=new Date('1/1/2000');f=15;ne=8;if(dc.getDay()!=6||" + "dc.getMonth()!=0){return'Data Not Available'}else{;z=parseInt(z);" + "if(y=='2009'){f=8;ne=1};gmar=new Date('3/1/'+y);dsts=f-gmar.getDay(" + ");gnov= new Date('11/1/'+y);dste=ne-gnov.getDay();spr=new Date('3/'" + "+dsts+'/'+y);fl=new Date('11/'+dste+'/'+y);cd=new Date();" + "if(cd>spr&&cd<fl){z=z+1}else{z=z};utc=cd.getTime()+(cd.getTimezoneO" + "ffset()*60000);tz=new Date(utc + (3600000*z));thisy=tz.getFullYear(" + ");var days=['Sunday','Monday','Tuesday','Wednesday','Thursday','Fr" + "iday','Saturday'];if(thisy!=y){return'Data Not Available'}else{;thi" + "sh=tz.getHours();thismin=tz.getMinutes();thisd=tz.getDay();var dow=" + "days[thisd];var ap='AM';var dt='Weekday';var mint='00';if(thismin>3" + "0){mint='30'}if(thish>=12){ap='PM';thish=thish-12};if (thish==0){th" + "ish=12};if(thisd==6||thisd==0){dt='Weekend'};var timestring=thish+'" + ":'+mint+ap;var daystring=dow;var endstring=dt;if(t=='h'){return tim" + "estring}if(t=='d'){return daystring};if(t=='w'){return en" + "dstring}}};");

/*
 *	Plug-in: crossVisitParticipation v1.7 - stacks values from
 *	specified variable in cookie and returns value
 */

s.crossVisitParticipation=new Function("v","cn","ex","ct","dl","ev","dv",""
+"var s=this,ce;if(typeof(dv)==='undefined')dv=0;if(s.events&&ev){var"
+" ay=s.split(ev,',');var ea=s.split(s.events,',');for(var u=0;u<ay.l"
+"ength;u++){for(var x=0;x<ea.length;x++){if(ay[u]==ea[x]){ce=1;}}}}i"
+"f(!v||v==''){if(ce){s.c_w(cn,'');return'';}else return'';}v=escape("
+"v);var arry=new Array(),a=new Array(),c=s.c_r(cn),g=0,h=new Array()"
+";if(c&&c!=''){arry=s.split(c,'],[');for(q=0;q<arry.length;q++){z=ar"
+"ry[q];z=s.repl(z,'[','');z=s.repl(z,']','');z=s.repl(z,\"'\",'');arry"
+"[q]=s.split(z,',')}}var e=new Date();e.setFullYear(e.getFullYear()+"
+"5);if(dv==0&&arry.length>0&&arry[arry.length-1][0]==v)arry[arry.len"
+"gth-1]=[v,new Date().getTime()];else arry[arry.length]=[v,new Date("
+").getTime()];var start=arry.length-ct<0?0:arry.length-ct;var td=new"
+" Date();for(var x=start;x<arry.length;x++){var diff=Math.round((td."
+"getTime()-arry[x][1])/86400000);if(diff<ex){h[g]=unescape(arry[x][0"
+"]);a[g]=[arry[x][0],arry[x][1]];g++;}}var data=s.join(a,{delim:',',"
+"front:'[',back:']',wrap:\"'\"});s.c_w(cn,data,e);var r=s.join(h,{deli"
+"m:dl});if(ce)s.c_w(cn,'');return r;");

/*
 *	Plug-in: crossVisitParticipationWithoutLastValOnce= v1.7 - stacks values from
 *	specified variable in cookie and returns value AND inserts once more the last value if found (avoids not counting the same tracking code twcise in a row)
 */

s.crossVisitParticipationWithoutLastValOnce=new Function("v","cn","ex","ct","dl","ev","dv",""
+"var s=this,ce;if(typeof(dv)==='undefined')dv=0;if(s.events&&ev){var"
+" ay=s.split(ev,',');var ea=s.split(s.events,',');for(var u=0;u<ay.l"
+"ength;u++){for(var x=0;x<ea.length;x++){if(ay[u]==ea[x]){ce=1;}}}}i"
+"f(!v||v==''){if(ce){s.c_w(cn,'');return'';}else return'';}v=escape("
+"v);var arry=new Array(),a=new Array(),c=s.c_r(cn),g=0,h=new Array()"
+";if(c&&c!=''){arry=s.split(c,'],[');for(q=0;q<arry.length;q++){z=ar"
+"ry[q];z=s.repl(z,'[','');z=s.repl(z,']','');z=s.repl(z,\"'\",'');arry"
+"[q]=s.split(z,',')}}var e=new Date();e.setFullYear(e.getFullYear()+"
+"5);arry[arry.length]=[v,new Date("
+").getTime()];var start=arry.length-ct<0?0:arry.length-ct;var td=new"
+" Date();for(var x=start;x<arry.length;x++){var diff=Math.round((td."
+"getTime()-arry[x][1])/86400000);if(diff<ex){h[g]=unescape(arry[x][0"
+"]);a[g]=[arry[x][0],arry[x][1]];g++;}}var data=s.join(a,{delim:',',"
+"front:'[',back:']',wrap:\"'\"});s.c_w(cn,data,e);var r=s.join(h,{deli"
+"m:dl});if(ce)s.c_w(cn,'');return r;");

/*
 * Plugin Utility: Replace v1.0
 */
s.repl=new Function("x","o","n",""
+"var i=x.indexOf(o),l=n.length;while(x&&i>=0){x=x.substring(0,i)+n+x."
+"substring(i+o.length);i=x.indexOf(o,i+l)}return x");

/*
 * Plugin: s.join (required by Campaign stacking)
*/

s.join = new Function("v","p",""
+"var s = this;var f,b,d,w;if(p){f=p.front?p.front:'';b=p.back?p.back"
+":'';d=p.delim?p.delim:'';w=p.wrap?p.wrap:'';}var str='';for(var x=0"
+";x<v.length;x++){if(typeof(v[x])=='object' )str+=s.join( v[x],p);el"
+"se str+=w+v[x]+w;if(x<v.length-1)str+=d;}return f+str+b;");

/*
 * Function to transform specific integer to an interval
 */
s.createInterval = function(val, bounds, unit, units, dutch) {
    if (val == parseFloat(val)) {        // numeric input
        var s_i = 0;
        var s_len = bounds.length;
        while (s_i < s_len && val > bounds[s_i]) {       
            s_i++;
        }
       
        if (dutch) {
            var smaller = "Minder dan ";
            var larger = "Meer dan ";
            var separator = " tot ";
            var plural = (units !== undefined ? units : unit + "en");
        }
        else {
            var smaller = "Less than ";
            var larger = "More than ";
            var separator = " to ";
            var plural = (units !== undefined ? units : unit + "s");
        }
       
       
        if (val == 0) {   
            val = "0 " + plural;
        }
        else if (s_i == 0) {        // val <= bound[0]
            val = smaller + bounds[0] + " " + plural;
        }
        else if (s_i == s_len) {    // val > bounds[s_len-1]
            val = larger + bounds[s_len - 1] + " " + plural;
        }
        else {                        // val <= bounds[s_i]
            if (bounds[s_i] - bounds[s_i-1] == 1) {    // unit interval
                val = val + " " + (val != 1 ? plural : unit);
            }
            else {
                val = bounds[s_i - 1] + separator + bounds[s_i] + " " + plural;
            }
        }
    }   
    return val;   
};

/****************************** MODULES *****************************/
/* Module: Media */
s.m_Media_c="var m=s.m_i('Media');if(m.completeByCloseOffset==undefined)m.completeByCloseOffset=1;if(m.completeCloseOffsetThreshold==undefined)m.completeCloseOffsetThreshold=1;m.cn=function(n){var m="
+"this;return m.s.rep(m.s.rep(m.s.rep(n,\"\\n\",''),\"\\r\",''),'--**--','')};m.open=function(n,l,p,b){var m=this,i=new Object,tm=new Date,a='',x;n=m.cn(n);if(!l)l=-1;if(n&&p){if(!m.l)m.l=new Object;"
+"if(m.l[n])m.close(n);if(b&&b.id)a=b.id;if(a)for (x in m.l)if(m.l[x]&&m.l[x].a==a)m.close(m.l[x].n);i.n=n;i.l=l;i.o=0;i.x=0;i.p=m.cn(m.playerName?m.playerName:p);i.a=a;i.t=0;i.ts=0;i.s=Math.floor(tm"
+".getTime()/1000);i.lx=0;i.lt=i.s;i.lo=0;i.e='';i.to=-1;i.tc=0;i.fel=new Object;i.vt=0;i.sn=0;i.sx=\"\";i.sl=0;i.sg=0;i.sc=0;i.us=0;i.co=0;i.cot=0;i.lm=0;i.lom=0;m.l[n]=i}};m._delete=function(n){var"
+" m=this,i;n=m.cn(n);i=m.l[n];m.l[n]=0;if(i&&i.m)clearTimeout(i.m.i)};m.close=function(n){this.e(n,0,-1)};m.play=function(n,o,sn,sx,sl){var m=this,i;i=m.e(n,1,o,sn,sx,sl);if(i&&!i.m){i.m=new Object;"
+"i.m.m=new Function('var m=s_c_il['+m._in+'],i;if(m.l){i=m.l[\"'+m.s.rep(i.n,'\"','\\\\\"')+'\"];if(i){if(i.lx==1)m.e(i.n,3,-1);i.m.i=setTimeout(i.m.m,1000)}}');i.m.m()}};m.complete=function(n,o){th"
+"is.e(n,5,o)};m.stop=function(n,o){this.e(n,2,o)};m.track=function(n){this.e(n,4,-1)};m.bcd=function(vo,i){var m=this,ns='a.media.',v=vo.linkTrackVars,e=vo.linkTrackEvents,pe='m_i',pev3,c=vo.context"
+"Data,x;c['a.contentType']='video';c[ns+'name']=i.n;c[ns+'playerName']=i.p;if(i.l>0){c[ns+'length']=i.l;}c[ns+'timePlayed']=Math.floor(i.ts);if(!i.vt){c[ns+'view']=true;pe='m_s';i.vt=1}if(i.sx){c[ns"
+"+'segmentNum']=i.sn;c[ns+'segment']=i.sx;if(i.sl>0)c[ns+'segmentLength']=i.sl;if(i.sc&&i.ts>0)c[ns+'segmentView']=true}if(!i.cot&&i.co){c[ns+\"complete\"]=true;i.cot=1}if(i.lm>0)c[ns+'milestone']=i"
+".lm;if(i.lom>0)c[ns+'offsetMilestone']=i.lom;if(v)for(x in c)v+=',contextData.'+x;pev3='video';vo.pe=pe;vo.pev3=pev3;var d=m.contextDataMapping,y,a,l,n;if(d){vo.events2='';if(v)v+=',events';for(x i"
+"n d){if(x.substring(0,ns.length)==ns)y=x.substring(ns.length);else y=\"\";a=d[x];if(typeof(a)=='string'){l=m.s.sp(a,',');for(n=0;n<l.length;n++){a=l[n];if(x==\"a.contentType\"){if(v)v+=','+a;vo[a]="
+"c[x]}else if(y){if(y=='view'||y=='segmentView'||y=='complete'||y=='timePlayed'){if(e)e+=','+a;if(c[x]){if(y=='timePlayed'){if(c[x])vo.events2+=(vo.events2?',':'')+a+'='+c[x];}else if(c[x])vo.events"
+"2+=(vo.events2?',':'')+a}}else if(y=='segment'&&c[x+'Num']){if(v)v+=','+a;vo[a]=c[x+'Num']+':'+c[x]}else{if(v)v+=','+a;vo[a]=c[x]}}}}else if(y=='milestones'||y=='offsetMilestones'){x=x.substring(0,"
+"x.length-1);if(c[x]&&d[x+'s'][c[x]]){if(e)e+=','+d[x+'s'][c[x]];vo.events2+=(vo.events2?',':'')+d[x+'s'][c[x]]}}}vo.contextData=0}vo.linkTrackVars=v;vo.linkTrackEvents=e};m.bpe=function(vo,i,x,o){v"
+"ar m=this,pe='m_o',pev3,d='--**--';pe='m_o';if(!i.vt){pe='m_s';i.vt=1}else if(x==4)pe='m_i';pev3=m.s.ape(i.n)+d+Math.floor(i.l>0?i.l:1)+d+m.s.ape(i.p)+d+Math.floor(i.t)+d+i.s+d+(i.to>=0?'L'+Math.fl"
+"oor(i.to):'')+i.e+(x!=0&&x!=2?'L'+Math.floor(o):'');vo.pe=pe;vo.pev3=pev3};m.e=function(n,x,o,sn,sx,sl,pd){var m=this,i,tm=new Date,ts=Math.floor(tm.getTime()/1000),c,l,v=m.trackVars,e=m.trackEvent"
+"s,ti=m.trackSeconds,tp=m.trackMilestones,to=m.trackOffsetMilestones,sm=m.segmentByMilestones,so=m.segmentByOffsetMilestones,z=new Array,j,t=1,w=new Object,x,ek,tc,vo=new Object;n=m.cn(n);i=n&&m.l&&"
+"m.l[n]?m.l[n]:0;if(i){if(o<0){if(i.lx==1&&i.lt>0)o=(ts-i.lt)+i.lo;else o=i.lo}if(i.l>0)o=o<i.l?o:i.l;if(o<0)o=0;i.o=o;if(i.l>0){i.x=(i.o/i.l)*100;i.x=i.x>100?100:i.x}if(i.lo<0)i.lo=o;tc=i.tc;w.name"
+"=n;w.length=i.l;w.openTime=new Date;w.openTime.setTime(i.s*1000);w.offset=i.o;w.percent=i.x;w.playerName=i.p;if(i.to<0)w.mediaEvent=w.event='OPEN';else w.mediaEvent=w.event=(x==1?'PLAY':(x==2?'STOP"
+"':(x==3?'MONITOR':(x==4?'TRACK':(x==5?'COMPLETE':('CLOSE'))))));if(!pd){if(i.pd)pd=i.pd}else i.pd=pd;w.player=pd;if(x>2||(x!=i.lx&&(x!=2||i.lx==1))) {if(!sx){sn=i.sn;sx=i.sx;sl=i.sl}if(x){if(x==1)i"
+".lo=o;if((x<=3||x==5)&&i.to>=0){t=0;v=e=\"None\";if(i.to!=o){l=i.to;if(l>o){l=i.lo;if(l>o)l=o}z=tp?m.s.sp(tp,','):0;if(i.l>0&&z&&o>=l)for(j=0;j<z.length;j++){c=z[j]?parseFloat(''+z[j]):0;if(c&&(l/i"
+".l)*100<c&&i.x>=c){t=1;j=z.length;w.mediaEvent=w.event='MILESTONE';i.lm=w.milestone=c}}z=to?m.s.sp(to,','):0;if(z&&o>=l)for(j=0;j<z.length;j++){c=z[j]?parseFloat(''+z[j]):0;if(c&&l<c&&o>=c){t=1;j=z"
+".length;w.mediaEvent=w.event='OFFSET_MILESTONE';i.lom=w.offsetMilestone=c}}}}if(i.sg||!sx){if(sm&&tp&&i.l>0){z=m.s.sp(tp,',');if(z){z[z.length]='100';l=0;for(j=0;j<z.length;j++){c=z[j]?parseFloat('"
+"'+z[j]):0;if(c){if(i.x<c){sn=j+1;sx='M:'+l+'-'+c;j=z.length}l=c}}}}else if(so&&to){z=m.s.sp(to,',');if(z){z[z.length]=''+(i.l>0?i.l:'E');l=0;for(j=0;j<z.length;j++){c=z[j]?parseFloat(''+z[j]):0;if("
+"c||z[j]=='E'){if(o<c||z[j]=='E'){sn=j+1;sx='O:'+l+'-'+c;j=z.length}l=c}}}}if(sx)i.sg=1}if((sx||i.sx)&&sx!=i.sx){i.us=1;if(!i.sx){i.sn=sn;i.sx=sx}if(i.to>=0)t=1}if(x>=2&&i.lo<o){i.t+=o-i.lo;i.ts+=o-"
+"i.lo}if(x<=2||(x==3&&!i.lx)){i.e+=(x==1||x==3?'S':'E')+Math.floor(o);i.lx=(x==3?1:x)}if(!t&&i.to>=0&&x<=3){ti=ti?ti:0;if(ti&&i.ts>=ti){t=1;w.mediaEvent=w.event='SECONDS'}}i.lt=ts;i.lo=o}if(!x||i.x>"
+"=100){x=0;m.e(n,2,-1,0,0,-1,pd);v=e=\"None\";w.mediaEvent=w.event=\"CLOSE\"}if(x==5||(m.completeByCloseOffset&&(!x||i.x>=100)&&i.l>0&&o>=i.l-m.completeCloseOffsetThreshold)){w.complete=i.co=1;t=1}e"
+"k=w.mediaEvent;if(ek=='MILESTONE')ek+='_'+w.milestone;else if(ek=='OFFSET_MILESTONE')ek+='_'+w.offsetMilestone;if(!i.fel[ek]) {w.eventFirstTime=true;i.fel[ek]=1}else w.eventFirstTime=false;w.timePl"
+"ayed=i.t;w.segmentNum=i.sn;w.segment=i.sx;w.segmentLength=i.sl;if(m.monitor&&x!=4)m.monitor(m.s,w);if(x==0)m._delete(n);if(t&&i.tc==tc){vo=new Object;vo.contextData=new Object;vo.linkTrackVars=v;vo"
+".linkTrackEvents=e;if(!vo.linkTrackVars)vo.linkTrackVars='';if(!vo.linkTrackEvents)vo.linkTrackEvents='';if(m.trackUsingContextData)m.bcd(vo,i);else m.bpe(vo,i,x,o);m.s.t(vo);if(i.us){i.sn=sn;i.sx="
+"sx;i.sc=1;i.us=0}else if(i.ts>0)i.sc=0;i.e=\"\";i.lm=i.lom=0;i.ts-=Math.floor(i.ts);i.to=o;i.tc++}}}return i};m.ae=function(n,l,p,x,o,sn,sx,sl,pd,b){var m=this,r=0;if(n&&(!m.autoTrackMediaLengthReq"
+"uired||(length&&length>0)) &&p){if(!m.l||!m.l[n]){if(x==1||x==3){m.open(n,l,p,b);r=1}}else r=1;if(r)m.e(n,x,o,sn,sx,sl,pd)}};m.a=function(o,t){var m=this,i=o.id?o.id:o.name,n=o.name,p=0,v,c,c1,c2,x"
+"c=m.s.h,x,e,f1,f2='s_media_'+m._in+'_oc',f3='s_media_'+m._in+'_t',f4='s_media_'+m._in+'_s',f5='s_media_'+m._in+'_l',f6='s_media_'+m._in+'_m',f7='s_media_'+m._in+'_c',tcf,w;if(!i){if(!m.c)m.c=0;i='s"
+"_media_'+m._in+'_'+m.c;m.c++}if(!o.id)o.id=i;if(!o.name)o.name=n=i;if(!m.ol)m.ol=new Object;if(m.ol[i])return;m.ol[i]=o;if(!xc)xc=m.s.b;tcf=new Function('o','var e,p=0;try{if(o.versionInfo&&o.curre"
+"ntMedia&&o.controls)p=1}catch(e){p=0}return p');p=tcf(o);if(!p){tcf=new Function('o','var e,p=0,t;try{t=o.GetQuickTimeVersion();if(t)p=2}catch(e){p=0}return p');p=tcf(o);if(!p){tcf=new Function('o'"
+",'var e,p=0,t;try{t=o.GetVersionInfo();if(t)p=3}catch(e){p=0}return p');p=tcf(o)}}v=\"var m=s_c_il[\"+m._in+\"],o=m.ol['\"+i+\"']\";if(p==1){p='Windows Media Player '+o.versionInfo;c1=v+',n,p,l,x=-"
+"1,cm,c,mn;if(o){cm=o.currentMedia;c=o.controls;if(cm&&c){mn=cm.name?cm.name:c.URL;l=cm.duration;p=c.currentPosition;n=o.playState;if(n){if(n==8)x=0;if(n==3)x=1;if(n==1||n==2||n==4||n==5||n==6)x=2;}"
+"';c2='if(x>=0)m.ae(mn,l,\"'+p+'\",x,x!=2?p:-1,0,\"\",0,0,o)}}';c=c1+c2;if(m.s.isie&&xc){x=m.s.d.createElement('script');x.language='jscript';x.type='text/javascript';x.htmlFor=i;x.event='PlayStateC"
+"hange(NewState)';x.defer=true;x.text=c;xc.appendChild(x);o[f6]=new Function(c1+'if(n==3){x=3;'+c2+'}setTimeout(o.'+f6+',5000)');o[f6]()}}if(p==2){p='QuickTime Player '+(o.GetIsQuickTimeRegistered()"
+"?'Pro ':'')+o.GetQuickTimeVersion();f1=f2;c=v+',n,x,t,l,p,p2,mn;if(o){mn=o.GetMovieName()?o.GetMovieName():o.GetURL();n=o.GetRate();t=o.GetTimeScale();l=o.GetDuration()/t;p=o.GetTime()/t;p2=o.'+f5+"
+"';if(n!=o.'+f4+'||p<p2||p-p2>5){x=2;if(n!=0)x=1;else if(p>=l)x=0;if(p<p2||p-p2>5)m.ae(mn,l,\"'+p+'\",2,p2,0,\"\",0,0,o);m.ae(mn,l,\"'+p+'\",x,x!=2?p:-1,0,\"\",0,0,o)}if(n>0&&o.'+f7+'>=10){m.ae(mn,l"
+",\"'+p+'\",3,p,0,\"\",0,0,o);o.'+f7+'=0}o.'+f7+'++;o.'+f4+'=n;o.'+f5+'=p;setTimeout(\"'+v+';o.'+f2+'(0,0)\",500)}';o[f1]=new Function('a','b',c);o[f4]=-1;o[f7]=0;o[f1](0,0)}if(p==3){p='RealPlayer '"
+"+o.GetVersionInfo();f1=n+'_OnPlayStateChange';c1=v+',n,x=-1,l,p,mn;if(o){mn=o.GetTitle()?o.GetTitle():o.GetSource();n=o.GetPlayState();l=o.GetLength()/1000;p=o.GetPosition()/1000;if(n!=o.'+f4+'){if"
+"(n==3)x=1;if(n==0||n==2||n==4||n==5)x=2;if(n==0&&(p>=l||p==0))x=0;if(x>=0)m.ae(mn,l,\"'+p+'\",x,x!=2?p:-1,0,\"\",0,0,o)}if(n==3&&(o.'+f7+'>=10||!o.'+f3+')){m.ae(mn,l,\"'+p+'\",3,p,0,\"\",0,0,o);o.'"
+"+f7+'=0}o.'+f7+'++;o.'+f4+'=n;';c2='if(o.'+f2+')o.'+f2+'(o,n)}';if(m.s.wd[f1])o[f2]=m.s.wd[f1];m.s.wd[f1]=new Function('a','b',c1+c2);o[f1]=new Function('a','b',c1+'setTimeout(\"'+v+';o.'+f1+'(0,0)"
+"\",o.'+f3+'?500:5000);'+c2);o[f4]=-1;if(m.s.isie)o[f3]=1;o[f7]=0;o[f1](0,0)}};m.as=new Function('e','var m=s_c_il['+m._in+'],l,n;if(m.autoTrack&&m.s.d.getElementsByTagName){l=m.s.d.getElementsByTag"
+"Name(m.s.isie?\"OBJECT\":\"EMBED\");if(l)for(n=0;n<l.length;n++)m.a(l[n]);}');if(s.wd.attachEvent)s.wd.attachEvent('onload',m.as);else if(s.wd.addEventListener)s.wd.addEventListener('load',m.as,fal"
+"se);if(m.onLoad)m.onLoad(s,m)";
s.m_i("Media");

/************* DO NOT ALTER ANYTHING BELOW THIS LINE ! **************/
var s_code='',s_objectID;function s_gi(un,pg,ss){var c="s.version='H.25';s.an=s_an;s.logDebug=function(m){var s=this,tcf=new Function('var e;try{console.log(\"'+s.rep(s.rep(s.rep(m,\"\\\\\",\"\\\\\\"
+"\\\"),\"\\n\",\"\\\\n\"),\"\\\"\",\"\\\\\\\"\")+'\");}catch(e){}');tcf()};s.cls=function(x,c){var i,y='';if(!c)c=this.an;for(i=0;i<x.length;i++){n=x.substring(i,i+1);if(c.indexOf(n)>=0)y+=n}return "
+"y};s.fl=function(x,l){return x?(''+x).substring(0,l):x};s.co=function(o){return o};s.num=function(x){x=''+x;for(var p=0;p<x.length;p++)if(('0123456789').indexOf(x.substring(p,p+1))<0)return 0;retur"
+"n 1};s.rep=s_rep;s.sp=s_sp;s.jn=s_jn;s.ape=function(x){var s=this,h='0123456789ABCDEF',i,c=s.charSet,n,l,e,y='';c=c?c.toUpperCase():'';if(x){x=''+x;if(s.em==3)x=encodeURIComponent(x);else if(c=='AU"
+"TO'&&('').charCodeAt){for(i=0;i<x.length;i++){c=x.substring(i,i+1);n=x.charCodeAt(i);if(n>127){l=0;e='';while(n||l<4){e=h.substring(n%16,n%16+1)+e;n=(n-n%16)/16;l++}y+='%u'+e}else if(c=='+')y+='%2B"
+"';else y+=escape(c)}x=y}else x=escape(''+x);x=s.rep(x,'+','%2B');if(c&&c!='AUTO'&&s.em==1&&x.indexOf('%u')<0&&x.indexOf('%U')<0){i=x.indexOf('%');while(i>=0){i++;if(h.substring(8).indexOf(x.substri"
+"ng(i,i+1).toUpperCase())>=0)return x.substring(0,i)+'u00'+x.substring(i);i=x.indexOf('%',i)}}}return x};s.epa=function(x){var s=this;if(x){x=s.rep(''+x,'+',' ');return s.em==3?decodeURIComponent(x)"
+":unescape(x)}return x};s.pt=function(x,d,f,a){var s=this,t=x,z=0,y,r;while(t){y=t.indexOf(d);y=y<0?t.length:y;t=t.substring(0,y);r=s[f](t,a);if(r)return r;z+=y+d.length;t=x.substring(z,x.length);t="
+"z<x.length?t:''}return ''};s.isf=function(t,a){var c=a.indexOf(':');if(c>=0)a=a.substring(0,c);c=a.indexOf('=');if(c>=0)a=a.substring(0,c);if(t.substring(0,2)=='s_')t=t.substring(2);return (t!=''&&"
+"t==a)};s.fsf=function(t,a){var s=this;if(s.pt(a,',','isf',t))s.fsg+=(s.fsg!=''?',':'')+t;return 0};s.fs=function(x,f){var s=this;s.fsg='';s.pt(x,',','fsf',f);return s.fsg};s.mpc=function(m,a){var s"
+"=this,c,l,n,v;v=s.d.visibilityState;if(!v)v=s.d.webkitVisibilityState;if(v&&v=='prerender'){if(!s.mpq){s.mpq=new Array;l=s.sp('webkitvisibilitychange,visibilitychange',',');for(n=0;n<l.length;n++){"
+"s.d.addEventListener(l[n],new Function('var s=s_c_il['+s._in+'],c,v;v=s.d.visibilityState;if(!v)v=s.d.webkitVisibilityState;if(s.mpq&&v==\"visible\"){while(s.mpq.length>0){c=s.mpq.shift();s[c.m].ap"
+"ply(s,c.a)}s.mpq=0}'),false)}}c=new Object;c.m=m;c.a=a;s.mpq.push(c);return 1}return 0};s.si=function(){var s=this,i,k,v,c=s_gi+'var s=s_gi(\"'+s.oun+'\");s.sa(\"'+s.un+'\");';for(i=0;i<s.va_g.leng"
+"th;i++){k=s.va_g[i];v=s[k];if(v!=undefined){if(typeof(v)!='number')c+='s.'+k+'=\"'+s_fe(v)+'\";';else c+='s.'+k+'='+v+';'}}c+=\"s.lnk=s.eo=s.linkName=s.linkType=s.wd.s_objectID=s.ppu=s.pe=s.pev1=s."
+"pev2=s.pev3='';\";return c};s.c_d='';s.c_gdf=function(t,a){var s=this;if(!s.num(t))return 1;return 0};s.c_gd=function(){var s=this,d=s.wd.location.hostname,n=s.fpCookieDomainPeriods,p;if(!n)n=s.coo"
+"kieDomainPeriods;if(d&&!s.c_d){n=n?parseInt(n):2;n=n>2?n:2;p=d.lastIndexOf('.');if(p>=0){while(p>=0&&n>1){p=d.lastIndexOf('.',p-1);n--}s.c_d=p>0&&s.pt(d,'.','c_gdf',0)?d.substring(p):d}}return s.c_"
+"d};s.c_r=function(k){var s=this;k=s.ape(k);var c=' '+s.d.cookie,i=c.indexOf(' '+k+'='),e=i<0?i:c.indexOf(';',i),v=i<0?'':s.epa(c.substring(i+2+k.length,e<0?c.length:e));return v!='[[B]]'?v:''};s.c_"
+"w=function(k,v,e){var s=this,d=s.c_gd(),l=s.cookieLifetime,t;v=''+v;l=l?(''+l).toUpperCase():'';if(e&&l!='SESSION'&&l!='NONE'){t=(v!=''?parseInt(l?l:0):-60);if(t){e=new Date;e.setTime(e.getTime()+("
+"t*1000))}}if(k&&l!='NONE'){s.d.cookie=k+'='+s.ape(v!=''?v:'[[B]]')+'; path=/;'+(e&&l!='SESSION'?' expires='+e.toGMTString()+';':'')+(d?' domain='+d+';':'');return s.c_r(k)==v}return 0};s.eh=functio"
+"n(o,e,r,f){var s=this,b='s_'+e+'_'+s._in,n=-1,l,i,x;if(!s.ehl)s.ehl=new Array;l=s.ehl;for(i=0;i<l.length&&n<0;i++){if(l[i].o==o&&l[i].e==e)n=i}if(n<0){n=i;l[n]=new Object}x=l[n];x.o=o;x.e=e;f=r?x.b"
+":f;if(r||f){x.b=r?0:o[e];x.o[e]=f}if(x.b){x.o[b]=x.b;return b}return 0};s.cet=function(f,a,t,o,b){var s=this,r,tcf;if(s.apv>=5&&(!s.isopera||s.apv>=7)){tcf=new Function('s','f','a','t','var e,r;try"
+"{r=s[f](a)}catch(e){r=s[t](e)}return r');r=tcf(s,f,a,t)}else{if(s.ismac&&s.u.indexOf('MSIE 4')>=0)r=s[b](a);else{s.eh(s.wd,'onerror',0,o);r=s[f](a);s.eh(s.wd,'onerror',1)}}return r};s.gtfset=functi"
+"on(e){var s=this;return s.tfs};s.gtfsoe=new Function('e','var s=s_c_il['+s._in+'],c;s.eh(window,\"onerror\",1);s.etfs=1;c=s.t();if(c)s.d.write(c);s.etfs=0;return true');s.gtfsfb=function(a){return "
+"window};s.gtfsf=function(w){var s=this,p=w.parent,l=w.location;s.tfs=w;if(p&&p.location!=l&&p.location.host==l.host){s.tfs=p;return s.gtfsf(s.tfs)}return s.tfs};s.gtfs=function(){var s=this;if(!s.t"
+"fs){s.tfs=s.wd;if(!s.etfs)s.tfs=s.cet('gtfsf',s.tfs,'gtfset',s.gtfsoe,'gtfsfb')}return s.tfs};s.mrq=function(u){var s=this,l=s.rl[u],n,r;s.rl[u]=0;if(l)for(n=0;n<l.length;n++){r=l[n];s.mr(0,0,r.r,r"
+".t,r.u)}};s.flushBufferedRequests=function(){};s.mr=function(sess,q,rs,ta,u){var s=this,dc=s.dc,t1=s.trackingServer,t2=s.trackingServerSecure,tb=s.trackingServerBase,p='.sc',ns=s.visitorNamespace,u"
+"n=s.cls(u?u:(ns?ns:s.fun)),r=new Object,l,imn='s_i_'+(un),im,b,e;if(!rs){if(t1){if(t2&&s.ssl)t1=t2}else{if(!tb)tb='2o7.net';if(dc)dc=(''+dc).toLowerCase();else dc='d1';if(tb=='2o7.net'){if(dc=='d1'"
+")dc='112';else if(dc=='d2')dc='122';p=''}t1=un+'.'+dc+'.'+p+tb}rs='http'+(s.ssl?'s':'')+'://'+t1+'/b/ss/'+s.un+'/'+(s.mobile?'5.1':'1')+'/'+s.version+(s.tcn?'T':'')+'/'+sess+'?AQB=1&ndh=1'+(q?q:'')"
+"+'&AQE=1';if(s.isie&&!s.ismac)rs=s.fl(rs,2047)}if(s.d.images&&s.apv>=3&&(!s.isopera||s.apv>=7)&&(s.ns6<0||s.apv>=6.1)){if(!s.rc)s.rc=new Object;if(!s.rc[un]){s.rc[un]=1;if(!s.rl)s.rl=new Object;s.r"
+"l[un]=new Array;setTimeout('if(window.s_c_il)window.s_c_il['+s._in+'].mrq(\"'+un+'\")',750)}else{l=s.rl[un];if(l){r.t=ta;r.u=un;r.r=rs;l[l.length]=r;return ''}imn+='_'+s.rc[un];s.rc[un]++}if(s.debu"
+"gTracking){var d='AppMeasurement Debug: '+rs,dl=s.sp(rs,'&'),dln;for(dln=0;dln<dl.length;dln++)d+=\"\\n\\t\"+s.epa(dl[dln]);s.logDebug(d)}im=s.wd[imn];if(!im)im=s.wd[imn]=new Image;im.s_l=0;im.onlo"
+"ad=new Function('e','this.s_l=1;var wd=window,s;if(wd.s_c_il){s=wd.s_c_il['+s._in+'];s.bcr();s.mrq(\"'+un+'\");s.nrs--;if(!s.nrs)s.m_m(\"rr\")}');if(!s.nrs){s.nrs=1;s.m_m('rs')}else s.nrs++;im.src="
+"rs;if(s.useForcedLinkTracking||s.bcf){if(!s.forcedLinkTrackingTimeout)s.forcedLinkTrackingTimeout=250;setTimeout('var wd=window,s;if(wd.s_c_il){s=wd.s_c_il['+s._in+'];s.bcr()}',s.forcedLinkTracking"
+"Timeout);}else if((s.lnk||s.eo)&&(!ta||ta=='_self'||ta=='_top'||(s.wd.name&&ta==s.wd.name))){b=e=new Date;while(!im.s_l&&e.getTime()-b.getTime()<500)e=new Date}return ''}return '<im'+'g sr'+'c=\"'+"
+"rs+'\" width=1 height=1 border=0 alt=\"\">'};s.gg=function(v){var s=this;if(!s.wd['s_'+v])s.wd['s_'+v]='';return s.wd['s_'+v]};s.glf=function(t,a){if(t.substring(0,2)=='s_')t=t.substring(2);var s=t"
+"his,v=s.gg(t);if(v)s[t]=v};s.gl=function(v){var s=this;if(s.pg)s.pt(v,',','glf',0)};s.rf=function(x){var s=this,y,i,j,h,p,l=0,q,a,b='',c='',t;if(x&&x.length>255){y=''+x;i=y.indexOf('?');if(i>0){q=y"
+".substring(i+1);y=y.substring(0,i);h=y.toLowerCase();j=0;if(h.substring(0,7)=='http://')j+=7;else if(h.substring(0,8)=='https://')j+=8;i=h.indexOf(\"/\",j);if(i>0){h=h.substring(j,i);p=y.substring("
+"i);y=y.substring(0,i);if(h.indexOf('google')>=0)l=',q,ie,start,search_key,word,kw,cd,';else if(h.indexOf('yahoo.co')>=0)l=',p,ei,';if(l&&q){a=s.sp(q,'&');if(a&&a.length>1){for(j=0;j<a.length;j++){t"
+"=a[j];i=t.indexOf('=');if(i>0&&l.indexOf(','+t.substring(0,i)+',')>=0)b+=(b?'&':'')+t;else c+=(c?'&':'')+t}if(b&&c)q=b+'&'+c;else c=''}i=253-(q.length-c.length)-y.length;x=y+(i>0?p.substring(0,i):'"
+"')+'?'+q}}}}return x};s.s2q=function(k,v,vf,vfp,f){var s=this,qs='',sk,sv,sp,ss,nke,nk,nf,nfl=0,nfn,nfm;if(k==\"contextData\")k=\"c\";if(v){for(sk in v)if((!f||sk.substring(0,f.length)==f)&&v[sk]&&"
+"(!vf||vf.indexOf(','+(vfp?vfp+'.':'')+sk+',')>=0)&&(!Object||!Object.prototype||!Object.prototype[sk])){nfm=0;if(nfl)for(nfn=0;nfn<nfl.length;nfn++)if(sk.substring(0,nfl[nfn].length)==nfl[nfn])nfm="
+"1;if(!nfm){if(qs=='')qs+='&'+k+'.';sv=v[sk];if(f)sk=sk.substring(f.length);if(sk.length>0){nke=sk.indexOf('.');if(nke>0){nk=sk.substring(0,nke);nf=(f?f:'')+nk+'.';if(!nfl)nfl=new Array;nfl[nfl.leng"
+"th]=nf;qs+=s.s2q(nk,v,vf,vfp,nf)}else{if(typeof(sv)=='boolean'){if(sv)sv='true';else sv='false'}if(sv){if(vfp=='retrieveLightData'&&f.indexOf('.contextData.')<0){sp=sk.substring(0,4);ss=sk.substrin"
+"g(4);if(sk=='transactionID')sk='xact';else if(sk=='channel')sk='ch';else if(sk=='campaign')sk='v0';else if(s.num(ss)){if(sp=='prop')sk='c'+ss;else if(sp=='eVar')sk='v'+ss;else if(sp=='list')sk='l'+"
+"ss;else if(sp=='hier'){sk='h'+ss;sv=sv.substring(0,255)}}}qs+='&'+s.ape(sk)+'='+s.ape(sv)}}}}}if(qs!='')qs+='&.'+k}return qs};s.hav=function(){var s=this,qs='',l,fv='',fe='',mn,i,e;if(s.lightProfil"
+"eID){l=s.va_m;fv=s.lightTrackVars;if(fv)fv=','+fv+','+s.vl_mr+','}else{l=s.va_t;if(s.pe||s.linkType){fv=s.linkTrackVars;fe=s.linkTrackEvents;if(s.pe){mn=s.pe.substring(0,1).toUpperCase()+s.pe.subst"
+"ring(1);if(s[mn]){fv=s[mn].trackVars;fe=s[mn].trackEvents}}}if(fv)fv=','+fv+','+s.vl_l+','+s.vl_l2;if(fe){fe=','+fe+',';if(fv)fv+=',events,'}if (s.events2)e=(e?',':'')+s.events2}for(i=0;i<l.length;"
+"i++){var k=l[i],v=s[k],b=k.substring(0,4),x=k.substring(4),n=parseInt(x),q=k;if(!v)if(k=='events'&&e){v=e;e=''}if(v&&(!fv||fv.indexOf(','+k+',')>=0)&&k!='linkName'&&k!='linkType'){if(k=='timestamp'"
+")q='ts';else if(k=='dynamicVariablePrefix')q='D';else if(k=='visitorID')q='vid';else if(k=='pageURL'){q='g';v=s.fl(v,255)}else if(k=='referrer'){q='r';v=s.fl(s.rf(v),255)}else if(k=='vmk'||k=='visi"
+"torMigrationKey')q='vmt';else if(k=='visitorMigrationServer'){q='vmf';if(s.ssl&&s.visitorMigrationServerSecure)v=''}else if(k=='visitorMigrationServerSecure'){q='vmf';if(!s.ssl&&s.visitorMigrationS"
+"erver)v=''}else if(k=='charSet'){q='ce';if(v.toUpperCase()=='AUTO')v='ISO8859-1';else if(s.em==2||s.em==3)v='UTF-8'}else if(k=='visitorNamespace')q='ns';else if(k=='cookieDomainPeriods')q='cdp';els"
+"e if(k=='cookieLifetime')q='cl';else if(k=='variableProvider')q='vvp';else if(k=='currencyCode')q='cc';else if(k=='channel')q='ch';else if(k=='transactionID')q='xact';else if(k=='campaign')q='v0';e"
+"lse if(k=='resolution')q='s';else if(k=='colorDepth')q='c';else if(k=='javascriptVersion')q='j';else if(k=='javaEnabled')q='v';else if(k=='cookiesEnabled')q='k';else if(k=='browserWidth')q='bw';els"
+"e if(k=='browserHeight')q='bh';else if(k=='connectionType')q='ct';else if(k=='homepage')q='hp';else if(k=='plugins')q='p';else if(k=='events'){if(e)v+=(v?',':'')+e;if(fe)v=s.fs(v,fe)}else if(k=='ev"
+"ents2')v='';else if(k=='contextData'){qs+=s.s2q('c',s[k],fv,k,0);v=''}else if(k=='lightProfileID')q='mtp';else if(k=='lightStoreForSeconds'){q='mtss';if(!s.lightProfileID)v=''}else if(k=='lightIncr"
+"ementBy'){q='mti';if(!s.lightProfileID)v=''}else if(k=='retrieveLightProfiles')q='mtsr';else if(k=='deleteLightProfiles')q='mtsd';else if(k=='retrieveLightData'){if(s.retrieveLightProfiles)qs+=s.s2"
+"q('mts',s[k],fv,k,0);v=''}else if(s.num(x)){if(b=='prop')q='c'+n;else if(b=='eVar')q='v'+n;else if(b=='list')q='l'+n;else if(b=='hier'){q='h'+n;v=s.fl(v,255)}}if(v)qs+='&'+s.ape(q)+'='+(k.substring"
+"(0,3)!='pev'?s.ape(v):v)}}return qs};s.ltdf=function(t,h){t=t?t.toLowerCase():'';h=h?h.toLowerCase():'';var qi=h.indexOf('?');h=qi>=0?h.substring(0,qi):h;if(t&&h.substring(h.length-(t.length+1))=='"
+".'+t)return 1;return 0};s.ltef=function(t,h){t=t?t.toLowerCase():'';h=h?h.toLowerCase():'';if(t&&h.indexOf(t)>=0)return 1;return 0};s.lt=function(h){var s=this,lft=s.linkDownloadFileTypes,lef=s.lin"
+"kExternalFilters,lif=s.linkInternalFilters;lif=lif?lif:s.wd.location.hostname;h=h.toLowerCase();if(s.trackDownloadLinks&&lft&&s.pt(lft,',','ltdf',h))return 'd';if(s.trackExternalLinks&&h.substring("
+"0,1)!='#'&&(lef||lif)&&(!lef||s.pt(lef,',','ltef',h))&&(!lif||!s.pt(lif,',','ltef',h)))return 'e';return ''};s.lc=new Function('e','var s=s_c_il['+s._in+'],b=s.eh(this,\"onclick\");s.lnk=this;s.t()"
+";s.lnk=0;if(b)return this[b](e);return true');s.bcr=function(){var s=this;if(s.bct&&s.bce)s.bct.dispatchEvent(s.bce);if(s.bcf){if(typeof(s.bcf)=='function')s.bcf();else if(s.bct&&s.bct.href)s.d.loc"
+"ation=s.bct.href}s.bct=s.bce=s.bcf=0};s.bc=new Function('e','if(e&&e.s_fe)return;var s=s_c_il['+s._in+'],f,tcf,t,n;if(s.d&&s.d.all&&s.d.all.cppXYctnr)return;if(!s.bbc)s.useForcedLinkTracking=0;else"
+" if(!s.useForcedLinkTracking){s.b.removeEventListener(\"click\",s.bc,true);s.bbc=s.useForcedLinkTracking=0;return}else s.b.removeEventListener(\"click\",s.bc,false);s.eo=e.srcElement?e.srcElement:e"
+".target;s.t();s.eo=0;if(s.nrs>0&&s.useForcedLinkTracking&&e.target){t=e.target.target;if(e.target.dispatchEvent&&(!t||t==\\'_self\\'||t==\\'_top\\'||(s.wd.name&&t==s.wd.name))){e.stopPropagation();"
+"e.stopImmediatePropagation();e.preventDefault();n=s.d.createEvent(\"MouseEvents\");n.initMouseEvent(\"click\",e.bubbles,e.cancelable,e.view,e.detail,e.screenX,e.screenY,e.clientX,e.clientY,e.ctrlKe"
+"y,e.altKey,e.shiftKey,e.metaKey,e.button,e.relatedTarget);n.s_fe=1;s.bct=e.target;s.bce=n;}}');s.oh=function(o){var s=this,l=s.wd.location,h=o.href?o.href:'',i,j,k,p;i=h.indexOf(':');j=h.indexOf('?"
+"');k=h.indexOf('/');if(h&&(i<0||(j>=0&&i>j)||(k>=0&&i>k))){p=o.protocol&&o.protocol.length>1?o.protocol:(l.protocol?l.protocol:'');i=l.pathname.lastIndexOf('/');h=(p?p+'//':'')+(o.host?o.host:(l.ho"
+"st?l.host:''))+(h.substring(0,1)!='/'?l.pathname.substring(0,i<0?0:i)+'/':'')+h}return h};s.ot=function(o){var t=o.tagName;if(o.tagUrn||(o.scopeName&&o.scopeName.toUpperCase()!='HTML'))return '';t="
+"t&&t.toUpperCase?t.toUpperCase():'';if(t=='SHAPE')t='';if(t){if((t=='INPUT'||t=='BUTTON')&&o.type&&o.type.toUpperCase)t=o.type.toUpperCase();else if(!t&&o.href)t='A';}return t};s.oid=function(o){va"
+"r s=this,t=s.ot(o),p,c,n='',x=0;if(t&&!o.s_oid){p=o.protocol;c=o.onclick;if(o.href&&(t=='A'||t=='AREA')&&(!c||!p||p.toLowerCase().indexOf('javascript')<0))n=s.oh(o);else if(c){n=s.rep(s.rep(s.rep(s"
+".rep(''+c,\"\\r\",''),\"\\n\",''),\"\\t\",''),' ','');x=2}else if(t=='INPUT'||t=='SUBMIT'){if(o.value)n=o.value;else if(o.innerText)n=o.innerText;else if(o.textContent)n=o.textContent;x=3}else if(o"
+".src&&t=='IMAGE')n=o.src;if(n){o.s_oid=s.fl(n,100);o.s_oidt=x}}return o.s_oid};s.rqf=function(t,un){var s=this,e=t.indexOf('='),u=e>=0?t.substring(0,e):'',q=e>=0?s.epa(t.substring(e+1)):'';if(u&&q&"
+"&(','+u+',').indexOf(','+un+',')>=0){if(u!=s.un&&s.un.indexOf(',')>=0)q='&u='+u+q+'&u=0';return q}return ''};s.rq=function(un){if(!un)un=this.un;var s=this,c=un.indexOf(','),v=s.c_r('s_sq'),q='';if"
+"(c<0)return s.pt(v,'&','rqf',un);return s.pt(un,',','rq',0)};s.sqp=function(t,a){var s=this,e=t.indexOf('='),q=e<0?'':s.epa(t.substring(e+1));s.sqq[q]='';if(e>=0)s.pt(t.substring(0,e),',','sqs',q);"
+"return 0};s.sqs=function(un,q){var s=this;s.squ[un]=q;return 0};s.sq=function(q){var s=this,k='s_sq',v=s.c_r(k),x,c=0;s.sqq=new Object;s.squ=new Object;s.sqq[q]='';s.pt(v,'&','sqp',0);s.pt(s.un,','"
+",'sqs',q);v='';for(x in s.squ)if(x&&(!Object||!Object.prototype||!Object.prototype[x]))s.sqq[s.squ[x]]+=(s.sqq[s.squ[x]]?',':'')+x;for(x in s.sqq)if(x&&(!Object||!Object.prototype||!Object.prototyp"
+"e[x])&&s.sqq[x]&&(x==q||c<2)){v+=(v?'&':'')+s.sqq[x]+'='+s.ape(x);c++}return s.c_w(k,v,0)};s.wdl=new Function('e','var s=s_c_il['+s._in+'],r=true,b=s.eh(s.wd,\"onload\"),i,o,oc;if(b)r=this[b](e);fo"
+"r(i=0;i<s.d.links.length;i++){o=s.d.links[i];oc=o.onclick?\"\"+o.onclick:\"\";if((oc.indexOf(\"s_gs(\")<0||oc.indexOf(\".s_oc(\")>=0)&&oc.indexOf(\".tl(\")<0)s.eh(o,\"onclick\",0,s.lc);}return r');"
+"s.wds=function(){var s=this;if(s.apv>3&&(!s.isie||!s.ismac||s.apv>=5)){if(s.b&&s.b.attachEvent)s.b.attachEvent('onclick',s.bc);else if(s.b&&s.b.addEventListener){if(s.n&&s.n.userAgent.indexOf('WebK"
+"it')>=0&&s.d.createEvent){s.bbc=1;s.useForcedLinkTracking=1;s.b.addEventListener('click',s.bc,true)}s.b.addEventListener('click',s.bc,false)}else s.eh(s.wd,'onload',0,s.wdl)}};s.vs=function(x){var "
+"s=this,v=s.visitorSampling,g=s.visitorSamplingGroup,k='s_vsn_'+s.un+(g?'_'+g:''),n=s.c_r(k),e=new Date,y=e.getYear();e.setYear(y+10+(y<1900?1900:0));if(v){v*=100;if(!n){if(!s.c_w(k,x,e))return 0;n="
+"x}if(n%10000>v)return 0}return 1};s.dyasmf=function(t,m){if(t&&m&&m.indexOf(t)>=0)return 1;return 0};s.dyasf=function(t,m){var s=this,i=t?t.indexOf('='):-1,n,x;if(i>=0&&m){var n=t.substring(0,i),x="
+"t.substring(i+1);if(s.pt(x,',','dyasmf',m))return n}return 0};s.uns=function(){var s=this,x=s.dynamicAccountSelection,l=s.dynamicAccountList,m=s.dynamicAccountMatch,n,i;s.un=s.un.toLowerCase();if(x"
+"&&l){if(!m)m=s.wd.location.host;if(!m.toLowerCase)m=''+m;l=l.toLowerCase();m=m.toLowerCase();n=s.pt(l,';','dyasf',m);if(n)s.un=n}i=s.un.indexOf(',');s.fun=i<0?s.un:s.un.substring(0,i)};s.sa=functio"
+"n(un){var s=this;if(s.un&&s.mpc('sa',arguments))return;s.un=un;if(!s.oun)s.oun=un;else if((','+s.oun+',').indexOf(','+un+',')<0)s.oun+=','+un;s.uns()};s.m_i=function(n,a){var s=this,m,f=n.substring"
+"(0,1),r,l,i;if(!s.m_l)s.m_l=new Object;if(!s.m_nl)s.m_nl=new Array;m=s.m_l[n];if(!a&&m&&m._e&&!m._i)s.m_a(n);if(!m){m=new Object,m._c='s_m';m._in=s.wd.s_c_in;m._il=s._il;m._il[m._in]=m;s.wd.s_c_in+"
+"+;m.s=s;m._n=n;m._l=new Array('_c','_in','_il','_i','_e','_d','_dl','s','n','_r','_g','_g1','_t','_t1','_x','_x1','_rs','_rr','_l');s.m_l[n]=m;s.m_nl[s.m_nl.length]=n}else if(m._r&&!m._m){r=m._r;r."
+"_m=m;l=m._l;for(i=0;i<l.length;i++)if(m[l[i]])r[l[i]]=m[l[i]];r._il[r._in]=r;m=s.m_l[n]=r}if(f==f.toUpperCase())s[n]=m;return m};s.m_a=new Function('n','g','e','if(!g)g=\"m_\"+n;var s=s_c_il['+s._i"
+"n+'],c=s[g+\"_c\"],m,x,f=0;if(s.mpc(\"m_a\",arguments))return;if(!c)c=s.wd[\"s_\"+g+\"_c\"];if(c&&s_d)s[g]=new Function(\"s\",s_ft(s_d(c)));x=s[g];if(!x)x=s.wd[\\'s_\\'+g];if(!x)x=s.wd[g];m=s.m_i(n"
+",1);if(x&&(!m._i||g!=\"m_\"+n)){m._i=f=1;if((\"\"+x).indexOf(\"function\")>=0)x(s);else s.m_m(\"x\",n,x,e)}m=s.m_i(n,1);if(m._dl)m._dl=m._d=0;s.dlt();return f');s.m_m=function(t,n,d,e){t='_'+t;var "
+"s=this,i,x,m,f='_'+t,r=0,u;if(s.m_l&&s.m_nl)for(i=0;i<s.m_nl.length;i++){x=s.m_nl[i];if(!n||x==n){m=s.m_i(x);u=m[t];if(u){if((''+u).indexOf('function')>=0){if(d&&e)u=m[t](d,e);else if(d)u=m[t](d);e"
+"lse u=m[t]()}}if(u)r=1;u=m[t+1];if(u&&!m[f]){if((''+u).indexOf('function')>=0){if(d&&e)u=m[t+1](d,e);else if(d)u=m[t+1](d);else u=m[t+1]()}}m[f]=1;if(u)r=1}}return r};s.m_ll=function(){var s=this,g"
+"=s.m_dl,i,o;if(g)for(i=0;i<g.length;i++){o=g[i];if(o)s.loadModule(o.n,o.u,o.d,o.l,o.e,1);g[i]=0}};s.loadModule=function(n,u,d,l,e,ln){var s=this,m=0,i,g,o=0,f1,f2,c=s.h?s.h:s.b,b,tcf;if(n){i=n.inde"
+"xOf(':');if(i>=0){g=n.substring(i+1);n=n.substring(0,i)}else g=\"m_\"+n;m=s.m_i(n)}if((l||(n&&!s.m_a(n,g)))&&u&&s.d&&c&&s.d.createElement){if(d){m._d=1;m._dl=1}if(ln){if(s.ssl)u=s.rep(u,'http:','ht"
+"tps:');i='s_s:'+s._in+':'+n+':'+g;b='var s=s_c_il['+s._in+'],o=s.d.getElementById(\"'+i+'\");if(s&&o){if(!o.l&&s.wd.'+g+'){o.l=1;if(o.i)clearTimeout(o.i);o.i=0;s.m_a(\"'+n+'\",\"'+g+'\"'+(e?',\"'+e"
+"+'\"':'')+')}';f2=b+'o.c++;if(!s.maxDelay)s.maxDelay=250;if(!o.l&&o.c<(s.maxDelay*2)/100)o.i=setTimeout(o.f2,100)}';f1=new Function('e',b+'}');tcf=new Function('s','c','i','u','f1','f2','var e,o=0;"
+"try{o=s.d.createElement(\"script\");if(o){o.type=\"text/javascript\";'+(n?'o.id=i;o.defer=true;o.onload=o.onreadystatechange=f1;o.f2=f2;o.l=0;':'')+'o.src=u;c.appendChild(o);'+(n?'o.c=0;o.i=setTime"
+"out(f2,100)':'')+'}}catch(e){o=0}return o');o=tcf(s,c,i,u,f1,f2)}else{o=new Object;o.n=n+':'+g;o.u=u;o.d=d;o.l=l;o.e=e;g=s.m_dl;if(!g)g=s.m_dl=new Array;i=0;while(i<g.length&&g[i])i++;g[i]=o}}else "
+"if(n){m=s.m_i(n);m._e=1}return m};s.voa=function(vo,r){var s=this,l=s.va_g,i,k,v,x;for(i=0;i<l.length;i++){k=l[i];v=vo[k];if(v||vo['!'+k]){if(!r&&(k==\"contextData\"||k==\"retrieveLightData\")&&s[k"
+"])for(x in s[k])if(!v[x])v[x]=s[k][x];s[k]=v}}};s.vob=function(vo){var s=this,l=s.va_g,i,k;for(i=0;i<l.length;i++){k=l[i];vo[k]=s[k];if(!vo[k])vo['!'+k]=1}};s.dlt=new Function('var s=s_c_il['+s._in"
+"+'],d=new Date,i,vo,f=0;if(s.dll)for(i=0;i<s.dll.length;i++){vo=s.dll[i];if(vo){if(!s.m_m(\"d\")||d.getTime()-vo._t>=s.maxDelay){s.dll[i]=0;s.t(vo)}else f=1}}if(s.dli)clearTimeout(s.dli);s.dli=0;if"
+"(f){if(!s.dli)s.dli=setTimeout(s.dlt,s.maxDelay)}else s.dll=0');s.dl=function(vo){var s=this,d=new Date;if(!vo)vo=new Object;s.vob(vo);vo._t=d.getTime();if(!s.dll)s.dll=new Array;s.dll[s.dll.length"
+"]=vo;if(!s.maxDelay)s.maxDelay=250;s.dlt()};s.track=s.t=function(vo){var s=this,trk=1,tm=new Date,sed=Math&&Math.random?Math.floor(Math.random()*10000000000000):tm.getTime(),sess='s'+Math.floor(tm."
+"getTime()/10800000)%10+sed,y=tm.getYear(),vt=tm.getDate()+'/'+tm.getMonth()+'/'+(y<1900?y+1900:y)+' '+tm.getHours()+':'+tm.getMinutes()+':'+tm.getSeconds()+' '+tm.getDay()+' '+tm.getTimezoneOffset("
+"),tcf,tfs=s.gtfs(),ta=-1,q='',qs='',code='',vb=new Object;if(s.mpc('t',arguments))return;s.gl(s.vl_g);s.uns();s.m_ll();if(!s.td){var tl=tfs.location,a,o,i,x='',c='',v='',p='',bw='',bh='',j='1.0',k="
+"s.c_w('s_cc','true',0)?'Y':'N',hp='',ct='',pn=0,ps;if(String&&String.prototype){j='1.1';if(j.match){j='1.2';if(tm.setUTCDate){j='1.3';if(s.isie&&s.ismac&&s.apv>=5)j='1.4';if(pn.toPrecision){j='1.5'"
+";a=new Array;if(a.forEach){j='1.6';i=0;o=new Object;tcf=new Function('o','var e,i=0;try{i=new Iterator(o)}catch(e){}return i');i=tcf(o);if(i&&i.next)j='1.7'}}}}}if(s.apv>=4)x=screen.width+'x'+scree"
+"n.height;if(s.isns||s.isopera){if(s.apv>=3){v=s.n.javaEnabled()?'Y':'N';if(s.apv>=4){c=screen.pixelDepth;bw=s.wd.innerWidth;bh=s.wd.innerHeight}}s.pl=s.n.plugins}else if(s.isie){if(s.apv>=4){v=s.n."
+"javaEnabled()?'Y':'N';c=screen.colorDepth;if(s.apv>=5){bw=s.d.documentElement.offsetWidth;bh=s.d.documentElement.offsetHeight;if(!s.ismac&&s.b){tcf=new Function('s','tl','var e,hp=0;try{s.b.addBeha"
+"vior(\"#default#homePage\");hp=s.b.isHomePage(tl)?\"Y\":\"N\"}catch(e){}return hp');hp=tcf(s,tl);tcf=new Function('s','var e,ct=0;try{s.b.addBehavior(\"#default#clientCaps\");ct=s.b.connectionType}"
+"catch(e){}return ct');ct=tcf(s)}}}else r=''}if(s.pl)while(pn<s.pl.length&&pn<30){ps=s.fl(s.pl[pn].name,100)+';';if(p.indexOf(ps)<0)p+=ps;pn++}s.resolution=x;s.colorDepth=c;s.javascriptVersion=j;s.j"
+"avaEnabled=v;s.cookiesEnabled=k;s.browserWidth=bw;s.browserHeight=bh;s.connectionType=ct;s.homepage=hp;s.plugins=p;s.td=1}if(vo){s.vob(vb);s.voa(vo)}if((vo&&vo._t)||!s.m_m('d')){if(s.usePlugins)s.d"
+"oPlugins(s);var l=s.wd.location,r=tfs.document.referrer;if(!s.pageURL)s.pageURL=l.href?l.href:l;if(!s.referrer&&!s._1_referrer){s.referrer=r;s._1_referrer=1}s.m_m('g');if(s.lnk||s.eo){var o=s.eo?s."
+"eo:s.lnk,p=s.pageName,w=1,t=s.ot(o),n=s.oid(o),x=o.s_oidt,h,l,i,oc;if(s.eo&&o==s.eo){while(o&&!n&&t!='BODY'){o=o.parentElement?o.parentElement:o.parentNode;if(o){t=s.ot(o);n=s.oid(o);x=o.s_oidt}}if"
+"(!n||t=='BODY')o='';if(o){oc=o.onclick?''+o.onclick:'';if((oc.indexOf('s_gs(')>=0&&oc.indexOf('.s_oc(')<0)||oc.indexOf('.tl(')>=0)o=0}}if(o){if(n)ta=o.target;h=s.oh(o);i=h.indexOf('?');h=s.linkLeav"
+"eQueryString||i<0?h:h.substring(0,i);l=s.linkName;t=s.linkType?s.linkType.toLowerCase():s.lt(h);if(t&&(h||l)){s.pe='lnk_'+(t=='d'||t=='e'?t:'o');s.pev1=(h?s.ape(h):'');s.pev2=(l?s.ape(l):'')}else t"
+"rk=0;if(s.trackInlineStats){if(!p){p=s.pageURL;w=0}t=s.ot(o);i=o.sourceIndex;if(o.dataset&&o.dataset.sObjectId){s.wd.s_objectID=o.dataset.sObjectId;}else if(o.getAttribute&&o.getAttribute('data-s-o"
+"bject-id')){s.wd.s_objectID=o.getAttribute('data-s-object-id');}else if(s.useForcedLinkTracking){s.wd.s_objectID='';oc=o.onclick?''+o.onclick:'';if(oc){var ocb=oc.indexOf('s_objectID'),oce,ocq,ocx;"
+"if(ocb>=0){ocb+=10;while(ocb<oc.length&&(\"= \\t\\r\\n\").indexOf(oc.charAt(ocb))>=0)ocb++;if(ocb<oc.length){oce=ocb;ocq=ocx=0;while(oce<oc.length&&(oc.charAt(oce)!=';'||ocq)){if(ocq){if(oc.charAt("
+"oce)==ocq&&!ocx)ocq=0;else if(oc.charAt(oce)==\"\\\\\")ocx=!ocx;else ocx=0;}else{ocq=oc.charAt(oce);if(ocq!='\"'&&ocq!=\"'\")ocq=0}oce++;}oc=oc.substring(ocb,oce);if(oc){o.s_soid=new Function('s','"
+"var e;try{s.wd.s_objectID='+oc+'}catch(e){}');o.s_soid(s)}}}}}if(s.gg('objectID')){n=s.gg('objectID');x=1;i=1}if(p&&n&&t)qs='&pid='+s.ape(s.fl(p,255))+(w?'&pidt='+w:'')+'&oid='+s.ape(s.fl(n,100))+("
+"x?'&oidt='+x:'')+'&ot='+s.ape(t)+(i?'&oi='+i:'')}}else trk=0}if(trk||qs){s.sampled=s.vs(sed);if(trk){if(s.sampled)code=s.mr(sess,(vt?'&t='+s.ape(vt):'')+s.hav()+q+(qs?qs:s.rq()),0,ta);qs='';s.m_m('"
+"t');if(s.p_r)s.p_r();s.referrer=s.lightProfileID=s.retrieveLightProfiles=s.deleteLightProfiles=''}s.sq(qs)}}else s.dl(vo);if(vo)s.voa(vb,1);s.lnk=s.eo=s.linkName=s.linkType=s.wd.s_objectID=s.ppu=s."
+"pe=s.pev1=s.pev2=s.pev3='';if(s.pg)s.wd.s_lnk=s.wd.s_eo=s.wd.s_linkName=s.wd.s_linkType='';return code};s.trackLink=s.tl=function(o,t,n,vo,f){var s=this;s.lnk=o;s.linkType=t;s.linkName=n;if(f){s.bc"
+"t=o;s.bcf=f}s.t(vo)};s.trackLight=function(p,ss,i,vo){var s=this;s.lightProfileID=p;s.lightStoreForSeconds=ss;s.lightIncrementBy=i;s.t(vo)};s.setTagContainer=function(n){var s=this,l=s.wd.s_c_il,i,"
+"t,x,y;s.tcn=n;if(l)for(i=0;i<l.length;i++){t=l[i];if(t&&t._c=='s_l'&&t.tagContainerName==n){s.voa(t);if(t.lmq)for(i=0;i<t.lmq.length;i++){x=t.lmq[i];y='m_'+x.n;if(!s[y]&&!s[y+'_c']){s[y]=t[y];s[y+'"
+"_c']=t[y+'_c']}s.loadModule(x.n,x.u,x.d)}if(t.ml)for(x in t.ml)if(s[x]){y=s[x];x=t.ml[x];for(i in x)if(!Object.prototype[i]){if(typeof(x[i])!='function'||(''+x[i]).indexOf('s_c_il')<0)y[i]=x[i]}}if"
+"(t.mmq)for(i=0;i<t.mmq.length;i++){x=t.mmq[i];if(s[x.m]){y=s[x.m];if(y[x.f]&&typeof(y[x.f])=='function'){if(x.a)y[x.f].apply(y,x.a);else y[x.f].apply(y)}}}if(t.tq)for(i=0;i<t.tq.length;i++)s.t(t.tq"
+"[i]);t.s=s;return}}};s.wd=window;s.ssl=(s.wd.location.protocol.toLowerCase().indexOf('https')>=0);s.d=document;s.b=s.d.body;if(s.d.getElementsByTagName){s.h=s.d.getElementsByTagName('HEAD');if(s.h)"
+"s.h=s.h[0]}s.n=navigator;s.u=s.n.userAgent;s.ns6=s.u.indexOf('Netscape6/');var apn=s.n.appName,v=s.n.appVersion,ie=v.indexOf('MSIE '),o=s.u.indexOf('Opera '),i;if(v.indexOf('Opera')>=0||o>0)apn='Op"
+"era';s.isie=(apn=='Microsoft Internet Explorer');s.isns=(apn=='Netscape');s.isopera=(apn=='Opera');s.ismac=(s.u.indexOf('Mac')>=0);if(o>0)s.apv=parseFloat(s.u.substring(o+6));else if(ie>0){s.apv=pa"
+"rseInt(i=v.substring(ie+5));if(s.apv>3)s.apv=parseFloat(i)}else if(s.ns6>0)s.apv=parseFloat(s.u.substring(s.ns6+10));else s.apv=parseFloat(v);s.em=0;if(s.em.toPrecision)s.em=3;else if(String.fromCh"
+"arCode){i=escape(String.fromCharCode(256)).toUpperCase();s.em=(i=='%C4%80'?2:(i=='%U0100'?1:0))}if(s.oun)s.sa(s.oun);s.sa(un);s.vl_l='timestamp,dynamicVariablePrefix,visitorID,vmk,visitorMigrationK"
+"ey,visitorMigrationServer,visitorMigrationServerSecure,ppu,charSet,visitorNamespace,cookieDomainPeriods,cookieLifetime,pageName,pageURL,referrer,contextData,currencyCode,lightProfileID,lightStoreFo"
+"rSeconds,lightIncrementBy,retrieveLightProfiles,deleteLightProfiles,retrieveLightData';s.va_l=s.sp(s.vl_l,',');s.vl_mr=s.vl_m='timestamp,charSet,visitorNamespace,cookieDomainPeriods,cookieLifetime,"
+"contextData,lightProfileID,lightStoreForSeconds,lightIncrementBy';s.vl_t=s.vl_l+',variableProvider,channel,server,pageType,transactionID,purchaseID,campaign,state,zip,events,events2,products,linkNa"
+"me,linkType';var n;for(n=1;n<=75;n++){s.vl_t+=',prop'+n+',eVar'+n;s.vl_m+=',prop'+n+',eVar'+n}for(n=1;n<=5;n++)s.vl_t+=',hier'+n;for(n=1;n<=3;n++)s.vl_t+=',list'+n;s.va_m=s.sp(s.vl_m,',');s.vl_l2='"
+",tnt,pe,pev1,pev2,pev3,resolution,colorDepth,javascriptVersion,javaEnabled,cookiesEnabled,browserWidth,browserHeight,connectionType,homepage,plugins';s.vl_t+=s.vl_l2;s.va_t=s.sp(s.vl_t,',');s.vl_g="
+"s.vl_t+',trackingServer,trackingServerSecure,trackingServerBase,fpCookieDomainPeriods,disableBufferedRequests,mobile,visitorSampling,visitorSamplingGroup,dynamicAccountSelection,dynamicAccountList,"
+"dynamicAccountMatch,trackDownloadLinks,trackExternalLinks,trackInlineStats,linkLeaveQueryString,linkDownloadFileTypes,linkExternalFilters,linkInternalFilters,linkTrackVars,linkTrackEvents,linkNames"
+",lnk,eo,lightTrackVars,_1_referrer,un';s.va_g=s.sp(s.vl_g,',');s.pg=pg;s.gl(s.vl_g);s.contextData=new Object;s.retrieveLightData=new Object;if(!ss)s.wds();if(pg){s.wd.s_co=function(o){return o};s.w"
+"d.s_gs=function(un){s_gi(un,1,1).t()};s.wd.s_dc=function(un){s_gi(un,1).t()}}",
w=window,l=w.s_c_il,n=navigator,u=n.userAgent,v=n.appVersion,e=v.indexOf('MSIE '),m=u.indexOf('Netscape6/'),a,i,j,x,s;if(un){un=un.toLowerCase();if(l)for(j=0;j<2;j++)for(i=0;i<l.length;i++){s=l[i];x=s._c;if((!x||x=='s_c'||(j>0&&x=='s_l'))&&(s.oun==un||(s.fs&&s.sa&&s.fs(s.oun,un)))){if(s.sa)s.sa(un);if(x=='s_c')return s}else s=0}}w.s_an='0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
w.s_sp=new Function("x","d","var a=new Array,i=0,j;if(x){if(x.split)a=x.split(d);else if(!d)for(i=0;i<x.length;i++)a[a.length]=x.substring(i,i+1);else while(i>=0){j=x.indexOf(d,i);a[a.length]=x.subst"
+"ring(i,j<0?x.length:j);i=j;if(i>=0)i+=d.length}}return a");
w.s_jn=new Function("a","d","var x='',i,j=a.length;if(a&&j>0){x=a[0];if(j>1){if(a.join)x=a.join(d);else for(i=1;i<j;i++)x+=d+a[i]}}return x");
w.s_rep=new Function("x","o","n","return s_jn(s_sp(x,o),n)");
w.s_d=new Function("x","var t='`^@$#',l=s_an,l2=new Object,x2,d,b=0,k,i=x.lastIndexOf('~~'),j,v,w;if(i>0){d=x.substring(0,i);x=x.substring(i+2);l=s_sp(l,'');for(i=0;i<62;i++)l2[l[i]]=i;t=s_sp(t,'');d"
+"=s_sp(d,'~');i=0;while(i<5){v=0;if(x.indexOf(t[i])>=0) {x2=s_sp(x,t[i]);for(j=1;j<x2.length;j++){k=x2[j].substring(0,1);w=t[i]+k;if(k!=' '){v=1;w=d[b+l2[k]]}x2[j]=w+x2[j].substring(1)}}if(v)x=s_jn("
+"x2,'');else{w=t[i]+' ';if(x.indexOf(w)>=0)x=s_rep(x,w,t[i]);i++;b+=62}}}return x");
w.s_fe=new Function("c","return s_rep(s_rep(s_rep(c,'\\\\','\\\\\\\\'),'\"','\\\\\"'),\"\\n\",\"\\\\n\")");
w.s_fa=new Function("f","var s=f.indexOf('(')+1,e=f.indexOf(')'),a='',c;while(s>=0&&s<e){c=f.substring(s,s+1);if(c==',')a+='\",\"';else if((\"\\n\\r\\t \").indexOf(c)<0)a+=c;s++}return a?'\"'+a+'\"':"
+"a");
w.s_ft=new Function("c","c+='';var s,e,o,a,d,q,f,h,x;s=c.indexOf('=function(');while(s>=0){s++;d=1;q='';x=0;f=c.substring(s);a=s_fa(f);e=o=c.indexOf('{',s);e++;while(d>0){h=c.substring(e,e+1);if(q){i"
+"f(h==q&&!x)q='';if(h=='\\\\')x=x?0:1;else x=0}else{if(h=='\"'||h==\"'\")q=h;if(h=='{')d++;if(h=='}')d--}if(d>0)e++}c=c.substring(0,s)+'new Function('+(a?a+',':'')+'\"'+s_fe(c.substring(o+1,e))+'\")"
+"'+c.substring(e+1);s=c.indexOf('=function(')}return c;");
c=s_d(c);if(e>0){a=parseInt(i=v.substring(e+5));if(a>3)a=parseFloat(i)}else if(m>0)a=parseFloat(u.substring(m+10));else a=parseFloat(v);if(a<5||v.indexOf('Opera')>=0||u.indexOf('Opera')>=0)c=s_ft(c);if(!s){s=new Object;if(!w.s_c_in){w.s_c_il=new Array;w.s_c_in=0}s._il=w.s_c_il;s._in=w.s_c_in;s._il[s._in]=s;w.s_c_in++;}s._c='s_c';(new Function("s","un","pg","ss",c))(s,un,pg,ss);return s}
function s_giqf(){var w=window,q=w.s_giq,i,t,s;if(q)for(i=0;i<q.length;i++){t=q[i];s=s_gi(t.oun);s.sa(t.un);s.setTagContainer(t.tagContainerName)}w.s_giq=0}s_giqf()