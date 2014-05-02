class ViewExamOfferings < BasePage

  wrapper_elements
  frame_element
  expected_element :exam_offerings_page_section

  element(:exam_offerings_page_section) { |b| b.frm.main(id: "viewExamOfferingsPage")}

  element(:eo_table_section) { |b| b.exam_offerings_page_section.div(data_parent: 'viewExamOfferingsPage') }
  element(:table_header) { |b| b.eo_table_section.header }
  value(:table_header_text) { |b| b.table_header.text}

  element(:cluster_list_div)  { |b| b.frm.section(id: 'KS-CourseOfferingManagement-ExamOfferingByAOClustersSection') }

  element(:cancel_link) { |b| b.frm.a( text: "Cancel")}
  action(:cancel) { |b| b.cancel_link.click; b.loading.wait_while_present}

  def co_eo_table
    return eo_table_section.table unless !eo_table_section.table.exists? #TODO: why unless?
  end

  VIEW_STATUS = 0
  VIEW_OVERRIDE_MATRIX = 1
  VIEW_CO_DAYS = 2
  VIEW_CO_ST_TIME = 3
  VIEW_CO_END_TIME = 4
  VIEW_CO_BLDG = 5
  VIEW_CO_ROOM = 6

  def co_target_row #always returns row 1, but this setup will allow future flexibility?
    co_eo_table.rows[1]
  end

  def get_eo_by_co_status_text #TODO: standardize these method names - eg 'co_status' instead of 'get_eo_by_co_status_text'
    co_target_row.cells[VIEW_STATUS].text
  end

  def get_eo_by_co_days_text
    #eo_by_co_target_row.cells[CO_DAYS].select(id: /eoRsiDayInExamPeriod/).option( selected: "selected").text
    co_target_row.cells[VIEW_CO_DAYS].text
  end


  def get_eo_by_co_st_time_text
    #eo_by_co_target_row.cells[CO_ST_TIME].text_field(id: /eoRsiStartTime/).value.to_s
    co_target_row.cells[VIEW_CO_ST_TIME].text
  end


  def get_eo_by_co_end_time_text
    #eo_by_co_target_row.cells[CO_END_TIME].text_field(id: /eoRsiEndTime/).value.to_s
    co_target_row.cells[VIEW_CO_END_TIME].text
  end


  def get_eo_by_co_bldg_text
    #eo_by_co_target_row.cells[CO_BLDG].div(id: /eoRsiBuilding/).text
    co_target_row.cells[VIEW_CO_BLDG].text
  end

  def get_eo_by_co_room_text
    #eo_by_co_target_row.cells[CO_ROOM].div(id: /eoRsiRoom/).text
    co_target_row.cells[VIEW_CO_ROOM].text
  end

  def co_eo_count
    row_count = 0
    co_eo_table.rows.each do |row|
      if row.text !~ /^Status.*$/ and row.text != ""
        row_count += 1
      end
    end
    return "#{row_count}"
  end

