/* BEGIN: SETTINGS */

var currentOpened;
$(document).ready(function() {
	$("#mainMenuUL").find(".mainMenuPoint").click(function(e) {
		if (typeof(currentOpened) != "undefined"){
		if (currentOpened[0]!=$(this)[0])
			currentOpened.find(".upperMenuPopup").removeClass("menuVisible");
		}
		$(this).find(".upperMenuPopup").toggleClass('menuVisible').click(function(event){
			event.stopPropagation();
		});
		currentOpened = $(this);
		e.stopPropagation();
	});
});

$(document).ready(function(){
		$('body').click(function(e) {
			$("#mainMenuUL .mainMenuPoint .upperMenuPopup").removeClass("menuVisible");
		});
	});


// Definitions for main menu
var menu_length = new Array();
menu_length['menu_point_1'] = { width: false, items: 3, full: false }
menu_length['menu_point_2'] = { width: false, items: 4, full: false }
menu_length['menu_point_3'] = { width: false, items: 4, full: false }
//menu_length['menu_point_t1'] = { width: false, items: 4, full: false }
//menu_length['menu_point_4'] = { width: false, items: false, full: true }
//menu_length['menu_point_5'] = { width: false, items: false, full: true }

/* END: SETTINGS */

// TOOLS
	/*function $() {
	// getElementById
		var elements = new Array();
		for (var i = 0; i < arguments.length; i++) {
			var element = arguments[i];
			if (typeof element == 'string')
				element = document.getElementById(element);
			if (arguments.length == 1)
				return element;
			elements.push(element);
		}
		return elements;
	}*/
	
	function getTop(_obj){
		var obj = _obj;
		var top = obj.offsetTop;
			
		while (obj.tagName != "BODY") {
			obj = obj.offsetParent ;
			top += obj.offsetTop ;
		}
		return top ;
	}

	function getLeft(_obj){
		var obj = _obj ;
		var left = obj.offsetLeft ;
				
		while (obj.tagName != "BODY") {
			obj = obj.offsetParent ;
			left += obj.offsetLeft ;
		}

		return left ;
	}

// END OF TOOLS


// PROCESSING DEFAULT INPUTES
	function inputFocus() {
		if (this.type == "text") {
			if (this.value==this.getAttribute('defaultValue')) this.value="";
		}
	}

	function inputBlur() {
		if (this.value.length==0) {
			this.value = this.getAttribute('defaultValue');
		}
	}
// 


// MON ESPACE CLIENT POPUP
	function showMEC() {
		document.getElementById("monEspaceClient").style.display = "block";
		document.getElementById("monEspaceClient").style.visibility = "visible";
		document.getElementById("additionalLineMec").style.display = "block";
	}
	function hideMEC() {
		document.getElementById("monEspaceClient").style.display = "none";
		document.getElementById("monEspaceClient").style.visibility = "hidden";
		document.getElementById("additionalLineMec").style.display = "none";
	}
	function showStoreField(){
		document.getElementById("store").style.display="block";
		document.getElementById("store").style.visibility="visible";
		document.getElementById("store").style.display="block";
	}
	function hideStoreField(){
		document.getElementById("store").style.display="none";
		document.getElementById("store").style.visibility="hidden";
		document.getElementById("store").style.display="none";
	}
	
    function showStore(event) {
	    if(timeOutId){
	    	clearTimeout(timeOutId);
	    	timeOutId = null;
	    }
	    geo.shouldHidePopup = false;
	    var elementToDisplay = MAP_ENABLED ? 'userLocatedPopup' : 'userLocatedPopupNoMap';
	    showChangeShopPopup(elementToDisplay);
        //emulate autocomplete 
        if(geo.triggerSearch){
        	geo.triggerAutocomplete();
        	geo.triggerSearch = false;
            geo.initAutocompletionHandlers();
        }
    }
    
    //timeoutid to check if cursor is inside the popup
    timeOutId = null;
    eventHandlerInitialized = false;
	function hideStore(event) {
		if ((geo.isMapLoadedCorrectly || !MAP_ENABLED)){
			if(isMobileBrowser){
				hideStoreWihtoutTimeOut(event);
			} else {
		        timeOutId = setTimeout(function(event){
		        	hideStoreWihtoutTimeOut(event);
		        }, 50);
			}
        } else {
        	geo.shouldHidePopup = true;
        }
	}
	function hideStoreWihtoutTimeOut(event){
		var wrapper = $(geo.currentPopupId + ' .chooseShopSelectorWr .selectbox-wrapper')[0];
	    $(wrapper).css('display', 'none');
	    var geoPopup = $(geo.currentPopupId);
	    $(geoPopup).find('.searchErrorMessage').hide();
	    $(geoPopup).find('form input[type="hidden"]').val("");
	    $(geoPopup).hide();
	    $('body .ac_results').die('mouseover').die('mouseout').hide();
	    $(geo.currentPopupId + ' .search-input').blur();
	    document.getElementById("additionalLineLeftCol").style.visibility = "hidden";
	    geo.triggerSearch = true;
	    shouldResetBounds = true;
	}
// EOF MON ESPACE CLIENT POPUP
// VOTRE MAGASIN AUTO Popup
	function executeFunctionByTimeout(method, timeout){
		setTimeout(method, timeout);
	}

	function viewGeolocationAutoPopup(timeout){
		showGeolocationAutoPopup();
		executeFunctionByTimeout(hideGeolocationAutoPopup, timeout);
	}

	function showGeolocationAutoPopup(){
		document.getElementById("votreMagasin").style.display = "block";
		document.getElementById("additionalLineLeftCol").style.visibility = "visible";
	}
	
	function hideGeolocationAutoPopup(){
		document.getElementById("votreMagasin").style.display = "none";
		document.getElementById("additionalLineLeftCol").style.visibility = "hidden";
	}
//EOF VOTRE MAGASIN POPUP
//CHANGE SHOP POPUP
var mapInitialized = false;
var shouldResetBounds = false;
var boundsWereChanged = false;
var isErrorMessageDisplayed = false;
function showChangeShopPopup(elementId) {

		/*try {
			s_sendOmnitureInfo("","","location:select");
		} catch (e){			
		}*/
        document.getElementById("additionalLineLeftCol").style.visibility = "visible";
        document.getElementById(elementId).style.display = "block";
        document.getElementById(elementId).style.visibility = "visible";
    if(!mapInitialized) {
        var currentStore = geo.getCookie();
        if(!currentStore || currentStore == '999'){
            geo.callGeolocationService(geo.userLocation, showChangeShopPopupCallback);
        } else {
        	var params = {storeId: currentStore};
        	geo.callGeolocationService(params, showChangeShopPopupCallback);
        }
        mapInitialized = true;
    }
}

function showChangeShopPopupCallback(responseData){
    if (responseData != null) {
        stores = responseData.stores;
        if (stores && stores.length > 0) {
            geo.initUserLocatedPopup(stores);
        }
    }
}
	
	function hideChangeShopPopup(){
		document.getElementById("additionalLineLeftCol").style.visibility = "hidden";
		document.getElementById("userLocatedPopup").style.display = "none";
		document.getElementById("userLocatedPopup").style.visibility = "hidden";
	}
//EOF CHANGE SHOP POPUP

// ROUNDED CORNERS GENERATOR
	function DomCheck(){
		return(document.createElement && document.getElementById);
	}

	function DomCorners(id, bk, h, tries){
		var el=$(id)[0];
		var c = new Array(4);
		for(var i=0;i<4;i++) {                      // create the four elements for rounded corners
			c[i]=document.createElement("b");
			if (i==2) {
				c[i].className = "rcgBL";
			}
			if (i==0) {
				c[i].className = "rcgTL";
			}
		}
		c[0].appendChild(c[1]);
		c[2].appendChild(c[3]);
		el.style.padding="0";
		el.insertBefore(c[0],el.firstChild);       // add top corners
		el.appendChild(c[2]);                      // and bottom ones
	}
// EOF ROUNDED CORNERS GENERATOR


$(window).load(function() {
	jQuery.each($(".grayFrame"), function () {DomCorners (this, "/cast/images/rc.png", 6);});
	
	if ($.browser.msie) {
		fixScrollingMSIE();
	} else {
		fixScrolling();
	}
	
	var inpArr = document.getElementById('newsletterMail');
	if (inpArr.value.length>0) {
		inpArr.setAttribute('defaultValue', inpArr.value);
		inpArr.onfocus = inputFocus;
		inpArr.onblur = inputBlur;
	}


	document.getElementById("mecLabel").onmouseover = showMEC;
	document.getElementById("monEspaceClient").onmouseover = showMEC;
	document.getElementById("mecLabel").onmouseout = hideMEC;
	document.getElementById("monEspaceClient").onmouseout = hideMEC;

	/*$('.chooseShopSelectorWr .selectedStore').bind('click', function(){
		$wrapper = $(this).parent().find('.selectbox-wrapper');
		$(this).parent().find('.selectbox-wrapper').toggle();
	});*/
	
	/*function getStoresSearchResult(query, callback){
		callback(geo.storesForSearch);
	}*/

	/* BEGIN: resize all menus */
	var list_all = document.getElementById("mainMenuUL").childNodes;
	var list_elements = [];
	
	/*for (i = 0; i < list_all.length; i++) {
		if (list_all[i].tagName == "LI") {
			var li_key = list_all[i].className.replace(/\smainMenuPoint$/, "") 
			list_elements[li_key] = { liElement: list_all[i] };
			var findDiv = list_all[i].childNodes;
			for (j = 0; j < findDiv.length; j++) {
				if (findDiv[j].className == "upperMenuPopup") {
					list_elements[li_key].popupMenu = findDiv[j];
					break;
				}
			}
		}
	}

	for (ki in menu_length) {
		if (menu_length[ki].width) {
                	list_elements[ki].popupMenu.style.width = menu_length[ki].width + "px";
		} else if (menu_length[ki].full) {
			list_elements = getLiParams(list_elements);

			var first = true;	
			var f_element = l_element = null;

			for (j in list_elements) {
				if (!/^menu\_point\_([0-9])+/.test(j)) {
					continue;
				}
				if (first) {
					f_element = j;
					first = false;
				}
				l_element = j;
			}

			list_elements[ki].popupMenu.style.marginLeft = "-"+ (list_elements[ki].coords.x - list_elements[f_element].coords.x) + "px";
			list_elements[ki].popupMenu.style.width = list_elements[l_element].coords.x + list_elements[l_element].coords.width - list_elements[f_element].coords.x + "px";
		} else {			
			list_elements = getLiParams(list_elements);
			
			var l_element = null
			var check = false;
			var count = 0;

			for (j in list_elements) {
				if (j == ki) {
					check = true;	
				}
				if (check && count < menu_length[ki].items) {
					count++;
					l_element = j;
				}
			}

			if(/MSIE\s6\.0/.test(navigator.userAgent)) {
				list_elements[ki].popupMenu.style.width = list_elements[l_element].coords.x + list_elements[l_element].coords.width - 10 - list_elements[ki].coords.x + "px";
			} else {
				list_elements[ki].popupMenu.style.width = list_elements[l_element].coords.x + list_elements[l_element].coords.width - list_elements[ki].coords.x + "px";
			}
		}
	}*/

	if(document.all && document.getElementById) { 
		for (i = 0; i < list_all.length; i++) {
			node = list_all[i]; 
			if (node.nodeName == "LI") {
				node.onmouseover = function() { 
					this.className += " over";
					if($.browser.msie && $.browser.version=="6.0") {
					 $('#hideThisSelect').css('visibility', 'hidden');
					}
					var overLinkList = this.childNodes;
					for (j = 0; j < overLinkList.length; j++) {
						if (overLinkList[j].nodeName == "A") {
							overLinkList[j].className += " over";
							break;
						}
					}
				}
				node.onmouseout = function() { 
					this.className = this.className.replace(/\sover$/, ""); 
					if($.browser.msie && $.browser.version=="6.0") {
           $('#hideThisSelect').css('visibility', 'visible');
          }
					var overLinkList = this.childNodes;
					for (j = 0; j < overLinkList.length; j++) {
						if (overLinkList[j].nodeName == "A") {
							overLinkList[j].className = overLinkList[j].className.replace(/\sover$/, "");
							break;
						}
					}
				} 
			} 
		} 
	} 
	/* END: resize all menus */

	resizeOverlay();
	
	
	
	// FOOTER BREADCRUMBS are changed for product details page
	var headerBreadcrumbs = $(".breadcrumbs div.homeBreadIco");
	var sameBreadcrumsOnPage = $("#sameBreadcrumsOnPage");
	
	if(sameBreadcrumsOnPage.length != 0){
		if(headerBreadcrumbs.length != 0){
			var topBreadcrumbsHtml = headerBreadcrumbs.parent().html();
			var footerBreadcrumbs = $(".footerBreadcrumbs");
			var homeIcoUrl = $("#homeIcoUrl").text();
	
	        footerBreadcrumbs.html(topBreadcrumbsHtml);
			var active = footerBreadcrumbs.find("h1.active");
			var activeHtml = active.html();
			footerBreadcrumbs.removeClass("active").addClass("blue");
			//active.removeClass("active").addClass("blue").html("<strong>"+activeHtml+"</strong>");
			
			$('.footerBreadcrumbs h1.active').replaceWith("<div class='blue'><strong>" + activeHtml + "</strong></div>");
			
			footerBreadcrumbs.find("div.homeBreadIco").find("img").attr("src", homeIcoUrl);
			footerBreadcrumbs.find("div.homeBreadIco").removeClass();
			
		}	  
	} 
	
});

$(window).resize(function() {
		resizeOverlay();
		
		if ($.browser.msie) {
			fixScrollingMSIE();
		} else {
			fixScrolling();
		}
});

function fixScrolling() {
	if ((document.documentElement.clientWidth > 990) & (document.documentElement.clientWidth < 1026)) {
		document.body.style.overflowX = "hidden"
	} else {
		document.body.style.overflowX = "auto"
	}
}

function fixScrollingMSIE() {
	if ((document.documentElement.clientWidth > 990) & (document.documentElement.clientWidth < 1026)) {
		document.documentElement.style.overflowX = "hidden"
	} else {
		document.documentElement.style.overflowX = "auto"
	}
}


function resizeOverlay() {
	$(".gray_overlay").height(($("#whiteContainer").height() > $(document).height())?$("#whiteContainer").height():$(document).height());
}

function getScrollXY() {
    var x = 0, y = 0;
    if (typeof( window.pageYOffset ) == 'number') {
        // Netscape
        x = window.pageXOffset;
        y = window.pageYOffset;
    } else if (document.body && ( document.body.scrollLeft || document.body.scrollTop )) {
        // DOM
        x = document.body.scrollLeft;
        y = document.body.scrollTop;
    } else if (document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop )) {
        // IE6 standards compliant mode
        x = document.documentElement.scrollLeft;
        y = document.documentElement.scrollTop;
    }
    return [x, y];
}

function showPopup(id) {

    var wrapper = $("#" + id);
    var isMobileBrowser = /Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent);
    if (isMobileBrowser) {
        wrapper.css({
            'position': 'fixed',
            'top': parseInt(($(window).height() - wrapper.height()) / 2 + getScrollXY()[1], 10) + "px",
            'left': parseInt(($(window).width() - wrapper.children().width()) / 2 + getScrollXY()[0], 10) + "px"
        });
    }

    $(".gray_overlay").show();
    wrapper.show();
    resizeOverlay();
    setNewCompPosition(id);
    hideAllSelectBoxes();

    if (typeof(setPhoneNumberMaxLengthJQ) === 'function') {
        var country = wrapper.find('select[name="state"]').val();
        if (country == "F") {
            setPhoneNumberMaxLengthJQ(10);
        } else {
            setPhoneNumberMaxLengthJQ(17);
        }
    }
}

function showTermsPopup(){
    var wrapper = $("#termsAndConditions");
    $(".gray_overlay").show();
    wrapper.show();
    resizeOverlay();
    setNewCompPosition(id);
    hideAllSelectBoxes();
}
function hideAllSelectBoxes() {
  if($.browser.msie && $.browser.version=="6.0") {
    $('SELECT').css('visibility','hidden');
    $('.whitePopupContainer SELECT').css('visibility','visible');
  }
}

function showAllSelectBoxes() {
  if($.browser.msie && $.browser.version=="6.0") {
    $('SELECT').css('visibility','visible');
  }
}

function showQuestionPopup(id) {
	showPopup(id);
	$("#newMessage").hide();
}

function hidePopup(_obj) {
	$("#product_video_from_popup").empty();
	$("#videoPPP").empty();
	$(".gray_overlay").hide();
	$(_obj).parents(".whitePopupContainer").hide();	
	resizeOverlay();
	showAllSelectBoxes();
}
function hideQuestionPopup(_obj) {
	hidePopup(_obj);
	showPopup('newMessage');
}

function setNewCompPosition(_obj) {
	winHeight = $(window).height();
	popupHeight = $('#'+_obj+'.whitePopupContainer').height();
	popupHeight = popupHeight + 100;
	
	$('#'+_obj+'.whitePopupContainer').css("position", "absolute");
	if (winHeight < popupHeight) {

		topPosition = $('#'+_obj+'.whitePopupContainer').position().top;
		$('#'+_obj+'.whitePopupContainer').css("top", topPosition);
		
		//if($.browser.msie && $.browser.version=="6.0") {
			$.scrollTo('#'+_obj, 800);
		//}
		
	}
	
}

function getLiParams (_obj) {
	for (i in _obj) {
		_obj[i].coords = getElementPosition(_obj[i].liElement);
	}
	return _obj;
}

