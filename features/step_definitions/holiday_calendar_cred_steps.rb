When /^I create a Holiday Calendar$/ do
  @holiday_calendar = create HolidayCalendar, :year => "2014"
end

When /^I edit a Holiday Calendar$/ do
  @holiday_calendar = make HolidayCalendar, :name=>"2012-2013 Holiday Calendar", :year => "2014"
end

Then /^The Make Official button for the Holiday Calendar should become active$/ do
  @holiday_calendar.edit
  on CreateEditHolidayCalendar do |page|
    page.make_official_link.present?.should be_true
  end
end

Then /^the holiday calendar is set to Official$/ do
  @holiday_calendar.view :perform_search => false
  on ViewHolidayCalendar do |page|
    #page.open_hcal_info_section
    page.hcal_status.upcase.should == "OFFICIAL"
  end
end

When /^I search for the Holiday Calendar$/ do
  @holiday_calendar.search
end

When /^I delete the Holiday Calendar draft$/ do
  @holiday_calendar.delete
end

When /^I add holidays to the Holiday Calendar$/ do
  @holiday_calendar.add_holiday :holiday => ( make Holiday, :type => "Christmas observed", :start_date=>"12/26/2012" )
  @holiday_calendar.add_holiday :holiday => ( make Holiday, :type => "Election Day", :start_date=>"11/26/2012" )
end

When /^I update holiday dates$/ do
  @holiday_calendar.edit
  @holiday_calendar.edit_holiday "Veterans Day", {:start_date => "11/15/#{@holiday_calendar.year}" }
end

When /^I remove holidays from the Holiday Calendar$/ do
  holiday_type_list = []
  holiday_type_list << "Labor Day"
  holiday_type_list << "New Year's Day"
  @holiday_calendar.delete_holidays :holiday_type_list =>  holiday_type_list
end

When /^I update the name and date range for Holiday Calendar$/ do
  @holiday_calendar.edit :name => random_alphanums.strip,
                         :start_date => "02/15/#{@holiday_calendar.year.to_i + 1}",
                         :end_date => "07/06/#{@holiday_calendar.year.to_i + 1}"
end

When /^I make the Holiday Calendar official$/ do
  @holiday_calendar.edit
  @holiday_calendar.make_official
end

When /^all holidays are copied successfully$/ do
  @holiday_calendar.view :perform_search => false
  on ViewHolidayCalendar do |page|
    @holiday_calendar.holiday_list.each do |holiday|
      page.holiday_start_date(holiday.type).should == holiday.start_date
      page.holiday_end_date(holiday.type).should == holiday.end_date
      page.holiday_instructional_status(holiday.type).should == holiday.instructional
    end
  end
end

When /^the holiday.*are updated when I view the Holiday Calendar$/ do
  @holiday_calendar.view
  on ViewHolidayCalendar do |page|
    @holiday_calendar.holiday_list.each do |holiday|
      #puts "checking #{holiday.type}"
      page.holiday_start_date(holiday.type).should == holiday.start_date
      page.holiday_end_date(holiday.type).should == holiday.end_date
      page.holiday_instructional_status(holiday.type).should == holiday.instructional
    end
  end
end


When /^I create a holiday calendar by copying an existing calendar from search$/ do
  @holiday_calendar = create HolidayCalendar, :year=> "2012", :create_by_copy_search =>(make HolidayCalendar, :name=>"2012-2013 Holiday Calendar")
end

Then /^the holiday calendar should reflect the updates$/ do
  @holiday_calendar.view :perform_search => false
  on ViewHolidayCalendar do |page|
    page.hcal_name.should == @holiday_calendar.name
    page.hcal_start_date.should == @holiday_calendar.start_date
    page.hcal_end_date.should == @holiday_calendar.end_date
  end
end

Then /^the holiday calendar appears in the search results$/ do
  on CalendarSearch do |page|
      page.results_list.should include @holiday_calendar.name
  end
