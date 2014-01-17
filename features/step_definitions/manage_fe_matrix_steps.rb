When /^I edit a Standard Final Exam rule on the matrix$/ do
  @matrix = make FinalExamMatrix
  @matrix.edit :defer_save => true, :defer_submit => true
end

When /^I edit a Common Final Exam rule on the matrix$/ do
  @matrix = make FinalExamMatrix, :exam_type => "Common", :rule_requirements => "PHYS270"
  @matrix.edit :defer_save => true, :defer_submit => true
end

When /^I open the Final Exam Matrix for ([^"]*)$/ do |term|
  @matrix = make FinalExamMatrix, :term_type => term
  @matrix.manage
end

When /^I add a Common Final Exam course rule to the Final Exam Matrix$/ do
  @matrix = create FinalExamMatrix, :rule => "If course is <Course>", :rule_requirements => "ENGL312",
                   :exam_type => "Common", :courses => "ENGL312", :start_time => "08:00", :end_time => "10:00"
end

When /^I add Building and Room location data to the Requested Exam Offering Scheduling Information$/ do
  @matrix.add_edit_rdl_info
end

When /^I add a Standard Final Exam timeslot rule to the Final Exam Matrix$/ do
  @matrix = create FinalExamMatrix, :term_type => "Winter Term"
end

When /^I add a Standard Final Exam text rule to the Final Exam Matrix$/ do
  @matrix = create FinalExamMatrix, :term_type => "Winter Term", :rule => "Free Form Text",
                   :free_text => "Course needs to match this text"
end

Given /^I have a Final Exam Matrix to which I have added multiple Standard Final Exam rule statements$/ do
  @matrix_rule_list = []
  @matrix_rule_list << (create FinalExamMatrix, :term_type => "Winter Term", :days => "TH", :start_time => "06:00",
                   :end_time => "07:00", :time_ampm => "am", :rule_requirements => "TH at 06:00 AM - 07:00 AM")

  @matrix_rule_list << (create FinalExamMatrix, :term_type => "Winter Term", :rule => "Free Form Text",
                               :free_text => "To test the editing of the statement",
                               :rule_requirements => "To test the editing of the statement")
end

Given /^I have added a Standard Final Exam timeslot rule to the Final Exam Matrix$/ do
  @matrix = create FinalExamMatrix, :term_type => "Winter Term", :days => "TH", :start_time => "06:00",
                   :end_time => "07:00", :time_ampm => "am", :rule_requirements => "TH at 06:00 AM - 07:00 AM"
end

Given /^I have added a Common Final Exam course rule to the Final Exam Matrix$/ do
  @matrix = create FinalExamMatrix, :rule => "If course is <Course>",
         :exam_type => "Common", :courses => "CHEM272", :rule_requirements => "CHEM272"
end

When /^I edit the Standard Final Exam rule$/ do
  @matrix.edit :edit_statement => true, :days => "TWH", :rule_requirements => @matrix.rule_requirements
end

When /^I edit the Common Final Exam course rule$/ do
  @matrix.edit :edit_statement => true, :courses => "CHEM277",
               :rule_requirements => @matrix.rule_requirements
end

When /^I edit the newly created Standard Final Exam rules$/ do
  @matrix_rule_list[0].edit :edit_statement => true, :days => "TWH",
                            :rule_requirements => @matrix_rule_list[0].rule_requirements
  @matrix_rule_list[0].manage

  @matrix_rule_list[1].edit :edit_statement => true, :free_text => "This statement has been edited",
               :rule_requirements => @matrix_rule_list[1].rule_requirements
end

When /^I have a Standard Final Exam group with a rule statement in the Final Exam Matrix$/ do
  @matrix = create FinalExamMatrix, :term_type => "Fall Term", :rule => "Free Form Text",
                   :free_text => "To test whether statement is deleted",
                   :rule_requirements => "To test whether statement is deleted"
end

When /^I choose to edit the existing rule statement$/ do
  @matrix.manage
  on(FEMatrixView).edit @matrix.rule_requirements, @matrix.exam_type
end

When /^I delete the statement and attempt to update the rule$/ do
  @matrix.delete_statement :rule_requirements => @matrix.rule_requirements, :edit_in_progress => true
end

When /^I delete an existing Standard Final Exam text rule to the Final Exam Matrix$/ do
  @matrix = create FinalExamMatrix, :term_type => "Winter Term", :rule => "Free Form Text",
                   :free_text => "To test whether rule is deleted",
                   :rule_requirements => "To test whether rule is deleted"

  @matrix.delete
end

When /^that I have a Final Exam Matrix with an existing Common Final Exam rule statement$/ do
  @matrix = create FinalExamMatrix, :term_type => "Winter Term", :rule => "If course is <Course>",
                   :exam_type => "Common", :courses => "HIST111", :rule_requirements => "HIST111",
                   :add_more_statements => true
end

When /^I add additional statements to the Common Final Exam rule on the Final Exam Matrix$/ do
  @matrix.add_additional_statements
end

When /^I view the Standard Final Exam rules on the Final Exam Matrix$/ do
  @matrix = make FinalExamMatrix
  @matrix.manage
end

When /^there is an Academic Term associated with a Final Exam matrix$/ do
  @matrix_term = make FinalExamMatrix, :term_type => "Fall Term"
end

When /^there is a second Academic Term that is not associated with any final exam matrix$/ do
  @matrix_second_term = make FinalExamMatrix, :term_type => "Summer Term"
end

When /^there is a second Academic Term associated with a Final Exam matrix$/ do
  @matrix_second_term = make FinalExamMatrix, :term_type => "Spring Term"
end

When /^there is an Academic Half Term that is not associated with any final exam matrix$/ do
  @matrix_halfterm = make FinalExamMatrix, :term_type => "Summer 1"
end

When /^I associate the second Term with the Final Exam matrix of the initial Term$/ do
  @matrix_second_term.manage
  on FEMatrixView do |page|
    page.term_type_select.select @matrix_term.term_type
    page.loading.wait_while_present
    page.submit
    page.loading.wait_while_present
  end
end

When /^I associate the Half Term with the Final Exam matrix of the initial Term$/ do
  @matrix_halfterm.manage
  on FEMatrixView do |page|
    page.term_type_select.select @matrix_term.term_type
    page.loading.wait_while_present
    page.submit
    page.loading.wait_while_present
  end
end

When /^I view the term in the Manage Final Exam Matrix screen$/ do
  @matrix_term.manage
end

When /^I view the initial term$/ do
  @matrix_term.manage
end

When /^I view the second term$/ do
  @matrix_second_term.manage
end

When /^I view the third term$/ do
  @matrix_third_term.manage
end

When /^I view the half term$/ do
  @matrix_halfterm.manage
end

When /^I associate the Half Term with the Final Exam matrix of the third Term$/ do
  @matrix_halfterm.manage
  on FEMatrixView do |page|
    page.term_type_select.select @matrix_second_term.term_type
    page.loading.wait_while_present
    page.submit
    page.loading.wait_while_present
  end
end

Then /^I should be able to choose any one of Day 1 to 6 for the rule$/ do
  on FEMatrixEdit do |page|
    for i in 1..6
      page.rdl_days.option( value: "#{i}").text.should == "Day #{i}"
    end
    page.cancel_rule
  end
  on(FEMatrixView).cancel
end

Then /^I can only view all the rules in the Final Exam Matrix$/ do
  on FEMatrixView do |page|
    page.standard_final_exam_section.text.should_not match /Edit/
    page.standard_final_exam_section.text.should_not match /Delete/
    page.common_final_exam_section.text.should_not match /Edit/
    page.common_final_exam_section.text.should_not match /Delete/
  end
end

Then /^I cannot add a new rule to the Final Exam Matrix$/ do
  on FEMatrixView do |page|
    page.standard_final_exam_section.text.should_not match /Add/
    page.common_final_exam_section.text.should_not match /Add/
  end
end

Then /^I have the option of editing or deleting rules in the Final Exam Matrix$/ do
  on FEMatrixView do |page|
    page.standard_final_exam_section.text.should match /Edit/
    page.standard_final_exam_section.text.should match /Delete/
    page.common_final_exam_section.text.should match /Edit/
    page.common_final_exam_section.text.should match /Delete/
  end
end

Then /^I have the option to add a new rule to the Final Exam Matrix$/ do
  on FEMatrixView do |page|
    page.standard_final_exam_section.text.should match /Add/
    page.common_final_exam_section.text.should match /Add/
  end
end

Then /^I should be able to see the newly created course rule in the Common Final Exam table$/ do
  @matrix.manage
  on FEMatrixView do |page|
    page.common_fe_target_row( @matrix.courses).should_not == nil
  end
end

Then /^I should be able to see the location data for the exam specified in the course rule in the Common Final Exam table$/ do
  @matrix.manage
  on FEMatrixView do |page|
    page.common_fe_target_row( @matrix.courses).should_not == nil
    page.get_common_fe_facility( @matrix.courses).should match /Mathematics Bldg/
    page.get_common_fe_room( @matrix.courses).should match /0304/
  end
end

Then /^I should be able to see the newly created timeslot rule in the Standard Final Exam table$/ do
  @matrix.manage
  on FEMatrixView do |page|
    requirements = page.get_standard_fe_requirements( @matrix.days)
    requirements.should match /#{@matrix.days} at #{@matrix.start_time} #{@matrix.time_ampm.upcase} - #{@matrix.end_time} #{@matrix.time_ampm.upcase}\./
    page.get_standard_fe_day( @matrix.days).should match /#{@matrix.rdl_days}/
    page.get_standard_fe_time( @matrix.days).should match /#{@matrix.start_time} #{@matrix.time_ampm.upcase}-#{@matrix.end_time} #{@matrix.time_ampm.upcase}/
  end
end

Then /^I should be able to see the newly created text rule in the Standard Final Exam table$/ do
  @matrix.manage
  on FEMatrixView do |page|
    page.standard_fe_target_row( @matrix.free_text).should_not == nil
    page.get_standard_fe_day( @matrix.free_text).should match /#{@matrix.rdl_days}/
    page.get_standard_fe_time( @matrix.free_text).should match /#{@matrix.start_time} #{@matrix.time_ampm.upcase}-#{@matrix.end_time} #{@matrix.time_ampm.upcase}/
  end
end

Then /^I should be able to see the edited timeslot rule in the Standard Final Exam table$/ do
  @matrix.manage
  on FEMatrixView do |page|
    requirements = page.get_standard_fe_requirements( @matrix.days)
    requirements.should match /#{@matrix.days} at #{@matrix.start_time} #{@matrix.time_ampm.upcase} - #{@matrix.end_time} #{@matrix.time_ampm.upcase}\./
    page.get_standard_fe_day( @matrix.days).should match /#{@matrix.rdl_days}/
    page.get_standard_fe_time( @matrix.days).should match /#{@matrix.start_time} #{@matrix.time_ampm.upcase}-#{@matrix.end_time} #{@matrix.time_ampm.upcase}/
  end
end

Then /^I should be able to see the edited course rule in the Common Final Exam table$/ do
  @matrix.manage
  on FEMatrixView do |page|
    page.common_fe_target_row( @matrix.courses).should_not == nil
    page.get_common_fe_day( @matrix.courses).should match /#{@matrix.rdl_days}/
    page.get_common_fe_time( @matrix.courses).should match /#{@matrix.start_time} #{@matrix.time_ampm.upcase}-#{@matrix.end_time} #{@matrix.time_ampm.upcase}/
  end
end

Then /^there should be a validation message displayed stating "([^"]+)"$/ do |exp_msg|
  on FEMatrixEdit do |page|
    page.loading.wait_while_present
    page.validation_message_text.should match /#{exp_msg}/
  end
