Given /^I am using a mobile emulator website$/ do
  @browser.goto "http://quirktools.com/screenfly"
  on EmulatorPageMain do |page1|
    page1.display_website "env13.ks.kuali.org/kscr-poc/index.jsp"
  end
  on EmulatorPageActive do |page2|
    page2.mobile_iphone
  end
end

