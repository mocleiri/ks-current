class CmVersionCodeObject < DataFactory
  include Foundry
  include DateFactory
  include StringFactory
  include Workflows
  include Utilities

  attr_accessor     :version_code_count,
                    :version_code,
                    :version_course_title,
                    :defer_save

  def initialize (browser, opts={})
    @browser = browser
    defaults = {
        version_code_count: 1,
        version_code: ('A'..'Z').to_a.sample,
        version_course_title: random_alphanums(10, 'test course title '),
        defer_save: false
    }
    set_options(defaults.merge(opts))
  end

  def create
    on CmCourseInformation do |page|
      page.add_version_code
      page.version_code_code(@version_code_count).set @version_code
      page.version_code_title(@version_code_count).set @version_course_title
    end
  end

  def edit (opts={})
    on CmCourseInformation do |page|
      page.expand_course_listing_section unless page.collapse_course_listing_section.visible?
      page.version_code_code(opts[:version_code_count]).fit opts[:version_code]
      page.version_code_title(opts[:version_code_count]).fit opts[:version_course_title]
    end
    determine_save_action unless opts [:defer_save]
    set_options(opts)
  end





  def delete
    on CmCourseInformation do |page|
      page.expand_course_listing_section unless page.collapse_course_listing_section.visible?
      page.delete_version_code(@version_code_count)
    end
  end

end