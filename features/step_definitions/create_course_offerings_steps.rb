When /^I create a Course Offering with "([^"]*)" delivery Formats$/ do |format_type|
  delivery_format_list = []
  if format_type == "selected lecture"
    delivery_format_list << (make DeliveryFormat, :format => "Lecture", :grade_format => "Course Offering", :final_exam_activity => "Lecture")
  else
    delivery_format_list << (make DeliveryFormat, :format => "Lecture", :grade_format => "Course Offering", :final_exam_activity => "Lecture")
    delivery_format_list << (make DeliveryFormat, :format => "Discussion/Lecture", :grade_format => "Course Offering", :final_exam_activity => "Lecture")
  end
  @course_offering = create CourseOffering, :term=> Rollover::OPEN_SOC_TERM, :course => "ENGL222", :delivery_format_list => delivery_format_list
end

And /^I create a Course Offering with random Delivery Formats$/ do
  @course_offering = create CourseOffering, :term=> Rollover::MAIN_TEST_TERM_TARGET
end

Then /^the new Course Offering should contain only the selected delivery formats$/ do
  @course_offering.search_by_subjectcode
  @course_offering.view_course_details
  on CourseOfferingInquiry do  |page|
    @course_offering.delivery_format_list.each do |del_option|
      page.get_delivery_format(del_option.format).should == del_option.format
      page.get_grade_roster_level(del_option.format).should == del_option.grade_format
    end
    page.close
  end

end

And /^I create a course offering from an existing offering$/ do
  @course_offering = create CourseOffering, :term=> Rollover::PUBLISHED_SOC_TERM, :course => "CHEM132", :create_from_existing=>(make CourseOffering, :term=> "201201", :course => "CHEM132")
end

When /^I create a course offering from an existing offering in a different term and choose to exclude instructor information$/ do
  @course_offering = create CourseOffering, :term=> Rollover::FINAL_EDITS_SOC_TERM, :course => "CHEM132", :exclude_instructor => true, :create_from_existing=>(make CourseOffering, :term=> Rollover::MAIN_TEST_TERM_SOURCE, :course => "CHEM132")
end

When /^I create a course offering from an existing offering in a different term and choose to exclude scheduling information$/ do
  @course_offering = create CourseOffering, :term=> Rollover::FINAL_EDITS_SOC_TERM, :course => "CHEM132", :exclude_scheduling => true, :create_from_existing=>(make CourseOffering, :term=> Rollover::MAIN_TEST_TERM_SOURCE, :course => "CHEM132")
end

When /^I create a course offering from an existing offering within same term and choose to exclude scheduling information$/ do
  @course_offering = create CourseOffering, :term=> Rollover::PUBLISHED_SOC_TERM, :course => "ENGL295", :exclude_scheduling => true, :create_from_existing=>(make CourseOffering, :term=> Rollover::PUBLISHED_SOC_TERM, :course => "ENGL295")
end

When /^I create a course offering from an existing offering within same term and choose to exclude instructor information using Create CO$/ do
  @course_offering = create CourseOffering, :term=> Rollover::PUBLISHED_SOC_TERM, :course => "ENGL295", :exclude_instructor => true, :create_from_existing=>(make CourseOffering, :term=> Rollover::PUBLISHED_SOC_TERM, :course => "ENGL295")
end

When /^I create a course offering from an existing offering within same term and choose to exclude instructor information using Manage CO$/ do
  @course_offering = create CourseOffering, :exclude_instructor => true, :create_by_copy => (make CourseOffering, :course => "CHEM132", :term=> Rollover::MAIN_TEST_TERM_SOURCE)
end

Then /^the new Course Offering should be displayed in the list of available offerings$/ do
  @course_offering.search_by_subjectcode
  @course_offering.view_course_details
end

And /^the new Course Offering should not contain any instructor information in its activity offerings$/ do
  @course_offering.manage
  on ManageCourseOfferings do |page|
    page.get_instructor_list.should == ""
  end
end

And /^the new Course Offering should not contain any scheduling information in its activity offerings$/ do
  @course_offering.manage_and_init
  on ManageCourseOfferings do |page|
    ao_list = @course_offering.get_ao_list
    ao_list.each do |ao_code|
      page.view_activity_offering(ao_code.code)
      on ActivityOfferingInquiry do |page2|
        page2.requested_scheduling_information_days.present?.should be_false
        page2.actual_scheduling_information_days.present?.should be_false
        page2.close
      end
    end
  end
end

