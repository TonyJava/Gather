package com.ProcessKeeplive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class KeepLive {
	/*
	 * 通过判断文件是否被锁定来判断程序是否运行
	 * 运行返回true 中断false
	 */
	private static boolean isruning(String sFilepath)
	{
		boolean bFlag=false;
		try {
			RandomAccessFile fis =new RandomAccessFile(sFilepath, "rw");
			FileChannel lockfc =fis.getChannel();
			FileLock flock =lockfc.tryLock();
			if(flock==null)
			{
				System.out.println("run");
				bFlag=true;
			}
			else
			{
				flock.release();
			}
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return  bFlag;
	}
	private static void ExecThread() throws InterruptedException
	{
		File f =new File(".");
		String  s=f.getPath();
		String strFilepathString ="JG.lock";
		String strbatpath ="JGather.bat";
		while(true)
		{
			boolean isrun=isruning(strFilepathString);
			System.out.println("程序是否正在运行"+isrun);
			if(!isrun)
			{
				try {
					System.out.println("程序开始执行");
					Runtime.getRuntime().exec("cmd /k start "+strbatpath);
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			Thread.sleep(2*60000);
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			try {
				ExecThread();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	}

}
