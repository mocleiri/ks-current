Given /^It is "After" the first day of classes$/ do
  step "I am logged in as a Schedule Coordinator"
  @calendar = make AcademicCalendar, :year => Rollover::PUBLISHED_MILESTONES_SOC_TERM_YEAR
  @term = make AcademicTermObject, :parent_calendar => @calendar, :term_name=> Rollover::PUBLISHED_MILESTONES_SOC_TERM_NAME,
               :term_type=>AcademicTermObject::WINTER_TERM_TYPE
  @calendar.terms << @term

  @calendar.terms[0].edit

  @keydategroup = make KeyDateGroupObject, :parent_term => @term, :key_date_group_type=> "Instructional"
  @keydate = make KeyDateObject, :parent_term => @term, :parent_key_date_group => @keydategroup,
                  :key_date_type => "First Day of Classes", :start_date => (Date.today - 1).strftime("%m/%d/%Y"),
                  :end_date=>(Date.today - 1).strftime("%m/%d/%Y"), :term_index=> @tindex
  @keydategroup.key_dates << @keydate
  @calendar.terms[0].add_key_date_group @keydategroup

end

Given /^It is "Before" the first day of classes and "Before" the first day to add classes/ do
  step "I am logged in as a Schedule Coordinator"
  @calendar = make AcademicCalendar, :year => Rollover::PUBLISHED_MILESTONES_SOC_TERM_YEAR
  @term = make AcademicTermObject, :parent_calendar => @calendar,
               :term_name=> Rollover::PUBLISHED_MILESTONES_SOC_TERM_NAME, :term_type=>AcademicTermObject::WINTER_TERM_TYPE
  @calendar.terms << @term

  @calendar.terms[0].edit

  @keydategroup = make KeyDateGroupObject, :parent_term => @term, :key_date_group_type=> "Instructional"
  @keydate = make KeyDateObject, :parent_term => @term, :parent_key_date_group => @keydategroup,
                  :key_date_type => "First Day of Classes", :start_date => (Date.today + 5).strftime("%m/%d/%Y"),
                  :end_date=>(Date.today + 5).strftime("%m/%d/%Y")
  @keydategroup.key_dates << @keydate
  @calendar.terms[0].add_key_date_group @keydategroup

  @keydategroup = make KeyDateGroupObject, :parent_term => @term, :key_date_group_type=> "Registration"
  @keydate2 = make KeyDateObject, :parent_term => @term, :parent_key_date_group => @keydategroup,
                   :key_date_type => "Registration Period 1", :start_date => (Date.today + 1).strftime("%m/%d/%Y"),
                   :end_date=>(Date.today + 4).strftime("%m/%d/%Y"), :date_range => true
  @keydategroup.key_dates << @keydate2
  @calendar.terms[0].add_key_date_group @keydategroup
end

Given /^It is "Before" the first day of classes and "After" the first day to add classes/ do
  step "I am logged in as a Schedule Coordinator"
  @calendar = make AcademicCalendar, :year => Rollover::PUBLISHED_MILESTONES_SOC_TERM_YEAR
  @term = make AcademicTermObject, :parent_calendar => @calendar,
               :term_name=> Rollover::PUBLISHED_MILESTONES_SOC_TERM_NAME, :term_type=>AcademicTermObject::WINTER_TERM_TYPE
  @calendar.terms << @term

  @calendar.terms[0].edit

  @keydategroup = make KeyDateGroupObject, :parent_term => @term, :key_date_group_type=> "Instructional"
  @keydate = make KeyDateObject, :parent_term => @term, :parent_key_date_group => @keydategroup, :key_date_type => "First Day of Classes",
                  :start_date => (Date.today + 5).strftime("%m/%d/%Y"), :end_date=>(Date.today + 5).strftime("%m/%d/%Y")
  @keydategroup.key_dates << @keydate
  @calendar.terms[0].add_key_date_group @keydategroup

  @keydategroup = make KeyDateGroupObject, :parent_term => @term,:key_date_group_type=> "Registration"
  @keydate2 = make KeyDateObject, :parent_term => @term, :parent_key_date_group => @keydategroup, :key_date_type => "Registration Period 1",
                   :start_date => (Date.today - 4).strftime("%m/%d/%Y"), :end_date=>(Date.today +1).strftime("%m/%d/%Y")
  @keydategroup.key_dates << @keydate2
  @calendar.terms[0].add_key_date_group @keydategroup
end

When /^I do not have access to copy an activity offering$/ do
  on ManageCourseOfferings do |page|
    page.copy_link("A").present?.should == false
  end
end
Then /^I edit an activity offering in my department$/ do
  step "I manage a course offering in my admin org"
  on ManageCourseOfferings do |page|
    page.edit("A")
  end
end

When /^I do not have access to edit maximum enrollment$/ do
  on ActivityOfferingMaintenance do |page|
    page.total_maximum_enrollment.present?.should == false
  end
end

When /^I verify the exam periods for the milestones test fall and spring terms$/ do
  @calendar = make AcademicCalendar, :year => "2014"
  @term = make AcademicTermObject, :parent_calendar => @calendar, :term => "Fall"
  @calendar.terms << @term

  @exam_period = make ExamPeriodObject, :parent_term => @term
  @calendar.terms[0].exam_period = @exam_period

  @calendar.terms[0].exam_period.edit :exclude_saturday => false, :exclude_sunday => false, :defer_save => true

  @calendar.year = "2015"
  @term = make AcademicTermObject, :parent_calendar => @calendar, :term => "Spring"
  @calendar.terms << @term

  @exam_period = make ExamPeriodObject, :parent_term => @term
  @calendar.terms[1].exam_period = @exam_period

  @calendar.terms[1].exam_period.edit :exclude_saturday => false, :exclude_sunday => false, :navigate_to_page => false
end

When /^I verify the exam periods for the published test fall and spring terms$/ do
  @calendar = make AcademicCalendar, :year => "2015"
  @term = make AcademicTermObject, :parent_calendar => @calendar, :term => "Fall"
  @calendar.terms << @term

  @exam_period = make ExamPeriodObject, :parent_term => @term
  @calendar.terms[0].exam_period = @exam_period

  @calendar.terms[0].exam_period.edit :exclude_saturday => false, :exclude_sunday => false, :defer_save => true

  @calendar.year = "2016"
  @term = make AcademicTermObject, :parent_calendar => @calendar, :term => "Spring"
  @calendar.terms << @term

  @exam_period = make ExamPeriodObject, :parent_term => @term
  @calendar.terms[1].exam_period = @exam_period

  @calendar.terms[1].exam_period.edit :exclude_saturday => false, :exclude_sunday => false, :navigate_to_page => false
end