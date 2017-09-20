package com.main;

import com.dh.DpsdkCore.fMediaDataCallback;

public class DPSDKMediaDataCallback implements fMediaDataCallback 
{
	public void invoke(int nPDLLHandle, int nSeq, int nMediaType, byte[] szNodeId, int nParamVal, byte[] szData, int nDataLen)
	{
		//System.out.printf("DPSDKMediaDataCallback");
		//System.out.println();
	}
}
