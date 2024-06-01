package com.identity.reconciliation;

import com.identity.reconciliation.model.Contact;
import com.identity.reconciliation.repository.ContactRepository;
import com.identity.reconciliation.service.ContactService;
import com.identity.reconciliation.utils.Constants;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class IdentityReconciliationApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ContactService contactService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIdentifyContact() throws Exception {
        Contact primaryContact = new Contact();
        primaryContact.setId(1L);
        primaryContact.setEmail("test@example.com");
        primaryContact.setPhoneNumber("1234567890");
        primaryContact.setLinkedId(null);
        primaryContact.setLinkPrecedence("primary");
        primaryContact.setCreatedAt(null);
        primaryContact.setUpdatedAt(null);

        List<Contact> contacts = Collections.singletonList(primaryContact);
        when(contactRepository.findByEmailOrPhoneNumber(any(), any())).thenReturn(contacts);
        when(contactRepository.save(any(Contact.class))).thenReturn(primaryContact);

        JSONObject request = new JSONObject();
        request.put("email", "test@example.com");
        request.put("phoneNumber", "1234567890");
        JSONObject response = new JSONObject(contactService.identifyContact(request));

        verify(contactRepository, times(1)).findByEmailOrPhoneNumber(anyString(), anyString());
        verify(contactRepository, times(1)).save(any(Contact.class));

        assertEquals(1L, response.get(Constants.PRIMARY_CONTACT_ID));
        assertEquals(Collections.singletonList("test@example.com"), response.get(Constants.EMAILS));
        assertEquals(Collections.singletonList("1234567890"), response.get(Constants.PHONE_NUMBERS));
        assertEquals(Collections.emptyList(), response.get(Constants.SECONDARY_CONTACT_IDS));

    }
}

