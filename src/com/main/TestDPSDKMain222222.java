/*
 * DPSDK��java��demo
 * ����ʱ�����޸�ip,�˿ڣ��û��������룬�޸�ͨ��ID���ο�����֯��XML�����ֲᡷ�͡������������ĵ�
 * ���޸�lib��native library·��
 * */

package com.main;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;



import java.util.Map;

import Decoder.BASE64Decoder;

import com.alibaba.fastjson.JSONObject;
import com.dh.DpsdkCore.*;
import com.dh.DpsdkCore.TvWall.Set_TvWall_Screen_Window_Source_t;
import com.dh.DpsdkCore.TvWall.TvWall_Layout_Info_t;
import com.dh.DpsdkCore.TvWall.TvWall_List_Info_t;
import com.dh.DpsdkCore.TvWall.TvWall_Screen_Close_Source_t;
import com.dh.DpsdkCore.TvWall.TvWall_Screen_Split_t;

public class TestDPSDKMain222222 
{
	public static int m_nDLLHandle = -1;
	public String   m_strAlarmCamareID = "1000175";    //�����豸ID
	public String   m_strRealCamareID = "1000175.1.0.0";    //ʵʱͨ��ID
	public String	m_strDownloadCamID = "1001615.1.0.0";	//����ͨ��ID
	
	//public String 	m_strIp 		= "60.191.94.121";   //��¼ƽ̨ip
	//public String 	m_strIp 		= "172.7.3.250";   //��¼ƽ̨ip
	//public int    	m_nPort 		= 9000;            //�˿�
	//public String 	m_strUser 		= "DPSDK";        //�û���
	//public String 	m_strPassword 	= "qwer1234";    //����
	
	//public String 	m_strIp 		= "192.168.9.236";   //��¼ƽ̨ip
	public String 	m_strIp 		= "222.173.147.202";   //��¼ƽ̨ip
	public int    	m_nPort 		= 9000;            //�˿�
	public String 	m_strUser 		= "dss0605";        //�û���
	public String 	m_strPassword 	= "dahua2017";    //����
	
	Return_Value_Info_t nGroupLen = new Return_Value_Info_t();
	
	public String   m_strQueryAlarmCamareID = "1000011.1.0.0";    //�����豸ID
	public String	m_strNVRDeviceID = "1001687";
	public String	davPath = "D:\\downoladtest.dav";
	FileOutputStream writer = null;
	public static int nDownloadSeq = 0;
	
	public static String StrCarNum;
	
	DPSDKAlarmCallback   m_AlarmCB = new DPSDKAlarmCallback();
	
