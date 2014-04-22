When /^I search for a course on course search page$/ do
  @course_offering = make CourseOffering, :course_code => "ENGL201"
  @course_offering.course_search
end


And(/^I view the details of the course on the Course Details page$/) do
 @course_offering.navigate_course_detail_page
end

Then /^I should be able to view all the required information on the Course Details page$/ do
  on CourseDetailPage do |page|
    course_code=@course_offering.course_code
    page.course_description(course_code).exists?.should == true
    page.course_detail_header.exists?.should == true
    page.course_requisites(course_code).exists?.should == true
    page.scheduled_terms(course_code).exists?.should == true
    page.projected_terms(course_code).exists?.should == true
    page.gened_requirements(course_code).exists?.should == true
    page.subject(course_code).exists?.should == true
    page.course_description(course_code).text.should match /historical/
    page.course_requisites(course_code).text.should match /None/
    page.scheduled_terms(course_code).text.should match /SP 14/
    page.projected_terms(course_code).text.should match /Check/
    page.gened_requirements(course_code).text.should match /General/
    page.subject(course_code).text.should match /English/
  end
  end