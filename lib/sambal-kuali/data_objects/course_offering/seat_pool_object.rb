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
                :exp_add_succeed,
                :parent_ao

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
        :priority_after_reseq => 1,
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

  # create - add seatpool to activity offering
  #
  # @param [Array] list of populations used in seatpools already added
  def create(pops_used_list)
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

  #while on activity offering edit page and updates the details for a specific seatpool
  # @example - must always call save
  #  @activity_offering.edit :honors_course=> true
  #  @activity_offering.edit_seatpool :seats => 2
  #  @activity_offering.save
  #
  # NB: 'save' is a separate step from edit as it allows validation steps to occur during the edit process
  #
  # @param opts [Hash] key => value for attribute to be updated
  def edit opts = {}

    defaults = {
        :defer_save => false,
        :edit_already_started => false,
    }
    options = defaults.merge(opts)

    @parent_ao.edit unless options[:edit_already_started]

    if !options[:seats].nil?
      on ActivityOfferingMaintenance do |page|
        page.update_seats(@population_name, options[:seats])
      end
    end

    if !options[:priority].nil?
      on ActivityOfferingMaintenance do |page|
        page.update_priority(@population_name,options[:priority])
      end
    end

    if !options[:expiration_milestone].nil?
      on ActivityOfferingMaintenance do |page|
        page.update_expiration_milestone(@population_name,options[:expiration_milestone])
      end
    end

    if !options[:priority_after_reseq].nil?
      @priority_after_reseq = options[:priority_after_reseq]
    end

    update_options(options)

    @parent_ao.save unless options[:defer_save]
  end

  #removes seatpool from activity offering
  #
  #@param [string] seat_pool_list hash key
  #TODO: if this works, get rid of parent.remove_seatpool
  def delete
    on ActivityOfferingMaintenance do |page|
      page.remove_seatpool(@population_name)
    end
    @parent_ao.seat_pool_list.delete(self)
  end


end

class SeatPoolCollection < CollectionsFactory

  contains SeatPoolObject

  def by_population(population_name)
    list = self.select {|sp| sp.population_name == population_name }
    list.nil?? nil : list[0]
  end
end