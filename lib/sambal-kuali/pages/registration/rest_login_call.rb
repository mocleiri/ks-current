class RestStudentLogin < PageFactory

  page_url "#{$test_site}/services/DevelopmentLoginClientService/login?userId=student&password=student"

  end

class RestMarthaLogin < PageFactory

  page_url "#{$test_site}/services/DevelopmentLoginClientService/login?userId=martha&password=martha"

end

class RestAdminLogin < PageFactory

  page_url "#{$test_site}/services/DevelopmentLoginClientService/login?userId=admin&password=admin"

end