/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY FUNAMBOL, FUNAMBOL DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact Funambol, Inc. headquarters at 643 Bair Island Road, Suite
 * 305, Redwood City, CA 94063, USA, or at email address info@funambol.com.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "Powered by Funambol" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by Funambol".
 */


#include <windows.h>
#include <shellapi.h>
#include <stdio.h>
#include "Tlhelp32.h"
#include "resource.h"
#include "wininet.h"

#define WM_ICONMESSAGE  WM_APP + 100
#define SWM_EXIT        WM_APP + 200
#define SWM_STARTUP     WM_APP + 300
#define SWM_SHUTDOWN    WM_APP + 400
#define SWM_ABOUT       WM_APP + 500

#define ABOUT_MSG                   TEXT("Funambol Data Synchronization Server")
#define ABOUT_TEXT                  TEXT("          Funambol Data Synchronization Server\n                               v.6.5.0")
#define ABOUT_COPYRIGHT             TEXT("           Copyright (c) 2003-2008 Funambol\n")
#define ABOUT_FILE_PROPERTIES       TEXT("bin\\about.properties")
#define ABOUT_MSG_CONST             "ABOUT_MSG:"
#define ABOUT_TEXT_CONST            "ABOUT_TEXT:"
#define ABOUT_COPYRIGHT_CONST       "ABOUT_COPYRIGHT:"
#define TOOL_TIP_CONST              "TOOL_TIP:"


#define BIN_STARTUP                 TEXT("bin\\restartall.cmd")
#define BIN_SHUTDOWN                TEXT("bin\\stopall.cmd")
#define SERVER_XML                  TEXT("tools\\tomcat\\conf\\server.xml")
#define SERVER_XML_TOKEN_START      TEXT("<!-- Funambol comment: don't modify or remove this Funambol comment! ##### -->")
#define SERVER_XML_TOKEN_END        TEXT("<!-- Funambol comment: don't modify or remove this Funambol comment! ###### -->")
#define SHUTDOWN_AND_CLOSE          "The server is still running.\nDo you want to shutdown the server before closing?"

#define USER_AGENT                  "Funambol Data Synchronization Server"
#define TIME_TIMER1                 10000
#define TIME_TIMER2                 5000
#define TOOL_TIP                    "Funambol Data Synchronization Server"
#define SHUTDOWN                    TEXT("Stop Server"  )
#define STARTUP                     TEXT("Start Server" )
#define EXIT                        TEXT("Exit"         )
#define ABOUT                       TEXT("About"        )


#define HOST_NAME                   TEXT("localhost")
#define HOST_PORT                   8080

#define TEST_REQUEST                TEXT("/funambol/ds/status")
#define CONFIG_FILE_PROPERTIES      TEXT("bin\\fnblstatus.properties")
#define TEST_REQUEST_CONST          "TEST_REQUEST:"

#define METHOD_GET                  TEXT("GET")
#define STATUS_OK                   200


HWND                hMainWindow;
NOTIFYICONDATA      Icondata;
STARTUPINFO         si;
PROCESS_INFORMATION pi;
WNDCLASS            wc;

long CALLBACK WndProc(HWND, UINT, WPARAM, LPARAM);
int  manageIcons(void);
BOOL initInstance(HINSTANCE hInstance);
void ShowContextMenu(HWND hWnd);
void about();
void startUp();
void shutDown();
BOOL changeIconRed(HINSTANCE hInstance);
BOOL changeIconGreen(HINSTANCE hInstance);
BOOL changeIconYellow(HINSTANCE hInstance);
int  checkStatus();
void prepareAbout();
void setPortNumber();
void setTestRequest();

BOOL isStartUpEnabled   = FALSE;
BOOL disableMenu        = FALSE;
char serverDir        [128];

char about_msg        [512];
char about_text       [512];
char about_copyright  [256];
char tool_tip         [256];
char test_request     [128];
int  hostPort;
int  status; // the status of the server. 0 if server is running, -1 if not

