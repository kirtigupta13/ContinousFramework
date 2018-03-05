/**
 * Function to validate whether checkBox is selected.
 */
$(document).ready(function() {
	disableDeleteAll();
	$('input:checkbox').click(disableDeleteAll);
});

var disableDeleteAll = function() {
	  if($('input:checked').length <= 0) {
		  $('#deleteAllBtn').prop('disabled',true);
	  } else {
		  $('#deleteAllBtn').prop('disabled',false);
	  }
};