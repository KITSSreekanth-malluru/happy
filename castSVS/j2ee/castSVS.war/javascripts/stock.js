function getStoreStatus(ctx) {
  var prodId = $('#sv_wstest_productId')[0].value;
  var storeId = $('#sv_wstest_storeId')[0].value;
  var quantity = $('#sv_wstest_quantity')[0].value;
  var ch = getCheckedValue(document.getElementsByName('sv_wstest_channel'));
  var url = ctx + "/stock?prodId=" + prodId + "&storeId=" + storeId + "&quantity=" + quantity + "&channel=" + ch;
  var urlText = $('#sv_wstest_url_text')[0];
  urlText.innerHTML = "<hr id='sv_wstest_url_hr' /><textarea id='sv_wstest_url' >" + url + "</textarea>";
  $.get(url,
    function (data) {
      var response = $('#sv_wstest_response_text')[0];
      response.innerHTML = "<hr id='sv_wstest_response_hr' /><pre id='sv_wstest_response' >" + xmlFormat(data) + "</pre>";
    }
  );
}

function getPostalCodeStatus(ctx) {
  var prodId = $('#sv_wstest_productId')[0].value;
  var postalCode = $('#sv_wstest_postalCode')[0].value;
  var quantity = $('#sv_wstest_quantity')[0].value;
  var size = $('#sv_wstest_size')[0].value;
  var ch = getCheckedValue(document.getElementsByName('sv_wstest_channel'));
  var url = ctx + "/stock?prodId=" + prodId + "&postalCode=" + postalCode + "&quantity=" + quantity + "&channel=" + ch + "&size=" + size;
  var urlText = $('#sv_wstest_url_text')[0];
  urlText.innerHTML = "<hr id='sv_wstest_url_hr' /><textarea id='sv_wstest_url' >" + url + "</textarea>";
  $.get(url,
    function (data) {
      var response = $('#sv_wstest_response_text')[0];
	  response.innerHTML = "<hr id='sv_wstest_response_hr' /><pre id='sv_wstest_response' >" + xmlFormat(data) + "</pre>";
    }
  );
}

function getCheckedValue(radioObj) {
  if(!radioObj)
    return "";
  var radioLength = radioObj.length;
  if(radioLength == undefined)
    if(radioObj.checked)
      return radioObj.value;
    else
      return "";
  for(var i = 0; i < radioLength; i++) {
    if(radioObj[i].checked) {
      return radioObj[i].value;
    }
  }
  return "";
}

function xmlFormat(node) {
	var buffer;
	if (node.xmlVersion) {
		// ff, chrome
		buffer = '<span class="sv_wstest_tag" >' + '&lt;?xml' + '</span>' +
				 ' ' +
				 '<span class="sv_wstest_attr" >' + 'version="' + '</span>' + 
				 '<span class="sv_wstest_value" >' + node.xmlVersion + '</span>' +
				 '<span class="sv_wstest_attr" >' + '"' + '</span>' +
				 ' ' + 
				 '<span class="sv_wstest_attr" >' + 'encoding="' + '</span>' +
				 '<span class="sv_wstest_value" >' + node.xmlEncoding + '</span>' +
				 '<span class="sv_wstest_attr" >' + '"' + '</span>' +
				 ' ' +
				 '<span class="sv_wstest_attr" >' + 'standalone="' + '</span>' +
				 '<span class="sv_wstest_value" >' + node.xmlStandalone + '</span>' +
				 '<span class="sv_wstest_attr" >' + '"' + '</span>' +
				 ' ' + 
				 '<span class="sv_wstest_tag" >' + '?&gt;' + '</span>';
		
		buffer = [buffer];
		format(node.firstChild, '', '  ', buffer);
		
	} else {
		// ie
		buffer = node.firstChild.xml.replace(/</g,"&lt;").replace(/>/g,"&gt");
		
		buffer = [buffer];
		format(node.getElementsByTagName('ns2:stocks').item(0), '', '  ', buffer);
	}
	
	return buffer.join('\n');
}

function format(node, indent, chr, buffer ){
	if (node == null) return;

	var xml = indent + '<span class="sv_wstest_tag" >' + '&lt;' + node.nodeName + '</span>';
	var nc = node.childNodes.length;
		
	if (node.attributes != null) {
		for( var i = 0; i < node.attributes.length; i++) {
			xml += ' ' + 
				'<span class="sv_wstest_attr" >' + node.attributes[i].nodeName + '="' + '</span>' + 
				'<span class="sv_wstest_value" >' + node.attributes[i].nodeValue + '</span>' + 
				'<span class="sv_wstest_attr" >' + '"' + '</span>';
		}	
	}
	xml += nc ? ('<span class="sv_wstest_tag" >' + '&gt;' + '</span>') : ( '<span class="sv_wstest_tag" >' + '/&gt;' + '</span>');

	buffer.push( xml );
		
	if( nc ){
		var child, i = 0;
		do{
			child = node.childNodes[i++];
			if( typeof child == 'string' ){
				if ( nc == 1 ) {
					return buffer.push( buffer.pop() + child + '<span class="sv_wstest_tag" >' +'&lt;/'+node.nodeName+'&gt;' + '</span>');
				} else {
					buffer.push( indent+chr+child );
				}
			}else if ( typeof child == 'object' ) {
				format(child, indent+chr, chr, buffer);
			}
		}while( i < nc );
		buffer.push( indent + '<span class="sv_wstest_tag" >' + '&lt;/'+node.nodeName +'&gt;' + '</span>');
	}
}
