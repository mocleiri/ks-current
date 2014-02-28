When /^I create an Academic Calendar$/ do
  @calendar = create AcademicCalendar
end

When /^I create an Academic Calendar in Official status$/ do
  @calendar = create AcademicCalendar
  @calendar.make_official
end

Then /^the Make Official button should become active$/ do
  on EditAcademicCalendar do |page|
    page.make_official_link.present?.should == true # TODO: Figure out why ".should_be enabled" does not work.
  end
end

When /^I search for the calendar$/ do
  @calendar.search
end

When /^I search for academic calendars$/ do
  @calendar = make AcademicCalendar, :name => "Academic Calendar"
  @calendar.search
end

When /^I search for holiday calendars$/ do
  @calendar = make HolidayCalendar, :name => "Holiday Calendar"
  @calendar.search
end

When /^I search for academic terms$/ do
  @calendar = make AcademicCalendar
  term = make AcademicTermObject, :term_name => "Term", :parent_calendar => @calendar
  @calendar.terms << term
  @calendar.terms[0].search
end

When /^I search for the Academic Calendar using (.*)$/ do |arg|
  search_terms = { :wildcards=>"*", :"partial name"=>@calendar.name[0..2] }
  visit Enrollment do |page|
    page.search_for_calendar_or_term
  end
  on CalendarSearch do |page|
    page.search_for "Academic Calendar", search_terms[arg.to_sym]

    while page.pagination_info.exists?
      if page.results_list.include? @calendar.name
        break
      else
        page.right_arrow.click
      end
    end
  end
end

Then /^the calendar (.*) appear in search results$/ do |arg|
  on CalendarSearch do |page|
    if arg == "should"
      page.results_list.should include @calendar.name
    else
      begin
        page.results_list.should_not include @calendar.name
      rescue Watir::Exception::UnknownObjectException
        # Implication here is that there were no search results at all.
      end
    end
  end
end

When /^I make the calendar official$/ do
  @calendar.make_official
end

Then /^the calendar (.*) be set to Official$/ do |arg|
  on CalendarSearch do |page|
    if arg == "should"
      is_official = page.calendar_action_text(@calendar.name).include? "Delete"
      is_official.should == false
    else
      is_official = page.calendar_status(@calendar.name).include? "Delete"
      is_official.should == true
    end
  end
end

When /^I copy the Academic Calendar$/ do
  @calendar_copy = make AcademicCalendar
  @calendar_copy.copy_from @calendar.name
end

When /^I update the Academic Calendar$/ do
  @calendar.edit :name => random_alphanums.strip,
                 :start_date => "02/15/#{@calendar.year.to_i + 1}",
                 :end_date => "07/06/#{@calendar.year.to_i + 1}"
end

When /^I edit the term and make it official$/ do
  @calendar.terms[0].make_official
  on(EditAcademicTerms).save
end

When /^I delete the Academic Calendar draft$/ do
  @calendar.delete_draft
end

When /^I delete the parent term of a subterm$/ do
  @calendar.terms[0].delete
end

Then /^the academic calendar should reflect the updates$/ do
  @calendar.view
  on ViewAcademicCalendar do |page|
    page.acal_name.should == @calendar.name
    page.acal_start_date.should == @calendar.start_date
    page.acal_end_date.should == @calendar.end_date
  end
end

And /^I make the term official$/ do
  @calendar.terms[0].make_official
end

Then /^I should be able to view the calendars$/ do
  on CalendarSearch do |page|
    begin
      # only check the visible rows of the table, and skip the header
      page.results_table.rows[2..-2].each do |row|
        row.i(class: "ks-fontello-icon-eye").present?.should be_true
      end
    rescue Watir::Exception::UnknownObjectException
      # Means no search results on the page.
      raise "Page has no results to check"
    end
  end
end

And /^I should not be able to edit a calendar$/ do
  on CalendarSearch do |page|
    begin
      page.results_table.rows.each do |row|
        row.i(class: "ks-fontello-icon-pencil").present?.should be_false
      end
    rescue Watir::Exception::UnknownObjectException
      # Means no search results on the page.
      raise "Page has no results to check"
    end
  end
end

