package org.kxml.wap;

/**
 * Represents a WbxmlInitialiser for MetaInf
 *
 * @version $Id: SyncMLMetaInfInitialiser.java,v 1.1.1.1 2006-10-28 22:09:32 stefano_fornari Exp $
 */

class SyncMLMetaInfInitialiser extends WbxmlInitialiser {

    public SyncMLMetaInfInitialiser() {
    }
    public void initialise(WbxmlParser aParser) {
        aParser.setTagTable(0, SyncML.tagTableMetainf);

        aParser.addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD MetInf 1.0//EN",
        "MetInf","http://www.syncml.org/docs/syncml_metinf_v11_20020215.dtd", "syncml:metinf",0));

        aParser.addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD MetInf 1.1//EN",
        "MetInf","http://www.syncml.org/docs/syncml_metinf_v11_20020215.dtd", "syncml:metinf",0));

        aParser.addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD MetInf 1.2//EN",
        "MetInf","http://www.openmobilealliance.org/tech/DTD/OMA-TS-SyncML_MetaInfo_DTD-V1_2.dtd", "syncml:metinf",0));

    }
}
