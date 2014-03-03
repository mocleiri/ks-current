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
  element(:result_pagination) {|b| b.div(id:"course_search_results_paginate")}
  element(:results_list_previous_enabled) { |b| b.a(id: "course_search_results_previous")}
  element(:results_list_previous_click) { |b| b.results_list_previous_enabled.click }
  element(:results_list_previous_disabled) { |b| b.a(class:"previous fg-button ui-button ui-state-default ui-state-disabled")}

  element(:results_list_next_enabled) { |b| b.a(id: "course_search_results_next") }
  element(:results_list_next_click) { |b| b.results_list_next_enabled.click }
  element(:results_list_next_disabled) { |b| b.a(class: "next fg-button ui-button ui-state-default ui-state-disabled") }
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
    results_table.rows.each do |row|
      sleep(1)
      list << row[COURSE_CODE].text
    end
    list.delete_if { |item| item == "Code" }
    list.delete_if {|item| item == "" }
    list
  end


  def results_list_courses (expected)
    trimmed_array_list= Array.new
    results_list
    if expected.length == 4
      trimmed_array_list<<results_list.map! {|x| x.slice(0,4) }
    elsif expected.length == 5
      trimmed_array_list<<results_list.map! {|x| x.slice(0,5) }
    else
      trimmed_array_list
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

#************************** Course Level Search--KSAP- 832  and US 618*********************  fair Draft

  def result_list_level(text)
    sleep(1)
    no_of_rows = results_table.rows.length-1
    for index in 1..no_of_rows do
      if index == no_of_rows
        sleep(2)
        course_code = results_table.rows[index].cells[COURSE_CODE].text
        puts  "courseCode1 #{course_code}"
        puts level_digit = text.slice(0)
        search_text = /(#{level_digit}\d\d)/

        sleep(2)
        sliced_course_code = course_code[4..course_code.length]
        if (search_text.match(sliced_course_code))
        else
          return false
          break
        end
      end
    end
  end

  def division_facet_validation(text)
    sleep(1)
    expectedDivisions = text.split(",",-1)
    no_of_rows = course_search_facet_divisions.length - 1
    no_of_divisions = expectedDivisions.length - 1
    if no_of_divisions == no_of_rows
      for index in 0..no_of_rows do
        facet_text = course_search_facet_divisions[index].a.text.downcase
        found = false
        for index2 in 0..no_of_divisions do
          division_text = expectedDivisions[index2].downcase
          if division_text == facet_text
            found = true
          end
        end
        if found != true
          return false
        end
      end
    else
      return false
    end
    return true
  end

  def result_list_multi_code(text)
    sleep(1)
    expectedCourses = text.split(",",-1)
    no_of_courses = expectedCourses.length - 1
    resultList = results_list()
    no_of_results = resultList.length - 1
    if no_of_courses == no_of_results
      for index in 0..no_of_courses
        course = expectedCourses[index].downcase
        found = false
        for index2 in 0..no_of_results
          result = resultList[index2].downcase
          if course == result
            found = true
          end
        end
        if found !=true
          return false
        end
      end
    else
      return false
    end
    return true
  end

  def result_list_multi_level(text)
    sleep(1)
    expectedCourses = text.gsub("00","").split(",",-1)
    no_of_courses = expectedCourses.length - 1
    resultList = results_list()
    no_of_results = resultList.length - 1
    for index in 0..no_of_results
      sleep(1)
      result = resultList[index].downcase.slice(0,5)
      found = false
      for index2 in 0..no_of_courses
        course = expectedCourses[index2].downcase
        if course == result
          found = true
        end
      end
      if found !=true
        return false
      end
    end
    return true
  end
end