function shuffleMenu(_obj) {
	if (_obj.parentNode.className != "menuPoint open") {
//		var childs = _obj.parentNode.parentNode.childNodes;
//		for (i = 0; i < childs.length; i++) {
//			if (childs[i].tagName == "DIV") {
//				childs[i].className = "menuPoint";
//			}
//		}
		_obj.parentNode.className = "menuPoint open";
	} else {
		_obj.parentNode.className = "menuPoint";	
	}
}

function shuffleBannerMenu(_obj) {
    if (_obj.parentNode.className != "menuPoint open") {
//      var childs = _obj.parentNode.parentNode.childNodes;
//      for (i = 0; i < childs.length; i++) {
//          if (childs[i].tagName == "DIV") {
//              childs[i].className = "menuPoint";
//          }
//      }
        _obj.parentNode.className = "menuPoint open";
    } else {
        _obj.parentNode.className = "menuPoint";    
    }
}

function getElementPosition(_obj) {
    var elem = _obj;
    
    var w = elem.offsetWidth;
    var h = elem.offsetHeight;
    
    var l = 0;
    var t = 0;
    
    while (elem) {
        l += elem.offsetLeft;
        t += elem.offsetTop;
        elem = elem.offsetParent;
    }

    return {"x":l, "y":t, "width": w, "height":h};
}

// show / hide products info
var timeoutIn, timeoutOut;
function elargeImage(_obj, _bool) {
	var inner_blocks = _obj.getElementsByTagName('div');
	for (i = 0; i < inner_blocks.length; i++) {
		if (inner_blocks[i].className == "elargeProduct") {
			var cur_block = inner_blocks[i];
			if (_bool) {
				clearTimeout(timeoutOut);
				timeoutIn = setTimeout(function() {
					clearTimeout(timeoutIn);					
					var all_popups = document.getElementsByTagName('div');
					
					for (j = 0; j < all_popups.length; j++) {
						if (all_popups[j].className == "elargeProduct") {
	                                		all_popups[j].style.visibility = "hidden"; 
							all_popups[j].parentNode.parentNode.style.zIndex = "5";
						}
					}
                               		cur_block.style.visibility = "visible"; 
					cur_block.parentNode.parentNode.style.zIndex = "6"; 
				}, 500);
				
			} else {				
				clearTimeout(timeoutIn);				
				timeoutOut = setTimeout(function() {
					clearTimeout(timeoutOut);
					cur_block.style.visibility = "hidden";
					
					var all_popups = document.getElementsByTagName('div');
					for (j = 0; j < all_popups.length; j++) {
						if (all_popups[j].className == "elargeProduct") {
	                                		all_popups[j].style.visibility = "hidden"; 
							all_popups[j].parentNode.parentNode.style.zIndex = "5";
						}
					}
					cur_block.parentNode.parentNode.style.zIndex = "5"; 
				}, 200);
			}
		}
	}
}

function toggleCamparateur() {
	var campExpaned = document.getElementById('campExpanded');
	var campCollapsed = document.getElementById('rd_campCollapsed');
	
	if (campExpaned.style.display == "block") {
		campExpaned.style.display = "none";
		campCollapsed.style.display = "block";
	} else {
		campExpaned.style.display = "block";
	}
}
/*
function toggleCamparateur() {
	$('#campExpanded').slideDown();
  $('#rd_campCollapsed').css('display', 'none');
}
*/

function collapseTxt(_obj) {
	if ($(_obj).parent("div").find("textarea:eq(0)").css("display") == "inline") {
		$(_obj).parent("div").find("textarea").hide();
		$(_obj).parent("div").find(".scrCollapser").html("&gt;");
	} else {
		$(_obj).parent("div").find("textarea").show();
		$(_obj).parent("div").find(".scrCollapser").html("v");
	}
}

function switchSearchTab(_obj){
}

function changeImage(obj) {
	$("#printButtonPopup").show(); //button for print
	$("#productLargeImage").show();
	$("#popupImgLrgActive").show();
	$("#product_video_from_popup").empty().hide();
	var src = $(obj).find("IMG:eq(0)").attr('src');	
	//src = src.replace(/43x43/, "270x270");	
	if ($(obj).parents("#zoomedImage:eq(0)").length) {
//	$("#popupImgLrgActive").css("visibility", "hidden");	
		$("#popupImgLrgActive").attr('src', src);
	} else {
		$("#lrgImg").css("visibility", "hidden");	
		$("#lrgImg").attr('src', src);
	}
		
	$(obj).parents("UL:eq(0)").find(".lImBorder").removeClass("lImBorder");
	$(obj).addClass("lImBorder");
	
	//$(_obj).parents("UL:eq(0)").find(".lImBorder").removeClass("lImBorder");
  //$(_obj).addClass("lImBorder");
	$('#popupImgLrgActive').attr('currentImgNumber', $(obj).parent().attr('imgNumber'));
  
}

function changeMagasinImage(obj) {
	var src = $(obj).find("IMG:eq(0)").attr('src');	
	$("#lrgImg").css("visibility", "hidden");	
	$("#lrgImg").attr('src', src);
	//$(obj).parents("UL:eq(0)").find(".lImBorder").hide();
	//$(obj).find(".lImBorder").show();
	
	$(obj).parents("UL:eq(0)").find(".lImBorder").removeClass("lImBorder");
  $(obj).addClass("lImBorder");
}


$(window).load(function(){
  $('.styled').selectbox({debug: false});
  
  $('.mainMenuUL').mouseover(function(){    
      $("INPUT").blur();
  });
  
  $(".filterChGroup > div:not([class*='autres'])").live('click',function(e){
	$(this).find('input:checkbox').trigger('click', e);
  });
  
  $(".filterChGroup > div:not([class*='autres']) input:checkbox").live('click', function(e){	
	return false;
  });
  
  $('#userNotLocatedNoMap .closePopup a, #userNotLocatedPopup .closePopup a').click(function(){
	  try {
		  s_sendOmnitureInfo("","","location:close",'web');
	  } catch (e){}
      $(".gray_overlay").hide();
      $(this).parent().parent().hide();
      $.cookie(DISPLAY_GEO_POPUP, true, { path: COOKIE_PATH });
      geo.initHeaderEventHandlers();
      setTimeout('window.location.reload()', OMNITURE_SYNCH_DELAY);
  });
  
  /*$('.selectbox-wrapper ul > li').click(function(){
	var text = $(this).text();
	var $spanInput = $(this).parent().parent().siblings('.selectedStore').find('.input');
	var castStoreId = $(this).attr('id');
	$spanInput.text(text);
	$spanInput.attr('id', castStoreId);
	if($(this).hasClass('iscc')){
		$(this).parent().parent().siblings('img').css('display', 'block');
	} else {
		$(this).parent().parent().siblings('img').css('display', 'none');
	}
	$(this).parent().parent().css('display', 'none');
  });*/
  
  $('.ajoutpanierContainer').show();
    if ($('.ajoutpanierContainer').size() > 0){
      $(".productItem .buttonCartContainer").addClass("galleryView");
      $('.ajoutpanierContainer .productsRow').addClass('clearfix');
      $(".productsRow").each(function(){
        galleryViewProductsRowHeight($(this));
      });
      if ($(".deliverOrCCwr").length){
        deliverOrCCwrEqualHeight();
      }
    }
    $('.azcontent .paginator A').click(function(){
      $('.azcontent .paginator A.active').removeClass('active');
      $(this).addClass('active');
    });

    $('#footer .rightCol .subCol UL:not(:has(LI))').hide();

    if($.browser.msie) {
      if ($('#featuredProd_input')) {
        $('#featuredProd_input').before('<div class="dropDownAnchor" id="featuredProd_input_anch"></div>');
        $('#featuredProd_input_anch').click(function(){
          $('#featuredProd_container').toggle();
        });
      }

      if ($('#baLeft_input')) {
        $('#baLeft_input').before('<div class="dropDownAnchor" id="baLeft_input_anch"></div>');
        $('#baLeft_input_anch').click(function(){
          $('#baLeft_container').toggle();
        });
      }

      if ($('#baRight_input')) {
        $('#baRight_input').before('<div class="dropDownAnchor" id="baRight_input_anch"></div>');
        $('#baRight_input_anch').click(function(){
          $('#baRight_container').toggle();
        });
      }
    }

	
	// ************************************************************
	// Set IFRAME only for IE6 (Fix selectbox overlay bug)
	// ************************************************************
	
	if($.browser.msie && $.browser.version=="6.0") {
		$('.gray_overlay').html('<iframe frameborder="0"></iframe>');
		
		userWidth = $('#monEspaceClient .userShadow').width();
		userHeight = $('#monEspaceClient .userShadow').height();
			
		$('#monEspaceClient .userShadow').before('<iframe frameborder="0" class="clientIframe"></iframe>');
		$('.clientIframe').width(userWidth);
		$('.clientIframe').height(userHeight);
	
		cornBoxHeight = $('.grayCornerGray').height();

	  if (cornBoxHeight%2 != 0) {
	     $('.cornerBottomLeft').css('bottom','-2px');
	     $('.cornerBottomRight').css('bottom','-2px');
	  }
		
	}
	
	
});


	// ************************************************************
	// Count comparator items and set equal width for product rows.
	// ************************************************************ 

function countCamparateurItems() {
	var prodImageRow = $('.productImages TD');
	
	if (prodImageRow.size() == 1) {
		
	} else if (prodImageRow.size() == 2) {
		prodImageRow.width(342);
	} else if (prodImageRow.size() == 3) {
		prodImageRow.width(228);
	} else if (prodImageRow.size() == 4) {
		prodImageRow.width(171);
	};
	
}

function showAjouterPopup(id) {
    $('.autresLnk').parent("DIV").not(id).css("z-index", 999);
    if ($(id).is(":visible")) {
        $(id).hide();
    } else {
        $(id).show();
    }
    $(id).parent("DIV").css("z-index", 1000);

}



function showContPopup(_id, _param) {
	if ($('.autresPopup').length > 1) {
		
		$('.autresPopup')
			.not('div#ajouterPopup_'+_id)
			.hide(1, function () {
				});
				
		$('div#ajouterPopup_'+_id)
					.toggle()
					.parent('div')
					.css('z-index', 1000);
		var loaded = $("div#ajouterPopup_" + _id).attr("loaded");
		if (loaded == 'false') {
			
			$("div#ajouterPopup_" + _id).load(_param, null, function() {$("div#ajouterPopup_" + _id).attr("loaded", 'true');});
			
		}		
				
		$('.autresPopup')
			.not('div#ajouterPopup_'+_id)					
			.parent('div')
			.css('z-index', 999);
			/**
			$('div#ajouterPopup_'+_id)
					.toggle()
					.parent('div')
					.css('z-index', 1000);
				}
			*/
		/*var loaded = $("div#ajouterPopup_" + _id).attr("loaded");
		if (loaded == 'false') {
			
			$("div#ajouterPopup_" + _id).load(_param);
			$("div#ajouterPopup_" + _id).attr("loaded", 'true');
		}*/
	} else {
		var loaded = $("div#ajouterPopup_" + _id).attr("loaded");
		if (loaded == 'false') {
			
			$("div#ajouterPopup_" + _id).load(_param, null, function() {$("div#ajouterPopup_" + _id).attr("loaded", 'true');});
			
		}
		$('.autresPopup')
			.toggle()
			.parent('div')
			.css('z-index', 1000);
	}
}

function showAtoutContPopup(_param) {
	var loaded = $("div#atoutPopup").attr("loaded");
	if (loaded == 'false'){
		$("div#atoutPopup").load(_param, null, function() {$("div#atoutPopup").attr("loaded", 'true'); $(".gray_overlay").show();});
	}else{
		$("div#atoutPopup").show();
		$(".gray_overlay").show();
	}
	
}

function closeAtoutContPopup(){
	$("div#atoutPopup").hide();
	$(".gray_overlay").hide();
}

function showVideoPopup(_id, _param){
	s_sendOmnitureInfo(_param.substring(0,_param.indexOf("&")),"Video Produit Castoche","","","");
	$("#" + _id).show();	
	$("#" + _id).load(_param+"&uid="+Math.random() * 100, null, function() {
	   $(".gray_overlay").show();
	   setNewCompPosition(_id);
	   resizeOverlay();
	});	
}

function hideVideoPopup(_id, _param){
	$("#" + _id).empty().hide();
	$(".gray_overlay").hide();	
}

function show3DPopup(_id, _param){
  $("#" + _id).show();  
  $("#" + _id).load(_param+"&uid="+Math.random() * 100, null, function() {
     $(".gray_overlay").show();
     setNewCompPosition(_id);
     resizeOverlay();
  }); 
}

function hide3DPopup(_id, _param){
  $("#" + _id).empty().hide();
  $(".gray_overlay").hide();  
}


function showImgPopup(_obj) {

	var imgUrl = $(_obj).parents(".productImage:eq(0)").find("#lrgImg").attr("src");	
	$("#popupImgLrgActive").attr("src", imgUrl);
	$("#zoomedImage").find(".popupContentContainer:eq(0)").find(".imageViews").remove();
	var previews = $(_obj).parents(".productImageColumn:eq(0)").find(".imageViews").clone();	
	
	$("#zoomedImage").find(".popupContentContainer:eq(0)").append(previews);
	var previewsIns = $("#zoomedImage").find(".popupContentContainer:eq(0)").find(".imageViews");
	previewsIns.find("h3").remove();
	previewsIns.append("<div class='clear'></div>");
	previewsIns.find("a").removeAttr("onclick");
  previewsIns.find("a").click(function(){
    changeImage(this);
  });
	
	if(_obj.className = "controlZoom") {
    $('#zoomedImage .imageViews A:first').click();
  };
	
	$("#zoomedImage").find(".whitePopupContent").css("width", "642px");
	$("#zoomedImage").show();
		
	$(".gray_overlay").show();	
	resizeOverlay();
}

function showImgPopup2(_obj, _param) {

  $(_obj).parents("UL:eq(0)").find(".lImBorder").removeClass("lImBorder");
  $(_obj).addClass("lImBorder");
	

if (_param != null) { // show video
      s_sendOmnitureInfo(_param.substring(0,_param.indexOf("&")),"Video Produit Castoche","","","");
      $("#productLargeImage").show();
      $('#popupImgLrgActive').hide();
      $("#printButtonPopup").hide();

			var _id="product_video_from_popup";
      $("#" + _id).show();
      $("#" + _id).load(_param+"&uid="+Math.random() * 100, null, function() {

          $("#videoPopupHeader").hide();
					$("#videoPopupPrice").hide();
          $("#3DPopupHeader").hide();
			
      });

  } else {	// show pictures

  var imgUrl = $(_obj).find("img").attr("src");  
  $("#productLargeImage").show();
  $('#product_video_from_popup').hide();
  $("#popupImgLrgActive").attr("src", imgUrl).show();
	$('#popupImgLrgActive').attr('currentImgNumber', $(_obj).parent().attr('imgNumber'));
  
  }


//

  $("#zoomedImage").find(".popupContentContainer:eq(0)").find(".imageViews").remove();
  var previews = $(_obj).parents(".productImageColumn:eq(0)").find(".imageViews").clone(true);

  
  $("#zoomedImage").find(".popupContentContainer:eq(0)").append(previews);
  var previewsIns = $("#zoomedImage").find(".popupContentContainer:eq(0)").find(".imageViews");
  previewsIns.find("h3").remove();
  previewsIns.append("<div class='clear'></div>");
	previewsIns.find("a").each(function(){
		if ($(this).attr('param')) {	// if video

			$(this).removeAttr("onclick");
			$(this).bind('click', function(){
				var _id = $(this).attr('id');
				$(this).removeAttr("id");
				$('.closeBut:eq(0)').click();
				$('#'+_id).click();
			});

		} else if ($(this).hasClass('controlLink')) { //3D
      $(this).bind('click', function(){
        window.open($(this).attr('href'));
        return false;
      });
      
    } else {	// if picture

			$(this).removeAttr("onclick");
			$(this).removeAttr("id");
 			$(this).click(function(){
    		changeImage(this);
  		});
	}
	});

//  previewsIns.find("a").removeAttr("onclick");
  
  $("#zoomedImage").find(".whitePopupContent").css("width", "800px");
  $("#zoomedImage").show();
	
	$(".gray_overlay").show();  
  resizeOverlay();    

  if ($('.popupContentContainer .imgGalleryItem').length == 1) {
	  $('.popupContentContainer .toLeft, .popupContentContainer .toRight').hide();
  } else {
	  $('.popupContentContainer .toLeft, .popupContentContainer .toRight').show();
  };
  

  //$(_obj).parents("UL:eq(0)").find(".lImBorder").hide();
  //$(_obj).find(".lImBorder").show();
  
  
  //$(_obj + ".productImageColumn .imageViews .lImBorder").removeClass("lImBorder");
  //$(_obj).addClass("lImBorder");

  
  
}

function zoomPopupByClickOnProductPicture(productPicture){
	var curPictureNumber = $(productPicture).attr('currentImgNumber');
	$('#popupImgLrgActive').attr('currentImgNumber', curPictureNumber);
	$('.imgGalleryItem[imgNumber='+ curPictureNumber +'] a:visible').click();

}

function popupKeyEventsHandler(event){
	if($('.gray_overlay').css('display') == "block"){
		switch(event.which){
		case 27: // esc key
			$('.closeBut').click();
			break;
		case 37: // arrow left
			showPreviousPicture();
			break;
		case 39: // arrow right
			showNextPicture();
			break;
		}
	}
}

