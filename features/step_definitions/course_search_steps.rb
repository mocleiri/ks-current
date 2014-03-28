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
    @course_offering.multi_text_search(text).should be_true
  else
    begin
      @course_offering.multi_text_search(text).should_not be_true
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

When /^I choose to see "(.*?)" records per page$/ do |per_page|
  on CourseSearch do |page|
    page.course_search_results_select.select per_page
    #Sleep for X seconds to wait for the js to process the change
    sleep(2)
    page.course_search_results_select.value.should == per_page
  end
end

Then /^The table header text will be "(.*?)"$/ do |header_text|
  on CourseSearch do |page|
    page.course_search_results_info.text.should == header_text
  end
end

Then /^There will be (.*?) pages of results with (.*?) records per page$/ do |pages, total_per_page|
  on CourseSearch do |page|
    page.result_pagination.span.links.size.to_s.should == pages

    #Doing size-1 here since results_table.rows.size includes the header row
    (page.results_table.rows.size-1).to_s.should == total_per_page
  end
end

Then /^Pagination controls will not be visible if there is only 1 page$/ do
  on CourseSearch do |page|
    #If there's only one page, no pagination controls or record# selection
    elementsPresent = true
    if "1" == page.result_pagination.span.links.size.to_s
      elementsPresent = false
    end

    page.result_pagination.visible?.should == elementsPresent
    page.course_search_results_select.visible?.should == elementsPresent
  end
end

#------------------------------------------------------------------------------------------------------------------------------------
When /^I search for "(.*?)"$/ do |text|
  @course_offering = make CourseOffering, :search_text => text
  @course_offering.course_search_with_search_text
end


Then /^"(.*?)" and courses matching at least one "(.*?)" are returned$/ do |expected_courses, expected_component|
  @course_offering.check_all_results_data(expected_courses,expected_component).should be_true
end


# KSAP-773------------------------------------------------------------------------------------------------------------------------------------


And /^I search for a course on course search$/ do
  @course_offering = make CourseOffering, :course_code => "General"
  @course_offering.course_search
end


When /^I sort the table by course code$/ do
  on CourseSearch do |page|
    page.code_sort_icon
  end
end


Then /^the course code listed should be sorted in "(.*?)" order$/ do |text|
  if text == "Descending"
   on CourseSearch do |page|
     page.code_element.wait_until_present
   end
    @course_offering.check_code_descending_order_in_all_pages.should be_true
    puts "Descending is Passed True"
  else
    on CourseSearch do |page|
      page.code_element.wait_until_present
    end
    @course_offering.check_code_ascending_order_in_all_pages.should be_true
    puts "Ascending is Passed True"
  end
end


When /^I sort the table by title$/ do
  on CourseSearch do |page|
    page.title_sort_icon
     end
end


Then /^the course Title listed should be sorted in "(.*?)" order$/ do |text|
  if text == "Ascending"
    on CourseSearch do |page|
      page.title_element.wait_until_present
    end
    @course_offering.check_title_ascending_order_in_all_pages.should be_true
    puts"Ascending is Passed True"
  else
    on CourseSearch do |page|
      page.title_element.wait_until_present
    end
    @course_offering.check_title_descending_order_in_all_pages.should be_true
    puts "Descending is Passed True"
  end
end

#-----------------------------------------------------------------------------------------------------------------------

When /^I search for a course with "(.*?)" option$/ do |text|
  @course_offering = make CourseOffering, :search_text => text
  @course_offering.course_search_with_search_text
end

Then /^the "(.*?)" and courses matching at least one "(.*?)" are returned$/ do |expected_courses, expected_component|
  @course_offering.check_all_results_data(expected_courses,expected_component).should be_true
end


Then /^course code or course title or course description containing any text of "(.*?)" text option are returned$/ do |expected_courses, expected_component|
    @course_offering.check_all_results_data(expected_courses,expected_component).should be_true
end