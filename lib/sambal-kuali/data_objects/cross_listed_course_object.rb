class CmCrossListedObject < DataFactory
  include Foundry
  include DateFactory
  include StringFactory
  include Workflows
  include Utilities

  attr_reader     :cross_list_course_count,
                  :cross_list_subject_code,
                  :cross_list_course_number,
                  :auto_lookup


  def initialize (browser, opts={})
    @browser = browser
    defaults = {
        cross_list_course_count: 1,
        cross_list_subject_code: ["ENGL", "CHEM", "HIST", "PHYS", "WMST"].sample,
        cross_list_course_number: (100..999).to_a.sample,
        auto_lookup: false

    }
    set_options(defaults.merge(opts))
  end


  def create
      on CmCourseInformation do |page|
        page.expand_course_listing_section unless page.collapse_course_listing_section.visible?
        page.add_cross_listed_course
        page.cross_listed_course_subject(@cross_list_course_count).set @cross_list_subject_code
        page.auto_lookup @cross_list_subject_code if @auto_lookup
        page.cross_listed_course_number(@cross_list_course_count).set @cross_list_course_number
      end
  end




end


class CmJointlyOfferedObject < DataFactory
  include Foundry
  include DateFactory
  include StringFactory
  include Workflows
  include Utilities

  attr_reader     :jointly_offered_course_count,
                  :jointly_offered_course,
                  :jointly_offered_course_code,
                  :search_by_given_name,
                  :search_by_course_code,
                  :search_by_description
                  :auto_lookup


  def initialize (browser, opts={})
    @browser = browser
    defaults = {
        jointly_offered_course_count: 1,
        jointly_offered_course: "ENGL280",
        search_type: "name", # "name", "course code", "plain text", "blank"
        search_by_given_name: "test",
        search_by_course_code: ["ENGL", "CHEM", "HIST", "PHYS", "WMST"].sample,
        search_by_description: "literature",
        auto_lookup: false

    }

    set_options(defaults.merge(opts))
  end

  def create
    on CmCourseInformation do |page|
      page.add_jointly_offered_course
      if @auto_lookup
        page.jointly_offered_course(@jointly_offered_course_count).set @jointly_offered_course
        page.auto_lookup @jointly_offered_course
      end

      if !@auto_lookup
        page.jointly_offered_quick_find(@jointly_offered_course_count)
        advanced_search_select
      end
    end
  end

  def advanced_search_select
    on CmAdvancedSearchPage do |search|

      case @search_type
        when "name"
          search.jointly_offered_given_name.set @search_by_given_name
          search.jointly_offered_search
          search.return_value(@search_by_given_name)
        when "course code"
          search.jointly_offered_course_code.set @search_by_course_code
          search.jointly_offered_search
          search.return_value(@search_by_course_code)
        when "plain text"
          search.jointly_offered_text.set @search_by_description
          search.jointly_offered_search
          search.return_value(@search_by_description)
        when "blank"
          search.jointly_offered_course_code.set ""
          search.jointly_offered_search
          search.return_value("Select")
      end
    end
  end



  end



class CmVersionCodeObject < DataFactory
  include Foundry
  include DateFactory
  include StringFactory
  include Workflows
  include Utilities

  attr_accessor     :version_code_count,
                    :version_code,
                    :version_course_title

  def initialize (browser, opts={})
    @browser = browser
    defaults = {
        version_code_count: 1,
        version_code: ('A'..'Z').to_a.sample,
        version_course_title: random_alphanums(10, 'test course title ')
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

end