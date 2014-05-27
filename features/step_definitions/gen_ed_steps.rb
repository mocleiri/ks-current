When(/^I search for a course in the Course Search Page$/) do
 @course_search_results = make CourseSearchResults,
                               :course_code => "ENGL201" ,
                               :description=>"historical",
                               :requisite=>"None",
                               :scheduled_terms=>"SP 14",
                               :projected_terms=>"Check",
                               :gened_requirements=>"General",
                               :subject=>"English",
                               :gened_code=>"DSHU",
                               :gened_course=>"General Education: Humanities"
 @course_search_results.course_search
 @course_search_results.clear_facets

end

Then(/^the course should be displayed with the requirement in the General Ed column$/) do
  on CourseSearch do |page|
    course_code=@course_search_results.course_code
    page.gened_code(course_code).text.should match /#{@course_search_results.gened_code}/
  end
end


And(/^I select a requirement in the General Education Requirement facet$/) do
  on CourseSearch do |page|
    @course_search_results.select_facet("term")
     page.clear_term_facet.wait_until_present
     #cannot avoid sleep currently since have already tried to wait for element existence.Can research in future if there is a way
     sleep 1
  end
 end

Then(/^I should be able to view the courses meeting the General Education Requirement$/) do
 on CourseSearch do |page|
     page.results_table.rows[1..-1].each do |row|
     page.gened(row).should match /#{@course_search_results.gened_code}/
 end
 end
end


When(/^I search for  courses in the Course Search Page$/) do
 @course_search_results = make CourseSearchResults,
                               :course_code => "ENGL" ,
                               :description=>"historical",
                               :requisite=>"None",
                               :scheduled_terms=>"SP 14",
                               :projected_terms=>"Check",
                               :gened_requirements=>"General",
                               :subject=>"English",
                               :gened_code=>"DSSP",
                               :gened_course=>"General Education: Scholarship in Practice"
 @course_search_results.course_search
 @course_search_results.clear_facets

end


