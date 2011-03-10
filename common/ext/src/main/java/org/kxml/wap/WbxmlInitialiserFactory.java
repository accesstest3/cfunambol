package org.kxml.wap;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
import java.util.HashMap;
import java.lang.Integer;

class WbxmlInitialiserFactory {


    static final Integer  UNKNOWN_PUBLIC_ID_CODE   = Integer.valueOf("01",16);
    static final Integer  WML10_PUBLIC_ID_CODE     = Integer.valueOf("02",16);
    static final Integer  WTA10_PUBLIC_ID_CODE     = Integer.valueOf("03",16);
    static final Integer  WML11_PUBLIC_ID_CODE     = Integer.valueOf("04",16);
    static final Integer  SI10_PUBLIC_ID_CODE      = Integer.valueOf("05",16);
    static final Integer  SL10_PUBLIC_ID_CODE      = Integer.valueOf("06",16);
    static final Integer  CO10_PUBLIC_ID_CODE      = Integer.valueOf("07",16);
    static final Integer  CHANNEL11_PUBLIC_ID_CODE = Integer.valueOf("08",16);
    static final Integer  WML12_PUBLIC_ID_CODE     = Integer.valueOf("09",16);
    static final Integer  WML13_PUBLIC_ID_CODE     = Integer.valueOf("0A",16);
    static final Integer  PROV10_PUBLIC_ID_CODE    = Integer.valueOf("0B",16);
    static final Integer  WTAWML12_PUBLIC_ID_CODE  = Integer.valueOf("0C",16);
    static final Integer  EMN10_PUBLIC_ID_CODE     = Integer.valueOf ("0D",16);
    static final Integer  DRMREL10_PUBLIC_ID_CODE  = Integer.valueOf("0E",16);

    /** SyncML 1.0 **/
    static final Integer  SYNCML_SYNCML10_PUBLIC_ID_CODE=Integer.valueOf("0FD1",16);
    static final Integer  SYNCML_DEVINF10_PUBLIC_ID_CODE=Integer.valueOf("0FD2",16);
    static final Integer  SYNCML_METINF10_PUBLIC_ID_CODE=Integer.valueOf("01",16);

    /** SyncML 1.1 **/
    static final Integer  SYNCML_SYNCML11_PUBLIC_ID_CODE=Integer.valueOf("0FD3",16);
    static final Integer  SYNCML_DEVINF11_PUBLIC_ID_CODE=Integer.valueOf("0FD4",16);
    static final Integer  SYNCML_METINF11_PUBLIC_ID_CODE=Integer.valueOf("01",16);

    /** SyncML 1.2 **/
    static final Integer  SYNCML_SYNCML12_PUBLIC_ID_CODE=Integer.valueOf("1201",16);
    static final Integer  SYNCML_DEVINF12_PUBLIC_ID_CODE=Integer.valueOf("1203",16);
    static final Integer  SYNCML_METINF12_PUBLIC_ID_CODE=Integer.valueOf("1202",16);

    private static final String WML10_PUBLIC_ID           = "-//WAPFORUM//DTD WML 1.0//EN";
    private static final String WTA10_PUBLIC_ID           = "-//WAPFORUM//DTD WTA 1.0//EN";
    private static final String WML11_PUBLIC_ID           = "-//WAPFORUM//DTD WML 1.1//EN";
    private static final String SI10_PUBLIC_ID            = "-//WAPFORUM//DTD SI 1.0//EN";
    private static final String SL10_PUBLIC_ID            = "-//WAPFORUM//DTD SL 1.0//EN";
    private static final String CO10_PUBLIC_ID            = "-//WAPFORUM//DTD CO 1.0//EN";
    private static final String CHANNEL11_PUBLIC_ID       = "-//WAPFORUM//DTD CHANNEL 1.1//EN";
    private static final String WML12_PUBLIC_ID           = "-//WAPFORUM//DTD WML 1.2//EN";
    private static final String WML13_PUBLIC_ID           = "-//WAPFORUM//DTD WML 1.3//EN";
    private static final String PROV10_PUBLIC_ID          = "-//WAPFORUM//DTD PROV 1.0//EN";
    private static final String WTAWML12_PUBLIC_ID        = "-//WAPFORUM//DTD WTA-WML 1.2//EN";
    private static final String CHANNEL12_PUBLIC_ID       = "-//WAPFORUM//DTD CHANNEL 1.2//EN";
    private static final String EMN10_PUBLIC_ID           = "-//WAPFORUM//DTD EMN 1.0//EN";
    private static final String DRMREL10_PUBLIC_ID        = "-//OMA//DTD DRMREL 1.0//EN";
    private static final String SYNCML_SYNCML12_PUBLIC_ID = "-//SYNCML//DTD SyncML 1.2//EN";
    private static final String SYNCML_DEVINF12_PUBLIC_ID = "-//SYNCML//DTD DevInf 1.2//EN";
    private static final String SYNCML_METINF12_PUBLIC_ID = "-//SYNCML//DTD MetInf 1.2//EN";
    private static final String SYNCML_SYNCML11_PUBLIC_ID = "-//SYNCML//DTD SyncML 1.1//EN";
    private static final String SYNCML_DEVINF11_PUBLIC_ID = "-//SYNCML//DTD DevInf 1.1//EN";
    private static final String SYNCML_METINF11_PUBLIC_ID = "-//SYNCML//DTD MetInf 1.1//EN";
    private static final String SYNCML_SYNCML10_PUBLIC_ID = "-//SYNCML//DTD SyncML 1.0//EN";
    private static final String SYNCML_DEVINF10_PUBLIC_ID = "-//SYNCML//DTD DevInf 1.0//EN";
    private static final String SYNCML_METINF10_PUBLIC_ID = "-//SYNCML//DTD MetInf 1.0//EN";

