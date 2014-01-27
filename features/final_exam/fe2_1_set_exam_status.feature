@nightly @blue_team
Feature: CO.FE2-1 Set Exam Status
  FE 2.1: As a Central Administrator I want to override the exam settings on a course offering inherited from the
  canonical version so that a final examination can be set to be offered or not in a particular course offering

  Background:
    Given I am logged in as admin

  #FE2.1.EB1 (KSENROLL-9242)
  Scenario: Test whether the Final Exam data for a course changes depending on the chosen Final Exam indicator when there will be no exam
    Given that the catalog version of the course is set to have a standard final exam
    When I create a Course Offering from catalog in a term with a final exam period
    And I change the Final Exam indicator from Standard Final Exam to Alternate Final Assessment or No Final Exam or Assessment
    Then the Final Exam Driver should not be Activity Offering or Course Offering
    And the Final Exam Driver Activity field should disappear

  #FE2.1.EB2 (KSENROLL-9242)
  Scenario: Test whether a warning is displayed on the edit CO page that exam status differs from catalog
    When I create a Course Offering from catalog with an alternate final assessment option
    And I edit the Course Offering to have a Standard Final Exam
    And I select a Final Exam Driver option from the drop-down
    And I return to the Edit Co page for the course after updating the change
    Then a warning about the FE on the Edit CO page is displayed stating "Course exam data differs from Catalog."

#FE2.1.EB3 (KSENROLL-9242)
  Scenario: Test whether the Final Exam data for a course changes depending on the chosen Final Exam indicator when there will be an exam
    Given that the catalog version of the course is set to have No final exam
    When I create a Course Offering from catalog in a term with a final exam period
    And I change the Final Exam indicator from No Final Exam or Assessment to Standard Final Exam
    Then the Final Exam Driver should allow the user to pick Activity Offering or Course Offering as the exam driver
    And the Final Exam Driver Activity field should appear if Activity Offering is selected as the exam driver

