"use strict";angular.module("regCartApp",["configuration","ngAnimate","ngCookies","ngResource","ngSanitize","ngTouch","ui.router","ui.bootstrap"]).config(["$stateProvider","$urlRouterProvider","$httpProvider",function(a,b,c){b.otherwise("/responsive/cart"),a.state("root.responsive",{templateUrl:"partials/responsive/responsive.html"}).state("root.responsive.schedule",{url:"/responsive/schedule",views:{mycart:{templateUrl:"partials/cart.html",controller:"CartCtrl"},schedule:{templateUrl:"partials/responsive/schedule.html",controller:"ScheduleCtrl"}}}).state("root.responsive.cart",{url:"/responsive/cart",views:{mycart:{templateUrl:"partials/cart.html",controller:"CartCtrl"},schedule:{templateUrl:"partials/responsive/schedule.html",controller:"ScheduleCtrl"}}}).state("root",{templateUrl:"partials/main.html",controller:"MainCtrl"}).state("root.additionalOptions",{url:"/options",templateUrl:"partials/additionalOptions.html"}),c.interceptors.push("loginInterceptor")}]),angular.module("configuration",[]).value("APP_URL","ks-with-rice-bundled-dev/services/"),angular.module("regCartApp").controller("MainCtrl",["$scope","$rootScope","$location","$state","TermsService","ScheduleService","GlobalVarsService","APP_URL","LoginService",function(a,b,c,d,e,f,g,h,i){console.log("In Main Controller"),a.appUrl=h.replace("/services/","/"),a.$watch("termId",function(b){b&&(a.termName=e.getTermNameForTermId(a.terms,b),f.getScheduleFromServer().query({termId:b},function(b){console.log("called rest service to get schedule data - in main.js"),g.updateScheduleCounts(b),a.cartCredits=g.getCartCredits,a.cartCourseCount=g.getCartCourseCount,a.registeredCredits=g.getRegisteredCredits,a.registeredCourseCount=g.getRegisteredCourseCount,a.waitlistedCredits=g.getWaitlistedCredits,a.waitlistedCourseCount=g.getWaitlistedCourseCount,a.showWaitlistedSection=g.getShowWaitlistedSection,a.userId=g.getUserId}))}),a.terms=e.getTermsFromServer().query({termCode:null,active:!0},function(b){a.termId="kuali.atp.2012Fall",e.setTermId(a.termId),a.termName=e.getTermNameForTermId(b,a.termId)}),a.logout=function(){i.logout().query({},function(){console.log("Logging out"),location.reload()})},a.goToPage=function(a){console.log("Navigating to page: "+a),c.url(a)},a.$parent.uiState=d.current.name,a.$on("$stateChangeStart",function(b,c){a.$parent.uiState=c.name})}]),angular.module("regCartApp").controller("CartCtrl",["$scope","$modal","CartService","ScheduleService","GlobalVarsService","$timeout",function(a,b,c,d,e,f){function g(b){c.getCart().query({termId:b},function(b){a.cart=b;for(var c,d=[],f=!1,g=0;g<a.cart.items.length;g++){var h=a.cart.items[g];if("processing"===e.getCorrespondingStatusFromState(h.state)){h.status="processing";var i=angular.copy(h);a.cartResults.items.push(i),a.cartResults.state="kuali.lpr.trans.state.processing",a.cartResults.status="processing",f=!0,c=h.cartId}else d.push(h)}a.cart.items=d,f&&k(c)})}function h(d,e,f,g,h,i,j){c.addCourseToCart().query({cartId:d,courseCode:e,termId:f,regGroupCode:g,regGroupId:h,gradingOptionId:i,credits:j},function(b){console.log("Searched for course: "+a.courseCode+", Term: "+a.termId),a.userMessage={txt:"Course Added to Cart",type:"success"},a.courseCode="",a.regCode="",a.cart.items.unshift(b)},function(c){console.log("CartId:",d),404===c.status?a.userMessage={txt:c.data,type:"error"}:400===c.status?(console.log("CartId: ",d),b.open({backdrop:"static",templateUrl:"partials/additionalOptions.html",resolve:{item:function(){return c.data},cartId:function(){return d}},controller:["$rootScope","$scope","item","cartId",function(a,b,c,d){console.log("Controller for modal... Item: ",c),b.newCartItem=c,b.newCartItem.credits=b.newCartItem.newCredits=b.newCartItem.creditOptions[0],b.newCartItem.grading=b.newCartItem.newGrading="kuali.resultComponent.grade.letter",b.newCartItem.editing=!0,b.dismissAdditionalOptions=function(){console.log("Dismissing credits and grading"),b.$close(!0)},b.saveAdditionalOptions=function(c){c.editing=!1,console.log("Save credits and grading for cartId:",d),a.$broadcast("addCourseToCart",d,b.newCartItem.courseCode,b.newCartItem.termId,b.newCartItem.regGroupCode,b.newCartItem.regGroupId,b.newCartItem.newGrading,b.newCartItem.newCredits),b.$close(!0)}}]})):(console.log("Error with adding course",c.data.consoleMessage),a.userMessage={txt:c.data.genericMessage,type:c.data.type,detail:c.data.detailedMessage})})}function i(){if(!a.cart)return 0;for(var b=0,c=0;c<a.cart.items.length;c++)b+=Number(a.cart.items[c].credits);return b}a.oneAtATime=!1,a.isCollapsed=!0;var j=!1;a.cartResults={items:[]},a.$watch("termId",function(b){console.log("term id has changed"),a.cartResults.items.splice(0,a.cartResults.items.length),a.userMessage&&a.userMessage.txt&&a.removeUserMessage(),b&&(j=!0,g(b))}),a.$watchCollection("cart.items",function(a){a&&(e.setCartCourseCount(a.length),e.setCartCredits(i()))}),a.getStatusMessageFromStatus=function(a){var b="";return"success"===a&&(b=" - Success!"),("error"===a||"action"===a)&&(b=" - Failed!"),b},a.addRegGroupToCart=function(){a.courseCode=a.courseCode.toUpperCase(),h(a.cart.cartId,a.courseCode,a.termId,a.regCode,null,null,null,null)},a.$on("addCourseToCart",function(a,b,c,d,e,f,g,i){console.log("Received event addCourseToCart ",a),h(b,c,d,e,f,g,i)}),a.cancelNewCartItem=function(){a.newCartItem=null,a.showNew=!1},a.deleteCartItem=function(b){var d,e=a.cart.items[b],f=e.actionLinks;angular.forEach(f,function(a){"removeItemFromCart"===a.action&&(d=a.uri)}),c.removeItemFromCart(d).query({},function(c){a.cart.items.splice(b,1);var d;angular.forEach(c.actionLinks,function(a){"undoDeleteCourse"===a.action&&(d=a.uri)}),a.userMessage={txt:e.courseCode+"("+e.regGroupCode+") has been successfully removed from your cart.",actionLink:d,linkText:"Undo",type:"success"},a.userActionSuccessful=!0})},a.invokeActionLink=function(b){a.userActionSuccessful=!1,c.invokeActionLink(b).query({},function(b){a.cart.items.unshift(b),a.userMessage={txt:""}})},a.editCartItem=function(a){a.newCredits=a.credits,a.newGrading=a.grading,a.status="editing",a.editing=!0},a.updateCartItem=function(b){console.log("Updating cart item. Grading: "+b.newGrading+", credits: "+b.newCredits),c.updateCartItem().query({cartId:a.cart.cartId,cartItemId:b.cartItemId,credits:b.newCredits,gradingOptionId:b.newGrading},function(c){console.log("old: "+b.credits+" To: "+c.credits),console.log("old: "+b.grading+" To: "+c.grading),b.credits=c.credits,b.grading=c.grading,b.status="",b.editing=!1,b.actionLinks=c.actionLinks,a.creditTotal=i(),b.alertMessage={txt:"Changes saved successfully",type:"success"}})},a.addCartItemToWaitlist=function(a){console.log("Adding cart item to waitlist... "),d.registerForRegistrationGroup().query({courseCode:a.courseCode,regGroupId:a.regGroupId,gradingOption:a.grading,credits:a.credits,allowWaitlist:!0},function(b){a.state="kuali.lpr.trans.item.state.processing",a.status="processing",a.cartItemId=b.registrationResponseItems[0].registrationRequestItemId,f(function(){},250),console.log("Just waited 250, now start the polling"),k(b.registrationRequestId)})},a.removeAlertMessage=function(a){a.alertMessage=null},a.removeUserMessage=function(){a.userMessage.txt=null,a.userMessage.linkText=null},a.register=function(){c.submitCart().query({cartId:a.cart.cartId},function(b){a.userMessage={txt:""},console.log("Submitted cart. RegReqId["+b.registrationRequestId+"]"),a.cartResults=angular.copy(a.cart),a.cart.items.splice(0,a.cart.items.length),a.showConfirmation=!1,a.cartResults.state="kuali.lpr.trans.state.processing",a.cartResults.status="processing",a.creditTotal=0,angular.forEach(a.cartResults.items,function(a){a.state="kuali.lpr.trans.item.state.processing",a.status="processing"}),f(function(){},250),console.log("Just waited 250, now start the polling"),k(b.registrationRequestId)})};var k=function(b){a.pollingCart=!1,f(function(){c.getRegistrationStatus().query({regReqId:b},function(c){a.cart.state=c.state,angular.forEach(c.responseItemResults,function(b){angular.forEach(a.cartResults.items,function(c){c.cartItemId===b.registrationRequestItemId&&(c.state=b.state,c.type=b.type,c.status=e.getCorrespondingStatusFromState(b.state),c.statusMessage=b.message),"kuali.lpr.trans.item.state.processing"===b.state&&(a.pollingCart=!0)})}),a.pollingCart?(console.log("Continue polling"),k(b)):(console.log("Stop polling"),a.cart.status="",a.cartResults.state="kuali.lpr.trans.state.succeeded",a.cartResults.successCount=0,a.cartResults.waitlistCount=0,a.cartResults.errorCount=0,angular.forEach(a.cartResults.items,function(b){switch(b.status){case"success":a.cartResults.successCount++;break;case"waitlist":a.cartResults.waitlistCount++,b.statusMessage=e.getCorrespondingMessageFromStatus(b.status);break;case"error":a.cartResults.errorCount++;break;case"action":a.cartResults.errorCount++}}),d.getScheduleFromServer().query({termId:a.termId},function(b){console.log("called rest service to get schedule data - in main.js"),e.updateScheduleCounts(b),a.registeredCredits=e.getRegisteredCredits,a.registeredCourseCount=e.getRegisteredCourseCount}))})},1e3)};a.removeCartResultItem=function(b){a.cartResults.items.splice(b,1)},a.$watchCollection("cart.items",function(){a.creditTotal=i()}),a.showBadge=function(a){return"Letter"!==a.gradingOptions[a.grading]},a.editing=function(a){return"editing"===a.status}}]);var cartServiceModule=angular.module("regCartApp");cartServiceModule.controller("ScheduleCtrl",["$scope","$modal","ScheduleService","GlobalVarsService","$timeout",function(a,b,c,d,e){a.getSchedules=d.getSchedule,a.registeredCredits=d.getRegisteredCredits,a.registeredCourseCount=d.getRegisteredCourseCount,a.waitlistedCredits=d.getWaitlistedCredits,a.waitlistedCourseCount=d.getWaitlistedCourseCount,a.numberOfDroppedWailistedCourses=0,a.userId=d.getUserId,a.$watch("termId",function(b){console.log("term id has changed: "+b),a.userMessage&&a.userMessage.txt&&a.removeUserMessage(),a.waitlistUserMessage&&a.waitlistUserMessage.txt&&a.removeWaitlistUserMessage(),c.getScheduleFromServer().query({termId:b},function(a){console.log("called rest service to get schedule data - in schedule.js"),d.updateScheduleCounts(a)})}),a.openDropConfirmation=function(b,c){console.log("Open drop confirmation"),c.dropping=!0,a.index=b,a.course=c},a.cancelDropConfirmation=function(a){a.dropping=!1},a.dropRegistrationGroup=function(b,d){console.log("Open drop confirmation for registered course"),c.dropRegistrationGroup().query({masterLprId:d.masterLprId},function(a){d.dropping=!1,d.dropProcessing=!0,f(a.registrationRequestId,d)},function(b){a.userMessage={txt:b.data,type:"error"}})},a.dropFromWaitlist=function(b,d){console.log("Open drop confirmation for waitlist course"),c.dropFromWaitlist().query({masterLprId:d.masterLprId},function(b){a.numberOfDroppedWailistedCourses=a.numberOfDroppedWailistedCourses+1,a.showWaitlistMessages=!0,d.dropping=!1,d.dropProcessing=!0,f(b.registrationRequestId,d)},function(a){d.statusMessage={txt:a.data,type:"error"}})};var f=function(a,b){console.log("start polling for course to be dropped from schedule"),b.statusMessage={txt:"<strong>"+b.courseCode+" ("+b.regGroupCode+")</strong> drop processing",type:"processing"},e(function(){c.getRegistrationStatus().query({regReqId:a},function(c){var e,g=d.getCorrespondingStatusFromState(c.state);switch(g){case"new":case"processing":console.log("continue polling"),f(a,b);break;case"success":console.log("stop polling - success"),b.dropped=!0,b.dropProcessing=!1,b.waitlisted?(d.setWaitlistedCredits(parseFloat(d.getWaitlistedCredits())-parseFloat(b.credits)),d.setWaitlistedCourseCount(parseInt(d.getWaitlistedCourseCount())-1),e="Removed from waitlist for <strong>"+b.courseCode+" ("+b.regGroupCode+")</strong> successfully"):(d.setRegisteredCredits(parseFloat(d.getRegisteredCredits())-parseFloat(b.credits)),d.setRegisteredCourseCount(parseInt(d.getRegisteredCourseCount())-1),e="<strong>"+b.courseCode+" ("+b.regGroupCode+")</strong> dropped successfully"),b.statusMessage={txt:e,type:"success"};break;case"error":console.log("stop polling - error"),b.dropProcessing=!1,e=c.responseItemResults[0].message,b.statusMessage={txt:e,type:"error"}}})},1e3)};a.editScheduleItem=function(a){a.newCredits=a.credits,a.newGrading=a.gradingOptionId,a.editing=!0},a.updateScheduleItem=function(b){console.log("Updating registered course:"),console.log(b.newCredits),console.log(b.newGrading),c.updateScheduleItem().query({courseCode:b.courseCode,regGroupCode:b.regGroupCode,masterLprId:b.masterLprId,termId:a.termId,credits:b.newCredits,gradingOptionId:b.newGrading},function(a){console.log(a),d.setRegisteredCredits(parseFloat(d.getRegisteredCredits())-parseFloat(b.credits)+parseFloat(a.credits)),b.credits=a.credits,b.gradingOptionId=a.gradingOptionId,b.editing=!1,b.statusMessage={txt:"Changes saved successfully",type:"success"}},function(a){b.statusMessage={txt:a.data,type:"error"}})},a.updateWaitlistItem=function(b){console.log("Updating waitlisted course:"),console.log(b.newCredits),console.log(b.newGrading),c.updateWaitlistItem().query({courseCode:b.courseCode,regGroupCode:b.regGroupCode,masterLprId:b.masterLprId,termId:a.termId,credits:b.newCredits,gradingOptionId:b.newGrading},function(a){console.log(a),d.setRegisteredCredits(parseFloat(d.getRegisteredCredits())-parseFloat(b.credits)+parseFloat(a.credits)),b.credits=a.credits,b.gradingOptionId=a.gradingOptionId,b.editing=!1,b.statusMessage={txt:"Changes saved successfully",type:"success"}},function(a){b.statusMessage={txt:a.data,type:"error"}})},a.removeStatusMessage=function(a){a.statusMessage=null},a.removeUserMessage=function(){a.userMessage.txt=null,a.userMessage.linkText=null},a.removeWaitlistStatusMessage=function(b){b.statusMessage=null,a.numberOfDroppedWailistedCourses=a.numberOfDroppedWailistedCourses-1,0===a.numberOfDroppedWailistedCourses&&(a.showWaitlistMessages=!1)},a.showBadge=function(a){return"Letter"!==a.gradingOptions[a.gradingOptionId]}}]),angular.module("regCartApp").service("CartService",["$resource","APP_URL",function(a,b){this.getCart=function(){return a(b+"CourseRegistrationCartClientService/searchForCart",{},{query:{method:"GET",cache:!1,isArray:!1}})},this.getGradingOptions=function(){return a(b+"CourseRegistrationCartClientService/getStudentRegistrationOptions",{},{query:{method:"GET",cache:!1,isArray:!1}})},this.addCourseToCart=function(){return a(b+"CourseRegistrationCartClientService/addCourseToCart",{},{query:{headers:{"Content-Type":"application/x-www-form-urlencoded; charset=UTF-8"},method:"POST",cache:!1,isArray:!1,transformRequest:function(a){var b=[];for(var c in a)a[c]&&b.push(encodeURIComponent(c)+"="+encodeURIComponent(a[c]));return b.join("&")}}})},this.removeItemFromCart=function(c){return a(b+c,{},{query:{method:"DELETE",cache:!1,isArray:!1}})},this.invokeActionLink=function(c){return a(b+c,{},{query:{method:"GET",cache:!1,isArray:!1}})},this.updateCartItem=function(){return a(b+"CourseRegistrationCartClientService/updateCartItem",{},{query:{headers:{"Content-Type":"application/x-www-form-urlencoded; charset=UTF-8"},method:"PUT",cache:!1,isArray:!1,transformRequest:function(a){var b=[];for(var c in a)a[c]&&b.push(encodeURIComponent(c)+"="+encodeURIComponent(a[c]));return b.join("&")}}})},this.submitCart=function(){return a(b+"CourseRegistrationCartClientService/submitCart",{},{query:{method:"GET",cache:!1,isArray:!1}})},this.getRegistrationStatus=function(){return a(b+"CourseRegistrationClientService/getRegistrationStatus",{},{query:{method:"GET",cache:!1,isArray:!1}})},this.undoDeleteCourse=function(){return a(b+"CourseRegistrationCartClientService/undoDeleteCourse",{},{query:{method:"GET",cache:!1,isArray:!1}})}}]),angular.module("regCartApp").service("TermsService",["$resource","APP_URL",function(a,b){var c=b+"ScheduleOfClassesClientService",d=c+"/terms",e="kuali.atp.2012Fall";this.getTermId=function(){return e},this.setTermId=function(a){e=a},this.getTermsFromServer=function(){return a(d,{},{query:{method:"GET",cache:!0,isArray:!0}})},this.getTermNameForTermId=function(a,b){var c;return angular.forEach(a,function(a){a.termId===b&&(c=a.termName)}),c}}]),angular.module("regCartApp").service("ScheduleService",["$resource","APP_URL",function(a,b){this.getScheduleFromServer=function(){return a(b+"CourseRegistrationClientService/personschedule",{},{query:{method:"GET",cache:!1,isArray:!1}})},this.updateSchedule=function(){return a(b+"CourseRegistrationClientService/updateScheduleItem",{},{query:{method:"GET",cache:!1,isArray:!0}})},this.updateScheduleItem=function(){return a(b+"CourseRegistrationClientService/updateScheduleItem",{},{query:{headers:{"Content-Type":"application/x-www-form-urlencoded; charset=UTF-8"},method:"PUT",cache:!1,isArray:!1,transformRequest:function(a){var b=[];for(var c in a)a[c]&&b.push(encodeURIComponent(c)+"="+encodeURIComponent(a[c]));return b.join("&")}}})},this.updateWaitlistItem=function(){return a(b+"CourseRegistrationClientService/updateWaitlistEntry",{},{query:{headers:{"Content-Type":"application/x-www-form-urlencoded; charset=UTF-8"},method:"PUT",cache:!1,isArray:!1,transformRequest:function(a){var b=[];for(var c in a)a[c]&&b.push(encodeURIComponent(c)+"="+encodeURIComponent(a[c]));return b.join("&")}}})},this.dropRegistrationGroup=function(){return a(b+"CourseRegistrationClientService/dropRegistrationGroup",{},{query:{method:"DELETE",cache:!1,isArray:!1}})},this.dropFromWaitlist=function(){return a(b+"CourseRegistrationClientService/dropFromWaitlistEntry",{},{query:{method:"DELETE",cache:!1,isArray:!1}})},this.registerForRegistrationGroup=function(){return a(b+"CourseRegistrationClientService/registerreggroup",{},{query:{method:"GET",cache:!1,isArray:!1}})},this.getRegistrationStatus=function(){return a(b+"CourseRegistrationClientService/getRegistrationStatus",{},{query:{method:"GET",cache:!1,isArray:!1}})}}]),angular.module("regCartApp").service("LoginService",["$resource","APP_URL",function(a,b){this.logOnAsAdmin=function(){return a(b+"DevelopmentLoginClientService/login",{},{query:{method:"GET",cache:!1,isArray:!1}})},this.logout=function(){return a(b+"DevelopmentLoginClientService/logout",{},{query:{method:"GET",cache:!1,isArray:!1}})}}]),angular.module("regCartApp").factory("loginInterceptor",["$q","$injector","$window",function(a,b,c){return{responseError:function(d){if(0===d.status){console.log("Failed to execute request - trying to login");var e=b.get("LoginService");e.logOnAsAdmin().query({userId:"admin",password:"admin"},function(){console.log("Logged in, reloading page."),c.location.reload()},function(){console.log("Not Logged in, reloading page."),c.location.reload()})}return a.reject(d)}}}]),angular.module("regCartApp").service("GlobalVarsService",function(){var a,b,c,d=0,e=0,f=0,g=0,h=0,i=["kuali.lpr.trans.item.state.processing","kuali.lpr.trans.state.processing"],j=["kuali.lpr.trans.state.succeeded","kuali.lpr.trans.item.state.succeeded"],k=["kuali.lpr.trans.item.state.waitlist"],l=["kuali.lpr.trans.state.failed","kuali.lpr.trans.item.state.failed"],m=["kuali.lpr.trans.item.state.waitlistActionAvailable"];this.getCartCredits=function(){return d},this.setCartCredits=function(a){d=a},this.getCartCourseCount=function(){return e},this.setCartCourseCount=function(a){e=a},this.getRegisteredCredits=function(){return a},this.setRegisteredCredits=function(b){a=b},this.getRegisteredCourseCount=function(){return f},this.setRegisteredCourseCount=function(a){f=a},this.getWaitlistedCredits=function(){return g},this.setWaitlistedCredits=function(a){g=a},this.getWaitlistedCourseCount=function(){return h},this.setWaitlistedCourseCount=function(a){h=a},this.getSchedule=function(){return b},this.setSchedule=function(a){b=a},this.getUserId=function(){return c},this.setUserId=function(a){c=a},this.getCorrespondingStatusFromState=function(a){var b="new";return i.indexOf(a)>=0?b="processing":j.indexOf(a)>=0?b="success":l.indexOf(a)>=0?b="error":k.indexOf(a)>=0?b="waitlist":m.indexOf(a)>=0&&(b="action"),b},this.updateScheduleCounts=function(a){var b=a.studentScheduleTermResults,c=a.userId,d=0,e=0,f=0,g=0;this.setSchedule(b),angular.forEach(b,function(a){angular.forEach(a.registeredCourseOfferings,function(a){d+=parseFloat(a.credits),e++;var b=0;angular.forEach(a.gradingOptions,function(){b++}),a.gradingOptionCount=b}),angular.forEach(a.waitlistCourseOfferings,function(a){f+=parseFloat(a.credits),g++;var b=0;angular.forEach(a.gradingOptions,function(){b++}),a.gradingOptionCount=b})}),this.setRegisteredCourseCount(e),this.setRegisteredCredits(d),this.setWaitlistedCredits(f),this.setWaitlistedCourseCount(g),this.setUserId(c)},this.getCorrespondingMessageFromStatus=function(a){var b="";return"waitlist"===a&&(b="If a seat becomes available you will be registered automatically"),b}}),angular.module("regCartApp").directive("courseOptions",function(){return{restrict:"E",transclude:!0,scope:{course:"=",maxOptions:"=max",prefix:"@",showAll:"=",cancelFn:"&onCancel",submitFn:"&onSubmit"},templateUrl:"partials/courseOptions.html",controller:["$scope","$modal",function(a,b){function c(a,b,c){if(a.length<=e)return!0;var d=a.indexOf(b),f=a.indexOf(c),g=e>d?0:d-2,h=e-1>d?e-1:d+1;return 3>d?(g=0,h=3):d>=a.length-2?(h=a.length-1,g=h-3):(g=d-2,h=d+1),f>=g&&h>=f}var d=a.course,e=a.maxOptions||4,f=a.showAll?!0:!1;a.showAllCreditOptions=f,a.showAllGradingOptions=f,a.gradingOptions=[],d&&d.gradingOptions&&angular.forEach(d.gradingOptions,function(a,b){this.push({key:b,label:a})},a.gradingOptions),a.creditOptionsFilter=function(b){return!d||a.showAllCreditOptions?!0:c(d.creditOptions,d.credits,b)},a.gradingOptionsFilter=function(b){return!d||a.showAllGradingOptions?!0:c(Object.keys(d.gradingOptions),d.grading,b.key)},a.showOptionsDialog=function(c){var d=a.$new();d.course=angular.copy(c),c.editing=!1,d.cancel=function(){},d.submit=function(){};var f=b.open({backdrop:"static",template:'<div class="kscr-AdditionalOptions"><course-options course="course" show-all="true" max="'+e+'" prefix="modal_'+(a.prefix?a.prefix:"")+'" on-submit="modalSubmit()" on-cancel="modalCancel()"></course-options></div>',scope:d,controller:["$scope",function(a){a.showAllCreditOptions=!0,a.showAllGradingOptions=!0,a.modalCancel=function(){a.$dismiss("cancel")},a.modalSubmit=function(){a.$close(a.course)}}]});f.result.then(function(b){c.newGrading=b.newGrading,c.newCredits=b.newCredits,a.submit()},function(){a.cancel()})},a.shouldShowMoreCreditOptionsToggle=function(){return!a.showAllCreditOptions&&d.creditOptions.length>e},a.shouldShowMoreGradingOptionsToggle=function(){return!a.showAllGradingOptions&&Object.keys(d.gradingOptions).length>e},a.cancel=function(){console.log("Canceling options changes"),d.newCredits=d.credits,d.newGrading=d.grading||d.gradingOptionId,d.status="",d.editing=!1,a.cancelFn&&a.cancelFn({course:d})},a.submit=function(){console.log("Submitting options form"),a.submitFn&&a.submitFn({course:d})},a.showGradingHelp=function(){b.open({templateUrl:"partials/gradingOptionsHelp.html"})}}]}}),angular.module("regCartApp").directive("focusMe",["$timeout","$parse",function(a,b){return{link:function(c,d,e){var f=b(e.focusMe);c.$watch(f,function(b){b===!0&&a(function(){d[0].focus()})}),d.bind("blur",function(){a(function(){d[0].focus()})})}}}]);