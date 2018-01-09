package com.JGather;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

 class IPMeterinfo
{
	 public String sIP;
	 public String sPort;
	 public String sMetercode;
	 public String sRemark;
	 public String sGuiyue;
	 public String sPower;
}
 class E_Meter_Instant
 {
	 
	 public String  fElectricitytime;//�ɼ�ʱ��
	 public	double dA_Phase_voltage; //A���ѹ
	 public	double dB_phase_voltage; //B���ѹ
	 public	double dC_Phase_voltage; //C���ѹ
	 public	double dA_Current; //A�����
	 public	double dB_Current; //B�����
	 public	double dC_Current; //C�����
	 public	double dTotal_Active;//���й�
	 public	double dTotal_Reactive;//���޹�
	 public	double dTotal_Powerfactor;//�ܹ�������
	 public	double dFreguency;//Ƶ��
	 public	double dForWard_Active;//�����й�(�ܹ���)
	 public	double dReForWard_Active;//�����й�
	 public	double dForWard_Active_A;//�����й�
	 public	double dReForWard_Active_A;//�����й�
	 public	double dInstant_Active;//˲ʱ����
	 public String sMeter_Instant_NO;//������
	 public String  sStatus;//״̬
 }
 class TStatus
 {
	 public String sMeterCode;//����
	 public String sStatus;//״̬
 }
 class  W_Meter_Instant
 {
 	String fRecord_Time;//�ɼ�ʱ��
 	double dRecord_Date;// �ɼ�����
 	String  sMeter_Instant_NO;//ˮ�����
 	String  sStatus;//״̬
 }
