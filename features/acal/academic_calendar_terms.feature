@nightly @yellow_team
Feature: CO.Academic Calendar Terms
  CO 2.17

  Background:
    Given I am logged in as a Schedule Coordinator

  Scenario: Successfully create a new term for a new academic calendar
    Given I create an Academic Calendar
    When I add a new term to the Academic Calendar
    Then the term is listed when I view the Academic Calendar

  Scenario: Successfully create a new term for an academic calendar copy
    Given I copy an existing Academic Calendar
    When I add a new term to the Academic Calendar
    Then the term is listed when I view the Academic Calendar

  Scenario: Successfully edit term information for an academic calendar
    Given I copy an existing Academic Calendar
    And I add a new term to the Academic Calendar
    When I edit the information for a term
    Then the updated term information is listed when I view the Academic Calendar

  Scenario: Successfully delete a term from a new academic calendar
    Given I create an Academic Calendar
    And I add a new term to the Academic Calendar
    When I delete the term
    Then the term is not listed when I view the Academic Calendar

  @smoke_test
  Scenario: Make an academic term official
    Given I create an Academic Calendar in Official status
    And I add a new term to the Academic Calendar
    When I make the term official
    Then the term is listed in official status when I view the Academic Calendar

  Scenario: Add a Key Date to an academic term
    Given I create an Academic Calendar
    And I add a new term to the Academic Calendar
    When I add an instructional Key Date
    Then the Key Date is listed with the academic term information

  Scenario: Modify a Key Date for an academic term
    Given I create an Academic Calendar
    And I add a new term to the Academic Calendar with an instructional key date
    When I edit an instructional Key Date
    Then the updated Key Date is listed with the academic term information

  @bug @KSENROLL-7146
  Scenario: Delete a Key Date for an academic term
    Given I copy an existing Academic Calendar
    When I delete an instructional Key Date
    Then the Key Date is not listed with the academic term information

  @bug @KSENROLL-7146
  Scenario: Delete a Key Date Group for an academic term
    Given I copy an existing Academic Calendar
    When I delete an instructional Key Date Group
    Then the Key Date Group is not listed with the academic term information

  Scenario: Copy Key Dates for an academic term
    Given I copy an existing Academic Calendar
    Then the Key Dates are copied without date values

  Scenario: Verify instructional days calculation for an academic term
    Given I create an Academic Calendar
    And I add a new term to the Academic Calendar with a defined instructional period
    Then the instructional days calculation is correct
    When I add a Holiday Calendar with holidays in the term
    Then the instructional days calculation is correct

  Scenario: Verify an error message is displayed when a term is added with dates do not fall within (parent) Academic Calendar dates
    Given I create an Academic Calendar
    And I add a new term with start date earlier than the Academic Calendar start date
    Then a term warning message is displayed stating "doesn't fall within Acal dates"

  Scenario: Verify an error message is displayed when a term is edited with dates do not fall within (parent) Academic Calendar dates
    Given I create an Academic Calendar with a term
    When I edit the term so that the start date is earlier than the Academic Calendar start date
    Then a term warning message is displayed stating "doesn't fall within Acal dates"

  Scenario: Verify an error message is displayed when a key date is added with dates that do not fall within (parent) term dates
    Given I create an Academic Calendar with a term
    And I add a new key date with a date later than the Academic Term end date
    Then a Key Dates warning message is displayed stating "doesn't fall within.*dates"

  Scenario: Verify an error message is displayed when a key date is edited with dates that do not fall within (parent) term dates
    Given I create an Academic Calendar with a term
    And I add an instructional Key Date
    When I edit the key date so that the start date is later than the Academic Term end date
    Then a Key Dates warning message is displayed stating "doesn't fall within.*dates"

  @bug @KSENROLL-7146
  Scenario: Verify a warning message is displayed when a key date is made blank
    Given I create an Academic Calendar with a term
    And I add an instructional Key Date
    When I make the key date blank
    Then a Key Dates warning message is displayed stating "Start date should not be empty"