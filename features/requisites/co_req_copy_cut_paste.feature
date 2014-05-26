@nightly @blue_team_krms
Feature: CO.Copy or Cut and Paste of CO Requisites

  Background:
    Given I am logged in as admin

  #KSENROLL-7051
  Scenario: ELIG9.10.1 Confirm the copy and paste of rule statement is working as expected
    When I add 3 statements to the Student Eligibility & Prerequisite section for course "HIST111" in the future term
    And I want to edit the Student Eligibility & Prerequisite section
    And I copy node "B" and paste it after node "C"
    Then the edit tab's text should match "D. free form text input value"
    And there should be a dropdown with value "AND" before node "D."
    When I switch to the other tab on the page
    Then the text area should contain "A AND B AND C AND D"
    When I switch to the other tab on the page
    And I edit node "D" by changing text to "edit copied prop type 1"
    Then the edit tab's text should match "edit copied prop type 1"
    And I persist the changes made to the proposition

  #KSENROLL-7051
  Scenario: ELIG9.10.2 Confirm the copy and paste of compound (group) rule statement is working as expected
    When I add 4 statements to the Student Eligibility & Prerequisite section for course "HIST111" in the future term
    And I want to edit the Student Eligibility & Prerequisite section
    And I copy the group containing node "A" and paste it after node "D"
    Then the edit tab's text should match "F. Must have successfully completed a minimum of 1 course from (HIST210, HIST395),H. Text"
    And there should be a dropdown with value "OR" before node "F."
    When I switch to the other tab on the page
    Then the text area should contain "(A AND B AND C) OR D OR (F AND G AND H) OR E"
    When I persist the changes and return to the proposition
    And I want to edit the Student Eligibility & Prerequisite section
    And I switch to the other tab on the page
    Then the text area should contain "(A AND B AND C) OR D OR (E AND F AND G) OR H"

  #KSENROLL-7052
  Scenario: ELIG9.10.3 Confirm the cut and paste of a rule statement is working as expected
    When I add 3 statements to the Corequisite section for course "HIST111" in the future term
    And I want to edit the Corequisite section
    And I cut node "B" and paste it after node "C"
    Then there should be a dropdown with value "AND" before node "B."
    When I switch to the other tab on the page
    Then the text area should contain "A AND C AND B"
    When I switch to the other tab on the page
    When I edit node "B" by changing text to "edit cut prop type"
    Then the edit tab's text should match "edit cut prop type"
    And I persist the changes made to the proposition

  #KSENROLL-7052
  Scenario: ELIG9.10.4 Confirm the cut and paste of compound (group) rule statement is working as expected
    When I add 4 statements to the Corequisite section for course "HIST111" in the future term
    And I want to edit the Corequisite section
    And I cut the group containing node "A" and paste it after node "D"
    Then there should be a dropdown with value "OR" before node "A."
    When I switch to the other tab on the page
    Then the text area should contain "D OR (A AND B AND C) OR E"
    When I persist the changes and return to the proposition
    And I want to edit the Corequisite section
    And I switch to the other tab on the page
    Then the text area should contain "A OR (B AND C AND D) OR E"