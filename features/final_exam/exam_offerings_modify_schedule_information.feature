@nightly @blue_team
Feature: CO.Exam Offerings change when Modifying Schedule Information
  FE5.1 As a Central Administrator I want exam offering requested scheduling information to be updated dynamically
  when I change AO RSIs so that scheduling information remains in line with those of the AO driving the exam and matrix slotting

  FE5.4 As a Central Administrator I want exam offering requested scheduling information to be updated dynamically
  when I add AO RSIs to an AO that was created with no scheduling data so that scheduling information on the exam is populated from the matrix

  Background:
    Given I am logged in as admin

  #KSENROLL-12280
  Scenario: FE5.1.1 When adding RSI to the Activity Offering then the Exam Offerings should be updated with the new info
    Given I create from catalog a Course Offering with an AO-driven exam that uses the exam matrix in a term with a defined final exam period
    And I create an Activity Offering that has RSI data but has no ASI data
    When I add additional Requested Scheduling Information to the Activity Offering that matches an entry on the exam matrix
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should be populated

  #KSENROLL-12280
  Scenario: FE5.1.2 When adding RSI to the Activity Offering and RSI not found on Exam Matrix then the EO data should not be populated
    Given I create from catalog a Course Offering with an AO-driven exam that uses the exam matrix in a term with a defined final exam period
    And I create an Activity Offering that has RSI data but has no ASI data
    When I add new Requested Scheduling Information to the Activity Offering that does not match an entry on the exam matrix
    Then there should be a warning message for the AO stating that "No match found on the Exam Matrix."
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should not be populated

  #KSENROLL-12283
  @pending
  Scenario: FE5.4.1 When adding RSI to an Activity Offering that has no RSI or ASI then the EO RSI data should be populated
    Given I create from catalog a Course Offering with an AO-driven exam that uses the exam matrix in a term with a defined final exam period
    And I create an Activity Offering that has no ASIs or RSIs
    When I add new Requested Scheduling Information to the Activity Offering that matches an entry on the exam matrix
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should be populated

  #KSENROLL-12283
  @pending
  Scenario: FE5.4.2 When adding RSI to an Activity Offering that has no RSI or ASI and the RSI is not found on the matrix then the EO data should not be populated
    Given I create from catalog a Course Offering with an AO-driven exam that uses the exam matrix in a term with a defined final exam period
    And I create an Activity Offering that has no ASIs or RSIs
    When I add new Requested Scheduling Information to the Activity Offering that does not match an entry on the exam matrix
    Then there should be a warning message for the AO stating that "No match found on the Exam Matrix."
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should not be populated