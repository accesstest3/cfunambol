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
 * $Id: kXMLPrintWriter.java,v 1.2 2008-05-14 16:22:03 nichele Exp $
 */
public class kXMLPrintWriter extends Writer
{
   private Writer out;

   /**
    */

   public kXMLPrintWriter( Writer out )
   {
       super( out );
       this.out = out;
   }

   public void close()
   {
       try {
	   out.close();
       }
       catch( IOException e ){
       }
   }

   public void flush()
   {
       try {
	   out.flush();
       }
       catch( IOException e ){
       }
   }

   public void write( int c )
   {
       try {
	   out.write( c );
       }
       catch( IOException e ){
       }
   }

   public void write( char buf[], int off, int len )
   {
       try {
	   out.write( buf, off, len );
       }
       catch( IOException e ){
       }
   }

   public void write( String s )
   {
       try {
	   out.write( s, 0, s.length() );
       }
       catch( IOException e ){
       }
   }

   /**
    */

   public void print( char ch )
   {
       write( String.valueOf( ch ) );
   }

   /**
    */

   private void newLine()
   {
       write( '\n' );
   }

   /**
    */

   public void print( String str )
   {
       if( str == null ){
	   str = "null";
       }
       write( str );
   }

   /**
    */

   public void println( char ch )
   {
       print( ch );
       newLine();
   }

   /**
    */

   public void println( String str )
   {
       print( str );
       newLine();
   }
}
