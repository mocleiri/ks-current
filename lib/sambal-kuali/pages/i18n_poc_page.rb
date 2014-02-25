class I18NPOCPage < BasePage

  page_url "#{$test_site}/kr-krad/rb?methodToCall=startRB&viewId=POCResourceBundle-FormView"

  wrapper_elements
  frame_element

  #element(:page_header){ |b| b.div(id: /POCResourceBundle-FormView/) }
  element(:page_header){ |b| b.div(id: /POCResourceBundle-FormView/).div().div().h2().span().text }

end