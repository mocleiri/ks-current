'use strict';

var cartServiceModule = angular.module('regCartApp');

cartServiceModule.controller('ScheduleCtrl', ['$scope', '$modal', 'ScheduleService',
    function ($scope, $modal, ScheduleService) {
        ScheduleService.populateSchedule('admin', $scope.termId);
        $scope.schedules = ScheduleService.getStudentSchedule();
        $scope.registeredCredits = ScheduleService.getRegisteredCredits;
        $scope.registeredCourseCount = ScheduleService.getRegisteredCourseCount;

        $scope.dropRegGroup = function (index) {
            console.log('Test !!! ' + index);
            $scope.schedules[0].courseOfferings.splice(index, 1);
        };

        $scope.openDropConfirmation = function (index, course) {
            console.log('Open drop confirmation');
            $modal.open({
                backdrop:'static',
                templateUrl:'partials/dropCourse.html',
                resolve:{
                     index:function () {
                         return index;
                     },
                     course:function () {
                        return course;
                    }
                },
                controller:['$rootScope', '$scope', 'index', 'course', function ($rootScope, $scope, index, course) {
                    console.log('in controller');
                    console.log($scope);
                    console.log(index);
                    console.log(course);
                    $scope.index = index;
                    $scope.course = course;
                    $scope.dismiss = function () {
                        console.log('dismiss');
                        $scope.$close(true);
                    };
                    $scope.dropRegistrationGroup = function () {
                        console.log('Drop registration group');
                        dropRegistrationGroup($scope.index, $scope.course.courseCode, $scope.course.regGroupCode, $scope.course.credits, $scope.course.masterLprId);
                        $scope.$close(true);
                    };
                }]
            });
        }

        function dropRegistrationGroup(index, courseCode, regGroupCode, credits, masterLprId) {
            console.log('Calling service to drop registration group');
            ScheduleService.dropRegistrationGroup().query({
                masterLprId:masterLprId
            }, function (response) {
                console.log('response: ' + JSON.stringify(response));
                $scope.schedules[0].courseOfferings.splice(index, 1);
                ScheduleService.setRegisteredCredits(parseFloat(ScheduleService.getRegisteredCredits()) - parseFloat(credits));
                $scope.userMessage = {txt:'Dropped ' + courseCode + ' (' + regGroupCode + ') successfully', type:'success'};
            });
        }


            }]);