int closeFnblStatus();
int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE, LPSTR lpCmdLine, int)
{

    HWND hwin = FindWindow("TrayIconFunambolServer", NULL) ;

    if (strstr(lpCmdLine, "/stop") != NULL) {
        closeFnblStatus();
        if (hwin != NULL)
            DestroyWindow(hwin);
        exit(0);
    }

    if (hwin != NULL) {
        exit(1);
    }


    if (lpCmdLine != NULL) {
        int l = 0;
        l = strlen(lpCmdLine);
        if (lpCmdLine[0] == '\"') {

            char* p1 = lpCmdLine + 1;
            char* p2 = lpCmdLine + l - 1;

            char* tmp = new char[l - 1];
            strncpy(tmp, p1, p2 - p1);
            tmp[l - 2] = 0;
            sprintf(serverDir, "%s", tmp);
			delete[] tmp;

        }
        else
            sprintf(serverDir, "%s", lpCmdLine);
    }
    else {
        serverDir[0] = 0;
    }
    status = -1;
    prepareAbout();
    setPortNumber();
	setTestRequest();

    MSG msg;

    //register a window
    //will never be shown, but we need it for message processing

    LPSTR lpszClassName="TrayIconFunambolServer";

    wc.style=CS_HREDRAW | CS_VREDRAW ;
    wc.lpfnWndProc=WndProc;
    wc.cbClsExtra=0;
    wc.cbWndExtra=0;
    wc.hInstance=hInstance;
    wc.hIcon=0;
    wc.hCursor=LoadCursor(NULL,IDC_ARROW);
    wc.hbrBackground=(HBRUSH)GetStockObject(WHITE_BRUSH);
    wc.lpszMenuName=NULL;
    wc.lpszClassName=lpszClassName;

    if(!RegisterClass(&wc)) return false;

    hMainWindow=CreateWindow(
        lpszClassName,
        NULL,
        WS_OVERLAPPEDWINDOW,
        CW_USEDEFAULT,CW_USEDEFAULT,
        CW_USEDEFAULT,CW_USEDEFAULT,
        NULL,
        NULL,
        hInstance,
        NULL);

    if(!hMainWindow)
        return FALSE;

    //create Menu
    initInstance(hInstance);
    disableMenu = TRUE;

    SetTimer(hMainWindow,       // handle to main window
             1,                 // timer identifier
             TIME_TIMER1,       // 10-second interval
             (TIMERPROC) NULL); // no timer callback


   while(GetMessage(&msg,NULL,NULL,NULL))
   {
        TranslateMessage(&msg);
        DispatchMessage(&msg);
   }


   return 0;

}
void removeEndCarriage(char** ptr) {
    int length = 0;
    if (*ptr != NULL)
        length = strlen(*ptr);


    // if start with CRLF,
    if ( ptr != NULL &&
        *ptr != NULL &&
        (int)((*ptr)[length -1]) == 10)
        (*ptr)[length -1] = 0;


}
void prepareAbout() {

    char line [1024 + 1];
    char* ptr = NULL;
    int len = 0;
    FILE* f = NULL;

    memset(about_msg, 0, 512);
    memset(about_text, 0, 512);
    memset(about_copyright, 0, 256);
    memset(tool_tip, 0, 256);
    memset(line, 0, 1025);
    //
    // read about value from about.properties
    //
    if (strcmp(serverDir, "") == 0) {
        sprintf(about_msg, "%s",  ABOUT_MSG  );
        sprintf(about_text, "%s", ABOUT_TEXT );
        sprintf(about_copyright, "%s", ABOUT_COPYRIGHT );
        sprintf(tool_tip, "%s",   TOOL_TIP   );

        goto finally;
    }
    SetCurrentDirectory(serverDir);
    f = fopen(ABOUT_FILE_PROPERTIES, "r");

    if (f == NULL) {
        sprintf(about_msg, "%s",  ABOUT_MSG  );
        sprintf(about_text, "%s", ABOUT_TEXT );
        sprintf(about_copyright, "%s", ABOUT_COPYRIGHT );
        sprintf(tool_tip, "%s",   TOOL_TIP   );
        goto finally;
    }

    while(fgets(line, 1024, f) != NULL) {
        ptr = strstr(line, ABOUT_MSG_CONST);
        if (ptr != NULL) {
            len = strlen(ABOUT_MSG_CONST);
            removeEndCarriage(&ptr);
            sprintf(about_msg, "%s",  &line[len]  );
            break;
        }
    }
    if (ptr == NULL) {
        sprintf(about_msg, "%s",  ABOUT_MSG  );
    } else {
        ptr = NULL;
        len = 0;
    }

    memset(line, 0, 1025);
    f = freopen(ABOUT_FILE_PROPERTIES, "r", f);
    while(fgets(line, 1024, f) != NULL) {
        ptr = strstr(line, ABOUT_TEXT_CONST);
        if (ptr != NULL) {
            len = strlen(ABOUT_TEXT_CONST);
            removeEndCarriage(&ptr);
            sprintf(about_text, "%s",  &line[len]  );
            break;
        }
    }
    if (ptr == NULL) {
        sprintf(about_text, "%s",  ABOUT_TEXT );
    } else {
        ptr = NULL;
        len = 0;
    }

    memset(line, 0, 1025);
    f = freopen(ABOUT_FILE_PROPERTIES, "r", f);
    while(fgets(line, 1024, f) != NULL) {
        ptr = strstr(line, ABOUT_COPYRIGHT_CONST);
        if (ptr != NULL) {
            len = strlen(ABOUT_COPYRIGHT_CONST);
            removeEndCarriage(&ptr);
            sprintf(about_copyright, "%s",  &line[len]  );
            break;
        }
    }
    if (ptr == NULL) {
        sprintf(about_copyright, "%s",  ABOUT_COPYRIGHT );
    } else {
        ptr = NULL;
        len = 0;
    }

    memset(line, 0, 1025);
    f = freopen(ABOUT_FILE_PROPERTIES, "r", f);
    while(fgets(line, 1024, f) != NULL) {
        ptr = strstr(line, TOOL_TIP_CONST);
        if (ptr != NULL) {
            len = strlen(TOOL_TIP_CONST);
            removeEndCarriage(&ptr);
            sprintf(tool_tip, "%s", &line[len] );
            break;
        }
    }
    if (ptr == NULL) {
        sprintf(tool_tip, "%s",  TOOL_TIP );
    } else {
        ptr = NULL;
        len = 0;
    }


finally:
    if (f != NULL) {
        fflush(f);
        fclose(f);
    }

}