And /^I should not be able to copy a calendar$/ do
  on CalendarSearch do |page|
    begin
      page.results_table.rows.each do |row|
        row.i(class: "ks-fontello-icon-docs").present?.should be_false
      end
    rescue Watir::Exception::UnknownObjectException
      # Means no search results on the page.
      raise "Page has no results to check"
    end
  end
end

Then /^I should be able to view the terms$/ do
  on CalendarSearch do |page|
    begin
      # only check the visible rows of the table, and skip the header
      last_row = page.showing_up_to.to_i - 1
      page.results_table.rows[1..last_row].each do |row|
        row.i(class: "ks-fontello-icon-eye").present?.should be_true
      end
    rescue Watir::Exception::UnknownObjectException
      # Means no search results on the page.
      raise "Page has no results to check"
    end
  end
end

And /^I should not be able to edit a term$/ do
  on CalendarSearch do |page|
    begin
      page.results_table.rows.each do |row|
        row.i(class: "ks-fontello-icon-pencil").present?.should be_false
      end
    rescue Watir::Exception::UnknownObjectException
      # Means no search results on the page.
      raise "Page has no results to check"
    end
  end
end

When /^I add a new term to the Academic Calendar$/ do
  term = make AcademicTermObject, :parent_calendar => @calendar, :term => 'Fall'
  @calendar.add_term term
  @manage_soc = make ManageSoc, :term_code => @calendar.terms[0].term_code
  @manage_soc.set_up_soc
end

When /^I add a new term to the Academic Calendar with a defined instructional period$/ do
  term = make AcademicTermObject, :parent_calendar => @calendar
  @calendar.add_term term
  @calendar.terms[0].expected_instructional_days = @calendar.terms[0].weekdays_in_term

  keydategroup = make KeyDateGroupObject, :parent_term => @calendar.terms[0], :key_date_group_type=> "Instructional"
  keydate = make KeyDateObject, :parent_term => @calendar.terms[0], :parent_key_date_group => keydategroup,
                    :key_date_type => "Instructional Period", :start_date => @calendar.terms[0].start_date,
                    :end_date => @calendar.terms[0].end_date
  keydategroup.key_dates << keydate
  @calendar.terms[0].add_key_date_group keydategroup
end

When /^I add a new subterm to the Academic Calendar with a defined instructional period$/ do
  term = make AcademicTermObject, :parent_calendar => @calendar, :term_type=> "Half Fall 1",
               :parent_term=> "Fall Term", :subterm => true
  @calendar.add_term term

  @calendar.terms[0].expected_instructional_days = @calendar.terms[0].weekdays_in_term

  keydategroup = make KeyDateGroupObject, :parent_term => @calendar.terms[0], :key_date_group_type=> "Instructional"
  keydate = make KeyDateObject, :parent_term => @calendar.terms[0], :parent_key_date_group => keydategroup,
                  :key_date_type => "Instructional Period", :start_date => @calendar.terms[0].start_date,
                  :end_date => @calendar.terms[0].end_date
  keydategroup.key_dates << keydate
  @calendar.terms[0].add_key_date_group keydategroup
end

Then /^the term is listed when I view the Academic Calendar$/ do
  @calendar.search

  on CalendarSearch do |page|
    page.view @calendar.name
  end

  on ViewAcademicTerms do |page|
    page.go_to_terms_tab
    page.open_term_section(@calendar.terms[0].term_type)
    page.term_name(@calendar.terms[0].term_type).should == @calendar.terms[0].term_name
    #page.term_code(@calendar.terms[0].term_type)
    page.term_start_date(@calendar.terms[0].term_type).should == @calendar.terms[0].start_date
    page.term_end_date(@calendar.terms[0].term_type).should == @calendar.terms[0].end_date
    page.term_status(@calendar.terms[0].term_type).should == "DRAFT"
    #puts page.term_instructional_days(@calendar.terms[0].term_type)
    #puts page.term_status(@calendar.terms[0].term_type)
    #puts page.key_date_start(@calendar.terms[0].term_type,"instructional","Grades Due")
    #puts page.key_date_start(@calendar.terms[0].term_type,"registration","Last Day to Add Classes")

  end
end

