Given /^It is "After" the first day of classes$/ do
  step "I am logged in as a Schedule Coordinator"
  @term = make AcademicTerm, :term_name=> Rollover::PUBLISHED_MILESTONES_SOC_TERM_NAME, :term_type=>AcademicTerm::WINTER_TERM_TYPE, :term_year => Rollover::PUBLISHED_MILESTONES_SOC_TERM_YEAR
  @term.edit

  @keydategroup = make KeyDateGroup, :key_date_group_type=> "Instructional", :term_type=> @term.term_type
  @keydate = make KeyDate, :parent_key_date_group => @keydategroup, :key_date_type => "First Day of Classes", :start_date => (Date.today - 1).strftime("%m/%d/%Y"), :end_date=>(Date.today - 1).strftime("%m/%d/%Y"), :term_index=> @tindex
  @keydategroup.key_dates = Array.new(1){@keydate}
  @keydategroup.create

end

Given /^It is "Before" the first day of classes and "Before" the first day to add classes/ do
  step "I am logged in as a Schedule Coordinator"
  @term = make AcademicTerm, :term_name=> Rollover::PUBLISHED_MILESTONES_SOC_TERM_NAME, :term_type=>AcademicTerm::WINTER_TERM_TYPE, :term_year => Rollover::PUBLISHED_MILESTONES_SOC_TERM_YEAR
  @term.edit

  @keydategroup = make KeyDateGroup, :key_date_group_type=> "Instructional", :term_type=> @term.term_type
  @keydate = make KeyDate, :parent_key_date_group => @keydategroup, :key_date_type => "First Day of Classes", :start_date => (Date.today + 5).strftime("%m/%d/%Y"), :end_date=>(Date.today + 5).strftime("%m/%d/%Y")
  @keydategroup.key_dates = Array.new(1){@keydate}
  @keydategroup.create

  @keydategroup = make KeyDateGroup,:key_date_group_type=> "Registration", :term_type=> @term.term_type
  @keydate2 = make KeyDate, :parent_key_date_group => @keydategroup, :key_date_type => "Registration Period 1", :start_date => (Date.today + 1).strftime("%m/%d/%Y"), :end_date=>(Date.today + 4).strftime("%m/%d/%Y"), :date_range => true
  @keydategroup.key_dates = Array.new(1){@keydate2}
  @keydategroup.create
  #step "I am logged in as a Department Schedule Coordinator"
end

Given /^It is "Before" the first day of classes and "After" the first day to add classes/ do
  step "I am logged in as a Schedule Coordinator"
  @term = make AcademicTerm, :term_name=> Rollover::PUBLISHED_MILESTONES_SOC_TERM_NAME, :term_type=>AcademicTerm::WINTER_TERM_TYPE, :term_year => Rollover::PUBLISHED_MILESTONES_SOC_TERM_YEAR
  @term.edit

  @keydategroup = make KeyDateGroup, :key_date_group_type=> "Instructional", :term_type=> @term.term_type
  @keydate = make KeyDate, :parent_key_date_group => @keydategroup, :key_date_type => "First Day of Classes", :start_date => (Date.today + 5).strftime("%m/%d/%Y"), :end_date=>(Date.today + 5).strftime("%m/%d/%Y")
  @keydategroup.key_dates = Array.new(1){@keydate}
  @keydategroup.create

  @keydategroup = make KeyDateGroup,:key_date_group_type=> "Registration", :term_type=> @term.term_type
  @keydate2 = make KeyDate, :parent_key_date_group => @keydategroup, :key_date_type => "Registration Period 1", :start_date => (Date.today - 4).strftime("%m/%d/%Y"), :end_date=>(Date.today +1).strftime("%m/%d/%Y")
  @keydategroup.key_dates = Array.new(1){@keydate2}
  @keydategroup.create
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
  @term = make AcademicTerm, :term => "Fall", :term_year => "2014"
  @exam_period = make ExamPeriod, :parent_term => @term
  @term.edit :defer_save => true
  @exam_period.edit :exclude_saturday => false, :exclude_sunday => false, :defer_save => true
  @term = make AcademicTerm, :parent_term => @term, :term => "Spring", :term_year => "2015"
  @exam_period = make ExamPeriod, :parent_term => @term
  @exam_period.edit :exclude_saturday => false, :exclude_sunday => false
end

When /^I verify the exam periods for the published test fall and spring terms$/ do
  @term = make AcademicTerm, :term => "Fall", :term_year => "2015"
  @exam_period = make ExamPeriod, :parent_term => @term, :term_type => @term.term_type, :calendar_year => @term.term_year
  @term.edit :defer_save => true
  @exam_period.edit :exclude_saturday => false, :exclude_sunday => false, :defer_save => true
  @term = make AcademicTerm, :term => "Spring", :term_year => "2016"
  @exam_period = make ExamPeriod, :parent_term => @term, :term_type => @term.term_type, :calendar_year => @term.term_year
  @exam_period.edit :exclude_saturday => false, :exclude_sunday => false
end