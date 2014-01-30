Given /^I am using a mobile emulator website$/ do
  @browser.goto "http://quirktools.com/screenfly/#u=http%3A//#{$test_site}/kscr-poc/index.jsp"
  on EmulatorMainPage do |page1|
    page1.display_reg_poc_website
  end
  on EmulatorActivePage do |page2|
    page2.mobile_iphone
  end
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

And /^I am logged into the mobile site as admin$/ do
  on MobileLogin do |page|
    page.login_with "admin","admin"
  end
end