@nightly @blue_team
Feature: SA.AZ FE2-4 Specific users should not be allowed to change the exam status
  FE 2.4: As a Central Administrator I want the system to be configured to allow only certain users to modify final
  exam settings so that only appropriate staff have the ability to override canonical or copied data

  Background:
    Given I am logged in as a Department Schedule Coordinator

  #FE2.4.EB1 (KSENROLL-9245)
  Scenario: Test that the Department Schedule Coordinator cannot change the final exam status while creating a course offering
    When I create a course offering for a subject with a standard final exam in my admin org
    Then I do not have access to the final exam status for the course offering from catalog

  #FE2.4.EB2 (KSENROLL-9245)
  Scenario: Test that the Department Schedule Coordinator cannot change the final exam status while editing a course offering
    When I edit a course offering with a standard final exm in my admin org
    Then I should not be able to update the status of the final exam period
