When /^I edit a Standard Final Exam rule on the matrix$/ do
  @matrix = make FinalExamMatrix
  @matrix.add_rule :rule_obj =>  (make ExamMatrixRuleObject)
  @matrix.rules[0].edit :defer_save => true, :defer_submit => true
end

When /^I edit a Common Final Exam rule on the matrix$/ do
  @matrix = make FinalExamMatrix, :exam_type => "Common", :rule_requirements => "PHYS270"
  @matrix.add_rule :rule_obj =>  (make ExamMatrixRuleObject,
              :exam_type => 'Common', :days => "MTW", :start_time => "01:00", :st_time_ampm => "pm",
              :end_time => "03:00", :end_time_ampm => "pm")
  @matrix.rules[0].edit :defer_save => true, :defer_submit => true
end

When /^I open the Final Exam Matrix for ([^"]*)$/ do |term|
  @matrix = make FinalExamMatrix, :term_type => term
  @matrix.manage
end

When /^I add a Common Final Exam course rule to the Final Exam Matrix$/ do
  @matrix = make FinalExamMatrix, :term_type => "Winter Term"
  @matrix.add_rule :rule_obj => (make ExamMatrixRuleObject, :exam_type => "Common", :facility => '', :room => '' )
end

When /^I add Building and Room location data to the Requested Exam Offering Scheduling Information$/ do
  @matrix.rules[0].edit :facility => 'MTH', :room => '0304'
end

When /^I add a Standard Final Exam timeslot rule to the Final Exam Matrix$/ do
  @matrix = make FinalExamMatrix, :term_type => "Winter Term"
  @matrix.add_rule :rule_obj => (make ExamMatrixRuleObject)
end

When /^I add a Standard Final Exam text rule to the Final Exam Matrix$/ do
  @matrix = create FinalExamMatrix, :term_type => "Winter Term", :rule => "Free Form Text",
                   :free_text => "Course needs to match this text"
end

Given /^I have a Final Exam Matrix to which I have added multiple Standard Final Exam rule statements$/ do
  @matrix = make FinalExamMatrix, :term_type => "Winter Term"
  statement = []
  statement << (make ExamMatrixStatementObject,
                     :statement_option => ExamMatrixStatementObject::FREE_TEXT_OPTION,
                     :free_text => "To test the editing of the statement")
  statement << (make ExamMatrixStatementObject,
                     :statement_option => ExamMatrixStatementObject::TIME_SLOT_OPTION,
                     :statement_operator => 'And',
                     :days => 'FS')

  rule = make ExamMatrixRuleObject,
              :exam_type => 'Standard', :days => "TH", :start_time => "02:00", :st_time_ampm => "pm",
              :end_time => "03:00", :end_time_ampm => "pm",
              :statements =>statement
  @matrix.add_rule :rule_obj => rule
end

Given /^I have added a Standard Final Exam timeslot rule to the Final Exam Matrix$/ do
  @matrix = make FinalExamMatrix, :term_type => "Winter Term"
  @matrix.add_rule :rule_obj => (make ExamMatrixRuleObject,  :days => "TH", :start_time => "06:00", :st_time_ampm => "am",
                                      :end_time => "07:00", :end_time_ampm => "am")
end

Given /^I have added a Common Final Exam course rule to the Final Exam Matrix$/ do
  @matrix = make FinalExamMatrix, :term_type => "Winter Term"
  @matrix.add_rule :rule_obj => (make ExamMatrixRuleObject, :exam_type => "Common")
end

When /^I edit the Standard Final Exam rule$/ do
  @matrix.rules[0].edit  :rsi_days => "Day 3"
  @matrix.rules[0].statements[0].edit  :days => "F"
end

When /^I edit the Common Final Exam course rule$/ do
  @matrix.rules[0].edit  :rsi_days => "Day 5"
  @matrix.rules[0].statements[0].edit  :courses => "CHEM272"
end

When /^I edit the newly created Standard Final Exam rule statements$/ do
  @matrix.rules[0].statements[1].edit :days => "TWH"
  @matrix.rules[0].statements[0].edit :free_text => "This statement has been edited"
end

When /^I have a Standard Final Exam group with a rule statement in the Final Exam Matrix$/ do
  @matrix = make FinalExamMatrix, :term_type => "Fall Term"
  statement = make ExamMatrixStatementObject,
                   :statement_option => ExamMatrixStatementObject::FREE_TEXT_OPTION,
                   :free_text => "To test whether statement is deleted"

  rule = make ExamMatrixRuleObject,
               :days => "TH", :start_time => "06:00", :st_time_ampm => "am",
               :end_time => "07:00", :end_time_ampm => "am",
               :statements => [ statement ]
  @matrix.add_rule :rule_obj => rule
end

When /^I choose to edit the existing rule statement$/ do
  @matrix.manage
  on(FEMatrixView).edit @matrix.rules[0], @matrix.rules[0].exam_type
end

When /^I delete the statement and attempt to update the rule$/ do
  @matrix.rules[0].statements[0].delete :defer_submit => true
end

When /^I delete an existing Standard Final Exam text rule to the Final Exam Matrix$/ do
  @matrix = make FinalExamMatrix, :term_type => "Winter Term"
  statement = (make ExamMatrixStatementObject,
                     :statement_option => ExamMatrixStatementObject::FREE_TEXT_OPTION,
                     :free_text => "To test whether rule is deleted")
  rule = make ExamMatrixRuleObject,
              :exam_type => 'Standard', :days => "WF", :start_time => "08:00", :st_time_ampm => "pm",
              :end_time => "09:00", :end_time_ampm => "pm",
              :statements => [ statement ]
  @matrix.add_rule :rule_obj => rule
  @deleted_rule_obj = @matrix.rules[0]

  @matrix.rules[0].delete
end

When /^I create a Final Exam Matrix with multiple rule statements$/ do
  @matrix = make FinalExamMatrix, :term_type => "Winter Term"
  statement = []
  statement << (make ExamMatrixStatementObject,
                   :statement_option => ExamMatrixStatementObject::FREE_TEXT_OPTION,
                   :free_text => "To test multi-statements")
  statement << (make ExamMatrixStatementObject,
                     :statement_option => ExamMatrixStatementObject::COURSE_OPTION,
                     :statement_operator => 'And',
                     :courses => 'ENGL211')

  rule = make ExamMatrixRuleObject,
              :exam_type => 'Common', :days => "TH", :start_time => "02:00", :st_time_ampm => "pm",
              :end_time => "03:00", :end_time_ampm => "pm",
              :statements =>statement
  @matrix.add_rule :rule_obj => rule

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
      page.rsi_days.option( value: "#{i}").text.should == "Day #{i}"
    end
    page.cancel_rule
  end
  on(FEMatrixView).cancel
end

Then /^I can only view all the rules in the Final Exam Matrix$/ do
  on FEMatrixView do |page|
    page.standard_final_exam_table.rows[1..-2].each do |row|
      row.link(id: /delete_rule/).present?.should be_false
      row.link(id: /edit_rule/).present?.should be_false
    end
    page.common_final_exam_table.rows[1..-2].each do |row|
      row.link(id: /delete_rule/).present?.should be_false
      row.link(id: /edit_rule/).present?.should be_false
    end
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
    page.standard_final_exam_table.rows[1..-2].each do |row|
      row.link(id: /delete_rule/).present?.should be_true
      row.link(id: /edit_rule/).present?.should be_true
    end
    page.common_final_exam_table.rows[1..-2].each do |row|
      row.link(id: /delete_rule/).present?.should be_true
      row.link(id: /edit_rule/).present?.should be_true
    end
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
    page.common_fe_target_row( @matrix.rules[0]).should_not == nil
  end
end

Then /^I should be able to see the location data for the exam specified in the course rule in the Common Final Exam table$/ do
  @matrix.manage
  on FEMatrixView do |page|
    page.common_fe_target_row( @matrix.rules[0]).should_not == nil
    page.get_common_fe_facility( @matrix.rules[0]).should match /Mathematics Bldg/
    page.get_common_fe_room( @matrix.rules[0]).should match /0304/
  end
end

Then /^I should be able to see the newly created timeslot rule in the Standard Final Exam table$/ do
  @matrix.manage
  on FEMatrixView do |page|
    page.get_standard_fe_day( @matrix.rules[0]).should match /#{@matrix.rules[0].rsi_days}/
    page.get_standard_fe_time( @matrix.rules[0]).should match /#{@matrix.rules[0].start_time} #{@matrix.rules[0].st_time_ampm.upcase}-#{@matrix.rules[0].end_time} #{@matrix.rules[0].end_time_ampm.upcase}/
  end
end

Then /^I should be able to see the newly created text rule in the Standard Final Exam table$/ do
  @matrix.manage
  on FEMatrixView do |page|
    page.standard_fe_target_row( @matrix.free_text).should_not == nil
    page.get_standard_fe_day( @matrix.free_text).should match /#{@matrix.rsi_days}/
    page.get_standard_fe_time( @matrix.free_text).should match /#{@matrix.start_time} #{@matrix.time_ampm.upcase}-#{@matrix.end_time} #{@matrix.time_ampm.upcase}/
  end
end

Then /^I should be able to see the edited timeslot rule in the Standard Final Exam table$/ do
  @matrix.manage
  on FEMatrixView do |page|
    page.get_standard_fe_day( @matrix.rules[0]).should match /#{@matrix.rules[0].rsi_days}/
    page.get_standard_fe_time( @matrix.rules[0]).should match /#{@matrix.rules[0].start_time} #{@matrix.rules[0].st_time_ampm.upcase}-#{@matrix.rules[0].end_time} #{@matrix.rules[0].end_time_ampm.upcase}/
  end
end

Then /^I should be able to see the edited course rule in the Common Final Exam table$/ do
  @matrix.manage
  on FEMatrixView do |page|
    page.common_fe_target_row( @matrix.rules[0]).should_not == nil
    page.get_common_fe_day( @matrix.rules[0]).should match /#{@matrix.rules[0].rsi_days}/
    page.get_common_fe_time( @matrix.rules[0]).should match /#{@matrix.rules[0].start_time} #{@matrix.rules[0].st_time_ampm.upcase}-#{@matrix.rules[0].end_time} #{@matrix.rules[0].end_time_ampm.upcase}/
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
    page.get_common_fe_day( @matrix.rules[0]).should match /#{@matrix.rules[0].rsi_days}/
    page.get_common_fe_time( @matrix.rules[0]).should match /#{@matrix.rules[0].start_time} #{@matrix.rules[0].st_time_ampm.upcase}-#{@matrix.rules[0].end_time} #{@matrix.rules[0].end_time_ampm.upcase}/
  end
end

Then /^I should be able to see all the changes I have made on the Final Exam Matrix$/ do
  @matrix.manage
  on FEMatrixView do |page|
    page.standard_fe_target_row( @matrix.rules[0]).should_not == nil
  end
end

Then /^the deleted text rule should not exist on the Final Exam Matrix$/ do
  @matrix.manage
  on FEMatrixView do |page|
    page.standard_fe_target_row( @deleted_rule_obj).should == nil
    page.cancel
  end
end

Then /^the rules should be sorted on the Days and Time columns$/ do
  on FEMatrixView do |page|
    array_of_days = page.get_all_standard_fe_days
    ordered_days = array_of_days.sort

    array_of_days.should <=> ordered_days #.should == 0  #ie array is unchanged after sorting

    for day_no in 1..6 do
      array_of_times = page.get_all_standard_fe_times_for_day( "Day #{day_no}")
      sorted_times = array_of_times.sort
      array_of_times.should <=> sorted_times #).should == 0  #ie array is unchanged after sorting
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

Given /^that the Course Offering exists on the Final Exam Matrix$/ do
  @course_offering = make CourseOffering, :term => "201301", :course => "ENGL304"

  @matrix = make FinalExamMatrix, :term_type => "Spring Term"
  statement = []
  statement << (make ExamMatrixStatementObject, :statement_option => ExamMatrixStatementObject::COURSE_OPTION,
                     :courses => @course_offering.course)
  @rule = make ExamMatrixRuleObject, :exam_type => 'Common', :days => "TH", :start_time => "02:00", :st_time_ampm => "pm",
              :end_time => "03:00", :end_time_ampm => "pm", :statements => statement
  @matrix.add_rule :rule_obj => @rule
end

Then /^the Schedule Information for the Exam Offering should be populated$/ do
  on ViewExamOfferings do |page|
    page.eo_by_co_st_time.should match /#{Regexp.escape(@matrix.rules[0].start_time @matrix.rules[0].st_time_ampm)}/i
    page.eo_by_co_end_time.should match /#{Regexp.escape(@matrix.rules[0].end_time @matrix.rules[0].end_time_ampm)}/i
  end
end