void setTestRequest() {

    char line [1024 + 1];
    char* ptr = NULL;
    int len = 0;
    FILE* f = NULL;

    memset(test_request, 0, 128);

    //
    // read test_request value from fnblstatus.properties
    //
    if (strcmp(serverDir, "") == 0) {
        sprintf(test_request, "%s",  TEST_REQUEST  );
        goto finally;
    }
    SetCurrentDirectory(serverDir);
    f = fopen(CONFIG_FILE_PROPERTIES, "r");

    if (f == NULL) {
        sprintf(test_request, "%s",   TEST_REQUEST   );
        goto finally;
    }

    while(fgets(line, 1024, f) != NULL) {
        ptr = strstr(line, TEST_REQUEST_CONST);
        if (ptr != NULL) {
            len = strlen(TEST_REQUEST_CONST);
            removeEndCarriage(&ptr);
            sprintf(test_request, "%s", &line[len] );
            break;
        }
    }
    if (ptr == NULL) {
        sprintf(test_request, "%s",  TEST_REQUEST );
    } else {
        ptr = NULL;
        len = 0;
    }


finally:
    if (f != NULL) {
        fflush(f);
        fclose(f);
    }

}

void setPortNumber() {

    char line [1024 + 1];
    char* ptr  = NULL;
    char* ptr2 = NULL;
    char  hostPortString [64];

    int len = 0, start = 0, end = 0;
    BOOL foundFunambolComment = FALSE, found = FALSE;
    FILE* f = NULL;

    memset(line, 0, 1024);
    memset(hostPortString, 0, 64);
    hostPort = HOST_PORT;

    //
    // read about value from s4jstatus.properties
    //
    if (strcmp(serverDir, "") == 0) {
        hostPort = HOST_PORT;
        goto finally;
    }
    SetCurrentDirectory(serverDir);
    f = fopen(SERVER_XML, "r");

    while(fgets(line, 1024, f) != NULL) {

        ptr = strstr(line, SERVER_XML_TOKEN_START);
        if (ptr != NULL) {
            foundFunambolComment = TRUE;
            continue;
        }
        if (foundFunambolComment) {
            ptr2 = strstr(line, "port");
            if (ptr2 != NULL) {
                for (int i = 0; i < 512; i ++) {
                    if (ptr2[i] == '"' && found == FALSE) {
                        start = i;
                        found = TRUE;

                    } else if (ptr2[i] == '"') {
                        end = i;
                        break;
                    }

                }
            strncpy(hostPortString, &ptr2[start + 1], end - start - 1);
            break;
            }

        }

        ptr = strstr(line, SERVER_XML_TOKEN_END);
        if (ptr != NULL) {
            // end
            break;
        }
    }
    if (ptr != NULL) {
        delete ptr;
    }

    hostPort = strtol(hostPortString, &ptr, 0);


finally:
    if (f != NULL) {
        fflush(f);
        fclose(f);
    }

}



