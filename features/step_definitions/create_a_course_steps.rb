#-----
# S1
#-----
When /^I create a course proposal$/ do
  @course_proposal = create CmCourseProposalObject, :create_new_proposal => false
end

When /^I create a course proposal from blank$/ do
  @course_proposal = create CmCourseProposalObject, :save_proposal => false, :proposal_title => nil, :course_title =>nil
end

Then /^I should see a blank course proposal$/ do
  on CmCourseInformation do |page|
    page.proposal_title.text.should == ""
    page.course_title.text.should == ""
  end
  end

And /^I cancel create a course$/ do
  @course_proposal.cancel_create_course
end

Then /^I should see CM Home$/ do
  on CmCurriculum do |page|
    page.cmcurriculum_header.exists?.should == true
  end
end


Then /^I should see data in the proposal title on course information$/ do
  on CmCourseInformation do |page|
    page.course_information
    page.proposal_title.value.should == @course_proposal.proposal_title
    #TODO:: add in validation for assessment_scale
  end
end

And /^I should see data in the course title on course information$/ do
  on CmCourseInformation do |page|
    page.course_information
    page.course_title.value.should == @course_proposal.course_title
  end
end

When /^I complete the required fields for save on the course proposal$/ do
  @course_proposal = create CmCourseProposalObject
end

When /^I complete the required fields on the course proposal and save$/ do
  @course_proposal = create CmCourseProposalObject, :curriculum_review_process => "Yes"
end


When /^I complete the required fields for save on the course admin proposal$/ do
  @course_proposal = create CmCourseProposalObject
end


Then /^I should see data in required fields for the course admin proposal$/ do
  on CmCourseInformation do |page|
    page.course_information
    page.proposal_title.value.should == @course_proposal.proposal_title
    page.course_title.value.should == @course_proposal.course_title
    page.page_validation_text.should == "Document was successfully saved."
    page.page_header_text.should == "#{@course_proposal.proposal_title} (Admin Proposal)"
  end

end

When /^I am on the course information page and I click save progress without entering any values$/ do
  @course_proposal = create CmCourseProposalObject, :proposal_title => nil, :course_title => nil
end

And /^I click the save progress button$/ do

end

Then /^I should receive an error message about the proposal title and course title being required for save$/ do
 on CmCourseInformation do |page|
   #page.course_information
   page.proposal_title_error_state.exists?.should == true
   page.course_title_error_state.exists?.should == true
   page.growl_text.should include "The form contains errors. Please correct these errors and try again."
   page.page_validation_header.should include "This page has 2 errors"
 end
end


When /^I create a course proposal for testing$/ do
  @course_proposal = create CmCourseProposalObject
end



Then /^I should see a course proposal being created$/ do
  on CmCourseInformation do |page|
    page.course_information
    page.proposal_title.value.should == @course_proposal.proposal_title
    page.course_title.value.should == @course_proposal.course_title
    page.page_validation_text.should == "Document was successfully saved."
  end
end
#-----
# S2
#-----
When /^I complete the required fields on the course proposal$/ do

  # Change to 'I complete the fields required to submit the course proposal'



  #@course_proposal.KradCourseProposalRequired
  #@course_proposal.create_course_proposal_required
  @course_proposal  = create CmCourseProposalObject,
                           #REQUIRED
                           #COURSE INFORMATION
                           #proposal_title: random_alphanums(10,'test proposal title '),
                           #course_title: random_alphanums(10, 'test course title'),
                           subject_code: 'MATH',
                           course_number: rand(100..999).to_s,
                           transcript_course_title:  random_alphanums(5,'test transcript'),

                           joint_offering_adding_method: ['adv_given_name', 'adv_course_code', 'adv_plain_text', 'auto_lookup'].sample,
                           instructor_adding_method:   ['auto_lookup', 'advanced', 'advanced_username', 'advanced_name'].sample,
                           description_rationale: random_alphanums(200),
                           proposal_rationale:    random_alphanums(200),
                           #GOVERNANCE
                           curriculum_oversight:  'CMNS-Mathematics',
                           #COURSE LOGISTICS
                           scheduling_term:      [:term_any, :term_fall, :term_spring, :term_summer],
                           final_exam_status:    [:exam_standard, :exam_alternate, :exam_none],
                           final_exam_rationale: random_alphanums(50),
                           outcome_type:       '::random::', #['Fixed', 'Multiple', 'Range'].sample,
                           outcome_value:      rand(1..3).to_s,
                           credit_value_min:   rand(1..3).to_s,
                           credit_value_max:   rand(4..8).to_s,
                           outcome_multiple:   rand(1..3).to_s,
                           outcome_multiple2:  rand(4..7).to_s,

                           activity_duration_type: '::random::', #['Day', 'Four Years', 'Half Semester', 'Hours', 'Mini-mester', 'Minutes', 'Month', 'Period', 'Quarter', 'Semester', 'Session', 'TBD', 'Term', 'Two Years', 'Week', 'Year'].sample,
                           activity_type: '::random::', #['Directed', 'Discussion', 'Experiential Learning/Other', 'Homework', 'Lab', 'Lecture', 'Tutorial', 'Web Discuss', 'Web Lecture'].sample,
                           activity_frequency: '::random::', #['per day', 'per month', 'per week'].sample,
                           activity_contacted_hours: rand(1..9).to_s,
                           activity_duration_count: rand(1..9).to_s,
                           activity_class_size: rand(1..9).to_s,

                           #ACTIVE DATES
                           start_term: 'Spring 1980',
                           pilot_course: :set,
                           end_term: 'Fall 1980'
