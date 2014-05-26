When(/^I search for courses in the Course Search Page$/) do
  @course_offering = make CourseOffering, :course_code => "ENGL" ,:description=>"historical",:requisite=>"None",:scheduled_terms=>"SP 14",:projected_terms=>"Check",:gened_requirements=>"General",:subject=>"English",:gened_code=>"DSHU",:credit=>"1",:course_level=> '3'
  @course_offering.course_search
end


And(/^I narrow the search results by a specific term$/) do
  on CourseSearch do |page|
    @course_offering.select_facet("term")
    page.clear_term_facet.wait_until_present
    sleep 1
  end
end


And(/^I narrow the search results by a specific Credit$/) do
  on CourseSearch do |page|
    @course_offering.select_facet("credit")
    page.clear_credit_facet.wait_until_present
    sleep 1
   end
end


And(/^I narrow the search results by a specific course prefix$/) do

  on CourseSearch do |page|
    @course_offering.select_facet("course_prefix")
    page.clear_courseprefix_facet.wait_until_present
    sleep 1
  end
  end




Then(/^I should see the courses scheduled for the specific term$/) do
  on CourseSearch do |page|
    page.results_table.rows[1..-1].each do |row|
    page.scheduled_terms(row).should match /#{@course_offering.scheduled_terms}/
    end
  end
end

Then(/^I should see the courses with the specific Credit$/) do

  on CourseSearch do |page|
    page.results_table.rows[1..-1].each do |row|
      page.credit(row).should match /#{@course_offering.credit}/
    end
  end

end



Then(/^I should see the courses with the specific course prefix$/) do

  on CourseSearch do |page|
    page.results_table.rows[1..-1].each do |row|
      page.courseprefix(row).should match /#{@course_offering.course_code}/
    end
  end


end


And(/^I narrow the search results by a specific course level$/) do
  on CourseSearch do |page|
    @course_offering.select_facet("course_level")
    page.clear_level_facet.wait_until_present
    sleep 1
  end
end

Then(/^I should see  the courses with the specific course level$/) do
  on CourseSearch do |page|
    page.results_table.rows[1..-1].each do |row|
    page.courselevel(row).should match /#{@course_offering.course_level}/
    end
  end
 end



When(/^I narrow the search results in the course search page using any facet$/) do
  @course_offering = make CourseOffering, :course_code => "ENGL" ,:description=>"historical",:requisite=>"None",:scheduled_terms=>"SP 14",:projected_terms=>"Check",:gened_requirements=>"General",:subject=>"English",:gened_code=>"DSHU",:credit=>"1",:course_level=> '3'
  @course_offering.course_search
  on CourseSearch do |page|
    @search_results_before_facet_selection==page.course_search_results_info.text
    @course_offering.select_facet("term")
    page.clear_term_facet.wait_until_present
    sleep 1
   end
end



And(/^I undo the filtering performed using the specified facet$/) do
  on CourseSearch do |page|
    page.clear_term_facet.click
    @search_results_after_clearing==page.course_search_results_info.text
    end

end

Then(/^I should see the courses in the search results without any filtering being applied$/) do
  @search_results_before_facet_selection.should == @search_results_after_clearing
end
