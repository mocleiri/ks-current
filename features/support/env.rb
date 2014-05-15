#$test_site = "http://localhost:8081/ks-with-rice-bundled-dev" #Local Env
$test_site = "http://env2.ks.kuali.org"
$test_site = ENV['TEST_SITE'] unless ENV['TEST_SITE'] == nil

$: << File.dirname(__FILE__)+'/../../lib'

require 'sambal-kuali'

World Foundry
World StringFactory
World DateFactory
World Workflows

browser = nil
headless = nil

if ENV['TEST_ENV_NUMBER'].nil? || ENV['TEST_ENV_NUMBER'] == ''
  $env_no = 1
else
  $env_no = ENV['TEST_ENV_NUMBER']
end

Selenium::WebDriver::Firefox::Binary.path = ENV['FIREFOX_PATH'] unless ENV['FIREFOX_PATH'].nil?

if ENV['HEADLESS']
  require 'headless'
  #if using parallel processes, start new headless instance for each process
  headless_display = 100 + $env_no.to_i
  headless = Headless.new(:reuse => false, :destroy_at_exit => true, :display => headless_display)
  headless.start
end

#re-use browser for each scenario
Before do
  if browser.nil?
    puts "debug  env.rb [#{$env_no}] - creating new browser"
    browser = get_browser_connection
    puts "debug  env.rb [#{$env_no}] - browser.nil? #{browser.nil?}"
  end
  @browser = browser
end

at_exit { browser.close unless browser.nil? }

if ENV['HEADLESS']
  #re-start browser after each failed scenario
  After do | scenario |
    if scenario.failed?
      begin
        screenshot_img = @browser.driver.save_screenshot("./failure#{$env_no}.png")
        embed(screenshot_img, 'data:image/png')
      rescue Exception => e
        puts "debug env.rb [#{$env_no}] - screenshot failed - #{e.message}"
      end
      @browser.close unless @browser.nil?
      browser = nil
    end
  end
end

def get_browser_connection
  client = Selenium::WebDriver::Remote::Http::Default.new
  #client.timeout = 15 # seconds default is 60

  #Profile Proxy Configuration
  #profile = Selenium::WebDriver::Firefox::Profile.new
  #profile.proxy = Selenium::WebDriver::Proxy.new :http => 'localhost:8001'
  #, :profile => profile

  the_browser = nil
  retry_ctr = 0
  while the_browser.nil? and retry_ctr < 10
    retry_ctr += 1
    puts "debug env.rb [#{$env_no}] - browser init - attempt #{retry_ctr}"
    begin
      the_browser = Watir::Browser.new :firefox , :http_client => client
      the_browser.goto("#{$test_site}/login.jsp")
      sleep 2
      raise "connect failed" unless  the_browser.text_field(id: "j_username").exists?
      #browser.goto('https://www.whatismybrowser.com/')
      #puts browser.div(class: 'simple-browser-string').text
      puts "debug env.rb [#{$env_no}] - success: browser connection attempt #{retry_ctr}"
    rescue
      puts "debug env.rb [#{$env_no}] - connect failed"
      the_browser.close unless the_browser.nil?
      the_browser = nil
      sleep 30
    end
  end
  the_browser
end