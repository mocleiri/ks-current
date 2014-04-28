# note: this test has been code-reviewed here: https://fisheye.kuali.org/cru/ks-370
# additional notes about future-refactoring were left in this jira: https://jira.kuali.org/browse/KSENROLL-5895

When /^I designate a valid term and cross-listed Catalog Course Code$/ do
  suffix = "AFT#{random_alphanums(2)}".upcase
  source_term = Rollover::MAIN_TEST_TERM_SOURCE

  @course_offering_owner = make CourseOffering, :course => "ENGL250", :suffix => suffix, :term => source_term, :cross_listed => true
  @course_offering_alias = make CourseOffering, :course => "WMST255#{suffix}", :term => source_term

end

And /^I create a Course Offering (with|without) selected cross-listed Catalog Course Code$/ do |crosslisted|
  @course_offering_owner.cross_listed = false unless crosslisted == 'with'
  @course_offering_owner.create
end

And /^the cross-listing is indicated for the alias Course Offering$/ do
  cross_listed_infoText_targetValue = @course_offering_owner.course + " (Owner)"

  @course_offering_alias.manage

  #  Validate the crosslisting messaging is present.
  on ManageCourseOfferings do |page|
    page.cross_listed_message_element.wait_until_present
    page.cross_listed_message.include? "crosslisted alias for: " << cross_listed_infoText_targetValue
  end
end

And /^the copy-link is not showing for the alias$/ do
  @course_offering_alias.search_by_subjectcode
  on(ManageCourseOfferingList).copy_link(@course_offering_alias.course).should_not be_present
end

Then /^the alias course does not exist$/ do
  @course_offering_alias.manage

  expected_errMsg = "Cannot find any course offering for the course code: \u201C#{@course_offering_alias.course}"
  on(ManageCourseOfferings).first_msg.should match /#{expected_errMsg}/
end

And /^I remove a cross-listed Course Offering$/ do
  @cross_listed_co.manage
  #on(ManageCourseOfferings).edit_course_offering
  @cross_listed_co.edit :cross_listed => false
end


And /^I manage the (owner|alias) Course Offering$/ do |cluType|

  if cluType == 'alias'
    @input_code_value = "#{@cross_listed_co.cross_listed_codes[0]}"
  else
    @input_code_value = "#{@cross_listed_co.course}"
  end
  #@input_code_value << "#{@suffix_with_cl}"

  @cross_listed_co.manage
end

Then /^the alias Course Offering does not exist$/ do
  expected_errMsg = "Cannot find any course offering"
  on ManageCourseOfferings do |page|
    puts "page.first_msg = #{page.first_msg}"
    page.first_msg.should match /.*#{Regexp.escape(expected_errMsg)}.*/
  end
end

Then /^the owner course offering is not indicated as cross-listed with the alias CO$/ do
  @cross_listed_co.manage
  on ManageCourseOfferings do |page|
    page.edit_course_offering
  end
  on CourseOfferingCreateEdit do |page|
    page.cross_listed_co_check_box.set?.should == false
  end
end


Then /^the alias is indicated as cross-listed with the owner CO$/ do
  expect_result = "Crosslisted as\n#{@cross_listed_co.cross_listed_codes[0]}"
  #expect_result = "#{@cross_listed_co.cross_listed_codes[0]}#{@suffix_with_cl} is a crosslisted alias for #{@cross_listed_co.course}#{@suffix_with_cl} (Owner)"
  on ManageCourseOfferings do |page|
    page.cross_listed_message.should include expect_result
  end

  end

Then /^the edit page (should|should not) indicate a cross-listing$/ do |condition|
  on CourseOfferingCreateEdit do |page|
    if(condition == 'should not')
      page.cross_listed_as_label.visible?.should_not be_true
    else
      page.cross_listed_as_label.visible?.should be_true
    end
  end
end


When /^I create a cross-listed Course Offering$/ do
  @cross_listed_co = create CourseOffering, :create_by_copy => (make CourseOffering, :course => "ENGL250", :term => Rollover::MAIN_TEST_TERM_SOURCE)
  #@suffix_with_cl = "AFT#{random_alphanums(2)}".upcase
  #@cross_listed_co = create CourseOffering, :create_by_copy => (make CourseOffering, :course => "ENGL250", :suffix => @suffix_with_cl, :term => Rollover::MAIN_TEST_TERM_SOURCE)
  #@cross_listed_co = make CourseOffering, :course => "ENGL250D", :term => Rollover::MAIN_TEST_TERM_SOURCE
  @cross_listed_co.manage
#  @cross_listed_co.edit :cross_listed => true
  @cross_listed_co.capture_crosslist_aliases
end


And /^I delete the alias Course Offering$/ do
  cross_listed_co_alias = make CourseOffering, :course => @cross_listed_co.cross_listed_codes[0],
                               :term => @cross_listed_co.term
  cross_listed_co_alias.manage
  cross_listed_co_alias.delete_co_coc_view :code_list => [cross_listed_co_alias.course],
                                            :should_confirm_delete => true
end

Then /^the owner Course Offering and all its aliases are deleted$/ do
  @cross_listed_co.search_by_subjectcode
  on ManageCourseOfferingList do |page|
    co_list = page.co_list
    co_list.should_not include @cross_listed_co.course
    @cross_listed_co.cross_listed_codes.each do |code|
      co_list.should_not include code
    end
  end
end

When(/^I edit the course offering$/) do
  on ManageCourseOfferings do |page|
    page.edit_course_offering
  end
end