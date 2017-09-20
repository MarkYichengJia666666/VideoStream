package com.main;

import com.dh.DpsdkCore.fDPSDKRemoteDeviceSnapCallback;

public class SnapCallback implements fDPSDKRemoteDeviceSnapCallback {
    int  nPDLLHandle ;
    byte[] szCameraId;
    byte[] szFullPath;
	@Override
	public void invoke(int nPDLLHandle, byte[] szCameraId, byte[] szFullPath) {
		// TODO Auto-generated method stub
		
	}

	public SnapCallback(int nPDLLHandle, byte[] szCameraId, byte[] szFullPath) {
		invoke(nPDLLHandle ,szCameraId ,szFullPath);
	}
    
    
}
