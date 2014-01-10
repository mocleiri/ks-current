Then /^I am using the schedule of classes page$/ do
  @schedule_of_classes_landing_page = go_to_display_schedule_of_classes
end

Then /^the nearest valid future Term is chosen in the Term select list$/ do
  #
  #  The term selector should default to the "nearest valid future term" where "future" is derived from comparing the
  #  start date of the term to "now". So, this test will break when "now" is past the start date of Winter 2015. Also,
  #  the test susceptible to changes in the term data. However, it didn't seem quite worth while to try and reproduce
  #  the algorithm to prevent the breakage.
  #
  #  For now just make sure a term was selected.
  @schedule_of_classes_landing_page.term.value.should match /kuali\.atp\.20/
end

When /^I search for course offerings by course by entering a subject code$/ do
  @schedule_of_classes = make ScheduleOfClasses, :course_search_parm => "CHEM", :exp_course_list => ["CHEM135","CHEM425"]
  @schedule_of_classes.display
end

When /^I search for a course offering that has activity offerings assigned to subterms by course code/ do
  @schedule_of_classes = make ScheduleOfClasses, :term => "Fall 2012", :course_search_parm => "CHEM105", :exp_course_list => ["CHEM105"]
  @schedule_of_classes.display
end

When /^I search for course offerings by course by entering a subject code: (.*)$/ do |subject_code|
  @schedule_of_classes = make ScheduleOfClasses, :course_search_parm => subject_code
  @schedule_of_classes.display
end

Then /^a list of course offerings with that subject code is displayed$/ do
  @schedule_of_classes.check_results_for_subject_code_match(@schedule_of_classes.course_search_parm)
end

Then /^the course offering details for a particular offering can be shown$/ do
  @schedule_of_classes.expand_course_details
end

And /^the subterm icon appears with the subterm information$/ do
  #icon_title_text = "This activity is in Half Fall 1 - 08/28/2012 - 10/20/2012"  # local env
  icon_title_text = "This activity is in Half Fall 1 - 08/29/2012 - 10/21/2012"   # env2
  #TODO: look up text above (term name & dates) from CO & ACal pages
  on DisplayScheduleOfClasses do |page|
    if !page.details_table.exists?
      raise "activities table not found"
    else
      page.details_table.rows[1..-1].each do |row|
        # check only rows with data in them
        if row.cells[DisplayScheduleOfClasses::AO_CODE_COLUMN].text =~ /[A-B]/
          row.cells[DisplayScheduleOfClasses::ICON_COLUMN].image.attribute_value("src").should match /subterm_icon\.png/
          row.cells[DisplayScheduleOfClasses::ICON_COLUMN].image.title.should == icon_title_text
        end
      end
    end
  end
end

Then /^the course offering details for all offerings can be shown$/ do
  @schedule_of_classes.expand_all_course_details
end

When /^I search for course offerings by course by entering a course offering code$/ do
  @schedule_of_classes = make ScheduleOfClasses, :course_search_parm => "ENGL206", :exp_course_list => ["ENGL206"], :exp_cluster_list => ["CL 101","CL Leftovers"], :exp_reg_group_list => ["1001","1002","1003","1004"]
  @schedule_of_classes.display
end

Then /^a list of course offerings with that course offering code is displayed$/ do
  @schedule_of_classes.check_expected_results_by_course
end

When /^I search for course offerings by instructor$/ do
  @schedule_of_classes = make ScheduleOfClasses, :type_of_search => "Instructor", :instructor_principal_name => "B.JOHND", :instructor_long_name => "BRITT, JOHN", :exp_course_list => ["BSCI399","CHEM241","ENGL390","HIST708"]
  @schedule_of_classes.display
end

Then /^a list of course offerings with activity offerings with that instructor is displayed$/ do
  @schedule_of_classes.check_results_for_instructor
end

When /^I search for course offerings by department$/ do
  @schedule_of_classes = make ScheduleOfClasses, :type_of_search => "Department", :department_long_name => "ARHU-English", :exp_course_list => ["ENGL206","ENGL377","ENGL899"]
  @schedule_of_classes.display
end

Then /^a list of course offerings for that department is displayed$/ do
   @schedule_of_classes.check_expected_results_by_course
end

When /^I search for course offerings by title and department by entering a keyword$/ do
  @schedule_of_classes = make ScheduleOfClasses, :type_of_search => "Title or Description", :keyword => "computer", :exp_course_list => ["PHYS485"]
  @schedule_of_classes.display
end

Then /^a list of course offerings with that keyword is displayed$/ do
  @schedule_of_classes.check_expected_results_by_course
end