end

Then /^the holiday calendar does not appear in the search results$/ do
  on CalendarSearch do |page|
      begin
      page.results_list.should_not include @holiday_calendar.name
      rescue Watir::Exception::UnknownObjectException
        #means no results at all - ok
      end
    end
end

When /^I search for the Holiday Calendar using (.*)$/ do |arg|
  search_terms = { :wildcards=>"#{@holiday_calendar.name[0..2]}*", :"partial name"=>@holiday_calendar.name[0..2] }
  visit Enrollment do |page|
    page.search_for_calendar_or_term
  end
  on CalendarSearch do |page|
    page.search_for "Holiday Calendar", search_terms[arg.to_sym]

    while page.pagination_info.exists?
      if page.results_list.include? @holiday_calendar.name
        break
      else
        page.right_arrow.click
      end
    end
  end
end
When /^I add a new Holiday with an end date later than the Holiday Calendar end date$/ do
  start_date = (Date.strptime( @holiday_calendar.end_date , '%m/%d/%Y') + 2).strftime("%m/%d/%Y")
  @holiday_calendar.add_holiday :holiday =>  (make Holiday, :type => "Veterans Day observed", :start_date=>start_date )
end

Then /^a Holiday Dates (?:error|warning) message is displayed stating "([^"]*)"$/ do |exp_msg|
  on CreateEditHolidayCalendar do |page|
    page.holiday_validation_messages[0].text.should match /#{exp_msg}/
    page.cancel
  end
end

Then /^a Holiday Calendar (?:error|warning) message is displayed stating "([^"]*)"$/ do |exp_msg|
  on CreateEditHolidayCalendar do |page|
    page.holiday_calendar_validation_messages[0].text.should match /#{exp_msg}/
  end
end

When /^I edit a Holiday date so that the start date is earlier than the Holiday Calendar start date$/ do
  new_start_date = (Date.strptime( @holiday_calendar.start_date , '%m/%d/%Y') - 2).strftime("%m/%d/%Y")
  @holiday_calendar.edit_holiday "Veterans Day", {:start_date => new_start_date }
end

When /^I add a new Holiday with a date later than the Holiday end date$/ do
  holiday = (make Holiday, :type => "Veterans Day",
                        :start_date => "11/15/#{@holiday_calendar.year}",
                        :end_date => "11/14/#{@holiday_calendar.year}")
  @holiday_calendar.add_holiday :holiday =>  holiday, :exp_success => false
end

When /^I edit the holiday calendar making the start date that is after the end date$/ do
  new_start_date = (Date.strptime( @holiday_calendar.end_date , '%m/%d/%Y') + 1).strftime("%m/%d/%Y")
  @holiday_calendar.edit :start_date => new_start_date,
                         :exp_success => false
end

When /^I add a new Holiday with a blank start date$/ do
  holiday = (make Holiday, :type => "Veterans Day",
                  :start_date => "")
  @holiday_calendar.add_holiday :holiday =>  holiday, :exp_success => false
end

When /^I add a new Holiday with a start date with an invalid format$/ do
  holiday = (make Holiday, :type => "Christmas observed",
                  :start_date => "2012/11/11",
                  :instructional => true) #ensures error message is displayed, focus leaves start date field)
  @holiday_calendar.add_holiday :holiday =>  holiday, :defer_add => true
end

Then /^the holiday start date field is highlighted for the error$/ do
  on CreateEditHolidayCalendar do |page|
    page.holiday_start_date.click
    page.holiday_start_time.click
    sleep 2
    page.holiday_start_date.attribute_value('class').should match /error/
    page.cancel
  end
end

When /^I create a holiday calendar with a start date after the end date$/ do
  @holiday_calendar = make HolidayCalendar, :year => "2014",
                             :start_date => "07/12/2014",
                             :end_date => "07/09/2014",
                             :holiday_list => []
  @holiday_calendar.create :exp_success => false

end