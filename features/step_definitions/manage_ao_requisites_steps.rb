#Student Eligibility & Prerequisite
When /^I navigate to the agendas page and open the Student Eligibility & Prerequisite section$/ do
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite"
  @prereq = make AOPreparationPrerequisiteRule
  @prereq.navigate_to_ao_requisites
end

When /^I navigate to the agendas page for a CO that I cannot edit and open the Student Eligibility & Prerequisite section$/ do
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite"
  @prereq = make AOPreparationPrerequisiteRule, :term => "201208", :course => "PHYS420"
  @prereq.navigate_to_ao_requisites( true)
end

When /^I copy and edit the Course Offering rule to the Student Eligibility & Prerequisite section$/ do
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite"
  @prereq = make AOPreparationPrerequisiteRule
  @prereq.sepr_copy_edit_co_rule
end

When /^I suppress the copied and edited Course Offering rule that was committed in the Student Eligibility & Prerequisite section$/ do
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite", :activity => "E"
  @prereq = make AOPreparationPrerequisiteRule, :activity => "E"
  @prereq.sepr_suppress_committed_copied_edited_co_rule
end

When /^I revert the rule that replaced the CO rule in the Student Eligibility & Prerequisite section$/ do
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite"
  @prereq = make AOPreparationPrerequisiteRule
  @prereq.sepr_revert_replaced_co_rule
end

When /^I commit after reverting the rule that replaced the CO rule in the Student Eligibility & Prerequisite section$/ do
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite", :activity => "B"
  @prereq = make AOPreparationPrerequisiteRule, :activity => "B"
  @prereq.sepr_revert_replaced_co_rule
  @prereq.commit_changes
end

When /^I add a rule to the Student Eligibility & Prerequisite section$/ do
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite"
  @prereq = make AOPreparationPrerequisiteRule, :term => "201301", :course => "ENGL313"
  @prereq.sepr_add_ao_rule
end

When /^I replace the CO rule in the Student Eligibility & Prerequisite section$/ do
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite"
  @prereq = make AOPreparationPrerequisiteRule
  @prereq.sepr_replace_co_rule( true)
end

When /^I edit and update the rule that replaced the CO rule in the Student Eligibility & Prerequisite section$/ do
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite", :activity => "B"
  @prereq = make AOPreparationPrerequisiteRule, :activity => "B"
  @prereq.sepr_edit_update_replaced_co_rule
end

When /^I edit the rule that replaced the CO rule in the Student Eligibility & Prerequisite section$/ do
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite", :activity => "D"
  @prereq = make AOPreparationPrerequisiteRule, :activity => "D"
  @prereq.sepr_edit_replaced_co_rule( true)
end

When /^I commit the rule that replaced the CO rule in the Student Eligibility & Prerequisite section$/ do
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite", :activity => "C"
  @prereq = make AOPreparationPrerequisiteRule, :activity => "C"
  @prereq.sepr_replace_co_rule
  @prereq.commit_changes
end

When /^I suppress the copied rule in the Student Eligibility & Prerequisite section$/ do
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite"
  @prereq = make AOPreparationPrerequisiteRule
  @prereq.sepr_suppress_copied_co_rule
end

When /^I view the catalog and course offering rule for the Student Eligibility & Prerequisite section$/ do
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite"
  @prereq = make AOPreparationPrerequisiteRule
  @prereq.sepr_view_catalog_co_rule
end

When /^I compare the replaced rule with the CO and CLU rules in the Student Eligibility & Prerequisite section$/ do
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite"
  @prereq = make AOPreparationPrerequisiteRule
  @prereq.sepr_compare_ao_to_clu_co_rule
end

When /^I compare the added rule with the CO and CLU rules in the Student Eligibility & Prerequisite section$/ do
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite"
  @prereq = make AOPreparationPrerequisiteRule, :term => "201301", :course => "ENGL313"
  @prereq.sepr_compare_new_ao_to_clu_co_rule
end

