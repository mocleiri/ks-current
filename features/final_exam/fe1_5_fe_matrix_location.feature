@nightly @blue_team
Feature: SA.FE1-5 Manage the Final Exam Matrix so that FEs can be Scheduled with the location of the AO
  FE 1.5: As a Central Administrator I want to indicate with a global setting that activity offering locations
  should be used when scheduling exam offerings so that matrix scheduled exam offerings will include location in
  requested delivery logistics

  Background:
    Given I am logged in as admin

  #FE1.5.EB1 (KSENROLL-9798)
  Scenario: Test that the option to set the location is visible but not accessible
    Given there is an Academic Term associated with a Final Exam matrix
    When I view the term in the Manage Final Exam Matrix screen
    Then the Exam Location indicator should be visible
    But the user is not able to access the Exam Location indicator