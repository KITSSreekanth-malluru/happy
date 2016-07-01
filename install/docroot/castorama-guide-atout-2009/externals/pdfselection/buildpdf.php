<?php
if (!isset($_POST['selectedPages']) && empty($_POST['selectedPages'])) die ("sendResult=false");

$pages = explode(",",$_POST['selectedPages']);
$outputFileName = $_POST['prefixName'].md5(uniqid(rand(), true)).".pdf";

$pdftkCommande = "pdftk ";
for ($i=0;$i<count($pages);$i++) {
	$pdftkInputFiles[]="../../data/".$pages[$i];
}
$pdftkCommande .= implode(" ",$pdftkInputFiles);
$pdftkCommande .= " cat output ../../../OUTPUT/";
$pdftkCommande .= $outputFileName;

exec($pdftkCommande,$v,$er);
if ($er == 0) echo "sendResult=true&filename=".$outputFileName; else echo "sendResult=false"; 
/*
function microtime_float()
{
    list($usec, $sec) = explode(" ", microtime());
    return ((float)$usec + (float)$sec);
}

$t = microtime_float();
$t1 = microtime_float();
*/
?>