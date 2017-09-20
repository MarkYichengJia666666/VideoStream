/*
 * DPSDK��java��demo
 * ����ʱ�����޸�ip,�˿ڣ��û��������룬�޸�ͨ��ID���ο�����֯��XML�����ֲᡷ�͡������������ĵ�
 * ���޸�lib��native library·��
 * */

package com.main;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;

import com.dh.DpsdkCore.*;
import com.dh.DpsdkCore.TvWall.Set_TvWall_Screen_Window_Source_t;
import com.dh.DpsdkCore.TvWall.TvWall_Layout_Info_t;
import com.dh.DpsdkCore.TvWall.TvWall_List_Info_t;
import com.dh.DpsdkCore.TvWall.TvWall_Screen_Close_Source_t;
import com.dh.DpsdkCore.TvWall.TvWall_Screen_Split_t;

public class TestDPSDKMain 
{
	public static int m_nDLLHandle = -1;
	public String   m_strAlarmCamareID = "1001341";    //�����豸ID
	public String   m_strRealCamareID = "1001341$1$0$0";    //ʵʱͨ��ID
	public String	m_strDownloadCamID = "1001615$1$0$0";	//����ͨ��ID
	
	//public String 	m_strIp 		= "60.191.94.121";   //��¼ƽ̨ip
	//public String 	m_strIp 		= "172.7.3.250";   //��¼ƽ̨ip
	//public int    	m_nPort 		= 9000;            //�˿�
	//public String 	m_strUser 		= "DPSDK";        //�û���
	//public String 	m_strPassword 	= "qwer1234";    //����
	
	public String 	m_strIp 		= "192.168.9.236";   //��¼ƽ̨ip
	public int    	m_nPort 		= 9000;            //�˿�
	public String 	m_strUser 		= "cuiduadmin";        //�û���
	public String 	m_strPassword 	= "cd123456";    //����
	
	Return_Value_Info_t nGroupLen = new Return_Value_Info_t();
	
