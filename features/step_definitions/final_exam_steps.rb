When /^I change the final exam period start date to be before the term start date and save$/ do
  #TODO: this modifies reference data
  @calendar = make AcademicCalendar, :year => "2012"

  term = make AcademicTermObject, :parent_calendar => @calendar, :term => "Fall"
  @calendar.terms << term

  exam_period = make ExamPeriodObject, :parent_term => term
  @calendar.terms[0].exam_period = exam_period

  @calendar.terms[0].exam_period.edit :start_date => "08/15/2012"
end

When /^I change the final exam period end date to be after the term end date and save$/ do
  #TODO: this modifies reference data
  @calendar = make AcademicCalendar, :year => "2012"

  term = make AcademicTermObject, :parent_calendar => @calendar, :term => "Fall"
  @calendar.terms << term

  exam_period = make ExamPeriodObject, :parent_term => term
  @calendar.terms[0].exam_period = exam_period

  @calendar.terms[0].exam_period.edit :start_date => "08/15/2012"
end

When /^I add a final exam period to the new academic calender and save$/ do
  @calendar = create AcademicCalendar

  term = make AcademicTermObject, :parent_calendar => @calendar, :start_date=>"08/20/#{@calendar.year}",
               :end_date=>"12/10/#{@calendar.year}", :term => "Fall"
  @calendar.add_term term

  exam_period = make ExamPeriodObject, :parent_term => term, :start_date=>"12/11/#{@calendar.year}",
                     :end_date=>"12/20/#{@calendar.year}"
  @calendar.terms[0].add_exam_period exam_period
  @calendar.terms[0].save
end

When /^I copy a newly created academic calendar that has a defined final exam period$/ do
  @source_calendar = create AcademicCalendar

  term = make AcademicTermObject, :parent_calendar => @source_calendar, :start_date=>"09/20/#{@source_calendar.year}",
               :end_date=>"12/10/#{@source_calendar.year}"
  @source_calendar.add_term term

  exam_period = make ExamPeriodObject, :parent_term => term, :start_date=>"12/11/#{@source_calendar.year}",
                     :end_date=>"12/20/#{@source_calendar.year}"
  @source_calendar.terms[0].add_exam_period exam_period
  @source_calendar.terms[0].save

  @calendar = make AcademicCalendar, :year => "#{@source_calendar.year.to_i + 1}"
  @calendar.copy_from @source_calendar.name
end

When /^I copy an existing academic calendar that has a defined final exam period$/ do
  @source_calendar = make AcademicCalendar, :year => "2012", :name => "2012-2013 Academic Calendar"

  term = make AcademicTermObject, :parent_calendar => @source_calendar
  @source_calendar.terms << term

  @calendar = make AcademicCalendar

  @calendar.copy_from @source_calendar.name
end

Given /^that the catalog version of the course is set to have a standard final exam$/ do
  @course_offering = make CourseOffering, :term => "201301", :course => "PHYS603", :defer_save => true
end

Given /^that the catalog version of the course is set to not have a standard final exam$/ do
  @course_offering = make CourseOffering, :term => "201208", :course => "ENGL101", :defer_save => true,
                          :final_exam_type => "ALTERNATE"
end

Given /^that the catalog version of the course is set to have No final exam$/ do
  @course_offering = make CourseOffering, :term => "201301", :course => "PHYS603", :defer_save => true,
                          :final_exam_type => "NONE"
end

When /^I create a Course Offering from catalog in a term with a final exam period$/ do
  @course_offering.create
end

When /^I create a Course Offering from catalog with an alternate final assessment option$/ do
  @course_offering = create CourseOffering, :term => "201208", :course => "ENGL101", :final_exam_type => "ALTERNATE"
end

When /^I create a Course Offering from an existing Course Offering with an alternate final assessment option$/ do
  @course_offering = create CourseOffering, :create_from_existing => (make CourseOffering, :term => "201208", :course => "ENGL101S")
end

When /^I edit the Course Offering to have a Standard Final Exam$/ do
  @course_offering.edit :final_exam_type => "Standard Final Exam"
end

And /^I select a Final Exam Driver option from the drop-down$/ do
  @course_offering.edit :final_exam_driver => "Final Exam Per Course Offering"
end

When /^I return to the Edit Co page for the course after updating the change$/ do
  on(ManageCourseOfferings).edit_course_offering
end

When /^I create a Course Offering from an existing Course Offering with a standard final exam option$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "CHEM277")

  @activity_offering = make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.edit :send_to_scheduler => true

  @course_offering_copy = create CourseOffering, :term=> @course_offering.term , :create_from_existing => @course_offering
  on(ManageCourseOfferings).edit_course_offering
end

When /^I create a course offering for a subject with a standard final exam in my admin org$/ do
  @course_offering = make CourseOffering, :term=> "201301", :course => "ENGL304"
  @course_offering.start_create_by_search
end

When /^I edit a course offering with a standard final exm in my admin org$/ do
  @course_offering = make CourseOffering, :term=> "201301", :course=>"ENGL304"
  @course_offering.manage
  on(ManageCourseOfferings).edit_course_offering
end

When /^I create an Academic Calendar and add an official term$/ do
  @calendar = create AcademicCalendar

  term = make AcademicTermObject, :parent_calendar => @calendar
  @calendar.add_term term

  exam_period = make ExamPeriodObject, :parent_term => term
  @calendar.terms[0].add_exam_period exam_period
  @calendar.terms[0].save

  @calendar.terms[0].make_official
  @manage_soc = make ManageSoc, :term_code => @calendar.terms[0].term_code
  @manage_soc.set_up_soc
end

When /^I create an Academic Calendar and add an official term with no exam period$/ do
  @calendar = create AcademicCalendar

  term = make AcademicTermObject, :parent_calendar => @calendar

  @calendar.add_term term

  @calendar.terms[0].make_official
  @manage_soc = make ManageSoc, :term_code => @calendar.terms[0].term_code
  @manage_soc.set_up_soc
end

When /^I have created a Final Exam Period for the term in the newly created Academic Calendar$/ do
  @calendar.terms[0].exam_period.edit :start_date=>"12/11/#{@calendar.year}", :end_date=>"12/20/#{@calendar.year}"
  @calendar.terms[0].save
end

When /^I have multiple Course Offerings each with a different Exam Offering in the source term$/ do
  @co_list = []

  @co_list << (create CourseOffering, :term => @calendar.terms[0].term_code, :course => "PHYS603",
                      :final_exam_type => "NONE")
  @co_list << (create CourseOffering, :term => @calendar.terms[0].term_code, :course => "PHYS603",
                      :final_exam_type => "ALTERNATE")
  @co_list << (create CourseOffering, :term => @calendar.terms[0].term_code, :course => "PHYS603",
                      :final_exam_driver => "Final Exam Per Activity Offering")
  co = make CourseOffering, :term => @calendar.terms[0].term_code, :course => "PHYS603", :use_final_exam_matrix => false
  co.delivery_format_list[0].format = "Lecture"
  co.delivery_format_list[0].grade_format = "Course Offering"
  co.delivery_format_list[0].final_exam_activity = "Lecture"
  @co_list << co.create
end

And /^I rollover the source term to a new academic term$/ do
  @calendar_target = create AcademicCalendar, :year => @calendar.year.to_i + 1

  term_target = make AcademicTermObject, :parent_calendar => @calendar_target
  @calendar_target.add_term term_target

  @calendar_target.terms[0].make_official

  @manage_soc = make ManageSoc, :term_code => @calendar.terms[0].term_code
  @manage_soc.set_up_soc

  @rollover = make Rollover, :target_term => @calendar_target.terms[0].term_code ,
                   :source_term => @calendar.terms[0].term_code,
                   :exp_success => false
  @rollover.perform_rollover
  @rollover.wait_for_rollover_to_complete
end

When /^I create multiple Course Offerings each with a different Exam Driver in the new term$/ do
  @co_list = []
  course_offering = make CourseOffering, :term => @calendar.terms[0].term_code,
                           :course => "BSCI215",
                           :final_exam_driver => "Final Exam Per Course Offering"
  course_offering.delivery_format_list[0].format = "Lecture"
  course_offering.delivery_format_list[0].grade_format = "Course Offering"
  course_offering.delivery_format_list[0].final_exam_activity = "Lecture"
  @co_list << course_offering.create

  @co_list[0].create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")

  course_offering = create CourseOffering, :term => @calendar.terms[0].term_code,
                           :course => "ENGL301",
                           :final_exam_driver => "Final Exam Per Activity Offering"
  @co_list << course_offering

  course_offering = (make CourseOffering, :term => @calendar.terms[0].term_code, :course => "PHYS272",
                      :final_exam_driver => "Final Exam Per Activity Offering")
  course_offering.delivery_format_list[0].format = "Lecture"
  course_offering.delivery_format_list[0].grade_format = "Course Offering"
  course_offering.delivery_format_list[0].final_exam_activity = "Lecture"
  @co_list << course_offering.create
  @co_list[2].create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")

  @co_list << (create CourseOffering, :term => @calendar.terms[0].term_code, :course => "CHEM611",
                      :final_exam_driver => "Final Exam Per Course Offering")
end

When /^I rollover the term to a new academic term that has no exam period$/ do
  @calendar_target = create AcademicCalendar, :year => @calendar.year.to_i + 1

  term_target = make AcademicTermObject, :parent_calendar => @calendar_target
  @calendar_target.add_term term_target

  @calendar_target.terms[0].make_official

  @manage_soc = make ManageSoc, :term_code => @calendar.terms[0].term_code
  @manage_soc.set_up_soc

  @rollover = make Rollover, :target_term => @calendar_target.terms[0].term_code,
                   :source_term => @calendar.terms[0].term_code,
                   :exp_success => false, :defer_continue_wo_exams => true
  @rollover.perform_rollover
end

