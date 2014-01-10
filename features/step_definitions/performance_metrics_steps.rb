When /^I log in as a Schedule Coordinator$/ do
  @performance_test = make PerformanceTest
  on KSFunctionalHome do |page|
  #visit LUMMainPage do |page|
    page.logout
    page.username_field.set "martha"
    page.password_field.set "martha"
    @performance_test.start
    page.login_button.click


    page.enrollment_link.wait_until_present
    @performance_test.end
  end

 # end
end

Then /^the transaction takes less than "([^"]*)" seconds$/ do |time_requirement|
  puts @performance_test.test_time.to_s
  #@performance_test.test_time.should <= time_requirement.to_f
end

When /^I search for an Academic Calendar$/ do
  @performance_test = make PerformanceTest
  go_to_calendar_search
  on CalendarSearch do |page|
    page.search_for_select.select "Academic Calendar"
    page.name.set "2012-2013 Academic Calendar"
    page.year.set "2012"
    @performance_test.start
    page.search
    page.loading.wait_while_present
    @performance_test.end
  end
end

When /^I edit the Academic Calendar$/ do
  on CalendarSearch do |page|
    @performance_test.start
    page.edit  "2012-2013 Academic Calendar"
    @performance_test.end
  end
end

When /^I update a field and save the Academic Calendar$/ do
  on EditAcademicCalendar do |page|
    page.calendar_end_date.set "08/23/2012"
    @performance_test.start
    page.save
    @performance_test.end
  end
end

When /^I create a new Academic Calendar$/ do
  on EditAcademicCalendar do |page|
    page.academic_calendar_name.set random_alphanums.strip
    page.calendar_start_date.set "09/01/#{next_year[:year]}"
    page.calendar_end_date.set "06/25/#{next_year[:year] + 1}"
    @performance_test.start
    page.save
    @performance_test.end
  end
end

When /^I start a blank Academic Calendar$/ do
  @performance_test = make PerformanceTest
  go_to_academic_calendar
  on CreateAcadCalendar do |page|
    @performance_test.start
    page.start_blank_calendar
    @performance_test.end
  end
end

When /^I search for an Holiday Calendar$/ do
  @performance_test = make PerformanceTest
  go_to_calendar_search
  on CalendarSearch do |page|
    page.search_for_select.select "Holiday Calendar"
    page.name.set "2015-2016 Holiday Calendar"
    page.year.set "2015"
    @performance_test.start
    page.search
    page.loading.wait_while_present
    @performance_test.end
  end
end


When /^I edit the Holiday Calendar$/ do
  on CalendarSearch do |page|
    @performance_test.start
    page.edit  "2015-2016 Holiday Calendar"
    @performance_test.end
  end
end

When /^I update a field and save the Holiday Calendar$/ do
  on CreateEditHolidayCalendar do |page|
    page.end_date.set "08/20/2016"
    @performance_test.start
    page.save
    @performance_test.end
  end
end

When /^I start a blank Holiday Calendar$/ do
  @performance_test = make PerformanceTest
  go_to_holiday_calendar
  on CopyHolidayCalendar do |page|
    @performance_test.start
    page.start_blank_calendar
    @performance_test.end
  end
end

When /^I create a new Holiday Calendar$/ do
  on CreateEditHolidayCalendar do |page|
    page.calendar_name.set random_alphanums.strip
    page.start_date.set "09/01/#{next_year[:year]}"
    page.end_date.set "06/25/#{next_year[:year] + 1}"
    @performance_test.start
    page.save
    @performance_test.end
  end
end

When /^I search for an existing population$/ do
  @performance_test = make PerformanceTest
  go_to_manage_population
  on ManagePopulations do |page|
    page.keyword.set "Athlete"
    @performance_test.start
    page.search
    @performance_test.end
  end
end

When /^I edit the population$/ do
  on ManagePopulations do |page|
    @performance_test.start
    page.target_row("Athlete").link(text: "edit").click
    page.loading.wait_while_present
    @performance_test.end
  end
end

When /^I update a field and save the population$/ do
  on EditPopulation do |page|
    page.description.set "Members of Sports"
    @performance_test.start
    page.update
    @performance_test.end
  end
end

When /^I create a new populations$/ do
  @performance_test = make PerformanceTest
  go_to_manage_population
  on ManagePopulations do |page|
    @performance_test.start
    page.create_new
    @performance_test.end
  end
