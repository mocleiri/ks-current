@draft @deprecated @KSENROLL-8286 @red_team
Feature: EC.Jointly-defined Course Offering

  Background:
    Given I am logged in as a Schedule Coordinator

 # KSENROLL-8061
  Scenario: Create from Catalog a Course Offering which is defined in the CLU as Joint
    When I remove a joint course offering from the reference data in order to enable this test
    And I create a joint course offering from catalog while creating a new course offering
    Then The joint course offerings are created.

 # KSENROLL-8061
  Scenario: Cancel the deletion of a jointly-defined Course Offering with NO shared AOs
    When I create a new jointly defined Course Offering
    And I attempt to "delete and cancel" a joint Course Offering
    Then the Course Offering is "not deleted"

 # KSENROLL-8061
  Scenario: Delete jointly-defined Course Offering with NO shared AOs
    When I create a new jointly defined Course Offering
    And I attempt to "delete" a joint Course Offering
    Then the Course Offering is "deleted"


