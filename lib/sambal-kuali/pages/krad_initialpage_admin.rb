class KradInitialPageAdmin < BasePage

  wrapper_elements
  krad_elements

#  element(:create_course) {|b| b.text_field(id: /^KS-CourseView-createCourseInitialPage/)}
  action(:blank_proposal) {|b| b.div(id:'KS-createCourseInitialPage-CMStartOptions').input(id:'KS-createCourseInitialPage-CMStartOptions_control_0')}
  element(:curriculum_review_process) {|b| b.div(id:'createCourseInitial_is_reviewProcess').input(id:'createCourseInitial_is_reviewProcess_control')}
  action(:curriculum_review_process_click) {|b| b.curriculum_review_process.click}
  action(:continue) {|b| b.continue.click; b.loading_wait}
  action(:cancel) {|b| b.cancel.click; b.loading_wait}
 # createCourseInitialAction

end #class