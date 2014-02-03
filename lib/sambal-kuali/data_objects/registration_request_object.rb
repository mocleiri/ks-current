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

  #generally set using options hash
  attr_accessor :student_id,
                :term_code,
                :term_descr,              #TODO - get term descr from term_code so they are always in sync
                :course_code,             # code or course_offering_object?
                :reg_group,
                :course_options_list,
                :modify_course_options

  # provides default data:
  #  defaults = {
  #    :student_id=>"student",
  #    :term_code=>"201301",
  #    :term_descr=>"Spring 2013",
  #    :course_code=>"CHEM231",
  #    :reg_group=>"1001",
  #    :course_options_list=> [],
  #    :modify_course_options=> false
  #  }
  # initialize is generally called using TestFactory Foundry .make or method
  
  def initialize(browser, opts={})
    @browser = browser

    defaults = {
      :student_id=>"student",
      :term_code=>"201301",
      :term_descr=>"Spring 2013",
      :course_code=>"CHEM231",
      :reg_group=>"1001",
      :course_options_list=> [ (make CourseOptions ) ],
      :modify_course_options=> "false"
    }
    options = defaults.merge(opts)
    update_options(options)
  end

  def create
    return new RegistrationRequest
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
    if opts[:reg_group].nil?
      return nil
    end
  end
  private :edit_reg_group

  class CourseOptions
    #to be added
  end
end
