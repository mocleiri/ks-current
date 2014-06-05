class CmAdminOrgObject < DataFactory

  include Foundry
  include DateFactory
  include StringFactory
  include Workflows
  include Utilities

  attr_reader :admin_org_name,
              :admin_org_level




  def initialize(browser, opts={})
    @browser = browser
    defaults = {
        admin_org_name: "Forage",
        admin_org_level: 1
    }
    set_options(defaults.merge(opts))

  end

  def create
    on CmGovernance do |page|
      page.governance unless page.current_page('Governance').exists?
      page.organization_add unless page.admin_org_name(@admin_org_level).exists?
      page.admin_org_name(@admin_org_level).set @admin_org_name
      page.auto_lookup @admin_org_name
      @admin_org_name = page.admin_org_name(@admin_org_level).value
    end
  end





  def edit (opts={})

    set_options(opts)
  end



  def delete

  end


end


