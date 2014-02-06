@nightly @blue_team
Feature: CO.ELIG7-4 Compare to Canonical

  Background:
    Given I am logged in as admin

  #ELIG7.4.EB1 (KSENROLL-7174)
  Scenario: Confirm that the selected course has data linked to it
    When I navigate to the Student Eligibility & Prerequisite section for course "BSCI202" in the historic term
    Then the Compare to Catalog link should exist for the Student Eligibility & Prerequisite section

  #ELIG7.4.EB2 (KSENROLL-7174)
  Scenario: Confirm that the selected course's natural language displays correctly
    When I navigate to the Student Eligibility & Prerequisite section for course "BSCI202" in the historic term
    Then the agenda page's text should match "Must have successfully completed BSCI201"
    When I want to edit the Student Eligibility & Prerequisite section
    Then the edit tab's text should match "Permission of CMNS-Biology required"

  #ELIG7.4.EB3 (KSENROLL-7174)
  Scenario: Confirm that the selected course's CO and CLU have the same text
    When I navigate to the Student Eligibility & Prerequisite section for course "BSCI202" in the historic term
    And I want to compare the CO to the CLU for the Student Eligibility & Prerequisite section
    Then the CO and CLU should both have text "Must have successfully completed BSCI201,Permission of CMNS-Biology required"

  #ELIG7.4.EB4 (KSENROLL-7174)
  Scenario: Confirm that there is a warning message after editing CO
    When I navigate to the Student Eligibility & Prerequisite section for course "BSCI202" in the historic term
    And I want to edit the Student Eligibility & Prerequisite section
    And I add a course statement after node "B" with course "HIST210"
    Then the info message "Course Offering Rule differs from Catalog Rule" should be present
    And I commit changes made to the proposition

  #ELIG7.4.EB5 (KSENROLL-7174)
  Scenario: Confirm that the selected course's CO and CLU have the different text after edit
    When I navigate to the Student Eligibility & Prerequisite section for course "BSCI202" in the historic term
    And I want to edit the Student Eligibility & Prerequisite section
    And I add a course statement after node "B" with course "HIST210"
    And I commit and return to see the changes made to the proposition
    And I want to compare the CO to the CLU for the Student Eligibility & Prerequisite section
    Then the CO and CLU should differ with text "Must have successfully completed HIST210"
