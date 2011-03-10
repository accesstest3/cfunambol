/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funambol.foundation.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.funambol.common.pim.contact.BusinessDetail;
import com.funambol.common.pim.contact.Contact;
import com.funambol.common.pim.contact.Email;
import com.funambol.common.pim.contact.PersonalDetail;

import com.funambol.foundation.exception.EntityException;
import com.funambol.foundation.items.manager.PIMContactManager;
import com.funambol.foundation.items.model.ContactWrapper;

/**
 *
 * @author pietro
 */
public class PIMContactManagerMock extends PIMContactManager {

    // ------------------------------------------------------------ Private data
    
    /** Mocked backend. */
    private HashMap<String, ContactWrapper> contactsWrappers =
            new HashMap<String, ContactWrapper>();
    
    private String userId;
    
    /** Contact current id. */
    private int ids;

    // ------------------------------------------------------------ Constructors
    
    public PIMContactManagerMock(String userId) {
        super(userId);
        this.userId = userId;
    }

    // ---------------------------------------------------------- Public methods
    
    public Contact addContact(){
        
        Contact c = new Contact();
        
        PersonalDetail pd = new PersonalDetail();
        BusinessDetail bd = new BusinessDetail();
        
        c.setPersonalDetail(pd);
        c.setBusinessDetail(bd);
        
        String id = nextId();
        ContactWrapper cw = new ContactWrapper(id, userId, c);
        contactsWrappers.put(id, cw);
        
        return c;
    }
    
    /**
     * @deprecated No email should be added without an email type
     */
    public void addPersonalEmail(Contact c, String addressString){
        Email email = new Email(addressString); 
        c.getPersonalDetail().addEmail(email);
    }

    /**
     * @deprecated No email should be added without an email type
     */
    public void addBusinessEmail(Contact c, String addressString){
        Email email = new Email(addressString); 
        c.getBusinessDetail().addEmail(email);
    }
    
    public void addPersonalEmail(Contact c           , 
                                 String addressString, 
                                 String emailType    ){
        Email email = new Email(addressString); 
        email.setEmailType(emailType);
        c.getPersonalDetail().addEmail(email);
    }

    public void addBusinessEmail(Contact c           , 
                                 String addressString, 
                                 String emailType    ){
        Email email = new Email(addressString); 
        email.setEmailType(emailType);
        c.getBusinessDetail().addEmail(email);
    }
    
    /*
     * Only methods used by methods tested are overridden.
     */
    @Override
    public List getAllItems() throws EntityException {
        
        List<String> items = new ArrayList<String>();
        for (ContactWrapper cw : contactsWrappers.values()) {
            items.add(cw.getId());
        }
        return items;
    }

    @Override
    public ContactWrapper getItem(String uid) throws EntityException {
        return contactsWrappers.get(uid);
    }
    
    // --------------------------------------------------------- Private methods
    
    /** Gets the next contact id. */
    private String nextId(){
        ids++;
        return Integer.toString(ids);
    }
}