end

Then /^I should see data in required fields for the course proposal$/ do
  on CmCourseInformation do |page|
    page.course_information
    page.proposal_title.value.should == @course_proposal.proposal_title
    page.course_title.value.should == @course_proposal.course_title
    page.page_validation_text.should == "Document was successfully saved."
  end
end


=begin
Then /^I should see data in required fields for the course proposal$/ do
  on(CmCourseInfo).course_information

  on CmCourseInfo do |page|
    page.subject_code.value.should == @course_proposal.subject_code
    page.course_number.value.should == @course_proposal.course_number

    #BUG KSCM-1240
    #page.version_code_version_code.value.should == @course_proposal.version_code_version_code
    #page.version_code_title.value.should == @course_proposal.version_code_title

    page.description_rationale.value.should == @course_proposal.description_rationale
    page.proposal_rationale.value.should == @course_proposal.proposal_rationale
  end

  on CmGovernance do |page|
    page.governance
    page.curriculum_oversight_when_added(@course_proposal.curriculum_oversight).should be_present
  end

  on CmLogistics do |page|
    page.logistics

    page.exam_standard.should be_checked unless @course_proposal.exam_standard.nil?
    page.exam_alternate.should be_checked  unless @course_proposal.exam_alternate.nil?
    page.exam_none.should be_checked unless @course_proposal.exam_none.nil?

    page.final_exam_rationale.value.should == @course_proposal.final_exam_rationale unless page.exam_standard.set?

    page.credit_value.value.should == @course_proposal.outcome_value if @course_proposal.outcome_type == 'Fixed'

    page.credit_value_multiple('0').value.should == @course_proposal.outcome_multiple if @course_proposal.outcome_type == 'Multiple'
    page.credit_value_multiple('1').value.should == @course_proposal.outcome_multiple2 if @course_proposal.outcome_type == 'Multiple'

    page.credit_value_min.value.should == @course_proposal.credit_value_min if @course_proposal.outcome_type == "Range"
    page.credit_value_max.value.should == @course_proposal.credit_value_max if @course_proposal.outcome_type == "Range"

    page.activity_type.selected?(@course_proposal.activity_type).should == true

    page.assessment_a_f.should be_checked if @course_proposal.assessment_a_f == :set
    page.assessment_notation.should be_checked if @course_proposal.assessment_notation == :set
    page.assessment_letter.should be_checked if @course_proposal.assessment_letter == :set
    page.assessment_pass_fail.should be_checked if @course_proposal.assessment_pass_fail == :set
    page.assessment_percentage.should be_checked if @course_proposal.assessment_percentage== :set
    page.assessment_satisfactory.should be_checked if @course_proposal.assessment_satisfactory == :set
  end

  on CmActiveDates do |page|
    page.active_dates
    page.start_term.selected?(@course_proposal.start_term).should == true unless @course_proposal.start_term.nil?
    page.pilot_course.should be_checked
    page.end_term.selected?(@course_proposal.end_term).should == true unless @course_proposal.end_term.nil?
  end
end
=end

