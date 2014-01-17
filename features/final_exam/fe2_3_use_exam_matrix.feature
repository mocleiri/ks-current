@nightly @blue_team
Feature: SA.FE2-3 Use Exam Matrix or not
  FE 2.3 As a Central Administrator I want to identify if the exam offering(s) for a course offering will be
  scheduled according to a matrix or another means so that scheduling information for exam offerings can be
  appropriately populated

  Background:
    Given I am logged in as admin

  #FE2.3.EB1 (KSENROLL-9244)
  Scenario: Test whether the Use Exam Matrix checkbox is only available for Standard FE
    Given that the catalog version of the course is set to have a standard final exam
    When I create a Course Offering from catalog in a term with a final exam period
    Then the ability to access the Use Final Exam Matrix field should only be available for a course offering set to have a Standard Final Exam