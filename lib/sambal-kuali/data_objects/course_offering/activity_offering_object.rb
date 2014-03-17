# stores test data for creating/editing and validating activity offerings and provides convenience methods for navigation and data entry
#
# ActivityOffering objects contain the following objects: SeatPool, DeliveryLogistic (requested and actual) and PersonnelObject (Affiliated PersonnelObjectObject)
#
# ActivityOffering is a child of a CourseOffering
#
# class attributes are initialized with default data unless values are explicitly provided
#
# Typical usage: (with optional setting of explicit data value in [] )
#  @activity_offering = make ActivityOfferingObject, [:seat_pool_list => {},...]
#  @activity_offering.create
# OR alternatively 2 steps together as
#  @activity_offering = create ActivityOfferingObject, [:seat_pool_list => {},...]
# Note the use of the ruby options hash pattern re: setting attribute values
class ActivityOfferingObject
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
  #type: collection
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

  # initialize is generally called using TestFactory Foundry .make or .create methods
  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :parent_course_offering => (make CourseOffering),
        :status => DRAFT_STATUS,
        :format => nil, #nil means use default
        :activity_type => "Lecture",
        :max_enrollment => 0,
        :actual_scheduling_information_list => collection('SchedulingInformation'),
        :requested_scheduling_information_list => collection('SchedulingInformation'),
        :personnel_list => collection('Personnel'),
        :seat_pool_list => collection('SeatPool'),
        :course_url => "",
        :requires_evaluation => false,
        :honors_course => false,
        :aoc_private_name => :default_cluster,
        :create_by_copy => nil, #if true create copy using :ao_code
        :colocated => false,
        :colocate_ao_list => [],
        :colocate_shared_enrollment => false,
        :subterm => nil,
        :waitlist_config => (make Waitlist, :parent_ao => self)
    }

    options = defaults.merge(opts)
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
  end

  def get_existing_info_from_page
    ao_table_row = on(ManageCourseOfferings).target_row(@code)
    init_existing(ao_table_row, @parent_course_offering, @aoc_private_name)
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
          @requested_scheduling_information_list << si_object
        else
          @actual_scheduling_information_list << si_object
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
      si = make SchedulingInformationObject,
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
  #   additional opts :edit_already_started (bool), :send_to_scheduler (bool), :defer_save (bool)
  def edit opts={}

    defaults = {
        :defer_save => false,
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
    edit_course_url options
    edit_evaluation options
    edit_honors_course options

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
            page.edit_separate_max_enr(@parent_course_offering.course, @code, opts[:max_enrollment])
            @max_enrollment = opts[:max_enrollment]
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

  def add_req_sched_info opts={}

    defaults = {
        :defer_save => false,
        :edit_already_started => false
    }
    options = defaults.merge(opts)
    edit :defer_save => true unless options[:edit_already_started]

    rsi_obj = options[:rsi_obj]
    rsi_obj.parent_ao = self
    rsi_obj.create
    @requested_scheduling_information_list << rsi_obj
    on(ActivityOfferingMaintenance).save unless options[:defer_save]
  end

  def add_personnel person
    #edit
    person.parent_ao = self
    person.create
    @personnel_list << person
  end

  def delete_personnel person
    on ActivityOfferingMaintenance do |page|
      #page.update_person_name(person.id,'blah, blah')
      page.delete_personnel(person.id)
    end
  end

  def add_seat_pool opts={}

    defaults = {
        :defer_save => false,
        :edit_already_started => false
    }
    options = defaults.merge(opts)

    edit :defer_save => true unless options[:edit_already_started]

    options[:seat_pool_obj].parent_ao = self
    options[:seat_pool_obj].create seatpool_populations_used
    on(ActivityOfferingMaintenance).save unless options[:defer_save]
    @seat_pool_list << options[:seat_pool_obj] if options[:seat_pool_obj].exp_add_succeed
  end

  def add_seat_pool_list opts={}

    defaults = {
        :defer_save => false,
        :edit_already_started => false
    }
    options = defaults.merge(opts)

    edit unless options[:edit_already_started]

    options[:seat_pool_list].each do |seatpool|
      add_seat_pool :seat_pool_obj => seatpool, :defer_save => true, :edit_already_started => true
    end

    on(ActivityOfferingMaintenance).save unless options[:defer_save]

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
    @seat_pool_list.each do |seat_pool|
      seats_used += seat_pool.seats.to_i
    end
    [@max_enrollment - seats_used , 0].max
  end

  #removes seatpool from activity offering
  #
  #@param [string] seat_pool_list hash key
  def remove_seatpool(seatpool_obj)
    on ActivityOfferingMaintenance do |page|
      page.remove_seatpool(seatpool_obj.population_name)
    end
    @seat_pool_list.delete(seatpool_obj)
  end

  # updates the value in @seatpool.priority with @seatpool.priority_after_reseq for each seatpool
  #
  # e.g. after removing the seatpool with priory 1, the seatpool with priority 2 becomes priority 1
  def resequence_expected_seatpool_priorities()
    @seat_pool_list.each do |seatpool|
      seatpool.priority = seatpool.priority_after_reseq unless seatpool.priority_after_reseq == ""
    end
  end

  #returns a list of populations used in the AO seatpool
  #
  # @returns [Array] list of population names
  def seatpool_populations_used
    populations_used = []
    @seat_pool_list.each do |seatpool|
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
        if options[:send_to_scheduler]
          page.deselect_ao(self.code)
          edit :send_to_scheduler => true
        end
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

class ActivityOfferingCollection < CollectionsFactory

  contains ActivityOfferingObject

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
                :allow_hold_list,
                :parent_ao

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
  def edit opts={}
    defaults = {
        :defer_save => false,
        :edit_already_started => false
    }
    options = defaults.merge(opts)

    on(ManageCourseOfferings).edit @parent_ao.code unless options[:edit_already_started]

    if !opts[:enabled].nil?
      on ActivityOfferingMaintenance do |page|
        options[:enabled] ? page.waitlist_checkbox.set : page.waitlist_checkbox.clear
        @enabled = options[:enabled]
      end
    end

    #return unless @enabled

    if !options[:type].nil?
      on ActivityOfferingMaintenance do |page|
        case options[:type]
          when "Automatic"
            page.waitlist_automatic_radio.set
          when "Confirmation"
            page.waitlist_confirmation_radio.set
          when "Manual"
            page.waitlist_manual_radio.set
          else
            raise "error: '#{options[:type]}' waitlist type not found"
        end
      end
    end

    if !options[:limit_size].nil?
      on ActivityOfferingMaintenance do |page|
        if options[:limit_size] > 0
          page.waitlist_limit_checkbox.set
          page.waitlist_limit.set options[:limit_size]
        else
          page.waitlist_limit_checkbox.clear
        end
      end
    end

    if !options[:allow_hold_list].nil?
      on ActivityOfferingMaintenance do |page|
        options[:allow_hold_list] ? page.waitlist_allow_hold_checkbox.set : page.waitlist_allow_hold_checkbox.clear
      end
    end

    set_options(options)
    @parent_ao.save unless options[:defer_save]
  end

  def waitlist_limit_str
    if @limit_size == 0
      return "Unlimited"
    else
      return "Limit to #{@limit_size}"
    end
  end
end