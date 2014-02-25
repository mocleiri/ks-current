When /^I view the POC page with "(.*?)" locale$/ do |text|
  @i18n_poc = make I18N_POC, locale: text
  @i18n_poc.navigate
end

Then /^the page header will be "(.*?)"$/ do |text|
  on I18NPOCPage do |page|
    page.page_header.should == text
  end
  @i18n_poc.endSession
end