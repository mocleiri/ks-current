# Created data used for testing
#
# CORequisitesData contains CourseOffering
#
# class attributes are initialized with default data unless values are explicitly provided
#
# Typical usage: (with optional setting of explicit data value in  )
#  @editAgenda = make ManageCORequisitesData
#  @editAgenda.create_data_advanced_search(section, course)
# Methods:
#  @initialize(browser, opts={})
#  @navigate(term, course)
#  @data_setup(sect, term, course)
#  @commit_changes( back)
#  @assert_agenda_tree_contents
#  @open_agenda_section
#  @advanced_search(field, code)
#  @check_data_existence
#  @check_new_data_existence
#  @create_data_advanced_search( sect)
#  @edit_data_advanced_search( sect)
#  @create_course_rule( course, sect)
#  @create_text_rule( text)
#  @create_all_courses_rule( courses, sect)
#  @create_number_courses_rule( courses, sect)
#  @edit_existing_node(node, field, code)
#  @add_new_node(field, node, code)
#  @create_new_group(sect, field, node, course, set)
#  @switch_tabs
#  @check_text_correct( text, correct, section)
#  @convert_text( text, page)
#  @test_text(section, text, boolean)
#  @test_multi_line_text( section, test_string, boolean)
#  @test_single_line_text( section, test_text, boolean)
#  @test_popup_text( text, boolean)
#  @change_operator(level, operator)
#  @test_node_level( level, node)
#  @move_around( node, direction)
#  @copy_cut_paste( node, node_after, action)
#
# Note the use of the ruby options hash pattern re: setting attribute values

