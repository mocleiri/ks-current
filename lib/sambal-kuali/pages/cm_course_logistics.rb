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
  element(:exam_standard) { |b| b.radio(value: 'STANDARD') }
  element(:exam_alternate) { |b| b.radio(value: 'ALTERNATE') }
  element(:exam_none) { |b| b.radio(value: 'NONE') }
  element(:final_exam_rationale) { |b| b.text_field(name: 'document.newMaintainableObject.finalExamRationale') }

#OUTCOME
  action(:add_outcome) {|b| b.button(text: 'Add Outcome').click; b.loading_wait }
  action(:outcome_type) { |outcome_level='0', b| b.select_list(name: /#{outcome_level}\]\.typeKey$/) }
  action(:credit_value) {|outcome_level='0', b| b.text_field(id: /^fixedCreditVale_line#{outcome_level}/) }
  action(:credit_value_min) {|outcome_level='0', b| b.text_field(id:/^MinRangeCreditVale_line#{outcome_level}/) }
  action(:credit_value_max) {|outcome_level='0', b| b.text_field(id: /^MaxRangeCreditVale_line#{outcome_level}/) }
  action(:credit_value_multiple) { |outcome_level='', b| b.text_field(name: /#{outcome_level}\]\.creditValueDisplay$/) }
  action(:outcome_add_multiple_btn) { |b| b.button(text: 'add').click; b.adding_line_wait }

#COURSE FORMAT(S)
  action(:add_additional_format) { |b| b.button(id: /CourseFormats-Widgets_add$/).click; b.loading_wait }
  action(:add_activity) { |b| b.button(text: 'Activity').click; b.loading_wait }
  action(:activity_type) { |activity_level='0', b| b.select_list(name: /activities\[#{activity_level}\]\.typeKey$/) }
  action(:activity_duration_count) { |activity_level='0', b| b.text_field(name: /activities\[#{activity_level}\]\.duration\.timeQuantity$/) }
  action(:activity_class_size) { |activity_level='0', b| b.text_field(name: /activities\[#{activity_level}\]\.defaultEnrollmentEstimate/) }
  action(:activity_duration_type) { |activity_level='0', b| b.select_list(name: /activities\[#{activity_level}\]\.duration\.atpDurationTypeKey$/) }
  element(:activity_frequency) { |b| b.select_list(name: /contactHours.unitTypeKey$/) }
  element(:activity_contacted_hours) { |b| b.text_field(name: /contactHours.unitQuantity$/) }
end



