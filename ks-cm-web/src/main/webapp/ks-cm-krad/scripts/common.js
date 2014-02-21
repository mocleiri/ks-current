/**
 * SCRIPT METHODS USED BY MULTIPLE .JS FILES
 */

function onCourseLoad(isCurriculumSpecialist) {
    //  Make the tab panel fixed/sticky.
    jQuery("#course_tabs_tabList").css("position", "fixed");

    /**
     *  Hacks for single-page view
     */
    if (isCurriculumSpecialist) {
        //  Don't allow the tabs to be hidden.
        jQuery("div[data-type='TabWrapper']").addClass('never_hide');

        //  Register a handler for tab clicks
        jQuery("#course_tabs_tabs" ).on( "tabsactivate",
            function( event, ui ) {
                //  Find the id of the section corresponding to the clicked tab.
                var sectionId = ui.newPanel.attr('id').replace('_tab','');
                sectionId = "#" + sectionId;

                 //  Give focus to the first input widget. Had problems doing this after the scroll.
                jQuery(sectionId).find("input[type!='hidden'],textarea,button,select,a").first().focus();

                //  Scroll the window to the section
                jQuery('html,body').animate({
                    scrollTop: jQuery(sectionId).offset().top
                }, 1000);
            }
        );
    }
}

function scrollToCourseSection(divId){
    jQuery(document).scrollTop( jQuery("#" + divId).offset().top );
}

function onCourseCancel(){
    if(dirtyFormState.isDirty()) {
        showLightboxComponent('KS-CourseView-UnSavedChangesConfirmation');
    } else {
        return true;
    }
}

/**
 * Enter key causes next action button to fire, so this routine turns the Enter
 * keypress into a Tab keypress, essentially
 */
function tabToNextInput(e) {
    if (e.keyCode === 13) {
        e.preventDefault();
        var inputs = jq("input:visible,select:visible");
        var next = inputs.get(inputs.index(this) + 1);
        if (next) {
            next.focus();
        }
    }
}

function confirmDeletion(msg) {
    var response = confirm(msg);

    if (response == true) {
        submitForm();
    }
}

function confirmDeletionResponse(msg) {
    var response = confirm(msg);

    return response;
}

function confirmDeletion(msg) {
    var response = confirm(msg);

    if (response == true) {
        submitForm();
    }
}

function stepBrowserBack() {
    window.history.back(-2);
}

function showLightbox(componentId, url) {
    // Insert the following css files before the lightbox appearing

    var isPortal = false;
    parent.jQuery('head').find('link').each(function () {
            var link = jQuery(this);
            var href = jQuery(link).attr('href');
            if (href.indexOf('rice-portal/css/portal.css') > -1) {
                link.remove();
                isPortal = true;
            }
        }
    );

    parent.jQuery('head').append('<link href="' + url + '/kr-dev/rice-portal/css/lightbox.css" rel="stylesheet" type="text/css">');
    parent.jQuery('head').append('<link href="' + url + '/krad/css/global/fss-reset.css" rel="stylesheet" type="text/css">');
    parent.jQuery('head').append('<link href="' + url + '/krad/css/global/fss-layout.css" rel="stylesheet" type="text/css">');
    parent.jQuery('head').append('<link href="' + url + '/krad/css/global/fss-text.css" rel="stylesheet" type="text/css">');
    parent.jQuery('head').append('<link href="' + url + '/krad/plugins/rice/datatables/jquery.dataTables.css" rel="stylesheet" type="text/css">');
    parent.jQuery('head').append('<link href="' + url + '/krad/plugins/rice/datatables/TableTools.css" rel="stylesheet" type="text/css">');
    parent.jQuery('head').append('<link href="' + url + '/krad/plugins/fancybox/jquery.fancybox.css" rel="stylesheet" type="text/css">');
    parent.jQuery('head').append('<link href="' + url + '/krad/css/global/base.css" rel="stylesheet" type="text/css">');
    parent.jQuery('head').append('<link href="' + url + '/krad/css/ks/theme-krad.css" rel="stylesheet" type="text/css">');
    parent.jQuery('head').append('<link href="' + url + '/krad/css/ks/fss-theme-krad.css" rel="stylesheet" type="text/css">');
    parent.jQuery('head').append('<link href="' + url + '/ks-enroll/css/enroll.css" rel="stylesheet" type="text/css">');
    parent.jQuery('head').append('<link href="' + url + '/ks-enroll/css/acal.css" rel="stylesheet" type="text/css">');
    parent.jQuery('head').append('<link href="' + url + '/ks-enroll/css/ks-jquery.css" rel="stylesheet" type="text/css">');

    var overrideOptions = { afterClose:function () {
        // Remove the following css files before the lightbox appearing
        parent.jQuery('link[href="' + url + '/kr-dev/rice-portal/css/lightbox.css"]').remove();
        parent.jQuery('link[href="' + url + '/krad/css/global/fss-reset.css"]').remove();
        parent.jQuery('link[href="' + url + '/krad/css/global/fss-layout.css"]').remove();
        parent.jQuery('link[href="' + url + '/krad/css/global/fss-text.css"]').remove();
        parent.jQuery('link[href="' + url + '/krad/plugins/rice/datatables/jquery.dataTables.css"]').remove();
        parent.jQuery('link[href="' + url + '/krad/plugins/rice/datatables/TableTools.css"]').remove();
        parent.jQuery('link[href="' + url + '/krad/plugins/fancybox/jquery.fancybox.css"]').remove();
        parent.jQuery('link[href="' + url + '/krad/css/global/base.css"]').remove();
        parent.jQuery('link[href="' + url + '/krad/css/ks/theme-krad.css"]').remove();
        parent.jQuery('link[href="' + url + '/krad/css/ks/fss-theme-krad.css"]').remove();
        parent.jQuery('link[href="' + url + '/ks-enroll/css/enroll.css"]').remove();
        parent.jQuery('link[href="' + url + '/ks-enroll/css/acal.css"]').remove();
        parent.jQuery('link[href="' + url + '/ks-enroll/css/ks-jquery.css"]').remove();
        if(isPortal){
            parent.jQuery('head').append('<link href="' + url + '/rice-portal/css/portal.css" rel="stylesheet" type="text/css">');
        }
    }};
    showLightboxComponent(componentId, overrideOptions);
}

