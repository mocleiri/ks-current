@nightly @blue_team
Feature: CO.Exam Offerings change when Modifying Schedule Information
  FE 5.2 As a Central Administrator I want exam offering requested scheduling information to be updated dynamically
  when I change AO ASIs so that scheduling information remains in line with those of the AO driving the exam and matrix slotting

  FE 4.10 As a Central Administrator I want CO Driven exam offering requested scheduling information to be populated
  from the matrix if the matrix setting for the Course Offering has changed from off to on matrix

  FE 4.11 As a Central Administrator I want AO Driven exam offering requested scheduling information to be populated
  from the matrix if the matrix setting for the Course Offering has changed from off to on matrix

  Background:
    Given I am logged in as admin

  #KSENROLL-12381
  Scenario: FE5.2.1 When I send the edited RSI to the scheduler to update the ASI then the EO data should be updated
    Given that the Course Offering has an AO-driven exam that is marked to use the matrix and Actual Scheduling Information for the exam does exist
    When I update the Actual Scheduling Information for the Activity Offering
    And I view the Exam Offerings for the Course Offering
    Then the EO's Scheduling Information should change to reflect the updates made to the AO's Actual Scheduling Info

  #KSENROLL-12381
  Scenario: FE5.2.2 When I send the edited AO RSI to the scheduler to update the AO ASI with data not on the Matrix then the EO scheduling info should be blank
    Given that the Course Offering has an AO-driven exam that is marked to use the matrix and Actual Scheduling Information for the exam does exist
    When I update the Actual Scheduling Information for the Activity Offering which does not match the Exam Matrix
    And I view the Exam Offerings for the Course Offering
    Then the EO's Scheduling Information for the Exam Offering of the AO should be updated to blank to reflect it was not found on the matrix

  #KSENROLL-12380
  Scenario: FE4.10.1 When the Exam is updated from Alternate Exam to Standard while the CO is not on the matrix then the EO data should not be populated
    Given I create a Course Offering from catalog with an Alternate Exam that is not found on the matrix in a term with a defined final exam period
    When I edit the Course Offering to use a Standard Exam that is CO-Driven
    And I view the Exam Offerings for the Course Offering
    Then the Schedule Information for the Exam Offering should be blank

  #KSENROLL-12380
  Scenario: FE4.10.2 When the Exam is updated from No Exam to Standard while the CO is on the matrix then the EO data should be populated
    Given I create a Course Offering from catalog with No Exam that is found on the matrix in a term with a defined final exam period
    When I edit the Course Offering to use a Standard Exam that is CO-Driven
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering should be populated

  #KSENROLL-12378
  Scenario: FE4.11.1 When the Exam is updated from No Exam to a Standard AO-Driven exam while the RSI of the AO is on the matrix then the EO data should be populated
    Given I create a Course Offering from catalog with No Exam that has an AO with RSI data found on the matrix in a term with a defined final exam period
    When I edit the Course Offering to use a Standard Exam that is AO-Driven
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should be populated

  #KSENROLL-12378
  Scenario: FE4.11.2 When the Exam is updated from Alternate Exam to a Standard AO-Driven exam while the RSI of the AO is not on the matrix then the EO data should not be populated
    Given I create a Course Offering from catalog with an Alternate Exam that has an AO with RSI data not found on the matrix in a term with a defined final exam period
    When I edit the Course Offering to use a Standard Exam that is AO-Driven
    Then there should be a warning message for the AO stating that "No match found on the Exam Matrix."
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should not be populated

  #KSENROLL-12377
  Scenario: FE4.11.3 When the Exam is updated from Alternate Exam to a Standard AO-Driven exam while the AO has no RSI and ASI then the EO slotting  data should not be populated
    Given I create a Course Offering from catalog with an Alternate Exam that has an AO with no RSI or ASI data
    And I create an Activity Offering that has no ASIs or RSIs
    When I edit the Course Offering to use a Standard Exam that is AO-Driven
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should not be populated

  #KSENROLL-12377
  Scenario: FE4.11.4 When the Exam is updated from No Exam to a Standard AO-Driven exam while the AO has no RSI and ASI then the EO slotting  data should not be populated
    Given I create a Course Offering from catalog with No Exam that has an AO with no RSI or ASI data
    And I create an Activity Offering that has no ASIs or RSIs
    When I edit the Course Offering to use a Standard Exam that is AO-Driven
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should not be populated

  #KSENROLL-12379
  Scenario: FE4.11.5 When the Exam is updated from No Exam to a Standard AO-Driven exam while the ASI of the AO is on the matrix then the EO data should be populated
    Given I create a Course Offering from catalog with No Exam that has an AO with ASI data found on the matrix in a term with a defined final exam period
    When I edit the Course Offering to use a Standard Exam that is AO-Driven
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should be populated

  #KSENROLL-12379
  Scenario: FE4.11.6 When the Exam is updated from Alternate Exam to a Standard AO-Driven exam while the ASI of the AO is not on the matrix then the EO data should not be populated
    Given I create a Course Offering from catalog with an Alternate Exam that has an AO with ASI data not found on the matrix in a term with a defined final exam period
    When I edit the Course Offering to use a Standard Exam that is AO-Driven
    Then there should be a warning message for the AO stating that "No match found on the Exam Matrix."
    And I view the Exam Offerings for the Course Offering
    Then the Requested Scheduling Information for the Exam Offering of the AO should not be populated