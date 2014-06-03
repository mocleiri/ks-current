class CmCrossListedObject < DataFactory
  include Foundry
  include DateFactory
  include StringFactory
  include Workflows
  include Utilities

  attr_reader     :cross_list_course_count,
                  :cross_list_subject_code,
                  :cross_list_course_number,
                  :auto_lookup,
                  :defer_save



  def initialize (browser, opts={})
    @browser = browser
    defaults = {
        cross_list_course_count: 1,
        cross_list_subject_code: ["ENGL", "CHEM", "HIST", "PHYS", "WMST"].sample,
        cross_list_course_number: (100..999).to_a.sample,
        auto_lookup: false,
        defer_save: false

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

  def edit (opts={})
    on CmCourseInformation do |page|
      page.expand_course_listing_section unless page.collapse_course_listing_section.visible?
      page.cross_listed_course_subject(opts[:cross_list_course_count]).fit opts[:cross_list_subject_code]
      page.auto_lookup @cross_list_subject_code if @auto_lookup
      page.cross_listed_course_number(opts[:cross_list_course_count]).fit opts[:cross_list_course_number]
    end

    determine_save_action unless opts[:defer_save]
    set_options(opts)

  end
  
  def delete (opts={})
    on CmCourseInformation do |page|
      page.expand_course_listing_section unless page.collapse_course_listing_section.visible?
      page.delete_cross_listed_course(opts[:cross_list_course_count])
    end
    determine_save_action unless opts[:defer_save]
  end



end

