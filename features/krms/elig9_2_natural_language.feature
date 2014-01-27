@nightly @blue_team
Feature: CO.ELIG9-2 Natural Language

  Background:
    Given I am logged in as admin

  #ELIG9.2.EB1.1 (KSENROLL-6954)
  Scenario: Confirm that the natural language displays all rules correctly for Recommended Preparation - rule one
    When I navigate to the Recommended Preparation section for course "ENGL304" in the future term
    And I want to add a new statement to the Recommended Preparation section
    And I add a new course statement with course "HIST210"
    Then both tabs' text should match "Must have successfully completed HIST210"
    When I update the manage course offering agendas page
    Then the agenda page's text should before and after the submit match "Must have successfully completed HIST210"
    When I want to edit the Recommended Preparation section
    Then both tabs' text should match "Must have successfully completed HIST210"

  #ELIG9.2.EB1.2 (KSENROLL-6954)
  Scenario: Confirm that the natural language displays all rules correctly for Recommended Preparation - rule two
    When I navigate to the Recommended Preparation section for course "ENGL304" in the future term
    And I want to edit the Recommended Preparation section
    And I add a courses statement after node "A" with courses "HIST110,HIST210" and course sets "CORE: Life Science Lab-Linked Courses (LL)"
    Then the edit tab's text should match "Must have successfully completed all courses from (BSCI124, HIST110, HIST210)"
    When I switch to the other tab on the page
    Then the logic tab's text should match "Must have successfully completed all courses from (HIST110, HIST210, BSCI124)"
    When I update the manage course offering agendas page
    Then the agenda page's text should before and after the submit match "Must have successfully completed all courses from,HIST110,HIST210,CORE: Life Science Lab-Linked Courses"
    When I want to edit the Recommended Preparation section
    Then the edit tab's text should match "Must have successfully completed all courses from (BSCI124, HIST110, HIST210)"
    When I switch to the other tab on the page
    Then the logic tab's text should match "Must have successfully completed all courses from (HIST110, HIST210, BSCI124)"

  #ELIG9.2.EB1.3 (KSENROLL-6954)
  Scenario: Confirm that the natural language displays all rules correctly for Recommended Preparation - rule three
    When I navigate to the Recommended Preparation section for course "ENGL304" in the future term
    And I want to edit the Recommended Preparation section
    And I add a number of courses statement after node "B" with number "1" and courses "HIST213,HIST204,HIST208" and course sets "CORE: Life Science Lab-Linked Courses (LL)"
    Then the edit tab's text should match "Must have successfully completed a minimum of 1 course from (BSCI124, HIST204, HIST208, HIST213)"
    When I switch to the other tab on the page
    Then the logic tab's text should match "Must have successfully completed a minimum of 1 course from (HIST204, HIST208, HIST213, BSCI124)"
    When I update the manage course offering agendas page
    Then the agenda page's text should before and after the submit match "Must have successfully completed a minimum of 1 course from,HIST204,HIST208,HIST213,CORE: Life Science Lab-Linked Courses"
    When I want to edit the Recommended Preparation section
    Then the edit tab's text should match "Must have successfully completed a minimum of 1 course from (BSCI124, HIST204, HIST208, HIST213)"
    When I switch to the other tab on the page
    Then the logic tab's text should match "Must have successfully completed a minimum of 1 course from (HIST204, HIST208, HIST213, BSCI124)"

  #ELIG9.2.EB1.4 (KSENROLL-6954)
  Scenario: Confirm that the natural language displays all rules correctly for Recommended Preparation - rule four
    When I navigate to the Recommended Preparation section for course "ENGL304" in the future term
    And I want to edit the Recommended Preparation section
    And I add a number of courses statement after node "C" with number "1" and courses "HIST250,HIST798" and course sets "General Education: Fundamental Studies-Professional Writing"
    Then the edit tab's text should match "Must have successfully completed a minimum of 1 course from (ENGL381, ENGL390, ENGL391, ENGL392, ENGL393, ENGL394, ENGL395, HIST250, HIST798)"
    When I switch to the other tab on the page
    Then the logic tab's text should match "Must have successfully completed a minimum of 1 course from (HIST250, HIST798, ENGL381, ENGL390, ENGL391, ENGL392, ENGL393, ENGL394, ENGL395)"
    When I update the manage course offering agendas page
    Then the agenda page's text should before and after the submit match "Must have successfully completed a minimum of 1 course from,HIST250,HIST798,General Education: Fundamental Studies-Professional Writing"
    When I want to edit the Recommended Preparation section
    Then the edit tab's text should match "Must have successfully completed a minimum of 1 course from (ENGL381, ENGL390, ENGL391, ENGL392, ENGL393, ENGL394, ENGL395, HIST250, HIST798)"
    When I switch to the other tab on the page
    Then the logic tab's text should match "Must have successfully completed a minimum of 1 course from (HIST250, HIST798, ENGL381, ENGL390, ENGL391, ENGL392, ENGL393, ENGL394, ENGL395)"

  #ELIG9.2.EB2 (KSENROLL-6954)
  Scenario: Confirm that the natural language displays all rules correctly for Antirequisite - rule one
    When I navigate to the Antirequisite section for course "ENGL304" in the future term
    And I want to add a new statement to the Antirequisite section
    And I add a new not completed course statement with course "HIST250"
    Then both tabs' text should match "Must not have successfully completed HIST250"
    When I update the manage course offering agendas page
    Then the agenda page's text should before and after the submit match "Must not have successfully completed HIST250"
    When I want to edit the Antirequisite section
    Then both tabs' text should match "Must not have successfully completed HIST250"

  #ELIG9.2.EB3.1 (KSENROLL-6954)
  Scenario: Confirm that the natural language displays all rules correctly for Corequisite - rule one
    When I navigate to the Corequisite section for course "ENGL304" in the future term
    And I want to add a new statement to the Corequisite section
    And I add a new concurrently enrolled courses statement with courses "HIST250,HIST798" and course sets "General Education: Fundamental Studies-Professional Writing"
    Then the edit tab's text should match "Must be concurrently enrolled in all courses from (ENGL381, ENGL390, ENGL391, ENGL392, ENGL393, ENGL394, ENGL395, HIST250, HIST798)"
    When I switch to the other tab on the page
    Then the logic tab's text should match "Must be concurrently enrolled in all courses from (HIST250, HIST798, ENGL381, ENGL390, ENGL391, ENGL392, ENGL393, ENGL394, ENGL395)"
    When I update the manage course offering agendas page
    Then the agenda page's text should before and after the submit match "Must be concurrently enrolled in all courses from,HIST250,HIST798,General Education: Fundamental Studies-Professional Writing"
    When I want to edit the Corequisite section
    Then the edit tab's text should match "Must be concurrently enrolled in all courses from (ENGL381, ENGL390, ENGL391, ENGL392, ENGL393, ENGL394, ENGL395, HIST250, HIST798)"
    When I switch to the other tab on the page
    Then the logic tab's text should match "Must be concurrently enrolled in all courses from (HIST250, HIST798, ENGL381, ENGL390, ENGL391, ENGL392, ENGL393, ENGL394, ENGL395)"

  #ELIG9.2.EB3.2 (KSENROLL-6954)
  Scenario: Confirm that the natural language displays all rules correctly for Corequisite - rule two
    When I navigate to the Corequisite section for course "ENGL304" in the future term
    And I want to edit the Corequisite section
    And I add a concurrently enrolled number of courses statement after node "A" with number "1" and courses "HIST250,HIST210" and course sets "General Education: Fundamental Studies-Professional Writing"
    Then the edit tab's text should match "Must be concurrently enrolled in a minimum of 1 course from (ENGL381, ENGL390, ENGL391, ENGL392, ENGL393, ENGL394, ENGL395, HIST210, HIST250)"
    When I switch to the other tab on the page
    Then the logic tab's text should match "Must be concurrently enrolled in a minimum of 1 course from (HIST210, HIST250, ENGL381, ENGL390, ENGL391, ENGL392, ENGL393, ENGL394, ENGL395)"
    When I update the manage course offering agendas page
    Then the agenda page's text should before and after the submit match "Must be concurrently enrolled in a minimum of 1 course from,HIST210,HIST250,General Education: Fundamental Studies-Professional Writing"
    When I want to edit the Corequisite section
    Then the edit tab's text should match "Must be concurrently enrolled in a minimum of 1 course from (ENGL381, ENGL390, ENGL391, ENGL392, ENGL393, ENGL394, ENGL395, HIST210, HIST250)"
    When I switch to the other tab on the page
    Then the logic tab's text should match "Must be concurrently enrolled in a minimum of 1 course from (HIST210, HIST250, ENGL381, ENGL390, ENGL391, ENGL392, ENGL393, ENGL394, ENGL395)"

  #ELIG9.2.EB3.3 (KSENROLL-6954)
  Scenario: Confirm that the natural language displays all rules correctly for Corequisite - rule three
    When I navigate to the Corequisite section for course "ENGL304" in the future term
    And I want to edit the Corequisite section
    And I add a concurrently enrolled course statement after node "B" with course "HIST798"
    Then both tabs' text should match "Must be concurrently enrolled in HIST798"
    When I update the manage course offering agendas page
    Then the agenda page's text should before and after the submit match "Must be concurrently enrolled in HIST798"
    When I want to edit the Corequisite section
    Then both tabs' text should match "Must be concurrently enrolled in HIST798"
