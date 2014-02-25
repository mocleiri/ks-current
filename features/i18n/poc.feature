@nightly
Feature: BT.i18n

    @draft @wip
  Scenario Outline: View page in appropriate locale
    Given I am logged in as admin with locale: <locale>
    When I view the POC page with "<locale>" locale
    Then the page header will be "<page_header>"
  Examples:
    | locale      | page_header                    |
    | en          | POC for Resource Bundles       |
    | fr,en       | fr_POC for Resource Bundles    |
    | fr-fr,fr,en | fr_FR_POC for Resource Bundles |
    # Do a second pass to make sure that everything loads up correctly from the ResourceBundle caches
    | en          | POC for Resource Bundles       |
    | fr,en       | fr_POC for Resource Bundles    |
    | fr-fr,fr,en | fr_FR_POC for Resource Bundles |