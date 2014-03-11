class CourseSearch < BasePage

  page_url "#{$test_site}myplan/course?methodToCall=start&viewId=CourseSearch-FormView"



  wrapper_elements
  frame_element

  element(:search_for_course) { |b| b.frm.text_field(id: "text_searchQuery_control") }
  element(:search_term_select) { |b| b.frm.select(name:"searchTerm") }
  action(:search) { |b| b.frm.button(id:"searchForCourses").click; b.loading.wait_while_present }
  #element(:results_table){ |b| b.frm.div(id: /course_search_results/).table }
  element(:results_table) { |b| b.table(id: "course_search_results") }
  ################
  action(:code_sort_icon) {|b| b.div(id:/course_search_results_wrapper/).table().thead().th(text:"Code").click}
  action(:title_sort_icon) {|b| b.div(id:/course_search_results_wrapper/).table().thead().th(text:"Title").click}
  element(:result_pagination) {|b| b.div(id:"course_search_results_paginate")}
  element(:results_list_previous_enabled) { |b| b.a(id: "course_search_results_previous")}
  element(:results_list_previous_click) { |b| b.results_list_previous_enabled.click }
  element(:results_list_previous_disabled) { |b| b.a(class:"previous paginate_button paginate_button_disabled",id:"course_search_results_previous")}
  #previous paginate_button paginate_button_disabled

  element(:results_list_next_enabled) { |b| b.a(id: "course_search_results_next") }
  element(:results_list_next_click) { |b| b.results_list_next_enabled.click }
  element(:results_list_next_disabled) { |b| b.a(class: "next paginate_button paginate_button_disabled",id:"course_search_results_next") }
  action(:course_code_result_link) { |ccode,b| b.tr(id: "#{ccode}").a(class: "ksap-text-ellipsis") }
  action (:course_code_result_link_click) {|b| b.course_code_result_link.click }
  action(:course_description) { |co_code,b| b.div(id: "#{co_code}_description").span(class: "uif-message").text }
  element(:back_to_search_results) { |b| b.link(text: "Back to Previous Page") }
  element(:course_search_results_info) { |b| b.div(id: "course_search_results_info") }
  element(:course_search_results_select) { |b| b.frm.select(name: "course_search_results_length") }
  element(:course_search_facet_divisions) { |b| b.div(id: "facet_curriculum_disclosureContent").div(class: "facets").ul.lis}
  element(:course_search_facet_level) { |b| b.div(id: "facet_level_disclosureContent").div(class: "facets").ul.lis}




  ################

  #plus symbol representing the add to plan and bookmark
  element(:plus_symbol) { |b| b.frm.input(class:"uif-field uif-imageField ksap-add") }
  action(:plus_symbol_popover) { |b| b.plus_symbol.click}

  # Add to plan pop over elements
  element(:add_plan_popover){|b| b.frm.div(class:"ksap-container-75")}
  element(:add_to_plan) {|b| b.div(class:"jquerybubblepopup jquerybubblepopup-ksap").a(class:"uif-actionLink uif-boxLayoutVerticalItem clearfix")}
  action(:adding_plan) {|b| b.add_to_plan.click}
  element(:bookmark_popover) {|b| b.frm.div(id: "course_add_course_page")}
  element(:add_to_plan_notes) { |b| b.text_field(name:"courseNote") }
  element(:add_to_plan_credit) { |b| b.text_field(name:"courseCredit")}
  action (:add_to_plan_button) { |b| b.frm.button(id:"u35").click}
  element(:term) { |b| b.frm.div(id:"course_add_course_page").select(name:"termId") }

  #Navigation plan to find course  and vice versa
  #action(:plan_page_click) {|b| b.div(id:"applicationNavigation").a(text:"Plan").click}

  COURSE_CODE = 0
  COURSE_NAME = 1


  def results_list

    list = []
    no_of_rows = get_results_table_rows_no(0) - 1
    for index in 0..no_of_rows do
      list << get_table_row_code(index,0)
    end
    list.delete_if { |item| item == "Code" }
    list.delete_if {|item| item == "" }
    list
  end

  # Get code from data table row safely
  def get_table_row_code(index,rescues)
    begin
      return results_table.rows[index].cells[COURSE_CODE].text
    rescue
      rescues = rescues+1
      puts "rescue from Selenium::WebDriver::Error::StaleElementReferenceError: #{rescues}"
      if rescues<5
        get_table_row_code(index,rescues)
      end
    end
  end

  # Get title from data table row safely
  def get_table_row_title(index,rescues)
    begin
      return results_table.rows[index].cells[COURSE_NAME].text
    rescue
      rescues = rescues+1
      puts "rescue from Selenium::WebDriver::Error::StaleElementReferenceError: #{rescues}"
      if rescues<5
        get_table_row_title(index,rescues)
      end
    end
  end

  # Get number of data table rows safely
  def get_results_table_rows_no(rescues)
    begin
      return results_table.rows.length
    rescue
      rescues = rescues+1
      puts "rescue from Selenium::WebDriver::Error::StaleElementReferenceError: #{rescues}"
      if rescues<5
        return get_results_table_rows_no(rescues)
      end
    end
  end

  def results_list_title
    title_list = []
    results_table.rows.each do |row|
      sleep(1)
      title_list << row[COURSE_NAME].text
    end
    title_list.delete_if { |item| item == "Code" }
    title_list.delete_if {|item| item == "" }

    puts "#{title_list}"
    sleep(2)
    title_list.sort!
    puts "#{title_list}"
  end

  def results_list_courses (expected)
    trimmed_array_list= Array.new
    results_list
    if expected.length == 4
      trimmed_array_list<<results_list.map! {|x| x.slice(0,4) }
    elsif expected.length == 5
      trimmed_array_list<<results_list.map! {|x| x.slice(0,5) }
    else
      trimmed_array_list<<results_list.map! {|x| x }
    end
    trimmed_array_list
  end


  def results_list_validation(split_text,search_FullText)
    sleep(2)
    no_of_rows = results_table.rows.length-1
    #puts "No of Rows = #{no_of_rows}"

    for index in 1..no_of_rows do

      if index == no_of_rows
        sleep(2)
        course_code = results_table.rows[index].cells[COURSE_CODE].text
        sleep(1)
        course_name = results_table.rows[index].cells[COURSE_NAME].text.downcase

        puts "Course name =  #{course_name}"
        course_code_result_link(course_code).click
        back_to_search_results.wait_until_present
        course_description_text = course_description(course_code).downcase
        back_to_search_results.click
        sleep(2)

        if ((course_code.downcase).include? (split_text).downcase) ||  ((course_name.include? (split_text).downcase )||(course_description_text.include? (split_text).downcase))
        else
          split_name = search_FullText.split(' ')
          for index in 0 ... split_name.size
            split_text = "#{split_name[index]}"
            if ((course_code.downcase).include? (split_text).downcase) ||
                ((course_name.include? (split_text).downcase )||(course_description_text.include? (split_text).downcase))
            else
              puts "#{course_code}"
              return false
            end
          end
        end
      end
    end
  end




  def single_text_search_results_validation(single_text)
    sleep(2)
    no_of_rows = results_table.rows.length-1
    for index in 1..no_of_rows do
      if index == no_of_rows
        sleep(2)
        course_code = results_table.rows[index].cells[COURSE_CODE].text
        sleep(1)
        course_name = results_table.rows[index].cells[COURSE_NAME].text.downcase
        course_code_result_link(course_code).click
        back_to_search_results.wait_until_present
        course_description_text = course_description(course_code).downcase
        back_to_search_results.click
        sleep(2)
        if ((course_code.downcase).include? (single_text).downcase) ||  ((course_name.include? (single_text).downcase )||(course_description_text.include? (single_text).downcase))
        else
          return false
        end
      end
    end
  end

