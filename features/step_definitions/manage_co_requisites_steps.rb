#############################
#Antirequisite
When /^I (?:|navigate|have navigated) to the Antirequisite section for course "(.*?)" in the future term$/ do |course|
  @courseOR = make CORequisitesData
  @antireq = make AntirequisiteRule, :term => "201301", :course => course
  @antireq.navigate_to_mco_requisites
end

When /^I add a new not completed course statement with course "([^\"]+)"$/ do |course|
  @antireq.ar_course_rule( "add", "", course)
end

When /^I want to edit the Antirequisite section$/ do
  @antireq.ar_edit_add( "edit")
end

When /^I want to add a new statement to the Antirequisite section$/ do
  @antireq.ar_edit_add( "add")
end

When /^I add a new course offering rule to a course$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "PHYS272")
  on ManageCourseOfferings do |page|
    page.codes_list.each do |code|
      aos = make ActivityOfferingObject, :code => code, :parent_course_offering => @course_offering
      aos.edit :send_to_scheduler => true
    end
  end
  @courseOR = make CORequisitesData
  @antireq = make AntirequisiteRule, :section => "Antirequisite", :term => @course_offering.term,
                  :course => @course_offering.course
  @antireq.navigate_to_mco_requisites( true)
  @antireq.ar_edit_add( "add")
  @antireq.ar_text_rule( "add", "", "Added Antirequisite on CO level")
  @antireq.commit_changes
end

#############################
#Corequisite
When /^I (?:|setup|edit|have setup) the Corequisite section for course "(.*?)" in the future term$/ do |course|
  @courseOR = make CORequisitesData
  @coreq = make CorequisiteRule, :term => "201301", :course => course
  @coreq.cr_data_setup
end

When /^I (?:|navigate|have navigated) to the Corequisite section for course "(.*?)" in the future term$/ do |course|
  @courseOR = make CORequisitesData
  @coreq = make CorequisiteRule, :term => "201301", :course => course
  @coreq.navigate_to_mco_requisites
end

When /^I add a concurrently enrolled course statement after node "(.)" with course "([^\"]+)"$/ do |node, course|
  @coreq.cr_course_rule( "add", node, course)
end

When /^I add a Corequisite text statement after node "(.)" with text "([^\"]+)"$/ do |node, text|
  @coreq.cr_text_rule( "add", node, text)
end

When /^I add a concurrently enrolled number of courses statement after node "(.)" with number "(\d+)" and courses "([^\"]+)" and course sets "([^\"]+)"$/ do |node, number, course, set|
  @coreq.cr_number_courses_rule( "add", node, number, course, set, "")
end

When /^I add a new Corequisite text statement with text "([^\"]+)"$/ do |text|
  @coreq.cr_text_rule( "add", "", text)
end

When /^I add a new concurrently enrolled courses statement with courses "([^\"]+)" and course sets "([^\"]+)"$/ do |course, set|
  @coreq.cr_all_courses_rule( "add", "", course, set, "")
end

When /^I group a Corequisite text statement with node "(.)" with text "([^\"]+)"$/ do |node, text|
  @coreq.cr_text_rule( "group", node, text)
end

When /^I want to edit the Corequisite section$/ do
  @coreq.cr_edit_add( "edit")
end

When /^I want to add a new statement to the Corequisite section$/ do
  @coreq.cr_edit_add( "add")
end

#############################
#Recommended Preparation AND Student Eligibility & Prerequisite
When /^I (?:|navigate|have navigated) to the Recommended Preparation section for course "(.*?)" in the future term$/ do |course|
  @courseOR = make CORequisitesData
  @prereq = make PreparationPrerequisiteRule, :section => "Recommended Preparation", :term => "201301", :course => course
  @prereq.navigate_to_mco_requisites
end

