"use strict";angular.module("regCartApp",["configuration","ngAnimate","ngCookies","ngResource","ngSanitize","ui.router","ui.bootstrap"]).config(["$stateProvider","$urlRouterProvider","$httpProvider",function(a,b,c){b.otherwise("/myCart"),a.state("root",{templateUrl:"partials/main.html",controller:"MainCtrl"}).state("root.cart",{url:"/myCart",templateUrl:"partials/cart.html",controller:"CartCtrl"}).state("root.schedule",{url:"/mySchedule",templateUrl:"partials/schedule.html",controller:"ScheduleCtrl"}).state("root.additionalOptions",{url:"/options",templateUrl:"partials/additionalOptions.html"}),c.interceptors.push("loginInterceptor")}]),angular.module("configuration",[]).value("APP_URL","ks-with-rice-bundled-dev/services/"),angular.module("regCartApp").controller("MainCtrl",["$scope","TermsService","ScheduleService","GlobalVarsService","APP_URL",function(a,b,c,d,e){console.log("In Main Controller"),a.appUrl=e.replace("/services/","/"),a.terms=b.getTermsFromServer().query(function(){}),a.termId="kuali.atp.2012Spring",b.setTermId(a.termId),c.getScheduleFromServer().query({termId:a.termId},function(b){console.log("called rest service to get schedule data - in main.js"),d.updateScheduleCounts(b),a.registeredCredits=d.getRegisteredCredits,a.registeredCourseCount=d.getRegisteredCourseCount})}]),angular.module("regCartApp").controller("CartCtrl",["$scope","$modal","CartService","ScheduleService","GlobalVarsService","$timeout",function(a,b,c,d,e,f){function g(b){c.getCart().query({termId:b},function(b){a.cart=b;for(var c=0;c<a.cart.items.length;c++){var d=a.cart.items[c];if("processing"===e.getCorrespondingStatusFromState(d.state)){d.status="processing",k(d.cartId);break}}})}function h(d,e,f,g,h,i,j){c.addCourseToCart().query({cartId:d,courseCode:e,termId:f,regGroupCode:g,regGroupId:h,gradingOptionId:i,credits:j},function(b){console.log("Searched for course: "+a.courseCode+", Term: "+a.termId),a.userMessage={txt:"Course Added Successfully",type:"success"},a.courseCode="",a.regCode="",a.cart.items.unshift(b)},function(c){console.log("CartId:",d),404===c.status?a.userMessage={txt:c.data,type:"error"}:400===c.status?(console.log("CartId: ",d),b.open({backdrop:"static",templateUrl:"partials/additionalOptions.html",resolve:{item:function(){return c.data},cartId:function(){return d}},controller:["$rootScope","$scope","item","cartId",function(a,b,c,d){console.log("Controller for modal... Item: ",c),b.newCartItem=c,b.newCartItem.credits=b.newCartItem.creditOptions[0],b.newCartItem.grading="kuali.resultComponent.grade.letter",b.dismissAdditionalOptions=function(){console.log("Dismissing credits and grading"),b.$close(!0)},b.saveAdditionalOptions=function(){console.log("Save credits and grading for cartId:",d),a.$broadcast("addCourseToCart",d,b.newCartItem.courseCode,b.newCartItem.termId,b.newCartItem.regGroupCode,b.newCartItem.regGroupId,b.newCartItem.grading,b.newCartItem.credits),b.$close(!0)}}]})):(console.log("Error with adding course",c.data.consoleMessage),a.userMessage={txt:c.data.genericMessage,type:c.data.type,detail:c.data.detailedMessage})})}function i(){if(!a.cart)return 0;for(var b=0,c=0;c<a.cart.items.length;c++)b+=Number(a.cart.items[c].credits);return b}a.oneAtATime=!1;var j=!1;a.$watch("termId",function(a){console.log("term id has changed"),a&&(j=!0,g(a))}),!j&&a.termId&&g(a.termId),a.addRegGroupToCart=function(){a.courseCode=a.courseCode.toUpperCase(),h(a.cart.cartId,a.courseCode,a.termId,a.regCode,null,null,null,null)},a.$on("addCourseToCart",function(a,b,c,d,e,f,g,i){console.log("Received event addCourseToCart ",a),h(b,c,d,e,f,g,i)}),a.cancelNewCartItem=function(){a.newCcartItem=null,a.showNew=!1},a.deleteCartItem=function(b){var d,e=a.cart.items[b],f=e.actionLinks;angular.forEach(f,function(a){"removeItemFromCart"===a.action&&(d=a.uri)}),c.removeItemFromCart(d).query({},function(c){a.cart.items.splice(b,1);var d;angular.forEach(c.actionLinks,function(a){"undoDeleteCourse"===a.action&&(d=a.uri)}),a.userMessage={txt:e.courseCode+"("+e.regGroupCode+") has been successfully removed from your cart.",actionLink:d,linkText:"Undo",type:"success"},a.userActionSuccessful=!0})},a.invokeActionLink=function(b){a.userActionSuccessful=!1,c.invokeActionLink(b).query({},function(b){a.cart.items.unshift(b),a.userMessage.txt=""})},a.editCartItem=function(b){a.newCredits=b.credits,a.newGrading=b.grading,b.status="editing"},a.cancelEditItem=function(a){a.status=""},a.updateCartItem=function(b,d,e){console.log("Updating cart item. Grading: "+e+", credits: "+d),c.updateCartItem().query({cartId:a.cart.cartId,cartItemId:b.cartItemId,credits:d,gradingOptionId:e},function(c){console.log(a),console.log(JSON.stringify(c)),b.credits=c.credits,console.log("old: "+b.grading+" To: "+c.grading),b.grading=c.grading,console.log("old: "+b.grading+" To: "+c.grading),b.status="",b.actionLinks=c.actionLinks,a.creditTotal=i(),a.userMessage={txt:"Updated Successfully",type:"success"}})},a.register=function(){c.submitCart().query({cartId:a.cart.cartId},function(b){a.userMessage.txt="",console.log("Submitted cart. RegReqId["+b.registrationRequestId+"]"),a.cart.state="kuali.lpr.trans.state.processing",a.cart.status="processing",a.creditTotal=0,angular.forEach(a.cart.items,function(a){a.state="kuali.lpr.trans.item.state.processing",a.status="processing"}),f(function(){},250),console.log("Just waited 250, now start the polling"),k(b.registrationRequestId)})};var k=function(b){a.pollingCart=!1,f(function(){c.getRegistrationStatus().query({regReqId:b},function(c){a.cart.state=c.state;a.cart;angular.forEach(c.responseItemResults,function(b){angular.forEach(a.cart.items,function(c){c.cartItemId===b.registrationRequestItemId&&(c.state=b.state,c.status=e.getCorrespondingStatusFromState(b.state),c.statusMessage=b.message),"kuali.lpr.trans.item.state.processing"===b.state&&(a.pollingCart=!0)})}),a.pollingCart?(console.log("Continue polling"),k(b)):(console.log("Stop polling"),a.cart.status="",d.getScheduleFromServer().query({termId:a.termId},function(b){console.log("called rest service to get schedule data - in main.js"),e.updateScheduleCounts(b),a.registeredCredits=e.getRegisteredCredits,a.registeredCourseCount=e.getRegisteredCourseCount}))})},1e3)};a.$watchCollection("cart.items",function(){a.creditTotal=i()}),a.showBadge=function(a){return"Letter"!=a.gradingOptions[a.grading]},a.editing=function(a){return"editing"==a.status}}]);var cartServiceModule=angular.module("regCartApp");cartServiceModule.controller("ScheduleCtrl",["$scope","$modal","ScheduleService","GlobalVarsService",function(a,b,c,d){c.getScheduleFromServer().query({termId:a.termId},function(b){console.log("called rest service to get schedule data - in schedule.js"),a.schedules=b,d.updateScheduleCounts(a.schedules),a.registeredCredits=d.getRegisteredCredits,a.registeredCourseCount=d.getRegisteredCourseCount}),a.openDropConfirmation=function(b,c){console.log("Open drop confirmation"),c.dropping=!0,a.index=b,a.course=c},a.cancelDropConfirmation=function(a){a.dropping=!1},a.dropRegistrationGroup=function(b,e){console.log("Open drop confirmation"),c.dropRegistrationGroup().query({masterLprId:e.masterLprId},function(){e.dropping=!1,a.schedules[0].courseOfferings.splice(b,1),d.updateScheduleCounts(a.schedules),a.userMessage={txt:e.courseCode+" dropped Successfully",type:"success"}})},a.editScheduleItem=function(b){a.newCredits=b.credits,a.newGrading=b.gradingOptionId,b.editing=!0},a.cancelEditScheduleItem=function(a){a.editing=!1},a.updateScheduleItem=function(b,e,f){console.log("Updating:"),console.log(e),console.log(f),c.updateScheduleItem().query({courseCode:b.courseCode,regGroupCode:b.regGroupCode,masterLprId:b.masterLprId,credits:e,gradingOptionId:f},function(c){console.log(c),b.credits=c.credits,b.gradingOptionId=c.gradingOptionId,d.updateScheduleCounts(a.schedules),b.editing=!1,a.userMessage={txt:"Updated Successfully",type:"success"}})},a.showBadge=function(a){return"Letter"!=a.gradingOptions[a.gradingOptionId]}}]),angular.module("regCartApp").service("CartService",["$resource","APP_URL",function(a,b){this.getCart=function(){return a(b+"CourseRegistrationCartClientService/searchForCart",{},{query:{method:"GET",cache:!1,isArray:!1}})},this.getGradingOptions=function(){return a(b+"CourseRegistrationCartClientService/getStudentRegistrationOptions",{},{query:{method:"GET",cache:!1,isArray:!1}})},this.addCourseToCart=function(){return a(b+"CourseRegistrationCartClientService/addCourseToCart",{},{query:{headers:{"Content-Type":"application/x-www-form-urlencoded; charset=UTF-8"},method:"POST",cache:!1,isArray:!1,transformRequest:function(a){var b=[];for(var c in a)a[c]&&b.push(encodeURIComponent(c)+"="+encodeURIComponent(a[c]));return b.join("&")}}})},this.removeItemFromCart=function(c){return a(b+c,{},{query:{method:"DELETE",cache:!1,isArray:!1}})},this.invokeActionLink=function(c){return a(b+c,{},{query:{method:"GET",cache:!1,isArray:!1}})},this.updateCartItem=function(){return a(b+"CourseRegistrationCartClientService/updateCartItem",{},{query:{headers:{"Content-Type":"application/x-www-form-urlencoded; charset=UTF-8"},method:"PUT",cache:!1,isArray:!1,transformRequest:function(a){var b=[];for(var c in a)a[c]&&b.push(encodeURIComponent(c)+"="+encodeURIComponent(a[c]));return b.join("&")}}})},this.submitCart=function(){return a(b+"CourseRegistrationCartClientService/submitCart",{},{query:{method:"GET",cache:!1,isArray:!1}})},this.getRegistrationStatus=function(){return a(b+"CourseRegistrationClientService/getRegistrationStatus",{},{query:{method:"GET",cache:!1,isArray:!1}})},this.undoDeleteCourse=function(){return a(b+"CourseRegistrationCartClientService/undoDeleteCourse",{},{query:{method:"GET",cache:!1,isArray:!1}})}}]),angular.module("regCartApp").service("TermsService",["$resource","APP_URL",function(a,b){var c="kuali.atp.2012Spring";this.getTermId=function(){return c},this.setTermId=function(a){c=a},this.getTermsFromServer=function(){return a(b+"ScheduleOfClassesService/terms",{},{query:{method:"GET",cache:!0,isArray:!0}})}}]),angular.module("regCartApp").service("ScheduleService",["$resource","APP_URL",function(a,b){this.getScheduleFromServer=function(){return a(b+"CourseRegistrationClientService/personschedule",{},{query:{method:"GET",cache:!1,isArray:!0}})},this.updateSchedule=function(){return a(b+"CourseRegistrationClientService/updateScheduleItem",{},{query:{method:"GET",cache:!1,isArray:!0}})},this.updateScheduleItem=function(){return a(b+"CourseRegistrationClientService/updateScheduleItem",{},{query:{headers:{"Content-Type":"application/x-www-form-urlencoded; charset=UTF-8"},method:"PUT",cache:!1,isArray:!1,transformRequest:function(a){var b=[];for(var c in a)a[c]&&b.push(encodeURIComponent(c)+"="+encodeURIComponent(a[c]));return b.join("&")}}})},this.dropRegistrationGroup=function(){return a(b+"CourseRegistrationClientService/dropRegistrationGroup",{},{query:{method:"DELETE",cache:!1,isArray:!1}})}}]),angular.module("regCartApp").service("LoginService",["$resource","APP_URL",function(a,b){this.logOnAsAdmin=function(){return a(b+"DevelopmentLoginClientService/login",{},{query:{method:"GET",cache:!1,isArray:!1}})}}]),angular.module("regCartApp").factory("loginInterceptor",["$q","$injector","$window",function(a,b,c){return{responseError:function(d){if(0===d.status){console.log("Failed to execute request - trying to login");var e=b.get("LoginService");e.logOnAsAdmin().query({userId:"admin",password:"admin"},function(){console.log("Logged in, reloading page."),c.location.reload()},function(){console.log("Not Logged in, reloading page."),c.location.reload()})}return a.reject(d)}}}]),angular.module("regCartApp").service("GlobalVarsService",function(){var a=0,b=0,c=["kuali.lpr.trans.item.state.processing","kuali.lpr.trans.state.processing"],d=["kuali.lpr.trans.state.succeeded","kuali.lpr.trans.item.state.succeeded"],e=["kuali.lpr.trans.state.failed","kuali.lpr.trans.item.state.failed"];this.getRegisteredCredits=function(){return a},this.setRegisteredCredits=function(b){a=b},this.getRegisteredCourseCount=function(){return b},this.setRegisteredCourseCount=function(a){b=a},this.getCorrespondingStatusFromState=function(a){var b="new";return c.indexOf(a)>=0?b="processing":d.indexOf(a)>=0?b="success":e.indexOf(a)>=0&&(b="error"),b},this.updateScheduleCounts=function(a){var b=0,c=0;angular.forEach(a,function(a){angular.forEach(a.courseOfferings,function(a){b+=parseFloat(a.credits),c++;var d=0;angular.forEach(a.gradingOptions,function(){d++}),a.gradingOptionCount=d})}),this.setRegisteredCourseCount(c),this.setRegisteredCredits(b)}});