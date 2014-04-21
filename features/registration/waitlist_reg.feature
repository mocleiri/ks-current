@nightly
Feature: REG.Wait List

  As a student I want to add or remove myself from a courses wait list.

  Background:
    Given I am logged in as admin

  #KSENROLL-12345
  Scenario: CR 8.1 I want to add myself to the waitlist for a full registration group after I submit my cart so I can establish priority for a space when it becomes available
    When I register for a full ENGL2 course offering that has a waitlist
    Then there is a message indicating that registration failed
    And I am given the option to add myself to a waitlist for the course
    When I add myself to a waitlist for the course
    Then there is a message indicating that I have been added to the waitlist
    When I view my schedule
    Then I can verify I am on the waitlist

  #KSENROLL-12219 #KSENROLL-12346
  Scenario: CR 7.1 I want to be notified if a registration group is full after I submit my cart so that I can adjust my registration appropriately
            CR 8.2 As an administrator I do not want students to be able to waitlist for a registration group when a waitlist is not offered
    When I register for a full ENGL3 course offering that does not have a waitlist
    Then there is a message indicating that registration failed
    And there is a message indicating that no waitlist is offered

#KSENROLL-12347
  Scenario: CR 8.3 As an administrator I want to enforce any waitlist capacity restrictions for a specific registration group so that students are not able to add themselves to that waitlist if there is not space available
    When I register for a full ENGL4 course offering that has a waitlist
    Then there is a message indicating that registration failed
    And I am given the option to add myself to a waitlist for the course
    When I add myself to a waitlist for the course
    Then I log out from student registration
    Then I am logged in as a Schedule Coordinator
    And I add an ENGL4 course offering to my registration cart
    And I register for the course
    Then there is a message indicating that registration failed
    And there is a message indicating that the waitlist is full

#KSENROLL-12348
  Scenario: CR 8.4 I want to remove myself from a waitlist for a registration group so I am no longer considered for a space that becomes available
    When I register for a full BSCI3 course offering that has a waitlist
    Then there is a message indicating that registration failed
    And I am given the option to add myself to a waitlist for the course
    When I add myself to a waitlist for the course
    And I view my schedule
    And I remove myself from the waitlist
    Then I can verify I am not on the waitlist

  #KSENROLL-12351
  Scenario: CR 8.7 I want to edit the parameters of a waitlisted registration group so if I am registered for it I can take it with my preferred options
    When I register for a full CHEM3 course offering and add myself to a waitlist
    And I view my schedule
    Then there is an option to edit the waitlisted course
    When I edit the waitlisted course
    Then the course is present in my waitlist, with the updated options

  #KSENROLL-12531 KSENROLL-12533
  @wip
  Scenario: CR 11.1 - As an administrator I want the next waitlisted student to be automatically registered when a student drops a RG so no administrative or student action is needed to fill open seats
            CR 11.2 - As a student, I want my student schedule to be updated after I have been registered for a RG from the waitlist so I can verify my registration
    Given I am logged in as student1
    When I drop a course I am registered for that has a waitlist
    And I log in as student2
    Then I can verify I am not on the waitlist
    And the course is present in my schedule
    And the number of courses and credits I am registered for is correctly updated in my schedule
