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
 * This class represents the possible status code.
 *
 * @version $Id: StatusCode.java,v 1.2 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class StatusCode {

    public static final int IN_PROGRESS                                   = 101;
    public static final int OK                                            = 200;
    public static final int ITEM_ADDED                                    = 201;
    public static final int ACCEPTED_FOR_PROCESSING                       = 202;
    public static final int NONAUTHORITATIVE_RESPONSE                     = 203;
    public static final int NO_CONTENT                                    = 204;
    public static final int RESET_CONTENT                                 = 205;
    public static final int PARTIAL_CONTENT                               = 206;
    public static final int CONFLICT_RESOLVED_WITH_MERGE                  = 207;
    public static final int CONFLICT_RESOLVED_WITH_CLIENT_COMMAND_WINNING = 208;
    public static final int CONFLICT_RESOLVED_WITH_DUPLICATE              = 209;
    public static final int DELETE_WITHOUT_ARCHIVE                        = 210;
    public static final int ITEM_NOT_DELETED                              = 211;
    public static final int AUTHENTICATION_ACCEPTED                       = 212;
    public static final int CHUNKED_ITEM_ACCEPTED                         = 213;
    public static final int OPERATION_CANCELLED_OK                        = 214;
    public static final int NOT_EXECUTED                                  = 215;
    public static final int ATOMIC_ROLLBACK_OK                            = 216;
    public static final int MULTIPLE_CHOICES                              = 300;
    public static final int MOVED_PERMANENTLY                             = 301;
    public static final int FOUND                                         = 302;
    public static final int SEE_ANOTHER_URI                               = 303;
    public static final int NOT_MODIFIED                                  = 304;
    public static final int USE_PROXY                                     = 305;
    public static final int BAD_REQUEST                                   = 400;
    public static final int INVALID_CREDENTIALS                           = 401;
    public static final int PAYMENT_REQUIRED                              = 402;
    public static final int FORBIDDEN                                     = 403;
    public static final int NOT_FOUND                                     = 404;
    public static final int COMMAND_NOT_ALLOWED                           = 405;
    public static final int OPTIONAL_FEATURE_NOT_SUPPORTED                = 406;
    public static final int MISSING_CREDENTIALS                           = 407;
    public static final int REQUEST_TIMEOUT                               = 408;
    public static final int UPDATE_CONFLICT                               = 409;
    public static final int GONE                                          = 410;
    public static final int SIZE_REQUIRED                                 = 411;
    public static final int INCOMPLETE_COMMAND                            = 412;
    public static final int REQUESTED_ENTITY_TOO_LARGE                    = 413;
    public static final int URI_TOO_LONG                                  = 414;
    public static final int UNSUPPORTED_MEDIA_TYPE                        = 415;
    public static final int REQUESTED_SIZE_TOO_BIG                        = 416;
    public static final int RETRY_LATER                                   = 417;
    public static final int ALREADY_EXISTS                                = 418;
    public static final int CONFLICT_RESOLVED_WITH_SERVER_DATA            = 419;
    public static final int DEVICE_FULL                                   = 420;
    public static final int UNKNOWN_SEARCH_GRAMMAR                        = 421;
    public static final int BAD_CGI_SCRIPT                                = 422;
    public static final int SOFT_DELETE_CONFLICT                          = 423;
    public static final int OBJECT_SIZE_MISMATCH                          = 424;
    public static final int PERMISSION_DENIED                             = 425;
    public static final int COMMAND_FAILED                                = 500;
    public static final int COMMAND_NOT_IMPLEMENTED                       = 501;
    public static final int BAD_GATEWAY                                   = 502;
    public static final int SERVICE_UNAVAILABLE                           = 503;
    public static final int GATEWAY_TIMEOUT                               = 504;
    public static final int VERSION_NOT_SUPPORTED                         = 505;
    public static final int PROCESSING_ERROR                              = 506;
    public static final int ATOMIC_FAILED                                 = 507;
    public static final int REFRESH_REQUIRED                              = 508;
    public static final int RECIPIENT_EXCEPTION_RESERVED1                 = 509;
    public static final int DATASTORE_FAILURE                             = 510;
    public static final int SERVER_FAILURE                                = 511;
    public static final int SYNCHRONIZATION_FAILED                        = 512;
    public static final int PROTOCOL_VERSION_NOT_SUPPORTED                = 513;
    public static final int OPERATION_CANCELLED                           = 514;
    public static final int ATOMIC_ROLLBACK_FAILED                        = 516;
    public static final int ATOMIC_RESPONSE_TOO_LARGE_TO_FIT              = 517;

    /**
     * These codes are application specific; they are not part of the SyncML
     * protocol.
     */
    public static final int SESSION_EXPIRED = 10000;
    public static final int SESSION_ABORTED = 20000;

    private static Hashtable descriptions = null;

    static {
        descriptions = new Hashtable(71);

        descriptions.put(new Integer(IN_PROGRESS                                  ), "IN PROGRESS"                                  );
        descriptions.put(new Integer(OK                                           ), "OK"                                           );
        descriptions.put(new Integer(ITEM_ADDED                                   ), "ITEM ADDED"                                   );
        descriptions.put(new Integer(ACCEPTED_FOR_PROCESSING                      ), "ACCEPTED FOR PROCESSING"                      );
        descriptions.put(new Integer(NONAUTHORITATIVE_RESPONSE                    ), "NONAUTHORITATIVE RESPONSE"                    );
        descriptions.put(new Integer(NO_CONTENT                                   ), "NO CONTENT"                                   );
        descriptions.put(new Integer(RESET_CONTENT                                ), "RESET CONTENT"                                );
        descriptions.put(new Integer(PARTIAL_CONTENT                              ), "PARTIAL CONTENT"                              );
        descriptions.put(new Integer(CONFLICT_RESOLVED_WITH_MERGE                 ), "CONFLICT RESOLVED WITH MERGE"                 );
        descriptions.put(new Integer(CONFLICT_RESOLVED_WITH_CLIENT_COMMAND_WINNING), "CONFLICT RESOLVED WITH CLIENT COMMAND WINNING");
        descriptions.put(new Integer(CONFLICT_RESOLVED_WITH_DUPLICATE             ), "CONFLICT RESOLVED WITH DUPLICATE"             );
        descriptions.put(new Integer(DELETE_WITHOUT_ARCHIVE                       ), "DELETE WITHOUT ARCHIVE"                       );
        descriptions.put(new Integer(ITEM_NOT_DELETED                             ), "ITEM NOT DELETED"                             );
        descriptions.put(new Integer(AUTHENTICATION_ACCEPTED                      ), "AUTHENTICATION ACCEPTED"                      );
        descriptions.put(new Integer(CHUNKED_ITEM_ACCEPTED                        ), "CHUNKED_ITEM_ACCEPTED"                        );
        descriptions.put(new Integer(OPERATION_CANCELLED_OK                       ), "OPERATION_CANCELLED_OK"                       );
        descriptions.put(new Integer(NOT_EXECUTED                                 ), "NOT_EXECUTED"                                 );
        descriptions.put(new Integer(ATOMIC_ROLLBACK_OK                           ), "ATOMIC_ROLLBACK_OK"                           );
        descriptions.put(new Integer(MULTIPLE_CHOICES                             ), "MULTIPLE CHOICES"                             );
        descriptions.put(new Integer(MOVED_PERMANENTLY                            ), "MOVED PERMANENTLY"                            );
        descriptions.put(new Integer(FOUND                                        ), "FOUND"                                        );
        descriptions.put(new Integer(SEE_ANOTHER_URI                              ), "SEE ANOTHER URI"                              );
        descriptions.put(new Integer(NOT_MODIFIED                                 ), "NOT MODIFIED"                                 );
        descriptions.put(new Integer(USE_PROXY                                    ), "USE PROXY"                                    );
        descriptions.put(new Integer(BAD_REQUEST                                  ), "BAD REQUEST"                                  );
        descriptions.put(new Integer(INVALID_CREDENTIALS                          ), "INVALID CREDENTIALS"                          );
        descriptions.put(new Integer(PAYMENT_REQUIRED                             ), "PAYMENT REQUIRED"                             );
        descriptions.put(new Integer(FORBIDDEN                                    ), "FORBIDDEN"                                    );
        descriptions.put(new Integer(NOT_FOUND                                    ), "NOT FOUND"                                    );
        descriptions.put(new Integer(COMMAND_NOT_ALLOWED                          ), "COMMAND NOT ALLOWED"                          );
        descriptions.put(new Integer(OPTIONAL_FEATURE_NOT_SUPPORTED               ), "OPTIONAL FEATURE NOT SUPPORTED"               );
        descriptions.put(new Integer(MISSING_CREDENTIALS                          ), "MISSING CREDENTIALS"                          );
        descriptions.put(new Integer(REQUEST_TIMEOUT                              ), "REQUEST TIMEOUT"                              );
        descriptions.put(new Integer(UPDATE_CONFLICT                              ), "UPDATE CONFLICT"                              );
        descriptions.put(new Integer(GONE                                         ), "GONE"                                         );
        descriptions.put(new Integer(SIZE_REQUIRED                                ), "SIZE REQUIRED"                                );
        descriptions.put(new Integer(INCOMPLETE_COMMAND                           ), "INCOMPLETE COMMAND"                           );
        descriptions.put(new Integer(REQUESTED_ENTITY_TOO_LARGE                   ), "REQUESTED ENTITY TOO LARGE"                   );
        descriptions.put(new Integer(URI_TOO_LONG                                 ), "URI TOO LONG"                                 );
        descriptions.put(new Integer(UNSUPPORTED_MEDIA_TYPE                       ), "UNSUPPORTED MEDIA TYPE"                       );
        descriptions.put(new Integer(REQUESTED_SIZE_TOO_BIG                       ), "REQUESTED SIZE TOO BIG"                       );
        descriptions.put(new Integer(RETRY_LATER                                  ), "RETRY LATER"                                  );
        descriptions.put(new Integer(ALREADY_EXISTS                               ), "ALREADY EXISTS"                               );
        descriptions.put(new Integer(CONFLICT_RESOLVED_WITH_SERVER_DATA           ), "CONFLICT RESOLVED WITH SERVER DATA"           );
        descriptions.put(new Integer(DEVICE_FULL                                  ), "DEVICE FULL"                                  );
        descriptions.put(new Integer(UNKNOWN_SEARCH_GRAMMAR                       ), "UNKNOWN SEARCH GRAMMAR"                       );
        descriptions.put(new Integer(BAD_CGI_SCRIPT                               ), "BAD CGI SCRIPT"                               );
        descriptions.put(new Integer(SOFT_DELETE_CONFLICT                         ), "SOFT DELETE CONFLICT"                         );
        descriptions.put(new Integer(OBJECT_SIZE_MISMATCH                         ), "OBJECT_SIZE_MISMATCH"                         );
        descriptions.put(new Integer(PERMISSION_DENIED                            ), "PERMISSION_DENIED"                            );
        descriptions.put(new Integer(COMMAND_FAILED                               ), "COMMAND FAILED"                               );
        descriptions.put(new Integer(COMMAND_NOT_IMPLEMENTED                      ), "COMMAND NOT IMPLEMENTED"                      );
        descriptions.put(new Integer(BAD_GATEWAY                                  ), "BAD GATEWAY"                                  );
        descriptions.put(new Integer(SERVICE_UNAVAILABLE                          ), "SERVICE UNAVAILABLE"                          );
        descriptions.put(new Integer(GATEWAY_TIMEOUT                              ), "GATEWAY TIMEOUT"                              );
        descriptions.put(new Integer(VERSION_NOT_SUPPORTED                        ), "VERSION NOT SUPPORTED"                        );
        descriptions.put(new Integer(PROCESSING_ERROR                             ), "PROCESSING ERROR"                             );
        descriptions.put(new Integer(ATOMIC_FAILED                                ), "ATOMIC FAILED"                                );
        descriptions.put(new Integer(REFRESH_REQUIRED                             ), "REFRESH REQUIRED"                             );
        descriptions.put(new Integer(RECIPIENT_EXCEPTION_RESERVED1                ), "RECIPIENT EXCEPTION RESERVED1"                );
        descriptions.put(new Integer(DATASTORE_FAILURE                            ), "DATASTORE FAILURE"                            );
        descriptions.put(new Integer(SERVER_FAILURE                               ), "SERVER FAILURE"                               );
        descriptions.put(new Integer(SYNCHRONIZATION_FAILED                       ), "SYNCHRONIZATION FAILED"                       );
        descriptions.put(new Integer(PROTOCOL_VERSION_NOT_SUPPORTED               ), "PROTOCOL VERSION NOT SUPPORTED"               );
        descriptions.put(new Integer(OPERATION_CANCELLED                          ), "OPERATION_CANCELLED"                          );
        descriptions.put(new Integer(ATOMIC_ROLLBACK_FAILED                       ), "ATOMIC_ROLLBACK_FAILED"                       );
        descriptions.put(new Integer(ATOMIC_RESPONSE_TOO_LARGE_TO_FIT             ), "ATOMIC_RESPONSE_TOO_LARGE_TO_FIT"             );
        descriptions.put(new Integer(SESSION_EXPIRED                              ), "SESSION_EXPIRED"                              );
        descriptions.put(new Integer(SESSION_ABORTED                              ), "SESSION_ABORTED"                              );
    }

    /**
     * Returns the description associated to the given status code.
     *
     * @return the description associated to the given status code
     */
    public static String getStatusDescription(int code) {
        return (String)descriptions.get(new Integer(code));
    }

    /**
     *
     * This constructor is declared 'private'
     * because we do not want to allow anybody to
     * instantiate instances of the class
     *
     */
    private StatusCode()
    {
    }

}
