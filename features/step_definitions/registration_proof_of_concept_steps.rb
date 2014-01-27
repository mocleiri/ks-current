When /^I search for course offerings by using the supplied course offering code$/ do
  on RegisterForCourseSearchMobile do |page|
    page.search_for_course
  end
end

When /^the course search results are displayed in mobile format$/ do
  on RegisterForCourseResultsMobile do |page|
    # this is the message about seeing course details
    page.details_message_element.present?.should == false
    # basic course info should be there
    page.course_name_element.present?.should == true
  end
end

