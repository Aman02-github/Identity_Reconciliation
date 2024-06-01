package com.identity.reconciliation.service;

import com.identity.reconciliation.model.Contact;
import com.identity.reconciliation.repository.ContactRepository;
import com.identity.reconciliation.utils.Constants;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ContactService {

    @Autowired
    public ContactRepository contactRepository;

    public String identifyContact(JSONObject request) {
        String email = request.optString(Constants.EMAIL, null);
        String phoneNumber = request.optString(Constants.PHONE_NUMBER, null);

        List<Contact> contacts = contactRepository.findByEmailOrPhoneNumber(email, phoneNumber);
        Contact primaryContact = null;

        List<Contact> primaryContacts = new ArrayList<>(contacts.stream().filter(contact -> Constants.PRIMARY.equals(contact.getLinkPrecedence())).toList());

        if (primaryContacts.size() > 1) {
            primaryContacts.sort(Comparator.comparingLong(Contact::getId).reversed());
            Contact greatestIdPrimaryContact = primaryContacts.get(0);
            primaryContacts.remove(greatestIdPrimaryContact);
            greatestIdPrimaryContact.setLinkPrecedence(Constants.SECONDARY);
            greatestIdPrimaryContact.setLinkedId(primaryContacts.get(0).getId());
            contactRepository.save(greatestIdPrimaryContact);
            primaryContact = primaryContacts.get(0);
        } else if (primaryContacts.size() == 1) {
            primaryContact = primaryContacts.get(0);
        }

        if (primaryContacts.isEmpty() && !contacts.isEmpty()) {
            primaryContact = contactRepository.findById(contacts.get(0).getLinkedId());
        }

        boolean isExistingEmail = contacts.stream().anyMatch(contact -> email != null && email.equals(contact.getEmail()));
        boolean isExistingPhone = contacts.stream().anyMatch(contact -> phoneNumber != null && phoneNumber.equals(contact.getPhoneNumber()));

        if (email != null && phoneNumber != null) {
            if (primaryContact == null) {
                primaryContact = new Contact();
                primaryContact.setEmail(email);
                primaryContact.setPhoneNumber(phoneNumber);
                primaryContact.setLinkPrecedence(Constants.PRIMARY);
                primaryContact = contactRepository.save(primaryContact);
            } else {
                if (!isExistingEmail || !isExistingPhone) {
                    Contact newSecondaryContact = new Contact();
                    newSecondaryContact.setEmail(email);
                    newSecondaryContact.setPhoneNumber(phoneNumber);
                    newSecondaryContact.setLinkedId(primaryContact.getId());
                    newSecondaryContact.setLinkPrecedence(Constants.SECONDARY);
                    contactRepository.save(newSecondaryContact);
                }
            }
        }

        Set<String> emails = new HashSet<>();
        Set<String> phoneNumbers = new HashSet<>();
        List<Long> secondaryContactIds = new ArrayList<>();

        if (primaryContact != null) {
            emails.add(primaryContact.getEmail());
            phoneNumbers.add(primaryContact.getPhoneNumber());
        }

        Contact finalPrimaryContact = primaryContact;
        List<Contact> secondaryContacts = contactRepository.findAll().stream().filter(user -> user.getLinkedId() != null && user.getLinkedId().equals(finalPrimaryContact.getId())).toList();

        for (Contact contact : secondaryContacts) {
            emails.add(contact.getEmail());
            phoneNumbers.add(contact.getPhoneNumber());
            secondaryContactIds.add(contact.getId());
        }

        List<String> allEmails = new ArrayList<>(emails);
        List<String> allPhoneNumbers = new ArrayList<>(phoneNumbers);

        if (primaryContact.getEmail() != null) {
            allEmails.remove(primaryContact.getEmail());
            allEmails.add(0, primaryContact.getEmail());
        }
        if (primaryContact.getPhoneNumber() != null) {
            allPhoneNumbers.remove(primaryContact.getPhoneNumber());
            allPhoneNumbers.add(0, primaryContact.getPhoneNumber());
        }

        JSONObject response = new JSONObject();
        JSONObject contact = new JSONObject();
        contact.put(Constants.PRIMARY_CONTACT_ID, primaryContact.getId());
        contact.put(Constants.EMAILS, allEmails);
        contact.put(Constants.PHONE_NUMBERS, allPhoneNumbers);
        contact.put(Constants.SECONDARY_CONTACT_IDS, secondaryContactIds);

        response.put(Constants.CONTACT, contact);
        return response.toString();
    }
}
