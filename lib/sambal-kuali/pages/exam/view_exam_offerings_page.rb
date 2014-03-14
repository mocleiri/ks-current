class ViewExamOfferings < BasePage

  wrapper_elements
  frame_element
  expected_element :exam_offerings_page_section

  element(:exam_offerings_page_section) { |b| b.frm.div(id: "viewExamOfferingsPage")}

  element(:canceled_table_header_text) { |b| b.exam_offerings_page_section.span( class: "uif-headerText-span").text}
  element(:canceled_eo_table) { |b| b.frm.div( class: "uif-tableCollectionSection").table}

  element(:eo_table_section) { |b| b.frm.div(class: /uif-boxSection uif-boxLayoutVerticalItem clearfix/)}
  element(:table_header) { |b| b.eo_table_section.span(class: "uif-headerText-span")}
  value(:table_header_text) { |b| b.table_header.text}

  element(:cluster_list_div)  { |b| b.frm.eo_table_section.div(class: "uif-stackedCollectionLayout") }

  STATUS = 0
  CO_DAYS = 1
  AO_CODE = 1
  CO_ST_TIME = 2
  AO_TYPE = 2
  CO_ST_TIME_AMPM = 3
  AO_DAYS = 3
  CO_END_TIME = 4
  AO_ST_TIME = 4
  CO_END_TIME_AMPM = 5
  AO_ST_TIME_AMPM = 5
  CO_BLDG = 6
  AO_END_TIME = 6
  CO_ROOM = 7
  AO_END_TIME_AMPM = 7
  AO_BLDG = 8
  AO_ROOM = 9

  def eo_by_co_results_table
    return eo_table_section.table unless !eo_table_section.table.exists?
  end

  def eo_by_ao_results_table(cluster_private_name = :default_cluster)
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
    raise "error in activity_offering_results_table - no AOs for #{cluster_private_name}"
  end

  def eo_by_co_target_row
    row = eo_by_co_results_table.rows[1]
    return row unless row.nil?
  end

  def eo_by_ao_target_row(code, cluster_private_name = :default_cluster)
    row = eo_by_ao_results_table(cluster_private_name).row(text: /\b#{Regexp.escape(code)}\b/)
    return row unless row.nil?
    raise "error in target_row: #{code} not found"
  end

  def get_eo_by_co_status_text
    eo_by_co_target_row.cells[STATUS].text
  end

  def get_eo_by_ao_status_text(code, cluster_private_name = :default_cluster)
    eo_by_ao_target_row(code, cluster_private_name).cells[STATUS].text
  end

  def get_eo_by_ao_type_text(code, cluster_private_name = :default_cluster)
    eo_by_ao_target_row(code, cluster_private_name).cells[AO_TYPE].text
  end

  def get_eo_by_co_days_text
    eo_by_co_target_row.cells[CO_DAYS].select(id: /eoRsiDayInExamPeriod/).option( selected: "selected").text
  end

  def get_eo_by_ao_days_text(code, cluster_private_name = :default_cluster)
    eo_by_ao_target_row(code, cluster_private_name).cells[AO_DAYS].select(id: /eoRsiDayInExamPeriod/).option( selected: "selected").text
  end

  def get_eo_by_co_st_time_text
    eo_by_co_target_row.cells[CO_ST_TIME].text_field(id: /eoRsiStartTime/).value.to_s
  end

  def get_eo_by_ao_st_time_text(code, cluster_private_name = :default_cluster)
    eo_by_ao_target_row(code, cluster_private_name).cells[AO_ST_TIME].text_field(id: /eoRsiStartTime/).value.to_s
  end

  def get_eo_by_co_end_time_text
    eo_by_co_target_row.cells[CO_END_TIME].text_field(id: /eoRsiEndTime/).value.to_s
  end

  def get_eo_by_ao_end_time_text(code, cluster_private_name = :default_cluster)
    eo_by_ao_target_row(code, cluster_private_name).cells[AO_END_TIME].text_field(id: /eoRsiEndTime/).value.to_s
  end

  def get_eo_by_co_bldg_text
    eo_by_co_target_row.cells[CO_BLDG].div(id: /eoRsiBuilding/).text
  end

  def get_eo_by_ao_bldg_text(code, cluster_private_name = :default_cluster)
    eo_by_ao_target_row(code, cluster_private_name).cells[AO_BLDG].div(id: /eoRsiBuilding/).text
  end

  def get_eo_by_co_room_text
    eo_by_co_target_row.cells[CO_ROOM].div(id: /eoRsiRoom/).text
  end

  def get_eo_by_ao_room_text(code, cluster_private_name = :default_cluster)
    eo_by_ao_target_row(code, cluster_private_name).cells[AO_ROOM].div(id: /eoRsiRoom/).text
  end

  def count_no_of_eos_by_co
    row_count = 0
    eo_by_co_results_table.rows.each do |row|
      if row.text !~ /^Status.*$/ and row.text != ""
        row_count += 1
      end
    end
    return "#{row_count}"
  end

  def view_eo_by_ao(code, cluster_private_name = :default_cluster)
    view_eo_by_ao_link(code, cluster_private_name).click
    loading.wait_while_present
  end

  def view_eo_by_ao_link(code, cluster_private_name = :default_cluster)
    eo_by_ao_results_table(cluster_private_name).link(text: code)
  end

  def return_array_of_ao_codes(cluster_private_name = :default_cluster)
    array = []
    results = eo_by_ao_results_table(cluster_private_name)
    if results != nil
      results.rows.each do |row|
        if row.cells[AO_CODE].text =~ /^[A-Z]$/
          array << row.cells[AO_CODE].text
        end
      end
    end
    if !array.empty?
      return array
    else
      return nil
    end
  end

  def cluster_div_list
    div_list = []
    if cluster_list_div.exists?
      div_list = cluster_list_div.divs(class: "uif-collectionItem uif-boxCollectionItem")
    end
    div_list
  end

  def target_cluster(private_name)
    div_list = cluster_div_list
    return div_list[0] if private_name == :default_cluster
    cluster_div_list.each do |div_element|
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
end