When /^I (?:|setup|edit|have setup) the Student Eligibility & Prerequisite section for course "(.*?)" in the historic term$/ do |course|
  @courseOR = make CORequisitesData
  @prereq = make PreparationPrerequisiteRule, :course => course
  @prereq.sepr_data_setup
end

When /^I (?:|setup|edit|have setup) the Student Eligibility & Prerequisite section for course "(.*?)" in the future term$/ do |course|
  @courseOR = make CORequisitesData
  @prereq = make PreparationPrerequisiteRule, :term => "201301", :course => course
  @prereq.sepr_data_setup
end

When /^I (?:|navigate|have navigated) to the Student Eligibility & Prerequisite section for course "(.*?)" in the historic term$/ do |course|
  @courseOR = make CORequisitesData
  @prereq = make PreparationPrerequisiteRule, :course => course
  @prereq.navigate_to_mco_requisites
end

When /^I add a course statement after node "(.)" with course "([^\"]+)"$/ do |node, course|
  @prereq.rp_sepr_course_rule( "add", node, course)
end

When /^I add a courses statement after node "(.)" with courses "([^\"]+)" and course sets "([^\"]+)"$/ do |node, course, set|
  @prereq.rp_sepr_all_courses_rule( "add", node, course, set, "")
end

When /^I add a courses statement after node "(.)" with courses "([^\"]+)"$/ do |node, course|
  @prereq.rp_sepr_all_courses_rule( "add", node, course, "", "")
end

When /^I add a number of courses statement after node "(.)" with number "(\d+)" and courses "([^\"]+)" and course sets "([^\"]+)"$/ do |node, number, course, set|
  @prereq.rp_sepr_number_courses_rule( "add", node, number, course, set, "")
end

When /^I add a new course statement with course "([^\"]+)"$/ do |course|
  @prereq.rp_sepr_course_rule( "add", "", course)
end

When /^I want to edit the Recommended Preparation section$/ do
  @prereq.rp_edit_add( "edit")
end

When /^I want to add a new statement to the Recommended Preparation section$/ do
  @prereq.rp_edit_add( "add")
end

When /^I want to edit the Student Eligibility & Prerequisite section$/ do
  @prereq.sepr_edit_add( "edit")
end

When /^I delete the tree in the Student Eligibility & Prerequisite section$/ do
  on CourseOfferingRequisites do |page|
    page.prereq_delete
  end
end

When /^I want to compare the CO to the CLU for the Student Eligibility & Prerequisite section$/ do
  on CourseOfferingRequisites do |page|
    page.prereq_compare
  end
end

When /^I add a new course offering requisite to a course$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => "201208", :course => "PHYS272")
  on ManageCourseOfferings do |page|
    page.codes_list.each do |code|
      aos = make ActivityOfferingObject, :code => code, :parent_course_offering => @course_offering
      aos.edit :send_to_scheduler => true
    end
  end
  @courseOR = make CORequisitesData
  @prereq = make PreparationPrerequisiteRule, :section => "Recommended Preparation", :term => @course_offering.term,
                 :course => @course_offering.course
  @prereq.navigate_to_mco_requisites( true)
  @prereq.rp_edit_add( "add")
  @prereq.rp_sepr_text_rule( "add", "", "Added Recommended Prep on CO level")
  @prereq.commit_changes
end

Then /^the tree in the Student Eligibility & Prerequisite section should be empty$/ do
  @prereq.open_agenda_section
  on CourseOfferingRequisites do |page|
    page.loading.wait_while_present
    page.agenda_management_section.text.should =~ /.*Student Eligibility & Prerequisite.*Rule.*\.\nCorequisite.*/m
  end
end

Then /^the Compare to Catalog link should exist for the Student Eligibility & Prerequisite section$/ do
  on CourseOfferingRequisites do |page|
    page.prereq_compare_link.should exist
  end
end

#############################
#Steps used by all rule types
When /^I commit and return to see the changes made to the proposition$/ do
  @courseOR.commit_changes( true)  #:return_to_edit_page =>
end

