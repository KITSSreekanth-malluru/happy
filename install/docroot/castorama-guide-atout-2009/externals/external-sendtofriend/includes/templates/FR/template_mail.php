<body>
<table width="580" cellpadding="1" cellspacing="0" border="0" style="border:1px solid #000000">
	<tr>
		<td>
		
		<table width="100%" cellpadding="1" cellspacing="0" border="0">
			<tr>
				<td>
				
					<table width="540" cellpadding="20" cellspacing="0" border="0">
						<tr>
							<td style="font-family:verdana;font-size:12px;color:#000000">
								<p>Bonjour !</p>
			
								<p><b><?php echo $_SESSION['firstname']." ".$_SESSION['lastname']?></b> vous invite � d�couvrir le nouveau catalogue <b><?php echo $kta_name?>.</b></p>
			
								<p>
									Tr�s pratique, feuilletez-le tout simplement de page en page !<br />
									Tr�s utile, zoomez � volont� pages et produits.<br />
									Et tr�s malin : si vous trouvez des pages signal�es d'un marque-page, ce sont tout simplement les pages pr�f�r�es que <b><?php echo $_SESSION['firstname']." ".$_SESSION['lastname'] ?></b> vous invite � consulter sans tarder.
								</p>
							</td>
						</tr>
					</table>

					<table width="536" cellpadding="0" cellspacing="0" border="0" style="margin:0 auto">
						<tr>
							<td valign="top" style="width:50px;"><a href="<?php echo $_SESSION['ktalink'] ?>"><img src="<?php echo $_SESSION['ktalocation'] ?>/data/0001v.jpg" style="border:1px solid #000000" alt=""/></a></td>
							<td  style="font-family:verdana;font-size:12px;color:#000000;padding:0 10px;" valign="top">
								Pour le plaisir de la d�couverte, <a href="<?php echo $_SESSION['ktalink'] ?>">cliquez ici</a> tout simplement.
							</td>
						</tr>
					</table>
					
					<table width="540" cellpadding="20" cellspacing="0" border="0">
						<tr>
							<td style="font-family:verdana;font-size:12px;color:#000000">
								<?php 
								
								if (!empty($_SESSION['message'])) {
								?>
								<p>Voici un message personnel � votre attention :</p>
								
								<p style="padding:0 20px;">"<?php echo $_SESSION['message'] ?>"</p>
								<?php } ?>
								
								<p style="padding:10px 0">
									Bonne d�couverte,<br />
									L'�quipe internet de <?php echo $kta_name?>
								</p>
							</td>
						</tr>
					</table>
					
					<table width="540" cellpadding="0" cellspacing="0" border="0" style="margin:0 auto">
						<tr>
							<td style="font-size:1px;line-height:1px;background-color:#000000">&nbsp;</td>
						</tr>
					</table>
					
					<table width="540" cellpadding="20" cellspacing="0" border="0">
						<tr>
							<td style="font-family:verdana;font-size:10px;color:#000000">
								<p>PS. Pour d�couvrir ce catalogue, vous pouvez aussi faire un copier/coller de ce lien dans le champ "adresse" de votre navigateur :</p>
								<p><i><?php echo $_SESSION['ktalink'] ?></i></p>
								
							</td>
						</tr>
					</table>

				</td>
			</tr>
		</table>
		
		</td>
	</tr>
</table>


</body>
</html>