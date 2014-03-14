Given /^I create a Course Offering from catalog using the default waitlists option \(enabled\)$/ do
  @course_offering = create CourseOffering, :course => "ENGL300", :waitlists => true

  @course_offering.view_course_details
  on CourseOfferingInquiry do |page|
    page.waitlist_state.should be_true
    page.close
  end
end

Given /^I create a Course Offering from catalog using the waitlists option disabled$/ do
  @course_offering = create CourseOffering, :course => "ENGL300", :waitlist => false
end

Given /^there is an existing course offering with activity offerings that have waitlists enabled$/ do
  @course_offering = make CourseOffering, :course => "ENGL293"
  @activity_offering = make ActivityOfferingObject, :parent_course_offering => @course_offering, :code => "A"
end

#Given /^I create a course and activity offering with waitlists enabled$/ do
#  step "I manage an activity offering with waitlists enabled"
#end

#I create a course and activity offering with waitlists enabled
#I manage an activity offering with waitlists enabled
Given /^I (?:manage|create)(?: a course)? and? activity offering with waitlists enabled$/ do
  if @calendar.nil?
    @calendar = make AcademicCalendar, :year => Rollover::MAIN_TEST_TERM_TARGET[0..3]
    term = make AcademicTermObject, :parent_calendar => @calendar, :term_code => Rollover::MAIN_TEST_TERM_TARGET
    @calendar.terms << term
  end
  @course_offering = create CourseOffering, :term => @calendar.terms[0].term_code, :course => "ENGL300", :waitlists => true
  @activity_offering = create ActivityOfferingObject, :parent_course_offering => @course_offering
end

Given /^I create a course and activity offering with waitlists disabled$/ do
  if @calendar.nil?
    @calendar = make AcademicCalendar, :year => Rollover::MAIN_TEST_TERM_TARGET[0..3]
    term = make AcademicTermObject, :parent_calendar => @calendar, :term_code => Rollover::MAIN_TEST_TERM_TARGET
    @calendar.terms << term
  end
  @course_offering_wl_disabled = create CourseOffering, :term => @calendar.terms[0].term_code, :course => "ENGL300", :waitlist => false
  @activity_offering_wl_disabled = create ActivityOfferingObject, :parent_course_offering => @course_offering_wl_disabled
end

