class MobileLogin < PageFactory

  page_url "#{$test_site}/kscr-poc/index.jsp"

  expected_element :username_field

  #def frm
  #  self.frame(class: "fancybox-iframe")
  #end
  #element(:tools_div) { |b| b.div(id: "screenfly-tools") }
  #element(:site_form) { |b| b.form(id: "screenfly-form") }
  #element(:viewport_div) { |b| b.div(id: "viewport") }
  element(:username_field) { |b| b.text_field(:name=>"j_username") }
  element(:password_field) { |b| b.text_field(:name=>"j_password") }
  element(:login_button) { |b| b.button(:value=>"Login") }
  action(:logout) { |b| b.button(value: "Logout").click }

  def login_with username, password
    username_field.set username
    password_field.set password
    login_button.click
  end

end