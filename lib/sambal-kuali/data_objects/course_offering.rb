class CourseOffering

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows
  include Comparable

  COURSE_ARRAY = 0
  COURSE_CODE = 0
  COURSE_NAME = 1
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
      # Checking for regular expression which needs to be considered as  white space
      puts " Entering Multi Text Search expected = #{expected}"
      if expected =~ Regexp.union("*",";",",","$","#")
        puts "Search Text contain Special Character"
        return false
      end
      split_name = expected.split(' ')
      puts split_name.length
      # if page.result_pagination.exists?
      if  split_name.length > 1
        for index in 0 ... split_name.size
          puts  "split_name[#{index}] = #{split_name[index].inspect}"
          result = check_all_results_data_for_text("#{split_name[index]}",expected)
          if result == false
            return false
          end
          until page.results_list_previous_disabled.exists? do
            sleep(2)
            page.results_list_previous_enabled.wait_until_present
            page.results_list_previous_enabled.click
          end
        end
      else

        check_all_page_data_for_singletext(expected)      # when its Single Word


      end
    end
  end

  def check_all_page_data_for_singletext(single_text)
    on CourseSearch do |page|
      puts "Search Text = #{single_text}"
      pgno = 1
      if page.results_list_next_enabled.exists?

        until page.results_list_next_disabled.exists?
          puts "------ page no = #{pgno}"
          page.single_text_search_results_validation(single_text)
          page.results_list_next_enabled.wait_until_present
          page.results_list_next_click
          pgno = pgno+1
          page.results_list_next_enabled.wait_until_present
        end
      else
        puts "------ page no = #{pgno}"
        ### - The line executes when its Single Page and also Last page in Multi page
        page.single_text_search_results_validation(single_text)
      end

    end
  end



  def check_all_results_data_for_text(split_text,search_FullText)
    on CourseSearch do |page|
      puts "Search Text = #{split_text}"
      pgno = 1
      if page.results_list_next_enabled.exists?
        until page.results_list_next_disabled.exists?
          puts "------ page no = #{pgno}"
          result = page.results_list_validation(split_text,search_FullText)
          if result == false
            return false
          end
          page.results_list_next_enabled.wait_until_present
          page.results_list_next_click
          pgno = pgno+1
          page.results_list_next_enabled.wait_until_present
        end
      else

        puts "------ page no = #{pgno}"
        ### - The line executes when its Single Page and also Last page in Multi page
        page.results_list_validation(split_text,search_FullText)

      end
    end
  end

  def check_all_results_data_for_level(text)
    on CourseSearch do |page|
      puts "Search Text = #{text}"
      pgno = 1
      puts level_digit = text.slice(0)
      if page.results_list_next_enabled.exists?
        until page.results_list_next_disabled.exists?
          puts "------ page no = #{pgno}"
          page.result_list_level(text)
          page.results_list_next_enabled.wait_until_present
          page.results_list_next_click
          pgno = pgno+1
          page.results_list_next_enabled.wait_until_present
        end
      else

        puts "------ page no = #{pgno}"
        page.result_list_level(text)
      end
    end
  end

  # Check that returned results meet search criteria and required courses are
  def check_all_results_data(expected_courses, expected_text)
    on CourseSearch do |page|
      puts "Search Text = #{expected_text}"
      pgno = 1
      puts level_digit = expected_text.slice(0)
      until page.results_list_next_disabled.exists?
        puts "------ page no = #{pgno}"
        pass = page.validate_result_list(expected_courses,expected_text)
        if pass!= true
          return false
        end
        page.results_list_next_enabled.wait_until_present
        page.results_list_next_click
        pgno = pgno+1
        page.results_list_next_enabled.wait_until_present
      end
      puts "------ page no = #{pgno}"
      pass = page.validate_result_list(expected_courses,expected_text)
      if pass!= true
        return false
      end
      if expected_courses == ""
        # only when all expected courses are found does the test pass
        return true
      end
      puts "Not all expected codes found, missing: #{expected_courses}"
      return false
    end
  end