When /^I rollover the term to a new academic term that has an exam period$/ do
  @calendar_target = create AcademicCalendar, :year => @calendar.year.to_i + 1

  term_target = make AcademicTermObject, :parent_calendar => @calendar_target
  @calendar_target.add_term term_target

  exam_period = make ExamPeriodObject, :parent_term => term_target
  @calendar_target.terms[0].add_exam_period exam_period
  @calendar_target.terms[0].save

  @calendar_target.terms[0].make_official

  @manage_soc = make ManageSoc, :term_code => @calendar_target.terms[0].term_code
  @manage_soc.set_up_soc

  @rollover = make Rollover, :target_term => @calendar_target.terms[0].term_code ,
                   :source_term => @calendar.terms[0].term_code,
                   :exp_success => false
  @rollover.perform_rollover
  @rollover.wait_for_rollover_to_complete
end

When /^I view the Exam Offerings for a CO created from an existing CO with a standard final exam driven by Course Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL304")
  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"

  @activity_offering = make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.edit :send_to_scheduler => true

  @create_co = create CourseOffering, :term=> @course_offering.term, :create_from_existing => @course_offering

  on(ManageCourseOfferings).view_exam_offerings
end

Given /^there is an exsiting CO with a Standard Final Exam option$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  @activity_offering = make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.edit :send_to_scheduler => true
end

Given /^that Activity Offerings exist for the selected Course Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL304")
  #setup existing format
  @course_offering.delivery_format_list[0].format = "Lecture/Discussion"
  @activity_offering = make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.edit :send_to_scheduler => true
end

When /^I create a Course Offering from an existing CO with a Standard Final Exam option$/ do
  @create_co = create CourseOffering, :term=> @course_offering.term, :create_from_existing => @course_offering
end

When /^I select Final Exam Per Course Offering as the Final Exam Driver and Update the Course Offering$/ do
  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"
end

Then /^I should be able to select the View Exam Offerings link on the Manage CO page$/ do
  on ManageCourseOfferings do |page|
    page.view_exam_offerings_link.present?.should == true
    page.view_exam_offerings
  end
end

