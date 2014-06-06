Given(/^I have a basic course proposal created as Faculty$/) do
  steps %{Given I am logged in as Faculty}
  @course_proposal = create CmCourseProposalObject, :create_new_proposal => false, :create_basic_propsal => true,
                            :proposal_title => random_alphanums(10,'test basic proposal title '),
                            :course_title => random_alphanums(10,'test basic course title ')
  @course_proposal.create_course_continue
  @course_proposal.create_basic_proposal
end

When(/^I add comments to the course proposal$/) do
  @comment_add = make CmCommentsObject
  @comment_add.commentText = random_alphanums(10,'test proposal comment')
  @course_proposal.load_comments_action
  sleep 10
  @comment_add.add_comment (@comment_add.commentText)
  @comment_add.close_comment_dialog
end

Then(/^I should see my comments on the course proposal$/) do
  on FindProposalPage do |page|
    page.edit_proposal_action(@course_proposal.proposal_title)
    page.loading_wait
  end
  @course_proposal.load_comments_action
end

Given(/^I have a basic course admin proposal created as Curriculum Specialist$/) do
  steps %{Given I am logged in as Curriculum Specialist}
  @course_proposal = create CmCourseProposalObject, :create_new_proposal => false, :create_basic_propsal => true,
                            :proposal_title => random_alphanums(10,'test basic proposal title '),
                            :course_title => random_alphanums(10,'test basic course title ')
  @course_proposal.create_course_continue
  @course_proposal.create_basic_proposal
end

Given(/^I have a basic course proposal with comments created as Faculty$/) do
  steps %{Given I have a basic course proposal created as Faculty}
  steps %{When I add comments to the course proposal}
end

When(/^I edit a comment$/) do
  @comment_edit = make CmCommentsObject
  @comment_edit.commentText = random_alphanums(10,'edit proposal comment')
  @course_proposal.load_comments_action
  sleep 10
  @comment_edit.edit_comment(0, @comment_edit.commentText)
  @comment_edit.close_comment_dialog
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