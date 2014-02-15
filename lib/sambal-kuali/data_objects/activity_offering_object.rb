# stores test data for creating/editing and validating activity offerings and provides convenience methods for navigation and data entry
#
# ActivityOffering objects contain the following objects: SeatPool, DeliveryLogistic (requested and actual) and Personnel (Affiliated Personnel)
#
# ActivityOffering is a child of a CourseOffering
#
# class attributes are initialized with default data unless values are explicitly provided
#
# Typical usage: (with optional setting of explicit data value in [] )
#  @activity_offering = make ActivityOffering, [:seat_pool_list => {},...]
#  @activity_offering.create
# OR alternatively 2 steps together as
#  @activity_offering = create ActivityOffering, [:seat_pool_list => {},...]
# Note the use of the ruby options hash pattern re: setting attribute values
class ActivityOffering
  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows
  include Comparable

  #type: string generally set using options hash
  attr_accessor :code,
                :status,
                :format,
                :activity_type,
                :max_enrollment,
                :seat_remaining_percent,
                :course_url,
                :aoc_private_name,
                :subterm
  #type: hash - generally set using options hash
  attr_accessor :actual_scheduling_information_list,
                :requested_scheduling_information_list,
                :seat_pool_list
  #array - generally set using options hash
  attr_accessor :personnel_list,
                :colocate_ao_list
  #boolean - generally set using options hash
  attr_accessor :requires_evaluation,
                :honors_course,
                :create_by_copy,
                :colocated,
                :colocate_shared_enrollment
  #type ActivityOffering object - generally set using options hash
  attr_accessor :parent_course_offering
  #type Waitlist object - generally set using options hash
  attr_accessor :waitlist_config

  # [String]
  OFFERED_STATUS = "Offered"
  # [String]
  DRAFT_STATUS = "Draft"
  # [String]
  APPROVED_STATUS = "Approved"


  # provides default data:
  #
  #  defaults = {
  #    :parent_course_offering => ""
  #    :status => "Draft",
  #    :format => "Lecture Only",
  #    :activity_type => "Lecture",
  #    :max_enrollment => 100,
  #    :actual_scheduling_information_list => {},
  #    :requested_scheduling_information_list => {} ,
  #    :personnel_list => [],
  #    :seat_pool_list => {} ,
  #    :course_url => "www.test_course.com",
  #    :evaluation => true,
  #    :honors_course => true,
  #    :colocated => false,
  #    :colocate_ao_list => [],
  #    :colocate_shared_enrollment => false,
  #    :subterm => "None"
  #    :waitlist_config => (make Waitlist)
  #  }
  # some basic e.g. list/hash values:
  # :seat_pool_list =>  {"random"=> (make SeatPool)}
  # :requested_scheduling_information_list => {"default"=> (make SchedulingInformation)}
  # :personnel_list => Array.new(1){make Personnel}
  # :colocate_ao_list => Array.new(1){make ActivityOffering}

  # initialize is generally called using TestFactory Foundry .make or .create methods
  def initialize(browser, opts={})
    @browser = browser

    # :seat_pool_list =  {"random"=> (make SeatPool)}
    # :requested_scheduling_information_list = {"default"=> (make SchedulingInformation)}
    # :personnel_list => [] -- Array.new(1){make Personnel}

    defaults = {
        :parent_course_offering => (make CourseOffering),
        :status => "Draft",
        :format => nil, #nil means use default
        :activity_type => "Lecture",
        :max_enrollment => 100,
        :actual_scheduling_information_list => {},
        :requested_scheduling_information_list => {},
        :personnel_list => [] ,
        :seat_pool_list => {},
        :course_url => "www.test_course.com",
        :requires_evaluation => true,
        :honors_course => true,
        :aoc_private_name => :default_cluster,
        :create_by_copy => nil, #if true create copy using :ao_code
        :colocated => false,
        :colocate_ao_list => [],
        :colocate_shared_enrollment => false,
        :subterm => nil,
        :waitlist_config => (make Waitlist)
    }

    options = defaults.merge(opts)

    #@format = options[:format]
    #@activity_type = options[:activity_type]

    set_options(options)

  end

  def <=>(other)
    @code <=> other.code
  end

  #create one or many simple aos without details
  #
  #   @example
  #   @activity_offering.create_simple :number_aos_to_create => 3 (defaults is 1)
  #
  #
  #   @param opts [Hash] :number_aos_to_create => integer (1 or more)
  #   @returns list of AO codes created
  def create_simple  opts={}

    defaults = {
        :number_aos_to_create => 1
    }
    options = defaults.merge(opts)

    @parent_course_offering.manage

    on ManageCourseOfferings do |page|
      begin
        pre_add_ao_list = page.codes_list
      rescue
        pre_add_ao_list = nil
      end

      post_add_ao_list = []
      #if page.codes_list.length == 0
      sleep 2
      page.add_activity
      page.format.select @format unless @format.nil?
      page.loading.wait_while_present
      sleep 2
      page.activity_type.select @activity_type #unless @activity_type.nil?
      page.quantity.set options[:number_aos_to_create]
      page.complete_add_activity
      post_add_ao_list = page.codes_list
      #end
      if(pre_add_ao_list != nil )
        new_code_list =  post_add_ao_list - pre_add_ao_list
      else
        new_code_list =  post_add_ao_list
      end

      return new_code_list
    end
  end

  #navigates to activity offering edit page and sets up activity offering based on class attributes
  def create
    #@parent_course_offering.manage

    if @create_by_copy then
      on ManageCourseOfferings do |page|
        pre_copy_list = page.get_cluster_assigned_ao_list(@aoc_private_name)
        page.copy(@code, @aoc_private_name)
        post_copy_list = page.get_cluster_assigned_ao_list(@aoc_private_name)
        @code = (post_copy_list - pre_copy_list).first
        return
      end
    end

    on ManageCourseOfferings do |page|
      pre_add_ao_list = page.codes_list
      post_add_ao_list = []
      #if page.codes_list.length == 0
      #sleep 2
      page.add_activity
      page.format.select @format unless @format.nil?
      page.loading.wait_while_present
      page.activity_type.wait_until_present
      page.activity_type.select @activity_type unless @format.nil?
      page.quantity.set "1"
      page.complete_add_activity
      sleep 1
      post_add_ao_list = page.codes_list
      #end
      new_code =  post_add_ao_list - pre_add_ao_list
      @code = new_code[0]
    end

    init = {
        :parent_course_offering=> @parent_course_offering,
        :max_enrollment => @max_enrollment,
        :actual_scheduling_information_list => @actual_scheduling_information_list,
        :requested_scheduling_information_list => @requested_scheduling_information_list,
        :personnel_list => @personnel_list ,
        :seat_pool_list => @seat_pool_list,
        :course_url => @course_url,
        :requires_evaluation => @requires_evaluation,
        :honors_course => @honors_course,
        :colocated => @colocated,
        :colocate_ao_list => @colocate_ao_list,
        :colocate_shared_enrollment => @colocate_shared_enrollment,
        :subterm => @subterm,
        :waitlist_config => @waitlist_config
    }

    edit(init)
  end

  def init_existing(ao_table_row, parent_co, cluster_name)
    @code =  ao_table_row.cells[ManageCourseOfferings::AO_CODE].text
    @status = ao_table_row.cells[ManageCourseOfferings::AO_STATUS].text
    @activity_type = ao_table_row.cells[ManageCourseOfferings::AO_TYPE].text
    @max_enrollment = ao_table_row.cells[ManageCourseOfferings::AO_MAX_ENR].text
    delivery_days = ao_table_row.cells[ManageCourseOfferings::AO_DAYS].text
    if delivery_days != "" then
      #there are scheduling information
      si_list = get_existing_scheduling_information (ao_table_row)
      si_list.each do |si_object|
        if si_object.isRSI then
          @requested_scheduling_information_list[si_object.si_key] = si_object
        else
          @actual_scheduling_information_list[si_object.si_key] = si_object
        end
      end
    end
    @aoc_private_name = cluster_name
    @parent_course_offering = parent_co
  end

  def get_existing_scheduling_information(ao_table_row)
    si_list = []
    is_rsi = ao_table_row.cells[ManageCourseOfferings::AO_DAYS].span(index: 1).present? and ao_table_row.cells[ManageCourseOfferings::AO_DAYS].span(index: 1).attribute_value("class") == "uif-scheduled-dl"
    si_days = ao_table_row.cells[ManageCourseOfferings::AO_DAYS].text.split("\n")
    st_times = ao_table_row.cells[ManageCourseOfferings::AO_ST_TIME].text.split("\n")
    end_times = ao_table_row.cells[ManageCourseOfferings::AO_END_TIME].text.split("\n")
    fac_names = ao_table_row.cells[ManageCourseOfferings::AO_BLDG].text.split("\n")
    fac_long_names = []
    fac_long_names_tooltip = ao_table_row.cells[ManageCourseOfferings::AO_BLDG].hidden.value
    if fac_long_names_tooltip[/(?<=, ')\S*/] == "<span"
      #createTooltip('u432_line0_line10', '<span class=&quot;uif-scheduled-dl&quot; >Tawes Fine Arts Bldg.</span><br><span class=&quot;uif-scheduled-dl&quot; >Mathematics Bldg.</span>', {always
      fac_long_names = fac_long_names_tooltip.scan(/(?<=;uif-scheduled-dl&quot; \>).*?(?=\<\/span)/)
    else
      #createTooltip('u432_line0_line2', 'Tawes Fine Arts Bldg.', {always...
      fac_long_names << fac_long_names_tooltip[/(?<=, ').*?(?=',)/]
    end
    rooms = ao_table_row.cells[ManageCourseOfferings::AO_ROOM].text.split("\n")

    i=0
    si_days.each do |day|
      st, st_ampm = st_times[i].split unless st_times[i].nil?
      et, et_ampm = end_times[i].split unless end_times[i].nil?
      si = make SchedulingInformation,
                :isRSI => is_rsi,
                :days => si_days[i],
                :start_time => st,
                :start_time_ampm => st_ampm,
                :end_time => et,
                :end_time_ampm => et_ampm,
                :facility => fac_names[i],
                :facility_long_name => fac_long_names[i],
                :room => rooms[i]
      si_list << si
      i += 1
    end

    si_list
  end

  #navigates activity offering edit page and updates activity offering based on class attributes
  # @example - must always call save
  #  @activity_offering.edit :honors_course=> true
  #  @activity_offering.save
  #
  # NB: 'save' is a separate step from edit as it allows validation steps to occur during the edit process
  #
  # @param opts [Hash] key => value for attribute to be updated -
  #   additional opts :edit_already_started (bool), :send_to_scheduler (bool) :TODO :defer_save
  def edit opts={}

    defaults = {
        :defer_save => true,
        :edit_already_started => false,
        :send_to_scheduler => false
    }
    options = defaults.merge(opts)

    on(ManageCourseOfferings).edit @code unless options[:edit_already_started]

    edit_code options
    edit_subterm options
    edit_colocation options
    edit_max_enrollment_no_colocation options
    edit_non_std_timeslots options
    edit_requested_scheduling_information options
    edit_course_url options
    edit_evaluation options
    edit_honors_course options
    edit_personnel_list options
    edit_seat_pool_list options
    edit_waitlist_config options

    on(ActivityOfferingMaintenance).send_to_scheduler if options[:send_to_scheduler]
    self.save unless options[:defer_save]

  end #END: edit

  # PRIVATE helper methods for edit()

  def edit_code opts
    if opts[:code].nil?
      return nil
    end
    on(ActivityOfferingMaintenance).activity_code.set opts[:code]
    @code = opts[:code]
  end #END: edit_code

  def edit_subterm opts

    if opts[:subterm] != nil
      sleep 1
      on ActivityOfferingMaintenance do |page|
        page.change_subterm opts[:subterm]
        @subterm = opts[:subterm]
      end
    end
  end

  def edit_colocation opts={}
    if on(ActivityOfferingMaintenance).colocated_checkbox.exists?
      is_colo_set = on(ActivityOfferingMaintenance).colocated_checkbox.set?


      if !opts[:colocated].nil?
        if opts[:colocated] == false
          edit_break_colocation if is_colo_set
          return
        else
          on(ActivityOfferingMaintenance).select_colocated_checkbox unless is_colo_set
          @colocated = true
          is_colo_set = true
        end
      end
      return unless is_colo_set

      # else perform normal colo-EDIT
      on ActivityOfferingMaintenance do |page|
        # add the colo-COs to this CO
        if !opts[:colocate_ao_list].nil?
          opts[:colocate_ao_list].each do |ao_to_colo|
            page.colocated_co_input_field.value = ao_to_colo.parent_course_offering.course
            page.colocated_ao_input_field.value = ao_to_colo.code
            page.add_colocated
            page.add_colocate_ao_confirmation_add
            @colocate_ao_list << ao_to_colo
          end
        end

        if !opts[:colocate_shared_enrollment].nil?
          if opts[:colocate_shared_enrollment]
            page.select_separately_manage_enrollment_radio #toggling to this and back is required or an error generates on submit
            page.select_jointly_share_enrollment_radio
            page.colocated_shared_max_enrollment_input_field.set opts[:max_enrollment]
            @max_enrollment = opts[:max_enrollment]
          else # ie: 'separately manage'
            page.select_separately_manage_enrollment_radio
            page.edit_separate_max_enr(@parent_course_offering.course, @code, @max_enrollment)
          end
        end

      end
    end

  end #END: edit_colocation
  private :edit_colocation

  def edit_break_colocation
    on ActivityOfferingMaintenance do |page|
      page.select_colocated_checkbox if page.colocated_checkbox.set?
      page.break_colocation
    end

    @colocated = false
  end #END: edit_break_colocation
  private :edit_break_colocation

  def edit_max_enrollment_no_colocation opts={}
  if on(ActivityOfferingMaintenance).colocated_checkbox.exists?
    return if on(ActivityOfferingMaintenance).colocated_checkbox.set?
  end

    if opts[:max_enrollment] != nil
      on ActivityOfferingMaintenance do |page|
        page.total_maximum_enrollment.set opts[:max_enrollment]
        page.total_maximum_enrollment.fire_event "onchange"
        @max_enrollment = opts[:max_enrollment]
      end
    end
  end #END: edit_max_enrollment_no_colocation
  private :edit_max_enrollment_no_colocation

  def edit_non_std_timeslots opts={}
    if opts[:allow_non_std_timeslots].nil?
      return nil
    end
    on ActivityOfferingMaintenance do |page|
      page.view_requested_scheduling_information
      if opts[:allow_non_std_timeslots]
        page.approve_non_std_ts
      else
        page.disallow_non_std_ts
      end
    end
  end
  private :edit_non_std_timeslots

  def edit_requested_scheduling_information opts={}

    if opts[:requested_scheduling_information_list].nil? || opts[:requested_scheduling_information_list].empty?
      return nil
    end

    #'save' vs 'save and process' determined by first rsi
    first_rsi = opts[:requested_scheduling_information_list].values[0]
    #list of requests added with updated keys
    requests_added = {}

    opts[:requested_scheduling_information_list].values.each do |request|
      request.create
      requests_added["#{request.days}#{request.start_time}#{request.start_time_ampm.upcase}".delete(' ')] = request
    end

  end #END: edit_request_scheduling_information
  private :edit_requested_scheduling_information

  def edit_course_url opts={}

    if opts[:course_url].nil?
      return nil
    end

    on(ActivityOfferingMaintenance).course_url.set opts[:course_url]
    @course_url = opts[:course_url]

  end #END: edit_course_url
  private :edit_course_url

  def edit_evaluation opts={}

    if opts[:requires_evaluation].nil?
      return nil
    end

    on ActivityOfferingMaintenance do |page|
      if opts[:requires_evaluation]
        page.requires_evaluation.set
      else
        page.requires_evaluation.clear
      end
    end

    @requires_evaluation =  opts[:requires_evaluation]

  end #END: edit_evaluation
  private :edit_evaluation

  def edit_honors_course opts={}

    if opts[:honors_course].nil?
      return nil
    end

    on ActivityOfferingMaintenance do |page|
      if opts[:honors_course]
        page.honors_flag.set
      else
        page.honors_flag.clear
      end
    end

    @honors_course = opts[:honors_course]

  end #END: edit_honors_course
  private :edit_honors_course

  def edit_personnel_list opts={}

    if opts[:personnel_list].nil?
      return nil
    end

    opts[:personnel_list].each do |person|
      person.create
    end

    @personnel_list = opts[:personnel_list]

  end #END: edit_personnel_list
  private :edit_personnel_list

  def edit_seat_pool_list opts={}

    if opts[:seat_pool_list].nil?
      return nil
    end

    opts[:seat_pool_list].each do |key,seat_pool|
      seat_pool.add_seatpool(seatpool_populations_used)
      if !seat_pool.exp_add_succeed then
        @seat_pool_list.delete(key)
      end
    end

  end #END: edit_seat_pool_list
  private  :edit_seat_pool_list

  def edit_waitlist_config opts={}

    return nil if opts[:waitlist_config].nil?
    opts[:waitlist_config].edit

  end #END: edit_waitlist_config
  private :edit_waitlist_config

  def add_personnel person
    person.create
    @personnel_list << person
  end

  def delete_personnel person
    on ActivityOfferingMaintenance do |page|
      page.update_person_name(person.id,'blah, blah')
      page.delete_personnel(person.id)
    end
  end

  #completes activity offering edit operation
  #this is a separate step from edit as it allows validation steps to occur during the edit process
  def save
    on ActivityOfferingMaintenance do |page|
      page.submit
    end
  end

  def save_and_remain_on_page()
    on ActivityOfferingMaintenance do |page|
      page.save
    end
  end

  #calculates the expected number of seats remaining
  #
  #@return [int] expected number of seats remaining
  def seats_remaining
    seats_used = 0
    @seat_pool_list.each do |key,seat_pool|
      seats_used += seat_pool.seats.to_i
    end
    [@max_enrollment - seats_used , 0].max
  end

  #removes seatpool from activity offering
  #
  #@param [string] seat_pool_list hash key
  def remove_seatpool(seatpool_key)
    on ActivityOfferingMaintenance do |page|
      page.remove_seatpool(@seat_pool_list[seatpool_key].population_name)
    end
    @seat_pool_list.delete(seatpool_key)
  end

  #removes all seatpools from activity offering
  def remove_seatpools()
    @seat_pool_list.each do |seatpool_key|
      if @seat_pool_list[seatpool_key].remove
        on ActivityOfferingMaintenance do |page|
          page.remove_seatpool(@seat_pool_list[seatpool_key].population_name)
        end
        @seat_pool_list.delete(seatpool_key)
      end
    end
  end

  # updates the value in @seatpool.priority with @seatpool.priority_after_reseq for each seatpool
  #
  # e.g. after removing the seatpool with priory 1, the seatpool with priority 2 becomes priority 1
  def resequence_expected_seatpool_priorities()
    @seat_pool_list.values.each do |seatpool|
      seatpool.priority = seatpool.priority_after_reseq
    end
  end

  #while on activity offering edit page and updates the details for a specific seatpool
  # @example - must always call save
  #  @activity_offering.edit :honors_course=> true
  #  @activity_offering.edit_seatpool :seats => 2
  #  @activity_offering.save
  #
  # NB: 'save' is a separate step from edit as it allows validation steps to occur during the edit process
  #
  # @param opts [Hash] key => value for attribute to be updated
  def edit_seatpool opts = {}

    sp_key = opts[:seatpool_key]
    sp_key = @seat_pool_list.keys[0] unless sp_key != nil

    defaults = {
        :priority => @seat_pool_list[sp_key].priority,
        :seats => @seat_pool_list[sp_key].seats,
        :expiration_milestone => @seat_pool_list[sp_key].expiration_milestone,
        :remove => false,
        :priority_after_reseq => @seat_pool_list[sp_key].priority_after_reseq
    }
    options=defaults.merge(opts)
    update_pop_name = @seat_pool_list[sp_key].population_name

    on ActivityOfferingMaintenance do |page|
      page.update_seats(update_pop_name, options[:seats])
      @seat_pool_list[sp_key].seats = options[:seats]
    end

    if options[:priority] != @seat_pool_list[sp_key].priority
      on ActivityOfferingMaintenance do |page|
        page.update_priority(update_pop_name,options[:priority])
        @seat_pool_list[sp_key].priority = options[:priority]
      end
    end

    if options[:expiration_milestone] != @seat_pool_list[sp_key].expiration_milestone
      on ActivityOfferingMaintenance do |page|
        page.update_expiration_milestone(update_pop_name,options[:expiration_milestone])
        @seat_pool_list[sp_key].expiration_milestone = options[:expiration_milestone]
      end
    end

    if options[:priority_after_reseq] != @seat_pool_list[sp_key].priority_after_reseq
      @seat_pool_list[sp_key].priority_after_reseq = options[:priority_after_reseq]
    end

    #remove has to be last...
    if options[:remove]
      on ActivityOfferingMaintenance do |page|
        page.remove_seatpool(update_pop_name)
        @seat_pool_list.delete(sp_key)
      end
    end
  end

  #returns a list of populations used in the AO seatpool
  #
  # @returns [Array] list of population names
  def seatpool_populations_used
    populations_used = []
    @seat_pool_list.values.each do |seatpool|
      populations_used << seatpool.population_name
    end
    populations_used
  end

  # suspends the activity offering
  def suspend opts={}

    defaults = {
        :navigate_to_page => true
    }
    options = defaults.merge(opts)

    @parent_course_offering.manage if options[:navigate_to_page]

    on ManageCourseOfferings do |page|
      page.select_ao(self.code)
      page.suspend_ao
      on(SuspendActivityOffering).suspend_activity
    end
  end

  # cancels the activity offering
  def cancel opts={}

    defaults = {
        :navigate_to_page => true
    }
    options = defaults.merge(opts)

    @parent_course_offering.manage if options[:navigate_to_page]

    on ManageCourseOfferings do |page|
      page.select_ao(self.code)
      page.cancel_ao
      on(CancelActivityOffering).cancel_activity
    end
  end

  # cancels the activity offering
  def reinstate opts={}

    defaults = {
        :navigate_to_page => true
    }
    options = defaults.merge(opts)

    @parent_course_offering.manage if options[:navigate_to_page]

    on ManageCourseOfferings do |page|
      page.select_ao(self.code)
      page.reinstate_ao
    end
    on(ReinstateActivityOffering).reinstate_activity
  end


  # approves the activity offering
  def approve opts={}

    defaults = {
        :navigate_to_page => true,
        :send_to_scheduler => false
    }
    options = defaults.merge(opts)

    @parent_course_offering.manage if options[:navigate_to_page]

    on ManageCourseOfferings do |page|
      page.select_ao(self.code)
      if page.approve_activity_button.enabled?
        page.approve_activity
      elsif options[:send_to_scheduler]
        page.deselect_ao(self.code)
        edit :send_to_scheduler => true
        save
      else
        raise "error: approve_activity_button is disabled"
      end
    end
  end

  # offers the activity offering (in published SOC, at least)
  def offer
    @parent_course_offering.manage
    on(ManageCourseOfferings).edit(self.code)
    on ActivityOfferingMaintenance do |page|
      page.send_revised_scheduling_information
      page.submit
    end
  end


end

# stores test data for creating/editing and validating seatpool and provides convenience methods for navigation and data entry
#
# SeatPool is a child of a ActivityOffering
#
# class attributes are initialized with default data unless values are explicitly provided
#
# Typical usage: (with optional setting of explicit data value in [] )
# seatpool_hash[1] = make SeatPool, :population_name => "Core", :seats => 10, :priority => 2, :priority_after_reseq => 2
#
# @activity_offering = create ActivityOffering, :seat_pool_list => seatpool_hash
#
#create generally called from ActivityOffering class
# Note the use of the ruby options hash pattern re: setting attribute values
class SeatPool

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows
  include PopulationsSearch

  #generally set using options hash
  attr_accessor :priority,
                :seats,
                :population_name,
                :expiration_milestone,
                :remove,
                :priority_after_reseq,
                :exp_add_succeed

  alias_method :remove?, :remove
  alias_method :exp_add_succeed?, :exp_add_succeed

  # provides default data:
  #defaults = {
  #    :priority => 1,
  #    :seats => 10,
  #    :population_name => "random",
  #    :expiration_milestone => "First Day of Classes",
  #    :remove => false,
  #    :priority_after_reseq => 0,
  #    :exp_add_succeed => true
  #}
  # initialize is generally called using TestFactory Foundry .make or .create methods
  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :priority => 1,
        :seats => 10,
        :population_name => "random",
        :expiration_milestone => "First Day of Classes",
        :remove => false,
        :priority_after_reseq => 0,
        :exp_add_succeed => true
    }
    options = defaults.merge(opts)
    options[:priority_after_reseq] = options[:priority] unless options[:priority_after_reseq] != 0
    set_options(options)

  end

  #calculates expected % of total enrollment for the seatpool
  #
  # @returns [int] expected % of total enrollment
  def percent_of_total(max_enrollment)
    "#{(@seats.to_i*100/max_enrollment.to_i).round(0)}%"
  end

  # add_seatpool to activity offering
  #
  # @param [Array] list of populations used in seatpools already added
  def add_seatpool(pops_used_list)
    on ActivityOfferingMaintenance do |page|
      if @population_name != ""
        page.add_lookup_population_name

        #TODO should really call Population.search_for_pop
        on ActivePopulationLookup do |page|
          if @population_name == "random"
            page.keyword.wait_until_present
            #page.keyword.set random_letters(1)
            page.search
            #page.change_results_page(1+rand(3))
            #names = page.results_list
            #@population_name = names[1+rand(9)]
            @population_name = search_for_random_pop(pops_used_list)
            page.return_value @population_name
          else
            page.keyword.set @population_name
            page.search
            page.return_value @population_name
          end
        end

      end
      page.add_pool_priority.set @priority
      page.add_pool_seats.set @seats
      page.add_pool_expiration_milestone.select @expiration_milestone unless @expiration_milestone.nil?
    end
  end
end

# stores test data for creating/editing and validating personnel data and provides convenience methods for navigation and data entry
#
# Personnel is a child of a ActivityOffering or CourseOffering (Affiliated Personnel)
#
# class attributes are initialized with default data unless values are explicitly provided
#
# Typical usage: (with optional setting of explicit data value in [] )
#
# personnel_list[0] = make Personnel, :id=> "user1", :affiliation =>"Instructor"
#
# @activity_offering.edit_offering :affiliated_person_list => personnel_list
#
#create generally called from ActivityOffering/CourseOffering class
# Note the use of the ruby options hash pattern re: setting attribute values
class Personnel
  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  #generally set using options hash
  attr_accessor :id,
                :name,
                :affiliation,
                :inst_effort

  # provides default data:
  # defaults = {
  #    :id => "admin",
  #    :affiliation => "Instructor",
  #    :inst_effort => 50
  # }
  # initialize is generally called using TestFactory Foundry .make or .create methods
  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :id => "admin",
        :name => "admin,admin",
        :affiliation => "Instructor",
        :inst_effort => 50
    }

    set_options(defaults.merge(opts))
  end

  # creates personnel based on class attributes
  #
  # generally called from ActivityOffering class
  PERSONNEL_ID_COLUMN = 0
  PERSONNEL_NAME_COLUMN = 1
  PERSONNEL_AFFILIATION_COLUMN = 2
  PERSONNEL_INST_EFFORT_COLUMN = 3
  PERSONNEL_DELETE_COLUMN      = 4

  def create
    on ActivityOfferingMaintenance do |page|
      page.add_personnel
      page.loading.wait_while_present
      page.add_personnel_id.set @id
      page.add_personnel_name.set @name
      page.add_personnel_affiliation.select(@affiliation)
      page.add_personnel_inst_effort.set @inst_effort
    end
  end

  # edits personnel based on values in options hash
  #
  #  @param opts [Hash] key => value for attribute to be updated
  def edit opts={}
    on ActivityOfferingMaintenance do |page|
      page.personnel_table.rows[1].cells[PERSONNEL_ID_COLUMN].text_field.set opts[:id]
      page.personnel_table.rows[1].cells[PERSONNEL_NAME_COLUMN].text_field.set opts[:name]
      page.personnel_table.rows[1].cells[PERSONNEL_AFFILIATION_COLUMN].select().select(opts[:affiliation])
      page.personnel_table.rows[1].cells[PERSONNEL_INST_EFFORT_COLUMN].text_field.set opts[:inst_effort]
    end
