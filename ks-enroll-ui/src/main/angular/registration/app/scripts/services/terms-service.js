'use strict';

angular.module('regCartApp')
    .service('TermsService', ['$resource', 'APP_URL', function TermsService($resource, APP_URL) {

        var scheduleOfClassesService=APP_URL+'ScheduleOfClassesClientService';
        var termsMethod=scheduleOfClassesService+'/terms';
        var termId = 'kuali.atp.2012Fall';   // default value

        this.getTermId = function () {
            return termId;
        };

        this.setTermId = function (value) {
            termId = value;
        };

        this.getTermsFromServer = function () {
            return $resource(termsMethod, {}, {
                query: { method: 'GET', cache: true, isArray: true }
            });
        };

        this.getTermNameForTermId = function(terms, termId){
            var retTermName;
            angular.forEach(terms, function (term) {
                if (term.termId === termId) {
                    retTermName = term.termName;
                }
            });
            return retTermName;
        };

    }]);
//angular.module('regCartApp')
//    .service('TermsService', function TermsService($resource) {
//        this.getTerms = function () {
//            return $resource('json/static-terms.json', {}, {
//                query:{method:'GET', cache:false, isArray:true}
//            });
//        };
//
//    });
