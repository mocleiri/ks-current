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
    Given I manage a course offering with a CO-driven exam offering RSI generated from the exam matrix
    When I manage the exam offerings for the course offering
    And update all fields on the exam offering RSI
    Then the exam offering RSI is successfully updated

  Scenario: FE 6.3.2 Verify successful Exam Offering RSI inline edit for an AO-driven Exam Offering
    Given I manage a course offering with an AO-driven exam offering RSI generated from the exam matrix
    When I manage the exam offerings for the course offering
    And update all fields on the exam offering RSI
    Then the exam offering RSI is successfully updated

  Scenario: FE 6.3.3 Verify that Exam Offering RSI facility and room fields can be set to blank
    Given I manage a course offering with an AO-driven exam offering RSI generated from the exam matrix
    When I manage the exam offerings for the course offering
    And delete the contents of the exam offering RSI facility and room number fields
    Then the exam offering RSI is successfully updated

  Scenario: FE 6.3.3 Verify Exam Offering RSI edit error message for invalid (blank) day
    Given I manage a course offering with an AO-driven exam offering RSI generated from the exam matrix
    When I manage the exam offerings for the course offering
    And blank the exam offering RSI Day field
    Then the error displayed for exam offerings RSI day field is: Days, start time and end time fields are required
    And the exam offering RSI is not updated

  Scenario: FE 6.3.3 Verify Exam Offering RSI edit error message for invalid start time
    Given I manage a course offering with a CO-driven exam offering RSI generated from the exam matrix
    When I manage the exam offerings for the course offering
    And enter an invalid date in the exam offering RSI start time field
    Then the error displayed for exam offerings RSI start time is: Valid time format hh:mm AM or hh:mm PM
    And the exam offering RSI is not updated

  Scenario: FE 6.3.3 Verify Exam Offering RSI edit error message for invalid end time
    Given I manage a course offering with an AO-driven exam offering RSI generated from the exam matrix
    When I manage the exam offerings for the course offering
    And enter a blank date in the exam offering RSI end time field
    Then the error displayed for exam offerings RSI end time is: Required
    And the exam offering RSI is not updated

  Scenario: FE 6.3.3 Verify Exam Offering RSI edit error message for invalid facility
    Given I manage a course offering with an AO-driven exam offering RSI generated from the exam matrix
    When I manage the exam offerings for the course offering
    And enter an invalid facility code in the exam offering RSI facility field
    Then the error displayed for exam offerings RSI facility is: Building code is invalid
    And the exam offering RSI is not updated

  Scenario: FE 6.3.3 Verify Exam Offering RSI edit error message for invalid room
    Given I manage a course offering with an AO-driven exam offering RSI generated from the exam matrix
    When I manage the exam offerings for the course offering
    And enter an invalid facility code in the exam offering RSI room field
    Then the error displayed for exam offerings RSI room is: Room code is invalid
    And the exam offering RSI is not updated

  Scenario: FE 6.3. Verify that the matrix override option is not present for a course not using the exam matrix
    Given I manage a course offering configured not to use the exam matrix that has an CO-driven exam
    When I manage the exam offerings for the course offering
    And update all fields on the exam offering RSI
    Then the exam offering RSI is successfully updated

  Scenario: FE 6.3.2 Verify successful Exam Offering RSI inline edit for an AO-driven Exam Offering not using the exam matrix
    Given I manage a course offering configured not to use the exam matrix that has an AO-driven exam
    When I manage the exam offerings for the course offering
    And update all fields on the exam offering RSI
    Then the exam offering RSI is successfully updated

    #don't think this is valid anymore
  Scenario: FE 6.3. Verify that the matrix override is automatically marked when day and/or time changes to the Exam Offering RSI are saved

  Scenario: FE 6.3. AO-driven verify that when the matrix override option is selected then updates to the activity offering RSI do not change the EO RSI
    Given I manage a course offering with an AO-driven exam offering RSI generated from the exam matrix
    And I manage the exam offerings for the course offering
    And I select matrix override and update the day and time fields on the exam offering RSI
    When I update the requested scheduling information for the related activity offering
    Then the exam offering RSI is not changed

  Scenario: FE 6.3. AO-driven verify that when the matrix override option is selected then updates to the AO ASI do not change the EO RSI
    Given I am working on a term in "Final Edits" SOC state
    And I manage an course offering in offered status with an AO-driven exam offering RSI generated from the exam matrix
    And I manage the exam offerings for the course offering
    And I select matrix override and update the day and time fields on the exam offering RSI
    When I update the scheduling information for the related activity offering and send to the scheduler
    Then the exam offering RSI is not changed

  #this needs to go in AZ feature file
  Scenario: FE 6.4.2 DSC (Carol) will be configured to have read-only permission on EO management screen
    Given I am logged in as a Department Schedule Coordinator
    And I manage a course offering with an AO-driven exam in my admin org
    When I manage exam offerings for the course offering
    Then I am not able to edit the exam offering RSI

  Scenario: FE 6.8.1 Successfully add facility and room information to a CO-driven Exam Offering RSI when exam matrix facility and room info is blank
    Given I manage a course offering with a CO-driven exam offering RSI generated from the exam matrix where facility and room info are blank
    When I manage the exam offerings for the course offering
    And add facility and room information to the exam offering RSI
    Then the exam offering RSI is successfully updated