#        update_options(opts)
  end

  def target_row_by_personnel_id
    on ActivityOfferingMaintenance do |page|
      idx = -1
      page.personnel_table.rows.each do |row|
        idx += 1
        if (idx < 1)
          next
        end
        begin
          row_key = row.cells[PERSONNEL_ID_COLUMN].text_field.value
          return row unless row_key != @id
        rescue Watir::Exception::UnknownObjectException
          return nil
        end
      end
    end
    return nil
  end


  # edits personnel based on values in options hash
  #
  #  @param opts [Hash] key => value for attribute to be updated
  #  def add_personnel(opts={})
  #    @id = opts[:id]
  #    @affiliation = opts[:affiliation]
  #    @inst_effort = opts[:inst_effort]
  #    on ActivityOfferingMaintenance do |page|
  #      page.add_person_id.set @id
  #      page.add_affiliation.select @affiliation
  #      page.add_inst_effort.set @inst_effort
  #      page.add_personnel
  #    end
  #  end
end

# stores test data for creating/editing and validating scheduling information data and provides convenience methods for navigation and data entry
#
# SchedulingInformation is a child of a ActivityOffering (Requested scheduling information - rsi and actual - asi)
#
# class attributes are initialized with default data unless values are explicitly provided
#
# Typical usage: (with optional setting of explicit data value in [] )
#
# rsi_list[0] = make SchedulingInformation, :days=> M, ...
#
# @activity_offering.edit_offering :requested_scheduling_information_list = rsi_list
#
#create generally called from ActivityOffering
# Note the use of the ruby options hash pattern re: setting attribute values
class SchedulingInformation
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
        :std_ts => false,
        :start_time  => "01:00",
        :start_time_ampm  => "pm",
        :end_time  => "02:00",
        :end_time_ampm  => "pm",
        :facility  => "ARM",
        :facility_long_name  => "Reckord Armory",
        :room  => "0126",
        :features_list  => []
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
        sleep 2
        ns_ts_allowed = !page.non_std_ts_text.nil? && page.non_std_ts_text=="true"
        # if non-standard TS allowed, then treat DSC as CSC
        if @dsc then
          if ns_ts_allowed
            @dsc=false
          end
        end

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

        if @start_time != nil then
          page.add_start_time.click
          page.loading.wait_while_present
          page.add_start_time.set @start_time + " " + @start_time_ampm
          page.add_start_time.click
          page.loading.wait_while_present
        end

        if @end_time != nil then
          if @dsc
            approved_for_nonStandard_timeslots = page.non_std_ts_checkbox.checked?
            if approved_for_nonStandard_timeslots
              page.add_end_time_div.click
              page.add_end_time.wait_until_present
              page.add_end_time.set "#{@end_time} #{@end_time_ampm}"
            else
              page.end_time_select_populate_list
              page.end_time_select.select("#{@end_time} #{@end_time_ampm.upcase}")
            end
          else
            page.end_time_select_populate_list

            if @std_ts then
              page.add_end_time.set @end_time.to_s[0]
              page.loading.wait_while_present
              hr,min = @end_time.split(":")
              if hr.length == 1 then
                hr="0"+hr
              end
              page.select_end_time("#{hr}:#{min} #{@end_time_ampm.upcase}")
            end
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
  #   SchedulingInformation.compare( @si1, @si2 )
  #
  # @param instance1 -- a SchedulingInformation object
  # @param instance2 -- a SchedulingInformation object
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

  # delete Scheduling Information request row
  #
  # generally called from ActivityOffering class - see ActivityOffering
  #
  # @param row
  def delete_rsi
    on ActivityOfferingMaintenance do |page|
      row = page.target_rsi_row(si_key)
      page.delete_rsi_row(row)
    end
  end

  def normalize_start_and_end_times
    st_hr,st_min = @start_time.split(":")
    st_hr = "0"+st_hr if st_hr.length==1
    st = "#{st_hr}:#{st_min}"
    et_hr,et_min = @end_time.split(":")
    et_hr = "0"+et_hr if et_hr.length==1
    et = "#{et_hr}:#{et_min}"
    return st, et
  end
