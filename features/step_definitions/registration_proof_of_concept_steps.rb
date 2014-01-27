When /^I search for course offerings by using the supplied course offering code$/ do
  on RegisterForCourseSearchMobile do |page|
    page.search_for_course
  end
end

When /^the course search results are displayed in mobile format$/ do
  on RegisterForCourseResultsMobile do |page|
    # this is the message about seeing course details - should not be there
    page.details_message.present?.should == false
    # basic course info - should be there
    page.results_course_code(0).present?.should == true
    page.results_course_title(0).present?.should == true
  end
end