public class OraclHelp {
	private Connection conn=null;
	private Statement st=null;
	private ResultSet rs=null;
	private String url="jdbc:oracle:thin:@11.11.1.11:1521:db1";// ���ݿ��ַ 
	private String name="programer";
	private String pw="emp";
	private String ClassName="oracle.jdbc.driver.OracleDriver";
	private String Threadid="";
	public OraclHelp(String sThread)
	{
		this.Threadid=sThread;
	}
	public boolean Oraclconn()
	{
		try {
			Class.forName(ClassName);
			conn=DriverManager.getConnection(url,
					name, pw);
			st =conn.createStatement();
		}catch (SQLException e)
		{
			System.out.println(Threadid+"oracle conn error");
			return false;
			
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("oracle conn error");
			e.printStackTrace();
			return false;
		}
		System.out.println(Threadid+"oracle conn success");
		return true;
	}
	public void OraclClose()
	{
		try {
			if(conn!=null)
			{
				st.close();
				conn.close();
				
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}
	/*
	�������ܣ��õ����ݱ����ж��ٸ�ip��ַ
	�������壺��
	����ֵ�� ip��ַ�ĸ���
	*/
	
	public ArrayList<IPMeterinfo> GetIPInfo() throws SQLException
	{
		 ArrayList<IPMeterinfo> al =new ArrayList<IPMeterinfo>();
		 al.clear();
		String SqlCmd="select distinct(CONVERTOR_IP),CONVERTOR_PORT from PROGRAMER.DEVICE_INFO where (STIPULATION =1 or STIPULATION=2 or METER_TYPE='W') and IS_DORMDEVICE=0 and CONVERTOR_IP is not null";
		rs =st.executeQuery(SqlCmd);
	
		while(rs.next())
		{
			//add( )�������ӵ��Ƕ�������ã�ÿ�ζ�������������õ�ֵ����Ȼlist�����ֵ��һ���ġ�
			//����IPaddress ipa =new IPaddress()Ӧ��ѭ����
			IPMeterinfo ipa =new IPMeterinfo();//
			ipa.sIP=rs.getString("CONVERTOR_IP");
			ipa.sPort=rs.getString("CONVERTOR_PORT");
			al.add(ipa);  
		
		}
		return al;
	}
	/*
	�������ܣ�ͨ����ͬip��ַ�����ظ�ip�ϵ����б���Ϣ
	�������壺IPAdress Ip��ַ��IpMeterInfo ����ip��ַ�ϵı���Ϣ��
	����ֵ��
	*/
	//BOOL  CDataBank::GetIpMeterInfo(CString IpAddress,CString IpPort,CArray<IPMETERINFO,IPMETERINFO&> * ArrayIpMeterInfo)
	public ArrayList<IPMeterinfo> GetIpMeterInfo(String sIp,String sPort)throws SQLException
	{
		ArrayList<IPMeterinfo> al =new ArrayList<IPMeterinfo>();
		al.clear();
		String SqlCmd="select lpad(METER_ID,12,0) METER_ID,CONVERTOR_IP,CONVERTOR_PORT,METER_TYPE,nvl(POWER,0.0) POWER,STIPULATION from PROGRAMER.DEVICE_INFO t where convertor_ip="+
		"'"+sIp+"'"+"and convertor_port="+"'"+sPort+"'"+
		"and(STIPULATION =1 or STIPULATION=2 or METER_TYPE='W')and METER_ID is not null";
		rs =st.executeQuery(SqlCmd);
			
		while(rs.next())
		{
			//add( )�������ӵ��Ƕ�������ã�ÿ�ζ�������������õ�ֵ����Ȼlist�����ֵ��һ���ġ�
			//����IPaddress ipa =new IPaddress()Ӧ��ѭ����
			IPMeterinfo ipa =new IPMeterinfo();//
			ipa.sIP=rs.getString("CONVERTOR_IP");
			ipa.sPort=rs.getString("CONVERTOR_PORT");
			ipa.sMetercode=rs.getString("METER_ID");//Integer.toString(rs.getInt("METER_ID"));
			ipa.sRemark=rs.getString("METER_TYPE");
			ipa.sGuiyue=Integer.toString(rs.getInt("STIPULATION"));
			ipa.sPower=Integer.toString(rs.getInt("POWER"));
			al.add(ipa);  
		}
		return al;
	}
	/*\
	 * �������ɼ�����
	 */
	public int InsertElectData(Object obj) throws SQLException
	{
		E_Meter_Instant InserElect =new E_Meter_Instant();
		InserElect =(E_Meter_Instant)obj;
		String SqlCmd="";
		SqlCmd=String.format("insert into PROGRAMER.E_METER_CLLCT values(PROGRAMER.E_METER_CLLCT_S.NEXTVAL,to_number('%s'),SYSDATE,000,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,'%s','','','','','')",
				InserElect.sMeter_Instant_NO,InserElect.dA_Phase_voltage,InserElect.dB_phase_voltage,InserElect.dC_Phase_voltage,InserElect.dA_Current,InserElect.dB_Current,InserElect.dC_Current,InserElect.dTotal_Active,InserElect.dTotal_Reactive,InserElect.dTotal_Powerfactor,
				InserElect.dFreguency,InserElect.dForWard_Active,InserElect.dReForWard_Active,InserElect.dForWard_Active_A,InserElect.dReForWard_Active_A,InserElect.sMeter_Instant_NO);
		int i=0;
		try {
			i=st.executeUpdate(SqlCmd);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		return i;
	}
	/*
	 * ���µ������
	 */
	public int UpdateElectData(Object obj)throws SQLException
	{
		
		E_Meter_Instant UpDateElect =new E_Meter_Instant();
		UpDateElect =(E_Meter_Instant)obj;
		String SqlCmd="";
		SqlCmd=String.format("update PROGRAMER.E_METER_INSTANT set ELECTRICITY_TIME =SYSDATE, A_PHASE_VOLTAGE=%f ,B_PHASE_VOLTAGE =%f,"
				+ "C_PHASE_VOLTAGE=%f,CURRENT_A=%f,CURRENT_B = %f,CURRENT_C =%f,TOTAL_ACTIVE=%f,TOTAL_REACTIVE=%f,"
				+ "TOTAL_POWER_FACTOR=%f ,FREQUENCY=%f,FORWARD_ACTIVE=%f,REFORWARD_ACTIVE=%f,FORWARD_ACTIVE_A=%f,"
				+"REFORWARD_ACTIVE_A=%f ,METER_INSTANT_NO='%s' where METER_INSTANT_ID =to_number('%s')",
				UpDateElect.dA_Phase_voltage,UpDateElect.dB_phase_voltage,UpDateElect.dC_Phase_voltage,UpDateElect.dA_Current,
				UpDateElect.dB_Current,UpDateElect.dC_Current,UpDateElect.dTotal_Active,UpDateElect.dTotal_Reactive,UpDateElect.dTotal_Powerfactor,
				UpDateElect.dFreguency,UpDateElect.dForWard_Active,UpDateElect.dReForWard_Active,
				UpDateElect.dForWard_Active_A,UpDateElect.dReForWard_Active_A,UpDateElect.sMeter_Instant_NO,UpDateElect.sMeter_Instant_NO);
		
		int i=0;
		try {
			i=st.executeUpdate(SqlCmd);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return i;
	}
	/*
	 * ����״̬��
	 */
	public int InsertStatusData(Object obj)
	{
		TStatus tStatus =new TStatus();
		tStatus =(TStatus)obj;
		String SqlCmd="";

		SqlCmd=String.format("insert into PROGRAMER.DEVICE_STATUS_HIS values(PROGRAMER.DEVICE_STATUS_S.NEXTVAL,to_number('%s'),'%s',SYSDATE)",tStatus.sMeterCode,tStatus.sStatus);
		int i=0;
		try {
			i=st.executeUpdate(SqlCmd);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return i;
	}
	public int UpDateStatusData(Object obj)
	{
		TStatus tStatus =new TStatus();
		tStatus =(TStatus)obj;
		String SqlCmd="";
		SqlCmd=String.format("update PROGRAMER.DEVICE_STATUS set STATUS='%s',UPDATE_TIME=SYSDATE where METER_ID=to_number('%s')",tStatus.sStatus,tStatus.sMeterCode);
		int i=0;
		try {
			i=st.executeUpdate(SqlCmd);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return i;
	}
	public int InsertWaterData(Object obj)
	{
		W_Meter_Instant WaterInfo =new W_Meter_Instant();
		WaterInfo =(W_Meter_Instant)obj;
		String SqlCmd="";
		SqlCmd=String.format("insert into PROGRAMER.w_meter_cllct t values( PROGRAMER.W_METER_CLLCT_S.NEXTVAL,to_number(%s),sysdate,000 ,%f,to_char(to_number('%s')),'','','','','')",
				WaterInfo.sMeter_Instant_NO,WaterInfo.dRecord_Date,WaterInfo.sMeter_Instant_NO);
		int i=0;
		try {
			i=st.executeUpdate(SqlCmd);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return i;
	}
	public int UpdateWaterData(Object obj)
	{
		W_Meter_Instant WaterInfo =new W_Meter_Instant();
		WaterInfo =(W_Meter_Instant)obj;
		String SqlCmd="";
		SqlCmd=String.format("update PROGRAMER.W_METER_INSTANT set RECORD_TIME=SYSDATE,RECORD_MS =000,RECORD_DATA=%f,METER_INSTANT_NO=to_char(to_number('%s')) where METER_INSTANT_ID = to_number(%s)",
				WaterInfo.dRecord_Date,WaterInfo.sMeter_Instant_NO,WaterInfo.sMeter_Instant_NO);
		int i=0;
		try {
			i=st.executeUpdate(SqlCmd);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return i;
	}
}