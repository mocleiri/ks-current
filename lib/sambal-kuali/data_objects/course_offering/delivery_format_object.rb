class DeliveryFormatObject
  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  attr_accessor :format,
                :grade_format,
                :final_exam_activity,
                :parent_co

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :format => "random",
        :grade_format => "Course Offering",
        :final_exam_activity => "Lecture"
    }
    options = defaults.merge(opts)
    set_options(options)
  end

  def create
    on CourseOfferingCreateEdit do |page|
      if ! page.new_format_select.present?
        page.add_format
      end
    end

    if @format == "random" then
      set_random_delivery_formats
    else
      on CourseOfferingCreateEdit do |page|
        #ordering of compound format type (eg Lecture/Discussion) is flexible
        #if the selectlist doesn't include the option, then try reordering
        if !@format.index('/').nil?
          if !page.new_format_select.include?(@format)
            formats = @format.split('/')
            @format = "#{formats[1]}/#{formats[0]}"
          end
        end
        page.new_format_select.select(@format)
        page.loading.wait_while_present
        page.new_grade_roster_level_select.wait_until_present
        page.new_grade_roster_level_select.select(@grade_format)
        page.new_final_exam_activity_select.select(@final_exam_activity) if page.new_final_exam_activity_select.present?
      end
    end
    standardize_format_values
  end

  def set_random_delivery_formats
    on CourseOfferingCreateEdit do  |page|
      @format = page.select_random_option(page.new_format_select)
      page.new_grade_roster_level_select.wait_until_present
      @grade_format = page.select_random_option(page.new_grade_roster_level_select)
      @final_exam_activity = page.select_random_option(page.new_final_exam_activity_select) if page.new_final_exam_activity_select.present?
    end
    standardize_format_values
  end

  def standardize_format_values
    if @format == "Lab"
      @format = "Lab Only"
    elsif @format == "Lecture"
      @format = "Lecture Only"
    end
    if @grade_format == "Course"
      @grade_format = "Course Offering"
    else
    end
  end
  private  :standardize_format_values

  def edit opts={}
    defaults = {
        :defer_save => false,
        :start_edit => true
    }
    options = defaults.merge(opts)

    @parent_co.edit :defer_save => true if options[:start_edit]

    if options[:grade_format] != nil
      on(CourseOfferingCreateEdit).edit_grade_roster_level(@format,options[:grade_format])
    end

    if options[:final_exam_activity] != nil
      on(CourseOfferingCreateEdit).edit_final_exam_activity(@format,options[:final_exam_activity])
    end
    @parent_co.save
    set_options(options)
  end

end

class DeliveryFormatCollection < CollectionsFactory
  contains DeliveryFormatObject
end