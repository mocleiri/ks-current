class CmCourseFinancials < BasePage

  wrapper_elements
  cm_elements

  element(:course_fees) { |b| b.text_field(name: /course.feeJustification.plain$/) }

end