function showPreviousPicture(){
	

	if ($('#popupImgLrgActive:visible').length == 0) {

		$('.imgGalleryItem[imgNumber='+ ($('#zoomedImage .imgGalleryItem').length-1) +'] a:visible').click();
		$("#product_video_from_popup").hide();
		//$('#popupImgLrgActive').attr('currentImgNumber', itemsCount);
		$("#popupImgLrgActive").show();
	} else {
		var curPictureNumber = parseInt($('#popupImgLrgActive').attr('currentImgNumber'));
		var prevPictureNumber = curPictureNumber -1;
		var itemsCount =$('.imgGalleryItem').size()/2; // small pictures clone when popup is zoomed
		if (prevPictureNumber < 0){
			prevPictureNumber = itemsCount - 1;
		}
		$('.imgGalleryItem[imgNumber='+ prevPictureNumber +'] a:visible').click();
//		$("#popupImgLrgActive").show();
	};
}

function showNextPicture(){
	if ($('#popupImgLrgActive:visible').length == 0) {
		$('.imgGalleryItem[imgNumber="0"] a:visible').click();
		$("#product_video_from_popup").hide();
//		$("#popupImgLrgActive").show();
	} else {
		var curPictureNumber = parseInt($('#popupImgLrgActive').attr('currentImgNumber'));
		var nextPictureNumber = curPictureNumber + 1;
		var itemsCount = $('.imgGalleryItem').size()/2; // small pictures clone when popup is zoomed
		if (nextPictureNumber > itemsCount - 1){
			nextPictureNumber = 0;
		}
		$('.imgGalleryItem[imgNumber='+ nextPictureNumber +'] a:visible').click();
		//$("#popupImgLrgActive").show();		
	};
}

function counAndShowCarItems(maxCount) {
  var carouselItems = $('.carThumbs UL > LI').size();
  if(carouselItems > 0) {
    if (carouselItems < maxCount) {
      $('.carousel').prepend('<div class="carProdCountHolder"><div class="carProdCount">' + carouselItems + ' produits</div></div>');
    } else {
      $('.carousel').prepend('<div class="carProdCountHolder"><div class="carProdCount">Les ' + maxCount + ' premiers produits</div></div>');
    }
  }
    if ($('.breadcrumbs').length == 0) {
        $('.carProdCount').css("top","-15px");
    }
}

function disableButton(_obj) {
   if(_obj) {
    var name = _obj.name;
    var value = _obj.value;
    var form = $(_obj).parent('form');
    if (!form.length) {
    	form = $(_obj).closest('form');
    }
    
    if(form) {
      $(_obj).attr("name", name + "_");
      $(_obj).attr("value", value);
      $(_obj).attr("disabled", true);
       // create hidden input with the same name and value
       form.append("<input type='hidden' name='" + name + "' value='" + value + "' />");
       form.submit();
    }
     return false;
   } 
   return true;
}

function typeGiftMessage() {
  var giftMessage = $("#giftMessage").attr("value");
  var giftCheckbox = $("#isGift");
  if(giftCheckbox) {
	  if(giftMessage && giftMessage.length > 0) {
	    giftCheckbox.attr("checked","true");
	  } else {
      giftCheckbox.attr("checked","");
	  }
  }
}

function removeGiftMessage(_obj) {
  if(!_obj.checked) {
    $("#giftMessage").attr("value", "");
  }
}


function processTRail(_obj, _str, _or, _inputsFacetValues, _rf){
    if (_inputsFacetValues.length ==0){
    	return _rf+":"+_str;
    }
    var _facetId = _str.split(/:/)[0];
    var _facetValue = _str.split(/:/)[1];

    
    var re1 = new RegExp(_facetId, "g");
    var re2 = new RegExp(":", "g");
    var _keyEndNum = 0;
    var _keyStartNum = 0;
    do {
        var m = re1.exec(_inputsFacetValues);
        if (re1.lastIndex != 0) {
            _keyEndNum = re1.lastIndex;
            _keyStartNum = m.index;
        }
    } while (re1.lastIndex != 0);
    var _valueStartNum = 0;
    var _valueEndNum = 0;
    var _sep = ":";
    
    var _match2 = /:/g.exec(_inputsFacetValues); 
    if (_match2 == null || _match2.length == 0){
        _sep = "/";
    }
    if (_keyEndNum == 0) {
        return _inputsFacetValues+_sep+_facetId+_sep+_facetValue;
    }

        var mm = re2.exec(_inputsFacetValues.substring(_keyEndNum));
        mm = re2.exec(_inputsFacetValues.substring(_keyEndNum));
        if (mm != null) {
            _valueStartNum = mm.index;
        }
        _valueEndNum = re2.lastIndex;
        var _facetValueString;
        if (_valueEndNum == 0){
            _facetValueString = _inputsFacetValues.substring(_keyEndNum+_valueEndNum+1);
        } else {
            _start =_keyEndNum; 
            _end =_keyEndNum+_valueEndNum-1;
            if (_keyEndNum > 0) {
                _start = _keyEndNum+1;
            }
            _facetValueString = _inputsFacetValues.substring(_start,_keyEndNum+_valueEndNum-1);
            var regEnd = new RegExp(_facetValueString+"$","g");
            if (regEnd.exec(_rf)){
                _facetValueString = _rf;
            }
        }
        if (_facetValueString == _facetValue) {
            _facetValueString = _facetId+_sep+_facetValue;
        }
        var restr = _facetValueString;
        var specials = new RegExp("[.*+?|()\\[\\]{}\\\\]", "g");// .*+?|()[]{}\
        var newRestr = restr.replace(specials, "\\$&");
        var re = new RegExp( newRestr );
        if ($(_obj).attr('checked')){
            if (_facetValueString != _rf){
                restrSum = restr+_or+_facetValue;
            } else {
                restrSum = restr+":"+_str;
            }
        } else {
            restrSum = restr.replace(_facetValue,"");
            restrSum = restrSum.replace(/_$/g,"");
            restrSum = restrSum.replace(/^_/g,"");
            restrSum = restrSum.replace(/:$/g,"");
            restrSum = restrSum.replace(/\/$/g,"");
            if (restrSum == _facetId){
                restrSum = ""; 
                re = new RegExp( _facetValueString, "g" );
            }
        }
        var _match1 = re.exec(_inputsFacetValues);
        var replacedText = _inputsFacetValues.replace(re,restrSum);
        replacedText = (replacedText.replace(/:$/g,""));
        replacedText = (replacedText.replace(/\/$/g,""));
        replacedText = (replacedText.replace(/^:/g,""));
        replacedText = (replacedText.replace(/=:/g,"="));
        replacedText = (replacedText.replace(/:\|/g,":"));
        replacedText = (replacedText.replace(/\|$/g,""));
        replacedText = (replacedText.replace(/\\$/g,""));
        replacedText = (replacedText.replace(/^\\/g,""));
        replacedText = (replacedText.replace(/=\\/g,"="));
        replacedText = (replacedText.replace(/::/g,":"));
        replacedText = (replacedText.replace(/\|\|/g,"\|"));
        replacedText = (replacedText.replace(/__/g,"_"));
        var _ma = replacedText.match(new RegExp(_sep,"g"));
        if(_ma != null &&_ma.length == 1){
        	replacedText = "";
        }
        _inputsFacetValues = replacedText;
     return _inputsFacetValues;
}
function getRootFacet(_inputsFacetValues){
	var re = new RegExp(":","g");
	var w = re.exec(_inputsFacetValues).index+1;
	var m = re.exec(_inputsFacetValues.substring(w)).index;
	var _resultVar = _inputsFacetValues.substring(0,w+m);
	return _resultVar 
}

function goToURL(url) { 
	var a = document.createElement("A"); 
	if(!a.click) 
		{ 
			window.location = url;  
			return; 
		} 
	a.setAttribute("href", url);
	a.style.display = "none";
	$("body").append(a);
	a.click();
}
function showLVMenu(){
//	document.getElementById("topicMenu").style.visibility='visible';
    $("#topicMenu").show('slide', {}, 1000, function(){
		this.style.visibility = 'visible';
	});
    return false;
}


function hideLVMenuButton(){
    $("#themesMenuButton").hide('slide', {}, 400, showLVMenu);
    return false;
}

function showLVMenuButton(){
    $("#themesMenuButton").show('slide', {}, 400);
    return false;
}

function hideLVMenu(){
    $("#topicMenu").hide('slide', {}, 1000, showLVMenuButton);
}

function createXMLHttpRequest()
{
  if (window.XMLHttpRequest) { // if Mozilla, Safari etc
    return new XMLHttpRequest();
  } else if (window.ActiveXObject){ // if IE
    try {
      return new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e) {
      try {
        return new ActiveXObject("Microsoft.XMLHTTP");
      } catch (ec) {}
    }
  }
  else {
    alert("Nous vous informons que votre navigateur ne prend pas en charge certaines fonctions\n" +
      "permettant la recherche assist�e sur le site Castorama.fr, ou bien ces fonctions sont\n" +
      "d�sactiv�es par un param�trage de s�curit� de votre navigateur.");
    return false;
  }
}

var ajaxRequest = createXMLHttpRequest();

function showAnotherThematiques(currIndex) {
  
	  var url = contextPath + "/troc/includes/pictureMenu.jsp?currentIndex=" + currIndex;
	  ajaxRequest.open("GET", url , true);
	  ajaxRequest.onreadystatechange = function() {
	    if(ajaxRequest.readyState == 4 && ajaxRequest.status == 200) {
		   document.getElementById("themesPictureMenuContainer").innerHTML=ajaxRequest.responseText;
	 
	    }
	  }
	  ajaxRequest.send(null);
}


function SearchTab(){
	this.searchTabs = new Array("produitsTab", "ideasTab", "magasinTab");
	
	this.getSearchTabHeader = function (tabId) {
		return document.getElementById(tabId + "Header");
	};
	
	this.inactiveSearchTabHeader = function (tabId) {
	
	    if (document.getElementById(tabId + "Header") != null && document.getElementById(tabId + "Header") != undefined){
		document.getElementById(tabId + "Header").className = "";
		}
	};
	
	this.inactiveAllSearchTabHeaders = function () {
		for (var i = 0; i < this.searchTabs.length; i++) {
			this.inactiveSearchTabHeader(this.searchTabs[i]);
		}
	};
	
	this.activeSearchTabHeader = function (tabId) {
	
		if (document.getElementById(tabId + "Header") != null && document.getElementById(tabId + "Header") != undefined){
		
			document.getElementById(tabId + "Header").className = "active";
			
			
		}
		
	};
	
	this.activeSearchTabContent = function (tabId) {
		if (document.getElementById(tabId) != null && document.getElementById(tabId) != undefined){
			document.getElementById(tabId).className = "searchContent_active";
		}
		
	};
	
	this.inactiveSearchTabContent = function (tabId) {
	  if (document.getElementById(tabId) != null && document.getElementById(tabId) != undefined){
		document.getElementById(tabId).className = "searchContent";
	  }
	};
	
	this.inActiveAllSearchTabContents = function () {
		for (var i = 0; i < this.searchTabs.length; i++) {
			this.inactiveSearchTabContent(this.searchTabs[i]);
		}
	};
	
	this.switchToSearchTab = function (tabId) {
		this.inactiveAllSearchTabHeaders();
		
		this.inActiveAllSearchTabContents();
		this.activeSearchTabHeader(tabId);
		this.activeSearchTabContent(tabId);
	};
	
}
function switchSearchTab(currentSearchTabId) {
	var searchTab = new SearchTab();
	
	searchTab.switchToSearchTab(currentSearchTabId);
	
	return false;
}



function activeTab(index,size)
	{	
		for(i=0;i<size;i++)
		{
			dojo.byId("title_"+i).className="";
			dojo.byId("content_"+i).className="tab_content";
		}
		dojo.byId("title_"+index).className="active";
		dojo.byId("content_"+index).className="tab_content active";
	}

function switchNewsletterTab(currentNewsletterTabId) {
    var newsletterTab = new NewsletterTab();
    
    newsletterTab.switchToNewsletterTab(currentNewsletterTabId);
    
    return false;
  }

function NewsletterTab(){
    this.newsletterTabs = new Array("subscribeTab", "unsubscribeTab", "editSubscriptionTab");
    
    this.getNewsletterTabHeader = function (tabId) {
      return document.getElementById(tabId + "Header");
    };
    
    this.inactiveNewsletterTabHeader = function (tabId) {
    
        if (document.getElementById(tabId + "Header") != null && document.getElementById(tabId + "Header") != undefined){
      document.getElementById(tabId + "Header").className = "";
      }
    };
    
    this.inactiveAllNewsletterTabHeaders = function () {
      for (var i = 0; i < this.newsletterTabs.length; i++) {
        this.inactiveNewsletterTabHeader(this.newsletterTabs[i]);
      }
    };
    
    this.activeNewsletterTabHeader = function (tabId) {
    
      if (document.getElementById(tabId + "Header") != null && document.getElementById(tabId + "Header") != undefined){
      
        document.getElementById(tabId + "Header").className = "active";
        
        
      }
      
    };
    
    this.switchToNewsletterTab = function (tabId) {

      this.inactiveAllNewsletterTabHeaders();
      this.activeNewsletterTabHeader(tabId);
    };
    
  }

function setNewsletterUpdateURLs(buttonType) {
    
    if (buttonType != null && buttonType != undefined && buttonType != 'undefined') {
        
        if(buttonType == 'validateEmail') {
          document.getElementsByName("/com/castorama/commerce/clientspace/CastNewsletterFormHandler.updateSuccessURL")[0].value = 'myNewsletters2.jsp';
          document.getElementsByName("/com/castorama/commerce/clientspace/CastNewsletterFormHandler.updateErrorURL")[0].value = 'myNewsletters2.jsp';
          return true;
        }
    }
    if (buttonType != null && buttonType != undefined && buttonType != 'undefined') {
        
        if (buttonType == 'updateSettings') {
          document.getElementsByName("/com/castorama/commerce/clientspace/CastNewsletterFormHandler.updateSuccessURL")[0].value = 'myNewsletters1.jsp';
          document.getElementsByName("/com/castorama/commerce/clientspace/CastNewsletterFormHandler.updateErrorURL")[0].value = 'myNewsletters2.jsp';
          return true;
        }
    }
}

var comparisonContainerUrl;
var comparisonMngUrl;
var arrayOfProducts = new Array();
var prodList = new Array();

