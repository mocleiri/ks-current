package com.sigmasys.kuali.ksa.krad.form;

import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.model.ElectronicContact;
import com.sigmasys.kuali.ksa.model.PersonName;
import com.sigmasys.kuali.ksa.model.PostalAddress;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by: dmulderink on 9/25/12 at 6:58 PM
 */
public class BiographicForm extends AbstractViewModel {

   /*
     Biographic Information
   */

   private Account account;

   private List<PersonName> personNameList;

   private List<PostalAddress> postalAddressList;

   private List<ElectronicContact> electronicContactList;

   private PersonName personName;

   private PostalAddress postalAddress;

   private ElectronicContact electronicContact;

   private String kimNameType;

   private String title;

   private String firstName;

   private String middleName;

   private String lastName;

   private String suffix;

   private String personDefault;

   // PersonName fields concatenated
   private String compositePersonName;

   // PostalAddress fields concatenated
   private String compositePostalAddress;

   private String compositeElectronicContacts;

   private String auditPersonCreator;

   private String auditPersonEditor;

   private Date auditPersonLastUpdateDate;

   private String auditPostalCreator;

   private String auditPostalEditor;

   private Date auditPostalLastUpdateDate;

   private String electronicContactDefault;

   private String auditElectronicContactCreator;

   private String auditElectronicContactEditor;

   private Date auditElectronicContactLastUpdateDate;

   private String addressType;

   private String address1;

   private String address2;

   private String address3;

   private String city;

   private String stateCode;

   private String postalCode;

   private String countryCode;

   private String postalDefault;

   private String electronicContactType;

   private String email;

   private String phoneType;

   private String phoneCountry;

   private String phoneNumber;

   private String phoneExtension;

   private String statusMessage;

   /*
     Get / Set methods
   */

