#pragma once
typedef void(CALLBACK * ONSERIESREAD)(void *pOwner  //������ָ��
									  ,BYTE *buf    //���յ��Ļ�����
									  ,DWORD dwBufLen);//���յ��Ļ���������
class CSerialComm
{
public:
	CSerialComm(void);
	~CSerialComm(void);
private:
	HANDLE hSerialComm; //���ھ��  hSerialComm
	HANDLE h_ReadThreadID;//���߳̾��
	HANDLE m_hReadCloseEvent;//���߳��˳��¼�
	DWORD  m_dwReadThreadID;
	BOOL bFlagOpen;
	void *m_pOwner;//֪��������ָ��
public:
	int InBufferCount( void );
	BOOL OpenPort(void *pOwner  //ָ��ָ��
		           ,UINT PortNo //���ں�
				   ,UINT baud   //������
				   ,UINT parity //��żУ��
				   ,UINT databits //����λ
				   ,UINT stopbits);//ֹͣλ
	void ClosePort();
	BOOL WritePort(const BYTE *buffer, DWORD dwBytesWritten);
	BOOL SetSerialTimeOuts(int time);
	void CloseReadThread();
	static DWORD WINAPI ReadThreadFunc(LPVOID lparam);

	ONSERIESREAD m_OnSeriesRead;//���ڶ�ȡ�ص�����
};
