class ViewAcademicTerms < BasePage

  frame_element
  wrapper_elements

  expected_element :acal_header_right

  element(:acal_header_right) { |b| b.frm.div(id: "KS-View-Acal-Header-RightGroup-PlaceHolder") }

  action(:go_to_calendar_tab) { |b| b.frm.a(id: "ui-id-1").click; b.loading.wait_while_present}
  action(:go_to_terms_tab) { |b| b.frm.a(id: "ui-id-2").click; b.loading.wait_while_present}

  element(:acal_overview_div) { |b| b.frm.div(id: "KS-AcademicCalendar-Overview-WithoutTerms") }
  value(:acal_name) { |b| b.acal_overview_div.div(data_label: "Academic Calendar Name").span(index: 1).text }
  value(:acal_start_date) { |b| b.acal_overview_div.div(data_label: "Start Date").span(index: 1).text }
  value(:acal_end_date) { |b| b.acal_overview_div.div(data_label: "End Date").span(index: 1).text }

  element(:acal_term_list_div) { |b| b.frm.div(id: "acal-term") }

  def terms_div_list
    term_info_div.div(class: "uif-stackedCollectionLayout").divs(class: "uif-group uif-boxGroup uif-verticalBoxGroup uif-collectionItem uif-boxCollectionItem")
  end
  private :terms_div_list

  def term_index_by_term_type(term_type)
    begin
      acal_term_list_div.link(text: /^#{term_type}$/).id[/\d+(?=_toggle)/]
    rescue
      #if the term_type is not found
      return -1
    end
  end

  def open_term_section(term_type)
    link =  acal_term_list_div.link(text: "#{term_type}")
    if link.image(alt: "collapse").visible? then # collapse means collapsed
      link.click
    end
  end

  #def target_term_div(term_type)
  #  terms_div_list.each do |div|
  #    if (div.cells[POPULATION_NAME].text =~ /\b#{name}\b/)
  #      return div
  #    end
  #  end
  #end


  def term_name(term_type)
    index = term_index_by_term_type(term_type)
    acal_term_list_div.span(id: "term_name_line#{index}_control").text
  end

  def term_code(term_type)
    index = term_index_by_term_type(term_type)
    acal_term_list_div.span(id: "term_code_line#{index}_control").text
  end

  def term_start_date(term_type)
    index = term_index_by_term_type(term_type)
    acal_term_list_div.span(id: "term_start_date_line#{index}_control").text
  end

  def term_end_date(term_type)
    index = term_index_by_term_type(term_type)
    acal_term_list_div.span(id: "term_end_date_line#{index}_control").text
  end

  def term_instructional_days(term_type)
    index = term_index_by_term_type(term_type)
    acal_term_list_div.span(id: "term_instructional_days_line#{index}_control").text
  end

  def term_status(term_type)
    index = term_index_by_term_type(term_type)
    acal_term_list_div.span(data_aft_id: /Lozenge/, index: index.to_i).text
  end

  def term_status_by_term_code(term_code)
    acal_term_list_div.span(data_aft_id: "#{term_code}-Draft-Lozenge").text
  end

  #date_group_type - instructional, registration
  def key_date_group_div(term_type, date_group_type)
    index = term_index_by_term_type(term_type)
    group_divs = acal_term_list_div.div(id: "acal-term-keydatesgroup_line#{index}").div(class: "uif-stackedCollectionLayout").divs(class: "uif-collectionItem uif-boxCollectionItem")
    group_divs.each do |group_div |
      if (group_div.span(class: "uif-headerText-span").text.upcase ==  "#{date_group_type.upcase} KEY DATES") then
        return group_div
      end
    end
    return nil
  end



  KEY_DATE_TYPE_COL = 0
  KEY_DATE_START = 1
  KEY_DATE_END = 2

  def key_date_group_info_table(term_type, date_group_type)
    key_date_group_div(term_type, date_group_type).table
  end

  def target_key_date_row(term_type, date_group_type, key_date_type)
    begin
      row = key_date_group_info_table(term_type, date_group_type).row(text: /^\b#{key_date_type}\b$/)
    rescue
      return nil
    end
    return row
  end

  def key_date_start(term_type, date_group_type, key_date_type)
    target_key_date_row(term_type, date_group_type, key_date_type).cells[KEY_DATE_START].text
  end

  def key_date_end(term_type, date_group_type, key_date_type)
    target_key_date_row.cells[KEY_DATE_END].text
  end

  #Final Exam
  def final_exam_section( term_type)
    term_index = term_index_by_term_type( term_type)
    acal_term_list_div.div( id: "acal-term-examdates_line#{term_index}")
  end

  def exam_target_row( term_type)
    final_exam_section( term_type).table.rows[1]
  end

  EXAM_STATUS = 0
  EXAM_START_DATE = 1
  EXAM_END_DATE = 2
  EXCLUDE_SATURDAY = 3
  EXCLUDE_SUNDAY = 4

  def get_exam_start_date( term_type); exam_target_row( term_type).cells[EXAM_START_DATE].text; end
  def get_exam_end_date( term_type); exam_target_row( term_type).cells[EXAM_END_DATE].text; end
  def get_exclude_saturday_value( term_type); exam_target_row( term_type).cells[EXCLUDE_SATURDAY].text; end
  def get_exclude_sunday_value( term_type); exam_target_row( term_type).cells[EXCLUDE_SUNDAY].text; end

end