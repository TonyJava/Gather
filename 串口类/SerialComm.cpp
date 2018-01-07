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
 * 打开串口
 */
BOOL CSerialComm::OpenPort(void *pOwner  //指向父指针
						   ,UINT PortNo //串口号
						   ,UINT baud   //波特率
						   ,UINT parity //奇偶校验
						   ,UINT databits //数据位
						   ,UINT stopbits)//停止位
{
	if(bFlagOpen)return TRUE;
	m_pOwner=pOwner;
	TCHAR szPort[30];
	memset(&szPort,0x00,30);
	wsprintf(szPort,L"COM%d:",PortNo);
	hSerialComm=::CreateFile(szPort,GENERIC_READ|GENERIC_WRITE,0,NULL,OPEN_EXISTING,0,NULL);
	if(hSerialComm==(HANDLE)-1)
	{
		TRACE(_T("打开失败"));
		return FALSE;
	}
	COMMTIMEOUTS ctoCommPort ;//设置COMMTIMEOUTS结构体
	//设定读超时
	ctoCommPort.ReadIntervalTimeout=MAXDWORD;
	ctoCommPort.ReadTotalTimeoutConstant=0;
	ctoCommPort.ReadTotalTimeoutMultiplier=0;
	ctoCommPort.WriteTotalTimeoutMultiplier=10;
	ctoCommPort.WriteTotalTimeoutConstant=1000;
	if(!SetCommTimeouts(hSerialComm,&ctoCommPort))
	{
		TRACE(L"超时设置失败");
		CloseHandle(hSerialComm);
		return FALSE;
	}
	//设置串口信息
	DCB dcbCommPort;//设置DCB结构体；
	GetCommState(hSerialComm,&dcbCommPort);
	dcbCommPort.BaudRate=baud;//波特率9600
	dcbCommPort.Parity=parity;//无奇偶校验
	dcbCommPort.ByteSize=databits;//每个字节8位
	dcbCommPort.StopBits=stopbits;//无停止位
	//DTR flow control type;
	/*
	*/
	if(SetCommState(hSerialComm,&dcbCommPort))
	{
		PurgeComm(hSerialComm,PURGE_TXCLEAR|PURGE_RXCLEAR);//清空输入输出缓存
		bFlagOpen=TRUE;
	}
	else
	{
		CloseHandle(hSerialComm);
		return FALSE;
	}
//指定串口检测事件集
	SetCommMask(hSerialComm,EV_RXCHAR);
	//分配串口缓冲区
	SetupComm(hSerialComm,512,512);//设置缓冲区；
	PurgeComm(hSerialComm,PURGE_TXCLEAR|PURGE_RXCLEAR);//清空输入输出缓存
	memset(&szPort,0x00,30);
	wsprintf(szPort,L"COM_ReadCloseEvent%d",PortNo);
	m_hReadCloseEvent=CreateEvent(NULL,TRUE,FALSE,szPort);
	
	//创建串口读线程
	h_ReadThreadID=CreateThread(NULL,0,ReadThreadFunc,this,0,&m_dwReadThreadID);
	return TRUE;
}
DWORD CSerialComm::ReadThreadFunc(LPVOID lparam)
{
	CSerialComm *SerialComm =(CSerialComm *)lparam;
	DWORD evtMask;
	BYTE *readBuf =NULL;//读取的字节数
	DWORD actualReadlen =0;//实际读取的字节数
	DWORD willReadlen=0; 
	DWORD dwReadErrors;
	COMSTAT cmState;
	//清空缓冲，检测串口是否打开
	ASSERT(SerialComm->hSerialComm!=INVALID_HANDLE_VALUE);
	PurgeComm(SerialComm->hSerialComm,PURGE_RXCLEAR|PURGE_TXCLEAR);
	SetCommMask(SerialComm->hSerialComm,EV_RXCHAR|EV_CTS|EV_DSR);
	while(TRUE)
	{
		if(WaitCommEvent(SerialComm->hSerialComm,&evtMask,0))
		{
			SetCommMask(SerialComm->hSerialComm,EV_RXCHAR|EV_CTS|EV_DSR);
			//表示串口收到数据
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
				//读取串口数据
				ReadFile(SerialComm->hSerialComm,readBuf,willReadlen,&actualReadlen,0);
				//如果读取的长度大于0
				if(actualReadlen>0)
				{
					//触发回调函数
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
		//如果收到读线程退出信号，退出线程
		if(WaitForSingleObject(SerialComm->hSerialComm,500)==WAIT_OBJECT_0)
		{
			break;
		}
	}
	return 0;
}
//关闭线程
void CSerialComm::CloseReadThread()
{
	SetEvent(m_hReadCloseEvent);
	//设置所有事件无效
	SetCommMask(hSerialComm,0);
	//清空读的数据
	PurgeComm(hSerialComm,PURGE_RXCLEAR);
	//等待4秒，没有退出则强制退出
	if(WaitForSingleObject(h_ReadThreadID,4000)==WAIT_TIMEOUT)
	{
		TerminateThread(h_ReadThreadID,0);
	}
	h_ReadThreadID=NULL;
}
//关闭端口
void CSerialComm::ClosePort()
{
	//串口没有打开
	if(hSerialComm==INVALID_HANDLE_VALUE)
	{
		return;
	}
	//关闭读现场
	CloseReadThread();
	//关闭串口
	CloseHandle(hSerialComm);
	//关闭事件
	CloseHandle(m_hReadCloseEvent);
	hSerialComm=INVALID_HANDLE_VALUE;
	bFlagOpen=FALSE;
}
BOOL CSerialComm::WritePort(const BYTE *buffer, DWORD dwBytesWritten)
{
	DWORD dwNumBytesWritten;
	DWORD dwHaveNumWritten=0;
	int iINC =0;//写入三次不成功返回FALSE；
	ASSERT(hSerialComm!=INVALID_HANDLE_VALUE);
	do 
	{
		if(WriteFile(hSerialComm,buffer+dwHaveNumWritten,dwHaveNumWritten-dwHaveNumWritten,&dwNumBytesWritten,NULL))
		{
			dwHaveNumWritten=dwHaveNumWritten+dwNumBytesWritten;
			//写入完成
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