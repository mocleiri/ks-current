@nightly @blue_team_krms
Feature: CO.Adding and Editing CO Requisites

  Background:
    Given I am logged in as admin

  #KSENROLL-7177
  Scenario: ELIG7.3.1/ELIG7.5.1 Test whether CO data persists if CLU was empty
    When I add 2 statements to the Student Eligibility & Prerequisite section for course "HIST307" in the future term
    Then the agenda page's text should match "minimum of 1 course from,HIST210,HIST395"
    When I want to edit the Student Eligibility & Prerequisite section
    Then the edit tab's text should match "1 course from (HIST210, HIST395),free form text input value"

  #KSENROLL-7239
  Scenario: ELIG7.3.2/ELIG7.5.2 Test whether CO data persists if CLU already had data
    When I add 2 statements to the Student Eligibility & Prerequisite section for course "ENGL478M" in the historic term
    Then the agenda page's text should match "free form text input value"
    And I want to edit the Student Eligibility & Prerequisite section
    Then the edit tab's text should match "1 course from (HIST210, HIST395)"

  #KSENROLL-6953
  Scenario: ELIG9.1.1 Test whether data is created and persisted to the DB
    When I setup the Student Eligibility & Prerequisite section for course "HIST110" in the future term
    Then the agenda page's text should match "all courses from,ENGL478,HIST416"
    When I want to edit the Student Eligibility & Prerequisite section
    Then the edit tab's text should match "minimum of 1 course from (HIST210, HIST395)"
    When I switch to the other tab on the page
    Then the text area should contain "(A OR (B AND C AND D) OR E) AND F AND G"