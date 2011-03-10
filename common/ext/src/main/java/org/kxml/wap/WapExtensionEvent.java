package org.kxml.wap;

import java.io.UnsupportedEncodingException;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.IOException;

import org.kxml.Xml;
import org.kxml.parser.ParseEvent;
import org.kxml.kdom.Document;
import org.kxml.io.AbstractXmlWriter;
import org.kxml.io.XmlWriter;

public class WapExtensionEvent extends ParseEvent
   {

   private int id;
   private Object content;

   public WapExtensionEvent(int id, Object content)
   {
      super(id, null);
      setContent(content);
   }

   /** returns Xml.WAP_EXTENSION */

   public int getType()
   {
      return Xml.WAP_EXTENSION;
   }

   /** returns the id of the WAP extendsion, one of Wbxml.EXT_0,
    Wbxml.EXT_1, Wbxml.EXT_2, Wbxml.EXT_T_0, Wbxml.EXT_T_1,
    Wbxml.EXT_T_2, Wbxml.EXT_I_0, Wbxml.EXT_I_1, Wbxml.EXT_I_2, or
    Wbxml.OPAQUE. */

   public int getId()
   {
      return id;
   }

   /** returns the content of the wap extension. The class
    depends on the type of the extension. null for EXT_0..EXT_2,
    String for EXT_I_0..EXT_I_2, Integer for EXT_T_0..EXT_T_2,
    byte [] for OPAQUE. */

   public Object getContent()
   {
      return content;
   }

   private void setContent(Object content)
   {
       if (content != null)
       {
          if (content instanceof byte[])
          {
              try
              {
                  ByteArrayInputStream is = new ByteArrayInputStream((byte[])content);
                  WbxmlParser parser = new WbxmlParser(is, -1);
                  Document document = new Document ();
                  document.parse(parser);
                  this.content=document;
              } catch(Exception ex)
              {
                  this.content=content;
              }

          }
          else
          {
              this.content=content;
          }
       }
   }

   public String getText()
      {
      if (content != null)
         {
         if (content instanceof byte[])
         {
            String stringRepresentation = null;
            try
               {
               // Convert the bytes to a String using the UTF-8 encoding so they can be
               // correctly converted back to a byte array (e.g. by Attribute's getValueAsBytes()
               // method.
               stringRepresentation = new String((byte[])content,"UTF-8");
               }
            catch (UnsupportedEncodingException e)
               {
               stringRepresentation = new String((byte[])content);
               }
            return stringRepresentation;
         }
         else if(content instanceof Document)
         {
             try
             {
                 StringWriter out = new StringWriter();
                 AbstractXmlWriter writer = new XmlWriter(out);
                 Document document = (Document) content;
                 document.write(writer);
                 writer.close();
                 return out.toString();
             } catch(IOException ex)
             {
                 ex.printStackTrace();
             }
         }
         else
            {
            return content.toString();
            }
         }
      return null;
      }
   }
