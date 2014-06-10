'use strict';

angular.module('regCartApp')

    // Server service URLs
    .constant('URLS', {
        scheduleOfClasses: 'ScheduleOfClassesClientService',
        courseRegistration: 'CourseRegistrationClientService',
        courseRegistrationCart: 'CourseRegistrationCartClientService',
        developmentLogin: 'DevelopmentLoginClientService'
    })


    // State identifiers
    .constant('STATE', (function() {
        // Assigned as a var so they can be used in the groupings
        var lprStates = {
            failed:                         'kuali.lpr.trans.state.failed',
            processing:                     'kuali.lpr.trans.state.processing',
            succeeded:                      'kuali.lpr.trans.state.succeeded',

            item: {
                failed:                     'kuali.lpr.trans.item.state.failed',
                processing:                 'kuali.lpr.trans.item.state.processing',
                succeeded:                  'kuali.lpr.trans.item.state.succeeded',
                waitlist:                   'kuali.lpr.trans.item.state.waitlist',
                waitlistActionAvailable:    'kuali.lpr.trans.item.state.waitlistActionAvailable'
            }
        };

        return {
            // LPR transaction states
            lpr: lprStates,


            // Aggregated states - used to map states to statuses in GlobalVarsService
            action: [
                lprStates.item.waitlistActionAvailable
            ],
            error: [
                lprStates.failed,
                lprStates.item.failed
            ],
            processing: [
                lprStates.processing,
                lprStates.item.processing
            ],
            success: [
                lprStates.succeeded,
                lprStates.item.succeeded
            ],
            waitlist: [
                lprStates.item.waitlist
            ]
        };
    })())


    // Status identifiers are used to indicate status within the client.
    // Often, they are a client-friendly translation of the longer STATE identifiers.
    .constant('STATUS', {
        action:     'action',
        editing:    'editing',
        error:      'error',
        new:        'new',
        processing: 'processing',
        success:    'success',
        waitlist:   'waitlist'
    })


    // Grading option identifiers
    .constant('GRADING_OPTION', {
        audit:      'kuali.resultComponent.grade.audit',
        letter:     'kuali.resultComponent.grade.letter',
        passFail:   'kuali.resultComponent.grade.passFail'
    })


    // Action link identifiers that get returned from the server
    .constant('ACTION_LINK', {
        removeItemFromCart: 'removeItemFromCart',
        undoDeleteCourse:   'undoDeleteCourse'
    })


    // Validation error type identifiers that get returned from the server
    .constant('VALIDATION_ERROR_TYPE', {
        maxCredits: 'kuali.lpr.trans.message.credit.load.exceeded',
        waitListAvailable: 'kuali.lpr.trans.message.waitlist.available',
        timeConflict: 'validation.timeConflict' // Currently a stub. Needs to be updated to match what the server returns
    })


    // Validation error type identifiers that get returned from the server
    .constant('VALIDATION_ERROR_TYPE', { // These currently are stubs, they will need to be updated to match what the server returns
        maxCredits: 'ks.maxConflict',
        timeConflict: 'ks.timeConflict'
    })
;
