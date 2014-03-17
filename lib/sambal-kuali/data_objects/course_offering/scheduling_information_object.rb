# stores test data for creating/editing and validating scheduling information data and provides convenience methods for navigation and data entry
#
# SchedulingInformationObject is a child of a ActivityOffering (Requested scheduling information - rsi and actual - asi)
#
# class attributes are initialized with default data unless values are explicitly provided
#
# Typical usage: (with optional setting of explicit data value in [] )
#
# rsi_list[0] = make SchedulingInformationObject, :days=> M, ...
#
# @activity_offering.edit_offering :requested_scheduling_information_list = rsi_list
#
#create generally called from ActivityOffering
# Note the use of the ruby options hash pattern re: setting attribute values
class SchedulingInformationObject
  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  #boolean - generally set using options hash
  attr_accessor :tba, :process, :isRSI
  #generally set using options hash
  attr_accessor :days,
                :start_time,
                :start_time_ampm,
                :end_time,
                :end_time_ampm,
                :facility,
                :facility_long_name,
                :room,
                :features_list
  #object
  attr_accessor :parent_ao


  alias_method :tba?, :tba
  alias_method :process?, :process

  # provides default data:
  # defaults = {
  #    :tba  => false,
  #    :days  => "MWF",
  #    :start_time  => "01:00",
  #    :start_time_ampm  => "pm",
  #    :end_time  => "02:00",
  #    :end_time_ampm  => "pm",
  #    :facility  => "ARM",
  #    :facility_long_name  => "Reckord Armory",
  #    :room  => "126",
  #    :features_list  => [],
  #    :process => true,
  #     :isRSI => true
  # }
  # initialize is generally called using TestFactory Foundry .make or .create methods
  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :isRSI => true,
        :tba  => false,
        :days  => "MWF",
        :start_time  => "10:00",
        :start_time_ampm  => "am",
        :end_time  => "10:50",
        :end_time_ampm  => "am",
        :facility  => "PHYS",
        :facility_long_name  => "PHYS",
        :room  => "4102",
        :features_list  => [],
        :use_std_ts => false
        #     :process => true
    }
    options = defaults.merge(opts)
    set_options(options)
  end

  # adds Scheduling Information request based on class attributes
  #
  # add must be completed by calling save or save_and_process (allows adding of multiple rsis)
  #
  # generally called from ActivityOffering class
  def create
    if isRSI then
      on ActivityOfferingMaintenance do |page|
        page.view_requested_scheduling_information
        page.add_tba.wait_until_present

        @end_time_ampm.upcase! unless @end_time_ampm.nil?
        @start_time_ampm.upcase! unless @start_time_ampm.nil?

        if @tba
          page.add_tba.set
        else
          page.add_tba.clear
        end

        if @days != nil then
          page.add_start_time.click
          page.loading.wait_while_present
          page.add_days.set @days
          sleep 2
        end

        if @start_time != nil
          page.add_start_time.click
          page.loading.wait_while_present
          page.add_start_time.set @start_time + " " + @start_time_ampm
          page.add_start_time.click
          page.loading.wait_while_present
        end

        if @end_time != nil then
          #approved_for_nonStandard_timeslots = page.non_std_ts_checkbox.checked?
          if @use_std_ts
            formatted_end_time = DateTime.strptime("#{@end_time} #{@end_time_ampm}", '%I:%M %p').strftime( '%I:%M %p' )
            page.end_time_select_populate_list
            if page.end_time_select.present?
              page.end_time_select.select(formatted_end_time)
            else
              page.add_end_time.set @end_time.to_s[0]
              page.loading.wait_while_present
              page.select_end_time(formatted_end_time)
            end
          else
            page.end_time_select_populate_list
            page.add_end_time.set @end_time + " " + @end_time_ampm
          end
        end

        if @facility != nil then
          page.add_facility.set @facility
        end

        if @room != nil then
          page.add_room.set @room
        end

        #opts["features_list"] TODO if implemented
        page.add_new_scheduling_information

      end
    else
      raise "error: cannot add Actual Scheduling Information"
    end
  end

  def init_existing(ao_table_row)
    @isRSI = ao_table_row.cells[ManageCourseOfferings::AO_DAYS].span(index: 1).present? and ao_table_row.cells[ManageCourseOfferings::AO_DAYS].span(index: 1).attribute_value("class") == "uif-scheduled-dl"
    @days = ao_table_row.cells[ManageCourseOfferings::AO_DAYS].text
    st_time = ao_table_row.cells[ManageCourseOfferings::AO_ST_TIME].text
    if st_time != "" then
      @start_time = st_time.split[0]
      @start_time_ampm = st_time.split[1]
    else
      @start_time = ""
      @start_time_ampm = ""
    end
    end_time = ao_table_row.cells[ManageCourseOfferings::AO_END_TIME].text
    if end_time != "" then
      @end_time = end_time.split[0]
      @end_time_ampm = end_time.split[1]
    else
      @end_time = ""
      @end_time_ampm = ""
    end
    @facility_long_name = ""
    @facility = ao_table_row.cells[ManageCourseOfferings::AO_BLDG].text
    @room = ao_table_row.cells[ManageCourseOfferings::AO_ROOM].text
  end

  # compares 2 instances of SchedulingInformation for field-equality
  # note: by default, the comparison ignores the isRSI-field
  # @example
  #   SchedulingInformationObject.compare( @si1, @si2 )
  #
  # @param instance1 -- a SchedulingInformationObject object
  # @param instance2 -- a SchedulingInformationObject object
  # @param opts [Hash] key => value
  def self.compare(instance1, instance2, opts={})

    defaults = {
        :ignore_si_type => true
    }
    options = defaults.merge(opts)


    if instance1.nil? && instance2.nil?
      puts 'EQUAL: both instances are nil'
      return true
    end
    if instance1.nil?
      puts 'NOT EQUAL: instance1 is nil'
      return false
    end
    if instance2.nil?
      puts 'NOT EQUAL: instance2 is nil'
      return false
    end

    if options[:ignore_si_type]
      puts 'Ignoring isRSI while testing for equality'
    else
      if instance1.isRSI != instance2.isRSI
        puts 'NOT EQUAL: isRSI is different (' + instance1.isRSI.to_s + ":" + instance2.isRSI.to_s + ")"
        return false
      end
    end

    if instance1.tba != instance2.tba
      puts 'NOT EQUAL: tba is different (' + instance1.tba + ":" + instance2.tba + ")"
      return false
    end

    if instance1.days != instance2.days
      puts 'NOT EQUAL: days is different (' + instance1.days + ":" + instance2.days + ")"
      return false
    end

    if instance1.start_time != instance2.start_time
      puts 'NOT EQUAL: start_time is different (' + instance1.start_time + ":" + instance2.start_time + ")"
      return false
    end

    if instance1.start_time_ampm != instance2.start_time_ampm
      puts 'NOT EQUAL: start_time_ampm is different (' + instance1.start_time_ampm + ":" + instance2.start_time_ampm + ")"
      return false
    end

    if instance1.end_time != instance2.end_time
      puts 'NOT EQUAL: end_time is different (' + instance1.end_time + ":" + instance2.end_time + ")"
      return false
    end

    if instance1.end_time_ampm != instance2.end_time_ampm
      puts 'NOT EQUAL: end_time_ampm is different (' + instance1.end_time_ampm + ":" + instance2.end_time_ampm + ")"
      return false
    end

    if instance1.facility != instance2.facility
      puts 'NOT EQUAL: facility is different (' + instance1.facility + ":" + instance2.facility + ")"
      return false
    end

    # bug in app prevents testing of this field
    # KSENROLL-6931
    #    if instance1.facility_long_name != instance2.facility_long_name
    #      puts 'NOT EQUAL: facility_long_name is different (' + instance1.facility_long_name + ":" + instance2.facility_long_name + ")"
    #      return false
    #    end

    if instance1.room != instance2.room
      puts 'NOT EQUAL: room is different (' + instance1.room + ":" + instance2.room + ")"
      return false
    end


    return true
  end

  def edit(opts)
    if isRSI then
      on ActivityOfferingMaintenance do |page|
        page.view_requested_scheduling_information
        target_row = page.target_rsi_row(si_key)
        page.edit_rsi_row(target_row)
        sleep 2

        if opts[:tba]
          page.add_tba.set
        else
          page.add_tba.clear
        end

        if opts[:days] != nil then
          page.add_days.set opts[:days]
        end

        if opts[:start_time] != nil then
          page.add_start_time.click
          page.loading.wait_while_present
          page.add_start_time.set opts[:start_time] + " " + opts[:start_time_ampm].upcase
          sleep(1)
        end

        if opts[:end_time] != nil then
          page.add_end_time.click
          page.loading.wait_while_present
          page.add_end_time.set opts[:end_time] + " " + opts[:end_time_ampm].upcase
        end

        if opts[:facility] != nil then
          page.add_facility.set opts[:facility]
        end

        if opts[:room] != nil then
          page.add_room.set opts[:room]
        end

        #opts["features_list"] TODO if implemented
        page.add_new_scheduling_information
      end
      set_options(opts)
      @end_time_ampm.upcase!
      @start_time_ampm.upcase!
    else
      raise "error: cannot edit Actual Scheduling Information"
    end
  end

  #def add(opts)
  #end

  def si_key
    "#{@days}#{@start_time}#{@start_time_ampm}"
  end

  #similar method in page object?????
  def target_row_by_si_key
    on ActivityOfferingMaintenance do |page|
      page.view_requested_scheduling_information
      page.requested_sched_info_table.rows.each do |row|
        row_key = "#{row.cells[ActivityOfferingMaintenance::DAYS_COLUMN].text}#{row.cells[ActivityOfferingMaintenance::ST_TIME_COLUMN].text}".delete(' ')
        return row unless row_key != si_key
      end
    end
    return nil
  end

  def delete
    on ActivityOfferingMaintenance do |page|
      row = page.target_rsi_row(self.si_key)
      page.delete_rsi_row(row)
    end
    @parent_ao.requested_scheduling_information_list.delete(self)
  end
end

class SchedulingInformationCollection < CollectionsFactory

  contains SchedulingInformationObject

  def by_key(key)
    list = self.select {|si| si.si_key == key }
    list.nil?? nil : list[0]
  end

end
