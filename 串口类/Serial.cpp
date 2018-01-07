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
		AfxMessageBox(_T("��ʧ��"));
		return FALSE;
	}
	
	SetupComm(hSerialComm,256,256);//���û�������
	COMMTIMEOUTS ctoCommPort ;//����COMMTIMEOUTS�ṹ��
	//�趨����ʱ
	ctoCommPort.ReadIntervalTimeout=MAXDWORD;
	ctoCommPort.ReadTotalTimeoutConstant=0;
	ctoCommPort.ReadTotalTimeoutMultiplier=0;
	SetCommTimeouts(hSerialComm,&ctoCommPort);
	//���ô�����Ϣ
	DCB dcbCommPort;//����DCB�ṹ�壻
	GetCommState(hSerialComm,&dcbCommPort);
	dcbCommPort.BaudRate=nBaud;//������9600
	dcbCommPort.Parity=0;//����żУ��
	dcbCommPort.ByteSize=8;//ÿ���ֽ�8λ
	dcbCommPort.StopBits=ONESTOPBIT;//��ֹͣλ
	if(SetCommState(hSerialComm,&dcbCommPort))
	{
		PurgeComm(hSerialComm,PURGE_TXCLEAR|PURGE_RXCLEAR);//��������������
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
		PURGE_RXABORT|PURGE_TXCLEAR|PURGE_RXCLEAR);//��ն�д���棬ֹͣΪ����Ķ�д����
	return dwBytesRead;
	*/
	
//	DWORD dwBytesRead =1024;//��ȡ���ֽ���	
//	memset(&lpInBuffer,'\0',1024);	
				
	COMSTAT ComStat;
	DWORD dwErrorFlags; //�����־λ
	BOOL bReadStatus;//��ȡ״̬
	OVERLAPPED m_osRead;//�����豸��һЩ��Ϣ 
	memset(&m_osRead,0,sizeof(OVERLAPPED));//��ʼ���ڴ�
	m_osRead.hEvent=CreateEvent(NULL,TRUE,TRUE,NULL);
	
	ClearCommError(hSerialComm,&dwErrorFlags,&ComStat);//������ڵĴ����־�Ա��������,�������
	if(!ComStat.cbInQue) return 0;
	dwBytesRead = min(dwBytesRead,(DWORD)ComStat.cbInQue);
	
	bReadStatus=ReadFile(hSerialComm,buffer,dwBytesRead,&dwBytesRead,&m_osRead);
	if(!bReadStatus) //�����������FALSE
	{
		if(ERROR_IO_PENDING==GetLastError())
		{
			WaitForSingleObject(m_osRead.hEvent,2000);
		}	
	}
	PurgeComm(hSerialComm,PURGE_TXABORT|
		PURGE_RXABORT|PURGE_TXCLEAR|PURGE_RXCLEAR);//��ն�д���棬ֹͣΪ����Ķ�д����
	
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