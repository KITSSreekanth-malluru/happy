<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
		<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
		<html xmlns="http://www.w3.org/1999/xhtml">
		<head>
		<dsp:getvalueof var="metaInfoInclude" param="metaInfoInclude" />
		<c:choose>
				<c:when test="${metaInfoInclude != null}">
						<dsp:include page="${metaInfoInclude}" />
				</c:when>
				<c:otherwise>
						<dsp:getvalueof var="metaKeyword" param="metaKeyword" />
						<dsp:getvalueof var="metaDescription" param="metaDescription" />
						<dsp:getvalueof var="title" param="title" />
						<c:if test="${title == null}">
								<dsp:getvalueof var="title" value="Castorama.fr" />
						</c:if>
						<c:if test="${metaKeyword == null}">
								<dsp:getvalueof var="metaKeyword" value="" />
						</c:if>
						<c:if test="${metaDescription == null}">
								<dsp:getvalueof var="metaDescription" value="" />
						</c:if>
						<title>${title}</title>
						<meta name="description" content="${metaDescription}" />
						<meta name="keywords" content="${metaKeyword}" />
				</c:otherwise>
		</c:choose>

		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />		
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/main.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/ui.all.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/ui.dialog.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/ui.resizable.css" />
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/ui.core.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/ui.draggable.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/ui.droppable.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/main.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/custom-form-elements.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/ui.dialog.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/ui.resizable.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/effects.core.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/effects.transfer.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/facetedSearch.js"></script>
		</head>
    
        <!--[if lt IE 7]>
            <link type="text/css" href="${pageContext.request.contextPath}/styles/ie6.css" rel="stylesheet" media="screen" />
            <script type="text/javascript" src="${pageContext.request.contextPath}/js/iepngfix_tilebg.js"></script>
          <style>
              .gfImgLabel_CL,
            .gfImgLabel_CC,
            .upperMenuPopup,
            .userShadow,
            .elargeProduct,
            .playIcon,
            .controlButton,
            .carouselPopup { behavior: url("${pageContext.request.contextPath}/js/iepngfix.htc") }
          </style>
        <![endif]-->
        
		<body>
		<div class="gray_overlay"></div>
		<div id="whiteContainer">
		<div class="m10">
</dsp:page>
