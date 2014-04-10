class AffiliatedOrgObject

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  attr_accessor :org_id,
                :org_name

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :org_id => "ORGID-BIOL",
        :org_name => "Biology"
    }
    options = defaults.merge(opts)
    set_options(options)
  end

end

class AffiliatedOrgCollection < CollectionsFactory
  contains AffiliatedOrgObject
end