Then /^the updated term information is listed when I view the Academic Calendar$/ do
  @calendar.search

  on CalendarSearch do |page|
    page.view @calendar.name
  end

  on ViewAcademicTerms do |page|
    page.go_to_terms_tab
    page.open_term_section(@calendar.terms[0].term_type)
    page.term_name(@calendar.terms[0].term_type).should == @calendar.terms[0].term_name
    #page.term_code(@calendar.terms[0].term_type)
    page.term_start_date(@calendar.terms[0].term_type).should == @calendar.terms[0].start_date
    page.term_end_date(@calendar.terms[0].term_type).should == @calendar.terms[0].end_date
    page.term_status(@calendar.terms[0].term_type).should == "DRAFT"
    #puts page.term_instructional_days(@calendar.terms[0].term_type)
    #puts page.term_status(@calendar.terms[0].term_type)
    #puts page.key_date_start(@calendar.terms[0].term_type,"instructional","Grades Due")
    #puts page.key_date_start(@calendar.terms[0].term_type,"registration","Last Day to Add Classes")

  end
end

Given /^I copy an existing Academic Calendar$/ do
  @source_calendar = make AcademicCalendar, :name => "2012-2013 Continuing Education Calendar"
  @calendar = make AcademicCalendar
  @calendar.copy_from @source_calendar.name
end

Given /^I create an Academic Calendar that supports subterms$/ do
  @calendar = create AcademicCalendar
  term = make AcademicTermObject, :parent_calendar => @calendar
  @calendar.add_term term
end

When /^I edit the information for a term$/ do
  @calendar.terms[0].edit :term_name => "CE Term1",
             :start_date => (Date.strptime( @calendar.terms[0].start_date , '%m/%d/%Y') + 2).strftime("%m/%d/%Y"), #add 2 days
             :end_date => (Date.strptime( @calendar.terms[0].end_date , '%m/%d/%Y') + 2).strftime("%m/%d/%Y")     #add 2 days
end

When /^I add events to the Academic Calendar$/ do
  @calendar.search
  on CalendarSearch do |page|
    page.edit @calendar.name
  end
  events = []
  events << (make CalendarEventObject, :parent_calendar => @calendar)
  events << (make CalendarEventObject, :parent_calendar => @calendar, :event_type => "Homecoming Week")
  events.each do |event|
    @calendar.add_event event
  end
end

When /^I update the event dates$/ do
  @calendar.search
  on CalendarSearch do |page|
    page.edit @calendar.name
  end
  @calendar.events[0].edit :start_date => "04/06/#{@calendar.year.to_i + 1}", :end_date=>"05/29/#{@calendar.year.to_i + 1}",
                  :start_time=>"11:11", :end_time=>"09:45"
end

When /^I remove the events from the Academic Calendar$/ do
  @calendar.search
  on CalendarSearch do |page|
    page.edit @calendar.name
  end
  @event_to_delete = @calendar.events[0].event_type
  @calendar.events[0].delete
end

When /^I add a Holiday Calendar to the Academic Calendar$/ do
  hol_cal_start_date = Date.strptime(@calendar.start_date, '%m/%d/%Y')
  hol_cal_start_date += 1
  hol_cal_end_date = Date.strptime(@calendar.end_date, '%m/%d/%Y')
  hol_cal_end_date -= 1
  @holiday_calendar = create HolidayCalendar, :year => @calendar.year,
                             :start_date => hol_cal_start_date.strftime(format='%m/%d/%Y'),
                             :end_date => hol_cal_end_date.strftime(format='%m/%d/%Y')
  #:holiday_types=>[:start_date => hol_cal_start_date.strftime(format='%m/%d/%Y'),
  @holiday_calendar.make_official
  @calendar.search
  on CalendarSearch do |page|
    page.edit @calendar.name
  end
  on EditAcademicCalendar do |page|
    #toggle Holiday?
    page.add_holiday_calendar_select.select @holiday_calendar.name
    page.add_holiday_calendar
    page.save
  end
end

