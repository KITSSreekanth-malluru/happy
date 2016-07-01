jQuery.fn.extend({
	carousel: function (options) {
		return this.each(function() {
			new jQuery.carousel(this, options);
		});
	}
});

jQuery.carousel = function (_obj, _options) {
	var element_width = 0;
	var elements_count = 0;
	var total_width = 0;
	var toScroll = $(_obj).find(_options.inside).eq(0)
	var list_ul = toScroll.children("UL");
	var toScrollCount = 0;
	var wait = false;	
	var stopMove = false;
	var speed = 550;
	var timeout = false;
	var direction = 1;
	var relSpeed = 0;
	var realShow = 0;
	var defContain;
	var maxShow = 0;
	var curSelCarPos = $('.cImBorder').parents('LI').prevAll().size() + 1;
	
	
  init();

	function init () {
		var isMobileBrowser = /Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent);
        if (isMobileBrowser) {
            _options.crollOnMouseOver = false;
        }
		var list_elements = list_ul.children("li");
                element_width = list_elements.eq(0).outerWidth(true);
                elements_count = list_elements.length;
                total_width = elements_count * element_width;
		list_ul.width(total_width);
		toScroll.width(10 * element_width);
                toScrollCount = Math.ceil(toScroll.innerWidth() / element_width + _options.preLoad);
                //toScroll.scrollLeft(0);
                
                toScroll.scrollLeft(curSelCarPos * element_width - Math.round(toScroll.innerWidth() / 2));
                toScrollCount = Math.round(toScroll.scrollLeft() / element_width);
         
    defContain = Math.ceil(toScroll.innerWidth() / element_width);
                
        realShow =  defContain + toScrollCount;
        
    
		 
		preloadImages();		
		initButtons();
		if (!isMobileBrowser) {
		initPreviews();
		}
	}

	function initButtons () {			
		if (_options.crollOnMouseOver) {			
			$(_obj).find(_options.prev).mouseover( function () {
				speed = _options.speedOver;				
				_obj.mouseOverMove(false);
			} ).mouseout( function () {
				_obj.stopMoving();
			} ).mousedown( function () {
				speed = _options.speedOverClick;
				_obj.coutRelSpeed();								
			}).mouseup( function () {
				speed = _options.speedOver;
				_obj.coutRelSpeed();				
			});
			
	        $(_obj).find(_options.nxt).mouseover( function () {
	        	speed = _options.speedOver;
				_obj.mouseOverMove(true);
			} ).mouseout( function () {
				_obj.stopMoving();
			}).mousedown( function () {
				speed = _options.speedOverClick;
				_obj.coutRelSpeed();
			}).mouseup( function () {
				speed = _options.speedOver;
				_obj.coutRelSpeed();
			});
			
		} else {
			speed = _options.speed;			
	        $(_obj).find(_options.prev).click( function () {
				_obj.moveImage(false, false);
				event.stopPropagation();
			} );
	        $(_obj).find(_options.nxt).click( function () {
				_obj.moveImage(true, false);
				event.stopPropagation();
			} );			
		}		
	}

	function initPreviews () {
		var timeoutIn, timeoutOut;
		for (i = 0; i < elements_count; i++) {
				
		        list_ul.children("li").eq(i).children("a").eq(0).mouseover(function () {
				var popup = $(_obj).find(".carouselPopup");
				
				$("div .inside").addClass("prdMarker slPrdMarker");
				
				if($(this).parent("li").find(".lImage").find("div#undraggableProduct").length != 0){
				    $(".prdMarker").removeClass("prdMarker");
				} else if ($(this).parent("li").find(".lImage").find("div#undraggableMultiSKUProduct").length != 0) {
					$(".slPrdMarker").draggable('destroy');
				} else {
					prepareDraggables();
				}
				
				popup.find(".inside").html($(this).parent("li").find(".lImage").html());
				popup.css("left", $(this).parents(".carThumbs").position().left + 7 + $(this).parent("li").position().left + $(this).parent("li").outerWidth(true) / 2 - popup.outerWidth(true) / 2)
					.css("top", $(this).parents(".carThumbs").position().top + $(this).parent("li").position().top - 25);
				popup.show();
				popup.css("visibility", "visible");
			});
			$(_obj).find(".carouselPopup").mouseover(function () {
				timeoutIn = true;
			});
            $(_obj).mouseout(function () {
				timeoutIn = false;
				timeoutOut = window.setTimeout(function() {
					if (!timeoutIn)	{				
						clearTimeout(timeoutOut);
						var popup = $(_obj).find(".carouselPopup");				
						popup.hide();
					}
				}, 100);
			});                        
		}
	}

	_obj.moveImage = function (_bool, _over) {		
		var direction = -1;
		if (_bool) {
			direction = 1;
		}								
		
		if (_over) {
			stopMove = false;
			var calculate = element_width * direction;			
			if (!wait) {
				_obj.startMoveImage(direction, calculate);
				}
		} else {		
			if (wait) { return false; }
			wait = true;
			var calculate = toScroll.scrollLeft() + element_width * direction * _options.moveCount;								
			toScroll.animate({ "scrollLeft" :  calculate } , speed, "linear", function () { 
				wait = false; 
				toScrollCount += _options.moveCount * direction;
				preloadImages();
				});
			}		
	}
	
	_obj.startMoveImage = function (_direction, _calculate) {
		wait = true;
		if (stopMove) {
			wait = false; 
			return false;
			}		
		toScroll.animate({ "scrollLeft" : toScroll.scrollLeft() + _calculate } , speed, "linear", function () {
			toScrollCount += _direction;
			preloadImages();
			_obj.startMoveImage(_direction, _calculate);			
			});
	}
	
	_obj.stopMoving = function () {		
		stopMove = true;				
	}
	
	_obj.coutRelSpeed = function() {			
		var cntVar = Math.round(1 / speed * 1000);	
		while (element_width % cntVar) {
			cntVar++;
		}		
		relSpeed = cntVar * direction;
	}
	
	_obj.mouseOverMove = function (_bool) {
		direction = -1;
		if (_bool) {
			direction = 1;
		}
		_obj.coutRelSpeed();			
		stopMove = false;
		if (!timeout) {	
			_obj.repeatScrollImg();
		}					
	}
	
	function preloadImages () {	
		var prevShow = toScrollCount;
		maxShow = prevShow; 							
		if (direction === 1) {	
		  if (realShow > maxShow - 1) {	    
			   maxShow = realShow + _options.preLoad;
			   for (i = prevShow - _options.preLoad; i < maxShow; i++) {
				    list_ul.children("li").eq(i).find("a img").show();
			   }
			}
		} else {		  		        			  
			   for (i = realShow - defContain - _options.preLoad; i < realShow - defContain; i++) {
            list_ul.children("li").eq(i).find("a img").show();
         }
    }												
	}	
	
	_obj.repeatScrollImg = function () {
		timeout = false;		
		if (stopMove) {		
			if (toScroll.scrollLeft() == (realShow - defContain) * element_width) {
				return false;
			}
		}						
		
		if ((defContain + toScroll.scrollLeft()) % element_width == 0) {
			realShow += direction ;			
			preloadImages();
			
			if (realShow > maxShow) {
				maxShow = realShow;
			}					  
		}
		
						
		toScroll.scrollLeft(toScroll.scrollLeft() + relSpeed);
		timeout = window.setTimeout(function() {_obj.repeatScrollImg()}, speed / 30);				
	}
}