#------------------------- KSAP- 885 ,KSAP- 771 and  US KSAP- 626-----------------------------------------------------------,

  def check_code_ascending_order_in_all_pages
    on CourseSearch do |page|
      pgno = 1
      if page.results_list_next_enabled.exists?
        until page.results_list_next_disabled.exists?
          puts "------ page no = #{pgno}"
          page.check_results_sort_order(true , 0)
          page.results_list_next_enabled.wait_until_present
          page.results_list_next_click
          pgno = pgno+1
          page.results_list_next_enabled.wait_until_present
        end
      else
        puts "------ page no = #{pgno}"
        page.check_results_sort_order(true , 0)
      end
    end
  end

  def check_code_descending_order_in_all_pages
    on CourseSearch do |page|
      pgno = 1
      if page.results_list_next_enabled.exists?
        until page.results_list_next_disabled.exists?
          puts "------ page no = #{pgno}"
          page.check_results_sort_order(false , 0)
          page.results_list_next_enabled.wait_until_present
          page.results_list_next_click
          pgno = pgno+1
          page.results_list_next_enabled.wait_until_present
        end
      else
        puts "------ page no = #{pgno}"
        page.check_results_sort_order(false , 0)
      end
    end
  end

  def check_title_ascending_order_in_all_pages
    on CourseSearch do |page|
      pgno = 1
      if page.results_list_next_enabled.exists?
        until page.results_list_next_disabled.exists?
          puts "------ page no = #{pgno}"
          page.check_results_sort_order(true , 1)
          page.results_list_next_enabled.wait_until_present
          page.results_list_next_click
          pgno = pgno+1
          page.results_list_next_enabled.wait_until_present
        end
      else
        puts "------ page no = #{pgno}"
        page.check_results_sort_order(true ,1)
      end
    end
  end

  def check_title_descending_order_in_all_pages
    on CourseSearch do |page|
      pgno = 1
      if page.results_list_next_enabled.exists?
        until page.results_list_next_disabled.exists?
          puts "------ page no = #{pgno}"
          page.check_results_sort_order(false , 1)
          page.results_list_next_enabled.wait_until_present
          page.results_list_next_click
          pgno = pgno+1
          page.results_list_next_enabled.wait_until_present
        end
      else
        puts "------ page no = #{pgno}"
        page.check_results_sort_order(false , 1)
      end
    end
  end


