class CourseOffering

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows
  include Comparable

  COURSE_ARRAY = 0
  attr_accessor :course_code,:credit,:notes

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :course_code=>"BSCI430",
        :credit=>3,
        :notes=>"#{random_alphanums(20).strip}_pub"
    }
    options = defaults.merge(opts)
    set_options(options)
  end

  def add_course_to_current_term
    navigate_to_course_planner_home
    sleep 5
    on CoursePlannerPage do |page|
      #page.loading.wait_while_present
      page.current_term_add
      sleep 2
      page.course_code_text.set @course_code
      page.notes.set @notes
      page.add_to_plan
      puts page.growl_text
    end
  end


  def add_course_to_future_term
    navigate_to_course_planner_home
    sleep 5
      on CoursePlannerPage do |page|
        page.future_term_add
        sleep 2
        page.course_code_text.set @course_code
        page.credit.set @credit
        page.notes.set @notes
        page.add_to_plan
        puts page.growl_text
      end
  end

  def edit_plan_item_current_term
    on CoursePlannerPage do |page|
      sleep 2
      page.course_code_current_term_click
      page.edit_plan_item_click
      sleep 2
      puts page.view_notes_popover
    end
  end

  def edit_plan_item_future_term
    on CoursePlannerPage do |page|
      sleep 2
      page.course_code_future_term_click
      page.edit_plan_item_click
      sleep 2
      puts page.view_notes_popover
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


