@nightly @green_team
Feature: WC.Colocated Activity Offerings

Background:
  Given I am logged in as a Schedule Coordinator

#KSENROLL-9442
  Scenario: Colocate Activity Offerings using shared enrollments
    When I create "3" COs with an AO in each
    And I colocate multiple activities, selecting to "share" enrollments
    Then the activities indicate they are colocated
    And I break colocation on the first colocated AO
    Then the first colocated AO is not colocated with any remaining AOs

#KSENROLL-9442
  Scenario: Colocate Activity Offerings using separately managed enrollments
    When I create "3" COs with an AO in each
    And I colocate multiple activities, selecting to "separately manage" enrollments
    Then the activities indicate they are colocated
    And I break colocation on the first colocated AO
    Then the first colocated AO is not colocated with any remaining AOs

  Scenario: Delete a fully colocated AO
    When I designate a valid term and Course Offering Code with a fully colocated AO
    And I delete the fully colocated AO
    Then The AO is successfully deleted

  Scenario: CCO 2.12A Verify colocation is copied when a Course Offering is copied within the same term
    Given there is a course offering with a colocated activity offering
    When I copy the course offering
    Then the activity offering in the course offering copy is added to the colocated set

  Scenario: CCO 2.12B Verify colocation is NOT copied when a Course Offering is copied from a prior term
    Given there is a course offering with a colocated activity offering
    When I create a new course offering in a subsequent term by copying the existing course offering
    Then the activity offering in the course offering copy is not colocated

  Scenario: CCO 2.12C Verify when a colocated activity offering is copied the copy is added to the colocated set
    Given I create "3" COs with an AO in each
    And I colocate multiple activities, selecting to "separately manage" enrollments
    When I copy one of the colocated activity offerings
    Then the activity offering copy is added to the colocated set

  Scenario: CCO 2.13 Successfully rollover a course offering with colocated Activity Offerings
    Given I create an Academic Calendar and add an official term
    And I create three Course Offerings with colocated AOs in the new term
    And I rollover the term to a new academic term
    Then the Activity Offerings are colocated in the rollover target

