<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" lang="fr-FR" xml:lang="fr">
    <head>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
        <meta http-equiv="content-language" content="fr-FR" />
        <meta http-equiv="X-UA-Compatible" content="IE=8" />
        <meta name="robots" content="index,follow" />
        <meta property="og:title" content="" />
        <meta property="og:description" content="" />
        <meta property="og:site_name" content="" />
        <meta property="og:type" content="website" />
        <meta content="user-scalable=0, initial-scale=1.0" name="viewport" />       
        <title></title>
        <meta name="description" content="" />
        <meta name="keywords" content="" />
        <style type="text/css">
            @-ms-viewport{width:device-width}
            @viewport{width:device-width}

            html,body,div,span,a,img,
            h1,h2,h3,h4,h5,h6,h7,p,blockquote,
            ol,ul,li,dl,dd,dt,
            input,select,textarea{margin:0;padding:0;border:0;font-size:100%;vertical-align:baseline;color:#3c3d51}
            
            .page{width:96%;margin:0 auto}

            body{font-family:Arial,sans-serif;font-size:.8em;line-height:170%}
            
            p{font:normal 14px/20px Arial,sans-serif;padding:4px 18px 1em 18px;text-align:left}
            p.paragraph{padding-top:1em}
            h1{font:800 30px/36px Arial,sans-serif;padding:18px 18px 1em 18px;text-align:left;color:#0078d7;font-weight:bold}
            h2{font:800 14px/20px Arial,sans-serif;padding:1em 18px 0 18px;margin:0}
            h3{font:400 18px/28px Arial,sans-serif;padding:18px 18px 0 18px;text-align:center}
            
            ul{margin:0 0 1em 36px}
            ul li{font:normal 14px/20px Arial,sans-serif;padding:4px 18px 8px 18px;text-align:justify}
            ul li span{width:60%;float:right;display:inline-block;border-bottom:1px solid #3c3d51;height:1.5em}
            ul li span span{padding-left:20px;float:left;width:95%;}
            
            .borderBlock {width:50%;border:1px solid #3c3d51;height:3em;margin:0 0 2em 18px}

            
        </style>    
        
        <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script> 
    </head>

    <body>

    <dsp:getvalueof var="retractionFormElementsUrl" bean="/com/castorama/CastConfiguration.retractionFormElementsUrl" />
    <dsp:importbean bean="/atg/dynamo/service/CurrentDate"/>    
    <dsp:getvalueof var="orderId" param="orderId" />
    
    <dsp:droplet name="/com/castorama/droplet/CastOrderDetailsDroplet">
        <dsp:param name="orderId" param="orderId" />
        <dsp:param name="profileId" param="profileId" />
        <dsp:oparam name="output">
            <dsp:getvalueof var="address1" param="details.orderItem.shippingGroups[0].address1" />
            <dsp:getvalueof var="address2" param="details.orderItem.shippingGroups[0].address2" />
            <dsp:getvalueof var="address3" param="details.orderItem.shippingGroups[0].address3" />
            <dsp:getvalueof var="locality" param="details.orderItem.shippingGroups[0].locality" />
            <dsp:getvalueof var="postalCode" param="details.orderItem.shippingGroups[0].postalCode" />
            <dsp:getvalueof var="city" param="details.orderItem.shippingGroups[0].city" />
            <dsp:getvalueof var="lastName" param="details.orderItem.shippingGroups[0].lastName" />
            <dsp:getvalueof var="firstName" param="details.orderItem.shippingGroups[0].firstName" />                        
        </dsp:oparam>
    </dsp:droplet>
        
    <c:import charEncoding="utf-8" url="${retractionFormElementsUrl}header.html"/>
    
    <ul>
        <li>N° de commande :<span><span>${orderId}</span></span></li>
        <li>Code article :<span></span></li>            
        <li>Commandé le :<span><span><dsp:valueof bean="CurrentDate.timeAsDate" date="dd/MM/yyyy"/></span></span></li>
        <li>Reçu le :<span></span></li>
        <li>Nom du (des) Client(s) :<span><span>${lastName}&nbsp;${firstName}</span></span></li>
        <li>Adresse du (des) Client(s) :<span><span>${address1}<c:if test="${not empty address2 }">&nbsp;-&nbsp;${address2}</c:if><c:if test="${not empty address3 }">&nbsp;-&nbsp;${address3}</c:if><c:if test="${not empty locality }">&nbsp;-&nbsp;${locality}</c:if></span></span></li>
        <li style="list-style-type: none;"><span><span>${postalCode}&nbsp;-&nbsp;${city}</span></span></li>
    </ul>
        <c:if test="${not empty address2 }"></c:if>
          
    <c:import charEncoding="utf-8" url="${retractionFormElementsUrl}footer.html"/>

    </body>
</html>
</dsp:page>