class FindProposalPage < BasePage

cm_elements

#expected_element :name


element(:name) { |b| b.text_field(name: "lookupCriteria[title]") }
element(:lookup_results) { |b| b.div(id: "uLookupResults") }
action(:find_a_proposal) { |b| b.button(id: "courseProposalLookup_searchButton").click; b.loading_wait }
action(:proposal_title_element)  { |proposal_title,b| b.div(id:"#{proposal_title}" ).span(class: "uif-readOnlyContent") }
action(:proposal_title_text)  { |proposal_title,b| b.div(id:"#{proposal_title}" ).span(class: "uif-readOnlyContent").text }
action(:review_proposal_action_link) { |proposal_title,b| b.a(id: "#{proposal_title}_view").i(class: "ks-fontello-icon-eye" ).click }
action(:edit_proposal_element) { |proposal_title,b| b.a(id: "#{proposal_title}_edit").i(class: "ks-fontello-icon-pencil") }
action(:edit_proposal_action) { |proposal_title,b| b.a(id: "#{proposal_title}_edit").i(class: "ks-fontello-icon-pencil").click }

end