class CmCourseLogistics < BasePage

  wrapper_elements # needed for loading and saving waits
  cm_elements

#TERM
  element(:term_any) { |b| b.checkbox(value: 'kuali.atp.season.Any') }
  element(:term_fall) { |b| b.checkbox(value: 'kuali.atp.type.Fall') }
  element(:term_spring) { |b| b.checkbox(value: 'kuali.atp.type.Spring') }
  element(:term_summer) { |b| b.checkbox(value: 'kuali.atp.type.Summer') }

#DURATION COUNT
  element(:duration_type) { |b| b.select_list(name: /course.duration.atpDurationTypeKey$/) }
  element(:duration_count) { |b| b.text_field(name: /course.duration.timeQuantity$/) }

#GRADE ASSESMENTS

  #action(:assessment_scale) { |grade, b| b.checkbox(value: grade) }
  element(:assessment_a_f) { |b| b.checkbox(value: 'kuali.result.group.grade.letter.plus.minus.standard')}
  element(:assessment_notation) { |b| b.checkbox(value: 'kuali.resultComponent.grade.completedNotation') }
  element(:assessment_letter) { |b| b.checkbox(value: 'kuali.resultComponent.grade.letter') }
  element(:assessment_pass_fail) { |b| b.checkbox(value: 'kuali.resultComponent.grade.passFail') }
  element(:assessment_percentage) {|b| b.checkbox(value: 'kuali.resultComponent.grade.percentage') }
  element(:assessment_satisfactory) {|b| b.checkbox(value: 'kuali.resultComponent.grade.satisfactory') }

#STUDENT REGISTRATION OPTIONS
  element(:audit) {|b| b.checkbox(name: 'document.newMaintainableObject.audit') }
  element(:pass_fail_transcript_grade) { |b| b.checkbox(name: 'document.newMaintainableObject.passFail') }

#FINAL EXAM
  element(:exam_standard) { |b| b.radio(value: 'STD') }
  element(:exam_alternate) { |b| b.radio(value: 'ALT') }
  element(:exam_none) { |b| b.radio(value: 'None') }
  element(:final_exam_rationale) { |b| b.text_field(name: 'document.newMaintainableObject.dataObject.finalExamRationale') }

#OUTCOME
  action(:add_outcome) {|b| b.button(id: 'KS-CourseView-CourseLogisticsPage-Outcome-Widgets_add').click; b.adding_line_wait }
  #action(:outcome_type) { |outcome_level='0', b| b.select_list(name: /#{outcome_level}\]\.typeKey$/) }
  action(:outcome_type) { |outcome_level, b| b.select_list(name: "document.newMaintainableObject.dataObject.creditOptionWrappers[#{outcome_level}].typeKey" ) }
  action(:credit_value_fixed) {|outcome_level, b| b.text_field(id: "fixedCreditVale_line#{outcome_level}_control") }
  action(:credit_value_min) {|outcome_level, b| b.text_field(id:"MinRangeCreditVale_line#{outcome_level}_control") }
  action(:credit_value_max) {|outcome_level, b| b.text_field(id: "MaxRangeCreditVale_line#{outcome_level}_control") }
  action(:credit_value_multiple_entry) { |outcome_level,b| b.text_field(id: "outcome-multiple-credit_line#{outcome_level}_add_control") }
  action(:credit_value_multiple) { |outcome_level,multiple_level,b| b.text_field(id: "outcome-multiple-credit_line#{outcome_level}_line#{multiple_level-2}_control") }
  action(:outcome_add_multiple_btn) { |outcome_level,b| b.button(id: "addBlankLine-outcome-multiple_line#{outcome_level}_add").click ; b.loading_wait }


#COURSE FORMAT(S)
  action(:add_additional_format) { |b| b.button(id: /CourseFormats-Widgets_add$/).click; b.adding_line_wait }
  action(:add_activity) { |b| b.button(id: 'activity-addline_line0').click; b.loading_wait }
  action(:activity_type) { |b| b.select_list(name: 'document.newMaintainableObject.dataObject.formats[0].activities[0].typeKey') }
  element(:activity_contacted_hours) { |b| b.text_field(name: /contactHours.unitQuantity$/) }
  element(:activity_frequency) { |b| b.select_list(name: /contactHours.unitTypeKey$/) }
  action(:activity_duration_type) { |b| b.select_list(id: "KS-Logistics-Format-DurationTypeDropDown_line0_line0_control") }
  action(:activity_duration_count) { |b| b.text_field(id: "KS-Logistics-Format-DurationTimeQuantity-Field_line0_line0_control") }
  action(:activity_class_size) { |b| b.text_field(name: /defaultEnrollmentEstimate$/) }

  #ADDED COURSE FORMATS
  action(:activity_type_added) { |format_count,b| b.select_list(name: "document.newMaintainableObject.dataObject.formats[0].activities[#{format_count-1}].typeKey") }
  element(:activity_contacted_hours_added) { |format_count,b| b.text_field(name: "document.newMaintainableObject.dataObject.formats[0].activities[#{format_count-1}].contactHours.unitQuantity").value }
  element(:activity_frequency_added) { |format_count,b| b.select_list(name: "document.newMaintainableObject.dataObject.formats[0].activities[#{format_count-1}].contactHours.unitTypeKey") }
  action(:activity_duration_type_added) { |format_count,b| b.select_list(name: "document.newMaintainableObject.dataObject.formats[0].activities[#{format_count-1}].duration.atpDurationTypeKey") }
  action(:activity_duration_count_added) { |format_count,b| b.text_field(name: "document.newMaintainableObject.dataObject.formats[0].activities[#{format_count-1}].duration.timeQuantity").value }
  action(:activity_class_size_added) { |format_count,b| b.text_field(name: "document.newMaintainableObject.dataObject.formats[0].activities[#{format_count-1}].defaultEnrollmentEstimate").value }


# LOGISTICS REVIEW FIELDS
  action(:edit_course_logistics) { |b| b.a(id: "CourseLogistics-Review-Edit-link").click }
  value(:assessment_scale_review) { |b| b.div(id: "CourseLogistics-Review-section").div(data_label: "Assessment Scale").input(class: "uif-textControl").value }
  value(:final_exam_status_review) { |b| b.div(id: "CourseLogistics-Review-section").div(data_label: "Final Exam Status").input(class: "uif-textControl").value }
  value(:final_exam_rationale_review) { |b| b.div(id: "CourseLogistics-Review-section").div(data_label: "Final Exam Rationale").textarea(id: "final_exam_rationale_control").value }

  value(:outcome_type_fixed_review) { |b| b.div(id: "CourseLogistics-Review-section").div(data_label: "Type").input(class: "uif-textControl").value }
  value(:outcome_credit_value_review) { |b| b.div(id: "CourseLogistics-Review-section").div(data_label: "Credit Value").input(class: "uif-readOnlyContent").text }
  value(:outcome_level_fixed_review) { |b| b.div(id: "CourseLogistics-Review-section").div(class: "uif-header").span(class:"uif-headerText-span").text }

  #TODO fix definitions after in flight styling changes are completed KSCM-1647

  #value(:outcome_type_range_review)
  #value(:outcome_credit_min_review)
  #value(:outcome_credit_min_review)
  #value(:outcome_level_range_review)

  #value(:outcome_type_multiple_review)
  #value(:outcome_credit_value_multiple1_review)
  #value(:outcome_credit_value_multiple2_review)
  #value(:outcome_level_multiple_review)


end



