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