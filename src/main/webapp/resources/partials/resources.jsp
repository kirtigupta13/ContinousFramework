<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div>
	<!--Resource content-->
	<table class="table-base table-striped table-hover">
		<thead class="table-header">
			<tr>
				<th scope="col"><fmt:message key="common.tableHeader.name" /></th>
				<th scope="col"><fmt:message key="common.tableHeader.link" /></th>
				<th scope="col"><fmt:message key="common.tableHeader.skillLevel" /></th>
			</tr>
		</thead>
		<tbody>
			<tr ng-repeat="resource in resources">
				<td><label>{{resource.description}}</label></td>
				<td><label>{{resource.resourceLink}}</label></td>
				<td><label>{{resource.requiredSkillLevel}}</label></td>
			</tr>
		</tbody>
	</table>
</div>