/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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
package com.funambol.email.admin.mailservers;


import com.funambol.admin.util.Log;

import com.funambol.email.admin.ComponentObserver;
import com.funambol.email.admin.MailServerController;
import com.funambol.email.admin.dao.WSDAO;
import com.funambol.email.model.MailServer;

import com.funambol.framework.filter.Clause;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * Panel for mail server handling search and selection.
 *
 * @version $Id: SearchMailServerPanel.java,v 1.1 2008-03-25 11:25:33 gbmiglia Exp $
 */
public class SearchMailServerPanel extends JPanel implements ComponentObserver {

    // ------------------------------------------------------------ Private data

    /** list of all the components whose view needs to be updated */
    private List<ComponentObserver> observers =
            new ArrayList<ComponentObserver>();

    //
    // Visual elements.
    //

    /** search clause panel */
    private FormSearchMailServerPanel formPanel;

    /** search result panel */
    private ResultSearchMailServerPanel resultPanel;

    // -------------------------------------------------------------- Properties

    /** controller */
    private MailServerController controller;

    /** Data access object. */
    private WSDAO WSDao;

    /**
     * Sets the WSDao property
     */
    public void setWSDao(WSDAO WSDao) {
        this.WSDao = WSDao;
        controller.setWSDao(this.WSDao);
    }

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a SearchMailServerPanel object.
     */
    public SearchMailServerPanel() {
        controller = new MailServerController();
        try {
            initGui();
        } catch (Exception e) {
            Log.error("Error while initilizing SearchMailServerPanel object", e);
        }
    }

    // --------------------------------------------------------- Private methods

    /**
     * Initializes visual elements.
     */
    private void initGui(){

        formPanel = new FormSearchMailServerPanel();
        resultPanel = new ResultSearchMailServerPanel();

        formPanel.addSearchAction(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                Clause clause = formPanel.getClause();

                try {

                    // get mail servers to be displaied
                    MailServer[] displaiedMailServers = WSDao.getMailServers(clause);
                    
                    // set mail server to be displaied in the result panel
                    resultPanel.setMailServers(displaiedMailServers);
                    
                    // reset result table selection
                    resultPanel.resetResultTableSelection();
                    
                } catch (Exception e) {
                    Log.error(e);
                }
            }
        });

        resultPanel.setAddAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.setParentFrame(getOwner());
                controller.insertMailServer();
            }
        });

        resultPanel.setModifyAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyMailserver();
            }
        });

        resultPanel.setDeleteAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                MailServer selectedMailServer = resultPanel.getSelectedMailServer();
                
                controller.setParentFrame(getOwner());
                                
                int numberRowsDeleted =
                        controller.deleteMailServer(selectedMailServer);

                StringBuilder sb = new StringBuilder();
                if (numberRowsDeleted > 0){
                    // update the search account result table, if not empty
                    resultPanel.deleteRowInTable(selectedMailServer);
                }
            }
        });

        resultPanel.setRowMouseAction(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    modifyMailserver();
                }
            }
        });
        
        setLayout(new BorderLayout());
        add(formPanel,BorderLayout.PAGE_START);
        add(resultPanel,BorderLayout.CENTER);

    }

    // ---------------------------------------------------------- Public methods

    /**
     * Adds a component to the components that need to be notified for updates.
     * @param observer component to be added
     */
    public void addObserver(ComponentObserver observer) {
        observers.add(observer);
    }

    /**
     * Updates this component.
     */
    public void update() {
        for (ComponentObserver observer : observers) {
            observer.update();
        }
    }

    // --------------------------------------------------------- Private methods
    /*
     * Returns the frame owner of this panel
     * @return Frame
     */
    private Frame getOwner() {
        Container parent = this;
        while ( (parent = parent.getParent()) != null) {
            if (parent instanceof Frame) {
                return (Frame)parent;
            }
        }
        return null;
    }

    /**
     * Action performed as modification of a mail server is requested.
     */
    private void modifyMailserver(){
        MailServer mailServer = resultPanel.getSelectedMailServer();
        if (mailServer == null){
            return;
        }
        controller.setParentFrame(getOwner());
        controller.modifyMailServer(mailServer);
        resultPanel.updateMailServer(mailServer);
        update();
    }
}