When /^I commit changes made to the proposition$/ do
  @courseOR.commit_changes
end

When /^I edit node "(.*?)" by changing course to "(.*?)"$/ do |node, change|
  @courseOR.edit_existing_node(node, "course", change)
end

When /^I edit node "(.*?)" by adding course "(.*?)"$/ do |node, change|
  @courseOR.edit_existing_node(node, "courses", change)
end

When /^I edit node "(.*?)" by changing text to "(.*?)"$/ do |node, change|
  @courseOR.edit_existing_node(node, "text", change)
end

When /^I delete the tree$/ do
  on ManageCORequisites do |page|
    page.edit_tree_section.span(:id => /u\d+_node_0_parent_root_span/).when_present.click
    page.del_btn
  end
end

When /^I delete node "(.)" in the tree$/ do |node|
  on ManageCORequisites do |page|
    page.edit_tree_section.span(:text => /.*#{Regexp.escape(node)}\..*/).when_present.click
    page.del_btn
  end
end

When /^I switch to the other tab on the page$/ do
  @courseOR.switch_tabs
end

When /^I update the manage course offering agendas page$/ do
  on ManageCORequisites do |page|
    page.update_rule_btn
  end
end

When /^I move node "(.)" down$/ do |node|
  @courseOR.move_around(node, "down")
end

When /^I move node "(.)" up$/ do |node|
  @courseOR.move_around(node, "up")
end

When /^I move node "(.)" out of the group$/ do |node|
  @courseOR.move_around(node, "out")
end

When /^I move node "(.)" out of and into the group$/ do |node|
  @courseOR.move_around(node, "out up in")
end

When /^I change the operator before node "(.*?)" to "(.*?)"$/ do |node, operator|
  @courseOR.change_operator(node, operator)
end

When /^I copy node "(.)" and paste it after node "(.)"$/ do |node, node_after|
  @courseOR.copy_cut_paste( node, node_after, "copy")
end

When /^I cut node "(.)" and paste it after node "(.)"$/ do |node, node_after|
  @courseOR.copy_cut_paste( node, node_after, "cut")
end

When /^I copy the group containing node "(.)" and paste it after node "(.)"$/ do |node, node_after|
  @courseOR.copy_cut_paste_group( node, node_after, "copy")
end

When /^I cut the group containing node "(.)" and paste it after node "(.)"$/ do |node, node_after|
  @courseOR.copy_cut_paste_group( node, node_after, "cut")
end

When /^I delete the group containing node "(.)"$/ do |node|
  @courseOR.delete_group( node)
end

Then /^the info message "(.*?)" should be present$/ do |mess|
  on ManageCORequisites do |page|
    page.info_message.text.should match /#{Regexp.escape(mess)}/
  end
end

Then /^there should be no node "(.*?)" on both tabs$/ do |node|
  on ManageCORequisites do |page|
    page.loading.wait_while_present
    page.edit_tree_section.text.should_not match /.*#{Regexp.escape(node)}\..*/
    page.logic_tab
    page.edit_loading.wait_while_present
    page.logic_text.text.should_not match /.*#{Regexp.escape(node)}.*/
  end
end

Then /^the agenda page's text should match "(.*)"$/ do |text|
  on CourseOfferingRequisites do |page|
    @courseOR.show_all_courses( "agenda")
    page.agenda_management_section.text.should match @courseOR.test_text("agenda", text)
  end
end

Then /^the agenda page's text should not match "(.*)"$/ do |text|
  on CourseOfferingRequisites do |page|
    @courseOR.show_all_courses( "agenda")
    page.agenda_management_section.text.should_not match @courseOR.test_text("agenda", text)
  end
end

Then /^the edit tab's text should match "(.*)"$/ do |text|
  on ManageCORequisites do |page|
    page.edit_loading.wait_while_present
    page.edit_tree_section.text.should match @courseOR.test_text("edit", text)
  end
end

Then /^the logic tab's text should match "(.*)"$/ do |text|
  on ManageCORequisites do |page|
    page.edit_loading.wait_while_present
    @courseOR.show_all_courses( "logic")
    page.preview_tree_section.text.should match @courseOR.test_text("logic", text)
  end
end

Then /^the CO and CLU should both have text "(.*?)"/ do |text|
  on CourseOfferingRequisites do |page|
    page.compare_tree.text.should match @courseOR.test_compare_text( text)
  end
end

Then /^the CO and CLU should differ with text "(.*?)"/ do |text|
  on CourseOfferingRequisites do |page|
    page.compare_tree.text.should_not match @courseOR.test_compare_text( text)
  end
end

Then /^the text area should contain "(.*)"$/ do |text|
  on ManageCORequisites do |page|
    page.edit_loading.wait_while_present
    page.logic_text.text.should == text
  end
end

Then /^the text area should be populated with "(.*)"$/ do |text|
  on ManageCORequisites do |page|
    page.edit_loading.wait_while_present
    page.logic_text.text.should == text
    page.update_rule_btn
  end
end

Then /^both tabs' text should match "(.*?)"$/ do |text|
  on ManageCORequisites do |page|
    page.edit_tree_section.text.should match @courseOR.test_text("edit", text)
    page.logic_tab.click
    page.edit_loading.wait_while_present
    @courseOR.show_all_courses( "logic")
    page.preview_tree_section.text.should match @courseOR.test_text("logic", text)
  end
end

Then /^the agenda page's text should before and after the submit match "(.*?)"$/ do |text|
  on CourseOfferingRequisites do |page|
    page.loading.wait_while_present
    @courseOR.show_all_courses( "agenda")
    page.agenda_management_section.text.should match @courseOR.test_text("agenda", text)
  end
  @courseOR.commit_changes( true)
  on CourseOfferingRequisites do |page|
    page.loading.wait_while_present
    @courseOR.show_all_courses( "agenda")
    page.agenda_management_section.text.should match @courseOR.test_text("agenda", text)
  end
end

Then /^node "(.*)" should be after node "(.*)"$/ do |second,first|
  on ManageCORequisites do |page|
    page.loading.wait_while_present
    page.edit_tree_section.text.should match /.*#{Regexp.escape(first)}\..+#{Regexp.escape(second)}\..*/m
    page.update_rule_btn
  end
end

Then /^the first node should match "(.*)"$/ do |text|
  on ManageCORequisites do |page|
    page.loading.wait_while_present
    page.edit_tree_section.text.should match /^Click on rule statement to move or modify\n[\s\t]*#{Regexp.escape(text)}.*/
    page.update_rule_btn
  end
end

Then /^node "(.*)" should be a "(.*)" node in the tree$/ do |node, level|
  on ManageCORequisites do |page|
    page.loading.wait_while_present
    page.edit_tree_section.span(:text => /.*#{node}\..*/).id.should match @courseOR.test_node_level(level)
    page.update_rule_btn
  end
end

Then /^there should be a dropdown with value "(.*)" before node "(.*)"$/ do |drop, node|
  on ManageCORequisites do |page|
    page.edit_loading.wait_while_present
    page.edit_tree_section.text.should match /.*#{Regexp.escape(drop)}.*#{Regexp.escape(node)}.*/m
  end
end

Then /^node "(.*)" should be preceded by an "(.*)" operator$/ do |drop, node|
  on ManageCORequisites do |page|
    page.edit_loading.wait_while_present
    page.edit_tree_section.text.should match /.*#{Regexp.escape(drop)}.*#{Regexp.escape(node)}.*/m
    page.update_rule_btn
  end
end

Then /^the Move In button should be disabled$/ do
  on ManageCORequisites do |page|
    if page.right_btn_element.attribute_value('disabled')
      page.right_btn_element.attribute_value('disabled').should == "true"
      page.update_rule_btn
    end
  end
end
