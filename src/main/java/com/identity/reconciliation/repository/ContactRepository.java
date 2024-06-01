package com.identity.reconciliation.repository;

import com.identity.reconciliation.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {

    List<Contact> findByEmailOrPhoneNumber(String email, String phoneNumber);

    Contact findById(Long id);
}
