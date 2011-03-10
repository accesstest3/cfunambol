/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY FUNAMBOL, FUNAMBOL DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact Funambol, Inc. headquarters at 643 Bair Island Road, Suite
 * 305, Redwood City, CA 94063, USA, or at email address info@funambol.com.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "Powered by Funambol" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by Funambol".
 */
package com.funambol.common.pim.contact;

import com.funambol.common.pim.common.TypifiedProperty;
import java.util.List;
import java.util.ArrayList;

import com.funambol.common.pim.utility.PDIMergeUtils;

import com.funambol.framework.tools.merge.MergeResult;

/**
 * An object containing details on how to reach a contact (phone numbers, 
 * e-mail addresses, webpage etc.).
 *
 * @version $Id: ContactDetail.java,v 1.2 2007-11-28 11:14:04 nichele Exp $
 */
public class ContactDetail {

    //--------------------------------------------------------------- Properties
    
    private List<Phone>       phones   = new ArrayList<Phone>();
    private List<Email>       emails   = new ArrayList<Email>(3);
    private List<WebPage>     webPages = new ArrayList<WebPage>(3);
    private List<IMPPAddress> impps    = new ArrayList<IMPPAddress>(3);

    /**
     * Returns the list of telephone numbers.
     *
     * @return a List containing Phone objects
     */
    public List getPhones() {
       return phones;
    }

    /**
     * Returns the list of e-mail addresses.
     *
     * @return a List containing Email objects
     */
    public List getEmails() {
       return emails;
    }

    /**
     * Returns the list of webpages.
     *
     * @return a List containing WebPage objects
     */
    public List getWebPages() {
       return webPages;
    }

    /**
     * Returns the list of webpages.
     *
     * @return a List containing IMPPAddress objects
     */
    public List<IMPPAddress> getIMPPs() {
       return impps;
    }

    /**
     * Sets a new list of phones.
     * 
     * @param phones new value of list phones
     */
    public void setPhones(List phones) {
        this.phones = phones;
    }

    /**
     * Sets a new list of e-mail addresses.
     * 
     * @param emails new value of list emails
     */
    public void setEmails(List emails) {
        this.emails = emails;
    }

    /**
     * Sets a new list of webpages
     * 
     * @param webPages new value of list webpages
     */
    public void setWebPages(List webPages) {
        this.webPages = webPages;
    }

    /**
     * Sets a new list of IMPP addresses
     *
     * @param impps new value of list webpages
     */
    public void setIMPPs(List<IMPPAddress> impps) {
        this.impps = impps;
    }
    
    //----------------------------------------------------------- Public methods

     /**
     * Adds a new phone number to the list.
     *
     * @param phone the new phone number
     *
     */
    public void addPhone(Phone phone) {
        if (phone == null) {
            return;
        }

        if (phones == null) {
            phones = new ArrayList<Phone>();
        }

        addTypifiedProperty(phones, phone);
    }
    
    /**
     * Adds a new email address to the list.
     *
     * @param email the new email address
     *
     */
    public void addEmail(Email email) {
        if (email == null) {
            return;
        }

        if (emails == null) {
            emails = new ArrayList<Email>(3);
        }

        addTypifiedProperty(emails, email);
    }
    
    /**
     * Adds a new webpage to the list.
     *
     * @param page the new webpage
     *
     */
    public void addWebPage(WebPage page) {
        if (page == null) {
            return;
        }

        if (webPages == null) {
            webPages = new ArrayList<WebPage>(3);
        }

        addTypifiedProperty(webPages, page);
    }
    
    /**
     * Merge this ContactDetail with another one.
     * 
     * @param otherDetail the other ContactDetail instance
     * @return true only if the original contact detail is changed
     */
    public MergeResult merge(ContactDetail otherDetail) {

        MergeResult contactDetailMergeResults = new MergeResult("ContactDetail");

        MergeResult result = null; // temporary object used to store the merge 
                                   // result for each field

        if (otherDetail == null) {
            throw new IllegalStateException("The given ContactDetail must not be null");
        }

        // Phones
        if (otherDetail.getPhones() != null || phones != null) {
            if (otherDetail.getPhones() == null) {
                otherDetail.setPhones(new ArrayList<Phone>());
            }
            if (phones == null) {
                phones = new ArrayList<Phone>();
            }

            result = PDIMergeUtils.mergeTypifiedPropertiestList(phones,
                                                                otherDetail.getPhones());
            contactDetailMergeResults.addMergeResult(result, "Phones");
        }

        // WebPages
        if (otherDetail.getWebPages() != null || webPages != null) {
            if (otherDetail.getWebPages() == null) {
                otherDetail.setWebPages(new ArrayList<WebPage>(3));
            }
            if (webPages == null) {
                webPages = new ArrayList<WebPage>(3);
            }
            result = PDIMergeUtils.mergeTypifiedPropertiestList(webPages,
                                                                otherDetail.getWebPages());
            contactDetailMergeResults.addMergeResult(result, "WebPages");
        }

        // Emails
        if (otherDetail.getEmails() != null || emails != null) {
            if (otherDetail.getEmails() == null) {
                otherDetail.setEmails(new ArrayList<Email>(3));
            }
            if (emails == null) {
                emails = new ArrayList<Email>(3);
            }
            result = PDIMergeUtils.mergeTypifiedPropertiestList(emails,
                                                                otherDetail.getEmails());
            contactDetailMergeResults.addMergeResult(result, "Emails");
        }

        // IMPP addresses
        if (otherDetail.getIMPPs() != null || impps != null) {
            if (otherDetail.getIMPPs() == null) {
                otherDetail.setIMPPs(new ArrayList<IMPPAddress>());
            }
            if (impps == null) {
                impps = new ArrayList<IMPPAddress>();
            }

            result = PDIMergeUtils.mergeLists(impps,
                                              otherDetail.getIMPPs(),
                                              "IMPPs");
            contactDetailMergeResults.addMergeResult(result, "IMPPs");
        }

        return contactDetailMergeResults;
    }

    /**
     * Adds a typified property to a list if no property of that type is already 
     * there, or updates it if there is one.
     * 
     * @param list a list of objects of some class that extends TypifiedProperty
     * @param addition the item to add or replace in the list
     */
    protected void addTypifiedProperty(List<? extends TypifiedProperty> list, 
                                       TypifiedProperty addition) {
        for (TypifiedProperty tp : list) {
            if (tp.getPropertyType().equals(addition.getPropertyType())) {
                tp.setPropertyValue(addition.getPropertyValue());
                return;
            }
        }
        ((List)list).add(addition);
    }

    /**
     * Adds an IM/PP address to the list if it is not already there, or updates
     * its attributes if it is.
     *
     * @param impp the IM/PP address to add
     */
    protected void addIMPPAddress(IMPPAddress impp) {

        if (impps == null) {
            impps = new ArrayList<IMPPAddress>();
        }

        for (IMPPAddress i : impps) {
            if (i.getUri().equals(impp.getUri())) {
                i.setLocation(impp.getLocation());
                i.setType(impp.getType());
                i.setPreferred(impp.isPreferred());
                return;
            }
        }
        
        impps.add(impp);
    }

}
