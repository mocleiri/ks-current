class CmOptionalFieldsObject < DataFactory

  include Foundry
  include DateFactory
  include StringFactory
  include Workflows
  include Utilities

  attr_accessor :instructor_list,
              :admin_org_list,
              :term_any, :term_fall, :term_spring, :term_summer,
              :duration_type,
              :duration_count,
              :audit,
              :pass_fail_transcript_grade,
              :pilot_course,
              :end_term,
              :justification_of_fees,
              :defer_save

  def initialize(browser,opts={})
    @browser = browser
    defaults = {
        instructor_list: [(make CmInstructorObject), (make CmInstructorObject, :instructor_name => "SHUANG", :instructor_level => 2)],
        admin_org_list: [(make CmAdminOrgObject), (make CmAdminOrgObject, :admin_org_name => "Pop", :admin_org_level => 2)],
        term_any: :set,
        term_fall: :set,
        term_spring: :set,
        term_summer: :clear,
        duration_type: '::random::',
        duration_count:  (1..999).to_a.sample,
        audit: :set,
        pass_fail_transcript_grade: :set,
        pilot_course: :set,
        end_term: '::random::',
        justification_of_fees: random_alphanums(10, 'fee justification text '),
        defer_save: false
    }
    set_options(defaults.merge(opts))
  end

  def create
    on CmCourseInformation do |page|
      page.course_information unless page.current_page('Course Information').exists?

      if @instructor_list != nil
        @instructor_list.each do |instructor|
          instructor.create
        end
      end

    end
    determine_save_action
    
    on CmGovernance do |page|
      page.governance unless page.current_page('Governance').exists?

      if @admin_org_list != nil
        @admin_org_list.each do |admin_org|
          admin_org.create
        end
      end
    end
    determine_save_action

    on CmCourseLogistics do |page|
      page.course_logistics unless page.current_page('Course Logistics').exists?
      fill_out page, :term_any, :term_fall, :term_spring, :term_summer
      page.duration_count_type.pick! @duration_type
      page.duration_count_count.set @duration_count
      fill_out page, :audit, :pass_fail_transcript_grade
    end
    determine_save_action

    on CmActiveDates do |page|
      page.active_dates unless page.current_page('Active Dates').exists?
      fill_out page, :pilot_course
      page.loading_wait
      page.end_term.pick! @end_term
    end
    determine_save_action


    on CmCourseFinancials do |page|
      page.financials unless page.current_page('Financials').exists?
      fill_out page, :justification_of_fees
    end
    determine_save_action


    
  end


  def edit (opts={})
    on CmCourseLogistics do |page|
      page.course_logistics unless page.current_page('Course Logistics').exists?
      #fill_out page, opts, opts[:term_fall], opts[:term_spring], opts[:term_summer]
      page.term_any.fit opts[:term_any]
      page.term_fall.fit opts[:term_fall]
      page.term_sprint.fit opts[:term_spring]
      page.term_summer.fit opts[:term_summer]
      page.duration_type.pick! opts[:duration_type]
      page.duration_count.fit opts[:duration_count]
      #fill_out page, opts[:audit], opts[:pass_fail_transcript_grade]
      page.audit.fit opts[:audit]
      page.pass_fail_transcript_grade.fit opts[:pass_fail_transcript_grade]
    end

    on CmActiveDates do |page|
      page.active_dates unless page.current_page('Active Dates').exists?
      fill_out page, opts[:pilot_course]
      page.loading_wait
      page.end_term.pick! opts[:end_term]
    end

    on CmCourseFinancials do |page|
      page.financials unless page.current_page('Financials').exists?
      fill_out page, opts[:justification_of_fees]
    end

  set_options(opts)
  end


   def checkbox_trans
    { "Yes" => :set, "No" => :clear }
  end

end