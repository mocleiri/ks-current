class FinalExamMatrix < DataObject

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  attr_accessor :term_type, :rules

  def initialize(browser,opts = {})
    @browser = browser

    defaults = {
        :term_type => "Fall Term",
        :rules => collection('ExamMatrixRule')
    }

    options = defaults.merge(opts)
    set_options(options)
  end


  def manage
    search
    on ManageFEMatrix do |page|
      page.term_type_select.select @term_type
      page.show
    end
  end

  def search
    go_to_manage_final_exam_matrix
  end

  def add_rule opts
    opts[:rule_obj].parent_exam_matrix = self
    opts[:rule_obj].create
    @rules << opts[:rule_obj]
  end

  def delete opts = {}  #rule_obj
    manage
    on FEMatrixView do |page|
      page.delete opts['rule_obj'].rule_requirements
    end

    on FEMatrixView do |page|
      page.submit
      page.loading.wait_while_present
    end

    @rules.delete(opts['rule_obj'])
  end

end

class ExamMatrixRuleObject

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  attr_accessor :parent_exam_matrix, :rsi_days, :exam_type,
                :start_time, :st_time_ampm, :end_time, :end_time_ampm,
                :facility, :room, :statements

  def initialize(browser,opts = {})
    @browser = browser

    defaults = {
        :parent_exam_matrix => nil,
        :exam_type => "Standard",
        :rsi_days => "Day 1",
        :start_time => "05:00",
        :st_time_ampm => "pm",
        :end_time => "06:00",
        :end_time_ampm => "pm",
        :facility => "MTH",
        :room => "0304",
        :statements => nil,
        :add_more_statements => false,
        :defer_save => false,
        :defer_submit => false
    }

    options = defaults.merge(opts)
    set_options(options)

    @statements = collection('ExamMatrixStatement')
    #set default statement
    if options[:statements].nil?
      if options[:exam_type] == "Standard"
        @statements << (make ExamMatrixStatementObject, :statement_option => ExamMatrixStatementObject::TIME_SLOT_OPTION)
      else
        @statements << (make ExamMatrixStatementObject, :statement_option => ExamMatrixStatementObject::COURSE_OPTION)
      end
    else
      @statements << options[:statements]
      @statements = @statements.flatten
    end
  end

  def create
    @parent_exam_matrix.manage

    if @exam_type == "Standard"
      on(FEMatrixView).add_standard_fe_rule
    else
      on(FEMatrixView).add_common_fe_rule
    end

    @statements.each do |statement|
      statement.parent_rule = self
      statement.create
    end

    on FEMatrixEdit do |page|
      page.rsi_days.select @rsi_days
      page.rsi_starttime.set @start_time
      page.rsi_starttime_ampm.select @st_time_ampm
      page.rsi_endtime.set @end_time
      page.rsi_endtime_ampm.select @end_time_ampm

      if @exam_type == 'Common'
        page.rsi_facility.set @facility
        page.rsi_room.set @room
      end

      page.update_rule unless @defer_save == true
      page.loading.wait_while_present
    end

    on FEMatrixView do |page|
      page.submit unless @defer_submit == true
      page.loading.wait_while_present
    end
  end

  def rule_requirements
    rule_req_text = ''
    @statements.each do |stmt_obj|
      rule_req_text += "#{stmt_obj.operator_str}#{stmt_obj.stmt_str}."
    end
    rule_req_text
  end

  def edit opts = {}

    defaults = {
        :defer_save => false,
        :navigate_to_page => true,
        :defer_submit => false
    }
    options = defaults.merge(opts)

    @parent_exam_matrix.manage unless options[:navigate_to_page] == false
    on(FEMatrixView).edit self, @exam_type

    on FEMatrixEdit do |page|
      if options[:rsi_days] != nil
        page.rsi_days.select options[:rsi_days]
      end

      if options[:start_time] != nil
        page.rsi_starttime.set options[:start_time]
      end

      if options[:st_time_ampm] != nil
        page.rsi_starttime_ampm.select options[:st_time_ampm]
      end

      if options[:end_time] != nil
        page.rsi_endtime.set options[:end_time]
      end

      if options[:end_time_ampm] != nil
        page.rsi_endtime_ampm.select options[:end_time_ampm]
      end

      if @exam_type == "Common"
        if options[:facility] != nil
          page.rsi_facility.set options[:facility]
        end
        if options[:room] != nil
          page.rsi_room.set options[:room]
        end
      end

      #page.update_rule

      on(FEMatrixEdit).update_rule unless options[:defer_save]

      unless options[:defer_submit]
        on FEMatrixView do |page|
          page.submit
          page.loading.wait_while_present
        end
      end

      set_options(options)
    end
  end

  def add_statement statement_obj
    statement_obj.parent_rule = self
    statement_obj.create
    @statements << statement_obj
  end

  def delete opts={}
    defaults = {
        :defer_save => @defer_submit,
    }
    options = defaults.merge(opts)

    @parent_exam_matrix.manage

    on FEMatrixView do |page|
      page.delete self, @exam_type
    end

    unless options[:defer_submit]
      on FEMatrixView do |page|
        page.submit
        page.loading.wait_while_present
      end
    end

    @parent_exam_matrix.rules.delete(self)
  end

  def select_statement()
    on FEMatrixEdit do |page|
      parent = page.rule_tree_section.a( text: /[A-Z]+\.\s+#{rule_requirements}/).parent
      if parent.class !~ /ruleBlockSelected/
        page.rule_tree_section.a( text: /[A-Z]+\.\s+#{rule_requirements}/).click
      end
    end
  end
end

class ExamMatrixRuleCollection < CollectionsFactory

  contains ExamMatrixRuleObject

end

class ExamMatrixStatementObject

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  attr_accessor :parent_rule, :courses, :days, :start_time, :st_time_ampm,
                :free_text, :courses, :courses_type,
                :statement_operator, :statement_option, :rsi_days

  TIME_SLOT_OPTION = 'If Course meets on <timeslot>'
  FREE_TEXT_OPTION = 'Free Form Text'
  COURSE_OPTION = 'If course is <Course>'
  COURSE_PART_OF_OPTION = 'If course is part of <Courses>'

  def initialize(browser,opts = {})
    @browser = browser

    defaults = {
        :parent_rule => nil,
        #:rule_requirements => "TH", this is a derived value
        :statement_option =>  TIME_SLOT_OPTION,
        :statement_operator => nil, #only needed for statement 2 and up
        :days => "MWF",
        :start_time => "01:00",
        :st_time_ampm => "pm",
        :free_text => "Free Form Text",
        :courses => "ENGL101",
        :rsi_days => "Day 1",
        :courses_type => "Approved Courses",
        :defer_save => false,
        :defer_submit => false
    }

    options = defaults.merge(opts)
    set_options(options)
  end

  def create

    on FEMatrixEdit do |page|
      page.add_statement
      page.rule_dropdown.select @statement_option
      page.loading.wait_while_present

      if @statement_option == TIME_SLOT_OPTION
        set_standard_rule
      elsif @statement_option == FREE_TEXT_OPTION
        set_free_text_rule
      else
        set_common_rule
      end

      page.preview_change
      page.loading.wait_while_present

    end
  end

  def edit opts = {}
    defaults = {
        :defer_save => false,
        :navigate_to_page => true,
        :defer_submit => false
    }
    options = defaults.merge(opts)

    parent_matrix = @parent_rule.parent_exam_matrix

    if options[:navigate_to_page]
      parent_matrix.manage
      on(FEMatrixView).edit @parent_rule, @parent_rule.exam_type
    end

    on FEMatrixEdit do |page|
      select_statement stmt_str
      page.edit
      page.loading.wait_while_present
      page.rule_dropdown.select options[:statement_option] unless options[:statement_option].nil?
      page.loading.wait_while_present
      #now update depending on statement option (after change)
      stmt_opt = options[:statement_option].nil? ? @statement_option : options[:statement_option]
      case stmt_opt
        when TIME_SLOT_OPTION
          page.rule_days.set options[:days] unless options[:days].nil?
          page.rule_starttime.set options[:start_time] unless options[:start_time].nil?
          page.rule_starttime_ampm.select options[:st_time_ampm] unless options[:st_time_ampm].nil?
          page.rule_endtime.set options[:end_time] unless options[:end_time].nil?
          page.rule_endtime_ampm.select options[:end_time_ampm] unless options[:end_time_ampm].nil?
        when COURSE_OPTION
          page.proposition_section.a( text: /Advanced Search/).click
          page.lookup_course_code.when_present.set options[:courses]
          page.lookup_search
          page.loading.wait_while_present
          page.return_course_code(options[:courses]).a( text: /Select/).click
        when COURSE_PART_OF_OPTION
          page.courses_type_dropdown.select COURSES_OPTION
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
        when FREE_TEXT_OPTION
          page.rule_freeformtext.set options[:free_text] unless options[:free_text].nil?
      end

      page.preview_change
      page.loading.wait_while_present
    end

    on(FEMatrixEdit).update_rule unless options[:defer_save]

    unless options[:defer_submit]
      on FEMatrixView do |page|
        page.submit
        page.loading.wait_while_present
      end
    end

    set_options(options)
  end

  def operator_str
    statement_operator.nil? ? "" : " #{@statement_operator} "
  end

  def stmt_str
    case @statement_option
      when TIME_SLOT_OPTION
        return "#{@days} at #{@start_time} #{@st_time_ampm.upcase} - #{@end_time} #{@end_time_ampm.upcase}"
      when COURSE_OPTION
        return "#{@courses}"
      when COURSE_PART_OF_OPTION
        courses_str = '('
        @courses.each do |course|
          courses_str += "#{course}, "
        end
        courses_str = "#{courses[0,-2]})"
        return courses_str
      when FREE_TEXT_OPTION
        return "#{@free_text}"

    end
  end

  def search
    go_to_manage_final_exam_matrix
  end

  def delete opts={}

    defaults = {
        :defer_save => false,
        :navigate_to_page => true,
        :defer_submit => false
    }

    options = defaults.merge(opts)
    @parent_rule.edit :defer_save => true, :defer_submit => true

    on FEMatrixEdit do |page|
      select_statement stmt_str
      page.delete
      page.loading.wait_while_present
    end

    on(FEMatrixEdit).update_rule unless options[:defer_save] == true

    unless options[:defer_submit]
      on FEMatrixView do |page|
        page.submit
        page.loading.wait_while_present
      end
    end

    @parent_rule.statements.delete(self)
  end

  def select_statement( rule_requirements)
    on FEMatrixEdit do |page|
      parent = page.rule_tree_section.a( text: /[A-Z]+\.\s+#{rule_requirements}/).parent
      if parent.class !~ /ruleBlockSelected/
        page.rule_tree_section.a( text: /[A-Z]+\.\s+#{rule_requirements}/).click
      end
    end
  end

  def set_standard_rule
    on FEMatrixEdit do |page|
      page.rule_dropdown.select TIME_SLOT_OPTION
      page.loading.wait_while_present
      page.rule_days.set @days
      page.rule_starttime.set @start_time
      page.rule_starttime_ampm.select @st_time_ampm
    end
  end

  def set_common_rule
    on FEMatrixEdit do |page|
      page.loading.wait_while_present
      if @statement_option == COURSE_OPTION
        page.rule_dropdown.select COURSE_OPTION
        page.loading.wait_while_present
        page.proposition_section.a( text: /Advanced Search/).click
        page.lookup_course_code.when_present.set @courses
        page.lookup_search
        page.loading.wait_while_present
        page.return_course_code(@courses).a( text: /Select/).click
      elsif @statement_option == COURSES_OPTION
        page.rule_dropdown.select COURSES_OPTION
        page.loading.wait_while_present
        courses_array = @courses.split(/,/)
        courses_array.each do |course|
          page.loading.wait_while_present
          page.frm.a( text: /Advanced Search/).when_present.click
          page.lookup_course_code.when_present.set course
          page.lookup_search
          page.loading.wait_while_present
          page.return_course_code(course).a( text: /Select/).click
          page.add_line
        end
        page.loading.wait_while_present
      end
    end
  end

  def set_free_text_rule
    on FEMatrixEdit do |page|
      page.rule_dropdown.select FREE_TEXT_OPTION
      page.loading.wait_while_present
      page.rule_freeformtext.set @free_text
    end
  end
end

class ExamMatrixStatementCollection < CollectionsFactory

  contains ExamMatrixStatementObject

end
