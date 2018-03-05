<div>
	<div>
		<input ng-model="query" type="text" placeholder="search category" class="field-input">
	</div>

	<!--Category content-->
	<div>
		<ul>
			<li ng-repeat="category in categories | filter:query">
				<a href="#/category_resources/{{category.id}}">{{category.name}}</a>
				<p>{{category.description}}</p>
			</li>
		</ul>
	</div>
</div>