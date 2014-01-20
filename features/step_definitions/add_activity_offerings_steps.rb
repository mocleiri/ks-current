When /^I add an Activity Offering to an existing Course Offering$/ do
  course_offering = make CourseOffering, :course=>"CHEM276"
  #course_offering.manage_and_init
  @new_ao = course_offering.create_ao :ao_obj => (make ActivityOffering, :format => "Lecture/Discussion")
end


Then /^the new Activity Offering is shown in the list of AOs$/ do
  on(ManageCourseOfferings).codes_list.should include(@new_ao.code)
end


When /^I copy an AO with Actual Scheduling Information$/ do

  # in ref-data this CO has ASIs but no RSIs (copy an ASI first if you need an RSI)
  course_offering = make CourseOffering, :course => "CHEM276"
  course_offering.manage_and_init

  @ao_source = course_offering.get_ao_obj_by_code("A")
  @ao_copy = create ActivityOffering, :create_by_copy => true,
                    :code => @ao_source.code,
                    :parent_course_offering => course_offering
end

When /^I copy an AO with Requested Scheduling Information$/ do

  # in ref-data this CO has ASIs but no RSIs (copy an ASI first if you need an RSI)
  course_offering = make CourseOffering, :course => "CHEM276"
  course_offering.manage_and_init

  # use the AO we copied in the previous step as the source for a new copy
  # this AO should have RSIs but no ASIs
  @ao_source = @ao_copy
  @ao_copy = create ActivityOffering, :create_by_copy => true,
                    :code => @ao_source.code,
                    :parent_course_offering => course_offering
end

Then /^the "(ASI|RSI)s" are successfully copied as RSIs in the new AO$/ do |source_scheduling_information_type|

  course_offering = @ao_source.parent_course_offering
  course_offering.manage_and_init
  source_scheduling_information = nil
  if source_scheduling_information_type == "ASI"
    source_scheduling_information = course_offering.get_ao_obj_by_code(@ao_source.code).actual_scheduling_information_list.values[0]
  else
    source_scheduling_information = course_offering.get_ao_obj_by_code(@ao_source.code).requested_scheduling_information_list.values[0]
  end
  source_scheduling_information.nil?.should be_false

  #@ao_copy.parent_course_offering.manage
  @ao_copy.edit

  on ActivityOfferingMaintenance do |page|
    page.view_requested_scheduling_information
    page.requested_sched_info_table.rows.size.should be > 2 #should be more than header/footer rows
    row = page.requested_sched_info_table.rows[1]
    page.get_requested_sched_info_days(row).delete(' ').should == source_scheduling_information.days
    page.get_requested_sched_info_start_time(row).delete(' ').should == "#{source_scheduling_information.start_time}#{source_scheduling_information.start_time_ampm}"
    page.get_requested_sched_info_end_time(row).delete(' ').should == "#{source_scheduling_information.end_time}#{source_scheduling_information.end_time_ampm}"
    page.get_requested_sched_info_facility(row).should == source_scheduling_information.facility_long_name
    page.get_requested_sched_info_room(row).should == source_scheduling_information.room
  end
end


### CREATE DUMMY DATA
### This data should already exist in the DB, having been put in there manually
#When /^I create some dummy test data to speed up AFT development for activity offerings$/ do
#  course_offering = make CourseOffering, :course => "CHEM276"
#  course_offering.manage_and_init

#  @ao_source = course_offering.activity_offering_cluster_list[0].get_ao_obj_by_code("D")
#  @ao_copy = course_offering.activity_offering_cluster_list[0].get_ao_obj_by_code("E")
#end