Then /^see Exam Offerings for each Activity Offering of the Course with a status of ([^"]*)$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Activity Offering/
    array = page.ao_code_list
    if array != nil
      array.each do |code|
        page.get_eo_by_ao_status_text(code).should match /#{exp_state}/
      end
      no_of_eos = array.length
    end
    array = page.ao_code_list("CL Leftovers")
    if array != nil
      array.each do |code|
        page.get_eo_by_ao_status_text(code, "CL Leftovers").should match /#{exp_state}/
      end
      no_of_eos = array.length
    end
    no_of_eos.should == 1
  end
end

Then /^see one Exam Offering for the Course Offering with a status of ([^"]*)$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Course Offering/
    page.get_eo_by_co_status_text.should match /#{exp_state}/
    page.co_eo_count.should == "1"
  end
end

When /^I view the Exam Offerings for a CO created from an existing CO with a standard final exam driven by Activity Offering$/ do
  @course_offering = make CourseOffering, :term => "201208", :course => "ENGL305"
  @create_co = create CourseOffering, :term=> @course_offering.term, :create_from_existing => @course_offering
  on(ManageCourseOfferings).view_exam_offerings
end

When /^I select Final Exam Per Activity Offering as the Final Exam Driver and Update the Course Offering$/ do
  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :defer_save => true
  @course_offering.delivery_format_list[0].edit :final_exam_activity => "Lecture", :start_edit => false
end

When /^I view the Exam Offerings for a CO created from an existing CO with multiple AOs and a standard final exam driven by Activity Offering$/ do
  @course_offering = make CourseOffering, :term => "201208", :course => "HIST110"
  @create_co = create CourseOffering, :term=> @course_offering.term, :create_from_existing => @course_offering

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings for a CO with two AOs and a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL201")
  on(ManageCourseOfferings).view_exam_offerings
end

Given /^that the CO has two existing AOs and a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL201")
  @activity_offering = make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.edit :send_to_scheduler => true
end

When /^I add two new AOs to the CO and then create a copy of the CO$/ do
  @add_ao_one = @course_offering.create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")
  @add_ao_two = @course_offering.create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")

  @create_co = create CourseOffering, :term=> @course_offering.term, :create_from_existing => @course_offering

  on(ManageCourseOfferings).view_exam_offerings
end

#TODO: confirm not used
When /^I view the Exam Offerings for a CO with two new AOs and a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL201")
  @activity_offering = @course_offering.activity_offering_cluster_list[0].ao_list[0]
  # make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.edit :send_to_scheduler => true

  @add_ao_one = @course_offering.create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")
  @add_ao_two = @course_offering.create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")

  @create_co = create CourseOffering, :term=> @course_offering.term, :create_from_existing => @course_offering

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I create a CO with two new AOs and then view the Exam Offerings where the CO has a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL201")
  @activity_offering = @course_offering.activity_offering_cluster_list[0].ao_list[0]
  #  make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.edit :send_to_scheduler => true

  @add_ao_one = @course_offering.create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")
  @add_ao_two = @course_offering.create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only"), :navigate_to_page => false

  @create_co = create CourseOffering, :term=> @course_offering.term, :create_from_existing => @course_offering

  on(ManageCourseOfferings).view_exam_offerings
end

Given /^that the SOC state is prior to Published$/ do
  @term = "201301"
end

When /^I view the Exam Offerings for a CO with a standard final exam driven by Course Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => @term, :course => "ENGL304")
  @course_offering.delivery_format_list[0].format = "Lecture/Discussion"

  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings for a CO in an Open SOC with a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201301", :course => "ENGL304")
  @course_offering.delivery_format_list[0].format = "Lecture/Discussion"

  @course_offering.edit :final_exam_type => "Standard Final Exam", :final_exam_driver => "Final Exam Per Activity Offering",
                        :defer_save => true

  @course_offering.delivery_format_list[0].edit :final_exam_activity => "Lecture", :start_edit => false

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings for a CO where the Course Offering Standard FE is changed to No Final Exam$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL305")
  # @course_offering.delivery_format_list[0].format = "Lecture Only"

  @course_offering.edit :final_exam_type => "Standard Final Exam", :final_exam_driver => "Final Exam Per Course Offering"

  @course_offering.edit :final_exam_type => "No Final Exam or Assessment"

  on(ManageCourseOfferings).view_exam_offerings
end

Given /^that the CO is set to have exam offerings driven by CO$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL305")
  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"
end

Given /^that the CO is set to have exam offerings driven by AO$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL305")
  @course_offering.delivery_format_list[0].format = "Lecture Only"
end

When /^I edit the CO to have an Alternate Final Exam$/ do
  @course_offering.manage_and_init

  @course_offering.edit :final_exam_type => "Alternate Final Assessment"
end

When /^I view the Exam Offerings for a CO where the Course Offering Standard FE setting is changed to No Final Exam$/ do
  @course_offering.edit :final_exam_type => "No Final Exam or Assessment"

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings for a CO where the Activity Offering Standard FE is changed to Alternate Final Exam$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL304")
  @course_offering.delivery_format_list[0].format = "Lecture/Discussion"
  @course_offering.manage_and_init

  @course_offering.edit :final_exam_type => "Alternate Final Assessment"

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings for a CO where the Course Offering No FE is changed to Standard Final Exam$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL304")
  @course_offering.edit :final_exam_type => "No Final Exam or Assessment"

  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver =>"Final Exam Per Course Offering"

  on(ManageCourseOfferings).view_exam_offerings
end

Given /^that the CO is set to have no exam offerings$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL304")
  @course_offering.edit :final_exam_type => "No Final Exam or Assessment"
end

When /^I view the Exam Offerings for a CO where the Course Offering No Standard Final Exam or Assessment is changed to Standard Final Exam driven by Course Offering$/ do
  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver =>"Final Exam Per Course Offering"

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings for a CO where the exam is changed to Standard Final Exam driven by Activity Offering$/ do
  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :defer_save => true
  @course_offering.delivery_format_list[0].edit :final_exam_activity => "Lecture", :start_edit => false

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings after changing the Final Exam Driver (?:to|back to) Course Offering$/ do
  @course_offering.manage
  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offering after changing the Course Offering back to Standard FE and the Final Exam Driver to Course Offering$/ do
  @course_offering.manage
  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings? after changing the Course Offering back to Standard FE and the Final Exam Driver to Activity Offering$/ do
  @course_offering.manage

  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :defer_save => true
  @course_offering.delivery_format_list[0].edit :final_exam_activity => "Lecture", :start_edit => false

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings after changing the Final Exam Driver to Activity Offering$/ do
  @course_offering.manage

  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :defer_save => true
  @course_offering.delivery_format_list[0].edit :final_exam_activity => "Lecture", :start_edit => false

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings after updating the Final Exam indicator to No final Exam$/ do
  @course_offering.manage
  @course_offering.edit :final_exam_type => "No Final Exam or Assessment"

  #on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings for a CO created from catalog with a standard final exam driven by Course Offering$/ do
  @course_offering = create CourseOffering, :term => "201208", :course => "ENGL304"

  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I add an Exam Period to the term$/ do
  exam_period = make ExamPeriodObject, :parent_term => @calendar.terms[0], :start_date=>"12/11/#{@calendar.year}",
                      :end_date=>"12/20/#{@calendar.year}"

  @calendar.terms[0].add_exam_period exam_period
  @calendar.terms[0].save
end

When /^I deselect Exclude Saturday and Exclude Sunday for the Exam Period$/ do
  @calendar.terms[0].exam_period.edit :exclude_saturday => false, :exclude_sunday => false, :navigate_to_page => false
  @calendar.terms[0].save
end

When /^I create a Fall Term Exam Period with 2 fewer days than the number of Final Exam Matrix days$/ do
  term = make AcademicTermObject, :parent_calendar => @calendar, :start_date => "09/01/#{@calendar.year}",
                 :end_date=>"12/20/#{@calendar.year}"
  @calendar.add_term term

  exam_period = make ExamPeriodObject, :parent_term => @calendar.terms[0], :start_date => "12/01/#{@calendar.year}", :length_ex_weekend => 4
  @calendar.terms[0].add_exam_period exam_period

  @calendar.terms[0].save :exp_success => false
end

When /^there is more than one Activity Offering for the Course$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
end

When /^I cancel an Activity Offering for a CO with a standard final exam driven by Course Offering$/ do
  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"

  @activity_offering = make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.cancel :navigate_to_page => false
end

When /^I cancel an Activity Offering for a CO with a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  @activity_offering = make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.cancel :navigate_to_page => false
end

Given /^that the Lecture AO that drives the exam is not in a cancelled state$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormatObject, :format => "Lecture/Discussion", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
end

When /^I cancel a Discussion Activity Offering for a CO with a standard final exam driven by Activity Offering$/ do
  @activity_offering = make ActivityOfferingObject, :code => "C", :parent_course_offering => @course_offering
  @activity_offering.cancel :navigate_to_page => false
end

When /^I cancel all Activity Offerings for a CO with a standard final exam driven by Course Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"

  on ManageCourseOfferings do |page|
    page.codes_list.each do |code|
      ao_cancel = make ActivityOfferingObject, :code => code, :parent_course_offering => @course_offering
      ao_cancel.cancel :navigate_to_page => false
    end
  end
end

When /^I reinstate the one or more Activity offerings for the CO$/ do
  on ManageCourseOfferings do |page|
    page.cluster_select_all_aos
    page.reinstate_ao
  end
  on(ReinstateActivityOffering).reinstate_activity
end

When /^I cancel all Activity Offerings for a CO with a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormatObject, :format => "Lecture/Discussion", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list

  on ManageCourseOfferings do |page|
    page.codes_list.each do |code|
      ao_cancel = make ActivityOfferingObject, :code => code, :parent_course_offering => @course_offering
      ao_cancel.cancel :navigate_to_page => false
    end
  end
end

When /^I? ?update all fields on the exam offering RSI$/ do
  @eo_rsi.edit :do_navigation => false,
               :day => 'Day 5',
               :start_time => '4:00 PM',
               :end_time => '4:50 PM'
end

When /^I select matrix override and update the day and time fields on the exam offering RSI$/ do
  @eo_rsi.edit :do_navigation => false,
               :day => 'Day 2',
               :start_time => '6:00 PM',
               :end_time => '7:00 PM',
               :override_matrix => true
end

When /^I subsequently remove matrix override from the exam offering RSI$/ do
  @eo_rsi.edit :override_matrix => true, :do_navigation => false
end

When /^I update the day and time fields on the exam offering RSI$/ do
  @eo_rsi.edit :do_navigation => false,
               :day => 'Day 2',
               :start_time => '6:00 PM',
               :end_time => '7:00 PM',
               :override_matrix => false
end

When /^I update the requested scheduling information for the related activity offering so there is no match on the exam matrix$/ do
  @course_offering.manage
  @activity_offering.edit :defer_save => true
  @activity_offering.requested_scheduling_information_list[0].edit :days => "SU",
                                                                   :start_time => "10:00", :start_time_ampm => "am",
                                                                   :end_time => "10:50", :end_time_ampm => "am"
  @activity_offering.save
end

When /^I update the scheduling information for the related activity offering and send to the scheduler$/ do
  @course_offering.manage
  @activity_offering.edit :defer_save => true
  @activity_offering.requested_scheduling_information_list[0].edit :days => "SU",
                                                                   :start_time => "10:00", :start_time_ampm => "am",
                                                                   :end_time => "10:50", :end_time_ampm => "am",
                                                                   :send_to_scheduler => true
  @activity_offering.save
end

When /^delete the contents of the exam offering RSI facility and room number fields$/ do
  @eo_rsi.edit :do_navigation => false, :facility => '', :room => '', :override_matrix => true
end

When /^blank the exam offering RSI Day field$/ do
  @eo_rsi.edit :do_navigation => false, :day => '', :override_matrix => true, :exp_success=> false
end

When /^enter a blank time in the exam offering RSI end time field$/ do
  @eo_rsi.edit :do_navigation => false, :end_time => '', :override_matrix => true, :exp_success=> false
end

When /^enter an invalid room code in the exam offering RSI room field$/ do
  @eo_rsi.edit :do_navigation => false, :room => '98989', :override_matrix => true, :exp_success=> false
end

When /^enter an invalid facility code in the exam offering RSI facility field$/ do
  @eo_rsi.edit :do_navigation => false, :facility => 'NX2', :override_matrix => true, :exp_success=> false
end

When /^enter an invalid time in the exam offering RSI start time field$/ do
  @eo_rsi.edit :do_navigation => false, :start_time => '13:00 AM', :override_matrix => true, :exp_success=> false
end

When /^the error displayed for AO-driven exam offerings RSI day field is: (.*?)$/ do |expected_errMsg|
  on ViewExamOfferings do |page|
    row = page.eo_by_ao_target_row(@activity_offering.code)
    page.rsi_day(row).click
    popup_text = page.div(id: /jquerybubblepopup/, data_for: "#{page.rsi_day(row).id}").table.text
    popup_text.should match /#{expected_errMsg}/
    page.cancel
  end
end

When /^the error displayed for CO-driven exam offerings RSI start time is: (.*?)$/ do |expected_errMsg|
  on ViewExamOfferings do |page|
    row = page.co_target_row
    element = page.rsi_start_time(row)
    element.click
    popup_text = page.div(id: /jquerybubblepopup/, data_for: "#{element.id}").table.text
    popup_text.should match /#{expected_errMsg}/
    page.cancel
  end
end

When /^the error displayed for AO-driven exam offerings RSI end time is: (.*?)$/ do |expected_errMsg|
  on ViewExamOfferings do |page|
    row = page.eo_by_ao_target_row(@activity_offering.code)
    element = page.rsi_end_time(row)
    element.click
    popup_text = page.div(id: /jquerybubblepopup/, data_for: "#{element.id}").table.text
    popup_text.should match /#{expected_errMsg}/
    page.cancel
  end
end

When /^the error displayed for AO-driven exam offerings RSI facility is: (.*?)$/ do |expected_errMsg|
  on ViewExamOfferings do |page|
    row = page.eo_by_ao_target_row(@activity_offering.code)
    element = page.rsi_facility(row)
    element.click
    popup_text = page.div(id: /jquerybubblepopup/, data_for: "#{element.id}").table.text
    popup_text.should match /#{expected_errMsg}/
    page.cancel
  end
end

When /^the error displayed for AO-driven exam offerings RSI room is: (.*?)$/ do |expected_errMsg|
  on ViewExamOfferings do |page|
    row = page.eo_by_ao_target_row(@activity_offering.code)
    element = page.rsi_room(row)
    element.click
    popup_text = page.div(id: /jquerybubblepopup/, data_for: "#{element.id}").table.text
    popup_text.should match /#{expected_errMsg}/
    page.cancel
  end
end

When /^I (?:view|manage) the Exam Offerings for the Course Offering$/ do
  on(ManageCourseOfferings).view_exam_offerings
end

When /^the CO-driven exam offering RSI is successfully updated$/ do
  @course_offering.manage
  on(ManageCourseOfferings).view_exam_offerings
  on ViewExamOfferings do |page|
    page.co_target_row.exists?.should be_true
    page.get_eo_by_co_days_text.should match /#{@eo_rsi.day}/
    page.get_eo_by_co_st_time_text.should == @eo_rsi.start_time
    page.get_eo_by_co_end_time_text.should == @eo_rsi.end_time
    #page.get_eo_by_co_bldg_text.should == @eo_rsi.facility TODO: issue with short vs full facility name
    page.get_eo_by_co_room_text.should == @eo_rsi.room
  end
end

When /^the CO-driven exam offering RSI is updated according to the exam matrix$/ do
  @course_offering.manage
  on(ManageCourseOfferings).view_exam_offerings
  on ViewExamOfferings do |page|
    page.co_target_row.exists?.should be_true
    page.get_eo_by_co_days_text.should match @matrix.rules[0].rsi_days
    page.get_eo_by_co_st_time_text.should == "#{@matrix.rules[0].start_time} #{@matrix.rules[0].st_time_ampm}"
    page.get_eo_by_co_end_time_text.should == "#{@matrix.rules[0].end_time} #{@matrix.rules[0].end_time_ampm}"
    #page.get_eo_by_co_bldg_text.should == @matrix.rules[0].facility TODO: issue with short vs full facility name
    page.get_eo_by_co_room_text.should == @matrix.rules[0].room
  end
end

When /^the AO-driven exam offering RSI is not updated$/ do
  @course_offering.manage
  on(ManageCourseOfferings).view_exam_offerings
  code = @activity_offering.code
  on ViewExamOfferings do |page|
    page.eo_by_ao_target_row(code).exists?.should be_true
    page.get_eo_by_ao_days_text(code).should match /#{@eo_rsi.day}/
    page.get_eo_by_ao_st_time_text(code).should == @eo_rsi.start_time
    page.get_eo_by_ao_end_time_text(code).should == @eo_rsi.end_time
    #page.get_eo_by_ao_bldg_text(code).should == @eo_rsi.facility TODO: issue with short vs full facility name
    page.get_eo_by_ao_room_text(code).should == @eo_rsi.room
  end
end

When /^the exam offering RSI is blank according to the new AO RSI information$/ do
  @course_offering.manage
  on(ManageCourseOfferings).view_exam_offerings
  code = @activity_offering.code
  on ViewExamOfferings do |page|
    page.eo_by_ao_target_row(code).exists?.should be_true
    page.get_eo_by_ao_days_text(code).should == ''
    page.get_eo_by_ao_st_time_text(code).should == ''
    page.get_eo_by_ao_end_time_text(code).should == ''
    page.get_eo_by_ao_bldg_text(code).should == ''
    page.get_eo_by_ao_room_text(code).should == ''
  end
end

When /^the CO-driven exam offering RSI is not updated$/ do
  @course_offering.manage
  on(ManageCourseOfferings).view_exam_offerings
  on ViewExamOfferings do |page|
    page.co_target_row.exists?.should be_true
    page.get_eo_by_co_days_text.should match /#{@eo_rsi.day}/
    page.get_eo_by_co_st_time_text.should == @eo_rsi.start_time
    page.get_eo_by_co_end_time_text.should == @eo_rsi.end_time
    #page.get_eo_by_co_bldg_text.should == @eo_rsi.facility TODO: issue with short vs full facility name
    page.get_eo_by_co_room_text.should == @eo_rsi.room
  end
end

When /^the AO-driven exam offering RSI is successfully updated$/ do
  @course_offering.manage
  on(ManageCourseOfferings).view_exam_offerings
  ao_code = @eo_rsi.ao_code
  on ViewExamOfferings do |page|
    page.eo_by_ao_target_row(ao_code).exists?.should be_true
    page.get_eo_by_ao_days_text(ao_code).should match /#{@eo_rsi.day}/
    page.get_eo_by_ao_st_time_text(ao_code).should == @eo_rsi.start_time
    page.get_eo_by_ao_end_time_text(ao_code).should == @eo_rsi.end_time
    #page.get_eo_by_ao_bldg_text(ao_code).should == @eo_rsi.facility TODO: issue with short vs full facility name
    page.get_eo_by_ao_room_text(ao_code).should == @eo_rsi.room
  end
end

When /^I suspend an Activity Offering for a CO with a standard final exam driven by Course Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"

  @course_offering.manage_and_init
  @activity_offering = @course_offering.activity_offering_cluster_list[0].ao_list[0]
  # @course_offering.find_ao_obj_by_code('A')
  @activity_offering.suspend :navigate_to_page => false
end

When /^I suspend the Course Offering for a CO with a standard final exam driven by Course Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"

  on ManageCourseOfferings do |page|
    page.cluster_select_all_aos
    page.suspend_ao
    on(SuspendActivityOffering).suspend_activity
  end
end

When /^I suspend all Activity Offerings for a CO with a standard final exam driven by Course Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"

  on ManageCourseOfferings do |page|
    page.cluster_select_all_aos
    page.suspend_ao
    on(SuspendActivityOffering).suspend_activity
  end
end

When /^I suspend an Activity Offering for a CO with a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormatObject, :format => "Lecture/Discussion", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list

  @course_offering.manage_and_init
  @activity_offering = @course_offering.find_ao_obj_by_code('A')
  @activity_offering.suspend :navigate_to_page => false
end

Then /^a warning in the Final Exam Period section is displayed stating "([^"]*)"$/ do |exp_msg|
  on EditAcademicTerms do |page|
    page.get_exam_warning_message( @calendar.terms[0].term_type).should match /#{exp_msg}/
    page.cancel
  end
end

Then /^an error in the Final Exam section is displayed stating "([^"]*)"$/ do |exp_msg|
  on EditAcademicTerms do |page|
    page.get_exam_error_message( @calendar.terms[0].term_type).should match /#{exp_msg}/
  end
