Given /^I have a course proposal created as Curriculum Specialist$/ do
  steps %{Given I am logged in as Curriculum Specialist}
  @course_proposal = create CmCourseProposalObject, :curriculum_review_process => "Yes"
end

Given /^I have a course proposal created as Faculty$/ do
  steps %{Given I am logged in as Fred}
  @course_proposal = create CmCourseProposalObject
end

Given /^I have a admin course proposal created as Curriculum Specialist and course proposal created as Faculty$/ do
  steps %{Given I am logged in as Curriculum Specialist}
  @course_proposal_cs = create CmCourseProposalObject, :proposal_title => "Alice Math class #{random_alphanums(10,'test proposal title ') }"
  puts "CS proposal title is #{@course_proposal_cs.proposal_title}"

  steps %{Given I am logged in as Fred}
  @course_proposal_faculty = create CmCourseProposalObject, :proposal_title => "Freds Math class #{random_alphanums(10,'test proposal title ') }"
  puts "Faculty proposal title is #{@course_proposal_faculty.proposal_title}"
end


When /^I perform a full search for the Course Proposal$/ do
  navigate_to_functional_home
  @course_proposal.search(@course_proposal.proposal_title)
end


Then /^I should see my proposal listed in the search result$/ do
on FindProposalPage do |page|
    page.proposal_title_element(@course_proposal.proposal_title).should exist
  end
end


Then /^I should see both proposals listed in the search result$/ do
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


And /^I can review the proposal created by (.*?)$/ do |proposal_to_review|

  if proposal_to_review == "Curriculum Specialist"
    @course_proposal_cs.review_proposal_action
    on CmCourseInformation do |page|
        page.proposal_title_review.should == @course_proposal_cs.proposal_title
        page.course_title_review.should == @course_proposal_cs.course_title
        page.page_header_text.should == "#{@course_proposal_cs.proposal_title} (Admin Proposal)"
    end
  else
    @course_proposal_faculty.review_proposal_action
    on CmCourseInformation do |page|
      page.proposal_title_review.should == @course_proposal_faculty.proposal_title
      page.course_title_review.should == @course_proposal_faculty.course_title
      page.page_header_text.should == "#{@course_proposal_faculty.proposal_title} (Proposal)"
    end
  end
end

And /^I can review my (.*?)$/ do |proposal_type|
  @course_proposal.review_proposal_action
  on CmCourseInformation do |page|
    page.proposal_title_review.should == @course_proposal.proposal_title
    page.course_title_review.should == @course_proposal.course_title
    if proposal_type == "proposal"
      page.page_header_text.should == "#{@course_proposal.proposal_title} (Proposal)"
    else
      page.page_header_text.should == "#{@course_proposal.proposal_title} (Admin Proposal)"
    end
  end
end


