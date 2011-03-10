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
package com.funambol.email.inboxlistener.plugin;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.inboxlistener.plugin.parser.IUDPMessageParser;
import com.funambol.email.inboxlistener.plugin.submitter.ISubmitter;
import com.funambol.email.util.Def;

import com.funambol.framework.cluster.Cluster;

import com.funambol.pushlistener.framework.PushListenerInterface;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Listens for udp mail notifications over a specified port and inserts a
 * <code>Task</code> in a specified push listern for each request.
 *
 * @version $Id: UDPMulticastReaderThread.java,v 1.1.1.1 2007-12-11 08:17:19 nichele Exp $
 */
public class UDPMulticastReaderThread extends Thread {

    // ------------------------------------------------------------ Private data

    private Configuration conf;

    private PushListenerInterface pushListenerInterface;

    private ISubmitter submitter;

    private IUDPMessageParser parser;

    private InetAddress ia;

    private MulticastSocket msocket;

    private boolean listening = true;

    private Logger log = Logger.getLogger(Def.LOGGER_NAME);

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of a <code>UDPMulticastReaderThread</code> class.
     * @param conf configuration
     * @param submitter submitter object
     */
    public UDPMulticastReaderThread(
            Configuration conf,
            ISubmitter submitter,
            IUDPMessageParser parser,
            PushListenerInterface pushListenerInterface)
            throws SocketException, UnknownHostException, InboxListenerException {

        this.conf                  = conf;
        this.submitter             = submitter;
        this.parser                = parser;
        this.pushListenerInterface = pushListenerInterface;

        super.setName("InboxListener-UDPMulticastReader");

    }

    // ---------------------------------------------------------- Public methods

    public void run() {

        if (log.isTraceEnabled()) {
            log.trace("Execute the UDPMulticastReaderThread");
        }

        // creates an empty datagram packet
        DatagramPacket packet = null;

        boolean isMyMessage = false;
        byte[] input ;

        try {

            // get the InetAddress of the MCAST group
            ia = InetAddress.getByName(this.conf.getMulticastGroup());
            // create a multicast socket on the specified local port number
            msocket = new MulticastSocket(this.conf.getMulticastPort());
            //Join a multicast group and wait for some action
            msocket.joinGroup(ia);


            if (log.isTraceEnabled()) {
                log.trace("Waiting for packets on group ["+this.conf.getMulticastGroup()+
                        "] on port [" + conf.getMulticastPort() + "] ...");
            }

            int totalReceiverNumber = -1;
            int indexReceiverNumber = -1;

            while (true){

                packet = new DatagramPacket(new byte[Def.PACKET_SIZE], Def.PACKET_SIZE);

                msocket.receive(packet);

                input = packet.getData();

                // update the totalReceiverNumber and indexReceiverNumber
                totalReceiverNumber = Cluster.getCluster().getClusterSize();
                indexReceiverNumber = Cluster.getCluster().getServerIndex();

                // check
                isMyMessage = PluginUtility.isMyRequest(new String(input),
                        totalReceiverNumber, indexReceiverNumber);

                //
                // By now translation process takes place into submitter.
                //
                // This is important in the case of a BufferedSubmitter in order to
                // achieve the highest frequency of packet submission by the
                // external system: in that case a request is inserted into the
                // temporary queue and AFTER is extracted by one submitter thread
                // and translated into an Task for the push listener.
                //
                // @TODO should submitter responsabilities (translation and
                // submission) be separated ?
                //
                if (isMyMessage){
                    submitter.submit(conf.getTaskBeanFile(),
                            input,
                            parser,
                            this.pushListenerInterface);

                }

            }

        } catch (UnknownHostException e) {
            log.error("Unknown host exception", e);
        } catch (IOException e) {
            log.error("IO exception", e);
        } catch (Exception e) {
            log.error("Generic exception", e);
        } finally {
            try {
                if (msocket != null){
                    if(!msocket.isClosed()){
                        msocket.leaveGroup(ia);
                        msocket.close();
                        Thread.sleep(1000);
                    }
                }
            } catch (Exception e) {
                log.error("Generic exception (finally)", e);
            }
        }
    }

    /**
     *
     */
    public void stopReading(){
        if (log.isTraceEnabled()) {
            log.trace("Stopping execution of multicastReadingThread [" + this + "]" );
        }
        try {
            if (msocket != null){
                if(!msocket.isClosed()){
                    msocket.leaveGroup(ia);
                    msocket.close();
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (log.isTraceEnabled()) {
            if (msocket != null){
                log.trace("MultiSocket is closed [" + msocket.isClosed() + "]" );
            } else {
                log.trace("MultiSocket is null" );
            }
        }
    }

    // --------------------------------------------------------- Private methods


    /**
     * Reset the buffer associated to a given <code>DatagramPacket</code>.
     * @param packet the packet which buffer has to be reset
     */
    private void resetPacket(DatagramPacket packet){
        packet.setData(new byte[Def.PACKET_SIZE]);
    }


}