When /^I edit a course offering requisite at the AO level by adding a new text statement$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "PHYS272")
  on ManageCourseOfferings do |page|
    page.codes_list.each do |code|
      aos = make ActivityOffering, :code => code, :parent_course_offering => @course_offering
      aos.edit :send_to_scheduler => true, :defer_save => false
    end
  end
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite", :activity => "B"
  @prereq = make AOPreparationPrerequisiteRule, :activity => "B", :term => @course_offering.term, :course => @course_offering.course
  @prereq.navigate_to_ao_requisites( true)
  on ActivityOfferingRequisites do |page|
    if page.prereq_copy_edit_link.exists?
      page.loading.wait_while_present
      page.prereq_copy_edit
      @prereq.rp_sepr_text_rule( "add", "B", "Changed the SE & Prerequisite on AO B only")
      @prereq.commit_changes
    end
  end
end

When /^I suppress a course offering rule for an activity in a course$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "PHYS272")
  on ManageCourseOfferings do |page|
    page.codes_list.each do |code|
      aos = make ActivityOffering, :code => code, :parent_course_offering => @course_offering
      aos.edit :send_to_scheduler => true, :defer_save => false
    end
  end
  @activityOR = make AORequisitesData, :section => "Student Eligibility & Prerequisite", :activity => "B"
  @prereq = make AOPreparationPrerequisiteRule, :activity => "B", :term => @course_offering.term,
                 :course => @course_offering.course
  @prereq.sepr_suppress_co_rule( true)
  @prereq.commit_changes
end

Then /^there should be no rule in the Student Eligibility & Prerequisite section$/ do
  @prereq.open_agenda_section
  on ActivityOfferingRequisites do |page|
    page.loading.wait_while_present
    page.agenda_management_section.text.should match /.*Student Eligibility & Prerequisite.*Rule.*\.\nCorequisite.*/m
  end
end

Then /^the created rule should be shown in the Student Eligibility & Prerequisite section$/ do
  @prereq.open_agenda_section
  on ActivityOfferingRequisites do |page|
    page.loading.wait_while_present
    @prereq.show_all_courses( "agenda")
    page.agenda_management_section.text.should match @prereq.test_text("agenda", "ENGL101,ENGL478,HIST416,BSCI202,BSCI361,HIST110,Text to copy,free form text input value")
  end
end

Then /^the edited rule should be shown in the Student Eligibility & Prerequisite section$/ do
  @prereq.open_agenda_section
  on ActivityOfferingRequisites do |page|
    page.loading.wait_while_present
    @prereq.show_all_courses( "agenda")
    page.agenda_management_section.text.should match @prereq.test_text("agenda", "ENGL313,Text added while editing,Text to copy")
  end
end

Then /^both rules for the Student Eligibility & Prerequisite section should be the same$/ do
  @prereq.open_agenda_section
  on ActivityOfferingRequisites do |page|
    page.loading.wait_while_present
    page.view_tree.text.should match @prereq.test_compare_text("at least one in literature,ARHU-English required")
  end
end

Then /^the AO rule should differ from the CO and CLU rules in the Student Eligibility & Prerequisite section$/ do
  @prereq.open_agenda_section
  on ActivityOfferingRequisites do |page|
    if !page.compare_section.present?
      page.loading.wait_while_present
      page.prereq_compare
    end
    page.loading.wait_while_present
    page.compare_tree.text.should_not match @prereq.test_ao_compare_text("at least one in literature,ARHU-English required")
  end
end

Then /^a (?:error|warning) in the Student Eligibility & Prerequisite section is displayed stating "([^"]*)"$/ do |exp_msg|
  @prereq.open_agenda_section
  on ActivityOfferingRequisites do |page|
    page.loading.wait_while_present
    page.prereq_message_section_warning.text.should match /.*#{exp_msg}.*/
  end
end

Then /^a (?:error|info) in the Student Eligibility & Prerequisite section is displayed stating "([^"]*)"$/ do |exp_msg|
  @prereq.open_agenda_section
  on ActivityOfferingRequisites do |page|
    page.loading.wait_while_present
    page.prereq_message_section_info.text.should match /.*#{exp_msg}.*/
  end
