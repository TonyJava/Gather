#pragma once
typedef void(CALLBACK * ONSERIESREAD)(void *pOwner  //父对象指针
									  ,BYTE *buf    //接收到的缓冲区
									  ,DWORD dwBufLen);//接收到的缓冲区长度
class CSerialComm
{
public:
	CSerialComm(void);
	~CSerialComm(void);
private:
	HANDLE hSerialComm; //串口句柄  hSerialComm
	HANDLE h_ReadThreadID;//读线程句柄
	HANDLE m_hReadCloseEvent;//读线程退出事件
	DWORD  m_dwReadThreadID;
	BOOL bFlagOpen;
	void *m_pOwner;//知道父对象指针
public:
	int InBufferCount( void );
	BOOL OpenPort(void *pOwner  //指向父指针
		           ,UINT PortNo //串口号
				   ,UINT baud   //波特率
				   ,UINT parity //奇偶校验
				   ,UINT databits //数据位
				   ,UINT stopbits);//停止位
	void ClosePort();
	BOOL WritePort(const BYTE *buffer, DWORD dwBytesWritten);
	BOOL SetSerialTimeOuts(int time);
	void CloseReadThread();
	static DWORD WINAPI ReadThreadFunc(LPVOID lparam);

	ONSERIESREAD m_OnSeriesRead;//串口读取回调函数
};
