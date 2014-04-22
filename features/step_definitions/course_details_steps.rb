When /^I search for a course on course search page$/ do
  @course_offering = make CourseOffering, :course_code => "ENGL201"
  @course_offering.course_search
end


And(/^I view the details of the course on the Course Details page$/) do
 @course_offering.navigate_course_detail_page
end

Then /^I should be able to view all the required information on the Course Details page$/ do
  on CourseDetailPage do |page|
    page.course_description(@course_offering.course_code).exists?.should == true
    page.course_detail_header.exists?.should == true
    page.course_requisites(@course_offering.course_code).exists?.should == true
    page.scheduled_terms(@course_offering.course_code).exists?.should == true
    page.projected_terms(@course_offering.course_code).exists?.should == true
  end
end