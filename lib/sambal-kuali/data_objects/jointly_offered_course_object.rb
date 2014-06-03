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
                  :search_by_description,
                  :auto_lookup,
                  :defer_save


  def initialize (browser, opts={})
    @browser = browser
    defaults = {
        jointly_offered_course_count: 1,
        jointly_offered_course: "ENGL679",
        search_by: "Courses and Proposals", # "Courses Only", "Proposals Only"
        search_type: "name", # "name", "course code", "plain text", "blank"
        search_by_given_name: "English",
        search_by_course_code: "ENGL",
        search_by_description: "writing",
        auto_lookup: false,
        defer_save: false

    }

    set_options(defaults.merge(opts))
  end

  def create
    on CmCourseInformation do |page|
      page.add_jointly_offered_course

      if @auto_lookup
        page.jointly_offered_course(@jointly_offered_course_count).set @jointly_offered_course
        page.auto_lookup @jointly_offered_course
      else
        page.jointly_offered_quick_find(@jointly_offered_course_count)
        advanced_search_select
      end

      @jointly_offered_course = page.jointly_offered_course(@jointly_offered_course_count).value

    end
  end


  def edit (opts={})
    on CmCourseInformation do |page|
      page.expand_course_listing_section unless page.collapse_course_listing_section.visible?
      page.jointly_offered_course(opts[:jointly_offered_course_count]).fit opts[:jointly_offered_course]
    end
    determine_save_action unless opts[:defer_save]
    set_options(opts)
  end

  def advanced_search_select
    on CmAdvancedSearchPage do |search|

      case @search_type
        when "name"
          search.jointly_offered_search_by.select @search_by unless @search_by.nil?
          search.jointly_offered_given_name.set @search_by_given_name
          search.jointly_offered_search
          search.return_value(@search_by_given_name)
        when "course code"
          search.jointly_offered_search_by.select @search_by unless @search_by.nil?
          search.jointly_offered_course_code.set @search_by_course_code
          search.jointly_offered_search
          search.return_value(@search_by_course_code)
        when "plain text"
          search.jointly_offered_search_by.select @search_by unless @search_by.nil?
          search.jointly_offered_text.set @search_by_description
          search.jointly_offered_search
          search.return_value(@search_by_description)
        when "blank"
          search.jointly_offered_search_by.select @search_by unless @search_by.nil?
          search.jointly_offered_course_code.set ""
          search.jointly_offered_search
          search.return_value("Select")
      end
    end
  end

  def delete (opts={})
    on CmCourseInformation do |page|
      page.expand_course_listing_section unless page.collapse_course_listing_section.visible?
      page.delete_jointly_offered_course(opts[:jointly_offered_course_count])
    end
    determine_save_action unless opts[:defer_save]
  end

end