/*
* It puts the initial yellow icon on the systray.
*/
BOOL initInstance(HINSTANCE hInstance) {

    Icondata.cbSize=sizeof (Icondata);
    Icondata.hWnd=hMainWindow;

    //since I don't have resources here, I take a standard Icon

    Icondata.hIcon=(HICON)LoadImage(hInstance, MAKEINTRESOURCE(IDI_MYICON_YELLOW), IMAGE_ICON, 32, 32, 0);
    Icondata.uFlags= NIF_ICON | NIF_TIP | NIF_MESSAGE;
    Icondata.uID=NULL;
    Icondata.uCallbackMessage=WM_ICONMESSAGE;
    strcpy(Icondata.szTip, tool_tip);

    Shell_NotifyIcon(NIM_ADD,&Icondata);
    ShowWindow(hMainWindow, SW_HIDE);
    UpdateWindow(hMainWindow);

    return TRUE;

}

/*
* It manages the icon on the systray cheking the status of the server.
* if server is alive it put green icon, red otherwise.
*/
int manageIcons(void) {

	int previousStatus = status;
    status = checkStatus();

	if (previousStatus != status) {

		if (status == 0) {
			changeIconGreen(wc.hInstance);
			isStartUpEnabled = FALSE;
		} else {
			changeIconRed(wc.hInstance);
			isStartUpEnabled = TRUE;
		}
	}
    return 0;

}

/*
* It creates the timer number 2. It is used after a shudtown or startup call
* that disable the startup and shutdown menu item.
* After 5 seconds the menu items are restored and the timer is killed *
*/
void timeWait() {
    SetTimer(hMainWindow,       // handle to main window
             2,                 // timer identifier
             TIME_TIMER2,              // 5-second interval
             (TIMERPROC) NULL); // no timer callback

}

/*
* It kills the timer number 2.
*/
void timeWaitKill() {
    KillTimer(hMainWindow, 2);

}

/*
* It listens to the message event and processes the action to do
*/
long CALLBACK WndProc(HWND hWnd,UINT msg, WPARAM wParam,LPARAM lParam)
{
    int ret = 0;
    switch(msg) {

        case WM_DESTROY:
        case WM_CLOSE:
        case WM_QUIT:

            PostQuitMessage(0);
            return 0;

        case WM_COMMAND:
            switch(LOWORD(wParam)) {
                case SWM_EXIT:
                    if (status == 0) {
                        ret = MessageBox (NULL, SHUTDOWN_AND_CLOSE, about_msg, MB_SETFOREGROUND | MB_YESNO);
                        if (ret == IDYES) {
                            changeIconYellow(wc.hInstance);
                            shutDown();
                            disableMenu = TRUE;
                            isStartUpEnabled = TRUE;
                            timeWait();
                        }
                    }
                    Shell_NotifyIcon(NIM_DELETE,&Icondata);
                    PostQuitMessage(0);
                    break;
                case SWM_ABOUT:
                    about();
                    break;
                 case SWM_STARTUP:
                    changeIconYellow(wc.hInstance);
                    setPortNumber();
                    startUp();
                    disableMenu = TRUE;
                    isStartUpEnabled = FALSE;
                    timeWait();
                    break;
                case SWM_SHUTDOWN:
                    changeIconYellow(wc.hInstance);
                    shutDown();
                    disableMenu = TRUE;
                    isStartUpEnabled = TRUE;
                    timeWait();
                    break;

            break;
            }

        case WM_ICONMESSAGE:
            switch (lParam){
                /*
                case WM_LBUTTONDOWN:
                    break;
                case WM_LBUTTONDBLCLK:
                    break;
                */

                case WM_RBUTTONDOWN:
                    ShowContextMenu(hWnd);
                    break;

                default:
                return 0;
            }
        case WM_TIMER:
            if (wParam == 1) {
                manageIcons();
            }
            if (wParam == 2) {
                disableMenu = FALSE;
                timeWaitKill();
            }
            break;
    }
    return DefWindowProc(hWnd,msg,wParam,lParam);
}