Then /^the .*event.*s are listed when I view the Academic Calendar$/ do
  @calendar.search

  on CalendarSearch do |page|
    page.view @calendar.name
  end

  on ViewAcademicCalendar do |page|
    page.go_to_calendar_tab
    page.open_events_section
    event_row = page.target_event_row_in_view(@calendar.events[0].event_type)
    if event_row == nil
      raise 'Created event not found in event table'
    else
      start_date_time = @calendar.events[0].start_date + " " + @calendar.events[0].start_time + " " + @calendar.events[0].start_time_ampm.upcase
      end_date_time = @calendar.events[0].end_date + " " + @calendar.events[0].end_time + " " + @calendar.events[0].end_time_ampm.upcase
      event_row.cells[ViewAcademicCalendar::VIEW_START_DATE_COL].text.should == start_date_time
      event_row.cells[ViewAcademicCalendar::VIEW_END_DATE_COL].text.should == end_date_time
    end
  end
end

Then /^the event list is updated when I view the Academic Calendar$/ do
  #search for the event, and it should not be there
  @calendar.search

  on CalendarSearch do |page|
    page.view @calendar.name
  end

  on ViewAcademicCalendar do |page|
    page.go_to_calendar_tab
    page.open_events_section
    event_row = nil
    event_row = page.target_event_row_in_view(@event_to_delete)
    event_row.should == nil
  end
end

When /^I delete the term$/ do
  @term_to_delete = @calendar.terms[0]
  @calendar.terms[0].delete
end

When /^the term is not listed when I view the Academic Calendar$/ do
  @calendar.view
  on ViewAcademicTerms do |page|
    page.term_index_by_term_type(@term_to_delete.term_type).should == -1 #means not present
  end
end

Then /^the term is listed in official status when I view the Academic Calendar$/ do
  @calendar.view
  on ViewAcademicTerms do |page|
    page.go_to_terms_tab
    page.term_status(@calendar.terms[0].term_type).should == "OFFICIAL"
  end
end

Then /^the subterm is listed in official status when I view the Academic Calendar$/ do
  @calendar.view
  on ViewAcademicTerms do |page|
    page.go_to_terms_tab
    page.term_status(@calendar.terms[1].term_type).should == "OFFICIAL"
  end
end

Then /^I add a new term to the Academic Calendar with an instructional key date$/ do
  step "I add a new term to the Academic Calendar"
  step "I add an instructional Key Date"
end

Then /^I add an instructional Key Date$/ do
  @calendar.terms[0].edit

  keydates = []
  keydategroup = make KeyDateGroupObject, :parent_term => @calendar.terms[0], :key_date_group_type=> "Instructional"
  keydates << (make KeyDateObject, :parent_term => @calendar.terms[0], :parent_key_date_group => keydategroup,
                  :key_date_type => "First Day of Classes", :start_date => "09/12/#{@calendar.year}", :end_date => "")
  keydates << (make KeyDateObject, :parent_term => @calendar.terms[0], :parent_key_date_group => keydategroup,
                  :key_date_type => "Last Day of Classes", :start_date => "12/09/#{@calendar.year}", :end_date => "")
  keydategroup.key_dates = keydates
  @calendar.terms[0].add_key_date_group keydategroup
end

Then /^I add an instructional Key Date to a subterm$/ do
  @calendar.terms[1].edit

  keydategroup = make KeyDateGroupObject, :parent_term => @calendar.terms[1], :key_date_group_type=> "Instructional"
  keydate = make KeyDateObject, :parent_term => @calendar.terms[1], :parent_key_date_group => keydategroup,
                  :key_date_type => "First Day of Classes", :start_date => "09/12/#{@calendar.year}", :end_date => ""
  keydategroup.key_dates << keydate
  @calendar.terms[1].add_key_date_group keydategroup
end

Then /^I edit an instructional Key Date$/ do
  @calendar.terms[0].edit
  @calendar.terms[0].key_date_groups[0].key_dates[0].edit :start_date => "09/14/#{@calendar.year}"
end

Then /^I delete an instructional Key Date Group$/ do
  term = make AcademicTermObject, :parent_calendar => @calendar, :term => "Continuing Education Term 1"
  @calendar.terms << term
  @calendar.terms[0].edit

  keydategroup = make KeyDateGroupObject, :parent_term => @calendar.terms[0], :key_date_group_type => "Instructional"
  @calendar.terms[0].key_date_groups << keydategroup
  @calendar.terms[0].key_date_groups[0].delete
