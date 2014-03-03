function togglePossibleSchedule(calendarObj, targetObj, index, uniqueId, hasTBA) {
    var sourceObject = jQuery.extend(
        targetObj.data("events"),
        {"className": ["schedulePossible__event", "schedulePossible--" + (index % 5)]}
    );

    for (var i = 0; i < sourceObject.events.length; i++) {
        sourceObject.events[i].title = (index + 1).toString();
    }

    var selected = calendarObj.fullCalendar('clientEvents', uniqueId).length > 0;

    if (selected) {
        calendarObj.fullCalendar('removeEventSource', sourceObject);
        targetObj.find(".schedulePossible__save").hide();
        if (hasTBA) {
            jQuery("#possible-tba-" + uniqueId).hide();
        }
    } else {
        calendarObj.fullCalendar('addEventSource', sourceObject);
        targetObj.find(".schedulePossible__save").show();
        if (hasTBA) {
            jQuery("#possible-tba-" + uniqueId).show();
        }
    }

    var tbaCount = jQuery(".schedulePossible__tba .schedulePossible__tbaItem").filter(function() {
        return jQuery(this).css('display') != 'none';
    }).length;

    if (tbaCount > 0) {
        jQuery(".schedulePossible__tba").show();
    } else {
        jQuery(".schedulePossible__tba").hide();
    }
}

function toggleSaveSchedule(uniqueId, methodToCall, event) {
    event.stopPropagation();
    // var target = (e.currentTarget) ? e.currentTarget : e.srcElement;
    jQuery("#kualiForm").ajaxSubmit({
        data : {
            methodToCall: methodToCall,
            uniqueId: uniqueId
        },
        dataType : 'json',
        success : function(response, textStatus, jqXHR) {
            jQuery.event.trigger("SAVED_SCHEDULE_" + methodToCall.toUpperCase(), response);
        },
        error : function(jqXHR, textStatus, errorThrown) {
            if (textStatus == "parsererror")
                textStatus = "JSON Parse Error";
            showGrowl(errorThrown, jqXHR.status + " " + textStatus);
        }
    });
}

function hidePossibleScheduleEvents(viewArr, calendarObj) {
    for (var i = 0; i < viewArr.length; i++) {
        var sourceObject = jQuery(viewArr[i]).find(".schedulePossible__option").data("events");
        var selected = calendarObj.fullCalendar('clientEvents', sourceObject.uniqueId).length > 0;
        if (selected) {
            calendarObj.fullCalendar('removeEventSource', sourceObject);
        }
    }
}

function addReservedScheduleOption(methodToCall, e) {
    var form = jQuery("#popupForm");
    form.ajaxSubmit({
        data: ksapAdditionalFormData({
            methodToCall : methodToCall
        }),
        dataType: 'json',
        success: function(response, textStatus, jqXHR){
            var container = jQuery("div.scheduleReserved__container");
            var template = jQuery("#sb-reserved-item-template").wrap('<div/>').parent().html();
            template = template.replace(/id="sb-reserved-item-template"/gi, "");
            template = template.replace(/__KSAP_ID__/gi, response.id);
            template = template.replace(/__KSAP_DAYSTIMES__/gi, response.daysTimes);
            var item = jQuery(template);
            container.append(item);
            item.attr("data-events", JSON.stringify(response));
            item.show();
            fnCloseAllPopups();
            jQuery.event.trigger("REFRESH_POSSIBLE_SCHEDULES");
        },
        error: function(jqXHR, textStatus, errorThrown) {
            if (textStatus == "parsererror")
                textStatus = "Parse Error in response";
            showGrowl(errorThrown, jqXHR.status + " " + textStatus);
            fnCloseAllPopups();
        }
    });
}

function removeReservedScheduleOption(uniqueId, e) {
    var form = jQuery("#kualiForm");
    form.ajaxSubmit({
        data: ksapAdditionalFormData({
            methodToCall : "remove",
            uniqueId : uniqueId
        }),
        dataType: 'json',
        success: function(response, textStatus, jqXHR) {
            var target = (e.currentTarget) ? jQuery(e.currentTarget) : jQuery(e.srcElement);
            target.parents(".scheduleReserved__item").remove();
            jQuery.event.trigger("REFRESH_POSSIBLE_SCHEDULES");
        },
        error: function(jqXHR, textStatus, errorThrown) {
            if (textStatus == "parsererror")
                textStatus = "JSON Parse Error";
            showGrowl(errorThrown, jqXHR.status + " " + textStatus);
        }
    });
}

function appendReservedScheduleOptions(calendarObj, parentSelector, itemSelector) {
    var items = jQuery(parentSelector).find(itemSelector);
    for (var i = 0; i < items.length; i++) {
        var sourceObject = jQuery.extend(
            jQuery(items[i]).data("events"),
            {"className": ["scheduleReserved__event"]});
        calendarObj.fullCalendar('addEventSource', sourceObject);
    }
}