end

Then /^no (?:error|warning) in the Student Eligibility & Prerequisite section is displayed stating "([^"]*)"$/ do |exp_msg|
  @prereq.open_agenda_section
  on ActivityOfferingRequisites do |page|
    page.loading.wait_while_present
    page.prereq_message_section_warning.text.should_not match /.*#{exp_msg}.*/
  end
end

Then /^I should only be allowed to view the CLU and CO rule in the Student Eligibility & Prerequisite section$/ do
  on ActivityOfferingRequisites do |page|
    page.prereq_view_link.present?.should be_true
    page.prereq_copy_edit_link.present?.should be_false
    page.prereq_replace_link.present?.should be_false
    page.prereq_suppress_link.present?.should be_false
  end
end

#Antirequisite
When /^I add a text rule to the Antirequisite section$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "PHYS272")
  on ManageCourseOfferings do |page|
    page.codes_list.each do |code|
      aos = make ActivityOffering, :code => code, :parent_course_offering => @course_offering
      aos.edit :send_to_scheduler => true, :defer_save => false
    end
  end
  @activityOR = make AORequisitesData, :section => "Antirequisite"
  @antireq = make AOAntirequisiteRule, :term => @course_offering.term, :course => @course_offering.course
  @antireq.navigate_to_ao_requisites( true)
  on ActivityOfferingRequisites do |page|
    if page.antireq_add_link.exists?
      page.loading.wait_while_present
      page.antireq_add
      @antireq.ar_text_rule( "add", "", "Add Antirequisite specific to AO A")
      @antireq.commit_changes
    end
  end
end

#Corequisite
When /^I suppress a course offering rule for a specific activity in a course$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "PHYS272")
  on ManageCourseOfferings do |page|
    page.codes_list.each do |code|
      aos = make ActivityOffering, :code => code, :parent_course_offering => @course_offering
      aos.edit :send_to_scheduler => true, :defer_save => false
    end
  end
  @activityOR = make AORequisitesData, :section => "Corequisite"
  @coreq = make AOCorequisiteRule, :term => @course_offering.term, :course => @course_offering.course
  @coreq.cr_suppress_co_rule( true)
  @coreq.commit_changes
end

When /^I edit a course offering rule at the AO level by adding a new text statement$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "PHYS272")
  on ManageCourseOfferings do |page|
    page.codes_list.each do |code|
      aos = make ActivityOffering, :code => code, :parent_course_offering => @course_offering
      aos.edit :send_to_scheduler => true, :defer_save => false
    end
  end
  @activityOR = make AORequisitesData, :section => "Corequisite", :activity => "B"
  @coreq = make AOCorequisiteRule, :activity => "B", :term => @course_offering.term, :course => @course_offering.course
  @coreq.navigate_to_ao_requisites( true)
  on ActivityOfferingRequisites do |page|
    if page.coreq_copy_edit_link.exists?
      page.loading.wait_while_present
      page.coreq_copy_edit
      @coreq.cr_text_rule( "add", "", "Changed the Corequisite on AO B only")
      @coreq.commit_changes
    end
  end
end

###General steps###
Given /^I have made changes to multiple AO Requisites for the same course offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "CHEM277")
  @activityOR = make AORequisitesData
  @activityOR.make_changes_to_multiple_ao_reqs @course_offering.course
end

When /^I copy a course offering from an existing offering that had changes made to its activity offerings$/ do
  @copyCO = create CourseOffering, :term => "201301", :course => "CHEM277", :create_from_existing => @course_offering
end

