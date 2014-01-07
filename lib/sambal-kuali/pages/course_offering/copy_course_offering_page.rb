class CopyCourseOffering < BasePage

  wrapper_elements
  frame_element

  expected_element :exclude_instructor

  element(:create_copy_element) { |b| b.frm.button(id: "createFromCopy_btn") }
  action(:create_copy) { |b| b.frm.create_copy_element.click; b.loading.wait_while_present(120) }

  element(:exclude_cancelled_aos) { |b| b.frm.label(text: /Exclude cancelled Activity Offerings/) }
  action(:select_exclude_cancelled_aos) { |b| b.exclude_cancelled_aos.wait_until_present; b.exclude_cancelled_aos.click }

  element(:exclude_scheduling) { |b| b.frm.label(text: /Exclude scheduling information/) }
  action(:select_exclude_scheduling) { |b| b.exclude_scheduling.wait_until_present; b.exclude_scheduling.click }

  element(:exclude_instructor) { |b| b.frm.label(text: /Exclude instructor information/) }
  action(:select_exclude_instructor) { |b| b.exclude_instructor.wait_until_present; b.exclude_instructor.click }


end