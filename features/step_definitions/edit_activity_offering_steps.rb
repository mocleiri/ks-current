Given /^I am editing the information attributes for an activity offering$/ do
  @activity_offering.edit :max_enrollment => 50
end

When /^I edit an activity offering code/ do
  @orig_code = @activity_offering.code
  @activity_offering.edit :code => @activity_offering.code + @activity_offering.code
end

When /^I revert the change to the activity code/ do
  @activity_offering.edit :code => @orig_code, :edit_already_started => true
end

Then /^the activity offering code change is persisted/ do
  @course_offering.manage
  @activity_offering.edit
  on ActivityOfferingMaintenance do |page|
    page.activity_code.value.should ==  @activity_offering.code.to_s
  end
end

And /^I submit the AO changes$/ do
  @activity_offering.save

  #validate the success-growl is being shown
  on ManageCourseOfferings do |page|
    page.growl_text.should == "Activity Offering modified."
    page.growl_div.div(class: "jGrowl-close").click
  end
end

When /^I save the changes and remain on the Edit AO page$/ do
  @activity_offering.save_and_remain_on_page
end

Then /^the changes of Activity Offering attributes are persisted$/ do
  @course_offering.manage
  @activity_offering.edit
  on ActivityOfferingMaintenance do |page|
    page.total_maximum_enrollment.value.should == @activity_offering.max_enrollment.to_s
  end
end

Then /^the changes of information attributes (are|are not) persisted$/ do |are_are_not|
  @course_offering.manage
  @activity_offering.edit
  on ActivityOfferingMaintenance do |page|
    if are_are_not == "are"
      page.total_maximum_enrollment.value.should == @activity_offering.max_enrollment.to_s
    elsif are_are_not == "are not"
      page.total_maximum_enrollment.value.should_not == @activity_offering.max_enrollment.to_s
    end
  end
end

When /^I save the changes and jump to the (next|previous) AO$/  do |direction|
  expected_title = "#{@course_offering.course} - "
  on ActivityOfferingMaintenance do |page|
    if direction == "next"
      expected_title += "#{page.next_ao_text}"
      page.next_ao
    else
      expected_title += "#{page.prev_ao_text}"
      page.prev_ao
    end
    page.save_and_continue
    page.ao_header_text.should == expected_title
  end
end

When /^I jump to the (next|previous) AO without saving the changes$/ do |direction|
  expected_title = "#{@course_offering.course} - "
  on ActivityOfferingMaintenance do |page|
    if direction == "next"
      expected_title += page.next_ao_text
      page.next_ao
    else
      expected_title += page.prev_ao_text
      page.prev_ao
    end
    page.continue_without_saving
    page.ao_header_text.should == expected_title
    page.cancel
  end
end

When /^I jump to an arbitrary AO without saving the changes$/ do
  target = "Lecture C"
  on ActivityOfferingMaintenance do |page|
    page.jump_to_ao(target)
    page.continue_without_saving
  end
  expected_title = "#{@course_offering.course} - #{target}"
  on ActivityOfferingMaintenance do |page|
    page.ao_header_text.should == expected_title
  end
end

When /^I save the changes and jump to an arbitrary AO$/ do
  target = "Lecture C"
  on ActivityOfferingMaintenance do |page|
    page.jump_to_ao(target)
    page.save_and_continue
  end
  expected_title = "#{@course_offering.course} - #{target}"
  on ActivityOfferingMaintenance do |page|
    page.ao_header_text.should == expected_title
  end
end

When /^I cancel the AO changes$/ do
  on ActivityOfferingMaintenance do |page|
    page.cancel
  end
end

When /^I jump to an arbitrary AO but cancel the change$/ do
  on ActivityOfferingMaintenance do |page|
    target = "Lecture C"
    page.jump_to_ao(target)
    page.cancel_save
  end
  expected_title = "#{@course_offering.course} - #{@activity_offering.activity_type} #{@activity_offering.code}"
  on ActivityOfferingMaintenance do |page|
    page.ao_header_text.should == expected_title
    # Click page.cancel to avoid dirty data dialog box when we attempt to return to the page to validate
    # changes were not persisted
    page.cancel
  end

end
When /^I change Personnel attributes$/ do
  person = make Personnel, :id => "admin", :name => "admin, admin", :affiliation => "Instructor", :inst_effort => 30
  @activity_offering.edit :personnel_list => [person]
end

Then /^the changes of the Personnel attributes are persisted$/ do
  @course_offering.manage
  @activity_offering.edit
  personnel = @activity_offering.personnel_list[0]
  personnel.should_not == nil
  on ActivityOfferingMaintenance do |page|
    page.person_name(personnel.id).should == personnel.name
    page.person_affiliation(personnel.id).should == personnel.affiliation
    page.person_inst_effort(personnel.id).should == personnel.inst_effort.to_s
  end
end

Then /^the deleted Personnel line should not be present$/ do
  @course_offering.manage
  @activity_offering.edit
  on ActivityOfferingMaintenance do |page|
    page.target_person_row(@person.id).nil?.should be_true
  end
end

When /^I add Personnel attributes$/ do
  @activity_offering.edit
  person = make Personnel, :id => "S.DAVIDB", :name => "SMITH, DAVID", :affiliation => "Instructor", :inst_effort => 30
  @activity_offering.add_personnel person