#-----
# S3
#-----
Given /^I complete all the fields on the course proposal$/ do
  #@course_proposal.KradCourseProposalNonrequired
  #@course_proposal.course_proposal_nonrequired
  @course_proposal  = create CmCourseProposalObject,

                             subject_code: 'MATH',
                             course_number: rand(100..999).to_s,
                             transcript_course_title:  random_alphanums(5,'test transcript'),

                             joint_offering_adding_method: ['adv_given_name', 'adv_course_code', 'adv_plain_text', 'auto_lookup'].sample,
                             instructor_adding_method:   ['auto_lookup', 'advanced', 'advanced_username', 'advanced_name'].sample,
                             description_rationale: random_alphanums(200),
                             proposal_rationale:    random_alphanums(200),
                             #GOVERNANCE
                             curriculum_oversight:  'CMNS-Mathematics',
                             #COURSE LOGISTICS
                             scheduling_term:      [:term_any, :term_fall, :term_spring, :term_summer],
                             final_exam_status:    [:exam_standard, :exam_alternate, :exam_none],
                             final_exam_rationale: random_alphanums(50),
                             outcome_type:       '::random::', #['Fixed', 'Multiple', 'Range'].sample,
                             outcome_value:      rand(1..3).to_s,
                             credit_value_min:   rand(1..3).to_s,
                             credit_value_max:   rand(4..8).to_s,
                             outcome_multiple:   rand(1..3).to_s,
                             outcome_multiple2:  rand(4..7).to_s,

                             activity_duration_type: '::random::', #['Day', 'Four Years', 'Half Semester', 'Hours', 'Mini-mester', 'Minutes', 'Month', 'Period', 'Quarter', 'Semester', 'Session', 'TBD', 'Term', 'Two Years', 'Week', 'Year'].sample,
                             activity_type: '::random::', #['Directed', 'Discussion', 'Experiential Learning/Other', 'Homework', 'Lab', 'Lecture', 'Tutorial', 'Web Discuss', 'Web Lecture'].sample,
                             activity_frequency: '::random::', #['per day', 'per month', 'per week'].sample,
                             activity_contacted_hours: rand(1..9).to_s,
                             activity_duration_count: rand(1..9).to_s,
                             activity_class_size: rand(1..9).to_s,

                             #ACTIVE DATES
                             start_term: 'Spring 1980',
                             pilot_course: :set,
                             end_term: 'Fall 1980',

                             #COURSE INFORMATION
                             course_listing_subject: 'FREN',
                             course_listing_number: '888',
                             joint_offering_number: 'HIST201',
                             instructor_display_name: 'SMITH, DAVID (s.davidb)',
                             instructor_first_name: 'DAVID',
                             instructor_last_name: 'SMITH',
                             instructor_username: 's.davidb',

                             instructor_adding_method:   'auto_lookup',#['auto_lookup', 'advanced', adv_username', 'adv_name'].sample,
                             joint_offering_adding_data: ['adv_given_name', 'adv_course_code', 'adv_plain_text', 'auto_lookup'].sample,

                             joint_offering_name:        'Interpreting American History: From 1865 to the Present',
                             joint_offering_description: 'The United States from the end of the Civil War to the present.',
                             joint_offering_course_code: 'HIST201',

                             #GOVERNANCE
                             admin_org_adding_method:    ['advanced', 'auto_lookup'].sample,
                             administering_organization: 'Biological Sciences',
                             campus_location:            [:location_all, :location_extended, :location_north, :location_south],
                             #BUG FOR IDENTIFIER
                             # adv_admin_org_identifier: 'ORGID-BISI',
                             adv_admin_org_name:         'Biological Sciences',
                             adv_admin_org_abbreviation: 'BISI',

                             #COURSE LOGISTICS
                             duration_type: '::random::', #['Day', 'Four Years', 'Half Semester', 'Hours', 'Mini-mester', 'Minutes', 'Month', 'Period', 'Quarter', 'Semester', 'Session', 'TBD', 'Term', 'Two Years', 'Week', 'Year'].sample,
                             duration_count: rand(1..9).to_s,
                             audit: :set, pass_fail_transcript_grade: :set,

                             #COURSE REQUISITES

                             eligibility_add_method: 'text', #['text', 'advanced'].sample,
                             rule_adv_course_title: 'American Jewish Experience', rule_adv_course_code: 'HIST106', rule_adv_course_description_snip: 'History of the Jews in America',
                             rule_course_field: '', rule_credit: rand(1..4),

                             student_eligibility_rule: 'Must have successfully completed <course>',
                             corequisiste_rule: 'Must be concurrently enrolled in <course>',
                             recommended_preparation_rule: 'Must have successfully completed <course>',
                             antirequisite_rule: 'Must not have successfully completed <course>',
                             repeatable_for_credit_rule: 'May be repeated for a maximum of <n> credits',
                             course_that_restricts_credits_rule: 'Must not have successfully completed <course>',

                             student_eligibility_course: 'HIST110',
                             student_eligibility_title: 'The Ancient World',
                             student_eligibility_phrase: 'Interpretation of select literature and art of the ancient Mediterranean world with a view to illuminating the antecedents of modern culture; religion and myth in the ancient near East; Greek philosophical, scientific, and literary invention; and the Roman tradition in politics and administration.',

                             corequisite_add_method: ['text', 'advanced'],
                             corequisite_title: 'Germany in the Nineteenth Century, 1815-1914',
                             corequisite_course: 'HIST440',
                             corequisite_phrase: 'Examines the social, economic, cultural, and political development of the major German states before 1871 and of Germany, excluding Austria, from 1871 to 1914.',

                             recommended_preparation_course: 'CHEM277',
                             recommended_preparation_title: 'Fundamentals of Analytical and Bioanalytical Chemistry Laboratory',
                             recommended_preparation_phrase: 'Quantitative analysis, inorganic analytical chemistry, and an introduction to bio-analytical instrumentation and techniques.',

                             antirequisite_course: 'HIST453',
                             antirequisite_title: 'Diplomatic History of the United States from 1914',
                             antirequisite_phrase: 'American foreign relations in the 20th-century. World War I, the Great Depression, World War II, the Cold War, the Korean War, and Vietnam. A continuation of HIST452.',

                             repeatable_for_credit_credit: rand(1..5).to_s,

                             course_that_restricts_credits_course: 'HIST454',
                             course_that_restricts_credits_title: 'Constitutional History of the United States: From Colonial Origins to 1860',
                             course_that_restricts_credits_phrase: 'The interaction of government, law, and politics in the constitutional system. The nature and purpose of constitutions and constitutionalism; the relationship between the constitution and social forces and influences, the way in which constitutional principles, rules, ideas, and institutions affect events and are in turn affected by events. The origins of American politics and constitutionalism through the constitutional convention of 1787. Major constitutional problems such as the origins of judicial review, democratization of government, slavery in the territories and political system as a whole.',

                             corequisite_add_method: ['text', 'advanced'].sample,
                             recommended_preparation_add_method: ['text', 'advanced'].sample,
                             antirequisite_add_method: ['text', 'advanced'].sample,
                             course_that_restricts_credits_add_method: ['text', 'advanced'].sample,
                             student_eligibility_add_method: ['text', 'advanced'].sample,

  #AUTHORS & COLLABORATORS
                             author_name_method: ['auto_lookup', 'advanced_name', 'advanced_username'].sample,
                             author_name_search: 'User',author_username_search: 'user1', author_display_name: 'One, User (user1)',
                             author_permission: '::random::', #['Edit, Comments, View', 'Comments, View', 'View'].sample,
                             action_request: 'FYI',




                             author_notation: :set



