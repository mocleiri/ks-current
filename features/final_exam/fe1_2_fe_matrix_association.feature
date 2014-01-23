@nightly @blue_team
Feature: CO.FE1-2 Associate Final Exam Matrix with more than one term
  FE 1.2: As a Central Administrator I want to associate a matrix with one or more term types so that a matrix
  specific to a time of year or to an organization is matched to the appropriate term or subterm

  Background:
    Given I am logged in as admin

  #FE1.2.EB1 (KSENROLL-9797)
  Scenario: Test that the options from which to choose the association of the FE Matrix already has a matrix
    Given there is an Academic Half Term that is not associated with any final exam matrix
    When I view the half term
    Then I should have a choice of terms from which to associate the Final Exam Matrix

  #FE1.2.EB2 (KSENROLL-9797)
  Scenario: Test that more than one term can be associated with the same Final Exam Matrix
    Given there is an Academic Term associated with a Final Exam matrix
    And there is a second Academic Term that is not associated with any final exam matrix
    When I associate the second Term with the Final Exam matrix of the initial Term
    And I view the initial term
    Then there is a message indicating that the final exam matrix is also used by the second term
    And Standard Final Exam or Common Final Exam rules from the initial term are listed with the second term

  #FE1.2.EB3 (KSENROLL-9797)
  Scenario: Test that only one matrix can be assigned to a term
    Given there is an Academic Term associated with a Final Exam matrix
    And there is a second Academic Term associated with a Final Exam matrix
    And there is an Academic Half Term that is not associated with any final exam matrix
    And I associate the Half Term with the Final Exam matrix of the initial Term
    When I associate the Half Term with the Final Exam matrix of the third Term
    And I view the initial term
    Then there is no message indicating that the final exam matrix is also used by the half term
    When I view the second term
    Then there is a message indicating that the final exam matrix is also used by the half term