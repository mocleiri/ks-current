class CmCourseInformation < BasePage

  wrapper_elements
  cm_elements

  element(:proposal_title) { |b| b.text_field(name: 'document.newMaintainableObject.dataObject.proposalInfo.name') }
  element(:proposal_title_error_state) { |b| b.text_field(name: 'document.newMaintainableObject.dataObject.proposalInfo.name', class: /error/) }

  element(:course_title) { |b| b.text_field(name: 'document.newMaintainableObject.dataObject.courseInfo.courseTitle') }
  element(:course_title_error_state) { |b| b.text_field(name: 'document.newMaintainableObject.dataObject.courseInfo.courseTitle', class: 'uif-textControl required validChar-document.newMaintainableObject.dataObject.courseInfo.courseTitle0 error') }

  element(:transcript_course_title) {|b| b.text_field(id: 'KS-TranscriptTitle_control') }
  element(:subject_code) { |b| b.text_field(id: 'KS-SubjectArea-Field_control') }
  element(:course_number) { |b| b.text_field(id: 'KS-CourseNumberSuffix-Field_control') }
  action(:cancel) { |b| b.a(id: 'ucancel').click; b.loading_wait }

#CROSS LIST SECTION
  element(:course_listing_section_collapsed) { |b| b.img(id: /^KS-CrossListingEtcDisclosure-Section/, alt: 'collapse') }
  action(:expand_course_listing_section) { |b| b.img(id: 'KS-CrossListingEtcDisclosure-Section_toggle_col').click; b.add_a_version_code_button.wait_until_present }
  element(:collapse_course_listing_section) { |b| b.img(id: 'KS-CrossListingEtcDisclosure-Section_toggle_exp') }

  element(:add_another_course_listing_button) { |b| b.button(id: 'KS-CrossListed-Section_add') }
  element(:add_another_course_button) { |b| b.button(id: 'KS-JointlyOffered-Section_add') }
  element(:add_a_version_code_button) { |b| b.button(id: 'KS-VersionCodes-Section_add') }

  action(:add_another_course_listing) { |b| b.add_another_course_listing_button.click; b.adding_line_wait }
  action(:add_another_course) { |b| b.add_another_course_button.click; b.loading_wait }
  action(:add_a_version_code) { |b| b.add_a_version_code_button.click; b.loading_wait }

  # Needed this way because if user has multiple cross_list to access those multiple fields
  action(:course_listing_subject) { |cross_list_subject_code='0', b| b.text_field(id: /^KS-CrossList-SubjectArea-Field_line#{cross_list_subject_code}/) }
  action(:course_listing_number) { |cross_list_course_number='0', b| b.text_field(id: /^KS-CrossList-CourseNumberSuffix-Field_line#{cross_list_course_number}/) }

  #action(:joint_offering_number) { |joint_offering_course_number='0', b| b.text_field(name: /#{joint_offering_course_number}\].courseCode$/) }
  action(:joint_offering_number) { |joint_offering_course_number='0', b| b.text_field(name: "document\.newMaintainableObject\.courseJointWrappers\[#{joint_offering_course_number}\]\.courseCode") }
  action(:version_code_code) { |version_version_code='0', b| b.text_field(name: /#{version_version_code}\]\.variationCode$/) }
  action(:version_code_title) { |version_course_title='0', b| b.text_field(name: /#{version_course_title}\]\.variationTitle$/) }

#INSTRUCTORS
  element(:instructor_name) { |b| b.text_field(name: /instructorWrappers\'\]\.displayName$/) }
  action(:instructor_add) {|b| b.button(id: 'KS-Instructors-Section_add').click; b.adding_line.wait_while_present }
  action(:added_instructor_name) { |instructor_level='0', b| b.text_field(name: "document.newMaintainableObject.instructorWrappers[#{instructor_level}].displayName") } #/instructorWrappers\[{instructor_level}\]\.displayName$/)}
  action(:instructor_advanced_search) { |b| b.div(id: 'KS-Instructor-displayName_add').link(text: 'Advanced Search' ).click; b.adv_search_button.wait_until_present }

#DESCRIPTION AND RATIONALE
  element(:description_rationale) { |b| b.text_field(name: /courseInfo.descr.plain$/) }
  element(:proposal_rationale) { |b| b.text_field(name: /proposalInfo.rationale.plain$/) }

# ADVANCED SEARCH
  action(:joint_offering_advanced_search) { |b| b.div(id: 'KS-JointlyOffered-Section').link(text: 'Advanced Search').click }

  element(:error_popup) { |b| b.div(text: 'The form contains errors. Please correct these errors and try again.') }
  action(:error_message) { |error_number='2', b| b.h3(text: "This page has #{error_number} errors") }

# COURSE INFORMATION REVIEW FIELDS
  action(:edit_find_course_proposal) { |b| b.i(class: "ks-fontello-icon-pencil").click }
  action(:edit_course_information) { |b| b.a(id: "CourseInfo-Review-Edit-link").click }
  value(:proposal_title_review) { |b| b.div(id: "CourseInfo-Review-section").div(data_label: "Proposal Title").text_field(name: /proposalInfo/).value }
  value(:course_title_review) { |b| b.div(id: "CourseInfo-Review-section").div(data_label: "Course Title").text_field(name: /courseTitle/).value }
  value(:subject_code_review) { |b| b.div(id: "CourseInfo-Review-section").div(data_label: "Subject Code").text_field(name: /subjectArea/).value }
  value(:course_number_review) { |b| b.div(id: "CourseInfo-Review-section").div(data_label: "Course Number").text_field(name: /courseNumberSuffix/).value }
  value(:description_review) { |b| b.div(id: "CourseInfo-Review-section").div(data_label: "Description").text_field(name: /descr/).value }
  value(:proposal_rationale_review) { |b| b.div(id: "CourseInfo-Review-section").div(data_label: "Proposal Rationale").text_field(name: /rationale/).value}

# COURSE INFORMATION READ-ONLY REVIEW FIELDS
  value(:proposal_title_review_read_only) { |b| b.span(id: "proposalInfo_name_control").text }
  value(:course_title_review_read_only) { |b| b.span(id: "courseInfo_courseTitle_control").text }



  # table results
  # b.frame(class: 'fancybox-iframe').div(class: 'dataTables_wrapper').table.row.cells[1].text



  #$b.frame(class: 'fancybox-iframe').div(class: 'dataTables_wrapper').table.rows.each do |row|
  #puts "#{row.cells[3].text} is the row"
  #end
  #





      # rice page

      element(:doc_search_results) { |the_course_title_looking_for, b| b.frame(id: /^easyXDM_default/).frame(id: 'iframeportlet').table.td(text: the_course_title_looking_for) }
  #
  #$b.frame(id: /^easyXDM_default/).frame(id: 'iframeportlet').table.img(alt: 'Route Log for Document')
  #


 end #class
