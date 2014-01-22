class CourseOffering

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows
  include Comparable

  COURSE_ARRAY = 0
  attr_accessor :course_code,:credit,:notes; :planned_term

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :course_code=>"BSCI430",
        :credit=>3,
        :notes=>"#{random_alphanums(20).strip}_pub",
        :planned_term=>"2013Fall"
    }
    options = defaults.merge(opts)
    set_options(options)
  end

  def add_course_to_current_term
    navigate_to_course_planner_home

    on CoursePlannerPage do |page|

      begin
        page.course_code_term(@planned_term, @course_code) != nil?
        page.course_code_term_click(@planned_term, @course_code)
        page.course_code_delete_click
        page.delete_course.wait_until_present(5)
        page.delete_course_click
        page.refresh
      rescue
        #means that course was NOT found, BUT be careful as the rescue will hide errors if they occur in cleanup steps
      end
      page.add_to_term(@planned_term)
      page.course_code_text.wait_until_present(5)
      page.course_code_text.set @course_code
      page.notes.set @notes
      page.add_to_plan
      puts page.growl_text
    end
  end


  def add_course_to_future_term
    navigate_to_course_planner_home
    on CoursePlannerPage do |page|
      if page.course_code_term(@planned_term, @course_code) != nil?
        page.course_code_term_click(@planned_term, @course_code)
        page.course_code_delete_click
        page.delete_course.wait_until_present(5)
        page.delete_course_click
        page.refresh
      end
      page.add_to_term(@planned_term)
      page.course_code_text.wait_until_present(5)
      page.course_code_text.set @course_code
      page.credit.set @credit
      page.notes.set @notes
      page.add_to_plan
      puts page.growl_text
    end
  end

  def edit_plan_item
    on CoursePlannerPage do |page|
      if @course_code == "ENGL388"
        sleep 2
        page.course_code_term_click(@planned_term, @course_code)
        page.edit_plan_item_click
      else
        sleep 2
        page.course_code_term_click(@planned_term, @course_code)
        page.edit_plan_item_click
      end
    end
  end

  def set_search_entry
    navigate_to_course_search_home
    on CourseSearch do |page|
      page.search_for_course.set @course_code
    end
  end


  def course_search (text=@course_code)
    navigate_to_course_search_home
    sleep 5
    on CourseSearch do |page|
      page.search_for_course.set text
      page.search
    end
  end

  def clear_search
    on CourseSearch do |page|
      page.clear
    end
  end


end




