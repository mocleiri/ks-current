When /^I change the final exam period start date to be before the term start date and save$/ do
  @calendar = make AcademicCalendar, :year => "2012"

  term = make AcademicTermObject, :parent_calendar => @calendar, :term => "Fall"
  @calendar.terms << term

  exam_period = make ExamPeriodObject, :parent_term => term
  @calendar.terms[0].exam_period = exam_period

  @calendar.terms[0].exam_period.edit :start_date => "08/15/2012"
end

When /^I change the final exam period end date to be after the term end date and save$/ do
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
  @source_calendar = make AcademicCalendar, :name => "2012-2013 Academic Calendar"

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
  @course_offering.edit_offering :final_exam_type => "Standard Final Exam"
  @course_offering.save
end

And /^I select a Final Exam Driver option from the drop-down$/ do
  @course_offering.edit_offering :final_exam_driver => "Final Exam Per Course Offering"
end

When /^I return to the Edit Co page for the course after updating the change$/ do
  @course_offering.save

  on(ManageCourseOfferings).edit_course_offering
end

When /^I create a Course Offering from an existing Course Offering with a standard final exam option$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "CHEM277")

  @activity_offering = make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.edit :send_to_scheduler => true, :defer_save => false

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

  delivery_format_list = []
  delivery_format_list << (make DeliveryFormat, :format => "Lecture", :grade_format => "Course Offering",
                                :final_exam_driver => "Course Offering")
  @co_list << (create CourseOffering, :term => @calendar.terms[0].term_code, :course => "PHYS603", @use_final_exam_matrix => false,
                      :delivery_format_list => delivery_format_list)
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
  delivery_format_list = []
  delivery_format_list << (make DeliveryFormat, :format => "Lecture", :grade_format => "Course Offering",
                                :final_exam_driver => "Course Offering")
  course_offering = create CourseOffering, :term => @calendar.terms[0].term_code,
                           :course => "BSCI215",
                           :delivery_format_list => delivery_format_list,
                           :final_exam_driver => "Final Exam Per Course Offering"

  @co_list << course_offering

  @co_list[0].create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")

  course_offering = create CourseOffering, :term => @calendar.terms[0].term_code,
                           :course => "ENGL301",
                           :final_exam_driver => "Final Exam Per Activity Offering"
  @co_list << course_offering

  delivery_format_list[0] = (make DeliveryFormat, :format => "Lecture", :grade_format => "Course Offering",
                                  :final_exam_driver => "Lecture")

  @co_list << (create CourseOffering, :term => @calendar.terms[0].term_code, :course => "PHYS272",
                      :delivery_format_list => delivery_format_list,
                      :final_exam_driver => "Final Exam Per Activity Offering")
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
  @calendar.terms[0].save

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
  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"
  @course_offering.save
  @activity_offering =  make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.edit :send_to_scheduler => true, :defer_save => false

  @create_co = create CourseOffering, :term=> @course_offering.term, :create_from_existing => @course_offering

  on(ManageCourseOfferings).view_exam_offerings
end

Given /^there is an exsiting CO with a Standard Final Exam option$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  @activity_offering =  make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.edit :send_to_scheduler => true, :defer_save => false
end

Given /^that Activity Offerings exist for the selected Course Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL304")
  @activity_offering =  make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.edit :send_to_scheduler => true, :defer_save => false
end

When /^I create a Course Offering from an existing CO with a Standard Final Exam option$/ do
  @create_co = create CourseOffering, :term=> @course_offering.term, :create_from_existing => @course_offering
end

When /^I select Final Exam Per Course Offering as the Final Exam Driver and Update the Course Offering$/ do
  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"
  @course_offering.save
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
    array = page.return_array_of_ao_codes
    if array != nil
      array.each do |code|
        page.eo_by_ao_status(code).should match /#{exp_state}/
      end
      no_of_eos = array.length
    end
    array = page.return_array_of_ao_codes("CL Leftovers")
    if array != nil
      array.each do |code|
        page.eo_by_ao_status(code, "CL Leftovers").should match /#{exp_state}/
      end
      no_of_eos = array.length
    end
    no_of_eos.should == 1
  end