var shoppingListSkus = new Array();
	
	$(document).ready(function(){
			
			prepareDraggables();
					
			comparisonContainerUrl = contextPath + "/comparison/comparisonContainer.jsp";
			comparisonMngUrl = contextPath + "/comparison/comparisonMng.jsp";
			
			shoppingListMngUrl = contextPath + "/shoppingList/shoppingListManager.jsp";
			
			// expand comparison block if there are items inside
			if($('#campExpanded a.campRemove').length){
				document.getElementById('campExpanded').style.display = "block";
				document.getElementById('rd_campCollapsed').style.display = "none";	
			}
			
			// make items in shopping list clicable
			$(".sliderItem").click(function(){
				var item = $(this);
				var link = item.find("#productUrl").val();
				window.location = link;
				
			});
			
            // remove "droppable" classes from items inside comparison block   
			var divs = $("div#campExpanded div.rendered");					
			jQuery.each(divs, function(i, val) {
				var holder = $(val);
				//holder.removeClass("ui-droppable");
				//holder.removeClass("droppable");
				//holder.droppable('option', 'accept', '');
		    });
		    
		    // add onclick event function to every item in comparison block
		    var images = $("div#campExpanded a.campRemove");					
			jQuery.each(images, function(i, val) {
		    	$(val).click(function(ev){removeImage(ev);} );
				$(val).siblings('img').click(function(ev){
					$(this).siblings('a.campRemove').trigger('click', ev);
				});
		    });	
			
			// add item to the comparison block
			$(".droppable").droppable({
				greedy: false,
				hoverClass: 'drophover',
				accept: '.prdMarker',
				scope: 'addToComp',
				tolerance: 'pointer',			
				drop: function(event, ui) {	
					var dropPlace = $(this);
					if(dropPlace.contents().length == 0){
						addImage($(this), ui.draggable);
					} 										
				}
			});
			
			$("#dropPlace").droppable({
				greedy: false,
				accept: '.slPrdMarker',
				scope: 'addToComp',
				tolerance: 'pointer',			
				drop: function(event, ui) {
					addImageToShoppingList($(this), ui.draggable);
				}
			});
			
			
			
			// expand comparison block if item is above it 
			$(".droppable2").droppable({
				greedy: false,
				hoverClass: 'drophover',
				accept: '.prdMarker',
				scope: 'addToComp', 
				tolerance: 'pointer', 
				over: function(event, ui) {					
					var campExpaned = document.getElementById('campExpanded');
					var campCollapsed = document.getElementById('rd_campCollapsed');

					if (campExpaned.style.display != "block") {						
						campExpaned.style.display = "block";
						campCollapsed.style.display = "none";						
					} 					
				},				
				drop: function(event, ui) {					
				}
			});
			
			$("body").droppable({
				greedy: false,
				accept: '.cmpMarker',	
				scope: 'removeFromComp',			
				drop: function(event, ui) {
					removeFromComparison($(this), ui.draggable);
				}
			});
			
			$(".cmpMarker").draggable({ 
				opacity: 0.7, 
				helper: 'clone',
				zIndex: 10000, 
				appendTo: 'body',
				containment: 'document',
				revert: 'invalid', 
				scope: 'removeFromComp'
			});
			
			function addHadlerToCompPopup(){			
				// add onclick event handler to remove product from comparison popup
				var compPopupRemoveLinks = $("#comparateurPopup .productImages .productImageHolder a.campRemove");
				$.each(compPopupRemoveLinks, function(i, val){
					$(val).click(function(ev){
						var self = $(this);
						// check if count of products is < 3
						var linksToCheck = $("#comparateurPopup .productImages .productImageHolder a.campRemove");
						if(linksToCheck.size() > 2){
							var index = linksToCheck.index(self);
							removeProdFromCompTable(index);
							// trigger removing product from comparison block
							var compIdSelector = "#comp_" + self.attr("id").substring(6);
							$(compIdSelector).trigger("click", ev);
							countCamparateurItems();
						}
					});
					$(val).siblings("img").click(function(ev){
						$(this).siblings('a.campRemove').trigger('click', ev);
					});
					
				});
				
				// code to remove product from the table in the comparison popup
				function removeProdFromCompTable(index){
					var rows = $("#comparateurPopup .compTable tr");
					$.each(rows, function(i, val){
						$(val).children("td").eq(index).remove();
					});
				}					
			}
			
			// opens comparison page
			$('#btnCompareProducts').click(function (){			
			    comparisonContainerUrl = comparisonContainerUrl + "?continueUrl=" + continueUrl;
				var comparedProducsAmount = document.getElementById('comparedProducsAmount').value;
				if(comparedProducsAmount > 1){
				  var s_pName = $("#omniturePageName").val();
				  var s_channel = $("#omnitureChannel").val();
				  
                    showPopup('comparateurPopup');
                    $("#comparateurPopup").load(comparisonContainerUrl, function(){

						addHadlerToCompPopup();
                        setNewCompPosition('comparateurPopup');
                        countCamparateurItems();
                      var buttonCartBig = $("#comparateurPopup").find(".buttonCartBig");
                      if (!$(buttonCartBig).length){
                        $(".addToCartComparisonContainer").remove();
                      }

				  var prodListString = "";
                        var prodStatustString = "";
                        var storeId = $("#omnitureFavoriteStoreId").val();
				  for (var i = 0; i < prodList.length; i++) {
				    if (prodListString.length != 0) {
				      prodListString = prodListString + ",";
                            prodStatustString = prodStatustString + ",";
				    }
				    prodListString = prodListString + prodList[i];
                          var status = "#indicator_"+prodList[i];
                          var prodStyle = $(status)[0].className;
                          if (prodStyle == "buttonStockLevelIndicatorGreen"){
                              prodStatustString = prodStatustString + "Available-"+storeId;
                          } else {
                              if (prodStyle == "buttonStockLevelIndicatorOrange"){
                                  prodStatustString = prodStatustString + "LimitedQty-"+storeId;
                              } else {
                                  prodStatustString = prodStatustString + "NotAvailable-"+storeId;
		      }
                          }
                        }
                        var logLink = $("#magasinId")[0].href;
                        if(logLink.match(/magasinId=\d+/) != null){
                            s_sendOmnitureInfo(s_pName+":Comparison",s_channel,"productComparison",prodListString, prodStatustString);
                        }
                      if ($(".deliverOrCCwr").length){
                        deliverOrCCwrEqualHeight();
                      }
					});
				} else if(comparedProducsAmount == 1) {
					alert("Vous devez selectionner au moins deux produits.");
				} else {
					alert("Aucun produit dans le comparateur.");
                }

		    });
		    
		    // clears comparison block
		    $('#btnClearComparator').click(function (){			
				var images = $("div#campExpanded a.campRemove");					
				jQuery.each(images, function(i, val) {
			    	var productId = val.id.substring(5);
					var placeholder = $(val).parent('div');
					var productIndex = placeholder.attr("id");
					doRemoveFromComparison(placeholder, productId, productIndex);
			    });			
		    });
		    
		  //When you click on a link with class of poplight and the href starts with a # 
		    $('a.poplight[href^=#]').click(function() {
          
          var popID = $(this).attr('rel'); //Get Popup Name
          var popURL = $(this).attr('href'); //Get Popup href to define size
          
          //Pull Query & Variables from href URL
          var query= popURL.split('?');
          var dim= query[1].split('&');
          var popWidth = dim[0].split('=')[1]; //Gets the first query string value
          
          //Fade in the Popup and add close button
          $('#' + popID).fadeIn(1).css({ 'width': Number( popWidth ) });//.prepend('<a href="#" class="close"><img src="../images/button-big.gif" class="btn_close" title="Close Window" alt="Close" /></a>');
          
          //Define margin for center alignment (vertical   horizontal) - we add 80px to the height/width to accomodate for the padding  and border width defined in the css
          var popMargTop = ($('#' + popID).height() + 80) / 2;
          var popMargLeft = ($('#' + popID).width() + 80) / 2;
          
          //Apply Margin to Popup
          $('#' + popID).css({
              'margin-top' : -popMargTop,
              'margin-left' : -popMargLeft
          }); 
          
          //Fade in Background
          $('body').append('<div id="fade"></div>'); //Add the fade layer to bottom of the body tag.
          $('#fade').css({'filter' : 'alpha(opacity=80)'}).fadeIn(1); //Fade in the fade layer - .css({'filter' : 'alpha(opacity=80)'}) is used to fix the IE Bug on fading transparencies 
          
          //$('#' + popID).show(); 
          
          bindClickByKeydown('a.buttonEmptyBlueBig', ESC_KEY_CODE);
          
          return false;
		    });

        //Close Popups and Fade Layer
        $('a.buttonEmptyBlueBig').click(function() { //When clicking on the close or fade layer...
            $('#fade').fadeOut(1);
            $('.popup_block').fadeOut(1); 
            return false;
        });
		    
		});
		
		function prepareDraggables() {
			if($.browser.msie && $.browser.version=="7.0") {
				$(".prdMarker").draggable({
					opacity: 0.7, 
					helper: 'clone',
					zIndex: 10000, 
					appendTo: 'body', 
					revert: 'invalid',
					scope: 'addToComp',
					cursorAt: { cursor: 'move', top: 46, left: 64 },
					refreshPositions: true,
					start: function (event, ui){
						$(".prdMarker .prodHighlight").hide();
						$(".prdMarker .priceSection").hide();
						$(".prdMarker .imgDesc").hide();
						$(".prdMarker .addToCartSection").hide();
					}			
				});
				
				$(".slPrdMarker").draggable({
					opacity: 0.7, 
					helper: 'clone',
					zIndex: 10000, 
					appendTo: 'body', 
					revert: 'invalid', 
					scope: 'addToComp',
					cursorAt: { cursor: 'move', top: 46, left: 64 },
					refreshPositions: true,
					start: function (event, ui){
						$(".slPrdMarker .prodHighlight").hide();
						$(".slPrdMarker .priceSection").hide();
						$(".slPrdMarker .imgDesc").hide();
						$(".slPrdMarker .addToCartSection").hide();
					}           
				});
			} else {
				$(".prdMarker").draggable({
					opacity: 0.7, 
					helper: 'clone',
					zIndex: 10000, 
					appendTo: 'body', 
					revert: 'invalid',
					containment: 'document',
					scope: 'addToComp',
					cursorAt: { cursor: 'move', top: 46, left: 64 },
					refreshPositions: true,
					start: function (event, ui){
						$(".prdMarker .prodHighlight").hide();
						$(".prdMarker .priceSection").hide();
						$(".prdMarker .imgDesc").hide();
						$(".prdMarker .addToCartSection").hide();
					}			
				});
				
				$(".slPrdMarker").draggable({
					opacity: 0.7, 
					helper: 'clone',
					zIndex: 10000, 
					appendTo: 'body', 
					revert: 'invalid', 
					containment: 'document',
					scope: 'addToComp',
					cursorAt: { cursor: 'move', top: 46, left: 64 },
					refreshPositions: true,
					start: function (event, ui){
						$(".slPrdMarker .prodHighlight").hide();
						$(".slPrdMarker .priceSection").hide();
						$(".slPrdMarker .imgDesc").hide();
						$(".slPrdMarker .addToCartSection").hide();
					}           
				});
			}
		}
		
		function addImage(holder, item) {	
			
			var skuId = item.attr("id");
			var productId = item.attr("productId");
			var altName = item.attr("alt");
			var skuCodeArticle = item.attr("skuCodeArticle");
			var discountValue = item.parents(".productItem").find(".discount").html();  /*or productBlock*/
			// item is dragged from carousel, it is not an image, but <div> with image inside
			if(! skuId){
				var skuId = item.find(".draggableImage").attr("id");
				var productId = item.find(".draggableImage").attr("productId");
				var altName = item.find(".draggableImage").attr("alt");
				var discountValue = item.find(".discount").html();
				var skuCodeArticle = item.find(".draggableImage").attr("skuCodeArticle");				
			}
			
			if (checkIndexOf(skuId, arrayOfProducts) == -1) {				
				arrayOfProducts[arrayOfProducts.length] = skuId;
				prodList[prodList.length] = skuCodeArticle;
			} else {
				return;
			}					
					
			holder.removeClass("ui-droppable");
			holder.removeClass("droppable");
			holder.droppable('option', 'accept', '');
			
			var imageSrc = item[0].src;
			if(! imageSrc){
				var imageSrc = item.find(".draggableImage").attr("src");
			}
						
			var newimage = $("<img src='" +  imageSrc + "' width='100' height='100'/>").click(function (ev) {$(this).siblings('a.campRemove').trigger('click', ev)});
			newimage.attr("title", altName);
			newimage.appendTo(holder);
			
			if(typeof(discountValue) !== 'undefined' && discountValue != null && discountValue != '') {
			  var discountFlag = $("<div class='rd_boxPriceStockTooltip'>" + discountValue + "</div>");
              discountFlag.appendTo(holder);
              var discountValue1 = discountValue.replace(/%/g,'');
			} else {
				var discountValue1 = '';
			}
			
			var removeLink = document.createElement("a");
			$(removeLink).attr("class", "campRemove").attr("title", "Supprimer").attr("href", "javascript:void(0)").attr("id", "comp_" + skuId).text("Supprimer").click(function (ev) {removeImage(ev)});
			holder.append(removeLink);
			var productIndex = holder.attr("id");
			
			$.ajax ({
				url: comparisonMngUrl,
				data: "action=add&skuId=" + skuId + "&productId=" + productId + "&productIndex=" + productIndex + "&skuDiscount=" + discountValue1,				
				cache: false
			});
			item.draggable({ opacity: 0.7, helper: 'clone',zIndex: 10000, appendTo: 'body', containment: 'document', revert: 'invalid'});
			increaseProductsAmount();
		}
		
		function addImageToShoppingList(holder, item) {	
			
			var skuId = item.attr("id");
			var productId = item.attr("productId");
			var altName = item.attr("alt");
			var imgSrcs = item.attr("srcList");
			var productList = item.attr("prodList");
			// item is dragged from carousel, it is not an image, but <div> with image inside
			if(! skuId){
				var skuId = item.find(".draggableImage").attr("id");
				var productId = item.find(".draggableImage").attr("productId");
				var altName = item.find(".draggableImage").attr("alt");
				var imgSrcs = item.find(".draggableImage").attr("srcList");
				var productList = item.find(".draggableImage").attr("prodList");
			}
			
			var imageSrc = item[0].src;
			if(! imageSrc){
				var imageSrc = item.find(".draggableImage").attr("src");
			}
			
	        $.ajax ({
	        	url: shoppingListMngUrl,
	        	data: "action=add&skuIds=" + skuId,
	        	cache: false,
	        	success: function(data, textStatus, jqXHR) {
	        		addRowToShoppingList(skuId, productId, altName, imageSrc, imgSrcs, productList, data);
	        	}
	        });
	        
	        resizeSlContainer();			
			scrollSlContainerBottom($slContainer);
			
			item.draggable({ opacity: 0.7, helper: 'clone',zIndex: 10000, appendTo: 'body', containment: 'document', revert: 'invalid'});
			
		}
		
		function addImageToShopListFromComp(skuId, productId, imageSrc) {
			if (checkIndexOf(skuId, shoppingListSkus) != -1) {
				return;
			}
			var name = $("#name_" + skuId)[0].value;
            $.ajax ({
                url: shoppingListMngUrl,
                data: "action=add&skuIds=" + skuId,
                cache: false,
                success: function(data, textStatus, jqXHR) {
                	addRowToShoppingList(skuId, productId, name, imageSrc, imageSrc, productId, data);
                }
            });
            
            resizeSlContainer();            
            scrollBottom($slContainer);
            
            increaseProductsInShoppinList(1);
		}
		
		function resizeSlContainer() {
			$slContainer = $("#slContainer");
			var windowHeight = $(window).height();
			var shoppingListHeight = $slContainer.height();
			
			if (windowHeight < shoppingListHeight + 350) {
				$slContainer.height(windowHeight - 350);
				$slContainer.css({"overflow": "auto"});				
			}
		}
		
		function scrollSlContainerBottom(toScroll) {
			var scrollHeight = toScroll[0].scrollHeight;
            toScroll.animate({scrollTop: scrollHeight});
		}
		
		function addRowToShoppingList(skuId, productId, altName, imageSrc, imgSrcs, productList, notAdded) {
			var skuArray = skuId.split(",");
			var namesArray = altName.split(",");		
			var srcArray = [];
			var productArray = [];
			
			if (skuArray.length > 1) {
			    srcArray = imgSrcs.split(",");
			    productArray = productList.split(",");
			} else {
			    srcArray[0] = imageSrc;
			    productArray[0] = productId;
			}
			
			//delete from skuArray elements which wasn't deleted due to error(s)
			if (notAdded.length > 0) {
				notAdded = notAdded.replace(/^\s*/, "").replace(/\s*$/, "");
				var notAddSkus = notAdded.split(",");
				for (var i=0;i<notAddSkus.length;i++) {
					var element = notAddSkus[i];
					for (var j=0;j<skuArray.length;j++) {
						if (skuArray[j] == element) {
							skuArray.splice(j,1);
							namesArray.splice(j,1);
							srcArray.splice(j,1);
							productArray.splice(j,1);
							break;
						}
					}
				}
			}
			
			for (i=0;i<skuArray.length;i++) {
				if (checkIndexOf(skuArray[i], shoppingListSkus) == -1) {
					var link = formShoppingListItemLink(productArray[i], skuArray[i]);
				    var itemRow = $("<tr class='sliderItem'><td class='slPicCell'><div class='slPictureBlock'><img class='clickable' src='" 
		                            +  srcArray[i] + "' width='44' height='44'/><input type='hidden' id='link' value='" 
		                            + link + "'/></td><td class='slDescrCell'><span class='clickable'>" + namesArray[i] + "</span></td></tr>");
		            itemRow.click(function() {
		                var imBlock = $(this);
		                var redirectLink = imBlock.find("#link")[0].value;
		                window.location = redirectLink;
		            });
		            
		            $shopList = $("#sliderList");
		            $shopList.append(itemRow);
		            
		            increaseProductsInShoppinList(1);
		        	shoppingListSkus[shoppingListSkus.length] = skuArray[i];
		        	
				} else {
					continue;
				}
			}
		}
		
		function formShoppingListItemLink(productId, skuId) {
			var contextPath = document.getElementById("contextPath").value;
			var linkManager = contextPath + "/shoppingList/productLinkManager.jsp"
			var link;
			$.ajax({
				url: linkManager,
                data: "productId=" + productId + "&skuId=" + skuId,
                cache: false,
                async: false,
                success: function(data, textStatus, jqXHR) {
                	link = data;
                }
			});
			return contextPath + link;
		}
		
		function removeImage(ev) {	
			var target = $(ev.target);
			var placeholder = target.parent('div');
			var skuId = target.attr("id").substring(5);
			var productIndex = placeholder.attr("id");	
			doRemoveFromComparison(placeholder, skuId, productIndex);						
		}	
		
		function removeFromComparison(holder, placeholder) {
			var productIndex = placeholder.attr("id");
			var picLink = placeholder.contents().filter("a");
			if(picLink.length != 0){
				var skuId = picLink.attr("id").substring(5);
				doRemoveFromComparison(placeholder, skuId, productIndex);
			}
		}
		
		function doRemoveFromComparison(placeholder, skuId, productIndex) {
			placeholder.empty();
			placeholder.droppable({
				accept: '.prdMarker',				
				drop: function(event, ui) {
					addImage($(this), ui.draggable);
				}
			});
			placeholder.droppable('option', 'accept', '.prdMarker');	
			placeholder.addClass("ui-droppable");
			placeholder.addClass("droppable");
			placeholder.removeClass("rendered");
			$.ajax ({
				url: comparisonMngUrl,
				data: "action=remove&productIndex=" + productIndex,				
				cache: false
			});
			
			if (checkIndexOf(skuId, arrayOfProducts) != -1) {
				arrayOfProducts.splice(checkIndexOf(skuId, arrayOfProducts), 1);
				prodList.splice(checkIndexOf(skuId, arrayOfProducts), 1);
			}
			
			decreaseProductsAmount();
		}
		
		function increaseProductsAmount() {
		    var comparedProducsAmount = document.getElementById('comparedProducsAmount');
		    var value = comparedProducsAmount.value;
		    comparedProducsAmount.value = 1 + parseInt(value);
		}
		function decreaseProductsAmount() {
		    var comparedProducsAmount = document.getElementById('comparedProducsAmount');
		    var value = comparedProducsAmount.value;
		    comparedProducsAmount.value = value - 1;
		}
		
		function increaseProductsInShoppinList(amount) {
			var activeButton = $("#actMaListeBtn");			
			activeButton.show();
			var inactiveButton = $("#inactMaListeBtn");			
			inactiveButton.hide();
			var emptyPicture = $("#emptyImage");
			emptyPicture.css({visibility: "hidden", position: "absolute"});
			
			var producsAmountInShoppingList = $("#producsAmountInShoppingList");
		    var value = producsAmountInShoppingList.text();
		    producsAmountInShoppingList.text(amount + parseInt(value));
		}
		
		function decreaseProductsInShoppinList() {
			var producsAmountInShoppingList = document.getElementById('producsAmountInShoppingList');
		    var value = producsAmountInShoppingList.value;
		    producsAmountInShoppingList.value = parseInt(value) - 1;
		}
		
		function checkIndexOf(searchString, arrayToSearchIn){
			var found = -1;
	
			for (var i = 0; i < arrayToSearchIn.length; i++) {
			  if (arrayToSearchIn[i] == searchString) {
			    found = i;
			    break;
			  }
			}
			return found;
		
		}		
		
		var productComparisonParams; 
		
		function addProductLink1() {
			
			var comparedProducsAmount = document.getElementById('comparedProducsAmount');
		    var value = comparedProducsAmount.value;
			if(value == 4){
				alert("Vous ne pouvez comparer que 4 produits maximum.");
				return;
			}
			
			params = productComparisonParams;
	
			var productId = params.productId;
			var imageSrc = params.skuImage;
			var skuId = params.skuId;
			var altName = params.altName;
			var discountValue = params.skuDiscountValue;
			var skuCodeArticle = params.skuCodeArticle;

			var holders = $(".rd_campProduct");
			var holder;
			jQuery.each(holders, function(i, val) {
				holder = $(val);
				if(holder[0].childNodes.length == 0){
					return false;
				}
		    });
			
			
			if (checkIndexOf(skuId, arrayOfProducts) == -1) {				
				arrayOfProducts[arrayOfProducts.length] = skuId;
				prodList[prodList.length] = skuCodeArticle;
			} else {
				return;
			}					
					
			holder.removeClass("ui-droppable");
			holder.removeClass("droppable");
			holder.droppable('option', 'accept', '');
			
			var removeLink = document.createElement("a");
			$(removeLink).attr("class", "campRemove").attr("title", "Supprimer").attr("href", "javascript:void(0)").attr("id", "comp_" + skuId).text("Supprimer").click(function (ev) {removeImage(ev)});
			holder.append(removeLink);
			
			if(typeof(discountValue) !== 'undefined' && discountValue != null && discountValue != '') {
			  var discountFlag = $("<div class='rd_boxPriceStockTooltip'>" + discountValue + "</div>");
              discountFlag.appendTo(holder);
              var discountValue1 = discountValue.replace(/%/g,'');
			} else {
				var discountValue1 = '';
			}
			
			var newimage = $("<img src='" +  imageSrc + "' width='100' height='100'/>");
			newimage.attr("title", altName);
			newimage.appendTo(holder);
			$(newimage).click(function(ev){
				$(this).siblings('a.campRemove').trigger('click', ev);
			});
			
			
			var productIndex = holder.attr("id");
			
			$.ajax ({
				url: comparisonMngUrl,
				data: "action=add&skuId=" + skuId + "&productId=" + productId + "&productIndex=" + productIndex + "&skuDiscount=" + discountValue1,				
				cache: false
			});
			increaseProductsAmount();
		}
		
		function addProductLink(parameters) {
			productComparisonParams = parameters;
			
			var campExpaned = document.getElementById('campExpanded');
			var campCollapsed = document.getElementById('rd_campCollapsed');
			if (campExpaned.style.display != "block") {						
				campExpaned.style.display = "block";
				campCollapsed.style.display = "none";
			}
			
			setTimeout("addProductLink1();",300);
		}
		