end


Then /^I delete an instructional Key Date$/ do
  term = make AcademicTermObject, :parent_calendar => @calendar, :term => "Continuing Education Term 1"
  @calendar.terms << term
  @calendar.terms[0].edit

  keydategroup = make KeyDateGroupObject, :parent_term => @calendar.terms[0], :key_date_group_type => "Instructional"
  keydate = make KeyDateObject, :parent_term => @calendar.terms[0], :parent_key_date_group => keydategroup,
                  :key_date_type => "First Day of Classes"
  keydategroup.key_dates << keydate
  @calendar.terms[0].key_date_groups << keydategroup
  @key_date_to_delete = @calendar.terms[0].key_date_groups[0].key_dates[0]
  @calendar.terms[0].key_date_groups[0].key_dates[0].delete
end

Then /^the Key Date is listed with the academic term information$/ do
  @calendar.view
  on ViewAcademicTerms do |page|
    page.go_to_terms_tab
    page.open_term_section @calendar.terms[0].term_type
    page.key_date_start(@calendar.terms[0].term_type, "Instructional", @calendar.terms[0].key_date_groups[0].key_dates[0].key_date_type ).should == @calendar.terms[0].key_date_groups[0].key_dates[0].start_date
  end
end

Then /^the updated Key Date is listed with the academic term information$/ do
  @calendar.view
  on ViewAcademicTerms do |page|
    page.go_to_terms_tab
    page.open_term_section(@calendar.terms[0].term_type)
    page.key_date_start(@calendar.terms[0].term_type, "Instructional", @calendar.terms[0].key_date_groups[0].key_dates[0].key_date_type ).should == @calendar.terms[0].key_date_groups[0].key_dates[0].start_date
  end
end

Then /^the Key Date is not listed with the academic term information$/ do
  @calendar.view
  on ViewAcademicTerms do |page|
    page.go_to_terms_tab
    page.open_term_section(@calendar.terms[0].term_type)
    page.target_key_date_row(@calendar.terms[0].term_type, "Instructional", @key_date_to_delete.key_date_type).exists?.should == false
  end
end

Then /^the Key Date Group is not listed with the academic term information$/ do
  @calendar.view
  on ViewAcademicTerms do |page|
    page.go_to_terms_tab
    page.open_term_section(@calendar.terms[0].term_type)
    page.key_date_group_div(@calendar.terms[0].term_type, "Instructional").nil?.should == true
  end
end

Then /^the Key Dates are copied without date values$/ do
  term = make AcademicTermObject, :parent_calendar => @calendar, :term => "Continuing Education Term 1"
  @calendar.terms << term
  @calendar.terms[0].edit

  on EditAcademicTerms do |page|
    page.go_to_terms_tab
    page.key_date_exists?(@calendar.terms[0].term_type, "Instructional", "First Day of Classes").should == true

    row = page.key_date_target_row(@calendar.terms[0].term_type, "Instructional", "First Day of Classes")
    page.key_date_start_date(row).should == ""
    page.key_date_start_time(row).should == ""
    page.key_date_end_date(row).should == ""
    page.key_date_end_time(row).should == ""

    page.key_date_exists?(@calendar.terms[0].term_type, "Registration", "Drop Date").should == true

    row = page.key_date_target_row(@calendar.terms[0].term_type, "Registration", "Drop Date")
    page.key_date_start_date(row).should == ""
    page.key_date_start_time(row).should == ""
    page.key_date_end_date(row).should == ""
    page.key_date_end_time(row).should == ""

  end
end

Then /^the instructional days calculation is correct$/ do
  @calendar.view
  on ViewAcademicTerms do |page|
    page.go_to_terms_tab
    page.open_term_section(@calendar.terms[0].term_type)
    page.term_instructional_days(@calendar.terms[0].term_type).to_i.should == @calendar.terms[0].expected_instructional_days.to_i
  end
end

