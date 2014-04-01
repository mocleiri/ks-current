When /^I remove the seat pool with priority 1$/ do
  @activity_offering.seat_pool_list[1].priority_after_reseq = 1
  @activity_offering.seat_pool_list[2].priority_after_reseq = 2
  @activity_offering.seat_pool_list[0].delete
end


When /^I remove all seat pools$/ do
  @activity_offering.seat_pool_list[2].delete
  @activity_offering.seat_pool_list[1].delete
  @activity_offering.seat_pool_list[0].delete
end

When /^the seat pool priorities are re-sequenced$/ do
  #actually is implemented in the edit step and check in the 'the activity offering is updated when saved' step
end

And /^the seat pools? (?:are|is) removed$/ do
  #actually is implemented in the edit step and check in the 'the activity offering is updated when saved' step
end