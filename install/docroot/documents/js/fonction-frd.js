jQuery(document).ready(function(){


	$("#clin-oeil-eco a").simpletooltip({ showEffect: "fadeIn", hideEffect: "fadeOut" });
	

	// Gestion des blocs détaillants les critères
	//-------------------------------------------//

		// ouverture automatique du 1er bloc
		if ($('#critere1 .lire_suite').text() == "Lire la suite...") {
	   		$('#critere1 .lire_suite').text('x Fermer');
	   		$($('#critere1 .lire_suite').parent() + '#critere1 h3')
	   		.css({'color':'#09438b'});
	   	} else {
	   		$('#critere1 .lire_suite').text('Lire la suite...');
	   		$( $('#critere1 .lire_suite').parent() + '#critere1 h3')
	   		.css({'color':'#1C1C1C'});
	   	}
		$('#critere1 .lire_suite').next().slideToggle();
		
		// changement du curseur de la souris en element actif
		$('#critere1 h3').css("cursor", "pointer");
		$('#critere2 h3').css("cursor", "pointer");
		$('#critere3 h3').css("cursor", "pointer");
		$('#critere4 h3').css("cursor", "pointer");
		$('#critere5 h3').css("cursor", "pointer");
		$('#critere6 h3').css("cursor", "pointer");
		$('.lire_suite').css("cursor", "pointer");
		$('.go').css("cursor", "pointer");
		$('#en_savoir_plus').css("cursor", "pointer");
	
		// ouverture des blocs au clic sur le titre h3
		$('#critere1 h3').click(function(){
			var i = '#critere1 .lire_suite';
			var j = '#critere1 h3';
			if ($(i).text() == "Lire la suite...") {
	    		$(i).text('x Fermer');
	    		$($(i).parent() + j).css({'color':'#09438b'});
	    	} else {
	    		$(i).text('Lire la suite...');
	    		$( $(i).parent() + j).css({'color':'#1C1C1C'});
	    	}
			$(i).next().slideToggle();
		});
		$('#critere2 h3').click(function(){
			var i = '#critere2 .lire_suite';
			var j = '#critere2 h3';
			if ($(i).text() == "Lire la suite...") {
	    		$(i).text('x Fermer');
	    		$($(i).parent() + j).css({'color':'#09438b'});
	    	} else {
	    		$(i).text('Lire la suite...');
	    		$( $(i).parent() + j).css({'color':'#1C1C1C'});
	    	}
			$(i).next().slideToggle();
		});		
		$('#critere3 h3').click(function(){
			var i = '#critere3 .lire_suite';
			var j = '#critere3 h3';
			if ($(i).text() == "Lire la suite...") {
	    		$(i).text('x Fermer');
	    		$($(i).parent() + j).css({'color':'#09438b'});
	    	} else {
	    		$(i).text('Lire la suite...');
	    		$( $(i).parent() + j).css({'color':'#1C1C1C'});
	    	}
			$(i).next().slideToggle();
		});		
		$('#critere4 h3').click(function(){
			var i = '#critere4 .lire_suite';
			var j = '#critere4 h3';
			if ($(i).text() == "Lire la suite...") {
	    		$(i).text('x Fermer');
	    		$($(i).parent() + j).css({'color':'#09438b'});
	    	} else {
	    		$(i).text('Lire la suite...');
	    		$( $(i).parent() + j).css({'color':'#1C1C1C'});
	    	}
			$(i).next().slideToggle();
		});
		$('#critere5 h3').click(function(){
			var i = '#critere5 .lire_suite';
			var j = '#critere5 h3';
			if ($(i).text() == "Lire la suite...") {
	    		$(i).text('x Fermer');
	    		$($(i).parent() + j).css({'color':'#09438b'});
	    	} else {
	    		$(i).text('Lire la suite...');
	    		$( $(i).parent() + j).css({'color':'#1C1C1C'});
	    	}
			$(i).next().slideToggle();
		});		
		$('#critere6 h3').click(function(){
			var i = '#critere6 .lire_suite';
			var j = '#critere6 h3';
			if ($(i).text() == "Lire la suite...") {
	    		$(i).text('x Fermer');
	    		$($(i).parent() + j).css({'color':'#09438b'});
	    	} else {
	    		$(i).text('Lire la suite...');
	    		$( $(i).parent() + j).css({'color':'#1C1C1C'});
	    	}
			$(i).next().slideToggle();
		});
	//---fin bloc titre h3---//



	// Liens "lire la suite" internes aux blocs	
	//-----------------------------------------//
	
		// lire la suite pour bloc .critere-detail
   	    $('.lire_suite' + '.cd').click(function(){ 
   	    	if ($(this).text() == "Lire la suite...") {
   	    		$(this).text('x Fermer');
   	    		//$($(this).parent() + ".critere-detail h3").css('background','url(./images/illu/puce-ouverte.png) no-repeat 2px 3px');
   	    		$($(this).parent() + ".critere-detail h3").css('color','#09438b');
   	    	} else {
   	    		$(this).text('Lire la suite...');
   	    		//$( $(this).parent() + ".critere-detail h3").css('background','url(./images/illu/puce-fermee.png) no-repeat 2px 3px');
   	    		$($(this).parent() + ".critere-detail h3").css('color','#1C1C1C');
   	    	}
   	    	$(this).next().slideToggle("slow");
   	    });
   	    
   	    // lire la suite pour le lien du bloc .savoir-plus
   	    $('.lire_suite' + '.sp').click(function(){ 
			if ($(this).text() == "Lire la suite...") {
   	    		$(this).text('x Fermer');		
   	    		$($(this).parent() + ".savoir-plus h3").css('background','url(./images/illu/puce-savoir-o.png) no-repeat 0px 2px');
   	    	} else {
   	    		$(this).text('Lire la suite...');
   	    		$( $(this).parent() + ".savoir-plus h3").css('background','url(./images/illu/puce-savoir.png) no-repeat 0px 3px');
   	    	}
   	    	$(this).next().slideToggle("slow");

   	    });
   	    
   	    // lire la suite pour le lien du bloc .savoir-plus sur le titre h3
   	    $('.savoir-plus h3').click(function(){ 
			if ($('.lire_suite' + '.sp').text() == "Lire la suite...") {
   	    		$('.lire_suite' + '.sp').text('x Fermer');		
   	    		$($('.lire_suite' + '.sp').parent() + ".savoir-plus h3").css('background','url(./images/illu/puce-savoir-o.png) no-repeat 0px 2px');
   	    	} else {
   	    		$('.lire_suite' + '.sp').text('Lire la suite...');
   	    		$( $('.lire_suite' + '.sp').parent() + ".savoir-plus h3").css('background','url(./images/illu/puce-savoir.png) no-repeat 0px 3px');
   	    	}
   	    	$('.lire_suite' + '.sp').next().slideToggle("slow");

   	    });

		// lire la suite pour bloc critere-detail
	    $('.element1 > div.critere-detail-element').each( function(i){
            $(this).attr({ id: ++i });
        }); 

		$(".go").click(function(){	
			if ($(this).text() == "Lire la suite...") {
				$(this).text('x Fermer');
				//$(this).parent().animate({'height':'300px'});
				$(this).next().css('margin-bottom','30px').slideDown("normal");	  
			} else {
				$(this).text('Lire la suite...');
				$(this).next().slideUp("normal");
				//$(this).parent().animate({'height':'171px'});
			}  
			
		 });
		 
		 
		 
	$('a[href*=#]').click(function() {
 
		if (location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'') && location.hostname == this.hostname) {
			var $target = $(this.hash);
			$target = $target.length && $target || $('[name=' + this.hash.slice(1) +']');
			if ($target.length) {
				var targetOffset = $target.offset().top;
				$('html,body').animate({scrollTop: targetOffset}, 1000); 
				var b = $target.selector;
				var g = "#" + $($(b)).parent().attr("id") + ' .lire_suite';
				
		    	if ( $( $(g) ).text() == "Lire la suite...") {
	   	    		$( $(g) ).text('x Fermer');
	   	    		if ($($(b)).parent().attr("id") == "savoir-plus") {
	   	    			$($(b)).css({'background':'url(./images/illu/puce-savoir-o.png) no-repeat 0px 2px'});
	   	    		} 
	   	    		$($(b)).css({'color':'#09438b'});
	   	    		$( $(g) ).next().slideDown("slow");
	   	    	}
				return false;
			}
		}
	});
	

	$("#slider").easySlider({
		prevText:'Pr&eacute;c&eacute;dent',
		nextText:'Suivant',
		orientation:'horizontal'
	});

});

function effet2(e,f1,f2, cible) {	
	
	if ((e).text() == "Lire la suite...") {
   		(e).text('x Fermer');		
   		$($(e).parent() + cible ).css(f1);
   	} else {
   		$(e).text('Lire la suite...');
   		$( $(e).parent() + cible ).css(f2);
   	}
   		
   	$(e).next().slideToggle("slow");
}

function effet(e,f1,f2) {	
	
	if ((e).text() == "Lire la suite...") {
   		(e).text('x Fermer');		
   		$($(e).parent() + ".savoir-plus h3").css('background',f1);
   	} else {
   		$(e).text('Lire la suite...');
   		$( $(e).parent() + ".savoir-plus h3").css('background',f2);
   	}
   		
   	$(e).next().slideToggle("slow");
}

