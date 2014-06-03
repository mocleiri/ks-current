'use strict';

angular.module('regCartApp')
    .service('TermsService', ['ServiceUtilities', 'URLS', function (ServiceUtilities, URLS) {

        var termId = 'kuali.atp.2012Fall';   // default value

        this.getTermId = function () {
            return termId;
        };

        this.setTermId = function (value) {
            termId = value;
        };

        this.getTermsFromServer = function () {
            return ServiceUtilities.getArray(URLS.scheduleOfClasses+'/terms');
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