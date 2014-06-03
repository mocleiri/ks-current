# stores test data for managing and validating the SOC process and provides convenience methods for navigation and data entry
#
# class attributes are initialized with default data unless values are explicitly provided
#
# Typical usage: (with optional setting of explicit data value in [] )
#  @manageSoc = make ManageSoc, [:term_code=>"201301"]
#  @manageSoc.search
#
# Note the use of the ruby options hash pattern re: setting attribute values
class ManageSoc < DataFactory

  include Foundry
  include DateFactory
  include StringFactory
  include Workflows

  OPEN_STATE_KEY = "kuali.soc.state.open"
  PUBLISHING_STATE_KEY = "kuali.soc.state.publishing"
  IN_PROGRESS_STATE_KEY = "kuali.soc.scheduling.state.inprogress"

  #generally set using options hash
  attr_accessor :term_code, :co_code

  # provides default data:
  #  defaults = {
  #    :term_code=>"20122" ,
  #    :co_code=>"ENGL103"
  #  }
  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :term_code=>Rollover::MAIN_TEST_TERM_SOURCE ,
        :co_code=>"ENGL222"
    }
    options = defaults.merge(opts)
    set_options(options)
  end

  #navigate to Manage SOC page and search for :term_code
  def search
    go_to_manage_soc
    on ManageSocPage do |page|
      page.term_code.set @term_code
      page.go_action
    end
  end

  #(on Manage SOC page) raise error unless state change button is in the expected state for specified current_state
  #
  #@raises error if SOC is in unexpected state
  #
  # @param curent_state [String] in Lock, FinalEdit, Scedule, Publish, Close
  def check_state_change_button_exists(current_state)
    on ManageSocPage do |page|
    case(current_state)
        when 'Lock'
          raise "SOC is not enabled for Lock" unless page.lock_button.enabled? and page.soc_status == 'Open'
          raise "Draft Date doesnt exists" unless page.is_date_exists('Draft')
          raise "Open Date doesnt exists" unless page.is_date_exists('Open')
        when 'FinalEdit'
          raise "SOC is not in final edit state" unless page.final_edit_button.enabled? and page.soc_status == 'Locked' and page.soc_scheduling_status == 'Completed'
          raise "Schedule Initiated Date is blank" unless page.schedule_initiated_date != nil
          raise "Schedule completed Date is blank" unless page.schedule_completed_date != nil
          raise "Schedule duration is blank" unless page.schedule_duration != nil
        when 'Schedule'
          raise "Send to Scheduler action not available" unless page.send_to_scheduler_button.enabled?
          raise "Final edit button not exists or disabled" unless page.final_edit_button.disabled?
          raise "SOC is not in Lock state or scheduling state is not completed" unless page.soc_status == 'Locked'
          raise "Locked Date doesnt exists" unless page.is_date_exists('Locked')
        when 'Publish'
          raise "SOC is not in publish state" unless page.publish_button.enabled? and page.soc_status == 'Final Edits' and page.soc_scheduling_status == 'Completed'
          raise "Final Edits date doesnt exists" unless page.is_date_exists('Final Edits')
        when 'Close'
          raise "SOC is not in close state" unless page.soc_status == 'Published' and page.soc_publishing_status == 'Published'
          raise "Publish Initiated Date is blank" unless page.publish_initiated_date != nil
          raise "Publish completed Date is blank" unless page.publish_completed_date != nil
          raise "Publish duration is blank" unless page.publish_duration != nil
          raise "Published Date doesnt exists" unless page.is_date_exists('Published')
        else
          raise "Your Soc State value must be one of the following:\n'Open', \n'Lock', \n'FinalEdit', \n'Schedule', \n'Publish', 'Close'.\nPlease update your script"
        end
    end
  end

  #proces SOC to next state
  #
  # @raises error if SOC is in unexpected state
  #
  #@param new_state [String] in Lock, FinalEdit, Publish, Schedule
  #@param confirm_state_change [String] Yes/No
  def change_action(new_state)
    on ManageSocPage do |page|
      case(new_state)
        when 'Lock'
          page.lock_action
          page.lock_confirm_action
          sleep 4 #work around for KSENROLL-12946
          page.go_action #work around for KSENROLL-12946
          raise "SOC state table not updated to 'Locked'" unless page.soc_status == 'Locked'
          #page.message_element.wait_until_present
          #raise "'Set of Courses has been Locked.' not displayed after Lock" unless page.message == 'Set of Courses has been Locked.'
        when 'Schedule'
          schedule_soc page
        when 'FinalEdit'
          page.final_edit_action
          page.final_edit_confirm_action
          sleep 4 #work around for KSENROLL-12946
          page.go_action #work around for KSENROLL-12946
          raise "SOC state table not updated to 'Final Edits'" unless page.soc_status == 'Final Edits'
          #page.message_element.wait_until_present
          #raise "Info message text at the top doesnt match" unless page.message == 'Set of Courses has been opened for Final Edits.'
        when 'Publish'
          publish_soc page
        else
          raise "Your Soc State value must be one of the following:\n'Lock', \n'FinalEdit', \n'Schedule', \n'Publish'.\nPlease update your script"
      end
    end
  end

  #private
  #schedule soc
  #
  #@param page(ManageSoc page)
  #@param confirm_state_change [String] Yes/No
  def schedule_soc(page)
    page.send_to_scheduler_action
    page.schedule_confirm_action
    sleep 10
    page.go_action #work around for KSENROLL-12946
    #page.message_element.wait_until_present
    tries = 0
    raise "Schedule Initiated Date is blank" unless page.schedule_initiated_date != nil
    raise "Once schedule started, schedule completed date should say 'Scheduling in progress'" unless page.schedule_completed_date == 'Scheduling in progress'
    raise "Schedule duration should have the '(in progress)' text at the end" unless page.schedule_duration =~ /(in progress)/
    #raise "Info message text at the top doesnt match" unless page.message == 'Approved activities were successfully sent to Scheduler.' #work around for KSENROLL-12946
    until page.final_edit_button.enabled? or tries == 15 do
      sleep 20
      tries += 1
      search
    end
    raise "Test timed out waiting for Scheduler process" unless page.final_edit_button.enabled?
  end

  #pubilsh soc
  #
  #@param page(ManageSoc page)
  #@param confirm_state_change [String] Yes/No
  def publish_soc(page)
    page.publish_action
    page.publish_confirm_action
    sleep 4 #work around for KSENROLL-12946
    page.go_action #work around for KSENROLL-12946
    #raise "SOC status doesnt change to Publishing In Progress" unless page.soc_status == 'Publishing In Progress'
