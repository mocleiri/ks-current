# Helper methods that don't properly belong elsewhere. This is
# a sort of "catch all" Module.
module Workflows

  # Site Navigation helpers...
  def go_to_rollover_details
    visit Enrollment do |page|
      page.view_rollover_details
    end
  end

  def go_to_perform_rollover
    visit Enrollment do |page|
      page.perform_rollover
    end
  end

  def go_to_create_population
    visit ManagePopulations do |page|
      page.create_new
    end
  end

  def go_to_manage_population
    visit ManagePopulations do |page|
      #page.manage_populations
    end
  end

  def go_to_manage_course_offerings
    visit Enrollment do |page|
      page.manage_course_offerings
    end
  end

  def go_to_display_schedule_of_classes
    # contrary to standard convention, we navigate directly to this view in order to supply an optional parameter
    # to force the display of the "Activity Offering Display"-selector widget
    visit DisplayScheduleOfClasses
  end

  def go_to_holiday_calendar
    visit Enrollment do |page|
      page.create_holiday_calendar
    end
  end

  def go_to_academic_calendar
    visit Enrollment do |page|
      page.create_academic_calendar
    end
  end

  def go_to_calendar_search
    visit Enrollment do |page|
      page.search_for_calendar_or_term
    end
  end

  def go_to_manage_time_slots
    visit Enrollment do |page|
      page.manage_time_slots
    end
  end

  def go_to_create_course_offerings
    visit Enrollment do |page|
      page.create_course_offerings
    end
  end

  def go_to_manage_soc
    visit Enrollment do |page|
      page.manage_soc
    end
  end

  #def go_to_manual_soc_change
  #  visit KSMaintenancePortal
  #  on(KSMaintenancePortal).test_soc_state_change
  #end
  #def go_to_krms_components
  #  visit KSMaintenancePortal do |page|
  #    page.krms_components
  #  end
  #end

  #def go_to_manage_co_agendas
  #  visit KSMaintenancePortal do |page|
  #    page.krms_manage_co_agendas
  #  end
  #end

  #def go_to_krms_manage_course_offerings
  #  visit KSMaintenancePortal do |page|
  #    page.manage_course_offerings
  #  end
  #end

  def go_to_manage_final_exam_matrix
    visit Enrollment do |page|
      page.manage_final_exam_matrix
    end
  end

  def log_in(user, pwd)
    current_user = ""

    visit KSFunctionalHome do |page|
      Watir::Wait.until { page.enrollment_link.present? || page.username_field.present? }
      current_user = page.current_logged_in_user_id
      if current_user == :no_user
        page.login_with user, pwd
      elsif current_user.downcase != user.downcase
        page.logout
        visit Login do |page|
          page.login_with user, pwd
        end
      end
    end
  end

  def log_out
    visit KSFunctionalHome do |page|
      Watir::Wait.until { page.enrollment_link.present? || page.username_field.present? }
      page.logout
    end
  end

end