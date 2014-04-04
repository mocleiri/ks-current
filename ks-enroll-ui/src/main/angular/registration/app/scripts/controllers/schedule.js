'use strict';

var cartServiceModule = angular.module('regCartApp');

cartServiceModule.controller('ScheduleCtrl', ['$scope', '$modal', 'ScheduleService', 'GlobalVarsService',
    function ($scope, $modal, ScheduleService, GlobalVarsService) {


        $scope.schedules = GlobalVarsService.getSchedule;
        $scope.registeredCredits = GlobalVarsService.getRegisteredCredits;
        $scope.registeredCourseCount = GlobalVarsService.getRegisteredCourseCount;
        $scope.waitlistedCredits = GlobalVarsService.getWaitlistedCredits;
        $scope.waitlistedCourseCount = GlobalVarsService.getWaitlistedCourseCount;

        if($scope.termId){
        ScheduleService.getScheduleFromServer().query({termId: $scope.termId }, function (result) {
            console.log('called rest service to get schedule data - in schedule.js');

            GlobalVarsService.updateScheduleCounts(result);
        });
        }

        $scope.openDropConfirmation = function (index, course) {
            console.log('Open drop confirmation');
            course.dropping = true;
            $scope.index = index;
            $scope.course = course;
        };

        $scope.cancelDropConfirmation = function (course) {
            course.dropping = false;
        };

        $scope.dropRegistrationGroup = function (index, course) {
            console.log('Open drop confirmation');
            ScheduleService.dropRegistrationGroup().query({
                masterLprId: course.masterLprId
            }, function () {
                course.dropping = false;
                $scope.schedules()[0].registeredCourseOfferings.splice(index, 1);
                GlobalVarsService.updateScheduleCounts($scope.schedules());
                //ScheduleService.setRegisteredCredits(parseFloat(ScheduleService.getRegisteredCredits()) - parseFloat(course.credits));
                $scope.userMessage = {txt:course.courseCode + ' dropped successfully', type:'success'};
            }, function (error) {
                //course.dropping = false;
                $scope.userMessage = {txt: error.data, type: 'error'};
            });
        };

        $scope.editScheduleItem = function (course) {
            $scope.newCredits = course.credits;
            $scope.newGrading = course.gradingOptionId;
            course.editing = true;
        };

        $scope.cancelEditScheduleItem = function (course) {
            course.editing = false;
        };

        $scope.updateScheduleItem = function (course, newCredits, newGrading) {
            console.log('Updating:');
            console.log(newCredits);
            console.log(newGrading);
            ScheduleService.updateScheduleItem().query({
                courseCode: course.courseCode,
                regGroupCode: course.regGroupCode,
                masterLprId: course.masterLprId,
                credits: newCredits,
                gradingOptionId: newGrading
            }, function (scheduleItemResult) {
                console.log(scheduleItemResult);
                course.credits = scheduleItemResult.credits;
                course.gradingOptionId = scheduleItemResult.gradingOptionId;
                GlobalVarsService.updateScheduleCounts($scope.schedules());
                course.editing = false;
                course.statusMessage = {txt: 'Changes saved successfully', type: 'success'};
                course.edited = true;
            }, function (error) {
                //course.editing = false;
                $scope.userMessage = {txt: error.data, type: 'error'};
            });
        };

        $scope.removeStatusMessage = function (course){
            course.statusMessage = null;
        }

        $scope.removeUserMessage = function() {
            $scope.userMessage.txt = null;
            $scope.userMessage.linkText = null;
        }

        $scope.showBadge = function (course) {
            return course.gradingOptions[course.gradingOptionId] != 'Letter';
        };

    }]);