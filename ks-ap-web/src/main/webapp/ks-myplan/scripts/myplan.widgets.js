/* This function may be redefined to add additional inputs to forms before posting */
function ksapAddPostOptionsToForm(tempForm) {
	return tempForm;
}

/**
 * Override at the institution level to specify additional parameters to append
 * to the query string for in-KSAP navigation links.
 */
function ksapAddGetParameters() {
	return '';
}

/**
 * Override at the institution level to specify additional parameters to append
 * to the query string for linking to other non-KSAP applicaitons in the same
 * enterprise environment.
 */
function ksapAddGetParametersExternal() {
	return '';
}

function ksapAdditionalFormData(additionalData) {
	return additionalData;
}

function readUrlHash(key) {
    var aHash = window.location.hash.replace('#', '').split('&');
    var oHash = {};
    jQuery.each(aHash, function (index, value) {
        oHash[value.split('=')[0]] = value.split('=')[1];
    });
    if (oHash[key]) {
        return oHash[key];
    } else {
        return false;
    }
}

function readUrlParam(key) {
    var aParams = window.location.search.replace('?', '').split('&');
    var oParams = {};
    jQuery.each(aParams, function (index, value) {
        oParams[value.split('=')[0]] = value.split('=')[1];
    });
    if (oParams[key]) {
        return oParams[key];
    } else {
        return false;
    }
}

/* This is for DOM changes to refresh the view on back to keep the view updated */
if (readUrlHash("modified") == "yes") {
    if (readUrlParam("viewId") != "CourseSearch-FormView") {
        var url = window.location.href;
        var hash = window.location.hash;
        if (url.split("#")[1].replace("modified=yes", "").length > 0) {
            window.location.assign(url.split("#")[0] + hash.replace("modified=yes", "modified=no"));
        } else {
            window.location.assign(url.split("#")[0]);
        }

    }
}

jQuery(document).ready(function () {
    jQuery("head").append('<!--[if ie 9]><style type="text/css" media="screen"> \
        button.uif-primaryActionButton,button.uif-secondaryActionButton, \
        button.uif-primaryActionButton:hover,button.uif-secondaryActionButton:hover,\
        button.uif-primaryActionButton[disabled="true"],\
        button.uif-primaryActionButton[disabled="disabled"],\
        button.uif-primaryActionButton[disabled="true"]:hover,\
        button.uif-primaryActionButton[disabled="disabled"]:hover,\
        button.uif-secondaryActionButton[disabled="true"],\
        button.uif-secondaryActionButton[disabled="disabled"],\
        button.uif-secondaryActionButton[disabled="true"]:hover,\
        button.uif-secondaryActionButton[disabled="disabled"]:hover{ \
            filter:none !important;}</style><![endif]-->\
    ');
});

function sessionExpired() {
    window.location = 'sessionExpired';
}

function stopEvent(e) {
    if (!e) {
        var e = window.event
    }
    ;
    if (e.stopPropagation) {
        e.preventDefault();
        e.stopPropagation();
    } else {
        e.returnValue = false;
        e.cancelBubble = true;
    }
    return false;
}

function openCourse(courseId, e) {
    stopEvent(e);
    var target = (e.currentTarget) ? e.currentTarget : e.srcElement;
    if (jQuery(target).parents(".jquerybubblepopup.jquerybubblepopup-myplan").length > 0) {
        window.location = "inquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + courseId;
    } else {
        openPlanItemPopUp(courseId, 'add_remove_course_popover_page', {courseId:courseId}, e, null, {tail:{align:'left'}, align:'left', position:'bottom', alwaysVisible:'false'}, true);
    }
}

/**
 * Open a popup which loads via ajax a separate view's component
 *
 * @param getId - Id of the component from the separate view to select to insert into popup.
 * @param retrieveData - Object of data used to passed to generate the separate view.
 * @param formAction - The action param of the popup inner form.
 * @param popupStyle - Object of css styling to apply to the initial inner div of the popup (will be replaced with remote component)
 * @param popupOptions - Object of settings to pass to the Bubble Popup jQuery Plugin.
 * @param e - An object containing data that will be passed to the event handler.
 */
function openPopup(getId, retrieveData, formAction, popupStyle, popupOptions, e) {
    stopEvent(e);
    fnCloseAllPopups();

    var popupOptionsDefault = {
        themePath:getConfigParam("kradUrl")+"/../ks-myplan/jquery-popover/jquerypopover-theme/",
        manageMouseEvents:true,
        selectable:true,
        tail:{align:"middle", hidden:false},
        position:"left",
        align:"center",
        alwaysVisible:false,
        themeMargins:{total:"20px", difference:"5px"},
        themeName:"myplan",
        distance:"0px",
        openingSpeed:5,
        closingSpeed:5
    };

    var target = (e.currentTarget) ? e.currentTarget : e.srcElement;
    var popupItem = (typeof popupOptions.selector == "undefined") ? jQuery(target) : jQuery(target).parents(popupOptions.selector);

    if (!popupItem.HasPopOver()) popupItem.CreatePopOver({manageMouseEvents:false});
    var popupSettings = jQuery.extend(popupOptionsDefault, popupOptions);
    var popupHtml = jQuery('<div />').attr("id", "KSAP-Popover");
    if (popupStyle) {
        jQuery.each(popupStyle, function (property, value) {
            popupHtml.css(property, value);
        });
    }
    popupSettings.innerHtml = popupHtml.wrap("<div>").parent().clone().html();

    popupItem.ShowPopOver(popupSettings, false);
    popupItem.FreezePopOver();

    var popupId = popupItem.GetPopOverID();

    fnPositionPopUp(popupId);
    if (!popupOptions.sticky) {
        clickOutsidePopOver(popupId, popupItem);
    }

    var retrieveForm = '<form id="retrieveForm" action="' + retrieveData.action + '" method="post" />'
    jQuery("body").append(retrieveForm);

    var elementToBlock = jQuery("#KSAP-Popover");

    var successCallback = function (htmlContent) {
        var component;
        if (jQuery("#requestStatus", htmlContent).length <= 0) {
            var popupForm = jQuery('<form />')
            		.attr("id", "popupForm")
            		.attr("action", formAction)
            		.attr("accept-charset", "UTF-8")
            		.attr("method", "post");
            component = jQuery("#" + getId, htmlContent).wrap(popupForm).parent();
        } else {
            var pageId = jQuery("#pageId").val();
            eval(jQuery("input[data-role='script'][data-for='" + pageId + "']", htmlContent).val().replace("#" + pageId, "body"));
            var errorMessage = '<img src="/student/ks-myplan/images/pixel.gif" alt="" class="icon"><div class="message">' + jQuery("#plan_item_action_response_page", htmlContent).data(kradVariables.VALIDATION_MESSAGES).serverErrors[0] + '</div>';
            component = jQuery("<div />").addClass("myplan-feedback error").html(errorMessage);
        }
        if (jQuery("#KSAP-Popover").length) {
            popupItem.SetPopOverInnerHtml(component);
            fnPositionPopUp(popupId);
            if (popupOptions.close || typeof popupOptions.close === 'undefined') jQuery("#" + popupId + " .jquerypopover-innerHtml").append('<img src="../ks-myplan/images/btnClose.png" class="myplan-popup-close"/>');
            jQuery("#" + popupId + " img.myplan-popup-close").on('click', function () {
                popupItem.HidePopOver();
                fnCloseAllPopups();
            });
        }
        runHiddenScripts(getId);
        // TODO: dont jump and focus when running hidden scripts
		var targetOffset = jQuery("#popupForm").offset().top - 100;
		jQuery('html,body').animate({
			scrollTop : targetOffset
		}, 250);
		jQuery("#popupForm input:first").focus();
        elementToBlock.unblock();
    };

    ksapAjaxSubmitForm(retrieveData, successCallback, elementToBlock, "retrieveForm");
    jQuery("form#retrieveForm").remove();
}

/**
 *   Gathers information for submission to the controller via ajax
 *
 * @param data - Variables and data to be submitted to the controller
 * @param successCallback - Code block to run after a successful return from the controller
 * @param elementToBlock - The html object being effected by the controller call
 * @param formId - Id of the form the submit is being called on
 * @param blockingSettings - Settings for the html object
 */
function ksapAjaxSubmitForm(data, successCallback, elementToBlock, formId, blockingSettings) {
	data = ksapAdditionalFormData(data);

    var submitOptions = {
        data:data,
        success:function (response) {
            var tempDiv = document.createElement('div');
            tempDiv.innerHTML = response;
            var hasError = checkForIncidentReport(response);
            if (!hasError) successCallback(tempDiv);
            jQuery("#formComplete").empty();
        },
        error:function(jqXHR, textStatus,
                errorThrown) {
	         hideLoading();
	         showGrowl(textStatus + " "
	             + errorThrown,
	             "Error");
	     },
	     statusCode : {
	         400 : function() {
	             showGrowl(
	                 "400 Bad Request",
	                 "Fatal Error");
	         },
	         500 : function() {
	             showGrowl(
	                 "500 Internal Server Error",
	                 "Fatal Error");
	         }
	     }
    };

    if (elementToBlock != null && elementToBlock.length) {
        var elementBlockingOptions = {
            beforeSend:function () {
                if (elementToBlock.hasClass("unrendered")) {
                    elementToBlock.append('<img src="' + getConfigParam("kradImageLocation") + 'loader.gif" alt="Loading..." /> Loading...');
                    elementToBlock.show();
                }
                else {
                    var elementBlockingDefaults = {
                        baseZ:500,
                        message:'<img src="../ks-myplan/images/ajaxLoader16.gif" alt="loading..." />',
                        fadeIn:0,
                        fadeOut:0,
                        overlayCSS:{
                            backgroundColor:'#fff',
                            opacity:0
                        },
                        css:{
                            border:'none',
                            width:'16px',
                            top:'0px',
                            left:'0px'
                        }
                    };
                    elementToBlock.block(jQuery.extend(elementBlockingDefaults, blockingSettings));
                }
            },
            complete:function () {
                elementToBlock.unblock();
            },
            error:function(jqXHR, textStatus,
                    errorThrown) {
	   	         hideLoading();
		         showGrowl(textStatus + " "
		             + errorThrown,
		             "Error");
                if (elementToBlock.hasClass("unrendered")) {
                    elementToBlock.hide();
                }
                else {
                    elementToBlock.unblock();
                }
            }
        };
    }
    jQuery.extend(submitOptions, elementBlockingOptions);
    var form = jQuery("#" + ((formId) ? formId : "kualiForm"));
    form.ajaxSubmit(submitOptions);
}


