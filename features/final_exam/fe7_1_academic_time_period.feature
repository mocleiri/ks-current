@nightly @blue_team
Feature: SA.FE7-1 Academic Time Period
  FE 7.1: As a Central Administrator I want a final exam Academic Time Period associated with a term so that final
  examinations for the term can be scheduled and offered

  Background:
    Given I am logged in as admin

  #FE7.1.EB1 (KSENROLL-8829)
  Scenario: Test whether an error message is given when the exam period start date is changed to before term start date
    When I change the final exam period start date to be before the term start date and save
    Then a warning in the Final Exam Period section is displayed stating "Final Exam Period doesn't fall within the dates of Fall 2012."

  #FE7.1.EB2 (KSENROLL-8829)
  Scenario: Test whether an error message is given when the exam period end date is changed to after term end date
    When I change the final exam period end date to be after the term end date and save
    Then a warning in the Final Exam Period section is displayed stating "Final Exam Period doesn't fall within the dates of Fall 2012."

  #FE7.1.EB3 (KSENROLL-8829)
  Scenario: Test whether a final exam period can be added to a newly created academic calendar
    When I add a final exam period to the new academic calender and save
    Then the final exam period for the Fall Term is listed when I view the Academic Calendar

  #FE7.1.EB4 (KSENROLL-8830)
  Scenario: Test whether the copied calendar's exam period is blank
    When I copy a newly created academic calendar that has a defined final exam period
    Then there should be no final exam period for any term in the copy

  #FE7.1.EB5 (KSENROLL-8830)
  Scenario: Test whether the exam period is blank when copying from an existing calendar
    When I copy an existing academic calendar that has a defined final exam period
    Then there should be no final exam period for any term in the copy