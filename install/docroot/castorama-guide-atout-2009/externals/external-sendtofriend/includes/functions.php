<?php
function setlanguage($LANG) {

	$LANG=strtoupper(trim($LANG));
	
	$OUTPUT_HEADERS = array();
	
	switch ($LANG) {
	
		case 'TK' : $OUTPUT_HEADERS['CHARSET']='iso-8859-9';
         			$OUTPUT_HEADERS['ENCODING']='base64';

					break;

		case 'GR' : $OUTPUT_HEADERS['CHARSET']='ISO-8859-7';
         			$OUTPUT_HEADERS['ENCODING']='base64';

					break;

		case 'CZ' :
		case 'PL' :
					$OUTPUT_HEADERS['CHARSET']='iso-8859-2';
					$OUTPUT_HEADERS['ENCODING']='base64';
					break;

		case 'AR' :
					$OUTPUT_HEADERS['CHARSET']='windows-1256';
					$OUTPUT_HEADERS['ENCODING']='base64';
					break;

		case 'JP' : 
		case 'HE' :
		case 'HEB' : 
					$OUTPUT_HEADERS['CHARSET']='utf-8';
					$OUTPUT_HEADERS['ENCODING']='base64';
					break;

		case 'CN' : $OUTPUT_HEADERS['CHARSET']='GB18030';
					$OUTPUT_HEADERS['ENCODING']='base64';
					break;

		case 'RU' : $OUTPUT_HEADERS['CHARSET']='koi8-r';
					$OUTPUT_HEADERS['ENCODING']='base64';
					break;

		default : $OUTPUT_HEADERS['CHARSET']='iso-8859-1';
				  $OUTPUT_HEADERS['ENCODING']='base64';
				  break;
	
	}

	return $OUTPUT_HEADERS;

}
?>