end

Then /^see one Exam Offering for the Course Offering with a status of ([^"]*)$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Course Offering/
    page.eo_by_co_status.should match /#{exp_state}/
    page.count_no_of_eos_by_co.should == "1"
  end
end

When /^I view the Exam Offerings for a CO created from an existing CO with a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL305")
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture Only", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save
  @activity_offering =  make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.edit :send_to_scheduler => true, :defer_save => false

  @create_co = create CourseOffering, :term=> @course_offering.term, :create_from_existing => @course_offering

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I select Final Exam Per Activity Offering as the Final Exam Driver and Update the Course Offering$/ do
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture Only", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save
end

When /^I view the Exam Offerings for a CO created from an existing CO with multiple AOs and a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "HIST110")
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture/Discussion", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save
  @activity_offering =  make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.edit :send_to_scheduler => true, :defer_save => false

  @create_co = create CourseOffering, :term=> @course_offering.term, :create_from_existing => @course_offering

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings for a CO with two AOs and a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL201")
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture Only", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save

  on(ManageCourseOfferings).view_exam_offerings
end

Given /^that the CO has two existing AOs and a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL201")
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture Only", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save
  @activity_offering = make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.edit :send_to_scheduler => true, :defer_save => false
end

When /^I add two new AOs to the CO and then create a copy of the CO$/ do
  @add_ao_one = @course_offering.create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")
  @add_ao_two = @course_offering.create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")

  @create_co = create CourseOffering, :term=> @course_offering.term, :create_from_existing => @course_offering

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings for a CO with two new AOs and a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL201")
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture Only", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save
  @activity_offering = make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.edit :send_to_scheduler => true, :defer_save => false

  @add_ao_one = @course_offering.create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")
  @add_ao_two = @course_offering.create_ao :ao_obj => (make ActivityOfferingObject, :format => "Lecture Only")

  @create_co = create CourseOffering, :term=> @course_offering.term, :create_from_existing => @course_offering

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I create a CO with two new AOs and then view the Exam Offerings where the CO has a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL201")
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture Only", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save
  @activity_offering =  make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.edit :send_to_scheduler => true, :defer_save => false

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
  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"
  @course_offering.save

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings for a CO in an Open SOC with a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201301", :course => "ENGL304")
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture/Discussion", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings for a CO where the Course Offering Standard FE is changed to No Final Exam$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL305")
  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"
  @course_offering.save

  @course_offering.edit_offering :final_exam_type => "No Final Exam or Assessment"
  @course_offering.save

  on(ManageCourseOfferings).view_exam_offerings
end

Given /^that the CO is set to have exam offerings driven by CO$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL305")
  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"
  @course_offering.save
end

Given /^that the CO is set to have exam offerings driven by AO$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL305")
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture Only", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save
end

When /^I view the Exam Offerings for a CO where the Course Offering Standard FE setting is changed to No Final Exam$/ do
  @course_offering.edit_offering :final_exam_type => "No Final Exam or Assessment"
  @course_offering.save

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings for a CO where the Activity Offering Standard FE is changed to Alternate Final Exam$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL304")
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture/Discussion", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
   @course_offering.save

  @course_offering.edit_offering :final_exam_type => "Alternate Final Assessment"
  @course_offering.save

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings for a CO where the Course Offering No FE is changed to Standard Final Exam$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL304")
  @course_offering.edit_offering :final_exam_type => "No Final Exam or Assessment"
  @course_offering.save

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver =>"Final Exam Per Course Offering"
  @course_offering.save

  on(ManageCourseOfferings).view_exam_offerings
end