When /^I add an Activity Offering in draft status to an existing Course Offering$/ do
  @course_offering = make CourseOffering, :term => "201208", :course=>"ENGL222"
  @course_offering.manage_and_init
  @new_ao = @course_offering.create_ao(make ActivityOffering, :format => "Lecture Only")
end

And /^I search for the Course Offering in the schedule of classes$/ do
  @schedule_of_classes = make ScheduleOfClasses, :term => "Fall 2012", :course_search_parm => @course_offering.course, :exp_course_list => ["ENGL222"]
  @schedule_of_classes.display
end

Then /^the added Activity Offering is not displayed in the expanded listing rendered by (AO Cluster|Registration Group)$/  do |rendering|
  rendering_param = (rendering=="AO Cluster") ? DisplayScheduleOfClasses::AO_CLUSTER_RENDERING : DisplayScheduleOfClasses::REG_GROUP_RENDERING
  @schedule_of_classes.choose_rendering rendering_param
  @schedule_of_classes.expand_course_details
  on DisplayScheduleOfClasses do |page|
    page.get_ao_list(rendering_param).should_not include @new_ao.code
  end
end

Then /^the course offering details displays a listing of AO clusters$/ do
  @schedule_of_classes.choose_rendering DisplayScheduleOfClasses::AO_CLUSTER_RENDERING
  @schedule_of_classes.expand_course_details
  on DisplayScheduleOfClasses do |page|
    page.get_cluster_headers.should == @schedule_of_classes.exp_cluster_list
  end
end

Then /^the course offering details displays a listing of registration groups$/ do
  @schedule_of_classes.choose_rendering DisplayScheduleOfClasses::REG_GROUP_RENDERING
  @schedule_of_classes.expand_course_details
  on DisplayScheduleOfClasses do |page|
    page.get_reg_group_list.should == @schedule_of_classes.exp_reg_group_list
  end
end

When /^I search for course offerings by course to view the course offering requisites$/ do
  @schedule_of_classes = make ScheduleOfClasses, :course_search_parm => "ENGL310", :exp_course_list => ["ENGL310"],
                              :term => "Fall 2012"
  @schedule_of_classes.display
end

Then /^the course offering requisites should be displayed alongside the course description$/ do
  on DisplayScheduleOfClasses do |page|
    page.get_requisites_message_text.should match /Student Eligibility & Prerequisite.*Two lower-level English courses.*at least one in literature.*permission of ARHU-English/m
  end
end

When /^I have made no chages to the CO Requisites of a course$/ do
  @course_offering = make CourseOffering, :term => "201208", :course => "PHYS272"
end

When /^I search for course offerings by course$/ do
  @schedule_of_classes = make ScheduleOfClasses, :course_search_parm => "PHYS272", :exp_course_list => [@course_offering.course],
                              :term => "Fall 2012"
  @schedule_of_classes.display
end

When /^I select a course that has existing (?:course offering|activity offering level) requisites$/ do
  @schedule_of_classes.expand_course_details
end

When /^I select a course that has existing (?:course offering|activity offering level) requisites in the registration group$/ do
  @schedule_of_classes.display
  @schedule_of_classes.choose_rendering DisplayScheduleOfClasses::REG_GROUP_RENDERING
  @schedule_of_classes.expand_course_details
end

Then /^the course offering requisites should be displayed with the course data$/ do
  on DisplayScheduleOfClasses do |page|
    page.get_requisites_message_text.should match /Prerequisite.*1 course from PHYS161 or PHYS171.*BSCI399.*Corequisite.*ENGL101 and HIST106/m
  end
end

Then /^the newly added course offering requisite should be displayed with the course data$/ do
  on DisplayScheduleOfClasses do |page|
    page.get_requisites_message_text.should match /Recommended Preparation.*Added Recommended Prep on CO level/m
  end
end

Then /^the added course offering requisite should be displayed with the course data$/ do
  on DisplayScheduleOfClasses do |page|
    page.get_requisites_message_text.should match /Antirequisite.*Added Antirequisite on CO level/m
  end
end

Then /^the added course offering requisite should not be displayed on Reg Group level$/ do
  on DisplayScheduleOfClasses do |page|
    page.details_table.row(text: /\bA\b/).text.should_not match /Antirequisite.*Added Antirequisite on CO level/m
    page.details_table.row(text: /\bB\b/).text.should_not match /Antirequisite.*Added Antirequisite on CO level/m
  end
end

Then /^any un-suppressed course offering requisites should be visible with the course data$/ do
  on DisplayScheduleOfClasses do |page|
    page.get_requisites_message_text.should match /Prerequisite.*1 course from PHYS161 or PHYS171.*And BSCI399/m
  end
end

Then /^the suppressed requisite should not be visible for the changed activity$/ do
  on DisplayScheduleOfClasses do |page|
    page.get_requisites_message_text.should_not match /Corequisite: ENGL101 and HIST106/m
    page.details_table.rows[(page.get_rule_text_for_ao("A")+1)].text.should_not match /Corequisite: ENGL101 and HIST106/m
  end