void about() {

    char message[1024];
    sprintf(message, "%s\n\n%s\n", about_text, about_copyright);

    MessageBox (NULL, message, about_msg, MB_SETFOREGROUND |MB_OK);
}

/*
* It calls the script to start up the server.
*/
void startUp() {

    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);
    ZeroMemory( &pi, sizeof(pi) );
    BOOL res = FALSE;
    // Start the child process.
    SetCurrentDirectory(serverDir);
    res = CreateProcess( NULL,   // No module name (use command line).
        BIN_STARTUP,
        NULL,             // Process handle not inheritable.
        NULL,             // Thread handle not inheritable.
        FALSE,            // Set handle inheritance to FALSE.
        0,                // No creation flags.
        NULL,             // Use parent's environment block.
        NULL,             // Use parent's starting directory.
        &si,              // Pointer to STARTUPINFO structure.
        &pi );            // Pointer to PROCESS_INFORMATION structure.


    // Wait until child process exits.
    WaitForSingleObject( pi.hProcess, INFINITE );

    // Close process and thread handles.
    CloseHandle( pi.hProcess );
    CloseHandle( pi.hThread );
}

/*
* It calls the script to shutdown the server.
*/
void shutDown() {

    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);
    ZeroMemory( &pi, sizeof(pi) );
    BOOL res = FALSE;
    // Start the child process.
    SetCurrentDirectory(serverDir);
    res = CreateProcess( NULL,   // No module name (use command line).
        BIN_SHUTDOWN,
        NULL,             // Process handle not inheritable.
        NULL,             // Thread handle not inheritable.
        FALSE,            // Set handle inheritance to FALSE.
        0,                // No creation flags.
        NULL,             // Use parent's environment block.
        NULL,             // Use parent's starting directory.
        &si,              // Pointer to STARTUPINFO structure.
        &pi );            // Pointer to PROCESS_INFORMATION structure.



    // Wait until child process exits.
    WaitForSingleObject( pi.hProcess, INFINITE );

    // Close process and thread handles.
    CloseHandle( pi.hProcess );
    CloseHandle( pi.hThread );
}

/*
* It changes the icon and put the green one
*/
BOOL changeIconGreen(HINSTANCE hInstance)
{

    Icondata.hIcon=(HICON)LoadImage(hInstance, MAKEINTRESOURCE(IDI_MYICON_GREEN), IMAGE_ICON, 16, 16, 0);
    Icondata.uFlags= NIF_ICON | NIF_TIP | NIF_MESSAGE;
    Icondata.uID=NULL;
    Icondata.uCallbackMessage=WM_ICONMESSAGE;
    strcpy(Icondata.szTip, tool_tip);

    Shell_NotifyIcon(NIM_MODIFY,&Icondata);
    ShowWindow(hMainWindow, SW_HIDE);
    UpdateWindow(hMainWindow);
    disableMenu = FALSE;
    return TRUE;
}

/*
* It changes the icon and put the red one
*/
BOOL changeIconRed(HINSTANCE hInstance)
{

    Icondata.hIcon=(HICON)LoadImage(hInstance, MAKEINTRESOURCE(IDI_MYICON_RED), IMAGE_ICON, 16, 16, 0);
    Icondata.uFlags= NIF_ICON | NIF_TIP | NIF_MESSAGE;
    Icondata.uID=NULL;
    Icondata.uCallbackMessage=WM_ICONMESSAGE;
    strcpy(Icondata.szTip, tool_tip);

    Shell_NotifyIcon(NIM_MODIFY,&Icondata);
    ShowWindow(hMainWindow, SW_HIDE);
    UpdateWindow(hMainWindow);
    disableMenu = FALSE;
    return TRUE;
}

