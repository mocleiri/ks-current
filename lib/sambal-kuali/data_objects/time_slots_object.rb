# Stores test data for creating/editing and validating a group of time slots for 1 or more term-types
#
# Class attributes are initialized with default data unless values are explicitly provided
#
# Typical usage: (with optional setting of explicit data value in [] )
#
#   To create 3 time-slots that shouldn't conflict with any existing ones: (see comment in add_unused_time_slots method for more detail)
#
#       @time_slots = create TimeSlots
#       @time_slots.add_unused_time_slots(3)
#
#   To add a specific time-slot (accepting defaults for start-time and end-time):
#
#       @time_slots = create TimeSlots, :term_types => ["Spring - Full", "Fall - Full"]           <- this will show all time-slots for both term-types
#       time_slot_to_add = make TimeSlots::TimeSlot, :term_type => "Spring - Full", :days => "T"  <- this will then add a time-slot for a specific term-type
#       @time_slots.add_new_time_slot( time_slot_to_add )
#
# Note the use of the ruby options hash pattern re: setting attribute values
class TimeSlots

  require 'date'

  include Foundry
  include DataFactory
  include Workflows

  attr_accessor :term_types

  attr_reader   :new_time_slots

  # provides default data
  # initialize is generally called using TestFactory Foundry .make or .create methods
  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :term_types => [ "Fall - Full" ],
        :new_time_slots => []
    }

    options = defaults.merge(opts)

    set_options(options)
  end

  # creates the TimeSlots-object and shows all time-slots for the specific term-types
  def create
    show_time_slots
  end #END-create

  # adds a new time-slot, validating that it was added successfully and adding it to it's internal list of added time-slots (for validation later)
  def add_new_time_slot( time_slot )
    time_slot.code = on(TimeSlotMaintenance).add_new_time_slot time_slot
    new_time_slots << time_slot
  end

  def edit_time_slot(opts)
    defaults = {
    }
    options = defaults.merge(opts)
    on TimeSlotMaintenance do |page|
      page.edit_time_slot(options)
    end
  end

  # adds a new time-slot but performs no validation and does not store in the internal list
  def add_new_time_slot_without_validation( time_slot )
    on(TimeSlotMaintenance).add_time_slot_without_validation(time_slot)
  end

  # duplicates a time-slot that already exists in the table (by it's code) -- note, does not validate nor store the result
  def add_duplicate_time_slot(code)
    target_timeslot_row = on(TimeSlotMaintenance).target_results_row(code)
    time_slot = make TimeSlots::TimeSlot, :term_type => target_timeslot_row[TimeSlotMaintenance::TIME_SLOT_RESULTS_TERM_TYPE].text,
                     :days => target_timeslot_row[TimeSlotMaintenance::TIME_SLOT_RESULTS_DAYS].text.gsub( /\s+/, ''),
                     :start_time => target_timeslot_row[TimeSlotMaintenance::TIME_SLOT_RESULTS_START_TIME].text.split(" ")[0],
                     :start_time_am_pm => target_timeslot_row[TimeSlotMaintenance::TIME_SLOT_RESULTS_START_TIME].text.split(" ")[1],
                     :end_time => target_timeslot_row[TimeSlotMaintenance::TIME_SLOT_RESULTS_END_TIME].text.split(" ")[0],
                     :end_time_am_pm => target_timeslot_row[TimeSlotMaintenance::TIME_SLOT_RESULTS_END_TIME].text.split(" ")[1]
    add_new_time_slot_without_validation(time_slot)
  end

  # adds a specified number of time-slots, generating a quasi-unique set of start/end-times
  #
  # note: refer to the singular add method (below) for more details
  def add_unused_time_slots(termType = "Fall - Full", nbr)
    (1..nbr).each do
      add_unused_time_slot( termType )
    end
  end

  # adds a time-slot, generating a quasi-unique set of start/end-times
  #
  # To facilitate uniqueness, this will use a day-combination that isn't widely used by ref-data and will then generate
  # a new set of start/end-times based on a random number of minutes later than the last start-time currently in the
  # table.
  #
  # note: current impl detects if there is a failure to add a new timeslot (for example, if there is a collision with
  # an already existing timeslot) and will attempt to add the timeslot again (using a newly-generated set of
  # start/end-times).  However, if collisions recur multiple times, then an exception will be raised.
  def add_unused_time_slot(termType = "Fall - Full")

    startTime = increment_time( on(TimeSlotMaintenance).get_start_time_of_last_row )

    i = 0
    max_attempts_to_create_unique_timeslots = 5
    done_creating_unique_timeslot = false
    until i > max_attempts_to_create_unique_timeslots || done_creating_unique_timeslot
      i += 1

      sTime, sAmPm = startTime.split(" ")
      eTime, eAmPm = increment_time( startTime ).split(" ")

      begin
        add_new_time_slot(make TimeSlots::TimeSlot, :term_type => termType, :start_time => sTime, :start_time_am_pm => sAmPm, :end_time => eTime, :end_time_am_pm => eAmPm)
        done_creating_unique_timeslot = true
      rescue Exception => e
        if i < max_attempts_to_create_unique_timeslots
          puts "Error generating unused time-slot (using start-time of #{startTime}); attempt #{i} of #{max_attempts_to_create_unique_timeslots}; will try again."
          startTime = increment_time( startTime )
        else
          puts "Error generating unused time-slot, abandoning after #{max_attempts_to_create_unique_timeslots} attempts; error was: #{e.message}"
          throw e
        end
      end

    end

  end

  # increments the time-string by some number of minutes
  # if the 'number of minutes to increment by' is not provided, it will be incremented by a random-value between 1-5
  #
  # note: the time-string provided must be of format: '02:35 AM', and will be returned in the same format
  def increment_time(full_time_with_am_pm, x_minutes_to_increment_by = 1+ rand(5))
    ( DateTime.strptime(full_time_with_am_pm, '%I:%M %p') + (x_minutes_to_increment_by.to_f/1440) ).strftime( '%I:%M %p' )
  end

  def delete(code)
    on TimeSlotMaintenance do |page|
      page.delete_time_slot(code)
    end
  end


  def show_time_slots
    go_to_manage_time_slots

    on TimeSlotMaintenance do |page|
      page.select_time_slot_types(term_types)
      page.show_time_slots
    end
  end

  def print_new_time_slots_to_console
    puts "Newly-added time slots:"
    new_time_slots.each do |slot|
      slot.print_time_slot_to_console
    end
  end

  def print_all_time_slot_codes_to_console
    ts_list = []
    puts "All time slot codes:"
    on TimeSlotMaintenance do |page|
      ts_list = page.get_time_slot_code_list
    end
    puts ts_list
  end

  # Stores test data for creating/editing and validating an individual time slot
  #
  # Class attributes are initialized with default data unless values are explicitly provided
  #
  # Note that typical usage of this should be via 'make' not 'create' and then pass to an add-method in the TimeSlots object
  #
  # For examples, see comment at the top of the file
  #
  # Note the use of the ruby options hash pattern re: setting attribute values
  class TimeSlot

    include DataFactory

    # type: string generally set using options hash
    attr_accessor :code,
                  :term_type,
                  :days,
                  :start_time,
                  :start_time_am_pm,
                  :end_time,
                  :end_time_am_pm

    # provides default data
    #
    # initialize is generally called using TestFactory Foundry .make or .create methods
    def initialize(browser, opts={})
      @browser = browser

      defaults = {
          :code => "",
          :term_type => "Fall - Full",
          :days => "MTWHFSU",
          :start_time => "12:01",
          :start_time_am_pm => "PM",
          :end_time => "12:06",
          :end_time_am_pm => "PM"
      }

      options = defaults.merge(opts)

      set_options(options)
    end

    def print_time_slot_to_console
      puts "Code #{code}, Term type #{term_type}, Days #{days}, Start #{start_time} #{start_time_am_pm}, End #{end_time} #{end_time_am_pm}"
    end
  end

end