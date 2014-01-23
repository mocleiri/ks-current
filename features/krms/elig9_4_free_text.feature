@nightly  @blue_team
Feature: CO.ELIG9-4 Free Text

  Background:
    Given I am logged in as admin
    And I have navigated to the Corequisite section for course "ENGL201" in the future term

  #ELIG9.4.EB1 (KSENROLL-7049)
  Scenario: Setup one level of data using Add Rule Statement button
    When I want to add a new statement to the Corequisite section
    And I add a new Corequisite text statement with text "Free Text 1"
    And I add a Corequisite text statement after node "A" with text "Free Text 2"
    And I add a Corequisite text statement after node "B" with text "Free Text 3"
    And I commit and return to see the changes made to the proposition
    And I want to edit the Corequisite section
    Then the edit tab's text should match "Free Text 1,Free Text 2,Free Text 3"

  #ELIG9.4.EB2 (KSENROLL-7049)
  Scenario: Setup second level of data (Groups) using Create Group button
    When I want to edit the Corequisite section
    And I group a Corequisite text statement with node "B" with text "Free Text 1b"
    And I group a Corequisite text statement with node "C" with text "Free Text 2b"
    And I group a Corequisite text statement with node "D" with text "Free Text 2c"
    And I commit and return to see the changes made to the proposition
    And I want to edit the Corequisite section
    Then the edit tab's text should match "Free Text 1b,Free Text 2c,Free Text 2b"