end

When /^I save the population$/ do
  on CreatePopulation do |page|
    page.name.set random_alphanums.strip
    page.description.set  random_alphanums.strip
    @performance_test.start
    page.create
    @performance_test.end
  end
end

When /^I search for a registration Window$/ do
  @performance_test = make PerformanceTest
  go_to_manage_reg_windows
  on RegistrationWindowsTermLookup do |page|
    page.term_type.select "Fall Term"
    page.year.set "2012"
    @performance_test.start
    page.search
    @performance_test.end
  end
end

When /^I select all registration periods$/ do
  on RegistrationWindowsPeriodLookup do |page|
    page.period_id.select 'All Registration Periods for this Term'
    @performance_test.start
    page.show
    @performance_test.end
  end
end

When /^I add a registration window and save$/ do
  on RegistrationWindowsCreate do |page|
    page.period_key.select "Senior Registration"
    page.appointment_window_info_name.set "TestDate"
    page.assigned_population_name.set "Athlete"
    page.start_date.set "03/30/2012"
    page.start_time.set "10:00"
    page.start_time_am_pm.select "am"
    page.window_type_key.select "One Slot per Window"
    page.loading.wait_while_present
    page.add
    @performance_test.start
    page.save
    @performance_test.end

    page.enrollment_via_breadcrumb

    while page.alert.exists?
      page.alert.ok
    end

  end
end

When /^I search for a course by course code$/ do
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set "ENGL101A"
    @performance_test.start
    page.perf_show
    @performance_test.end
  end
end

When /^I edit the course offering for performance$/ do
  on ManageCourseOfferings do |page|
    @performance_test.start
    page.edit_course_offering
    @performance_test.end
  end
end

When /^I save the course change$/ do
  on CourseOfferingCreateEdit do |page|
    if page.waitlist_checkbox.set?
      page.waitlist_checkbox.clear
    else
      page.waitlist_checkbox.set
    end
    @performance_test.start
    page.submit
    @performance_test.end
  end
end

When /^I delete the course offering$/ do
  on ManageCourseOfferings do |page|
    page.delete_course_offering
  end
  on DeleteCourseOffering do |page|
    @performance_test.start
    page.confirm_delete
    @performance_test.end
  end
end


When /^I copy a course offering$/ do
  on CopyCourseOffering do |page|
    @performance_test.start
    page.create_copy
    @performance_test.end
  end
end

When /^I click copy for a large course offering$/ do
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set "ENGL101"
    page.perf_show
  end
  on ManageCourseOfferingList do |page|
    @performance_test.start
    page.copy "ENGL101"
    @performance_test.end
  end
end

When /^I click copy for a medium course offering$/ do
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set "ENGL24"
    page.perf_show
  end
  on ManageCourseOfferingList do |page|
    @performance_test.start
    page.copy "ENGL241"
    @performance_test.end
  end
end

When /^I click copy for a small course offering$/ do
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set "ENGL3"
    page.perf_show
  end
  on ManageCourseOfferingList do |page|
    @performance_test.start
    page.copy "ENGL311"
    @performance_test.end
  end
end





When /^I create a basic course offering$/ do
  @performance_test = make PerformanceTest
  go_to_create_course_offerings
  on CreateCourseOffering do  |page|
    page.target_term.set "201301"
    page.catalogue_course_code.set "ENGL316"
    page.continue
    while page.alert.exists?
      page.alert.cancel
    end
  end
  on CourseOfferingCreateEdit do |page|
    page.suffix.set random_alphanums.strip
    create DeliveryFormat
    @performance_test.start
    page.create_offering
    @performance_test.end
  end
end


When /^I create a jointly defined course offering$/ do
  @performance_test = make PerformanceTest
  go_to_create_course_offerings
  on CreateCourseOffering do  |page|
    page.target_term.set "201301"
    page.catalogue_course_code.set "ENGL316"
    page.continue
  end
  on CourseOfferingCreateEdit do |page|
    page.suffix.set random_alphanums.strip
    page.create_new_joint_defined_course_row_1
    create DeliveryFormat
    @performance_test.start
    page.create_offering
    @performance_test.end
  end
end

When /^I edit a large Activity Offering for performance$/ do
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set "ENGL101A"
    page.perf_show
    @performance_test.start
    page.edit "A"
    @performance_test.end
  end