	public String   m_strQueryAlarmCamareID = "1000011$1$0$0";    //�����豸ID
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
	public String   m_strTvWallSourceCamareID = "1001341$1$0$0";    //�����豸ID
	
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
		public void invoke(int nPDLLHandle, byte[] szJson) {
			System.out.printf("General Json Return, ReturnJson = %s", new String(szJson));
			System.out.println("             helloworld");
		}
	};
	//���ڹ������ݻص�
	public fDPSDKGetBayCarInfoCallbackEx fBayCarInfo = new fDPSDKGetBayCarInfoCallbackEx() {
		@Override
		public void invoke(int nPDLLHandle, byte[] szDeviceId, int nDeviceIdLen, int nDevChnId, byte[] szChannelId, int nChannelIdLen, byte[] szDeviceName, int	nDeviceNameLen, byte[] szDeviceChnName, int	nChanNameLen, byte[] szCarNum, int nCarNumLen, int	nCarNumType, int nCarNumColor, int nCarSpeed,int nCarType, int	nCarColor, int	nCarLen, int	nCarDirect, int	nWayId, long lCaptureTime, long lPicGroupStoreID, int nIsNeedStore, int nIsStoraged, byte[] szCaptureOrg, int nCaptureOrgLen, byte[] szOptOrg, int nOptOrgLen, byte[] szOptUser, int nOptUserLen, byte[] szOptNote, int nOptNoteLen, byte[] szImg0Path, int nImg0PathLen, byte[] szImg1Path, int nImg1PathLen, byte[] szImg2Path, int nImg2PathLen, byte[] szImg3Path, int nImg3PathLen, byte[] szImg4Path, int nImg4PathLen, byte[] szImg5Path, int nImg5PathLen, byte[] szImgPlatePath, int nImgPlatePathLen, int icarLog, int iPlateLeft, int iPlateRight, int iPlateTop, int iPlateBottom) {
			try {
					StrCarNum = new String(szCarNum, "UTF-8");
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        } 
			System.out.printf("Bay Car Info Report, DeviceId=%s, szChannelId=%s, szDeviceChnName=%s, szCarNum=%s, szImg0Path=%s", new String(szDeviceId), new String(szChannelId), new String(szDeviceChnName), StrCarNum, new String(szImg0Path));
			System.out.println();
		}
	};
	
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
			
			nRet = IDpsdkCore.DPSDK_SetDPSDKGetBayCarInfoCallbackEx(m_nDLLHandle, fBayCarInfo);
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
		//System.out.println();
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
				
				FileOutputStream out = new FileOutputStream(file);
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
	
	public void GetGPSXML()
	{
		Return_Value_Info_t nGpsXMLLen = new Return_Value_Info_t();
		int nRet = IDpsdkCore.DPSDK_AskForLastGpsStatusXMLStrCount(m_nDLLHandle, nGpsXMLLen, 10*1000);
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS && nGpsXMLLen.nReturnValue > 0)
		{
			byte[] LastGpsIStatus = new byte[nGpsXMLLen.nReturnValue - 1];
			nRet = IDpsdkCore.DPSDK_AskForLastGpsStatusXMLStr(m_nDLLHandle, LastGpsIStatus, nGpsXMLLen.nReturnValue);
			
			if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
			{
				System.out.printf("��ȡGPS XML�ɹ���nRet = %d�� LastGpsIStatus = [%s]", nRet, new String(LastGpsIStatus));
				try {
					File file = new File("D:\\GPS.xml");
					if(!file.exists())
					{
						file.createNewFile();	
					}
					
					FileOutputStream  out = new FileOutputStream(file);
					out.write(LastGpsIStatus);
					out.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}else
			{
				System.out.printf("��ȡGPS XMLʧ�ܣ�nRet = %d", nRet);
			}
		}else if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS && nGpsXMLLen.nReturnValue == 0)
		{
			System.out.printf("��ȡGPS XML  XMLlength = 0");
		}
		else
		{
			System.out.printf("��ȡGPS XMLʧ�ܣ�nRet = %d", nRet);
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
	
	
	/*
	 * ��������
	 * */
	public void SetAlarm()
	{
		int nRet = IDpsdkCore.DPSDK_SetDPSDKAlarmCallback(m_nDLLHandle,m_AlarmCB);//���ñ�����������
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("�������������ɹ���nRet = %d", nRet);
		}else
		{
			System.out.printf("������������ʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
		
		//Alarm_Enable_Info_t alarmInfo = new Alarm_Enable_Info_t(1);
		//alarmInfo.sources[0].szAlarmDevId = m_strAlarmCamareID.getBytes();
		//alarmInfo.sources[0].nVideoNo = 0;
		//alarmInfo.sources[0].nAlarmInput = 0;
		//alarmInfo.sources[0].nAlarmType = dpsdk_alarm_type_e.DPSDK_CORE_ALARM_TYPE_VIDEO_LOST;
		//int nRet =  IDpsdkCore.DPSDK_EnableAlarm(m_nDLLHandle, alarmInfo,10000);
		
		//if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		//{
		//	System.out.printf("�������سɹ���nRet = %d", nRet);
		//}else
		//{
		//	System.out.printf("��������ʧ�ܣ�nRet = %d", nRet);
		//}
		//System.out.println();
	}
	
	/*
	 * ������ѯ
	 * */
	public void OnQueryAlarm()
	{
		Date tmStart = new Date(2015-1900,6-1,3,0,0,0);
		Date tmEnd = new Date(2015-1900,6-1,3,12,0,0);
		
		//System.out.printf("%s",tmStart.toLocaleString());
		//System.out.println();
		//System.out.printf("%s",tmEnd.toLocaleString());
		//System.out.println();
		Alarm_Query_Info_t stuQueryInfo = new Alarm_Query_Info_t();
		stuQueryInfo.szCameraId = m_strQueryAlarmCamareID.getBytes();
		stuQueryInfo.nAlarmType = dpsdk_alarm_type_e.DPSDK_CORE_ALARM_TYPE_VIDEO_SHELTER;
		stuQueryInfo.uStartTime = tmStart.getTime()/1000;//ת������
		stuQueryInfo.uEndTime = tmEnd.getTime()/1000;
		
		//System.out.printf("��ѯ����ʱ�䣺%d - %d",stuQueryInfo.uStartTime, stuQueryInfo.uEndTime );
		//System.out.println();
		
		Return_Value_Info_t nCount = new Return_Value_Info_t();
		int nRet = IDpsdkCore.DPSDK_QueryAlarmCount(m_nDLLHandle, stuQueryInfo, nCount, 10000);
		
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("��ѯ���������ɹ���nRet = %d�� nCount= %d", nRet, nCount.nReturnValue);
		}else
		{
			System.out.printf("��ѯ��������ʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
		
		if (nCount.nReturnValue > 0)
		{
			Alarm_Info_t stuAlarmInfo = new Alarm_Info_t(nCount.nReturnValue);
			
			nRet = IDpsdkCore.DPSDK_QueryAlarmInfo(m_nDLLHandle, stuQueryInfo, stuAlarmInfo, 0, nCount.nReturnValue,10000);
			if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
			{
				System.out.printf("��ѯ������Ϣ�ɹ���nRet = %d�� nCount= %d", nRet, stuAlarmInfo.nRetCount);
				for(int i=0;i<stuAlarmInfo.nRetCount;i++)
				{
					System.out.println();
					Date dTime = new Date(stuAlarmInfo.pAlarmInfo[i].uAlarmTime * 1000);
					System.out.printf("���=%d������ = %d��ʱ��=%s���¼�=%d���豸ID=%s��ͨ����=%d�� ����״̬= %d", i+1,
							stuAlarmInfo.pAlarmInfo[i].nAlarmType,dTime.toLocaleString(), stuAlarmInfo.pAlarmInfo[i].nEventType,
							new String(stuAlarmInfo.pAlarmInfo[i].szDevId).trim(),stuAlarmInfo.pAlarmInfo[i].uChannel, stuAlarmInfo.pAlarmInfo[i].nDealWith);
				}
			}else
			{
				System.out.printf("��ѯ������Ϣʧ�ܣ�nRet = %d", nRet);
			}
			System.out.println();
		}
	}
	
	public void OnSendOSDInfo()
	{
		String strDeviceId = new String("1000001");
		String strMsg = new String("qqq"); 
		Send_OSDInfo_t stuSendOSDInfo = new Send_OSDInfo_t();
		stuSendOSDInfo.szDevId = strDeviceId.getBytes();
		stuSendOSDInfo.szMessage = strMsg.getBytes();
		int nRet = IDpsdkCore.DPSDK_SendOSDInfo(m_nDLLHandle, stuSendOSDInfo, 10000);
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("����OSD��Ϣ�ɹ���nRet = %d", nRet);
		}else
		{
			System.out.printf("����OSD��Ϣʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
	}
	
	public void OnGetTvWallList()
	{
		Return_Value_Info_t nCount = new Return_Value_Info_t();
		int nRet = IDpsdkCore.DPSDK_GetTvWallListCount(m_nDLLHandle, nCount, 10000);
		
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("��ѯ����ǽ�б������ɹ���nRet = %d�� nCount= %d", nRet, nCount.nReturnValue);
		}else
		{
			System.out.printf("��ѯ����ǽ�б�����ʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
		
		int nCurTvWallId = -1;
		if (nCount.nReturnValue > 0)
		{
			TvWall_List_Info_t pTvWallListInfo = new TvWall_List_Info_t(nCount.nReturnValue);
			
			nRet = IDpsdkCore.DPSDK_GetTvWallList(m_nDLLHandle, pTvWallListInfo);
			if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
			{
				System.out.printf("��ȡ����ǽ�б���Ϣ�ɹ���nRet = %d�� nCount= %d", nRet, pTvWallListInfo.nCount);
				for(int i=0;i<pTvWallListInfo.nCount;i++)
				{
					System.out.println();
					System.out.printf("���=%d��nTvWallId = %d��nState=%s��szName=%s", i+1,pTvWallListInfo.pTvWallInfo[i].nTvWallId,pTvWallListInfo.pTvWallInfo[i].nState,
							new String(pTvWallListInfo.pTvWallInfo[i].szName).trim());
				
					if(i==0)
					{
						nCurTvWallId = pTvWallListInfo.pTvWallInfo[i].nTvWallId;
					}
				}
			}else
			{
				System.out.printf("��ȡ����ǽ�б���Ϣʧ�ܣ�nRet = %d", nRet);
			}
			System.out.println();
		}
		if(nCurTvWallId > 0)
			GetTvWallLayout(nCurTvWallId);
		
	}
	
	public void GetTvWallLayout(int nTvWallId)
	{
		System.out.printf("nTvWallId=%d;",nTvWallId);
		Return_Value_Info_t nCount = new Return_Value_Info_t();
		int nRet = IDpsdkCore.DPSDK_GetTvWallLayoutCount(m_nDLLHandle, nTvWallId, nCount, 10000);
		
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("��ѯ����ǽ���ֳɹ���nRet = %d�� nCount= %d", nRet, nCount.nReturnValue);
		}else
		{
			System.out.printf("��ѯ����ǽ����ʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
		
		int nScreenId = -1;
		if (nCount.nReturnValue > 0)
		{
			TvWall_Layout_Info_t pTvWallLayoutInfo = new TvWall_Layout_Info_t(nCount.nReturnValue);
			pTvWallLayoutInfo.nTvWallId = nTvWallId;
			
			nRet = IDpsdkCore.DPSDK_GetTvWallLayout(m_nDLLHandle, pTvWallLayoutInfo);
			if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
			{
				System.out.printf("��ȡ����ǽ������Ϣ�ɹ���nRet = %d�� nCount= %d", nRet, pTvWallLayoutInfo.nCount);
				for(int i=0;i<pTvWallLayoutInfo.nCount;i++)
				{
					if(i == 0)
					{
						nScreenId = pTvWallLayoutInfo.pScreenInfo[i].nScreenId;
					}
					System.out.println();
					System.out.printf("���=%d��nScreenId = %d��szName=%s, szDecoderId=%s,fLeft=%f,fTop=%f,fWidth=%f,fHeight=%f,bBind=%d", i+1,pTvWallLayoutInfo.pScreenInfo[i].nScreenId,new String(pTvWallLayoutInfo.pScreenInfo[i].szName).trim(),
							new String(pTvWallLayoutInfo.pScreenInfo[i].szDecoderId).trim(), pTvWallLayoutInfo.pScreenInfo[i].fLeft, pTvWallLayoutInfo.pScreenInfo[i].fTop,
							pTvWallLayoutInfo.pScreenInfo[i].fWidth, pTvWallLayoutInfo.pScreenInfo[i].fHeight, pTvWallLayoutInfo.pScreenInfo[i].bBind?1:0);
				}
			}else
			{
				System.out.printf("��ȡ����ǽ������Ϣʧ�ܣ�nRet = %d", nRet);
			}
			System.out.println();
		}
		
		if(nScreenId > 0)
		{
			{
				TvWall_Screen_Split_t  pInfo = new TvWall_Screen_Split_t();
				pInfo.nTvWallId = nTvWallId;
				pInfo.nScreenId = nScreenId;
				pInfo.enSplitNum = tvwall_screen_split_caps.Screen_Split_4;
				
				nRet = IDpsdkCore.DPSDK_SetTvWallScreenSplit(m_nDLLHandle, pInfo,1000);
				if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
				{
					System.out.printf("�ָ�ڳɹ���nRet = %d", nRet);
				}else
				{
					System.out.printf("�ָ��ʧ�ܣ�nRet = %d", nRet);
				}
				System.out.println();
			}
			SetWndSource(nTvWallId, nScreenId, 0);
		}
	}
	
	void SetWndSource(int nTvWallId, int nScreenId, int nWndId)
	{
		{
			Set_TvWall_Screen_Window_Source_t pInfo = new Set_TvWall_Screen_Window_Source_t();
			pInfo.nTvWallId = nTvWallId;
			pInfo.nScreenId = nScreenId;
			pInfo.nWindowId = nWndId;
			pInfo.szCameraId = m_strTvWallSourceCamareID.getBytes();
			pInfo.enStreamType = dpsdk_stream_type_e.DPSDK_CORE_STREAMTYPE_MAIN;
			pInfo.nStayTime = 30;
			
			int nRet = IDpsdkCore.DPSDK_SetTvWallScreenWindowSource(m_nDLLHandle, pInfo,1000);
			if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
			{
				System.out.printf("������ƵԴ�ɹ���nRet = %d", nRet);
			}else
			{
				System.out.printf("������ƵԴʧ�ܣ�nRet = %d", nRet);
			}
			System.out.println();
		}
		
		{
			TvWall_Screen_Close_Source_t pInfo = new TvWall_Screen_Close_Source_t();
			pInfo.nTvWallId = nTvWallId;
			pInfo.nScreenId = nScreenId;
			pInfo.nWindowId = nWndId;
			int nRet = IDpsdkCore.DPSDK_CloseTvWallScreenWindowSource(m_nDLLHandle, pInfo,1000);
			if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
			{
				System.out.printf("�ر���ƵԴ�ɹ���nRet = %d", nRet);
			}else
			{
				System.out.printf("�ر���ƵԴʧ�ܣ�nRet = %d", nRet);
			}
			System.out.println();
		}
		
		{
			int nRet = IDpsdkCore.DPSDK_ClearTvWallScreen(m_nDLLHandle, nTvWallId, 1000);
			if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
			{
				System.out.printf("�����ɹ���nRet = %d", nRet);
			}else
			{
				System.out.printf("����ʧ�ܣ�nRet = %d", nRet);
			}
			System.out.println();
		}
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
	
	public void OnSetDoorCmd()
	{
		SetDoorCmd_Request_t pInfo = new SetDoorCmd_Request_t();
		String strDeviceID = "1000000$4$0$0";    //�豸ID
		pInfo.szCameraId = strDeviceID.getBytes();
		pInfo.cmd = dpsdk_SetDoorCmd_e.DPSDK_CORE_DOOR_CMD_ALWAYS_OPEN;
		pInfo.start = 10;
		pInfo.end = 110;
		
		int nRet = IDpsdkCore.DPSDK_SetDoorCmd(m_nDLLHandle, pInfo, 10000);
		
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("DPSDK_SetDoorCmd:�ɹ���nRet = %d", nRet);
		}else
		{
			System.out.printf("DPSDK_SetDoorCmd:ʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
	}
	
	public void OnGetLinkResource()
	{
		Return_Value_Info_t nCount = new Return_Value_Info_t();
		int nRet = IDpsdkCore.DPSDK_QueryLinkResource(m_nDLLHandle, nCount, 10000);
		
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("DPSDK_QueryLinkResource�ɹ���nRet = %d�� nCount= %d", nRet, nCount.nReturnValue);
		}else
		{
			System.out.printf("DPSDK_QueryLinkResourceʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
		
		if (nCount.nReturnValue > 0)
		{
			GetLinkResource_Responce_t pResponse = new GetLinkResource_Responce_t(nCount.nReturnValue);
			
			nRet = IDpsdkCore.DPSDK_GetLinkResource(m_nDLLHandle, pResponse);
			if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
			{
				System.out.printf("DPSDK_GetLinkResource�ɹ���nRet = %d�� nCount= %d", nRet, pResponse.nLen);
				System.out.println();
				System.out.printf("pXmlData=%s", new String(pResponse.pXmlData).trim());
				
			}else
			{
				System.out.printf("DPSDK_GetLinkResourceʧ�ܣ�nRet = %d", nRet);
			}
			System.out.println();
		}
	}
	
	public void BlindShots()
	{
		String szJson = "{ \"method\":\"dev.snap\",\"params\":{\"DevID\":\"1001341\",\"DevChannel\":0,\"PicNum\":1,\"SnapType\":2,\"CmdSrc\":1},\"id\":42 }";
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
	
	
	
	public int StartDownLoadRecordByTime(long begintime, long endtime)
	{
		Query_Record_Info_t queryInfo = new Query_Record_Info_t();
		Return_Value_Info_t nRecordCount = new Return_Value_Info_t();
		queryInfo.szCameraId = m_strDownloadCamID.getBytes();
		queryInfo.nRecordType = dpsdk_record_type_e.DPSDK_CORE_PB_RECORD_UNKONWN;//����ģʽ
		queryInfo.nRight = dpsdk_check_right_e.DPSDK_CORE_NOT_CHECK_RIGHT; //�����Ȩ�ޣ�������Ƶ�������������֯�ṹ
		queryInfo.nSource = 2;//�豸¼��
		queryInfo.uBeginTime = begintime;//ת������;
		queryInfo.uEndTime = endtime;
		int nRet = IDpsdkCore.DPSDK_QueryRecord( m_nDLLHandle, queryInfo, nRecordCount, 60*1000);
		
		if(nRet != 0)
		{
			System.out.printf("¼���ѯʧ�ܣ�nRet= %d", nRet);
			System.out.println();
			return -1;
		}
		
		if(nRecordCount.nReturnValue == 0)
		{
			System.out.printf("û��¼�񣡣�������");
			System.out.println();
			return -1;
		}
		
		Return_Value_Info_t nDownLoadSeq = new Return_Value_Info_t();
		Get_RecordStream_Time_Info_t getInfo = new Get_RecordStream_Time_Info_t();
		getInfo.szCameraId = m_strDownloadCamID.getBytes();
		getInfo.nMode = 2;//����ģʽ
		getInfo.nRight = dpsdk_check_right_e.DPSDK_CORE_NOT_CHECK_RIGHT; //�����Ȩ�ޣ�������Ƶ�������������֯�ṹ
		getInfo.nSource = 2;//�豸¼��
		
		System.out.printf("��ʼ¼������   begintime = %d�� endtime = %d", begintime, endtime);
		getInfo.uBeginTime = begintime;//ת������;
		getInfo.uEndTime = endtime;
		
		nRet = IDpsdkCore.DPSDK_GetRecordStreamByTime( m_nDLLHandle, nDownLoadSeq, getInfo, m_MediaDownloadCB, 10000);
		
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("��ʼ¼�����سɹ���nRet = %d�� nSeq = %d", nRet, nDownLoadSeq.nReturnValue);
		}else
		{
			System.out.printf("��ʼ¼������ʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
			return nDownLoadSeq.nReturnValue;
		else
			return -1;
	}
	
	public void StopDownLoadRecordByTime(int nDownloadSeq)
	{
		int nRet = IDpsdkCore.DPSDK_CloseRecordStreamBySeq(m_nDLLHandle, nDownloadSeq, 10000);
		
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			//�ر��ļ�
			try {  
	     		if(writer != null){
	     			writer.flush();
	     			writer.close();
	     			writer = null;
	    		}
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        } 
			
			System.out.printf("ֹͣ���سɹ���nRet = %d�� nSeq = %d", nRet, nDownloadSeq);
		}else
		{
			System.out.printf("ֹͣ����ʧ�ܣ�nRet = %d", nRet);
		}
		System.out.println();
	}
	
	public void SubscribeALLBayCarInfo()
	{
		Subscribe_Bay_Car_Info_t pGetInfo = new Subscribe_Bay_Car_Info_t(1);
		pGetInfo.nSubscribeFlag = 1;
		pGetInfo.nChnlCount = 0;
		int nRet = IDpsdkCore.DPSDK_SubscribeBayCarInfo(m_nDLLHandle, pGetInfo, 5000);
		if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{	
			System.out.printf("�������п��ڹ�����Ϣ�ɹ���nRet = %d", nRet);
		}else
		{
			System.out.printf("�������п��ڹ�����Ϣʧ�ܣ�nRet = %d", nRet);
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
	
	//ץͼ
	public void snapPicture(){
		
		String cameraId = "1001341";
		String path = "E://";
		int callback = IDpsdkCore.DPSDK_SetDPSDKRemoteDeviceSnapCallback(m_nDLLHandle , new SnapCallback(m_nDLLHandle,cameraId.getBytes(),path.getBytes()));
		if(callback == dpsdk_retval_e.DPSDK_RET_SUCCESS)
		{
			System.out.printf("DPSDK_GeneralJsonTransport:ץͼ�ɹ���nRet = %d", callback);
		}else
		{
			System.out.printf("DPSDK_GeneralJsonTransport:ץͼʧ�ܣ�nRet = %d", callback);
		}
	}
	

	
	public static void main(String[] args) 
	{
		TestDPSDKMain app=new TestDPSDKMain();
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
		//app.snapPicture();
		//�������п��ڹ�����Ϣ
		//app.SubscribeALLBayCarInfo();
		
		/*
		//��ʱ������¼�����ص�dav�ļ������ô󻪹������ڵ�SmartPlayer����������
		@SuppressWarnings("deprecation")
		Date tmStart = new Date(2016-1900,9-1,29,11,57,0);
		@SuppressWarnings("deprecation")
		Date tmEnd = new Date(2016-1900,9-1,29,11,57,20);
		nDownloadSeq = app.StartDownLoadRecordByTime(tmStart.getTime()/1000, tmEnd.getTime()/1000);
		
		//int DownloadSeq = app.StartDownLoadRecordByTime(1475121300, 1475121420);
		try{
			Thread.sleep(180*1000);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		
		if(nDownloadSeq > 0)
		{
			app.StopDownLoadRecordByTime(nDownloadSeq);
		}
		*/
		
		//app.GetGPSXML(); //��ȡgpsXML
		
		//app.SetAlarm();//�򿪱�������,������֯�ṹ����ܽ��յ�������Ϣ
		//app.OnQueryAlarm();//��ѯ������Ϣ
		//app.OnSendOSDInfo();//������ͷ������Ϣ
		//app.OnGetTvWallList();//��ȡ����ǽ�б�
		app.GetExternUrl();//��ȡ��ƵURL
		app.BlindShots();//��Ԥ��ץͼ
		//app.snapPicture();
		//�Ž����ܽӿ� begin
		//app.OnGetLinkResource();//��ȡ�Ž��󶨵���ƵԴ��Ϣ
		//app.OnSetDoorCmd();//Զ�̿���
		//�Ž����ܽӿ� end
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
