@blue_team
Feature: CO.Perform Simple Rollover for Exam Offering Scheduling Information
  FE 4.1: As a Central Administrator I want to have on matrix exam offerings populated with requested scheduling
  information when created in bulk so that exam offering scheduling requests are consistent with the established exam matrix

  Background:
    Given I am logged in as admin

  #KSENROLL-9628
  @pending
  Scenario: FE4.1.1 Test whether Exam Offering Schedule Info is populated after a rollover is completed
    Given I create an Academic Calendar and add an official term
    When I create multiple Course Offerings each with different Exam Offerings and Requested Scheduling Information
    And I rollover the term to a new academic term that has an exam period
    Then all the Exam Offerings RSI data should be populated or not depending on whether the data is found on the Exam Matrix