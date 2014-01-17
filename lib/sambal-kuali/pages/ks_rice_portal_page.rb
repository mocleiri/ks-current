class KSRicePortal < BasePage

  page_url "#{$test_site}/portal.do"
  wrapper_elements

  element(:course_search_link) { |b| b.link(text: "KSAP Course Search") }
  action(:course_search_home) { |b| b.course_search_link.click }


  element(:course_planner_link) { |b| b.link(text:"KSAP Planner") }
  action(:course_planner_home) { |b| b.course_planner_link.click }

  element(:enrollment_link) { |b| b.div(id: "KS-StudentHome-Enrollment") }
  action(:enrollment_home) { |b| b.enrollment_home_link.click }

  #action(:kuali_student_home) { |b| b.link(text: "Kuali Student Home").click }
  action(:cm_home) { |b| b.div(id: "KS-StudentHome-CurriculumManagement").click }
  action(:ks_maintenace) { |b| b.div(id: "KS-StudentHome-KSMaintenance").click }

  element(:username_field) { |b| b.text_field(:name=>"j_username") }
  element(:password_field) { |b| b.text_field(:name=>"j_password") }
  element(:login_button) { |b| b.button(:value=>"Login") }
  action(:logout) { |b| b.div(id: "search").button.click }
  value(:logged_in_user) { |b| b.div(id: "login-info").text }
  element(:search_for_course) { |b| b.frm.select(name: "searchQuery") }



  def login_with username, password
    username_field.set username
    password_field.set password
    login_button.click
    #enrollment_link.wait_until_present
    course_search_link.wait_until_present
    sleep 2
  end


  def current_logged_in_user_id
    user = ""
    begin
      user = logged_in_user.slice(16, logged_in_user.length)
    rescue Watir::Exception::UnknownObjectException
      user = :no_user
    end
    user
  end


end