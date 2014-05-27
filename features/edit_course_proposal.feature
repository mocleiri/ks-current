@nightly
Feature: GT.Edit Course Proposal for both Fred and CS

Scenario: CC8.1 Edit the Course Proposal - CS
  Given I have a course admin proposal created as Curriculum Specialist
  When I perform a full search for the course proposal
  And I edit the course proposal
  Then I should see updated data on the Review proposal page


Scenario: CC8.2 Edit the Course Proposal - Faculty
  Given I have a course proposal created as Faculty
  When I perform a full search for the course proposal
  Then I edit the course proposal
  And I should see updated data on Review proposal page


Scenario: CC8.3 Edit the faculty proposal - CS
  Given I have a course proposal created as Faculty
  When I am logged in as Curriculum Specialist
  Then I perform a search for the course proposal
  And I edit the course proposal
  And I should see updated data on the Review proposal page


Scenario: CC8.4 Faculty cannot edit CS proposal
  Given I have a course admin proposal created as Curriculum Specialist
  When I am logged in as Faculty
  Then I perform a search for the course proposal
  And I should not see the edit option in the search results for the Course Admin Proposal