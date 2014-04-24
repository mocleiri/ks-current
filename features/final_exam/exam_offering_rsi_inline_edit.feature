@draft @yellow_team
Feature: CO.Exam Offering RSI Inline Edit

  FE 6.3: As a Central Administrator I want to update the requested scheduling information for AO driven exam offerings with a
  scheduling state of Matrix Error so that AOs with non standard times will have exam offerings with RSI

  FE 6.4: As a Central Administrator I want the system to enforce appropriate authorization to add or change RSI information for exam offerings

  FE 6.7: As a Central Administrator I want to override the day and time of an exam slotted by the matrix so that scheduling data
  that is preferred to that derived from the matrix is sent to the scheduler

  FE 6.8: As a Central Administrator I want to add a location to the requested scheduling information of an on matrix CO driven exam offering
  if the location is not part of the matrix so that the preferred examination site is sent to the scheduler

  Background:
    Given I am logged in as a Schedule Coordinator

  Scenario: FE 6.3.1 Verify successful Exam Offering RSI inline edit for a CO-driven Exam Offering
    Given I manage a CO-driven exam offering with RSI generated from the exam matrix
    When I update all fields on the exam offering RSI
    Then the CO-driven exam offering RSI is successfully updated

  Scenario: FE 6.3.2 Verify successful Exam Offering RSI inline edit for an AO-driven Exam Offering
    Given I manage an AO-driven exam offering with RSI generated from the exam matrix
    And update all fields on the exam offering RSI
    Then the AO-driven exam offering RSI is successfully updated

  Scenario: FE 6.3.3 Verify that Exam Offering RSI facility and room fields can be set to blank
    Given I manage a CO-driven exam offering with RSI generated from the exam matrix
    And delete the contents of the exam offering RSI facility and room number fields
    Then the CO-driven exam offering RSI is successfully updated

  Scenario: FE 6.3.4 Verify Exam Offering RSI edit error message for invalid (blank) day
    Given I manage an AO-driven exam offering with RSI generated from the exam matrix
    And blank the exam offering RSI Day field
    Then the error displayed for AO-driven exam offerings RSI day field is: Days, start time and end time fields are required
    And the AO-driven exam offering RSI is not updated

  Scenario: FE 6.3.5A Verify Exam Offering RSI edit error message for invalid start time
    Given I manage a CO-driven exam offering with RSI generated from the exam matrix
    And enter an invalid time in the exam offering RSI start time field
    Then the error displayed for CO-driven exam offerings RSI start time is: Start Time is invalid
    And the CO-driven exam offering RSI is not updated

  Scenario: FE 6.3.5B Verify Exam Offering RSI edit error message for invalid end time
    Given I manage an AO-driven exam offering with RSI generated from the exam matrix
    And enter a blank time in the exam offering RSI end time field
    Then the error displayed for AO-driven exam offerings RSI end time is: Days, start time and end time fields are required
    And the AO-driven exam offering RSI is not updated

  Scenario: FE 6.3.5C Verify Exam Offering RSI edit error message for invalid facility
    Given I manage an AO-driven exam offering with RSI generated from the exam matrix
    And enter an invalid facility code in the exam offering RSI facility field
    Then the error displayed for AO-driven exam offerings RSI facility is: Building code is invalid
    And the AO-driven exam offering RSI is not updated

  Scenario: FE 6.3.5D Verify Exam Offering RSI edit error message for invalid room
    Given I manage an AO-driven exam offering with RSI generated from the exam matrix
    And enter an invalid room code in the exam offering RSI room field
    Then the error displayed for AO-driven exam offerings RSI room is: Room code is invalid
    And the AO-driven exam offering RSI is not updated

  Scenario: FE 6.3.6 Verify that the matrix override option is not present for a course not using the exam matrix
    Given I manage CO-driven exam offerings for a course offering configured not to use the exam matrix
    Then the Override Matrix field should not be present

  Scenario: FE 6.3.7 Verify successful Exam Offering RSI inline edit for an AO-driven Exam Offering not using the exam matrix
    Given I manage AO-driven exam offerings for a course offering configured not to use the exam matrix
    And I update all fields on the exam offering RSI
    Then the AO-driven exam offering RSI is successfully updated

  Scenario: FE 6.7.1 AO-driven verify that when the matrix override option is selected then updates to the activity offering RSI do not change the EO RSI
    Given I manage an AO-driven exam offering with RSI generated from the exam matrix
    When I select matrix override and update the day and time fields on the exam offering RSI
    And I update the requested scheduling information for the related activity offering so there is no match on the exam matrix
    Then the AO-driven exam offering RSI is not updated

  Scenario: FE 6.7.2 AO-driven verify that when the matrix override option is selected then updates to the AO ASI do not change the EO RSI
    Given I manage an AO-driven exam offering with RSI generated from the exam matrix in a term in "Published" SOC state
    And I select matrix override and update the day and time fields on the exam offering RSI
    When I update the scheduling information for the related activity offering and send to the scheduler
    Then the AO-driven exam offering RSI is not updated

  Scenario: FE 6.7.3 AO-driven verify that when the matrix override option is NOT selected then updates to the activity offering RSI change the EO RSI
    Given I manage an AO-driven exam offering with RSI generated from the exam matrix
    And I update the day and time fields on the exam offering RSI
    When I update the requested scheduling information for the related activity offering so there is no match on the exam matrix
    Then the exam offering RSI is blank according to the new AO RSI information

  Scenario: FE 6.7.4 Verify that when the matrix override option removed then the EO RSI updates to the exam matrix value
    Given I manage a CO-driven exam offering with RSI generated from the exam matrix
    And I select matrix override and update the day and time fields on the exam offering RSI
    When I subsequently remove matrix override from the exam offering RSI
    Then the CO-driven exam offering RSI is updated according to the exam matrix

#this needs to go in AZ feature file
  Scenario: FE 6.4.2 DSC (Carol) will be configured to have read-only permission on EO management screen
    Given I am logged in as a Department Schedule Coordinator
    And I manage an AO-driven exam offering for a course offering in my admin org
    Then I am not able to edit the AO-driven exam offering RSI

  Scenario: FE 6.8.1 Successfully add facility and room information to a CO-driven Exam Offering RSI when exam matrix facility and room info is blank
    Given I manage a course offering with a CO-driven exam offering with RSI generated from the exam matrix where facility and room info are blank
    When I manage the Exam Offerings for the Course Offering
    And add facility and room information to the exam offering RSI
    Then the CO-driven exam offering RSI is successfully updated