var conditionsAccepted = false;

function increaseAmount(elementName, isOnRemainingStock) {
  var obj = document.getElementById("quantityValue"+elementName);
  var currentVal = parseInt(obj.value);
  if (currentVal != NaN && currentVal != 0) {
    obj.value = (currentVal + 1);
    checkQuantity(elementName, isOnRemainingStock);
  }
}

function decreaseAmount(elementName, isOnRemainingStock) {
  var obj = document.getElementById("quantityValue"+elementName);
  var currentVal = parseInt(obj.value);
  if (currentVal != NaN && currentVal > 1) {
    obj.value = (currentVal - 1);
    checkQuantity(elementName, isOnRemainingStock);
  }
}

function calculateSurface(mValue, qty){
    var val = (parseFloat(mValue)*parseFloat(qty)).toFixed(2).replace(".", ",")
    $("#surfaceValue").val(val);
}

function updateQuantity(itemId, divId, PUPUV, isOnRemainingStock){
    var qty = $("#quantityValue"+itemId+divId).val();
    var surf = parseFloat($("#surfaceValue").val().replace(",", ".").replace("-","")).toFixed(2);
    if (surf == 'NaN'){
        surf = PUPUV;
    }
    $("#surfaceValue").val(surf);
    var pupuv = PUPUV;
    var newQuantity = Math.ceil((surf/pupuv).toFixed(2));
    if (isOnRemainingStock){
        var stockLevel = Number(document.getElementById("stockLevel"+itemId+divId).value);
        if(stockLevel != -1 && isOnRemainingStock != 'undefined' && isOnRemainingStock && newQuantity >= stockLevel){
            if((newQuantity > stockLevel) || (newQuantity == stockLevel)){
              $("#quantityMessage"+itemId+divId).css("color","black");
              $("a#iconPlus"+itemId+divId).addClass('disabledPlus');
            } else {
              $("#quantityMessage"+itemId+divId).css("color","black");
              $("a#iconPlus"+itemId+divId).removeClass('disabledPlus');
            }
            $("#quantityValue"+itemId+divId).val(stockLevel);
            $("#surfaceValue").val((stockLevel*pupuv).toFixed(2)); 
        } else {
            $("#quantityValue"+itemId+divId).val(newQuantity);
            $("a#iconPlus"+itemId+divId).removeClass('disabledPlus');
        }
        if(newQuantity == 1){
            $("a#iconMinus"+itemId+divId).addClass('disabledMinus');
        } else {
            $("a#iconMinus"+itemId+divId).removeClass('disabledMinus');
        }
    } else {
        $("#quantityValue"+itemId+divId).val(newQuantity);
        $("#surfaceValue").val(surf.replace(".", ",")); 
        checkQuantity(itemId+divId);
    }
    $("#surfaceValue").val($("#surfaceValue").val().replace(".", ",")); 
}

function pickupsurface (itemId, divId, PUPUV){
    var qty = $("#quantityValue"+itemId+divId).val();
    calculateSurface(PUPUV,qty);
}

function doCalculation (itemId, divId, PUPUV, quantity, needUpdateQTY){
        calculateSurface(PUPUV,quantity);
        $("#surfaceValue").keypress(function(e){
          if (e.keyCode==13) {
            updateQuantity(itemId, divId, PUPUV, needUpdateQTY);
            return false;
          } else {
            return true;
          }
        });
        $("#surfaceValue").blur(function() {
          updateQuantity(itemId, divId, PUPUV, needUpdateQTY);
          return false;
        });

    $("#iconMinus"+itemId+divId).click(function(){
        pickupsurface(itemId, divId, PUPUV);
    });
    $("#iconPlus"+itemId+divId).click(function(){
        pickupsurface(itemId, divId, PUPUV);
    });
    $("#quantityValue"+itemId+divId).change(function(){
        pickupsurface(itemId, divId, PUPUV);
    });
    $("#quantityValue"+itemId+divId).keyup(function(){
        pickupsurface(itemId, divId, PUPUV);
    });
}

function checkQuantity(elementName, isOnRemainingStock){
  var $quantityValue = $("#quantityValue"+elementName);
  quantity = Number($quantityValue.val());

  if(isNaN(quantity) || quantity <= 0){
      quantity = 1;
  } else if (quantity > 999){
      quantity = 999
  }
  
  var stockLevel = Number(document.getElementById("stockLevel"+elementName).value);
  if(stockLevel != -1 && isOnRemainingStock != 'undefined' && isOnRemainingStock) {
    var remainsInStock = $("span#remainsInStock"+elementName);
    if(quantity >= stockLevel){
      quantity = stockLevel;
      $("#quantityMessage"+elementName).css("color","black");
      $("a#iconPlus"+elementName).addClass('disabledPlus');
    } else {
      $("#quantityMessage"+elementName).css("color","black");
      $("a#iconPlus"+elementName).removeClass('disabledPlus');
    }
    
    remainsInStock.text(stockLevel);
  } else {
    $("div#quantityMessage"+elementName).hide();
  }
  
  if(quantity == 1){
    $("a#iconMinus"+elementName).addClass('disabledMinus');
  } else {
     $("a#iconMinus"+elementName).removeClass('disabledMinus');
  }
  
  if (quantity != $quantityValue.val()) {
      $quantityValue.val(quantity);
  }
  
  delivery(elementName, $quantityValue, isOnRemainingStock);
}


var quantityInput;
var fQtyItemId;
var fQtyNeedUpdateQTY;
var currentQtyVal;

function changeAmountMS(currentAmountBtn, increase) {
  var fQtyNewValue;
  quantityInput = $(currentAmountBtn).parent().find(".quantityInput");
  fQtyItemId = $(currentAmountBtn).parent().find("#fQtyItemId").val();
  fQtyNeedUpdateQTY = $(currentAmountBtn).parent().find("#fQtyNeedUpdateQTY").val() == "true";
  currentQtyVal = parseInt($(quantityInput).val());
  if (!increase && currentQtyVal != NaN && currentQtyVal > 1){
    fQtyNewValue =  currentQtyVal - 1;
  }
  if (increase && currentQtyVal != NaN && currentQtyVal != 0){
    fQtyNewValue = currentQtyVal + 1;
  }
  $(quantityInput).val(fQtyNewValue);
  $(currentAmountBtn).focus();
  checkQuantity(fQtyItemId, fQtyNeedUpdateQTY);
}

$(document).ready(function(){
  $(".iconMinus").click(function() {
    changeAmountMS($(this), false);
  });
  $(".iconPlus").click(function() {
    changeAmountMS($(this), true);
  });

  $('.iconChangeAmount, .quantityInput').bind('keydown', function(e) {
    if ( e.keyCode === 13 ) {
      e.preventDefault();
      var recalculerBtn = $(this).parents(".boxCartInnerHowMuch").find(".recalculerBtn");
      if ($(recalculerBtn).length){
        $(recalculerBtn).click();
      }
    }
  });

  $(".recalculerBtn").click(function() {
    recalculateQuantity();
  });

});


function delivery(elementName, obj, isOnRemainingStock){
  var value_quantity = obj.val();
  var deliverySkuId = document.getElementById("deliverySkuId" + elementName).value;
  var url = '/store/castCatalog/includes/productDelivery.jsp?quantity='+value_quantity+'&skuId='+deliverySkuId;
  
  var xmlhttp = null;
  if (window.XMLHttpRequest) {
    xmlhttp = new XMLHttpRequest();
    if ( typeof xmlhttp.overrideMimeType != 'undefined') { 
      xmlhttp.overrideMimeType('text/xml'); 
    }
  } else if(window.ActiveXObject) {
    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
  } else {
    alert('Perhaps your browser does not support xmlhttprequests?');
  }
  
  xmlhttp.open('GET', url, true);
   
  xmlhttp.onreadystatechange = function() {
    if (xmlhttp.readyState == 4) {
      //replace whitespaces at the beginning and at the end of string.
      var html = xmlhttp.responseText;
      var reg = /^[ \t\r\n]+|[ \t\r\n]+$/g;
      var $deliveryTime = $(".deliveryProd"+elementName);
      
      if (!isOnRemainingStock || $deliveryTime.hasClass('hidden') || $deliveryTime.html() === "") {
          html=html.replace(reg,"");
          
          $deliveryTime.each(function(){
            $(this).html(html).removeClass("hidden");
          });
      }

      if ($(".deliverOrCCwr").length){
        deliverOrCCwrEqualHeight();
      }
    }
  };
  xmlhttp.send(null);
}

function multiDelivery(productDeliverySkusIds) {
  if (productDeliverySkusIds.length > 0) {
  var url = '/store/castCatalog/includes/productDelivery.jsp?skusIds='+productDeliverySkusIds;
  
  var xmlhttp = null;
  if (window.XMLHttpRequest) {
    xmlhttp = new XMLHttpRequest();
    if ( typeof xmlhttp.overrideMimeType != 'undefined') { 
      xmlhttp.overrideMimeType('text/xml'); 
    }
  } else if(window.ActiveXObject) {
    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
  } else {
    alert('Perhaps your browser does not support xmlhttprequests?');
  }
  
  xmlhttp.open('GET', url, true);
   
  xmlhttp.onreadystatechange = function() {
    if (xmlhttp.readyState == 4) {
      var text = xmlhttp.responseText;

      //replace whitespaces at the beginning and at the end of string.
      var reg = /^[ \t\r\n]+|[ \t\r\n]+$/g;
      text=text.replace(reg,"");
    
      var textArray = text.split(',');
      for(var i = 0; i < textArray.length; i++) {
        textArray[i] = textArray[i].replace(/^\s*/, "").replace(/\s*$/, "");
        if (textArray[i].length > 0) {
          var item = textArray[i].split(':');
          item[0] = item[0].replace(/^\s*/, "").replace(/\s*$/, "");
          item[1] = item[1].replace(/^\s*/, "").replace(/\s*$/, "");
          if (item[0].length > 0 && item[1].length > 0) {
            $(".deliveryProd"+item[0]).each(function(){
              $(this).html(item[1]).removeClass("hidden");
            });

            if ($(".deliverOrCCwr").length){
              deliverOrCCwrEqualHeight();
            }
          }
        }
      }
    }
  };
  xmlhttp.send(null);
  }
}

/**
 * 
 * @param cartType {addToCartArea, addToCartTabs, addToCartSmall, addToCartList}
 * @param productId
 * @param divId
 */
function ajaxCartArea(cartType, productId, divId) {
  $.ajax ({
      url: '/store/castCatalog/includes/ajaxAddToCart.jsp',
      data: "cartType="+cartType+"&productId="+productId+"&divId="+divId,
      cache: true,
      async: false,
      beforeSend: function(xht) {
      },
      success: function(data, textStatus, jqXHR) {
  
          if (textStatus == 'success') {
            $("#"+divId).html(data);
          }
      },
      complete: function(jqXHR, textStatus) {
  
          if (textStatus == 'success') {
            try {
             ajaxCheckQuantity();
            } catch (e) {}
          }
      }
  });
}

var subVar = false;
function recalculateQuantity(){
  subVar = true;
  $("#recalculateCartButton").click();

}

function initFields(){
  var acceptCarteLAtout = document.getElementById("acceptCarteLAtoutCheckBox");
  var acceptSalesConditions = document.getElementById("acceptSalesConditionsCheckBox");
  
  if(acceptCarteLAtout) {
    if(acceptCarteLAtout.value == "true"){
      document.getElementById("summchk").checked = true;
    }
  }
  if(acceptSalesConditions) {
    if(document.getElementById("acceptSalesConditionsCheckBox").value == "true"){
      document.getElementById("summAgree").checked = true;
    }
  } 
  var anonymousCoupon = document.getElementById("anonymousProfileUserResource");
  if(anonymousCoupon){
    $("#identifiant").show();
  } else {
    var idn = document.getElementById("identifiant");
    if(idn){
      $("#identifiant").hide();
    }
  }
    
  var showLoginSection = document.getElementById("showLoginSection");
  if(showLoginSection) {
    $("#identifiant").show();
  }

}


function setCarteCheckBox(element) {
  document.getElementById("acceptCarteLAtoutCheckBox").value = element.checked;
  document.getElementById("lAtoutChangedOnPage").value = true;
  subVar = true;  
  $("#recalculateCartButton").click();
}

function  setAgreeCheckBox(element) {
  document.getElementById("acceptSalesConditionsCheckBox").value = element.checked;
  $("#unacceptedConditions").hide();
}


function submitform() {
  if(document.getElementById("summAgree").checked){
    subVar = true;
    document.getElementById("moveToPurchaseInfoByRelIdButton").click();
  } else {
    showPopup("unacceptedConditions");
    bindClickByKeydown('#unacceptedConditions button', ESC_KEY_CODE);
  }
}

function recalcEnterPres(e) {
  if (e.keyCode==13) {  
    subVar = true;
    $("#recalculateCartButton").click();
  }
}

function quantityEnterPres(e) //disableEnterKey
{
     if (e.keyCode==13) {
      return false;
     } else {
      return true;
     }
}




var currentlySelected;

function showHideOrderContent(ticketId) {
  
  var imageSrc = document.getElementById(ticketId + '_image').src;
  var state = imageSrc.match(/icoExpand.gif$/);

  if (currentlySelected != null && currentlySelected != '' && currentlySelected != ticketId) {
    collapseCurrentlySelected();
  }
  
  if(state != null){
    $('#' + ticketId + '_image').attr('src','/images/icoCollapse.gif');
    $('tr[name='+ ticketId + '_top]').css("display","");
    $('tr[name='+ ticketId + '_body]').css("display","");
    $('tr[name='+ ticketId + '_bottom]').css("display","");
    $('tr[name='+ ticketId + '_productRow]').css("display","");
    $('div[name='+ ticketId + '_image]').css("display",""); // IE issue
    currentlySelected = ticketId;
  } else {
    $('#' + ticketId + '_image').attr('src','/images/icoExpand.gif');
    $('tr[name='+ ticketId + '_top]').css('display','none');
    $('tr[name='+ ticketId + '_body]').css('display','none');
    $('tr[name='+ ticketId + '_bottom]').css('display','none');
    $('tr[name='+ ticketId + '_productRow]').css('display','none');
    $('div[name='+ ticketId + '_image]').css('display','none'); // IE issue
  }
  
  // Mantis 0001920: [Purchase History] Display trouble when unfold older ticket after a long one  
  // Problem -  If a ticket contains a lot of product, we need to scroll down
  // but if we click on a new ticket to unfold it, we stay at the same vertical position (in the footer of the page).
  var verticalScroll = window.pageYOffset;

  // window.pageXOffset is undefined in IE
  if (typeof(window.pageYOffset) == 'undefined') {
    verticalScroll = document.documentElement.scrollTop;
  }
  if (verticalScroll > 0) {
    $(window).scrollTop(45 * $('#' + ticketId + '_index').attr("value"));
  }   
  
}

