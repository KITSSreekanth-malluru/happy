var WidthCata = 1000;
var HeightCata = 670;
var redim=true;
var page = "";
var onglet = "";
var idprod = "";
var urlSuppl = "";
var ns4 = (document.layers)? true:false;
var ie4 = (document.all)? true:false;
var dom = (document.getElementById)? true:false;
var DefaultWidth = 1024;
var DefaultHeight = 768;
var ssuppl=window.location.search;

if (ie4 || dom)	{
	xMax = screen.width;
	yMax = screen.height;
} else if (ns4) {
	xMax = window.outerWidth;
	yMax = window.outerHeight;
} else {
	xMax = DefaultWidth;
	yMax = DefaultHeight;
}

function launchwin(winurl,winname,winWidth,winHeight)
	{
	var xMax, yMax, xOffset, yOffset;
	if (ie4 || dom)
		{
		xMax = screen.width;
		yMax = screen.height;
		}
	else if (ns4)
		{
		xMax = window.outerWidth;
		yMax = window.outerHeight;
		}
	else
		{
		xMax = 800;
		yMax = 600;
		}
	xOffset = (xMax - winWidth)/2;
	yOffset = (yMax - winHeight)/2;
	ktawin=window.open(winurl,winname,'width='+winWidth
+',height='+winHeight
+',screenX='+xOffset
+',screenY='+yOffset
+',top='+yOffset
+',left='+xOffset
+',scrollbars=no,resizable=yes,status=no,menubar=no');
	ktawin.focus();
	//ktawin.redim=redim;
	//ktawin.resizeTo(winWidth,winHeight);
}

function launch_catalogue(urlbasekta,param,WidthCataClient,HeightCataClient) {
	if(WidthCataClient!=undefined) WidthCata=WidthCataClient;
	if(HeightCataClient!=undefined) HeightCata=HeightCataClient;
	if(!urlbasekta) urlbasekta="appli.htm";
	if (urlbasekta.indexOf("appli")<0) urlbasekta=urlbasekta+"appli.htm";
	if(!param){ 
		launchwin(urlbasekta+ssuppl,'catalogue',WidthCata,HeightCata); 
		}
	else{
		launchwin(urlbasekta+'?'+param,'catalogue',WidthCata,HeightCata);
	}
}
