<body>
<table width="580" cellpadding="1" cellspacing="0" border="0" style="border:1px solid #788ea3">
	<tr>
		<td>
		
		<table width="100%" cellpadding="20" cellspacing="0" border="0">
			<tr>
				<td>
				
					<table width="540" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td style="font-family:verdana;font-size:12px;color:#000000">
								<p>Bonjour !</p>
			
								<p><b><?php echo $xmldata->firstname." ".$xmldata->lastname?></b> vous invite à découvrir le nouveau guide <b><?php echo $kta_name?></b>.</p>
			
								<p>
									Très pratique, feuilletez-le tout simplement de page en page !<br />
									Très utile, zoomez à volonté pages et produits.<br />
									Et très malin : si vous trouvez des pages signalées d'un marque-page, ce sont tout simplement les pages préférées que <b><?php echo $xmldata->firstname." ".$xmldata->lastname ?></b> vous invite à consulter sans tarder.
								</p>
							</td>
						</tr>
					</table>
					<br/>
					<table width="540" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td valign="top"><a href="<?php echo $xmldata->finalurl ?>"><img src="<?php echo $xmldata->linktokta ?>data/0001v.jpg" style="border:1px solid #000000" alt=""/></a></td>
							<td style="font-family:verdana;font-size:12px;color:#000000;width:410px;" valign="top">
								Pour le plaisir de la découverte, <a href="<?php echo $xmldata->finalurl ?>">cliquez ici</a> tout simplement.
							</td>
						</tr>
					</table>
					<br />
					<?php
					
					if (count($xmldata->annotations)>0) {
					?>
					<table width="540" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td style="font-family:verdana;font-size:12px;color:#000000">
								Voici les pages annotées par <?php echo $xmldata->firstname." ".$xmldata->lastname?> :
								
								<table cellpadding="0" cellspacing="0" border="0" width="540">
									<?php
									foreach ($xmldata->annotations as $page=>$value) {
									?>
									<tr>
										<td style="width:70px;border-left:1px solid #000000;border-top:1px solid #000000;padding:5px;"><a href="<?php echo $xmldata->finalurl ?>"><img src="<?php echo $xmldata->linktokta ?>data/<?php echo str_pad($page+$xmldata->offset,4,"0",STR_PAD_LEFT)?>v.jpg" style="border:1px solid #000000" width="50" height="70" alt=""/></a></td>
										<td style="font-family:verdana;font-size:12px;color:#000000;padding:5px;border-right:1px solid #000000;border-top:1px solid #000000;" valign="top">
											<?php foreach ($value as $v) {?>
												<p style="margin:0px;padding:0px;margin-bottom:5px;">"<?php echo $v['text']?>"</p>
											<?php }?>
											<a href="<?php echo $v['link']?>">Consulter la page</a>
										</td>
									</tr>
									<?php }?>
									<tr>
										<td colspan="2" style="border-left:1px solid #000000;border-right:1px solid #000000;border-bottom:1px solid #000000;font-size:1px;line-height:1px;">&nbsp;</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
					<?php									
					}
					?>
					
					<table width="540" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td style="font-family:verdana;font-size:12px;color:#000000">
								
								<?php 
								if (!empty($xmldata->message)) {
								?>
								<p>Voici un message personnel à votre attention :</p>
								
								<p><pre style="margin:0;padding:0;">"<?php echo $xmldata->message ?>"</pre></p>
								<?php } ?>
								
								<p style="padding:10px 0;">
									Bonne découverte,<br />
									L'équipe internet de <?php echo $kta_name?>
								</p>
							</td>
						</tr>
					</table>
					
					<table width="540" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td style="font-size:1px;line-height:1px;background-color:#000000">&nbsp;</td>
						</tr>
					</table>
					
					<table width="540" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td style="font-family:verdana;font-size:10px;color:#000000">
								<p style="padding:10px 0;">
									PS. Pour découvrir ce guide, vous pouvez aussi faire un copier/coller de ce lien dans le champ "adresse" de votre navigateur :<br />
									<i><?php echo $xmldata->finalurl ?></i>
								</p>
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