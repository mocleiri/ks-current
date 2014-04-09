@blue_team
Feature: CO.Exam Offerings change when Modifying Schedule Information
  FE 5.2: As a Central Administrator I want exam offering requested scheduling information to be updated dynamically
  when I change AO ASIs so that scheduling information remains in line with those of the AO driving the exam and matrix slotting

  Background:
    Given I am logged in as admin

  #KSENROLL-12381
  @pending
  Scenario: FE5.2.1 When I send the edited RSI to the scheduler to update the ASI then the EO data should be updated
    Given that the Course Offering has an AO-driven exam that is marked to use the matrix and Actual Scheduling Information for the exam does exist
    When I update the Actual Scheduling Information for the Activity Offering
    And I view the Exam Offerings for the Course Offering
    Then the Actual Scheduling Information for the Exam Offering of the AO should be populated

  #KSENROLL-12381
  @pending
  Scenario: FE5.2.2 When I send the edited RSI to the scheduler to update the ASI with data not on the Matrix then the EO data should not be updated
    Given that the Course Offering has an AO-driven exam that is marked to use the matrix and Actual Scheduling Information for the exam does exist
    When I update the Actual Scheduling Information for the Activity Offering which does not match the Exam Matrix
    And I view the Exam Offerings for the Course Offering
    Then the Actual Scheduling Information for the Exam Offering of the AO should not be populated