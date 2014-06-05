class CmInstructorObject < DataFactory

  include Foundry
  include DateFactory
  include StringFactory
  include Workflows
  include Utilities

  attr_reader :instructor_name,
              :instructor_level




  def initialize(browser, opts={})
    @browser = browser
    defaults = {
        instructor_name: "TYSON",
        instructor_level: 1
    }
    set_options(defaults.merge(opts))

  end

  def create
    on CmCourseInformation do |page|
      page.course_information unless page.current_page('Course Information').exists?
      page.add_instructor unless page.instructor_name(@instructor_level).exists?
      page.instructor_name(@instructor_level).set @instructor_name
      page.auto_lookup @instructor_name
      @instructor_name = page.instructor_name(@instructor_level).value
    end
  end





  def edit (opts={})

    set_options(opts)
  end



  def delete

  end


end


