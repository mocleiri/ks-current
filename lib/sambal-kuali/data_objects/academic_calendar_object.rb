# stores test data for creating/editing and validating academic calendars and provides convenience methods for navigation and data entry
#
# class attributes are initialized with default data unless values are explicitly provided
#
# Typical usage: (with optional setting of explicit data value in [] )
#  @calendar = make AcademicCalendar, [:name=>"acal_name1", :start_date=>"12/12/2012", :end_date=>"12/12/2013"
#  @calendar.create
# OR alternatively 2 steps together as
#  @calendar = create AcademicCalendar, :name=>"acal_name1", :start_date=>"12/12/2012", :end_date=>"12/12/2013"
# Note the use of the ruby options hash pattern re: setting attribute values
class AcademicCalendar < DataObject

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows
  include CalendarUtils

  #generally set using options hash
  attr_accessor :name, :start_date, :end_date, :terms, :year, :events
  #not implemented
  attr_accessor :holiday_calendar_list

  # provides default data:
  #  defaults = {
  #      :name=>random_alphanums.strip,
  #      :start_date=>"09/01/#{next_year[:year]}",
  #      :end_date=>"06/25/#{next_year[:year] + 1}",
  #      :organization=>"Registrar's Office"   GONE: per KSENROLL-7685
  #  }
  # initialize is generally called using TestFactory Foundry .make or .create methods
  def initialize(browser, opts={})
    @browser = browser

    if opts[:year].nil? then
      calendar_year = get_random_calendar_year
    else
      calendar_year = opts[:year].to_i
    end

    defaults = {
        :name => random_alphanums.strip,
        :year => calendar_year,
        :start_date => "08/20/#{calendar_year}",
        :end_date => "06/25/#{calendar_year + 1}",
        :terms => collection('AcademicTerm'),
        :holiday_calendar_list => [],
        :events => collection('CalendarEvent')
    }

    options = defaults.merge(opts)
    set_options(options)
  end

  #navigates to Create Calendar page and creates academic calendar from blank
  def create opts={}

    go_to_academic_calendar

    on CreateAcadCalendar do |page|
      page.start_blank_calendar
    end

    on EditAcademicCalendar do |page|
      page.academic_calendar_name.set @name
      page.calendar_start_date.set @start_date
      page.calendar_end_date.set @end_date

      page.save unless opts[:defer_save]
    end
  end

  #navigates to Create Calendar page and creates academic calendar by copying the calendar matching the name parameter
  #
  #@param name [String] name of source calendar
  def copy_from name
    go_to_academic_calendar

    if right_source? name
      on CreateAcadCalendar do |page|
        page.name.set @name
        page.start_date.set @start_date
        page.end_date.set @end_date
      end
    else
      on CreateAcadCalendar do |page|
        page.choose_different_calendar
      end
      on CalendarSearch do |page|
        page.search_for "Academic Calendar", name
        page.copy name
      end
      on EditAcademicCalendar do |page|
        page.academic_calendar_name.set @name
        page.calendar_start_date.set @start_date
        page.calendar_end_date.set @end_date
      end
    end

    #add logic to update the term dates and make term names consistent by adding year
    on EditAcademicTerms do |page|
      page.go_to_terms_tab

      term_section_div_list = page.term_section_div_list
      if term_section_div_list.length > 0 then
        page.open_term_section_by_index(0)
        old_base_year = page.term_start_date(0).value[/\d{4}$/].to_i
      end
      index = 0
      term_section_div_list.each do |div|
        page.open_term_section_by_index(index)
        start_date = page.term_start_date(index).value
        updated_start_date = convert_date_to_updated_year(start_date, old_base_year)
        page.term_start_date(index).set updated_start_date
        end_date = page.term_end_date(index).value
        page.term_end_date(index).set convert_date_to_updated_year(end_date, old_base_year)

        term_name = page.term_name_edit(index).value
        #strip term off the end (if there)
        if term_name.index('Continuing').nil?
          term_name = term_name[/\S+(?= Term)/].nil? ? term_name : term_name[/\S+(?= Term)/]
        end
        page.term_name_edit(index).set "#{term_name} #{updated_start_date[-4..-1]}" #eg Fall 2033

        index += 1
      end

      page.save
    end
  end

  def edit opts = {}

    defaults = {
        :exp_success=> true
    }
    options = defaults.merge(opts)

    search
    on(CalendarSearch).edit @name

    on EditAcademicCalendar do |page|
      if !options[:name].nil? then
        page.academic_calendar_name.set options[:name]
      end

      if !options[:start_date].nil? then
        page.calendar_start_date.set options[:start_date]
      end

      if !options[:end_date].nil? then
        page.calendar_end_date.set options[:end_date]
      end

      page.save :exp_success => options[:exp_success]
    end

    set_options(options) if options[:exp_success]
  end

  #when updating the term dates, make sure get the calendar yr correct ie 2012 vs 2013 dates
  def convert_date_to_updated_year(date, base_year)
    date_year = date[/\d{4}$/].to_i
    date_month_day = date[/\d{2}\/\d{2}\//]
    new_date_year = @year.to_i + (date_year - base_year)
    "#{date_month_day}#{new_date_year}"
  end

  #search for academic calendar matching the :name attribute
  def search
    go_to_calendar_search

    on CalendarSearch do |page|
      page.search_for "Academic Calendar", @name
    end
  end

  #checks currently selected calendar matches name
  #
  #@param name [String] name of selected calendar
  #
  #@returns boolean
  def right_source? name
    on(CreateAcadCalendar).source_name == name
  end

  #make currently selected calendar official
  def make_official
    on EditAcademicCalendar do |page|
      page.make_official
      page.make_offical_confirm
    end
  end

  def delete_draft
    edit

    on EditAcademicCalendar do |page|
      page.delete_draft
      page.confirm_delete
    end
  end

  def view
    search
    on(CalendarSearch).view @name
  end

  def add_term term_object
    term_object.parent_calendar = self
    term_object.create
    @terms << term_object
    on(EditAcademicTerms).save
  end

  #holiday_calendar is created separately
  def add_holiday_calendar hcal_object
    edit

    on EditAcademicCalendar do |page|
      page.add_holiday_calendar_select.select hcal_object.name
      page.add_holiday_calendar
      page.save
    end

    holiday_calendar_list << hcal_object
  end

  def add_event event_object
    event_object.parent_calendar = self
    event_object.create
    @events << event_object
  end

  def get_all_term_names_in_calendar
    edit

    on(EditAcademicCalendar).terms_tab

    array = []
    on EditAcademicTerms do |page|
      page.acal_term_list_div.spans( class: "uif-headerText-span").each do |span|
        array << span.text if span.parent.id =~ /term_section_line\d+_toggle/
      end
    end
    return array
  end
end

class AcademicTermObject

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  attr_accessor :term, :term_type, :term_name, :start_date, :end_date, :expected_instructional_days,
                :key_date_groups, :parent_calendar, :parent_term, :subterm, :subterm_type, :term_code, :exam_period

  WINTER_TERM_TYPE = "Winter Term"
  FALL_TERM_TYPE = "Fall Term"

  def initialize(browser,opts = {})
    @browser = browser

    #establish the year in order to make default start/end dates
    #if opts[:term_year] == "random" then
    #  calendar_year = AcademicCalendar.get_random_calendar_year
    #else
    calendar_year = opts[:parent_calendar].year
    #end

    defaults = {
      :parent_calendar => nil,
      :term => 'Fall',
      :start_date=>"09/02/#{calendar_year}",
      :end_date=>"09/24/#{calendar_year}",
      :term_type=> nil,
      :term_name => nil,
      :term_code => "#{calendar_year}08" ,
      #:term_year=> calendar_year,
      :parent_term=> nil,
      :subterm=> false,
      :key_date_groups => collection('KeyDateGroup'),
      :exam_period => nil
    }

    #A subterm can't use the same Key date Group as its parent, because when
    # the parent selects a certain value, it's gone from the drop-down for the sub.
    #if opts[:subterm] == true
    #  defaults[:key_date_group_list] = Array.new(1){make KeyDateGroup, :key_date_group_type=> KeyDateGroup::REGISTRATION_DATE_GROUP}
    #else
    #  defaults[:key_date_group_list] = Array.new(1){make KeyDateGroup}
    #end

    options = defaults.merge(opts)
    set_options(options)

    case @term
      when /Summer|Continuing Education/
        @term_name = "#{@term} #{options[:parent_calendar].year}" if @term_name.nil?
        @term_type = "#{@term}" if @term_type.nil?
      when /Fall|Winter|Spring/
        @term_name = "#{@term} #{options[:parent_calendar].year}" if @term_name.nil?
        @term_type = "#{@term} Term" if @term_type.nil?
    end

    if @subterm then
      @subterm_type = @term_type
      @term_type = "Subterm: #{@term_type}"
      @term_name =  "#{@subterm_type} #{options[:parent_calendar].year}"
    end
  end

  def create

    on EditAcademicTerms do |page|
      page.go_to_terms_tab

      if @subterm then
        term_type = @subterm_type
      else
        term_type = @term_type
      end

      page.term_type_add.select term_type
      page.adding.wait_while_present

      page.term_start_date_add.set @start_date
      page.term_end_date_add.set @end_date

      page.acal_term_add

      begin
        page.adding.wait_while_present
      rescue
        page.alert.ok
        sleep 60
      end

      page.open_term_section @term_type
      term_index = page.term_index_by_term_type @term_type
      page.term_name_edit(term_index).set @term_name unless @subterm

      @key_date_groups.each do |date_group|
        date_group.parent_term = self
        date_group.create
      end

      if @exam_period != nil
        @exam_period.parent_term = self
        @exam_period.create :navigate_to_page => false
      end
    end

    save
  end

  ##
  def edit opts = {}

    defaults = {
        :exp_success=> true,
        :exam_period => nil,
        :include_non_active_days => false,
        :defer_save => false,
        :do_navigation => true
    }
    options = defaults.merge(opts)

    if options[:do_navigation]
      search
      on(CalendarSearch).edit @term_name
    end

    term_index = 0
    on EditAcademicTerms do |page|
      page.open_term_section @term_type
      term_index = page.term_index_by_term_type @term_type

      if options[:term_name] != nil
        page.term_name_edit(term_index).set options[:term_name]
      end

      if options[:start_date] != nil
        page.term_start_date(term_index).set options[:start_date]
      end

      if options[:end_date] != nil
        page.term_end_date(term_index).set options[:end_date]
      end

      page.terms_tab_link.click #close any open date pickers
      unless options[:defer_save]
        page.save :exp_success => options[:exp_success]
      end
    end

    set_options(options)
  end

  def save opts = {}
    defaults = {
        :exp_success=> true,
    }
    options = defaults.merge(opts)

    on(EditAcademicTerms).save :exp_success => options[:exp_success]
  end

  #TODO - move should statements to step definitions
  def verify
    on EditAcademicTerms do |page|
      page.get_term_type(0).should == @term_name
      page.get_term_start_date(0).should == @start_date
      page.get_term_end_date(0).should == @end_date
    end
    @keyDateGroup[0].verify
  end

  def search
    go_to_calendar_search

    term_criteria = "Academic Term"
    term_criteria = "Sub Term" if @subterm
    on CalendarSearch do |page|
      page.search_for term_criteria, @term_name, @parent_calendar.year
    end
  end

  def make_official
    search

    on(CalendarSearch).edit @term_name

    on EditAcademicTerms do |page|
      page.go_to_terms_tab
      page.make_term_official @term_type
    end
  end

  def delete
    edit
    on EditAcademicTerms do |page|
      page.go_to_terms_tab
      page.delete_term @term_type
      @parent_calendar.terms.delete self
      page.save
    end
  end

  def weekdays_in_term
    date1 = Date.strptime( @start_date , '%m/%d/%Y')
    date2 = Date.strptime( @end_date , '%m/%d/%Y')
    weekdays = 0
    date = date2
    while date >= date1
      weekdays = weekdays + 1 unless date.saturday? or date.sunday?
      date = date - 1
    end
    weekdays
  end

  #checks to see if group already exists   NOT being called!
  def add_key_date_group key_date_group_object
    key_date_group_object.parent_term = self
    key_date_group_object.create
    @key_date_groups << key_date_group_object
  end

  #Added a def due to some steps that create a term with no exam period first and then later needs to add the exam
  #   period without having to run @term.create again
  def add_exam_period exam_period_object
    exam_period_object.parent_term = self
    exam_period_object.create
    @exam_period = exam_period_object
  end
end

class AcademicTermCollection < CollectionsFactory
  contains AcademicTermObject
end

class KeyDateGroupObject

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  attr_accessor :parent_term, :key_date_group_type, :key_dates, :term_index

  INSTRUCTIONAL_DATE_GROUP = "Instructional"
  REGISTRATION_DATE_GROUP = "Registration"

  def initialize(browser,opts = {})
    @browser = browser

    defaults = {
        :parent_term => nil,
        :key_date_group_type => INSTRUCTIONAL_DATE_GROUP,
        :key_dates => collection('KeyDate')
    }

    options = defaults.merge(opts)
    set_options(options)
  end

  def create opts = {}

    on EditAcademicTerms do |page|
      sleep 3
      page.open_term_section @parent_term.term_type

      #only create if not already there
      if page.key_date_group_div(@parent_term.term_type, @key_date_group_type).nil? then
        @term_index = page.term_index_by_term_type @parent_term.term_type
        page.key_date_group_dropdown(@term_index).select @key_date_group_type
        page.loading.wait_while_present

        page.key_date_group_add @term_index
        page.adding.wait_while_present
      end

      @key_dates.each do |key_date|
        key_date.parent_term = @parent_term
        key_date.parent_key_date_group = self
        key_date.create
      end

      page.save unless opts[:exp_success] == false
    end
  end

  #def edit(opts = {})
  #
  #end

  def delete
    on EditAcademicTerms  do |page|
      page.delete_key_date_group @parent_term.term_type, @key_date_group_type
      @parent_term.key_date_groups.delete self
      page.save
    end
  end

  def verify
    @key_dates[0].verify
  end

  def add_key_date key_date_object
    key_date_object.parent_term = @parent_term
    key_date_object.parent_key_date_group = self
    key_date_object.create
    @key_dates << key_date_object
  end
  private :add_key_date
end

class KeyDateGroupCollection < CollectionsFactory
  contains KeyDateGroupObject
end

class KeyDateObject

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  attr_accessor :key_date_type, :start_date, :end_date, :start_time, :end_time, :start_time_ampm, :end_time_ampm,
                :term_index, :parent_key_date_group, :parent_term

  def initialize(browser,opts = {})
    @browser = browser

    defaults = {
      :parent_key_date_group => nil,
      :parent_term => nil,
      :key_date_type => "Grading Period",
      :start_date => "09/02/#{next_year[:year]}",
      :end_date => nil,
      :start_time => nil,
      :end_time => nil,
      :start_time_ampm => nil,
      :end_time_ampm => nil
    }

    options = defaults.merge(opts)
    set_options(options)
  end

  def create

    #only create if doesn't already exist, otherwise edit the existing one
    on EditAcademicTerms do |page|
      page.go_to_terms_tab
      page.open_term_section @parent_term.term_type

      if !page.key_date_exists?  @parent_term.term_type, @parent_key_date_group.key_date_group_type, @key_date_type then
        @term_index = page.term_index_by_term_type @parent_term.term_type
        key_date_group_index = page.key_date_group_index @parent_term.term_type, @parent_key_date_group.key_date_group_type

        if !page.key_date_dropdown_addline(@term_index, key_date_group_index).exists?
          page.key_date_button( @term_index, key_date_group_index).click
          #sleep 1 #wait til new row
          page.key_date_dropdown_addline(@term_index, key_date_group_index).wait_until_present
        end

        page.key_date_dropdown_addline( @term_index, key_date_group_index).select @key_date_type
        page.key_date_start_date_addline( @term_index,key_date_group_index).set @start_date
        page.key_date_end_date_addline( @term_index,key_date_group_index).set @end_date
      else
        #TODO - need the opposite of set_options here
        edit :key_date_type => @key_date_type, :start_date => @start_date, :end_date  => @end_date, :start_time  => @start_time, :end_time  => @end_time, :start_time_ampm  => @start_time_ampm,  :end_time_ampm => @end_time_ampm
      end

      page.save
    end
  end

  def edit opts={}

    defaults = {
        :exp_success => true
    }
    options = defaults.merge(opts)

    on EditAcademicTerms  do |page|
      edit_row = page.key_date_target_row @parent_term.term_type, @parent_key_date_group.key_date_group_type, @key_date_type

      if options[:start_date] != nil
        page.edit_key_date_start_date edit_row, options[:start_date]
      end

      if options[:end_date] != nil
        page.edit_key_date_end_date edit_row,options[:end_date]
      end

      if options[:start_time] != nil
        page.edit_key_date_start_time edit_row, options[:start_time]
      end

      if options[:start_time_ampm] != nil
        page.edit_key_date_start_ampm edit_row, options[:start_time_ampm]
      end

      if options[:end_time] != nil
        page.edit_key_date_end_time edit_row, options[:end_time]
      end

      if options[:end_time_ampm] != nil
        page.edit_key_date_end_ampm edit_row, options[:end_time_ampm]
      end

      page.save :exp_success => options[:exp_success]
    end

    set_options(options)
  end

  def delete
    on EditAcademicTerms  do |page|
      delete_row = page.key_date_target_row @parent_term.term_type, @parent_key_date_group.key_date_group_type, @key_date_type

      page.delete_key_date delete_row
      page.loading.wait_while_present
      @parent_key_date_group.key_dates.delete self
      page.save
    end
  end

  #TODO - move to step definitions
  def verify()
    on EditAcademicTerms do |page|
      page.key_date_type(0,0,0).should == @key_date_type
      page.key_date_start_date(0,0,0).should == @start_date
      page.key_date_end_date(0,0,0).should == @end_date
    end
  end
end

class KeyDateCollection < CollectionsFactory
  contains KeyDateObject
end

class CalendarEventObject

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  attr_accessor :event_type, :start_date, :end_date, :start_time, :end_time, :start_time_ampm, :end_time_ampm,
                :parent_calendar

  def initialize(browser,opts = {})
    @browser = browser

    event_year = opts[:parent_calendar].year.to_i

    defaults = {
        :parent_calendar => nil,
        :event_type => "Commencement - Seattle Campus",
        :start_date => "04/15/#{event_year + 1}",
        :end_date => "05/15/#{event_year + 1}",
        :start_time => "07:30",
        :end_time => "09:00",
        :start_time_ampm => "pm",
        :end_time_ampm => "pm"
    }

    options = defaults.merge(opts)
    set_options(options)
  end

  def create()

    on EditAcademicCalendar do |page|
      page.open_events_section
      wait_until { page.add_event_button.present? }

      page.add_event_button.click
      page.loading.wait_while_present
      wait_until { page.add_event_type.present? }

      page.add_event_type.select @event_type

      page.add_event_start_date.set @start_date
      page.add_event_start_time.set @start_time
      page.loading.wait_while_present
      if @start_time_ampm == "am"
        page.add_event_start_am_set
      else
        page.add_event_start_pm_set
      end

      page.add_event_end_date.set @end_date
      page.add_event_end_time.set @end_time
      page.loading.wait_while_present
      if @end_time_ampm == "am"
        page.add_event_end_am_set
      else
        page.add_event_end_pm_set
      end

      page.save
    end
  end

  def edit options={}

    on EditAcademicCalendar  do |page|
      edit_row = page.target_event_row_in_edit @event_type

      if options[:start_date] != nil
        page.edit_start_date edit_row, options[:start_date]
      end

      if options[:start_time] != nil
        page.edit_start_time edit_row, options[:start_time]
      end

      if options[:start_time_ampm] != nil
        page.edit_start_ampm edit_row, options[:start_time_ampm]
      end

      if options[:end_date] != nil
        page.edit_end_date edit_row, options[:end_date]
      end

      if options[:end_time] != nil
        page.edit_end_time edit_row, options[:end_time]
      end

      if options[:end_time_ampm] != nil
        page.edit_end_ampm edit_row, options[:end_time_ampm]
      end

      page.save
    end

    set_options(options)
  end

  def delete
    on EditAcademicCalendar  do |page|
      delete_row = page.target_event_row_in_edit @event_type
      page.delete delete_row
      page.loading.wait_while_present
      @parent_calendar.events.delete self
      page.save
    end
  end
end

class CalendarEventCollection < CollectionsFactory
  contains CalendarEventObject
end

class ExamPeriodObject

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  attr_accessor :parent_term, :start_date, :end_date, :exclude_saturday, :exclude_sunday, :length_ex_weekend

  def initialize(browser,opts = {})
    @browser = browser

    calendar_year = opts[:parent_term].parent_calendar.year

    defaults = {
        :parent_term => nil,
        :start_date => "09/02/#{calendar_year}",
        :end_date => "09/24/#{calendar_year}",
        :exclude_saturday => true,
        :exclude_sunday => true,
        :length_ex_weekend => 0 #if not zero, calc start/end dates
    }

    options = defaults.merge(opts)
    set_options(options)
  end

  def create opts={}

    defaults = {
        :navigate_to_page => true
    }
    options = defaults.merge(opts)

    unless options[:navigate_to_page] == false
      @parent_term.edit :defer_save => true
    end

    on EditAcademicTerms do |page|
      page.open_term_section @parent_term.term_type
      page.loading.wait_while_present

      if page.add_exam_period_btn( @parent_term.term_type).present?
        page.add_exam_period @parent_term.term_type
      end

      if @length_ex_weekend > 0
        @start_date = calc_start_date_wednesday @start_date
        @end_date = calc_end_date_ex_weekends @start_date, @length_ex_weekend
      end

      page.set_exam_start_date @parent_term.term_type, @start_date
      page.set_exam_end_date @parent_term.term_type, @end_date
      page.set_exclude_saturday @parent_term.term_type unless @exclude_saturday == true
      page.set_exclude_sunday @parent_term.term_type unless @exclude_sunday == true

      #page.save -- page won't save if dates aren't correct, need to save separately
    end

    set_options(options)
  end

  def edit opts={}

    defaults = {
        :exp_success => true,
        :defer_save => false,
        :navigate_to_page => true
    }
    options = defaults.merge(opts)

    unless options[:navigate_to_page] == false
      @parent_term.edit :defer_save => true
    end

    on EditAcademicTerms do |page|
      page.open_term_section @parent_term.term_type
      page.loading.wait_while_present

      if options[:start_date] != nil
        page.set_exam_start_date @parent_term.term_type, options[:start_date]
      end

      if options[:end_date] != nil
        page.set_exam_end_date @parent_term.term_type, options[:end_date]
      end

      if !options[:exclude_saturday]
        page.clear_exclude_saturday @parent_term.term_type
      else
        page.set_exclude_saturday @parent_term.term_type
      end

      if !options[:exclude_sunday]
        page.clear_exclude_sunday @parent_term.term_type
      else
        page.set_exclude_sunday @parent_term.term_type
      end

      if !options[:defer_save]
        page.save :exp_success => options[:exp_success]
      end
    end

    set_options(options)
  end

  def delete
    on EditAcademicTerms do |page|
      page.open_term_section @parent_term.term_type
      page.loading.wait_while_present

      page.exam_delete @parent_term.term_type
      @parent_term.exam_period self
      page.save
    end
  end

  def calc_start_date_wednesday start_date
    s_date = Date.strptime( start_date , '%m/%d/%Y')
    #while (s_date.saturday? or s_date.sunday?) do
    while !s_date.wednesday? do
      s_date += 1
    end
    s_date.strftime("%m/%d/%Y")
  end

  #assumes start_date is a weekday (M-F)
  def calc_end_date_ex_weekends start_date, no_of_days
    end_date = Date.strptime( start_date , '%m/%d/%Y')
    while no_of_days > 1 do
      end_date += 1
      unless (end_date.saturday? or end_date.sunday?)
        no_of_days -= 1
      end
    end
    end_date.strftime("%m/%d/%Y")
  end
end

class ExamPeriodCollection < CollectionsFactory
  contains ExamPeriodObject
end