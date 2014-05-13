class CmReviewProposal < BasePage

  wrapper_elements
  cm_elements

  action(:submit) { |b| b.button(text: 'Submit').click; b.loading_wait }
  action(:blanket_approve) { |b| b.button(text: 'Blanket Approve').click }
  action(:edit_course_information) { |b| b.a(id: "CourseInfo-Review-Edit-link").click }
  element(:review_proposal_header) { |b| b.div(id: "KS-CourseView-Header").p(class: "uif-viewHeader-supportTitle").text }

  # COURSE INFORMATION REVIEW FIELDS
  element(:proposal_title_element) { |b| b.textarea(id: "proposalInfo_name_control") }
  value(:proposal_title_review) { |b| b.proposal_title_element.text }
  value(:course_title_review) { |b| b.textarea(id: "courseInfo_courseTitle_control").text }
  value(:subject_code_review) { |b| b.textarea(id:"courseInfo_subjectArea_control").text }
  value(:course_number_review) { |b| b.textarea(id: "courseInfo_courseNumberSuffix_control").text }
  value(:description_review) { |b| b.textarea(id: "courseInfo_descr_control").text }
  value(:proposal_rationale_review) { |b| b.textarea(id: "proposalInfo_rationale_control").text }

  # COURSE INFORMATION READ-ONLY REVIEW FIELDS
  value(:proposal_title_review_read_only) { |b| b.div(id: "proposalInfo_name").text }
  value(:course_title_review_read_only) { |b| b.div(id: "courseInfo_courseTitle").text }


  # GOVERNANCE REVIEW FIELDS
  action(:edit_governance) { |b| b.a(id: "Governance-Review-Edit-link").click }
  value(:campus_locations_review) { |b| b.textarea(id: 'governanceSection_campusLocations_control').text }
  value(:curriculum_oversight_review) { |b| b.textarea(id: 'governanceSection_curriculumOversightAsString_control').text }


  # LOGISTICS REVIEW FIELDS
  action(:edit_course_logistics) { |b| b.a(id: "CourseLogistics-Review-Edit-link").click }
  value(:assessment_scale_review) { |b| b.textarea(id: "courseLogisticsSection_gradingOptions_control").text }
  value(:final_exam_status_review) { |b| b.textarea(id: "courseLogisticsSection_finalExamStatus_control").text }
  value(:final_exam_rationale_review) { |b| b.textarea(id: "courseLogisticsSection_finalExamStatusRationale_control").text }

  value(:outcome_level_review) { |outcome_level,b| b.div(id: "course_review_outcome_details").header(id: /line#{outcome_level-1}/).span(class: "uif-headerText-span").text }
  value(:outcome_type_review) { |outcome_level,b| b.div(id: "course_review_outcome_details").div(id: /line#{outcome_level-1}/, data_label: "Type").text }
  value(:outcome_credit_review) { |outcome_level,b| b.div(id: "course_review_outcome_details").div(id: /line#{outcome_level-1}/, data_label: "Credit Value").text }

  #ACTIVITY FORMATS
  value(:activity_level_review) { |activity_level, b| b.div(id: "course_review_format_details").header(id: /line#{activity_level-1}/).span(class: "uif-headerText-span").text }
  value(:activity_type_review)  { |activity_level, b| b.div(id: "course_review_activity_details_line#{activity_level-1}").header(id: /line#{activity_level-1}_line#{activity_level-1}/).span(class: "uif-headerText-span").text }
  value(:activity_contact_hours_frequency_review) { |activity_level, b| b.div(id: "course_review_activity_details_line#{activity_level-1}").div(data_label: "Contact Hours").text }
  value(:activity_duration_type_count_review) { |activity_level, b| b.div(id: "course_review_activity_details_line#{activity_level-1}").div(data_label: "Duration").text }
  value(:activity_class_size_review) { |activity_level, b| b.div(id: "course_review_activity_details_line#{activity_level-1}").div(data_label: "Anticipated Class Size").text }

  # ACTIVE DATES REVIEW FIELDS
  action(:edit_active_dates) { |b| b.a(id: 'ActiveDates-Review-Edit-link').click }
  value(:start_term_review) { |b| b.textarea(id: "activeDatesSection_startTerm_control").text }


end