end

When /^I edit a medium Activity Offering for performance$/ do
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set "ENGL241"
    page.perf_show
    @performance_test.start
    page.edit "A"
    @performance_test.end
  end
end

When /^I edit a small Activity Offering for performance$/ do
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set "ENGL311"
    page.perf_show
    @performance_test.start
    page.edit "A"
    @performance_test.end
  end
end

When /^I copy a large Activity Offering for performance$/ do
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set "ENGL101A"
    page.perf_show
    @performance_test.start
    page.copy "A"
    @performance_test.end
  end
end

When /^I copy a medium Activity Offering for performance$/ do
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set "ENGL241"
    page.perf_show
    @performance_test.start
    page.copy "A"
    @performance_test.end
  end
end

When /^I copy a small Activity Offering for performance$/ do
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set "ENGL311"
    page.perf_show
    @performance_test.start
    page.copy "A"
    @performance_test.end
  end
end

When /^I add Delivery Logistics to a large activity offering and save$/ do
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set "ENGL101A"
    page.perf_show
    page.edit "A"
  end

  on ActivityOfferingMaintenance do |page|
    page.add_days.set "MWF"
    page.add_start_time.click
    page.add_start_time.set "10:00 AM"
    page.add_start_time.fire_event "onblur"
    page.add_end_time.click
    page.loading.wait_while_present
    page.add_end_time.set "11:00 AM"
    page.add_facility.click
    page.loading.wait_while_present
    page.add_facility.set "IPT"
    page.add_room.set "1116"
    page.add_new_delivery_logistics
    @performance_test.start
    page.save
    @performance_test.end
  end
end

When /^I add Delivery Logistics to a medium activity offering and save$/ do
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set "ENGL241"
    page.perf_show
    page.edit "A"
  end

  on ActivityOfferingMaintenance do |page|
    page.add_days.set "MWF"
    page.add_start_time.click
    page.add_start_time.set "10:00 AM"
    page.add_start_time.fire_event "onblur"
    page.add_end_time.click
    page.loading.wait_while_present
    page.add_end_time.set "11:00 AM"
    page.add_facility.click
    page.loading.wait_while_present
    page.add_facility.set "IPT"
    page.add_room.set "1116"
    page.add_new_delivery_logistics
    @performance_test.start
    page.save
    @performance_test.end
  end
end


When /^I add Delivery Logistics to a small activity offering and save$/ do
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set "ENGL311"
    page.perf_show
    page.edit "A"
  end

  on ActivityOfferingMaintenance do |page|
    page.add_days.set "MWF"
    page.add_start_time.click
    page.add_start_time.set "10:00 AM"
    page.add_start_time.fire_event "onblur"
    page.add_end_time.click
    page.loading.wait_while_present
    page.add_end_time.set "11:00 AM"
    page.add_facility.click
    page.loading.wait_while_present
    page.add_facility.set "IPT"
    page.add_room.set "1116"
    page.add_new_delivery_logistics
    @performance_test.start
    page.save
    @performance_test.end
  end
end

When /^I search for a SOC$/ do
  @performance_test = make PerformanceTest
  go_to_manage_soc
  on ManageSocPage do |page|
    page.term_code.set "201301"
    @performance_test.start
    page.go_action
    @performance_test.end
  end
end

When /^I search for a course by the "([^"]*)" subject code$/ do |subj|
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set subj
    @performance_test.start
    page.perf_show
    @performance_test.end
  end
end

When /^I search for a large course by course code to delete$/ do
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set "ENGL101H"
    @performance_test.start
    page.perf_show
    @performance_test.end
  end
end

When /^I search for a medium course by course code to delete$/ do
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set "ENGL241A"
    @performance_test.start
    page.perf_show
    @performance_test.end
  end
end

When /^I search for a small course by course code to delete$/ do
  @performance_test = make PerformanceTest
  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set "201301"
    page.input_code.set "ENGL311A"
    @performance_test.start
    page.perf_show
    @performance_test.end
  end
end

When /^I manage an AO's prerequisites$/ do
  @performance_test = make PerformanceTest
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite"
  @prereq = make AOPreparationPrerequisiteRule, :term => "201301", :course => "ENGL313"
  #@prereq.sepr_add_ao_rule

  @course_offering = make CourseOffering, {:course => "ENGL313", :term => "201301"}
  @course_offering.manage

  on ManageCourseOfferings do |page|
   page.loading.wait_while_present(200)
   @performance_test.start
   page.ao_requisites("A")
   @performance_test.end
  end
