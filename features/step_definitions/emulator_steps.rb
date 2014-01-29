Given /^I am using a mobile emulator website$/ do
  @browser.goto "http://quirktools.com/screenfly"
  on EmulatorMainPage do |page1|
    page1.display_website
  end
  on EmulatorActivePage do |page2|
    page2.mobile_iphone
  end
end

Given /^I am using a tablet emulator website$/ do
  @browser.goto "http://quirktools.com/screenfly"
  on EmulatorMainPage do |page1|
    page1.display_website
  end
  on EmulatorActivePage do |page2|
    page2.tablet_ipad
  end
end

