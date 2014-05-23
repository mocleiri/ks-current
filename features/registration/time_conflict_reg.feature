@draft @red_team

Feature: REG.Time Conflict

  CR 9 - As an Administrator, I want the system to validate that there are no time conflicts amongst
         the Activity Offerings in a student's Registration Cart and existing Schedule.

  Background:
    Given I log in to student registration as student2

  #KSENROLL-13010
  @wip
  Scenario: CR 9.3 - As a student,I want to be informed of time conflict violations on my schedule
                    resulting from overlapping meeting times so that I can resolve them
    When I register for two course offerings that do not have a time conflict
    Then I log out from student registration
    And I log in as a Schedule Coordinator
    And update one of the courses so that its time conflicts with the other
    Then I log out
    And I log in to student registration as student2
    When I view my schedule
    Then there is a message indicating that there is a time conflict