When /^I navigate to manage course offerings and I do not have access$/ do
  go_to_manage_course_offerings
  on ErrorPage do |page|
    page.error_401.should == true
  end
end

When /^I navigate to create course offerings and I do not have access$/ do
  go_to_create_course_offerings
  on ErrorPage do |page|
    page.error_401.should == true
  end
end

When /^I do not have direct url access to the manage course offerings search page$/ do
  @browser.goto @direct_navigation.manage_course_offering
  on ErrorPage do |page|
    page.error_401.should == true
  end
end

When /^I do not have direct url access to the course offering results page$/ do
  #access is now blocked with error
  @browser.goto @direct_navigation.course_offering_results
  on ErrorPage do |page|
    page.error_401.should == true
  end
end

When /^I do not have direct url access to the course offering single course results page$/ do
  #access is now blocked with error
  @browser.goto @direct_navigation.single_course_results
  on ErrorPage do |page|
    page.error_401.should == true
  end
end
When /^I do not have direct url access to the edit course offerings page$/ do
  @browser.goto @direct_navigation.edit_course_offering
  on ErrorPage do |page|
    page.error_401.should == true
  end
end

When /^I do not have direct url access to the edit activity offering page$/ do
  @browser.goto @direct_navigation.edit_activity_offering
  on ErrorPage do |page|
    page.error_401.should == true
  end
end

When /^I do not have direct url access to the create course offerings page$/ do
  @browser.goto @direct_navigation.create_course_offering
  on ErrorPage do |page|
    page.error_401.should == true
  end
end

Given /^there is direct url access to manage and search a course offering$/ do
  @direct_navigation = make DirectNavigation
  @direct_navigation.setup_search_edit_co_urls
end

Given /^there is direct url access to edit a course offering and activity offering$/ do
  @direct_navigation = make DirectNavigation
  @direct_navigation.setup_search_edit_co_urls
  @direct_navigation.setup_edit_ao_urls
  @direct_navigation.setup_create_co_urls
end