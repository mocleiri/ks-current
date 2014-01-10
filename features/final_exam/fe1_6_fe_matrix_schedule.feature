@nightly @blue_team
Feature: SA.FE1-6 Manage the Final Exam Matrix so that FEs can be Scheduled in predetermined ways
  FE 1.6: As a Central Administrator I want to create and manage the final exam matrix so that final examinations
  can be scheduled in predetermined ways

  Background:
    Given I am logged in as admin

  #FE1.6.EB1 (KSENROLL-9796)
  Scenario: Test that after submitting to the DB the new rule still exists in the Standard FE section
    When I add a Standard Final Exam timeslot rule to the Final Exam Matrix
    Then I should be able to see the newly created timeslot rule in the Standard Final Exam table

  #FE1.6.EB2 (KSENROLL-9796)
  Scenario: Test that after submitting to the DB the new rule still exists in the Common FE section
    When I add a Common Final Exam course rule to the Final Exam Matrix
    Then I should be able to see the newly created course rule in the Common Final Exam table

  #FE1.6.EB3 (KSENROLL-9796)
  Scenario: Test that editing a previously created rule will be persisted to the DB
    Given I have added a Standard Final Exam timeslot rule to the Final Exam Matrix
    When I edit the Standard Final Exam rule
    Then I should be able to see the edited timeslot rule in the Standard Final Exam table

  #FE1.6.EB4 (KSENROLL-9796)
  Scenario: Test that editing a previously created Common Finals rule will be persisted to the DB
    Given I have added a Common Final Exam course rule to the Final Exam Matrix
    When I edit the Common Final Exam course rule
    Then I should be able to see the edited course rule in the Common Final Exam table

  #FE1.6.EB5 (KSENROLL-9796)
  Scenario: Test that the Days are sorted from beginning to end in the table
    Given there is an Academic Term associated with a Final Exam matrix
    When I view the Standard Final Exam rules on the Final Exam Matrix
    Then the rules should be sorted on the Days and Time columns

  #FE1.6.EB6 (KSENROLL-9796)
  Scenario: Test that a rule must have at least one rule statement for any Requested Exam Offering Scheduling Information
    Given I have a Standard Final Exam group with a rule statement in the Final Exam Matrix
    When I choose to edit the existing rule statement
    And I delete the statement and attempt to update the rule
    Then there should be a validation message displayed stating "Cannot 'Update Rule' until at least one rule statement has been added"

  #FE1.6.EB7 (KSENROLL-9796)
  Scenario: Test that multiple statements can be added to one rule on the Final Exam Matrix
    Given that I have a Final Exam Matrix with an existing Common Final Exam rule statement
    When I add additional statements to the Common Final Exam rule on the Final Exam Matrix
    Then I should be able to see the Common Final Exam rule with the multiple statements

  #FE1.6.EB8 (KSENROLL-9796)
  Scenario: Test that editing or adding rules and submitting multiple times works as expected
    Given I have a Final Exam Matrix to which I have added multiple Standard Final Exam rule statements
    When I edit the newly created Standard Final Exam rules
    Then I should be able to see all the changes I have made on the Final Exam Matrix

  #FE1.6.EB9 (KSENROLL-9796)
  Scenario: Test that deleting an existing rule works as expected
    When I delete an existing Standard Final Exam text rule to the Final Exam Matrix
    Then the deleted text rule should not exist on the Final Exam Matrix
