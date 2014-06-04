class CmCourseFinancials < BasePage

  wrapper_elements
  cm_elements

  element(:financial_fee_text) { |b| b.text_field(id: 'CourseView-Financials-Fee-Text_control') }

end
