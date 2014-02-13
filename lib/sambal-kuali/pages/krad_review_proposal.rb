class KradReviewProposal < BasePage

  wrapper_elements
  krad_elements

  action(:submit) { |b| b.button(text: 'Submit').click; b.loading_wait }
  action(:blanket_approve) { |b| b.button(text: 'Blanket Approve').click }

end