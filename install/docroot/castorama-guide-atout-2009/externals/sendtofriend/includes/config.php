<?php
define('LANG','FR');

// ACTIVATE PRODUCTION MODE
define('DEBUG',true);
define('OPENINGPAGE','index.htm');
define('SWF_ENGINE','v5.0.swf');

if (DEBUG) {
	ini_set('display_errors','On');	
	error_reporting(E_ALL);
} else {
	ini_set('display_errors','Off');	
	error_reporting(0);
}

include('functions.php');
$OUTPUT_HEADERS = setlanguage(LANG);
iconv_set_encoding("input_encoding", "UTF-8");
iconv_set_encoding("internal_encoding", "UTF-8");
iconv_set_encoding("output_encoding", $OUTPUT_HEADERS['CHARSET']);

$mime_headers = array (

	'head_charset' => $OUTPUT_HEADERS['CHARSET'],
	'text_charset'  => $OUTPUT_HEADERS['CHARSET'],
	'html_charset' => $OUTPUT_HEADERS['CHARSET'],
	'html_encoding' => $OUTPUT_HEADERS['ENCODING'],
	'head_encoding' => $OUTPUT_HEADERS['ENCODING']

)

?>