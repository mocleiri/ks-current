class KradRice < BasePage

  element(:krad_curriculum_management_element) { |b| b.div(id: 'KS-StudentHome-CurriculumManagementKrad') }
  #element(:krad_curriculum_management_element) { |b| b.span(text: 'Curriculum Management KRAD') }

  action(:krad_curriculum_management) { |b| b.krad_curriculum_management_element.click }



end



