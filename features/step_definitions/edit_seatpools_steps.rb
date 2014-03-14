When /^I change the seat pool count and expiration milestone$/ do
  @activity_offering.seat_pool_list[0].edit :edit_already_started => true,
                                            :seats => 20,
                                            :expiration_milestone => "Last Day of Registration"
end

Then /^the seats remaining is updated$/ do
  on ActivityOfferingMaintenance do |page|
    page.seats_remaining_span.wait_until_present
    page.seat_count_remaining.should == @activity_offering.seats_remaining.to_s
  end
end

When /^I edit an existing activity offering with (\d+) seat pools?$/ do |number|
  pool_list = []
  ctr = 0
  #create required number of seatpools
  while ctr < number.to_i do
    ctr = ctr + 1
    seatpool = make SeatPoolObject, :priority => (ctr), :priority_after_reseq => (ctr)
    pool_list << seatpool
  end

  @course_offering = create CourseOffering, :create_by_copy=>(make CourseOffering)
  @course_offering.manage
  @activity_offering = create ActivityOfferingObject, :parent_course_offering => @course_offering
  @activity_offering.edit :max_enrollment => 100, :defer_save => true

  @activity_offering.add_seat_pool_list :seat_pool_list => pool_list,
                                   :edit_already_started => true,
                                   :defer_save => false

  @activity_offering.parent_course_offering.manage
  @activity_offering.edit :defer_save => true
end

When /^I switch the priorities for 2 seat pools$/ do
  @activity_offering.seat_pool_list[0].edit :priority => 2, :priority_after_reseq => 2, :edit_already_started => true, :defer_save => true
  @activity_offering.seat_pool_list[1].edit :priority => 1, :priority_after_reseq => 1, :edit_already_started => true, :defer_save => true
end


And /^I increase the overall max enrollment$/ do
  @activity_offering.edit :max_enrollment => @activity_offering.max_enrollment.to_i + 20, :edit_already_started => true, :defer_save => true
end

And /^the updated seat pool priorities are saved$/ do
  #checked in the 'the activity offering is updated when saved' step
end

And /^the seat pool is not saved with the activity offering$/ do
  #checked in the 'the activity offering is updated when saved' step
end

#checks the read only page after submit, and then reopens in edit mode to check persistence
Then /^the activity offering is updated when saved$/ do

  #seat_pool priorities are resequenced when you go back into to edit AO
  @activity_offering.resequence_expected_seatpool_priorities()
  @activity_offering.save
  #reopens activity offering in edit mode to recheck everything persisted
  @activity_offering.parent_course_offering.manage
  on ManageCourseOfferings do |page|
    page.edit @activity_offering.code
  end

  on ActivityOfferingMaintenance do |page|
    page.total_maximum_enrollment.value.should == @activity_offering.max_enrollment.to_s

    if  @activity_offering.actual_scheduling_information_list.length != 0
      page.actual_sched_info_table.rows[1..-1].each do |row|
        row_key = "#{page.get_actual_sched_info_days(row)}#{page.get_actual_sched_info_start_time(row)}".delete(' ')
        asi = @activity_offering.actual_scheduling_information_list[row_key]
        if row_key != ''
          if asi.tba?
            page.get_actual_sched_info_tba(row).should == "TBA"
          else
            page.get_actual_sched_info_tba(row).should == ""
          end
          page.get_actual_sched_info_days(row).delete(' ').should == asi.days
          page.get_actual_sched_info_start_time(row).should == "#{asi.start_time} #{asi.start_time_ampm.upcase}"
          page.get_actual_sched_info_end_time(row).should == "#{asi.end_time} #{asi.end_time_ampm.upcase}"
          page.get_actual_sched_info_facility(row).should == asi.facility_long_name
          page.get_actual_sched_info_room(row).should == asi.room
          #TODO - validate (facility) features when implemented
        end
      end
    end

    page.seat_pool_count.should == @activity_offering.seat_pool_list.count.to_s
    page.course_url.value.should == @activity_offering.course_url

    @activity_offering.personnel_list.each do |p|
      page.person_affiliation(p.id).should == p.affiliation.to_s
      page.person_inst_effort(p.id).should == p.inst_effort.to_s
    end

    @activity_offering.seat_pool_list.each do |seatpool|
      page.get_seats(seatpool.population_name).should == seatpool.seats.to_s
      page.get_expiration_milestone(seatpool.population_name).should == seatpool.expiration_milestone
      page.pool_percentage(seatpool.population_name).should == seatpool.percent_of_total(@activity_offering.max_enrollment)
      page.get_priority(seatpool.population_name).should == seatpool.priority.to_s
    end

    page.requires_evaluation.set?.should == @activity_offering.requires_evaluation
  end
end

Given /^I am managing a course offering$/ do
  @course_offering = make CourseOffering, :term => Rollover::MAIN_TEST_TERM_TARGET
  @course_offering.manage
end
