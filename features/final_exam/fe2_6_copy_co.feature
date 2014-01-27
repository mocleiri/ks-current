@nightly @blue_team
Feature: CO.FE2-6 Create CO with FE and then copy
  FE 2.6: As a Central Administrator I want exam information to be copied when creating a course offering from copy
  so that exam settings are retained session over session until modified

  Background:
    Given I am logged in as admin

  #FE2.6.EB1 (KSENROLL-9246)
  Scenario: Test whether the copied CO's FE data is the same as that of the original
    When I create a Course Offering from an existing Course Offering with a standard final exam option
    Then the exam data for the newly created course offering should match that of the original

  #FE2.6.EB2 (KSENROLL-9246) – New test
  Scenario: Test whether a warning is displayed on the edit CO page that exam status differs from catalog
    When I create a Course Offering from an existing Course Offering with an alternate final assessment option
    And I edit the Course Offering to have a Standard Final Exam
    And I select a Final Exam Driver option from the drop-down
    And I return to the Edit Co page for the course after updating the change
    Then a warning about the FE on the Edit CO page is displayed stating "Course exam data differs from Catalog."
