class RestStudentLogin < PageFactory

  page_url "#{$test_site}/services/DevelopmentLoginClientService/login?userId=student&password=student"

  end

class RestStudent1Login < PageFactory

  page_url "#{$test_site}/services/DevelopmentLoginClientService/login?userId=student1&password=student1"

  end

class RestStudent2Login < PageFactory

  page_url "#{$test_site}/services/DevelopmentLoginClientService/login?userId=student2&password=student2"

  end

class RestStudent3Login < PageFactory

  page_url "#{$test_site}/services/DevelopmentLoginClientService/login?userId=student3&password=student3"

  end

class RestStudent4Login < PageFactory

  page_url "#{$test_site}/services/DevelopmentLoginClientService/login?userId=student4&password=student4"

  end

class RestStudent5Login < PageFactory

  page_url "#{$test_site}/services/DevelopmentLoginClientService/login?userId=student5&password=student5"

  end

class RestMarthaLogin < PageFactory

  page_url "#{$test_site}/services/DevelopmentLoginClientService/login?userId=martha&password=martha"

end

class RestAdminLogin < PageFactory

  page_url "#{$test_site}/services/DevelopmentLoginClientService/login?userId=admin&password=admin"

end

class RestEILEENBLogin < PageFactory

  page_url "#{$test_site}/services/DevelopmentLoginClientService/login?userId=B.EILEENB&password=B.EILEENB"

end

class RestEILEENLLogin < PageFactory

  page_url "#{$test_site}/services/DevelopmentLoginClientService/login?userId=B.EILEENL&password=B.EILEENL"

end
