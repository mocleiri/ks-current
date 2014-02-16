class CmAuthorsCollaborators < BasePage

  wrapper_elements
  cm_elements

  element(:author_name) { |b| b.text_field(name: 'newCollectionLines[\'document.newMaintainableObject.collaboratorWrappers\'].displayName') }
  element(:author_permission) { |b| b.select_list(name: 'newCollectionLines[\'document.newMaintainableObject.collaboratorWrappers\'].permission') }
  element(:action_request) { |b| b.select_list(name: 'newCollectionLines[\'document.newMaintainableObject.collaboratorWrappers\'].action') }
  element(:author_notation) { |b| b.checkbox(name: 'newCollectionLines[\'document.newMaintainableObject.collaboratorWrappers\'].author') }
  action(:add_author) { |b| b.button(text: 'add').click; b.adding_line_wait }
  action(:delete_author) { |b| b.button(text: 'delete').click }
  action(:added_author_information) { |text_present, b| b.span(text: text_present) }
  # view, comment, edit, fyi, false, true, <complete_username>
end