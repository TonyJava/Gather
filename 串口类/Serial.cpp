#include "stdafx.h"
#include "Serial.h"

CSerial::CSerial(void)
{
	bFlagOpen=FALSE;
	hSerialComm=NULL;
}

CSerial::~CSerial(void)
{
}
BOOL CSerial::OpenComm(int port,int nBaud)
{
	/*
	if(bFlagOpen)return TRUE;
	TCHAR szPort[15];
	memset(&szPort,0x00,15);
	DCB dcb;
	wsprintf(szPort,L"COM%d:",port);
	hSerialComm=::CreateFile(szPort,GENERIC_READ | GENERIC_WRITE,0,NULL,OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL | FILE_FLAG_OVERLAPPED, NULL );
	if(hSerialComm==(HANDLE)-1)
	{
		return FALSE;
	}
	memset(&m_OverlappedRead,0,sizeof(OVERLAPPED));
	memset(&m_OverlappedWrite,0,sizeof(OVERLAPPED));

	COMMTIMEOUTS CommtimeOuts;
	CommtimeOuts.ReadIntervalTimeout=MAXDWORD;
	CommtimeOuts.ReadTotalTimeoutMultiplier=0;
	CommtimeOuts.ReadTotalTimeoutConstant=0;
	CommtimeOuts.WriteTotalTimeoutConstant=0;
	CommtimeOuts.WriteTotalTimeoutMultiplier=5000;
	SetCommTimeouts(hSerialComm,&CommtimeOuts);

	m_OverlappedRead.hEvent=CreateEvent(NULL,TRUE,FALSE,NULL);
	m_OverlappedWrite.hEvent=CreateEvent(NULL,TRUE,FALSE,NULL);

	dcb.DCBlength = sizeof( DCB );
	GetCommState( hSerialComm, &dcb );
	dcb.BaudRate = nBaud;
	dcb.ByteSize = 8;
	if( !SetCommState( hSerialComm, &dcb ) ||
		!SetupComm( hSerialComm, 10000, 10000 ) ||
		m_OverlappedRead.hEvent == NULL ||
		m_OverlappedWrite.hEvent == NULL )
	{
			DWORD dwError = GetLastError();
			if( m_OverlappedRead.hEvent != NULL ) CloseHandle( m_OverlappedRead.hEvent );
			if( m_OverlappedWrite.hEvent != NULL ) CloseHandle( m_OverlappedWrite.hEvent );
			CloseHandle( hSerialComm);
			return FALSE;
	}
	bFlagOpen = TRUE;
	return bFlagOpen;
	*/
	if(bFlagOpen)return TRUE;
	TCHAR szPort[15];
	memset(&szPort,0x00,15);
	wsprintf(szPort,L"COM%d:",port);
	hSerialComm=::CreateFile(szPort,GENERIC_READ|GENERIC_WRITE,0,NULL,OPEN_EXISTING,0,NULL);
	if(hSerialComm==(HANDLE)-1)
	{
		AfxMessageBox(_T("打开失败"));
		return FALSE;
	}
	
	SetupComm(hSerialComm,256,256);//设置缓冲区；
	COMMTIMEOUTS ctoCommPort ;//设置COMMTIMEOUTS结构体
	//设定读超时
	ctoCommPort.ReadIntervalTimeout=MAXDWORD;
	ctoCommPort.ReadTotalTimeoutConstant=0;
	ctoCommPort.ReadTotalTimeoutMultiplier=0;
	SetCommTimeouts(hSerialComm,&ctoCommPort);
	//设置串口信息
	DCB dcbCommPort;//设置DCB结构体；
	GetCommState(hSerialComm,&dcbCommPort);
	dcbCommPort.BaudRate=nBaud;//波特率9600
	dcbCommPort.Parity=0;//无奇偶校验
	dcbCommPort.ByteSize=8;//每个字节8位
	dcbCommPort.StopBits=ONESTOPBIT;//无停止位
	if(SetCommState(hSerialComm,&dcbCommPort))
	{
		PurgeComm(hSerialComm,PURGE_TXCLEAR|PURGE_RXCLEAR);//清空输入输出缓存
		bFlagOpen=TRUE;
		return TRUE;
	}
	else
	{
		CloseHandle(hSerialComm);
		return FALSE;
	}
		
}
int CSerial::InBufferCount()
{
	if( !bFlagOpen || hSerialComm == NULL ) return 0;

	DWORD dwErrorFlags;
	COMSTAT ComStat;

	ClearCommError( hSerialComm, &dwErrorFlags, &ComStat );
	return (int)ComStat.cbInQue;
}
DWORD CSerial::ReadData(void *buffer, DWORD dwBytesRead)
{
	if( !bFlagOpen || hSerialComm == NULL ) return 0;
	/*
	BOOL bReadStatus;
	DWORD dwErrorFlags;
	COMSTAT ComStat;
	
	ClearCommError( hSerialComm, &dwErrorFlags, &ComStat );
	//if(!ComStat.cbInQue) return 0;

	dwBytesRead = min(dwBytesRead,(DWORD) ComStat.cbInQue);

	bReadStatus = ReadFile( hSerialComm, buffer, dwBytesRead, &dwBytesRead, &m_OverlappedRead );
	if( !bReadStatus ){
		if( GetLastError() == ERROR_IO_PENDING )
		{
			WaitForSingleObject( m_OverlappedRead.hEvent, 2000 );
			return dwBytesRead;
		}
		buffer="NULL";
		return 0;
	}
	PurgeComm(hSerialComm,PURGE_TXABORT|
		PURGE_RXABORT|PURGE_TXCLEAR|PURGE_RXCLEAR);//清空读写缓存，停止为解决的读写操作
	return dwBytesRead;
	*/
	
//	DWORD dwBytesRead =1024;//读取的字节数	
//	memset(&lpInBuffer,'\0',1024);	
				
	COMSTAT ComStat;
	DWORD dwErrorFlags; //错误标志位
	BOOL bReadStatus;//读取状态
	OVERLAPPED m_osRead;//串口设备的一些信息 
	memset(&m_osRead,0,sizeof(OVERLAPPED));//初始化内存
	m_osRead.hEvent=CreateEvent(NULL,TRUE,TRUE,NULL);
	
	ClearCommError(hSerialComm,&dwErrorFlags,&ComStat);//清除串口的错误标志以便继续输入,输出操作
	if(!ComStat.cbInQue) return 0;
	dwBytesRead = min(dwBytesRead,(DWORD)ComStat.cbInQue);
	
	bReadStatus=ReadFile(hSerialComm,buffer,dwBytesRead,&dwBytesRead,&m_osRead);
	if(!bReadStatus) //如果函数返回FALSE
	{
		if(ERROR_IO_PENDING==GetLastError())
		{
			WaitForSingleObject(m_osRead.hEvent,2000);
		}	
	}
	PurgeComm(hSerialComm,PURGE_TXABORT|
		PURGE_RXABORT|PURGE_TXCLEAR|PURGE_RXCLEAR);//清空读写缓存，停止为解决的读写操作
	
	return dwBytesRead;

}
DWORD CSerial::SendData(const char *buffer, DWORD dwBytesWritten)
{
	if( !bFlagOpen || hSerialComm == NULL ) return 0;
	BOOL bWriteStat;

	bWriteStat = WriteFile( hSerialComm, buffer, dwBytesWritten, &dwBytesWritten, &m_OverlappedWrite );
	if( !bWriteStat)
	{
		if ( GetLastError() == ERROR_IO_PENDING ) 
		{
			WaitForSingleObject( m_OverlappedWrite.hEvent, 1000 );
			return dwBytesWritten;
		}
		return 0;
	}
	return dwBytesWritten;

}
BOOL CSerial::CloseComm()
{
	return CloseHandle(hSerialComm);
}