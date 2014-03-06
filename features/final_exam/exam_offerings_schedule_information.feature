@nightly @blue_team
Feature: CO.Exam Offering Schedule Information
  FE 4.2: As a Central Adminstrator I want to have on matrix exam offerings populated with requested delivery logistics
  when they are created by dynamic processes so that exam offering scheduling requests are consistent with the
  established exam matrix

  KSENROLL-12096-FE4.x(1) As a Central Admin I want to schedule CO driven exams during the Create from Catalog as part of the dynamic
  process so that exam offering scheduling requests are consistent with the established exam matrix

  KSENROLL-12097-FE4.x(3) As a Central Admin I want to slot RSIs for CO driven exams during the create from Copy as part of the dynamic
  process so that exam offering scheduling requests are consistent with the established exam matrix

  KSENROLL-12098-FE4.x(2) As a Central Admin I want to schedule AO driven exams during create from copy as part of the dynamic process
  so that exam offering scheduling requests are consistent with the established exam matrix

  Background:
    Given I am logged in as admin

  #KSENROLL-12013
  Scenario: FE4.2.1 Test that when an on-matrix CO is created from catalog that the requested scheduling info is correctly populated on the EO
    Given that the Course Offering has a CO-driven final exam that is marked to use the matrix and exists on the Final Exam Matrix for the term
    And I create a Course Offering from catalog in a term with a defined final exam period that uses the FE matrix
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering should be populated

  #KSENROLL-12086
  Scenario: FE4.x(3).1 Test that when Course Offering with a CO-driven exam is created from copy and is not present on the FE Matrix that the schedule info is not populated
    Given that the Course Offering has a CO-driven final exam that is marked to use the matrix but does not exist on the Final Exam Matrix for the term
    When I create a Course Offering from copy in a term with a defined final exam period that uses the FE matrix
    Then there should be a warning message stating that "EO RSI data not populated, no valid entry found on the Matrix." when the system attempts to assign RSI data to the newly created exam offering
    And I view the Exam Offerings for the Course Offering
    And the Schedule Information for the Exam Offering should be blank

  #KSENROLL-12086
  Scenario: FE4.x(3).2 Test that when Course Offering with a CO-driven exam is created from copy and is present on the FE Matrix that the schedule info is populated
    Given that the Course Offering has a CO-driven final exam that is marked to use the matrix and exists on the Final Exam Matrix for the term
    And I create a Course Offering from copy in a term with a defined final exam period that uses the FE matrix
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering should be populated

  #KSENROLL-12090
  Scenario: FE4.x(1).1 Test that when Course offering with CO-driven exam is created from catalog and is not present on the FE Matrix that the schedule info is not populated
    Given that the Course Offering has a CO-driven final exam that is marked to use the matrix but does not exist on the Final Exam Matrix for the term
    When I create a Course Offering from catalog in a term with a defined final exam period that uses the FE matrix
    Then there should be a warning message stating that "EO RSI data not populated, no valid entry found on the Matrix." when the system attempts to assign RSI data to the newly created exam offering
    And I view the Exam Offerings for the Course Offering
    And the Schedule Information for the Exam Offering should be blank

  #KSENROLL-12090
  Scenario: FE4.x(1).2 Test that when Course Offering with CO-driven exam is created from catalog and is present on the FE Matrix that the schedule info is populated
    Given that the Course Offering has a CO-driven final exam that is marked to use the matrix and exists on the Final Exam Matrix for the term
    And I create a Course Offering from catalog in a term with a defined final exam period that uses the FE matrix
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering should be populated

  #KSENROLL-12087
  @draft
  Scenario: FE4.x(2).1 Test that when CO created from copy with exam period that is AO Driven then the schedule info is populated
    Given that the Requested Scheduling Information exists on the Final Exam Matrix
    And I create a Course Offering from copy in a term that uses the matrix and has an AO Driven final exam period defined
    And I edit the Exam Period of the CO to be AO Driven and to have Discussion set as exam activity
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should be populated

  #KSENROLL-12087
  @draft
  Scenario: FE4.x(2).2 Test that when CO created from copy with exam period that is AO Driven where AO has no RSI then the schedule info is populated
    Given that the Requested Scheduling Information does not exist on the Final Exam Matrix
    When I create a Course Offering from copy in a term that uses the matrix and has an AO Driven final exam period defined
    And I edit the Exam Period of the CO to be AO Driven and to have Discussion set as exam activity
    Then there should be a warning message stating that "No match found on the Matrix." when the system attempts to assign RSI data to the newly created exam offering
    And I view the Exam Offerings for the Course Offering
    And the Requested Scheduling Information for the Exam Offering of the AO should not be populated

  #KSENROLL-12104
  @draft
  Scenario: FE4.x(2).3 Test that when CO created from copy with exam period that is AO Driven and that AO has no scheduling info
    Given I create a Course Offering from catalog in a term that has a final exam period defined
    And I create an Activity Offering that has no ASIs or RSIs
    When I create a Course Offering from copy in a term that uses the matrix and has an AO Driven final exam period defined
    Then there should be a warning message stating that "No match found on the Matrix." when the system attempts to assign RSI data to the newly created exam offering
    And I view the Exam Offerings for the Course Offering
    And the Requested Scheduling Information for the Exam Offering of the AO should not be populated

  #KSENROLL-12105
  @draft
  Scenario: FE4.x(2).4 Test that when copied CO has one AO and the RSI exists on the matrix that the EO info is populated
    Given that the Requested Scheduling Information for the CO with one Activity Offering exists on the Final Exam Matrix
    And I create a Course Offering from copy in a term that uses the matrix and has an AO Driven final exam period defined
    And I edit the Exam Period of the CO to be AO Driven and to have Lecture set as exam activity
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should be populated

  #KSENROLL-12105
  @draft
  Scenario: FE4.x(2).5 Test that when copied CO has one AO and the RSI does not exist on the matrix that the EO info is not populated
    Given that the Requested Scheduling Information for the CO with one Activity Offering does not exist on the Final Exam Matrix
    And I create a Course Offering from copy in a term that uses the matrix and has an AO Driven final exam period defined
    And I edit the Exam Period of the CO to be AO Driven and to have Lecture set as exam activity
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should not be populated




#    Given I create an Academic Calendar and add an official term
#    And I have created a Final Exam Period for the term in the newly created Academic Calendar
#    And I have created a Course Offering from catalog in the source term that uses the matrix and has a final exam period defined
#    And I have created an Activity Offering that only has Requested Scheduling Information
#    And I ensure that the Course Offering exists on the Final Exam Matrix
#    And I encure that the AO's Requested Scheduling Information exists on the Final Exam Matrix