end

Then /^I should be able to see the Common Final Exam rule with the multiple statements$/ do
  @matrix.manage
  on FEMatrixView do |page|
    page.get_common_fe_day( @matrix.courses).should match /#{@matrix.rdl_days}/
    page.get_common_fe_time( @matrix.courses).should match /#{@matrix.start_time} #{@matrix.time_ampm.upcase}-#{@matrix.end_time} #{@matrix.time_ampm.upcase}/
  end
end

Then /^I should be able to see all the changes I have made on the Final Exam Matrix$/ do
  @matrix_rule_list[1].manage
  on FEMatrixView do |page|
    page.standard_final_exam_table.present?.should be_true
    requirements = page.get_standard_fe_requirements( @matrix_rule_list[0].days)
    requirements.should match /#{@matrix_rule_list[0].days} at #{@matrix_rule_list[0].start_time} #{@matrix_rule_list[0].time_ampm.upcase} - #{@matrix_rule_list[0].end_time} #{@matrix_rule_list[0].time_ampm.upcase}\./
    page.get_standard_fe_requirements( @matrix_rule_list[1].free_text).should match /#{@matrix_rule_list[1].free_text}/
  end
end

Then /^the deleted text rule should not exist on the Final Exam Matrix$/ do
  @matrix.manage
  on FEMatrixView do |page|
    page.standard_fe_target_row( @matrix.free_text).should == nil
  end
