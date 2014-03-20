@nightly
Feature: GT.Create a Course Proposal as Faculty

  Background:
    Given I am logged in as Fred
   # And I create a course proposal

#  Scenario: Create Course Proposal from blank
#    When I create a course proposal from blank
#    Then I should see data in the proposal title on course information
#    And I should see data in the course title on course information

  Scenario: CC2.1 Create a course proposal with only required fields
    When I complete the required fields for save on the course proposal
    Then I should see data in required for save fields for the course proposal
    And I cancel the course proposal page
    Then I should see CM Home
    And I perform a complete search for the Course Proposal
    Then I should see my proposal listed in the search result
    And I can review my proposal

#  Scenario: CC2.2 Click the back button in the course proposal page for each tab
#    When I complete the required fields for save on the course proposal
#    Then I click the back button in each tab of the course proposal page
#    And I see that it navigates to the previous tab in the course proposal page

