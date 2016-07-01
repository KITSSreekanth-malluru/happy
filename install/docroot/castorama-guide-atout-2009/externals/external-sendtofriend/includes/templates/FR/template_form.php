<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
	<title>TITLE</title>
	<link rel="stylesheet" type="text/css" href="css/styles.css"/>
</head>
<body>
<form id="formsend" method="post" action="" style="margin:0;padding:0;display:block;">
<input name="postuserdata" type="hidden" value="true" />
<table width="600" style="height:400px" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td align="right" style="padding-top:50px">
			<table border="0" cellpadding="3" cellspacing="0" width="350">
				
				<tr>
					<td align="left" class="txt" <?php if (isset($er['userposteddata']['firstname'])) echo "style=\"color:#ff0000\""?>>Your firstname* :</td>
					<td><input name="firstname" type="text" value="<?php if (isset($_POST['firstname'])) echo stripslashes($_POST['firstname'])?>" class="input" size="30" style="width:233px;color:#000000;font-size:14px;border-color:#C0C0C0" /></td>
				</tr>
				<tr>
					<td align="left" class="txt" <?php if (isset($er['userposteddata']['lastname'])) echo "style=\"color:#ff0000\""?>>Your name* :</td>
					<td><input name="lastname" type="text" value="<?php if (isset($_POST['lastname'])) echo stripslashes($_POST['lastname'])?>" class="input" size="30" style="width:233px;color:#000000;font-size:14px;border-color:#C0C0C0" /></td>
				</tr>
				<tr>
					<td align="left" class="txt" <?php if (isset($er['userposteddata']['from'])) echo "style=\"color:#ff0000\""?>>Your E-mail :</td>
					<td><input name="from" type="text" value="<?php if (isset($_POST['from'])) echo stripslashes($_POST['from'])?>" class="input" size="30" style="width:233px;color:#000000;font-size:14px;border-color:#C0C0C0" maxlength="255" /></td>
				</tr>
				<tr>
					<td align="left" valign="top" class="txt" <?php if (isset($er['userposteddata']['to'])) echo "style=\"color:#ff0000\""?>>Reciepient's <br />E-mail * :</td>
					<td> 				
						<input name="to[0]" type="text" value="<?php if (isset($_POST['to'][0])) echo stripslashes($_POST['to'][0])?>" class="input" size="30" style="width:233px;color:#000000;font-size:14px;border-color:#C0C0C0" /><br/>					
						<input name="to[1]" type="text" value="<?php if (isset($_POST['to'][1])) echo stripslashes($_POST['to'][1])?>" class="input" size="30" style="width:233px;color:#000000;font-size:14px;border-color:#C0C0C0" /><br/>
					</td>
				</tr>
 				<tr>
					<td align="left" valign="top" class="txt">Your message :</td>
					<td><textarea name="message" cols="25" rows="5" class="input"  style="width:233px;color:#000000;font-size:14px;border-color:#C0C0C0"><?php if (isset($_POST['message'])) echo stripslashes($_POST['message'])?></textarea></td>
				</tr>
				<tr>
					<td colspan="3" align="center">
						<table border="0" cellspacing="3" cellpadding="0" class="txt1">
							<tr>
								<td style="padding-left:10px;"><input name="sendpagemarks" type="checkbox" value="1" checked="checked" /></td>
								<td class="txtonglets">also send my page-markers</td>
								<td><img src="images/onglets.gif" alt="" style="border:0" /></td>
							</tr>
						</table>
					</td>
				</tr>
				
			</table>
		</td>
	</tr>
	<tr>
		<td align="center" style="padding-top:20px;">

			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="txtcredits" style="padding-left:10px;height:48px">* Mandatory fields<br />You can recommend this catalogue to a friend <br />Data are confidential and won’t be distributed</td>
					<td align="center" valign="bottom" style="width:133px;padding-left:20px">
						<input type="image" src="images/bt_send.gif" alt="" style="border:0" />
					</td>					
				</tr>
			</table>
		</td>
	</tr>
</table>
</form>
</body>
</html>
