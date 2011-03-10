/*
 * <FUNAMBOLCOPYRIGHT>
 * Copyright (C) 2009 Funambol.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Funambol.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Funambol MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. Funambol SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * </FUNAMBOLCOPYRIGHT>
 */

package com.funambol.common.pim.common;

import com.funambol.framework.tools.merge.MergeResult;

/**
 * This interface is for all those fields that are to be stored in lists that
 * can be merged with the following logics:
 * <ul>
 * <li>The items in one list are matched with the ones with the same URIs in the
 * other list</li>
 * <li>Matching items are merged on the basis of their merge method</li>
 * <li>Non-matching items are added into the other list as well</li>
 * </ul>
 * Thus, what is necessary in this interface is just a <code>getUri</code> 
 * method and a <code>merge</code> method.
 *
 * @version $Id$
 */
public interface MergeableWithURI {

    public String getUri();

    public MergeResult merge(Object other);

}
