package org.kxml.wap;

/**
 * Represents a WbxmlInitialiser for DevInf
 *
 * @version $Id: SyncMLDevInfInitialiser.java,v 1.1.1.1 2006-10-28 22:09:32 stefano_fornari Exp $
 */

class SyncMLDevInfInitialiser extends WbxmlInitialiser {

    public SyncMLDevInfInitialiser() {
    }

    public void initialise(WbxmlParser aParser) {
        String publicIdentifier = null;
        int publicIdentifierId  = 0;
        if (aParser != null) {
            publicIdentifier   = aParser.getPublicIdentifier();
            publicIdentifierId = aParser.getPublicIdentifierId();
        }

        if (publicIdentifier != null &&
            "-//SYNCML//DTD DevInf 1.2//EN".equals(publicIdentifier)) {
            aParser.setTagTable(0, SyncML.tagTableDevInf12);
        } else if (publicIdentifierId == WbxmlInitialiserFactory.SYNCML_DEVINF12_PUBLIC_ID_CODE.intValue()) {
            aParser.setTagTable(0, SyncML.tagTableDevInf12);
        } else {
            aParser.setTagTable(0, SyncML.tagTableDevInf);
        }

        aParser.addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD DevInf 1.0//EN",
        "DevInf","http://www.syncml.org/docs/devinf_v11_20020215.dtd", "syncml:devinf",0));

        aParser.addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD DevInf 1.1//EN",
        "DevInf","http://www.syncml.org/docs/devinf_v11_20020215.dtd", "syncml:devinf",0));

        aParser.addPublicIDEntry(new PublicIDEntry(null,"-//SYNCML//DTD DevInf 1.2//EN",
        "DevInf","http://www.syncml.org/docs/devinf_v11_20020215.dtd", "syncml:devinf",0));

    }
}
