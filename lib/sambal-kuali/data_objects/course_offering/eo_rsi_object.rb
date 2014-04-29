class EoRsiObject < DataFactory

  include Foundry
  include DateFactory
  include StringFactory
  include Workflows

  #generally set using options hash
  attr_accessor :ao_driven,
                :ao_code,
                :status,
                :day,
                :room,
                :facility,
                :override_matrix
  attr_writer   :start_time,
                :end_time

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :ao_driven => false,
        :ao_code => nil,
        :ao_driver_activity => nil,
        :status => 'Draft',
        :day => 'Day 1',
        :start_time => '11:00 AM',
        :end_time => '12:00 PM',
        :facility => 'MTH', #TODO: use object
        :facility_long =>  'Mathematics Bldg.',
        :room => '0304'
    }

    set_options(defaults.merge(opts))
    @start_time.upcase!
    @end_time.upcase!
  end

  def start_time
    @start_time.rjust(8,'0')
  end

  def end_time
    @end_time.rjust(8,'0')
  end
  # ...

  #created automatically by CO configured use exam matrix
  #def create


  def edit opts={}

    defaults = {
        :exp_success=> true,
        :defer_save => false,
        :do_navigation => true
    }
    options = defaults.merge(opts)

    if options[:do_navigation]
      on(ManageCourseOfferings).view_exam_offerings
    end

    if @ao_driven
      edit_row =  on(ViewExamOfferings).eo_by_ao_target_row(@ao_code) #TODO: AO Cluster
    else
      edit_row = on(ViewExamOfferings).co_target_row
    end

    on(ViewExamOfferings).edit_rsi(edit_row)

    if options[:day]
      on(ViewExamOfferings).rsi_day(edit_row).select options[:day]
    end

    if options[:start_time]
      on(ViewExamOfferings).rsi_start_time(edit_row).set options[:start_time]
    end

    if options[:end_time]
      on(ViewExamOfferings).rsi_end_time(edit_row).set options[:end_time]
    end

    if options[:room]
      on(ViewExamOfferings).rsi_room(edit_row).set options[:room]
    end

    if options[:facility]
      on(ViewExamOfferings).rsi_facility(edit_row).set options[:facility]
    end

    if ! options[:override_matrix].nil?
      if options[:override_matrix]
        on(ViewExamOfferings).override_checkbox(edit_row).set
      else
        on(ViewExamOfferings).override_checkbox(edit_row).clear
      end
    end

    on(ViewExamOfferings).save_edit(edit_row) unless options[:defer_save]

    update_options(opts) if options[:exp_success]
  end

end

class EoRsiCollection < CollectionsFactory

  contains EoRsiObject

end

