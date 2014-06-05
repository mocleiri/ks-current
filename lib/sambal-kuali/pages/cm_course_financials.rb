class CmCourseFinancials < BasePage

  wrapper_elements
  cm_elements

  element(:justification_of_fees) { |b| b.text_field(id: 'CourseView-Financials-Fee-Text_control') }

end