Given /^that the CO is set to have no exam offerings$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL304")
  @course_offering.edit_offering :final_exam_type => "No Final Exam or Assessment"
  @course_offering.save
end

When /^I view the Exam Offerings for a CO where the Course Offering No Standard Final Exam or Assessment is changed to Standard Final Exam driven by Course Offering$/ do
  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver =>"Final Exam Per Course Offering"
  @course_offering.save

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings for a CO where the exam is changed to Standard Final Exam driven by Activity Offering$/ do
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture Only", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings after changing the Final Exam Driver (?:to|back to) Course Offering$/ do
  @course_offering.manage
  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"
  @course_offering.save

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offering after changing the Course Offering back to Standard FE and the Final Exam Driver to Course Offering$/ do
  @course_offering.manage
  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"
  @course_offering.save

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings? after changing the Course Offering back to Standard FE and the Final Exam Driver to Activity Offering$/ do
  @course_offering.manage
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture Only", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings after changing the Final Exam Driver to Activity Offering$/ do
  @course_offering.manage
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture/Discussion", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save

  on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings after updating the Final Exam indicator to No final Exam$/ do
  @course_offering.manage
  @course_offering.edit_offering :final_exam_type => "No Final Exam or Assessment"
  @course_offering.save

  #on(ManageCourseOfferings).view_exam_offerings
end

When /^I view the Exam Offerings for a CO created from catalog with a standard final exam driven by Course Offering$/ do
  @course_offering = create CourseOffering, :term => "201208", :course => "ENGL304"
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture Only", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save

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
  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"
  @course_offering.save
  @activity_offering = make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.cancel :navigate_to_page => false
end

When /^I cancel an Activity Offering for a CO with a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture/Discussion", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save

  @activity_offering = make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering
  @activity_offering.cancel :navigate_to_page => false
end

Given /^that the Lecture AO that drives the exam is not in a cancelled state$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture/Discussion", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save
end

When /^I cancel a Discussion Activity Offering for a CO with a standard final exam driven by Activity Offering$/ do
  @activity_offering = make ActivityOfferingObject, :code => "C", :parent_course_offering => @course_offering
  @activity_offering.cancel :navigate_to_page => false
end

When /^I cancel all Activity Offerings for a CO with a standard final exam driven by Course Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"
  @course_offering.save
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
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture/Discussion", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save

  on ManageCourseOfferings do |page|
    page.codes_list.each do |code|
      ao_cancel = make ActivityOfferingObject, :code => code, :parent_course_offering => @course_offering
      ao_cancel.cancel :navigate_to_page => false
    end
  end
end

When /^I view the Exam Offerings for the Course Offering$/ do
  on(ManageCourseOfferings).view_exam_offerings
end

When /^I suspend an Activity Offering for a CO with a standard final exam driven by Course Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"
  @course_offering.save
  @course_offering.manage_and_init
  @activity_offering = @course_offering.find_ao_obj_by_code('A')
  @activity_offering.suspend :navigate_to_page => false
end

When /^I suspend the Course Offering for a CO with a standard final exam driven by Course Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"
  @course_offering.save
  on ManageCourseOfferings do |page|
    page.cluster_select_all_aos
    page.suspend_ao
    on(SuspendActivityOffering).suspend_activity
  end
end

When /^I suspend all Activity Offerings for a CO with a standard final exam driven by Course Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"
  @course_offering.save
  on ManageCourseOfferings do |page|
    page.cluster_select_all_aos
    page.suspend_ao
    on(SuspendActivityOffering).suspend_activity
  end
end

When /^I suspend an Activity Offering for a CO with a standard final exam driven by Activity Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "ENGL301")
  delivery_format_list = []
  delivery_format_list[0] = make DeliveryFormat, :format => "Lecture/Discussion", :grade_format => "Course Offering", :final_exam_activity => "Lecture"

  @course_offering.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Activity Offering",
                                 :delivery_format_list => delivery_format_list
  @course_offering.save

  @course_offering.manage_and_init
  @activity_offering = @course_offering.find_ao_obj_by_code('A')
  @activity_offering.suspend :navigate_to_page => false
