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

class AORequisitesData
  include Foundry
  include DataFactory

  attr_accessor :submit_btn,
                :section,
                :activity

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :section => "Student Eligibility & Prerequisite",
        :activity => "A"
    }

    options = defaults.merge(opts)

    set_options(options)
  end

  def navigate_to_ao_requisites
    @course_offering = make CourseOffering, {:course => @course, :term => @term}
    @course_offering.manage
    on ManageCourseOfferings do |page|
      page.loading.wait_while_present(200)
      page.ao_requisites(@activity)
    end
    on ActivityOfferingRequisites do |page|
      page.loading.wait_while_present
      open_agenda_section
    end
  end

  def commit_changes( return_to_edit_page = false )
    begin
      on ManageAORequisites do |page|
        page.loading.wait_while_present
        page.update_rule_btn
      end
    rescue Watir::Wait::TimeoutError
      #Means update rule button already clicked
    end
    on ActivityOfferingRequisites do |page|
      page.loading.wait_while_present
      page.submit
      page.loading.wait_while_present(200)
    end
    if return_to_edit_page == true
      on ManageCourseOfferings do |page|
        page.ao_requisites(@activity)
      end
    end
  end

  def open_agenda_section
    sections = {"Student Eligibility & Prerequisite"=>:eligibility_prereq_section_link,
                "Antirequisite"=>:antirequisite_section_link, "Corequisite"=>:corequisite_section_link,
                "Recommended Preparation"=>:recommended_prep_section_link,
                "Repeatable for Credit"=>:repeatable_credit_section_link,
                "Course that Restricts Credits"=>:restricted_credit_section_link}
    begin
      on ActivityOfferingRequisites do |page|
        page.loading.wait_while_present(60)
        if page.send(sections[@section]).img(id: /KSAO-AgendaManage-RulePrototype_rule[A-F]_toggle_col/).present?
          page.send(sections[@section]).when_present.click
        end
      end
    rescue Watir::Wait::TimeoutError
      #Means agenda section is already open
      on ActivityOfferingRequisites do |page|
        page.alert.ok
      end
    end
  end

  def advanced_search(field, code)
    on ManageAORequisites do |page|
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
      row = page.lookup_results.table.row(:text => /\b#{Regexp.escape(code)}\b/i)
      row.a(:text => "Select").when_present.click

      ##############################################################
      ### TODO adding if statement due to tabs switching when AFT runs, will have to investigate if this is a bug
      if page.object_tab.parent.attribute_value('class') !~ /ui-tabs-active/
        page.object_tab.when_present.click
        page.edit_loading.wait_while_present
      end
      ##############################################################
    end
  end

  def click_search_link( regex)
    on ManageAORequisites do |page|
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
    on ManageAORequisites do |page|
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
    on ManageAORequisites do |page|
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
    on ManageAORequisites do |page|
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
      #if range != "" && range != nil         ###FIX##################################
      #  page.multi_course_dropdown.when_present.select /Course Ranges/
      #  advanced_search("course range", range)
      #  page.add_line_btn
      #  page.adding.wait_while_present
      #end                              ######################################
    end
  end

  def add_program( program)
    on ManageAORequisites do |page|
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
    on ManageAORequisites do |page|
      advanced_search("class standing", standing)
      page.edit_loading.wait_while_present
    end
  end

  def add_org( org)
    on ManageAORequisites do |page|
      advanced_search("org", org)
      page.edit_loading.wait_while_present
    end
  end

  def choose_grade_type_grade( grade, type)
    types = { /completed notation/i=>:completed, "Letter"=>:letter, "Percentage"=>:percentage,
              "Pass/Fail"=>:pass_fail, "Administrative Grade"=>:grade}
    on ManageAORequisites do |page|
      page.send(types[type])
      page.edit_loading.wait_while_present
      page.grade_dropdown.when_present.select grade
    end
  end

  def edit_existing_node(node, field, code)
    on ManageAORequisites do |page|
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
    on ManageAORequisites do |page|
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
    on ManageAORequisites do |page|
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
      on ManageAORequisites do |page|
        page.preview_tree_section.links.each do |link|
          if link.text == "Show Courses"
            link.click
          end
        end
      end
    elsif section == "agenda"
      on ActivityOfferingRequisites do |page|
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
    string = ""
    array.each do |elem|
      string += ".*" + Regexp.escape(elem) + "\n.*" + Regexp.escape(elem)
      i += 1
      if i < array.size
        string += "\n[ANDOR]+\n[ANDOR]+\n"
      end
    end
    return Regexp.new(string, Regexp::MULTILINE)
  end

  def test_ao_compare_text(text)
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
    string = ""
    array.each do |elem|
      string += ".*" + Regexp.escape(elem) + "\n.*" + Regexp.escape(elem) + "\n.*" + Regexp.escape(elem)
      i += 1
      if i < array.size
        string += "\n[ANDOR]+\n[ANDOR]+\n[ANDOR]+\n"
      end
    end
    return Regexp.new(string, Regexp::MULTILINE)
  end

  def change_operator(node, operator)
    on ManageAORequisites do |page|
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
    if level == "primary"
      test = /u\d+_node_\d_parent_node_0_parent_root_span/
    elsif level == "secondary"
      test = /u\d+_node_\d_parent_node_\d_parent_node_0_parent_root_span/
    elsif level == "tertiary"
      test = /u\d+_node_\d_parent_node_\d_parent_node_\d_parent_node_0_parent_root_span/
    end
    return Regexp.new(test)
  end

  def select_node( node)
    on ManageAORequisites do |page|
      if node != "" && node != nil && page.edit_tree_section.span(:text => /.*#{Regexp.escape(node)}\..*/).exists?
        if page.edit_tree_section.html =~ /li.+class.+ruleBlockSelected/
          selection = page.edit_tree_section.li(:class => /ruleBlockSelected/).text
          if selection !~ /.*#{Regexp.escape(node)}\..*/
            page.edit_tree_section.span(:text => /.*#{Regexp.escape(node)}\..*/).when_present.click
          end
        else
          page.edit_tree_section.span(:text => /.*#{Regexp.escape(node)}\..*/).when_present.click
        end
      end
    end
  end

  def move_around( node, direction)
    select_node( node)
    on ManageAORequisites do |page|
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
      elsif direction == "up in"
        page.up_btn
        page.right_btn
      elsif direction == "out up in"
        page.left_btn
        page.up_btn
        page.right_btn
      end
    end
  end

  def copy_cut_paste( node, node_after, action)
    on ManageAORequisites do |page|
      select_node( node)
      if action == "copy"
        page.copy_btn
      elsif action == "cut"
        page.cut_btn
      end
      select_node( node_after)
      page.paste_btn
    end
  end

  def copy_cut_paste_group( node, node_after, action)
    on ManageAORequisites do |page|
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

  def delete_statement( node)
    on ManageAORequisites do |page|
      select_node( node)
      page.del_btn
      page.edit_loading.wait_while_present
    end
  end

  def delete_group( node)
    on ManageAORequisites do |page|
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

  def make_changes_to_multiple_ao_reqs
    @prereq = make AOPreparationPrerequisiteRule, :course => "CHEM277"
    @prereq.sepr_copy_edit_co_rule_for_copy
    commit_changes

    @coreq = make AOCorequisiteRule, :course => "CHEM277"
    @coreq.cr_suppress_co_rule
    commit_changes

    @antireq = make AOAntirequisiteRule, :course => "CHEM277", :activity => "D"
    @antireq.ar_add_ao_rule_for_copy
    commit_changes

    @coreq2 = make AOCorequisiteRule, :course => "CHEM277", :activity => "D"
    @coreq2.cr_replace_co_rule_for_copy
    commit_changes
  end
end

class AOAntirequisiteRule < AORequisitesData
  include Foundry
  include DataFactory

  attr_accessor :submit_btn,
                :section

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :section => "Antirequisite",
        :term => "201208",
        :course => "ENGL304",
        :activity => "A"
    }

    options = defaults.merge(opts)

    set_options(options)
  end

  def ar_copy_edit_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.antireq_copy_edit_link.exists?
        page.loading.wait_while_present
        page.antireq_copy_edit
        ar_add_statements_to_rule
      end
    end
  end

  def ar_copy_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.antireq_copy_edit_link.exists?
        page.loading.wait_while_present
        page.antireq_copy_edit
        on ManageAORequisites do |inner_page|
          inner_page.update_rule_btn
        end
      end
    end
  end

  def ar_revert_copy_co_rule
    ar_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.antireq_revert_link.exists?
        page.loading.wait_while_present
        page.antireq_revert
      end
    end
  end

  def ar_revert_copy_edit_co_rule
    ar_copy_edit_co_rule
    on ActivityOfferingRequisites do |page|
      if page.antireq_revert_link.exists?
        page.loading.wait_while_present
        page.antireq_revert
      end
    end
  end

  def ar_revert_add_ao_rule
    ar_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.antireq_revert_link.exists?
        page.loading.wait_while_present
        page.antireq_revert
      end
    end
  end

  def ar_add_ao_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.antireq_add_link.exists?
        page.loading.wait_while_present
        page.antireq_add
        ar_add_statements_to_rule
      end
    end
  end

  def ar_add_ao_rule_for_copy
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.antireq_add_link.exists?
        page.loading.wait_while_present
        page.antireq_add
        ar_text_rule( "add", "", "free form text input value")
      end
    end
  end

  def ar_replace_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.antireq_replace_link.exists?
        page.loading.wait_while_present
        page.antireq_replace
        ar_add_statements_to_rule
      end
    end
  end

  def ar_edit_replaced_co_rule
    ar_replace_co_rule
    commit_changes( true)
    on ActivityOfferingRequisites do |page|
      if page.antireq_edit_link.exists?
        page.loading.wait_while_present
        page.antireq_edit
      end
    end
  end

  def ar_edit_update_replaced_co_rule
    ar_replace_co_rule
    commit_changes( true)
    on ActivityOfferingRequisites do |page|
      if page.antireq_edit_link.exists?
        page.loading.wait_while_present
        page.antireq_edit
        edit_statements_in_rule
      end
    end
  end

  def ar_suppress_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.antireq_suppress_link.exists?
        page.loading.wait_while_present
        page.antireq_suppress
      end
    end
  end

  def ar_suppress_added_ao_rule
    ar_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.antireq_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.antireq_edit_suppress
      end
    end
  end

  def ar_suppress_copied_co_rule
    ar_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.antireq_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.antireq_edit_suppress
      end
    end
  end

  def ar_suppress_copied_edited_co_rule
    ar_copy_edit_co_rule
    on ActivityOfferingRequisites do |page|
      if page.antireq_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.antireq_edit_suppress
      end
    end
  end

  def ar_view_catalog_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.antireq_view_link.exists?
        page.loading.wait_while_present
        page.antireq_view
      end
    end
  end

  def ar_compare_catalog_co_ao_rule
    ar_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.antireq_compare_link.exists?
        page.loading.wait_while_present
        page.antireq_compare
      end
    end
  end

  def ar_compare_ao_to_clu_co_rule
    ar_replace_co_rule
    on ActivityOfferingRequisites do |page|
      if page.antireq_compare_link.exists?
        page.loading.wait_while_present
        page.antireq_compare
      end
    end
  end

  def ar_compare_new_ao_to_clu_co_rule
    ar_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.antireq_compare_link.exists?
        page.loading.wait_while_present
        page.antireq_compare
      end
    end
  end

  def ar_revert_replaced_co_rule
    ar_replace_co_rule
    on ActivityOfferingRequisites do |page|
      if page.antireq_revert_link.exists?
        page.loading.wait_while_present
        page.antireq_revert
      end
    end
  end

  def ar_add_statements_to_rule
    ar_course_rule( "add", "", "ENGL101")
    ar_text_rule( "add", "", "free form text input value")
    ar_all_courses_rule( "group", "A", "HIST416,ENGL478", "", "")
    ar_text_rule( "add", "", "Text to copy")
    ar_any_credits_rule( "group", "C", "BSCI202,BSCI361,HIST110", "", "")
  end

  def ar_course_rule( group, node, course)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must not have successfully completed <course>/
      advanced_search("course code", course)
      page.preview_btn
    end
  end

  def ar_text_rule( group, node, text)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Free Form Text/
      page.free_text_field.when_present.set text
      page.preview_btn
    end
  end

  def ar_all_courses_rule( group, node, course, set, range)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must not have successfully completed any courses from <courses>/
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def ar_any_credits_rule( group, node, course, set, range)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must not have successfully completed any credits from <courses>/
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def ar_grade_courses_rule( group, node, course, set, range, type, grade)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must not have earned a grade of <gradeType> <grade> or higher in <courses>/
      choose_grade_type_grade( grade, type)
      add_courses( course, set, range)
      page.preview_btn
    end
  end
end

class AOCorequisiteRule < AORequisitesData
  include Foundry
  include DataFactory

  attr_accessor :submit_btn,
                :section

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :section => "Corequisite",
        :term => "201208",
        :course => "ENGL304",
        :activity => "A"
    }

    options = defaults.merge(opts)

    set_options(options)
  end

  def cr_copy_edit_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.coreq_copy_edit_link.exists?
        page.loading.wait_while_present
        page.coreq_copy_edit
        cr_add_statements_to_rule
      end
    end
  end

  def cr_copy_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.coreq_copy_edit_link.exists?
        page.loading.wait_while_present
        page.coreq_copy_edit
        on ManageAORequisites do |inner_page|
          inner_page.update_rule_btn
        end
      end
    end
  end

  def cr_revert_copy_co_rule
    cr_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.coreq_revert_link.exists?
        page.loading.wait_while_present
        page.coreq_revert
      end
    end
  end

  def cr_revert_copy_edit_co_rule
    cr_copy_edit_co_rule
    on ActivityOfferingRequisites do |page|
      if page.coreq_revert_link.exists?
        page.loading.wait_while_present
        page.coreq_revert
      end
    end
  end

  def cr_revert_add_ao_rule
    cr_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.coreq_revert_link.exists?
        page.loading.wait_while_present
        page.coreq_revert
      end
    end
  end

  def cr_add_ao_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.coreq_add_link.exists?
        page.loading.wait_while_present
        page.coreq_add
        cr_add_statements_to_rule
      end
    end
  end

  def cr_replace_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.coreq_replace_link.exists?
        page.loading.wait_while_present
        page.coreq_replace
        cr_add_statements_to_rule
      end
    end
  end

  def cr_replace_co_rule_for_copy
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.coreq_replace_link.exists?
        page.loading.wait_while_present
        page.coreq_replace
        cr_number_courses_rule( "add", "C", "2", "BSCI202,BSCI361,HIST110", "", "")
      end
    end
  end

  def cr_edit_replaced_co_rule
    cr_replace_co_rule
    commit_changes( true)
    on ActivityOfferingRequisites do |page|
      if page.coreq_edit_link.exists?
        page.loading.wait_while_present
        page.coreq_edit
      end
    end
  end

  def cr_edit_update_replaced_co_rule
    cr_replace_co_rule
    commit_changes( true)
    on ActivityOfferingRequisites do |page|
      if page.coreq_edit_link.exists?
        page.loading.wait_while_present
        page.coreq_edit
        edit_statements_in_rule
      end
    end
  end

  def cr_suppress_added_ao_rule
    cr_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.coreq_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.coreq_edit_suppress
      end
    end
  end

  def cr_suppress_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.coreq_suppress_link.exists?
        page.loading.wait_while_present
        page.coreq_suppress
      end
    end
  end

  def cr_suppress_copied_co_rule
    cr_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.coreq_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.coreq_edit_suppress
      end
    end
  end

  def cr_suppress_copied_edited_co_rule
    cr_copy_edit_co_rule
    on ActivityOfferingRequisites do |page|
      if page.coreq_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.coreq_edit_suppress
      end
    end
  end

  def cr_view_catalog_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.coreq_view_link.exists?
        page.loading.wait_while_present
        page.coreq_view
      end
    end
  end

  def cr_compare_catalog_co_ao_rule
    cr_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.coreq_compare_link.exists?
        page.loading.wait_while_present
        page.coreq_compare
      end
    end
  end

  def cr_compare_ao_to_clu_co_rule
    cr_replace_co_rule
    on ActivityOfferingRequisites do |page|
      if page.coreq_compare_link.exists?
        page.loading.wait_while_present
        page.coreq_compare
      end
    end
  end

  def cr_compare_new_ao_to_clu_co_rule
    cr_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.coreq_compare_link.exists?
        page.loading.wait_while_present
        page.coreq_compare
      end
    end
  end

  def cr_revert_replaced_co_rule
    cr_replace_co_rule
    on ActivityOfferingRequisites do |page|
      if page.coreq_revert_link.exists?
        page.loading.wait_while_present
        page.coreq_revert
      end
    end
  end

  def cr_add_statements_to_rule
    cr_course_rule( "add", "", "ENGL101")
    cr_text_rule( "add", "", "free form text input value")
    cr_all_courses_rule( "group", "A", "HIST416,ENGL478", "", "")
    cr_text_rule( "add", "", "Text to copy")
    cr_number_courses_rule( "group", "C", "2", "BSCI202,BSCI361,HIST110", "", "")
  end

  def cr_course_rule( group, node, course)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must be concurrently enrolled in <course>/
      advanced_search("course code", course)
      page.preview_btn
    end
  end

  def cr_text_rule( group, node, text)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Free Form Text/
      page.free_text_field.when_present.set text
      page.preview_btn
    end
  end

  def cr_all_courses_rule( group, node, course, set, range)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must be concurrently enrolled in all courses from <courses>/
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def cr_number_courses_rule( group, node, number, course, set, range)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must be concurrently enrolled in a minimum of <n> courses from <courses>/
      page.integer_field.when_present.set number
      add_courses( course, set, range)
      page.preview_btn
    end
  end
end

class AOPreparationPrerequisiteRule < AORequisitesData
  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :section => "Student Eligibility & Prerequisite",
        :term => "201208",
        :course => "ENGL301",
        :activity => "A"
    }

    options = defaults.merge(opts)

    set_options(options)
  end

  def sepr_copy_edit_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.prereq_copy_edit_link.exists?
        page.loading.wait_while_present
        page.prereq_copy_edit
        add_statements_to_rule
      end
    end
  end

  def rp_copy_edit_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.prep_copy_edit_link.exists?
        page.loading.wait_while_present
        page.prep_copy_edit
        add_statements_to_rule
      end
    end
  end

  def sepr_copy_edit_co_rule_for_copy
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.prereq_copy_edit_link.exists?
        page.loading.wait_while_present
        page.prereq_copy_edit
        rp_sepr_course_rule( "add", "", "ENGL101")
      end
    end
  end

  def rp_copy_edit_co_rule_for_copy
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.prep_copy_edit_link.exists?
        page.loading.wait_while_present
        page.prep_copy_edit
        rp_sepr_course_rule( "add", "", "ENGL101")
      end
    end
  end

  def sepr_copy_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.prereq_copy_edit_link.exists?
        page.loading.wait_while_present
        page.prereq_copy_edit
        on ManageAORequisites do |inner_page|
          inner_page.update_rule_btn
        end
      end
    end
  end

  def rp_copy_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.prep_copy_edit_link.exists?
        page.loading.wait_while_present
        page.prep_copy_edit
        on ManageAORequisites do |inner_page|
          inner_page.update_rule_btn
        end
      end
    end
  end

  def sepr_revert_copy_co_rule
    sepr_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.prereq_revert_link.exists?
        page.loading.wait_while_present
        page.prereq_revert
      end
    end
  end

  def rp_revert_copy_co_rule
    rp_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.prep_revert_link.exists?
        page.loading.wait_while_present
        page.prep_revert
      end
    end
  end

  def sepr_revert_copy_edit_co_rule
    sepr_copy_edit_co_rule
    on ActivityOfferingRequisites do |page|
      if page.prereq_revert_link.exists?
        page.loading.wait_while_present
        page.prereq_revert
      end
    end
  end

  def rp_revert_copy_edit_co_rule
    rp_copy_edit_co_rule
    on ActivityOfferingRequisites do |page|
      if page.prep_revert_link.exists?
        page.loading.wait_while_present
        page.prep_revert
      end
    end
  end

  def sepr_revert_add_ao_rule
    sepr_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.prereq_revert_link.exists?
        page.loading.wait_while_present
        page.prereq_revert
      end
    end
  end

  def rp_revert_add_ao_rule
    rp_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.prep_revert_link.exists?
        page.loading.wait_while_present
        page.prep_revert
      end
    end
  end

  def sepr_add_ao_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.prereq_add_link.exists?
        page.loading.wait_while_present
        page.prereq_add
        add_statements_to_rule
      end
    end
  end

  def rp_add_ao_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.prep_add_link.exists?
        page.loading.wait_while_present
        page.prep_add
        add_statements_to_rule
      end
    end
  end

  def sepr_replace_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.prereq_replace_link.exists?
        page.loading.wait_while_present
        page.prereq_replace
        add_statements_to_rule
      end
    end
  end

  def rp_replace_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.prep_replace_link.exists?
        page.loading.wait_while_present
        page.prep_replace
        add_statements_to_rule
      end
    end
  end

  def sepr_edit_replaced_co_rule
    sepr_replace_co_rule
    commit_changes( true)
    on ActivityOfferingRequisites do |page|
      if page.prereq_edit_link.exists?
        page.loading.wait_while_present
        page.prereq_edit
      end
    end
  end

  def rp_edit_replaced_co_rule
    rp_replace_co_rule
    commit_changes( true)
    on ActivityOfferingRequisites do |page|
      if page.prep_edit_link.exists?
        page.loading.wait_while_present
        page.prep_edit
      end
    end
  end

  def sepr_edit_update_replaced_co_rule
    sepr_replace_co_rule
    commit_changes( true)
    on ActivityOfferingRequisites do |page|
      if page.prereq_edit_link.exists?
        page.loading.wait_while_present
        page.prereq_edit
        edit_statements_in_rule
      end
    end
  end

  def rp_edit_update_replaced_co_rule
    rp_replace_co_rule
    commit_changes( true)
    on ActivityOfferingRequisites do |page|
      if page.prep_edit_link.exists?
        page.loading.wait_while_present
        page.prep_edit
        edit_statements_in_rule
      end
    end
  end

  def sepr_suppress_added_ao_rule
    sepr_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.prereq_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.prereq_edit_suppress
      end
    end
  end

  def rp_suppress_added_ao_rule
    rp_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.prep_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.prep_edit_suppress
      end
    end
  end

  def sepr_suppress_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.prereq_suppress_link.exists?
        page.loading.wait_while_present
        page.prereq_suppress
      end
    end
  end

  def rp_suppress_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.prep_suppress_link.exists?
        page.loading.wait_while_present
        page.prep_suppress
      end
    end
  end

  def sepr_suppress_copied_co_rule
    sepr_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.prereq_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.prereq_edit_suppress
      end
    end
  end

  def rp_suppress_copied_co_rule
    rp_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.prep_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.prep_edit_suppress
      end
    end
  end

  def sepr_suppress_copied_edited_co_rule
    sepr_copy_edit_co_rule
    on ActivityOfferingRequisites do |page|
      if page.prereq_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.prereq_edit_suppress
      end
    end
  end

  def rp_suppress_copied_edited_co_rule
    rp_copy_edit_co_rule
    on ActivityOfferingRequisites do |page|
      if page.prep_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.prep_edit_suppress
      end
    end
  end

  def sepr_suppress_committed_copied_edited_co_rule
    sepr_copy_edit_co_rule
    commit_changes( true)
    on ActivityOfferingRequisites do |page|
      if page.prereq_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.prereq_edit_suppress
      end
    end
  end

  def rp_suppress_committed_copied_edited_co_rule
    rp_copy_edit_co_rule
    commit_changes( true)
    on ActivityOfferingRequisites do |page|
      if page.prep_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.prep_edit_suppress
      end
    end
  end

  def sepr_view_catalog_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.prereq_view_link.exists?
        page.loading.wait_while_present
        page.prereq_view
      end
    end
  end

  def rp_view_catalog_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.prep_view_link.exists?
        page.loading.wait_while_present
        page.prep_view
      end
    end
  end

  def sepr_compare_catalog_co_ao_rule
    sepr_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.prereq_compare_link.exists?
        page.loading.wait_while_present
        page.prereq_compare
      end
    end
  end

  def rp_compare_catalog_co_ao_rule
    rp_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.prep_compare_link.exists?
        page.loading.wait_while_present
        page.prep_compare
      end
    end
  end

  def sepr_compare_ao_to_clu_co_rule
    sepr_replace_co_rule
    on ActivityOfferingRequisites do |page|
      if page.prereq_compare_link.exists?
        page.loading.wait_while_present
        page.prereq_compare
      end
    end
  end

  def rp_compare_ao_to_clu_co_rule
    rp_replace_co_rule
    on ActivityOfferingRequisites do |page|
      if page.prep_compare_link.exists?
        page.loading.wait_while_present
        page.prep_compare
      end
    end
  end

  def sepr_compare_new_ao_to_clu_co_rule
    sepr_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.prereq_compare_link.exists?
        page.loading.wait_while_present
        page.prereq_compare
      end
    end
  end

  def rp_compare_new_ao_to_clu_co_rule
    rp_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.prep_compare_link.exists?
        page.loading.wait_while_present
        page.prep_compare
      end
    end
  end

  def sepr_revert_replaced_co_rule
    sepr_replace_co_rule
    on ActivityOfferingRequisites do |page|
      if page.prereq_revert_link.exists?
        page.loading.wait_while_present
        page.prereq_revert
      end
    end
  end

  def rp_revert_replaced_co_rule
    rp_replace_co_rule
    on ActivityOfferingRequisites do |page|
      if page.prep_revert_link.exists?
        page.loading.wait_while_present
        page.prep_revert
      end
    end
  end

  #def sepr_modify_copied_co_rule
  #  navigate_to_ao_requisites
  #  on ActivityOfferingRequisites do |page|
  #    if page.prereq_copy_edit_link.exists?
  #      page.loading.wait_while_present
  #      page.prereq_copy_edit
  #      add_statements_to_rule
  #    end
  #  end
  #end

  def add_statements_to_rule
    rp_sepr_course_rule( "add", "", "ENGL101")
    rp_sepr_text_rule( "add", "", "free form text input value")
    rp_sepr_all_courses_rule( "group", "A", "HIST416,ENGL478", "", "")
    rp_sepr_text_rule( "add", "", "Text to copy")
    rp_sepr_number_courses_rule( "group", "C", "2", "BSCI202,BSCI361,HIST110", "", "")
    on ManageAORequisites do |page|
      page.update_rule_btn
    end
  end

  def edit_statements_in_rule
    delete_statement( "E")
    delete_group( "B")
    edit_existing_node( "A", "course", "ENGL313")
    rp_sepr_text_rule( "add", "", "Text added while editing")
    on ManageAORequisites do |page|
      page.update_rule_btn
    end
  end

  def rp_sepr_course_rule( group, node, course)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must have successfully completed <course>/
      advanced_search("course code", course)
      page.preview_btn
    end
  end

  def rp_sepr_text_rule( group, node, text)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Free Form Text/
      page.free_text_field.when_present.set text
      page.preview_btn
    end
  end

  def rp_sepr_all_courses_rule( group, node, course, set, range)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must have successfully completed all courses from <courses>/
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def rp_sepr_number_courses_rule( group, node, number, course, set, range)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must have successfully completed a minimum of <n> courses from <courses>/
      page.integer_field.when_present.set number
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def rp_sepr_less_number_courses_rule( group, node, number, course, set, range)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must have successfully completed no more than <n> courses from <courses>/
      page.integer_field.when_present.set number
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def rp_sepr_less_credits_rule( group, node, number, course, set, range, equal)
    add_new_node( group, node)
    on ManageAORequisites do |page|
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
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must have earned a minimum grade of <gradeType> <grade> in <courses>/
      choose_grade_type_grade( grade, type)
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def rp_sepr_higher_grade_courses_rule( group, node, course, set, range, type, grade)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must not have earned a grade of <gradeType> <grade> or higher in <courses>/
      choose_grade_type_grade( grade, type)
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def rp_sepr_grade_number_courses_rule( group, node, course, set, range, type, grade, number)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must successfully complete a minimum of <n> courses from <courses> with a minimum grade of <gradeType> <grade>/
      page.integer_field.when_present.set number
      add_courses( course, set, range)
      choose_grade_type_grade( grade, type)
      page.preview_btn
    end
  end

  def prereq_gpa_courses_rule( group, node, course, set, range, gpa)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must have earned a minimum GPA of <GPA> in <courses>/
      page.integer_field.when_present.set gpa
      add_courses( course, set, range)
      page.preview_btn
    end
  end

  def rp_sepr_gpa_duration_rule( group, node, gpa, type, duration)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must have earned a minimum cumulative GPA of <GPA> in <duration><durationType>/
      page.integer_field.when_present.set gpa
      page.duration_field.when_present.set duration
      page.duration_dropdown.when_present.select type
      page.preview_btn
    end
  end

  def rp_sepr_gpa_rule( group, node, gpa)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must have earned a minimum cumulative GPA of <GPA>/
      page.integer_field.when_present.set gpa
      page.preview_btn
    end
  end

  def rp_sepr_program_rule( group, node, program)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must have been admitted to the <program> program/
      add_program( program)
      page.preview_btn
    end
  end

  def rp_sepr_not_program_rule( group, node, program)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must not have been admitted to the <program> program/
      add_program( program)
      page.preview_btn
    end
  end

  def rp_sepr_any_program_rule( group, node)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must be admitted to any program offered at the course campus location/
      page.edit_loading.wait_while_present
      page.preview_btn
    end
  end

  def rp_sepr_permission_instructor_rule( group, node)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Permission of instructor required/
      page.edit_loading.wait_while_present
      page.preview_btn
    end
  end

  def rp_sepr_course_term_rule( group, node, course, term, prior_or_as = "as of")
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must have successfully completed <course> #{Regexp.escape(prior_or_as)} <term>/
      advanced_search("course code", course)
      page.term_field.set term
      page.preview_btn
    end
  end

  def rp_sepr_course_between_terms_rule( group, node, course, term1, term2)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must have successfully completed <course> between <term1> and <term2>/
      advanced_search("course code", course)
      page.term_field.set term1
      page.term_two_field.set term2
      page.preview_btn
    end
  end

  def rp_sepr_program_org_rule( group, node, org)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must have been admitted to a program offered by <org>/
      add_org(org)
      page.preview_btn
    end
  end

  def rp_sepr_admin_org_rule( group, node, org)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Permission of <administering org> required/
      add_org(org)
      page.preview_btn
    end
  end

  def rp_sepr_min_credits_org_rule( group, node, org, credit)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must have successfully completed a minimum of <n> credits from courses in the <org>/
      page.integer_field.when_present.set credit
      add_org(org)
      page.preview_btn
    end
  end

  def rp_sepr_min_total_credits_rule( group, node, credit)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must have earned a minimum of <n> total credits/
      page.integer_field.when_present.set credit
      page.preview_btn
    end
  end

  def rp_sepr_population_rule( group, node, pop)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Student must be a member of <population>/
      advanced_search("population", pop)
      page.preview_btn
    end
  end

  def rp_sepr_program_org_duration_rule( group, node, program, integer, org, duration, type)
    add_new_node( group, node)
    on ManageAORequisites do |page|
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
    on ManageAORequisites do |page|
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

class AORepeatCreditRule < AORequisitesData
  include Foundry
  include DataFactory

  attr_accessor :submit_btn,
                :section

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :section => "Repeatable for Credit",
        :term => "201208",
        :course => "ENGL304",
        :activity => "A"
    }

    options = defaults.merge(opts)

    set_options(options)
  end

  def rc_copy_edit_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.repeat_copy_edit_link.exists?
        page.loading.wait_while_present
        page.repeat_copy_edit
        rc_add_statements_to_rule
      end
    end
  end

  def rc_copy_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.repeat_copy_edit_link.exists?
        page.loading.wait_while_present
        page.repeat_copy_edit
        on ManageAORequisites do |inner_page|
          inner_page.update_rule_btn
        end
      end
    end
  end

  def rc_revert_copy_co_rule
    rc_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.repeat_revert_link.exists?
        page.loading.wait_while_present
        page.repeat_revert
      end
    end
  end

  def rc_revert_copy_edit_co_rule
    rc_copy_edit_co_rule
    on ActivityOfferingRequisites do |page|
      if page.repeat_revert_link.exists?
        page.loading.wait_while_present
        page.repeat_revert
      end
    end
  end

  def rc_revert_add_ao_rule
    rc_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.repeat_revert_link.exists?
        page.loading.wait_while_present
        page.repeat_revert
      end
    end
  end

  def rc_add_ao_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.repeat_add_link.exists?
        page.loading.wait_while_present
        page.repeat_add
        rc_add_statements_to_rule
      end
    end
  end

  def rc_replace_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.repeat_replace_link.exists?
        page.loading.wait_while_present
        page.repeat_replace
        rc_add_statements_to_rule
      end
    end
  end

  def rc_edit_replaced_co_rule
    rc_replace_co_rule
    commit_changes( true)
    on ActivityOfferingRequisites do |page|
      if page.repeat_edit_link.exists?
        page.loading.wait_while_present
        page.repeat_edit
      end
    end
  end

  def rc_edit_update_replaced_co_rule
    rc_replace_co_rule
    commit_changes( true)
    on ActivityOfferingRequisites do |page|
      if page.repeat_edit_link.exists?
        page.loading.wait_while_present
        page.repeat_edit
        edit_statements_in_rule
      end
    end
  end

  def rc_suppress_added_ao_rule
    rc_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.repeat_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.repeat_edit_suppress
      end
    end
  end

  def rc_suppress_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.repeat_suppress_link.exists?
        page.loading.wait_while_present
        page.repeat_suppress
      end
    end
  end

  def rc_suppress_copied_co_rule
    rc_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.repeat_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.repeat_edit_suppress
      end
    end
  end

  def rc_suppress_copied_edited_co_rule
    rc_copy_edit_co_rule
    on ActivityOfferingRequisites do |page|
      if page.repeat_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.repeat_edit_suppress
      end
    end
  end

  def rc_view_catalog_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.repeat_view_link.exists?
        page.loading.wait_while_present
        page.repeat_view
      end
    end
  end

  def rc_compare_catalog_co_ao_rule
    rc_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.repeat_compare_link.exists?
        page.loading.wait_while_present
        page.repeat_compare
      end
    end
  end

  def rc_compare_ao_to_clu_co_rule
    rc_replace_co_rule
    on ActivityOfferingRequisites do |page|
      if page.repeat_compare_link.exists?
        page.loading.wait_while_present
        page.repeat_compare
      end
    end
  end

  def rc_compare_new_ao_to_clu_co_rule
    rc_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.repeat_compare_link.exists?
        page.loading.wait_while_present
        page.repeat_compare
      end
    end
  end

  def rc_revert_replaced_co_rule
    rc_replace_co_rule
    on ActivityOfferingRequisites do |page|
      if page.repeat_revert_link.exists?
        page.loading.wait_while_present
        page.repeat_revert
      end
    end
  end

  def rc_add_statements_to_rule
    rc_text_rule( "add", "", "ENGL101")
    rc_text_rule( "add", "", "free form text input value")
    rc_repeated_credit_rule( "group", "A", "16")
    rc_text_rule( "add", "", "Text to copy")
    rc_text_rule( "group", "C", "BSCI202,BSCI361,HIST110")
  end

  def rc_text_rule( group, node, text)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Free Form Text/
      page.free_text_field.when_present.set text
      page.preview_btn
    end
  end

  def rc_repeated_credit_rule( group, node, number)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /May be repeated for a maximum of <n> credits/
      page.integer_field.when_present.set number
      page.preview_btn
    end
  end
