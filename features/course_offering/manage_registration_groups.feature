@nightly @yellow_team
Feature: CO.Create registration groups

As an Administrator, I want to create registration groups for a Course Offering

  Background:
    Given I am logged in as a Schedule Coordinator

  #ARG 6.1 - see Simple Rollover feature
  #ARG 6.2 When creating a CO by copy AOC and AOs should also be copied and RGs are generated - see copy_activity_offering_clusters.feature

  Scenario: RG 6.3 Registration groups are automatically generated when adding or copying an AO
    Given I manage registration groups for a new course offering
    When I copy an Activity Offering
    Then the corresponding number of registration groups for each cluster is correct
    And I add an Activity Offering
    Then the corresponding number of registration groups for each cluster is correct

  Scenario: RG 6.4A when an AO is updated and creates a time conflict or a total seats issue the reg group state and the messaging should reflect this
    Given I manage registration groups for a new course offering
    When I update an Activity Offering to have less seats
    Then a warning message is displayed stating "The sums of maximum enrollment seats"
    And I update an Activity Offering to create a time conflict
    Then a warning message is displayed stating "invalid due to scheduling conflicts"

  Scenario: RG 6.4B: Error message is displayed if I attempt to create 2 activity offering clusters with the same private name
    Given I manage registration groups for a new course offering
    And I create an activity offering cluster
    When I try to create a second activity offering cluster with the same private name
    Then a cluster error message appears stating "The cluster private name: \w* is already in use. Choose another private name"
    When I try to create a second activity offering cluster with a different private name
    And I try to rename the second activity offering cluster to the same private name as the first
    Then a cluster error message appears stating "The cluster private name: \w* is already in use. Choose another private name"

  Scenario: RG 6.4C When an AO cluster does not have all AO types represented a warning message should appear
    Given I manage registration groups for a new course offering with multiple AO types and only one lecture activity
    When I create an activity offering cluster
    And I move a lecture activity offering to the new cluster
    Then for the original cluster a warning message appears stating "You must add Lecture to form reg groups"

  Scenario: RG 6.5 When deleting an AOC delete all associated AOs as well
    Given I manage registration groups for a new course offering
    And I delete an Activity Offering
    Then the corresponding number of registration groups for each cluster is correct
    When I create an activity offering cluster
    And Move one lab and one lecture activity offering to the second cluster
    And I remove the newly created cluster
    Then the cluster and pertaining AO's are deleted

  Scenario: RG 6.6/6.9  Ability to view AOs in their AOCs and RG's are generated correctly
    Given I manage registration groups for a new course offering
    Then the Activity Offerings are present in the cluster table
    And the corresponding number of registration groups for each cluster is correct
    Then I create an activity offering cluster
    And Move one lab and one lecture activity offering to the second cluster
    Then the corresponding number of registration groups for each cluster is correct

  Scenario: RG 6.7 When deleting an AOC delete all associated AOs as well
    Given I manage registration groups for a new course offering
    When I create an activity offering cluster
    And Move one lab and one lecture activity offering to the second cluster
    And I remove the newly created cluster
    Then the cluster and pertaining AO's are deleted

  #KSENROLL-8103
  Scenario: RG 6.8 Create new cluster for second format offering
    Given I edit a course offering with 2 format types
    And I add a delivery format option of Discussion Lecture
    When I manage the course offering
    And I create a Discussion Lecture activity offering cluster
    Then the new activity offering cluster is present

