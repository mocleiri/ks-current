'use strict';

angular.module('regCartApp')
    .service('ScheduleService', ['$resource', 'APP_URL', function ScheduleService($resource, APP_URL) {

    var registeredCredits = 0;
    var registeredCourseCount = 0;
    var studentSchedule;

    this.getRegisteredCredits = function (){
       return registeredCredits;
    };

    this.setRegisteredCredits = function (value){
       registeredCredits = value;
    };

    this.getRegisteredCourseCount = function() {
        return registeredCourseCount;
    };

    this.setRegisteredCourseCount = function(value) {
        registeredCourseCount = value;
    };

    this.getStudentSchedule = function(){
        return studentSchedule;
    };

    this.setStudentSchedule = function(value) {
        studentSchedule = value;
    };

    this.getScheduleFromServer = function () {
        return $resource(APP_URL + 'CourseRegistrationClientService/personschedule', {}, {
            query:{method:'GET', cache:false, isArray:true}
        });
    };
    this.updateSchedule = function () {
        return $resource(APP_URL + 'CourseRegistrationClientService/updateScheduleItem', {}, {
            query:{method:'GET', cache:false, isArray:true}
        });
    };

    this.populateSchedule = function(userIdInput, termIdInput){
        this.setStudentSchedule(this.getScheduleFromServer().query({ userId:userIdInput, termId:termIdInput }, function (result) {
            console.log('called rest service to get schedule data');
            var creditCount = 0;
            var courses = 0;
            angular.forEach(result, function (schedule) {
                angular.forEach(schedule.courseOfferings, function (course) {
                    creditCount += parseFloat(course.credits);
                    courses++;
                });
            });

            registeredCredits = creditCount;
            registeredCourseCount = courses;
        }));
    };


}]);