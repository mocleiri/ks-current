Given /^I have initiated two browser sessions with different users$/ do
  log_in "martha", "martha"

  @browser1 = @browser
  @browser2 = Watir::Browser.new :firefox

  @browser = @browser2
  log_in "admin", "admin"

  @browser = @browser1
end

When /^two users are concurrently editing the same course offering$/ do
  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :course=>"CHEM132")
  orig_co_code = @course_offering.course

  @course_offering.manage
  @course_offering.edit_offering :suffix => random_alphanums(2).upcase

  @browser = @browser2

  @course_offering_concurrent = make CourseOffering, :course=>orig_co_code, :term=>@course_offering.term
  @course_offering_concurrent.manage
  @course_offering_concurrent.edit_offering :suffix => random_alphanums(2).upcase

  @browser = @browser1
end

When /^the first user updates the suffix and saves the course offering$/ do
  @browser = @browser1

  @course_offering.save
end

When /^the second user updates the suffix and saves the course offering$/ do
  @browser = @browser2

  @course_offering_concurrent.save
end

When /^the update for the first user is successful$/ do
  @browser = @browser1

  @course_offering.manage
end

When /^the second user gets a VersionMismatchError$/ do
  @browser = @browser2

  on CourseOfferingCreateEdit do |page|
    #add verify error message
    true.should == false
  end
  @browser = @browser1
end

When /^two users are concurrently copying the same course offering$/ do
  @source_course_code = "ENGL211"
  @term = "201301"

  @browser = @browser1

  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set @term
    page.input_code.set @source_course_code[0,4] #subject code + course level (assumes always more than one CO returned)
    page.show
  end

  on ManageCourseOfferingList do |page|
    page.copy @source_course_code
  end

  @browser = @browser2

  go_to_manage_course_offerings
  on ManageCourseOfferings do |page|
    page.term.set @term
    page.input_code.set @source_course_code[0,4] #subject code + course level (assumes always more than one CO returned)
    page.show
  end

  on ManageCourseOfferingList do |page|
    page.copy @source_course_code
  end

end

When /^the two users complete the copy at nearly the same instant$/ do
  @browser = @browser1

  on CopyCourseOffering do |page|
    page.create_copy_element.click #don't wait
  end

  @browser = @browser2

  on CopyCourseOffering do |page|
    page.create_copy_element.click
    page.loading.wait_while_present
  end

end

When /^the two new course offerings have unique suffixes$/ do
  @browser = @browser1

  new_co_code1 = ''

  on ManageCourseOfferings do |page|
    new_co_code1 = page.input_code.value # source_course_code[0,5] #subject code + course level (assumes always more than one CO returned)
  end

  @browser = @browser2

  new_co_code2 = ''
  on ManageCourseOfferings do |page|
    new_co_code2 = page.input_code.value # source_course_code[0,5] #subject code + course level (assumes always more than one CO returned)
  end

 new_co_code1.should_not == new_co_code2
end

