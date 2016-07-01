var isIE  = (navigator.appVersion.indexOf("MSIE") != -1) ? true : false;
var isWin = (navigator.appVersion.toLowerCase().indexOf("win") != -1) ? true : false;
var isOpera = (navigator.userAgent.indexOf("Opera") != -1) ? true : false;
var UrlSuppl="";

var tmpflash = '';
var addToUrl = '';
function HandleMiniBook(param) {
launch_catalogue(param,'onglet=&amp;page=');
}	
function putMyFlash(filename,logo,SEOPage) {	
	var flashContent=document.getElementById('flashContent');
	var noFlashContent=document.getElementById('noFlashContent');
	for(i=0;i<filename.length;i++){
		var tempFilename=filename[i].split('appli.htm');
		if(tempFilename[0].length==0){
			var minibook="minibook.swf";
			var kta=".";		
			}
		else{
			var minibook=tempFilename[0]+"minibook.swf";
			var kta=tempFilename[0];		
			}		
		tmpflash += '<OBJECT WIDTH="180" HEIGHT="170" type="application/x-shockwave-flash" data="'+minibook+'?kta='+kta+'" id="flashpiece">';
		tmpflash += '<PARAM NAME="movie" VALUE="'+minibook+'?kta='+kta+'">';
		tmpflash += '<PARAM NAME="menu" VALUE="false"> ';
		tmpflash += '<PARAM NAME="quality" VALUE="high"> ';
		tmpflash += '<PARAM NAME="bgcolor" VALUE="#FFFFFF">';
		tmpflash += '<PARAM NAME="swLiveConnect" VALUE="true">';
		tmpflash += '<param name="allowScriptAccess" value="always" />';	
		tmpflash += '<param name="allowFullScreen" value="true" />';	
		tmpflash += '<param name="wmode" value="transparent" />';	
		tmpflash += '</OBJECT><br />';
	}
	
	if(isIE){
	flashContent.removeChild(flashContent.firstChild);
	document.write(tmpflash)	
	}
	else flashContent.innerHTML=tmpflash;
}

function ControlVersion()
{
	var version;
	var axo;
	var e;

	try {
		axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7");
		version = axo.GetVariable("$version");
		//version = version.split(" ");
		//version = version[1];
		//version = version.split(",");
		//if(version[0]==9&&version[1]==0&&version[2]<124){window.location.href="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=shockwaveFlash";return;}
	} catch (e) {
	}

	if (!version)
	{
		try {
			axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");
			version = "WIN 6,0,21,0";
			axo.AllowScriptAccess = "always";
			version = axo.GetVariable("$version");
		} catch (e) {
		}
	}
return version;
}

function GetSwfVer(){
	var flashVer = -1;
	if (navigator.plugins != null && navigator.plugins.length > 0) {
		if (navigator.plugins["Shockwave Flash 2.0"] || navigator.plugins["Shockwave Flash"]) {
			var swVer2 = navigator.plugins["Shockwave Flash 2.0"] ? " 2.0" : "";
			var flashDescription = navigator.plugins["Shockwave Flash" + swVer2].description;
			var descArray = flashDescription.split(" ");
			var tempArrayMajor = descArray[2].split(".");			
			var versionMajor = tempArrayMajor[0];
			var versionMinor = tempArrayMajor[1];
			var versionRevision = descArray[3];
			if (versionRevision == "") {
				versionRevision = descArray[4];
			}
			if (versionRevision[0] == "d") {
				versionRevision = versionRevision.substring(1);
			} else if (versionRevision[0] == "r") {
				versionRevision = versionRevision.substring(1);
				if (versionRevision.indexOf("d") > 0) {
					versionRevision = versionRevision.substring(0, versionRevision.indexOf("d"));
				}
			}
			//if(versionMajor==9&&versionMinor==0&&versionRevision<124){window.location.href="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=shockwaveFlash";return;}
			var flashVer = versionMajor + "." + versionMinor + "." + versionRevision;
		}
	}
	else if (navigator.userAgent.toLowerCase().indexOf("webtv/2.6") != -1) flashVer = 4;
	else if (navigator.userAgent.toLowerCase().indexOf("webtv/2.5") != -1) flashVer = 3;
	else if (navigator.userAgent.toLowerCase().indexOf("webtv") != -1) flashVer = 2;
	else if ( isIE && isWin && !isOpera ) {
		flashVer = ControlVersion();
	}	
	return flashVer;
}