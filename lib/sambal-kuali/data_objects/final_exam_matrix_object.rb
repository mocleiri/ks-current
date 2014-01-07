class FinalExamMatrix

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  attr_accessor :term_type, :courses, :days, :start_time,
                :end_time, :time_ampm, :free_text, :rdl_days,
                :rule_requirements, :exam_type, :facility,
                :room

  def initialize(browser,opts = {})
    @browser = browser

    defaults = {
      :term_type => "Fall Term",
      :exam_type => "Standard",
      :rule_requirements => "TH",
      :rule => "If Course meets on <timeslot>",
      :days => "MWF",
      :start_time => "05:00",
      :end_time => "06:00",
      :time_ampm => "pm",
      :free_text => "Free Form Text",
      :courses => "ENGL101",
      :facility => "MTH",
      :room => "0304",
      :rdl_days => "Day 1",
      :courses_type => "Approved Courses",
      :add_more_statements => false,
      :defer_save => false,
      :defer_submit => false
    }

    options = defaults.merge(opts)
    set_options(options)
  end

  def create
    manage

    if @exam_type == "Standard"
      on(FEMatrixView).add_standard_fe_rule
    else
      on(FEMatrixView).add_common_fe_rule
    end

    on FEMatrixEdit do |page|
      page.add_statement
      page.rule_dropdown.select @rule

      if @exam_type == "Standard"
        opts = {:rule => @rule,
                :days => @days,
                :start_time => @start_time,
                :end_time => @end_time,
                :time_ampm => @time_ampm,
                :free_text => @free_text}
        set_standard_rule opts
      else
        opts = {:rule => @rule,
                :courses => @courses,
                :courses_type => @courses_type,
                :free_text => @free_text}
        set_common_rule opts
      end

      page.preview_change
      page.loading.wait_while_present

      page.rdl_days.select @rdl_days
      page.rdl_starttime.set @start_time
      page.rdl_starttime_ampm.select @time_ampm
      page.rdl_endtime.set @end_time
      page.rdl_endtime_ampm.select @time_ampm

      page.update_rule unless @defer_save == true
      page.loading.wait_while_present
    end

    on FEMatrixView do |page|
      page.submit unless @defer_submit == true
      page.loading.wait_while_present
    end
  end

  def edit opts = {}
    defaults = {
        :exam_type => @exam_type,
        :rule_requirements => @rule_requirements,
        :add_statement => false,
        :edit_statement => false,
        :rule => @rule,
        :days => @days,
        :start_time => @start_time,
        :end_time => @end_time,
        :time_ampm => @time_ampm,
        :free_text => @free_text,
        :defer_save => @defer_save,
        :navigate_to_page => true,
        :defer_submit => @defer_submit
    }
    options = defaults.merge(opts)

    manage unless options[:navigate_to_page] == false
    on(FEMatrixView).edit options[:rule_requirements], options[:exam_type]

    if options[:add_statement] == true
      add_statement options
    end

    if options[:edit_statement] == true
      edit_statement options
    end

    if options[:defer_save] == false
      on(FEMatrixEdit).update_rule
    end

    if options[:defer_submit] == false
      on FEMatrixView do |page|
        page.submit
        page.loading.wait_while_present
      end
    end

    set_options(options)
  end

  def add_statement options
    on FEMatrixEdit do |page|
      page.add_statement
      page.loading.wait_while_present
      page.rule_dropdown.select options[:rule]

      if options[:exam_type] == "Standard"
        set_standard_rule options
      else
        set_common_rule options
      end

      page.preview_change
      page.loading.wait_while_present
    end
  end

  def edit_statement options
    on FEMatrixEdit do |page|
      select_statement options[:rule_requirements]
      page.edit
      page.loading.wait_while_present
      page.rule_dropdown.select options[:rule]

      if options[:exam_type] == "Standard"
        set_standard_rule options
      else
        set_common_rule options
      end

      page.preview_change
      page.loading.wait_while_present
    end
  end

  def delete_statement opts = {}
    defaults = {
        :exam_type => @exam_type,
        :rule_requirements => @rule_requirements,
        :add_statement => false,
        :edit_statement => false,
        :rule => @rule,
        :days => @days,
        :start_time => @start_time,
        :end_time => @end_time,
        :time_ampm => @time_ampm,
        :free_text => @free_text,
        :defer_save => @defer_save,
        :edit_in_progress => false
    }
    options = defaults.merge(opts)

    if options[:edit_in_progress] == false
      manage
      on(FEMatrixView).edit options[:rule_requirements], options[:exam_type]
    end

    on FEMatrixEdit do |page|
      select_statement options[:rule_requirements]
      page.delete
      page.loading.wait_while_present
    end

    on(FEMatrixEdit).update_rule unless options[:defer_save] == true

    set_options(options)
  end

  def add_edit_rdl_info opts = {}
    defaults = {
        :exam_type => @exam_type,
        :rule_requirements => @rule_requirements,
        :rdl_days => @rdl_days,
        :start_time => @start_time,
        :end_time => @end_time,
        :time_ampm => @time_ampm,
        :facility => @facility,
        :room => @room
    }
    options = defaults.merge(opts)

    manage
    on(FEMatrixView).edit options[:rule_requirements], options[:exam_type]

    on FEMatrixEdit do |page|
      if options[:rdl_days] != nil
        page.rdl_days.select options[:rdl_days]
      end

      if options[:start_time] != nil
        page.rdl_starttime.set options[:start_time]
      end

      if options[:end_time] != nil
        page.rdl_endtime.set options[:end_time]
      end

      if options[:time_ampm] != nil
        page.rdl_starttime_ampm.select options[:time_ampm]
        page.rdl_endtime_ampm.select options[:time_ampm]
      end

      if options[:exam_type] == "Common"
        if options[:facility] != nil
          page.rdl_facility.set options[:facility]
        end
        if options[:room] != nil
          page.rdl_room.set options[:room]
        end
      end

      page.update_rule
    end

    on FEMatrixView do |page|
      page.submit
      page.loading.wait_while_present
    end
  end

  def search
    go_to_manage_final_exam_matrix
  end

  def manage
    search
    on ManageFEMatrix do |page|
      page.term_type_select.select @term_type
      page.show
    end
  end

  def delete
    manage
    on FEMatrixView do |page|
      page.delete @rule_requirements, @exam_type
    end

    on FEMatrixView do |page|
      page.submit
      page.loading.wait_while_present
    end
  end

  def select_statement( rule_requirements)
    on FEMatrixEdit do |page|
      parent = page.rule_tree_section.a( text: /[A-Z]+\.\s+#{rule_requirements}/).parent
      if parent.class !~ /ruleBlockSelected/
        page.rule_tree_section.a( text: /[A-Z]+\.\s+#{rule_requirements}/).click
      end
    end
  end

  def set_standard_rule options
    on FEMatrixEdit do |page|
      if options[:rule] =~ /<timeslot>/
        page.rule_days.set options[:days]
        page.rule_starttime.set options[:start_time]
        page.rule_starttime_ampm.select options[:time_ampm]
        page.rule_endtime.set options[:end_time]
        page.rule_endtime_ampm.select options[:time_ampm]
      else
        page.rule_freeformtext.set options[:free_text]
      end
    end
  end

  def set_common_rule options
    on FEMatrixEdit do |page|
      page.loading.wait_while_present
      if options[:rule] =~ /<Course>/
        page.proposition_section.a( text: /Advanced Search/).click
        page.lookup_course_code.when_present.set options[:courses]
        page.lookup_search
        page.loading.wait_while_present
        page.return_course_code(options[:courses]).a( text: /Select/).click
      elsif options[:rule] =~ /<Courses>/
        page.courses_type_dropdown.select options[:courses_type]
        courses_array = options[:courses].split(/,/)
        courses_array.each do |course|
          page.loading.wait_while_present
          sleep 2 #Adding sleep due to loading image that disappears too quickly on some occasions but not always
          page.frm.a( text: /Advanced Search/).click
          page.lookup_course_code.when_present.set course
          page.lookup_search
          page.loading.wait_while_present
          page.return_course_code(course).a( text: /Select/).click
          page.add_line
        end
      else
        page.rule_freeformtext.set options[:free_text]
      end
      page.loading.wait_while_present
    end
  end

  def add_additional_statements
    manage
    on(FEMatrixView).edit @rule_requirements, @exam_type

    on FEMatrixEdit do |page|
      if @exam_type == "Standard"
        opts = {:rule => "Free Form Text",
                :free_text => "Standard FE free text"}
        page.add_statement
        page.rule_dropdown.select opts[:rule]
        set_standard_rule opts
        page.preview_change
        page.loading.wait_while_present
      elsif @add_more_statements == true
        opts = {:rule => "If course is part of <Courses>",
                :courses => "HIST110,ENGL304,BSCI202",
                :courses_type => "Approved Courses"}
        page.add_statement
        page.rule_dropdown.select opts[:rule]
        set_common_rule opts
        page.preview_change
        page.loading.wait_while_present

        opts = {:rule => "Free Form Text",
                :free_text => "Common FE free text"}
        page.add_statement
        page.rule_dropdown.select opts[:rule]
        set_common_rule opts
        page.preview_change
        page.loading.wait_while_present
      end
    end

    on(FEMatrixEdit).update_rule

    on FEMatrixView do |page|
      page.submit
      page.loading.wait_while_present
    end
  end
end