end

Then /^the suppressed rule should not be visible with the course data$/ do
  on DisplayScheduleOfClasses do |page|
    page.get_requisites_message_text.should_not match /Prerequisite.*1 course from PHYS161 or PHYS171.*And BSCI399/m
  end
end

Then /^the suppressed rule should not be visible for the changed activity$/ do
  on DisplayScheduleOfClasses do |page|
    page.details_table.rows[(page.get_rule_text_for_ao("B")+1)].text.should_not match /Prerequisite.*1 course from PHYS161 or PHYS171.*And BSCI399/m
  end
end

Then /^the suppressed rule should be visible for any unchanged activity$/ do
  on DisplayScheduleOfClasses do |page|
    page.details_table.rows[(page.get_rule_text_for_ao("A")+1)].text.should match /Prerequisite.*1 course from PHYS161 or PHYS171.*And BSCI399/m
  end
end

Then /^the suppressed rule should be visible for any unchanged activity that shares a Reg Group with the changed activity$/ do
  on DisplayScheduleOfClasses do |page|
    page.details_table.rows[(page.get_rule_text_for_ao("A")+1)].text.should match /Prerequisite.*1 course from PHYS161 or PHYS171.*And BSCI399/m
    page.details_table.rows[page.get_rule_text_for_ao("B")].text.should_not match /Prerequisite.*1 course from PHYS161 or PHYS171.*And BSCI399/m
  end
end

Then /^any un-suppressed course offering rules should be visible with the course data$/ do
  on DisplayScheduleOfClasses do |page|
    page.get_requisites_message_text.should match /Corequisite: ENGL101 and HIST106/m
  end
end

Then /^the activity offering requisites should be displayed with the correct activity$/ do
  on DisplayScheduleOfClasses do |page|
    if !page.details_table.exists?
      raise "activities table not found"
    else
      page.details_table.rows[(page.get_rule_text_for_ao("A")+1)].text.should match /Antirequisite: Add Antirequisite specific to AO A/m
    end
  end
end

Then /^the edited course offering requisite should be displayed with the affected activity offering$/ do
  on DisplayScheduleOfClasses do |page|
    if !page.details_table.exists?
      raise "activities table not found"
    else
      page.details_table.rows[(page.get_rule_text_for_ao("B")+1)].text.should match /Prerequisite.*1 course from PHYS161 or PHYS171.*And BSCI399.*And Changed the SE & Prerequisite on AO B only/m
    end
  end
end

Then /^the unedited course offering requisite should be displayed with any un-affected activity offerings$/ do
  on DisplayScheduleOfClasses do |page|
    if !page.details_table.exists?
      raise "activities table not found"
    else
      page.details_table.rows[(page.get_rule_text_for_ao("A")+1)].text.should match /Prerequisite.*1 course from PHYS161 or PHYS171.*And BSCI399/m
      page.details_table.rows[(page.get_rule_text_for_ao("A")+1)].text.should_not match /And Changed the SE & Prerequisite on AO B only/m
    end
  end
end

Then /^the suppressed requisite should be visible for any unchanged activity$/ do
  on DisplayScheduleOfClasses do |page|
    if !page.details_table.exists?
      raise "activities table not found"
    else
      page.details_table.rows[page.get_rule_text_for_ao("B")].text.should match /Corequisite: ENGL101 and HIST106/m
    end
  end
end

Then /^the edited course offering rule should be displayed with the affected activity offering$/ do
  on DisplayScheduleOfClasses do |page|
    if !page.details_table.exists?
      raise "activities table not found"
    else
      page.details_table.rows[(page.get_rule_text_for_ao("B")+1)].text.should match /Changed the Corequisite on AO B only/m
    end
  end
end

Then /^the unedited course offering rule should be displayed with any un-affected activity offerings$/ do
  on DisplayScheduleOfClasses do |page|
    if !page.details_table.exists?
      raise "activities table not found"
    else
      ao_code_row = page.details_table.row(text: /\bA\b/)
      index = ao_code_row.span(text: /\bA\b/).id[/\d+_control/].to_i + 1
      page.details_table.rows[(page.get_rule_text_for_ao("A")+1)].text.should match /Corequisite: ENGL101 and HIST106/m
      page.details_table.rows[(page.get_rule_text_for_ao("A")+1)].text.should_not match /Changed the Corequisite on AO B only/m
    end
  end
end

Then /^the edited course offering rule should be displayed at the activity level on Reg Groups that contain the affected activity$/ do
  on DisplayScheduleOfClasses do |page|
    if !page.details_table.exists?
      raise "activities table not found"
    else
      page.details_table.rows[(page.get_rule_text_for_ao("B")+1)].text.should match /Changed the Corequisite on AO B only/m
    end
  end