class CORequisitesData
  include Foundry
  include DataFactory

  attr_accessor :submit_btn,
                :section

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
    }

    options = defaults.merge(opts)

    set_options(options)
  end

  def navigate_to_mco_requisites
    #@course_offering = make CourseOffering, {:course => @course, :term => @term}
    @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering, :term => @term, :course => @course)
    @course_offering.manage
    on ManageCourseOfferings do |page|
      page.loading.wait_while_present(200)
      begin
        page.manage_course_offering_requisites
      rescue Watir::Wait::TimeoutError
        page.alert.ok
      end
    end
    on CourseOfferingRequisites do |page|
      page.loading.wait_while_present
      open_agenda_section
    end
  end

  def commit_changes( return_to_edit_page = false )
    begin
      on ManageCORequisites do |page|
        page.loading.wait_while_present
        page.update_rule_btn
      end
    rescue Watir::Wait::TimeoutError
      #means Update Rule button already clicked
    end
    begin
      on CourseOfferingRequisites do |page|
        page.loading.wait_while_present
        page.submit
        page.loading.wait_while_present(200)
      end
    rescue Watir::Wait::TimeoutError
      on ManageCourseOfferings do |page|
        page.alert.ok
      end
    end

    if return_to_edit_page == true
      on ManageCourseOfferings do |page|
        page.manage_course_offering_requisites
      end
    end
  end

  def open_agenda_section
    sections = {"Student Eligibility & Prerequisite"=>:eligibility_prereq_section,
                "Antirequisite"=>:antirequisite_section, "Corequisite"=>:corequisite_section,
                "Recommended Preparation"=>:recommended_prep_section,
                "Repeatable for Credit"=>:repeatable_credit_section,
                "Course that Restricts Credits"=>:restricted_credit_section}
    begin
      on CourseOfferingRequisites do |page|
        page.loading.wait_while_present(60)
        if page.send(sections[@section]).img(id: /KSCO-AgendaManage-RulePrototype_rule[A-F]_toggle_col/).present?
          page.send(sections[@section]).when_present.click
        end
      end
    rescue Watir::Wait::TimeoutError
      #Means agenda section is already open
      on CourseOfferingRequisites do |page|
        page.alert.ok
      end
    end
  end

  def advanced_search(field, code)
    on ManageCORequisites do |page|
      page.edit_loading.wait_while_present
      if field == "course code"
        click_search_link( Regexp.new(".*editTree.+proposition\.courseInfo\.code"))
        page.lookup_course_code.when_present.set code
      elsif field == "courses code"
        click_search_link( Regexp.new(".*editTree.+proposition\.cluSet\.clus"))
        page.lookup_course_code.when_present.set code
      elsif field == "course title"
        click_search_link( Regexp.new(".*editTree.+proposition\.cluSet\.clus"))
        page.lookup_course_title.when_present.set code
      elsif field == "course set"
        click_search_link( Regexp.new(".*editTree.+proposition\.cluSet\.cluSets"))
        page.lookup_set_name.when_present.set code
      elsif field == "program code"
        click_search_link( Regexp.new(".*editTree.+proposition\.progCluSet\.clus"))
        page.lookup_course_code.when_present.set code
      elsif field == "class standing"
        click_search_link( Regexp.new(".*editTree.+proposition\.classStanding"))
        page.lookup_class_standing.when_present.set code
      elsif field == "org"
        click_search_link( Regexp.new(".*editTree.+proposition\.orgInfo\.id"))
        page.lookup_abrev_org.when_present.set code
      elsif field == "population"
        click_search_link( Regexp.new(".*editTree.+proposition\.orgInfo\.population\.id"))
        page.lookup_population.when_present.set code
      end
      page.lookup_search_button
      page.loading.wait_while_present
      page.return_course_code(/.*#{Regexp.escape(code)}.*/i).a( text: /Select/).click
      page.loading.wait_while_present
    end
  end

  def click_search_link( regex)
    on ManageCORequisites do |page|
      elements = page.edit_tree_section.elements( :tag_name, 'a')
      elements.each do |elem|
        if elem.text == "Advanced Search" && page.edit_tree_section.a( id: elem.id).attribute_value('data-submit_data') =~ regex
          page.edit_tree_section.a(id: elem.id).click
          break
        end
      end
    end
  end

  def check_number_groups
    on ManageCORequisites do |page|
      groups = -1
      page.edit_tree_section.lis.each do |list|
        if list.text =~ /^[\s\t]*Must meet 1 of the following/ || list.text =~ /^[\s\t]*Must meet all of the following/
          groups += 1
        end
      end
      if groups == -1
        groups = 0
      end
      return groups
    end
  end

  def check_number_statements
    on ManageCORequisites do |page|
      statements = 0
      page.edit_tree_section.lis.each do |list|
        if list.text =~ /^[\s\t]*[A-Z]\./ && list.attribute_value('class') =~ /^ruleTreeNode simple/
          statements += 1
        end
      end
      return statements
    end
  end

  def add_courses( course, set, range)
    on ManageCORequisites do |page|
      courses = create_array( course)
      if courses != "" && courses != nil
        page.multi_course_dropdown.when_present.select /Approved Courses/
        if courses.length > 1
          courses.each do |elem|
            advanced_search("courses code", elem)
            page.add_line_btn
            page.adding.wait_while_present
          end
        else
          advanced_search("courses code", course)
          page.add_line_btn
          page.adding.wait_while_present
        end
      end
      sets = create_array( set)
      if sets != "" && sets != nil
        page.multi_course_dropdown.when_present.select /Course Sets/
        if sets.length > 1
          sets.each do |elem|
            advanced_search("course set", elem)
            page.add_line_btn
            page.adding.wait_while_present
          end
        else
          advanced_search("course set", set)
          page.add_line_btn
          page.adding.wait_while_present
        end
      end
    end
  end

  def add_program( program)
    on ManageCORequisites do |page|
      programs = create_array( program)
      if programs != "" && programs != nil
        page.program_dropdown.when_present.select /Approved Programs/
        programs.each do |elem|
          advanced_search("program code", elem)
          page.add_line_btn
          page.adding.wait_while_present
        end
      end
    end
  end

  def add_class_standing( standing)
    on ManageCORequisites do |page|
      advanced_search("class standing", standing)
      page.edit_loading.wait_while_present
    end
  end

  def add_org( org)
    on ManageCORequisites do |page|
      advanced_search("org", org)
      page.edit_loading.wait_while_present
    end
  end

  def choose_grade_type_grade( grade, type)
    types = { /completed notation/i=>:completed, "Letter"=>:letter, "Percentage"=>:percentage,
              "Pass/Fail"=>:pass_fail, "Administrative Grade"=>:grade}
    on ManageCORequisites do |page|
      page.send(types[type])
      page.edit_loading.wait_while_present
      page.grade_dropdown.when_present.select grade
    end
  end

  def edit_existing_node(node, field, code)
    on ManageCORequisites do |page|
      page.edit_tree_section.span(:text => /.*#{Regexp.escape(node)}\..*/).when_present.click
      page.edit_btn
      if field == "course"
        advanced_search("course code", code)
      elsif field == "courses"
        page.multi_course_dropdown.when_present.select /Approved Courses/
        advanced_search("courses code", code)
        page.add_line_btn
        page.adding.wait_while_present
      elsif field == "text"
        page.free_text_field.when_present.set code
      end
      page.preview_btn
    end
  end

  def add_new_node( group, node)
    on ManageCORequisites do |page|
      page.loading.wait_while_present
      node_to_edit = page.edit_tree_section.span(:text => /.*#{Regexp.escape(node)}\..*/)
      if node != "" && node != nil && node_to_edit.exists?
        if node_to_edit.parent.parent.attribute_value('class') !~ /ruleBlockSelected/
          page.edit_tree_section.span(:text => /.*#{Regexp.escape(node)}\..*/).when_present.click
        end
      end
      sleep 2
      if group == "group"
        page.group_btn
      else
        page.add_btn
      end
    end
  end

  def create_array( string)
    if string != "" && string != nil
      strings = string.split(/,/)
    else
      strings = string
    end
    return strings
  end

  def switch_tabs
    on ManageCORequisites do |page|
      page.edit_loading.wait_while_present(60)
      tab = page.tab_section.li(:class => /ui-state-active/).text
      if tab == "Edit Rule"
        page.logic_tab.when_present.click
      else
        page.object_tab.when_present.click
      end
    end
  end

  def convert_text( text, page)
    if page == "agenda" || page == "logic"
      if text =~ /^(.+)\s\((.+)\)$/
        converted = $1
        array = $2.split(/, /)
        array.each do |elem|
          converted += "," + elem
        end
      else
        converted = text
      end
    else
      converted = text
    end
    return converted
  end

  def show_all_courses( section)
    if section == "logic"
      on ManageCORequisites do |page|
        page.preview_tree_section.links.each do |link|
          if link.text == "Show Courses"
            link.click
          end
        end
      end
    elsif section == "agenda"
      on CourseOfferingRequisites do |page|
        page.agenda_management_section.links.each do |link|
          if link.text == "Show Courses"
            link.click
          end
        end
      end
    end
  end

  def test_text( section, text)
    string = ".*"
    new_text = convert_text( text, section)
    if (section == "edit" && new_text =~ /^.+\(.+\)$/) || new_text !~ /,/
      string += Regexp.escape(new_text) + ".*"
    elsif new_text =~ /^.+\(.+\).+$/
      array = new_text.split(/,/)
      array.each do |elem|
        if elem =~ /\(/
          string += Regexp.escape(elem) + ","
        else
          string += Regexp.escape(elem) + ".*"
        end
      end
    else
      array = new_text.split(/,/)
      array.each do |elem|
        string += Regexp.escape(elem) + ".*"
      end
    end
    return Regexp.new(string, Regexp::MULTILINE)
  end

  def test_compare_text( text)
    string = ".*"
    if text =~ /^(.+)\((.+)\)$/
      first_match = $1
      second_match = $2
      array = first_match.split(/,/)
      size = array.length - 1
      last_elem = "#{array[size]}(#{second_match})"
      array[size] = last_elem
    else
      array = text.split(/,/)
    end
    i = 0
    array.each do |elem|
      string += Regexp.escape(elem) + "\n" + Regexp.escape(elem)
      i += 1
      if i < array.size
        string += "\n[ANDOR]+\n[ANDOR]+\n"
      end
    end
    return Regexp.new(string, Regexp::MULTILINE)
  end

  def change_operator(node, operator)
    on ManageCORequisites do |page|
      i = 0
      lists = page.edit_tree_section.lis
      lists.each do |list|
        i += 1
        if( list.text =~ /^[\s\t]*AND\nOR$/ and lists[i].text =~ /^[\s\t]*#{node}\..*$/)
          list.select(:class=>/uif-dropdownControl/).select operator
          break
        end
      end
    end
  end

  def test_node_level( level)
    on ManageCORequisites do |page|
      if level == "primary"
        test = /u\d+_node_\d_parent_node_0_parent_root_span/
      elsif level == "secondary"
        test = /u\d+_node_\d_parent_node_\d_parent_node_0_parent_root_span/
      elsif level == "tertiary"
        test = /u\d+_node_\d_parent_node_\d_parent_node_\d_parent_node_0_parent_root_span/
      end
      return Regexp.new(test)
    end
  end

  def move_around( node, direction)
    on ManageCORequisites do |page|
      if page.edit_tree_section.html =~ /li.+class.+ruleBlockSelected/
        selection = page.edit_tree_section.li(:class => /ruleBlockSelected/).text
        if selection !~ /.*#{Regexp.escape(node)}\..*/
          page.edit_tree_section.span(:text => /.*#{Regexp.escape(node)}\..*/).when_present.click
        end
      else
        page.edit_tree_section.span(:text => /.*#{Regexp.escape(node)}\..*/).when_present.click
      end
      if direction == "down"
        page.down_btn
      elsif direction == "up"
        page.up_btn
      elsif direction == "out"
        page.left_btn
      elsif direction == "in"
        page.right_btn
      elsif direction == "out in"
        page.left_btn
        page.right_btn
      elsif direction == "out up in"
        page.left_btn
        page.up_btn
        page.right_btn
      end
    end
  end

  def copy_cut_paste( node, node_after, action)
    on ManageCORequisites do |page|
      if node != "" && node != nil && page.edit_tree_section.span(:text => /.*#{Regexp.escape(node)}\..*/).exists?
        page.edit_tree_section.span(:text => /.*#{Regexp.escape(node)}\..*/).when_present.click
      end
      if action == "copy"
        page.copy_btn
      elsif action == "cut"
        page.cut_btn
      end
      if node_after != "" && node_after != nil && page.edit_tree_section.span(:text => /.*#{Regexp.escape(node_after)}\..*/).exists?
        page.edit_tree_section.span(:text => /.*#{Regexp.escape(node_after)}\..*/).when_present.click
      end
      page.paste_btn
    end
  end

  def copy_cut_paste_group( node, node_after, action)
    on ManageCORequisites do |page|
      page.edit_tree_section.lis.each do |list|
        if list.text =~ /^[\s\t]*#{node}\./ && list.attribute_value('class') =~ /^ruleTreeNode simple/
          list.parent.parent.a(:class => /ruleTreeNode compoundNode/).click
          break
        end
      end
      if action == "copy"
        page.copy_btn
      elsif action == "cut"
        page.cut_btn
      end
      if node_after != "" && node_after != nil && page.edit_tree_section.span(:text => /.*#{Regexp.escape(node_after)}\..*/).exists?
        page.edit_tree_section.span(:text => /.*#{Regexp.escape(node_after)}\..*/).when_present.click
      end
      page.paste_btn
    end
  end

  def delete_group( node)
    on ManageCORequisites do |page|
      page.edit_tree_section.lis.each do |list|
        if list.text =~ /^[\s\t]*#{node}\./ && list.attribute_value('class') =~ /^ruleTreeNode simple/
          list.parent.parent.a(:class => /ruleTreeNode compoundNode/).click
          page.del_btn
          break
        end
      end
      page.edit_loading.wait_while_present
    end
  end
end

class AntirequisiteRule < CORequisitesData
  include Foundry
  include DataFactory

  attr_accessor :submit_btn,
                :section

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :section => "Antirequisite",
        :term => "201208",
        :course => "ENGL101"
    }

    options = defaults.merge(opts)

    set_options(options)
  end

  def ar_edit_add( edit_or_add)
    begin
      open_agenda_section
      on CourseOfferingRequisites do |page|
        if edit_or_add == "add" and page.antireq_add_link.exists?
          page.antireq_add
        else
          page.antireq_edit
        end
      end
    rescue Watir::Wait::TimeoutError
      #means Data setup was not needed
      on CourseOfferingRequisites do |page|
        page.alert.ok
      end
    end
  end

  def ar_data_setup
    navigate_to_mco_requisites
    on CourseOfferingRequisites do |page|
      page.loading.wait_while_present
      if page.antireq_edit_link.exists?
        page.antireq_edit
      else
        page.antireq_add
      end
      page.loading.wait_while_present
      if ar_create_rule_tree == true
        commit_changes( true)
      end
    end
  end

  def ar_create_rule_tree
    groups = check_number_groups
    statements = check_number_statements
    data_setup_needed = false
    if statements == 0
      ar_course_rule( "add", "", "ENGL101")
      statements+=1
      data_setup_needed = true
    end
    if statements == 1
      ar_course_rule( "add", "A", "HIST639")
      statements+=1
      data_setup_needed = true
    end
    if statements == 2
      ar_course_rule( "add", "B", "ENGL101")
      statements+=1
      data_setup_needed = true
    end
    if groups == 0 && statements == 3
      ar_text_rule( "group", "A", "free form text input value")
      groups+=1
      ar_all_courses_rule( "add", "", "ENGL478,HIST416", "", "")
      statements+=1
      data_setup_needed = true
    else
      if groups == 0
        ar_text_rule( "group", "A", "free form text input value")
        groups+=1
        data_setup_needed = true
      end
      if statements == 3
        ar_all_courses_rule( "add", "B", "ENGL478,HIST416", "", "")
        statements+=1
        data_setup_needed = true
      end
    end
    if groups == 1
      ar_text_rule( "group", "D", "Text")
      groups += 1
      data_setup_needed = true
    end
    if statements == 4
      ar_grade_courses_rule( "add", "C", "HIST395,HIST210", "", "", "completed notation", "Completed")
      statements += 1
      data_setup_needed = true
    end
    if groups >= 2
      on ManageCORequisites do |page|
        page.loading.wait_while_present
        page.edit_tree_section.select(:id => /u\d+_node_\d+_parent_node_0_parent_root_control/).when_present.select "OR"
        page.edit_loading.wait_while_present
        page.edit_tree_section.select(:id => /u\d+_node_\d+_parent_node_\d+_parent_node_0_parent_root_control/).when_present.select "OR"
        page.edit_loading.wait_while_present
        page.update_rule_btn
      end
    end
    return data_setup_needed
  end

  def ar_course_rule( group, node, course)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must not have successfully completed <course>/
      advanced_search("course code", course)
      page.preview_btn
    end
  end

  def ar_text_rule( group, node, text)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Free Form Text/
      page.free_text_field.when_present.set text
      page.preview_btn
    end
  end

  def ar_all_courses_rule( group, node, course, set, range)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must not have successfully completed any courses from <courses>/
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def ar_any_credits_rule( group, node, course, set, range)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must not have successfully completed any credits from <courses>/
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def ar_grade_courses_rule( group, node, course, set, range, type, grade)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must not have earned a grade of <gradeType> <grade> or higher in <courses>/
      choose_grade_type_grade( grade, type)
      add_courses( course, set, range)
      page.preview_btn
    end
  end
end

class CorequisiteRule < CORequisitesData
  include Foundry
  include DataFactory

  attr_accessor :submit_btn,
                :section

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :section => "Corequisite",
        :term => "201208",
        :course => "ENGL101"
    }

    options = defaults.merge(opts)

    set_options(options)
  end

  def cr_edit_add( edit_or_add)
    begin
      open_agenda_section
      on CourseOfferingRequisites do |page|
        if edit_or_add == "add"
          page.coreq_add
        else
          page.coreq_edit
        end
      end
    rescue Watir::Wait::TimeoutError
      #means Data setup was not needed
      on CourseOfferingRequisites do |page|
        page.alert.ok
      end
    end
  end

  def cr_data_setup
    navigate_to_mco_requisites
    on CourseOfferingRequisites do |page|
      page.loading.wait_while_present
      if page.coreq_edit_link.exists?
        page.coreq_edit
      else
        page.coreq_add
      end
      page.loading.wait_while_present
      if cr_create_rule_tree == true
        commit_changes( true)
      end
    end
  end

  def cr_create_rule_tree
    groups = check_number_groups
    statements = check_number_statements
    data_setup_needed = false
    if statements == 0
      cr_course_rule( "add", "", "ENGL101")
      statements+=1
      data_setup_needed = true
    end
    if statements == 1
      cr_course_rule( "add", "A", "HIST639")
      statements+=1
      data_setup_needed = true
    end
    if statements == 2
      cr_text_rule( "add", "B", "free form text input value")
      statements+=1
      data_setup_needed = true
    end
    if groups == 0 && statements == 3
      cr_all_courses_rule( "group", "A", "ENGL478,HIST416", "", "")
      groups+=1
      cr_text_rule( "add", "", "Text to copy")
      statements+=1
      data_setup_needed = true
    else
      if groups == 0
        cr_all_courses_rule( "group", "A", "ENGL478,HIST416", "", "")
        groups+=1
        data_setup_needed = true
      end
      if statements == 3
        cr_text_rule( "add", "D", "Text to copy")
        statements+=1
        data_setup_needed = true
      end
    end
    if groups == 1
      cr_text_rule( "group", "D", "Text")
      groups += 1
      data_setup_needed = true
    end
    if statements == 4
      cr_number_courses_rule( "add", "C", "1", "HIST395,HIST210", "", "")
      statements += 1
      data_setup_needed = true
    end
    if groups >= 2
      on ManageCORequisites do |page|
        page.loading.wait_while_present
        page.edit_tree_section.select(:id => /prop_compoundOpCode_node_\d+_parent_node_0_parent_root_control/).when_present.select "OR"
        page.edit_loading.wait_while_present
        page.edit_tree_section.select(:id => /prop_compoundOpCode_node_\d+_parent_node_\d+_parent_node_0_parent_root_control/).when_present.select "OR"
        page.edit_loading.wait_while_present
        page.update_rule_btn
      end
    end
    return data_setup_needed
  end

  def cr_course_rule( group, node, course)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must be concurrently enrolled in <course>/
      advanced_search("course code", course)
      page.preview_btn
    end
  end

  def cr_text_rule( group, node, text)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Free Form Text/
      page.free_text_field.when_present.set text
      page.preview_btn
    end
  end

  def cr_all_courses_rule( group, node, course, set, range)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must be concurrently enrolled in all courses from <courses>/
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def cr_number_courses_rule( group, node, number, course, set, range)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must be concurrently enrolled in a minimum of <n> courses from <courses>/
      page.integer_field.when_present.set number
      add_courses( course, set, range)
      page.preview_btn
    end
  end
end

class PreparationPrerequisiteRule < CORequisitesData
  include Foundry
  include DataFactory

  attr_accessor :submit_btn,
                :section

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :section => "Student Eligibility & Prerequisite",
        :term => "201208",
        :course => "ENGL101"
    }

    options = defaults.merge(opts)

    set_options(options)
  end

  def sepr_edit_add( edit_or_add)
    begin
      open_agenda_section
      on CourseOfferingRequisites do |page|
        if edit_or_add == "add"
          page.prereq_add
        else
          page.prereq_edit
        end
      end
    rescue Watir::Wait::TimeoutError
      #means Data setup was not needed
      on CourseOfferingRequisites do |page|
        page.alert.ok
      end
    end
  end

  def rp_edit_add( edit_or_add)
    begin
      open_agenda_section
      on CourseOfferingRequisites do |page|
        if edit_or_add == "add"
          page.prep_add
        else
          page.prep_edit
        end
      end
    rescue Watir::Wait::TimeoutError
      #means Data setup was not needed
      on CourseOfferingRequisites do |page|
        page.alert.ok
      end
    end
  end

  def sepr_data_setup
    navigate_to_mco_requisites
    on CourseOfferingRequisites do |page|
      page.loading.wait_while_present
      if page.prereq_edit_link.exists?
        page.prereq_edit
      else
        page.prereq_add
      end
      page.loading.wait_while_present
      if rp_sepr_create_rule_tree == true
        commit_changes( true)
      end
    end
  end

  def rp_data_setup
    navigate_to_mco_requisites
    on CourseOfferingRequisites do |page|
      page.loading.wait_while_present
      if page.prep_edit_link.exists?
        page.prep_edit
      else
        page.prep_add
      end
      page.loading.wait_while_present
      if rp_sepr_create_rule_tree == true
        commit_changes( true)
      end
    end
  end

  def rp_sepr_create_rule_tree
    groups = check_number_groups
    statements = check_number_statements
    data_setup_needed = false
    if statements == 0
      rp_sepr_course_rule( "add", "", "ENGL101")
      statements+=1
      data_setup_needed = true
    end
    if statements == 1
      rp_sepr_course_rule( "add", "A", "HIST639")
      statements+=1
      data_setup_needed = true
    end
    if statements == 2
      rp_sepr_text_rule( "add", "B", "free form text input value")
      statements+=1
      data_setup_needed = true
    end
    if groups == 0 && statements == 3
      rp_sepr_all_courses_rule( "group", "A", "ENGL478,HIST416", "", "")
      groups+=1
      rp_sepr_text_rule( "add", "", "Text")
      statements+=1
      data_setup_needed = true
    else
      if groups == 0
        rp_sepr_all_courses_rule( "group", "A", "ENGL478,HIST416", "", "")
        groups+=1
        data_setup_needed = true
      end
      if statements == 3
        rp_sepr_text_rule( "add", "D", "Text")
        statements+=1
        data_setup_needed = true
      end
    end
    if groups == 1
      rp_sepr_text_rule( "group", "D", "Text to copy")
      groups += 1
      data_setup_needed = true
    end
    if statements == 4
      rp_sepr_number_courses_rule( "add", "C", "1", "HIST395,HIST210", "", "")
      statements += 1
      data_setup_needed = true
    end
    if groups >= 2
      on ManageCORequisites do |page|
        page.loading.wait_while_present
        page.edit_tree_section.select(:id => /prop_compoundOpCode_node_\d+_parent_node_0_parent_root_control/).when_present.select "OR"
        #id="prop_compoundOpCode_node_1_parent_node_0_parent_node_0_parent_root_control"
        page.edit_loading.wait_while_present
        page.edit_tree_section.select(:id => /prop_compoundOpCode_node_\d+_parent_node_\d+_parent_node_0_parent_root_control/).when_present.select "OR"
        page.edit_loading.wait_while_present
        page.update_rule_btn
      end
    end
    return data_setup_needed
  end

  def rp_sepr_course_rule( group, node, course)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must have successfully completed <course>/
      advanced_search("course code", course)
      page.preview_btn
    end
  end

  def rp_sepr_text_rule( group, node, text)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Free Form Text/
      page.free_text_field.when_present.set text
      page.preview_btn
    end
  end

  def rp_sepr_all_courses_rule( group, node, course, set, range)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must have successfully completed all courses from <courses>/
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def rp_sepr_number_courses_rule( group, node, number, course, set, range)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must have successfully completed a minimum of <n> courses from <courses>/
      page.integer_field.when_present.set number
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def rp_sepr_less_number_courses_rule( group, node, number, course, set, range)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must have successfully completed no more than <n> courses from <courses>/
      page.integer_field.when_present.set number
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def rp_sepr_less_credits_rule( group, node, number, course, set, range, equal)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      if equal == ">"
        page.rule_dropdown.when_present.select /Must have successfully completed a minimum of <n> credits from <courses>/
      elsif equal == "<"
        page.rule_dropdown.when_present.select /Must successfully complete no more than <n> credits from <courses>/
      end
      page.integer_field.when_present.set number
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def rp_sepr_grade_courses_rule( group, node, course, set, range, type, grade)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must have earned a minimum grade of <gradeType> <grade> in <courses>/
      choose_grade_type_grade( grade, type)
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def rp_sepr_higher_grade_courses_rule( group, node, course, set, range, type, grade)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must not have earned a grade of <gradeType> <grade> or higher in <courses>/
      choose_grade_type_grade( grade, type)
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def rp_sepr_grade_number_courses_rule( group, node, course, set, range, type, grade, number)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must successfully complete a minimum of <n> courses from <courses> with a minimum grade of <gradeType> <grade>/
      page.integer_field.when_present.set number
      add_courses( course, set, range)
      choose_grade_type_grade( grade, type)
      page.preview_btn
    end
  end

  def prereq_gpa_courses_rule( group, node, course, set, range, gpa)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must have earned a minimum GPA of <GPA> in <courses>/
      page.integer_field.when_present.set gpa
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def rp_sepr_gpa_duration_rule( group, node, gpa, type, duration)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must have earned a minimum cumulative GPA of <GPA> in <duration><durationType>/
      page.integer_field.when_present.set gpa
      page.duration_field.when_present.set duration
      page.duration_dropdown.when_present.select type
      page.preview_btn
    end
  end

  def rp_sepr_gpa_rule( group, node, gpa)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must have earned a minimum cumulative GPA of <GPA>/
      page.integer_field.when_present.set gpa
      page.preview_btn
    end
  end

  def rp_sepr_program_rule( group, node, program)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must have been admitted to the <program> program/
      add_program( program)
      page.preview_btn
    end
  end

  def rp_sepr_not_program_rule( group, node, program)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must not have been admitted to the <program> program/
      add_program( program)
      page.preview_btn
    end
  end

  def rp_sepr_any_program_rule( group, node)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must be admitted to any program offered at the course campus location/
      page.edit_loading.wait_while_present
      page.preview_btn
    end
  end

  def rp_sepr_permission_instructor_rule( group, node)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Permission of instructor required/
      page.edit_loading.wait_while_present
      page.preview_btn
    end
  end

  def rp_sepr_course_term_rule( group, node, course, term, prior_or_as = "as of")
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must have successfully completed <course> #{Regexp.escape(prior_or_as)} <term>/
      advanced_search("course code", course)
      page.term_field.set term
      page.preview_btn
    end
  end

  def rp_sepr_course_between_terms_rule( group, node, course, term1, term2)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must have successfully completed <course> between <term1> and <term2>/
      advanced_search("course code", course)
      page.term_field.set term1
      page.term_two_field.set term2
      page.preview_btn
    end
  end

  def rp_sepr_program_org_rule( group, node, org)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must have been admitted to a program offered by <org>/
      add_org(org)
      page.preview_btn
    end
  end

  def rp_sepr_admin_org_rule( group, node, org)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Permission of <administering org> required/
      add_org(org)
      page.preview_btn
    end
  end

  def rp_sepr_min_credits_org_rule( group, node, org, credit)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must have successfully completed a minimum of <n> credits from courses in the <org>/
      page.integer_field.when_present.set credit
      add_org(org)
      page.preview_btn
    end
  end

  def rp_sepr_min_total_credits_rule( group, node, credit)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must have earned a minimum of <n> total credits/
      page.integer_field.when_present.set credit
      page.preview_btn
    end
  end

  def rp_sepr_population_rule( group, node, pop)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Student must be a member of <population>/
      advanced_search("population", pop)
      page.preview_btn
    end
  end

  def rp_sepr_program_org_duration_rule( group, node, program, integer, org, duration, type)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Students admitted to <program> may take no more than <n> courses in the <org> in <duration><durationType>/
      add_program( program)
      page.integer_field.when_present.set integer
      add_org(org)
      page.duration_field.when_present.set duration
      page.duration_dropdown.when_present.select type
      page.preview_btn
    end
  end

  def rp_sepr_not_program_org_duration_rule( group, node, program, integer, org, duration, type)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Students not admitted to <program> may take no more than <n> courses in the <org> in <duration><durationType>/
      add_program( program)
      page.integer_field.when_present.set integer
      add_org(org)
      page.duration_field.when_present.set duration
      page.duration_dropdown.when_present.select type
      page.preview_btn
    end
  end
end

class RepeatCreditRule < CORequisitesData
  include Foundry
  include DataFactory

  attr_accessor :submit_btn,
                :section

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :section => "Repeatable for Credit",
        :term => "201208",
        :course => "ENGL101"
    }

    options = defaults.merge(opts)

    set_options(options)
  end

  def rc_edit_add( edit_or_add)
    begin
      open_agenda_section
      on CourseOfferingRequisites do |page|
        if edit_or_add == "add"
          page.repeat_add
        else
          page.repeat_edit
        end
      end
    rescue Watir::Wait::TimeoutError
      #means Data setup was not needed
      on CourseOfferingRequisites do |page|
        page.alert.ok
      end
    end
  end

  def rc_data_setup
    navigate_to_mco_requisites
    on CourseOfferingRequisites do |page|
      page.loading.wait_while_present
      if page.repeat_edit_link.exists?
        page.repeat_edit
      else
        page.repeat_add
      end
      page.loading.wait_while_present
      if rc_create_rule_tree == true
        commit_changes( true)
      end
    end
  end

  def rc_create_rule_tree
    groups = check_number_groups
    statements = check_number_statements
    data_setup_needed = false
    if statements == 0
      rc_repeated_credit_rule( "add", "", "8")
      statements+=1
      data_setup_needed = true
    end
    if statements == 1
      rc_text_rule( "add", "A", "HIST639")
      statements+=1
      data_setup_needed = true
    end
    if statements == 2
      rc_text_rule( "add", "B", "ENGL101")
      statements+=1
      data_setup_needed = true
    end
    if groups == 0 && statements == 3
      rc_text_rule( "group", "A", "free form text input value")
      groups+=1
      rc_repeated_credit_rule( "add", "", "16")
      statements+=1
      data_setup_needed = true
    else
      if groups == 0
        rc_text_rule( "group", "A", "free form text input value")
        groups+=1
        data_setup_needed = true
      end
      if statements == 3
        rc_text_rule( "add", "B", "ENGL478 and HIST416")
        statements+=1
        data_setup_needed = true
      end
    end
    if groups == 1
      rc_text_rule( "group", "D", "Text")
      groups += 1
      data_setup_needed = true
    end
    if statements == 4
      rc_text_rule( "add", "C", "HIST395 and HIST210")
      statements += 1
      data_setup_needed = true
    end
    if groups >= 2
      on ManageCORequisites do |page|
        page.loading.wait_while_present
        page.edit_tree_section.select(:id => /u\d+_node_\d+_parent_node_0_parent_root_control/).when_present.select "OR"
        page.edit_loading.wait_while_present
        page.edit_tree_section.select(:id => /u\d+_node_\d+_parent_node_\d+_parent_node_0_parent_root_control/).when_present.select "OR"
        page.edit_loading.wait_while_present
        page.update_rule_btn
      end
    end
    return data_setup_needed
  end

  def rc_text_rule( group, node, text)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Free Form Text/
      page.free_text_field.when_present.set text
      page.preview_btn
    end
  end

  def rc_repeated_credit_rule( group, node, number)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /May be repeated for a maximum of <n> credits/
      page.integer_field.when_present.set number
      page.preview_btn
    end
  end
end

class RestrictCreditRule < CORequisitesData
  include Foundry
  include DataFactory

  attr_accessor :submit_btn,
                :section

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :section => "Course that Restricts Credits",
        :term => "201208",
        :course => "ENGL101"
    }

    options = defaults.merge(opts)

    set_options(options)
  end

  def edit_add_restrict( edit_or_add)
    begin
      open_agenda_section
      on CourseOfferingRequisites do |page|
        if edit_or_add == "add"
          page.restrict_add
        else
          page.restrict_edit
        end
      end
    rescue Watir::Wait::TimeoutError
      #means Data setup was not needed
      on CourseOfferingRequisites do |page|
        page.alert.ok
      end
    end
  end

  def crc_data_setup
    navigate_to_mco_requisites
    on CourseOfferingRequisites do |page|
      page.loading.wait_while_present
      if page.restrict_edit_link.exists?
        page.restrict_edit
      else
        page.restrict_add
      end
      page.loading.wait_while_present
      if crc_create_rule_tree == true
        commit_changes( true)
      end
    end
  end

  def crc_create_rule_tree
    groups = check_number_groups
    statements = check_number_statements
    data_setup_needed = false
    if statements == 0
      crc_course_rule( "add", "", "BSCI202")
      statements+=1
      data_setup_needed = true
    end
    if statements == 1
      crc_course_rule( "add", "A", "HIST639")
      statements+=1
      data_setup_needed = true
    end
    if statements == 2
      crc_text_rule( "add", "B", "ENGL101")
      statements+=1
      data_setup_needed = true
    end
    if groups == 0 && statements == 3
      crc_text_rule( "group", "A", "free form text input value")
      groups+=1
      crc_all_courses_rule( "add", "", "ENGL478,HIST416", "", "")
      statements+=1
      data_setup_needed = true
    else
      if groups == 0
        crc_text_rule( "group", "A", "free form text input value")
        groups+=1
        data_setup_needed = true
      end
      if statements == 3
        crc_all_courses_rule( "add", "B", "ENGL478,HIST416", "", "")
        statements+=1
        data_setup_needed = true
      end
    end
    if groups == 1
      crc_text_rule( "group", "D", "Text")
      groups += 1
      data_setup_needed = true
    end
    if statements == 4
      crc_all_courses_rule( "add", "C", "HIST395,HIST210", "", "")
      statements += 1
      data_setup_needed = true
    end
    if groups >= 2
      on ManageCORequisites do |page|
        page.loading.wait_while_present
        page.edit_tree_section.select(:id => /u\d+_node_\d+_parent_node_0_parent_root_control/).when_present.select "OR"
        page.edit_loading.wait_while_present
        page.edit_tree_section.select(:id => /u\d+_node_\d+_parent_node_\d+_parent_node_0_parent_root_control/).when_present.select "OR"
        page.edit_loading.wait_while_present
        page.update_rule_btn
      end
    end
    return data_setup_needed
  end

  def crc_course_rule( group, node, course)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must not have successfully completed <course>/
      advanced_search("course code", course)
      page.preview_btn
    end
  end

  def crc_text_rule( group, node, text)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Free Form Text/
      page.free_text_field.when_present.set text
      page.preview_btn
    end
  end

  def crc_all_courses_rule( group, node, course, set, range)
    add_new_node( group, node)
    on ManageCORequisites do |page|
      page.rule_dropdown.when_present.select /Must not have successfully completed any courses from <courses>/
      add_courses( course, set, range)
      page.preview_btn
    end
  end
end