function collapseCurrentlySelected() {
  $('#' + currentlySelected + '_image').attr('src','/images/icoExpand.gif');
  $('tr[name='+ currentlySelected + '_top]').css('display','none');
  $('tr[name='+ currentlySelected + '_body]').css('display','none');
  $('tr[name='+ currentlySelected + '_bottom]').css('display','none');
  $('tr[name='+ currentlySelected + '_productRow]').css('display','none');
  $('div[name='+ currentlySelected + '_image]').css('display','none'); // IE issue
  currentlySelected= '';
}

function printTicketPDF(pTicketId) {
  if (pTicketId == currentlySelected) {
    var imageSrc = document.getElementById(pTicketId + '_image').src;
    var state = imageSrc.match(/icoExpand.gif$/);
    if(state != null){
      showHideOrderContent(pTicketId);
    }
  } else {
    showHideOrderContent(pTicketId);
  }
  window.setTimeout('_asyncSendOmnitureOrderReceipt()', 0);
}

function _asyncSendOmnitureOrderReceipt(){
    try {
        s_sendOmnitureInfo('','','orderreceipt:print');
    } catch (e) {}
    
    return true;
}

var ajaxStockLevelRequest = createXMLHttpRequest();

function checkStockLevelOnCartArea(contextPath, categoryId, prodId, featuredProd, skuId, featuredSkuId, isDocumentProduct, templateUrl){
  ajaxStockLevelRequest = createXMLHttpRequest();
  if (ajaxStockLevelRequest.readyState == 0 || ajaxStockLevelRequest.readyState == 4 ) {
    var url = contextPath + "/castCatalog/includes/checkStockLevel.jsp?productId=" + prodId + "&featuredProduct=" + featuredProd
    + "&categoryId=" + categoryId + "&featuredSkuId=" + featuredSkuId + "&skuId=" + skuId + "&documentProduct=" + isDocumentProduct
    + "&templateUrl="+encodeURIComponent(templateUrl) ;
    
    ajaxStockLevelRequest.open("GET", url , true);
    ajaxStockLevelRequest.onreadystatechange = function() {
      if(ajaxStockLevelRequest.readyState == 4 && ajaxStockLevelRequest.status == 200) {
        try {
          document.getElementById("checkStockLevel" + skuId).innerHTML=ajaxStockLevelRequest.responseText;
        } catch (e) {
          // IE fails unless we wrap the string in another element.
          var wrappingElement = document.createElement('div');
          wrappingElement.innerHTML = ajaxStockLevelRequest.responseText;
          document.getElementById("checkStockLevel" + skuId).appendChild(wrappingElement);
        }
      }
      $(".featuredProduct .fPrix").addClass("wCheckStock");
    }
    ajaxStockLevelRequest.send(null);
  }
}

function setPostalCode(prodId, skuId, favoriteMagazinId, contextPath) {
  if (document.getElementById("chosenFavStoreAsyncHolder")){
	favoriteMagazinId = document.getElementById("chosenFavStoreAsyncHolder").value;
  }
	
  if (ajaxStockLevelRequest.readyState == 0 || ajaxStockLevelRequest.readyState == 4 ) {

  var postalCode = document.getElementById("postalCodeSV").value;
  
  if(postalCode == null || postalCode == "" ) {
    var vla = document.getElementById("setPostalCodeErrorSV");
    document.getElementById("setPostalCodeErrorSV").style.visibility="visible";
    return;
  }
  
  if (!postalCode.match(/^[0-9]+$/)){
      var delimIndex = postalCode.indexOf(', ');
      if (isNaN(postalCode.substring(0, delimIndex))) {
        postalCode = postalCode.substring(delimIndex + 1);
      } else {
        postalCode = postalCode.substring(0, delimIndex);
      }
  }

  if (postalCode == null || postalCode == "" || isNaN(postalCode) || Number(postalCode) < 0) {
    document.getElementById("setPostalCodeErrorSV").style.visibility="visible";
    //document.getElementById("postalCodeSV").value = "";
    return;
  }

  document.getElementById("setPostalCodeErrorSV").style.visibility="hidden";

  document.getElementById("queryConditionHiddenInput").value = "byPostalCode";
  
  document.getElementById("postalCodeHiddenInput").value = postalCode;

  //var cardMode = document.getElementById('cardMode').innerHTML;
  //var urlLegalNotice = document.getElementById('urlLegalNotice').innerHTML;
  var url = contextPath + "/svTest/svDropletPage.jsp?prodId=" + prodId + "&postalCode=" + postalCode + "&queryCondition=byPostalCode" + "&skuId=" + skuId;

  ajaxStockLevelRequest.open("GET", url , true);
  ajaxStockLevelRequest.onreadystatechange = function() {
    if(ajaxStockLevelRequest.readyState == 4 && ajaxStockLevelRequest.status == 200) {
    var responseContent = ajaxStockLevelRequest.responseText;
    // indicates that provided postal code was not found
    if (responseContent.search("noteArea") == -1) {
      document.getElementById("setPostalCodeErrorSV").style.visibility="visible";
      //document.getElementById("postalCodeSV").value = "";
      return;
    }

    document.getElementById("stockVisualizationRefreshableContent").innerHTML=ajaxStockLevelRequest.responseText;
    
    //document.getElementById("stockVisPopup").style.display = "block";
    showPopup("stockVisPopup");
    bindClickByKeydown('#stockVizCloseButton', ESC_KEY_CODE);
    sendOmnitureInfoAfterAjax();
    }
  }

  ajaxStockLevelRequest.send(null);
  }
}

function applySVQuantity(prodId,skuId,contextPath) {  
  if (ajaxStockLevelRequest.readyState == 0 || ajaxStockLevelRequest.readyState == 4 ) {

  var queryCondition = document.getElementById("queryConditionHiddenInput").value;
  var quantity = document.getElementById("quantityValueblabla").value;

  var url = contextPath + "/svTest/svDropletPage.jsp?prodId=" + prodId + "&quantity="+ quantity + "&skuId=" + skuId;
  if (queryCondition == "byFavoriteStore") {
    url = url + "&queryCondition=byFavoriteStore";
  } else if (queryCondition == "byDefinedStore") {
    var magasinId = document.getElementById("magasinIdHiddenInput").value;
    url = url + "&queryCondition=byDefinedStore" + "&magasinId=" + magasinId;
  } else if (queryCondition == "byPostalCode") {
    var postalCode = document.getElementById("postalCodeHiddenInput").value;
    url = url + "&queryCondition=byPostalCode" + "&postalCode=" + postalCode;
  }

  ajaxStockLevelRequest.open("GET", url , true);
  ajaxStockLevelRequest.onreadystatechange = function() {
    if(ajaxStockLevelRequest.readyState == 4 && ajaxStockLevelRequest.status == 200) {
      document.getElementById("stockVisualizationRefreshableContent").innerHTML=ajaxStockLevelRequest.responseText;
//      var logLink = $("#magasinId")[0].href;
//      if(logLink.match(/magasinId=\d+/) != null){
        sendOmnitureInfoAfterAjax();
//      } 
      } 
    }

  ajaxStockLevelRequest.send(null);
  }
}

function increaseSVQuantity(elementName) {
  var obj = document.getElementById("quantityValue"+elementName);
  var currentVal = parseInt(obj.value);
  if (currentVal != NaN && currentVal != 0) {
  obj.value = (currentVal + 1);
  checkSVQuantity(elementName);
  }   
}

function decreaseSVQuantity(elementName) {
  var obj = document.getElementById("quantityValue"+elementName);
  var currentVal = parseInt(obj.value);
  if (currentVal != NaN && currentVal > 1) {
  obj.value = (currentVal - 1);
  checkSVQuantity(elementName);
  }
}

function checkSVQuantity(elementName) {
  var obj = $("#quantityValue"+elementName);
  var a = obj.val();

  if(isNaN(a)){
    obj.val(1);
    a = 1;
  }

  quantity=Number(a);
  if(quantity <= 0){
    obj.val(1);
    quantity=1;
  }

  obj.val(quantity);
  
  if(obj.val() == 1){
    $("a#iconMinus"+elementName).addClass('disabledMinus');
  } else {
     $("a#iconMinus"+elementName).removeClass('disabledMinus');
  }
}

function pcEnterPress(e, prodId, skuId, favoriteMagazinId, contextPath) {
  if (e.keyCode==13) {
    setPostalCode(prodId, skuId, favoriteMagazinId, contextPath);
    return false;
  }
  return true;
}


function sendOmnitureInfoAfterAjax(){
      var p1 = $("#refreshableTabCodearticle")[0].value;
      var p2 = $("#refreshableTabStatuses")[0].value;
    try {
      s_sendOmnitureInfo("","","stockCheck:productView:VisuTab",p1, p2);
    } catch (e){
//        alert("sendOmnitureInfo() return exception: " + e.message);
    }
  }
  
 function showSearchPopup(id) {
	$("#"+id).show();
	resizeOverlay();
	setNewCompPosition(id);
}

function hideSearchPopup(id) {
	$("#"+id).hide();
	resizeOverlay();
	$("#search-question").onblur="";
}

function msieversion()
  {
     var ua = window.navigator.userAgent
     var msie = ua.indexOf ( "MSIE " )

	 if ( msie > 0 )      // If Internet Explorer, return version number
         return parseInt (ua.substring (msie+5, ua.indexOf (".", msie )))
      else                 // If another browser, return 0
         return 0

   }

/*MS height for products on Gallery View*/
function galleryViewProductsRowHeight(currentProductRow){
  var maxProdDecriptionHeight = 0;
  var prodDecriptions = $(currentProductRow).find(".prodDecription");

  /*if there is one and more soit after cast card then align them*/
  var thereIsCastCardWithSoit = false;
  var castCardWithSoit = $(currentProductRow).find(".castCardWithSoit");
  if ($(castCardWithSoit).find(".additionalPrice").length){
    thereIsCastCardWithSoit = true;
  }

  $(prodDecriptions).each(function(){
    var castCardWithSoit = $(this).find(".castCardWithSoit");
    if (thereIsCastCardWithSoit){
      $(castCardWithSoit).height(75);/*default height if there is soit*/
    }
    var prodDecriptionHeight = $(this).height();
    if (prodDecriptionHeight > maxProdDecriptionHeight){
      maxProdDecriptionHeight = prodDecriptionHeight;
    }
  });

  $(prodDecriptions).each(function(){
    $(this).height(maxProdDecriptionHeight);
    $(this).find(".buttonCartContainer").addClass("galleryView");
    var priceContent = $(this).find(".priceContent");
    var prodDecriptionName = $(this).find(".prodDecriptionName");
    $(priceContent).each(function(){
        var priceContentElement = $(this);
        if (!priceContentElement.hasClass('multiSkuMessage')){
            priceContentElement.height(maxProdDecriptionHeight - $(prodDecriptionName).height());
        }
    });
    var castCardWithSoit = $(this).find(".castCardWithSoit");
    $(castCardWithSoit).addClass("bottomed");
  });
}

function deliverOrCCwrEqualHeight(){
  var deliverMessageDefaultHeight = 28;
  var deliverMessageMaxHeight = deliverMessageDefaultHeight;
  var pickupMessageDefaultHeight = 14;
  var pickupMessageMaxHeight = pickupMessageDefaultHeight;
  /* Check if there is deliverMessage width height more then default */
  $(".deliverMessage").each(function(index){
    var currentDeliverMessageHeight = $(this).height();
    var currentPickupMessageHeight = $(this).next().height();

    if (currentDeliverMessageHeight > deliverMessageMaxHeight){
      deliverMessageMaxHeight = currentDeliverMessageHeight;
    }

    if (currentPickupMessageHeight > pickupMessageMaxHeight){
      pickupMessageMaxHeight = currentPickupMessageHeight;
    }

  });
  if (deliverMessageMaxHeight > deliverMessageDefaultHeight){
    $(".deliverMessage").each(function(){
      $(this).css("height", deliverMessageMaxHeight);
    });
  }

  if (pickupMessageMaxHeight > pickupMessageDefaultHeight){
    $(".pickupMessage").each(function(){
      $(this).css("height", pickupMessageMaxHeight);
    });
  }
}

function deliverOrCCwrTooltip(deliverOrCCwr){
  var distance = 30;
  var time = 250;
  var hideDelay = 50;
  var hideDelayTimer = null;
  var beingShown = false;
  var shown = false;
  var toolTipHover = $(deliverOrCCwr).find('.toolTipHover');
  var deliverToolTipWr = $(deliverOrCCwr).find('.deliverToolTipWr');
  var tooltipCombined = $(toolTipHover).add($(deliverToolTipWr));

  $(tooltipCombined).mouseover(function () {
    if (hideDelayTimer) clearTimeout(hideDelayTimer);
    if (beingShown || shown) {
      return;
    } else {
      beingShown = true;

      $(deliverToolTipWr).css({
        top: 20,
        display: 'block'
      }).animate({
          top: '-=' + distance + 'px',
          opacity: 1
        }, time, 'swing', function() {
          beingShown = false;
          shown = true;
        });
    }
    return false;
  }).mouseout(function () {
    if (hideDelayTimer) clearTimeout(hideDelayTimer);
    hideDelayTimer = setTimeout(function () {
      hideDelayTimer = null;
      $(deliverToolTipWr).animate({
        top: '-=' + distance + 'px',
        opacity: 0
      }, time, 'swing', function () {
        shown = false;
        $(deliverToolTipWr).css('display', 'none');
      });
    }, hideDelay);
    return false;
  });
}

$(document).ready(function() {
  $('.shoppingCartV2 .deliverOrCCwr li').each(function () {
    deliverOrCCwrTooltip($(this));
  });
  var gallery = $(".sorter").find(".viewSwitch .btnGalSwither span.light").size();
  var list = $(".sorter").find(".viewSwitch .btnListeSwither span.light").size();
  if (gallery > 0){
    $(".productsRow").each(function(){
      galleryViewProductsRowHeight($(this));
    });
  }
  if (list > 0) {
    $(".productListe .priceContent .cast_card").addClass("tablePriceCastCard"); 
    $(".buttonCartContainer").css("margin", "0px");
  }
  /*displaying fQty correctly for different pages*/
  $(".fPrix").find(".fQty:first").addClass('firstfQty');


  $("a.consForumBackToTop").click(function () { 
        elementClick = $(this).attr("href");
        destination = $(elementClick).offset().top;
        if($.browser.safari){
          $('body').animate( { scrollTop: destination }, 1100 );
        }else{
          $('html').animate( { scrollTop: destination }, 1100 );
        }
        return false;
   });

  
  $(".consForumCloseButton").click(function () { $(".consForumPopUpWr").css("display","none");  });
  
});

function showConseilsForumsPopup() {
	$(".consForumPopUpWr").css("display","block");
}



//function for view video from popup
function showVideoFromPopup(_id, _param){
	$("#printButtonPopup").hide();
	$("#productLargeImage").hide();
	$("#" + _id).show();	
	$("#" + _id).load(_param+"&uid="+Math.random() * 100, null, function() {
	$("#videoPopupFormContainer").css('padding','0px 0px');
		$("#videoPopupHeader").hide();
		$("#videoPopupPrice").hide();
	   $(".gray_overlay").show();
	   setNewCompPosition(_id);
	   resizeOverlay();
	})
}
// function for view 3d from popup
function show3DFromPopup(_id, _param){
	$("#printButtonPopup").hide();
	$("#productLargeImage").hide();
	$("#" + _id).show();	
	$("#" + _id).load(_param+"&uid="+Math.random() * 100, null, function() {
	$("#videoPopupFormContainer").css('padding','0px 0px');
		$("#3DPopupHeader").hide();
	   $(".gray_overlay").show();
	   setNewCompPosition(_id);
	   resizeOverlay();
	});	
}
// StockVis New Look Collapsed and Uncollapsed state  
$(document).ready(function(){
	$(".rbBlockV2:nth-child(2)").addClass("secondBlock");
});

// C&F width fix
$(document).ready(function(){
	$(".rbBlockV2:nth-child(2)").addClass("secondBlock");
});

$(document).ready(function(){
	$(".rd_buttonCompareAddGray").click(function(){
		$("#rd_campCollapsed").hide();	
});
	/*display menu in IE6*/
	if($.browser.msie && $.browser.version=="6.0"){
	  $(".mainNavPanel UL LI.mainMenuPoint").hover(
        function () {
          $(this).addClass("hover");
        }, 
        function () {
          $(this).removeClass("hover");
        }
      );
    }	  
});

$(document).ready(function () {
	  $(".shoppingCartV2:contains('Qua')").addClass('yesToQuanity');
	  $(".fPrix:contains('indisponible')").addClass('priceBlockUno');
	});

$(window).load(function(){
    initCCStoresDropDown('.chooseShopSelectorWr');
    
    // fix bug on createAccount.jsp (myProfile.jsp) when user set CastProfileFormHandler.currentLocalStore (CastNewsletterFormHandler.prefStore), 
    // click "valider" and get some errors. In this case value of CastNewsletterFormHandler.prefStore is erased.
    var initStoreId;
    var initStoreText;
    var initIscc;
    $('.favStorePair').each(function(){
        if ($(this).find('select').val() != "") {
            var dropdownId = $(this).find('select').attr('id');
            initStoreId = $(this).find('select').val();
            initStoreText = $(this).find('.selectbox').val().trim();
            initIscc = $(this).find('li#'+dropdownId+'_input_'+initStoreId).hasClass('iscc');
        };
    });
    
    $('.favStorePair').each(function(){
        $(this).find('select').val(initStoreId);
        $(this).find('.selectbox').val(initStoreText);
        if (initIscc) $(this).find('img').css('display', 'block');
    });
});

