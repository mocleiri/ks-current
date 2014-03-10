@Draft
Feature: BT.Search_result_sort

  Background:
         Given I am logged in as a Student
          And I search for a course on course search

       Scenario: CS 14.1 Successfully sorting the course code in both ascending and descending order.
         When I click the sort icon near Code column header in the table
         Then the  course code listed should be sorted in descending order
         When I again click on the sort icon near Code column header in the table
         Then the  course code listed should be sorted in ascending order




       Scenario: CS 14.2 Successfully sorting the course Title in both ascending and descending order.
         When I click the sort icon near Title column header in the table
         Then the  course Title listed should be sorted in ascending order
         When I again click on the sort icon near Title column header in the table
         Then the  course Title listed should be sorted in descending order