    private static WbxmlInitialiserFactory theFactory=null;
    private HashMap myPublicIdentifierMap;
    private HashMap myPublicIdentifierCodeMap;

    private WbxmlInitialiserFactory()
    throws ClassNotFoundException
    {
        myPublicIdentifierMap= new HashMap();
        myPublicIdentifierCodeMap =new HashMap();

        Class initClass;

        initClass=Class.forName("org.kxml.wap.SyncMLInitialiser");
        myPublicIdentifierMap.put(SYNCML_SYNCML10_PUBLIC_ID.toUpperCase(), initClass );
        myPublicIdentifierMap.put(SYNCML_SYNCML11_PUBLIC_ID.toUpperCase(), initClass );
        myPublicIdentifierMap.put(SYNCML_SYNCML12_PUBLIC_ID.toUpperCase(), initClass );
        myPublicIdentifierCodeMap.put(SYNCML_SYNCML10_PUBLIC_ID_CODE, initClass);
        myPublicIdentifierCodeMap.put(SYNCML_SYNCML11_PUBLIC_ID_CODE, initClass);
        myPublicIdentifierCodeMap.put(SYNCML_SYNCML12_PUBLIC_ID_CODE, initClass);

        initClass=Class.forName("org.kxml.wap.SyncMLDevInfInitialiser");
        myPublicIdentifierMap.put(SYNCML_DEVINF10_PUBLIC_ID.toUpperCase(), initClass );
        myPublicIdentifierMap.put(SYNCML_DEVINF11_PUBLIC_ID.toUpperCase(), initClass );
        myPublicIdentifierMap.put(SYNCML_DEVINF12_PUBLIC_ID.toUpperCase(), initClass );
        myPublicIdentifierCodeMap.put(SYNCML_DEVINF10_PUBLIC_ID_CODE, initClass);
        myPublicIdentifierCodeMap.put(SYNCML_DEVINF11_PUBLIC_ID_CODE, initClass);
        myPublicIdentifierCodeMap.put(SYNCML_DEVINF12_PUBLIC_ID_CODE, initClass);

        initClass=Class.forName("org.kxml.wap.SyncMLMetaInfInitialiser");
        myPublicIdentifierMap.put(SYNCML_METINF10_PUBLIC_ID.toUpperCase(), initClass );
        myPublicIdentifierMap.put(SYNCML_METINF11_PUBLIC_ID.toUpperCase(), initClass );
        myPublicIdentifierMap.put(SYNCML_METINF12_PUBLIC_ID.toUpperCase(), initClass );
        myPublicIdentifierCodeMap.put(SYNCML_METINF10_PUBLIC_ID_CODE, initClass);
        myPublicIdentifierCodeMap.put(SYNCML_METINF11_PUBLIC_ID_CODE, initClass);
        myPublicIdentifierCodeMap.put(SYNCML_METINF12_PUBLIC_ID_CODE, initClass);

        initClass=Class.forName("org.kxml.wap.WMLInitialiser");
        myPublicIdentifierMap.put(WML10_PUBLIC_ID.toUpperCase(), initClass );
        myPublicIdentifierMap.put(WML11_PUBLIC_ID.toUpperCase(), initClass );
        myPublicIdentifierCodeMap.put(WML10_PUBLIC_ID_CODE, initClass );
        myPublicIdentifierCodeMap.put(WML11_PUBLIC_ID_CODE, initClass );


        /** @todo finish loading other document type */
    }

    private WbxmlInitialiser getInitByPublicIdentifier(String anIdentifier)
    throws IllegalAccessException, InstantiationException
    {
        Class initClass=(Class)myPublicIdentifierMap.get(anIdentifier.toUpperCase());
        if (initClass == null) {
            return null;
        }
        WbxmlInitialiser init = (WbxmlInitialiser)initClass.newInstance();
        return init;
    }

    private WbxmlInitialiser getInitByPublicIdentifierCode(int aCode)
    throws IllegalAccessException, InstantiationException
    {
        Class initClass=(Class)myPublicIdentifierCodeMap.get(new Integer(aCode));
        if (initClass == null) {
            return null;
        }
        WbxmlInitialiser init = (WbxmlInitialiser)initClass.newInstance();
        return init;
    }

    public static WbxmlInitialiser getInitialiserByPublicIdentifier(String anIdentifier)
    throws IllegalAccessException, InstantiationException, ClassNotFoundException
    {
        if(theFactory==null)
        {
            theFactory = new WbxmlInitialiserFactory();
        }
        return theFactory.getInitByPublicIdentifier(anIdentifier);
    }

    public static WbxmlInitialiser getInitialiserByPublicIdentifierCode(int aCode)
    throws IllegalAccessException, InstantiationException, ClassNotFoundException
    {
        if(theFactory==null)
        {
            theFactory = new WbxmlInitialiserFactory();
        }
        return theFactory.getInitByPublicIdentifierCode(aCode);
    }

}
