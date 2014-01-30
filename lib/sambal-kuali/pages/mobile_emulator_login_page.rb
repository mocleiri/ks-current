class MobileLogin < PageFactory

  page_url "http://quirktools.com/screenfly/#u=#{$test_site}/kscr-poc/index.jsp&w=320&h=568&a=37"

  expected_element :tools_div

  def frm
    self.frame(id: "frame")
  end

  element(:tools_div) { |b| b.div(id: "screenfly-tools") }
  element(:site_form) { |b| b.form(id: "screenfly-form") }
  element(:viewport_div) { |b| b.frm.div(id: "viewport") }
  element(:username_field) { |b| b.frm.text_field(:name=>"j_username") }
  element(:password_field) { |b| b.frm.text_field(:name=>"j_password") }
  element(:login_button) { |b| b.frm.button(:value=>"Login") }
  action(:logout) { |b| b.button(value: "Logout").click }

  def login_with username, password
    username_field.set username
    password_field.set password
    login_button.click
  end

end