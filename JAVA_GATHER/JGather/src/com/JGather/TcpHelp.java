package com.JGather;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpHelp {
	private Socket socket;  
	//private BufferedReader in;  
	//private PrintWriter out; 
	//private BufferedWriter bw;
	//private BufferedReader line;
    private  DataOutputStream dos ;
	private DataInputStream dis ;
	private int NETFLAG=1; //1:���� 2:���粻ͨ 3:�޷������� 4:��������У��� 8:�߼�ɾ�� 9:��������
	private String SERVERIP="127.0.0.1";
	private int PORT=10001;
	private String METERCODE="";
    public  TcpHelp(String sServerip ,int Port)  
	{  
    	
    	this.SERVERIP=sServerip;
    	this.PORT= Port;
	} 
    public boolean ConnServer(int nTimeOut)
    {
    	try {
    		socket =new Socket();
        	socket.connect(new InetSocketAddress(SERVERIP, PORT),nTimeOut);
        	dos =new DataOutputStream(socket.getOutputStream());
			dis =new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO: handle exception
			//e.printStackTrace();
			System.out.println(SERVERIP+"net_conn error");
			//Closesocket();
			NETFLAG=2;
			return false;
		} catch(Exception e)
		{
			System.out.println(SERVERIP+e.toString());
			//e.printStackTrace();
			NETFLAG=2;
			return false;
		}
    	System.out.println(SERVERIP+"net_conn sucess");
    	return true;
    }
    //�õ�����״̬
    public int GetNetFlag()
    {
    
    	return NETFLAG;
    }
/*
	1:���� 2:���粻ͨ 3:�޷������� 4:��������У��� 8:�߼�ɾ�� 9:��������)
	97��Լ��1�� 07��Լ��2�� �ϵ����ˮ����0
*/
    public E_Meter_Instant SendElectCmd(String sCmd,String sRemark,String sGuiyue,String sMetercode)
    {
    	sRemark.trim();
    	sGuiyue.trim();
    	sRemark.toUpperCase();
    	METERCODE=sMetercode;
  	  E_Meter_Instant pElectRecv =new E_Meter_Instant();
//���͵������
    	if(sRemark.equals("E"))
    	{  
// 2007��Լ
    		if(sGuiyue.equals("2")) //2007��Լ
    		{
    			//int nRecv=0;
    			byte [] cRecv;
    			String tmpCmd=sCmd.substring(0, 40);
    			//�����й��ܵ���
    			 cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_2007 =GetRecvValues(cRecv,sizeof(cRecv),0);
    			double dData_2007 = GetRecvDoubleValues(sData_2007,sizeof(cRecv),0,2);
    			pElectRecv.dForWard_Active=dData_2007;
    			tmpCmd=sCmd.substring(41,81);
    			cRecv.clone();
    			  //�����й��ܵ���
    			 cRecv=SendAndRecvCmd(tmpCmd);
    			 if(NETFLAG!=1) return pElectRecv;
    			String sData_FXYG_2007 = GetRecvValues(cRecv,sizeof(cRecv),0);
    			double dData_FXYG_2007 =GetRecvDoubleValues(sData_FXYG_2007,sizeof(cRecv),0,2);
    			pElectRecv.dReForWard_Active=dData_FXYG_2007;
    			tmpCmd= sCmd.substring(82,122);
    			cRecv.clone();
    			//��ѹ
    			cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_DY_A_2007 =  GetRecvValues(cRecv,sizeof(cRecv),0);
    			double dData_DY_A_2007 = GetRecvDoubleValues(sData_DY_A_2007,sizeof(cRecv),0,1);
    			pElectRecv.dA_Phase_voltage=dData_DY_A_2007;
    			//����
    			tmpCmd = sCmd.substring(123,163);
    			cRecv.clone();
    			cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			//String  sData_DL_A_2007 = GetRecvValues(cRecv,sizeof(cRecv),0);
    			String  sData_DL_A_2007 = GetRecvValues_plus(cRecv,sizeof(cRecv),0);
    			double dData_DL_A_2007 =GetRecvDoubleValues(sData_DL_A_2007,sizeof(cRecv),0,3);
    			
    			pElectRecv.dA_Current=dData_DL_A_2007;
    			//�ܹ�������
    			tmpCmd = sCmd.substring(164,204);
    			cRecv.clone();
    			  cRecv=SendAndRecvCmd(tmpCmd);
    			  if(NETFLAG!=1) return pElectRecv;
    			//String sData_ZGLYS_2007 =  GetRecvValues(cRecv,sizeof(cRecv),0);
    			String sData_ZGLYS_2007 = GetRecvValues_plus(cRecv,sizeof(cRecv),0);
    			double dData_ZGLYS_2007 =GetRecvDoubleValues(sData_ZGLYS_2007,sizeof(cRecv),0,3);
    			pElectRecv.dTotal_Powerfactor=dData_ZGLYS_2007;
    			//�����й��ܹ���
    			tmpCmd = sCmd.substring(205,245);
    			cRecv.clone();
    			  cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_ZXYGZGL_2007 =  GetRecvValues(cRecv,sizeof(cRecv),0);
    			double dData_ZXYGZGL_2007 =GetRecvDoubleValues(sData_ZXYGZGL_2007,sizeof(cRecv),0,4);
    			//pElectRecv.dForWard_Active=dData_ZXYGZGL_2007;
    			pElectRecv.dTotal_Active=dData_ZXYGZGL_2007;
    			//�����й��ܹ���
    			tmpCmd = sCmd.substring(246,286);
    			cRecv.clone();
    			  cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_FXYGZGL_2007 = GetRecvValues(cRecv,sizeof(cRecv),0);
    			double dData_FXYGZGL_2007 =GetRecvDoubleValues(sData_FXYGZGL_2007,sizeof(cRecv),0,4);
    			//pElectRecv.dReForWard_Active=dData_FXYGZGL_2007;
    			pElectRecv.dTotal_Reactive=dData_FXYGZGL_2007;
    			//˲ʱ�й�����
    			tmpCmd =sCmd.substring(287,327);
    			cRecv.clone();
    			 cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_SSZYGGL_2007 =GetRecvValues_plus(cRecv,sizeof(cRecv),0);
    			double dData_SSZYGGL_2007 = GetRecvDoubleValues(sData_SSZYGGL_2007,sizeof(cRecv),0,4);
    			pElectRecv.dInstant_Active=dData_SSZYGGL_2007;
    		}
//1997��Լ
    		if(sGuiyue.equals("1"))//1997��Լ
    		{
    			//int nRecv=0;
    			byte[]cRecv;
    			String tmpCmd=sCmd.substring(0,34);
    			//�����й��ܵ���
    			cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData =GetRecvValues(cRecv,sizeof(cRecv),1);
    			double dData = GetRecvDoubleValues(sData,sizeof(cRecv),1,2);
    			pElectRecv.dForWard_Active=dData;
    			//�����й�����
    			cRecv.clone();
    			tmpCmd=sCmd.substring(35,69);
    			cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_FXYG = GetRecvValues(cRecv,sizeof(cRecv),1);
    			double dData_FXYG =GetRecvDoubleValues(sData_FXYG,sizeof(cRecv),1,2);
    			//pElectRecv.dTotal_Reactive=dData_FXYG;
    			pElectRecv.dReForWard_Active=dData_FXYG;
    			//��ѹA
    			cRecv.clone();
    			tmpCmd=sCmd.substring(70,104);
    			 cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_DY_A = GetRecvValues(cRecv,sizeof(cRecv),1);
    			double dData_DY_A = GetRecvDoubleValues(sData_DY_A,sizeof(cRecv),1,1);
    			pElectRecv.dA_Phase_voltage=dData_DY_A;
    			//��ѹB
    			cRecv.clone();
    			tmpCmd=sCmd.substring(105,139);
    			 cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_DY_B = GetRecvValues(cRecv,sizeof(cRecv),1);
    			double dData_DY_B =GetRecvDoubleValues(sData_DY_B,sizeof(cRecv),1,1);
    			pElectRecv.dB_phase_voltage =dData_DY_B;
    			//��ѹc
    			cRecv.clone();
    			tmpCmd=sCmd.substring(140,174);
    			 cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_DY_C =GetRecvValues(cRecv,sizeof(cRecv),1);
    			double dData_DY_C =GetRecvDoubleValues(sData_DY_C,sizeof(cRecv),1,1);
    			pElectRecv.dC_Phase_voltage=dData_DY_C;
    			//����A
    			cRecv.clone();
    			tmpCmd=sCmd.substring(175,209);
    			 cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_DL_A = GetRecvValues(cRecv,sizeof(cRecv),1);
    			double dData_DL_A = GetRecvDoubleValues(sData_DL_A,sizeof(cRecv),1,2);
    			pElectRecv.dA_Current=dData_DL_A;
    			//����B
    			cRecv.clone();
    			tmpCmd=sCmd.substring(210,244);
    			 cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_DL_B = GetRecvValues(cRecv,sizeof(cRecv),1);
    			double dData_DL_B = GetRecvDoubleValues(sData_DL_B,sizeof(cRecv),1,2);
    			pElectRecv.dB_Current= dData_DL_B;
    			//����c
    			cRecv.clone();
    			tmpCmd=sCmd.substring(245,279);
    			 cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_DL_C = GetRecvValues(cRecv,sizeof(cRecv),1);
    			double dData_DL_C = GetRecvDoubleValues(sData_DL_C,sizeof(cRecv),1,2);
    			pElectRecv.dC_Current =dData_DL_C;
    			//�ܹ�������
    			cRecv.clone();
    			tmpCmd=sCmd.substring(280,314);
    			 cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_ZGLYS = GetRecvValues(cRecv,sizeof(cRecv),1);
    			double dData_ZGLYS = GetRecvDoubleValues(sData_ZGLYS,sizeof(cRecv),1,3);
    			pElectRecv.dTotal_Powerfactor = dData_ZGLYS;

    			//�����й��ܹ���
    			cRecv.clone();
    			tmpCmd=sCmd.substring(315,349);
    			 cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_ZXYGZGL = GetRecvValues(cRecv,sizeof(cRecv),1);
    			double dData_ZXYGZGL = GetRecvDoubleValues(sData_ZXYGZGL,sizeof(cRecv),1,2);
    			//pElectRecv.dForWard_Active = dData_ZXYGZGL;
    			pElectRecv.dTotal_Active=dData_ZXYGZGL;
    			//�����й��ܹ���
    			cRecv.clone();
    			tmpCmd=sCmd.substring(350,384);
    			 cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_FXYGZGL = GetRecvValues(cRecv,sizeof(cRecv),1);
    			double dData_FXYGZGL = GetRecvDoubleValues(sData_FXYGZGL,sizeof(cRecv),1,2);
    			//pElectRecv.dReForWard_Active = dData_FXYGZGL;
    			pElectRecv.dTotal_Reactive=dData_FXYGZGL;
    			//�����޹��ܵ���
    			cRecv.clone();
    			tmpCmd=sCmd.substring(385,419);
    			 cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_ZXWGZDN = GetRecvValues(cRecv,sizeof(cRecv),1);
    			double dData_ZXWGZDN = GetRecvDoubleValues(sData_ZXWGZDN,sizeof(cRecv),1,2);
    			pElectRecv.dForWard_Active_A = dData_ZXWGZDN;

    			//�����޹��ܵ���
    			cRecv.clone();
    			tmpCmd=sCmd.substring(420,454);
    			 cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_FXWGZDN = GetRecvValues(cRecv,sizeof(cRecv),1);
    			double dData_FXWGZDN = GetRecvDoubleValues(sData_FXWGZDN,sizeof(cRecv),1,2);
    			pElectRecv.dReForWard_Active_A =dData_FXWGZDN;

    			//˲ʱ�й�����
    			tmpCmd =sCmd.substring(455,489);
    			cRecv.clone();
    			 cRecv=SendAndRecvCmd(tmpCmd);
    			if(NETFLAG!=1) return pElectRecv;
    			String sData_SSZYGGL =GetRecvValues_plus(cRecv,sizeof(cRecv),1);
    			double dData_SSZYGGL = GetRecvDoubleValues(sData_SSZYGGL,sizeof(cRecv),1,4);
    			pElectRecv.dInstant_Active=dData_SSZYGGL;
    		}
    		pElectRecv.sMeter_Instant_NO=sMetercode;
    		NETFLAG=1;
    	}
		return pElectRecv;
    }
    public W_Meter_Instant SendWaterCmd(String sCmd,String sRemark,String sGuiyue,String sMetercode)
    {
    	W_Meter_Instant dResult =new W_Meter_Instant();
    	if(sRemark.equals("W"))
    	{
    		int nRecv=0;
    		byte [] cRecv;
    		
    		cRecv=SendAndRecvCmd(sCmd);
    		if(cRecv[0]!=0xfe) return dResult;
    		int nstart=0;
    		for (int i=0;i<nRecv;i++)
    		{
    			if(cRecv[i]==0x68)
    			{
    				nstart=i;
    				break;
    			}
    		}
    		if(cRecv[nstart+9]!=0x81)
    		{
    			NETFLAG=3;  //�����쳣
    			return dResult;//���������쳣
    		}
    		String sWaterData= GetRecvValues(cRecv,nRecv,2);
    		double dWaterData= GetRecvDoubleValues(sWaterData,nRecv,2,2);
    		dResult.dRecord_Date=dWaterData;
    		String sStatus;
    		sStatus=String.format("%d",NETFLAG);
    		dResult.sStatus=sStatus;
    		dResult.sMeter_Instant_NO=sMetercode;
    		NETFLAG=1;
      }
    	return dResult;
    }
    public  byte[] SendAndRecvCmd(String s)
    {
    	NETFLAG=1;
    	
    	System.out.println("��������:"+s);
    	 byte [] send;
    	 byte [] recv =new byte[256];
		 try {
			 socket.setSoTimeout(10000);
			 send =hexStringToByte(s);
			 dos.write(send);
				recv.clone();
				Thread.sleep(100);
				dis.read(recv);
				System.out.println("��������Ϣ : " + ByteConversionString(recv,sizeof(recv)));
				if(!VerifyRecvData(recv,sizeof(recv)))
				{
					NETFLAG=4;
					return recv;
				}
		} catch (IOException e) {
			// TODO: handle exception
			//e.printStackTrace();
			System.out.println(SERVERIP+e.toString());
			NETFLAG=3;
			return recv;
		}catch (Exception e)
		 {   
			 NETFLAG=3;
			 System.out.println(SERVERIP+e.toString());
			return recv;
			 
		 }
		return recv;  
    }

    public String ByteConversionString(byte []pchBuffer, int nLength)
    {
    	String m_strSwapBuff = "";
    	String m_strTempBuff="";	
 
    	for(int i=0; i<nLength; i++)
    	{
    		m_strTempBuff="";
    		m_strTempBuff=String.format("%x", (pchBuffer[i] & 0xF0) >> 4);
    		m_strSwapBuff += m_strTempBuff;
    		//
    		m_strTempBuff="";
    		m_strTempBuff=String.format("%x", (pchBuffer[i] & 0x0F));
    		m_strSwapBuff += m_strTempBuff;
    	}	
    	return m_strSwapBuff;
    }
    /*
      �������ص��ַ�����
       nRemark  
    */
    public String GetRecvValues(byte[] pchbuffer,int nRecvLen,int nRemark)
    {
       
    	byte[] rc;
    	rc =new byte[nRecvLen];
    	rc.clone();
    	int len;
    	int nstart=0;
    	for (int i=0;i<nRecvLen;i++)
    	{
    		if(pchbuffer[i]==0x68)
    		{
    			nstart=i;
    			break;
    		}
    	}
    	switch (nRemark)
    	{
    	case 0:
    		{
    			 len=pchbuffer[nstart+9]-4;
    			for (int i=0;i<len;i++)
    			{
    				rc[i]=(byte) (pchbuffer[nstart+9+i+5]-0x33);
    			}
    		}
    		break;
    	case  1:
    		{
    			 len=pchbuffer[nstart+9]-2;
    			for (int i=0;i<len;i++)
    			{
    				rc[i]=(byte) (pchbuffer[nstart+9+i+3]-0x33);
    			}
    		}
    		break;
    	case  2:  //ˮ��
    		{   
    			len =4;
    			for (int i=0;i<4;i++)
    			{
    				rc[i]=pchbuffer[nstart+14+i];
    			}
    		}
    		break;
    	default:
    		 len =4;
    		break;
    	}
    	String stmp,sRecv="";
    	for(int i=len-1; i>=0; i--)
    	{
    		
    		stmp=String.format("%02x",rc[i]);
    		sRecv+=stmp;
    	}
    	return sRecv;
    }
    public double GetRecvDoubleValues(String sValues,int nRecvLen,int nRmark,int nIndex)
    {
    	double dValues=0.0;
    	//CString sValues=GetRecvValues(pchbuffer,nRecvLen,nRmark);
    	if(sValues.length()==0) return dValues;
    	dValues=Double.parseDouble(sValues);
    	switch (nIndex)
    	{
    	case 0:
    		dValues=dValues/1;
    		break;
    	case 1:
    		dValues=dValues/10;
    		break;
    	case 2:
    		dValues=dValues/100;
    		break;
    	case 3:
    		dValues=dValues/1000;
    		break;
    	case 4:
    		dValues=dValues/10000;
    		break;
    	default:
    		dValues=dValues/1;
    		break;
    	}
    	return dValues;

    }

    /*
    	�������ص����ݣ����λΪ����λ
    */
    public String GetRecvValues_plus(byte []pchbuffer,int nRecvLen,int nRemark)
    {
       
    	byte []rc;
    	rc =new byte[nRecvLen];
    	rc.clone();
    	int len;
    	int nstart=0;
    	for (int i=0;i<nRecvLen;i++)
    	{
    		if(pchbuffer[i]==0x68)
    		{
    			nstart=i;
    			break;
    		}
    	}
    	byte uc = 0;
    	switch (nRemark)
    	{
    	case 0:
    		{
    			 len=pchbuffer[nstart+9]-4;
    			for (int i=0;i<len;i++)
    			{
    				rc[i]=(byte) (pchbuffer[nstart+9+i+5]-0x33);
    			}
    			uc=(byte) (rc[len-1]&0x80);
    			if(uc!=0x00)
    			{
    			   rc[len-1]=(byte) (rc[len-1]&0x7F);
    			}
    		}
    		break;
    	case  1:
    		{
    			 len=pchbuffer[nstart+9]-2;
    			for (int i=0;i<len;i++)
    			{
    				rc[i]=(byte) (pchbuffer[nstart+9+i+3]-0x33);
    			}
    			uc=(byte) (rc[len-1]&0x80);
    			if(uc!=0x00)
    			{
    			   rc[len-1]=(byte) (rc[len-1]&0x7F);
    			}
    		}
    		break;
    	case  2:
    		{   
    			len =4;
    			for (int i=0;i<4;i++)
    			{
    				rc[i]=(byte) (pchbuffer[nstart+14+i]-0x33);
    			}
    			uc=(byte) (rc[len-1]&0x80);
    			if(uc!=0x00)
    			{
    			   rc[len-1]=(byte) (rc[len-1]&0x7F);
    			}
    		}
    		break;
    	default:
    		 len =4;
    		break;
    	}
    	String stmp,sRecv="";
    	for(int i=len-1; i>=0; i--)
    	{
    		stmp=String.format("%02x",rc[i]);
    		sRecv+=stmp;
    	}
    	if(uc!=0x00)
    	{
    		sRecv="-"+sRecv;
    	}
    	return sRecv;
    }
    /*
    У�鷵�ص�����
    FEFEFEFE685100260811206891083334333333333333B216   2007
    6847490211000068810643C333333333CC16    1997
    fefefe68110801002000200768a414143343333357453956563745533232323257443317d616  ˮ��
    */
    public Boolean VerifyRecvData(byte []uRecvData,int nRecvLen)
    {
    	Boolean bFlag=false;
    	byte[] rc;
    	rc =new byte[nRecvLen];
    	rc.clone();
    	rc=uRecvData;
    	byte ntotal=0;
    	byte nResult=0;
    	int nEnd=nRecvLen-1;
    	int nstart=0;
    	/*
    	for(int i=nRecvLen-1;i>=0;i--)
    	{
    		if(rc[i]==0x68)
    		{
    			if(rc[i+2]<=0x01) return FALSE;
    		}
    	}
    	*/
    	for (int i=0;i<nRecvLen;i++)
    	{
    		if(rc[i]==0x68)
    		{
    			nstart=i;
    			break;
    		}
    	}

    	for(int i=nRecvLen-1;i>=0;i--)
    	{
    		if(rc[i]==0x16)
    		{
    			nEnd=i;
    			break;
    		}
    	}
    	for (int i=nstart;i<nEnd-1;i++)
    	{
    		ntotal+=rc[i];
    	}
    	nResult=(byte) (ntotal%256);
    	//System.out.println("У����="+nResult+"�ɼ����="+rc[nEnd-1]);
    	if(nResult==rc[nEnd-1]) bFlag=true;	
    	return bFlag;
    }
    //�ر�socket����
    public void Closesocket()
    {
    	try {
    		if(dis!=null)
    		{
    			dis.close();
    		}
    		if(dos!=null)
    		{
    			dos.close();
    		}
    		if(socket!=null)
    		{
    			socket.close();
    		}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	
    }
    public  byte[] hexStringToByte(String hex) {
        
        int nsLength =hex.length();
        byte[] result = new byte[nsLength/2];
        if(0!=nsLength%2) return null;
        int c =0;
        for(int i=0;i<nsLength;i+=2)
        {
        	String stmp=hex.substring(i,i+2);
        	stmp.trim();
        	result[c++]=(byte)Integer.parseInt(stmp,16);
        } 
        //result[c++]='\n';
        return result;
    }
    private int sizeof(byte[] recv) {
		// TODO Auto-generated method stub
		int nlength=0;
		for(int i=0;i<recv.length;i++)
		{
			nlength=i;
			if(recv[i]==0x16)
				break;
		}
		return nlength+1;
	}
}