###############################

  VIEW_AO_CODE = 1
  VIEW_AO_TYPE = 2
  VIEW_AO_OVERRIDE_MATRIX = 3
  VIEW_AO_DAYS = 4
  VIEW_AO_ST_TIME = 5
  VIEW_AO_END_TIME = 6
  VIEW_AO_BLDG = 7
  VIEW_AO_ROOM = 8

  def ao_eo_table(cluster_private_name = :default_cluster)
    if cluster_private_name == :default_cluster then
      return cluster_div_list[0].table unless !cluster_div_list[0].table.exists?
    else
      cluster = target_cluster(cluster_private_name)
      if cluster != nil
        return cluster.table unless !cluster.table.exists?
      else
        return nil
      end
    end
    raise "error in activity_offering_results_table - no AOs for cluster: #{cluster_private_name}"
  end

  def eo_by_ao_target_row(code, cluster_private_name = :default_cluster)
    ao_eo_table(cluster_private_name).row(text: /\b#{Regexp.escape(code)}\b/)
  end


  def get_eo_by_ao_status_text(code, cluster_private_name = :default_cluster)
    eo_by_ao_target_row(code, cluster_private_name).cells[VIEW_STATUS].text
  end

  def get_eo_by_ao_type_text(code, cluster_private_name = :default_cluster)
    eo_by_ao_target_row(code, cluster_private_name).cells[VIEW_AO_TYPE].text
  end

  def get_eo_by_ao_days_text(code, cluster_private_name = :default_cluster)
    #eo_by_ao_target_row(code, cluster_private_name).cells[AO_DAYS].select(id: /eoRsiDayInExamPeriod/).option( selected: "selected").text
    eo_by_ao_target_row(code, cluster_private_name).cells[VIEW_AO_DAYS].text
  end

  def get_eo_by_ao_st_time_text(code, cluster_private_name = :default_cluster)
    #eo_by_ao_target_row(code, cluster_private_name).cells[AO_ST_TIME].text_field(id: /eoRsiStartTime/).value.to_s
    eo_by_ao_target_row(code, cluster_private_name).cells[VIEW_AO_ST_TIME].text
  end

  def get_eo_by_ao_end_time_text(code, cluster_private_name = :default_cluster)
    #eo_by_ao_target_row(code, cluster_private_name).cells[AO_END_TIME].text_field(id: /eoRsiEndTime/).value.to_s
    eo_by_ao_target_row(code, cluster_private_name).cells[VIEW_AO_END_TIME].text
  end

  def get_eo_by_ao_bldg_text(code, cluster_private_name = :default_cluster)
    #eo_by_ao_target_row(code, cluster_private_name).cells[AO_BLDG].div(id: /eoRsiBuilding/).text
    eo_by_ao_target_row(code, cluster_private_name).cells[VIEW_AO_BLDG].text
  end

  def get_eo_by_ao_room_text(code, cluster_private_name = :default_cluster)
    #eo_by_ao_target_row(code, cluster_private_name).cells[AO_ROOM].div(id: /eoRsiRoom/).text
    eo_by_ao_target_row(code, cluster_private_name).cells[VIEW_AO_ROOM].text
  end

  def view_eo_by_ao(code, cluster_private_name = :default_cluster)
    view_eo_by_ao_link(code, cluster_private_name).click
    loading.wait_while_present
  end

  def view_eo_by_ao_link(code, cluster_private_name = :default_cluster)
    eo_by_ao_results_table(cluster_private_name).link(text: code)
  end

  def ao_code_list(cluster_private_name = :default_cluster)
    array = []
    results = ao_eo_table(cluster_private_name)
    if results != nil
      results.rows.each do |row|
        if row.cells[VIEW_AO_CODE].text =~ /^[A-Z]$/
          array << row.cells[VIEW_AO_CODE].text
        end
      end
    end
    #TODO: nil check is built-in to step def logic, need some analysis to figure out if this is really necessary
    if !array.empty?
      return array
    else
      return nil
    end
  end

  def cluster_div_list
    div_list = []
    if cluster_list_div.exists?
      div_list = cluster_list_div.divs(id: /line\d+/, data_parent: 'KS-CourseOfferingManagement-ExamOfferingByAOClustersSection')
    end
    div_list
  end

  def target_cluster(private_name)
    div_list = cluster_div_list
    return div_list[0] if private_name == :default_cluster
    div_list.each do |div_element|
      if cluster_div_private_name(div_element) == private_name then
        return div_element
      end
    end
    return nil
  end

  def cluster_div_private_name(cluster_div_element)
    tmp_text = cluster_div_element.fieldset.label.text
    end_of_private_name = -1
    end_of_private_name = tmp_text.index('(')-2 unless tmp_text.index('(') == nil
    tmp_text[9..end_of_private_name]
  end

#edit fields methods
  def edit_rsi_element(row)
    row.link(id: /EO-toggleEditButton_line/)
  end

  def edit_rsi(row)
    edit_rsi_element(row).click
  end

  def override_checkbox(row)
    row.checkbox(id: /eoOverrideMatrix_line\d+_/)
  end

  def rsi_day(row)
    row.select(id: /eoRsiDayInExamPeriod_line\d+_/)
  end

  def rsi_start_time(row)
    row.text_field(id: /eoRsiStartTime_line\d+_/)
  end

  def rsi_end_time(row)
    row.text_field(id: /eoRsiEndTime_line\d+_/)
  end

  def rsi_facility(row)
    row.text_field(id: /eoRsiBuilding_line\d+_/)
  end

  def rsi_facility_lookup(row)
    row.div(id: /eoRsiBuilding_line/).image
  end

  def rsi_room(row)
    row.text_field(id: /eoRsiRoom_line\d+_/)
  end

  def rsi_room_lookup(row)
    row.div(id: /eoRsiRoom_line/).image
  end

  def save_edit(row)
    row.link(id: /EO-toggleUpdateButton_line/).click
    edit_rsi_element(row).wait_until_present
  end

  def cancel_edit(row)
    row.link(id: /EO-toggleCancelButton_line/).click
    edit_rsi_element(row)
  end
end