When /^I add a Holiday Calendar with holidays in the term$/ do
  holiday_list =  Array.new(1){make Holiday, :type=>"Columbus Day", :start_date=>"09/05/#{@calendar.year}", :instructional=>false}
  @holiday_calendar = create HolidayCalendar, :year => @calendar.year,
                             :start_date => @calendar.start_date,
                             :end_date => @calendar.end_date,
                             :holiday_list => holiday_list
  @holiday_calendar.make_official

  @calendar.add_holiday_calendar(@holiday_calendar)
  @calendar.terms[0].expected_instructional_days -= 1 # 1 holiday added

end

Given /^I add a subterm$/ do
  terms = []
  terms << (make AcademicTermObject, :parent_calendar => @calendar, :term_type=> "Half Fall 1",
                          :parent_term=> "Fall Term", :subterm => true)
  @calendar.add_term terms[0]
end

Then /^the subterm is listed when I view the Academic Calendar$/ do
  @calendar.search

  on CalendarSearch do |page|
    page.view @calendar.name
  end

  on ViewAcademicTerms do |page|
    page.go_to_terms_tab
    page.open_term_section(@calendar.terms[1].term_type)
    page.term_name(@calendar.terms[1].term_type).should == @calendar.terms[1].term_name
    #page.term_code(@term.term_type)
    page.term_start_date(@calendar.terms[1].term_type).should == @calendar.terms[1].start_date
    page.term_end_date(@calendar.terms[1].term_type).should == @calendar.terms[1].end_date
    page.term_status(@calendar.terms[1].term_type).should == "DRAFT"
    #puts page.term_instructional_days(@term.term_type)
    #puts page.term_status(@term.term_type)
    #puts page.key_date_start(@term.term_type,"instructional","Grades Due")
    #puts page.key_date_start(@term.term_type,"registration","Last Day to Add Classes")

  end
end

Then /^the Holiday Calendar (.*) when I view the Academic Calendar$/ do |arg|
  @calendar.search

  on CalendarSearch do |page|
    page.view @calendar.name
  end
  on ViewAcademicCalendar do |page|
    page.go_to_calendar_tab
    if arg == "is listed"
      # what if multiple hcals
      page.hcal_name_text.should == @holiday_calendar.name
      page.hcal_start_date.should == @holiday_calendar.start_date
      page.hcal_end_date.should == @holiday_calendar.end_date
    else
      if page.hcal_name_div.exists?
        page.hcal_name_text.should_not == @holiday_calendar.name
      else
        #return true
      end
    end
  end
end

Then /^I remove the Holiday Calendar$/ do
  @calendar.search

  on CalendarSearch do |page|
    page.edit @calendar.name
  end

  on EditAcademicCalendar do |page|
    #page.go_to_calendar_tab
    page.delete_holiday_cal(@holiday_calendar.name)
    page.save
  end
end

Given /^I create an Academic Calendar with subterms$/ do
  @calendar = create AcademicCalendar #, :year => "2235", :name => "fSZtG62zfU"
  term = make AcademicTermObject, :parent_calendar => @calendar
  @calendar.add_term term

  terms = []
  terms << (make AcademicTermObject, :parent_calendar => @calendar, :term_type=> "Half Fall 1", :parent_term=> "Fall Term",
                          :subterm => true, :start_date => "09/02/#{@calendar.year}", :end_date => "09/11/#{@calendar.year}")

  terms << (make AcademicTermObject, :parent_calendar => @calendar, :term_type=> "Half Fall 2", :parent_term=> "Fall Term",
                          :subterm => true, :start_date => "09/12/#{@calendar.year}", :end_date => "09/24/#{@calendar.year}")

  terms.each do |term|
    @calendar.add_term term
  end

  @manage_soc = make ManageSoc, :term_code => @calendar.terms[0].term_code
  @manage_soc.set_up_soc
  @manage_soc.perform_manual_soc_state_change "open"
end

Given /^I make the subterms official$/ do
  @calendar.edit
  @calendar.terms[1..2].each do |subterm|
    subterm.make_official
  end
end

