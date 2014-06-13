@draft @red_team

Feature: REG.Time Conflict

  CR 9 - As an Administrator, I want the system to validate that there are no time conflicts amongst
         the Activity Offerings in a student's Registration Cart and existing Schedule.
  CR 9.1 - As an admin, I want to prevent students from being registered in courses whose times conflict
           so that they are able to attend courses in their entirety

 Background:
    Given I am using a mobile screen size
    Given I log in to student registration as student2

  #KSENROLL-13008
  Scenario: CR 9.1 - Attempt to register for two courses whose offering times are in conflict
    When I attempt to register for two courses whose times conflict
    Then there is a message indicating a time conflict
    When I view my schedule
    Then the course is not present in my schedule