function openMenu(id, getId, atpId, e, selector, popupClasses, popupOptions, close) {
    stopEvent(e);
    if (atpId != null) {
        var openForPlanning = jQuery('input[id^="' + atpId + '_plan_status"]').val()

        if (openForPlanning == "false" && getId != "completed_menu_items") {
            getId = "completed_backup_menu_items"
        }
    }

    var popupBox;
    var target = (e.currentTarget && e.currentTarget != document) ? e.currentTarget : e.srcElement;
    if (selector === null) {
        popupBox = jQuery(target);
    } else {
        popupBox = jQuery(target).parents(selector);
    }

    var popupHtml = jQuery('<div />').attr("id", id + "_popup").attr("class", popupClasses)
    		.html(jQuery("#" + getId).html()).wrap("<div>").parent().clone().html();
    jQuery.each(popupBox.data(), function (key, value) {
    	var nvalue = value.replace(/'/, "&#39;");
    	popupHtml = eval("popupHtml.replace(/__KSAP__"+key+"__/gi,'"+nvalue+"')");
    });

    var popupOptionsDefault = {
        innerHtml:popupHtml,
        themePath:getConfigParam("kradUrl")+"/../ks-myplan/jquery-bubblepopup/jquerybubblepopup-theme/",
        manageMouseEvents:false,
        selectable:true,
        tail:{align:'middle', hidden:false},
        position:'left',
        align:'center',
        alwaysVisible:false,
        themeMargins:{total:'20px', difference:'5px'},
        themeName:'myplan',
        distance:'0px',
        openingSpeed:0,
        closingSpeed:0
    };

    var popupSettings = jQuery.extend(popupOptionsDefault, popupOptions);

    fnCloseAllPopups();
    
    popupBox.addClass("uif-tooltip");
    initBubblePopups();
    popupBox.SetBubblePopupOptions(popupSettings, true);
    popupBox.SetBubblePopupInnerHtml(popupSettings.innerHTML, true);
    popupBox.ShowBubblePopup();
    var popupBoxId = popupBox.GetBubblePopupID();
    popupBox.FreezeBubblePopup();

    jQuery("#" + id + "_popup a").each(function () {
        var linkId = jQuery(this).attr("id");
        var nlid = linkId + "_popup_" + id;
        jQuery(this).siblings("input[data-for='" + linkId + "']")
        		.removeAttr("script")
        		.attr("name", "script")
        		.attr("data-for", nlid)
        		.val(function (index, value) {
            return value.replace(linkId, nlid);
        });
        jQuery(this).attr("id", nlid);
        jQuery.each(jQuery(target).data(), function (key, value) {
            jQuery("#" + nlid).attr("data-" + key, value);
        });
    });

    if (close || typeof close === 'undefined') jQuery("#" + popupBoxId + " .jquerybubblepopup-innerHtml").append('<img src="../ks-myplan/images/btnClose.png" class="myplan-popup-close"/>');

    runHiddenScripts(id + "_popup");

    jQuery(document).on('click', function (e) {
        var tempTarget = (e.target) ? e.target : e.srcElement;
        if (jQuery(tempTarget).parents("div.jquerybubblepopup.jquerybubblepopup-myplan").length === 0) {
            popupBox.HideBubblePopup();
            fnCloseAllPopups();
        }
    });
}

/*
 ######################################################################################
 Function: Launch generic bubble popup
 ######################################################################################
 */
function openPopUpForm(id, getId, methodToCall, action, retrieveOptions, e, selector, popupStyles, popupOptions, close) {
    stopEvent(e);

    var popupHtml = jQuery('<div />').attr("id", id + "_popup");

    if (popupStyles) {
        jQuery.each(popupStyles, function (property, value) {
            jQuery(popupHtml).css(property, value);
        });
    }

    var popupOptionsDefault = {
        innerHtml:popupHtml.wrap("<div>").parent().clone().html(),
        themePath:getConfigParam("kradUrl")+"/../ks-myplan/jquery-bubblepopup/jquerybubblepopup-theme/",
        manageMouseEvents:false,
        selectable:true,
        tail:{align:'middle', hidden:false},
        position:'left',
        align:'center',
        alwaysVisible:false,
        themeMargins:{total:'20px', difference:'5px'},
        themeName:'myplan',
        distance:'10px',
        openingSpeed:0,
        closingSpeed:0
    };

    var popupSettings = jQuery.extend(popupOptionsDefault, popupOptions);

    var popupBox;
    var target = (e.currentTarget) ? e.currentTarget : e.srcElement;
    if (selector === null) {
        popupBox = jQuery(target);
    } else {
        popupBox = jQuery(target).parents(selector);
    }

    fnCloseAllPopups();

    popupBox.SetBubblePopupOptions(popupSettings, true);
    popupBox.SetBubblePopupInnerHtml(popupSettings.innerHTML, true);
    popupBox.ShowBubblePopup();
    var popupBoxId = popupBox.GetBubblePopupID();
    popupBox.FreezeBubblePopup();

    jQuery(document).on('click', function (e) {
        var tempTarget = (e.target) ? e.target : e.srcElement;
        if (jQuery(tempTarget).parents("div.jquerybubblepopup.jquerybubblepopup-myplan").length === 0) {
            popupBox.HideBubblePopup();
            fnCloseAllPopups();
        }
    });

    var tempForm = jQuery('<form />').attr("id", id + "_form").attr("action", action).attr("method", "post").hide();
    var tempFormInputs = '<div style="display:none;">';
    jQuery.each(retrieveOptions, function (name, value) {
        tempFormInputs += '<input type="hidden" name="' + name + '" value="' + value + '" />';
    });
    tempFormInputs += '</div>';
    jQuery(tempForm).append(tempFormInputs);
    jQuery("body").append(tempForm);

    var elementToBlock = jQuery("#" + id + "_popup");

    var updateRefreshableComponentCallback = function (htmlContent) {
        var component;
        if (jQuery("span#request_status_item_key", htmlContent).length <= 0) {
            component = jQuery("#" + getId, htmlContent);
            var planForm = jQuery('<form />').attr("id", id + "_form").attr("action", "plan").attr("method", "post");
        } else {
            var sError = '<img src="../ks-myplan/images/pixel.gif" alt="" class="icon"><span class="message">' + jQuery("#plan_item_action_response_page", htmlContent).data(kradVariables.VALIDATION_MESSAGES).serverErrors[0] + '</span>';
            component = jQuery("<div />").html(sError).addClass("myplan-feedback error").width(175);
        }
        elementToBlock.unblock({onUnblock:function () {
            if (jQuery("#" + id + "_popup").length) {
                popupBox.SetBubblePopupInnerHtml(component);
                jQuery("#" + popupBoxId + " .jquerybubblepopup-innerHtml").wrapInner(planForm);
                if (close || typeof close === 'undefined') jQuery("#" + popupBoxId + " .jquerybubblepopup-innerHtml").append('<img src="../ks-myplan/images/btnClose.png" class="myplan-popup-close"/>');
                jQuery("#" + popupBoxId + " img.myplan-popup-close").on('click', function () {
                    popupBox.HideBubblePopup();
                    fnCloseAllPopups();
                });
            }
            runHiddenScripts(getId);
        }});
    };

    myplanAjaxSubmitForm(methodToCall, updateRefreshableComponentCallback, {reqComponentId:id, skipViewInit:"false"}, elementToBlock, id);
    jQuery("form#" + id + "_form").remove();
}


/*
 ######################################################################################
 Function: Launch plan item bubble popup
 ######################################################################################
 */
function openPlanItemPopUp(xid, getId, retrieveOptions, e, selector, popupOptions, close) {
    stopEvent(e);

    var popupHtml = jQuery('<div />').attr("id", xid + "_popup").css({
        width:"300px",
        height:"16px"
    });

    var popupOptionsDefault = {
        innerHtml:popupHtml.wrap("<div>").parent().clone().html(),
        themePath:getConfigParam("kradUrl")+"/../ks-myplan/jquery-bubblepopup/jquerybubblepopup-theme/",
        manageMouseEvents:true,
        selectable:true,
        tail:{align:'middle', hidden:false},
        position:'left',
        align:'center',
        alwaysVisible:false,
        themeMargins:{total:'18px', difference:'5px'},
        themeName:'myplan',
        distance:'10px',
        openingSpeed:0,
        closingSpeed:0
    };

    var popupSettings = jQuery.extend(popupOptionsDefault, popupOptions);

    var popupBox;
    var target = (e.currentTarget) ? e.currentTarget : e.srcElement;
    if (selector === null) {
        popupBox = jQuery(target);
    } else {
        popupBox = jQuery(target).parents(selector);
    }

    fnCloseAllPopups();

    popupBox.addClass("uif-tooltip");
    initBubblePopups();
    popupBox.SetBubblePopupOptions(popupSettings, true);
    popupBox.SetBubblePopupInnerHtml(popupSettings.innerHTML, true);
    popupBox.ShowBubblePopup();
    var popupBoxId = popupBox.GetBubblePopupID();
    fnPositionPopUp(popupBoxId);
    popupBox.FreezeBubblePopup();

    jQuery(document).on('click', function (e) {
        var tempTarget = (e.target) ? e.target : e.srcElement;
        if (jQuery(tempTarget).parents("div.jquerybubblepopup.jquerybubblepopup-myplan").length === 0) {
            popupBox.HideBubblePopup();
            fnCloseAllPopups();
        }
    });

    var tempForm = jQuery('<form />').attr("id", xid + "_form").attr("action", "plan").attr("method", "post").hide();
    var tempFormInputs = '<div style="display:none;"><input type="hidden" name="viewId" value="PlannedCourse-FormView" />';
    jQuery.each(retrieveOptions, function (name, value) {
        tempFormInputs += '<input type="hidden" name="' + name + '" value="' + value + '" />';
    	tempFormInputs = ksapAddPostOptionsToForm(tempFormInputs);
    });
    tempFormInputs += '</div>';
    jQuery(tempForm).append(tempFormInputs);
    jQuery("body").append(tempForm);

    var elementToBlock = jQuery("#" + xid + "_popup");

    var updateRefreshableComponentCallback = function (htmlContent) {
        var component;
        if (jQuery("span#request_status_item_key", htmlContent).length <= 0) {
            component = jQuery("#" + getId, htmlContent);
            var planForm = jQuery('<form />').attr("id", xid + "_form").attr("action", "plan").attr("method", "post");
        } else {
            var sError = '<img src="../ks-myplan/images/pixel.gif" alt="" class="icon"><span class="message">' + jQuery("#plan_item_action_response_page", htmlContent).data(kradVariables.VALIDATION_MESSAGES).serverErrors[0] + '</span>';
            component = jQuery("<div />").html(sError).addClass("myplan-feedback error").width(175);
        }
        
        var ngid = getId + "_" + xid;
        var cpgid = "course_popover_group_" + xid;
        var dlgid = "delete_from_list_confirmation_" + xid;
        component.attr("id", ngid);
        jQuery("#course_popover_group", htmlContent).attr("id", cpgid);
        jQuery("#delete_from_list_confirmation", htmlContent).attr("id", dlgid);
        jQuery("button", htmlContent).each(function () {
            var butId = jQuery(this).attr("id");
            var nlid = butId + "_" + xid;
            jQuery(this).siblings("input[data-for='" + butId + "']")
            		.removeAttr("script")
            		.attr("name", "script")
            		.attr("data-for", nlid)
            		.val(function (index, value) {
                return eval('value.replace(/'+butId+'/g, nlid)')
                	.replace("course_popover_group", cpgid)
                	.replace("delete_from_list_confirmation", dlgid);
            });
            jQuery(this).attr("id", nlid);
            jQuery.each(jQuery(target).data(), function (key, value) {
                jQuery("#" + nlid).attr("data-" + key, value);
            });
        });
        
        elementToBlock.unblock({onUnblock:function () {
            if (jQuery("#" + xid + "_popup").length) {
                popupBox.SetBubblePopupInnerHtml(component);
                fnPositionPopUp(popupBoxId);
                if (status != 'error') jQuery("#" + popupBoxId + " .jquerybubblepopup-innerHtml").wrapInner(planForm);
                if (close || typeof close === 'undefined') jQuery("#" + popupBoxId + " .jquerybubblepopup-innerHtml").append('<img src="../ks-myplan/images/btnClose.png" class="myplan-popup-close"/>');
                jQuery("#" + popupBoxId + " img.myplan-popup-close").on('click', function () {
                    popupBox.HideBubblePopup();
                    fnCloseAllPopups();
                });
            }
            runHiddenScripts(ngid);

            jQuery(document).on('click', function (e) {
                var tempTarget = (e.target) ? e.target : e.srcElement;
                if (jQuery(tempTarget).parents("div.jquerybubblepopup.jquerybubblepopup-myplan").length === 0) {
                    popupBox.HideBubblePopup();
                    fnCloseAllPopups();
                }
            });
        }});
    };

    myplanAjaxSubmitForm("startAddPlannedCourseForm", updateRefreshableComponentCallback, {reqComponentId:xid, skipViewInit:"false"}, elementToBlock, xid);
    jQuery("form#" + xid + "_form").remove();
}
function openDialog(sText, e, close) {
    stopEvent(e);

    var dialogHtml = jQuery('<div />').html(sText).css({
        width:"300px"
    });

    var popupSettings = {
        innerHtml:dialogHtml.wrap("<div>").parent().clone().html(),
        themePath:getConfigParam("kradUrl")+"/../ks-myplan/jquery-bubblepopup/jquerybubblepopup-theme/",
        manageMouseEvents:false,
        selectable:true,
        tail:{hidden:true},
        position:'top',
        align:'center',
        alwaysVisible:false,
        themeMargins:{total:'20px', difference:'5px'},
        themeName:'myplan',
        distance:'0px',
        openingSpeed:0,
        closingSpeed:0
    };

    var popupBox;
    var target = (e.currentTarget) ? e.currentTarget : e.srcElement;
    popupBox = jQuery(target);

    fnCloseAllPopups();
    initBubblePopups();
    popupBox.SetBubblePopupOptions(popupSettings, true);
    popupBox.SetBubblePopupInnerHtml(popupSettings.innerHTML, true);
    popupBox.ShowBubblePopup();
    var popupBoxId = popupBox.GetBubblePopupID();


    if (close || typeof close === 'undefined') jQuery("#" + popupBoxId + " .jquerybubblepopup-innerHtml").append('<img src="../ks-myplan/images/btnClose.png" class="myplan-popup-close"/>');

    fnPositionPopUp(popupBoxId);
    popupBox.FreezeBubblePopup();

    jQuery(document).on('click', function (e) {
        var tempTarget = (e.target) ? e.target : e.srcElement;
        if (jQuery(tempTarget).parents("div.jquerybubblepopup.jquerybubblepopup-myplan").length === 0) {
            popupBox.HideBubblePopup();
            fnCloseAllPopups();
        }
    });

    jQuery("#" + popupBoxId + " img.myplan-popup-close").on('click', function () {
        popupBox.HideBubblePopup();
        fnCloseAllPopups();
    });
}

function fnPositionPopUp(popupBoxId) {
    if (parseFloat(jQuery("#" + popupBoxId).css("top")) < 0 || parseFloat(jQuery("#" + popupBoxId).css("left")) < 0) {
        var top = (document.documentElement && document.documentElement.scrollTop) || document.body.scrollTop;
        var left = (document.documentElement && document.documentElement.scrollLeft) || document.body.scrollLeft;
        var iTop = ( top + ( jQuery(window).height() / 2 ) ) - ( jQuery("#" + popupBoxId).height() / 2 );
        var iLeft = ( left + ( jQuery(window).width() / 2 ) ) - ( jQuery("#" + popupBoxId).width() / 2 );
        jQuery("#" + popupBoxId).css({top:iTop + 'px', left:iLeft + 'px'});
    }
}


function myplanWriteHiddenToForm(propertyName, propertyValue, formId) {
    //removing because of performFinalize bug
    jQuery('input[name="' + escapeName(propertyName) + '"]').remove();

    if (propertyValue.indexOf("'") != -1) {
        jQuery("<input type='hidden' name='" + propertyName + "'" + ' value="' + propertyValue + '"/>').appendTo(jQuery("#" + formId));
    } else {
        jQuery("<input type='hidden' name='" + propertyName + "' value='" + propertyValue + "'/>").appendTo(jQuery("#" + formId));
    }
}
/*
 ######################################################################################
 Function: Submit
 ######################################################################################
 */
function myplanAjaxSubmitPlanItem(id, xid, type, methodToCall, e, bDialog) {
    var target = (e.currentTarget) ? e.currentTarget : e.srcElement;
    var targetText = ( jQuery.trim(jQuery(target).text()) != '') ? jQuery.trim(jQuery(target).text()) : "Error";
    var elementToBlock = (target.nodeName != 'INPUT') ? jQuery(target) : jQuery(target).parent();
    jQuery('input[name="methodToCall"]').remove();
    jQuery('#' + xid + '_form input[name="' + type + '"]').remove();
    jQuery('#' + xid + '_form input[name="viewId"]').remove();
    jQuery("#" + xid + "_form").append(ksapAddPostOptionsToForm('<input type="hidden" name="methodToCall" value="' + methodToCall + '" /><input type="hidden" name="' + type + '" value="' + id + '" /><input type="hidden" name="viewId" value="PlannedCourse-FormView" />'));
    var updateRefreshableComponentCallback = function (htmlContent) {
        var status = jQuery.trim(jQuery("span#request_status_item_key_control", htmlContent).text().toLowerCase());
        elementToBlock.unblock();
        switch (status) {
            case 'success':
                var oMessage = { 'message':'<img src="../ks-myplan/images/pixel.gif" alt="" class="icon"><span class="message">' + jQuery("#plan_item_action_response_page", htmlContent).data(kradVariables.VALIDATION_MESSAGES).serverInfo[0] + '</span>', 'cssClass':'myplan-feedback success' };
                var json = jQuery.parseJSON(jQuery.trim(jQuery("span#json_events_item_key_control", htmlContent).text()));
                for (var key in json) {
                    if (json.hasOwnProperty(key)) {
                        eval('jQuery.publish("' + key + '", [' + JSON.stringify(jQuery.extend(json[key], oMessage)) + ']);');
                    }
                }
                setUrlHash('modified', 'yes');
                break;
            case 'error':
                var oMessage = { 'message':'<img src="../ks-myplan/images/pixel.gif" alt="" class="icon"><span class="message">' + jQuery("#plan_item_action_response_page", htmlContent).data(kradVariables.VALIDATION_MESSAGES).serverErrors[0] + '</span>', 'cssClass':'myplan-feedback error' };
                if (!bDialog) {
                    var sContent = jQuery("<div />").append(oMessage.message).addClass("myplan-feedback error").css({"background-color":"#fff"});
                    var sHtml = jQuery("<div />").append('<div class="uif-headerField uif-sectionHeaderField"><h3 class="uif-header">' + targetText + '</h3></div>').append(sContent);
                    if (jQuery("body").HasBubblePopup()) jQuery("body").HideBubblePopup();
                    openDialog(sHtml.html(), e);
                } else {
                    eval('jQuery.publish("ERROR", [' + JSON.stringify(oMessage) + ']);');
                }
                break;
        }
    };
    var blockOptions = {
        message:'<img src="../ks-myplan/images/btnLoader.gif"/>',
        css:{
            width:'100%',
            border:'none',
            backgroundColor:'transparent'
        },
        overlayCSS:{
            backgroundColor:'#fff',
            opacity:0.6,
            padding:'0px 1px',
            margin:'0px -1px'
}
    };
    myplanAjaxSubmitForm(methodToCall, updateRefreshableComponentCallback, {reqComponentId:xid, skipViewInit:'false'}, elementToBlock, xid, blockOptions);
}

/*Function used for moving the plan Item from planned to backup*/
function myPlanAjaxPlanItemMove(id, xid, type, methodToCall, e) {
    stopEvent(e);
    var tempForm = jQuery('<form />').attr("id", xid + "_form").attr("action", "plan").attr("method", "post").hide();
    jQuery("body").append(tempForm);
    myplanAjaxSubmitPlanItem(id, xid, type, methodToCall, e, false);
    fnCloseAllPopups();
    jQuery("form#" + id + "_form").remove();
}


function myplanAjaxSubmitSectionItem(id, methodToCall, action, formData, e) {
    stopEvent(e);
    var target = (e.currentTarget) ? e.currentTarget : e.srcElement;
    var targetText = ( jQuery.trim(jQuery(target).text()) != '') ? jQuery.trim(jQuery(target).text()) : "Error";
    var elementToBlock = (target.nodeName != 'INPUT') ? jQuery(target) : jQuery(target).parent();
    var tempForm = '<form id="' + id + '_form" action="' + action + '" method="post" style="display:none;">';
    tempForm += '<input type="hidden" name="methodToCall" value="' + methodToCall + '" />';
    jQuery.each(formData, function (name, value) {
        tempForm += '<input type="hidden" name="' + name + '" value="' + value + '" />';
    });
    tempForm += '</form>';
    jQuery("body").append(tempForm);

    var updateRefreshableComponentCallback = function (htmlContent) {
        var status = jQuery.trim(jQuery("span#request_status_item_key_control", htmlContent).text().toLowerCase());
        elementToBlock.unblock();
        switch (status) {
            case 'success':
                var oMessage = { 'message':'<img src="../ks-myplan/images/pixel.gif" alt="" class="icon"><span class="message">' + jQuery("#plan_item_action_response_page", htmlContent).data(kradVariables.VALIDATION_MESSAGES).serverInfo[0] + '</span>', 'cssClass':'myplan-feedback success' };
               /* var json = jQuery.parseJSON(jQuery.trim(jQuery("span#json_events_item_key_control", htmlContent).text()));
                for (var key in json) {
                    if (json.hasOwnProperty(key)) {
                        eval('jQuery.publish("' + key + '", [' + JSON.stringify(jQuery.extend(json[key], oMessage)) + ']);');
                    }
                }*/
                var sContent = jQuery("<div />").append(oMessage.message).addClass("myplan-feedback success").css({"background-color":"#fff"});
                var sHtml = jQuery("<div />").append('<div class="uif-headerField uif-sectionHeaderField"><h3 class="uif-header">' + "Success" + '</h3></div>').append(sContent);

                if (jQuery("body").HasBubblePopup()) jQuery("body").RemoveBubblePopup();
                openDialog(sHtml.html(), e);
                setUrlHash('modified', 'yes');
                break;
            case 'error':
                var oMessage = { 'message':'<img src="../ks-myplan/images/pixel.gif" alt="" class="icon"><span class="message">' + jQuery("#plan_item_action_response_page", htmlContent).data(kradVariables.VALIDATION_MESSAGES).serverErrors[0] + '</span>', 'cssClass':'myplan-feedback error' };
                var sContent = jQuery("<div />").append(oMessage.message).addClass("myplan-feedback error").css({"background-color":"#fff"});
                var sHtml = jQuery("<div />").append('<div class="uif-headerField uif-sectionHeaderField"><h3 class="uif-header">' + targetText + '</h3></div>').append(sContent);
                if (jQuery("body").HasBubblePopup()) jQuery("body").RemoveBubblePopup();
                openDialog(sHtml.html(), e);
                break;
        }
    };
    var blockOptions = {
        message:'<img src="../ks-myplan/images/btnLoader.gif"/>',
        css:{
            width:'100%',
            border:'none',
            backgroundColor:'transparent'
        },
        overlayCSS:{
            backgroundColor:'#fff',
            opacity:0.6,
            padding:'0px 1px',
            margin:'0px -1px'
        }
    };
    myplanAjaxSubmitForm(methodToCall, updateRefreshableComponentCallback, {reqComponentId:id, skipViewInit:'false'}, elementToBlock, id, blockOptions);
    jQuery("form#" + id + "_form").remove();
}
/*
 ######################################################################################
 Function: Retrieve component content through ajax
 ######################################################################################
 */
function myplanRetrieveComponent(id, getId, methodToCall, action, retrieveOptions, highlightId, elementBlockingSettings) {
    var tempForm = '<form id="' + id + '_form" action="' + action + '" method="post" style="display:none;">'; //jQuery('<form />').attr("id", id + "_form").attr("action", action).attr("method", "post").hide();
    jQuery.each(retrieveOptions, function (name, value) {
        tempForm += '<input type="hidden" name="' + name + '" value="' + value + '" />';
        tempForm = ksapAddPostOptionsToForm(tempForm);
    });
    tempForm += '</form>';
    jQuery("body").append(tempForm);

    var elementToBlock = jQuery("#" + id);

    var updateRefreshableComponentCallback = function (htmlContent) {
        var component = jQuery("#" + getId, htmlContent);
        elementToBlock.unblock({onUnblock:function () {
            // replace component
            if (jQuery("#" + id).length) {
                jQuery("#" + id).replaceWith(component);
            }

            runHiddenScripts(getId);

            if (jQuery("input[data-role='script'][data-for='" + getId + "']", htmlContent).length > 0) {
                eval(jQuery("input[data-role='script'][data-for='" + getId + "']", htmlContent).val());
            }

            if (highlightId) {
                jQuery("[id^='" + highlightId + "']").parents('li').animate({backgroundColor:"#ffffcc"}, 1).animate({backgroundColor:"#ffffff"}, 1500, function () {
                    jQuery(this).removeAttr("style");
                });
            }
        }});
    };

    if (!methodToCall) {
        methodToCall = "search";
    }

    myplanAjaxSubmitForm(methodToCall, updateRefreshableComponentCallback, {reqComponentId:id, skipViewInit:"false"}, elementToBlock, id, elementBlockingSettings);
    jQuery("form#" + id + "_form").remove();
}
/*
 ######################################################################################
 Function:   KRAD's ajax submit function modified to allow submission of a form
 other then the kuali form
 ######################################################################################
 */
function myplanAjaxSubmitForm(methodToCall, successCallback, additionalData, elementToBlock, formId, elementBlockingSettings) {
    var data = {};

    // methodToCall checks
    if (methodToCall == null) {
        var methodToCallInput = jQuery("input[name='methodToCall']");
        if (methodToCallInput.length > 0) {
            methodToCall = jQuery("input[name='methodToCall']").val();
        }
    }

    // check to see if methodToCall is still null
    if (methodToCall != null || methodToCall !== "") {
        data.methodToCall = methodToCall;
    }

    data.renderFullView = false;

    // remove this since the methodToCall was passed in or extracted from the page, to avoid issues
    jQuery("input[name='methodToCall']").remove();

    if (additionalData != null) {
        jQuery.extend(data, additionalData);
    }

    var viewState = jQuery(document).data(kradVariables.VIEW_STATE);
    if (!jQuery.isEmptyObject(viewState)) {
        var jsonViewState = jQuery.toJSON(viewState);

        // change double quotes to single because escaping causes problems on URL
        jsonViewState = jsonViewState.replace(/"/g, "'");
        jQuery.extend(data, {clientViewState:jsonViewState});
    }

    var submitOptions = {
        data:data,
        success:function (response) {
            var tempDiv = document.createElement('div');
            tempDiv.innerHTML = response;
            var hasError = checkForIncidentReport(response);
            if (!hasError) {
                successCallback(tempDiv);
            }
            jQuery("#formComplete").empty();
        },
        error:function (jqXHR, textStatus) {
            alert("Request failed: " + textStatus);
        }
    };

    if (elementToBlock != null && elementToBlock.length) {
        var elementBlockingOptions = {
            beforeSend:function () {
                if (elementToBlock.hasClass("unrendered")) {
                    elementToBlock.append('<img src="' + getConfigParam("kradImageLocation") + 'loader.gif" alt="Loading..." /> Loading...');
                    elementToBlock.show();
                }
                else {
                    var elementBlockingDefaults = {
                        baseZ:500,
                        message:'<img src="../ks-myplan/images/ajaxLoader16.gif" alt="loading..." />',
                        fadeIn:0,
                        fadeOut:0,
                        overlayCSS:{
                            backgroundColor:'#fff',
                            opacity:0
                        },
                        css:{
                            border:'none',
                            width:'16px',
                            top:'0px',
                            left:'0px'
                        }
                    };
                    elementToBlock.block(jQuery.extend(elementBlockingDefaults, elementBlockingSettings));
                }
            },
            complete:function () {
                elementToBlock.unblock();
            },
            error:function(jqXHR, textStatus,
                           errorThrown) {
                hideLoading();
                showGrowl(textStatus + " "
                    + errorThrown,
                    "Error");
                if (elementToBlock.hasClass("unrendered")) {
                    elementToBlock.hide();
                }
                else {
                    elementToBlock.unblock();
                }
            },
            statusCode : {
                500 : function() {
                    showGrowl(
                        "500 Internal Server Error",
                        "Fatal Error");
                }
            }

        };
    }
    jQuery.extend(submitOptions, elementBlockingOptions);
    var form;
    if (formId) {
        form = jQuery("#" + formId + "_form");
    } else {
        form = jQuery("#kualiForm");
    }
    form.ajaxSubmit(submitOptions);
}
/*
 ######################################################################################
 Function:   Truncate (ellipse) a single horizontally aligned item so all items
 fit on one line.
 ######################################################################################
 */
function truncateField(id) {
    jQuery("#" + id + " .uif-horizontalFieldGroup").each(function () {
        jQuery(this).css("display", "block");
        var fixed = 0;
        var margin = 10;
        jQuery(this).find(".uif-boxLayoutHorizontalItem:not(.myplan-text-ellipsis)").each(function () {
            fixed = fixed + jQuery(this).width();
        });
        var ellipsis = jQuery(this).width() - ( ( fixed + 1 ) + margin );
        jQuery(this).find(".uif-boxLayoutHorizontalItem.myplan-text-ellipsis").width(ellipsis);
    });
}
function truncateAuditTitle(id) {
    jQuery("#" + id + " .myplan-audit-title").each(function () {
        if (readUrlParam("viewId") == "DegreeAudit-FormView") {
            if (
                (readUrlParam("auditId") == false && jQuery(this).siblings("div[id^='hidden_recentAuditId']").length > 0) ||
                (readUrlParam("auditId") == jQuery(this).parents(".uif-verticalFieldGroup").attr("id"))
            ) {
                jQuery(this).find(".uif-label label").html("Viewing");
            }
        }

        var width = jQuery(this).width();
        var label = parseFloat(jQuery(this).find(".uif-label label").css({"color":"#777777"}).width()) + parseFloat(jQuery(this).find(".uif-label label").css("padding-right"));

        jQuery(this).find(".uif-label").next("span").width(width - label - 1).css({
            "text-overflow":"ellipsis",
            "white-space":"nowrap",
            "overflow":"hidden",
            "display":"block",
            "float":"left"
        });
    });
}
/*
 ######################################################################################
 Function:   Slide into view hidden horizontally aligned items specifying the id
 of the item being brought into view.
 ######################################################################################
 */
function fnPopoverSlider(showId, parentId, direction) {
    var newDirection;
    if (direction === 'left') {
        newDirection = 'right';
    } else {
        newDirection = 'left';
    }
    jQuery("#" + parentId + " > .uif-horizontalBoxLayout > div.uif-boxLayoutHorizontalItem:visible").hide("slide", {
        direction:direction
    }, 100, function () {
        jQuery("#" + parentId + " > .uif-horizontalBoxLayout > div.uif-boxLayoutHorizontalItem").filter("#" + showId).show("slide", {
            direction:newDirection
        }, 100, function () {
        	jQuery(this).focus();
        });
    });
}
/*
 ######################################################################################
 Function:   Close all bubble popups
 ######################################################################################
 */
function fnCloseAllPopups() {
	hideBubblePopups();
	// Remove inner HTML for My Plan created popups 
    jQuery(".jquerybubblepopup-myplan > .jquerybubblepopup-innerHtml").children().remove();
    // TODO remove after review: if (jQuery("body").HasBubblePopup()) jQuery("body").HideBubblePopup();
    // TODO remove after review: jQuery(document).off();
    // KRAD 2.2.0 uses a global event handler to update popups
}

/**
 * Close the current popup
 *
 * Used for the manual close of the popup.
 */
function fnClosePopup() {
    if (jQuery("body").HasPopOver()) {
        jQuery("body").HidePopOver();
        jQuery("body").RemovePopOver();
    }
    jQuery("div.jquerypopover").remove();
    jQuery("body").off("click");
}

/*
 ######################################################################################
 Function:   Build Term Plan View heading
 ######################################################################################
 */
function fnBuildTitle(aView) {
    var aFirst = jQuery.trim(jQuery(aView[0]).find("div:hidden[id^='plan_base_atpId']").text());
    var aLast = jQuery.trim(jQuery(aView[aView.length - 1]).find("div:hidden[id^='plan_base_atpId']").text());
    jQuery("#planned_courses_detail .myplan-plan-header").html(aFirst+ ' - ' + aLast);
}
/*
 ######################################################################################
 Function:   expand/collapse backup course set within plan view
 ######################################################################################
 */
function fnToggleBackup(e) {
    stopEvent(e);
    var target = (e.currentTarget) ? e.currentTarget : e.srcElement;
    if (!jQuery(target).hasClass("disabled")) {
        var oBackup = jQuery(target).parents(".myplan-term-backup").find(".uif-stackedCollectionLayout");
        var oQuarter = jQuery(target).parents("li");
        var iSpeed = 500;
        var iDefault = 26;
        if (jQuery(target).hasClass("expanded")) {
            var iAdjust = ( oBackup.height() - ( iDefault * 2 ) ) * -1;
            jQuery(target).removeClass("expanded");
            jQuery(target).find("span").html("Show");
        } else {
            var iAdjust = ( oBackup.find("span a").size() * iDefault ) - oBackup.height();
            jQuery(target).addClass("expanded");
            jQuery(target).find("span").html("Hide");
        }
        oBackup.animate({"height":oBackup.height() + iAdjust}, {duration:iSpeed});
        oQuarter.animate({"height":oQuarter.height() + iAdjust}, {duration:iSpeed});
    }
}
/*
 ######################################################################################
 Function:   expand/collapse backup course set within plan view
 ######################################################################################
 */
function myplanCreateLightBoxLink(controlId, options) {
    jQuery(function () {
        var showHistory = false;

        // Check if this is called within a light box
        if (!jQuery(".fancybox-wrap", parent.document).length) {

            // Perform cleanup when lightbox is closed
            options['beforeClose'] = cleanupClosedLightboxForms;

            // If this is not the top frame, then create the lightbox
            // on the top frame to put overlay over whole window
            if (top == self) {
                jQuery("#" + controlId).fancybox(options);
            } else {
                jQuery("#" + controlId).click(function (e) {
                    e.preventDefault();
                    top.jQuery.fancybox(options);
                });
            }
        } else {
            //jQuery("#" + controlId).attr('target', '_self');
            showHistory = true;
        }

        // Set the renderedInLightBox = true param
        if (options['href'].indexOf('&renderedInLightBox=true') == -1) {
            options['href'] = options['href'] + '&renderedInLightBox=true'
                + '&showHome=false' + '&showHistory=' + showHistory
                + '&history=' + jQuery('#formHistory\\.historyParameterString').val();
        }
    });
}

function myplanLightBoxLink(href, options, e) {
    stopEvent(e);
    var target = (e.currentTarget) ? e.currentTarget : e.srcElement;
    options['autoHeight'] = true;
    options['href'] = href;
    options['beforeClose'] = cleanupClosedLightboxForms;
    top.jQuery.fancybox(options);
}

function myplanCreateTooltip(id, text, options, onMouseHoverFlag, onFocusFlag) {
    var elementInfo = getHoverElement(id);
    var element = elementInfo.element;
    options['themePath'] = getConfigParam("kradUrl")+"/../krad/plugins/tooltip/jquerybubblepopup-theme/";

    // Check to see if a data attribute help is defined. Use that if defined.
    // This is built so that SpringEL can be used for generating the html. But it
    // also introduces a limitation of not allowing &quot; in the text
    if (jQuery("#" + id).data('help') && jQuery("#" + id).data('help').length > 0) {
        options['innerHtml'] = jQuery("#" + id).data('help');
    } else {
        options['innerHtml'] = text;
    }
    options['manageMouseEvents'] = false;
    if (onFocusFlag) {
        // Add onfocus trigger
        jQuery("#" + id).focus(function () {
            //            if (!jQuery("#" + id).IsBubblePopupOpen()) {
            // TODO : use data attribute to check if control
            if (!isControlWithMessages(id)) {
                if (!jQuery("#" + id).HasBubblePopup()) jQuery("#" + id).CreateBubblePopup(options);
                jQuery("#" + id).SetBubblePopupOptions(options, true);
                jQuery("#" + id).SetBubblePopupInnerHtml(options.innerHTML, true);
                jQuery("#" + id).ShowBubblePopup();
            }
            //            }
        });
        jQuery("#" + id).blur(function () {
            jQuery("#" + id).HideBubblePopup();
        });
    }
    if (onMouseHoverFlag) {
        // Add mouse hover trigger
        jQuery("#" + id).hover(function () {
            if (!jQuery("#" + id).IsBubblePopupOpen()) {
                if (!isControlWithMessages(id)) {
                    if (!jQuery("#" + id).HasBubblePopup()) jQuery("#" + id).CreateBubblePopup(options);
                    jQuery("#" + id).SetBubblePopupOptions(options, true);
                    jQuery("#" + id).SetBubblePopupInnerHtml(options.innerHTML, true);
                    jQuery("#" + id).ShowBubblePopup();
                }
            }
        }, function (event) {
            if (!onFocusFlag || !jQuery("#" + id).is(":focus")) {
                var result = mouseInTooltipCheck(event, id, element, this, elementInfo.type);
                if (result) {
                    mouseLeaveHideTooltip(id, jQuery("#" + id), element, elementInfo.type);
                }
            }
        });
    }
}

function degreeAuditButton() {
    if (jQuery.cookie('myplan_audit_running')) {
        return true;
    } else {
        var id = getAuditProgram("id");

        if (id) {
            return (id == 'default');
        } else {
            return true;
        }
    }
}

var blockPendingAuditStyle = {
    message: '<img src="../ks-myplan/images/ajaxAuditRunning32.gif" alt="" class="icon"/><div class="heading">We are currently running your degree audit for \'<span class="programName"></span>\'.</div><div class="content">Audits may take 1-5 minutes to load. Feel free to leave this page to explore MyPlan further while your audit is running. You will receive a browser notification when your report is complete.</div>',
    fadeIn: 400,
    fadeOut: 800,
    css: {
        padding: '30px 30px 30px 82px',
        margin: '30px',
        width: 'auto',
        textAlign: 'left',
        border: 'solid 1px #ffd14c',
        backgroundColor: '#fffdd7',
        'border-radius': '15px',
        '-webkit-border-radius': '15px',
        '-moz-border-radius': '15px'
    },
    overlayCSS: {
        backgroundColor: '#fff',
        opacity: 0.85,
        border: 'none',
        cursor: 'wait'
    }
};

var blockPendingAudit;

function changeLoadingMessage(selector) {
    blockPendingAudit = setInterval(function(){setLoadingMessage(selector)},100);
}

function setLoadingMessage(selector) {
	if (jQuery('.myplan-audit-report div.blockUI.blockMsg.blockElement').length > 0){
        fnAddLoadingText(selector);
	}
}

function fnAddLoadingText(selector) {
    clearInterval(blockPendingAudit);
    jQuery(selector + " div.blockUI.blockOverlay").css(blockPendingAuditStyle.overlayCSS);
    jQuery(selector + " div.blockUI.blockMsg.blockElement").html(blockPendingAuditStyle.message).css(blockPendingAuditStyle.css).data("growl","false");
    jQuery(selector + " div.blockUI.blockMsg.blockElement .programName").text(getAuditProgram("name"));
}

function removeCookie() {
    jQuery.cookie("myplan_audit_running", null, {expires: new Date().setTime(0)});
}

function getAuditProgram(param) {
    var id = 'select_programParam_control';
    if (param == 'id') {
        return jQuery('select#' + id).val();
    } else {
        return jQuery('select#' + id + ' option:selected').text();
    }

//    var id;
//    switch (parseFloat(jQuery("input[name='campusParam']:checked").val())) {
//        case 310:
//            id = 'select_programParam_for_campus_310_control';
//            break;
//        case 311:
//            id = 'select_programParam_for_campus_311_control';
//            break;
//        case 312:
//            id = 'select_programParam_for_campus_312_control';
//            break;
//        case 313:
//            id = 'select_programParam_for_campus_313_control';
//            break;
//        case 314:
//            id = 'select_programParam_for_campus_314_control';
//            break;
//        case 315:
//            id = 'select_programParam_for_campus_315_control';
//            break;
//        case 316:
//            id = 'select_programParam_for_campus_316_control';
//            break;
//        case 317:
//            id = 'select_programParam_for_campus_317_control';
//            break;
//        default:
//            id = null;
//    }

}

function setPendingAudit(minutes) {
    if (jQuery.cookie('myplan_audit_running') == null) {
        var data = {};

        data.expires = new Date();
        data.expires.setTime(data.expires.getTime() + (minutes * 60 * 1000));
        data.programId = getAuditProgram('id');
        data.programName = getAuditProgram('name');
        data.recentAuditId = jQuery("input[id^='hidden_recentAuditId']").val();

        if (typeof data.recentAuditId === 'undefined') data.recentAuditId = '';

        if (data.programId != 'default') {
            jQuery.ajax({
                url:"audit/status",
                data:{"programId":data.programId, "auditId":data.recentAuditId},
                dataType:"json",
                beforeSend:null,
                success:function (response) {
                    if (response.status == "PENDING") {
                        jQuery.cookie('myplan_audit_running', JSON.stringify(data), {expires:data.expires});
                        jQuery("button#degree_audit_run").attr("disabled", true);
                        jQuery.publish('REFRESH_AUDITS');
                    }
                },
                statusCode:{
                    500: function() { /*sessionExpired();*/ }
                    }
            });
        }
    } else {
        showGrowl("Another audit is currently pending.", "Degree Audit Error", "errorGrowl");
    }
}

function getPendingAudit(id) {
    if (jQuery.cookie('myplan_audit_running')) {
        var component = jQuery("#" + id + " ul");
        var data = jQuery.parseJSON(decodeURIComponent(jQuery.cookie('myplan_audit_running')));
        if (data) {
            var item = jQuery("<li />").addClass("pending").html('<img src="../ks-myplan/images/ajaxPending16.gif" class="icon"/><span class="title">Running <span class="program">' + data.programName + '</span></span>');
            component.prepend(item);
            pollPendingAudit(data.programId, data.recentAuditId);
        }
        if (component.find("li").size() > 0 && component.next("div.uif-boxGroup").length > 0) {
            component.next("div.uif-boxGroup").remove();
        }
    }
}

function blockPendingAudit(id) {
    if (readUrlParam("auditId") == false && jQuery.cookie('myplan_audit_running')) {
        var elementToBlock = jQuery("#" + id);
        var audit = jQuery.parseJSON(decodeURIComponent(jQuery.cookie('myplan_audit_running')));
        elementToBlock.block(blockPendingAuditStyle);
        jQuery("#" + id + " div.blockUI.blockMsg.blockElement").data("growl","true");
        jQuery("#" + id + " div.blockUI.blockMsg.blockElement .programName").text(audit.programName);
        jQuery("#" + id).subscribe('AUDIT_COMPLETE', function() { window.location.assign(window.location.href.split("#")[0]); });
    }
}

function pollPendingAudit(programId, recentAuditId) {
    var interval = 1; // polling interval in seconds
    var maxDuration = 5; // max duration to poll in minutes
    jQuery.ajaxPollSettings.maxInterval = (maxDuration * 60) / interval;
    jQuery.ajaxPollSettings.pollingType = "interval";
    jQuery.ajaxPollSettings.interval = (interval * 1000);

    jQuery.ajaxPoll({
        url: "audit/status",
        data: {"programId":programId, "auditId":recentAuditId},
        dataType: "json",
        beforeSend: null,
        successCondition: function(response) {
            return (response.status == 'DONE' || response.status == 'FAILED' || jQuery.cookie("myplan_audit_running") == null);
        },
        success:function (response) {
            var growl = true;
            if (readUrlParam("viewId") == "DegreeAudit-FormView") {
                growl = jQuery(".myplan-audit-report div.blockUI.blockMsg.blockElement").data("growl");
            }

            if (jQuery.cookie("myplan_audit_running") == null || response.status == 'FAILED') {
                if (growl) showGrowl("Your audit was unable to complete.", "Degree Audit Error", "errorGrowl");
            } else {
                var data = jQuery.parseJSON(decodeURIComponent(jQuery.cookie("myplan_audit_running")));
                if (growl) showGrowl(data.programName + " audit is ready to view.", "Degree Audit Completed", "infoGrowl");
            }
            jQuery.cookie("myplan_audit_running", null, {expires:new Date().setTime(0)});
            jQuery.publish("AUDIT_COMPLETE");
        }
    });
}

function buttonState(jqueryObj, buttonId) {
    if (jqueryObj.val().length === 0) {
        jQuery("button#" + buttonId).attr('disabled', true);
    } else {
        jQuery("button#" + buttonId).attr('disabled', false);
    }
}

function setUrlHash(key, value) {
    var aHash = [];
    if (window.location.hash) {
        aHash = window.location.hash.replace('#', '').split('&');
    }
    var oHash = {};
    if (aHash.length > 0) {
        jQuery.each(aHash, function (index, value) {
            oHash[value.split('=')[0]] = value.split('=')[1];
        });
        var oTemp = {};
        oTemp[key] = value;
        jQuery.extend(oHash, oTemp);
    } else {
        oHash[key] = value;
    }

    aHash = [];
    for (var key in oHash) {
        aHash.push(encodeURIComponent(key) + "=" + encodeURIComponent(oHash[key]));
    }

    window.location.replace("#" + aHash.join("&"));
}

(function ($) {

    $.fn.characterCount = function (options) {

        var oDefaults = {
            maxLength:100,
            warningLength:20,
            classCounter:'counter',
            classWarning:'warning'
        };

        var options = $.extend(oDefaults, options);

        function calculate(obj, options) {
            var iCount = $(obj).val().length;
            var iAvailable = options.maxLength - iCount;
            var sValue = $(obj).val();
            if (iCount > options.maxLength) {
                $(obj).val(sValue.substr(0, options.maxLength));
            }
            if (iAvailable <= options.warningLength && iAvailable >= 0) {
                $('.' + options.classCounter).addClass(options.classWarning);
            } else {
                $('.' + options.classCounter).removeClass(options.classWarning);
            }
            $('.' + options.classCounter).html('<strong>' + $(obj).val().length + '</strong> / ' + options.maxLength + ' characters');
        }

        ;

        this.each(function () {
            calculate(this, options);
            $(this).keyup(function () {
                calculate(this, options);
            });
            $(this).change(function () {
                calculate(this, options);
            });
        });
    };

})(jQuery);

function fnCreateDate(sData) {
    var jTemp = jQuery(sData);
    jTemp.find("legend, .myplan-sort-remove").remove();
    var sDate = jQuery.trim(jTemp.text());

    if (sDate.length > 2) {
        return Date.parse(sDate);
    } else {
        return 0;
    }
}

jQuery.fn.dataTableExt.oSort['longdate-asc'] = function (x, y) {
    x = fnCreateDate(x);
    y = fnCreateDate(y);

    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
};
jQuery.fn.dataTableExt.oSort['longdate-desc'] = function (x, y) {
    x = fnCreateDate(x);
    y = fnCreateDate(y);

    return ((x < y) ? 1 : ((x > y) ? -1 : 0));
};
Array.max = function (array) {
    return Math.max.apply(Math, array);
};


/*Quick Add*/

function openQuickAddPopUp(id, getId, retrieveOptions, e, selector, popupOptions, close) {
    stopEvent(e);

    var popupHtml = jQuery('<div />').attr("id", id + "_popup").css({
        width:"353px",
        height:"16px"
    });

    var popupOptionsDefault = {
        innerHtml:popupHtml.wrap("<div>").parent().clone().html(),
        themePath:getConfigParam("kradUrl")+"/../ks-myplan/jquery-bubblepopup/jquerybubblepopup-theme/",
        manageMouseEvents:false,
        selectable:true,
        tail:{align:'middle', hidden:false},
        position:'left',
        align:'center',
        alwaysVisible:false,
        themeMargins:{total:'20px', difference:'5px'},
        themeName:'myplan',
        distance:'0px',
        openingSpeed:0,
        closingSpeed:0
    };

    var popupSettings = jQuery.extend(popupOptionsDefault, popupOptions);

    var popupBox;
    var target = (e.currentTarget) ? e.currentTarget : e.srcElement;
    if (selector === null) {
        popupBox = jQuery(target);
    } else {
        popupBox = jQuery(target).parents(selector);
    }

    fnCloseAllPopups();
    initBubblePopups();
    popupBox.SetBubblePopupOptions(popupSettings, true);
    popupBox.SetBubblePopupInnerHtml(popupSettings.innerHTML, true);
    popupBox.ShowBubblePopup();
    var popupBoxId = popupBox.GetBubblePopupID();
    fnPositionPopUp(popupBoxId);
    popupBox.FreezeBubblePopup();

    jQuery(document).on('click', function (e) {
        var tempTarget = (e.target) ? e.target : e.srcElement;
        if (jQuery(tempTarget).parents("div.jquerybubblepopup.jquerybubblepopup-myplan").length === 0) {
            popupBox.HideBubblePopup();
            fnCloseAllPopups();
        }
    });

    var tempForm = jQuery('<form />').attr("id", id + "_form").attr("action", "quickAdd").attr("method", "post").hide();
    var tempFormInputs = '<div style="display:none;"><input type="hidden" name="viewId" value="QuickAdd-FormView" />';
    jQuery.each(retrieveOptions, function (name, value) {
        tempFormInputs += '<input type="hidden" name="' + name + '" value="' + value + '" />';
        tempFormInputs = ksapAddPostOptionsToForm(tempFormInputs);
    });
    tempFormInputs += '</div>';
    jQuery(tempForm).append(tempFormInputs);
    jQuery("body").append(tempForm);

    var elementToBlock = jQuery("#" + id + "_popup");

    var updateRefreshableComponentCallback = function (htmlContent) {
        var component;
        if (jQuery("span#request_status_item_key", htmlContent).length <= 0) {
            component = jQuery("#" + getId, htmlContent);
            var quickAddForm = jQuery('<form />').attr("id", id + "_form").attr("action", "quickAdd").attr("method", "post");
        } else {
            var sError = '<img src="../ks-myplan/images/pixel.gif" alt="" class="icon"/><span class="message">' + jQuery("#quick_add_action_response_page", htmlContent).data(kradVariables.VALIDATION_MESSAGES).serverErrors[0] + '</span>';
            component = jQuery("<div />").html(sError).addClass("myplan-feedback error").width(175);
        }
        elementToBlock.unblock({onUnblock:function () {
            if (jQuery("#" + id + "_popup").length) {
                popupBox.SetBubblePopupInnerHtml(component);
                fnPositionPopUp(popupBoxId);
                if (status != 'error') jQuery("#" + popupBoxId + " .jquerybubblepopup-innerHtml").wrapInner(quickAddForm);
                if (close || typeof close === 'undefined') jQuery("#" + popupBoxId + " .jquerybubblepopup-innerHtml").append('<img src="../ks-myplan/images/btnClose.png" class="myplan-popup-close"/>');
                jQuery("#" + popupBoxId + " img.myplan-popup-close").on('click', function () {
                    popupBox.HideBubblePopup();
                    fnCloseAllPopups();
                });
            }
            runHiddenScripts(getId);

            jQuery(document).on('click', function (e) {
                var tempTarget = (e.target) ? e.target : e.srcElement;
                if (jQuery(tempTarget).parents("div.jquerybubblepopup.jquerybubblepopup-myplan").length === 0) {
                    popupBox.HideBubblePopup();
                    fnCloseAllPopups();
                }
            });
        }});
    };

    myplanAjaxSubmitForm("start", updateRefreshableComponentCallback, {reqComponentId:id, skipViewInit:"false"}, elementToBlock, id);
    jQuery("form#" + id + "_form").remove();
}

function myplanAjaxSubmitQuickAdd(id, submitOptions, methodToCall, e, bDialog) {
    var target = (e.currentTarget) ? e.currentTarget : e.srcElement;
    var targetText = ( jQuery.trim(jQuery(target).text()) != '') ? jQuery.trim(jQuery(target).text()) : "Error";
    var elementToBlock = (target.nodeName != 'INPUT') ? jQuery(target) : jQuery(target).parent();
    jQuery('input[name="methodToCall"]').remove();
    jQuery('#' + id + '_form input[name="viewId"]').remove();
    var formInputs = '<div style="display:none;"><input type="hidden" name="methodToCall" value="' + methodToCall + '" /><input type="hidden" name="viewId" value="QuickAdd-FormView" />';
    jQuery.each(submitOptions, function (name, value) {
        formInputs += '<input type="hidden" name="' + name + '" value="' + value + '" />';
        jQuery('#' + id + '_form input[name="' + name + '"]').remove();
    });
    formInputs += '</div>';
    jQuery("#" + id + "_form").append(formInputs);

    /*Set up Response Status Message*/
    var updateRefreshableComponentCallback = function (htmlContent) {
        var status = jQuery.trim(jQuery("span#request_status_item_key_control", htmlContent).text().toLowerCase());
        elementToBlock.unblock();
        switch (status) {
            case 'success':
                var oMessage = { 'message':'<img src="../ks-myplan/images/pixel.gif" alt="" class="icon"><span class="message">' + jQuery("#quick_add_action_response_page", htmlContent).data(kradVariables.VALIDATION_MESSAGES).serverInfo[0] + '</span>', 'cssClass':'myplan-feedback success' };
                var json = jQuery.parseJSON(jQuery.trim(jQuery("span#json_events_item_key_control", htmlContent).text()));
                for (var key in json) {
                    if (json.hasOwnProperty(key)) {
                        eval('jQuery.publish("' + key + '", [' + JSON.stringify(jQuery.extend(json[key], oMessage)) + ']);');
                    }
                }
                setUrlHash('modified', 'yes');
                break;
            case 'error':
                var oMessage = { 'message':'<img src="../ks-myplan/images/pixel.gif" alt="" class="icon"><span class="message">' + jQuery("#quick_add_action_response_page", htmlContent).data(kradVariables.VALIDATION_MESSAGES).serverErrors[0] + '</span>', 'cssClass':'myplan-feedback error' };
                eval('jQuery.publish("ERROR", [' + JSON.stringify(oMessage) + ']);');
                break;
        }
    };

    /*Sets up the status indicator (loader icon)*/
    var blockOptions = {
        message:'<img src="../ks-myplan/images/btnLoader.gif"/>',
        css:{
            width:'100%',
            border:'none',
            backgroundColor:'transparent'
        },
        overlayCSS:{
            backgroundColor:'#fff',
            opacity:0.6,
            padding:'0px 1px',
            margin:'0px -1px'
        }
    };

    /*Submit information to the application*/
    myplanAjaxSubmitForm(methodToCall, updateRefreshableComponentCallback, {reqComponentId:id, skipViewInit:'false'}, elementToBlock, id, blockOptions);
}
function autoCompleteText(atpId) {
    var sQuery = jQuery("input[id='search_text_box_control']").val();
    var emptySuggestions = ["No courses Found"];
    jQuery("#search_text_box_control").autocomplete({source:function (request, response) {
        jQuery.ajax({
            url:"quickAdd/autoSuggestions?courseCd=" + sQuery + "&atpId=" + atpId,
            type:"GET",
            beforeSend:null,
            data:"list=" + '',
            dataType:"json",
            error:function () {
                jQuery("#search_text_box_control").autocomplete({source:emptySuggestions});
            },
            success:function (data) {
                if (data.aaData.length > 0) {
                    response(data.aaData);
                }
                else {
                    response(emptySuggestions)
                }
            }
        });
    }
    });
    jQuery(document).ajaxStart(jQuery.unblockUI).ajaxStop(jQuery.unblockUI);

}
function showDataTableDetail(actionComponent, tableId, useImages) {
    var oTable = null;
    var tables = jQuery.fn.dataTable.fnTables();
    jQuery(tables).each(function () {
        var dataTable = jQuery(this).dataTable();
        if (jQuery(actionComponent).closest(dataTable).length) {
            oTable = dataTable;
        }
    });

    if (oTable != null) {
        var nTr = jQuery(actionComponent).parents('tr')[0];
        if (useImages && jQuery(actionComponent).find("img").length) {
            jQuery(actionComponent).find("img").hide();
        } else {
            jQuery(actionComponent).hide();
        }
        var newRow = oTable.fnOpen(nTr, toggleRowDetails(actionComponent), "uif-rowDetails");
        var detailsId = jQuery(newRow).find(".uif-group").first().attr("id");
        jQuery(newRow).find(".uif-group").first().attr("id", detailsId + "_details")
        jQuery(newRow).find("a").each(function () {
            var linkId = jQuery(this).attr("id");
            jQuery(this).siblings("input[data-for='" + linkId + "']").removeAttr("script").attr("name", "script").val(function (index, value) {
                return value.replace("'" + linkId + "'", "'" + linkId + "_details'");
            });
            jQuery(this).attr("id", linkId + "_details");
        });
        runHiddenScripts(detailsId + "_details");
        jQuery(newRow).find(".uif-group").first().show();
    }
}

function expandDataTableDetail(actionComponent, tableId, useImages, expandText, collapseText) {
    var oTable = null;
    var tables = jQuery.fn.dataTable.fnTables();
    jQuery(tables).each(function () {
        var dataTable = jQuery(this).dataTable();
        //ensure the dataTable is the one that contains the action that was clicked
        if (jQuery(actionComponent).closest(dataTable).length) {
            oTable = dataTable;
        }
    });

    if (oTable != null) {
        var nTr = jQuery(actionComponent).parents('tr')[0];
        if (oTable.fnIsOpen(nTr)) {
            if (useImages && jQuery(actionComponent).find("img").length) {
                jQuery(actionComponent).find("img").replaceWith(detailsOpenImage.clone());
            }
            if (expandText) {
                jQuery(actionComponent).text(expandText);
            }
            jQuery(nTr).next().first().find(".uif-group").first().slideUp(function () {
                oTable.fnClose(nTr);
            });
        }
        else {
            if (useImages && jQuery(actionComponent).find("img").length) {
                jQuery(actionComponent).find("img").replaceWith(detailsCloseImage.clone());
            }
            if (collapseText) {
                jQuery(actionComponent).text(collapseText);
            }
            var newRow = oTable.fnOpen(nTr, actionComponent, "uif-rowDetails");
            var detailsId = jQuery(newRow).find(".uif-group").first().attr("id");
            jQuery(newRow).find(".uif-group").first().attr("id", detailsId + "_details")
            jQuery(newRow).find("a").each(function () {
                var linkId = jQuery(this).attr("id");
                jQuery(this).siblings("input[data-for='" + linkId + "']").removeAttr("script").attr("name", "script").val(function (index, value) {
                    return value.replace("'" + linkId + "'", "'" + linkId + "_details'");
                });
                jQuery(this).attr("id", linkId + "_details");
            });
            runHiddenScripts(detailsId + "_details");
            jQuery(newRow).find(".uif-group").first().slideDown();
        }
    }
}

function expandHiddenSubcollection(actionComponent, expandText, collapseText) {
    var subcollection = jQuery(actionComponent).closest('.uif-group.uif-collectionItem').children('.uif-boxLayout').children('.uif-subCollection');

    if (subcollection.is(":visible")) {
        subcollection.slideUp(250, function() {
            if (expandText) {
                jQuery(actionComponent).text(expandText);
            }
        });
    } else {
        subcollection.slideDown(250, function() {
            if (collapseText) {
                jQuery(actionComponent).text(collapseText);
            }
        });
    }
}

function myplanReplaceWithJson(id, url, retrieveOptions) {
    jQuery.getJSON(url, retrieveOptions, function (response) {
        jQuery("#" + id).fadeOut(250, function () {
            jQuery(this).html("<strong>" + response.enrollment + "</strong> / " + response.limit).fadeIn(250);
        });
    });
}

function myplanGetSectionEnrollment(url, retrieveOptions, componentId) {
    var elementToBlock = jQuery(".myplan-enrl-data").parent();
    if (componentId) elementToBlock = jQuery("#" + componentId + " .myplan-enrl-data").parent();
    jQuery.ajax({
        url:url,
        data:retrieveOptions,
        dataType:"json",
        beforeSend:function () {
            elementToBlock.block({
                message:'<img src="../ks-myplan/images/ajaxLoader16.gif" alt="Fetching enrollment data..." />',
                fadeIn:0,
                fadeOut:0,
                overlayCSS:{
                    backgroundColor:'#fff',
                    opacity:0
                },
                css:{
                    border:'none',
                    width:'16px',
                    top:'0px',
                    left:'0px'
                }
            });
        },
        error:function () {
            elementToBlock.fadeOut(250);
            elementToBlock.each(function () {
                jQuery(this).css("text-align", "center").find("img.myplan-enrl-data").addClass("alert").attr("alt", "Oops, couldn't fetch the data. Refresh the page.").attr("title", "Oops, couldn't fetch the data. Refresh the page.");
            });
            elementToBlock.fadeIn(250);
            elementToBlock.unblock();
        },
        success:function (response) {
            elementToBlock.fadeOut(250);
            jQuery.each(response, function (sectionId, enrlObject) {
                var message = "<strong>" + enrlObject.enrollCount + "</strong> / " + enrlObject.enrollMaximum;
                var title = enrlObject.enrollCount + " enrolled out of " + enrlObject.enrollMaximum;
                if (enrlObject.enrollEstimate) {
                    message += "E";
                    title += " estimated";
                }
                title += " limit. Updated few minutes ago."
                var data = jQuery("<span />").addClass("myplan-enrl-data").attr("title", title).html(message);
                jQuery("#" + sectionId + " .myplan-enrl-data").replaceWith(data);
            });
            elementToBlock.fadeIn(250);
            elementToBlock.unblock();
        }
    });
}


function updateHiddenScript(id, script) {
    jQuery("#" + id).unbind();
    var input = jQuery("input[data-for='" + id + "'][data-role='script']");
    input.removeAttr("script").attr("name", "script").val(script);
    runScriptsForId(id);
}

function switchFetchAction(actionId, toggleId) {
    var script = "jQuery('#' + '" + actionId + "').click(function(e){ toggleSections('" + actionId + "', '" + toggleId + "', 'myplan-section-planned', 'Show all scheduled sections', 'Hide non-selected sections'); });";
    updateHiddenScript(actionId, script);
    jQuery("#" + actionId).text("Hide non-selected sections").removeAttr("data-hidden").data("hidden", false);
}

function toggleSections(actionId, toggleId, showClass, showText, hideText) {
    var group = jQuery("#" + toggleId + " table tbody tr.row").not("." + showClass);
    var action = jQuery("#" + actionId);
    if (action.data("hidden")) {
        group.each(function () {
            var toggle = jQuery(this).find("a[id^='toggle_']");
            if (toggle.data("hidden") || typeof toggle.data("hidden") == "undefined") {
                jQuery(this).show();
            } else {
                jQuery(this).show().next("tr.collapsible").show().next("tr.collapsible").show();
            }
        });
        jQuery(".myplan-quarter-detail .activityInstitutionHeading").show();
        action.text(hideText).data("hidden", false);
    } else {
        group.each(function () {
            var toggle = jQuery(this).find("a[id^='toggle_']");
            if (toggle.data("hidden") || typeof toggle.data("hidden") == "undefined") {
                jQuery(this).hide();
            } else {
                jQuery(this).hide().next("tr.collapsible").hide().next("tr.collapsible").hide();
            }
        });
        jQuery(".myplan-quarter-detail .activityInstitutionHeading").hide();
        action.text(showText).data("hidden", true);
    }
}

function toggleSectionDetails(sectionRow, obj, expandText, collapseText) {
    if (typeof obj.data("hidden") == "undefined") {
        obj.data("hidden", true);
    }
    var collapsibleRow = sectionRow.next("tr.collapsible");
    if (obj.data("hidden")) {
        sectionRow.find("td").first().attr("rowspan", "3");
        sectionRow.find("td").last().attr("rowspan", "3");
        collapsibleRow.show().next("tr.collapsible").show();
        obj.text(collapseText).data("hidden", false);
    } else {
        sectionRow.find("td").first().attr("rowspan", "1");
        sectionRow.find("td").last().attr("rowspan", "1");
        collapsibleRow.hide().next("tr.collapsible").hide();
        obj.text(expandText).data("hidden", true);
    }
}

function toggleRegisteredDetails(sectionRow, obj) {
    var collapsibleRow = sectionRow.next("tr.collapsible");
    if (collapsibleRow.is(":visible")) {
        obj.parents("td").attr("rowspan", "1");
        collapsibleRow.hide();
        obj.find("img.uif-image").toggleClass("expanded");
    } else {
        obj.parents("td").attr("rowspan", "2");
        collapsibleRow.show();
        obj.find("img.uif-image").toggleClass("expanded");
    }
}

function buildHoverText(obj) {
    var message = '';
    var temp = '';
    // condition to check whether section is primary or secondary
    if (obj.data("primary")) {
        // Primary sections
        // condition to check if planned or not planned
        if (obj.data("planned")) {
            var secondarySections = [];
            // Find list of secondary sections associated
            jQuery("div[data-courseid='" + obj.data("courseid") + "'][data-primarysection='" + obj.data("coursesection") + "'][data-planned='true'][data-primary='false']").each(function () {
                secondarySections.push(jQuery(this).data("coursesection"));
            });
            // Build string of secondary sections associated
            if (secondarySections.length > 0) {
                if (secondarySections.length == 1) {
                    temp = " and " + secondarySections.join();
                } else {
                    // commas separated string of secondary sections
                    temp = ", " + secondarySections.slice(0, -1).join(", ") + ", and " + secondarySections[secondarySections.length - 1];
                }
            }
            // Text should give "Delete {primary section} {list of secondary sections if any exist}"
            message = "Delete " + obj.data("coursesection") + temp;
        } else {
            // Text should give "Add {primary section}"
            message = "Add " + obj.data("coursesection");
        }
    } else {
        // Secondary sections
        // condition to check if planned or not planned
        if (obj.data("planned")) {
            // Text should give "Delete {secondary section}"
            message = "Delete " + obj.data("coursesection");
        } else {
            // Text should give "Add {secondary section} and {primary section if not planned}"
            if (!jQuery("div[data-courseid='" + obj.data("courseid") + "'][data-coursesection='" + obj.data("primarysection") + "']").data("planned")) {
                temp = " and " + obj.data("primarysection");
            }
            message = "Add " + obj.data("coursesection") + temp;
        }
    }
    obj.attr("title", message).find("img.uif-image").attr("alt", message);
}


/**
 * Sets up the popup actions if the user clicks outside the popup
 *
 * @param popoverId - Popups id
 * @param element - The popup html object.
 */
function clickOutsidePopOver(popoverId, element) {
    jQuery("body").on("click", function (e) {
        var tempTarget = (e.target) ? e.target : e.srcElement;
        if (jQuery(tempTarget).parents("#" + popoverId).length === 0) {
            element.HidePopOver();
            jQuery("body").off("click");
        }
    });
}

/**
 * Prepares a dialog for submission to the controller.
 *
 * @param additionalFormData - Variables and data sent from the dialog
 * @param e - An object containing data that will be passed to the event handler.
 * @param bDialog - Boolean
 */
function submitPopupForm(additionalFormData, e, bDialog) {
    var target = (e.currentTarget) ? e.currentTarget : e.srcElement;
    var targetText = ( jQuery.trim(jQuery(target).text()) != '') ? jQuery.trim(jQuery(target).text()) : "Error";
    var elementToBlock = (target.nodeName != 'INPUT') ? jQuery(target) : jQuery(target).parent();
    var successCallback = function (htmlContent) {
        var pageId = jQuery("#pageId", htmlContent).val();
        var status = jQuery.trim(jQuery("#requestStatus", htmlContent).text().toLowerCase());
        eval(jQuery("input[data-role='script'][data-for='" + pageId + "']", htmlContent).val().replace("#" + pageId, "body"));
        var data = {};
        data.message = '<img src="/student/ks-myplan/images/pixel.gif" alt="" class="icon"><div class="message"><span /></div>';
        data.cssClass = "myplan-feedback " + status;
        elementToBlock.unblock();
        switch (status) {
            case 'success':
                data.message = data.message.replace("<span />", jQuery("#plan_item_action_response_page", htmlContent).data(kradVariables.VALIDATION_MESSAGES).serverInfo[0]);
                var jsonText = jQuery.trim(jQuery("#jsonEvents", htmlContent).text()).replace(/\n/g,"\\n");
                var json = jQuery.parseJSON(jsonText);
                for (var key in json) {
                    if (json.hasOwnProperty(key)) {
                        eval('jQuery.event.trigger("' + key + '", ' + JSON.stringify(jQuery.extend(json[key], data)) + ');');
                    }
                }
                setUrlHash('modified', 'true');
                break;
            case 'error':
                data.message = data.message.replace("<span />", jQuery("#plan_item_action_response_page", htmlContent).data(kradVariables.VALIDATION_MESSAGES).serverErrors[0]);
                if (bDialog) {
                    var sContent = jQuery("<div />").append(data.message).addClass("myplan-feedback error").css({"background-color":"#fff"});
                    var sHtml = jQuery("<div />").append('<div class="uif-headerField uif-sectionHeaderField"><h3 class="uif-header">' + targetText + '</h3></div>').append(sContent);
                    if (jQuery("body").HasPopOver()) jQuery("body").HidePopOver();
                    openDialog(sHtml.html(), e);
                } else {
                    eval('jQuery.event.trigger("ERROR", ' + JSON.stringify(data) + ');');
                }
                break;
        }
    };
    var blockOptions = {
        message:'<img src="../ks-myplan/images/btnLoader.gif"/>',
        css:{
            width:'100%',
            border:'none',
            backgroundColor:'transparent'
        },
        overlayCSS:{
            backgroundColor:'#fff',
            opacity:0.6,
            padding:'0px 1px',
            margin:'0px -1px'
        }
    };
    ksapAjaxSubmitForm(additionalFormData, successCallback, elementToBlock, "popupForm", blockOptions);
}

// KSAP 0.7.1 and previous deprecated planner scripts

function ksapLoadPlanItems(imageUrl) {	
	var user = jQuery.trim(jQuery('#hidden_new_user_flag').text());
	var retrieveOptions = {};
	var sFocusAtpId = '';
	var aParams = window.location.search.replace('?','').split('&amp;');
	jQuery.each(aParams, function(index, value) {
	    if (value.split('=')[0].indexOf('focusAtpId') >= 0) {
	        sFocusAtpId = decodeURIComponent(value.split('=')[1]);
	    }
	});
	if(sFocusAtpId != ''){
	    retrieveOptions = {viewId:'PlannedCourses-LookupView', focusAtpId:sFocusAtpId};
	} else {
	    retrieveOptions = {viewId:'PlannedCourses-LookupView'};
	}
	myplanRetrieveComponent('planned_mock_detail','planned_courses_detail','search','lookup', retrieveOptions, null,
		{	message: '<p><img src="' + imageUrl +
				'ajaxAuditRunning32.gif" alt="loading..." /></p><p>Please wait while we are fetching your plan...</p>',
			fadeIn : 0,
			fadeOut : 0
		});
}

function ksapInitializePlanItems(pageSize) {
    var popupStyle = {width:'300px', height:'16px'};
    var popupOptions = {tail:{hidden:true}, position:'right', align:'top', close:true};

    jQuery('.myplan-carousel-list li .myplan-term-current.open, .myplan-carousel-list li .myplan-term-future.open').find('.myplan-term-planned .uif-stackedCollectionLayout, .myplan-term-backup .uif-stackedCollectionLayout').each(function(){
    	var atpId = jQuery(this).parents('.myplan-carousel-term').data('atpid');
    	var backup = jQuery(this).parents('.myplan-carousel-term').data('plantype');
    	var size = jQuery(this).parents('.myplan-carousel-term').data('size');
      var jQuickAdd = jQuery('<div />')
                      .addClass('quick-add-cell ks-plan-Bucket-addItem')
                      .html('Add a course to plan')
                      .click(function(e){
              var retrieveData = {
                              action : 'plan' ,
                              viewId : 'PlannedCourse-FormView' ,
                              methodToCall : 'startAddPlannedCourseForm' ,
                              atpId : atpId ,
                              backup : backup ,
                              pageId : 'plan_item_add_page'
                      };
           openPopup('plan_item_add_page', retrieveData, 'plan', popupStyle, popupOptions, e);
      });
      // TODO: consider removing size limit enforcement
      if (size < 8) {
          jQuery(this).append(jQuickAdd).show();
      } else {
          jQuery(this).append(jQuickAdd).show();
    	}
    });

    if (jQuery('#planned_courses_detail_list ul:not(.errorLines) li').length > 0) {
        var iMaxHeight = Math.max.apply(null, jQuery('#planned_courses_detail_list ul:not(.errorLines) li')
        	.map(function() {
        		return jQuery(this).height();
        	}).get(	));
        jQuery('#planned_courses_detail_list ul:not(.errorLines) li').height(iMaxHeight);
        var iStart = 0;
        if (readUrlHash('planView')) {
            iStart = parseFloat(readUrlHash('planView'));
        } else if ( jQuery("div:hidden[id^='atp_start_index']").length > 0 ) {
        	iStart = parseFloat(jQuery("div:hidden[id^='atp_start_index']").text());
        }

        jQuery('#planned_courses_detail_list').jCarouselLite({
            btnNext: '.myplan-carousel-next',
            btnPrev: '.myplan-carousel-prev',
            scroll: pageSize,
            visible: pageSize,
            start: iStart,
            afterEnd: function(a) {
                fnBuildTitle(a);
                var planView = jQuery(a[0]).index();
                if (planView == 0 && a.length < pageSize) {
                	planView = a.length - pageSize;
                }
                setUrlHash('planView', planView);
            },
            initCallback: function(a) { fnBuildTitle(a); jQuery.unblockUI(); }
        });
    }

    truncateField('planned_courses_detail_list', true);
    jQuery('#planned_courses_detail_list')
        .subscribe('PLAN_ITEM_ADDED', function(data){
        	var campusCode = data.courseDetails.campusCode;
        	var courseCode = data.courseDetails.courseCode;
        	var activityCode = data.courseDetails.activityCode;
        	var code;
        	if (campusCode != null)
        		code = campusCode + " " + courseCode;
        	else
        		code = courseCode;
        	if (activityCode != null)
        		code += " " + activityCode;
            fnAddPlanItem(data.atpId, data.planItemType, data.planItemId, code, data.courseDetails.courseTitle, data.courseDetails.credit, data.showAlert, data.termName, data.timeScheduleOpen);
        })
        .subscribe('PLAN_ITEM_DELETED', function(data){
            fnRemovePlanItem(data.atpId, data.planItemType, data.planItemId);
        })
        .subscribe('UPDATE_NEW_TERM_TOTAL_CREDITS', function(data){
            fnUpdateCredits(data.atpId, data.totalCredits, data.cartCredits);
        })
        .subscribe('UPDATE_OLD_TERM_TOTAL_CREDITS', function(data){
            fnUpdateCredits(data.atpId, data.totalCredits, data.cartCredits);
        });
    jQuery('#planned_courses_detail_list').on('PLAN_ITEM_ADDED', function(event, data) {
        fnAddPlanItem(data.atpId, data.planItemType, data.planItemId, data.courseDetails.code, data.courseDetails.courseTitle, data.courseDetails.credit, data.showAlert, data.termName, data.timeScheduleOpen);
    }).on('PLAN_ITEM_DELETED', function(event, data) {
        fnRemovePlanItem(data.atpId, data.planItemType, data.planItemId);
    }).on('PLAN_ITEM_UPDATED', function(event, data) {
        fnUpdatePlanItem(data); // TODO: REMOVE
    }).on('TERM_NOTE_UPDATED', function(event, data) {
        fnUpdateTermNote(data.atpId, data.termNote);
    }).on('UPDATE_NEW_TERM_TOTAL_CREDITS', function(event, data) {
        fnUpdateCredits(data.atpId, data.totalCredits, data.cartCredits);
    }).on('UPDATE_OLD_TERM_TOTAL_CREDITS', function(event, data) {
        fnUpdateCredits(data.atpId, data.totalCredits, data.cartCredits);
    });
}