/*
* It changes the icon and put the yellow one
*/
BOOL changeIconYellow(HINSTANCE hInstance)
{

    Icondata.hIcon=(HICON)LoadImage(hInstance, MAKEINTRESOURCE(IDI_MYICON_YELLOW), IMAGE_ICON, 16, 16, 0);
    Icondata.uFlags= NIF_ICON | NIF_TIP | NIF_MESSAGE;
    Icondata.uID=NULL;
    Icondata.uCallbackMessage=WM_ICONMESSAGE;
    strcpy(Icondata.szTip, tool_tip);

    Shell_NotifyIcon(NIM_MODIFY,&Icondata);
    ShowWindow(hMainWindow, SW_HIDE);
    UpdateWindow(hMainWindow);
    return TRUE;
}

/*
* It shows the context menu
*/
void ShowContextMenu(HWND hWnd)
{
    POINT pt;
    GetCursorPos(&pt);
    HMENU hMenu = CreatePopupMenu();
    if(hMenu)
    {
        if (isStartUpEnabled == FALSE) {
            InsertMenu(hMenu, -1, MF_GRAYED, SWM_STARTUP,  STARTUP);
        }
        else {
            if (disableMenu == FALSE)
                InsertMenu(hMenu, -1, MF_BYPOSITION, SWM_STARTUP,  STARTUP);
            else
                InsertMenu(hMenu, -1, MF_GRAYED, SWM_STARTUP,  STARTUP);
        }

        if (isStartUpEnabled == FALSE) {
            if (disableMenu == FALSE)
                InsertMenu(hMenu, -1, MF_BYPOSITION, SWM_SHUTDOWN, SHUTDOWN);
            else
                InsertMenu(hMenu, -1, MF_GRAYED, SWM_SHUTDOWN, SHUTDOWN);
        }
        else {
            InsertMenu(hMenu, -1, MF_GRAYED, SWM_SHUTDOWN, SHUTDOWN);
        }


        InsertMenu(hMenu, -1, MF_BYPOSITION, SWM_ABOUT,    ABOUT);
        InsertMenu(hMenu, -1, MF_BYPOSITION, SWM_EXIT,     EXIT);

        // note:    must set window to the foreground or the
        //          menu won't disappear when it should
        SetForegroundWindow(hWnd);

        TrackPopupMenu(hMenu, TPM_BOTTOMALIGN, pt.x, pt.y, 0, hWnd, NULL );
        DestroyMenu(hMenu);
    }
}

/*
* It checks the server status. It calls a url and if it doesn't response, server is down.
* return 0 if server is alive, -1 otherwise
*/
int checkStatus() {

    int ret = -1;
    HINTERNET inet       = NULL;
    HINTERNET connection = NULL;
    HINTERNET request    = NULL;
    DWORD flags = INTERNET_FLAG_RELOAD | INTERNET_FLAG_NO_CACHE_WRITE;
    LPCSTR acceptTypes[2] = {TEXT("*/*"), NULL};
    int serverStatus;
    DWORD size;
    BOOL queryInfo;


    inet = InternetOpen (TEXT(USER_AGENT), INTERNET_OPEN_TYPE_PRECONFIG, NULL, 0, 0);

    if (!inet) {
        // MessageBox (NULL, TEXT("Not InternetOpen"), ABOUT, MB_SETFOREGROUND |MB_OK);
        goto finally;
    }
    if (!(connection = InternetConnect (inet,
                                        HOST_NAME,
                                        hostPort,
                                        NULL,
                                        NULL,
                                        INTERNET_SERVICE_HTTP,
                                        0,
                                        0))) {

    }
    if (!connection) {
        // MessageBox (NULL, TEXT("Not InternetConnect"), ABOUT, MB_SETFOREGROUND |MB_OK);
        goto finally;
    }

    //request = InternetOpenUrl(inet, test_url, NULL, 0, flags, 0 );

    // Open an HTTP request handle.
    if (!(request = HttpOpenRequest (connection,
                                     METHOD_GET,
                                     test_request,
                                     HTTP_VERSION,
                                     NULL,
                                     acceptTypes,
                                     flags, 0))) {
        goto finally;
    }

    // Sends the request to the HTTP server.
    if (!HttpSendRequest (request, NULL, 0, NULL, 0) ) {
        goto finally;
    }


    // Verify if status code is OK (200)
    size = sizeof(serverStatus);
    if (!(queryInfo = HttpQueryInfo (request,
                                     HTTP_QUERY_STATUS_CODE | HTTP_QUERY_FLAG_NUMBER,
                                     (LPDWORD)&serverStatus,
                                     (LPDWORD)&size,
                                     NULL))) {
        goto finally;
    }

    if (serverStatus == STATUS_OK)
        ret = 0;



finally:

    if (inet != NULL && inet) {
        InternetCloseHandle (inet);
    }
    if (connection != NULL && connection) {
        InternetCloseHandle (connection);
    }
    if (request != NULL && request) {
        InternetCloseHandle (request);
    }
    return ret;

}

