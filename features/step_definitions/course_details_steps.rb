When /^I search for a course on course search page$/ do
 @course_search_result = make CourseSearchResults, :course_code => "ENGL201" ,:description=>"historical",:requisite=>"None",:scheduled_terms=>"SP 14",:projected_terms=>"Check",:gened_requirements=>"General",:subject=>"English"
 @course_search_result.course_search
end



And(/^I view the details of the course on the Course Details page$/) do
@course_search_result.navigate_course_detail_page
end

Then /^I should be able to view all the required information on the Course Details page$/ do
  on CourseDetailPage do |page|
    course_code=@course_search_result.course_code
    page.course_description(course_code).text.should match /#{@course_search_result.description}/
    page.course_requisites(course_code).text.should match /#{@course_search_result.requisite}/
    page.scheduled_terms(course_code).text.should match /#{@course_search_result.scheduled_terms}/
    page.projected_terms(course_code).text.should match /#{@course_search_result.projected_terms}/
    page.gened_requirements(course_code).text.should match /#{@course_search_result.gened_requirements}/
    page.subject(course_code).text.should match /#{@course_search_result.subject}/
  end
end