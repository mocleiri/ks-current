@nightly @blue_team
Feature: CO.ELIG7-5 Adding CO to empty CLU

  Background:
    Given I am logged in as admin

  #ELIG7.5.EB1 (KSENROLL-7177)
  Scenario: Test whether CO data persists if CLU was empty
    When I setup the Student Eligibility & Prerequisite section for course "BSCI202" in the future term
    Then the agenda page's text should match "all courses from,ENGL478,HIST416"
    When I want to edit the Student Eligibility & Prerequisite section
    Then the edit tab's text should match "minimum of 1 course from (HIST210, HIST395)"
    When I switch to the other tab on the page
    Then the text area should contain "(A OR (B AND C) OR D) OR E OR F OR G"

  #ELIG7.5.EB2 (KSENROLL-7239)
  Scenario: Test whether CO data persists if CLU already had data
    When I edit the Student Eligibility & Prerequisite section for course "ENGL402" in the historic term
    And I want to edit the Student Eligibility & Prerequisite section
    Then the edit tab's text should match "minimum of 1 course from (HIST210, HIST395)"
    When I switch to the other tab on the page
    Then the text area should contain "(A OR (B AND C) OR D) OR E OR F OR G"
    When I update the manage course offering agendas page
    Then the agenda page's text should match "all courses from,ENGL478,HIST416"
