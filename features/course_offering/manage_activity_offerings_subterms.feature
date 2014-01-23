@nightly @yellow_team
Feature: CO.Manage Activity Offerings - subterms

  CO 26 - As a Departmental administrator, I want to be able to manage AOs in subterms

  Background:
    Given I am logged in as a Schedule Coordinator

  Scenario: CO 26.1 Successfully change the subterm for an AO
    Given I edit an Activity Offering that has available subterms
    When I set a subterm for the activity offering
    Then the AO subterm change is successful
    When I update the subterm for the activity offering
    Then the AO subterm change is successful
    When I remove the subterm for the activity offering
    Then the AO subterm is successfully removed

  Scenario: CO 26.8A Successfully copy an AO attached to a subterm
    Given I edit an Activity Offering that has available subterms
    And I set a subterm for the activity offering
    When I copy the activity offering
    Then the AO subterm indicator is successfully copied

  Scenario: CO 26.8B Successfully copy a CO with an AO attached to a subterm
    Given I edit an Activity Offering that has available subterms
    And I set a subterm for the activity offering
    When I copy the parent course offering
    Then the AO subterm indicator is successfully copied with the parent CO

  Scenario: CO 26.8C1 Successfully copy an existing Course Offering from a prior term which has an AO attached to a subterm
    Given I create an Academic Calendar with subterms
    And I make the subterms official
    And I create a Course Offering from catalog with Activity Offerings assigned to subterms
    And I approve the Course Offering for scheduling
    When I advance the SOC state from open to published state
    Then the Course Offering is in offered state
    And I setup a second target term with those subterms setup
    Then I can create a Course Offering in the second term from the existing CO in the first term
    Then the Activity Offerings for the copied CO are assigned to the target subterms

  @draft @reference_only
  Scenario: CO 26.8C2A Successfully promote a Course Offering to offered with AOs NOT attached to a subterm
    Given I create an Academic Calendar with subterms
    And I make the subterms official
    And I create a Course Offering from catalog with Activity Offerings
    And I rollover the subterms' parent term to a target term with those subterms setup
    And I approve the Course Offering for scheduling in the target term
    When I advance the SOC state from open to published state
    Then the Course Offering is in offered state

  @draft @reference_only
  Scenario: CO 26.8C2B Successfully promote a Course Offering to offered with AOs attached to a subterm
    Given I create an Academic Calendar with subterms
    And I make the subterms official
    And I create a Course Offering from catalog with Activity Offerings assigned to subterms
    And I rollover the subterms' parent term to a target term with those subterms setup
    And I approve the Course Offering for scheduling in the target term
    When I advance the SOC state from open to published state
    Then the Course Offering is in offered state

  Scenario: CO 26.4A Successfully rollover a term where a Course Offering has AOs attached to subterms
    Given I create an Academic Calendar with subterms
    And I make the subterms official
    And I create a Course Offering from catalog with Activity Offerings assigned to subterms
    When I rollover the subterms' parent term to a target term with those subterms setup
    Then the Activity Offerings are assigned to the target subterms

  Scenario: CO 26.4B Confirm error message is displayed for a rollover where the source term has AOs/subterms and the target term does not
    Given I create an Academic Calendar with subterms
    And I make the subterms official
    And I create a Course Offering with an Activity Offerings assigned to subterms
    When I rollover the subterms' parent term to a target term with those subterms are NOT setup
    Then there is an exception for the course on rollover page stating: Target term does not have subterm types found in AOs