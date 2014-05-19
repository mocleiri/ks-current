When(/^I search for a course in the Course Search Page$/) do
  @course_offering = make CourseOffering, :course_code => "ENGL201" ,:description=>"historical",:requisite=>"None",:scheduled_terms=>"SP 14",:projected_terms=>"Check",:gened_requirements=>"General",:subject=>"English",:gened_code=>"DSHU"
  @course_offering.course_search

end

Then(/^the courses should be displayed with the requirement in the General Ed column$/) do
  on CourseSearch do |page|
    course_code=@course_offering.course_code
    page.gened_code(course_code).text.should match /#{@course_offering.gened_code}/
  end

end


And(/^I select a requirement in the General Education Requirement facet$/) do
  @course_offering.select_facet

end

Then(/^I should be able to view the courses meeting the General Education Requirement$/) do
  on CourseSearch do |page|
    puts page.results_list_gened
    puts @course_offering.gened_code
      page.results_list_gened.each { |e| e.should match /#{@course_offering.gened_code}/ }
  end
end

When(/^I search for  courses in the Course Search Page$/) do
  @course_offering = make CourseOffering, :course_code => "ENGL" ,:description=>"historical",:requisite=>"None",:scheduled_terms=>"SP 14",:projected_terms=>"Check",:gened_requirements=>"General",:subject=>"English",:gened_code=>"DSHU"
  @course_offering.course_search
end


