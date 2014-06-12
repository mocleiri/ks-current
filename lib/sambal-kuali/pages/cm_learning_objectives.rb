class CmLearningObjectives < BasePage

  wrapper_elements
  cm_elements # for loading

  action(:lookup_multiple_lines) { |b| b.link(text: 'Lookup/Add Multiple Lines').click }
  #SEARCH POPUP

  element(:keyword_in_learning_objective) { |b| b.frame(class: 'fancybox-iframe').text_field(name: 'lookupCriteria[descr.plain]') }
  element(:learning_objective_category) { |b| b.frame(class: 'fancybox-iframe').text_field(name: 'lookupCriteria[name]') }
  element(:organization_name) { |b| b.frame(class: 'fancybox-iframe').text_field(name: 'lookupCriteria[orgName]') }
  element(:organization_type) { |b| b.frame(class: 'fancybox-iframe').select_list(name:'lookupCriteria[orgType]') }
  # ['Student Oversight Unit', 'Deployment Division', 'Financial Control Unit', 'Curriculum Oversight Unit', 'Student Oversight Division',
  # 'Financial Resources Division', 'Curriculum Oversight Division', 'Financial Resources Unit', 'Deployment Unit',
  # 'Administering Org', 'Financial Control Division', 'Curriculum Oversight', 'Institution']

  action(:add_learning_objective) { |b| b.button(id: 'uif-addBlankLine-outcome').click; b.loading_wait }

  action(:objective_detail) { |objective_level, b| b.text_field(id: "KS-LoDisplayInfoWrapper-descr_line#{objective_level-1}_control") }
  action(:search_for_lo) {|objective_level,b| b.a(id: "KS-LoDisplayInfoWrapper-descr_line#{objective_level-1}_quickfinder_act") }
  action(:delete_learning_objective) { |objective_level, b| b.button(id: "LearningObjective-CollectionSection_del_line#{objective_level-1}").click ; b.deleting_line_wait }

  action(:base_category_detail) { |category_level,b| b.text_field(id: "KS-LearningObjective-Category_line#{category_level-1}_add_control") }
  action(:base_browse_for_categories) { |category_level, b| b.a(id: "KS-LearningObjective-Category_line#{category_level-1}_add_quickfinder_act") }
  action(:add_category) { |objective_level,b| b.button(id: "KS-LearningObjective-Category-SubSection_line#{objective_level-1}_add").click }

  action(:multiple_category_detail) { |objective_level,category_level,b| b.text_field(id: "KS-LearningObjective-Category_line#{objective_level-1}_line#{category_level-1}_control" ) }
  action(:multiple_browse_for_categories) { |objective_level,category_level,b| b.a(id: "KS-LearningObjective-Category_line#{objective_level-1}_line#{category_level-1}_quickfinder_act")}
  action(:delete_category) { |objective_level,category_level,b| b.button(id: "KS-LearningObjective-Category-SubSection_line#{objective_level-1}_del_line#{objective_level-1}_line#{category_level-1}").click; b.deleting_line_wait }
end
