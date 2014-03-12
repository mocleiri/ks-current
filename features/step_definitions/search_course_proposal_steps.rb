Given /^I have a course proposal created as CS$/ do
  steps %{Given I am logged in as Curriculum Specialist}
  @course_proposal = create CmCourseProposalObject, :curriculum_review_process => "Yes"
end

Given /^I have a course proposal created as Faculty$/ do
  steps %{Given I am logged in as Fred}
  @course_proposal = create CmCourseProposalObject
end

Given /^I have a admin course proposal created as CS and course proposal created as Faculty$/ do
  steps %{Given I am logged in as Curriculum Specialist}
  @course_proposal_cs = create CmCourseProposalObject, :proposal_title => "Alice Math class #{random_alphanums(10,'test proposal title ') }"
  puts "CS proposal title is #{@course_proposal_cs.proposal_title}"

  steps %{Given I am logged in as Fred}
  @course_proposal_faculty = create CmCourseProposalObject, :proposal_title => "Freds Math class #{random_alphanums(10,'test proposal title ') }"
  puts "Faculty proposal title is #{@course_proposal_faculty.proposal_title}"
end


When /^I perform a full search for the Course Proposals$/ do
  navigate_to_functional_home
  @course_proposal.search(@course_proposal.proposal_title)
end


Then /^I should see results matching my search criteria$/ do
on FindProposalPage do |page|
    page.proposal_title_element(@course_proposal.proposal_title).should exist
  end
end


Then /^I should see all results matching my search criteria$/ do
  on FindProposalPage do |page|
    page.proposal_title_element(@course_proposal_cs.proposal_title).should exist
    page.proposal_title_element(@course_proposal_faculty.proposal_title).should exist
  end
end

And /^I perform a partial search for Course Proposals$/ do
  #using part of the text that is common across both test proposals
  search_text = @course_proposal_cs.proposal_title.slice(6,15)
  @course_proposal_cs.search(search_text)
end


And /^I select my proposal from the results$/ do
  @course_proposal.review_proposal_action
end

And /^I select the (.*?) proposal from the results$/ do |proposal_to_select|
  if proposal_to_select == "CS"
    @course_proposal_cs.review_proposal_action
  else
    @course_proposal_faculty.review_proposal_action
  end
end

And /^I should see the Review Proposal page for that proposal$/ do
  on CmCourseInformation do |page|
    page.proposal_title_review.should == @course_proposal.proposal_title
    page.course_title_review.should == @course_proposal.course_title
  end
end

And /^I should see the Review Proposal page for the (.*?) proposal$/ do |proposal_author|
  on CmCourseInformation do |page|
    if proposal_author == "CS"
      page.proposal_title_review.should == @course_proposal_cs.proposal_title
      page.course_title_review.should == @course_proposal_cs.course_title
    else
      page.proposal_title_review.should == @course_proposal_faculty.proposal_title
      page.course_title_review.should == @course_proposal_faculty.course_title
    end
  end
end