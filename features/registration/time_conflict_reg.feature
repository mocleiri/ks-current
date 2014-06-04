@draft @red_team

Feature: REG.Time Conflict

  CR 9 - As an Administrator, I want the system to validate that there are no time conflicts amongst
         the Activity Offerings in a student's Registration Cart and existing Schedule.

  Background:
    Given I am using a mobile screen size
    Given I log in to student registration as student2

  #KSENROLL-13008
  @wip
  Scenario: CR 9.1 - As an admin, I want to prevent students from being registered in courses
                     whose times conflict so that they are able to attend courses in their entirety
    When I add a HIST2 course offering to my registration cart
    And I register for the course
    Then there is a message indicating successful registration
    When I view my schedule
    Then the course is present in my schedule
    When I add a HIST3 course offering to my registration cart
    And I register for the course
    And there is a message indicating that registration failed
    And there is a message indicating a time conflict
    When I view my schedule
    Then the course is not present in my schedule

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