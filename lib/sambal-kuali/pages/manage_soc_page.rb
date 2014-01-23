class ManageSocPage < BasePage

  expected_element :term_code

  wrapper_elements
  frame_element

  element(:message) {|b| b.frm.ul(id: "pageValidationList").li.text}
  element(:term_code)  { |b| b.frm.text_field(id: "socTermField_control") }
  element(:lock_button)  { |b| b.frm.button(id: "ManageSOCView-SchedulingDetails-LockSetButton") }
  element(:final_edit_button)  { |b| b.frm.button(id: "ManageSOCView-SchedulingDetails-FinalEditButton") }
  element(:send_to_scheduler_button)  { |b| b.frm.button(id: "ManageSOCView-SchedulingDetails-SendToSchedulerButton") }
  element(:publish_button)  { |b| b.frm.button(id: "ManageSOCView-SchedulingDetails-PublishButton") }
  element(:close_button)  { |b| b.frm.button(id: "ManageSOCView-SchedulingDetails-CloseButton") }

  action(:go_action) { |b| b.frm.button(id: "socShowButton").click; b.loading.wait_while_present }
  action(:lock_action) { |b| b.frm.button(id: "ManageSOCView-SchedulingDetails-LockSetButton").click; b.loading.wait_while_present }
  action(:final_edit_action) { |b| b.frm.button(id: "ManageSOCView-SchedulingDetails-FinalEditButton").click; b.loading.wait_while_present }
  action(:send_to_scheduler_action) { |b| b.frm.button(id: "ManageSOCView-SchedulingDetails-SendToSchedulerButton").click; b.loading.wait_while_present }
  action(:publish_action) { |b| b.frm.button(id: "ManageSOCView-SchedulingDetails-PublishButton").click; b.loading.wait_while_present }

  element(:lock_popup_div) { |b| b.div(id: "lockConfirmDialog") }
  action(:lock_confirm_action) { |b| b.lock_popup_div.radio(index: 0).click; b.loading.wait_while_present }
  action(:lock_cancel_action) { |b| b.lock_popup_div.radio(index: 1).click; b.loading.wait_while_present }

  element(:schedule_popup_div) { |b| b.div(id: "massScheduleConfirmDialog") }
  action(:schedule_confirm_action) { |b| b.schedule_popup_div.radio(index: 0).click; b.loading.wait_while_present }
  action(:schedule_cancel_action) { |b| b.schedule_popup_div.radio(index: 1).click; b.loading.wait_while_present }

  element(:final_edit_popup_div) { |b| b.div(id: "finalEditConfirmDialog") }
  action(:final_edit_confirm_action) { |b| b.final_edit_popup_div.radio(index: 0).click; b.loading.wait_while_present }
  action(:final_edit_cancel_action) { |b| b.final_edit_popup_div.radio(index: 1).click; b.loading.wait_while_present }

  element(:publish_popup_div) { |b| b.div(id: "massPublishConfirmDialog") }
  action(:publish_confirm_action) { |b| b.publish_popup_div.radio(index: 0).click; b.loading.wait_while_present }
  action(:publish_cancel_action) { |b| b.publish_popup_div.radio(index: 1).click; b.loading.wait_while_present }

  element(:soc_status_table) { |b| b.div(id: "ManageSOCView-StatusHistory-SubSection2").table }
  STATE_COLUMN = 0
  EFFECTIVE_DATE_COLUMN = 1

  SOC_DRAFT = 'Draft'
  SOC_OPEN = 'Open'
  SOC_LOCKED = 'Locked'
  SOC_FINAL_EDITS = 'Final Edits'
  SOC_PUBLISHED = 'Published'
  SOC_CLOSED = 'Closed'

  def target_row soc_state
    soc_status_table.row(text: /#{Regexp.escape(soc_state)}/)
  end

  def soc_status
    last_completed_state = 'Draft'
    soc_status_table.rows[1..-1].each do |row|
      puts row.cells[EFFECTIVE_DATE_COLUMN].text
      if row.cells[EFFECTIVE_DATE_COLUMN].text != ''
        last_completed_state = row.cells[STATE_COLUMN].text
      else
        break
      end
    end
    last_completed_state
  end

  element(:soc_scheduling_status) { |b| b.span(id: "socSchedulingStatus_control").text }
  element(:soc_publishing_status) { |b| b.span(id: "socPublishingStatus_control").text }
  element(:schedule_initiated_date) { |b| b.span(id: "schedule_initiated_date_control").text }
  element(:schedule_completed_date) { |b| b.span(id: "schedule_completed_date_control").text }
  element(:schedule_duration) { |b| b.span(id: "schedule_duration_control").text }
  element(:publish_initiated_date) { |b| b.span(id: "publish_initiated_date_control").text }
  element(:publish_completed_date) { |b| b.span(id: "publish_completed_date_control").text }
  element(:publish_duration) { |b| b.span(id: "publish_duration_control").text }


  element(:status_table) { |b| b.div(id: "ManageSOCView-StatusHistory-SubSection2").table }

  def is_date_exists(status_name)
    status_table.row(text: /\b#{Regexp.escape(status_name)}\b/).cells[1].text != nil
  end

end