# Request by or on behalf of a student to register for a course offering by registration group
#
#
# Note the use of the ruby options hash pattern re: setting attribute values
class RegistrationRequest

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  #string - generally set using options hash
  attr_accessor :student_id,
                :term_code,
                :term_descr,              #TODO - get term descr from term_code so they are always in sync
                :course_code,
                :reg_group_code
  #array - generally set using options hash
  attr_accessor :course_options
  #boolean - - generally set using options hash true/false
  attr_accessor :modify_course_options

  # provides default data:
  #  defaults = {
  #    :student_id=>"student",
  #    :term_code=>"201201",
  #    :term_descr=>"Spring 2012",
  #    :course_code=>"CHEM231",
  #    :reg_group_code=>"1001",
  #    :modify_course_options=> false   This refers only to modifying during add to cart operation
  #  }
  # initialize is generally called using TestFactory Foundry .make or .create methods
  
  def initialize(browser, opts={})
    @browser = browser

    defaults = {
      :student_id=>"student",
      :term_code=>"201201",
      :term_descr=>"Spring 2012",
      :course_code=>"CHEM231",
      :reg_group_code=>"1001",
      :course_options=> (make CourseOptions),
      :modify_course_options=> false
    }
    options = defaults.merge(opts)
    update_options(options)
  end

  def create
    visit RegistrationCart do |page|
      # wait in case list has not loaded yet
      page.wait_until {page.term_select.include? @term_descr }
      page.select_term @term_descr
      page.course_code_input.set @course_code
      page.reg_group_code_input.set @reg_group_code
      page.add_to_cart
      page.new_item_cancel_button.wait_until_present
      if @modify_course_options
        edit_course_options_on_new_item
      end
      page.save_new_item
    end
  end

  def edit opts={}
    options = defaults.merge(opts)
    edit_student_id options
    edit_term_code options
    edit_term_descr options
    edit_course_code options
    edit_reg_group options
  end

  def edit_student_id opts={}
    if opts[:student_id].nil?
      return nil
    end
  end
  private :edit_student_id

  def edit_term_code opts={}
    if opts[:term_code].nil?
      return nil
    end
  end
  private :edit_term_code

  def edit_term_descr opts={}
    if opts[:term_descr].nil?
      return nil
    end
  end
  private :edit_term_descr

  def edit_course_code opts={}
    if opts[:term_code].nil?
      return nil
    end
  end
  private :edit_course_code

  def edit_reg_group opts={}
    if opts[:reg_group_code].nil?
      return nil
    end
  end
  private :edit_reg_group

  def remove_from_cart
    on RegistrationCart do |page|
      page.course_code(@course_code,@reg_group_code).wait_until_present
      page.toggle_course_details @course_code,@reg_group_code
      page.remove_course_from_cart @course_code,@reg_group_code
    end
  end

  def edit_course_options_on_new_item
    if @course_options.nil?
      return nil
    end
    on RegistrationCart do |page|
      page.new_item_credits_selection.wait_until_present
      page.select_credits_on_new_item @course_options.credit_option
      page.select_grading_on_new_item @course_options.grading_option
    end
  end
  private :edit_course_options_on_new_item

  def edit_course_options_in_cart
    if @course_options.nil?
      return nil
    end
    on RegistrationCart do |page|
      page.course_code(@course_code,@reg_group_code).wait_until_present
      page.toggle_course_details @course_code,@reg_group_code
      page.edit_course_options @course_code,@reg_group_code
      page.select_credits_in_cart @course_code,@reg_group_code,@course_options.credit_option
      page.select_grading_in_cart @course_code,@reg_group_code,@course_options.grading_option
      page.save_edits @course_code,@reg_group_code
    end
  end
  #private :edit_course_options_in_cart

end

  class CourseOptions

    include Foundry
    include DataFactory
    include DateFactory
    include StringFactory
    include Workflows

    attr_accessor :credit_option,
                  :grading_option

    def initialize(browser, opts={})
      @browser = browser

      defaults = {
          :credit_option => "3.0",
          :grading_option => "Letter"
      }
      options = defaults.merge(opts)
      set_options(options)
    end

  end
