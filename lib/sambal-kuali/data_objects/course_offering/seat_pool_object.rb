# stores test data for creating/editing and validating seatpool and provides convenience methods for navigation and data entry
#
# SeatPoolObject is a child of a ActivityOffering
#
# class attributes are initialized with default data unless values are explicitly provided
#
# Typical usage: (with optional setting of explicit data value in [] )
# seatpool_hash[1] = make SeatPoolObject, :population_name => "Core", :seats => 10, :priority => 2, :priority_after_reseq => 2
#
# @activity_offering = create ActivityOfferingObject, :seat_pool_list => seatpool_hash
#
#create generally called from ActivityOffering class
# Note the use of the ruby options hash pattern re: setting attribute values
class SeatPoolObject

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows
  include PopulationsSearch

  #generally set using options hash
  attr_accessor :priority,
                :seats,
                :population_name,
                :expiration_milestone,
                :remove,
                :priority_after_reseq,
                :exp_add_succeed

  alias_method :remove?, :remove
  alias_method :exp_add_succeed?, :exp_add_succeed

  # provides default data:
  #defaults = {
  #    :priority => 1,
  #    :seats => 10,
  #    :population_name => "random",
  #    :expiration_milestone => "First Day of Classes",
  #    :remove => false,
  #    :priority_after_reseq => 0,
  #    :exp_add_succeed => true
  #}
  # initialize is generally called using TestFactory Foundry .make or .create methods
  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :priority => 1,
        :seats => 10,
        :population_name => "random",
        :expiration_milestone => "First Day of Classes",
        :remove => false,
        :priority_after_reseq => 0,
        :exp_add_succeed => true
    }
    options = defaults.merge(opts)
    options[:priority_after_reseq] = options[:priority] unless options[:priority_after_reseq] != 0
    set_options(options)

  end

  #calculates expected % of total enrollment for the seatpool
  #
  # @returns [int] expected % of total enrollment
  def percent_of_total(max_enrollment)
    "#{(@seats.to_i*100/max_enrollment.to_i).round(0)}%"
  end

  # add_seatpool to activity offering
  #
  # @param [Array] list of populations used in seatpools already added
  def add_seatpool(pops_used_list)
    on ActivityOfferingMaintenance do |page|
      page.add_seat_pool unless page.add_pool_name == '' #check to see if blank line is already there
      page.add_pool_priority.set @priority
      page.add_pool_seats.set @seats
      page.add_pool_seats.fire_event "onblur"
      if @population_name != ""
        page.add_lookup_population_name

        #TODO should really call Population.search_for_pop
        on ActivePopulationLookup do |page|
          if @population_name == "random"
            page.keyword.wait_until_present
            #page.keyword.set random_letters(1)
            page.search
            #page.change_results_page(1+rand(3))
            #names = page.results_list
            #@population_name = names[1+rand(9)]
            @population_name = search_for_random_pop(pops_used_list)
            page.return_value @population_name
          else
            page.keyword.set @population_name
            page.search
            page.return_value @population_name
          end
        end

      end
      page.add_pool_expiration_milestone.select @expiration_milestone unless @expiration_milestone.nil?
    end
  end
end

class SeatPoolCollection < CollectionsFactory

  contains SeatPoolObject

end