<?php
session_start();
require 'includes/config.php';
if(!isset($_GET['url'])) exit;
	else {
		$url = parse_url($_GET['url']);
		$_SESSION['url']=preg_replace(array('/appli.htm/','/appli.php/','/'.SWF_ENGINE.'/'),array('',''),$url['scheme']."://".$url['host'].$url['path']);
	}
if(isset($_GET['pagemarks'])) $_SESSION['pagemarks']=$_GET['pagemarks'];


if (isset($_POST['postuserdata'])) {
	
	if (empty($_POST['firstname'])) $er['userposteddata']['firstname']=true;
	if (empty($_POST['lastname'])) $er['userposteddata']['lastname']=true;
	if (!preg_match("/^[A-Za-z0-9_\\.-]+@[A-Za-z0-9_\\.-]+[.][A-Za-z0-9_\\.-]+$/",$_POST['from'])) $er['userposteddata']['from']=true;
	
	$rec = array();
	
	foreach($_POST['to'] as $to) {
		if (!empty($to)) {
			if (!preg_match("/^[A-Za-z0-9_\\.-]+@[A-Za-z0-9_\\.-]+[.][A-Za-z0-9_\\.-]+$/",$to)) $er['userposteddata']['to']=true;
				else $rec[]=$to;
		}
	}
	
	if (count($rec) == 0) $er['userposteddata']['to'] = true;
	
	if (!isset($er['userposteddata'])) {
		
		$_SESSION['userdata'] = true;
		$_SESSION['firstname'] = stripslashes($_POST['firstname']);
		$_SESSION['lastname'] = stripslashes($_POST['lastname']);
		$_SESSION['from'] = $_POST['from'];
		$_SESSION['to'] = $rec;
		$_SESSION['sendpagemarks'] = $_POST['sendpagemarks'];
		$_SESSION['message'] = stripslashes($_POST['message']);
		$_SESSION['ktalocation'] = $_SESSION['url'];
		
		$queryVars = array();
		$query ="";
		
		if ($_SESSION['sendpagemarks']==1) $queryVars[]="pagemark=".$_SESSION['pagemarks'];
		if (count($queryVars)!=0) $query="?".implode("&",$queryVars);
		
		$_SESSION['ktalink'] = $_SESSION['url'].OPENINGPAGE.$query;
		
	}
	
}


if (!isset($_SESSION['userdata'])) {
	
	include 'includes/templates/' . LANG . '/template_form.php';
	
}  else {

		ob_start();
		require 'includes/templates/' . LANG . '/template_textes.php';
		require 'includes/templates/' . LANG . '/template_mail.php';
		$mail_body = ob_get_contents();
		ob_end_clean();
		
		include ('includes/classes/class.phpmailer.php');
		
		$mail = new phpmailer();
		
		$mail->From     = $_SESSION['from'];
		$mail->FromName = $_SESSION['firstname'].' '.$_SESSION['lastname'];
		
		
		$mail->Host     = "localhost";
		$mail->Mailer   = "smtp";
		
		$mail->Encoding = $OUTPUT_HEADERS['ENCODING'];
		$mail->ContentType = 'text/html;';
		$mail->CharSet=$OUTPUT_HEADERS['CHARSET'];
			
		$mail->IsHTML(true);
		
		$mail->Subject = $mail_subject;
		$mail->Body    = $mail_body;
		
		
		foreach ($_SESSION['to'] as $to) {
			
			$mail->AddAddress($to);	
			
		}
		
		if(!$mail->Send()) 
			require 'includes/templates/' . LANG . '/mail-error.php';
			else 
			require 'includes/templates/' . LANG . '/thank-you.php';
			
		session_destroy();
		include ('includes/close.php');	
				
}



?>