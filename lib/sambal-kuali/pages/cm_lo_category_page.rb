class CmLoCategoryPage < BasePage

  wrapper_elements
  cm_elements

  action(:add_category) { |b| b.div(id: "KS-LoCatTable").button(class: "btn btn-primary btn-sm btn btn-primary").click }
  action(:update_category) { |b| b.button(text: "update").click }
  action(:delete_category) { |b| b.button(text: "delete").click }



end


class AddLoCategoryPopup < BasePage

expected_element :category_name

  def frm
    self.div(class: "fancybox-skin")
  end

  element(:category_name) { |b| b.frm.textarea(name: "newCollectionLines['loCategories'].name") }
  element(:category_type) { |b| b.frm.select_list(name: "newCollectionLines['loCategories'].typeKey") }
  action(:create_category) { |b| b.frm.button(id: "KS-LoCatTable_add").click }

end