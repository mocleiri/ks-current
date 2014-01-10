@nightly @green_team
Feature: WC.Display Schedule of Classes rendered by AO cluster

  As an Admin I want to display a published schedule of classes ordered by
  activity offering cluster for a specific term in order to understand the courses being offered,
  including both descriptive and scheduling info.

  Background:
    Given I am logged in as a Schedule Coordinator
    # Although the following step is background for every scenario, it is tweaked to jump directly to the
    # display schedule of classes page with an added parameter in the URL for selection of rendering
    # In some of the scenarios, we must do some preliminary setup (adding an AO) before jumping to the
    # display schedule of classes page.  Thus, this step has been moved into each of the scenarios,
    # at least for the time being.
#    And I am using the schedule of classes page

  Scenario: Display schedule of classes for a particular course and verify that AO clusters are displayed
    Given I am using the schedule of classes page
    When I search for course offerings by course by entering a course offering code
    Then a list of course offerings with that course offering code is displayed
    And the course offering details displays a listing of AO clusters

  Scenario: Ensure that only courses in offered state are displayed in AO Cluster rendering
    When I add an Activity Offering in draft status to an existing Course Offering
    And I am using the schedule of classes page
    And I search for the Course Offering in the schedule of classes
    Then the added Activity Offering is not displayed in the expanded listing rendered by AO Cluster
