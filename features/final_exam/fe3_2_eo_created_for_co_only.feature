@nightly @blue_team
Feature: SA.FE3-2 View Exam Offerings for AO not created when no Activity Offerings are defined
  FE 3.2 As a Central Administrator I want to create CO driven exam offerings dynamically after the bulk exam offering
  creation so that new exam offerings will be created as new Course Offerings are created

  Background:
    Given I am logged in as admin

  #FE3.2.EB1 (KSENROLL-9534)
  Scenario: Test whether the EO table only shows EOs by AO for courses with the Exam Driver set to Activity when Activity Offerings exist for the CO
    When I view the Exam Offerings for a CO created from catalog with a standard final exam driven by Course Offering
    Then the Exam Offerings for Course Offering in the EO for CO table should be in a Draft state
    When I view the Exam Offerings after changing the Final Exam Driver to Activity Offering
    Then there should be no Exam Offering for Activity Offering table present
    And the Exam Offering listed in the EO for CO table should be in a Canceled state

  #FE3.2.EB2 (KSENROLL-9536)
  Scenario: Test whether the Exam Offering is initially in Draft state when the exam driver is set to Course Offering
    Given there is an exsiting CO with a Standard Final Exam option
    When I select Final Exam Per Course Offering as the Final Exam Driver and Update the Course Offering
    Then I should be able to select the View Exam Offerings link on the Manage CO page
    And see one Exam Offering for the Course Offering with a status of Draft

  #FE3.2.EB3 (KSENROLL-9536)
  Scenario: Test whether each Exam Offering is initially in Draft state when the exam driver is set to Activity Offering
    Given that Activity Offerings exist for the selected Course Offering
    When I select Final Exam Per Activity Offering as the Final Exam Driver and Update the Course Offering
    Then I should be able to select the View Exam Offerings link on the Manage CO page
    And see Exam Offerings for the each Activity Offering of the Course with a status of Draft

