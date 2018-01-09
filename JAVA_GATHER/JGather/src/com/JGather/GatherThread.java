package com.JGather;

import java.sql.SQLException;
import java.util.ArrayList;

public class GatherThread extends Thread
{
  String sip;
  String sport;
  public GatherThread(String sarg0,String sarg1)
  {
	sip=sarg0;
	sport=sarg1;
	  
  }
  /*
  public void run()
  {
	  TcpHelp th =new TcpHelp(sip, Integer.parseInt(sport));
	  th.ConnServer(20000);
	  th.Closesocket();
	  OraclHelp oh =new OraclHelp(sip);
	  oh.Oraclconn();
	  oh.OraclClose();
  }
  */
  
 public void run()
 {
	 OraclHelp oh =new OraclHelp(sip);
	 TcpHelp th =new TcpHelp(sip, Integer.parseInt(sport));
	 try
		{
			System.out.println(sip+" Thread run");
			//OraclHelp oh =new OraclHelp();
			if(!oh.Oraclconn())return;
			ArrayList<IPMeterinfo> al = new ArrayList<IPMeterinfo>();
			al=oh.GetIpMeterInfo(sip, sport);
			//TcpHelp th =new TcpHelp(sip, Integer.parseInt(sport));
			int ncount =al.size();
			if(!th.ConnServer(20000)) //�������Ӳ��ɹ� 
			{
				TStatus ts = new TStatus();
				IPMeterinfo ipi =new IPMeterinfo();
				for(int i=0;i<ncount;i++)
				{
					ipi=al.get(i);
					ts.sMeterCode=ipi.sMetercode;
					ts.sStatus="2";
					//oh.UpDateStatusData(ts);
				}
				//oh.OraclClose();
				//th.Closesocket();
				int ns=th.GetNetFlag();
				return;
			}
			int ns=th.GetNetFlag();
			for(int i=0;i<ncount;i++)
			{
				IPMeterinfo ipi =new IPMeterinfo();
				ipi =al.get(i);
				System.out.println(ipi.sMetercode);
				IpMeterCMD imc =new IpMeterCMD();
				String strcmd =imc.GetCBCommandS(ipi.sMetercode, ipi.sRemark, ipi.sGuiyue, "0");
				if(!strcmd.equals("error"))
				{
					if(ipi.sRemark.equals("E")) //����ɼ�
					{
						E_Meter_Instant dElectRecv =new E_Meter_Instant();
						TStatus ts = new TStatus();
						int nIndex=0;
						int nStatus=0;
						while(nIndex<2)
						{
							dElectRecv=th.SendElectCmd(strcmd, "E", ipi.sGuiyue,ipi.sMetercode);
							if(dElectRecv.sMeter_Instant_NO!=ipi.sMetercode) //��ñ����֤�����ݲɼ�����
							{
								break;
							}
							else
							{
								nIndex++;
							}
						}
						nStatus=th.GetNetFlag();
						ts.sMeterCode=dElectRecv.sMeter_Instant_NO;
						ts.sStatus=Integer.toString(nStatus);	
						System.out.println(ipi.sMetercode+"״̬��"+nStatus);
						if(nStatus==1)
						{
							//�������ݿ⣬�������ݿ�
							//oh.InsertElectData(dElectRecv);
							//oh.UpdateElectData(dElectRecv);
							System.out.println(dElectRecv.sMeter_Instant_NO+"A_Current"+dElectRecv.dA_Current);
							System.out.println(dElectRecv.sMeter_Instant_NO+"A_Phase_voltage"+dElectRecv.dA_Phase_voltage);
							double fPower=Integer.parseInt(ipi.sPower);
							if((fPower!=0.0)&&(0.6*fPower<dElectRecv.dInstant_Active))
							if(0.6*fPower<dElectRecv.dInstant_Active)
							{
								ts.sStatus="10";//���ر���
							}
						}
						//oh.UpDateStatusData(ts);
					}
					if(ipi.sRemark.equals("W"))
					{
						W_Meter_Instant dWaterRecv =new W_Meter_Instant();
						TStatus ts = new TStatus();
						int nIndex=0;
						int nStatus=0;
						while(nIndex<2)
						{
							dWaterRecv=th.SendWaterCmd(strcmd, "W", ipi.sGuiyue, ipi.sMetercode);
							if(dWaterRecv.sMeter_Instant_NO!=ipi.sMetercode)
							{
								break;
							}
							else
							{
								nIndex++;
							}
						}
						nStatus=th.GetNetFlag();
						ts.sMeterCode=dWaterRecv.sMeter_Instant_NO;
						ts.sStatus=Integer.toString(nStatus);
						if(nStatus==1)
						{
							//oh.InsertWaterData(dWaterRecv);
							//oh.UpdateWaterData(dWaterRecv);
						}
						//oh.UpDateStatusData(ts);
					}
				}
			}
			oh.OraclClose();
			th.Closesocket();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	    finally
	    {
	    	oh.OraclClose();
			th.Closesocket();	
	    }
 }
}