module Utilities

  include StringFactory

  def get(item)
    instance_variable_get(snakify(item))
  end

  def set(item, obj)
    instance_variable_set(snakify(item), obj)
  end

  def snake_case(string)
    StringFactory.damballa(string)
  end

  private

  def snakify(item)
    if item.to_s[0]=='@'
      item
    else
      "@#{snake_case(item.to_s)}"
    end
  end
end




#added these to wrapper_elements on kuali_base_page, saved here for reference
#element(:saving) { |b| b.img(alt: /^Saving/) }
#action(:saving_wait) {|b| b.saving.wait_while_present }
#action(:auto_lookup_link) { |the_lookup, b| b.link(text: the_lookup) }
#action(:auto_lookup) { |the_lookup, b| b.auto_lookup_link(the_lookup).when_present.click }