end

Then /^the rules should be sorted on the Days and Time columns$/ do
  on FEMatrixView do |page|
    array_of_days = page.get_all_standard_fe_days
    ordered_days = array_of_days.shift
    array_of_days.each do |day|
      ordered_days << "#{day}"
    end
    for i in 1..6 do
      if i != 6
        j = i + 1
        ordered_days.should match /Day #{i}.*Day #{(j)}/m
        ordered_days.should_not match /Day #{(j)}.*Day #{i}/m
      end

      array_of_times = page.get_all_standard_fe_times_for_day( "Day #{i}")
      first_time = array_of_times.shift
      if first_time =~ /^(\d\d:\d\d [AP]M)-(\d\d:\d\d [AP]M)$/
        first_start_time = DateTime.strptime($1, '%I:%M %p')
      end
      array_of_times.each do |time|
        if time =~ /^(\d\d:\d\d [AP]M)-(\d\d:\d\d [AP]M)$/
          start_time = DateTime.strptime($1, '%I:%M %p')
        end
        if first_start_time.to_s < start_time.to_s
          true
        else
          false
        end
        first_start_time = start_time
      end
    end
  end
end

Then /^the Exam Location indicator should be visible$/ do
  on FEMatrixView do |page|
    page.set_standard_exam_location.visible?.should == true
  end
