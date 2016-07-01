<dsp:page>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" class="castorama">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <style type="text/css">
        @page {
            size: 12.69in 15.27in;
        }
		@media print {
			ul {
				padding:0!important;
			}
			.m10 {
			  page-break-inside: avoid;
			}
			.floatNone {
				padding-left: 10px;
			}
		}
    </style>
<link rel="stylesheet" type="text/css" href="/store/styles/main.css">
<link rel="stylesheet" type="text/css" href="/store/styles/cc_main.css">
</head>
<body style="overflow-x: auto; background-image: none;">
<div id="whiteContainer">
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/dynamo/service/CurrentDate" />

<dsp:droplet name="/com/castorama/droplet/CastOrderDetailsDroplet">
    <dsp:param name="orderId" param="orderId" />
    <dsp:param name="profileId" param="profileId" />
	<dsp:oparam name="output">
		<dsp:getvalueof var="result_submittedDate" vartype="java.util.Date" bean="CurrentDate.timeAsDate" />
		<c:set var="submittedDate">
		  <fmt:formatDate value="${result_submittedDate}" pattern="dd/MM/yyyy - HH:mm" />
		</c:set>
		<dsp:getvalueof var="storeId" param="details.orderItem.magasinId"/>
		<dsp:droplet name="ForEach">			
            <dsp:param name="array" param="details.orderItem.commerceItems" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="quantity" param="element.quantity"/>
				<dsp:getvalueof var="skuId" param="element.catalogRefId" />
				<dsp:getvalueof var="productId" param="element.productId" />
				<dsp:droplet name="/atg/commerce/catalog/ProductLookup">
					<dsp:param name="id" value="${productId}"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="type" param="element.type"/>
						<dsp:getvalueof var="bundleLinks" param="element.childSKUs[0].bundleLinks"/>
						<dsp:getvalueof var="isPack" value="${(not empty type) and (type == 'casto-pack')}"/>
						<c:choose>
							<c:when test="${isPack}">
								<dsp:droplet name="ForEach">
									<dsp:param name="array" value="${bundleLinks}"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="set" param="element.item.parentProducts.updatedValue"/>
										<dsp:param name="list" value="${castCollection:list(set)}"/>
										<dsp:include page="basicDetailsInfo.jsp">
											<dsp:param name="productId" param="list[0].repositoryId"/>
											<dsp:param name="skuId" param="element.item.id"/>
											<dsp:param name="orderDate" value="${submittedDate}"/>
											<dsp:param name="storeId" value="${storeId}"/>
											<dsp:param name="quantity" value="${quantity}"/>
											<dsp:param name="packId" value="${productId}"/>
										</dsp:include>
									</dsp:oparam>
								</dsp:droplet>
							</c:when>
							<c:otherwise>
								<dsp:include page="basicDetailsInfo.jsp">
									<dsp:param name="productId" param="element.repositoryId"/>
									<dsp:param name="skuId" value="${skuId}"/>
									<dsp:param name="orderDate" value="${submittedDate}"/>
									<dsp:param name="storeId" value="${storeId}"/>
									<dsp:param name="quantity" value="${quantity}"/>
								</dsp:include>
							</c:otherwise>
						</c:choose>					
					</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>
	
	</dsp:oparam>
	
</dsp:droplet>
</div>
</body>
</html>
</dsp:page>