# Helper methods that don't properly belong elsewhere. This is
# a sort of "catch all" Module.
module Workflows

  # Site Navigation helpers...
  def navigate_to_course_search_home
    on KSRicePortal do |page|
      page.course_search_home
    end
  end

  def navigate_to_course_planner_home
    on KSRicePortal do |page|
      page.course_planner_home
    end
  end


  def log_in(user, pwd)
    current_user = ""

    visit KSRicePortal do |page|
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