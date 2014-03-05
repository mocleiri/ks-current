@nightly @blue_team
Feature: CO.Exam Offering Schedule Information
  FE 4.2: As a Central Adminstrator I want to have on matrix exam offerings populated with requested delivery logistics
  when they are created by dynamic processes so that exam offering scheduling requests are consistent with the
  established exam matrix

  KSENROLL-12096-FE4.x(1) As a Central Admin I want to schedule CO driven exams during the Create from Catalog as part of the dynamic
  process so that exam offering scheduling requests are consistent with the established exam matrix

  KSENROLL-12098-FE4.x(2) As a Central Admin I want to schedule AO driven exams during create from copy as part of the dynamic process
  so that exam offering scheduling requests are consistent with the established exam matrix

  Background:
    Given I am logged in as admin

  #KSENROLL-12013
  Scenario: FE4.2.1 Test that when an on-matrix CO is created from catalog that the requested scheduling info is correctly populated on the EO
    Given that the Course Offering exists on the Final Exam Matrix
    And I create a Course Offering from catalog in a term that uses the matrix and has a final exam period defined
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering should be populated

  #KSENROLL-12086
  Scenario: FE4.2.2 Test that when CO created from copy while not present on the FE Matrix that the schedule info is not populated
    Given that the Course Offering does not exist on the Final Exam Matrix
    And I create a Course Offering from copy in a term that uses the matrix and has a final exam period defined
    Then there should be a warning message stating that "No match found on the Matrix."
    And I view the Exam Offerings for the Course Offering
    And the Schedule Information for the Exam Offering should not be populated

  #KSENROLL-12086
  Scenario: FE4.2.3 Test that when CO created from copy while present on the FE Matrix that the schedule info is populated
    Given that the Course Offering exists on the Final Exam Matrix
    And I create a Course Offering from copy in a term that uses the matrix and has a final exam period defined
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering should be populated

  #KSENROLL-12090
  Scenario: FE4.x(1).1 Test that when CO created from catalog while not present on the FE Matrix that the schedule info is not populated
    Given that the Course Offering does not exist on the Final Exam Matrix
    And I create a Course Offering from catalog in a term that uses the matrix and has a final exam period defined
    Then there should be a warning message stating that "No match found on the Matrix."
    And I view the Exam Offerings for the Course Offering
    And the Schedule Information for the Exam Offering should not be populated

  #KSENROLL-12090
  Scenario: FE4.x(1).2 Test that when CO created from catalog while present on the FE Matrix that the schedule info is populated
    Given that the Course Offering exists on the Final Exam Matrix
    And I create a Course Offering from catalog in a term that uses the matrix and has a final exam period defined
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering should be populated

  #KSENROLL-12087
  @wip @llf
  Scenario: FE4.x(2).1 Test that when CO created from copy with exam period that is AO Driven then the schedule info is populated
    Given that the Requested Scheduling Information exists on the Final Exam Matrix
    And I create a Course Offering from copy in a term that uses the matrix and has an AO Driven final exam period defined
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should be populated

  #KSENROLL-12104
  @wip @llf
  Scenario: FE4.x(2).2 Test that when CO created from copy with exam period that is AO Driven then the schedule info is populated
    Given that the Requested Scheduling Information exists on the Final Exam Matrix
    And I create a Course Offering from copy in a term that uses the matrix and has an AO Driven final exam period defined
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should be populated






#    Given I create an Academic Calendar and add an official term
#    And I have created a Final Exam Period for the term in the newly created Academic Calendar
#    And I have created a Course Offering from catalog in the source term that uses the matrix and has a final exam period defined
#    And I have created an Activity Offering that only has Requested Scheduling Information
#    And I ensure that the Course Offering exists on the Final Exam Matrix
#    And I encure that the AO's Requested Scheduling Information exists on the Final Exam Matrix

