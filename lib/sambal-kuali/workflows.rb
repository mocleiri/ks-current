# Helper methods that don't properly belong elsewhere. This is
# a sort of "catch all" Module.
module Workflows

  # Site Navigation helpers...
  def navigate_to_find_course_proposal
    on(CmCurriculum).find_a_course_proposal
  end

  def navigate_rice_to_cm_home
    on CmRice do |create|
      #puts @assessment_a_f.inspect
      create.curriculum_management
    end
  end

  def navigate_to_create_course_proposal
    on(CmCurriculum).create_a_course
  end

  def navigate_to_cm_home
    on(CmCourseInformation).cm_home_via_breadcrumb
  end

  def navigate_to_functional_home
    on(CmCourseInformation).functional_home_via_breadcrumb
  end



  def log_in(user, pwd)
    current_user = ""

    visit KSFunctionalHome do |page|
      current_user = page.current_logged_in_user_id
      if current_user == :no_user
        page.login_with user, pwd
      elsif current_user != user
        page.logout
        visit Login do |page|
          page.login_with user, pwd
        end
      end
    end
  end

end