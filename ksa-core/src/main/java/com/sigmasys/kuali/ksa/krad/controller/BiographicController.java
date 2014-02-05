package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.BiographicForm;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.service.PersistenceService;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: dmulderink on 9/25/12 at 6:59 PM
 */
@Controller
@RequestMapping(value = "/biographicView")
public class BiographicController extends GenericSearchController {

    private static final Log logger = LogFactory.getLog(BiographicController.class);

    @Autowired
    private PersistenceService persistenceService;

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected BiographicForm createInitialForm(HttpServletRequest request) {

        BiographicForm form = new BiographicForm();
        form.setStatusMessage("");
        String userId = request.getParameter("userId");

        if (userId != null) {

            Account account = accountService.getFullAccount(userId);

            if (account == null) {
                String errMsg = "Cannot find Account by ID = " + userId;
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }

            form.setAccount(account);

        }

        return form;
    }


    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
    public ModelAndView get(@ModelAttribute("KualiForm") BiographicForm form) {

        Account account = form.getAccount();

        account = accountService.getFullAccount(account.getId());
        form.setAccount(account);
        if (account == null) {
            String errMsg = "No account available";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        form.setPersonNameList(new ArrayList<PersonName>());
        form.getPersonNameList().addAll(account.getPersonNames());

        form.setPostalAddressList(new ArrayList<PostalAddress>());
        form.getPostalAddressList().addAll(account.getPostalAddresses());

        form.setElectronicContactList(new ArrayList<ElectronicContact>());
        form.getElectronicContactList().addAll(account.getElectronicContacts());

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=update")
    public ModelAndView update(@ModelAttribute("KualiForm") BiographicForm form) {

        Account account = form.getAccount();
        String userId = account.getId();

        try {

            // Get all objects from the screen and validate single Default object selection:
            List<PersonName> personList = form.getPersonNameList();
            List<PostalAddress> addressList = form.getPostalAddressList();
            List<ElectronicContact> contactList = form.getElectronicContactList();

            assertSingleDefaultSelection(personList, "Person Name");
            assertSingleDefaultSelection(addressList, "Postal Address");
            assertSingleDefaultSelection(contactList, "Electronic Contact");

            // Define arrays objects to be deleted:
            List<PersonName> namesToDelete = new ArrayList<PersonName>(account.getPersonNames());
            List<PostalAddress> postalAddressesToDelete = new ArrayList<PostalAddress>(account.getPostalAddresses());
            List<ElectronicContact> electronicContactsToDelete = new ArrayList<ElectronicContact>(account.getElectronicContacts());

            // Persist PersonNames
            for (PersonName person : personList) {
                if (person.getId() == null) {
                    accountService.addPersonName(userId, person);
                } else {
                    accountService.persistPersonName(person);
                    removeById(namesToDelete, person);
                }
            }

            // Persist PostalAddresses
            for (PostalAddress address : addressList) {
                if (address.getId() == null) {
                    accountService.addPostalAddress(userId, address);
                } else {
                    accountService.persistPostalAddress(address);
                    removeById(postalAddressesToDelete, address);
                }
            }

            // Persist ElectronicContacts:
            for (ElectronicContact contact : contactList) {
                if (contact.getId() == null) {
                    accountService.addElectronicContact(userId, contact);
                } else {
                    accountService.persistElectronicContact(contact);
                    removeById(electronicContactsToDelete, contact);
                }
            }

            // Delete objects:
            if (!namesToDelete.isEmpty() || !postalAddressesToDelete.isEmpty() || !electronicContactsToDelete.isEmpty()) {
                account.getPersonNames().removeAll(namesToDelete);
                account.getPostalAddresses().removeAll(postalAddressesToDelete);
                account.getElectronicContacts().removeAll(electronicContactsToDelete);
                persistenceService.persistEntity(account);
            }

            GlobalVariables.getMessageMap().putInfo("BiographicView", RiceKeyConstants.ERROR_CUSTOM, "Saved");
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            GlobalVariables.getMessageMap().putError("BiographicView", RiceKeyConstants.ERROR_CUSTOM, "Error saving data: " + t.getLocalizedMessage());
        }

        return getUIFModelAndView(form);
    }

    private <T extends Identifiable> void removeById(List<T> list, Identifiable toRemove) {
        for (int i = 0, sz = list.size(); i < sz; i++) {
            if (ObjectUtils.equals(list.get(i).getId(), toRemove.getId())) {
                list.remove(i);
                return;
            }
        }
    }

    private <T> void assertSingleDefaultSelection(List<T> objects, String objectType) {
        int count = 0;

        for (int i = 0, sz = objects.size(); (i < sz) && (count < 2); i++) {
            DefaultMethodHolder dmh = toDefaultMethodHolder(objects.get(i));
            if ((dmh != null) && BooleanUtils.isTrue(dmh.isDefault())) {
                count++;
            }
        }

        if (count > 1) {
            String errorMsg = String.format("Only one default %s may be specified.", objectType);
            logger.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        if (count < 1) {
            String errorMsg = String.format("One default %s must be specified.", objectType);
            logger.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
    }

    private interface DefaultMethodHolder {
        Boolean isDefault();
    }

    private class PersonNameAdapter implements DefaultMethodHolder {
        private PersonName adaptee;

        public PersonNameAdapter(PersonName pn) {
            adaptee = pn;
        }

        public Boolean isDefault() {
            return adaptee.isDefault();
        }
    }

    private class PostalAddressAdapter implements DefaultMethodHolder {

        private PostalAddress adaptee;

        public PostalAddressAdapter(PostalAddress pa) {
            adaptee = pa;
        }

        public Boolean isDefault() {
            return adaptee.isDefault();
        }
    }

    private class ElectronicContactAdapter implements DefaultMethodHolder {

        private ElectronicContact adaptee;

        public ElectronicContactAdapter(ElectronicContact ec) {
            adaptee = ec;
        }

        public Boolean isDefault() {
            return adaptee.isDefault();
        }
    }

    private DefaultMethodHolder toDefaultMethodHolder(Object o) {
        if (o instanceof PersonName) {
            return new PersonNameAdapter((PersonName) o);
        } else if (o instanceof ElectronicContact) {
            return new ElectronicContactAdapter((ElectronicContact) o);
        } else if (o instanceof PostalAddress) {
            return new PostalAddressAdapter((PostalAddress) o);
        } else {
            return null;
        }
    }
}
