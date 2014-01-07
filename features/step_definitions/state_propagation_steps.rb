When /^I run the State Propagation test$/ do
  @state_propagation = make StatePropagation
  @state_propagation.perform_propagation_test
end

Then /^all State Propagation test rows should have status pass$/ do
  on StatePropagation do |page|
    page.read_results_tables.should be_true
  end
end