   public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }

   public List<PersonName> getPersonNameList() {
       if(personNameList == null) {
           personNameList = new ArrayList<PersonName>();
       }
      return personNameList;
   }

   public void setPersonNameList(List<PersonName> personNameList) {
      this.personNameList = personNameList;
   }

   public List<PostalAddress> getPostalAddressList() {
       if(postalAddressList == null){
           postalAddressList = new ArrayList<PostalAddress>();
       }
      return postalAddressList;
   }

   public void setPostalAddressList(List<PostalAddress> postalAddressList) {
      this.postalAddressList = postalAddressList;
   }

   public List<ElectronicContact> getElectronicContactList() {
       if(electronicContactList == null){
           electronicContactList = new ArrayList<ElectronicContact>();
       }
      return electronicContactList;
   }

   public void setElectronicContactList(List<ElectronicContact> electronicContactList) {
      this.electronicContactList = electronicContactList;
   }

   public String getKimNameType() {
      return kimNameType;
   }

   public void setKimNameType(String kimNameType) {
      this.kimNameType = kimNameType;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getMiddleName() {
      return middleName;
   }

   public void setMiddleName(String middleName) {
      this.middleName = middleName;
   }

   public String getLastName() {
      return lastName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public String getSuffix() {
      return suffix;
   }

   public void setSuffix(String suffix) {
      this.suffix = suffix;
   }

   public String getPersonDefault() {
      return personDefault;
   }

   public void setPersonDefault(String personDefault) {
      this.personDefault = personDefault;
   }

   public String getCompositePersonName() {
      return compositePersonName;
   }

   public void setCompositePersonName(String compositePersonName) {
      this.compositePersonName = compositePersonName;
   }

   public String getCompositePostalAddress() {
      return compositePostalAddress;
   }

   public void setCompositePostalAddress(String compositePostalAddress) {
      this.compositePostalAddress = compositePostalAddress;
   }

   public String getCompositeElectronicContacts() {
      return compositeElectronicContacts;
   }

   public void setCompositeElectronicContacts(String compositeElectronicContacts) {
      this.compositeElectronicContacts = compositeElectronicContacts;
   }

   public String getAuditPersonCreator() {
      return auditPersonCreator;
   }

   public void setAuditPersonCreator(String auditPersonCreator) {
      this.auditPersonCreator = auditPersonCreator;
   }

   public String getAuditPersonEditor() {
      return auditPersonEditor;
   }

   public void setAuditPersonEditor(String auditPersonEditor) {
      this.auditPersonEditor = auditPersonEditor;
   }

   public Date getAuditPersonLastUpdateDate() {
      return auditPersonLastUpdateDate;
   }

   public void setAuditPersonLastUpdateDate(Date auditPersonLastUpdateDate) {
      this.auditPersonLastUpdateDate = auditPersonLastUpdateDate;
   }

   public String getAuditPostalCreator() {
      return auditPostalCreator;
   }

   public void setAuditPostalCreator(String auditPostalCreator) {
      this.auditPostalCreator = auditPostalCreator;
   }

   public String getAuditPostalEditor() {
      return auditPostalEditor;
   }

   public void setAuditPostalEditor(String auditPostalEditor) {
      this.auditPostalEditor = auditPostalEditor;
   }

   public Date getAuditPostalLastUpdateDate() {
      return auditPostalLastUpdateDate;
   }

   public void setAuditPostalLastUpdateDate(Date auditPostalLastUpdateDate) {
      this.auditPostalLastUpdateDate = auditPostalLastUpdateDate;
   }

   public String getElectronicContactDefault() {
      return electronicContactDefault;
   }

   public void setElectronicContactDefault(String electronicContactDefault) {
      this.electronicContactDefault = electronicContactDefault;
   }

   public String getAuditElectronicContactCreator() {
      return auditElectronicContactCreator;
   }

   public void setAuditElectronicContactCreator(String auditElectronicContactCreator) {
      this.auditElectronicContactCreator = auditElectronicContactCreator;
   }

   public String getAuditElectronicContactEditor() {
      return auditElectronicContactEditor;
   }

   public void setAuditElectronicContactEditor(String auditElectronicContactEditor) {
      this.auditElectronicContactEditor = auditElectronicContactEditor;
   }

   public Date getAuditElectronicContactLastUpdateDate() {
      return auditElectronicContactLastUpdateDate;
   }

   public void setAuditElectronicContactLastUpdateDate(Date auditElectronicContactLastUpdateDate) {
      this.auditElectronicContactLastUpdateDate = auditElectronicContactLastUpdateDate;
   }

   public String getAddressType() {
      return addressType;
   }

   public void setAddressType(String addressType) {
      this.addressType = addressType;
   }

   public String getAddress1() {
      return address1;
   }

   public void setAddress1(String address1) {
      this.address1 = address1;
   }

   public String getAddress2() {
      return address2;
   }

   public void setAddress2(String address2) {
      this.address2 = address2;
   }

   public String getAddress3() {
      return address3;
   }

   public void setAddress3(String address3) {
      this.address3 = address3;
   }

   public String getCity() {
      return city;
   }

   public void setCity(String city) {
      this.city = city;
   }

   public String getStateCode() {
      return stateCode;
   }

   public void setStateCode(String stateCode) {
      this.stateCode = stateCode;
   }

   public String getPostalCode() {
      return postalCode;
   }

   public void setPostalCode(String postalCode) {
      this.postalCode = postalCode;
   }

   public String getCountryCode() {
      return countryCode;
   }

   public void setCountryCode(String countryCode) {
      this.countryCode = countryCode;
   }

   public String getPostalDefault() {
      return postalDefault;
   }

   public void setPostalDefault(String postalDefault) {
      this.postalDefault = postalDefault;
   }

   public String getElectronicContactType() {
      return electronicContactType;
   }

   public void setElectronicContactType(String electronicContactType) {
      this.electronicContactType = electronicContactType;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getPhoneType() {
      return phoneType;
   }

   public void setPhoneType(String phoneType) {
      this.phoneType = phoneType;
   }

   public String getPhoneCountry() {
      return phoneCountry;
   }

   public void setPhoneCountry(String phoneCountry) {
      this.phoneCountry = phoneCountry;
   }

   public String getPhoneNumber() {
      return phoneNumber;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   public String getPhoneExtension() {
      return phoneExtension;
   }

   public void setPhoneExtension(String phoneExtension) {
      this.phoneExtension = phoneExtension;
   }

   public String getStatusMessage() {
      return statusMessage;
   }

   public void setStatusMessage(String statusMessage) {
      this.statusMessage = statusMessage;
   }

   public PersonName getPersonName() {
      return personName;
   }

   public void setPersonName(PersonName personName) {
      this.personName = personName;
   }

   public PostalAddress getPostalAddress() {
      return postalAddress;
   }

   public void setPostalAddress(PostalAddress postalAddress) {
      this.postalAddress = postalAddress;
   }

   public ElectronicContact getElectronicContact() {
      return electronicContact;
   }

   public void setElectronicContact(ElectronicContact electronicContact) {
      this.electronicContact = electronicContact;
   }
}
