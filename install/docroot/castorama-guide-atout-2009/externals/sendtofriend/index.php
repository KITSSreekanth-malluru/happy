<?php
require 'includes/config.php';
require 'includes/classes/get-xml-data.php';
$xmldata = new xmldata();

ob_start();
require 'includes/templates/' . LANG . '/template_textes.php';
require 'includes/templates/' . LANG . '/template_mail.php';
$mail_body = ob_get_contents();
ob_end_clean();

if (empty($xmldata->subject)) $xmldata->subject = $mail_subject;

include ('includes/classes/class.phpmailer.php');

$mail = new phpmailer();

$mail->From     = $xmldata->from;
$mail->FromName = mb_encode_mimeheader($xmldata->firstname." ".$xmldata->lastname,$OUTPUT_HEADERS['CHARSET'],$OUTPUT_HEADERS['HEADERENCODING']);

$mail->Host     = "localhost";
$mail->Mailer   = "smtp";

$mail->Encoding = $OUTPUT_HEADERS['ENCODING'];
$mail->ContentType = 'text/html;';
$mail->CharSet=$OUTPUT_HEADERS['CHARSET'];

$mail->IsHTML(true);

$mail->Subject = mb_encode_mimeheader($xmldata->subject,$OUTPUT_HEADERS['CHARSET'],$OUTPUT_HEADERS['HEADERENCODING']);
$mail->Body    = $mail_body;


foreach ($xmldata->to as $to) {
	
	$mail->AddAddress($to);	
	
}

if(!$mail->Send()) die("sendResult=false"); else die("sendResult=true");
?>