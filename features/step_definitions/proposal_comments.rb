Given(/^I have a basic course proposal created as Faculty$/) do
  steps %{Given I am logged in as Faculty}
  @course_proposal = create CmCourseProposalObject, :create_new_proposal => false, :create_basic_propsal => true,
                            :proposal_title => random_alphanums(10,'test proposal comment title '),
                            :course_title => random_alphanums(10,'test proposal comment title ')
end

When(/^I add comments to the course proposal$/) do
  @course_proposal.load_comments_action
  sleep 10
  on CmProposalComments do |page|
    page.comment_text_input.set random_alphanums(10,'test proposal comment')
    page.add_comment
    sleep 2
    page.close_comment_dialog
  end

end

Then(/^I should see my comments on the course proposal$/) do
  @course_proposal.load_comments_action
end

Given(/^I have a basic course admin proposal created as Curriculum Specialist$/) do

end

Given(/^I have a basic course proposal with comments created as Faculty$/) do

end

When(/^I edit a comment$/) do

end

And(/^I delete my comments$/) do

end

Then(/^I should not see any comments on the course proposal$/) do

end

Given(/^I have a basic course admin proposal with comments created as CS$/) do

end

And(/^I undo delete of one comment$/) do

end

Then(/^I should see the undeleted comment on the course admin proposal$/) do

end

Given(/^I have a basic course proposal with comments created as Faculty with comments by CS$/) do

end

Then(/^I should see my comments and CS comments on the course proposal$/) do

end

And(/^I should not have edit or delete options for CS comments$/) do

end

Then(/^I should see CS comments on the course proposal$/) do

end

And(/^I should not have ability to add comments$/) do

end