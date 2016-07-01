<?php
// ACTIVATE PRODUCTION MODE
define('DEBUG',true);
define('OPENINGPAGE','index.htm');
define('SWF_ENGINE','v5.0.swf');

// OVERIDE THE DEFAULT STYLE FOR THE TEXT;
define('OVERWRITE_FONT_STYLE',false);
define('OVERWRITE_FOOTER_FONT_STYLE',false);

// SET THE LANGUAGE OF THE CATALOGUE;
// AVAILABLE LANGUAGES
// FRENCH- ABREVIATION = FR
// ENGLISH - ABREVIATION = EN
// GREEK - ABREVIATION = GR
// RUSIAN - ABREVIATION = RU
// GERMAN - ABREVIATION = DE

define('LANG','FR');

//THE COLOR FOR THE BORDER AROUND THE MAIL
define('BORDER_COLOR','#000000');

//THE BACKGROUND COLOR FOR THE MAIL
define('BACKGROUND_COLOR','#FFFFFF');

// THE MAIL DIMENSIONS
// PUT ONLY NUMBERS HERE (NO 'px' TEXT)

define('MAIL_WIDTH',580);
define('INDENTATION',14);

//THE TITLE FOR THE TABLE CONTAINING THE MAIL
define('PADDING','2px 12px 2px 12px'); // top right bottom left is the order
define('TITLE_COLOR','#515151');
define('TITLE_SIZE','14px');
define('TITLE_FONT','arial');

//THE TEXT STYLE FOR THE MAIL
define('TEXT_COLOR','#515151');
define('TEXT_SIZE','12px');
define('TEXT_FONT','arial');

//THE FOOTER TEXT STYLE FOR THE MAIL
define('FOOTER_TEXT_COLOR','#515151');
define('FOOTER_TEXT_SIZE','9px');
define('FOOTER_TEXT_FONT','arial');


// PUT YOUR OWN PERSONALISED STYLE HERE. WILL OVERWRITE THE ONE ABOVE HERE.
define('OVERWRITEED_FONT_STYLE','font-size:12px;font-family:arial;color:#000000');
define('OVERWRITEED_FOOTER_FONT_STYLE','font-size:10px;font-family:arial;color:#C0C0C0');

/////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////
////////////// DO NOT MODIFY UNDER THIS WARNING        //////////////////////////////////////////
////////////// DO NOT MODIFY UNDER THIS WARNING        //////////////////////////////////////////
////////////// DO NOT MODIFY UNDER THIS WARNING        //////////////////////////////////////////
////////////// DO NOT MODIFY UNDER THIS WARNING        //////////////////////////////////////////
////////////// DO NOT MODIFY UNDER THIS WARNING        //////////////////////////////////////////
////////////// DO NOT MODIFY UNDER THIS WARNING        //////////////////////////////////////////
////////////// DO NOT MODIFY UNDER THIS WARNING        //////////////////////////////////////////
////////////// DO NOT MODIFY UNDER THIS WARNING        //////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////


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