end

Then /^I should see data in all non required fields for the course proposal$/ do
  on CmCourseInformation do |page|
    page.course_information
    page.transcript_course_title.value.should == @course_proposal.transcript_course_title
    #page.subject_code.value.should == @course_proposal.subject_code
    #@course_proposal.verify_text_field(page, 'course_listing_subject', 'course_listing_number', 'joint_offering_number')
    page.expand_course_listing_section unless page.collapse_course_listing_section.visible?
    page.course_listing_subject.value.should == @course_proposal.course_listing_subject
    page.course_listing_number.value.should == @course_proposal.course_listing_number
    page.joint_offering_number.value.should == @course_proposal.joint_offering_number
    page.added_instructor_name.value.should == @course_proposal.instructor_display_name
  end

  on CmGovernance do |page|
    page.governance
    #@course_proposal.verify_text_field(page, 'added_administering_organization' )
    page.added_administering_organization.value.should == @course_proposal.administering_organization unless @course_proposal.administering_organization.nil?
    page.location_north.should be_checked if @course_proposal.location_north == 'set'
    page.location_south.should be_checked if @course_proposal.location_south == 'set'
    page.location_extended.should be_checked if @course_proposal.location_extended == 'set'
    page.location_all.should be_checked if @course_proposal.location_all == 'set'
  end

  on CmCourseLogistics do |page|
    page.course_logistics
    page.term_any.should be_checked if @course_proposal.term_any == 'set'
    page.term_fall.should be_checked if @course_proposal.term_fall == 'set'
    page.term_spring.should be_checked if @course_proposal.term_spring == 'set'
    page.term_summer.should be_checked if @course_proposal.term_summer == 'set'

    page.duration_count.value.should == @course_proposal.duration_count unless @course_proposal.duration_count.nil?
    page.duration_type.selected?(@course_proposal.duration_type).should == true unless @course_proposal.duration_type.nil?

    page.audit.should be_checked
    page.pass_fail_transcript_grade.should be_checked

    #@course_proposal.verify_text_field(page, 'activity_contacted_hours', 'activity_duration_count', 'activity_class_size' )

    page.activity_contacted_hours.value.should == @course_proposal.activity_contacted_hours
    page.activity_duration_count.value.should == @course_proposal.activity_duration_count
    page.activity_class_size.value.should == @course_proposal.activity_class_size
    page.activity_frequency.selected?(@course_proposal.activity_frequency).should == true unless @course_proposal.activity_frequency.nil?
    page.activity_duration_type.selected?(@course_proposal.activity_duration_type).should == true unless @course_proposal.activity_duration_type.nil?
  end
  #on KradLearningObjectives do |page|
  #  page.learning_objectives
  #end

  on CmCourseRequisites do |page|
    page.course_requisites

    page.course_requisite_added_rule(@course_proposal.student_eligibility_rule_with_value).should be_present unless @course_proposal.student_eligibility_rule.nil?

    page.course_requisite_added_rule(@course_proposal.corequisite_rule_with_value).should be_present unless @course_proposal.corequisite_rule.nil?

    #page.course_requisite_added_rule().should be_present unless @course_proposal.student_eligibility_prerequisite_rule.nil?
    #page.corequisite_added_rule.should be_present unless @course_proposal.corequisite_rule.nil?

    page.recommended_preparation_added_rule(@course_proposal.recommended_preparation_rule_with_value).should be_present unless @course_proposal.recommended_preparation_rule.nil?
    page.antirequisite_added_rule(@course_proposal.antirequisite_rule_with_value).should be_present unless @course_proposal.antirequisite_rule.nil?
    page.course_that_restricts_credits_added_rule(@course_proposal.course_that_restricts_credits_rule_with_value).should be_present unless @course_proposal.course_that_restricts_credits_rule.nil?

    page.repeatable_for_credit_added_rule(@course_proposal.repeatable_for_credit_rule_with_value).should be_present unless @course_proposal.repeatable_for_credit_rule.nil?

    #page.course_requisite_added_rule('lets fail here').should be_present

  end

    on CmAuthorsCollaborators do |page|
    page.authors_collaborators
    page.added_author_information('edit').should be_present if @course_proposal.author_permission == 'Edit, Comments, View'
    page.added_author_information('comment').should be_present if @course_proposal.author_permission == 'Comments, View'
    page.added_author_information('view').should be_present if @course_proposal.author_permission == 'View'

    page.added_author_information('fyi').should be_present if @course_proposal.action_request == 'FYI'
    page.added_author_information('false').should be_present if @course_proposal.author_notation.nil?
    page.added_author_information('true').should be_present unless @course_proposal.author_notation.nil?

    page.added_author_information(@course_proposal.author_display_name).should be_present
  end
