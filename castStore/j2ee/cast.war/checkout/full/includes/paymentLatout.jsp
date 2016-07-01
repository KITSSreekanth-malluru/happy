<dsp:page>
	<h3><fmt:message key="payment.latout.title" /></h3>
	<p>
		<fmt:message key="payment.latout.line1" />
	</p>
	<p>
		<fmt:message key="payment.latout.line2" />
	</p>
	<p>
		<strong class="blue"><fmt:message key="payment.latout.line3" /></strong><br />
		<fmt:message key="payment.latout.line4" />
	</p>
	<p>
		<strong class="blue">
			<fmt:message key="payment.latout.line5" /><br />
			<fmt:message key="payment.latout.line6" />
		</strong>
	</p>	
	<dsp:getvalueof var="sofincoLink" param="sofincoLink" />
	
	
	<div class="formButtons">
		<span class="inputButton right">
            <fmt:message var="msgSelect" key="payment.select.method" />
			<form method="post" >
				<input type="button" value="${msgSelect}" onclick="javascript:showSofinco('${sofincoLink}');"/>
			</form>
		</span>
	</div>
</dsp:page>