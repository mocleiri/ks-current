class EmulatorActivePage < PageFactory

  page_url "http://quirktools.com/screenfly/#u=http%3A//env13.ks.kuali.org/kscr-poc/index.jsp&w=1024&h=600"
  expected_element :tools_div

  element(:tools_div) { |b| b.div(id: "screenfly-tools") }
  element(:desktop_icon) { |b| b.link(id: "size-desktop") }
  element(:tablet_icon) { |b| b.link(id: "size-tablet") }
  element(:mobile_icon) { |b| b.link(id: "size-mobile") }
  element(:iphone_link) { |b| b.link(href: "#320x568x37")}
  element(:ipad_link) { |b| b.link(href: "#768x1024x22")}
  action(:mobile) { |b| b.mobile_icon.click}
  action(:iphone) { |b| b.iphone_link.click}
  action(:tablet) { |b| b.tablet_icon.click}
  action(:ipad) { |b| b.ipad_link.click}

  def mobile_iphone
    # click on the mobile link, a dropdown appears, then click on iphone
    mobile
    iphone_link.wait_until_present
    iphone
  end

  def tablet_ipad
    # click on the tablet link, a dropdown appears, then click on ipad
    tablet
    ipad_link.wait_until_present
    ipad
  end

end