Then /^the subterms are successfully copied$/ do
  @calendar.search

  on CalendarSearch do |page|
    page.view @calendar.name
  end

  @calendar.terms[1..2].each do |subterm|
    on ViewAcademicTerms do |page|
      page.go_to_terms_tab
      page.open_term_section(subterm.term_type)
      page.term_name(subterm.term_type).should == subterm.term_name
      #page.term_code(@term.term_type)
      page.term_start_date(subterm.term_type).should == subterm.start_date
      page.term_end_date(subterm.term_type).should == subterm.end_date
      page.term_status(subterm.term_type).should == "DRAFT"

    end
  end
end

Then /^I can search and view the subterm in read only mode$/ do
  @calendar.terms[1].search

  on CalendarSearch do |page|
    page.view @calendar.terms[1].term_name
  end

  on ViewAcademicTerms do |page|
    page.go_to_terms_tab
    page.open_term_section(@calendar.terms[1].term_type)
    page.term_name(@calendar.terms[1].term_type).should == @calendar.terms[1].term_name
    #page.term_code(@term.term_type)
    page.term_start_date(@calendar.terms[1].term_type).should == @calendar.terms[1].start_date
    page.term_end_date(@calendar.terms[1].term_type).should == @calendar.terms[1].end_date
    page.term_status(@calendar.terms[1].term_type).should == "DRAFT"

  end
end

When /^I edit the subterm information$/ do
  @calendar.terms[1].edit :term_name => random_alphanums ,
                        :start_date => (Date.strptime( @calendar.terms[1].start_date , '%m/%d/%Y') + 2).strftime("%m/%d/%Y"), #add 2 days
                        :end_date => (Date.strptime( @calendar.terms[1].end_date , '%m/%d/%Y') - 2).strftime("%m/%d/%Y")  #less 2 days
end

When /^I edit the calendar$/ do
  @calendar.edit
end

When /^I delete a subterm$/ do
  @term_to_delete = @calendar.terms[1]
  @calendar.terms[1].delete
end

Then /^the subterm in updated successfully$/ do
  @calendar.terms[1].search

  on CalendarSearch do |page|
    page.view @calendar.terms[1].term_name
  end

  on ViewAcademicTerms do |page|
    page.go_to_terms_tab
    page.open_term_section(@calendar.terms[1].term_type)
    page.term_name(@calendar.terms[1].term_type).should == @calendar.terms[1].term_name
    #page.term_code(@term.term_type)
    page.term_start_date(@calendar.terms[1].term_type).should == @calendar.terms[1].start_date
    page.term_end_date(@calendar.terms[1].term_type).should == @calendar.terms[1].end_date
    page.term_status(@calendar.terms[1].term_type).should == "DRAFT"
  end

end

Then /^the subterm is no longer listed on the calendar$/ do
  @calendar.search

  on CalendarSearch do |page|
    page.view @calendar.name
  end

  on ViewAcademicTerms do |page|
    page.go_to_terms_tab
    page.term_index_by_term_type(@term_to_delete.term_type).should == -1 #ie not found
  end

end

When /^I add a new term with start date earlier than the Academic Calendar start date$/ do
  term = make AcademicTermObject, :parent_calendar => @calendar,
               :start_date => (Date.strptime( @calendar.start_date , '%m/%d/%Y') - 2).strftime("%m/%d/%Y") #minus 2 days
  @calendar.add_term term
end

When /^I add a new subterm with start date earlier than the Academic Calendar start date$/ do
  terms = []
  terms << (make AcademicTermObject, :parent_calendar => @calendar,
                          :start_date => (Date.strptime( @calendar.start_date , '%m/%d/%Y') - 2).strftime("%m/%d/%Y"), #minus 2 days
                          :subterm => true, :term_type=> "Half Fall 1", :parent_term=> "Fall Term" )
  @calendar.add_term terms[0]
end

Then /^a term warning message is displayed stating "([^"]*)"$/ do |exp_msg|
  on EditAcademicTerms do |page|
    page.open_term_section @calendar.terms[0].term_type
    page.term_validation_messages(@calendar.terms[0].term_type)[0].text.should match /#{exp_msg}/
  end
end

Then /^a subterm warning message is displayed stating "([^"]*)"$/ do |exp_msg|
  on EditAcademicTerms do |page|
    page.open_term_section @calendar.terms[1].term_type
    page.term_validation_messages(@calendar.terms[1].term_type)[0].text.should match /#{exp_msg}/
  end
