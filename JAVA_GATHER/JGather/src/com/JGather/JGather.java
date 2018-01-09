package com.JGather;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.sql.SQLException;
import java.util.ArrayList;

public class JGather {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		OraclHelp oh =new OraclHelp("mainThread");
		ArrayList<IPMeterinfo> al = new ArrayList<IPMeterinfo>();
		IPMeterinfo ipi =new IPMeterinfo();
		FileChannel lockfc=null ;
		try {
			File f =new File("JG.lock");
			String spath =f.getPath();
			RandomAccessFile fis =new RandomAccessFile(spath, "rw");
			lockfc =fis.getChannel();
			lockfc.lock();
			if (oh.Oraclconn()) {
				al = oh.GetIPInfo();
				int count = al.size();
				for (int i = 0; i < count; i++) {
					ipi = al.get(i);
					GatherThread gt = new GatherThread(ipi.sIP, ipi.sPort);
					gt.start();
					Thread.sleep(100);
				}
			}
			oh.OraclClose();	
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			oh.OraclClose();
			lockfc.close();
			
			
		}
		
	}
}
