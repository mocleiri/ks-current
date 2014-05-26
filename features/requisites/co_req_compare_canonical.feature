@nightly @blue_team_krms
Feature: CO.ELIG7-4 Compare to Canonical

  Background:
    Given I am logged in as admin
    And I navigate to the Student Eligibility & Prerequisite section for course "BSCI202" in the historic term

  #KSENROLL-7174
  Scenario: ELIG7.4.1 Confirm that the selected course has data linked to it
    Then the Compare to Catalog link should exist for the Student Eligibility & Prerequisite section

  #KSENROLL-7174
  Scenario: ELIG7.4.2 Confirm that the selected course's natural language displays correctly
    Then the agenda page's text should match "Must have successfully completed BSCI201"
    When I want to edit the Student Eligibility & Prerequisite section
    Then the edit tab's text should match "Permission of CMNS-Biology required"

  #KSENROLL-7174
  Scenario: ELIG7.4.3 Confirm that the selected course's CO and CLU have the same text
    When I want to compare the CO to the CLU for the Student Eligibility & Prerequisite section
    Then the CO and CLU should both have text "Must have successfully completed BSCI201,Permission of CMNS-Biology required"

  #KSENROLL-7174
  Scenario: ELIG7.4.4 Confirm that there is a warning message after editing CO
    When I want to edit the Student Eligibility & Prerequisite section
    And I add a course statement after node "B" with course "HIST210"
    Then the info message "Course Offering Rule differs from Catalog Rule" should be present

  #KSENROLL-7174
  Scenario: ELIG7.4.5 Confirm that the selected course's CO and CLU have the different text after edit
    When I want to edit the Student Eligibility & Prerequisite section
    And I add a course statement after node "B" with course "HIST210"
    And I persist the changes and return to the proposition
    And I want to compare the CO to the CLU for the Student Eligibility & Prerequisite section
    Then the CO and CLU should differ with text "Must have successfully completed HIST210"
