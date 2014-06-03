class CmProposalComments < BasePage

  wrapper_elements
  cm_elements

  element(:comment_text_input) {|b| b.text_field(id: 'commentsTextArea_control') }
  action(:add_comment) { |b| b.button(text: 'Add Comment').click; b.loading_wait }
  action(:cancel) { |b| b.button(id: 'cancel').click }
  action(:close_comment_dialog) { |b| b.a(title: "Close").click }

end