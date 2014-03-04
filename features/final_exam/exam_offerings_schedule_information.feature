@blue_team
Feature: CO.Exam Offering Schedule Information
  FE 4.2: As a Central Adminstrator I want to have on matrix exam offerings populated with requested delivery logistics
  when they are created by dynamic processes so that exam offering scheduling requests are consistent with the
  established exam matrix

  Background:
    Given I am logged in as admin

  #KSENROLL-12013
  @pending
  Scenario: FE4.2.1 Test that when CO created from catalog while present on the FE Matrix that the schedule info is populated
    Given that the Course Offering exists on the Final Exam Matrix
    And I create a Course Offering from catalog in a term with a final exam period
    When I view the Exam Offerings for the Course Offering
    Then the Schedule Information for the Exam Offering should be populated

  #KSENROLL-12086
  @pending
  Scenario: FE4.2.2 Test that when CO created from copy while not present on the FE Matrix that the schedule info is not populated
    Given that the Course Offering does not exist on the Final Exam Matrix
    And I create a Course Offering from copy in a term with a final exam period
    Then there should be a warning message stating that "No match found on the Matrix."
    And I view the Exam Offerings for the Course Offering
    And the Schedule Information for the Exam Offering should not be populated

  #KSENROLL-12086
  @pending
  Scenario: FE4.2.3 Test that when CO created from copy while present on the FE Matrix that the schedule info is populated
    Given that the Course Offering exists on the Final Exam Matrix
    And I create a Course Offering from copy in a term with a final exam period
    When I view the Exam Offerings for the Course Offering
    Then the Schedule Information for the Exam Offering should be populated