function initCCStoresDropDown(selector){
    $(selector).each(function(){
        var dropdown = $(this);
        var dropdownId = dropdown.find('select').attr('id');
        
        // display cc icon when cc store choosed after page load
        var selectedStoreText = $.trim($(dropdown).find('.selectbox').val());
        if ($(dropdown).find('option:contains('+selectedStoreText+')').hasClass('iscc')){
            $(dropdown).find('img').css('display', 'block');
        }
        
        // don't display "Votre choix..." in options list in all stores selector
        $(dropdown).find('li:first').remove();
        
        // add class "iscc" to click and collect stores in dropdown
        $(dropdown).find('option[class="iscc"]').each(function() {
            var storeId = $(this).val();
            $(this).parent().parent().find('li#'+dropdownId+'_input_' + storeId).addClass('iscc');
        });
        
        $(dropdown).find('li').click(function() {
            // display cc icon when cc store has chosen
            if ($(this).hasClass('iscc'))
                $(dropdown).find('img').css('display', 'block');
            else
                $(dropdown).find('img').css('display', 'none');
            
            // change mirror field
            if($(this).parent().parent().parent().hasClass('favStorePair')){
                var newStoreId = $(this).attr('id').replace(''+dropdownId+'_input_','');
                var newStoreText = $(this).html().trim();
                var newStoreIscc = $(this).hasClass('iscc');
                
                $('.favStorePair li').each(function(){
                    if ($(this).attr('id')!=''+dropdownId+'_input_'+ newStoreId)
                        $(this).removeClass('selected');
                    else 
                        $(this).addClass('selected');
                });
                $('.favStorePair').each(function(){
                    $(this).find('.selectbox').val(newStoreText);
                    $(this).find('select').val(newStoreId);
                    
                    if (newStoreIscc)
                        $(this).find('img').css('display', 'block');
                    else
                        $(this).find('img').css('display', 'none');
                });
            };
        });
    });
};
var isMobileBrowser = /Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent);

$(document).ready(function(){
	if(MAP_ENABLED){
		if(google){
        	google.load("maps", "3", {other_params:'sensor=false', key: '${castConfig.googleGeolocationAPIKey}', callback: function(response){
            	geo.load();
        	}});
		} else {
			MAP_ENABLED = false;
		}
    } else {
    	geo.load();
    }
    _makeCheckoutButtonsClickable();
    _hideShoppingCartPromoTable();
    $('body').click(function(event){
    	if(geo.displayGeoAutoPopup){
    		hideGeolocationAutoPopup();
    		geo.displayGeoAutoPopup = false;
    	}
    });
    // fix for tablets: need for generating 'mouseover' event
    //var isMobileBrowser = /Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent);
    if(isMobileBrowser){
    	$('body').addClass('clickable');
    }
});

function appendShopListTableToElement(elemSelector, storeList) {
	$(elemSelector).empty();
    var table = $('<table></table>').addClass('content');
    var size = storeList.length - 1;
    var hiddenId = $(geo.currentPopupId + ' form input[type="hidden"]')[0].value;
    $.each(storeList, function (index, store) {
        var storeUrl = (store.storeUrl) ? store.storeUrl : '#';
        var blank = (store.storeUrl) ? "_blank" : '';
        var $row = $('<tr></tr>').addClass('shopListItem');
        $row.append($('<td></td>').addClass('itemName').html(store.postalCode + ' - ' + store.storeName + '<br/><a href="' + storeUrl + '" target="' + blank + '">' + leadformanceLink + '</a>'));
        var isCCCell = $('<td></td>').addClass('itemIsCC');
        if (store.isCC) {
            isCCCell.append('<img src="/images/isCCbig.png">');
        }
        $row.append(isCCCell);
        var distanceCell = $('<td></td>').addClass('itemDistance');
        if(store.distance && store.distance != "0") {
        	if(!(index == 0 && hiddenId && hiddenId == store.postalCode)){
        		distanceCell.text(store.distance + ' km');
        	}
    	}
    	$row.append(distanceCell);
    	if(geo.getCookie() && geo.getCookie() == store.id){
    		$row.append($('<td></td>').addClass('itemLink').html('Votre magasin'));
    	} else {
    		$row.append($('<td></td>').addClass('itemLink').html($('<a></a>').click(function(){geo.changeContextFromPopup(store.id);}).attr('href', '#').text(chooseStoreMessage)));
    	}
        table.append($row);
        if (index != size) {
            table.append('<tr class="delimiter"/>');
        }
    });
    $(elemSelector).append(table);
}

function appendShopListDivsToElement(elemSelector, storeList){
	$(elemSelector).empty();
	var delimiter;
    var container;
	var size = storeList.length - 1;
    if(MAP_ENABLED){
        delimiter = '<div class="delimiter"></div>';
        container = $('<div></div>').addClass('content');
        $.each(storeList, function(index, store){
            var $row = $('<div></div>').addClass('shopListItem');
            if(store.isCC){
            	$row.addClass('isCC');
            }
            var $link = $('<a></a>').text(store.postalCode + " - " + store.storeName).click(function(){
            		if(closeButtonEnabled){
            			try {
            				s_sendOmnitureInfo("","","location:close",store.id);
            			} catch(e){}
            		}
            		geo.changeContextFromPopup(store.id);
            	});
            $row.append($link);
            container.append($row);
            if(index != size) {
                container.append(delimiter);
            }
        });
    } else {
        delimiter = '<tr class="delimiter"/>';
        container = $('<table></table>').addClass('content');
        $.each(storeList, function(index, store){
            var storeUrl = (store.storeUrl) ? store.storeUrl : '#';
            var blank = (store.storeUrl) ? "_blank" : '';
            var $row = $('<tr></tr>').addClass('shopListItem');
            var $link = $('<td></td>').addClass('itemName').html(store.postalCode + ' - ' + store.storeName + '<br/><a href="' + storeUrl + '" target="' + blank + '">' + leadformanceLink + '</a>');
            $row.append($link);
            var isCCCell = $('<td></td>').addClass('itemIsCC');
            if (store.isCC) {
                isCCCell.append('<img src="/images/isCCbig.png">');
            }
            $row.append(isCCCell);
            var $rightCell = $('<td></td>').addClass('itemLink').html($('<a></a>').attr('href', '#').text(chooseStoreMessage)).click(function(){
            	if(closeButtonEnabled){
        			try {
        				s_sendOmnitureInfo("","","location:close",store.id);
        			} catch(e){}
        		}
            	geo.changeContextFromPopup(store.id);
            	});
            $row.append($rightCell);
            container.append($row);
            if(index != size) {
                container.append(delimiter);
            }
        });
        container.append(delimiter);
    }
	$(elemSelector).append(container);
}
var numberPattern = /^\d+$/;

//if string matches pattern - return postal code
function prepareSearchString(inputValue){
	var postalCode = "";
	var results = inputValue.match(numberPattern);
	geo.isPostalCodeSearchError = false;
	if(results){
		if(results[0].length < 3){
			postalCode = "";
			geo.isPostalCodeSearchError = true;
		} else {
			postalCode = results[0];
		}
	} else if ("" != inputValue) {
		postalCode = inputValue.replace(/^\s+|\s+$/gm,'');
		postalCode = postalCode.replace(/ /g,"+");
	}
	return postalCode;
}

var isPopupShowed = false;

var geo = {
    stores: "",
    currentPopupId: "",
    //storesForSearch : [],
    userLocation: {
        latitude: "",
        longitude: ""
    },
    geocoder: "",
    map: "",
    markersMap: "",
    isPostalCodeSearchError: false,
    isMapLoadedCorrectly: false,
    displayGeoAutoPopup: false,
    triggerSearch : false,
    shouldHidePopup: false,
    isUserLocatedPopupShowed: false,
    load: function() {
        if (display.bonnesAffairesPopup){
            sendOmnitureInfoSafely("","","contextChange:promo");
            showPopup('bonnesAffairesPopup');
            bindClickByKeydown('#bonnesAffairesPopup .cancelButton button', ESC_KEY_CODE, true);
            geo.initHeaderEventHandlers();
        } else if (display.eraseBasketPopup){
            showPopup('eraseBasketPopup');
            bindClickByKeydown('#eraseBasketPopup .cancelButton button', ESC_KEY_CODE, true);
            geo.initHeaderEventHandlers();
        } else {
            var cookieStoreId = geo.getCookie();
            var isLeadFormance = getURLParams()[LEADFORMANCE_STORE_URL_PARAM];
            if ((!cookieStoreId || cookieStoreId == "999") && !isLeadFormance && !$.cookie(DISPLAY_GEO_POPUP) && !$.cookie('s_spmc')){ // user go to site first time and not from leadformance
                // html5 geolocation, if error cause google geolocation
                if (navigator && navigator.geolocation) {
                    navigator.geolocation.getCurrentPosition(geo.getLocationSuccess, geo.getLocationError, {maximumAge:600000, timeout:3000});
                } else {
                    geo.getLocationWithGoogle();
                }
            } else {
                geo.displayGeoAutoPopup = $.cookie(TMP_COOKIE_NAME) == "true";
                if (geo.displayGeoAutoPopup) {
                    $.cookie(TMP_COOKIE_NAME, false, { path: COOKIE_PATH });
                    viewGeolocationAutoPopup(DISPLAY_DURATION * 1000);
                }
                geo.updateCookieValue(cookieStoreId);
                	geo.initHeaderEventHandlers();
                }
            }
	},
	initHeaderEventHandlers: function(){
	        if(!isMobileBrowser){
	        	$('#shopContextInfo, #userLocatedPopup, #userLocatedPopupNoMap, #additionalLineLeftCol').mouseout(function(event){
		        	hideStore(event);
		    	});
				$('#shopContextInfo, #userLocatedPopup, #userLocatedPopupNoMap, #additionalLineLeftCol').mouseover(function(event){
		        	showStore(event);
		        });
	        } else {
		        $('#shopContextInfo, #userLocatedPopup, #userLocatedPopupNoMap, #additionalLineLeftCol').mouseout(function(event){
		        	if(!event.relatedTarget.className == 'ac_even' && !event.relatedTarget.className == 'ac_odd' && !event.relatedTarget.className == 'ac_results'){
		        		hideStore(event);
		        	}
		    	});
				$('#userLocatedPopup, #userLocatedPopupNoMap, #additionalLineLeftCol').mouseover(function(event){
		        	showStore(event);
		        });
				$('#shopContextInfo').live('mouseover click', function(event){
					if(geo.isUserLocatedPopupShowed){
						hideStore(event);
						geo.isUserLocatedPopupShowed = false;
					} else {
						showStore(event);
						geo.isUserLocatedPopupShowed = true;
					}
		        });
	        }
		
        /*$('#shopContextInfo').mouseover(function(event){
        	showStore(event);
        	geo.initArrayForAutoCompletion();
        	geo.triggerAutocomplete();
        });*/
	},
	initAutocompletionHandlers: function(){
        $('body .ac_results').live('mouseover', function(event){
        	showStore(event);
        });
        $('body .ac_results').live('mouseout', function(event){
        	hideStore(event);
        });
	},
	setNearestShop: function(storeList){
		geo.stores = storeList;
		$.cookie(TMP_COOKIE_NAME, true, { path: COOKIE_PATH });
		geo.changeContext(storeList[0].id);
	},
    initUserLocatedPopup: function (storeList) {
        geo.stores = storeList;
        geo.currentPopupId = MAP_ENABLED ? '#userLocatedPopup' : '#userLocatedPopupNoMap';
        appendShopListTableToElement(geo.currentPopupId + ' .customScrollBox', storeList);
        $(geo.currentPopupId + ' .customScrollBox').jScrollPane({
			autoReinitialise: true,
			verticalDragMinHeight: 100,
			verticalDragMaxHeight: 100,
			showArrows : true
			});
        if(MAP_ENABLED) {
            geo.initGoogleMap(storeList, 'userLocatedMap');
            $(geo.currentPopupId + ' .search-input').keyup(geo.restoreBoundsOnClearInput);
        }
        geo.initArrayForAutoCompletion();
        geo.initAutocompletionHandlers();
    },
	initUserNotLocatedPopup: function(storeList){
		geo.stores = storeList;
        var elementToDisplay = MAP_ENABLED ? 'userNotLocatedPopup' : 'userNotLocatedNoMap';
		// it's important to display Popup earlier than shopList filling, else - do not work scrolling
        try {
			s_sendOmnitureInfo("","","location:select");
		} catch (e){
			console.log(e);
		}
		showPopup(elementToDisplay);
		geo.currentPopupId = '#' + elementToDisplay;
		geo.initPlaceHolder();
		appendShopListDivsToElement(geo.currentPopupId + ' .customScrollBox', storeList);
		$(geo.currentPopupId + ' .customScrollBox').jScrollPane({
			autoReinitialise: true,
			verticalDragMinHeight: 100,
			verticalDragMaxHeight: 100,
			showArrows : true
			});
        if(MAP_ENABLED) {
            geo.initGoogleMap(storeList, 'userNotLocatedMap');
            $(geo.currentPopupId + ' .search-input').keyup(geo.restoreBoundsOnClearInput);
        }
        geo.initArrayForAutoCompletion();
	},
	initPlaceHolder: function(){
		var searchInput = $(geo.currentPopupId + ' .search-input');
		var placeHolder = $(searchInput).attr('customph'); 
		$(searchInput).addClass('font-italic').val(placeHolder);
		$(searchInput).siblings('input[type=submit]').attr('disabled', 'disabled');
		$(geo.currentPopupId + ' .search-input').bind('click',function(){
			$(this).removeClass('font-italic').unbind('click');
			$(this).val('');
			$(searchInput).siblings('input[type=submit]').removeAttr('disabled');
		});
	},
	restoreBoundsOnClearInput: function(){
		var currentValue = $(this).val();
		if(currentValue.length == 0 && boundsWereChanged){
			geo.resetBounds(geo.stores);
			boundsWereChanged = false;
		}
	},
	initArrayForAutoCompletion: function(){
		/*$.each(geo.stores, function(index, store){
			var tempStore = {'label' : store.storeName + ', ' + store.postalCode, 'value' : store.id};
			geo.storesForSearch.push(tempStore);
		});*/
	    var width = $(geo.currentPopupId + ' .search-input').outerWidth(true);
		//$(geo.currentPopupId + ' .search-input').bind('keydown.autocomplete', function(){
			$(geo.currentPopupId + ' .search-input').autocomplete("/store/castCommon/includes/CityOrPostalAutocomplete.jsp",{
				minChars: 3,
	            width: width,
	            cacheLength: 1,
	            matchContains: true,
	            autoFill: false,
	            scrollHeight: 300,
	            selectFirst: false,
	            formatResult: function(row) {
                    return row = row[0].replace("&nbsp;", " ").replace("&amp;","&").replace("&#39;", "'");
	            },
	            formatItem: function(row) {
	                return row = row[0].replace("&nbsp;", " ");
	            }
			}).trigger('autocomplete');
		//}).trigger('keydown.autocomplete');
	},
	unbindAutoCompletion: function(){
		$(geo.currentPopupId + ' .search-input').trigger('unautocomplete');
	},
	triggerAutocomplete: function(){
		var inputValue = $(geo.currentPopupId + ' .search-input').val();
		if(inputValue.length > 2){
			$(geo.currentPopupId + ' .search-input').trigger('click');
		}
	},
	initGoogleMap: function(storeList, mapElementId){
		var image = '/store/images/singleStoreIcon.png';
		var image_cluster = '/store/images/multiStoreIcon.png';
		var bounds = new google.maps.LatLngBounds ();
		var map;
        //marker clusterer
        var mc;
		//marker clusterer options
        var mcOptions = {
			gridSize: 40,
			maxZoom: 17,
			averageCenter: true,
			styles: [{
				height: 53,
				url: image_cluster,
				width: 48,
				textSize: 15,
				textColor: "white",
				anchorIcon: [53, 24],
				anchorText: [0, -4]
			}
		]};

		var options = {
			mapTypeId: google.maps.MapTypeId.ROADMAP,
			mapTypeControl: false,
			streetViewControl: false,
			rotateControl: false,
			scaleControl: false,
			panControl: false
		};
		map = new google.maps.Map(document.getElementById(mapElementId), options); 
        var infowindow = new google.maps.InfoWindow({maxWidth: 200});

		//marker cluster

		mc = new MarkerClusterer(map, [], mcOptions);
		mc.setCalculator(function(marker, numStyles) {
						return {
								text: "x " + marker.length,//I can write here string and show on cluster icon but i can't put my data here dynamically...
								index: 0
							};
						});
						
		//init current user location
        //bounds.extend (new google.maps.LatLng(geo.userLocation.latitude,geo.userLocation.longitude));
		geo.markersMap = new Object();
        for (var i = 0; i < storeList.length; i++) {
            if (storeList[i].latitude && storeList[i].longitude) {
                var latlng = new google.maps.LatLng(storeList[i].latitude, storeList[i].longitude);
                bounds.extend(latlng);
                var storeName = storeList[i].storeName;
                if(storeName.length > 30){
                	storeName = storeName.substring(0, 30);
                }
                var tooltipText = '<div style="overflow:hidden;white-space:nowrap;display:block; font-family: Arial, Helvetica, sans-serif;font-size:12px;"><strong><span style="white-space:normal">CASTORAMA ' + storeName + '</span></strong>'
                    + '<br/>' //+ storeList[i].street + '<br/>' + storeList[i].postalCode + ' ' + storeList[i].storeName
                    + '<br/><a style="text-decoration:underline;" href="' + storeList[i].storeUrl + '" target="_blank">' + leadformanceLink + '</a><br/>'
                    + '<a style="text-decoration:underline;" onclick="geo.changeContextFromPopup({0})">{1}</a>'.format(storeList[i].id, chooseStoreMessage) + '</div>';
                var marker = geo.createMarker(latlng, tooltipText, map, image, infowindow, storeList[i].id);
                geo.markersMap[storeList[i].id] =  marker;
                mc.addMarker(marker);
            }
        }
        map.fitBounds(bounds);
        var listener = google.maps.event.addListenerOnce(map, 'idle', function(){
        	google.maps.event.removeListener(listener);
            geo.isMapLoadedCorrectly = true;
            if(geo.shouldHidePopup){
            	geo.shouldHidePopup = false;
            	hideStore();
            }
        });
        geo.map = map;
	},
	resetBounds: function(stores){
		var bounds = new google.maps.LatLngBounds ();
		var stepsCount = stores.length;
    	for (var i = 0; i < stepsCount; i++) {
            if (stores[i].latitude && stores[i].longitude) {
                var latlng = new google.maps.LatLng(stores[i].latitude, stores[i].longitude);
                bounds.extend(latlng);
            }
        }
    	geo.map.fitBounds(bounds);
	},
	createMarker: function(latlng, text, map, image, infowindow, storeId) {
		var marker = new google.maps.Marker({
			position: latlng,
			map: map,
			icon: image,
			anchorPoint: new google.maps.Point(0, -20)
		});
		
		google.maps.event.addListener(marker, 'click', function() {
			infowindow.close();
			infowindow.setContent(text);
			infowindow.setPosition(marker.position);
			infowindow.open(map, marker);			
		});
		return marker;
    },
    searchStore: function(elem){
    	geo.isMapLoadedCorrectly = false;
        var element = $(elem);
        var searchInput = $(elem).find('.search-input');
        var strToSearch = $(searchInput).val();
        var postalCode = prepareSearchString(strToSearch);
    	var hiddenField = element.find('form input[type="hidden"]')[0];
    	var hiddenId = hiddenField.value;
        if (postalCode) {
        	if(!hiddenId || postalCode != hiddenId){
        		$(hiddenField).val(postalCode);
        		var params = {postalCode : postalCode};
        		$('.ac_results').hide();
        		isErrorMessageDisplayed = false;
        		//do not trigger autocomplete after search string not from autocompletion list
        		geo.callGeolocationService(params, geo.redrawListAndMap);        		
        	} else if(hiddenId && postalCode == hiddenId && isErrorMessageDisplayed){
        		geo.showAutocompletionError();
        	}
        } else {
        	$(hiddenField).val("");
            geo.showAutocompletionError();
        }
    	return false;
    },
    showAutocompletionError: function(){
    	var searchInput = $(geo.currentPopupId).find('.search-input');
    	var searchErrorMessage = $(geo.currentPopupId).find('.searchErrorMessage');
    	if(geo.isPostalCodeSearchError){
    		$(searchErrorMessage).find('.emptySearchMessage').text(searchForPCodeError);
    	} else {
    		$(searchErrorMessage).find('.emptySearchMessage').text(searchForCityError);
    	}    	
        searchErrorMessage.fadeIn(400);
    	$('.ac_results').hide();
        searchInput.keydown(function() {
            searchErrorMessage.fadeOut(400);
        });
        isErrorMessageDisplayed = true;
        geo.isMapLoadedCorrectly = true;
        if(geo.shouldHidePopup){
        	geo.shouldHidePopup = false;
        	hideStore();
        }
    },
    searchStoreByPostalCode: function(postalCode){
    	if(!geo.geocoder){
			geo.geocoder = new google.maps.Geocoder();
		}
		var arguments = {
				address : postalCode + ' france',
				sensor : 'false'
		};
		geo.geocoder.geocode(arguments, function(results, status){
			if(status == google.maps.GeocoderStatus.OK){
				params = {
					latitude : results[0].geometry.location.lat,
					longitude : results[0].geometry.location.lng,
				};
				geo.callGeolocationService(params, geo.redrawListAndMap);
			} else {
				geo.showAutocompletionError();
			}
		});
    },
    redrawListAndMap: function(data){
    	//redraw store list
    	if(data.stores){
    		var api = $(geo.currentPopupId + ' .customScrollBox').data('jsp');
	    	if(geo.currentPopupId.indexOf('Not') == -1){
	    		appendShopListTableToElement(geo.currentPopupId + ' .customScrollBox .jspPane', data.stores);
	    	} else {
	    		appendShopListDivsToElement(geo.currentPopupId + ' .customScrollBox .jspPane', data.stores);
	    	}
	    	api.scrollTo(0, 0);
    	
	    	//redraw the map
	    	if(MAP_ENABLED){
	        	var storesForMap = data.stores.slice(0, 2);
	    		geo.resetBounds(storesForMap);
	        	// trigger 'click' event to open infowindow
	        	//var markerToClick = geo.markersMap[data.stores[0].id];
	        	google.maps.event.trigger(geo.markersMap[data.stores[0].id], "click");
	    	}
    		$(geo.currentPopupId + ' .search-input').blur().focus();
        	google.maps.event.addListenerOnce(geo.map, 'idle', function(){
                geo.isMapLoadedCorrectly = true;
                if(geo.shouldHidePopup){
                	geo.shouldHidePopup = false;
                	hideStore();
                }
            });
    		//$('.ac_results').hide();
    		boundsWereChanged = true;
    	} else {
    		geo.showAutocompletionError();
    	}
    	return false;
    },
    //not add elements with the same id to result list 
    prepareSearchResult: function(array1, array2){
    	var resultArray = new Array();
    	var storeIds = new Array();
    	$.each(array1, function(index, store){
    		storeIds.push(store.id);
    		resultArray.push(store);
    	});
    	$.each(array2, function(index, store){
    		if($.inArray(store.id, resultArray)){
    			storeIds.push(store.id);
    			resultArray.push(store);
    		}
    	});
    	return resultArray;
    },
    getCookie: function() {
        return $.cookie(COOKIE_NAME);
    },
    updateCookieValue: function(storeId){
        if (storeId && storeId!=WEB_CONTEXT_STORE_ID) 
            $.cookie(COOKIE_NAME, storeId, { expires: COOKIE_AGE, path: COOKIE_PATH });
        else 
            $.cookie(COOKIE_NAME, WEB_CONTEXT_STORE_ID, { path: COOKIE_PATH });
    },
    trackableChangeContext: function(storeId, castStoreId){
        var isNearest = (castStoreId == nearestCastStoreId);
        if (isNearest)
            sendOmnitureInfoSafely("","","location:select:default",castStoreId);
        else
            sendOmnitureInfoSafely("","","location:select:other",castStoreId);
        geo.changeContext(storeId);
    },
    forceChangeContext: function(storeId, castStoreId){
        if (castStoreId){
            sendOmnitureInfoSafely("","","location:select:other",castStoreId);
        }
        document.getElementsByName('/com/castorama/ClearBasketFormHandler.useContext')[0].value=storeId;
        document.eraseBasketForm.submit();
    },
    baChangeContext: function(productId){
        if (productId) {
            sendOmnitureInfoSafely("","","contextChange:promo:ok");
            var addToCartBigButton = $('input[value="'+productId+'"]').parent().find('input[type="submit"].buttonCartBig');
            var addToCartSmallButton = $('input[value="'+productId+'"]').parent().find('input[type="submit"].buttonCart');
            setTimeout(function(){
                if (addToCartBigButton.length > 0)
                    addToCartBigButton.click();
                else if (addToCartSmallButton.length > 0)
                    addToCartSmallButton.click();
                else
                    document.bonnesAffairesForm.submit();
            }, OMNITURE_SYNCH_DELAY);
        } else
            document.bonnesAffairesForm.submit();
    },
    changeContext: function(storeId){
        if (storeId) geo.updateCookieValue(storeId);
        setTimeout('window.location.reload()', OMNITURE_SYNCH_DELAY);
    },
    changeContextFromPopup: function(storeId){
    	if(storeId){
    		geo.updateCookieValue(storeId);
    	}
    	if($.cookie('s_spmc')){
    		$.cookie('s_spmc', storeId);
    	}
    	setTimeout('window.location.reload()', OMNITURE_SYNCH_DELAY);
    },
    getLocationSuccess: function(position) {
        geo.userLocation = {
            "latitude": position.coords.latitude, 
            "longitude": position.coords.longitude
        };
        geo.callGeolocationService(geo.userLocation, geo.getLocationSuccessCallback);
    },
    getLocationSuccessCallback: function(responseData){
        if (responseData != null) {
            stores = responseData.stores;
            isFrance = responseData.isFrance;
            if (stores && stores.length > 0) {
                if (isFrance) {
                    geo.setNearestShop(stores);
                } else {
                    var cookieStoreId = geo.getCookie();
                    if (!cookieStoreId || cookieStoreId == "999") {
                        geo.initUserNotLocatedPopup(stores);
                    } else {
                        geo.initHeaderEventHandlers();                    	
                    }
                    geo.updateCookieValue(cookieStoreId);
                }
            }
        }
    },
    getLocationError: function() {
        if(GOOGLE_API_ENABLED) {
            geo.getLocationWithGoogle();
        } else {
            geo.callGeolocationService("", geo.getLocationWithGoogleCallback);
        }
    },
    getLocationWithGoogle: function () {
        var userLocation;
        try {
            userLocation = google.loader.ClientLocation;
            if (userLocation == null)
                userLocation = {"latitude": 0, "longitude": 0};
        } catch (e) {
            userLocation = null;
            /*{"latitude":0, "longitude":0}*/
        }
        if (userLocation == null) {
            geo.callGeolocationService("", geo.getLocationWithGoogleCallback);
        } else {
            geo.callGeolocationService(userLocation, geo.getLocationWithGoogleCallback);
        }
    },
    getLocationWithGoogleCallback: function (responseData) {
        if (responseData != null) {
            stores = responseData.stores;
            isFrance = responseData.isFrance;
            if (stores && stores.length > 0) {
                if (isFrance) {
                    geo.setNearestShop(stores);
                } else {
                    var cookieStoreId = geo.getCookie();
                    if (!cookieStoreId || cookieStoreId == "999") {
                        geo.initUserNotLocatedPopup(stores);
                    } else {
                        geo.initHeaderEventHandlers();
                    }
                    geo.updateCookieValue(cookieStoreId);
                }
            }
        }
    },
    callGeolocationService: function(params, callback){
        $.ajax({
            cache : false,
            url : "/store/magasin/storeInfoList.jsp",
            type : "GET",
            data : params,
            dataType : "text",
            error : function(XMLHttpRequest, textStatus, errorThrown) {
                callback(null);
            },
            success : function(data) {
                callback(JSON.parse(data));
            }
        });
        
    }
};

