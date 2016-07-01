//update auto refresh meta tag urls in both popentry and popexit htm files

// ----Declare Variables Below----replace all ??? values below if applicable

var subx="http://www.crmmetrix.fr/projects/2009/sc/casrmscfrq49/tscreen.asp";     //Submit popexit to
var subn="";	//Submit popentry to
var popstealthlogo=""; //This will be the logo that will display on popstelath.htm
var cookieexpire=3*24*30; //mention the number in hours eg: for 3 months you enter 3*30*24

var trackx="http://www.crmmetrix.fr/invitetrack.asp?project=exit_casrmscfrq49";  //This is used for counter. See image tag before </body> tag.
var trackn="";  //This is used for counter. See image tag before </body> tag.
//---For above trackx and trackn use the projectID
document.title="Castorama";

//Parameters for Invitation Window
var heightx=430;
var widthx=540;
var xpos=20;
var ypos=20;



//-----------------------------------------------------FUNCTIONS---DO NOT EDIT BELOW THIS

function sweepfx()
{
window.open(sweep,'sweeps','top=5,left=5,resizable=yes,scrollbars=yes,height=300,width=400');
}
function privacyfx()
{
window.open(privacy,'privacy','top=5,left=5,resizable=yes,scrollbars=yes,height=500,width=700');
}

function writeCookie(name, value, hours)
{
  var expire = "";
  if(hours != null)
  {
    expire = new Date((new Date()).getTime() + hours * 3600000);
    expire = "; expires=" + expire.toGMTString();
  }
  document.cookie = name + "=" + escape(value) + expire +";path=/;";
}


function readCookie(name)
{
  var cookieValue = "";
  var search = name + "=";
  if(document.cookie.length > 0)
  { 
    offset = document.cookie.indexOf(search);
    if (offset != -1)
    { 
      offset += search.length;
      end = document.cookie.indexOf(";", offset);
      if (end == -1) end = document.cookie.length;
      cookieValue = unescape(document.cookie.substring(offset, end))
    }
  }
  return cookieValue;
}

function GetParam(name)
{
  var start=location.search.indexOf("?"+name+"=");
  if (start<0) start=location.search.indexOf("&"+name+"=");
  if (start<0) return '';
  start += name.length+2;
  var end=location.search.indexOf("&",start)-1;
  if (end<0) end=location.search.length;
  var result=location.search.substring(start,end);
  var result='';
  for(var i=start;i<=end;i++) {
    var c=location.search.charAt(i);
    result=result+(c=='+'?' ':c);
  }
  return unescape(result);
}
