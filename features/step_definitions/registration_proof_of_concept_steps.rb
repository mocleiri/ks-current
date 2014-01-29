When /^I search for course offerings by using the supplied course offering code$/ do
  on RegisterForCourseSearch do |page|
    page.begin_course_search
  end
end

When /^I search for course offerings by using a custom course offering code$/ do
  on RegisterForCourseSearch do |page|
    page.search_for_a_course("CHEM23")
  end
end

When /^the course search results are displayed in mobile format$/ do
  on RegisterForCourseResults do |page|
    # this is the message about seeing course details - should not be there
    page.details_message.present?.should == false
    # basic course info - should be there
    page.results_course_code(0).should == "CHEM231"
    page.results_course_title(0).should == "Organic Chemistry I"
  end
end

When /^the course search results are displayed in tablet format$/ do
  on RegisterForCourseResults do |page|
    # this is the message about seeing course details - should be there
    page.details_message.present?.should == true
    # basic course info - should be there
    page.results_course_code(0).present?.should == true
    page.results_course_title(0).present?.should == true
  end
end

When /^I select a course offering from the search results$/ do
  on RegisterForCourseResults do |page|
    page.results_link(0).click
  end
end

When /^the course detail is displayed in mobile format$/ do
  on RegisterForCourseResults do |page|
    # only one course detail should be present (the one that was clicked on)
    # (not sure how to code this)
    # course detail for selected course should be there
    page.results_detail_course_code.text.should match /CHEM231/
    page.results_detail_grading_options.text.should == "Audit, Pass/Fail"
  end
end

