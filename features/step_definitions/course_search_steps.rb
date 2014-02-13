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


When /^I search for a course and code separated by spaces$/ do
  @course_offering = make CourseOffering, :search_text => "ENGL 799"
  @course_offering.course_search_with_search_text
end


Then /^the results should return the specific course code\.$/ do
pending # express the regexp above with the code you wish you had
end

And /^also return results for search by course and search by code$/ do
  on CourseSearch do |page|
    page.multiple_page_check("ENGL").should be_true
    page.multiple_page_check("799").should be_true
  end
end

