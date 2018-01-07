#include "stdafx.h"
#include "SerialComm.h"

CSerialComm::CSerialComm(void)
{
	hSerialComm=INVALID_HANDLE_VALUE;
	bFlagOpen=FALSE;
	m_OnSeriesRead=NULL;
}

CSerialComm::~CSerialComm(void)
{
	if(bFlagOpen)
	{
		CloseHandle(hSerialComm);
	}
}
/*
 * �򿪴���
 */
BOOL CSerialComm::OpenPort(void *pOwner  //ָ��ָ��
						   ,UINT PortNo //���ں�
						   ,UINT baud   //������
						   ,UINT parity //��żУ��
						   ,UINT databits //����λ
						   ,UINT stopbits)//ֹͣλ
{
	if(bFlagOpen)return TRUE;
	m_pOwner=pOwner;
	TCHAR szPort[30];
	memset(&szPort,0x00,30);
	wsprintf(szPort,L"COM%d:",PortNo);
	hSerialComm=::CreateFile(szPort,GENERIC_READ|GENERIC_WRITE,0,NULL,OPEN_EXISTING,0,NULL);
	if(hSerialComm==(HANDLE)-1)
	{
		TRACE(_T("��ʧ��"));
		return FALSE;
	}
	COMMTIMEOUTS ctoCommPort ;//����COMMTIMEOUTS�ṹ��
	//�趨����ʱ
	ctoCommPort.ReadIntervalTimeout=MAXDWORD;
	ctoCommPort.ReadTotalTimeoutConstant=0;
	ctoCommPort.ReadTotalTimeoutMultiplier=0;
	ctoCommPort.WriteTotalTimeoutMultiplier=10;
	ctoCommPort.WriteTotalTimeoutConstant=1000;
	if(!SetCommTimeouts(hSerialComm,&ctoCommPort))
	{
		TRACE(L"��ʱ����ʧ��");
		CloseHandle(hSerialComm);
		return FALSE;
	}
	//���ô�����Ϣ
	DCB dcbCommPort;//����DCB�ṹ�壻
	GetCommState(hSerialComm,&dcbCommPort);
	dcbCommPort.BaudRate=baud;//������9600
	dcbCommPort.Parity=parity;//����żУ��
	dcbCommPort.ByteSize=databits;//ÿ���ֽ�8λ
	dcbCommPort.StopBits=stopbits;//��ֹͣλ
	//DTR flow control type;
	/*
	*/
	if(SetCommState(hSerialComm,&dcbCommPort))
	{
		PurgeComm(hSerialComm,PURGE_TXCLEAR|PURGE_RXCLEAR);//��������������
		bFlagOpen=TRUE;
	}
	else
	{
		CloseHandle(hSerialComm);
		return FALSE;
	}
//ָ�����ڼ���¼���
	SetCommMask(hSerialComm,EV_RXCHAR);
	//���䴮�ڻ�����
	SetupComm(hSerialComm,512,512);//���û�������
	PurgeComm(hSerialComm,PURGE_TXCLEAR|PURGE_RXCLEAR);//��������������
	memset(&szPort,0x00,30);
	wsprintf(szPort,L"COM_ReadCloseEvent%d",PortNo);
	m_hReadCloseEvent=CreateEvent(NULL,TRUE,FALSE,szPort);
	
	//�������ڶ��߳�
	h_ReadThreadID=CreateThread(NULL,0,ReadThreadFunc,this,0,&m_dwReadThreadID);
	return TRUE;
}
DWORD CSerialComm::ReadThreadFunc(LPVOID lparam)
{
	CSerialComm *SerialComm =(CSerialComm *)lparam;
	DWORD evtMask;
	BYTE *readBuf =NULL;//��ȡ���ֽ���
	DWORD actualReadlen =0;//ʵ�ʶ�ȡ���ֽ���
	DWORD willReadlen=0; 
	DWORD dwReadErrors;
	COMSTAT cmState;
	//��ջ��壬��⴮���Ƿ��
	ASSERT(SerialComm->hSerialComm!=INVALID_HANDLE_VALUE);
	PurgeComm(SerialComm->hSerialComm,PURGE_RXCLEAR|PURGE_TXCLEAR);
	SetCommMask(SerialComm->hSerialComm,EV_RXCHAR|EV_CTS|EV_DSR);
	while(TRUE)
	{
		if(WaitCommEvent(SerialComm->hSerialComm,&evtMask,0))
		{
			SetCommMask(SerialComm->hSerialComm,EV_RXCHAR|EV_CTS|EV_DSR);
			//��ʾ�����յ�����
			if(evtMask&EV_RXCHAR)
			{
				ClearCommError(SerialComm->hSerialComm,&dwReadErrors,&cmState);
				willReadlen=cmState.cbInQue;
				if(willReadlen<=0)
				{
					continue;
				}
				readBuf =new BYTE[willReadlen];
				ZeroMemory(readBuf,willReadlen);
				//��ȡ��������
				ReadFile(SerialComm->hSerialComm,readBuf,willReadlen,&actualReadlen,0);
				//�����ȡ�ĳ��ȴ���0
				if(actualReadlen>0)
				{
					//�����ص�����
					if(SerialComm->m_OnSeriesRead)
					{
						SerialComm->m_OnSeriesRead(SerialComm->m_pOwner,readBuf,actualReadlen);
					}
				}
				delete[] readBuf;
				readBuf =NULL;
			}
		}
		Sleep(10);
		//����յ����߳��˳��źţ��˳��߳�
		if(WaitForSingleObject(SerialComm->hSerialComm,500)==WAIT_OBJECT_0)
		{
			break;
		}
	}
	return 0;
}
//�ر��߳�
void CSerialComm::CloseReadThread()
{
	SetEvent(m_hReadCloseEvent);
	//���������¼���Ч
	SetCommMask(hSerialComm,0);
	//��ն�������
	PurgeComm(hSerialComm,PURGE_RXCLEAR);
	//�ȴ�4�룬û���˳���ǿ���˳�
	if(WaitForSingleObject(h_ReadThreadID,4000)==WAIT_TIMEOUT)
	{
		TerminateThread(h_ReadThreadID,0);
	}
	h_ReadThreadID=NULL;
}
//�رն˿�
void CSerialComm::ClosePort()
{
	//����û�д�
	if(hSerialComm==INVALID_HANDLE_VALUE)
	{
		return;
	}
	//�رն��ֳ�
	CloseReadThread();
	//�رմ���
	CloseHandle(hSerialComm);
	//�ر��¼�
	CloseHandle(m_hReadCloseEvent);
	hSerialComm=INVALID_HANDLE_VALUE;
	bFlagOpen=FALSE;
}
BOOL CSerialComm::WritePort(const BYTE *buffer, DWORD dwBytesWritten)
{
	DWORD dwNumBytesWritten;
	DWORD dwHaveNumWritten=0;
	int iINC =0;//д�����β��ɹ�����FALSE��
	ASSERT(hSerialComm!=INVALID_HANDLE_VALUE);
	do 
	{
		if(WriteFile(hSerialComm,buffer+dwHaveNumWritten,dwHaveNumWritten-dwHaveNumWritten,&dwNumBytesWritten,NULL))
		{
			dwHaveNumWritten=dwHaveNumWritten+dwNumBytesWritten;
			//д�����
			if(dwHaveNumWritten==dwBytesWritten)
			{
				break;
			}
			iINC++;
			if(iINC>=3)
			{
				return FALSE;
			}
			Sleep(10);
		}
		else
		{
			return FALSE;
		}
	} while (TRUE);
	return TRUE;
}