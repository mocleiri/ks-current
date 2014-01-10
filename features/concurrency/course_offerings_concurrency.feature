@concurrency @yellow_team
Feature: EC.Course Offerings Concurrency
  Background:
    Given I have initiated two browser sessions with different users

  Scenario: Verify VersionMismatchError is displayed on concurrent course offering edits
    Given two users are concurrently editing the same course offering
    When the first user updates the suffix and saves the course offering
    And the second user updates the suffix and saves the course offering
    Then the update for the first user is successful
    And the second user gets a VersionMismatchError

  Scenario: Verify course offerings created simultaneously have unique auto-generated suffixes
    Given two users are concurrently copying the same course offering
    When the two users complete the copy at nearly the same instant
    Then the two new course offerings have unique suffixes