end

Then /^the unedited course offering rule should be displayed at the Reg Group level on Reg Groups that do not contain the affected activity$/ do
  on DisplayScheduleOfClasses do |page|
    if !page.details_table.exists?
      raise "activities table not found"
    else
      page.details_table.rows[(page.get_rule_text_for_ao("A")+1)].text.should_not match /Changed the Corequisite on AO B only/m
    end
  end
end

When /^I loaded the list of Schedule of Classes for term "(.*?)" and Course "(.*?)"$/ do |arg1, arg2|
  @schedule_of_classes = make ScheduleOfClasses, :course_search_parm => arg2,
                                                 :exp_course_list => ["CHEM131S"],
                                                 :term => arg1
  @schedule_of_classes.display
end

Then /^the activity A of the course offering "(.*?)" has a colocated icon$/ do |arg1|
  @schedule_of_classes.display
  @schedule_of_classes.expand_course_details
  on DisplayScheduleOfClasses do |page|
    if !page.details_table.exists?
      raise "activities table not found"
    else
      page.details_table.rows[1].cells[DisplayScheduleOfClasses::AO_CODE_COLUMN].image(src: /colocate_icon/).present?.should be_true
      before_popup_style = @browser.divs(:class=>"jquerybubblepopup jquerybubblepopup-black" )[0].style
      (before_popup_style == "").should == true
      page.details_table.rows[1].cells[DisplayScheduleOfClasses::AO_CODE_COLUMN].image(src: /colocate_icon/).hover
      popup_style = @browser.divs(:class=>"jquerybubblepopup jquerybubblepopup-black" )[0].style
      (popup_style.include? "display: block").should == true
    end
  end
end

And /^the activity A of the course offering "(.*?)" has tooltip text "(.*?)"$/ do |arg1, arg2|
  @schedule_of_classes.display
  @schedule_of_classes.expand_course_details
  on DisplayScheduleOfClasses do |page|
    if !page.details_table.exists?
      raise "activities table not found"
    else
      colocated_tooltip_text = page.details_table.rows[1].cells[DisplayScheduleOfClasses::AO_CODE_COLUMN].image(src: /colocate_icon/).alt.upcase
      colocated_tooltip_text.should == arg2.upcase
    end
  end
end


When /^I loaded the Schedule of Classes for term "(.*?)" and Course "(.*?)"$/ do |arg1, arg2|
  @schedule_of_classes = make ScheduleOfClasses, :course_search_parm => arg2,
                              :exp_course_list => ["ENGL250"],
                              :term => arg1
  @schedule_of_classes.display
end

Then /^the course offering "(.*?)" has cross listed icon$/ do |arg1|
  @schedule_of_classes.display
  on DisplayScheduleOfClasses do |page|
    page.target_course_row(arg1)[DisplayScheduleOfClasses::COURSE_CODE_COLUMN].image(src: /cross-listed/).present?.should be_true
    before_popup_style = @browser.divs(:class=>"jquerybubblepopup jquerybubblepopup-black" )[0].style
    (before_popup_style == "").should == true
    page.target_course_row(arg1)[DisplayScheduleOfClasses::COURSE_CODE_COLUMN].image(src: /cross-listed/).hover
    popup_style = @browser.divs(:class=>"jquerybubblepopup jquerybubblepopup-black" )[0].style
    (popup_style.include? "display: block").should == true
  end
end

And /^the course offering "(.*?)" has tooltip text "(.*?)"$/ do |arg1, arg2|
  @schedule_of_classes.display
  on DisplayScheduleOfClasses do |page|
    cross_listed_tooltip_text = page.target_course_row(arg1)[DisplayScheduleOfClasses::COURSE_CODE_COLUMN].image(src: /cross-listed/).alt.upcase
    cross_listed_tooltip_text.should == arg2.upcase
  end
end

Then /^the course offering "(.*?)" has Audit grading option icon and tooltip popped up$/ do |arg1|
  @schedule_of_classes.display
  on DisplayScheduleOfClasses do |page|
    page.target_course_row(arg1)[DisplayScheduleOfClasses::ADDITIONAL_INFO].image(src: /a/).present?.should be_true
    display_style = @browser.divs(:class=>"jquerybubblepopup jquerybubblepopup-black")[0].style
    (result = display_style == "").should == true

    page.target_course_row(arg1)[DisplayScheduleOfClasses::ADDITIONAL_INFO].image(src: /a/).hover
    display_style1 = @browser.divs(:class=>"jquerybubblepopup jquerybubblepopup-black")[0].style
    (display_style1.include? "display: block").should == true
  end
end
