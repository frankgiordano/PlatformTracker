/**
 * This function is only supported for a 1 tier dropdown menu
 * 
 * TODO NOTE: a refresh of the page will result in an incorrect menu selection, this is currently hard coded and needs
 * to be made dynamic. 
 * 
 * @param anchor this anchor that was clicked
 */
function toggleMenuActive(anchor) {
	var li = anchor.parents('li:first');
    var ul = li.parents('ul:first');
    var isDropdown = (li.filter("[class*='dropdown']").length > 0 || ul.filter("[class*='dropdown']").length > 0);
    
    if(isDropdown) {
    	var root_li = ul.parents('li:first');
    	var root_ul = root_li.parents('ul:first');
    	
    	if(!li.hasClass('dropdown') && !root_li.hasClass('active')) {
    		// remove other active classes
    		root_ul.find('li.active').removeClass('active');
	        
    		root_li.addClass('active');
    	}	
    }
    else {
		if (!li.hasClass('active')){
	        // remove other active classes
    		ul.find('li.active').removeClass('active');
	        
	        li.addClass('active');
	    }
    }
}