end

Then /^a warning in the Final Exam Period section is displayed stating "([^"]*)"$/ do |exp_msg|
  on(EditAcademicTerms).get_exam_warning_message( @calendar.terms[0].term_type).should match /#{exp_msg}/
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
    exam_driver_activity_label.should match /\* Final Exam Driver Activity/
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
    page.span( id: "finalExamType_control").text.should == "Standard Final Exam"
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
    page.span( id: "finalExamType_control").text.should == "Standard Final Exam"
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
    page.eo_by_co_status.should match /Draft/
  end

  @test_co_list << (make CourseOffering, :term => @calendar_target.terms[0].term_code, :course => @co_list[1].course)
  @test_co_list[1].manage
  on ManageCourseOfferings do |page|
    page.view_exam_offerings_link.present?.should == false
  end

  @test_co_list << (make CourseOffering, :term => @calendar_target.terms[0].term_code, :course => @co_list[2].course)
  @test_co_list[2].manage
  on(ManageCourseOfferings).view_exam_offerings
  on(ViewExamOfferings).eo_by_ao_status("A").should match /Draft/

  @test_co_list << (make CourseOffering, :term => @calendar_target.terms[0].term_code, :course => @co_list[3].course)
  @test_co_list[3].manage
  on(ManageCourseOfferings).view_exam_offerings
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Course Offering/
    page.eo_by_co_status.should match /Draft/
  end
end

