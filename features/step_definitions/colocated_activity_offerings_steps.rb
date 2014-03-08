When /^there is a course offering with a colocated activity offering$/ do
  @course_offering = make CourseOffering, :term => "201208", :course => "CHEM441"
  @course_offering.manage

  @ao_list = []
  @ao_list << (make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering)
  colocated_ao_parent = make CourseOffering, :course => "CHEM641", :term => "201208"
  @ao_list << (make ActivityOfferingObject, :code => "A", :parent_course_offering => colocated_ao_parent)
  @ao_list[0].colocate_ao_list << @ao_list[1]
  on(ManageCourseOfferings).has_colo_icon(@ao_list[0].code).should be_true
end

When /^I create "([2-9])" COs with an AO in each$/ do |number_of_cos_to_create|
  @ao_list = []

  for i in 1..number_of_cos_to_create.to_i
    co = create CourseOffering, :create_by_copy => (make CourseOffering, :course => "ENGL255", :term => Rollover::MAIN_TEST_TERM_TARGET)
    ao = make ActivityOfferingObject, :code => "A", :parent_course_offering => co
    @ao_list << ao
  end
end

When /^I create three Course Offerings with colocated AOs in the new term$/ do
  @ao_list = []

  for i in 1..3
    delivery_format_list = []
    delivery_format_list << (make DeliveryFormat, :format => "Lecture", :grade_format => "Course Offering", :final_exam_driver => "Lecture", :final_exam_activity => "Lecture")
    co = create CourseOffering, :course => "ENGL211", :term => @calendar.terms[0].term_code, :delivery_format_list => delivery_format_list

    ao = create ActivityOfferingObject, :parent_course_offering => co, :format => "Lecture Only", :activity_type => "Lecture"
    ao.save
    @ao_list << ao
  end

  # colocate the first AO to all the others
  @ao_list[0].parent_course_offering.manage
  @ao_list[0].edit :colocated => true,
                  :colocate_ao_list => @ao_list[1, @ao_list.length],
                  :colocate_shared_enrollment => true,
                  :max_enrollment => 48
  @ao_list[0].save
end

When /^I colocate multiple activities, selecting to "(share|separately manage)" enrollments$/ do |max_enrollment_flag|

  should_enrollment_be_shared_flag = true
  if max_enrollment_flag == 'separately manage'
    should_enrollment_be_shared_flag = false
  end

  # colocate the first AO to all the others
  @ao_list[0].parent_course_offering.manage
  @ao_list[0].edit :colocated => true,
                   :colocate_ao_list => @ao_list[1, @ao_list.length],
                   :colocate_shared_enrollment => should_enrollment_be_shared_flag,
                   :max_enrollment => 48
  @ao_list[0].save
end

Then /^the activities indicate they are colocated$/ do

  @ao_list[0].parent_course_offering.manage
  on ManageCourseOfferings do |page|
    colocated_tooltip_text = page.target_row('A')[1].image(src: /colocate_icon/).alt.upcase

    # validate tooltip text contains each colo
    @ao_list[1, @ao_list.length].each do |other_ao|
      expected = other_ao.parent_course_offering.course.upcase + ' ' + other_ao.code.upcase
      colocated_tooltip_text.should include expected
    end
  end

end

When /^I break colocation on the first colocated AO$/ do
  @ao_list[0].parent_course_offering.manage
  @ao_list[0].edit :colocated => false
  @ao_list[0].save
end

Then /^the first colocated AO is not colocated with any remaining AOs$/ do

  # first AO should no indicate colocation
  @ao_list[0].parent_course_offering.manage
  on ManageCourseOfferings do |page|
    page.target_row('A')[1].image.should_not be_present

    # validate should also include ensuring there are no SIs
  end

  # second AO should indicate colocation with all the remaining
  @ao_list[1].parent_course_offering.manage
  on ManageCourseOfferings do |page|
    colocated_tooltip_text = page.target_row('A')[1].image(src: /colocate_icon/).alt.upcase

    # validate tooltip text contains each colo
    @ao_list[2, @ao_list.length].each do |other_ao|
      expected = other_ao.parent_course_offering.course.upcase + ' ' + other_ao.code.upcase
      colocated_tooltip_text.should include expected

      #validation should also include ensuring SIs still exist
    end
  end
end