#************************** Course Level Search--KSAP- 832  and US 618*********************

  # Validate that all returned results on current page meet search criteria
  def validate_result_list(expected_code, expected_text)
    resultList = results_list()
    no_of_results = resultList.length - 1
    for index in 0..no_of_results
      result_code = resultList[index]
      requiredFound = result_list_required_code_match(result_code,expected_code)
      if requiredFound == false
        # check for components in the course code
        found = result_list_code_match(result_code, expected_text)
        if found == false
          # if not found check against the course details full text
           found = course_details_text_match(index+1,result_code, expected_text)
        end
        if found == false
          # if not found fail
          puts "Failed on #{result_code}"
          return false
        end
      end
    end

    return true
  end

  # Determine if a specific required course was returned
  def result_list_required_code_match(result_code, expected_code)
    expectedCode = expected_code.split(",",-1)
    no_of_code = expectedCode.length
    for index in 0...no_of_code do
      code = expectedCode[index]
      formatted_code = code.downcase
      formatted_result = result_code.downcase

      if formatted_code == formatted_result
        # remove found code from expected codes
        expected_code.gsub!(code," ")
        expected_code.gsub!(" ,","")
        expected_code.gsub!(", ","")
        expected_code.gsub!(" ","")
        return true
      end
    end
    # if none are found return nil
    return false
  end

  # Determine if a search result was from a course code base search
  def result_list_code_match(result_code, expected_text)
    expectedText = expected_text.gsub("00","").gsub("xx","").gsub("XX","").split(",",-1)
    no_of_text = expectedText.length
    for index in 0...no_of_text do
      text = expectedText[index]
      size = text.length

      # Size 1 indicates an exact level so only check against the codes first digit
      if size == 1
        formatted_text = text.slice(0,size).downcase
        formatted_result = result_code.slice(4,size).downcase
      else
        # Size 3 indicates an exact code so only check against the last 3 digits
        if size == 3
          formatted_text = text.slice(0,size).downcase
          formatted_result = result_code.slice(4,size).downcase
        else
          formatted_text = text.slice(0,size).downcase
          formatted_result = result_code.slice(0,size).downcase
        end
      end

      if formatted_text == formatted_result
        return true
      end
    end
    puts "No code match on #{result_code}"
    return false
  end

  # Determine if a search result from a full text search
  def course_details_text_match (row_number, result_code, expected_text)
    expectedText = expected_text.split(",",-1)
    no_of_text = expectedText.length

    course_code = get_table_row_code(row_number,0)
    course_name = get_table_row_title(row_number,0).downcase

    course_code_result_link(course_code).click
    back_to_search_results.wait_until_present
    course_description_text = course_description(course_code).downcase
    back_to_search_results.click
    sleep(2)

    for index in 0...no_of_text do
      text = expectedText[index]
      if (((course_code.downcase).include? (text).downcase) ||  (course_name.include? (text).downcase )||(course_description_text.include? (text).downcase))
        return true
      end
    end
    puts "No text match on #{result_code}"
    return false
  end

  def check_ascending_order_code()

    puts "test for checking ascending order"
    sleep(1)
    no_of_rows = results_table.rows.length-1
    puts no_of_rows
    current_code = nil
    previous_Code = nil
    for index in 1..no_of_rows do
      sleep(1)
      puts  "previous_Code =  #{previous_Code}"
      current_code = results_table.rows[index].cells[COURSE_CODE].text
      puts  "current_code =  #{current_code}"
      if index > 1
        if (previous_Code <=> current_code) > 0
          return false
        end
      end
      previous_Code = current_code
    end
  end


  def check_descending_order_code()

    puts "test for checking descending order"
    sleep(1)
    no_of_rows = results_table.rows.length-1
    puts no_of_rows
    current_code = nil
    previous_Code = nil
    for index in 1..no_of_rows do
      sleep(1)
      puts  "previous_Code =  #{previous_Code}"
      current_code = results_table.rows[index].cells[COURSE_CODE].text
      puts  "current_code =  #{current_code}"
      if index > 1
        if (previous_Code <=> current_code) < 0
          return false
        end
      end

      previous_Code = current_code

    end
  end

  def check_ascending_order_title()

    puts " ======= test for checking ascending order for title ======= "
    sleep(2)
    no_of_rows = results_table.rows.length-1
    puts "no_of_rows = #{no_of_rows} "
    current_title = nil
    previous_title = nil
    for index in 1..no_of_rows do
      puts "index = #{index}"
      puts  "previous_Title =  #{previous_title}"
      sleep(2)
      current_title = results_table.rows[index].cells[COURSE_NAME].text
      puts "current tile = #{current_title}"
      if index > 1
        puts "index > 0 "
        if (previous_title <=> current_title) > 0
          puts " coming inside the loop !!!!!!!!!!!!!!!!"
          return false
        end
      end
      previous_title = current_title
    end
  end




  def check_descending_order_title()

    puts " ======== descending order for title ======= "
    sleep(1)
    no_of_rows = results_table.rows.length-1
    puts no_of_rows
    current_title = nil
    previous_title = nil
    for index in 1..no_of_rows do
      puts  "previous_Title =  #{previous_title}"
      sleep(1)
      current_title = results_table.rows[index].cells[COURSE_NAME].text
      puts  "current_Title =  #{current_title}"
      if index > 1
           if (previous_title <=> current_title) < 0
           return false
        end
      end
      previous_title = current_title
    end
  end
end


