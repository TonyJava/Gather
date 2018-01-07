#pragma once

class CSerial
{
public:
	CSerial(void);
	~CSerial(void);
	BOOL OpenComm(int port,int nBaud);
	int InBufferCount( void );
	DWORD ReadData( void *buffer, DWORD dwBytesRead);
	DWORD SendData(const char *buffer, DWORD dwBytesWritten);
	HANDLE hSerialComm;
	BOOL bFlagOpen;
	OVERLAPPED m_OverlappedRead,m_OverlappedWrite;
	BOOL CloseComm();
};
