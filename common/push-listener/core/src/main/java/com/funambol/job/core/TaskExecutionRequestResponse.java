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
package com.funambol.job.core;

import java.io.Serializable;

/**
 * Represents an object which is returned by the JobExecutor listener
 * after receiving an task execution request.
 * @version $Id: TaskExecutionRequestResponse.java 35930 2010-09-08 11:25:14Z onufryk $
 */
public class TaskExecutionRequestResponse implements Serializable{
    private static final long serialVersionUID = -5136351640488858976L;   
    /**
     * Result of the task insertion
     */
    private boolean accepted=false;

    /**
     * Error message returned by the job executor
     */
    private String errorCode;
    /**
     * Error message returned by the job executor
     */
    private String errrorMessage;

    /**
     * Constructs new object with specified properties.
     * @param id UID of the inserted task
     * @param inserted Result of the task insertion
     */
    public TaskExecutionRequestResponse(boolean accepted) {
        this.accepted = accepted;
    }

    /**
     * Constructs new object with specified properties.
     * @param id UID of the inserted task
     * @param inserted Result of the task insertion
     */
    public TaskExecutionRequestResponse(boolean accepted,String errorCode,String errorMessage) {
        this.accepted = accepted;
        this.errorCode=errorCode;
        this.errrorMessage=errorMessage;
    }
    /**
     * Returns result of the task insertion.
     * @return the result of the task insertion
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Sets result of the task insertion.
     * @param inserted result of the task insertion
     */
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errrorMessage
     */
    public String getErrrorMessage() {
        return errrorMessage;
    }

    /**
     * @param errrorMessage the errrorMessage to set
     */
    public void setErrrorMessage(String errrorMessage) {
        this.errrorMessage = errrorMessage;
    }
}
