class CmCreateCourseStart < BasePage

  wrapper_elements
  cm_elements

  element(:blank_proposal) { |b| b.div(id:'KS-createCourseInitialPage-CMStartOptions').input(id:'KS-createCourseInitialPage-CMStartOptions_control_0') }
  #element(:curriculum_review_process) { |b| b.div(id:'createCourseInitial_is_reviewProcess').input(id:'createCourseInitial_is_reviewProcess_control') }
  action(:continue) { |b| b.button(id: 'createCourseInitial-continueCreate').click }
  action(:cancel) { |b| b.button(id: 'cancel').click }
  element(:curriculum_review_process) { |b| b.checkbox(id: "createCourseInitial_is_reviewProcess_control")}


end #class