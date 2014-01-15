class CourseOfferingCreateEdit < BasePage

  wrapper_elements
  validation_elements
  frame_element

  expected_element :suffix

  action(:submit) { |b| b.frm.button(text: "Update").click; b.loading.wait_while_present }
  action(:save_progress) { |b| b.frm.button(text: "Save Progress").click; b.loading.wait_while_present }
  #action(:cancel) { |b| b.frm.link(id: "COEdit_CancelLink").click; b.loading.wait_while_present }

  element(:sticky_footer_div) { |b| b.frm.div(class: "ks-uif-footer uif-stickyFooter uif-stickyButtonFooter", index: 1) }
  action(:create_offering) { |b| b.frm.button(id: "createUpdateFromCatalog_btn").click; b.loading.wait_while_present }
  action(:cancel) { |b| b.sticky_footer_div.link(text: "Cancel").click; b.loading.wait_while_present }

  # CO-navigation carousel (and confirmation dialog)
  element(:edit_previous_co_link) { |b| b.frm.link(id: "edit_co_prev") }
  action(:edit_previous_co) { |b| b.edit_previous_co_link.click; b.loading.wait_while_present }
  element(:edit_next_co_link) { |b| b.frm.link(id: "edit_co_next") }
  action(:edit_next_co) { |b| b.edit_next_co_link.click; b.loading.wait_while_present }
  element(:navigation_confirmation_dialog) { |b| b.div(class: "fancybox-wrap fancybox-desktop fancybox-type-html fancybox-opened").div(id: "CourseOfferingEdit-NavigationConfirmation") }

  element(:nav_save_and_continue_element) { |b| b.navigation_confirmation_dialog.button(id: "edit_co_save_and_continue") }
  action(:navigation_save_and_continue) { |b| b.nav_save_and_continue_element.wait_until_present; b.nav_save_and_continue_element.click; b.loading.wait_while_present }

  element(:nav_cancel_and_continue_element) { |b| b.navigation_confirmation_dialog.button(id: "edit_co_cancel") }
  action(:navigation_cancel_and_continue) { |b| b.nav_cancel_and_continue_element.wait_until_present; b.nav_cancel_and_continue_element.click; b.loading.wait_while_present }
  element(:edit_relatedCos_dropdown_list) { |b| b.frm.select(id: "edit_co_select_control") }

  element(:term_label_div) { |b| b.frm.div(data_label: "Term") }

  element(:suffix) { |b| b.frm.div(data_label: "Course Number Suffix").text_field }

  element(:grading_reg_opts_div) { |b| b.frm.div(id: "KS-GradingAndRegOptions") }
  element(:grading_option_div) { |b| b.frm.div(id: "gradingOptionId") }
  action(:set_grading_option) { |option,b| b.frm.radio(value: "kuali.resultComponent.grade.#{option.downcase}").click() }
  element(:grading_letter) { |b| b.grading_reg_opts_div.radio(text: "Letter") }

  element(:pass_fail_checkbox) { |b| b.checkbox(name: "document.newMaintainableObject.dataObject.passFailStudentRegOpts") }
  element(:audit_checkbox) { |b| b.checkbox(name: "document.newMaintainableObject.dataObject.auditStudentRegOpts") }

  element(:credit_type_option_fixed) { |b| b.frm.radio(id: "KS-CourseOfferingEdit-CreditType_OptionTypeSelector_control_0") }

  element(:fixed_credit_select_menu) { |b| b.frm.select(id: "KS-CourseOfferingEdit-CreditType_OptionTypeFixed_control") }
  action(:select_fixed_credit_option) { |b| b.credit_type_option_fixed.set() }

  def select_fixed_credits(credits)
    fixed_credit_select_menu.select(credits)
  end

  element(:credit_type_option_multiple) { |b| b.frm.radio(id: "KS-CourseOfferingEdit-CreditType_OptionTypeSelector_control_1") }
  action(:select_multiple_credit_option) { |b| b.credit_type_option_multiple.set() }

  def multiple_credit_checkbox(credit_value)
    checkbox(name: "document.newMaintainableObject.dataObject.creditOption.credits", value: "#{credit_value}")
  end

  def set_multiple_credit_checkbox(credit_value)
    multiple_credit_checkbox(credit_value).set
  end

  def clear_multiple_credit_checkbox(credit_value)
    multiple_credit_checkbox(credit_value).clear
  end

  value(:delivery_assessment_warning) { |b| b.error_list.li( class: "uif-warningMessageItem").text}

  element(:final_exam_option_div) { |b| b.frm.div(id: "finalExamType") }
  #TODO:: need elements for AZ here
  action(:final_exam_option_standard) { |b| b.final_exam_option_standard_element.set; b.loading.wait_while_present}
  element(:final_exam_option_standard_element) { |b| b.final_exam_option_div.radio(value: "STANDARD")}
  action(:final_exam_option_alternate) { |b| b.final_exam_option_alternate_element.set; b.loading.wait_while_present }
  element(:final_exam_option_alternate_element) { |b| b.final_exam_option_div.radio(value: "ALTERNATE") }
  action(:final_exam_option_none) { |b| b.final_exam_option_none_element.set; b.loading.wait_while_present }
  element(:final_exam_option_none_element) { |b| b.final_exam_option_div.radio(value: "NONE") }

  element(:cross_listed_as_label) { |b| b.frm.span(text:/Crosslisted as:/)}
  element(:cross_listed_co_check_boxes) { |b| b.frm.div(id:"KS-COEditListed-Checkbox-Group")}
  element(:cross_listed_co_check_box) { |b| b.checkbox(id: "KS-COEditListed-Checkbox-Group_control_0") }
  action(:cross_listed_co_set) {|b| b.cross_listed_co_check_box.set; b.loading.wait_while_present   }
  action(:cross_listed_co_clear) {|b| b.cross_listed_co_check_box.clear; b.loading.wait_while_present   }

  element(:final_exam_driver_element) { |b| b.frm.select(id: "KS-CourseOfferingEdit-FinalExamDriver_control") }
  action(:final_exam_driver_select) { |driver,b| b.final_exam_driver_element.select driver; b.loading.wait_while_present }

  element(:use_exam_matrix_div) { |b| b.frm.div(id: "finalExamMatrix")}
  element(:use_exam_matrix_checkbox) { |b| b.use_exam_matrix_div.checkbox}

  action(:set_exam_matrix) { |b| b.use_exam_matrix_checkbox.set }
  action(:clear_exam_matrix) { |b| b.use_exam_matrix_checkbox.clear }

  def check_final_exam_matrix( use_final_exam_matrix)
    if use_final_exam_matrix
      set_exam_matrix
    else
      clear_exam_matrix
    end
  end

  element(:delivery_formats_table) { |b| b.frm.div(id: "KS-CourseOffering-FormatOfferingSubSection").table() }
  FORMAT_COLUMN = 0
  GRADE_ROSTER_LEVEL_COLUMN = 1
  FINAL_EXAM_DRIVER_COLUMN = 2
  FINAL_EXAM_ACTIVITY_COLUMN = 3
  ACTIONS_COLUMN = 4

  #work for the newest row, ie when adding
  element(:new_format_select) {|b| b.delivery_formats_table.rows[-2].cells[FORMAT_COLUMN].select }
  element(:new_grade_roster_level_select) {|b| b.delivery_formats_table.rows[-2].cells[GRADE_ROSTER_LEVEL_COLUMN].select }
  value(:new_final_exam_driver_value) { |b| b.delivery_formats_table.rows[-2].cells[FINAL_EXAM_DRIVER_COLUMN].text}
  element(:new_final_exam_activity_select)  {|b| b.delivery_formats_table.rows[-2].cells[FINAL_EXAM_ACTIVITY_COLUMN].select }
  value(:new_final_exam_activity_value) { |b| b.new_final_exam_activity_select.selected_options[0].text}
  element(:new_delivery_format_delete)  {|b| b.delivery_formats_table.rows[-2].cells[ACTIONS_COLUMN].link(text: "Delete") }

  element(:add_format_btn) { |b| b.frm.button(id: "KS-CourseOffering-FormatOfferingSubSection_add")}
  action(:add_format) { |b| b.add_format_btn.click; b.loading.wait_while_present }
  #end elements for adding delivery format

  def set_delivery_format_(format)
    if new_format_select.include? format
      format_select.select format
    else
      raise "Option #{format} not in format select"
    end
  end

  def set_grade_roster_level(grade_format)
      new_grade_roster_level_select.select grade_format
  end

  def set_final_exam_activity(final_exam_activity)
     new_final_exam_activity_select.select final_exam_activity
  end

  def edit_grade_roster_level(format,grade_format)
    delivery_format_row(format).cells[GRADE_ROSTER_LEVEL_COLUMN].select().select(grade_format)
  end

  def edit_final_exam_activity(format,final_exam_activity)
    delivery_format_row(format).cells[FINAL_EXAM_ACTIVITY_COLUMN].select().select(final_exam_activity)
  end

  def delivery_format_row(format)
    delivery_formats_table.rows[1..-1].each do |row|
      return row if row.cells[FORMAT_COLUMN].text == format
    end
    return nil
  end

  def delete_delivery_format(format)
    delivery_format_row(format).cells[ACTIONS_COLUMN].link(text: "Delete").click
  end

  def select_random_option(sel_list)
    options = sel_list.options.map{|option| option.text}
    if options != []
      options.delete_if{|a| a.index("Select") != nil or  a == "" }
      sel_opt = rand(options.length)
      sel_list.select(options[sel_opt])
      return options[sel_opt]
    end
  end

  element(:waitlist_div)  { |b| b.frm.div(id: "KS-CourseOfferingEdit-HasWaitlist") }
  element(:waitlist_checkbox) { |b| b.waitlist_div.checkbox() }
  value(:has_waitlist?) { |b| b.waitlist_checkbox.value == "on" }
  action(:waitlist_on )  { |b| b.waitlist_checkbox.set; b.loading.wait_while_present }
  action(:waitlist_off )  { |b| b.waitlist_checkbox.clear; b.loading.wait_while_present }

  element(:waitlist_popup_div) { |b| b.div(id: "CourseOfferingEdit-Waitlist-Inactivate-Prompt") }
  action(:waitlist_continue_action) { |b| b.waitlist_popup_div.radio(index: 0).click; b.loading.wait_while_present }
  action(:waitlist_cancel_action) { |b| b.waitlist_popup_div.radio(index: 1).click; b.loading.wait_while_present }

  ID_COLUMN = 0
  NAME_COLUMN = 1
  AFFILIATION_COLUMN = 2
  #ACTIONS_COLUMN -- defined above

  element(:personnel_div) { |b| b.frm.div(id: "KS-CourseOfferingEdit-PersonnelTableSection") }
  element(:personnel_table) { |b| b.personnel_div.table() }

  element(:add_person_id) { |b| b.personnel_table.rows[1].cells[ID_COLUMN].text_field() }
  element(:personnel_id) { |b| b.personnel_div.text_field(name: "document.newMaintainableObject.dataObject.instructors[0].offeringInstructorInfo.personId") }
  element(:personnel_name) { |b| b.personnel_div.text_field(name: "document.newMaintainableObject.dataObject.instructors[0].offeringInstructorInfo.personName") }
  element(:personnel_affiliation) { |b| b.personnel_table.rows[1].cells[AFFILIATION_COLUMN].select() }
  #action(:lookup_person) { |b| b.personnel_table.rows[1].cells[ID_COLUMN].image().click; b.loading.wait_while_present } # Need persistent ID!
  action(:lookup_person) { |b| b.personnel_table.rows[1].cells[ID_COLUMN].input(title: "Search Field").click; b.loading.wait_while_present }
  element(:add_affiliation) { |b| b.personnel_table.rows[1].cells[AFFILIATION_COLUMN].select() }
  element(:add_personnel_button_element) { |b| b.personnel_div.button(text: "Add Personnel") }
  action(:add_personnel) { |b| b.add_personnel_button_element.click; b.adding.wait_while_present } # Needs persistent ID value

  def update_affiliation(id, affiliation)
    target_person_row(id).select(index: 0).select affiliation
  end

  def get_affiliation(id)
    target_person_row(id).cells[AFFILIATION_COLUMN].select.selected_options[0].text  #cell is hard-coded, getting this value was very problematic
  end

  def get_person_name(id)
    target_person_row(id).cells[NAME_COLUMN].text  #cell is hard-coded, getting this value was very problematic
  end

  def delete_person(id)
    target_person_row(id).button.click
    loading.wait_while_present
  end

  ORG_ID_COLUMN = 0
  ORG_NAME_COLUMN = 1
  ORG_ACTIONS_COLUMN = 2

  element(:admin_orgs_table)  { |b| b.frm.div(id: "KS-CourseOfferingEdit-OrganizationSection").table() }

  element(:add_org_id) { |b| b.admin_orgs_table.rows[1].cells[ORG_ID_COLUMN].text_field() }
  action(:lookup_org) { |b| b.admin_orgs_table.rows[1].cells[ORG_ID_COLUMN].button().click; b.loading.wait_while_present }

  element(:add_org_id_new) { |b| b.admin_orgs_table.rows[-2].cells[ORG_ID_COLUMN].text_field() }
  action(:lookup_org_new) { |b| b.admin_orgs_table.rows[-2].cells[ORG_ID_COLUMN].button().click; b.loading.wait_while_present }

  action(:add_org_button) { |b| b.button(id: "KS-CourseOfferingEdit-OrganizationSection_add") }
  action(:add_org) { |b| b.add_org_button.click; b.adding.wait_while_present() }

  def get_org_name(id)
    target_orgs_row(id).cells[NAME_COLUMN].text  #cell is hard-coded, getting this value was very problematic
  end

  def delete_org(id)
    target_orgs_row(id).button(text: "delete").click
    loading.wait_while_present
  end

  element(:honors_flag) { |b| b.div(data_label: "Honors Flag").checkbox() }

  private

  def target_orgs_row(org_id)
    #workaround here as id field value is not returned in rows[1].text
    #admin_orgs_table.row(text: /#{Regexp.escape(org_id)}/)
    admin_orgs_table.rows[1..-1].each do |row|
       if row.cells[ORG_ID_COLUMN].text_field().value == org_id
         return row
       end
    end
  end

  def target_person_row(id)
    personnel_table.row(text: /#{Regexp.escape(id.to_s)}/)
  end

end