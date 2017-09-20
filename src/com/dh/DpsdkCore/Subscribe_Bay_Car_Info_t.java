package com.dh.DpsdkCore;

//璁㈤槄鍗″彛杩囪溅(鎴栧尯闂存祴閫�淇℃伅璇锋眰淇℃伅
public class Subscribe_Bay_Car_Info_t {
	public int							nChnlCount;									// 璁㈤槄閫氶亾鐨勬暟閲�
	public Enc_Channel_Info_t[]			pEncChannelnfo;								// 閫氶亾淇℃伅
	public int							nSubscribeFlag;								// 璁㈤槄鏍囪銆�:鍙栨秷璁㈤槄锛�锛氳闃�
	
	public Subscribe_Bay_Car_Info_t(int nMaxCount)
	{
		nChnlCount = nMaxCount;
		pEncChannelnfo = new Enc_Channel_Info_t[nChnlCount];
		for(int i = 0; i < nChnlCount; i++)
		{
			pEncChannelnfo[i] = new Enc_Channel_Info_t();
		}
	}
}