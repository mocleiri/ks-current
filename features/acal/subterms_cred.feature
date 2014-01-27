@nightly @yellow_team
Feature: CO.Subterms CRED

  S16: Ability to manage academic subterms within academic calendars and terms

  Background:
    Given I am logged in as a Schedule Coordinator

  Scenario: CAL 4.1 Successfully copy an Academic Calendar with subterms
    Given I create an Academic Calendar with subterms
    When I copy the Academic Calendar
    Then the subterms are successfully copied

  Scenario: CAL 4.2A Successfully create a subterm in an Academic calendar
    Given I create an Academic Calendar that supports subterms
    When I add a subterm
    Then the subterm is listed when I view the Academic Calendar

  Scenario: CAL 4.2B Successfully search for and view and subterm
    Given I create an Academic Calendar with subterms
    Then I can search and view the subterm in read only mode

  Scenario: CAL 4.2C Verify an error message is displayed when a subterm is added with dates do not fall within (parent) Academic Calendar dates
    Given I create an Academic Calendar that supports subterms
    And I add a new subterm with start date earlier than the Academic Calendar start date
    Then a subterm warning message is displayed stating "doesn't fall within Acal dates"

  Scenario: CAL 4.2D Verify an error message is displayed when a subterm is edited with dates do not fall within (parent) Academic Calendar dates
    Given I create an Academic Calendar with subterms
    When I edit the subterm so that the start date is earlier than the Academic Calendar start date
    Then a subterm warning message is displayed stating "doesn't fall within Acal dates"

  Scenario: CAL 4.2E Verify an error message is displayed when a key date is added with dates that do not fall within (parent) subterm dates
    Given I create an Academic Calendar with subterms
    And I add a new key date with a date later than the Academic Subterm end date
    Then a subterm Key Dates warning message is displayed stating "doesn't fall within.*dates"

  Scenario: CAL 4.2F Verify an error message is displayed when a key date is edited with dates that do not fall within (parent) subterm dates
    Given I create an Academic Calendar with subterms
    And I add an instructional Key Date to a subterm
    When I edit the key date so that the start date is later than the Academic Subterm end date
    Then a subterm Key Dates warning message is displayed stating "doesn't fall within.*dates"

  Scenario: CAL 4.2G Verify an error message is displayed when a key date is made blank
    Given I create an Academic Calendar with subterms
    And I add an instructional Key Date to a subterm
    When I make the subterm key date blank
    Then a subterm Key Dates warning message is displayed stating "Start date should not be empty"

  Scenario: CAL 4.3 Successfully edit a subterm
    Given I create an Academic Calendar with subterms
    When I edit the subterm information
    Then the subterm in updated successfully

  Scenario: CAL 4.7 Successfully verify the instruction dates calculation for a subterm
    Given I create an Academic Calendar that supports subterms
    And I add a new subterm to the Academic Calendar with a defined instructional period
    Then the instructional days calculation is correct
    When I add a Holiday Calendar with holidays in the term
    Then the instructional days calculation is correct

  Scenario: CAL 4.8A Make subterm official when parent term is official
    Given I create an Academic Calendar with subterms
    And I edit the term and make it official
    When I make the subterms official
    Then the subterm is listed in official status when I view the Academic Calendar

  Scenario: CAL 4.8B Make subterm official when parent term is draft and parent academic calendar is draft
    Given I create an Academic Calendar with subterms
    When I make the subterms official
    Then the subterm is listed in official status when I view the Academic Calendar
    And the term is listed in official status when I view the Academic Calendar
    When I search for the calendar
    Then the calendar should be set to Official

  Scenario: CAL 4.10 Successfully delete a subterm
    Given I create an Academic Calendar with subterms
    When I edit the calendar
    And I delete a subterm
    Then the subterm is no longer listed on the calendar

  Scenario: CAL 4.10A Delete draft academic calendar and ensure draft subterm is also deleted.
     Given I create an Academic Calendar with subterms
     When I delete the Academic Calendar draft
     Then the subterm is also deleted

    Scenario: CAL 4.10B Delete draft parent term and ensure that draft subterm is also deleted
      Given I create an Academic Calendar with subterms
      When I delete the parent term of a subterm
      Then the subterm is also deleted

