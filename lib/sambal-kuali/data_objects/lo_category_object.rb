class CmLoCategoryObject < DataFactory

  include Foundry
  include DateFactory
  include StringFactory
  include Workflows
  include Utilities

  attr_reader :category,
              :type


  def initialize(browser, opts={})
    @browser = browser
    defaults = {
        :category_name => random_alphanums(10,'category name '),
        :category_type => '::random::'
    }
    set_options(defaults.merge(opts))

  end

  def create
      navigate_to_lo_categories
      on CmLoCategoryPage do |page|
        page.add_category
          on AddLoCategoryPopup do |add_lo|
            add_lo.category_name.set @category_name
            add_lo.category_type.pick! @category_type
            add_lo.create_category
          end
        page.adding_line_wait
      end


  end





  def edit (opts={})

    set_options(opts)
  end



  def delete

  end


end


