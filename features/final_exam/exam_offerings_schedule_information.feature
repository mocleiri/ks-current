@nightly @blue_team
Feature: CO.Exam Offering Schedule Information
  FE4.7 As a Central Admin I want to slot RSIs for CO driven exams during Create from Catalog as part of the dynamic
  process so that exam offering scheduling requests are consistent with the established exam matrix

  FE4.8 As a Central Admin I want to slot RSIs for CO driven exams during create from Copy as part of the dynamic
  process so that exam offering scheduling requests are consistent with the established exam matrix

  FE4.9 As a Central Admin I want to slot RSIs for AO driven exams during create from copy as part of the dynamic
  process so that exam offering scheduling requests are consistent with the established exam matrix

  Background:
    Given I am logged in as admin

  #KSENROLL-12013
  Scenario: FE4.7.1 Test that when an on-matrix CO is created from catalog that the requested scheduling info is correctly populated on the EO
    Given that the Course Offering has a CO-driven final exam that is marked to use the matrix and exists on the Final Exam Matrix for the term
    And I create a Course Offering from catalog in a term with a defined final exam period that uses the FE matrix
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering should be populated

  #KSENROLL-12090
  Scenario: FE4.7. Test that when Course offering with CO-driven exam is created from catalog and is not present on the FE Matrix that the schedule info is not populated
    Given that the Course Offering has a CO-driven final exam that is marked to use the matrix but does not exist on the Final Exam Matrix for the term
    When I create a Course Offering from catalog in a term with a defined final exam period that uses the FE matrix
    Then there should be a warning message stating that "EO RSI data not populated, no valid entry found on the Matrix."
    And I view the Exam Offerings for the Course Offering
    And the Schedule Information for the Exam Offering should be blank

  #KSENROLL-12090
  Scenario: FE4.7.3 Test that when Course Offering with CO-driven exam is created from catalog and is present on the FE Matrix that the schedule info is populated
    Given that the Course Offering has a CO-driven final exam that is marked to use the matrix and exists on the Final Exam Matrix for the term
    And I create a Course Offering from catalog in a term with a defined final exam period that uses the FE matrix
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering should be populated

  #KSENROLL-12086
  Scenario: FE4.8.1 Test that when Course Offering with a CO-driven exam is created from copy and is not present on the FE Matrix that the schedule info is not populated
    Given that the Course Offering has a CO-driven final exam that is marked to use the matrix but does not exist on the Final Exam Matrix for the term
    When I create a Course Offering from copy in a term with a defined final exam period that uses the FE matrix
    Then there should be a warning message stating that "EO RSI data not populated, no valid entry found on the Matrix."
    And I view the Exam Offerings for the Course Offering
    And the Schedule Information for the Exam Offering should be blank

  #KSENROLL-12086
  Scenario: FE4.8.2 Test that when Course Offering with a CO-driven exam is created from copy and is present on the FE Matrix that the schedule info is populated
    Given that the Course Offering has a CO-driven final exam that is marked to use the matrix and exists on the Final Exam Matrix for the term
    And I create a Course Offering from copy in a term with a defined final exam period that uses the FE matrix
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering should be populated

  #KSENROLL-12087
  Scenario: FE4.9.1 Test that when Course Offering with an AO-driven exam is created from copy where AO has RSI then the Exam RSI data is populated
    Given that the Course Offering has an AO-driven exam that is marked to use the matrix, Requested Scheduling Information for the exam exists on the Final Exam Matrix, and the parent AO of the exam offering has RSI data
    When I create a Course Offering from copy in a term with a defined final exam period that uses the matrix
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should be populated

  #KSENROLL-12087
  Scenario: FE4.9.2 Test that when Course Offering witn an AO-driven exam is created from copy where AO has no RSI then the schedule info is not populated
    Given that the Course Offering has an AO-driven exam that is marked to use the matrix and Requested Scheduling Information for the exam does not exist on the Final Exam Matrix
    When I create a Course Offering from copy in a term with a defined final exam period that uses the matrix
    Then there should be a warning message stating that "EO RSI data not populated, no valid entry found on the Matrix."
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should not be populated

  #KSENROLL-12104
  Scenario: FE4.9.3 Test that when Course Offering with AO-driven exam is created from copy that the schedule info is not populated
    Given I create a Course Offering with an AO-driven exam from catalog in a term with a defined final exam period
    And I create an Activity Offering that has no ASIs or RSIs
    When I create a copy of the initial course offering in a term that uses the FE matrix and has defined final exam period
    Then there should be a warning message stating that "EO RSI data not populated, no valid entry found on the Matrix."
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should not be populated

  #KSENROLL-12105
  Scenario: FE4.9.4 Test that when a Course Offering with an AO-driven exam that has one AO with RSI data that exists on the FE matrix is copied that the EO RSI is populated
    Given that the Course Offering has one Activity Offering with Requested Scheduling Information that exists on the Final Exam Matrix
    And I create a copy of the initial course offering in a term that uses the FE matrix and has defined final exam period
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should be populated

  #KSENROLL-12105
  Scenario: FE4.9.5 Test that when a Course Offering with an AO-driven exam that has one AO with RSI data that does not exist on the FE matrix is copied that the EO RSI is not populated
    Given that the Course Offering has one Activity Offering with Requested Scheduling Information that does not exist on the Final Exam Matrix
    And I create a copy of the initial course offering in a term that uses the FE matrix and has defined final exam period
    When I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should not be populated




#    Given I create an Academic Calendar and add an official term
#    And I have created a Final Exam Period for the term in the newly created Academic Calendar
#    And I have created a Course Offering from catalog in the source term that uses the matrix and has a final exam period defined
#    And I have created an Activity Offering that only has Requested Scheduling Information
#    And I ensure that the Course Offering exists on the Final Exam Matrix
#    And I encure that the AO's Requested Scheduling Information exists on the Final Exam Matrix