	DPSDKMediaDataCallback  m_MediaCB = new DPSDKMediaDataCallback();
	DPSDKMediaDataCallback  m_MediaDownloadCB = new DPSDKMediaDataCallback(){
		@Override
		public void invoke(int nPDLLHandle, int nSeq, int nMediaType, byte[] szNodeId, int nParamVal, byte[] szData, int nDataLen)
		{
			if(nMediaType == 2 && nDataLen == 0)
			{ //¼�����ؽ���,���̵߳���ֹͣ¼�񣬷���ӿڻᳬʱ
				nDownloadSeq = nSeq;
				Thread t = new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {  
				     		if(writer != null){
				     			writer.flush();
				     			writer.close();
				     			writer = null;
				    		}
				        } catch (IOException e) {  
				            e.printStackTrace();  
				        } 
						
						int nRet = IDpsdkCore.DPSDK_CloseRecordStreamBySeq( m_nDLLHandle, nDownloadSeq, 10000);
						if(nRet == 0){
							nDownloadSeq = -1;
						}
						System.out.printf("���ؽ�����ֹͣ����nRet = %d", nRet);
						System.out.println();
					}
					
				});
				t.start();
			}
			try {  
				if(davPath != null){
					if(writer == null)
					{
						writer = new FileOutputStream(davPath,true);
				   	}
					if(nDataLen>0){
						writer.write(szData);    
					}
				}
			} catch (IOException e) {  
				e.printStackTrace();  
			}  
		}
	};
	public String   m_strTvWallSourceCamareID = "1000175.1.0.0";    //�����豸ID
	
	public fDPSDKDevStatusCallback fDeviceStatus = new fDPSDKDevStatusCallback() {
		@Override
		public void invoke(int nPDLLHandle, byte[] szDeviceId, int nStatus) {
			String status = "����";
			if(nStatus == 1)
			{
				status = "����";
				Device_Info_Ex_t deviceInfo = new Device_Info_Ex_t();
				int nRet = IDpsdkCore.DPSDK_GetDeviceInfoExById(m_nDLLHandle, szDeviceId, deviceInfo);
				if(deviceInfo.nDevType == dpsdk_dev_type_e.DEV_TYPE_NVR || deviceInfo.nDevType == dpsdk_dev_type_e.DEV_TYPE_EVS || deviceInfo.nDevType == dpsdk_dev_type_e.DEV_TYPE_SMART_NVR || deviceInfo.nDevType == dpsdk_dev_type_e.DEV_MATRIX_NVR6000)
				{
					nRet = IDpsdkCore.DPSDK_QueryNVRChnlStatus(m_nDLLHandle, szDeviceId, 10*1000);
					
					if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
					{
						//System.out.printf("��ѯNVRͨ��״̬�ɹ���deviceID = %s", new String(szDeviceId));
					}else
					{
						System.out.printf("��ѯNVRͨ��״̬ʧ�ܣ�deviceID = %s, nRet = %d", new String(szDeviceId), nRet);
					}
					//System.out.println();
				}
			}
			//System.out.printf("Device Status Report!, szDeviceId = %s, nStatus = %s", new String(szDeviceId),status);
			//System.out.println();
		}
	};
	
	public fDPSDKNVRChnlStatusCallback fNVRChnlStatus = new fDPSDKNVRChnlStatusCallback() {
		@Override
		public void invoke(int nPDLLHandle, byte[] szCameraId, int nStatus) {
			String status = "����";
			if(nStatus == 1)
			{
				status = "����";
			}
			//System.out.printf("NVR Channel Status Report!, szCameraId = %s, nStatus = %s", new String(szCameraId),status);
			//System.out.println();
		}
	};
	
	public fDPSDKGeneralJsonTransportCallback fGeneralJson = new fDPSDKGeneralJsonTransportCallback() {
		@Override
		public void invoke(int nPDLLHandle, byte[] szJson)  {
			String string = new String(szJson);
			Map m = JSONObject.parseObject(string, Map.class);
			Map params = (Map)m.get("params");
			System.out.printf("ץͼ���أ�General Json Return, ReturnJson = %s", string);
			System.out.println("sss��"+params.get("PicInfo").toString());
			TestDPSDKMain222222.GenerateImage(params.get("PicInfo").toString());
			System.out.println("helloworld");
		}
	};

	
	
	 public static boolean GenerateImage(String imgStr)
	    {//���ֽ������ַ�������Base64���벢����ͼƬ
	        if (imgStr == null) //ͼ������Ϊ��
	            return false;
	        BASE64Decoder decoder = new BASE64Decoder();
	        try 
	        {
	            //Base64����
	            byte[] b = decoder.decodeBuffer(imgStr);
	            for(int i=0;i<b.length;++i)
	            {
	                if(b[i]<0)
	                {//�����쳣����
	                    b[i]+=256;
	                }
	            }
	            //����jpegͼƬ
	            String imgFilePath = "d:\\999.jpg";//�����ɵ�ͼƬ
	            OutputStream out = new FileOutputStream(imgFilePath);    
	            out.write(b);
	            out.flush();
	            out.close();
	            return true;
	        } 
	        catch (Exception e) 
	        {
	            return false;
	        }
	    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * ����DPSDK
	 * */
	public void OnCreate()
	{
		int nRet = -1;
		Return_Value_Info_t res = new Return_Value_Info_t();
		nRet =IDpsdkCore.DPSDK_Create(dpsdk_sdk_type_e.DPSDK_CORE_SDK_SERVER,res);
		
		m_nDLLHandle = res.nReturnValue;
		String dpsdklog = "D:\\dpsdkjavalog";
		nRet = IDpsdkCore.DPSDK_SetLog(m_nDLLHandle, dpsdklog.getBytes());
		String dumpfile = "D:\\dpsdkjavadump";
		nRet = IDpsdkCore.DPSDK_StartMonitor(m_nDLLHandle, dumpfile.getBytes());
		if(m_nDLLHandle > 0)
		{
			//�����豸״̬�ϱ���������
			nRet = IDpsdkCore.DPSDK_SetDPSDKDeviceStatusCallback(m_nDLLHandle, fDeviceStatus);
			//����NVRͨ��״̬�ϱ���������
			nRet =IDpsdkCore.DPSDK_SetDPSDKNVRChnlStatusCallback(m_nDLLHandle, fNVRChnlStatus);
			//����ͨ��JSON�ص�
			nRet = IDpsdkCore.DPSDK_SetGeneralJsonTransportCallback(m_nDLLHandle, fGeneralJson);
			
			//nRet = IDpsdkCore.DPSDK_SetDPSDKGetBayCarInfoCallbackEx(m_nDLLHandle, fBayCarInfo);
		}
		
		System.out.print("����DPSDK, ���� m_nDLLHandle = ");
		System.out.println(m_nDLLHandle);
	}
	
	/*
	 * ��¼
	 * */
	public void OnLogin()
	{
		Login_Info_t loginInfo = new Login_Info_t();
		loginInfo.szIp = m_strIp.getBytes();
		loginInfo.nPort = m_nPort;
		loginInfo.szUsername = m_strUser.getBytes();
		loginInfo.szPassword = m_strPassword.getBytes();
		loginInfo.nProtocol = dpsdk_protocol_version_e.DPSDK_PROTOCOL_VERSION_II;
		loginInfo.iType = 1;

		int nRet = IDpsdkCore.DPSDK_Login(m_nDLLHandle,loginInfo,10000);
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("��¼�ɹ���nRet = %d", nRet);
		}else
		{
			System.out.printf("��¼ʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
	}
	
	/*
	 * ����������֯��
	 * */
	public void LoadAllGroup()
	{
		int nRet = IDpsdkCore.DPSDK_LoadDGroupInfo(m_nDLLHandle, nGroupLen, 180000 );
		
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("����������֯���ɹ���nRet = %d�� nDepCount = %d", nRet, nGroupLen.nReturnValue);
		}else
		{
			System.out.printf("����������֯��ʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
	}
	
	/*
	 * ��ȡ������֯����
	 * */
	public void GetGroupStr()
	{
		byte[] szGroupBuf = new byte[nGroupLen.nReturnValue];
		int nRet = IDpsdkCore.DPSDK_GetDGroupStr(m_nDLLHandle, szGroupBuf, nGroupLen.nReturnValue, 10000);
		
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			String GroupBuf = "";
			try {
				GroupBuf = new String(szGroupBuf, "UTF-8");
			} catch (IOException e) {  
            e.printStackTrace();  
			} 
			System.out.printf("��ȡ������֯�����ɹ���nRet = %d�� szGroupBuf = [%s]", nRet, GroupBuf);
			try {
				File file = new File("D:\\text.xml");
				if(!file.exists())
				{
					file.createNewFile();	
				}
				
				FileOutputStream  out = new FileOutputStream(file);
				out.write(szGroupBuf);
				out.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else
		{
			System.out.printf("��ȡ������֯����ʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
	}
	
	
	
	
	/*
	 * ��ѯNVR�豸��ͨ��״̬
	 * */
	public void QureyNVRChannelStatus()
	{
		Device_Info_Ex_t deviceInfo = new Device_Info_Ex_t();
		int nRet = IDpsdkCore.DPSDK_GetDeviceInfoExById(m_nDLLHandle, m_strNVRDeviceID.getBytes(),deviceInfo);
		if(deviceInfo.nDevType == dpsdk_dev_type_e.DEV_TYPE_NVR || deviceInfo.nDevType == dpsdk_dev_type_e.DEV_TYPE_EVS || deviceInfo.nDevType == dpsdk_dev_type_e.DEV_TYPE_SMART_NVR || deviceInfo.nDevType == dpsdk_dev_type_e.DEV_MATRIX_NVR6000)
		{
			nRet = IDpsdkCore.DPSDK_QueryNVRChnlStatus(m_nDLLHandle, m_strNVRDeviceID.getBytes(), 10*1000);
			
			if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
			{
				System.out.printf("��ѯNVRͨ��״̬�ɹ���nRet = %d", nRet);
			}else
			{
				System.out.printf("��ѯNVRͨ��״̬ʧ�ܣ�nRet = %d", nRet);
			}
			System.out.println();
		}
	}
	
	/*
	 * ������֯��
	 * */
	public void LoadGroup()
	{
		String strCoding="001";
		Load_Dep_Info_t depInfo = new Load_Dep_Info_t();
		depInfo.nOperation = dpsdk_getgroup_operation_e.DPSDK_CORE_GEN_GETGROUP_OPERATION_CHILD;
		depInfo.szCoding=strCoding.getBytes();
		Return_Value_Info_t nLen = new Return_Value_Info_t();
		int nRet = IDpsdkCore.DPSDK_LoadDGroupInfoLayered(m_nDLLHandle,depInfo,nLen,10000);
		
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("������֯���ɹ���nRet = %d�� nGroupLen = %d", nRet,nLen.nReturnValue);
		}else
		{
			System.out.printf("������֯��ʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
		
		Get_Dep_Count_Info_t depCountInfo = new Get_Dep_Count_Info_t();
		depCountInfo.szCoding=strCoding.getBytes();
		nRet = IDpsdkCore.DPSDK_GetDGroupCount(m_nDLLHandle,depCountInfo);
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("��ȡ����֯���豸�����ɹ���nRet = %d�� nDepCount = %d, nDeviceCount= %d", nRet,depCountInfo.nDepCount,depCountInfo.nDeviceCount);
		}else
		{
			System.out.printf("��ȡ����֯���豸ʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
		
	}
	
	int GetReal()
	{
		Return_Value_Info_t nRealSeq = new Return_Value_Info_t();
		Get_RealStream_Info_t getInfo = new Get_RealStream_Info_t();
		getInfo.szCameraId = m_strRealCamareID.getBytes();
		getInfo.nStreamType = dpsdk_stream_type_e.DPSDK_CORE_STREAMTYPE_MAIN;
		getInfo.nRight = dpsdk_check_right_e.DPSDK_CORE_NOT_CHECK_RIGHT; //�����Ȩ�ޣ�������Ƶ�������������֯�ṹ
		getInfo.nMediaType = dpsdk_media_type_e.DPSDK_CORE_MEDIATYPE_VIDEO;
		getInfo.nTransType = dpsdk_trans_type_e.DPSDK_CORE_TRANSTYPE_TCP;
		
		int nRet = IDpsdkCore.DPSDK_GetRealStream(m_nDLLHandle, nRealSeq, getInfo, m_MediaCB, 10000);
		
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("��ʵʱ��Ƶ�ɹ���nRet = %d�� nSeq = %d", nRet, nRealSeq.nReturnValue);
		}else
		{
			System.out.printf("��ʵʱ��Ƶʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
			return nRealSeq.nReturnValue;
		else
			return -1;
	}
	
	void CloseReal(int nRealSeq)
	{
		int nRet = IDpsdkCore.DPSDK_CloseRealStreamBySeq(m_nDLLHandle, nRealSeq, 10000);
		
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("�ر�ʵʱ��Ƶ�ɹ���nRet = %d�� nSeq = %d", nRet, nRealSeq);
		}else
		{
			System.out.printf("�ر�ʵʱ��Ƶʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
	}
	
	void GetExternUrl()
	{
		Get_ExternalRealStreamUrl_Info_t pExternalRealStreamUrlInfo = new Get_ExternalRealStreamUrl_Info_t();
		pExternalRealStreamUrlInfo.szCameraId = m_strRealCamareID.getBytes();
		pExternalRealStreamUrlInfo.nMediaType = 1;
		pExternalRealStreamUrlInfo.nStreamType = 1;
		pExternalRealStreamUrlInfo.nTrackId = 8011;
		pExternalRealStreamUrlInfo.nTransType = 1;
		pExternalRealStreamUrlInfo.bUsedVCS = 0;
		pExternalRealStreamUrlInfo.nVcsbps = 0;
		pExternalRealStreamUrlInfo.nVcsfps = 0;
		pExternalRealStreamUrlInfo.nVcsResolution = 0;
		pExternalRealStreamUrlInfo.nVcsVideocodec = 0;
		int nRet = IDpsdkCore.DPSDK_GetExternalRealStreamUrl(m_nDLLHandle, pExternalRealStreamUrlInfo, 10000);
		
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.println(new String(pExternalRealStreamUrlInfo.szUrl).trim());
		}else
		{
			System.out.printf("��ȡURLʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
	}
	
	

	
	
	
	

	
	public String GetStringAsUTF8(byte[] data)
	{
		String str = "";
		try {
			str = new String(data, "UTF-8").trim();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	
	
	public void BlindShots()
	{
		String szJson = "{ \"method\":\"dev.snap\",\"params\":{\"DevID\":\"1000175\",\"DevChannel\":0,\"PicNum\":1,\"SnapType\":2,\"CmdSrc\":0},\"id\":1000175 }";
		int mdltype = dpsdk_mdl_type_e.DPSDK_MDL_DMS;
		int trantype = generaljson_trantype_e.GENERALJSON_TRAN_REQUEST;
		
		int nRet = IDpsdkCore.DPSDK_GeneralJsonTransport(m_nDLLHandle, szJson.getBytes(), mdltype, trantype, 30*1000);
		
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("DPSDK_GeneralJsonTransport:�ɹ���nRet = %d", nRet);
		}else
		{
			System.out.printf("DPSDK_GeneralJsonTransport:ʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
	}
	

	/*
	 * �ǳ�
	 * */
	public void OnLogout()
	{
		int nRet = IDpsdkCore.DPSDK_Logout(m_nDLLHandle, 10000);
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("�ǳ��ɹ���nRet = %d", nRet);
		}else
		{
			System.out.printf("�ǳ�ʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
	}
	
	/*
	 * �ͷ��ڴ�
	 * */
	public void OnDestroy()
	{
		int nRet = IDpsdkCore.DPSDK_Destroy(m_nDLLHandle);
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("�ͷ��ڴ�ɹ���nRet = %d", nRet);
		}else
		{
			System.out.printf("�ͷ��ڴ�ʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
	}
	
	public void run()
	{
		Menu menu = new Menu();
		menu.Run();
	}
	


	
	public static void main(String[] args) 
	{
		TestDPSDKMain222222 app=new TestDPSDKMain222222();
		app.OnCreate();//��ʼ��
		app.OnLogin();//��½
		app.LoadAllGroup();//������֯�ṹ
		
		app.GetGroupStr();//��ȡ��֯�ṹ��
		
		//������֯�ṹ֮��Ҫ��ʱ3�������ң��ȴ��������ģ��ȡ����ϵ
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){
			e.printStackTrace();
		}	


		app.GetExternUrl();//��ȡ��ƵURL
		app.BlindShots();//��Ԥ��ץͼ
		int nRealSeq = app.GetReal();//����ʵʱ�������ɹ�����Ƶ��������m_MediaCB������
		app.run();
		if(nRealSeq > 0)
		{
			app.CloseReal(nRealSeq);//�ر�ʵʱ����
		}
		app.OnLogout();
		app.OnDestroy();
	}

	
}
