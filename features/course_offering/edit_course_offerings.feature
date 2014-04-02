@nightly @yellow_team
Feature: CO.Edit Course Offerings
  Background:
    Given I am logged in as a Schedule Coordinator

#KSENROLL-1499
  Scenario: Verify course offering edit page navigation
    When  I edit a course offering
    And I can return to manage course offering using the cancel button

#KSENROLL-9443
  Scenario: Edit the course offering information data for an existing course offering
    When I edit a course offering with multiple registration options and credit types
    And I clear the existing registration options
    And I change the credit type from multiple to fixed
    And I change the number of credits
    Then after I update the credit type, credit value and registration options reflect the changes

  Scenario: Edit an existing course offering's grading option
    When I edit a course offering with multiple grading options
    And I change the grading options
    Then the grading option is changed

#KSENROLL-1503.1
  Scenario: Edit an existing course offering to update the grade roster level
    Given I edit a course offering with multiple format types
    And I change the grade roster level value
    Then after I update the grade roster level reflects the changes

  Scenario: Edit an existing course offering and add and delete a delivery format line
    Given I edit a course offering with multiple format types
    And I add a delivery format option
    Then after I update the new delivery format offering is present
    When I delete the added delivery format offering
    Then after I update the added delivery format offering is not present

#KSENROLL-9263
  Scenario: Edit an existing course offering's waitlist option
    Given I edit a course offering with the waitlists active option selected
    And I deactivate waitlists
    Then after I update the course offering the waitlist option is updated
    Then I edit the same course offering
    And I activate waitlists
    Then after I update the course offering the waitlist option is updated

#KSENROLL-1505
  Scenario: Add personnel to an existing course offering
    When I edit a course offering
    And I add an entry to the personnel section
    Then after I update the course offering the new personnel is present

#KSENROLL-1506
  Scenario: Add an administering organization to a course offering
    When I edit a course offering
    And I add an administering organization to the course offering
    Then after I update the course offering the new administering organization is present

  Scenario: Verify the Honors Course setting is updated when I save and remain in Edit
    When I edit a course offering
    And I set the honors flag option
    And I save progress and remain on the Edit Course Offering page
    Then the honors flag is persisted when I view the Course Offering details

  Scenario: Verify the Honors Course setting is updated when I jump to a different course offering and choose Save
    When I edit a course offering
    And I set the honors flag option
    And I navigate to a different Course Offering and select save and continue when prompted
    Then the honors flag is persisted when I view the Course Offering details

  Scenario: Verify the Honors Course setting is not updated when I jump to a different course offering without saving
    When I edit a course offering
    And I set the honors flag option
    And I navigate to the next Course Offering and select continue without saving when prompted
    Then the honors flag is not persisted when I view the Course Offering details
