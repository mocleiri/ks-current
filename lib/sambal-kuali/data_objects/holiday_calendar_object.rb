# stores test data for creating/editing and validating holiday calendars and provides convenience methods for navigation and data entry
#
# class attributes are initialized with default data unless values are explicitly provided
#
# Typical usage: (with optional setting of explicit data value in [] )
#  @calendar = make HolidayCalendar, [:name=>"acal_name1", :start_date=>"12/12/2012", :end_date=>"12/12/2013"]
#  @calendar.create
# OR alternatively 2 steps together as
#  @calendar = create HolidayCalendar, :name=>"acal_name1", :start_date=>"12/12/2012", :end_date=>"12/12/2013"
# Note the use of the ruby options hash pattern re: setting attribute values
class HolidayCalendar

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows
  include CalendarUtils

  #generally set using options hash
  attr_accessor :name, :start_date, :end_date, :holiday_list, :year

  # provides default data:
  #defaults = {
  # :name=>random_alphanums.strip,
  # :start_date=>"09/01/#{next_year[:year]}",
  # :end_date=>"06/25/#{next_year[:year] + 1}",
  # :organization=>"Registrar's Office",  GONE per KSENROLL-5641
  # :holiday_list=>[
  # {:type=>"random", :start_date=>"02/01/#{next_year[:year] + 1}", :instructional=>false},
  # {:type=>"random", :start_date=>"03/02/#{next_year[:year] + 1}", :end_date=>"03/04/#{next_year[:year] + 1}", :instructional=>false},
  # {:type=>"random", :start_date=>"04/05/#{next_year[:year] + 1}", :start_time=>"03:00", :start_meridian=>"pm", :end_time=>"07:44", :end_meridian=>"pm", :instructional=>false},
  # {:type=>"random", :start_date=>"05/11/#{next_year[:year] + 1}", :start_time=>"02:22", :start_meridian=>"am", :end_date=>"05/22/#{next_year[:year] + 1}", :end_time=>"07:44", :end_meridian=>"pm", :instructional=>false}
  # ]
  #}
  # initialize is generally called using TestFactory Foundry .make or .create methods
  def initialize(browser, opts={})
    @browser = browser

    calendar_year = opts[:year].to_i

    defaults = {
        :name=>random_alphanums.strip,
        :year => calendar_year,
        :start_date=>"09/01/#{calendar_year}",
        :end_date=>"08/25/#{calendar_year.to_i + 1}",
        :create_by_copy_search => nil,
        :holiday_list=>[
            (make Holiday, :type=>"Columbus Day", :start_date=>"02/01/#{calendar_year.to_i + 1}", :instructional=>false) ,
            (make Holiday, :type=>"Good Friday", :start_date=>"03/02/#{calendar_year.to_i + 1}", :end_date=>"03/05/#{calendar_year.to_i + 1}", :instructional=>false )
        ]
    }
    options = defaults.merge(opts)
    set_options(options)
  end

  #navigates to Create Calendar page and creates academic calendar from blank
  def create(opts = {})
    defaults = {
        :exp_success=> true
    }
    options = defaults.merge(opts)

    if !@create_by_copy_search.nil?
      go_to_calendar_search
      on CalendarSearch do |page|
        page.search_for "Holiday Calendar", @create_by_copy_search.name
        page.copy @create_by_copy_search.name
      end
      on CreateEditHolidayCalendar do |page|
        @name = random_alphanums.strip
        page.calendar_name.set @name
        page.start_date.set @start_date
        page.end_date.set @end_date
        page.save
        init_holiday_list
      end
    else
      go_to_holiday_calendar
      on CopyHolidayCalendar do |page|
        page.start_blank_calendar
      end
      on CreateEditHolidayCalendar do |page|
        page.calendar_name.set @name
        page.start_date.set @start_date
        page.end_date.set @end_date
        @holiday_list.each do |holiday|
          holiday.create
        end
        init_holiday_list
        page.save :exp_success => options[:exp_success]
      end
    end
  end

  #navigates to Create Calendar page and creates academic calendar by copying the calendar matching the name parameter
  #
  #@param name [String] name of source calendar
  def copy_from(name)
    go_to_holiday_calendar
    if right_source? name
      on CopyHolidayCalendar do |page|
        page.name.set @name
        page.start_date.set @start_date
        page.end_date.set @end_date
      end
    else
      on CopyHolidayCalendar do |page|
        page.choose_different_calendar
      end
      on CalendarSearch do |page|
        page.search_for "Holiday Calendar", name
        page.copy name
      end
      on CreateEditHolidayCalendar do |page|
        page.academic_calendar_name.set @name
        page.calendar_start_date.set @start_date
        page.calendar_end_date.set @end_date
      end
      on CreateEditHolidayCalendar do |page|
        page.save
      end
    end
  end

  def edit(opts={})
    defaults = {
        :exp_success=> true
    }
    options = defaults.merge(opts)
    search
    on(CalendarSearch).edit @name

    if !options[:name].nil? then
      on(CreateEditHolidayCalendar).calendar_name.set options[:name]
    end

    if !options[:start_date].nil? then
      on(CreateEditHolidayCalendar).start_date.set options[:start_date]
    end

    if !options[:end_date].nil? then
      on(CreateEditHolidayCalendar).end_date.set options[:end_date]
    end

    on(CreateEditHolidayCalendar).save :exp_success => options[:exp_success]
    set_options(options) if options[:exp_success]

  end

  #def copy(name)
  #  on CalendarSearch do |page|
  #    page.search_for "Holiday Calendar", name
  #    page.copy name
  #  end
  #  on CreateEditHolidayCalendar do |page|
  #    @name = random_alphanums.strip
  #    page.calendar_name.set @name
  #    page.start_date.set @holiday_list.first.start_date
  #    page.end_date.set @holiday_list.last.start_date
  #    @start_date = page.start_date.text
  #    @end_date = page.end_date.text
  #    page.save
  #    init_holiday_list
  #  end
  #end

  def view(opts={})
    defaults = {
        :perform_search=> true
    }
    options = defaults.merge(opts)
    search if options[:perform_search]
    on(CalendarSearch).view @name
  end

  def delete
    search
    on CalendarSearch do |page|
      page.search_for "Holiday Calendar", @name
      page.hcal_delete name
    end
  end

  def init_holiday_list
    @holiday_list.clear
    sleep 2
    on CreateEditHolidayCalendar do |page|
      page.holiday_table.rows[2..page.holiday_table.rows.count].each do |holiday_row|
        temp_holiday = make Holiday
        temp_holiday.init_holiday(holiday_row) unless holiday_row.cells[CreateEditHolidayCalendar::HOLIDAY_TYPE].text == ""
        @holiday_list.push(temp_holiday) unless temp_holiday.type == "random"
      end
    end
  end


  def search
    go_to_calendar_search
    on CalendarSearch do |page|
      page.search_for "Holiday Calendar", @name
    end
  end

  def right_source?(name)
    on CreateAcadCalendar do |page|
      if page.source_name == name
        return true
      else
        return false
      end
    end
  end

  def make_official
    on CreateEditHolidayCalendar do |page|
      page.make_official
    end
  end

  def edit_holiday(holiday_type, opts)
    holiday = get_holiday_obj_by_type(holiday_type)
    holiday.edit opts
  end

  def add_holiday(opts)
    defaults = {
        :exp_success=> true,
        :defer_save => false
    }
    options = defaults.merge(opts)

    edit
    options[:holiday].create options
    @holiday_list << options[:holiday] if options[:exp_success] and !options[:defer_save]
  end

  def delete_holidays(opts)
    edit
    opts[:holiday_type_list].each do |holiday_type|
      on(CreateEditHolidayCalendar).delete_holiday(holiday_type)
      @holiday_list.delete(get_holiday_obj_by_type(holiday_type))
    end
    on(CreateEditHolidayCalendar).save
  end

  def get_holiday_obj_by_type(holiday_type)
    @holiday_list.select{|h| h.type == holiday_type}[0]
  end

  def instructional_days_off
    return 1
  end
