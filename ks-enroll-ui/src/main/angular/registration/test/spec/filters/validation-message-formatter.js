'use strict';

describe('Filter: FormatValidationMessage', function() {

    // load the module
    beforeEach(module('regCartApp'));

    var filter,
        VALIDATION_ERROR_TYPE,
        baseCourseId = 'BASE_COURSE_ID';

    // instantiate the filter
    beforeEach(inject(function(formatValidationMessageFilter, _VALIDATION_ERROR_TYPE_) {
        filter = formatValidationMessageFilter;
        VALIDATION_ERROR_TYPE = _VALIDATION_ERROR_TYPE_;
    }));


    function filterWithCourse(errorType, course) {
        if (!course) {
            course = {};
        }

        if (!course.id) {
            course.id = baseCourseId;
        }

        return filterWithKey(errorType, course);
    }

    function filterWithKey(errorType, course) {
        return filter({ messageKey: errorType }, course);
    }


    it('should handle garbage input elegantly', function() {
        expect(filter()).toBe('');
        expect(filter(false)).toBe('');
        expect(filter(true)).toBe('');
        expect(filter(true, 'string course???')).toBe('');
        expect(filterWithCourse(true)).toBe('');
        expect(filter(['nonExistentMessageKey'])).toBe('');
        expect(filter({messageKey: 'nonExistentMessageKey'})).toBe('');
    });

    it('should return a string message straight out', function() {
        expect(filter('test message')).toBe('test message');
    });


    describe('max credits', function() {
        it('should format the message correctly', function() {
            expect(filterWithKey(VALIDATION_ERROR_TYPE.maxCredits)).toBe('Reached maximum credit limit');
        });

        it('should handle a null course', function() {
            expect(filterWithKey(VALIDATION_ERROR_TYPE.maxCredits)).toBe('Reached maximum credit limit');
        });
    });


    describe('time conflict', function() {
        it('should return the correct message for garbage data', function() {
            expect(filterWithKey(VALIDATION_ERROR_TYPE.timeConflict)).toBe('Time conflict');
            expect(filterWithCourse(VALIDATION_ERROR_TYPE.timeConflict, {})).toBe('Time conflict');
            expect(filterWithCourse(VALIDATION_ERROR_TYPE.timeConflict, {conflictingItems: []})).toBe('Time conflict');
        });

        it('should handle a courseCode on the root object', function() {
            var data = {
                messageKey: VALIDATION_ERROR_TYPE.timeConflict,
                courseCode: 'code1',
                id: 'id1'
            };

            // Base case
            expect(filter(data, {})).toBe('Time conflict (<strong>code1</strong>)');

            // Malformed conflictingItems array
            data.conflictingItems = {};
            expect(filter(data, {})).toBe('Time conflict (<strong>code1</strong>)');

            // Existing but empty conflictingItems array
            data.conflictingItems = [];
            expect(filter(data, {})).toBe('Time conflict (<strong>code1</strong>)');

            // Populated conflicting items array
            data.conflictingItems.push({courseCode: 'code2'});
            expect(filter(data, {})).toBe('Time conflict (<strong>code1</strong>, <strong>code2</strong>)');

            // Duplicate item in conflictingItems array
            data.conflictingItems.push({courseCode: 'code1', id: 'id1'});
            expect(filter(data, {})).toBe('Time conflict (<strong>code1</strong>, <strong>code2</strong>)');
        });

        it('should not show a conflicting courseCode that matches the current course', function() {
            var data = {
                    messageKey: VALIDATION_ERROR_TYPE.timeConflict,
                    courseCode: 'code1',
                    id: baseCourseId
                },
                course = {
                    courseCode: 'code1',
                    id: baseCourseId
                };

            expect(filter(data, course)).toBe('Time conflict');
        });

        it('should handle an array of conflictingItems', function() {
            var data = {
                messageKey: VALIDATION_ERROR_TYPE.timeConflict,
                conflictingItems: [
                    { courseCode: 'code1', id: 'id1' }
                ]
            };

            // Single item base case
            expect(filter(data, {})).toBe('Time conflict (<strong>code1</strong>)');

            // Multiple items
            data.conflictingItems.push({courseCode: 'code2', id: 'id2'});
            expect(filter(data, {})).toBe('Time conflict (<strong>code1</strong>, <strong>code2</strong>)');

            // Duplicate item in conflictingItems
            data.conflictingItems.push({courseCode: 'code1', id: 'id1'});
            expect(filter(data, {})).toBe('Time conflict (<strong>code1</strong>, <strong>code2</strong>)');
        });

        it('should not show the course code in a conflictingItem that matches the current course', function() {
            var data = {
                    messageKey: VALIDATION_ERROR_TYPE.timeConflict,
                    conflictingItems: [
                        { courseCode: 'code1', id: 'id1' }
                    ]
                },
                course = {
                    courseCode: 'BASE_COURSE_CODE',
                    id: baseCourseId
                };

            // Single item base case
            expect(filter(data, course)).toBe('Time conflict (<strong>code1</strong>)');

            // Multiple items
            data.conflictingItems.push({courseCode: 'BASE_COURSE_CODE', id: baseCourseId});
            expect(filter(data, course)).toBe('Time conflict (<strong>code1</strong>)');
        });
    });

});
