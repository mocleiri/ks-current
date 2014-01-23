@nightly @yellow_team
Feature: CO.AZ Milestones

  Background:
    Given I am logged in as a Schedule Coordinator
    And I am working on a term in "Published" SOC state for milestones testing

  Scenario: Department Schedule Coordinator Carol has access to delete AO's Before the first day of class
    Given It is "Before" the first day of classes and "Before" the first day to add classes
    And there is a "Draft" course offering in my admin org
    Given I am logged in as a Department Schedule Coordinator
    Then I have access to delete an activity offering in a "Draft" state
    And I have access to delete a course offering in a "Draft" state
    And there is a "Offered" course offering in my admin org
    And I do not have access to select the "Offered" course offering for approve, delete
    Then I do not have access to select an activity offering in a "Offered" state
    #And I do not have access to delete an activity offering in a "Offered" state

  Scenario:Department Schedule Coordinator Carol does not have access to delete AO's After the first day of class
    Given It is "After" the first day of classes
    And there is a "Draft" course offering in my admin org
    Given I am logged in as a Department Schedule Coordinator
    And I do not have access to select course offerings for approve, delete
    Then I do not have access to select an activity offering in a "Draft" state
    #Then I do not have access to delete an activity offering in a "Draft" state
    And there is a "Offered" course offering in my admin org
    And I do not have access to select course offerings for approve, delete
    Then I do not have access to select an activity offering in a "Offered" state
    #And I do not have access to delete an activity offering in a "Offered" state

  Scenario: Department Schedule Coordinator Carol Access Verification for Manage course offering List
    Given It is "After" the first day of classes
    And I am logged in as a Department Schedule Coordinator
    Then I manage course offerings for a subject code in my admin org
    And I have access to manage course offerings
    And I do not have access to copy the listed course offering
    And I do not have access to edit the listed course offering
    Given It is "Before" the first day of classes and "Before" the first day to add classes
    And I am logged in as a Department Schedule Coordinator
    Then I manage course offerings for a subject code in my admin org
    And I have access to manage course offerings
    And I do not have access to copy the listed course offering
    And I do not have access to edit the listed course offering

  Scenario: Department Schedule Coordinator Carol Access Verification for Create course offerings
    Given It is "After" the first day of classes
    And I am logged in as a Department Schedule Coordinator
    Then I attempt to create a course offering for a subject in my admin org
    And I do not have access to create the course offering from existing
    And I do not have access to create the course offering from catalog
    Given It is "Before" the first day of classes and "Before" the first day to add classes
    And I am logged in as a Department Schedule Coordinator
    Then I attempt to create a course offering for a subject in my admin org
    And I do not have access to create the course offering from existing
    And I do not have access to create the course offering from catalog

  Scenario: Department Schedule Coordinator Carol Access Verification for Manage Course Offerings
    Given It is "After" the first day of classes
    And I am logged in as a Department Schedule Coordinator
    Then I manage a course offering in my admin org
    And I have access to view the activity offering details
    And I do not have access to copy an activity offering
    And I have access to edit an activity offering
    Given It is "Before" the first day of classes and "Before" the first day to add classes
    And I am logged in as a Department Schedule Coordinator
    Then I manage a course offering in my admin org
    And I have access to view the activity offering details
    And I do not have access to copy an activity offering
    And I have access to edit an activity offering

  Scenario: Department Schedule Coordinator Carol Access Verification for Rollover
    Given It is "After" the first day of classes
    And I am logged in as a Department Schedule Coordinator
    Then I attempt to perform a rollover
    Then I do not have access to the page
    Given It is "Before" the first day of classes and "Before" the first day to add classes
    And I am logged in as a Department Schedule Coordinator
    Then I attempt to perform a rollover
    Then I do not have access to the page
    Given It is "Before" the first day of classes and "After" the first day to add classes
    And I am logged in as a Department Schedule Coordinator
    Then I attempt to perform a rollover
    Then I do not have access to the page

  Scenario: Department Schedule Coordinator Carol Access Verification for Edit Activity Offerings
    Given It is "Before" the first day of classes and "After" the first day to add classes
    And I am logged in as a Department Schedule Coordinator
    Then I manage course offerings for a subject code in my admin org
    And I have access to manage course offerings
    And I edit an activity offering in my department
    Then I do not have access to edit the activity code
    And I do not have access to edit maximum enrollment
    And I have access to add or edit affiliated personnel
    And I do not have access to seat pools
    And I do not have access to add new scheduling information
    And I have access to edit the honors flag
    And I have access to edit the course url
    And I have access to edit the evaluation flag

