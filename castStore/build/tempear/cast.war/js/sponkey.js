function isTel() {
  value_tel = document.create_account.telephone_telform.value;
  
  var phoneRegexFR=/^[0-9]{10}$/;
  var phoneRegex=/^\+?[0-9]{4,16}$/; 
  
  if(phoneRegexFR.test(value_tel)) {
	  if(!alreadySent(value_tel)) {
		  webServ_telform(value_tel);
	  } else {
		  processPreviousResponce(value_tel);
	  }
  } else if(phoneRegex.test(value_tel)) {
	  document.create_account.phone2.value = value_tel;
  } else {
	  document.create_account.phone2.value="";
  }
}

var requests = new Array();
var responses = new Array();

function alreadySent(tel) {
	for(i=0;i<requests.length;i++) {
		if(requests[i] == tel) {
			return true;
		}
	}
	requests[requests.length] = tel;
	return false;
}

function saveResponse(tel, responseText) {
	for(i=0;i<requests.length;i++) {
		if(requests[i] == tel) {
			responses[i]=responseText;
			return;
		}
	}
}

function processPreviousResponce(tel) {
	for(i=0;i<requests.length;i++) {
		if(requests[i] == tel) {
			if(i < responses.length && responses[i] != null) {
				processResponse(responses[i]);
			}
			break;
		}
	}
    document.create_account.phone2.value = tel; // phone
}

function webServ_telform(tel) {
  value_session_telform = document.create_account.session_telform.value;
  
  // The full path to the proxy
  var url = 'accesServiceTelForm.jsp?Tel='+tel+'&session_telform='+value_session_telform;

  // Create xmlhttprequest object
  var xmlhttp = null;
  if (window.XMLHttpRequest) {
    xmlhttp = new XMLHttpRequest();
    
	  //make sure that Browser supports overrideMimeType
	  if ( typeof xmlhttp.overrideMimeType != 'undefined') { 
      xmlhttp.overrideMimeType('text/xml'); 
	  }
  } else if(window.ActiveXObject) {
    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
  } else {
    alert('Perhaps your browser does not support xmlhttprequests?');
  }
  
  // Create an HTTP GET request
  xmlhttp.open('GET', url, true);

  // Set the callback function
  xmlhttp.onreadystatechange = function() {
    if (xmlhttp.readyState == 4) {
    	processResponse(xmlhttp.responseText);
    }
  };
  // Make the actual request
  xmlhttp.send(null);
}

function processResponse(responseText) {
	    var valSplit = responseText.split('#');
	    if (valSplit[0] == 1) {
	      document.create_account.firstname.value = valSplit[1]; // first Name
	      if(valSplit[2] == '') {
            document.create_account.lastname.value = valSplit[7]; // second Name
	      } else {
            document.create_account.lastname.value = valSplit[2]; // second Name
	      }
          document.create_account.city.value = valSplit[3]; // city
          document.create_account.postcode.value = valSplit[4]; // postal code
          for(i=0; i<document.create_account.state.length; i++) {
            if(document.create_account.state.options[i].value == 'F') {
              document.create_account.state.options[i].selected=true;
            }
          } 
          document.create_account.phone2.value = valSplit[6]; // phone
          var arrayAddress1 = new Array();
          validateAddress(arrayAddress1, valSplit[8]);
          validateAddress(arrayAddress1, valSplit[9]);
          validateAddress(arrayAddress1, valSplit[10]);
          validateAddress(arrayAddress1, valSplit[11]);
          document.create_account.address1.value = arrayAddress1.join(''); // address1 (N, voie)
          document.create_account.address2.value = valSplit[12]; // address2 (etage, appartement)
          var arrayLocality = new Array();
          validateAddress(arrayLocality, valSplit[13]);
          validateAddress(arrayLocality, valSplit[14]);
          document.create_account.locality.value = arrayLocality.join(''); // locality (lieu dit)    
          
          saveResponse(valSplit[6], responseText);
        } else {
	        document.create_account.phone2.value = document.getElementById("phoneFix").value; // phone
	    }    
}

function validateAddress(array, element) {
  if(element != '') {
    array.push(element + ' ');
  }
}

  