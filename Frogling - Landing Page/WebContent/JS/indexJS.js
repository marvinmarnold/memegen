var contentHeight = 70;

$(document).ready( function() {
	$('section#main-content').css('height', contentHeight+'px');
	
	$('.learn').click( function () {
		$text =  $(this).parent().children('p');
		if($text.is(':visible')) {
			$text.slideUp();
			contentHeight -= 170;
		} else {
			$text.slideDown();
			contentHeight += 170;
		}
		$('section#main-content').animate({
			height: contentHeight
		}, 'medium');
	});
});