end

Then /^no error in the Final Exam section is displayed when I save the data$/ do
  on EditAcademicTerms do |page|
    page.exam_error_message( @calendar.terms[0].term_type).present?.should be_false
    page.cancel
  end
end

Then /^the final exam period for the Fall Term is listed when I view the Academic Calendar$/ do
  @calendar.search

  on(CalendarSearch).view @calendar.name

  on ViewAcademicTerms do |page|
    page.go_to_terms_tab
    page.open_term_section(@calendar.terms[0].term_type)
    page.get_exam_start_date( @calendar.terms[0].term_type).should match /12\/11\/#{@calendar.year}/
    page.get_exam_end_date( @calendar.terms[0].term_type).should match /12\/20\/#{@calendar.year}/
  end
end

Then /^there should be no final exam period for any term in the copy$/ do
  all_terms = @calendar.get_all_term_names_in_calendar
  on EditAcademicTerms do |page|
    all_terms.each do |term_name|
      page.open_term_section(term_name)
      page.final_exam_section( term_name).text.should match /Final Exam Period\nAdd Final Exam Period/
    end
    page.cancel
  end
end

When /^I change the Final Exam indicator from Standard Final Exam to Alternate Final Assessment or No Final Exam or Assessment$/ do
  on CourseOfferingCreateEdit do |page|
    page.final_exam_option_alternate
    page.new_final_exam_driver_value.should == "Alternate exam for this offering"
    page.final_exam_option_none
    page.new_final_exam_driver_value.should == "No final exam for this offering"
  end
end

When /^I change the Final Exam indicator from Alternate Final Assessment to Standard Final Exam$/ do
  on CourseOfferingCreateEdit do |page|
    page.final_exam_option_standard
    page.final_exam_driver_select "Final Exam Per Activity Offering"
  end
end

When /^I change the Final Exam indicator from No Final Exam or Assessment to Standard Final Exam$/ do
  on CourseOfferingCreateEdit do |page|
    page.final_exam_option_standard
    page.final_exam_driver_select "Final Exam Per Course Offering"
  end
end

Then /^the Final Exam Driver should not be Activity Offering or Course Offering$/ do
  on CourseOfferingCreateEdit do |page|
    page.new_final_exam_driver_value.should_not == "Activity Offering"
    page.new_final_exam_driver_value.should_not == "Course Offering"
  end
end

Then /^the Final Exam Driver Activity field should disappear$/ do
  on CourseOfferingCreateEdit do |page|
    exam_driver_activity_label = page.delivery_formats_table.rows[0].cells[3].text
    exam_driver_activity_label.should_not match /FINAL EXAM DRIVER ACTIVITY/
    page.cancel
  end
end

Then /^the Final Exam Driver should allow the user to pick Activity Offering or Course Offering as the exam driver$/ do
  on CourseOfferingCreateEdit do |page|
    page.final_exam_driver_element.option(value: /ActivityOffering/).text.should == "Final Exam Per Activity Offering"
    page.final_exam_driver_element.option(value: /CourseOffering/).text.should == "Final Exam Per Course Offering"
  end
end

Then /^the Final Exam Driver Activity field should appear if Activity Offering is selected as the exam driver$/ do
  on CourseOfferingCreateEdit do |page|
    exam_driver_activity_label = page.delivery_formats_table.rows[0].cells[3].text
    exam_driver_activity_label.should match /Final Exam Driver Activity \*/
    page.final_exam_driver_select "Final Exam Per Activity Offering"
    page.new_final_exam_driver_value.should == "Activity Offering"
    page.cancel
  end
end

Then /^the option to specify a Final Exam Driver should only be available for a course offering with a Standard Final Exam option selected$/ do
  on CourseOfferingCreateEdit do |page|
    page.final_exam_option_standard
    page.final_exam_driver_element.present?.should == true
    page.final_exam_option_alternate
    page.final_exam_driver_element.present?.should == false
    page.final_exam_option_none
    page.final_exam_driver_element.present?.should == false
    page.create_offering
  end
end

Then /^a warning about the FE on the Edit CO page is displayed stating "([^"]*)"$/ do |exp_msg|
  on(CourseOfferingCreateEdit).delivery_assessment_warning.should match /#{exp_msg}/
end

Then /^the status of the Final Exam Driver should change to indicate the driver chosen for the Standard Final Exam$/ do
  on CourseOfferingCreateEdit do |page|
    page.final_exam_option_standard
    page.final_exam_driver_select "Final Exam Per Activity Offering"
    page.new_final_exam_driver_value.should == "Activity Offering"
    page.final_exam_driver_select "Final Exam Per Course Offering"
    page.new_final_exam_driver_value.should == "Course Offering"
    page.create_offering
  end
end

Then /^the Final Exam Driver delivery format value should reflect the value selected in the Final Exam Driver field dropdown$/ do
  on(CourseOfferingCreateEdit).new_final_exam_driver_value.should == "Activity Offering"
end

Then /^the Final Exam Driver Activity field should exist and be populated with the first activity type of the format offering$/ do
  on CourseOfferingCreateEdit do |page|
    page.new_final_exam_activity_value.should == "Lecture"
    page.cancel
  end
end

Then /^I should be able to edit and update the Final Exam status$/ do
  on CourseOfferingCreateEdit do |page|
    page.final_exam_option_none
    page.new_final_exam_driver_value.should == "No final exam for this offering"
    page.submit
  end
end

Then /^the exam data for the newly created course offering should match that of the original$/ do
  on(CourseOfferingCreateEdit).new_final_exam_driver_value.should == "Activity Offering"
end

Then /^the ability to access the Use Final Exam Matrix field should only be available for a course offering set to have a Standard Final Exam$/ do
  on CourseOfferingCreateEdit do |page|
    page.final_exam_option_standard
    page.use_exam_matrix_div.present?.should == true
    page.final_exam_option_alternate
    page.use_exam_matrix_div.present?.should == false
    page.final_exam_option_none
    page.use_exam_matrix_div.present?.should == false
    page.cancel
  end
end

Then /^I should not be able to update the status of the final exam period$/ do
  on CourseOfferingCreateEdit do |page|
    page.final_exam_option_div.radio(value: "STANDARD").present?.should == false
    page.final_exam_option_div.radio(value: "ALTERNATE").present?.should == false
    page.final_exam_option_div.radio(value: "NONE").present?.should == false
    page.final_exam_option_div.text.should == "Exam Type\nStandard Final Exam"
    page.cancel
  end
end

Then /^I do not have access to the final exam status for the course offering from catalog$/ do
  on CreateCourseOffering do |page|
    page.choose_from_catalog
    page.continue
  end

  on CourseOfferingCreateEdit do |page|
    page.final_exam_option_div.radio(value: "STANDARD").present?.should == false
    page.final_exam_option_div.radio(value: "ALTERNATE").present?.should == false
    page.final_exam_option_div.radio(value: "NONE").present?.should == false
    page.final_exam_option_div.text.should == "Exam Type\nStandard Final Exam"
  end
end