end

# stores test data for creating/editing and validating waitlist data and provides convenience methods for navigation and data entry
#
# Waitlist is contained in ActivityOffering
#
# class attributes are initialized with default data unless values are explicitly provided
#
# Typical usage: (with optional setting of explicit data value in [] )
#
# waitlist = make Waitlist, :=> "user1", :affiliation =>"Instructor"
#
# @activity_offering.edit_offering :affiliated_person_list => personnel_list
#
#create generally called from ActivityOffering/CourseOffering class
# Note the use of the ruby options hash pattern re: setting attribute values
class Waitlist
  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  #generally set using options hash
  attr_accessor :enabled,
                :type, #Confirmation, Automatic, Manual
                :limit_size, #0 means not enabled
                :allow_hold_list

  # provides default data:
  # defaults = {
  #    :enabled => false, #must be enabled for parent_course_offering
  #    :type => "Automatic",
  #    :limit_size => 0
  #    :allow_hold_list => false
  # }
  # initialize is generally called using TestFactory Foundry .make or .create methods
  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :enabled => nil,  #true/false/nil (nil means use default config)
        :type => "Confirmation",  #Automatic, Confirmation, Manual
        :limit_size => 0,
        :allow_hold_list => true
    }

    set_options(defaults.merge(opts))
  end

  # edits waitlist options based on instance vars
  #
  #  @param opts [Hash] key => value for attribute to be updated
  def edit
    return if @enabled.nil?

    on ActivityOfferingMaintenance do |page|
      @enabled ? page.waitlist_checkbox.set : page.waitlist_checkbox.clear
    end

    return unless @enabled

    on ActivityOfferingMaintenance do |page|
      case @type
        when "Automatic"
          page.waitlist_automatic_radio.set
        when "Confirmation"
          page.waitlist_confirmation_radio.set
        when "Manual"
          page.waitlist_manual_radio.set
        else
          raise "error: '#{opts[:type]}' waitlist type not found"
      end
    end

    on ActivityOfferingMaintenance do |page|
      if @limit_size > 0
        page.waitlist_limit_checkbox.set
        page.waitlist_limit.set @limit_size
      else
        page.waitlist_limit_checkbox.clear
      end
    end

    on ActivityOfferingMaintenance do |page|
      @allow_hold_list ? page.waitlist_allow_hold_checkbox.set : page.waitlist_allow_hold_checkbox.clear
    end
  end

  def waitlist_limit_str
    if @limit_size == 0
      return "Unlimited"
    else
      return "Limit to #{@limit_size}"
    end
  end
end