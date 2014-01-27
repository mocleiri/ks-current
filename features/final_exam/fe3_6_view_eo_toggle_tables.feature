@nightly @blue_team
Feature: CO.FE3-6 View Exam Offerings table renedered depending on FE Driver
  FE 3.6 As a Central Administrator I want new exam offerings to be created if the exam driver changes after the
  bulk creation so that exam offerings are in sync with their drivers

  Background:
    Given I am logged in as admin

  #FE3.6.EB1 (KSENROLL-9790)
  Scenario: Verify that when the exam driver changes the correct EOs exist in the View EO table
    Given that the SOC state is prior to Published
    When I view the Exam Offerings for a CO with a standard final exam driven by Course Offering
    Then the Exam Offerings for Course Offering in the EO for CO table should be in a Draft state
    When I view the Exam Offerings after changing the Final Exam Driver to Activity Offering
    Then the Exam Offerings for each Activity Offering in the EO for AO table should be in a Draft state
    And there should be no Exam Offering for Course Offering table present
    When I view the Exam Offerings after changing the Final Exam Driver back to Course Offering
    Then the Exam Offerings for Course Offering in the EO for CO table should be in a Draft state
    And there should be no Exam Offering for Activity Offering table present

  #FE3.6.EB2 (KSENROLL-9876)
  Scenario: Verify that Cos with multiple Format Offerings have only one Exam Offering when the exam driver is by Course Offering
    Given that a CO allows for multiple Format Offerings and has one existing format offering and a standard exam driven by Course Offering
    When I edit the CO to add a second Format Offering
    And I view the Exam Offerings for the Course Offering
    Then there should only be one EO in the Exam Offerings for Course Offering table
