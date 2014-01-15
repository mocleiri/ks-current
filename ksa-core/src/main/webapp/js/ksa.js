// KSA Javascript library

jQuery.download = function (url, data, method) {
    //url and data options required
    if (url && data) {
        //data can be string of parameters or array/object
        data = typeof data == 'string' ? data : jQuery.param(data);
        //split params into form inputs
        var inputs = '';
        jQuery.each(data.split('&'), function () {
            var pair = this.split('=');
            inputs += '<input type="hidden" name="' + pair[0] + '" value="' + pair[1] + '" />';
        });
        //send request
        jQuery('<form action="' + url + '" method="' + (method || 'post') + '">' + inputs + '</form>')
            .appendTo('body').submit().remove();
    }
};

/*
* On "Generate 1098T" button click.
*/
function onGenerate1098TClick(/*String*/accountId) {
	// Hide the previous error message:
	jQuery("#documentGenerationErrorMessage_span").css("display", "none");
	
	// Get the selected report year:
	var reportYear = jQuery("#reportYearSelectControl_control option:selected").text();

	// Start download:
	jQuery.download("/ksa/uif/generate1098T", "viewId=Generate1098TView&methodToCall=download1098TForm&userId=" + accountId + "&reportYear=" + reportYear);
}

/*
* On "Null Effective and Recognition Dates" radio-button click. 
*/
function onClickRolloverRatesRadioNullDates() {
	// Turn any of the previosly selected radio buttons off and turn this option on:
	jQuery("#radioAutoAdvancedDates_control_0").prop("checked", false);
	jQuery("#radioCustomDates_control_0").prop("checked", false);
	jQuery("#radioNullDates_control_0").prop("checked", true);
	
	// Hide the input fields belonging to the other options:
	jQuery("#customTransactionDate").css("display", "none");
	jQuery("#customRecognitionDate").css("display", "none");
	jQuery("#inputNumDaysToAdvance").css("display", "none");
}

/*
* On "Automatically Advanced Effective and Recognition Dates" radio-button click. 
*/
function onClickRolloverRatesRadioAutoAdvancedDates() {
	// Turn any of the previosly selected radio buttons off and turn this option on:
	jQuery("#radioNullDates_control_0").prop("checked", false);
	jQuery("#radioCustomDates_control_0").prop("checked", false);
	jQuery("#radioAutoAdvancedDates_control_0").prop("checked", true);
	
	// Hide the input fields belonging to the other options:
	jQuery("#customTransactionDate").css("display", "none");
	jQuery("#customRecognitionDate").css("display", "none");
	
	// Display the input fields belonging to this option:
	jQuery("#inputNumDaysToAdvance").css("display", "block");
	jQuery("#inputNumDaysToAdvance").css("margin-left", "20px");
}

/*
* On "Custom Effective and Recognition Dates" radio-button click. 
*/
function onClickRolloverRatesRadioCustomDates() {
	// Turn any of the previosly selected radio buttons off and turn this option on:
	jQuery("#radioNullDates_control_0").prop("checked", false);
	jQuery("#radioAutoAdvancedDates_control_0").prop("checked", false);
	jQuery("#radioCustomDates_control_0").prop("checked", true);
	
	// Hide the input fields belonging to the other options:
	jQuery("#inputNumDaysToAdvance").css("display", "none");
	
	// Display the input fields belonging to this option:
	jQuery("#customTransactionDate").css("display", "block");
	jQuery("#customRecognitionDate").css("display", "block");
	jQuery("#customTransactionDate").css("margin-left", "20px");
	jQuery("#customRecognitionDate").css("margin-left", "20px");
}


