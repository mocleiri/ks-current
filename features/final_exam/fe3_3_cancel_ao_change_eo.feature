@nightly @blue_team
Feature: CO.FE3-3 Cancelling an AO should cancel the EO depending on all the exam settings
  FE 3.3 As a Central Administrator I want exam offerings to be Cancelled if the governing course offering or activity
  offering is cancelled after the bulk creation so that unnecessary exam offerings are removed

  Background:
    Given I am logged in as admin

  #FE3.3.EB1 (KSENROLL-10220)
  Scenario: Test that cancelling an AO does not effect the EOs when the FE Driver is set to CO
    Given there is more than one Activity Offering for the Course
    When I cancel an Activity Offering for a CO with a standard final exam driven by Course Offering
    And I view the Exam Offerings for the Course Offering
    Then the Exam Offerings for Course Offering in the EO for CO table should be in a Draft state

  #FE3.3.EB2 (KSENROLL-10220)
  Scenario: Test that cancelling a secondary AO does not cancel an existing EO in the EO for AO table
    Given that the Lecture AO that drives the exam is not in a cancelled state
    When I cancel a Discussion Activity Offering for a CO with a standard final exam driven by Activity Offering
    And I view the Exam Offerings for the Course Offering
    Then the Exam Offering for Activity Offering should not be in a Canceled state

  #FE3.3.EB3 (KSENROLL-10220)
  Scenario: Test that cancelling all AOs does cancel the EO when the FE Driver is set to CO
    When I cancel all Activity Offerings for a CO with a standard final exam driven by Course Offering
    And I view the Exam Offerings for the Course Offering
    Then the Exam Offering listed in the EO for CO table should be in a Canceled state

  #FE3.3.EB4 (KSENROLL-10220)
  Scenario: Test that cancelling all AOs does cancel the EO when the FE Driver is set to AO
    When I cancel all Activity Offerings for a CO with a standard final exam driven by Activity Offering
    And I view the Exam Offerings for the Course Offering
    Then the Exam Offerings for each Activity Offering in the EO for AO table should be in a Canceled state
