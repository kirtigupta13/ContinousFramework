/**
 * 
 */
$(document).ready(function() {
	
		// Range slider for difficulty level
		var diffLevels = [ 'Very Easy', 'Easy', 'Medium', 'Hard', 'Very Hard',
				'Master', 'Expert' ];
		var width = 50 / (diffLevels.length - 1);
		
		$("#difficultyRangeSlider").slider({
			min : 1,
			max : diffLevels.length,
			change: function(event, ui) { 
				$('#diffiultyLevel').val(ui.value); 
		    } 
		});
		
		$.each(diffLevels, function(key, value) {
			var labelWidth = width;
			if (key === 0 || key === diffLevels.length - 1)
				labelWidth = width / 2;
			var val = $("<label style='width: " + labelWidth + "%'>" + value
					+ "</label>");
			$("#difficultyRating").append(val);
		});
	});