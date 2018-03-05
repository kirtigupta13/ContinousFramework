/**
 * Auto-complete function for Course view.
 */
$(document)
	.ready(function() {
	$("#categoryName")
		.autocomplete({
		source: function(request, response) {			
			var cache = JSON.parse(localStorage.getItem("cache"));
			if (cache != null) {
				var categoryName = {};
				var term = request.term.toLowerCase();
				for (var i; i < cache.length; i++) {
					categoryName = cache[i].name.toLowerCase();
					if (categoryName.indexOf(term) > -1) {
						response($.map(cache, function(item) {
						return {
							label: item.name,
							id: item.id,
							desc: item.description
						};
					}));
				  }
				}
			}
			$.ajax({
				url: "categories/list/all",
				dataType: "json",
				data: {
					startsWith: request.term
				},
				success: function(data) {
					localStorage.setItem("cache", JSON.stringify(data));
					response($.map(data, function(item) {
						return {
							label: item.name,
							id: item.id,
							desc: item.description
						};
					}));
				}
			});
		},

		// when mouse hovers over suggestions,
		// this function will be triggered
		// to populate selected value and also
		// pop-ups Tooltip
		focus: function(event, ui) {
			$(event.target)
				.val(ui.item.label);
			$(".ui-autocomplete > li")
				.prop("title", ui.item.desc);
			return false;
		},

		// When user selects a suggestion, form
		// will be submitted
		select: function(event, ui) {
			if (ui.item) {
				$(event.target)
					.val((ui.item.label));
				$("#categoryID")
					.val((ui.item.id));
			}
			$(event.target.form)
				.submit();
		}
	});
	
	setInterval(function() {
		localStorage.removeItem("cache");
	}, 1000 * 60 * 10); // 10min timeout for cache
});