/* kXML
 *
 * The contents of this file are subject to the Enhydra Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License
 * on the Enhydra web site ( http://www.enhydra.org/ ).
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific terms governing rights and limitations
 * under the License.
 *
 * The Initial Developer of kXML is Stefan Haustein. Copyright (C)
 * 2000, 2001 Stefan Haustein, D-46045 Oberhausen (Rhld.),
 * Germany. All Rights Reserved.
 *
 * Contributor(s): Nicola Fankhauser
 *
 * */

package org.kxml.wap;

import java.io.*;

/**
 * A parser for SyncML built on top of the WbxmlParser by
 * setting the corresponding TagTable defined in the class SyncML
 *
 * @author Nicola Fankhauser
 * @version $Id: SyncMLParser.java,v 1.1.1.1 2006-10-28 22:09:32 stefano_fornari Exp $
 */
public class SyncMLParser extends WbxmlParser {

    /**
     * Calls constructor of WbxmlParser and sets then the appropriate tag table
     *
     * @param in an <code>InputStream</code> value
     * @exception IOException if an error occurs
     */
    public SyncMLParser(InputStream in) throws Exception {
        this(in, -1, null);
    }


    public SyncMLParser(InputStream in, int expectedVersion) throws Exception {
        this(in, expectedVersion, null);
    }


    public SyncMLParser(InputStream in, String charset) throws Exception {
        this(in, -1, charset);
    }


    public SyncMLParser(InputStream in, int expectedVersion, String charset) throws Exception {
        super (in, expectedVersion, charset);

        setTagTable(0, SyncML.tagTableSyncml);
        setTagTable(1, SyncML.tagTableMetainf);
        setTagTable(2, SyncML.tagTableDevInf);

        addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD SyncML 1.0//EN",
        "SyncML","http://www.syncml.org/docs/syncml_represent_v11_20020215.dtd","SYNCML:SYNCML1.0",0));
        addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD MetInf 1.0//EN",
        "MetInf","http://www.syncml.org/docs/syncml_metinf_v11_20020215.dtd", "syncml:metinf",1));
        addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD DevInf 1.0//EN",
        "DevInf","http://www.syncml.org/docs/devinf_v11_20020215.dtd", "syncml:devinf",2));

        addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD SyncML 1.1//EN",
        "SyncML","http://www.syncml.org/docs/syncml_represent_v11_20020215.dtd","SYNCML:SYNCML1.1",0));
        addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD MetInf 1.1//EN",
        "MetInf","http://www.syncml.org/docs/syncml_metinf_v11_20020215.dtd", "syncml:metinf",1));
        addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD DevInf 1.1//EN",
        "DevInf","http://www.syncml.org/docs/devinf_v11_20020215.dtd", "syncml:devinf",2));

        addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD SyncML 1.2//EN",
        "SyncML","http://www.openmobilealliance.org/tech/DTD/OMA-TS-SyncML_RepPro_DTD-V1_2.dtd","SYNCML:SYNCML1.2",0));
        addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD MetInf 1.2//EN",
        "MetInf","http://www.openmobilealliance.org/tech/DTD/OMA-TS-SyncML_MetaInfo_DTD-V1_2.dtd", "syncml:metinf",1));
        addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD DevInf 1.2//EN",
        "DevInf","http://www.syncml.org/docs/devinf_v11_20020215.dtd", "syncml:devinf",2));

    }
}
