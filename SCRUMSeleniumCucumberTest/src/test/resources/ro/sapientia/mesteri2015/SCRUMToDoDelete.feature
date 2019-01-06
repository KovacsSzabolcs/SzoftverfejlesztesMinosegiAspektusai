Feature: Check scrum story to do update


   Scenario: Title1
   Given I select the first storys first to do
   When I delete the selected to do from list
   Then I should have 1 less to do