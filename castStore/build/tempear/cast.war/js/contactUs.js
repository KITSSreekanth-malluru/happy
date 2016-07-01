function checkMotif(motifObject) {
      var opt = motifObject.options[motifObject.selectedIndex];
      if(opt.value != "" && opt.text != "") {     
	      try{
	       	var motiff = eval(opt.value);
	       	var message = motiff['message'];
	       	var motifType = motiff['formType'];
	       	var messageToAddress = motiff['messageToAddress'];
	      } catch(err){ }
	      
	      if(motifType == "PRODUCT") {
	      	$("#productMessage").show();
			$("#webmasterMessage").hide();
			$("#iphoneMessage").hide();
	      } else if(motifType == "WEB_MASTER") {
	       	$("#webmasterMessage").show();
			$("#productMessage").hide();
			$("#iphoneMessage").hide();		
	      } else if(motifType == "IPHONE") {
	      	$("#iphoneMessage").show();
	      	$("#webmasterMessage").hide();
			$("#productMessage").hide();
	      }
	      
	      document.getElementById("motifValue").value=opt.text;

	      if(message){
		 	var tmpMessage = strip(message); 
	       	var myNewString = tmpMessage.replace(/href=\"/, "href=\"/store");
	       	document.getElementById("messageDiv").innerHTML=myNewString;
	       	$("#messageDiv").show();
	       	$("#formDiv").hide();
	      } else {
	        $("#formDiv").show();
	       	$("#messageDiv").hide();
	      }
	      myVal = null;
	      var firstopt = motifObject.options[0];
	      if(firstopt.value == "" && firstopt.text == "") {
	      	motifObject.remove(0);
	      }
      }
 }
 
 function checkCivilite(){
	var prefixObject = document.getElementById("civilite");
      var civiliteValue = document.getElementById("civiliteValue").value;
      for (var i=0; i<prefixObject.length; i++){
		var englishValue = prefixObject.options[i].label;
		if(civiliteValue == englishValue) {
			prefixObject.options[i].selected = 'selected';
			break;
		}
      } 
 }
 
 function strip(html) {
   var tmp = document.createElement("DIV");
   tmp.innerHTML = html;
   return tmp.textContent||tmp.innerText;
}

function limitText(limitField, limitNum) {
    if (limitField.value.length > limitNum) {
        limitField.value = limitField.value.substring(0, limitNum);
    } 
}

function convertMotif(motifObject) {
      var opt = motifObject.options[motifObject.selectedIndex];
      if(opt.text != "") {     
       	var message = opt.text;
       	var tmpMessage = strip(message);
      	document.getElementById("motifValue").value=tmpMessage;
      }
 }