end

class Holiday

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  attr_accessor :type, :start_date, :start_time, :start_ampm, :end_date, :end_time, :end_ampm, :instructional

  def initialize(browser,opts = {})
    @browser = browser

    defaults = {
        :type=> "random",
        :start_date => "12/12/2012",
        :start_time => "",
        :start_ampm => "",
        :end_date => "",
        :end_time => "",
        :end_ampm => "",
        :instructional => true
    }

    options = defaults.merge(opts)
    set_options(options)
  end

  def create(opts = {})
    defaults = {
        :exp_success=> true,
        :defer_add => false
    }
    options = defaults.merge(opts)

    on CreateEditHolidayCalendar do |page|
      if @type == "random"
        page.holiday_type.select page.select_random_holiday
        @type=page.holiday_type.value
      else
        page.holiday_type.select @type
      end
      page.holiday_start_date.set @start_date
      page.holiday_end_date.set @end_date
      #page.start_time
      if !@instructional then
        page.instructional.clear
        #make sure date is not on a weekend
        if options[:exp_success] then
          st_date = Date.strptime( @start_date , '%m/%d/%Y')
          #e_date = Date.strptime( @end_date , '%m/%d/%Y') unless @end_date.nil? or @end_date == ""
          while st_date.saturday? or st_date.sunday? do
            st_date = st_date + 1
            #  e_date = e_date + 1 unless e_date.nil?
          end
          @start_date = st_date.strftime("%m/%d/%Y")
          page.holiday_start_date.set @start_date
          #if !e_date.nil? then
          #  @end_date = e_date.strftime("%m/%d/%Y")
          #  page.holiday_end_date.set @end_date
          #end
        end
      else
        page.instructional.set
      end
      return if options[:defer_add]
      page.add_link.click
      page.adding.wait_while_present
      page.save if options[:exp_success]
    end

  end

  def edit(opts)
    on CreateEditHolidayCalendar do |page|
      page.edit_start_date @type, opts[:start_date] unless opts[:start_date].nil?
      page.edit_start_time @type, opts[:start_time], opts[:start_ampm]  unless opts[:start_time].nil?
      page.edit_end_date @type, opts[:end_date] unless opts[:end_date].nil?
      page.edit_end_time @type, opts[:end_time], opts[:end_ampm]  unless opts[:end_time].nil?
      page.edit_instructional @type, opts[:instructional] unless opts[:instructional].nil?
      page.save
    end

    set_options(opts)
  end

  def init_holiday(holiday_row)
    @type = holiday_row.cells[CreateEditHolidayCalendar::HOLIDAY_TYPE].text
    @start_date = holiday_row.cells[CreateEditHolidayCalendar::START_DATE].text_field.value
    @start_time = holiday_row.cells[CreateEditHolidayCalendar::START_TIME].text_field.value
    @start_ampm = ""
    if holiday_row.cells[CreateEditHolidayCalendar::START_AMPM].radio.enabled? then
      if holiday_row.cells[CreateEditHolidayCalendar::START_AMPM].radio.set? then
        @start_ampm = "am"
      else
        @start_ampm = "pm"
      end
    end
    @end_date = holiday_row.cells[CreateEditHolidayCalendar::END_DATE].text_field.value
    @end_time = holiday_row.cells[CreateEditHolidayCalendar::END_TIME].text_field.value
    @end_ampm = ""
    if holiday_row.cells[CreateEditHolidayCalendar::END_AMPM].radio.enabled? then
      if holiday_row.cells[CreateEditHolidayCalendar::END_AMPM].radio.set? then
        @end_ampm = "am"
      else
        @end_ampm = "pm"
      end
    end
    @instructional = holiday_row.cells[CreateEditHolidayCalendar::INSTRUCTIONAL].checkbox.set?
  end
end
