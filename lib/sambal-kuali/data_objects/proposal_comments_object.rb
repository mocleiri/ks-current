class CmCommentsObject < DataFactory

  include Foundry
  include DateFactory
  include StringFactory
  include Workflows
  include Utilities

  attr_accessor :commenterId, :commentText, :comment_date




  def initialize(browser, opts={})
    @browser = browser
    defaults = {
        commenterId: "",
        commentText: "",
        comment_date: ""
    }
  end

  def add_comment (inputText)
    on CmProposalComments do |page|
      page.comment_text_input.set inputText
      page.add_comment
      page.loading_wait
    end
  end

  def edit_comment (index, newInput)
    on CmProposalComments do |page|
      page.edit_comment(index)
      if(page.alert.exists?)
        page.alert.ok
      end
      page.edit_comment_text_field.set newInput
      page.save_edited_comment
      sleep 5
    end
  end



  def delete (index)
    on CmProposalComments do |page|
      page.delete_comment(index)
      if(page.alert.exists?)
        page.alert.ok
      end
    end
  end

  def close_comment_dialog()
    on CmProposalComments do |page|
      page.close_dialog
    end
  end
end