end


#-----
# S4
#-----
When /^I complete all fields on the course proposal with advanced search$/ do
  @course_proposal  = create CmCourseProposalObject,

                             subject_code: 'MATH',
                             course_number: rand(100..999).to_s,
                             transcript_course_title:  random_alphanums(5,'test transcript'),

                             joint_offering_adding_method: ['adv_given_name', 'adv_course_code', 'adv_plain_text'].sample,
                             instructor_adding_method:   ['advanced', 'advanced_username', 'advanced_name'].sample,
                             description_rationale: random_alphanums(200),
                             proposal_rationale:    random_alphanums(200),
                             #GOVERNANCE
                             curriculum_oversight:  'CMNS-Mathematics',
                             #COURSE LOGISTICS
                             scheduling_term:      [:term_any, :term_fall, :term_spring, :term_summer],
                             final_exam_status:    [:exam_standard, :exam_alternate, :exam_none],
                             final_exam_rationale: random_alphanums(50),
                             outcome_type:       '::random::', #['Fixed', 'Multiple', 'Range'].sample,
                             outcome_value:      rand(1..3).to_s,
                             credit_value_min:   rand(1..3).to_s,
                             credit_value_max:   rand(4..8).to_s,
                             outcome_multiple:   rand(1..3).to_s,
                             outcome_multiple2:  rand(4..7).to_s,

                             activity_duration_type: '::random::', #['Day', 'Four Years', 'Half Semester', 'Hours', 'Mini-mester', 'Minutes', 'Month', 'Period', 'Quarter', 'Semester', 'Session', 'TBD', 'Term', 'Two Years', 'Week', 'Year'].sample,
                             activity_type: '::random::', #['Directed', 'Discussion', 'Experiential Learning/Other', 'Homework', 'Lab', 'Lecture', 'Tutorial', 'Web Discuss', 'Web Lecture'].sample,
                             activity_frequency: '::random::', #['per day', 'per month', 'per week'].sample,
                             activity_contacted_hours: rand(1..9).to_s,
                             activity_duration_count: rand(1..9).to_s,
                             activity_class_size: rand(1..9).to_s,

                             #ACTIVE DATES
                             start_term: 'Spring 1980',
                             pilot_course: :set,
                             end_term: 'Fall 1980',

                             #COURSE INFORMATION
                             course_listing_subject: 'FREN',
                             course_listing_number: '888',
                             joint_offering_number: 'HIST201',
                             instructor_display_name: 'SMITH, DAVID (s.davidb)',
                             instructor_first_name: 'DAVID',
                             instructor_last_name: 'SMITH',
                             instructor_username: 's.davidb',

                             instructor_adding_method:   ['advanced', 'adv_username', 'adv_name'].sample,
                             joint_offering_adding_data: ['adv_given_name', 'adv_course_code', 'adv_plain_text'].sample,

                             joint_offering_name:        'Interpreting American History: From 1865 to the Present',
                             joint_offering_description: 'The United States from the end of the Civil War to the present.',
                             joint_offering_course_code: 'HIST201',

                             #GOVERNANCE
                             admin_org_adding_method:    'advanced',
                             administering_organization: 'Biological Sciences',
                             campus_location:            [:location_all, :location_extended, :location_north, :location_south],
                             #BUG FOR IDENTIFIER
                             # adv_admin_org_identifier: 'ORGID-BISI',
                             adv_admin_org_name:         'Biological Sciences',
                             adv_admin_org_abbreviation: 'BISI',

                             #COURSE LOGISTICS
                             duration_type: '::random::', #['Day', 'Four Years', 'Half Semester', 'Hours', 'Mini-mester', 'Minutes', 'Month', 'Period', 'Quarter', 'Semester', 'Session', 'TBD', 'Term', 'Two Years', 'Week', 'Year'].sample,
                             duration_count: rand(1..9).to_s,
                             audit: :set, pass_fail_transcript_grade: :set,

                             #COURSE REQUISITES

                             eligibility_add_method: 'text', #['text', 'advanced'].sample,
                             rule_adv_course_title: 'American Jewish Experience', rule_adv_course_code: 'HIST106', rule_adv_course_description_snip: 'History of the Jews in America',
                             rule_course_field: '', rule_credit: rand(1..4),

                             student_eligibility_rule: 'Must have successfully completed <course>',
                             corequisiste_rule: 'Must be concurrently enrolled in <course>',
                             recommended_preparation_rule: 'Must have successfully completed <course>',
                             antirequisite_rule: 'Must not have successfully completed <course>',
                             repeatable_for_credit_rule: 'May be repeated for a maximum of <n> credits',
                             course_that_restricts_credits_rule: 'Must not have successfully completed <course>',

                             student_eligibility_course: 'HIST110',
                             student_eligibility_title: 'The Ancient World',
                             student_eligibility_phrase: 'Interpretation of select literature and art of the ancient Mediterranean world with a view to illuminating the antecedents of modern culture; religion and myth in the ancient near East; Greek philosophical, scientific, and literary invention; and the Roman tradition in politics and administration.',

                             corequisite_add_method: ['text', 'advanced'],
                             corequisite_title: 'Germany in the Nineteenth Century, 1815-1914',
                             corequisite_course: 'HIST440',
                             corequisite_phrase: 'Examines the social, economic, cultural, and political development of the major German states before 1871 and of Germany, excluding Austria, from 1871 to 1914.',

                             recommended_preparation_course: 'CHEM277',
                             recommended_preparation_title: 'Fundamentals of Analytical and Bioanalytical Chemistry Laboratory',
                             recommended_preparation_phrase: 'Quantitative analysis, inorganic analytical chemistry, and an introduction to bio-analytical instrumentation and techniques.',

                             antirequisite_course: 'HIST453',
                             antirequisite_title: 'Diplomatic History of the United States from 1914',
                             antirequisite_phrase: 'American foreign relations in the 20th-century. World War I, the Great Depression, World War II, the Cold War, the Korean War, and Vietnam. A continuation of HIST452.',

                             repeatable_for_credit_credit: rand(1..5).to_s,

                             course_that_restricts_credits_course: 'HIST454',
                             course_that_restricts_credits_title: 'Constitutional History of the United States: From Colonial Origins to 1860',
                             course_that_restricts_credits_phrase: 'The interaction of government, law, and politics in the constitutional system. The nature and purpose of constitutions and constitutionalism; the relationship between the constitution and social forces and influences, the way in which constitutional principles, rules, ideas, and institutions affect events and are in turn affected by events. The origins of American politics and constitutionalism through the constitutional convention of 1787. Major constitutional problems such as the origins of judicial review, democratization of government, slavery in the territories and political system as a whole.',

                             corequisite_add_method: 'advanced',
                             recommended_preparation_add_method: 'advanced',
                             antirequisite_add_method: 'advanced',
                             course_that_restricts_credits_add_method: 'advanced',
                             student_eligibility_add_method: 'advanced',

                             #AUTHORS & COLLABORATORS
                             author_name_method: ['advanced_name', 'advanced_username'].sample,
                             author_name_search: 'User',author_username_search: 'user1', author_display_name: 'One, User (user1)',
                             author_permission: '::random::', #['Edit, Comments, View', 'Comments, View', 'View'].sample,
                             action_request: 'FYI',

                             joint_offering_adding_data: ['adv_given_name', 'adv_course_code', 'adv_plain_text'].sample,
                             instructor_adding_method: ['adv_username', 'adv_name'].sample,
                             admin_org_adding_method: 'advanced',
                             author_name_method: ['advanced_name', 'advanced_username'].sample,



                            author_notation: :set





  #@course_proposal.create_course_proposal_required
  #@course_proposal.course_proposal_nonrequired

