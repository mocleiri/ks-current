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
  action(:expand_course_listing_section) { |b| b.a(id: 'KS-CrossListingEtcDisclosure-Section_toggle').span(class: 'uif-headerText-span').click; b.add_version_code_button.wait_until_present }
  element(:collapse_course_listing_section) { |b| b.div(id: 'KS-CrossListingEtcDisclosure-Section_disclosureContent') }

  #CROSS LISTED COURSES
  action(:cross_listed_course_subject) { |cross_list_course_level, b| b.text_field(id: "subjectArea_line#{cross_list_course_level-1}_control") }
  action(:cross_listed_course_number) { |cross_list_course_level, b| b.text_field(id: "courseNumberSuffix_line#{cross_list_course_level-1}_control") }
  action(:add_cross_listed_course) { |b| b.button(id: 'addCrosslistedCourse').click; b.loading_wait }
  action(:delete_cross_listed_course) { |cross_list_course_level,b| b.a(id: "delete_crosslistedcourse_line#{cross_list_course_level-1}").i(class: "ks-fontello-icon-cancel").click; b.loading_wait }

  #JOINTLY OFFERED COURSES
  element(:add_jointly_offered_course) { |b| b.button(id: 'addJointlyOfferedCourse').click; b.loading_wait }
  element(:jointly_offered_course) { | joint_offered_course_level,b| b.text_field(id: "KS-CourseCode-Field_line#{joint_offered_course_level-1}_control") }
  action(:jointly_offered_quick_find) { |joint_offered_course_level, b| b.a(id: "KS-CourseCode-Field_line#{joint_offered_course_level-1}_quickfinder_act").click }
  action(:delete_jointly_offered_course) { |joint_offered_course_level, b| b.a(id: "delete_jointofferedcourse_line#{joint_offered_course_level-1}").i(class: "ks-fontello-icon-cancel").click; b.loading_wait }
  ##ADVANCED SEARCH

  #VERSION CODES
  element(:add_version_code_button) { |b| b.button(id: 'addVersionCode') }
  action(:add_version_code) { |b| b.add_version_code_button.click; b.loading_wait }
  action(:version_code_code) { |version_code_level, b| b.text_field(id: "variationCode_line#{version_code_level-1}_control") }
  action(:version_code_title) { |version_code_level, b| b.text_field(id: "variationTitle_line#{version_code_level-1}_control") }
  action(:delete_version_code) { |version_code_level, b| b.a(id: "delete_versioncode_line#{version_code_level-1}").i(class: "ks-fontello-icon-cancel").click; b.loading_wait  }


  #INSTRUCTORS
  element(:instructor_name) { |instructor_level,b| b.text_field(id: "KS-Instructor-displayName_line#{instructor_level-1}_control") }
  action(:add_instructor) {|b| b.button(id: 'addInstructor').click; b.adding_line.wait_while_present }
  action(:delete_instructor) { |instructor_level,b| b.a(id: "delete_instructor_line#{instructor_level-1}").i(class: "ks-fontello-icon-cancel").click }

#DESCRIPTION AND RATIONALE
  element(:description_rationale) { |b| b.text_field(name: /courseInfo.descr.plain$/) }
  element(:proposal_rationale) { |b| b.text_field(name: /proposalInfo.rationale.plain$/) }

# ADVANCED SEARCH
  element(:error_popup) { |b| b.div(text: 'The form contains errors. Please correct these errors and try again.') }
  action(:error_message) { |error_number='2', b| b.h3(text: "This page has #{error_number} errors") }



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
