@nightly @yellow_team
Feature: CO.AZ Manage Course Offerings with subterms

  CO 26.6: As an administrator I want authorization applied to AOs associated with sub terms

  Background:
    Given I am logged in as a Schedule Coordinator
    And I create an Academic Calendar with subterms
    And I make the subterms official
    And I create a Course Offering from catalog with Activity Offerings assigned to subterms in my admin org
    #And I rollover the subterms' parent term to a target term with those subterms setup

  Scenario: CO 26.6A Carol has access to add/remove/update the subterm for an AO (in admin org) when SOC=OPEN
    Given I am logged in as a Department Schedule Coordinator
    And I manage the course offering
    When I remove the subterm for the activity offering
    Then the AO subterm is successfully removed
    When I edit the Activity Offering
    And I set a subterm for the activity offering
    Then the AO subterm change is successful
    When I update the subterm for the activity offering
    Then the AO subterm change is successful

  Scenario: CO 26.6B Carol has access to add/remove/update the subterm for an AO (in admin org) when SOC=FINAL EDITS
    Given I advance the SOC state from open to final edits state
    And I am logged in as a Department Schedule Coordinator
    And I manage the Course Offering in the term
    When I remove the subterm for the activity offering
    Then the AO subterm is successfully removed
    When I edit the Activity Offering
    And I set a subterm for the activity offering
    Then the AO subterm change is successful
    When I update the subterm for the activity offering
    Then the AO subterm change is successful

  Scenario: CO 26.6C Carol does not have access to add/remove/update the subterm for an AO (in admin org) when SOC=PUBLISHED
    Given I advance the SOC state from open to published state
    And I am logged in as a Department Schedule Coordinator
    And I manage the Course Offering in the term
    When I edit the activity offering I do not have access to change the subterm

