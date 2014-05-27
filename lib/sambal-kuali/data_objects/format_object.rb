class CmFormatsObject < DataFactory

  include Foundry
  include DateFactory
  include StringFactory
  include Workflows
  include Utilities

  attr_accessor   :format_level,
                  :activity_level,
                  :type,
                  :contacted_hours,
                  :contact_frequency,
                  :duration_count,
                  :duration_type,
                  :class_size


  def initialize (browser, opts={})
    @browser = browser
    defaults = {
            format_level: 1,
            activity_level: 1,
            type: '::random::',
            contacted_hours: (1..9).to_a.sample,
            contact_frequency: '::random::',
            duration_count: (1..9).to_a.sample,
            duration_type: '::random::',
            class_size: (1..9).to_a.sample

    }
    set_options(defaults.merge(opts))
  end

  def create (opts = {})
    on CmCourseLogistics do |page|
      page.type(@format_level,@activity_level).pick! @type
      page.course_logistics unless page.current_page('Course Logistics').exists?
      sleep 1 # to allow to drop down selection to catch up
      page.contacted_hours(@format_level,@activity_level).fit @contacted_hours
      page.contact_frequency(@format_level,@activity_level).pick! @contact_frequency
      page.duration_count(@format_level,@activity_level).fit @duration_count
      page.duration_type(@format_level,@activity_level).pick! @duration_type
      page.class_size(@format_level,@activity_level).fit @class_size
    end
  end


  def edit (opts={})
    on CmCourseLogistics do |page|
      page.course_logistics unless page.current_page('Course Logistics').exists?
      page.type(opts[:format_level],opts[:activity_level]).pick! opts[:type] unless opts[:type].nil?
      page.contacted_hours(opts[:format_level],opts[:activity_level]).fit opts[:contacted_hours]
      page.contact_frequency(opts[:format_level],opts[:activity_level]).pick! opts[:contact_frequency] unless opts[:contact_frequency].nil?
      page.duration_type(opts[:format_level],opts[:activity_level]).pick! opts[:duration_type] unless opts[:duration_type].nil?
      page.duration_count(opts[:format_level],opts[:activity_level]).fit opts[:duration_count]
      page.class_size(opts[:format_level],opts[:activity_level]).fit opts[:class_size]
    end
    determine_save_action
    set_options(opts)
  end


  def delete (opts={})
  on CmCourseLogistics do |page|
    page.course_logistics unless page.current_page('Course Logistics').exists?
    page.delete_format opts[:activity_level].nil?
    page.loading_wait
  end
  determine_save_action
end

def determine_save_action
  on CmCourseInformation do |create|
    create.save_progress if create.logged_in_user == "Alice"
    create.save_and_continue if create.logged_in_user == "Fred"
  end
end

end