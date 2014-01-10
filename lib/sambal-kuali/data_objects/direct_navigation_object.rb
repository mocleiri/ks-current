# Stores constants for direct navigation URL's
# class attributes are initialized with the constants to act as a repository for direct links
# Current Usage: Navigation to pages that cannot be navigated to by hyperlink for Authorization Tests
class DirectNavigation
  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  attr_accessor :edit_course_offering,
                :edit_activity_offering,
                :manage_course_offering,
                :create_course_offering,
                :course_offering_results,
                :single_course_results,
                :manage_registration_group

  def initialize(browser, opts={})
    @browser = browser
  end

  def setup_search_edit_co_urls

    go_to_manage_course_offerings
    on(ManageCourseOfferings).term.wait_until_present
    @manage_course_offering = @browser.url

    @course_offering = make CourseOffering, :term=>Rollover::MAIN_TEST_TERM_SOURCE, :course=>"CHEM132"
    @course_offering.search_by_subjectcode
    @course_offering_results = @browser.url

    @course_offering.manage
    @single_course_results = @browser.url

    on ManageCourseOfferings do |page|
      page.edit_course_offering
    end
    @edit_course_offering = @browser.url
    on CourseOfferingCreateEdit do |page|
      page.cancel
    end

  end

  def setup_create_co_urls
    @course_offering = make CourseOffering, :term=>Rollover::MAIN_TEST_TERM_SOURCE, :course=>"CHEM132"
    @course_offering.start_create_by_search
    @create_course_offering = @browser.url
    on CreateCourseOffering do  |page|
      page.catalogue_course_code.set ""
      page.target_term.set ""
      page.cancel
    end
  end

  def setup_edit_ao_urls
    @course_offering = make CourseOffering, :term=>Rollover::MAIN_TEST_TERM_SOURCE, :course=>"CHEM132"
    @course_offering.manage
    on ManageCourseOfferings do |page|
      page.edit('A')
    end
    @edit_activity_offering = @browser.url
    on ActivityOfferingMaintenance do |page|
      page.cancel
    end
  end
end