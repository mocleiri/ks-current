class ActivityOfferingInquiry < BasePage

  wrapper_elements
  expected_element :term_element

  def frm
    self.iframe(class: "fancybox-iframe")
  end

  action(:close) { |b| b.frm.button(text: "Close").click; b.loading.wait_while_present }

  value(:subterm) { |b| b.frm.div(id: "subterm_name").text }
  value(:subterm_start_date) { |b| b.frm.div(id: "start_end_date").text[/.*(?=-)/].strip }
  value(:subterm_end_date) { |b| b.frm.div(id: "start_end_date").text[/(?<=-).*/].strip }
  value(:course_offering_code) { |b| b.frm.span(id: "u14").text } # Persistent ID needed!
  value(:activity_code) { |b| b.frm.div(id: "activityCode").text }
  value(:course_offering_title) { |b| b.frm.span(id: "u32").text } # Persistent ID needed!
  element(:term_element) { |b| b.frm.div(id: "term") }
  value(:term) { |b| b.frm.term_element.text }
  value(:type) { |b| b.frm.span(id: "type_name_control").text }
  value(:format_offering) { |b| b.frm.span(id: "u59").text } # Persistent ID needed!
  value(:total_maximum_enrollment) { |b| b.frm.span(id: "maximumEnrollment_label").text }
  element(:actual_scheduling_information) { |b| b.frm.section(id: "ActivityOffering-Inquiry-DeliveryLogistic-Actuals") }
  element(:actual_scheduling_information_days) { |b| b.frm.div(id: "adlDays_line0") }
  element(:requested_scheduling_information) { |b| b.frm.section(id: "ActivityOffering-Inquiry-DeliveryLogistic-Requested") }
  element(:requested_scheduling_information_days) { |b| b.frm.div(id: "rdlDays_line0") }
  value(:waitlists_active?) { |b| b.frm.div(data_label: "Waitlist Active").p(text: "Yes").exists? }
  value(:waitlists_processing) { |b| b.frm.div(data_label: "Waitlist Processing").p.text }
  value(:waitlists_max_size) { |b| b.frm.div(data_label: "Waitlist Max Size").p.text }
  value(:waitlists_allow_holds?) { |b| b.frm.div(data_label: "Allow Holds in Waitlist").p(text: "Yes").exists? }

  value(:state) { |b| b.frm.span(id: "u77").text } # Persistent ID needed!
  value(:requires_evaluation) { |b| b.frm.span(id: "u86").text } # Persistent ID needed!
  value(:honors_offering) { |b| b.frm.span(id: "u95").text } # Persistent ID needed!
  value(:activity_offering_url) { |b| b.frm.span(id: "u104").text } # Persistent ID needed!
  value(:affiliated_personnel) { |b| b.frm.span(id: "u114").text } # Persistent ID needed!
  value(:maximum_enrollment) { |b| b.frm.span(id: "u123").text } # Persistent ID needed!

end