end

When /^I add a rule to the Prerequisite section$/ do

  on ActivityOfferingRequisites do |page|
    page.loading.wait_while_present
    @activityOR.open_agenda_section

    if page.prereq_add_link.exists?
      page.loading.wait_while_present
      page.prereq_add
    end
    end

  @prereq.rp_sepr_course_rule( "add", "", "ENGL101")
  @prereq.rp_sepr_text_rule( "add", "", "free form text input value")
  @prereq.rp_sepr_all_courses_rule( "group", "A", "HIST416,ENGL478", "", "")
  @prereq.rp_sepr_text_rule( "add", "", "Text to copy")
  @prereq.rp_sepr_number_courses_rule( "group", "C", "2", "BSCI202,BSCI361,HIST110", "", "")

  on ManageAORequisites do |page|
    @performance_test.start
    page.update_rule_btn
    page.loading.wait_while_present
    @performance_test.end
  end
end

When /^then I submit the rule changes$/ do
  on ActivityOfferingRequisites do |page|
    @performance_test.start
    page.submit
    page.loading.wait_while_present(200)
    @performance_test.end
  end
end

When /^I suppress a new rule change$/ do
  on ManageCourseOfferings do |page|
    page.loading.wait_while_present(200)
    page.ao_requisites("A")
  end

  on ActivityOfferingRequisites do |page|
    page.loading.wait_while_present
    @activityOR.open_agenda_section

    if page.prereq_edit_suppress_link.exists?
      page.loading.wait_while_present
      @performance_test.start
      page.prereq_edit_suppress
      page.loading.wait_while_present(200)
      @performance_test.end
      page.submit
    end
  end
end

When /^I revert the new rule change$/ do
 # on ManageCourseOfferings do |page|
 #   page.loading.wait_while_present(200)
 #   page.ao_requisites("A")
 # end

  on ActivityOfferingRequisites do |page|
    page.loading.wait_while_present
    @activityOR.open_agenda_section

    if page.prereq_revert_link.exists?
      page.loading.wait_while_present
      @performance_test.start
      page.prereq_revert
      page.loading.wait_while_present(200)
      @performance_test.end
      page.submit
    end
  end
end


When /^I search for a scheduled class$/ do
  go_to_display_schedule_of_classes
  @performance_test = make PerformanceTest
  @schedule_of_classes = make ScheduleOfClasses, :course_search_parm => "ENGL206", :exp_course_list => ["ENGL206"], :exp_cluster_list => ["CL 101","CL Leftovers"], :exp_reg_group_list => ["1001","1002","1003","1004"]
  @performance_test.start
  @schedule_of_classes.display
  @performance_test.end
end

When /^I view the detailed schedule of classes$/ do

  on DisplayScheduleOfClasses do |page|
    @performance_test.start
    page.course_expand("ENGL206")
    @performance_test.end
  end
end

When /^I search for a Final Exam$/ do
  @performance_test = make PerformanceTest
  @matrix = make FinalExamMatrix, :term_type => "Fall Term"

  go_to_manage_final_exam_matrix

  on ManageFEMatrix do |page|
    page.term_type_select.select  @matrix.term_type
    @performance_test.start
    page.show
    @performance_test.end
  end
end

When /^when I edit the matrix$/ do
  on FEMatrixView do |page|
    @performance_test.start
    page.edit "TH at 11:00 AM - 12:15 PM.", "Standard"
    @performance_test.end
  end
end

When /^when I add a standard rule$/ do
  on FEMatrixEdit do |page|
    page.add_statement
    page.rule_dropdown.select "If Course meets on <timeslot>"

    page.rule_days.set @matrix.days
    page.rule_starttime.set @matrix.start_time
    page.rule_starttime_ampm.select @matrix.time_ampm
    page.rule_endtime.set @matrix.end_time
    page.rule_endtime_ampm.select @matrix.time_ampm
    page.preview_change
    page.loading.wait_while_present


    @performance_test.start
    page.update_rule
    page.loading.wait_while_present
    @performance_test.end
  end
end

And /^when I submit the rule change$/ do
  on FEMatrixView do |page|
    @performance_test.start
    page.submit
    @performance_test.end
  end
end
