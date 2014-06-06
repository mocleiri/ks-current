class CmProposalComments < BasePage

  wrapper_elements
  cm_elements

  element(:comment_text_input) {|b| b.text_field(id: 'commentsTextArea_control') }
  element(:comment_edit_link) {|index,b|b.a(id: "Comment-Edit-NavigationActionLink_line#{index}")}
  element(:comment_delete_link) {|index,b|b.a(id: "Comment-Delete-NavigationActionLink_line#{index}")}
  element(:edit_comment_text_field) {|b|b.text_field(id: 'KS-EditCommentSection-InputField_control')}

  action(:add_comment) { |b| b.button(text: 'Add Comment').click; b.loading_wait }
  action(:close_dialog) { |b| b.button(id: 'commentsLightBoxContents-cancel').click }
  action(:close_comment_dialog) { |b| b.a(title: "Close").click }

  action(:save_edited_comment) { |b| b.button(id: 'EditCommentSection-Save').click; b.loading_wait }
  action(:cancel_edited_comment) { |b| b.button(id: 'cancel').click; b.loading_wait }
  action(:edit_comment) {|index,b|b.a(id: "Comment-Edit-NavigationActionLink_line#{index}").click}
  action(:delete_comment) {|index,b|b.a(id: "Comment-Delete-NavigationActionLink_line#{index}").click}

  value(:comment_list_header_text) {|b|b.div(id: "Comment_list_Header").text}
  value(:comment_content_text) {|index,b|b.div(id: "commentsDisplayArea_line#{index}").text}

end