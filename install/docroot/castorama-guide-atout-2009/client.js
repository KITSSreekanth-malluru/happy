var page = "";
var onglet = "";
var urlSuppl = "";
var idprod = "";
var height=screen.availHeight;
var width=screen.availWidth;
var URLBASE="";
var WindowStyle="width=800,height=600,resizable=1,scrollbars=1,menubar=1,toolbar=1,status=1,location=1,left=0,top=0";

var ns4 = (document.layers)? true:false;
var ie4 = (document.all)? true:false;
var dom = (document.getElementById)? true:false;
var DefaultWidth = 1008;
var DefaultHeight = 751;
var InternetExplorer = navigator.appName.indexOf("Microsoft") != -1;
var Firefox = navigator.userAgent.indexOf("Firefox") != -1;

//FONCTIONS CLIENTS

function openArticle(params,base){
	//client's choice :
	//1 - la fiche produit s'ouvre dans le opener(le domain du site doit etre identique au domain du kta)
	//1 - product description page opens in the page that has opened the kta window (the site's domain has to be the same as the kta domain)
	//0 - defaut (la fiche produit s'ouvre dans une nouvelle fenetre chaque fois)
	//0 - default (the product description page opens in a new window every time)
	var clientchoice=0;
	FinalUrl=URLBASE+params;	
	if(params.indexOf("http")>=0 || base=='false') FinalUrl=params;	
	if(clientchoice==0){
		openNewWin(FinalUrl,WindowStyle);
	}
	else if(clientchoice==1){
		if(!VerifyDomain(FinalUrl)){
			openNewWin(FinalUrl,WindowStyle);
		}
		else{
			try{
				if (!window.opener.closed) {	
		    	window.opener.location=FinalUrl;
		   		window.opener.focus();
				}
				else {
					openNewWin(FinalUrl,WindowStyle);
				}
			}
			catch(e){
				openNewWin(FinalUrl,WindowStyle);
			}
		}
				
	}
}

function VerifyDomain(FinalUrl){
	if(FinalUrl.indexOf("http")<0) return true;
	var domappli=window.location.href;
	var domsite=FinalUrl;
	var cpos1=domappli.indexOf("http://");
	var cpos2=domsite.indexOf("http://");
		domappli=domappli.substring(cpos1+7,domappli.length);
		domsite=domsite.substring(cpos2+7,domsite.length);
		cpos1=domappli.indexOf("/");
		cpos2=domsite.indexOf("/");
		domappli=domappli.substring(0,cpos1);
		domsite=domsite.substring(0,cpos2);
	var tempappli=new Array;
	var tempsite=new Array;
		tempappli=domappli.split('.');
		tempsite=domsite.split('.');
	var lappli=tempappli.length;
	var lsite=tempsite.length;
		if(lappli==lsite&&domappli==domsite) return true;	
		if(lappli!=lsite){
			if(tempappli[lappli-1]==tempsite[lsite-1]&&tempappli[lappli-2]==tempsite[lsite-2]) return true;}
		return false;
}

function openNewWin(url,params){
	 newWindow = window.open(url,"KTA", params);	 
}
function closeWin(){
	 window.close();	 
}
function redimCata(xresize,yresize) {	
	if (xresize == null||xresize>width) {
		xresize = width;
		newxpos = 0;
	} else {
		newxpos = (width - xresize)/2;		
	}
	if (yresize == null||yresize>height) {
		yresize = height;
		newypos = 0;
	} else {
		newypos = (height - yresize)/2;		
	}

	if(newxpos+xresize<width){
		self.resizeTo(xresize, yresize);
		self.moveTo(newxpos,newypos);
		}
	else{
		self.moveTo(newxpos,newypos);
		self.resizeTo(xresize, yresize);		
		}
}

function ITCMI_ParseURL(win,loc)
{
	var i,s1=new String(win.document.location),s2,s3;	
	i=s1.indexOf("?");
	if(i>-1)
	{
		s1=s1.substring(i+1,s1.length);
		while( s1.length>0)
		{
			i=s1.indexOf("&");
			if(i>-1)
			{
				s2=s1.substring(0,i);
				s1=s1.substring(i+1,s1.length);
			}
			else
			{
				s2=s1;
				s1=""
			}
			i=s2.indexOf("=")
			if(i>-1)
			{
				s3=s2.substring(0,i);
				s3.toUpperCase();
				if(s3=="page" || s3=="PAGE")
				{
					page=s2.substring(i+1,s2.length);
					page=unescape(page);
				}
				
				if(s3=="onglet" || s3=="ONGLET")
				{
					onglet=s2.substring(i+1,s2.length);
					onglet=unescape(onglet);
				}
				
				if(s3=="idprod" || s3=="IDPROD")
				{
					idprod=s2.substring(i+1,s2.length);
					idprod=unescape(idprod);
				}
			}
		}
	}
	if ((idprod != '') || (page != '') || (onglet != '') ) {
		urlSuppl = "?G_SAVE_PAGE="+page+"&G_SAVE_ONGLET="+onglet+"&G_IDPRODUCT="+idprod;		
	}	
};

function launchwin(winurl,winname,winWidth,winHeight)
	{
	var xMax, yMax, xOffset, yOffset;	
	xMax = screen.width;
	yMax = screen.height;
	xOffset = (xMax - winWidth)/2;
	yOffset = (yMax - winHeight)/2;
	ktawin=window.open(winurl,winname,'width='+winWidth+',height='+winHeight+',top='+yOffset+',left='+xOffset+',scrollbars=no,resizable=yes,status=no,menubar=no');
	ktawin.focus();	
}