end

Then /^the user is not able to access the Exam Location indicator$/ do
  on FEMatrixView do |page|
    page.set_standard_exam_location.attribute_value('disabled').should == "true"
  end
end

Then /^there is a message indicating that the final exam matrix is also used by the second term$/ do
  on FEMatrixView do |page|
    page.info_validation_message_text.should match /Matrix is also linked to #{@matrix_second_term.term_type}\./
  end
end

Then /^there is a message indicating that the final exam matrix is also used by the half term$/ do
  on FEMatrixView do |page|
    page.info_validation_message_text.should match /Matrix is also linked to #{@matrix_halfterm.term_type}\./
  end
end

Then /^there is no message indicating that the final exam matrix is also used by the half term$/ do
  on FEMatrixView do |page|
    if page.info_validation_message.exists?
      page.info_validation_message_text.should_not match /Matrix is also linked to #{@matrix_halfterm.term_type}\./
    end
  end
end

Then /^Standard Final Exam or Common Final Exam rules from the initial term are listed with the second term$/ do
  @matrix_second_term.manage
  on FEMatrixView do |page|
    page.standard_final_exam_section.visible?.should == true
    page.common_final_exam_section.visible?.should == true
  end
end

Then /^I should have a choice of terms from which to associate the Final Exam Matrix$/ do
  on FEMatrixView do |page|
    page.term_type_select.option(value: "kuali.atp.type.Fall").text.should == "Fall Term"
    page.term_type_select.option(value: "kuali.atp.type.Spring").text.should == "Spring Term"
  end
end