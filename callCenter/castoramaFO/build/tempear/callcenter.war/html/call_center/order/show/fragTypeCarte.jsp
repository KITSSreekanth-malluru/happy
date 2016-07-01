<dsp:page xml="true">

<dsp:droplet name="/atg/dynamo/droplet/SQLQueryForEach">
	<dsp:param name="dataSource" bean="/atg/dynamo/service/jdbc/JTDataSource"/>
	<dsp:param name="querySQL" value="select min(payment_means) from CASTO_SIPS_LOG where order_id=:order.id and response_code=00000"/>
	<dsp:param name="transactionManager" bean="/atg/dynamo/transaction/TransactionManager"/>  								
	<dsp:param name="elementName" value="sips"/>
	<dsp:oparam name="output">
		<dsp:valueof param="sips.column[0]"/>
	</dsp:oparam>
</dsp:droplet>

</dsp:page>