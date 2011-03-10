/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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
package com.funambol.email.model;


/**
 *
 * @version $Id: FlagProperties.java,v 1.1 2008-03-25 11:28:19 gbmiglia Exp $
 */
public class FlagProperties
{

   private boolean seen      ;
   private boolean answered  ;
   private boolean deleted   ;
   private boolean draft     ;
   private boolean flagged   ;
   private boolean forwarded ;

   /**
    *
    */
   public FlagProperties(){
   }

   /**
    *
    * @param _seen boolean
    * @param _answered boolean
    * @param _deleted boolean
    * @param _draft boolean
    * @param _flagged boolean
    * @param _forwarded boolean
    */
   public FlagProperties(boolean _seen,
                         boolean _answered,
                         boolean _deleted,
                         boolean _draft,
                         boolean _flagged,
                         boolean _forwarded){
      this.seen     = _seen;
      this.answered = _answered;
      this.deleted  = _deleted;
      this.draft    = _draft;
      this.flagged  = _flagged;
      this.forwarded  = _forwarded;

   }

   public void setSeen(boolean _seen){
       this.seen = _seen;
   }
   public void setAnswered(boolean _answered){
       this.answered = _answered;
   }
   public void setDeleted(boolean _deleted){
       this.deleted = _deleted;
   }
   public void setDraft(boolean _draft){
       this.draft = _draft;
   }
   public void setFlagged(boolean _flagged){
       this.flagged = _flagged;
   }
   public void setForwarded(boolean _forwarded){
       this.forwarded = _forwarded;
   }

   public boolean getSeen( ){
       return this.seen;
   }
   public boolean getAnswered( ){
       return this.answered;
   }
   public boolean getDeleted( ){
       return this.deleted;
   }
   public boolean getDraft( ){
       return this.draft;
   }
   public boolean getFlagged( ){
       return this.flagged;
   }
   public boolean getForwarded( ){
       return this.forwarded;
   }

   /**
    *
    *
    * @return String
    */
    @Override
   public String toString(){
       StringBuilder sb = new StringBuilder();
       sb.append(this.seen).append(',');
       sb.append(this.answered).append(',');
       sb.append(this.forwarded).append(',');
       sb.append(this.flagged).append(',');
       sb.append(this.deleted).append(',');
       sb.append(this.draft);

       return sb.toString();
   }

}
