<div class="widget-wrapper">
    <div class="widget-header">
        <h2>${welcomeWidgetTitle}</h2>
    </div>
    <div class="widget-body well">
        <div class="details-container" data-toggle="tooltip" title="<fmt:message key="homepage.generalDetails.widget.label.topCategory" />">
            <div class="details-value">
                <span class="glyphicon glyphicon-star"></span>
            </div>
            <div class="details-label">
                <span>${topCategory}</span>
            </div>                         
        </div>
        <div class="details-container" data-toggle="tooltip" title="<fmt:message key="homepage.generalDetails.widget.label.completedResources" />">
            <div class="details-value">
                <span>${numberOfCompletedResources}</span>
            </div>
            <div class="details-label">
                <fmt:message key="homepage.generalDetails.widget.label.completedResources" />
            </div> 
        </div> 
    </div>
    <div class="widget-footer">
        <span><a href="${pageContext.request.contextPath}/app/profile"><fmt:message key="homepage.generalDetails.widget.link" /></a></span>
    </div>
</div>