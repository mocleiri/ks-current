When /^I search for a course$/ do
  @course_offering = make CourseOffering
  @course_offering.course_search
end


When /^I search for a course with "(.*?)" text option$/ do |text|
  @course_offering = make CourseOffering
  @course_offering.course_search(text)
end

Then /^the course "(.*?)" appear in the search results$/ do |test_condition|
  on CourseSearch do |page|
    if test_condition == "should"
      page.results_list.should include @course_offering.course_code
    else
      begin
        page.results_list.should_not include @course_offering.course_code
      rescue Watir::Exception::UnknownObjectException
        # Implication here is that there were no search results at all.
      end
    end
  end
end


Then /^courses containing  "(.*?)" text option appears$/ do |expected|
  on CourseSearch do |page|
  page.results_list_courses(expected).each { |e| e.should include (expected) }
  end
  end

When /^I search for a "(.*?)" "(.*?)" by "(.*?)"$/ do |course_status,course, term_selection|
    @course_offering = make CourseOffering, :course_code => course, :term_select => term_selection
    @course_offering.course_search
end



#-------------------------------------------------------------------------------------------

When /^I search for a course on the course search page$/ do
  @course_offering = make CourseOffering, :course_code => "ENGL2"
  @course_offering.course_search
end


Then /^I should check and display results across multiple pages\.$/ do
 on CourseSearch do |page|
   puts page.course_code_list
 end
end

And /^the search result should match with the course description$/ do
  on CourseSearch do |page|
    puts page.course_description_match
  end
end

Then /^I should extract and print the course code, course title and course description for the course$/ do
  on CourseSearch do |page|
    page.course_description_match should be true
  end
end


When /^I search for a course on the course search page with course title$/ do
  @course_offering = make CourseOffering, :search_text => "eng"
  @course_offering.course_search_with_search_text
end


When /^I search for a course with one word"(.*?)" text option$/ do |text|
  @course_offering = make CourseOffering, :search_text => text
  @course_offering.course_search_with_search_text
end

Then /^course title or course description containing "(.*?)"text option "(.*?)" appear$/ do |text,condition|
  @course_offering = make CourseOffering
  if condition == "should"
    @course_offering.check_all_results_data_for_text(text,nil).should be_true
  else
    begin
      @course_offering.check_all_results_data_for_text(text,nil).should_not be_true
    rescue Watir::Exception::UnknownObjectException
      # Implication here is that there were no search results at all.
    end
  end
  end

When /^I search for a course with multi word"(.*?)" text option$/ do |text|
  @course_offering = make CourseOffering, :search_text => text
  @course_offering.course_search_with_search_text

end


Then(/^course code or course title or course description containing any word of "(.*?)"text option "(.*?)" appear$/) do |expected, condition|
  @course_offering = make CourseOffering
  if condition == "should"
    @course_offering.multi_text_search(expected).should be_true
  else
    begin
      @course_offering.multi_text_search(expected).should_not be_true
    rescue Watir::Exception::UnknownObjectException
      # Implication here is that there were no search results at all.
    end
  end
  end

#------------------------------------------------------------------------------------------------------------------------------------

When /^I search for a course with "(.*?)" level option$/ do |level|
  @course_offering = make CourseOffering
  @course_offering.course_search(level)
end

Then /^only "(.*?)" level courses "(.*?)" be displayed$/ do |text, condition|
  @course_offering = make CourseOffering
  if condition == "should"
    @course_offering.check_all_results_data_for_level(text).should be_true
    puts "Test is Passed True"
  else
    begin
      @course_offering.check_all_results_data_for_level(text).should_not be_true
      rescue Watir::Exception::UnknownObjectException
    end
  end
end