When /^I designate a valid term and Course Offering Code with a fully colocated AO$/ do
  co = create CourseOffering, :create_by_copy => (make CourseOffering, :course => "CHEM441", :term => "201208")
  @activity_offering = make ActivityOfferingObject, :code => "A", :parent_course_offering => co
end

And /^I delete the fully colocated AO$/ do
  @activity_offering.parent_course_offering.manage
  on ManageCourseOfferings do |page|
    @activity_offering.parent_course_offering.delete_ao_list :code_list =>  page.codes_list
  end
end

Then /^The AO is successfully deleted$/ do
  @activity_offering.parent_course_offering.manage
  begin
    on(ManageCourseOfferings).codes_list # this line is broken
  rescue RuntimeError => e
    e.message.should include "error in activity_offering_results_table - no AOs"
  end
end

Then /^the activity offering in the course offering copy is added to the colocated set$/ do
  @course_offering_copy.manage

  on ManageCourseOfferings do |page|
    page.has_colo_icon(@ao_list[0].code).should be_true
    colocated_tooltip_text = page.target_row(@ao_list[0].code)[1].image(src: /colocate_icon/).alt.upcase

    @ao_list.each do |colo_ao|
      expected = colo_ao.parent_course_offering.course.upcase + ' ' + colo_ao.code.upcase
      colocated_tooltip_text.should include expected
    end
  end
end

Then /^the activity offering in the course offering copy is not colocated$/ do
  @course_offering_copy.manage
  on(ManageCourseOfferings).has_colo_icon(@ao_list[0].code).should be_false
end

When /^I copy one of the colocated activity offerings$/ do
  @activity_offering_copy = create ActivityOfferingObject, :create_by_copy => true,
                                   :code => @ao_list[0].code,
                                   :parent_course_offering => @ao_list[0].parent_course_offering
end

Then /^the activity offering copy is added to the colocated set$/ do
  @activity_offering_copy.parent_course_offering.manage
  on ManageCourseOfferings do |page|
    page.has_colo_icon(@activity_offering_copy.code).should be_true

    colocated_tooltip_text = page.target_row(@activity_offering_copy.code)[1].image(src: /colocate_icon/).alt.upcase

    # validate tooltip text contains each colo
    @ao_list.each do |other_ao|
      expected = other_ao.parent_course_offering.course.upcase + ' ' + other_ao.code.upcase
      colocated_tooltip_text.should include expected

    end
  end
end


### CREATE DUMMY DATA
### This data should already exist in the DB, with an AO 'A' in each CO,
### and with the first CO's AO colocated to the other 2
When /^I create some dummy test data to speed up AFT development$/ do

  # populate an array with a bunch of COs that already exist in the DB
  @ao_list = []

  # 201301 ENGL211G AO:A
  co = make CourseOffering, :term => '201301', :course => 'ENGL211Q'
  ao = make ActivityOfferingObject, :code => "A", :parent_course_offering => co
  @ao_list << ao

  # 201301 ENGL211H AO:A
  co = make CourseOffering, :term => '201301', :course => 'ENGL211R'
  ao = make ActivityOfferingObject, :code => "A", :parent_course_offering => co
  @ao_list << ao

  # 201301 ENGL211I AO:A
  co = make CourseOffering, :term => '201301', :course => 'ENGL211S'
  ao = make ActivityOfferingObject, :code => "A", :parent_course_offering => co
  @ao_list << ao

end

Then /^the Activity Offerings are colocated in the rollover target$/ do
  @ao_list_rollover = []

  for i in 0..2
    co = make CourseOffering, :course =>  @ao_list[i].parent_course_offering.course,
              :term => @calendar_target.terms[0].term_code
    ao = make ActivityOfferingObject, :code => "A", :parent_course_offering => co
    @ao_list_rollover << ao
  end

  @ao_list_rollover[0].parent_course_offering.manage
  on ManageCourseOfferings do |page|
    colocated_tooltip_text = page.target_row('A')[1].image(src: /colocate_icon/).alt.upcase

    # validate tooltip text contains each colo
    @ao_list_rollover[1, @ao_list_rollover.length].each do |other_ao|
      expected = other_ao.parent_course_offering.course.upcase + ' ' + other_ao.code.upcase
      colocated_tooltip_text.should include expected
    end
  end

end



