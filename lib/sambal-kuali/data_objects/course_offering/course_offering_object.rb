# stores test data for creating/editing and validating course offerings and provides convenience methods for navigation and data entry
#
# CourseOffering objects contain ActivityOfferings, ActivityOfferingClusters, DeliveryFormatObjects....
#
# class attributes are initialized with default data unless values are explicitly provided
#
# Typical usage: (with optional setting of explicit data value in [] )
#  @course_offering = make CourseOffering, [:course => "CHEM317",...]
#  @course_offering.create
# OR alternatively 2 steps together as
#  @course_offering = create CourseOffering, [:course => "CHEM317",...]
# Note the use of the ruby options hash pattern re: setting attribute values
class CourseOffering < DataFactory

  include Foundry
  include DateFactory
  include StringFactory
  include Workflows
  include Comparable

  #string - generally set using options hash
  attr_accessor :term,
                :course,
                :suffix,
                :final_exam_type,
                :final_exam_driver
  #array - generally set using options hash
  attr_accessor :activity_offering_cluster_list,
                :affiliated_person_list,
                :admin_org_list
  #string - generally set using options hash
  attr_accessor :grade_format,
                :delivery_format_list,
                :honors_flag,
                :grade_options,
                :reg_options,
                :pass_fail_flag,
                :audit_flag,
                :credit_type,
                :fixed_credit_count,
                :multiple_credit_list,
                :search_by_subj,
                :joint_co_to_create,
                :cross_listed_codes
  #object - generally set using options hash - course offering object to copy
  attr_accessor  :create_by_copy,
                 :eo_rsi
  #boolean - - generally set using options hash true/false
  attr_accessor :cross_listed,
                :waitlist #nil means use default setting

  DRAFT_STATUS = "Draft"
  PLANNED_STATUS = "Planned"
  OFFERED_STATUS = "Offered"

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :term=>Rollover::MAIN_TEST_TERM_TARGET,
        :course=>"ENGL211",
        :suffix=>"",
        :activity_offering_cluster_list=> collection('ActivityOfferingCluster') << (make ActivityOfferingClusterObject, :private_name=> :default_cluster),
        :final_exam_type => "STANDARD",
        :waitlist => nil,
        :grade_format => "",
        :delivery_format_list => collection('DeliveryFormat') << (make DeliveryFormatObject),
        :honors_flag => false,
        :affiliated_person_list => {},
        :admin_org_list => collection('AffiliatedOrg') << (make AffiliatedOrgObject),
        :grade_options => "Letter",
        :reg_options => "Pass/Fail Grading",
        :pass_fail_flag => true,
        :audit_flag => false,
        :credit_type => "",
        :fixed_credit_count => "",
        :multiple_credit_list => {},
        :search_by_subj => false,
        :create_by_copy => nil,
        :create_from_existing => nil,
        :joint_co_to_create => nil,
        :cross_listed => false,
        :cross_listed_codes => [],
        :defer_save => false,
        :final_exam_driver => "Final Exam Per Course Offering",
        :exclude_cancelled_aos => false,
        :exclude_scheduling => false,
        :exclude_instructor => false,
        :use_final_exam_matrix => true,
        :eo_rsi => nil
    }
    options = defaults.merge(opts)
    set_options(options)
    #make sure parent_co setup for all collections
    @delivery_format_list.each do |dl|
      dl.parent_co = self
    end
  end

  def <=>(other)
    @course <=> other.course
  end

  # creates course offering based on class attributes
  def create
    if @create_by_copy != nil
      @course = create_co_copy(@create_by_copy.course, @create_by_copy.term)
      #deep copy
      @term = @create_by_copy.term
      @activity_offering_cluster_list = @create_by_copy.activity_offering_cluster_list.sort
    elsif @create_from_existing != nil
      @course = @create_from_existing.course
      #will update @course if suffix added
      @course = create_from_existing_course(@create_from_existing.course, @create_from_existing.term)
      #deep copy
      @activity_offering_cluster_list = @create_from_existing.activity_offering_cluster_list
    else #create from catalog
      start_create_by_search
      on CreateCourseOffering do  |page|
        page.continue
      end

      on CourseOfferingCreateEdit do |page|
        @suffix = random_alphanums(3).upcase if @suffix == ""
        page.suffix.set @suffix
        @course = "#{@course}#{@suffix}"
        if @joint_co_to_create != nil
          create_joint_co()
        end
        page.cross_listed_co_check_box.set if @cross_listed

        if @final_exam_type == "STANDARD"
          page.final_exam_option_standard
          page.final_exam_driver_select( @final_exam_driver)
          page.check_final_exam_matrix( @use_final_exam_matrix)
        elsif @final_exam_type == "ALTERNATE"
          page.final_exam_option_alternate
        else
          page.final_exam_option_none
        end

        index = 0 #FIXME
        @delivery_format_list.each do |dfl|
          on(CourseOfferingCreateEdit).add_format if index > 0  #update default df row on first iteration
          index += 1
          dfl.parent_co = self
          dfl.create
        end

        if @waitlist.nil?  #if waitlist is nil, means use default
          @waitlist = page.has_waitlist?
        elsif  !@waitlist
          page.waitlist_off
          page.waitlist_continue_action
        else
          page.waitlist_on
        end

        page.create_offering unless @defer_save
      end
    end
    return self
  end

  def create_joint_co()

    # TODO: this is hardcoded to create joint-co from row-1;
    # needs to be parameterized using the @joint_co_to_create
    # variable
    on CourseOfferingCreateEdit do |page|
      page.create_new_joint_defined_course_row_1
      page.create_new_joint_defined_course_row_2
    end

  end
  private :create_joint_co

  # searches for and edits an existing course offering course_code matching @course attribute
  # @example
  #  @course_offering.edit :honors_flag=> true
  #
  # @param opts [Hash] key => value for attribute to be updated
  def edit opts={}
    defaults = {
        :defer_save => false,
        :start_edit => true,
        :exp_success => true
    }
    options = defaults.merge(opts)

    on(ManageCourseOfferings).edit_course_offering if options[:start_edit]

    if options[:suffix] != nil
      on(CourseOfferingCreateEdit).suffix.set options[:suffix]
      @course = "#{@course[0..6]}#{options[:suffix]}"
    end

    if options[:grade_options] != nil
      on CourseOfferingCreateEdit do |page|
        page.set_grading_option(options[:grade_options])
        @grade_options = options[:grade_options] if options[:exp_success]
      end
    end

    if options[:waitlist].nil?
      @waitlist = on(CourseOfferingCreateEdit).has_waitlist?
    else
      on CourseOfferingCreateEdit do |page|
        if options[:waitlist]
          page.waitlist_on
        else
          page.waitlist_off
          page.waitlist_continue_action
        end
        @waitlist = options[:waitlist] if options[:exp_success]
      end
    end

    if options[:honors_flag] != nil
      on CourseOfferingCreateEdit do |page|
        if options[:honors_flag]
          page.honors_flag.set
        else
          page.honors_flag.clear
        end
        @honors_flag = options[:honors_flag] if options[:exp_success]
      end
    end

    if options[:final_exam_type] != nil
      on CourseOfferingCreateEdit do |page|
        case options[:final_exam_type]
          when "Standard Final Exam"
            page.final_exam_option_standard
            @final_exam_type = "STANDARD"
          when "Alternate Final Assessment"
            page.final_exam_option_alternate
            @final_exam_type = "ALTERNATE"
          when "No Final Exam or Assessment"
            page.final_exam_option_none
            @final_exam_type = "NONE"
        end
      end
    end

    if options[:final_exam_driver] != nil
      on CourseOfferingCreateEdit do |page|
        page.final_exam_driver_select(options[:final_exam_driver])
      end
      @final_exam_driver = options[:final_exam_driver] if options[:exp_success]
    end

    if options[:use_final_exam_matrix]
      on CourseOfferingCreateEdit do |page|
        page.check_final_exam_matrix( options[:use_final_exam_matrix])
      end
      @use_final_exam_matrix = options[:use_final_exam_matrix] if options[:exp_success]
    end

    if options[:grade_format] != nil
      on CourseOfferingCreateEdit do |page|
        page.select_grade_roster_level(options[:grade_format])
      end
      @grade_format = options[:grade_format] if options[:exp_success]
    end

    if options[:pass_fail_flag] != nil
      on CourseOfferingCreateEdit do |page|
        if options[:pass_fail_flag]
          page.pass_fail_checkbox.set
        else
          page.pass_fail_checkbox.clear
        end
        @pass_fail_flag = options[:pass_fail_flag]
        @reg_options = set_reg_options(options) if options[:exp_success]
      end
    end

    if options[:audit_flag] != nil
      on CourseOfferingCreateEdit do |page|
        if options[:audit_flag]
          page.audit_checkbox.set
        else
          page.audit_checkbox.clear
        end
        @audit_flag = options[:audit_flag]
        @reg_options = set_reg_options(options)
      end
    end

    if options[:credit_type] != nil
      @credit_type = options[:credit_type]
      on CourseOfferingCreateEdit do |page|
        if options[:credit_type] == "fixed"
          page.select_fixed_credit_option
        elsif options[:credit_type] == "multiple"
          page.select_fixed_multiple_option
        end
      end
    end

    if options[:fixed_credit_count] != nil
      @fixed_credit_count = options[:fixed_credit_count] if options[:exp_success]
      on CourseOfferingCreateEdit do |page|
        page.select_fixed_credits(@fixed_credit_count)
      end
    end

    if options[:multiple_credit_list] != nil
      @multiple_credit_list = options[:multiple_credit_list] if options[:exp_success]
      on CourseOfferingCreateEdit do |page|
        @multiple_credit_list.each do |credits, checked|
          if checked
            page.set_multiple_credit_checkbox(credits)
          else
            page.clear_multiple_credit_checkbox(credits)
          end
        end
      end
    end

    if options[:affiliated_person_list] != nil
      options[:affiliated_person_list].values.each do |person|
        on CourseOfferingCreateEdit do |page|
          page.lookup_person
        end
        on PersonnelLookup do |page|
          page.principal_name.set person.id
          page.search
          page.return_value(person.id)
        end
        on CourseOfferingCreateEdit do |page|
          page.add_affiliation.select(person.affiliation)
          page.add_personnel
        end
      end
      @affiliated_person_list = options[:affiliated_person_list] if options[:exp_success]
    end

    if options[:cross_listed] != nil
      on CourseOfferingCreateEdit do |page|
        options[:cross_listed] ? page.cross_listed_co_set : page.cross_listed_co_clear
      end
    end

    #set_options(options) -- can't use this, some custom values e.g final_exam_type
    save unless options[:defer_save]
  end

  def save
    on CourseOfferingCreateEdit do |page|
      page.submit
    end
  end

  def set_reg_options (options)
    if options[:pass_fail_flag] and options[:audit_flag]
      @reg_options = "Allow students to audit; Pass/Fail Grading"
    elsif options[:pass_fail_flag]
      @reg_options = "Pass/Fail Grading"
    elsif options[:audit_flag]
      @reg_options = "Allow students to audit"
    else
      @reg_options = "None available"
    end
  end

  def manage
    go_to_manage_course_offerings
    on ManageCourseOfferings do |page|
      page.term.set @term

      if @search_by_subj
        page.input_code.set @course[0,4]
      else
        page.input_code.set @course
      end

      page.show

    end
    #check to see if course code returns multiple rows
    begin
      on ManageCourseOfferings do |page|
        page.create_co_button.wait_until_present(5)
      end

      on ManageCourseOfferingList do |page|
        page.manage(@course)
      end
    rescue Watir::Wait::TimeoutError
      #means was single CO returned (or nothing returned), AO list is already displayed
    end
  end


  def manage_and_init

    manage

    cluster_divs = []
    on ManageCourseOfferings do |page|
      cluster_divs = page.cluster_div_list
    end

    if cluster_divs.length == 0
      @activity_offering_cluster_list = []
    else
      @activity_offering_cluster_list = []
      cluster_divs.each do |cluster_div|
        temp_aoc = make ActivityOfferingClusterObject
        temp_aoc.init_existing(cluster_div, self)

        @activity_offering_cluster_list.push(temp_aoc)
      end
    end

  end

  def capture_crosslist_aliases

    # note: we nav to subject-view for this because the course-view does not currently support showing the
    #       SUFFIX of the cross-listed course
    search_by_subjectcode
    @cross_listed_codes = on(ManageCourseOfferingList).crosslisted_codes(course)
  end

  def search_by_subjectcode
    go_to_manage_course_offerings
    on ManageCourseOfferings do |page|
      page.term.set @term
      page.input_code.set @course[0,4]
      st_time = Time.new
      page.show
      end_time = Time.new
      #in case there is only 1 course, want to show list
      if page.list_all_course_link.exists? then
        page.list_all_courses
      end
      puts "#{@course[0,4]} subj code search time: #{end_time-st_time}"
    end
  end

  def search_by_coursecode
    go_to_manage_course_offerings
    on ManageCourseOfferings do |page|
      page.term.set @term
      page.input_code.set @course
      page.show
    end
  end

  def start_create_by_search
    go_to_create_course_offerings
    on CreateCourseOffering do  |page|
      page.target_term.set @term
      #page.target_term.fire_event "onchange"
      #page.catalogue_course_code.click
      page.catalogue_course_code.set @course[0,7]   #always use canonical code
      page.choose_from_catalog
    end
  end

  def view_course_details
    search_by_subjectcode
    on ManageCourseOfferingList do |page|
      page.view_course_offering @course.upcase
    end
  end

  # checks to see if course offering specified state is present, otherwise creates a new course offering
  # @example
  #  @course_offering.check_course_in_status(opts)
  # updates the @course instance variable
  #
  # @param opts :co_status =>  "Offered", "Draft" ... [String]
  #             :select_co => true/false
  def check_course_in_status(opts)

    defaults = {
        :select_co => false
    }

    options = defaults.merge(opts)

    search_by_subjectcode
    existing_co = on(ManageCourseOfferingList).select_co_by_status(options[:co_status])
    if existing_co != nil
      @course = existing_co
    else
      @course = create_co_copy(@course, @term)
      on(ManageCourseOfferings).list_all_courses

      if options[:co_status] == OFFERED_STATUS or options[:co_status] == PLANNED_STATUS
        approve_co
      end
    end
    if options[:select_co] then
      on(ManageCourseOfferingList).select_co(@course)
    else
      on(ManageCourseOfferingList).deselect_co(@course)
    end
  end


  # work in progress
  # checks to see if an activity offering in the specified state is present, otherwise creates a new activity offering
  # @example
  #  @course_offering.check_activity_offering_in_status(opts)
  #
  #
  # @param :ao_status =>  [String] "Offered", "Draft", "Approved"
  #         :select_ao => true/false
  def check_activity_offering_in_status(opts)

    defaults = {
        :select_ao => false
    }

    options = defaults.merge(opts)

    manage_and_init
    ao_obj = make ActivityOfferingObject, :parent_course_offering => self
    on ManageCourseOfferings do |page|
      ao_obj.code = page.select_ao_by_status(options[:ao_status])
      if ao_obj.code.nil?
        ao_code = ao_obj.create_simple
        ao_obj.code = ao_code[0]
        if options[:ao_status] == ActivityOfferingObject::OFFERED_STATUS
          approve_ao :ao_obj => ao_obj
        end
      end
      if options[:select_ao] then
        page.select_ao(ao_obj.code)
      else
        page.deselect_ao(ao_obj.code)
      end
    end
  end


  # approves CourseOffering
  # @example
  #  @course_offering.approve_co
  #
  #
  # @param
  def approve_co
    #search_by_subjectcode
    approve_co_list :co_obj_list => [ self ]
  end

  # approves list of CourseOffering objects
  # @example
  #  @course_offering.approve_co_list
  #
  # @param opts [Hash] :co_obj_list => [co_obj1, co_obj2, ...]
  def approve_co_list(opts)
    search_by_subjectcode
    on ManageCourseOfferingList do |page|
      begin
        opts[:co_obj_list].each do |co|
          page.select_co(co.course.upcase)
        end
        page.approve_course_offering

      rescue Timeout::Error => e
        puts "rescued approve course offering"
      end
    end
  end


  # approves subject code for CourseOffering e.g. ENGL202, approves ENGL subject code
  # @example
  #  @course_offering.approve_subject_code
  #
  #
  # @param none
  def approve_subject_code
    search_by_subjectcode
    on ManageCourseOfferingList do |page|
      sleep 1
      page.select_all_cos
      page.approve_course_offering
    end
    approved = false
    on ManageCourseOfferingList do |page|
      sleep 1
      approved = page.course_offering_results_table.rows[2].cells[ManageCourseOfferingList::CO_STATUS_COLUMN].text == PLANNED_STATUS
    end
    #to avoid data setup failure retry approve subject
    if !approved then
      search_by_subjectcode
      on ManageCourseOfferingList do |page|
        sleep 1
        page.select_all_cos
        page.approve_course_offering
      end
    end
  end

 def add_delivery_format opts
   defaults = {
       :config_only => false,
       :defer_save => false
   }
   options = defaults.merge(opts)

   delivery_format_obj = options[:delivery_format]
   edit :defer_save => true
   delivery_format_obj.create unless options[:config_only]
   delivery_format_obj.parent_co = self
   @delivery_format_list <<  delivery_format_obj
   save unless options[:defer_save]
 end

  def add_admin_org opts
    defaults = {
        :config_only => false,
        :defer_save => false,
        :start_edit => true
    }
    options = defaults.merge(opts)

    admin_org_obj = options[:admin_org]
    edit :defer_save => true  if options[:start_edit]
    admin_org_obj.create unless options[:config_only]
    admin_org_obj.parent_co = self
    @admin_org_list <<  admin_org_obj
    save unless options[:defer_save]
  end

