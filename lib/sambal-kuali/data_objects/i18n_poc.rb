class I18N_POC

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows
  include Comparable

  attr_accessor :locale

  def initialize(browser, opts={})
    @browser = browser
    puts "I18N_POC.initialize"

    defaults = {
        :locale => "en_US"
    }

    options = defaults.merge(opts)
    set_options(options)
    puts "Testing locale: #{@locale}"

  end

  def navigate
    navigate_to_maintenance_portal
    navigate_to_i18n_poc_home
  end

  def endSession
    # Is it okay to do this here?
    # Close the browser since we are creating a new one for each test
    @browser.close
  end
end