function setupCharCounters() {
	jQuery(".cm-charcount-max-24").jqEasyCounter({'maxChars': 24, 'maxCharsWarning': 14, 'msgTextAlign': 'left'});
	jQuery(".cm-charcount-max-255").jqEasyCounter({'maxChars': 255, 'maxCharsWarning': 220, 'msgTextAlign': 'left'});
	jQuery(".cm-charcount-max-500").jqEasyCounter({'maxChars': 500, 'maxCharsWarning': 450, 'msgTextAlign': 'left'});
}

/*Learning Objective functionality*/

function moveLo(button, collectionGroupId, controllerMethod) {
	performLOToolbarAction(button, collectionGroupId, controllerMethod);
	retrieveComponent(collectionGroupId, controllerMethod);
}

function performLOToolbarAction(button, collectionGroupId, controllerMethod) {
	var id = button.id;
	var lineSuffixIndex = id.lastIndexOf("_line");
	var lineNumberIndex = lineSuffixIndex + "_line".length;
	var index = id.substr(lineNumberIndex);
	/* Set the 'selected' property on the LoItem */
	jQuery('#KS-LoDisplayInfoWrapper-selected_line' + index + '_control').prop('value', true);
}

function applyIndentationStyling() {
	 jQuery(".uif-collectionItem[data-parent='LearningObjective-CollectionSection']").each(function(index) {
		 var indentString = jQuery('#KS-LoDisplayInfoWrapper-indentLevel_line' + index + '_control').prop('value');
		 var indentLevel = parseInt(indentString);
		 
		 var collectionItem = jQuery(this);
		 collectionItem.removeClass("cm-lo-indent-0 cm-lo-indent-50 cm-lo-indent-100 cm-lo-indent-150 cm-lo-indent-200 cm-lo-indent-250 cm-lo-indent-300 cm-lo-indent-350 cm-lo-indent-400 cm-lo-indent-450 cm-lo-indent-500");
		 collectionItem.addClass(getIndentationClass(indentLevel));
	 });
}

function getIndentationClass(indentLevel) {
	var indentationClass;
	switch (indentLevel) {
		case 0:
			indentationClass = "cm-lo-indent-0";
			break;
		case 1:
			indentationClass = "cm-lo-indent-50";
			break;
		case 2:
			indentationClass = "cm-lo-indent-100";
			break;
		case 3:
			indentationClass = "cm-lo-indent-150";
			break;
		case 4:
			indentationClass = "cm-lo-indent-200";
			break;
		case 5:
			indentationClass = "cm-lo-indent-250";
			break;
		case 6:
			indentationClass = "cm-lo-indent-300";
			break;
		case 7:
			indentationClass = "cm-lo-indent-350";
			break;
		case 8:
			indentationClass = "cm-lo-indent-400";
			break;
		case 9:
			indentationClass = "cm-lo-indent-450";
			break;
		case 10:
			indentationClass = "cm-lo-indent-500";
			break;
	}
	return indentationClass;
}

function showHideCreateCourseOptionalElements () {
    var showingAll = "Showing required and optional fields";
    var showAll = "Show all fields."
    var showingRequired = "Showing only required fields.";
    var showRequired = "Show only required fields.";

    var actualShowMsg = jQuery("#Create-CourseView-Admin-Message_span").text();

    if(actualShowMsg != null && actualShowMsg == showingRequired) {    // display all
        jQuery("#Create-CourseView-Admin-Message_span").text(showingAll);
        jQuery("#Create-CourseView-Admin-Message-expand-optional-link").text(showRequired);
        jQuery(".admin-not-required-field").show();
        jQuery(".hide-when-show-all-fields").hide();
    }  else  {
        jQuery("#Create-CourseView-Admin-Message_span").text(showingRequired);
        jQuery("#Create-CourseView-Admin-Message-expand-optional-link").text(showAll);
        jQuery(".admin-not-required-field").hide();
        jQuery(".hide-when-show-all-fields").show();
    }
    jQuery("#CreateCourseProposalTitleInputField_control").focus();

}

function createCourseShowHideObjectiveElements(hideId, showId) {
    jQuery("#"+hideId).hide();
    jQuery("#"+showId).show();

}