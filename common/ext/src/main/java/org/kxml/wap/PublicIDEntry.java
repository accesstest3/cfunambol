package org.kxml.wap;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PublicIDEntry {

  private Integer myPublicIDCode;
  private String myPublicIdentifier;
  private String myRootElement;
  private String myDtdUri;
  private String myNameSpace;
  private int myStrTableIndex;
  private PublicIDEntry() {
  }

  public PublicIDEntry(   Integer aPublicIDCode,
                          String aPublicIdentifier,
                          String aRootElement,
                          String aDtdUri,
                          String aNameSpace,
                          int aStrTableIndex)
  {
    myPublicIDCode=aPublicIDCode;
    myPublicIdentifier=aPublicIdentifier;
    myRootElement=aRootElement;
    myDtdUri=aDtdUri;
    myNameSpace=aNameSpace;
    myStrTableIndex=aStrTableIndex;
  }

  public Integer getPublicIDCode() {
    return myPublicIDCode;
  }
  public void setPublicIDCode(Integer aPublicIDCode) {
    myPublicIDCode = aPublicIDCode;
  }
  public void setPublicIdentifier(String aPublicIdentifier) {
    myPublicIdentifier = aPublicIdentifier;
  }
  public String getPublicIdentifier() {
    return myPublicIdentifier;
  }
  public void setRootElement(String aRootElement) {
    myRootElement = aRootElement;
  }
  public String getRootElement() {
    return myRootElement;
  }
  public void setDtdUri(String aDtdUri) {
    myDtdUri = aDtdUri;
  }
  public String getDtdUri() {
    return myDtdUri;
  }
  public void setNameSpace(String aNameSpace) {
    myNameSpace = aNameSpace;
  }
  public String getNameSpace() {
    return myNameSpace;
  }

  public int getStrTableIndex() { return myStrTableIndex; }

  public void setStrTableIndex(int aStrTableIndex)
  {
    myStrTableIndex=aStrTableIndex;
  }
}