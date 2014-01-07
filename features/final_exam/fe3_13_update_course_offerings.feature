@nightly @blue_team
Feature: SA.FE3-13 Update Exam Offerings
  FE 3-13 As a Central Admin I want existing exam offerings to be Cancelled if the CO setting changes
  to "No final Exam" or "Alternative final assessment" after bulk creation and after the SoC is published so that exam
  offerings match CO exam settings

  Background:
    Given I am logged in as admin

  #FE3.13.EB1(KSENROLL-9801)
  Scenario: Modify exam setting for Course Offering with Exam Driver to CO and confirm Exam Offering is in a Cancelled state.
    Given that the CO is set to have exam offerings driven by CO
    When I view the Exam Offerings for a CO where the Course Offering Standard FE setting is changed to No Final Exam
    Then the EO in the Exam Offering by Course Offering table should be in a Canceled state

  #FE3.13.EB2(KSENROLL-9801)
  Scenario: Modify exam setting for Course Offering with Exam Driver to CO and confirm Exam Offering is in a Draft state
    Given that the CO is set to have no exam offerings
    When I view the Exam Offerings for a CO where the Course Offering No Standard Final Exam or Assessment is changed to Standard Final Exam driven by Course Offering
    Then the EO in the Exam Offerings for Course Offering table should be in a Draft state

  #FE3.13.EB3(KSENROLL-9801)
  Scenario: Modify exam setting for Course Offering with Exam Driver to AO and confirm Exam Offering is in a Cancelled state.
    Given that the CO is set to have exam offerings driven by AO
    When I view the Exam Offerings for a CO where the Course Offering Standard FE setting is changed to No Final Exam
    Then the EO in the Exam Offering by Activity Offering table should be in a Canceled state

  #FE3.13.EB4(KSENROLL-9801)
  Scenario: Modify exam setting for Course Offering with Exam Driver to AO and confirm Exam Offerings are in a Draft state
    Given that the CO is set to have exam offerings driven by AO
    When I view the Exam Offerings for a CO where the exam is changed to Standard Final Exam driven by Activity Offering
    Then the EOs in the Exam Offerings for Activity Offering table should be in a Draft state
