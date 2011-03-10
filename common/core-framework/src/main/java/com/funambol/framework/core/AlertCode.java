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
package com.funambol.framework.core;

import java.util.Hashtable;

/**
 * This class represents the possible alerts code.
 *
 * @version $Id: AlertCode.java,v 1.8 2007/06/15 14:43:35 luigiafassina Exp $
 */
public class AlertCode {
    public final static int DISPLAY                       = 100;
    public final static int TWO_WAY                       = 200;
    public final static int SLOW                          = 201;
    public final static int ONE_WAY_FROM_CLIENT           = 202;
    public final static int REFRESH_FROM_CLIENT           = 203;
    public final static int ONE_WAY_FROM_SERVER           = 204;
    public final static int REFRESH_FROM_SERVER           = 205;
    public final static int TWO_WAY_BY_SERVER             = 206;
    public final static int ONE_WAY_FROM_CLIENT_BY_SERVER = 207;
    public final static int REFRESH_FROM_CLIENT_BY_SERVER = 208;
    public final static int ONE_WAY_FROM_SERVER_BY_SERVER = 209;
    public final static int REFRESH_FROM_SERVER_BY_SERVER = 210;
    public final static int RESULT_ALERT                  = 221;
    public final static int NEXT_MESSAGE                  = 222;
    public final static int NO_END_OF_DATA                = 223;
    public final static int SUSPEND                       = 224;
    public final static int RESUME                        = 225;

    public final static int SMART_ONE_WAY_FROM_CLIENT     = 250;

    public final static int HEART_BEAT                    = 666;

    private AlertCode() {}

    /**
     * Determines if the given code is an initialization code, such as one of:
     * <ul>
     *   <li> TWO_WAY
     *   <li> SLOW
     *   <li> ONE_WAY_FROM_CLIENT
     *   <li> REFRESH_FROM_CLIENT
     *   <li> ONE_WAY_FROM_SERVER
     *   <li> REFRESH_FROM_SERVER
     *   <li> TWO_WAY_BY_SERVER
     *   <li> ONE_WAY_FROM_CLIENT_BY_SERVE
     *   <li> REFRESH_FROM_CLIENT_BY_SERVER
     *   <li> ONE_WAY_FROM_SERVER_BY_SERVER
     *   <li> REFRESH_FROM_SERVER_BY_SERVER
     *   <li> SMART_ONE_WAY_FROM_CLIENT
     * </ul>
     *
     * @param code the code to be checked
     *
     * @return true if the code is an initialization code, false otherwise
     */
    static public boolean isInitializationCode(int code) {
        return (   (code == TWO_WAY                      )
                || (code == SLOW                         )
                || (code == ONE_WAY_FROM_CLIENT          )
                || (code == REFRESH_FROM_CLIENT          )
                || (code == ONE_WAY_FROM_SERVER          )
                || (code == REFRESH_FROM_SERVER          )
                || (code == TWO_WAY_BY_SERVER            )
                || (code == ONE_WAY_FROM_CLIENT_BY_SERVER)
                || (code == REFRESH_FROM_CLIENT_BY_SERVER)
                || (code == ONE_WAY_FROM_SERVER_BY_SERVER)
                || (code == REFRESH_FROM_SERVER_BY_SERVER)
                || (code == SMART_ONE_WAY_FROM_CLIENT    )
                || (code == RESUME                       )
         );
    }

    /**
     * Determines if the given code represents a client only action, such as is
     * one of:
     * <ul>
     *   <li>ONE_WAY_FROM_CLIENT</li>
     *   <li>REFRESH_FROM_CLIENT</li>
     *   <li>SMART_ONE_WAY_FROM_CLIENT</li>
     * </ul>
     *
     * @param code the code to be checked
     *
     * @return true if the code represents a client only action, false otherwise
     */
    static public boolean isClientOnlyCode(int code) {
        return ((code == ONE_WAY_FROM_CLIENT) || 
                (code == REFRESH_FROM_CLIENT) || 
                (code == SMART_ONE_WAY_FROM_CLIENT));
    }

    private static Hashtable descriptions = null;

    static {
        descriptions = new Hashtable(18);

        descriptions.put(new Integer(DISPLAY                      ), "DISPLAY"                      );
        descriptions.put(new Integer(TWO_WAY                      ), "TWO-WAY"                      );
        descriptions.put(new Integer(SLOW                         ), "SLOW SYNC"                    );
        descriptions.put(new Integer(ONE_WAY_FROM_CLIENT          ), "ONE-WAY FROM CLIENT");
        descriptions.put(new Integer(REFRESH_FROM_CLIENT          ), "REFRESH FROM CLIENT");
        descriptions.put(new Integer(ONE_WAY_FROM_SERVER          ), "ONE-WAY FROM SERVER");
        descriptions.put(new Integer(REFRESH_FROM_SERVER          ), "REFRESH FROM SERVER");
        descriptions.put(new Integer(TWO_WAY_BY_SERVER            ), "TWO-WAY BY SERVER"            );
        descriptions.put(new Integer(ONE_WAY_FROM_CLIENT_BY_SERVER), "ONE-WAY FROM CLIENT BY SERVER");
        descriptions.put(new Integer(REFRESH_FROM_CLIENT_BY_SERVER), "REFRESH FROM CLIENT BY SERVER");
        descriptions.put(new Integer(ONE_WAY_FROM_SERVER_BY_SERVER), "ONE-WAY FROM SERVER BY SERVER");
        descriptions.put(new Integer(REFRESH_FROM_SERVER_BY_SERVER), "REFRESH FROM SERVER BY SERVER");
        descriptions.put(new Integer(SMART_ONE_WAY_FROM_CLIENT    ), "SMART ONE WAY FROM CLIENT"    );
        descriptions.put(new Integer(RESULT_ALERT                 ), "RESULT ALERT"                 );
        descriptions.put(new Integer(NEXT_MESSAGE                 ), "NEXT MESSAGE"                 );
        descriptions.put(new Integer(NO_END_OF_DATA               ), "NO END OF DATA"               );
        descriptions.put(new Integer(SUSPEND                      ), "SUSPEND"                      );
        descriptions.put(new Integer(RESUME                       ), "RESUME"                       );
        descriptions.put(new Integer(HEART_BEAT                   ), "HEART_BEAT"                   );
    }

    /**
     * Returns the description associated to the given alert code.
     *
     * @return the description associated to the given alert code
     */
    public static String getAlertDescription(int code) {
        return (String)descriptions.get(new Integer(code));
    }
}
