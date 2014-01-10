class PerformanceTest

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  attr_accessor :start_time,
                :end_time,
                :test_time

  def initialize(browser, opts={})
    @browser = browser

    defaults = {
        :start_time=> 1000,
        :end_time=> 0,
        :test_time => 300
    }
    options = defaults.merge(opts)
    set_options(options)
  end


  def start
    @start_time = Time.new
  end

  def end
    @end_time = Time.new
    test_calc
  end

  def test_calc
    @test_time = @end_time - @start_time
  end

end