end


#-----
# S5
#-----

When /^I complete all fields on the course proposal with auto\-lookup$/ do
  @course_proposal  = create CmCourseProposalObject,
                           subject_code: 'MATH',
                           course_number: rand(100..999).to_s,
                           transcript_course_title:  random_alphanums(5,'test transcript'),

                           joint_offering_adding_method: ['adv_given_name', 'adv_course_code', 'adv_plain_text'].sample,
                           instructor_adding_method:   ['advanced', 'advanced_username', 'advanced_name'].sample,
                           description_rationale: random_alphanums(200),
                           proposal_rationale:    random_alphanums(200),
                           #GOVERNANCE
                           curriculum_oversight:  'CMNS-Mathematics',
                           #COURSE LOGISTICS
                           scheduling_term:      [:term_any, :term_fall, :term_spring, :term_summer],
                           final_exam_status:    [:exam_standard, :exam_alternate, :exam_none],
                           final_exam_rationale: random_alphanums(50),
                           outcome_type:       '::random::', #['Fixed', 'Multiple', 'Range'].sample,
                           outcome_value:      rand(1..3).to_s,
                           credit_value_min:   rand(1..3).to_s,
                           credit_value_max:   rand(4..8).to_s,
                           outcome_multiple:   rand(1..3).to_s,
                           outcome_multiple2:  rand(4..7).to_s,

                           activity_duration_type: '::random::', #['Day', 'Four Years', 'Half Semester', 'Hours', 'Mini-mester', 'Minutes', 'Month', 'Period', 'Quarter', 'Semester', 'Session', 'TBD', 'Term', 'Two Years', 'Week', 'Year'].sample,
                           activity_type: '::random::', #['Directed', 'Discussion', 'Experiential Learning/Other', 'Homework', 'Lab', 'Lecture', 'Tutorial', 'Web Discuss', 'Web Lecture'].sample,
                           activity_frequency: '::random::', #['per day', 'per month', 'per week'].sample,
                           activity_contacted_hours: rand(1..9).to_s,
                           activity_duration_count: rand(1..9).to_s,
                           activity_class_size: rand(1..9).to_s,

                           #ACTIVE DATES
                           start_term: 'Spring 1980',
                           pilot_course: :set,
                           end_term: 'Fall 1980',

                           #COURSE INFORMATION
                           course_listing_subject: 'FREN',
                           course_listing_number: '888',
                           joint_offering_number: 'HIST201',
                           instructor_display_name: 'SMITH, DAVID (s.davidb)',
                           instructor_first_name: 'DAVID',
                           instructor_last_name: 'SMITH',
                           instructor_username: 's.davidb',

                           instructor_adding_method:   'auto_lookup',#['auto_lookup', 'advanced', adv_username', 'adv_name'].sample,
                           joint_offering_adding_data: ['adv_given_name', 'adv_course_code', 'adv_plain_text'].sample,

                           joint_offering_name:        'Interpreting American History: From 1865 to the Present',
                           joint_offering_description: 'The United States from the end of the Civil War to the present.',
                           joint_offering_course_code: 'HIST201',

                           #GOVERNANCE
                           admin_org_adding_method:    'auto_lookup',
                           administering_organization: 'Biological Sciences',
                           campus_location:            [:location_all, :location_extended, :location_north, :location_south],
                           #BUG FOR IDENTIFIER
                           # adv_admin_org_identifier: 'ORGID-BISI',
                           adv_admin_org_name:         'Biological Sciences',
                           adv_admin_org_abbreviation: 'BISI',

                           #COURSE LOGISTICS
                           duration_type: '::random::', #['Day', 'Four Years', 'Half Semester', 'Hours', 'Mini-mester', 'Minutes', 'Month', 'Period', 'Quarter', 'Semester', 'Session', 'TBD', 'Term', 'Two Years', 'Week', 'Year'].sample,
                           duration_count: rand(1..9).to_s,
                           audit: :set, pass_fail_transcript_grade: :set,

                           #COURSE REQUISITES
                           eligibility_add_method: 'text', #['text', 'advanced'].sample,
                           rule_adv_course_title: 'American Jewish Experience', rule_adv_course_code: 'HIST106', rule_adv_course_description_snip: 'History of the Jews in America',
                           rule_course_field: '', rule_credit: rand(1..4),

                           student_eligibility_rule: 'Must have successfully completed <course>',
                           corequisiste_rule: 'Must be concurrently enrolled in <course>',
                           recommended_preparation_rule: 'Must have successfully completed <course>',
                           antirequisite_rule: 'Must not have successfully completed <course>',
                           repeatable_for_credit_rule: 'May be repeated for a maximum of <n> credits',
                           course_that_restricts_credits_rule: 'Must not have successfully completed <course>',

                           student_eligibility_course: 'HIST110',
                           student_eligibility_title: 'The Ancient World',
                           student_eligibility_phrase: 'Interpretation of select literature and art of the ancient Mediterranean world with a view to illuminating the antecedents of modern culture; religion and myth in the ancient near East; Greek philosophical, scientific, and literary invention; and the Roman tradition in politics and administration.',

                           corequisite_add_method: ['text', 'advanced'],
                           corequisite_title: 'Germany in the Nineteenth Century, 1815-1914',
                           corequisite_course: 'HIST440',
                           corequisite_phrase: 'Examines the social, economic, cultural, and political development of the major German states before 1871 and of Germany, excluding Austria, from 1871 to 1914.',

                           recommended_preparation_course: 'CHEM277',
                           recommended_preparation_title: 'Fundamentals of Analytical and Bioanalytical Chemistry Laboratory',
                           recommended_preparation_phrase: 'Quantitative analysis, inorganic analytical chemistry, and an introduction to bio-analytical instrumentation and techniques.',

                           antirequisite_course: 'HIST453',
                           antirequisite_title: 'Diplomatic History of the United States from 1914',
                           antirequisite_phrase: 'American foreign relations in the 20th-century. World War I, the Great Depression, World War II, the Cold War, the Korean War, and Vietnam. A continuation of HIST452.',

                           repeatable_for_credit_credit: rand(1..5).to_s,

                           course_that_restricts_credits_course: 'HIST454',
                           course_that_restricts_credits_title: 'Constitutional History of the United States: From Colonial Origins to 1860',
                           course_that_restricts_credits_phrase: 'The interaction of government, law, and politics in the constitutional system. The nature and purpose of constitutions and constitutionalism; the relationship between the constitution and social forces and influences, the way in which constitutional principles, rules, ideas, and institutions affect events and are in turn affected by events. The origins of American politics and constitutionalism through the constitutional convention of 1787. Major constitutional problems such as the origins of judicial review, democratization of government, slavery in the territories and political system as a whole.',

                           corequisite_add_method: 'text',
                           recommended_preparation_add_method: 'text',
                           antirequisite_add_method: 'text',
                           course_that_restricts_credits_add_method: 'text',
                           student_eligibility_add_method: 'text',

                           #AUTHORS & COLLABORATORS
                           author_name_method: 'auto_lookup',
                           author_name_search: 'User',author_username_search: 'user1', author_display_name: 'One, User (user1)',
                           author_permission: '::random::', #['Edit, Comments, View', 'Comments, View', 'View'].sample,
                           action_request: 'FYI',

                           joint_offering_adding_data: 'auto_lookup',
                           instructor_adding_method: 'auto_lookup',
                           admin_org_adding_method: 'auto_lookup',
                           author_name_method: 'auto_lookup',

                           author_notation: :set,

                           instructor_adding_method: 'auto_lookup',
                           joint_offering_adding_data: 'auto_lookup',
                           admin_org_adding_method: 'auto_lookup',
                           author_name_method: 'auto_lookup'

  #@course_proposal.create_course_proposal_required
  #@course_proposal.course_proposal_nonrequired

end