#KSAP-1000--------------------------------------------------------------------------



  def check_logical_order_display_of_elements_in_allpages(textArray_length,textArray,numArray,output)
    on CourseSearch do |page|
      no_of_rows = page.results_table.rows.length-1
      cloned_textArray= textArray.clone
      cloned_numArray= numArray.clone
      numArray_length = numArray.size
      cloned_textArray_length = cloned_textArray.size
      cloned_numArray_length = cloned_numArray.size

      # have to implement for other pages
      if textArray_length > 0
        #check for courseCode in Text Array
        for index in 1..no_of_rows do
          course_code = page.results_table.rows[index].cells[COURSE_CODE].text
          sleep(1)
          course_name = page.results_table.rows[index].cells[COURSE_NAME].text.downcase
          if course_code.include? "#{textArray[0]}"

          else

            if textArray_length > 1
              textArray.delete_at(0)
              #text_array_element=text_array_element+1
              if course_code.include? "#{textArray[0]}"
              else
                textArray.delete_at(0)
                if course_code.include? "#{numArray[0]}"
                else
                  if numArray_length > 1
                    numArray.delete_at(0)
                    if course_code.include? "#{numArray[0]}"

                    else
                    end
                  else
                    numArray.delete_at(0)
                    if ((course_name.include? "#{cloned_textArray[0]}".downcase) ||(course_description_text.include? "#{cloned_textArray[0]}".downcase))
                    else
                      if cloned_textArray_length > 1
                        cloned_textArray.delete_at(0)
                        #text_array_element=text_array_element+1
                        if ((course_name. include? "#{cloned_textArray[0]}".downcase )||(course_description_text.include? "#{cloned_textArray[0]}".downcase))
                        else
                        end
                      else
                        cloned_textArray.delete_at(0)
                        if ((course_name. include? "#{cloned_numArray[0]}".downcase )||(course_description_text.include? "#{cloned_textArray[0]}".downcase))

                        else
                          if numArray_length > 1
                            cloned_numArray.delete_at(0)
                            if ((course_name. include? "#{cloned_numArray[0]}" .downcase )||(course_description_text.include? "#{cloned_textArray[0]}".downcase))
                            else
                              return false
                            end
                          end
                        end
                      end
                    end
                  end
                end
              end
            end
          end
        end
      else
        # check directly in Num Array
        for index in 1..no_of_rows do
          course_code = page.results_table.rows[index].cells[COURSE_CODE].text
          sleep(1)
          course_name = page.results_table.rows[index].cells[COURSE_NAME].text.downcase
          if course_code. include? "#{numArray[0]}"
          else
            if numArray > 1
              numArray.delete_at(0)
              if course_code. include? "#{numArray[0]}"
              else
              end
            end
          end
        end
      end
    end
  end


  def check_logical_order_display_of_elements_in_firstpage(textArray_length,numArray_length,textArray,numArray,output)
    on CourseSearch do |page|
      no_of_rows = page.results_table.rows.length-1
      concat_array_length=  output.size
      for index in 1..no_of_rows do
        sleep(1)
        course_code = page.results_table.rows[index].cells[COURSE_CODE].text
        sleep(1)
        course_name = page.results_table.rows[index].cells[COURSE_NAME].text.downcase
        if index > concat_array_length
          #for k in 0 ..textArray_length
          if course_code.include? "#{textArray[0]}"
          else
            if textArray_length > 1
              textArray.delete_at(0)
              #text_array_element=text_array_element+1
              if course_code. include? "#{textArray[0]}"
              else
              end
            else
              textArray.delete_at(0)
              if course_code.include? "#{numArray[0]}"
              else
                if numArray_length > 1
                  numArray.delete_at(0)
                  if course_code.include? "#{numArray[0]}"
                  else
                  end
                end
              end
            end
          end
        else
          puts "else loop  concatArray =  #{concat_array_length}"
          courseCode =  course_code
          puts "index = #{index};  courseCode = #{courseCode};   ConcatArray 1 = #{output[index-1]}"
          if  (courseCode == output[index-1])

          else
            return false
          end
        end
      end # end of for loop
    end
  end

  def logical_order_sort(text)
    output=[]
    arr = text.split( " " )
    numArray = Array.new
    numberArray = Array.new
    textArray = Array.new
    textstringArray = Array.new
    for index in 0 ... arr.size
      arrValue = "#{arr[index]}"
      if (arrValue.match(/^\d+$/))
        numberArray.push arrValue
      else
        textstringArray.push arrValue
      end
    end
    numArray=  numberArray.sort
    textArray =  textstringArray.sort
    numArray_length=numArray.size
    textArray_length=textArray.size
    puts numArray_length
    puts textArray_length
    for j in 0...textArray_length do
      for i in 0...numArray_length do
        output<< textArray[j] + numArray[i].to_s
      end
    end
    on CourseSearch do |page|
      no_of_rows = page.results_table.rows.length-1
      puts "no of rows = #{no_of_rows}"
      pgno = 1
      if page.results_list_next_enabled.exists?
        until page.results_list_next_disabled.exists?
          puts "------ page no = #{pgno}"
          if pgno == 1
            puts "inside pageno = 1 "
            result = check_logical_order_display_of_elements_in_firstpage(textArray_length,numArray_length,textArray,numArray,output)
            if result == false
              return false
            end
          else
            puts "<<<<<<<<<<<<<<<<<<<<<<<  page no = #{pgno}   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
            puts   " text Array Size = #{textArray.size}"
            puts   " Num Array Size = #{numArray.size}"

            check_logical_order_display_of_elements_in_allpages(textArray_length,textArray,numArray,output)

          end
          page.results_list_next_enabled.wait_until_present
          page.results_list_next_click
          pgno = pgno+1
          page.results_list_next_enabled.wait_until_present
        end
      else
        if pgno == 1
          result = check_logical_order_display_of_elements_in_firstpage(textArray_length,numArray_length,textArray,numArray, output)
          if result == false
            return false
          end
        end
      end
    end
  end
end
