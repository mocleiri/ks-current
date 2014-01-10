@nightly @blue_team
Feature: SA.AZ FE1-7 Specific users should not be allowed to change the final exam matrix
  FE 1.7: As a Departmental or Central Administrator I want appropriate permissions in order to view or manage
  existing Final Exam matrices so that I can know how examinations will be scheduled

  #FE1.7.EB1 (KSENROLL-9800)
  Scenario: Test that the Department Schedule Coordinator can only view the Final Exam Matrix
    Given I am logged in as a Department Schedule Coordinator
    When I open the Final Exam Matrix for Fall Term
    Then I can only view all the rules in the Final Exam Matrix
    And I cannot add a new rule to the Final Exam Matrix

  #FE1.7.EB2 (KSENROLL-9800)
  Scenario: Test that the Schedule Coordinator has the option to edit and delete rules on the Final Exam Matrix
    Given I am logged in as a Schedule Coordinator
    When I open the Final Exam Matrix for Fall Term
    Then I have the option of editing or deleting rules in the Final Exam Matrix
    And I have the option to add a new rule to the Final Exam Matrix

  #FE1.7.EB3 (KSENROLL-9800)
  Scenario: Test that the Administrator has the option to edit and delete rules on the Final Exam Matrix
    Given I am logged in as admin
    When I open the Final Exam Matrix for Fall Term
    Then I have the option of editing or deleting rules in the Final Exam Matrix
    And I have the option to add a new rule to the Final Exam Matrix
