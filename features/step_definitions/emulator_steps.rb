Given /^I am using a (mobile|tablet) screen size$/ do |deviceType|
  if deviceType=="mobile"
    @browser.window.resize_to(320, 568)
  elsif deviceType=="tablet"
    @browser.window.resize_to(600, 1024)
  end
  @browser.goto "#{$test_site}/kscr-poc/index.jsp"
end

Given /^I am using a tablet emulator website$/ do
  @browser.goto "http://quirktools.com/screenfly"
  on EmulatorMainPage do |page1|
    page1.display_reg_poc_website
  end
  on EmulatorActivePage do |page2|
    page2.tablet_ipad
  end
end

And /^I am logged into the POC site as admin$/ do
  on MobileLogin do |page|
    page.login_with "admin","admin"
  end
end