class CourseOffering

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows
  include Comparable

  COURSE_ARRAY = 0
  attr_accessor :course_code,:credit,:notes, :planned_term, :term, :term_select, :course_desc, :course_name, :search_text

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :course_code=>"BSCI430",
        :credit=>3,
        :notes=>"#{random_alphanums(20).strip}_pub",
        :planned_term=>"2014Fall",
        :term=> "Spring 2014",
        :term_select=>nil,
        :course_desc =>nil,
        :course_name =>nil,
        :search_text =>nil
    }
    options = defaults.merge(opts)
    set_options(options)
  end

  def remove_code_from_term
    # navigate_to_course_planner_home
    on CoursePlannerPage do |page|
      begin
        page.course_planner_header.wait_until_present
        page.course_code_term(@planned_term, @course_code) != nil?
        page.course_code_term_click(@planned_term, @course_code)
        page.course_code_delete_click
        page.delete_course.wait_until_present
        page.delete_course_click
        page.refresh
      rescue
        #means that course was NOT found, BUT be careful as the rescue will hide errors if they occur in cleanup steps
      end
    end
  end

  def course_search_to_planner
    on CourseSearch  do |page|
      page.plan_page_click
    end
    on CoursePlannerPage do |page|
      page.course_planner_header.wait_until_present
      remove_code_from_term
      page.course_page_click
    end
  end

  def verify_saved_course_code_notes
    on CoursePlannerPage do |page|
      page.course_page_click
      page.plan_page_click
      page.course_planner_header.wait_until_present
      page.info_icon(@planned_term, @course_code).exists?.should == true
      page.course_code_term_click(@planned_term, @course_code)
      page.view_course_summary_click
      page.close_popup.wait_until_present
    end
  end

  def add_course_to_term
    navigate_to_maintenance_portal
    navigate_to_course_planner_home
    on CoursePlannerPage do |page|
      remove_code_from_term
      page.add_to_term(@planned_term)
      page.course_code_text.wait_until_present
      page.course_code_text.set @course_code
      page.credit.set @credit
      page.notes.set @notes
      page.add_to_plan
      puts page.growl_text
    end
  end

  def edit_plan_item_verify_notes
    on CoursePlannerPage do |page|
      sleep 2
      page.course_code_term_click(@planned_term, @course_code)
      page.edit_plan_item_click
    end
  end

  def edit_plan_item
    on CoursePlannerPage do|page|
      sleep 2
      page.course_code_term_click(@planned_term, @course_code)
      page.edit_plan_item_click
      page.close_popup.wait_until_present
      page.notes.set @notes
      page.save_click
      sleep 2
      page.course_code_term_click(@planned_term, @course_code)
      page.view_course_summary_click
    end
  end

  def set_search_entry
    on CourseSearch do |page|
      page.search_for_course.set @course_code
      page.search
    end
  end


  def course_search (text=@course_code, term_select=@term_select)
    navigate_to_maintenance_portal
    navigate_to_course_search_home
    sleep 5
    on CourseSearch do |page|
      page.search_for_course.set text
      if @term_select != nil
        page.search_term_select.select term_select
      end
      page.search
    end
  end

  def select_add_to_plan
    on CourseSearch do |page|
      page.plus_symbol.wait_until_present
      page.plus_symbol_popover
      page.adding_plan
      #sleep(2)
      page.term.wait_until_present
      page.term.select @term
      page.add_to_plan_credit.set @credit
      page.add_to_plan_notes.set @notes
      page.add_to_plan_button
      page.plan_page_click
    end
  end




#############################

  def course_search_with_search_text(text=@search_text, term_select=@term_select)
    navigate_to_maintenance_portal
    navigate_to_course_search_home
    sleep 5
    on CourseSearch do |page|
      page.search_for_course.set text
      if @term_select != nil
        page.search_term_select.select term_select
      end
      page.search
    end
  end


  def multi_text_search(expected)
    on CourseSearch do |page|
      split_name = expected.split(' ')
      puts split_name.length
      # if page.result_pagination.exists?
      if  split_name.length > 1
        for index in 0 ... split_name.size
          puts  "split_name[#{index}] = #{split_name[index].inspect}"

          check_all_results_data_for_text("#{split_name[index]}",expected)

          puts " ** - --- after Search text ----"

          until page.results_list_previous_disabled.exists? do
            sleep(2)
            puts "clicking previous page  #################################"
            page.results_list_previous_enabled.wait_until_present
            page.results_list_previous_enabled.click
          end
        end
      else
        page.check_all_results_data_for_text(expected,expected)
      end
    end
  end



  def check_all_results_data_for_text(split_text,search_FullText)
    on CourseSearch do |page|
    puts "Search Text = #{split_text}"
    pgno = 1

    until page.results_list_next_disabled.exists?

      puts "------ page no = #{pgno}"
      page.results_list_validation(split_text,search_FullText)
      page.results_list_next_enabled.wait_until_present
      page.results_list_next_click
      pgno = pgno+1
      page.results_list_next_enabled.wait_until_present
    end
    puts "------ page no = #{pgno}"
    page.results_list_validation(split_text,search_FullText)
    end
    end



def check_all_results_data_for_level(text)
  on CourseSearch do |page|
    puts "Search Text = #{text}"
    pgno = 1
    puts level_digit = text.slice(0)
    until page.results_list_next_disabled.exists?
      puts "------ page no = #{pgno}"
       page.result_list_level(text)
       page.results_list_next_enabled.wait_until_present
       page.results_list_next_click
       pgno = pgno+1
       page.results_list_next_enabled.wait_until_present
    end
    puts "------ page no = #{pgno}"
    page.result_list_level(text)
  end
  end
end
