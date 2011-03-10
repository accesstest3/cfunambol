/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.funambol.email.integration;


import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 */
public class IntegrationTestSuite extends TestSuite {
    
    public IntegrationTestSuite(String testName) {
        super(testName);
    }                
    
    public static Test suite() {
      TestSuite suite = new TestSuite();
           
      // integration test
      suite.addTest(new InboxListenerOrderTest("getUIDS_Gmail_imap_range"));
      suite.addTest(new InboxListenerOrderTest("getUIDS_Gmail_imap_all"));
      suite.addTest(new InboxListenerOrderTest("getUIDS_Gmail_pop_range"));
      suite.addTest(new InboxListenerOrderTest("getUIDS_Gmail_pop_all"));
      suite.addTest(new InboxListenerOrderTest("getUIDS_Gmail_imap_range"));
      suite.addTest(new InboxListenerOrderTest("getUIDS_Gmail_imap_all"));
      suite.addTest(new InboxListenerOrderTest("getUIDS_AOL_imap_range"));            
      suite.addTest(new InboxListenerOrderTest("getUIDS_AOL_imap_all"));                  
      suite.addTest(new InboxListenerOrderTest("getUIDS_Hotmail_pop_range"));
      suite.addTest(new InboxListenerOrderTest("getUIDS_Hotmail_pop_all"));
      suite.addTest(new InboxListenerOrderTest("getUIDS_Yahoo_pop_range"));
      suite.addTest(new InboxListenerOrderTest("getUIDS_Yahoo_pop_all"));
      suite.addTest(new InboxListenerOrderTest("getUIDS_Funambol_imap_all"));
      suite.addTest(new InboxListenerOrderTest("getUIDS_Funambol_imap_range"));
      suite.addTest(new InboxListenerOrderTest("getUIDS_Funambol_pop_all"));
      suite.addTest(new InboxListenerOrderTest("getUIDS_Funambol_pop_range"));
      
      return suite;
    }

}
