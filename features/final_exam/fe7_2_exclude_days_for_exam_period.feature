@nightly @blue_team
Feature: CO.Exclude Non-Active Days for Scheduling of Exam Period
  FE 7.2: As a Central Administrator I want non active days within the exam period configured so that these days are
  not used in the scheduling of the Final Examination as exams are not written on Saturdays or Sundays

  Background:
    Given I am logged in as admin
    And I create an Academic Calendar and add an official term with no exam period

  #KSENROLL-9792
  Scenario: FE7.2.1 Test whether the exclude non-active day toggles are selected by default
    When I add an Exam Period to the term
    Then the Exclude Saturday and Exclude Sunday toggles should be selected by default

  #KSENROLL-9792
  Scenario: FE7.2.2 Test that when the non-active days are included and saved that it retains this setting when the term is viewed
    When I add an Exam Period to the term
    And I deselect Exclude Saturday and Exclude Sunday for the Exam Period
    Then the Exclude Saturday or Exclude Sunday fields should be deselected when view the term