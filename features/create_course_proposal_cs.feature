@wip
Feature: Create a Course Proposal as a Curriculum Specialist

Background:
Given I am logged in as Curriculum Specialist

#CC.1.1 "Create from blank" from initiation screen and "Continue" presents a blank proposal page (course title and proposal title are blank")- DONE

  Scenario:  CC.1 Create Course Proposal from blank
When I create a course proposal from blank
Then I should see a blank course proposal

#CC.1.2 "Create from blank" from initiation screen and "Cancel" returns user to CM Home
Scenario: CC.2 Cancel Create a Course proposal process
When I create a course proposal
And I cancel create a course
Then I should see CM Home

#CC.3 - "Create from blank" and "use curric review" from initiation screen and "Continue" presents an all in one screen data entry form with "show all/hide" yellow bar, and when alice completes "proposal title" and "Course title" and clicks "Save Progress", verify success message and data persists in those two "required for save" fields)
Scenario: CC.3 Create a course proposal with only required fields
When When I complete the required fields for save on the course proposal
Then I should see data in required fields for the course proposal