Then /^the Exam Offerings for Course Offering should be in a ([^"]*) state$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Course Offering/
    page.eo_by_co_status.should match /#{exp_state}/
    page.count_no_of_eos_by_co.should == "1"
  end
end

Then /^the EO in the Exam Offerings for Course Offering table should be in a ([^"]*) state$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Course Offering/
    page.eo_by_co_status.should match /#{exp_state}/
    page.count_no_of_eos_by_co.should == "1"
  end
end

Then /^there should only be one EO in the Exam Offerings for Course Offering table$/ do
  on(ViewExamOfferings).count_no_of_eos_by_co.should == "1"
end

Then /^the Exam Offerings for Course Offering in the EO for CO table should be in a ([^"]*) state$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Course Offering/
    page.eo_by_co_status.should match /#{exp_state}/
    page.count_no_of_eos_by_co.should == "1"
  end
end

Then /^the Exam Offerings for each Activity Offering in the EO for AO table should be in a ([^"]*) state$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Activity Offering/
    array = page.return_array_of_ao_codes
    array.each do |code|
      page.eo_by_ao_status(code).should match /#{exp_state}/
    end
    array = page.return_array_of_ao_codes("CL Leftovers")
    if array != nil
      array.each do |code|
        page.eo_by_ao_status(code, "CL Leftovers").should match /#{exp_state}/
      end
    end
  end
end

Then /^the Exam Offering for Activity Offering should not be in a ([^"]*) state$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Activity Offering/
    array = page.return_array_of_ao_codes
    array.each do |code|
      page.eo_by_ao_status(code).should_not match /#{exp_state}/
    end
  end
end

Then /^the Exam Offerings? for Activity Offering should be in a ([^"]*) state$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Activity Offering/
    array = page.return_array_of_ao_codes
    if array != nil
      array.each do |code|
        page.eo_by_ao_status(code).should match /#{exp_state}/
      end
      no_of_eos = array.length
    end
    array = page.return_array_of_ao_codes("CL Leftovers")
    if array != nil
      array.each do |code|
        page.eo_by_ao_status(code, "CL Leftovers").should match /#{exp_state}/
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
    array = page.return_array_of_ao_codes
    if array != nil
      array.each do |code|
        if code == @activity_offering.code
          page.eo_by_ao_status(code).should match /#{exp_state}/
          no_of_eos += 1
        end
      end
    end
    array = page.return_array_of_ao_codes("CL Leftovers")
    if array != nil
      array.each do |code|
        if code == @activity_offering.code
          page.eo_by_ao_status(code, "CL Leftovers").should match /#{exp_state}/
          no_of_eos += 1
        end
      end
    end
    no_of_eos.should == 1
  end
end

Then /^the EOs in the Exam Offerings for Activity Offering table should be in a ([^"]*) state$/ do |exp_state|
  on ViewExamOfferings do |page|
    page.table_header_text.should match /for Activity Offering/
    array = page.return_array_of_ao_codes
    if array != nil
      array.each do |code|
        page.eo_by_ao_status(code).should match /#{exp_state}/
      end
    end
    array = page.return_array_of_ao_codes("CL Leftovers")
    if array != nil
      array.each do |code|
        page.eo_by_ao_status(code, "CL Leftovers").should match /#{exp_state}/
      end
    end
  end
end

Then /^there should be one EO for each AO of the course in the Exam Offering for Activity Offering table$/ do
  on ViewExamOfferings do |page|
    array = page.return_array_of_ao_codes
    if array != nil
      no_of_eos = array.length
    end
    array = page.return_array_of_ao_codes("CL Leftovers")
    if array != nil
      no_of_eos = array.length
    end
    no_of_eos.should >= 1
  end
end

Then /^the ([\d]*) Exam Offerings? for Activity Offering should be in a ([^"]*) state$/ do |num,exp_state|
  on ViewExamOfferings do |page|
    page.ao_table_header_text.should match /for Activity Offering/
    array = page.return_array_of_ao_codes
    if array != nil
      array.each do |code|
        page.eo_by_ao_status(code).should match /#{exp_state}/
      end
      no_of_eos = array.length
    end
    array = page.return_array_of_ao_codes("CL Leftovers")
    if array != nil
      array.each do |code|
        page.eo_by_ao_status(code, "CL Leftovers").should match /#{exp_state}/
      end
      no_of_eos = array.length
    end
    no_of_eos.should == num
  end
end

Then /^there should be ([^"]*) Exam Offerings? by Activity Offering for the course$/ do |no_of_aos|
  on ViewExamOfferings do |page|
    array = page.return_array_of_ao_codes
    array.length.should == no_of_aos.to_i
  end
end

Then /^there should be ([\d]*) EOs in the Exam Offerings by Activity Offering table for the course$/ do |no_of_aos|
  on ViewExamOfferings do |page|
    array = page.return_array_of_ao_codes
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
  on(ViewExamOfferings).canceled_eo_table.rows[1].cells[0].text.should == exp_msg
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
  on(ViewExamOfferings).canceled_eo_table.rows[1].text.should match /Canceled.*#{@activity_offering.code}/m
end

Given /^that a CO allows for multiple Format Offerings and has one existing format offering and a standard exam driven by Course Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term=> "201208", :course => "ENGL304")
end

When /^I edit the CO to add a second Format Offering$/ do
  on(ManageCourseOfferings).edit_course_offering
  delivery_format = make DeliveryFormat, :format => "Lecture", :grade_format => "Course Offering", :final_exam_activity => "Lecture"
  @course_offering.add_delivery_format delivery_format
  @course_offering.save
end

When /^I create a Course Offering from copy in a term with a final exam period$/ do
  @copy_co = create CourseOffering, :create_by_copy => @course_offering

  @copy_co.edit_offering :final_exam_type => "Standard Final Exam",
                                 :final_exam_driver => "Final Exam Per Course Offering"
  @copy_co.save
end

Then /^there should be a warning message stating that "(.*?)"$/ do |exp_msg|
  on(ManageCourseOfferings).growl_warning_text.should match /#{Regexp.escape(exp_msg)}/
end