function sendOmnitureInfoSafely(pName, channel, pState, val1, val2){
    try{
        s_sendOmnitureInfo(pName, channel, pState, val1, val2);
    } catch(e){
    }
}

function getURLParams(){
    var url = window.location.href;
    // url contains your data.
    var qs = url.indexOf("?");
    if(qs==-1) return {};
    var fr = url.indexOf("#");
    var q="";
    q = (fr==-1)? url.substr(qs+1) : url.substr(qs+1, fr-qs-1);
    var parts=q.split("&");
    var vars={};
    for(var i=0;i<parts.length; i++){
        var p = parts[i].split("=");
        if(p[1]){
            vars[decodeURIComponent(p[0])] = decodeURIComponent(p[1]);
        }else{
            vars[decodeURIComponent(p[0])] = "";
        }
    }
    // vars contain all the variables in an array.
    return vars;
};

// Fix bug "Buttons are not clickable - only labels"
function _makeCheckoutButtonsClickable(){
    var reassignClicks = function(source, destination){
        $(source).each(function(){
            $(this).find(destination).click(function(e){
                e.stopPropagation();
            });
            $(this).click(function(){
                $(this).find(destination).click();
            });
        });
    };

    reassignClicks('span.ccBlueButton','.ccBlueButton');
    reassignClicks('span.ccBascetButton','.ccBascetButton');
    reassignClicks('span.ccBascetButtonForm','.ccBascetButtonForm');
};

// Fix bug "Some popups are not closed via ESC button"
// Fix bug "Chosen store not applied via Enter button"
var ESC_KEY_CODE = 27;
var ENTER_KEY_CODE = 13;
function bindClickByKeydown(selector, keyCode, stopProcessing){
    $('body').keydown(function(e){
        if (e.which === keyCode) {
            $(selector).click();
        }
        
        // Fix bug in FF: if close popup by esc and then reload page -> page isn't reloaded
        if (stopProcessing){
            e.preventDefault();//return false;
        }
    });
};

// Fix bug "Shopping Cart: Single Basket: Table header is thicker than on mokup"
// Fix bug "Shopping Cart: Missing border line in case of two promotions"
function _hideShoppingCartPromoTable(){
    var $promoTable = $('.boxCartGiftWr');
    var promoTableHasFilled = false;
    $promoTable.find('td:not(:empty)').each(function(){
        if ($.trim($(this).html()) != ""){
            promoTableHasFilled = true;
        }
    })
    if (!promoTableHasFilled) {
        $promoTable.css('display','none');
    };
}

var castoramaCardNumberRegex = /^913[0-9]{16}$/;

function validateCastoramaCardNumber(number) {

    if (castoramaCardNumberRegex.test(number)) {
        if (validateCheckDigit(number)) {
            hideRedCross();
            showGreenCheck();
            document.getElementById('castoramaCardNumberServerValue').value = true;
        } else {
            hideGreenCheck();
            showRedCross();
            document.getElementById('castoramaCardNumberServerValue').value = false;
        }
    } else {
        hideGreenCheck();
        showRedCross();
        document.getElementById('castoramaCardNumberServerValue').value = false;
    }

    if (number.length == 0) {
        document.getElementById('castoramaCardNumberServerValue').value = true;
    }
}

function validateCastoramaCardNumberWithoutInfo() {
    var castoramaCard = $('#castoramaCardNumber').val();

    if (castoramaCardNumberRegex.test(castoramaCard)) {
        if (validateCheckDigit(castoramaCard)) {
            document.getElementById('castoramaCardNumberServerValue').value = true;
        } else {
            document.getElementById('castoramaCardNumberServerValue').value = false;
        }
    } else {
        document.getElementById('castoramaCardNumberServerValue').value = false;
    }

    if (castoramaCard.length == 0) {
        document.getElementById('castoramaCardNumberServerValue').value = true;
    }
}

function submitCastoramaCard() {
    var castoramaCardForm = $('form[name="castoramaCardForm"]');
    var deliveryForm = $('form[name="deliveryForm"]');
    if (typeof castoramaCardForm != 'undefined' && typeof deliveryForm != 'undefined') {
        var castoramaCardValue = deliveryForm.find("#castoramaCardNumber").val();
        validateCastoramaCardNumber(castoramaCardValue);
        var isCastoramaCardValid = deliveryForm.find("#castoramaCardNumberServerValue").val();
        castoramaCardForm.find("#castoramaCardNumber").val(castoramaCardValue);
        castoramaCardForm.find("#isCastoramaCardNumberCorrect").val(isCastoramaCardValid);
        $.ajax({
            type: "GET",
            url: "",
            data: $("#castoramaCardForm").serialize(),
            success: function (html, textStatus, jqXHR) {
                /* Code here is just for debugging reasons */
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                /* Code here is just for debugging reasons */
            }
        });
    }
}

function showRedCross(){
	document.getElementById('redCross').style.display = "inline";
	document.getElementById('redCross').style.visibility = "visible";
}

function showGreenCheck(){
	document.getElementById('greenCheck').style.display = "inline";
	document.getElementById('greenCheck').style.visibility = "visible";
}

function hideRedCross(){
	document.getElementById('redCross').style.display = "none";
	document.getElementById('redCross').style.visibility = "hidden";
}

function hideGreenCheck(){
	document.getElementById('greenCheck').style.display = "none";
	document.getElementById('greenCheck').style.visibility = "hidden";
}
	
function validateCheckDigit(number){
	
	var checkDigit=0;
	var cDigit, nDigit;
	var even=false;
		
	for (var n = number.length - 1; n >= 0; n--) {
			
	cDigit = number.charAt(n),
	nDigit = parseInt(cDigit, 10);
		
	if (even) {
		nDigit=nDigit*2;
		if (nDigit > 9) 
			nDigit = nDigit - 9;
	}
	checkDigit = checkDigit + nDigit;
	even = !even;
	}
	return (checkDigit % 10) == 0;
}

String.prototype.format = function() {
    var formatted = this;
    for (var i = 0; i < arguments.length; i++) {
        var regexp = new RegExp('\\{'+i+'\\}', 'gi');
        formatted = formatted.replace(regexp, arguments[i]);
    }
    return formatted;
};

function changeContextFromDropDownList(container){
    var storeId = container.find(".selectedStore .input")[0].getAttribute("id");
    if(storeId){
    	geo.changeContext(storeId);
    }
    return null;
}


function getTotalWidthOfObject(object, addMarginAndPadding) {

    if (object == null || object.length == 0) {
        return 0;
    }

    var value = object.width();
    value += parseInt(object.css("borderLeftWidth"), 10) + parseInt(object.css("borderRightWidth"), 10); //Total Border Width
    if (addMarginAndPadding) {
        value += parseInt(object.css("padding-left"), 10) + parseInt(object.css("padding-right"), 10); //Total Padding Width
        value += parseInt(object.css("margin-left"), 10) + parseInt(object.css("margin-right"), 10); //Total Margin Width
    }
    return value;
}

function getTotalHeightOfObject(object, addMarginAndPadding) {

    if (object == null || object.length == 0) {
        return 0;
    }

    var value = object.height();
    value += parseInt(object.css("borderTopWidth"), 10) + parseInt(object.css("borderBottomWidth"), 10); //Total Border Width
    if (addMarginAndPadding) {
        value += parseInt(object.css("padding-top"), 10) + parseInt(object.css("padding-bottom"), 10); //Total Padding Width
        value += parseInt(object.css("margin-top"), 10) + parseInt(object.css("margin-bottom"), 10); //Total Margin Width
    }
    return value;
}