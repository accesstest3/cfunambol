<%@page import="com.funambol.server.config.Configuration"%><?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE autoupdate_catalog PUBLIC "-//NetBeans//DTD Autoupdate Catalog 2.3//EN"
"http://www.netbeans.org/dtds/autoupdate-catalog-2_3.dtd">

<%
  String url = Configuration.getConfiguration().getServerConfig().getEngineConfiguration().getServerURI();

  // if serverURI is null or empty, default to localhost
  // otherwise, if it ends with /ds, we need to remove it
  if ((url == null) || (url.length() == 0)) {
      url = "http://localhost:8080/funambol/autoupdate";
  } else {
    if (url.endsWith("/ds")) {
      url = url.substring(0, url.length() - 3);
    }
    url += "/autoupdate";
  }
%>

<module_updates timestamp="%UPDATES_TIMESTAMP%">

<module_group name="Funambol">

  <module codenamebase="com.funambol.admin"
          homepage="http://www.funambol.org"
          distribution="<%= url %>/funambol-admin.nbm"
          downloadsize="%LENGTH_ADMIN_NBM%"
          needsrestart="true"
          moduleauthor="Funambol"
          releasedate="%RELEASE_DATE%"
          >
    <manifest OpenIDE-Module="com.funambol.admin/1"
              OpenIDE-Module-Display-Category="Funambol"
              OpenIDE-Module-Implementation-Version="%IMPLEMENTATION_VERSION%"
              OpenIDE-Module-Module-Dependencies="com.funambol.admin.libs &gt; %ADMIN_VERSION%, org.netbeans.modules.javahelp/1 &gt; 2.6.1, org.openide.dialogs &gt; 6.2.1, org.openide.explorer &gt; 6.3.1, org.openide.io &gt; 1.7.1, org.openide.modules &gt; 6.3.1, org.openide.nodes &gt; 6.5.1, org.openide.util &gt; 6.4.1, org.openide.windows &gt; 6.2.1"
              OpenIDE-Module-Name="Funambol Administration Tool"
              OpenIDE-Module-Requires="org.openide.windows.IOProvider, org.openide.modules.ModuleFormat1"
              OpenIDE-Module-Short-Description="Funambol Administration Tool"
              OpenIDE-Module-Specification-Version="%ADMIN_VERSION%"
    />
  </module>

  <module codenamebase="com.funambol.admin.libs"
          homepage="http://www.funambol.org"
          distribution="<%= url %>/funambol-admin-libs.nbm"
          downloadsize="%LENGTH_ADMIN_LIBS_NBM%"
          needsrestart="true"
          moduleauthor="Funambol"
          releasedate="%RELEASE_DATE%"
          >
    <manifest OpenIDE-Module="com.funambol.admin.libs"
              OpenIDE-Module-Display-Category="Funambol"
              OpenIDE-Module-Implementation-Version="%IMPLEMENTATION_VERSION%"
              OpenIDE-Module-Name="Funambol Administration Libraries"
              OpenIDE-Module-Requires="org.openide.modules.ModuleFormat1"
              OpenIDE-Module-Short-Description="Funambol Administration Tool required libraries"
              OpenIDE-Module-Specification-Version="%ADMIN_VERSION%"
    />
  </module>
</module_group>


</module_updates>
