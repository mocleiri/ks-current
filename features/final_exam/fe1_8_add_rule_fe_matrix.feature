@nightly @blue_team
Feature: SA.FE1-8 Add new rule to the Final Exam Matrix
  FE 1.8 As a Central Administrator I want to specify an exam location for one or more exam offerings for course
  offering driven exam time slots so that predetermined common exam locations are preslotted in the exam offering
  requested delivery logistics

  Background:
    Given I am logged in as admin

  #FE1.8.EB1 (KSENROLL-9799)
  Scenario: Test whether location data can be added to a Common Final Exam rule in the FE Matrix
    When I add a Common Final Exam course rule to the Final Exam Matrix
    And I add Building and Room location data to the Requested Exam Offering Delivery Logistic information
    Then I should be able to see the location data for the exam specified in the course rule in the Common Final Exam table
