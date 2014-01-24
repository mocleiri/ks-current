class EmulatorPageActive < PageFactory

  page_url "http://quirktools.com/screenfly/#u=http%3A//env13.ks.kuali.org/kscr-poc/index.jsp&w=1024&h=600"
  expected_element :tools_div

  element(:tools_div) { |b| b.div(id: "screenfly-tools") }
  element(:desktop_icon) { |b| b.link(id: "size-desktop") }
  element(:tablet_icon) { |b| b.link(id: "size-tablet") }
  element(:mobile_icon) { |b| b.link(id: "size-mobile") }
  element(:iphone_link) { |b| b.link(text: "Apple iPhone 5 ")}
  action(:mobile) { |b| b.mobile_icon.click} #; b.loading.wait_while_present }  need new loading meth.
  action(:iphone) { |b| b.iphone_link.click} #; b.loading.wait_while_present }  need new loading meth.

  def mobile_iphone
    mobile
    iphone
  end

end