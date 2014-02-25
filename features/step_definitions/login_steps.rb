Given /^I am logged in as admin$/ do
    log_in "admin", "admin"
end

Given /^I am logged in as a Schedule Coordinator/ do
    log_in "martha", "martha"
end

Given /^I am logged in as a Student/ do
      log_in "student", "student"
end

Given /^I am logged in as a Department Schedule Coordinator$/ do
    log_in "carol", "carol"
end

Given /^I am logged in as admin with locale: (.*)$/ do |locale|
  client = Selenium::WebDriver::Remote::Http::Default.new
  language_profile = Selenium::WebDriver::Firefox::Profile.new

  puts "Creating new browser with #{locale}"
  language_profile["intl.accept_languages"] = locale

  #ensure there is no existing browser session
  @browser.close
  @browser = Watir::Browser.new :firefox, :http_client => client, :profile => language_profile

  log_in "admin", "admin"
end