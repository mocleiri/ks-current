Feature: Exam Offerings change when Modifying Schedule Information

  FE 5.1: As a Central Administrator I want exam offering requested scheduling information to be updated dynamically
  when I change AO RSIs so that scheduling information remains in line with those of the AO driving the exam and matrix slotting

  Background:
    Given I am logged in as admin

    @draft #KSENROLL-12280
  Scenario: FE5.1.1 When adding RSI to the Activity Offering then the Exam Offerings should be updated with the new info
    Given that I copy a Course Offering that has an AO-driven exam that is marked to use the matrix and Requested Scheduling Information for the exam exists on the Final Exam Matrix
    And there is an Activity Offering that has RSI data but has no ASI data
    When I add new Requested Scheduling Information to the Activity Offering
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should be populated

    @draft #KSENROLL-12280
  Scenario: FE5.1.2 When adding RSI to the Activity Offering and RSI not found on Exam Matrix then the EO data should not be populated
    Given that I copy a Course Offering that has an AO-driven exam that is marked to use the matrix and Requested Scheduling Information for the exam exists on the Final Exam Matrix
    And there is an Activity Offering that has RSI data but has no ASI data
    When I add new Requested Scheduling Information to the Activity Offering that does not exist on the Exam Matrix
    Then there should be a warning message for the AO stating that "No match found on the Exam Matrix."
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should not be populated