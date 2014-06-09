class CmInstructorObject < DataFactory

  include Foundry
  include DateFactory
  include StringFactory
  include Workflows
  include Utilities

  attr_reader :instructor_name,
              :instructor_level,
              :defer_save




  def initialize(browser, opts={})
    @browser = browser
    defaults = {
        instructor_name: "TYSON",
        instructor_level: 1,
        defer_save: false
    }
    set_options(defaults.merge(opts))

  end

  def create
    on CmCourseInformation do |page|
      page.course_information unless page.current_page('Course Information').exists?
      page.add_instructor unless page.instructor_name(@instructor_level).exists?
      page.instructor_name(@instructor_level).set @instructor_name
      page.auto_lookup @instructor_name
      #@instructor_name = page.instructor_name(@instructor_level).value
    end
  end





  def edit (opts={})
    on CmCourseInformation do |page|
      page.course_information unless page.current_page('Course Information').exists?
      page.add_instructor unless page.instructor_name(opts[:instructor_level]).exists?
      page.instructor_name(opts[:instructor_level]).fit opts[:instructor_name]
      page.auto_lookup opts[:instructor_name] unless opts[:instructor_name].nil?
    end
    set_options(opts)
  end



  def delete (opts={})
    on CmCourseInformation do |page|
      page.delete_instructor(opts[:instructor_level])
    end
    determine_save_action unless opts[:defer_save]
  end


end