#    raise "Close button not displayed" unless page.close_button.exists?
    #raise "Publish Initiated Date is blank" unless page.schedule_initiated_date != nil #work around for KSENROLL-12946
    #raise "Once publish started, schedule completed date should say 'Publishing in progress'" unless page.publish_completed_date == 'Publishing in progress' #work around for KSENROLL-12946
    #raise "Publish duration should have the '(in progress)' text at the end" unless page.publish_duration =~ /(in progress)/ #work around for KSENROLL-12946
    #raise "Publishing In Progress Date is blank" unless page.is_date_exists('Publishing In Progress') #work around for KSENROLL-12946
    tries = 0
    until page.soc_status == 'Published' or tries == 15 do
      sleep 20
      tries += 1
      search
    end
    raise "Test timed out waiting for Publish process" unless page.soc_status == 'Published'
  end

  def verify_schedule_state_changes
    @browser.goto "#{$test_site}/kr-krad/statusview/#{@term_code}/#{@co_code}"
    on StatusViewPage do |page|
      raise "expected SOC state 'Locked', was #{page.soc_state}" unless page.soc_state == 'Locked'
      raise "expected SOC scheduling state 'Completed', was #{page.soc_scheduling_state}" unless page.soc_scheduling_state == 'Completed'
      raise "expected CO state 'Offered', was #{page.co_state}" unless (page.co_state =~ /Offered/) != nil
      page.approved_aos.each do |row|
        raise "expected FO state 'Offered', was #{page.fo_state(row)}" unless page.fo_state(row) == 'Offered'
      end
    end
  end

  def verify_publish_state_changes
    @browser.goto "#{$test_site}/kr-krad/statusview/#{@term_code}/#{@co_code}"
    on StatusViewPage do |page|
      raise "expected SOC state 'Published', was #{page.soc_state}" unless page.soc_state == 'Published'
      raise "expected SOC scheduling state 'Completed', was #{page.soc_scheduling_state}" unless page.soc_scheduling_state == 'Completed'
      raise "expected CO state 'Offered', was #{page.co_state}" unless (page.co_state =~ /Offered$/) != nil
      page.approved_aos.each do |row|
        raise "expected AO state 'Offered', was #{page.ao_state(row)}" unless page.ao_state(row) == 'Offered'
        raise "expected FO state 'Offered', was #{page.fo_state(row)}" unless page.fo_state(row) == 'Offered'
      end
    end
  end

  # initiates a manual SOC state-change
  #
  # @example
  #   @manual_soc_state_change.perform_manual_soc_state_change :new_soc_state => "Publishing"
  #
  # @param opts [String] new_soc_state => "Publishing", "In Progress" (default is "Open")}
  def perform_manual_soc_state_change(new_soc_state="open")
    case new_soc_state
      when "Publishing"
        soc_state = ManageSoc::PUBLISHING_STATE_KEY
      when "In Progress"
        soc_state = ManageSoc::IN_PROGRESS_STATE_KEY
      else
        soc_state = ManageSoc::OPEN_STATE_KEY
    end

    #go_to_manual_soc_change
    visit ManualSocStateChangePage do |page|
      page.change_soc_state_termCode.value = @term_code
      page.change_soc_state_newSocState.value = soc_state
      page.change_soc_state
    end
  end

  #can't do this for a subterm
  def set_up_soc
    #go_to_create_soc
    visit CreateSocForTerm do |page|
      page.term_code.set @term_code
      page.submit
    end
  end

  def advance_soc_from_open_to_scheduler_run
    search
    change_action "Lock"
    check_state_change_button_exists "Schedule"
    change_action "Schedule"
    check_state_change_button_exists "FinalEdit"
  end

  def advance_soc_from_open_to_final_edits
    search
    change_action "Lock"
    check_state_change_button_exists "Schedule"
    change_action "Schedule"
    check_state_change_button_exists "FinalEdit"
    change_action "FinalEdit"
    check_state_change_button_exists "Publish"
  end

  def advance_soc_from_open_to_published
    advance_soc_from_open_to_final_edits()
    change_action "Publish"
    check_state_change_button_exists "Close"
  end
end