end

When(/^I delete Personnel attributes$/) do
  @activity_offering.edit
  @person = make Personnel, :id => "O.JEFFREYF" #in reference data
  @activity_offering.delete_personnel @person
end

When /^I change Miscellaneous Activity Offering attributes$/ do
  @activity_offering.edit

  on ActivityOfferingMaintenance do |page|
    page.requires_evaluation.set?.should be_false
    page.honors_flag.set?.should be_false
    page.course_url.value.should == 'null'
  end

  @activity_offering.edit :edit_already_started => true,
                          :requires_evaluation => true,
                          :honors_course => true,
                          :course_url => "www.kuali.org"
end

Then /^the miscellaneous changes are persisted$/ do
  @course_offering.manage
  @activity_offering.edit
  on ActivityOfferingMaintenance do |page|
    page.requires_evaluation.set?.should ==  @activity_offering.requires_evaluation
    page.honors_flag.set?.should == @activity_offering.honors_course
    page.course_url.value.should == @activity_offering.course_url
  end
end

Given /^I manage a given Course Offering$/ do
  @course_offering = create CourseOffering, :create_by_copy => (make CourseOffering, :course=>"ENGL244")
  #@course_offering.manage
end

Given /^I edit an Activity Offering$/ do
  @activity_offering = make ActivityOffering, :parent_offering => @course_offering, :code => 'B'
end

Given /^I edit an Activity Offering that has available subterms$/ do
  @course_offering = create CourseOffering, :create_by_copy => (make CourseOffering, :course=>"ENGL222", :term=>"201208")
  @activity_offering =  make ActivityOffering, :code => "A", :parent_course_offering => @course_offering
  @course_offering.manage
  @course_offering.edit_ao :ao_code=> @activity_offering.code

  @subterm_list = Array.new(2)
  @subterm_list[0] = make AcademicTerm, :term_year => "2012", :start_date => "08/29/2012", :end_date => "10/21/2012",
                          :term_type=> "Half Fall 1", :parent_term=> "Fall Term", :subterm => true
  @subterm_list[1] = make AcademicTerm, :term_year => "2012", :start_date => "10/22/2012", :end_date => "12/11/2012",
                          :term_type=> "Half Fall 2", :parent_term=> "Fall Term", :subterm => true

end

Then /^I set a subterm for the activity offering$/ do
  @activity_offering.edit :edit_already_started => true, :subterm => @subterm_list[0].subterm_type
  @activity_offering.save
end

Then /^I update the subterm for the activity offering$/ do
  @activity_offering.edit :subterm => @subterm_list[1].subterm_type
  @activity_offering.save
end

Then /^I remove the subterm for the activity offering$/ do
  @activity_offering.edit :subterm => "None"
  @activity_offering.save
end

Then /^the AO subterm change is successful$/ do
  @course_offering.manage
  on ManageCourseOfferings do |page|
    page.has_subterm_icon("A").should == true
    page.view_activity_offering("A")
  end

  on ActivityOfferingInquiry do |page|
    page.subterm.should == @activity_offering.subterm
    page.close
  end

  @activity_offering.edit
  on ActivityOfferingMaintenance do |page|
      page.subterm.should == @activity_offering.subterm
      page.cancel
  end
end

Then /^the AO subterm is successfully removed$/ do
  @course_offering.manage
  on ManageCourseOfferings do |page|
    page.has_subterm_icon("A").should == false
    page.view_activity_offering("A")
  end

  on ActivityOfferingInquiry do |page|
    page.subterm.should == "None"
    page.close
  end

  @activity_offering.edit
  on ActivityOfferingMaintenance do |page|
    page.subterm.should == "None"
    page.cancel
  end
end

Then /^I copy the activity offering$/ do
  @activity_offering_copy = create ActivityOffering, :create_by_copy => true,
                                                :code => @activity_offering.code,
                                                :parent_course_offering => @activity_offering.parent_course_offering
end

Then /^I copy the parent course offering$/ do
  @course_offering_copy = create CourseOffering, :create_by_copy => @course_offering
end

Then /^the AO subterm indicator is successfully copied$/ do
  @course_offering.manage
  on ManageCourseOfferings do |page|
    page.has_subterm_icon(@activity_offering_copy.code).should == true
    page.view_activity_offering(@activity_offering_copy.code)
  end

  on ActivityOfferingInquiry do |page|
    page.subterm.should == @activity_offering.subterm
    page.close
  end

  @activity_offering_copy.edit
  on ActivityOfferingMaintenance do |page|
    page.subterm.should == @activity_offering.subterm
    page.cancel
  end
end

Then /^the AO subterm indicator is successfully copied with the parent CO$/ do
  @course_offering_copy.manage
  on ManageCourseOfferings do |page|
    page.has_subterm_icon(@activity_offering.code).should == true
    page.view_activity_offering(@activity_offering.code)
  end

  on ActivityOfferingInquiry do |page|
    page.subterm.should == @activity_offering.subterm
    page.close
  end

  @activity_offering.edit
  on ActivityOfferingMaintenance do |page|
    page.subterm.should == @activity_offering.subterm
    page.cancel
  end
end