Then /^all the exam settings and messages are retained after the rollover is completed for the courses that were rolled over$/ do
  @test_co_list = []
  @test_co_list << (make CourseOffering, :term => @calendar_target.terms[0].term_code, :course => @co_list[0].course)
  @test_co_list[0].manage
  on(ManageCourseOfferings).edit_course_offering
  on CourseOfferingCreateEdit do |page|
    page.delivery_assessment_warning.should == "Course exam data differs from Catalog."
    page.new_final_exam_driver_value.should == "No final exam for this offering"
  end

  @test_co_list << (make CourseOffering, :term => @calendar_target.terms[0].term_code, :course => @co_list[1].course)
  @test_co_list[1].manage
  on(ManageCourseOfferings).edit_course_offering
  on CourseOfferingCreateEdit do |page|
    page.delivery_assessment_warning.should == "Course exam data differs from Catalog."
    page.new_final_exam_driver_value.should == "Alternate exam for this offering"
  end


  @test_co_list << (make CourseOffering, :term => @calendar_target.terms[0].term_code, :course => @co_list[2].course)
  @test_co_list[2].manage
  on(ManageCourseOfferings).edit_course_offering
  on(CourseOfferingCreateEdit).new_final_exam_driver_value.should == "Activity Offering"

  @test_co_list << (make CourseOffering, :term => @calendar_target.terms[0].term_code, :course => @co_list[3].course)
  @test_co_list[3].manage
  on(ManageCourseOfferings).edit_course_offering
  on(CourseOfferingCreateEdit).new_final_exam_driver_value.should == "Course Offering"
  on(CourseOfferingCreateEdit).cancel
end

Then /^all the Final Exam and Exam Driver data for the COs should be retained after the rollover is completed and Exam Offerings should be created in a state of Draft$/ do
  @test_co_list = []
  @test_co_list << (make CourseOffering, :term => @calendar_target.terms[0].term_code, :course => @co_list[0].course)
  @test_co_list[0].manage
  on(ManageCourseOfferings).view_exam_offerings
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Course Offering/
    page.get_eo_by_co_status_text.should match /Draft/
  end

  @test_co_list << (make CourseOffering, :term => @calendar_target.terms[0].term_code, :course => @co_list[1].course)
  @test_co_list[1].manage
  on ManageCourseOfferings do |page|
    page.view_exam_offerings_link.present?.should == false
  end

  @test_co_list << (make CourseOffering, :term => @calendar_target.terms[0].term_code, :course => @co_list[2].course)
  @test_co_list[2].manage
  on(ManageCourseOfferings).view_exam_offerings
  on(ViewExamOfferings).get_eo_by_ao_status_text("A").should match /Draft/

  @test_co_list << (make CourseOffering, :term => @calendar_target.terms[0].term_code, :course => @co_list[3].course)
  @test_co_list[3].manage
  on(ManageCourseOfferings).view_exam_offerings
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Course Offering/
    page.get_eo_by_co_status_text.should match /Draft/
  end
end

