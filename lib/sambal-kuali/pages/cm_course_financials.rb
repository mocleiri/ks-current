class CmCourseFinancials < BasePage

  wrapper_elements
  cm_elements

  element(:course_fees) { |b| b.text_field(name: /course.feeJustification.plain$/) }
  element(:financial_fee_text) {|b| b.text_field(id: 'CourseView-Financials-Fee-Text_control')}

end
