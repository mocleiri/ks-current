@Draft
Feature: BT.Search_result_sort

  Background:
         Given I am logged in as a Student
          And I search for a course on course search

       Scenario: CS 14.1 Successfully sorting the course code in both ascending and descending order.
         When I sort the table by course code
         Then the course code listed should be sorted in "Descending" order
         When I sort the table by course code
         Then the course code listed should be sorted in "Ascending" order




       Scenario: CS 14.2 Successfully sorting the course Title in both ascending and descending order.
         When I sort the table by title
         Then the course Title listed should be sorted in "Ascending" order
         When I sort the table by title
         Then the course Title listed should be sorted in "Descending" order

