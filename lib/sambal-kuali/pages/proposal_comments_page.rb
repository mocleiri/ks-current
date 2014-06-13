class CmProposalComments < BasePage

  wrapper_elements
  cm_elements

  def frm
    self.iframe(class: "fancybox-iframe")
  end

  element(:comment_text_input) {|b| b.frm.text_field(id: 'KSCM-NewCommentField_control') }
  element(:comment_edit_link) {|index,b|b.frm.a(id: "KSCM-CommentEditAction_line#{index}")}
  element(:comment_delete_link) {|index,b|b.frm.a(id: "KSCM-CommentDeleteAction_line#{index}")}
  element(:edit_comment_text_field) {|index,b|b.frm.text_field(id: "KSCM-CommentField_line#{index}_control") }
  element(:add_comment_button) { |b| b.frm.button(text: 'Add Comment')}

  action(:add_comment) { |b| b.frm.button(text: 'Add Comment').click; b.loading_wait }
  action(:close_dialog) { |b| b.frm.button(id: 'commentsLightBoxContents-cancel').click }
  action(:close_comment_dialog) { |b| b.frm.a(title: "Close").click }

  action(:save_edited_comment) { |index,b| b.frm.button(id: "KSCM-CommentSaveAction_line#{index}").click; b.loading_wait }
  action(:cancel_edited_comment) { |index,b| b.frm.button(id: "cancel_line#{index}").click; b.loading_wait }
  action(:edit_comment) {|index,b|b.frm.a(id: "KSCM-CommentEditAction_line#{index}").click}
  action(:delete_comment) {|index,b|b.frm.a(id: "KSCM-CommentDeleteAction_line#{index}").click}

  value(:comment_list_header_text) {|b|b.frm.header(id: "Comment_list_Header").text}
  value(:comment_content_text) {|index,b|b.frm.div(id: "KSCM-CommentField_line#{index}").text}

end