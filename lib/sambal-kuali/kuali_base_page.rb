class BasePage < PageFactory

  class << self

    def wrapper_elements
      element(:logout_el) { |b| b.link(text: "Logout") }
      action(:logout) { |b| b.logged_in_link.click; b.logout_el.click }

      action(:enrollment_via_breadcrumb) { |b| b.link(id: "KS-HomewardPathBreadcrumbs-Enrollment").click }
      action(:home) { |b| b.frm.link(id: "KS-HomewardPathBreadcrumbs-Home").click }

      action(:action_list) { |b| b.link(title: "Action List").click }
      element(:logged_in_link) { |b| b.div(class: "ks-uif-viewHeader-container navbar-inverse navbar").link(class: "dropdown-toggle") }
      value(:logged_in_user) { |b| b.div(class: "ks-uif-viewHeader-container navbar-inverse navbar").link(class: "dropdown-toggle").text }

      element(:loading) { |b| b.image(alt: "Loading...") }
      element(:adding) { |b| b.frm.image(alt: "Adding Line...") }
      element(:growl_div) { |b| b.frm.div(id: "jGrowl") }
      element(:growl_message_success_div) { |b| b.growl_div.div(class: /SUCCESS/).div(class: "jGrowl-message") }
      value(:growl_text) { |b| b.growl_message_success_div.wait_until_present; b.growl_message_success_div.text }
      element(:growl_message_warning_div) { |b| b.growl_div.div(class: /WARNING/).div(class: "jGrowl-message") }
      value(:growl_warning_text) { |b| b.growl_message_warning_div.wait_until_present; b.growl_message_warning_div.text }
    end

    def frame_element
      #element(:frm) { |b| b.frame(id: "iframeportlet") }
      element(:frm) { |b| b } #with iframe removed
    end

    def green_search_buttons
      action(:search) { |b| b.frm.button(text: "Search").click; b.loading.wait_while_present } # Persistent ID needed!
      action(:clear_values) { |b| b.frm.button(text: "Clear Values").click; b.loading.wait_while_present } # Persistent ID needed!
      action(:cancel) { |b| b.frm.button(text: "Cancel").click; b.loading.wait_while_present } # Persistent ID needed!
      action(:close) { |b| b.frm.button(text: "Close").click; b.loading.wait_while_present } # Persistent ID needed!
    end

    def validation_elements
      value(:error_header) { |b| b.frm.h3(id: "pageValidationHeader").text }
      value(:info_header) { |b| b.frm.h3(id: "pageValidationHeader").text }
      element(:error_list) { |b| b.frm.ul(id: "pageValidationList") }
      element(:info_list) { |b| b.frm.ul(id: "pageValidationList") }
      value(:first_error) { |b| b.error_list.link.text }
      value(:first_msg) { |b| b.info_list.li.text }
      element(:auth_error) { |b| b.error_list.li(text: /(:?You cannot access|You are not authorized)/) }
    end

    def krms_frame_elements
      #element(:frm) { |b| b.frame(:id=>/easyXDM_default\d+_provider/).frame(id: "iframeportlet") }
      element(:frm) { |b| b }
      element(:frm_popup) { |b| b.frame(:class=>"fancybox-iframe")}
    end
  end
end
