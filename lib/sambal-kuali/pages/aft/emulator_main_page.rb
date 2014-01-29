class EmulatorMainPage < PageFactory

  page_url "http://quirktools.com/screenfly"
  expected_element :tools_div

  element(:tools_div) { |b| b.div(id: "screenfly-tools") }
  element(:site_form) { |b| b.form(id: "screenfly-form") }
  element(:site_input) { |b| b.site_form.text_field(id: "siteurl") }
  element(:site_submit_button) { |b| b.site_form.input(id: "go") }
  action(:site_submit) { |b| b.site_submit_button.click} #; b.loading.wait_while_present }  need new loading meth.

  def display_website(site)
    site_input.set site
    site_submit
  end

end