# TEMPORARY - This will eventually be replaced by a call to course_offering.delivery_format_list,
# the format deleted from the list and the new list passed on the options hash to course_offering.edit

  def delete_delivery_format (format)
    on CourseOfferingCreateEdit do |page|
      page.delete_delivery_format(format)
    end
  end

  #delete specified activity offering
  #
  # @course_offering.delete_ao :ao_code => "A"
  #
  #@param  opts [Hash] {:ao_code => "code", :cluster_private_name => "cluster_name", :confirm_delete => true/false}
  #@returns confirmation_message (from delete confirmation dialog)
  def delete_ao(opts)

    defaults = {
        :cluster_private_name => :default_cluster,
        :confirm_delete => true
    }
    options = defaults.merge(opts)
    options[:code_list] = [options[:ao_code]]
    delete_ao_list(options)
  end

  #delete specified activity offerings
  #
  #   @example
  #   @course_offering.delete_ao_list :code_list => ["A","B"]
  #        :cluster_private_name default value is first cluster
  #
  #
  #@param  opts [Hash] {:code_list => ["code1","code2", ...], :cluster_private_name => "cluster_name", :confirm_delete => true/false}
  #@returns confirmation_message (from delete confirmation dialog)
  def delete_ao_list(opts)

    defaults = {
        :cluster_private_name => :default_cluster,
        :confirm_delete => true
    }
    options = defaults.merge(opts)

    on ManageCourseOfferings do |page|
      page.select_aos(options[:code_list], options[:cluster_private_name])
      page.delete_aos
    end

    confirmation_message = ""
    on ActivityOfferingConfirmDelete do |page|
      confirmation_message = page.delete_confirm_message
      if options[:confirm_delete] then
        page.delete_activity_offering
      else
        page.cancel
      end
    end

    #update expected object data
    options[:code_list].each do |ao_code|
      ao_cluster = get_cluster_obj_by_private_name(options[:cluster_private_name])
      ao_obj = get_ao_obj_by_code(ao_code)
      ao_cluster.ao_list.delete(ao_obj)
    end

    confirmation_message
  end

  # checks to see if AOs of a specific status can be deleted (for Authorization testing)
  # @example
  #  @course_offering.attempt_ao_delete_by_status(ActivityOfferingObject::OFFERED_STATUS)
  #    :cluster_private_name default value is first cluster
  #
  # @param opts [Hash] :co_obj_list => [co_obj1, co_obj2, ...]
  # @returns boolean - delete opertion was available
  def attempt_ao_delete_by_status(ao_state, cluster_private_name = :default_cluster)
    on ManageCourseOfferings do |page|
      if page.row_by_status(ao_state, cluster_private_name).exists?
        ao = page.select_ao_by_status(ao_state, cluster_private_name)
        if page.delete_aos_button.enabled?
          page.delete_aos
          on ActivityOfferingConfirmDelete do |page|
            @delete_present = page.delete_activity_offering_button.present?
            page.cancel
          end
          on(ManageCourseOfferings).deselect_ao(ao)
          return @delete_present
        else
          page.deselect_ao(ao)
          return false
        end
      else
        new_ao = copy_ao :ao_code => "A"
        page.select_ao(new_ao.code)
        if ao_state == ActivityOfferingObject::APPROVED_STATUS
          page.approve_activity
          new_ao = page.select_ao_by_status(ao_state)
        end
        if page.delete_aos_button.enabled?
          page.delete_aos
          on ActivityOfferingConfirmDelete do |page|
            @delete_present = page.delete_activity_offering_button.present?
            page.cancel
          end
          on(ManageCourseOfferings).deselect_ao(new_ao)
          return @delete_present
        else
          page.deselect_ao(new_ao)
          return false
        end
      end
    end
  end

  # checks to see if COs of a specific status can be deleted (for Authorization testing)
  # @example
  #  @course_offering.attempt_co_delete_by_status(CourseOffering::OFFERED_STATUS)
  #    :cluster_private_name default value is first cluster
  #
  # @param opts [Hash] :co_obj_list => [co_obj1, co_obj2, ...]
  # @returns boolean - delete opertion was available
  def attempt_co_delete_by_status(co_state)
    on ManageCourseOfferingList do |page|
      @course = page.select_co_by_status(co_state)
      if page.delete_cos_button.enabled?
        page.delete_cos
      else
        page.deselect_co(@course)
        return false
      end
      on DeleteCourseOffering do |page|
        return page.confirm_delete_button.present?
      end
    end
  end

  # checks to see if AOs of a specific status can be selected (for Authorization testing)
  # @example
  #  @course_offering.attempt_ao_select_by_status(ActivityOfferingObject::OFFERED_STATUS)
  #    :cluster_private_name default value is first cluster
  #
  # @param opts [Hash] :co_obj_list => [co_obj1, co_obj2, ...]
  # @returns boolean - checkbox to select AO was available
  def ao_has_checkbox_by_status(ao_status, cluster_private_name = :default_cluster)
    on ManageCourseOfferings do |page|
      row = page.row_by_status(ao_status, cluster_private_name)
      return row.cells[0].checkbox.exists?
    end
  end

  #approve activity offering
  #
  #   @example
  #   @course_offering.approve_ao :ao_obj=> ao_obj1
  #        :cluster_private_name default value is first cluster
  #
  #@param  opts [Hash] {:ao_obj => activity_offering1, :cluster_private_name => "priv_name"}
  def approve_ao(opts)

    defaults = {
        :cluster_private_name => :default_cluster
    }
    options = defaults.merge(opts)

    approve_ao_list :ao_obj_list => [ options[:ao_obj] ], :cluster_private_name => options[:cluster_private_name]
  end



  #approve list of activity offerings
  #
  #   @example
  #   @course_offering.approve_ao_list :ao_obj_list => [ao_obj1, ao_obj2]
  #        :cluster_private_name default value is first cluster
  #
  #@param  opts [Hash] {:ao_obj_list => [activity_offering1,activity_offering2, ...], :cluster_private_name => "priv_name"}
  def approve_ao_list(opts)

    defaults = {
        :cluster_private_name => :default_cluster
    }
    options = defaults.merge(opts)

    on ManageCourseOfferings do |page|
      options[:ao_obj_list].each do |ao|
        page.select_aos([ao.code], options[:cluster_private_name])
      end
      page.approve_activity
    end
  end

  #reinstate list of activity offerings
  #
  #   @example
  #   @course_offering.reinstate_ao_list :ao_obj_list => [ao_obj1, ao_obj2]
  #        :cluster_private_name default value is first cluster
  #
  #@param  opts [Hash] {:ao_obj_list => [activity_offering1,activity_offering2, ...], :cluster_private_name => "priv_name"}
  def reinstate_ao_list(opts)

    defaults = {
        :cluster_private_name => :default_cluster
    }
    options = defaults.merge(opts)

    on ManageCourseOfferings do |page|
      options[:ao_obj_list].each do |ao|
        page.select_aos([ao.code], options[:cluster_private_name])
      end
      page.reinstate_ao
    end
    on(ReinstateActivityOffering).reinstate_activity
  end



  def get_ao_list(cluster_private_name = :default_cluster)
   get_cluster_obj_by_private_name(cluster_private_name).ao_list
  end


  #create a new list of activity offerings
  #
  #  @example
  #  @course_offering.create_list_aos :number_aos_to_create => 3, :ao_object => ao_obj
  #
  #@param opts [Hash] {:number_aos_to_create => int, :ao_object => ao_obj }
  def create_list_aos(opts)
    activity_offering_object = opts[:ao_object]
    activity_offering_object.parent_course_offering = self
    activity_offering_object.create_simple :number_aos_to_create => opts[:number_aos_to_create]
  end

  #create a new specified activity offering
  #
  # @example
  #  @course_offering.create_ao(activity_offering_object)
  #
  #@param opts :ao_obj => ActivityOffering object, :navigate_to_page => true/false
  def create_ao(opts = {})

    defaults = {
        :navigate_to_page => true,
        :ao_obj => (make ActivityOfferingObject)
    }
    options = defaults.merge(opts)

    self.manage if options[:navigate_to_page]

    activity_offering_object = options[:ao_obj]
    activity_offering_object.parent_course_offering = self
    activity_offering_object.create
    get_cluster_obj_by_private_name(activity_offering_object.aoc_private_name).ao_list << activity_offering_object
    return activity_offering_object
  end

  #copy the specified activity offering
  #
  # @example
  #  @course_offering.copy_ao :ao_code => "CODE", :cluster_private_name => "private_name"
  #       :cluster_private_name default value is first cluster
  #
  #@param  opts [Hash] {:ao_code => "CODE", :cluster_private_name => "private_name" (see default value = :default_cluster)}
  def copy_ao(opts)

    defaults = {
        :cluster_private_name => :default_cluster
    }
    options = defaults.merge(opts)
    new_activity_offering = make ActivityOfferingObject, :code => options[:ao_code], :aoc_private_name => options[:cluster_private_name], :create_by_copy => true, :parent_course_offering => self

    new_activity_offering.create
    get_cluster_obj_by_private_name(options[:cluster_private_name]).ao_list << new_activity_offering
    return new_activity_offering
  end

  #enter the edit page for the specified activity offering
  #
  #@param  opts [Hash] {:ao_code => "CODE"}
  def edit_ao(opts)
    defaults = {
        :cluster_private_name => :default_cluster
    }
    options = defaults.merge(opts)
    on ManageCourseOfferings do |page|
      page.edit(options[:ao_code], options[:cluster_private_name])
    end
  end

  # returns a list of AOs matching a given state
  # note: can return an empty array but not nil
  #
  # @param opts [Hash] {:cluster_private_name => "private_name", (see default value = :default_cluster)
  #                     :aos => [],
  #                     :ao_status => "target_status" (see default value = "Draft") }
  # example: draft_aos = @courseOffering.get_aos_by_status :aos => array_of_all_aos
  def get_aos_by_status(opts)
    defaults = {
        :cluster_private_name => :default_cluster,
        :aos => [],
        :ao_status => ActivityOfferingObject::DRAFT_STATUS
    }
    options = defaults.merge(opts)

    retVal = []

    options[:aos].each { |ao|
      status = on(ManageCourseOfferings).ao_status( ao.code, options[:cluster_private_name] )
      if status == options[:ao_status]
        retVal << ao
      end
    }

    retVal
  end

  # add/create an ao_cluster to the CourseOffering
  # @example
  #  @course_offering.add_ao_cluster(ao_cluster_object)
  #
  #
  # @param ao_cluster [ActivityOfferingClusterObject]
  def add_ao_cluster(ao_cluster)
    ao_cluster.create
    @activity_offering_cluster_list << ao_cluster
  end

  # delete an ao_cluster from the CourseOffering
  # @example
  #  @course_offering.delete_ao_cluster(ao_cluster_object)
  #
  #
  # @param ao_cluster [ActivityOfferingClusterObject]
  def delete_ao_cluster(ao_cluster)
    ao_cluster.delete
    @activity_offering_cluster_list.delete(get_cluster_obj_by_private_name(ao_cluster.private_name))
  end

  # delete an ao_cluster from the CourseOffering
  # @example
  #  @course_offering.get_cluster_obj_by_private_name(cluster_private_name)
  #
  #
  # @param cluster_private_name [String]
  # @returns  ActivityOfferingClusterObject
  def get_cluster_obj_by_private_name(cluster_private_name)
    return @activity_offering_cluster_list[0] unless cluster_private_name != :default_cluster
    @activity_offering_cluster_list.select{|cluster| cluster.private_name == cluster_private_name}[0]
  end


  def get_ao_obj_by_code(ao_code, cluster_private_name = :default_cluster)
   get_cluster_obj_by_private_name(cluster_private_name).ao_list.select{|ao| ao.code == ao_code}[0]
  end

  # searches all clusters
  def find_ao_obj_by_code(ao_code)
    activity_offering_cluster_list.each do |cluster_obj|
      cluster_obj.ao_list.each do |ao|
        return ao unless ao.code != ao_code
      end
    end
    return nil
  end

  def create_from_existing_course(course, term)
    start_create_by_search
    on CreateCourseOffering do |page|
      page.choose_from_existing
      page.continue
    end

    on CreateCOFromExisting do |page|
      page.select_copy_for_existing_course(term, course)

      page.select_exclude_cancelled_aos if @exclude_cancelled_aos
      page.select_exclude_scheduling if @exclude_scheduling
      page.select_exclude_instructor if @exclude_instructor
      page.create
    end
    co_code = ""
    on ManageCourseOfferings do |page|
      co_code = page.input_code.value
    end
    co_code
  end
  private :create_from_existing_course

  def create_co_copy(source_course_code, term)
    go_to_manage_course_offerings
    on ManageCourseOfferings do |page|
      page.term.set term
      page.input_code.set source_course_code[0,4] #subject code + course level (assumes always more than one CO returned)
      page.show
    end

    on ManageCourseOfferingList do |page|
      page.copy source_course_code
    end
    on CopyCourseOffering do |page|

      page.select_exclude_cancelled_aos if @exclude_cancelled_aos
      page.select_exclude_scheduling if @exclude_scheduling
      page.select_exclude_instructor if @exclude_instructor
      page.create_copy
    end

    on ManageCourseOfferings do |page|
      @course = page.input_code.value # source_course_code[0,5] #subject code + course level (assumes always more than one CO returned)
    end
    return @course
  end
  private :create_co_copy

  # deletes a list of COs from the subject-code view using the toolbar
  #
  # @example
  #   @course_offering.delete_co_list :co_obj_list => [obj1, obj2, ..."]
  #
  # @param opts [Hash] {:should_confirm_delete => false (default is true), :code_list => ["ENGL222, ..."]}
  # @returns delete confirmation/warning message
  def delete_co_list(opts={})
    defaults = {
        :should_confirm_delete => true
    }
    opts = defaults.merge(opts)

    on ManageCourseOfferingList do |page|
      opts[:co_obj_list].each do |co|
        page.select_co(co.course)
      end
      page.delete_cos
    end

    confirmation_message = ""
    on DeleteCourseOffering do |page|
      confirmation_message = page.delete_warning_message
      if opts[:should_confirm_delete]
        page.confirm_delete
      else
        page.cancel_delete
      end
    end
    confirmation_message
  end

  # deletes CO from the single-CO view using the link
  #
  # @param opts [Hash] {:should_confirm_delete => false}
  def delete_co_coc_view(opts={})
    defaults = {
        :should_confirm_delete => true
    }
    opts = defaults.merge(opts)

    on ManageCourseOfferings do |page|
      page.delete_course_offering
    end
    on DeleteCourseOffering do |page|
      if opts[:should_confirm_delete]
        page.confirm_delete
      else
        page.cancel_delete
      end
    end
  end

  def formatted_multiple_credits_list
    formatted_credits_list = ""
    @multiple_credit_list.each do |credits, set|
      if set
        formatted_credits_list << credits.to_i.to_s + ", "
      end
    end

    # If there is a string, remove final ", "
    formatted_credits_list[-2..-1] = "" if formatted_credits_list.length > 0
    formatted_credits_list
  end

  def full_ao_list
    #TODO - required for existing validations
  end

  #TODO - this method is not used
  def reset_ao_clusters
    #move all aos back first cluster - NB init_existing needs to be run first
    @activity_offering_cluster_list[1..-1].each do |cluster|
      puts "reset cluster name: #{cluster.private_name}"
      cluster.move_all_aos_to_another_cluster(@activity_offering_cluster_list[0])
      cluster.delete
      #TODO - delete from array  - test this
      @activity_offering_cluster_list.delete(cluster)
    end
  end

end