end

Given /^I create an Academic Calendar with a term$/ do
  @calendar = create AcademicCalendar
  term = make AcademicTermObject, :parent_calendar => @calendar
  @calendar.add_term term
end

When /^I edit the term so that the start date is earlier than the Academic Calendar start date$/ do
  @calendar.terms[0].edit :start_date => (Date.strptime( @calendar.start_date , '%m/%d/%Y') - 2).strftime("%m/%d/%Y") #minus 2 days
end

When /^I edit the subterm so that the start date is earlier than the Academic Calendar start date$/ do
  @calendar.terms[1].edit :start_date => (Date.strptime( @calendar.start_date , '%m/%d/%Y') - 2).strftime("%m/%d/%Y") #minus 2 days
end

When /^I add a new key date with a date later than the Academic Term end date$/ do
  @calendar.terms[0].edit

  keydategroup = make KeyDateGroupObject, :parent_term => @calendar.terms[0], :key_date_group_type=> "Instructional"
  keydate = make KeyDateObject, :parent_term => @calendar.terms[0], :parent_key_date_group => keydategroup,
                    :key_date_type => "First Day of Classes",
                    :start_date => (Date.strptime( @calendar.terms[0].end_date , '%m/%d/%Y') + 2).strftime("%m/%d/%Y"),
                    :end_date => (Date.strptime( @calendar.terms[0].end_date , '%m/%d/%Y') + 2).strftime("%m/%d/%Y")
  keydategroup.key_dates << keydate
  @calendar.terms[0].add_key_date_group keydategroup
end

When /^I add a new key date with a date later than the Academic Subterm end date$/ do
  @calendar.terms[1].edit

  keydategroup = make KeyDateGroupObject, :parent_term => @calendar.terms[0], :key_date_group_type=> "Instructional"
  keydate = make KeyDateObject, :parent_term => @calendar.terms[0], :parent_key_date_group => keydategroup,
                  :key_date_type => "First Day of Classes",
                  :start_date => (Date.strptime( @calendar.terms[0].end_date , '%m/%d/%Y') + 2).strftime("%m/%d/%Y"), :end_date => ""
  keydategroup.key_dates << keydate
  @calendar.terms[1].add_key_date_group keydategroup
end


When /^I edit the key date so that the start date is later than the Academic Term end date$/ do
  @calendar.terms[0].edit
  @calendar.terms[0].key_date_groups[0].key_dates[0].edit :start_date => (Date.strptime( @calendar.terms[0].end_date , '%m/%d/%Y') + 2).strftime("%m/%d/%Y")
end

When /^I edit the key date so that the start date is later than the Academic Subterm end date$/ do
  @calendar.terms[1].edit
  @calendar.terms[1].key_date_groups[0].key_dates[0].edit :exp_success => false,
                    :start_date => (Date.strptime( @calendar.terms[1].end_date , '%m/%d/%Y') + 2).strftime("%m/%d/%Y")
end

When /^I make the key date blank$/ do
  @calendar.terms[0].edit
  @calendar.terms[0].key_date_groups[0].key_dates[0].edit :start_date => "", :exp_success => false
end

When /^I make the subterm key date blank$/ do
  @calendar.terms[1].edit
  @calendar.terms[1].key_date_groups[0].key_dates[0].edit :start_date => "", :exp_success => false
end

Then /^a Key Dates warning message is displayed stating "([^"]*)"$/ do |exp_msg|
  on EditAcademicTerms do |page|
    page.key_date_validation_messages(@calendar.terms[0].term_type)[0].text.should match /#{exp_msg}/
  end
end

Then /^a subterm Key Dates warning message is displayed stating "([^"]*)"$/ do |exp_msg|
  on EditAcademicTerms do |page|
    page.key_date_validation_messages(@calendar.terms[1].term_type)[0].text.should match /#{exp_msg}/
  end
end

Then /^the subterm is also deleted$/ do
  @calendar.terms[1].search

  on CalendarSearch do |page|
    begin
      page.results_list.should_not include @calendar.terms[1].term_name
    rescue Watir::Exception::UnknownObjectException
      # Implication here is that there were no search results at all.
    end
  end
end

