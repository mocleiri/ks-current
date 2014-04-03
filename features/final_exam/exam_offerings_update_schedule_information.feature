@blue_team
Feature: CO.Exam Offerings change when Modifying Schedule Information
  FE 5.2: As a Central Administrator I want exam offering requested scheduling information to be updated dynamically
  when I change AO ASIs so that scheduling information remains in line with those of the AO driving the exam and matrix slotting

  FE 4.10: As a Central Administrator I want CO Driven exam offering requested scheduling information to be populated
  from the matrix if the matrix setting for the Course Offering has changed from off to on matrix

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

  #KSENROLL-12380
  @wip @llf
  Scenario: FE4.10.1 When the Exam is updated from Alternate Exam to Standard while the CO is not on the matrix then the EO data should not be populated
    Given I create a Course Offering from catalog with an Alternate Exam that is not found on the matrix in a term with a defined final exam period
    When I edit the Course Offering to use a Standard Exam that is CO-Driven
    And I view the Exam Offerings for the Course Offering
    Then the Schedule Information for the Exam Offering should be blank

  #KSENROLL-12380
  @wip @llf
  Scenario: FE4.10.2 When the Exam is updated from No Exam to Standard while the CO is on the matrix then the EO data should be populated
    Given I create a Course Offering from catalog with No Exam that is found on the matrix in a term with a defined final exam period
    When I edit the Course Offering to use a Standard Exam that is CO-Driven
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering should be populated