Then /^the Exam Offerings for Course Offering should be in a ([^"]*) state$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Course Offering/
    page.get_eo_by_co_status_text.should match /#{exp_state}/
    page.co_eo_count.should == "1"
  end
end

Then /^the EO in the Exam Offerings for Course Offering table should be in a ([^"]*) state$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Course Offering/
    page.get_eo_by_co_status_text.should match /#{exp_state}/
    page.co_eo_count.should == "1"
  end
end

Then /^there should only be one EO in the Exam Offerings for Course Offering table$/ do
  on(ViewExamOfferings).co_eo_count.should == "1"
end

Then /^the Exam Offerings for Course Offering in the EO for CO table should be in a ([^"]*) state$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Course Offering/
    page.get_eo_by_co_status_text.should match /#{exp_state}/
    page.co_eo_count.should == "1"
  end
end

Then /^the Exam Offerings for each Activity Offering in the EO for AO table should be in a ([^"]*) state$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Activity Offering/
    @course_offering.activity_offering_cluster_list.each do |cluster|
      cluster.ao_list.each do |ao|
        page.get_eo_by_ao_status_text(ao.code).should match /#{exp_state}/ if ao.activity_type == @course_offering.delivery_format_list[0].final_exam_activity
      end
    end
  end
end

Then /^the Exam Offering for Activity Offering should not be in a ([^"]*) state$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Activity Offering/
    array = page.ao_code_list
    array.each do |code|
      page.get_eo_by_ao_status_text(code).should_not match /#{exp_state}/
    end
  end
end

Then /^the Exam Offerings? for Activity Offering should be in a ([^"]*) state$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Activity Offering/
    array = page.ao_code_list
    if array != nil
      array.each do |code|
        page.get_eo_by_ao_status_text(code).should match /#{exp_state}/
      end
      no_of_eos = array.length
    end
    array = page.ao_code_list("CL Leftovers")
    if array != nil
      array.each do |code|
        page.get_eo_by_ao_status_text(code, "CL Leftovers").should match /#{exp_state}/
      end
      no_of_eos = array.length
    end
    no_of_eos.should == 1
  end
end

Then /^the EO for the suspended AO in the Exam Offering for Activity Offering table should be in a ([^"]*) state$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Activity Offering/
    no_of_eos = 0
    array = page.ao_code_list
    if array != nil
      array.each do |code|
        if code == @activity_offering.code
          page.get_eo_by_ao_status_text(code).should match /#{exp_state}/
          no_of_eos += 1
        end
      end
    end
    array = page.ao_code_list("CL Leftovers")
    if array != nil
      array.each do |code|
        if code == @activity_offering.code
          page.get_eo_by_ao_status_text(code, "CL Leftovers").should match /#{exp_state}/
          no_of_eos += 1
        end
      end
    end
    no_of_eos.should == 1
  end
end

Then /^the EOs in the Exam Offerings for Activity Offering table should be in a ([^"]*) state$/ do |exp_state|
  #TODO: should be driven by @course_offering object
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Activity Offering/
    @course_offering.activity_offering_cluster_list.each do |cluster|
      cluster.ao_list.each do |ao|
        page.get_eo_by_ao_status_text(ao.code).should match /#{exp_state}/ if ao.activity_type == @course_offering.delivery_format_list[0].final_exam_activity
      end
    end
  end
end

Then /^there should be one EO for each AO of the course in the Exam Offering for Activity Offering table$/ do
  on ViewExamOfferings do |page|
    array = page.ao_code_list
    if array != nil
      no_of_eos = array.length
    end
    array = page.ao_code_list("CL Leftovers")
    if array != nil
      no_of_eos = array.length
    end
    no_of_eos.should >= 1
  end
end

Then /^the ([\d]*) Exam Offerings? for Activity Offering should be in a ([^"]*) state$/ do |num,exp_state|
  on ViewExamOfferings do |page|
    page.ao_table_header_text.should match /for Activity Offering/
    array = page.ao_code_list
    if array != nil
      array.each do |code|
        page.get_eo_by_ao_status_text(code).should match /#{exp_state}/
      end
      no_of_eos = array.length
    end
    array = page.ao_code_list("CL Leftovers")
    if array != nil
      array.each do |code|
        page.get_eo_by_ao_status_text(code, "CL Leftovers").should match /#{exp_state}/
      end
      no_of_eos = array.length
    end
    no_of_eos.should == num
  end
end

Then /^there should be ([^"]*) Exam Offerings? by Activity Offering for the course$/ do |no_of_aos|
  on ViewExamOfferings do |page|
    array = page.ao_code_list
    array.length.should == no_of_aos.to_i
  end
end

Then /^there should be ([\d]*) EOs in the Exam Offerings by Activity Offering table for the course$/ do |no_of_aos|
  on ViewExamOfferings do |page|
    array = page.ao_code_list
    array.length.should == no_of_aos.to_i
  end
end

Then /^there should be no View Exam Offering option present$/ do
  on ManageCourseOfferings do |page|
    page.view_exam_offerings_link.present?.should == false
  end
  #on ViewExamOfferings do |page|
  #  page.exam_offerings_page_section.text.should_not match /^Exam Offerings for Course Offering/
  #  page.exam_offerings_page_section.text.should_not match /^Exam Offerings for Activity Offering/
  #end
end

Then /^I expect a popup to appear with a displayed warning stating "([^"]*)"$/ do |exp_msg|
  on PerformRollover do |page|
    page.continue_wo_exams_dialog_div.visible?.should == true
    page.continue_wo_exams_dialog_div.text.should match /#{exp_msg}/
    page.continue_wo_exams_dialog_confirm
  end
end

Then /^the Exclude Saturday and Exclude Sunday toggles should be selected by default$/ do
  on EditAcademicTerms do |page|
    page.exclude_saturday_toggle( @calendar.terms[0].term_type).attribute_value('checked').should == "true"
    page.exclude_sunday_toggle( @calendar.terms[0].term_type).attribute_value('checked').should == "true"
  end
end

Then /^the Exclude Saturday or Exclude Sunday fields should be deselected when view the term$/ do
  @calendar.terms[0].search
  on(CalendarSearch).view @calendar.terms[0].term_name
  on ViewAcademicTerms do |page|
    page.open_term_section(@calendar.terms[0].term_type)
    sleep 30
    page.get_exclude_saturday_value(@calendar.terms[0].term_type).should == "false"
    page.get_exclude_sunday_value(@calendar.terms[0].term_type).should == "false"
  end
end

Then /^the Exam Offering listed in the EO for CO table should be in a ([^"]*) state$/ do |exp_msg|
  on(ViewExamOfferings).get_eo_by_co_status_text.should == exp_msg
end

Then /^the Exam Offering table should be in a Canceled state$/ do
  on(ViewExamOfferings).exam_offerings_page_section.text.should match /Cancelled Exam Offerings for Activity Offerings/
end

Then /^the EO in the Exam Offering by Course Offering table should be in a Canceled state$/ do
  on(ViewExamOfferings).exam_offerings_page_section.text.should match /Cancelled Exam Offerings? for Course Offerings?/
end

Then /^the EO in the Exam Offering by Activity Offering table should be in a Canceled state$/ do
  on(ViewExamOfferings).exam_offerings_page_section.text.should match /Cancelled Exam Offerings for Activity Offerings/
end

Then /^the header for the table should be labelled as Canceled$/ do
  on(ViewExamOfferings).exam_offerings_page_section.text.should match /Cancelled Exam Offerings for Activity Offerings/
end

Then /^there should be an Activity Offering table header explaining that the Exam Offerings have been canceled$/ do
  on(ViewExamOfferings).exam_offerings_page_section.text.should match /Cancelled Exam Offerings for Activity Offerings/
end

Then /^there should be no Exam Offering for Course Offering table present$/ do
  on(ViewExamOfferings).table_header_text.should_not match /for Course Offerings/
end

Then /^there should be no Exam Offering for Activity Offering table present$/ do
  on(ViewExamOfferings).table_header_text.should_not match /for Activity Offerings/
end

Then /^the Exam Offering table for the canceled AO should also be in the same state$/ do
  on(ViewExamOfferings).get_eo_by_ao_status_text('A').should == 'Canceled'
end

Given /^that a CO allows for multiple Format Offerings and has one existing format offering and a standard exam driven by Course Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term=> "201208", :course => "ENGL304")
end

When /^I edit the CO to add a second Format Offering$/ do
  delivery_format = make DeliveryFormatObject, :format => "Lecture", :grade_format => "Course Offering", :final_exam_activity => "Lecture"
  @course_offering.add_delivery_format :delivery_format => delivery_format
end

When /^I create a Course Offering from copy in a term that uses the matrix and has a final exam period defined$/ do
  @copy_co = create CourseOffering, :create_by_copy => @course_offering

  @copy_co.edit :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"
end

When /^I create a Course Offering from copy in a term with a defined final exam period that uses the FE matrix$/ do
  @copy_co = create CourseOffering, :create_by_copy => @course_offering
end

Then /^there should be a warning message stating that "(.*?)"$/ do |exp_msg|
  on ManageCourseOfferings do |page|
    begin
      page.wait_until(120) { page.growl_warning_text.exists? }
      page.growl_warning_text.should match /#{@course_offering.course}.*#{Regexp.escape(exp_msg)}/
    rescue
      puts "growl warning message for the EO did not appear"
    end
  end
end

Then /^there should be a warning message for the AO stating that "(.*?)"$/ do |exp_msg|
  on ManageCourseOfferings do |page|
    begin
      page.wait_until(120) { page.growl_message_warning_div.exists? }
      page.growl_warning_text.should match /#{@course_offering.course}.*#{@course_offering.activity_offering_cluster_list[0].ao_list[0].code}.*#{Regexp.escape(exp_msg)}/
    rescue
      puts "growl warning message for the EO did not appear"
    end
  end
end

When /^I create a Course Offering from catalog in a term with a defined final exam period that uses the FE matrix$/ do
  @course_offering.create
end

And /^I have created a Course Offering from catalog in the source term that uses the matrix and has a final exam period defined$/ do
  @course_offering = create CourseOffering, :term => @calendar.terms[0].term_code, :course => "HIST110"
end

And /^I have created an Activity Offering that only has Requested Scheduling Information$/ do
  @activity_offering = @course_offering.create_ao :ao_obj => (make ActivityOfferingObject)
  si_obj =  make SchedulingInformationObject, :days => "MWF",
                 :start_time => "01:00", :start_time_ampm => "pm",
                 :end_time => "01:50", :end_time_ampm => "pm"
  @activity_offering.add_req_sched_info :rsi_obj => si_obj
end

When /^I create a Course Offering from copy in a term with a defined final exam period that uses the matrix$/ do
  @course_offering = create CourseOffering, :create_by_copy => @original_co
  @course_offering.manage_and_init
  @activity_offering = @course_offering.activity_offering_cluster_list[0].ao_list[0]
end

When /^I create a copy of the initial course offering in a term that uses the FE matrix and has defined final exam period$/ do
  @course_offering = create CourseOffering, :create_by_copy => @original_co
  @course_offering.manage_and_init
  @activity_offering = @course_offering.activity_offering_cluster_list[0].ao_list[0]
end

Given /^I create a Course Offering with an AO-driven exam from catalog in a term with a defined final exam period$/ do
  @course_offering = make CourseOffering, :term => "201301", :course => "BSCI361",
                            :final_exam_driver => "Final Exam Per Activity Offering"
  @course_offering.delivery_format_list[0].format = "Lecture"
  @course_offering.delivery_format_list[0].grade_format = "Lecture"
  @course_offering.delivery_format_list[0].final_exam_activity = "Lecture"
  @course_offering.create
end

Given /^that the Course Offering has an AO-driven exam in a term that uses the FE matrix and has defined final exam period$/ do
  @original_co = make CourseOffering, :term => "201301", :course => "BSCI361"
end

When /^I create a copy of the Course Offering and decide to exclude all scheduling information$/ do
  @course_offering = create CourseOffering, :create_by_copy => @original_co, :exclude_scheduling => true
  @course_offering.manage_and_init
  @activity_offering = @course_offering.activity_offering_cluster_list[0].ao_list[0]
end

Given /^I create an Activity Offering that has no ASIs or RSIs$/ do
  @activity_offering = make ActivityOfferingObject, :activity_type => "Lecture", :parent_course_offering => @course_offering
  new_code_list = @activity_offering.create_simple
  new_code_list.each do |code|
    @activity_offering.code = code
    @activity_offering.approve :navigate_to_page => false
  end
end

Given /^I create an Activity Offering that has RSI data but has no ASI data$/ do
  @activity_offering = make ActivityOfferingObject, :activity_type => "Lecture", :parent_course_offering => @course_offering
  new_code_list = @activity_offering.create_simple
  new_code_list.each do |code|
    @activity_offering.code = code
    @activity_offering.approve :navigate_to_page => false
  end

  @course_offering.manage
  end_time = (DateTime.strptime("#{@matrix.rules[0].statements[0].start_time}", '%I:%M') + ("50".to_f/1440)).strftime( '%I:%M')
  @activity_offering.add_req_sched_info :rsi_obj => (make SchedulingInformationObject, :days  => "T",
                                                          :start_time  => @matrix.rules[0].statements[0].start_time,
                                                          :start_time_ampm  => @matrix.rules[0].statements[0].st_time_ampm,
                                                          :end_time  => end_time, :end_time_ampm => @matrix.rules[0].statements[0].st_time_ampm)
end

When /^I add additional Requested Scheduling Information to the Activity Offering that matches an entry on the exam matrix$/ do
  @course_offering.manage
  end_time = (DateTime.strptime("#{@matrix.rules[0].statements[0].start_time}", '%I:%M') + ("50".to_f/1440)).strftime( '%I:%M')
  @activity_offering.add_req_sched_info :rsi_obj => (make SchedulingInformationObject, :days  => "H",
                                                          :start_time  => @matrix.rules[0].statements[0].start_time,
                                                          :start_time_ampm  => @matrix.rules[0].statements[0].st_time_ampm,
                                                          :end_time  => end_time, :end_time_ampm => @matrix.rules[0].statements[0].st_time_ampm)
end

When /^I add new Requested Scheduling Information to the Activity Offering that does not match an entry on the exam matrix$/ do
  @course_offering.manage
  end_time = (DateTime.strptime("#{@matrix.rules[0].statements[0].start_time}", '%I:%M') + ("50".to_f/1440)).strftime( '%I:%M')

  rsi_object = make SchedulingInformationObject, :days  => "F", :start_time  => @matrix.rules[0].statements[0].start_time,
                    :start_time_ampm  => @matrix.rules[0].statements[0].st_time_ampm,
                    :end_time  => end_time, :end_time_ampm  => @matrix.rules[0].statements[0].st_time_ampm

  @activity_offering.add_req_sched_info :rsi_obj => rsi_object
end

When /^I add new Requested Scheduling Information to the Activity Offering that matches an entry on the exam matrix$/ do
  @course_offering.manage
  end_time = (DateTime.strptime("#{@matrix.rules[0].statements[0].start_time}", '%I:%M') + ("50".to_f/1440)).strftime( '%I:%M')

  rsi_object = make SchedulingInformationObject, :days  => @matrix.rules[0].statements[0].days,
                    :start_time  => @matrix.rules[0].statements[0].start_time,
                    :start_time_ampm  => @matrix.rules[0].statements[0].st_time_ampm,
                    :end_time  => end_time, :end_time_ampm  => @matrix.rules[0].statements[0].st_time_ampm

  @activity_offering.add_req_sched_info :rsi_obj => rsi_object
end

When /^I create multiple Course Offerings each with different Exam Offerings and Requested Scheduling Information$/ do
  @matrix = make FinalExamMatrix
  @matrix.create_common_rule_matrix_object_for_rsi( "CHEM242")
  @matrix.create_common_rule_matrix_object_for_rsi( "PHYS161")
  @matrix.create_standard_rule_matrix_object_for_rsi( "TH")
  @matrix.create_standard_rule_matrix_object_for_rsi( "F")
  th_end_time = (DateTime.strptime("#{@matrix.rules[2].statements[0].start_time}", '%I:%M') + ("50".to_f/1440)).strftime( '%I:%M')
  f_end_time = (DateTime.strptime("#{@matrix.rules[3].statements[0].start_time}", '%I:%M') + ("50".to_f/1440)).strftime( '%I:%M')

  @co_list = []

  @co_list << (create CourseOffering, :term => @calendar.terms[0].term_code, :course => "CHEM242",
                      :final_exam_driver => "Final Exam Per Course Offering")

  course_offering = make CourseOffering, :term => @calendar.terms[0].term_code, :course => "PHYS161",
                      :final_exam_driver => "Final Exam Per Course Offering"
  course_offering.delivery_format_list[0].format = "Lecture/Discussion"
  course_offering.delivery_format_list[0].grade_format = "Lecture"
  @co_list << (course_offering.create)

  @co_list[1].create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture/Discussion", :activity_type => "Lecture")
  @co_list[1].activity_offering_cluster_list[0].ao_list[0].add_req_sched_info :rsi_obj => (make SchedulingInformationObject,
                                                :days  => @matrix.rules[2].statements[0].days,
                                                :start_time  => @matrix.rules[2].statements[0].start_time,
                                                :start_time_ampm  => @matrix.rules[2].statements[0].st_time_ampm,
                                                :end_time  => th_end_time, :end_time_ampm  => @matrix.rules[2].statements[0].st_time_ampm)

  @co_list[1].create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture/Discussion", :activity_type => "Lecture")
  @co_list[1].activity_offering_cluster_list[0].ao_list[1].add_req_sched_info :rsi_obj => (make SchedulingInformationObject,
                                                :days  => @matrix.rules[3].statements[0].days,
                                                :start_time  => @matrix.rules[3].statements[0].start_time,
                                                :start_time_ampm  => @matrix.rules[3].statements[0].st_time_ampm,
                                                :end_time  => f_end_time, :end_time_ampm  => @matrix.rules[3].statements[0].st_time_ampm)

  @co_list[1].create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture/Discussion", :activity_type => "Discussion")

  course_offering = make CourseOffering, :term => @calendar.terms[0].term_code, :course => "ENGL362",
                         :final_exam_driver => "Final Exam Per Activity Offering"
  course_offering.delivery_format_list[0].format = "Lecture"
  course_offering.delivery_format_list[0].grade_format = "Course Offering"
  course_offering.delivery_format_list[0].final_exam_activity = "Lecture"
  @co_list << (course_offering.create)

  @co_list[2].create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")
  @co_list[2].activity_offering_cluster_list[0].ao_list[0].add_req_sched_info :rsi_obj => (make SchedulingInformationObject,
                                                :days  => @matrix.rules[2].statements[0].days,
                                                :start_time  => @matrix.rules[2].statements[0].start_time,
                                                :start_time_ampm  => @matrix.rules[2].statements[0].st_time_ampm,
                                                :end_time  => th_end_time, :end_time_ampm  => @matrix.rules[2].statements[0].st_time_ampm)

  @co_list[2].create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")
  @co_list[2].activity_offering_cluster_list[0].ao_list[1].add_req_sched_info :rsi_obj => (make SchedulingInformationObject,
                                                :days  => @matrix.rules[3].statements[0].days,
                                                :start_time  => @matrix.rules[3].statements[0].start_time,
                                                :start_time_ampm  => @matrix.rules[3].statements[0].st_time_ampm,
                                                :end_time  => f_end_time, :end_time_ampm  => @matrix.rules[3].statements[0].st_time_ampm)

  @co_list[2].create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")

  course_offering = make CourseOffering, :term => @calendar.terms[0].term_code, :course => "PHYS171",
                         :final_exam_driver => "Final Exam Per Activity Offering"
  course_offering.delivery_format_list[0].format = "Lecture"
  course_offering.delivery_format_list[0].grade_format = "Course Offering"
  course_offering.delivery_format_list[0].final_exam_activity = "Lecture"
  @co_list << (course_offering.create)

  @co_list[3].create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")
  @co_list[3].activity_offering_cluster_list[0].ao_list[0].add_req_sched_info :rsi_obj => (make SchedulingInformationObject,
                                                :days  => @matrix.rules[3].statements[0].days,
                                                :start_time  => @matrix.rules[3].statements[0].start_time,
                                                :start_time_ampm  => @matrix.rules[3].statements[0].st_time_ampm,
                                                :end_time  => f_end_time, :end_time_ampm  => @matrix.rules[3].statements[0].st_time_ampm)

  course_offering = make CourseOffering, :term => @calendar.terms[0].term_code, :course => "ENGL313",
      :final_exam_driver => "Final Exam Per Activity Offering"
  course_offering.delivery_format_list[0].format = "Lecture"
  course_offering.delivery_format_list[0].grade_format = "Lecture"
  course_offering.delivery_format_list[0].final_exam_activity = "Lecture"
  @co_list << (course_offering.create)

  @co_list[4].create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")
  @co_list[4].activity_offering_cluster_list[0].ao_list[0].add_req_sched_info :rsi_obj => (make SchedulingInformationObject,
                                                :days  => @matrix.rules[2].statements[0].days,
                                                :start_time  => @matrix.rules[2].statements[0].start_time,
                                                :start_time_ampm  => @matrix.rules[2].statements[0].st_time_ampm,
                                                :end_time  => th_end_time, :end_time_ampm  => @matrix.rules[2].statements[0].st_time_ampm)

  @co_list[4].create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")
  @co_list[4].activity_offering_cluster_list[0].ao_list[1].add_req_sched_info :rsi_obj => (make SchedulingInformationObject,
                                                :days  => @matrix.rules[3].statements[0].days,
                                                :start_time  => @matrix.rules[3].statements[0].start_time,
                                                :start_time_ampm  => @matrix.rules[3].statements[0].st_time_ampm,
                                                :end_time  => f_end_time, :end_time_ampm  => @matrix.rules[3].statements[0].st_time_ampm)

  course_offering = make CourseOffering, :term => @calendar.terms[0].term_code, :course => "CHEM242",
                    :final_exam_driver => "Final Exam Per Activity Offering"
  course_offering.delivery_format_list[0].format = "Lab/Lecture"
  course_offering.delivery_format_list[0].grade_format = "Lecture"
  course_offering.delivery_format_list[0].final_exam_activity = "Lecture"
  @co_list << (course_offering.create)

  @co_list[5].create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lab/Lecture" )
end

Then /^the Exam Offerings Slotting info should be populated or left blank depending on whether the AO RSI was found on the Exam Matrix$/ do
  @co_list.each do |co|
    test_co = make CourseOffering, :term => @calendar_target.terms[0].term_code, :course => co.course
    test_co.manage_and_init
    if test_co.activity_offering_cluster_list.any? and test_co.course != @co_list[1].course
      on(ManageCourseOfferings).view_exam_offerings
      test_co.activity_offering_cluster_list[0].ao_list.each do |ao|
        ao_rsi_list = ao.requested_scheduling_information_list
        if !ao_rsi_list.empty?
          if ao_rsi_list[0].days != "TH"
            on ViewExamOfferings do |page|
              page.get_eo_by_ao_days_text(ao.code).should == ""
              page.get_eo_by_ao_st_time_text(ao.code).should == ""
              page.get_eo_by_ao_end_time_text(ao.code).should == ""
            end
          else
            on ViewExamOfferings do |page|
              page.get_eo_by_ao_days_text(ao.code).should match /#{@matrix.rules[2].rsi_days}/
              page.get_eo_by_ao_st_time_text(ao.code).should match /#{@matrix.rules[2].start_time}/i
              page.get_eo_by_ao_end_time_text(ao.code).should match /#{@matrix.rules[2].end_time}/i
            end
          end
        else
          on ViewExamOfferings do |page|
            page.get_eo_by_ao_days_text(ao.code).should == ""
            page.get_eo_by_ao_st_time_text(ao.code).should == ""
            page.get_eo_by_ao_end_time_text(ao.code).should == ""
          end
        end
      end
    else
      on(ManageCourseOfferings).view_exam_offerings
      if test_co.course == @co_list[0].course
        on ViewExamOfferings do |page|
          page.get_eo_by_co_days_text.should == ""
          page.get_eo_by_co_st_time_text.should == ""
          page.get_eo_by_co_end_time_text.should == ""
        end
      else
        on ViewExamOfferings do |page|
          page.get_eo_by_co_days_text.should match /#{@matrix.rules[1].rsi_days}/
          page.get_eo_by_co_st_time_text.should match /#{@matrix.rules[1].start_time}/i
          page.get_eo_by_co_end_time_text.should match /#{@matrix.rules[1].end_time}/i
        end
      end
    end
    #on(ManageCourseOfferings).view_exam_offerings
  end
end

Given /^I create a Course Offering from catalog with an Alternate Exam that is not found on the matrix in a term with a defined final exam period$/ do
  @course_offering = create CourseOffering, :term=> "201208", :course => "CHEM242", :final_exam_type => "ALTERNATE"
end

Given /^I create a Course Offering from catalog with No Exam that is found on the matrix in a term with a defined final exam period$/ do
  @course_offering = make CourseOffering, :term=> "201208", :course => "ENGL403", :final_exam_type => "NONE"

  @matrix = make FinalExamMatrix
  @matrix.create_common_rule_matrix_object_for_rsi( @course_offering.course)

  @course_offering.create
end

When /^I edit the Course Offering to use a Standard Exam that is CO-Driven$/ do
  @course_offering.edit :final_exam_type => "Standard Final Exam", :final_exam_driver => "Final Exam Per Course Offering",
                        :use_final_exam_matrix => true
end

Given /^I create a Course Offering from catalog with No Exam that has an AO with RSI data found on the matrix in a term with a defined final exam period$/ do
  @course_offering = make CourseOffering, :term=> "201208", :course => "ENGL304", :final_exam_type => "NONE"
  @course_offering.delivery_format_list[0].format = "Lecture"
  @course_offering.delivery_format_list[0].grade_format = "Lecture"

  @matrix = make FinalExamMatrix
  @matrix.create_standard_rule_matrix_object_for_rsi( "MWF")

  @course_offering.create

  @activity_offering = @course_offering.create_ao :ao_obj => (make ActivityOfferingObject)
  end_time = (DateTime.strptime("#{@matrix.rules[0].statements[0].start_time}", '%I:%M') + ("50".to_f/1440)).strftime( '%I:%M')
  @course_offering.activity_offering_cluster_list[0].ao_list[0].add_req_sched_info :rsi_obj => (make SchedulingInformationObject,
                                                     :days => @matrix.rules[0].statements[0].days,
                                                     :start_time => @matrix.rules[0].statements[0].start_time,
                                                     :start_time_ampm => @matrix.rules[0].statements[0].st_time_ampm,
                                                     :end_time => end_time, :end_time_ampm => @matrix.rules[0].statements[0].st_time_ampm)
end

When /^I edit the Course Offering to use a Standard Exam that is AO-Driven$/ do
  @course_offering.edit :final_exam_type => "Standard Final Exam", :final_exam_driver => "Final Exam Per Activity Offering",
                        :use_final_exam_matrix => true
end

Given /^I create a Course Offering from catalog with an Alternate Exam that has an AO with RSI data not found on the matrix in a term with a defined final exam period$/ do
  @matrix = make FinalExamMatrix
  @matrix.create_standard_rule_matrix_object_for_rsi( "F")

  @course_offering = create CourseOffering, :term=> "201208", :course => "CHEM242", :final_exam_type => "ALTERNATE"

  @activity_offering = @course_offering.create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lab/Lecture",
                                                                   :activity_type => "Lab")
  end_time = (DateTime.strptime("#{@matrix.rules[0].statements[0].start_time}", '%I:%M') + ("50".to_f/1440)).strftime( '%I:%M')
  @course_offering.activity_offering_cluster_list[0].ao_list[0].add_req_sched_info :rsi_obj => (make SchedulingInformationObject,
                                                     :days => @matrix.rules[0].statements[0].days,
                                                     :start_time => @matrix.rules[0].statements[0].start_time,
                                                     :start_time_ampm => @matrix.rules[0].statements[0].st_time_ampm,
                                                     :end_time => end_time, :end_time_ampm => @matrix.rules[0].statements[0].st_time_ampm)
end

Given /^I create a Course Offering from catalog with an Alternate Exam that has an AO with no RSI or ASI data$/ do
  @course_offering = make CourseOffering, :term => "201208", :course => "ENGL250", :final_exam_type => "ALTERNATE"

  @course_offering.delivery_format_list[0].format = "Lecture"
  @course_offering.delivery_format_list[0].grade_format = "Lecture"
  @course_offering.delivery_format_list[0].final_exam_activity = "Lecture"
  @course_offering.create

end


Given /^I create a Course Offering from catalog with No Exam that has an AO with no RSI or ASI data$/ do
  @course_offering = make CourseOffering, :term => "201301", :course => "ENGL250", :final_exam_type => "NONE"

  @course_offering.delivery_format_list[0].format = "Lecture"
  @course_offering.delivery_format_list[0].grade_format = "Lecture"
  @course_offering.delivery_format_list[0].final_exam_activity = "Lecture"
  @course_offering.create

end

Given /^I create a Course Offering from catalog with No Exam that has an AO with ASI data found on the matrix in a term with a defined final exam period$/ do
  @course_offering = make CourseOffering, :term=> "201208", :course => "ENGL304", :final_exam_type => "NONE"
  @course_offering.delivery_format_list[0].format = "Lecture"
  @course_offering.delivery_format_list[0].grade_format = "Lecture"

  @matrix = make FinalExamMatrix
  @matrix.create_standard_rule_matrix_object_for_rsi( "TH")

  @course_offering.create

  @activity_offering = @course_offering.create_ao :ao_obj => (make ActivityOfferingObject)
  @course_offering.activity_offering_cluster_list[0].ao_list[0].edit :send_to_scheduler => true

  end_time = (DateTime.strptime("#{@matrix.rules[0].statements[0].start_time}", '%I:%M') + ("50".to_f/1440)).strftime( '%I:%M')
  @course_offering.activity_offering_cluster_list[0].ao_list[0].add_req_sched_info :rsi_obj => (make SchedulingInformationObject,
                                                     :days => @matrix.rules[0].statements[0].days,
                                                     :start_time => @matrix.rules[0].statements[0].start_time,
                                                     :start_time_ampm => @matrix.rules[0].statements[0].st_time_ampm,
                                                     :end_time => end_time, :end_time_ampm => @matrix.rules[0].statements[0].st_time_ampm)
end

Given /^I create a Course Offering from catalog with an Alternate Exam that has an AO with ASI data not found on the matrix in a term with a defined final exam period$/ do
  @matrix = make FinalExamMatrix
  @matrix.create_standard_rule_matrix_object_for_rsi( "F")

  @course_offering = create CourseOffering, :term=> "201208", :course => "CHEM242", :final_exam_type => "ALTERNATE"

  @activity_offering = @course_offering.create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lab/Lecture",:activity_type => "Lab")
  @course_offering.activity_offering_cluster_list[0].ao_list[0].edit :send_to_scheduler => true

  end_time = (DateTime.strptime("#{@matrix.rules[0].statements[0].start_time}", '%I:%M') + ("50".to_f/1440)).strftime( '%I:%M')
  @course_offering.activity_offering_cluster_list[0].ao_list[0].add_req_sched_info :rsi_obj => (make SchedulingInformationObject,
                                                     :days => @matrix.rules[0].statements[0].days,
                                                     :start_time => @matrix.rules[0].statements[0].start_time,
                                                     :start_time_ampm => @matrix.rules[0].statements[0].st_time_ampm,
                                                     :end_time => end_time, :end_time_ampm => @matrix.rules[0].statements[0].st_time_ampm)
end

When /^I create multiple Course Offerings in the term$/ do
  @matrix = make FinalExamMatrix
  @matrix.create_common_rule_matrix_object_for_rsi( "ENGL313")
  @matrix.create_standard_rule_matrix_object_for_rsi( "MTH")
  @matrix.create_standard_rule_matrix_object_for_rsi( "WHF")
  th_end_time = (DateTime.strptime("#{@matrix.rules[1].statements[0].start_time}", '%I:%M') + ("50".to_f/1440)).strftime( '%I:%M')
  f_end_time = (DateTime.strptime("#{@matrix.rules[2].statements[0].start_time}", '%I:%M') + ("50".to_f/1440)).strftime( '%I:%M')

  @co_list = []
  course_offering = make CourseOffering, :term => @calendar.terms[0].term_code, :course => "ENGL313",
                         :final_exam_driver => "Final Exam Per Course Offering"
  course_offering.delivery_format_list[0].format = "Lecture"
  course_offering.delivery_format_list[0].grade_format = "Course Offering"
  @co_list << (course_offering.create)

  @co_list[0].create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")

  course_offering = make CourseOffering, :term => @calendar.terms[0].term_code, :course => "ENGL362",
                         :final_exam_driver => "Final Exam Per Activity Offering"
  course_offering.delivery_format_list[0].format = "Lecture"
  course_offering.delivery_format_list[0].grade_format = "Course Offering"
  course_offering.delivery_format_list[0].final_exam_activity = "Lecture"
  @co_list << (course_offering.create)

  @co_list[1].create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")
  @co_list[1].activity_offering_cluster_list[0].ao_list[0].add_req_sched_info :rsi_obj => (make SchedulingInformationObject,
                                                :days  => @matrix.rules[1].statements[0].days,
                                                :start_time  => @matrix.rules[1].statements[0].start_time,
                                                :start_time_ampm  => @matrix.rules[1].statements[0].st_time_ampm,
                                                :end_time  => th_end_time, :end_time_ampm  => @matrix.rules[1].statements[0].st_time_ampm)

  @co_list[1].create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")
  @co_list[1].activity_offering_cluster_list[0].ao_list[1].add_req_sched_info :rsi_obj => (make SchedulingInformationObject,
                                                :days  => @matrix.rules[2].statements[0].days,
                                                :start_time  => @matrix.rules[2].statements[0].start_time,
                                                :start_time_ampm  => @matrix.rules[2].statements[0].st_time_ampm,
                                                :end_time  => f_end_time, :end_time_ampm  => @matrix.rules[2].statements[0].st_time_ampm)
end

When /^I initiate a rollover to create a term in open state$/ do
  @calendar_target = create AcademicCalendar, :year => @calendar.year.to_i + 1

  term_target = make AcademicTermObject, :parent_calendar => @calendar_target
  @calendar_target.add_term term_target

  exam_period = make ExamPeriodObject, :parent_term => term_target
  @calendar_target.terms[0].add_exam_period exam_period
  @calendar_target.terms[0].save

  @calendar_target.terms[0].make_official

  @manage_soc = make ManageSoc, :term_code => @calendar_target.terms[0].term_code
  @manage_soc.set_up_soc

  @rollover = make Rollover, :target_term => @calendar_target.terms[0].term_code ,
                   :source_term => @calendar.terms[0].term_code,
                   :exp_success => false
  @rollover.perform_rollover
  @rollover.wait_for_rollover_to_complete
  @rollover.release_to_depts
end

When /^I create Exam Matrix rules from which the Exam Offering Slotting info is populated$/ do
  @matrix.rules.each do |rule|
    rule.parent_exam_matrix = @matrix
    rule.create
  end
end

Then /^the Exam Offerings Slotting info should be populated after the Mass Scheduling Event has been triggered$/ do
  @co_list.each do |co|
    test_co = make CourseOffering, :term => @calendar_target.terms[0].term_code, :course => co.course
    test_co.manage_and_init
    on(ManageCourseOfferings).view_exam_offerings
    if test_co.course != @co_list[0].course
      on ViewExamOfferings do |page|
        ao = test_co.activity_offering_cluster_list[0].ao_list[0]
        page.get_eo_by_ao_days_text(ao.code).should match /#{@matrix.rules[1].rsi_days}/
        page.get_eo_by_ao_st_time_text(ao.code).should match /#{@matrix.rules[1].start_time}/i
        page.get_eo_by_ao_end_time_text(ao.code).should match /#{@matrix.rules[1].end_time}/i

        ao = test_co.activity_offering_cluster_list[0].ao_list[1]
        page.get_eo_by_ao_days_text(ao.code).should match /#{@matrix.rules[2].rsi_days}/
        page.get_eo_by_ao_st_time_text(ao.code).should match /#{@matrix.rules[2].start_time}/i
        page.get_eo_by_ao_end_time_text(ao.code).should match /#{@matrix.rules[2].end_time}/i
      end
    else
      on ViewExamOfferings do |page|
        page.get_eo_by_co_days_text.should match /#{@matrix.rules[0].rsi_days}/
        page.get_eo_by_co_st_time_text.should match /#{@matrix.rules[0].start_time}/i
        page.get_eo_by_co_end_time_text.should match /#{@matrix.rules[0].end_time}/i
      end
    end
  end
end

When /^I? ?add facility and room information to the exam offering RSI$/ do
  @eo_rsi.edit :do_navigation => false,
               :day => 'Day 5',
               :start_time => '12:00 PM',
               :end_time => '1:50 PM',
               :facility => 'VMH',
               :room => '1212'
end