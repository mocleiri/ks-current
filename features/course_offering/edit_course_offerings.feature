@nightly @green_team
Feature: WC.Edit Course Offerings
  Background:
    Given I am logged in as a Schedule Coordinator

  #KSENROLL-1499
  Scenario: Verify course offering pages are accessible
    When I search course offerings by subject code
    Then I view the course offering details
    And I can edit the course offering
    And I can return to search using the cancel button

  #KSENROLL-9443
  Scenario: Edit an existing course offering changing the student registration options
    When I edit a course offering with multiple registration options
    And I clear the registration options checkboxes
    Then I can submit and the registration options are changed

  #KSENROLL-9443
  Scenario: Edit an existing course offering changing credit type and credit count
    When I edit a course offering with multiple credit options
    And I change the credit type from multiple to fixed
    And I change the number of credits
    Then I can submit and the credit options are changed

  #KSENROLL-9443
  Scenario: Edit an existing course offering changing credit count values
    When I edit a course offering with multiple credit options
    And I change the multiple credit values
    Then I can submit and the credit values are changed

  #KSENROLL-1503.1
  Scenario: Edit an existing course offering deactivating final examinations and update the grade roster level
    When I edit a course offering with multiple format types
    And I select a final exam type of "No final exam or assessment"
    And I change the delivery format options
    Then after I submit the course offering exam options and delivery format are updated

  #KSENROLL-1503.2
  @smoke_test
  Scenario: Edit an existing course offering activating final examinations and update the grade roster level
    When I edit a course offering with multiple format types
    And I select a final exam type of "Standard final Exam"
    And I change the delivery format options
    Then I can submit and the course offering is updated

  Scenario: Edit an existing course offering and add a delivery format line
    Given I edit a course offering with multiple delivery format types
    And I add a delivery format option
    Then I can submit and the delivery formats are updated

  Scenario: Edit an existing course offering and modify a delivery format line
    Given I edit a course offering with multiple delivery format types
    And I modify a delivery format option
    Then I can submit and the modified delivery formats are updated

  Scenario: Edit an existing course offering and delete a delivery format line
    Given I edit a course offering with multiple delivery format types
    And I add a delivery format option
    Then I can submit and the delivery formats are updated
    When I delete the added delivery format option
    Then I can submit and the added delivery format is not present

  #KSENROLL-9263
  Scenario: Edit an existing course offering's wait list options
    When I edit a course offering
    And I deactivate the wait list
    Then I can submit and the course offering is updated
    Then I edit the same course offering
    And I activate the wait list
    Then I can submit and the course offering is updated

  #KSENROLL-1505
  Scenario: Edit an existing course offering's affiliated personnel
    When I edit a course offering
    And I add an affiliated person
    Then I can submit and the course offering is updated
    And the changes of the affiliated person are persisted

  #KSENROLL-1506
  Scenario: Edit an existing course offering's administering organizations and honors flag
    When I edit a course offering
    And I add an administering organization and activate the honors flag
    Then I can submit and the course offering is updated

  # KSENROLL-2860/3022
  Scenario: Test that user is unable to manage course offerings when SOC is in certain states
    When I manually change a given soc-state to "Publishing"
    Then I verify that I "cannot" manage course offerings
    And I manually change a given soc-state to "In Progress"
    Then I verify that I "cannot" manage course offerings
    And I manually change a given soc-state to "Open"
    Then I verify that I "can" manage course offerings

  Scenario: Change Honors Course setting without submitting
    When I edit a course offering
    And I set the Honors Course selection
    And I save the changes and remain on the Edit CO page
    Then I can verify that the Honors Course setting is set

  Scenario: Change Honors Course setting and then save and jump to previous CO
    When I edit a course offering
    And I set the Honors Course selection
    And I jump to the previous CO while saving changes
    Then I can verify that the Honors Course setting is set

  Scenario: Change Honors Course setting and then save and jump to next CO
    When I edit a course offering
    And I set the Honors Course selection
    And I jump to the next CO while saving changes
    Then I can verify that the Honors Course setting is set

  Scenario: Change Honors Course setting and jump to previous CO without saving
    When I edit a course offering
    And I set the Honors Course selection
    And I jump to the previous CO while not saving changes
    Then I can verify that the Honors Course setting is not set

  Scenario:  Change Honors Course setting and jump to next CO without saving
    When I edit a course offering
    And I set the Honors Course selection
    And I jump to the next CO while not saving changes
    Then I can verify that the Honors Course setting is not set

  Scenario: Change Honors Course setting and jump to an arbitrary CO without saving
    When I edit a course offering
    And I set the Honors Course selection
    And I jump to an arbitrary CO while not saving changes
    Then I can verify that the Honors Course setting is not set

  Scenario: Change Honors Course setting and jump to an arbitrary CO
    When I edit a course offering
    And I set the Honors Course selection
    And I jump to an arbitrary CO while saving changes
    Then I can verify that the Honors Course setting is set

  Scenario: Edit an existing course offering's grading option
    When I edit a course offering with multiple grading options
    And I change the grading option
    Then I can verify that the grading option is changed