Given /^I (?:manage|create) an activity offering with the limit waitlist size set$/ do
  @course_offering = create CourseOffering, :course => "ENGL300", :waitlists => true
  waitlist_config = make Waitlist, :enabled => true, :limit_size => 30
  @activity_offering = create ActivityOfferingObject, :parent_course_offering => @course_offering
  @activity_offering.edit :waitlist_config => waitlist_config

  on(ManageCourseOfferings).view_activity_offering(@activity_offering.code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_max_size.should == "Limit to 30"
    page.close
  end
end

Given /^I manage an activity offering with waitlists processing type set to (.*)$/ do |processing_type|
  @course_offering = create CourseOffering, :course => "ENGL300", :waitlists => true
  waitlist_config = make Waitlist, :enabled => true, :type => processing_type
  @activity_offering = create ActivityOfferingObject, :parent_course_offering => @course_offering
  @activity_offering.edit :waitlist_config => waitlist_config

  on(ManageCourseOfferings).view_activity_offering(@activity_offering.code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_processing.should == processing_type
    page.close
  end
end

Given /^I can update the processing type to (.*)$/ do |processing_type|
  waitlist = @activity_offering.waitlist_config
  waitlist.type = processing_type
  @activity_offering.edit :waitlist_config => waitlist

  on(ManageCourseOfferings).view_activity_offering(@activity_offering.code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_processing.should == processing_type
    page.close
  end
end

Then /^I make changes to the default waitlist configuration for one of the activity offerings$/ do
  @ao_list[0].parent_course_offering.manage
  waitlist = @ao_list[0].waitlist_config
  waitlist.enabled = true
  waitlist.type = "Manual"
  waitlist.limit_size = 10
  waitlist.allow_hold_list = true
  @ao_list[0].edit :waitlist_config => waitlist
end

Then /^I make changes to the default waitlist configuration for the activity offering$/ do
  waitlist = @activity_offering.waitlist_config
  waitlist.enabled = true
  waitlist.type = "Manual"
  waitlist.limit_size = 10
  waitlist.allow_hold_list = true
  @activity_offering.edit :waitlist_config => waitlist
end


Then /^I set the limit waitlist size$/ do
  waitlist = @activity_offering.waitlist_config
  waitlist.enabled = true
  waitlist.limit_size = 25
  @activity_offering.edit :waitlist_config => waitlist
end

Then /^I remove the limit waitlist size$/ do
  waitlist = @activity_offering.waitlist_config
  waitlist.limit_size = 0
  @activity_offering.edit :waitlist_config => waitlist
end

Then /^I modify the limit waitlist size$/ do
  waitlist = @activity_offering.waitlist_config
  waitlist.limit_size = 50
  @activity_offering.edit :waitlist_config => waitlist
end

Then /^I enable the allow hold list option$/ do
  waitlist = @activity_offering.waitlist_config
  waitlist.enabled = true
  waitlist.allow_hold_list = true
  @activity_offering.edit :waitlist_config => waitlist
end

Then /^I disable the allow hold list option$/ do
  waitlist = @activity_offering.waitlist_config
  waitlist.allow_hold_list = false
  @activity_offering.edit :waitlist_config => waitlist
end

Given /^I add two activity offerings$/ do
  @course_offering.manage
  @activity_offering = create ActivityOfferingObject, :parent_course_offering => @course_offering
  @activity_offering2 = create ActivityOfferingObject, :parent_course_offering => @course_offering
end

Given /^I add an activity offering$/ do
  @activity_offering = create ActivityOfferingObject, :parent_course_offering => @course_offering
end

Given /^the activity offerings have active waitlists and waitlists have the default configuration$/ do
  on(ManageCourseOfferings).view_activity_offering(@activity_offering.code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_active?.should be_true
    page.waitlists_allow_holds?.should be_false
    page.waitlists_processing.should == "Confirmation"
    page.waitlists_max_size.should == "Unlimited"
    page.close
  end

  on(ManageCourseOfferings).view_activity_offering(@activity_offering2.code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_active?.should be_true
    page.waitlists_allow_holds?.should be_false
    page.waitlists_processing.should == "Confirmation"
    page.waitlists_max_size.should == "Unlimited"
    page.close
  end

  @activity_offering.edit :defer_save => true
  on ActivityOfferingMaintenance do |page|
    page.waitlist_checkbox.set?.should be_true
    page.waitlist_confirmation_radio.set?.should be_true
    page.waitlist_limit_checkbox.set?.should be_false
    page.waitlist_allow_hold_checkbox.set?.should be_false
    page.cancel
  end

  @activity_offering2.edit :defer_save => true
  on ActivityOfferingMaintenance do |page|
    page.waitlist_checkbox.set?.should be_true
    page.waitlist_confirmation_radio.set?.should be_true
    page.waitlist_limit_checkbox.set?.should be_false
    page.waitlist_allow_hold_checkbox.set?.should be_false
    page.cancel
  end

end

Given /^the waitlist option cannot be enabled for the activity offering$/ do
  on(ManageCourseOfferings).view_activity_offering(@activity_offering.code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_active?.should be_false
    page.close
  end

  @activity_offering.edit :defer_save => true
  on ActivityOfferingMaintenance do |page|
    page.no_waitlists_msg.should == "Waitlists are deactivated for the Course Offering"
    page.cancel
  end
end

Given /^the limit waitlist size is successfully updated$/ do
  on(ManageCourseOfferings).view_activity_offering(@activity_offering.code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_active?.should be_true
    page.waitlists_max_size.should == @activity_offering.waitlist_config.waitlist_limit_str
    page.close
  end
end

Given /^the allow hold list option is successfully updated$/ do
  on(ManageCourseOfferings).view_activity_offering(@activity_offering.code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_active?.should be_true
    page.waitlists_allow_holds?.should == @activity_offering.waitlist_config.allow_hold_list
    page.close
  end
end

Given /^I re-enable the waitlists option for the activity offering the modified waitlist configuration is restored$/ do
  @activity_offering.edit :defer_save => true

  on ActivityOfferingMaintenance do |page|
    page.waitlist_checkbox.set
    page.waitlist_processing_type.should == @activity_offering.waitlist_config.type
    page.waitlist_limit_checkbox.set?.should == @activity_offering.waitlist_config.limit_size > 0
    page.waitlist_limit.value.to_i.should == @activity_offering.waitlist_config.limit_size
    page.waitlist_allow_hold_checkbox.set?.should == @activity_offering.waitlist_config.allow_hold_list
    page.cancel
  end
end

Given /^I (?:can )?disable the waitlists option for the activity offering$/ do
  waitlist = @activity_offering.waitlist_config
  waitlist.enabled = false
  @activity_offering.edit :waitlist_config => waitlist

  on(ManageCourseOfferings).view_activity_offering(@activity_offering.code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_active?.should be_false
    page.close
  end
end

Given /^the limit waitlist size is successfully updated to unlimited$/ do
  on(ManageCourseOfferings).view_activity_offering(@activity_offering.code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_max_size.should == "Unlimited"
    page.close
  end
end

Given /^I manage an activity offering with the waitlist allow hold list option enabled$/ do
  @course_offering = create CourseOffering, :course => "ENGL300", :waitlists => true
  waitlist_config = make Waitlist, :enabled => true, :allow_hold_list => true
  @activity_offering = create ActivityOfferingObject, :parent_course_offering => @course_offering
  @activity_offering.edit :waitlist_config => waitlist_config

  on(ManageCourseOfferings).view_activity_offering(@activity_offering.code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_allow_holds?.should == true
    page.close
  end
end


Given /^the waitlist enabled configuration is copied to the new course and activity offering in the target term$/ do
  @course_offering_copy = make CourseOffering, :course => @course_offering.course, :term => @calendar_target.terms[0].term_code
  step "the waitlist configuration is copied to the new course and activity offering"
end

Given /^the waitlist configuration is copied to the new course and activity offering.*$/ do
  @course_offering_copy.view_course_details
  on CourseOfferingInquiry do |page|
    page.waitlist_state.should be_true
    page.close
  end

  @course_offering_copy.manage
  on(ManageCourseOfferings).view_activity_offering(@activity_offering.code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_active?.should be_true
    page.waitlists_allow_holds?.should == @activity_offering.waitlist_config.allow_hold_list
    page.waitlists_processing.should == @activity_offering.waitlist_config.type
    page.waitlists_max_size.should == @activity_offering.waitlist_config.waitlist_limit_str
    page.close
  end
end

Given /^the waitlist disabled configuration is copied to the course and activity offering in the target term$/ do
  @course_offering_copy = make CourseOffering, :course => @course_offering_wl_disabled.course, :term => @calendar_target.terms[0].term_code
  @course_offering_copy.manage
 step "the waitlists are disabled for the new course and activity offering"
end

Given /^the waitlists are disabled for the new course and activity offering$/ do
  @course_offering_copy.view_course_details
  on CourseOfferingInquiry do |page|
    page.waitlist_state.should be_false
    page.close
  end

  @course_offering_copy.manage
  on(ManageCourseOfferings).view_activity_offering(@activity_offering.code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_active?.should be_false
    page.close
  end
end

Given /^there are two other activity offering with waitlists enabled and no waitlist limit$/ do
  @ao_list = []
  ["ENGL416","ENGL420"].each do |co_code|
    course_offering = create CourseOffering, :course => co_code, :waitlists => true
    activity_offering = create ActivityOfferingObject, :parent_course_offering => course_offering
    @ao_list << activity_offering
  end
end

When /^I colocate the activity offering with other two offerings and select shared maximum enrolment$/ do
  @activity_offering.parent_course_offering.manage
  @activity_offering.edit :colocated => true,
                          :colocate_ao_list => @ao_list,
                          :colocate_shared_enrollment => true,
                          :max_enrollment => 48

  @ao_list.unshift(@activity_offering)
end

Then /^all three activity offerings have the same waitlist limit size$/ do
  @ao_list.each do |ao|
    ao.parent_course_offering.manage
    on(ManageCourseOfferings).view_activity_offering(ao.code)

    on ActivityOfferingInquiry do |page|
      page.waitlists_active?.should be_true
      page.waitlists_max_size.should == @ao_list[0].waitlist_config.waitlist_limit_str
      page.close
    end
  end
end

Given /^I create three course offerings with one activity offering in each with waitlists enabled$/ do
  @term = make AcademicTermObject, :parent_calendar => (make AcademicCalendar), :term_code => Rollover::MAIN_TEST_TERM_TARGET if @term.nil?

  @ao_list = []

  ["ENGL300","WMST300","WMST400"].each do |co_code|
    course_offering = create CourseOffering, :course => co_code, :waitlists => true, :term => @term.term_code
    activity_offering = create ActivityOfferingObject, :parent_course_offering => course_offering
    @ao_list << activity_offering
  end
end

Given /^I colocate the three activity offerings \(shared enrolment\)$/ do
  @ao_list[0].parent_course_offering.manage
  @ao_list[0].edit :colocated => true,
                   :colocate_ao_list => @ao_list[1..2],
                   :colocate_shared_enrollment => true,
                   :max_enrollment => 120
end

Then /activity offerings have the same waitlist configuration$/ do
  @ao_list.each do |ao|
    ao.parent_course_offering.manage
    on(ManageCourseOfferings).view_activity_offering(ao.code)

    on ActivityOfferingInquiry do |page|
      page.waitlists_active?.should be_true
      page.waitlists_allow_holds?.should == @ao_list[0].waitlist_config.allow_hold_list
      page.waitlists_processing.should == @ao_list[0].waitlist_config.type
      page.waitlists_max_size.should == @ao_list[0].waitlist_config.waitlist_limit_str
      page.close
    end
  end
end

Then /^the waitlist configuration is copied to the(?: new)? colocated activity offering/ do
  course_offering_target = make CourseOffering, :course => @ao_list[0].parent_course_offering.course,
                                :term => @term.term_code
  @activity_offering_copy = make ActivityOfferingObject, :code => @ao_list[0].code, :parent_course_offering => course_offering_target

  @activity_offering_copy.parent_course_offering.manage
  on(ManageCourseOfferings).view_activity_offering(@activity_offering_copy.code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_active?.should be_true
    page.waitlists_allow_holds?.should == @ao_list[0].waitlist_config.allow_hold_list
    page.waitlists_processing.should == @ao_list[0].waitlist_config.type
    page.waitlists_max_size.should == @ao_list[0].waitlist_config.waitlist_limit_str
    page.close
  end

end

Given /^there is an existing course offering with a colocated activity offering \(shared enrolment\) with waitlists enabled$/ do
  @course_offering = make CourseOffering, :term => "201208", :course => "ENGL362"
  @course_offering.manage

  @ao_list = []
  @ao_list << (make ActivityOfferingObject, :code => "A", :parent_course_offering => @course_offering)
  colocated_ao_parent = make CourseOffering, :course => "HIST310", :term => "201208"
  @ao_list << (make ActivityOfferingObject, :code => "A", :parent_course_offering => colocated_ao_parent, :colocated => true)
  @ao_list[0].colocate_ao_list << @ao_list[1]
  on(ManageCourseOfferings).has_colo_icon(@ao_list[0].code).should be_true
end

Given /^the waitlist configuration is copied to the new activity offering.*$/ do
  @course_offering_copy.manage
  on(ManageCourseOfferings).view_activity_offering(@ao_list[0].code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_active?.should be_true
    page.waitlists_allow_holds?.should == @ao_list[0].waitlist_config.allow_hold_list
    page.waitlists_processing.should == @ao_list[0].waitlist_config.type
    page.waitlists_max_size.should == @ao_list[0].waitlist_config.waitlist_limit_str
    page.close
  end
end

When /^I delete one of the colocated activity offerings$/ do
  @ao_list[0].parent_course_offering.manage
  @ao_list[0].parent_course_offering.delete_ao :ao_code =>'A'
end

Then /^the remaining two activity offerings still have the same waitlist configuration$/ do
  @ao_list[1..2].each do |ao|
    ao.parent_course_offering.manage
    on(ManageCourseOfferings).view_activity_offering(ao.code)

    on ActivityOfferingInquiry do |page|
      page.waitlists_active?.should be_true
      page.waitlists_allow_holds?.should == @ao_list[0].waitlist_config.allow_hold_list
      page.waitlists_processing.should == @ao_list[0].waitlist_config.type
      page.waitlists_max_size.should == @ao_list[0].waitlist_config.waitlist_limit_str
      page.close
    end
  end
end

When /^I delete one of the related course offerings$/ do
  @ao_list[0].parent_course_offering.manage
  @ao_list[0].parent_course_offering.delete_co_coc_view
end

Then /^the (?:remaining )?activity offerings.*are still colocated$/ do
  @ao_list[1..2].each do |ao|
    ao.parent_course_offering.manage
    on(ManageCourseOfferings).has_colo_icon(ao.code).should be_true
  end
end

When /^I delete two of the related course offerings$/ do
  @ao_list[0..1].each do |ao|
    ao.parent_course_offering.manage
    ao.parent_course_offering.delete_co_coc_view
  end
end

Then /^the remaining activity offering is no longer colocated$/ do
  @ao_list[-1].parent_course_offering.manage
  on(ManageCourseOfferings).has_colo_icon(@ao_list[-1].code).should be_false
end

Then /^the remaining activity offering still has the same waitlist configuration$/ do
  @ao_list[2].parent_course_offering.manage
  on(ManageCourseOfferings).view_activity_offering(@ao_list[2].code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_active?.should be_true
    page.waitlists_allow_holds?.should == @ao_list[0].waitlist_config.allow_hold_list
    page.waitlists_processing.should == @ao_list[0].waitlist_config.type
    page.waitlists_max_size.should == @ao_list[0].waitlist_config.waitlist_limit_str
    page.close
  end
end

When /^I deactivate waitlists on the first activity offering$/ do
  @ao_list[0].parent_course_offering.manage
  waitlist = @ao_list[0].waitlist_config
  waitlist.enabled = false
  @ao_list[0].edit :waitlist_config => waitlist
end

Then /^the waitlist configuration for the.*activity offerings? is not changed$/ do
  @ao_list[1..-1].each do |ao|
    ao.parent_course_offering.manage
    on(ManageCourseOfferings).view_activity_offering(ao.code)

    on ActivityOfferingInquiry do |page|
      page.waitlists_active?.should be_true
      #page.waitlists_allow_holds?.should == @ao_list[0].waitlist_config.allow_hold_list
      #page.waitlists_processing.should == @ao_list[0].waitlist_config.type
      #page.waitlists_max_size.should == @ao_list[0].waitlist_config.waitlist_limit_str
      page.close
    end
  end
end

Given /^I create two colocated activity offerings \(shared enrolment\) with waitlists enabled$/ do
  #TODO: # of AOs can be parameterized
  @term = make AcademicTermObject, :parent_calendar => (make AcademicCalendar), :term_code => Rollover::MAIN_TEST_TERM_TARGET if @term.nil?

  @ao_list = []

  ["ENGL300","WMST300"].each do |co_code|
    course_offering = create CourseOffering, :course => co_code, :waitlists => true, :term => @term.term_code
    activity_offering = create ActivityOfferingObject, :parent_course_offering => course_offering
    @ao_list << activity_offering
  end

  @ao_list[0].parent_course_offering.manage
  @ao_list[0].edit :colocated => true,
                   :colocate_ao_list => @ao_list[1..1],
                   :colocate_shared_enrollment => true,
                   :max_enrollment => 120
end

When /^I deactivate waitlists at the course offering level for one of the activity offerings$/ do
  @ao_list[0].parent_course_offering.search_by_subjectcode
  on(ManageCourseOfferingList).edit @ao_list[0].parent_course_offering.course
  @ao_list[0].parent_course_offering.edit_offering :waitlist => false, :edit_in_progress => true
  on(CourseOfferingCreateEdit).submit
end

Then /^waitlists is deactived for both activity offerings$/ do
  @ao_list.each do |ao|
    ao.parent_course_offering.manage
    on(ManageCourseOfferings).view_activity_offering(ao.code)

    on ActivityOfferingInquiry do |page|
      page.waitlists_active?.should be_false
      page.close
    end
  end
end

When /^I add another activity offering to the colocated set$/ do
    course_offering = create CourseOffering, :course => "WMST400", :waitlists => true, :term => @term.term_code
    activity_offering = create ActivityOfferingObject, :parent_course_offering => course_offering
    @ao_list << activity_offering

    @ao_list[0].parent_course_offering.manage
    @ao_list[0].edit :colocated => true,
                     :colocate_ao_list => [activity_offering]
end

When /^I update the colocation settings to manage enrolments separately$/ do
  @ao_list[0].parent_course_offering.manage
  @ao_list[0].edit :colocate_shared_enrollment => false
end

When /^the other activity offering still has the default configuration$/ do
  @ao_list[-1].parent_course_offering.manage
  on(ManageCourseOfferings).view_activity_offering(@ao_list[-1].code)

  on ActivityOfferingInquiry do |page|
    page.waitlists_active?.should be_true
    page.waitlists_allow_holds?.should be_false
    page.waitlists_processing.should == "Confirmation"
    page.waitlists_max_size.should == "Unlimited"
    page.close
  end
end
