class StatePropagation

  include Foundry
  include DataFactory
  include DateFactory
  include StringFactory
  include Workflows

  def initialize(browser, opts={})
    @browser = browser
  end

  def perform_propagation_test
    visit StatePropagationTest do |page|
      st_time = Time.new
      page.perform_test
      end_time = Time.new
      elapsed_time = end_time-st_time
      puts "State Propagation test time: #{elapsed_time.to_i} sec."
    end
  end

  def read_results_tables
    fail_count = 0
    test_passed = true
    on StatePropagationTest do |page|
      puts "", "Failed Tests:"

      puts "#{page.ao_results_table_header.text}"
      page.ao_results_table.rows.each do |row|
        if row.cells[StatePropagationTest::AO_TABLE_STATUS_COLUMN].text == "fail"
          puts "#{row.cells[StatePropagationTest::AO_TABLE_SOC_STATE_COLUMN].text} from #{row.cells[StatePropagationTest::AO_TABLE_AO_FROM_COLUMN].text} to #{row.cells[StatePropagationTest::AO_TABLE_AO_TO_COLUMN].text}"
          test_passed = false
          fail_count += 1
        end
      end

      puts "#{page.fo_results_table_header.text}"
      page.fo_results_table.rows.each do |row|
        if row.cells[StatePropagationTest::AO_TABLE_STATUS_COLUMN].text == "fail"
          puts "#{row.cells[StatePropagationTest::AO_TABLE_SOC_STATE_COLUMN].text} from #{row.cells[StatePropagationTest::AO_TABLE_AO_FROM_COLUMN].text} to #{row.cells[StatePropagationTest::AO_TABLE_AO_TO_COLUMN].text}"
          test_passed = false
          fail_count += 1
        end
      end

      puts "#{page.co_results_table_header.text}"
      page.co_results_table.rows.each do |row|
        if row.cells[StatePropagationTest::AO_TABLE_STATUS_COLUMN].text == "fail"
          puts "#{row.cells[StatePropagationTest::AO_TABLE_SOC_STATE_COLUMN].text} from #{row.cells[StatePropagationTest::AO_TABLE_AO_FROM_COLUMN].text} to #{row.cells[StatePropagationTest::AO_TABLE_AO_TO_COLUMN].text}"
          test_passed = false
          fail_count += 1
        end
      end

      puts "#{page.rg_ao_results_table_header.text}"
      page.rg_ao_results_table.rows.each do |row|
        if row.cells[StatePropagationTest::RG_AO_TABLE_STATUS_COLUMN].text == "fail"
          puts "A0 1 #{row.cells[StatePropagationTest::RG_AO_TABLE_AO_1_COLUMN].text}  AO 2 #{row.cells[StatePropagationTest::RG_AO_TABLE_AO_2_COLUMN].text}  AO 3 #{row.cells[StatePropagationTest::RG_AO_TABLE_AO_3_COLUMN].text}"
          test_passed = false
          fail_count += 1
        end
      end

      puts "#{page.rg_invalid_results_table_header.text}"
      page.rg_invalid_results_table.rows.each do |row|
        if row.cells[StatePropagationTest::RG_INVALID_TABLE_STATUS_COLUMN].text == "fail"
          puts "From #{row.cells[StatePropagationTest::RG_INVALID_TABLE_FROM_COLUMN].text} to #{row.cells[StatePropagationTest::RG_INVALID_TABLE_TO_COLUMN].text}"
          test_passed = false
          fail_count += 1
        end
      end

    end

    puts "#{fail_count} tests failed" if fail_count > 0
    return test_passed
  end

end