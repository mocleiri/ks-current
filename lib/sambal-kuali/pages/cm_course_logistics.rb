class CmCourseLogistics < BasePage

  wrapper_elements # needed for loading and saving waits
  cm_elements

#TERM
  element(:term_any) { |b| b.checkbox(value: 'kuali.atp.season.Any') }
  element(:term_fall) { |b| b.checkbox(value: 'kuali.atp.type.Fall') }
  element(:term_spring) { |b| b.checkbox(value: 'kuali.atp.type.Spring') }
  element(:term_summer) { |b| b.checkbox(value: 'kuali.atp.type.Summer') }

#DURATION COUNT
  element(:duration_count_type) { |b| b.select_list(name: /course.duration.atpDurationTypeKey$/) }
  element(:duration_count_count) { |b| b.text_field(name: /course.duration.timeQuantity$/) }

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
  action(:add_outcome) {|b| b.button(id: 'outcome-addline').click; b.loading_wait }
  action(:outcome_type) { |outcome_level, b| b.select_list(id: "typeKey_line#{outcome_level}_control" ) }
  action(:credit_value) { |outcome_level, b| b.text_field(id: "creditValue_line#{outcome_level}_control") }
  action(:delete_outcome) { |outcome_level,b| b.a(id: "delete_outcome_line#{outcome_level}").i(class: "ks-fontello-icon-cancel").click }


#COURSE FORMAT(S)
  action(:add_additional_format) { |b| b.button(id: /CourseFormats-Widgets_add$/).click; b.adding_line_wait }
  action(:add_activity) { |b| b.button(id: 'activity-addline_line0').click; b.loading_wait }
  action(:type) { |format_level,activity_level,b| b.select_list(name: "document.newMaintainableObject.dataObject.formats[#{format_level-1}].activities[#{activity_level-1}].typeKey") }
  element(:contacted_hours) { |format_level,activity_level,b| b.text_field(id: /line#{format_level-1}_line#{activity_level-1}_control/, name: /contactHours.unitQuantity$/) }
  element(:contact_frequency) { |format_level,activity_level,b| b.select_list(id: /line#{format_level-1}_line#{activity_level-1}_control/, name: /contactHours.unitTypeKey$/) }
  action(:duration_count) { |format_level,activity_level,b| b.text_field(id: "KS-Logistics-Format-DurationTimeQuantity-Field_line#{format_level-1}_line#{activity_level-1}_control") }
  action(:duration_type) { |format_level,activity_level,b| b.select_list(id: "KS-Logistics-Format-DurationTypeDropDown_line#{format_level-1}_line#{activity_level-1}_control") }
  action(:class_size) { |format_level,activity_level,b| b.text_field(id:/line#{format_level-1}_line#{activity_level-1}_control/, name: /defaultEnrollmentEstimate$/) }

  #ADDED COURSE FORMATS
  action(:type_added) { |format_level,activity_level,b| b.select_list(name: "document.newMaintainableObject.dataObject.formats[#{format_level-1}].activities[#{activity_level-1}].typeKey") }
  element(:contacted_hours_added) { |format_level,activity_level,b| b.text_field(name: "document.newMaintainableObject.dataObject.formats[#{format_level-1}].activities[#{activity_level-1}].contactHours.unitQuantity").value }
  element(:frequency_added) { |format_level,activity_level,b| b.select_list(name: "document.newMaintainableObject.dataObject.formats[#{format_level-1}].activities[#{activity_level-1}].contactHours.unitTypeKey") }
  action(:duration_type_added) { |format_level,activity_level,b| b.select_list(name: "document.newMaintainableObject.dataObject.formats[#{format_level-1}].activities[#{activity_level-1}].duration.atpDurationTypeKey") }
  action(:duration_count_added) { |format_level,activity_level,b| b.text_field(name: "document.newMaintainableObject.dataObject.formats[#{format_level-1}].activities[#{activity_level-1}].duration.timeQuantity").value }
  action(:class_size_added) { |format_level,activity_level,b| b.text_field(name: "document.newMaintainableObject.dataObject.formats[#{format_level-1}].activities[#{activity_level-1}].defaultEnrollmentEstimate").value }




end