int closeFnblStatus() {
    DWORD out = 0;
    PROCESSENTRY32 lppe;
    BOOL next = TRUE;
    HANDLE hProcess = 0;

    HANDLE hSnapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0); // 0 is however ignored

    if (hSnapshot == INVALID_HANDLE_VALUE)
        return 0;

    lppe.dwSize = sizeof( PROCESSENTRY32 );

    if (!Process32First(hSnapshot, &lppe))
        return 1;

    if (strstr(lppe.szExeFile, "fnblstatus.exe") != NULL) {
        hProcess = OpenProcess(0, FALSE, lppe.th32ProcessID);
        TerminateProcess(hProcess, -1);
        goto exit;
    }

    do {
        next = Process32Next(hSnapshot, &lppe);

        if (!next)
            break;

        if (strstr(lppe.szExeFile, "fnblstatus.exe") != NULL ) {
            DWORD d = 0;
            hProcess = OpenProcess(PROCESS_TERMINATE, FALSE, lppe.th32ProcessID);
            d = GetLastError();
            TerminateProcess(hProcess, -1);
            goto exit;
        }

    } while(next);

exit:
    CloseHandle(hSnapshot);
    CloseHandle(hProcess);
    return 0;

}



/*
int checkHead() {

    int ret = -1;
    HINTERNET inet       = NULL;
    HINTERNET connection = NULL;
    HINTERNET request    = NULL;
    DWORD flags = INTERNET_FLAG_RELOAD | INTERNET_FLAG_NO_CACHE_WRITE;

    inet = InternetOpen (TEXT(USER_AGENT), INTERNET_OPEN_TYPE_PRECONFIG, NULL, 0, 0);

    if (!inet) {
        // MessageBox (NULL, TEXT("Not InternetOpen"), ABOUT, MB_SETFOREGROUND |MB_OK);
        goto finally;
    }
    if (!(connection = InternetConnect (inet,
                                        HOST_NAME,
                                        HOST_PORT,
                                        NULL,
                                        NULL,
                                        INTERNET_SERVICE_HTTP,
                                        0,
                                        0))) {

    }
    if (!connection) {
        // MessageBox (NULL, TEXT("Not InternetConnect"), ABOUT, MB_SETFOREGROUND |MB_OK);
        goto finally;
    }

    // Open an HTTP request handle.
    if (!(request = HttpOpenRequest (connection,
                                     METHOD_POST,
                                     url.resource,
                                     HTTP_VERSION,
                                     NULL,
                                     (LPCTSTR*)acceptTypes,
                                     flags, 0))) {
      lastErrorCode = ERR_CONNECT;
      wsprintf (lastErrorMsg, TEXT("%s: %d"), TEXT("HttpOpenRequest Error"), GetLastError());
      goto exit;
    }


    HttpQueryInfo (request,
                   HTTP_QUERY_STATUS_CODE | HTTP_QUERY_FLAG_NUMBER,
                   (LPDWORD)&status,
                   (LPDWORD)&size,
                   NULL);

finally:

    if (inet != NULL && inet) {
        InternetCloseHandle (inet);
    }
    if (connection != NULL && connection) {
        InternetCloseHandle (connection);
    }
    if (request != NULL && request) {
        InternetCloseHandle (request);
    }
    return ret;

}

  */
