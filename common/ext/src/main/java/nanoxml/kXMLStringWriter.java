/* This file is part of NanoXML.
 *
 *
 * Copyright (C) 2000 Marc De Scheemaecker, All Rights Reserved.
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software in
 *     a product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 *
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *
 *  3. This notice may not be removed or altered from any source distribution.
 */

package nanoxml;

import java.io.*;

/**
 * $Id: kXMLStringWriter.java,v 1.2 2008-05-14 16:22:03 nichele Exp $
 */
public class kXMLStringWriter extends Writer
{
   private StringBuffer buf;

   /**
    */

   public kXMLStringWriter()
   {
       buf = new StringBuffer();
   }

   /**
    */

   public void close()
   {
   }

   /**
    */

   public void flush()
   {
   }

   /**
    */

   public void write( int c )
   {
       buf.append( (char) c );
   }

   /**
    */

   public void write( char b[], int off, int len )
   {
       buf.append( b, off, len );
   }

   /**
    */

   public void write( String str )
   {
       buf.append( str );
   }

   /**
    */

   public String toString()
   {
       return buf.toString();
   }
}
