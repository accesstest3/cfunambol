package org.kxml.wap;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

class SyncMLInitialiser extends WbxmlInitialiser {

    public SyncMLInitialiser() {
    }
    public void initialise(WbxmlParser aParser) {
        aParser.setTagTable(0, SyncML.tagTableSyncml);
        aParser.setTagTable(1, SyncML.tagTableMetainf);
        aParser.setTagTable(2, SyncML.tagTableDevInf);

        aParser.addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD SyncML 1.0//EN",
        "SyncML","http://www.syncml.org/docs/syncml_represent_v11_20020215.dtd","SYNCML:SYNCML1.0",0));
        aParser.addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD MetInf 1.0//EN",
                "MetInf","http://www.syncml.org/docs/syncml_metinf_v11_20020215.dtd", "syncml:metinf",1));
        aParser.addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD DevInf 1.0//EN",
        "DevInf","http://www.syncml.org/docs/devinf_v11_20020215.dtd", "syncml:devinf",2));

        aParser.addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD SyncML 1.1//EN",
        "SyncML","http://www.syncml.org/docs/syncml_represent_v11_20020215.dtd","SYNCML:SYNCML1.1",0));
        aParser.addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD MetInf 1.1//EN",
                "MetInf","http://www.syncml.org/docs/syncml_metinf_v11_20020215.dtd", "syncml:metinf",1));
        aParser.addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD DevInf 1.1//EN",
        "DevInf","http://www.syncml.org/docs/devinf_v11_20020215.dtd", "syncml:devinf",2));

        aParser.addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD SyncML 1.2//EN",
        "SyncML","http://www.openmobilealliance.org/tech/DTD/OMA-TS-SyncML_RepPro_DTD-V1_2.dtd","SYNCML:SYNCML1.2",0));
        aParser.addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD MetInf 1.2//EN",
        "MetInf","http://www.openmobilealliance.org/tech/DTD/OMA-TS-SyncML_MetaInfo_DTD-V1_2.dtd", "syncml:metinf",1));
        aParser.addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD DevInf 1.2//EN",
        "DevInf","http://www.syncml.org/docs/devinf_v11_20020215.dtd", "syncml:devinf",2));

    }
}
