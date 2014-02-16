class CmRice < BasePage

  element(:curriculum_management_element) { |b| b.div(id: 'KS-StudentHome-CurriculumManagementKrad') }
  #element(:curriculum_management_element) { |b| b.span(text: 'Curriculum Management KRAD') }

  action(:curriculum_management) { |b| b.curriculum_management_element.click }

end