And /^I create a Course Offering from catalog with Activity Offerings assigned to subterms$/ do
  delivery_format_list = []
  delivery_format_list << (make DeliveryFormat, :format => "Lab", :grade_format => "Lab", :final_exam_activity => "Lab")

  @course_offering = create CourseOffering, :term=> @term.term_code,
                            :course => "CHEM132",
                            :delivery_format_list => delivery_format_list

  @rsi_list = {}
  @rsi_list["MT"] = make SchedulingInformation, :days => "MT",
                         :start_time => "10:00", :start_time_ampm => "am",
                         :end_time => "10:50", :end_time_ampm => "am",
                         :facility => "PHYS", :facility_long_name => "PHYS" , :room => "4102"

  @activity_offering = create ActivityOffering, :parent_course_offering => @course_offering,  :subterm => @subterm_list[0].subterm_type,
                              :format => "Lab Only", :activity_type => "Lab" , :requested_scheduling_information_list => @rsi_list
  @activity_offering.save

  @rsi_list2 = {}
  @rsi_list2["WF"] = make SchedulingInformation, :days => "WF", :start_time => "10:00", :start_time_ampm => "am", :end_time => "10:50", :end_time_ampm => "am",
                          :facility => "PHYS", :facility_long_name => "PHYS", :room => "4102"
  @activity_offering2 = create ActivityOffering,  :parent_course_offering => @course_offering, :subterm => @subterm_list[1].subterm_type,
                               :format => "Lab Only", :activity_type => "Lab" , :requested_scheduling_information_list => @rsi_list2

  @activity_offering2.save
end

And /^I create a Course Offering with an Activity Offerings assigned to subterms$/ do
  step 'I create a Course Offering from catalog with Activity Offerings assigned to subterms in my admin org'
end

And /^I create a Course Offering from catalog with Activity Offerings assigned to subterms in my admin org$/ do
  delivery_format_list = []
  delivery_format_list << (make DeliveryFormat, :format => "Lecture", :grade_format => "Lecture", :final_exam_activity => "Lecture")

  @course_offering = create CourseOffering, :term=> @term.term_code,
                            :course => "ENGL211",
                            :delivery_format_list => delivery_format_list

  @rsi_list = {}
  @rsi_list["MT"] = make SchedulingInformation, :days => "MT", :start_time => "10:00", :start_time_ampm => "am", :end_time => "10:50", :end_time_ampm => "am",
                         :facility => "PHYS", :facility_long_name => "PHYS", :room => "4102"

  @activity_offering = create ActivityOffering, :parent_course_offering => @course_offering,  :subterm => @subterm_list[0].subterm_type,
                              :format => "Lecture Only", :activity_type => "Lecture" , :requested_scheduling_information_list => @rsi_list
  @activity_offering.save

  @rsi_list2 = {}
  @rsi_list2["WF"] = make SchedulingInformation, :days => "WF", :start_time => "10:00", :start_time_ampm => "am", :end_time => "10:50", :end_time_ampm => "am",
                          :facility => "PHYS", :facility_long_name => "PHYS", :room => "4102"
  @activity_offering2 = create ActivityOffering,  :parent_course_offering => @course_offering, :subterm => @subterm_list[1].subterm_type,
                               :format => "Lecture Only", :activity_type => "Lecture" , :requested_scheduling_information_list => @rsi_list2

  @activity_offering2.save
end

And /^I create a Course Offering from catalog with Activity Offerings$/ do
  delivery_format_list = []
  delivery_format_list << (make DeliveryFormat, :format => "Lab", :grade_format => "Lab", :final_exam_activity => "Lab")

  @course_offering = create CourseOffering, :term=> @term.term_code,
                            :course => "CHEM132",
                            :delivery_format_list => delivery_format_list

  @rsi_list = {}
  @rsi_list["MT"] = make SchedulingInformation, :days => "MT", :start_time => "10:00", :start_time_ampm => "am", :end_time => "10:50", :end_time_ampm => "am",
                         :facility => "PHYS", :facility_long_name => "PHYS", :room => "4102"

  @activity_offering = create ActivityOffering, :parent_course_offering => @course_offering, # :subterm => @subterm_list[0].subterm_type,
                              :format => "Lab Only", :activity_type => "Lab" , :requested_scheduling_information_list => @rsi_list
  @activity_offering.save

  @rsi_list2 = {}
  @rsi_list2["WF"] = make SchedulingInformation, :days => "WF", :start_time => "10:00", :start_time_ampm => "am", :end_time => "10:50", :end_time_ampm => "am",
                          :facility => "PHYS", :facility_long_name => "PHYS", :room => "4102"
  @activity_offering2 = create ActivityOffering,  :parent_course_offering => @course_offering, #:subterm => @subterm_list[1].subterm_type,
                               :format => "Lab Only", :activity_type => "Lab" , :requested_scheduling_information_list => @rsi_list2
  @activity_offering2.save
end