Then /^the copied course offering should have the same AO Requisites as the original$/ do
  on ManageCourseOfferings do |page|
    page.ao_requisites("A")
  end
  on ActivityOfferingRequisites do |page|
    page.loading.wait_while_present
    @activityOR.show_all_courses( "agenda")
    page.eligibility_prereq_section.text.should match @activityOR.test_text("agenda", "CHEM241,CHEM242,CHEM247,Must have been admitted to the (Biochemistry, Chemistry) program,ENGL101")
    page.prereq_message_section_info.text.should match /Activity Offering Rule differs from Course Offering Rule/
    page.coreq_message_section_warning.text.should match /Rule statements deleted/
    page.submit
  end
  on ManageCourseOfferings do |page|
    page.ao_requisites("D")
  end
  on ActivityOfferingRequisites do |page|
    page.loading.wait_while_present
    @activityOR.show_all_courses( "agenda")
    page.corequisite_section.text.should match @activityOR.test_text("agenda", "concurrently enrolled in a minimum,BSCI202,BSCI361,HIST110")
    page.coreq_message_section_info.text.should match /Activity Offering Rule differs from Course Offering Rule/
    page.antirequisite_section.text.should match @activityOR.test_text("agenda", "free form text input value")
  end
end

Then /^both Activity Offerings should have the AR icon present$/ do
  on ManageCourseOfferings do |page|
    page.loading.wait_while_present
    page.has_ar_icon("A")
    page.has_ar_icon("D")
  end
end

Then /^the AO icon should be shown for the edited Activity Offering$/ do
  on ManageCourseOfferings do |page|
    page.loading.wait_while_present
    page.has_ar_icon( @activityOR.activity).should == true
  end
end

Then /^the AO icon should not be shown for the edited Activity Offering$/ do
  on ManageCourseOfferings do |page|
    page.loading.wait_while_present
    page.has_ar_icon( @activityOR.activity).should == false
  end
end

Then /^I should be able to use all the function buttons on the Edit Rule tab$/ do
  on ManageAORequisites do |page|
    @activityOR.move_around( "A", "out")
    page.edit_tree_section.span(:text => /.*A\..*/).id.should match @activityOR.test_node_level( "primary")

    @activityOR.move_around( "B", "up")
    page.edit_tree_section.text.should match /.*B\..+A\..*/m

    @activityOR.move_around( "C", "down")
    page.edit_tree_section.text.should match /.*E\..+C\..*/m

    @activityOR.move_around( "B", "up in")
    page.edit_tree_section.text.should match /.*B\..+C\..*/m

    @activityOR.delete_statement( "C")
    page.edit_tree_section.text.should_not match /.*C\..*/

    @activityOR.copy_cut_paste( "B", "D", "cut")
    page.edit_tree_section.text.should match /.*D\..+B\..*/m

    @activityOR.copy_cut_paste( "E", "B", "copy")
    page.edit_tree_section.text.should match /.*E\..+B\..+F\..*/m

    @activityOR.add_new_node( "add", "F")
    page.rule_dropdown.when_present.select /Free Form Text/
    page.free_text_field.when_present.set "new statement can be added"
    page.preview_btn
    page.edit_tree_section.text.should match /.*F\..+G\..*/m

    @activityOR.add_new_node( "group", "A")
    page.rule_dropdown.when_present.select /Free Form Text/
    page.free_text_field.when_present.set "new group can be created"
    page.preview_btn
    page.edit_tree_section.span(:text => /.*H\..*/).id.should match @activityOR.test_node_level( "secondary")

    @activityOR.edit_existing_node("D", "text", "existing statement can be edited")
    page.edit_tree_section.text.should match /existing statement can be edited/

    page.update_rule_btn
  end
end

Then /^I should be able to use the functionality of the Edit Rule Logic tab$/ do
  on ManageAORequisites do |page|
    @activityOR.switch_tabs
    page.edit_loading.wait_while_present
    page.logic_text.set "A OR C OR (E AND B) OR D"
    page.preview_btn
    page.preview_tree_section.text.should match /Must meet 1 of the following.+Must meet all of the following/m

    @activityOR.switch_tabs
    page.edit_loading.wait_while_present
    page.edit_tree_section.text.should match /.*A\..+C\..+E\..+B\..+D\..*/m
    page.edit_tree_section.span(:text => /.*B\..*/).id.should match @activityOR.test_node_level( "secondary")

    page.update_rule_btn
  end
end
