<dsp:importbean bean="/castorama/OrderFormHandler"/>

<dsp:page xml="true">

<dsp:setvalue bean="OrderFormHandler.repositoryId" paramvalue="orderItem.repositoryId" />
							
<table align=center border=0 width=300 cellspacing=0 cellpadding=0>
							
								<tr bgcolor="#FFDF63">
									<td width=1><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
									<td width=290><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
									<td width=8><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
									<td width=1><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
								</tr>
								
								<tr>
									<td width=1 bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
									<td width=8><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
									<td align=center width=290>
										<table align=left border=0 width=290 cellspacing=1 cellpadding=1>
										<tr align="center">
											<td class="texte" align=left width=290><b>Num&eacute;ro	du carte l'Atout : </b>
											<dsp:valueof param="numeroCarteAtout"><i>non renseign&eacute;</i></dsp:valueof></td>
										</tr>
										<tr align="center">
											<td class="texte" align=left width=290><b>Date de validit&eacute; de carte l'Atout : </b>
											<dsp:valueof param="dateValidAtout" converter="date" date="dd/MM/yyyy"><i>non renseign&eacute;</i></dsp:valueof></td>
										</tr>
										<tr align="center">
											<td class="texte" align=left width=290><b>Option de	paiement : </b> 
											<dsp:droplet name="/atg/dynamo/droplet/Switch">
												<dsp:param name="value"	param="optionPaiementAtout" />
												<dsp:oparam name="1">paiement en une fois</dsp:oparam>
												<dsp:oparam name="3">paiement en petites mensualites</dsp:oparam>
												<dsp:oparam name="default">
													<dsp:droplet name="/castorama/atout/CastoCarteAtout" var="cca">
														<dsp:param name="elementName" value="moyenDePaiement" />
														<dsp:param name="code" param="optionPaiementAtout" />
														<dsp:oparam name="output">
															<dsp:valueof param="moyenDePaiement.libelle"><i>non renseign&eacute;</i></dsp:valueof>
														</dsp:oparam>
														<dsp:oparam name="notFound">
															<i>non renseign&eacute;</i>
														</dsp:oparam>
													</dsp:droplet>
												</dsp:oparam>
											</dsp:droplet>
											</td>
										</tr>
									</table>
									</td>
									<td width=1 bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
								</tr>
								
								<tr bgcolor="#FFDF63">
									<td width=1><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
									<td width=290><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
									<td width=8><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
									<td width=1><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
								</tr>
								
</table>
<br>

</dsp:page>
