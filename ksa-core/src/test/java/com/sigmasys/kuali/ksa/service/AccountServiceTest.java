package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.jaxb.Ach;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;
import org.kuali.rice.ksb.api.bus.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
public class AccountServiceTest extends AbstractServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;


    protected Transaction transaction1;
    protected Transaction transaction2;
    protected Transaction transaction3;


    @Before
    public void setUpWithinTransaction() throws Exception {
        // set up test data within the transaction
        String userId = "admin";

        accountService.getOrCreateAccount(userId);

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);

        Date date1 = dateFormat.parse("06/01/2012");
        Date date2 = dateFormat.parse("10/16/2012");
        Date date3 = dateFormat.parse("01/23/2013");

        // Creating transactions with the test user ID
        transaction1 = transactionService.createTransaction("1020", userId, date1, new BigDecimal(10e7));
        transaction2 = transactionService.createTransaction("cash", userId, date2, new BigDecimal(300.99));
        transaction3 = transactionService.createTransaction("chip", userId, date3, new BigDecimal(77777.980));

    }

    @Test
    public void getAccount() throws Exception {

        Account account = accountService.getOrCreateAccount("admin1");

        Assert.notNull(account);
        Assert.notNull(account.getId());

        PersonService personService = KimApiServiceLocator.getPersonService();

        Assert.notNull(personService);

        Person person = personService.getPersonByPrincipalName("admin1");

        Assert.notNull(person);
        Assert.notNull(person.getPrincipalName());

        Assert.isTrue(account.getId().equals(person.getPrincipalName()));

        // Add more assertions when we have some test data
    }

    @Test
    public void getFullAccount() throws Exception {

        Account account = accountService.getOrCreateAccount("admin1");

        Assert.notNull(account);
        Assert.notNull(account.getId());

        account = accountService.getFullAccount(account.getId());

        Assert.notNull(account);

        Assert.notNull(account.getPersonNames());
        Assert.notNull(account.getPostalAddresses());
        Assert.notNull(account.getElectronicContacts());

        Assert.notEmpty(account.getPersonNames());
        Assert.notEmpty(account.getPostalAddresses());
        Assert.notEmpty(account.getElectronicContacts());

        Assert.notNull(account.getDefaultPersonName());
        Assert.notNull(account.getDefaultPostalAddress());
        Assert.notNull(account.getDefaultElectronicContact());

        // Add more assertions when we have some test data
    }

    @Test
    public void getFullAccounts() throws Exception {

        List<Account> accounts = accountService.getFullAccounts();

        Assert.notNull(accounts);
        Assert.notEmpty(accounts);

        for (Account account : accounts) {

            Assert.notNull(account);

            Assert.notNull(account.getPersonNames());
            Assert.notNull(account.getPostalAddresses());
            Assert.notNull(account.getElectronicContacts());

            Assert.notEmpty(account.getPersonNames());
            Assert.notEmpty(account.getPostalAddresses());
            Assert.notEmpty(account.getElectronicContacts());

            Assert.notNull(account.getDefaultPersonName());
            Assert.notNull(account.getDefaultPostalAddress());
            Assert.notNull(account.getDefaultElectronicContact());

        }

        // Add more assertions when we have some test data
    }

    @Test
    public void getDueBalance() {

        String userId = "admin";

        BigDecimal balanceDue = accountService.getDueBalance(userId, false);

        Assert.notNull(balanceDue);
        Assert.isTrue(balanceDue.compareTo(BigDecimal.ZERO) >= 0);

        logger.info("Due balance = " + balanceDue);

    }

    @Test
    public void rebalance() {

        String userId = "admin";

        List<Pair<Debit, BigDecimal>> amounts = accountService.rebalance(userId, false);

        Assert.notNull(amounts);

        for (Pair<Debit, BigDecimal> pair : amounts) {
            Assert.notNull(pair.getA());
            Assert.notNull(pair.getB());
            logger.info("Debit date = " + pair.getA().getEffectiveDate() + ", amount = " + pair.getB());
        }

    }

    @Test
    public void ageDebt1() {

        String userId = "admin";

        ChargeableAccount account = (ChargeableAccount) accountService.getFullAccount(userId);

        Assert.notNull(account);

        logger.info("Amount Late1 Before = " + account.getAmountLate1());
        logger.info("Amount Late2 Before = " + account.getAmountLate2());
        logger.info("Amount Late3 Before = " + account.getAmountLate3());

        account = accountService.ageDebt(userId, false);

        Assert.notNull(account);
        Assert.notNull(account.getAmountLate1());
        Assert.notNull(account.getAmountLate2());
        Assert.notNull(account.getAmountLate3());

        logger.info("Amount Late1 After = " + account.getAmountLate1());
        logger.info("Amount Late2 After = " + account.getAmountLate2());
        logger.info("Amount Late3 After = " + account.getAmountLate3());

    }

    @Test
    public void ageDebt2() {

        String userId = "admin";

        ChargeableAccount account = (ChargeableAccount) accountService.getFullAccount(userId);

        Assert.notNull(account);

        logger.info("Amount Late1 Before = " + account.getAmountLate1());
        logger.info("Amount Late2 Before = " + account.getAmountLate2());
        logger.info("Amount Late3 Before = " + account.getAmountLate3());

        account = accountService.ageDebt(userId, AgeDebtMethod.OPEN_ITEM, false);

        Assert.notNull(account);
        Assert.notNull(account.getAmountLate1());
        Assert.notNull(account.getAmountLate2());
        Assert.notNull(account.getAmountLate3());

        logger.info("Amount Late1 After = " + account.getAmountLate1());
        logger.info("Amount Late2 After = " + account.getAmountLate2());
        logger.info("Amount Late3 After = " + account.getAmountLate3());

    }

    @Test
    public void ageDebt3() {

        String userId = "admin";

        ChargeableAccount account = (ChargeableAccount) accountService.getFullAccount(userId);

        Assert.notNull(account);

        logger.info("Amount Late1 Before = " + account.getAmountLate1());
        logger.info("Amount Late2 Before = " + account.getAmountLate2());
        logger.info("Amount Late3 Before = " + account.getAmountLate3());

        account = accountService.ageDebt(userId, AgeDebtMethod.BALANCE_FORWARD, true);

        Assert.notNull(account);
        Assert.notNull(account.getAmountLate1());
        Assert.notNull(account.getAmountLate2());
        Assert.notNull(account.getAmountLate3());

        logger.info("Amount Late1 After = " + account.getAmountLate1());
        logger.info("Amount Late2 After = " + account.getAmountLate2());
        logger.info("Amount Late3 After = " + account.getAmountLate3());

    }

    @Test
    public void ageAllDebts() {

        accountService.getFullAccount("admin");
        accountService.getFullAccount("user1");
        accountService.getFullAccount("dev1");

        accountService.ageDebt(false);

    }

    @Test
    public void getAccountKsb() {

        String userId = "admin";

        QName serviceName = new QName("http://sigmasys.com/ksa", "accountService");

        Endpoint endpoint = KsbApiServiceLocator.getServiceBus().getEndpoint(serviceName);

        Assert.notNull(endpoint);
        Assert.notNull(endpoint.getServiceConfiguration());

        URL endpointUrl = endpoint.getServiceConfiguration().getEndpointUrl();

        Assert.notNull(endpointUrl);

        logger.info("Endpoint URL = " + endpointUrl);

        AccountService service = (AccountService) KsbApiServiceLocator.getServiceBus().getService(serviceName);


        Assert.notNull(service);

        Account account = service.getFullAccount(userId);

        Assert.notNull(account);

    }

    @Test
    public void addPersonName() {

        String userId = "admin";

        PersonName personName = new PersonName();
        personName.setTitle("Mr.");
        personName.setFirstName("AdminFirst");
        personName.setMiddleName("AdminMiddle");
        personName.setLastName("AdminLast");
        personName.setDefault(true);

        personName = accountService.addPersonName(userId, personName);

        Assert.notNull(personName);
        Assert.notNull(personName.getId());

        Assert.isTrue("AdminFirst".equals(personName.getFirstName()));
        Assert.isTrue("AdminMiddle".equals(personName.getMiddleName()));
        Assert.isTrue("AdminLast".equals(personName.getLastName()));

        Assert.notNull(personName.isDefault());
        Assert.isTrue(personName.isDefault());

    }

    @Test
    public void addPostalAddress() {

        String userId = "admin";

        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setCountry("Russia");
        postalAddress.setState("N/A");
        postalAddress.setCity("Saint Petersburg");
        postalAddress.setStreetAddress1("1917 Lenin avenue");
        postalAddress.setPostalCode("198330");
        postalAddress.setDefault(true);

        postalAddress = accountService.addPostalAddress(userId, postalAddress);

        Assert.notNull(postalAddress);
        Assert.notNull(postalAddress.getId());

        Assert.isTrue("Russia".equals(postalAddress.getCountry()));
        Assert.isTrue("N/A".equals(postalAddress.getState()));
        Assert.isTrue("Saint Petersburg".equals(postalAddress.getCity()));
        Assert.isTrue("1917 Lenin avenue".equals(postalAddress.getStreetAddress1()));
        Assert.isTrue("198330".equals(postalAddress.getPostalCode()));

        Assert.notNull(postalAddress.isDefault());
        Assert.isTrue(postalAddress.isDefault());

    }

    @Test
    public void addElectronicContact() {

        String userId = "admin";

        ElectronicContact contact = new ElectronicContact();
        contact.setPhoneCountry("7");
        contact.setPhoneNumber("812-378-56-47");
        contact.setEmailAddress("admin@yahoo.com");
        contact.setDefault(true);

        contact = accountService.addElectronicContact(userId, contact);

        Assert.notNull(contact);
        Assert.notNull(contact.getId());

        Assert.isTrue("7".equals(contact.getPhoneCountry()));
        Assert.isTrue("812-378-56-47".equals(contact.getPhoneNumber()));
        Assert.isTrue("admin@yahoo.com".equals(contact.getEmailAddress()));

        Assert.notNull(contact.isDefault());
        Assert.isTrue(contact.isDefault());

    }

    @Test
    public void getAch() {

        String userId = "user1";
        Ach ach = accountService.getAch(userId);

        Assert.notNull(ach);

        Assert.isTrue("C".equals(ach.getAccountType()));
        Assert.isTrue("0019921".equals(ach.getRoutingNumber()));
        Assert.isTrue("20100020101".equals(ach.getAccountNumber()));
    }

    @Test
    public void updateAccount() {

        String userId = "admin";

        Account account = accountService.getFullAccount(userId);

        Assert.notNull(account);

        PersonName name = account.getDefaultPersonName();

        Assert.notNull(name);

        name.setFirstName("Mario");

        ElectronicContact contact = account.getDefaultElectronicContact();

        Assert.notNull(contact);

        contact.setPhoneNumber("1-202-800-9999");
        contact.setEmailAddress("admin@test.edu");

        PostalAddress address = account.getDefaultPostalAddress();

        Assert.notNull(address);

        address.setCity("Paris");

        accountService.updateAccount(account, "admin123");

    }

    @Test
    public void createAccount() {

        String userId = "_admin5";
        String password = "_admin123";

        PersonName name = new PersonName();
        name.setFirstName("Mario");
        name.setLastName("Morris");
        name.setTitle("Mr.");
        name.setKimNameType("PRFR");
        name.setMiddleName("L");
        name.setSuffix("Jr.");
        name.setDefault(true);

        PostalAddress address = new PostalAddress();
        address.setStreetAddress1("1500");
        address.setStreetAddress2("Mass ave");
        address.setStreetAddress3("#37");
        address.setCity("Washington");
        address.setState("DC");
        address.setPostalCode("20005");
        address.setCountry("USA");
        address.setDefault(true);

        ElectronicContact contact = new ElectronicContact();
        contact.setPhoneNumber("202-245-0988");
        contact.setPhoneExtension("457");
        contact.setPhoneCountry("US");
        contact.setEmailAddress("admin5@test.edu");
        contact.setKimEmailAddressType("WRK");
        contact.setKimPhoneType("WRK");
        contact.setDefault(true);

        Account account = new DirectChargeAccount();
        account.setId(userId);
        account.setAbleToAuthenticate(true);
        account.setKimAccount(true);

        account = accountService.createAccount(account, name, address, contact, password);

        Assert.notNull(account);
        Assert.notNull(account.getId());
        Assert.notNull(account.getDefaultPersonName());
        Assert.notNull(account.getDefaultPostalAddress());
        Assert.notNull(account.getDefaultElectronicContact());
        Assert.notNull(account.getLastKimUpdate());

        Assert.isTrue(account.isKimAccount());
        Assert.isTrue(account.isAbleToAuthenticate());

    }

    @Test
    public void getAccountsByNamePattern1() throws Exception {

        String searchString = "user";

        Assert.isTrue(accountService.accountExists("user1"));

        List<Account> accounts = accountService.getAccountsByNamePattern(searchString);

        Assert.notNull(accounts);
        Assert.notEmpty(accounts);

        searchString = "adm";

        Assert.isTrue(accountService.accountExists("admin"));

        accounts = accountService.getAccountsByNamePattern(searchString);

        Assert.notNull(accounts);
        Assert.notEmpty(accounts);

    }

    @Test
    public void getAccountsByNamePattern2() throws Exception {

        String searchString = "user";

        Assert.isTrue(accountService.accountExists("user1"));

        List<? extends Account> accounts = accountService.getAccountsByNamePattern(searchString, Account.class);

        Assert.notNull(accounts);
        Assert.notEmpty(accounts);

        searchString = "adm";

        Assert.isTrue(accountService.accountExists("admin"));

        accounts = accountService.getAccountsByNamePattern(searchString, DirectChargeAccount.class);

        Assert.notNull(accounts);
        Assert.notEmpty(accounts);

    }


}
