@nightly @blue_team
Feature: SA.ELIG9-10 Cut and Paste

  Background:
    Given I am logged in as admin
    And I have setup the Corequisite section for course "ENGL101X" in the future term

  #ELIG9.10.EB1 (KSENROLL-7052)
  Scenario: Confirm the cut and paste of a rule statement is working as expected
    When I want to edit the Corequisite section
    And I cut node "D" and paste it after node "G"
    Then there should be a dropdown with value "OR" before node "D."
    When I switch to the other tab on the page
    Then the text area should contain "(A OR (B AND C)) OR E OR F OR G OR D"
    When I switch to the other tab on the page
    When I edit node "D" by changing text to "edit cut prop type"
    Then the edit tab's text should match "edit cut prop type"
    When I commit changes made to the proposition

  #ELIG9.10.EB2 (KSENROLL-7052)
  Scenario: Confirm the cut and paste of compound (group) rule statement is working as expected
    When I want to edit the Corequisite section
    And I cut the group containing node "B" and paste it after node "F"
    Then there should be a dropdown with value "OR" before node "B."
    When I switch to the other tab on the page
    Then the text area should contain "A OR D OR E OR F OR (B AND C) OR G"
    When I commit and return to see the changes made to the proposition
    And I want to edit the Corequisite section
    And I switch to the other tab on the page
    Then the text area should contain "A OR B OR C OR D OR (E AND F) OR G"