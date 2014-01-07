@nightly @blue_team
Feature: SA.FE7-2 Exclude Non-Active Days for Scheduling of Exam Period
  FE 7.2: As a Central Administrator I want non active days within the exam period configured so that these days are
  not used in the scheduling of the Final Examination as exams are not written on Saturdays or Sundays

  Background:
    Given I am logged in as admin
    And I create an Academic Calendar and add an official term

  #FE7.2.EB1 (KSENROLL-9792)
  Scenario: Test whether the exclude non-active day toggles are selected by default
    When I add an Exam Period to the term
    Then the Exclude Saturday and Exclude Sunday toggles should be selected by default

  #FE7.2.EB2 (KSENROLL-9792)
  Scenario: Test that when the non-active days are included and saved, that it retains this setting when the term is viewed
    When I uncheck the toggle for the Exclude Saturday or Exclude Sunday fields in the term's Exam Period and Save the data
    Then the Exclude Saturday or Exclude Sunday fields should be unchecked and included in the Exam Period when I return to view the term