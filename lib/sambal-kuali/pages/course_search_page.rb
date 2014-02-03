class CourseSearch < BasePage

  page_url "#{$test_site}myplan/course?methodToCall=start&viewId=CourseSearch-FormView"



  wrapper_elements
  frame_element

  element(:search_for_course) { |b| b.frm.text_field(id: "text_searchQuery_control") }
  element(:search_term_select) { |b| b.frm.select(name:"searchTerm") }
  action(:search) { |b| b.frm.button(id:"searchForCourses").click; b.loading.wait_while_present }
  element(:results_table){ |b| b.frm.div(id: /course_search_results/).table }

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
    if expected == "ENGL"
      trimmed_array_list<<results_list.map! {|x| x.slice(0,4) }
    else
      trimmed_array_list<<results_list.map! {|x| x.slice(0,5) }
    end
    trimmed_array_list
  end


  def modified_list
    results_list

  end

=begin
  def adding_notes
    @browser.select_list(:class => "uif-dropdownControl", :index => 1).select("kuali.atp.2014Winter")

  end
=end
end