end

class AORestrictCreditRule < AORequisitesData
  include Foundry
  include DataFactory

  attr_accessor :submit_btn,
                :section

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :section => "Course that Restricts Credits",
        :term => "201208",
        :course => "ENGL304",
        :activity => "A"
    }

    options = defaults.merge(opts)

    set_options(options)
  end

  def crc_copy_edit_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.restrict_copy_edit_link.exists?
        page.loading.wait_while_present
        page.restrict_copy_edit
        crc_add_statements_to_rule
      end
    end
  end

  def crc_copy_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.restrict_copy_edit_link.exists?
        page.loading.wait_while_present
        page.restrict_copy_edit
        on ManageAORequisites do |inner_page|
          inner_page.update_rule_btn
        end
      end
    end
  end

  def crc_revert_copy_co_rule
    crc_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.restrict_revert_link.exists?
        page.loading.wait_while_present
        page.restrict_revert
      end
    end
  end

  def crc_revert_copy_edit_co_rule
    crc_copy_edit_co_rule
    on ActivityOfferingRequisites do |page|
      if page.restrict_revert_link.exists?
        page.loading.wait_while_present
        page.restrict_revert
      end
    end
  end

  def crc_revert_add_ao_rule
    crc_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.restrict_revert_link.exists?
        page.loading.wait_while_present
        page.restrict_revert
      end
    end
  end

  def crc_add_ao_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.restrict_add_link.exists?
        page.loading.wait_while_present
        page.restrict_add
        crc_add_statements_to_rule
      end
    end
  end

  def crc_replace_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.restrict_replace_link.exists?
        page.loading.wait_while_present
        page.restrict_replace
        crc_add_statements_to_rule
      end
    end
  end

  def crc_edit_replaced_co_rule
    crc_replace_co_rule
    commit_changes( true)
    on ActivityOfferingRequisites do |page|
      if page.restrict_edit_link.exists?
        page.loading.wait_while_present
        page.restrict_edit
      end
    end
  end

  def crc_edit_update_replaced_co_rule
    crc_replace_co_rule
    commit_changes( true)
    on ActivityOfferingRequisites do |page|
      if page.restrict_edit_link.exists?
        page.loading.wait_while_present
        page.restrict_edit
        edit_statements_in_rule
      end
    end
  end

  def crc_suppress_added_ao_rule
    crc_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.restrict_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.restrict_edit_suppress
      end
    end
  end

  def crc_suppress_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.restrict_suppress_link.exists?
        page.loading.wait_while_present
        page.restrict_suppress
      end
    end
  end

  def crc_suppress_copied_co_rule
    crc_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.restrict_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.restrict_edit_suppress
      end
    end
  end

  def crc_suppress_copied_edited_co_rule
    crc_copy_edit_co_rule
    on ActivityOfferingRequisites do |page|
      if page.restrict_edit_suppress_link.exists?
        page.loading.wait_while_present
        page.restrict_edit_suppress
      end
    end
  end

  def crc_view_catalog_co_rule
    navigate_to_ao_requisites
    on ActivityOfferingRequisites do |page|
      if page.restrict_view_link.exists?
        page.loading.wait_while_present
        page.restrict_view
      end
    end
  end

  def crc_compare_catalog_co_ao_rule
    crc_copy_co_rule
    on ActivityOfferingRequisites do |page|
      if page.restrict_compare_link.exists?
        page.loading.wait_while_present
        page.restrict_compare
      end
    end
  end

  def crc_compare_ao_to_clu_co_rule
    crc_replace_co_rule
    on ActivityOfferingRequisites do |page|
      if page.restrict_compare_link.exists?
        page.loading.wait_while_present
        page.restrict_compare
      end
    end
  end

  def crc_compare_new_ao_to_clu_co_rule
    crc_add_ao_rule
    on ActivityOfferingRequisites do |page|
      if page.restrict_compare_link.exists?
        page.loading.wait_while_present
        page.restrict_compare
      end
    end
  end

  def crc_revert_replaced_co_rule
    crc_replace_co_rule
    on ActivityOfferingRequisites do |page|
      if page.restrict_revert_link.exists?
        page.loading.wait_while_present
        page.restrict_revert
      end
    end
  end

  def crc_add_statements_to_rule
    crc_course_rule( "add", "", "ENGL101")
    crc_text_rule( "add", "", "free form text input value")
    crc_course_rule( "group", "A", "HIST111")
    crc_text_rule( "add", "", "Text to copy")
    crc_all_courses_rule( "group", "C", "BSCI202,BSCI361,HIST110", "", "")
  end

  def crc_course_rule( group, node, course)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must not have successfully completed <course>/
      advanced_search("course code", course)
      page.preview_btn
    end
  end

  def crc_text_rule( group, node, text)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Free Form Text/
      page.free_text_field.when_present.set text
      page.preview_btn
    end
  end

  def crc_all_courses_rule( group, node, course, set, range)
    add_new_node( group, node)
    on ManageAORequisites do |page|
      page.rule_dropdown.when_present.select /Must not have successfully completed any courses from <courses>/
      add_courses( course, set, range)
      page.preview_btn
    end
  end
end