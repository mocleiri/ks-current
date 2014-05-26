@nightly @blue_team
Feature: CO.Add Statement and Create Group of CO Requisites

  Background:
    Given I am logged in as admin
    And I have navigated to the Corequisite section for course "HIST210" in the future term

  #KSENROLL-7049/KSENROLL-7049
  Scenario: ELIG9.10.1/ELIG9.4.1 Setup one level of data using Add Statement button
    When I want to add a new statement to the Corequisite section
    And I add a new Corequisite text statement with text "Free Text 1"
    And I add a Corequisite text statement after node "A" with text "Free Text 2"
    And I add a Corequisite text statement after node "B" with text "Free Text 3"
    And I persist the changes and return to the proposition
    And I want to edit the Corequisite section
    Then the edit tab's text should match "Free Text 1,Free Text 2,Free Text 3"

  #KSENROLL-7049/KSENROLL-7049
  Scenario: ELIG9.10.2/ELIG9.4.1 Setup second level of data (Groups) using Create Group button
    When I want to add a new statement to the Corequisite section
    And I add a new Corequisite text statement with text "Free Text 1b"
    And I group a Corequisite text statement with node "A" with text "Free Text 2b"
    And I group a Corequisite text statement with node "A" with text "Free Text 2c"
    And I persist the changes and return to the proposition
    And I want to edit the Corequisite section
    Then the edit tab's text should match "Free Text 1b,Free Text 2c,Free Text 2b"
