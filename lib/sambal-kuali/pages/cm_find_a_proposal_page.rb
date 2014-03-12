class FindProposalPage < BasePage

cm_elements

element(:name) { |b| b.text_field(name: "lookupCriteria[title]") }
element(:lookup_results) { |b| b.div(id: "uLookupResults") }
action(:find_a_proposal) { |b| b.div(id: "Uif-PageContentWrapper").button(class: "btn btn uif-boxLayoutHorizontalItem").click; b.loading_wait }
action(:proposal_title_element)  { |proposal_title,b| b.div(id:"#{proposal_title}" ).span(class: "uif-readOnlyContent") }
action(:proposal_title_text)  { |proposal_title,b| b.div(id:"#{proposal_title}" ).span(class: "uif-readOnlyContent").text }
action(:review_proposal_action_link) { |proposal_title,b| b.a(id: "#{proposal_title}_view").i(class: "ks-fontello-icon-eye" ).click }

expected_element :name


end