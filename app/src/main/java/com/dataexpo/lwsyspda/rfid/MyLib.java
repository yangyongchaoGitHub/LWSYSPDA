package com.dataexpo.lwsyspda.rfid;


import com.device.serialport.SerialPort;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.dataexpo.lwsyspda.MyApplication;
import com.example.scarx.idcardreader.SimpleInterface;
import com.jnitool.UHFAndIDCardControl;
import com.uhf.api.cls.JniModuleAPI;
import com.uhf.api.cls.Reader;

import static com.dataexpo.lwsyspda.MyApplication.currentDeviceName;


/**
 * 工具类，此类用于操作UHF模块的各种功能
 * <p>Author CYD</p>
 * <p>Date 2020/5/28</p>
 * <p>Email jszc@idatachina.com</p>
 * <p>verion 2.3.1</p>
 * <p>注意：</p>
 * <p>1.此API与Demo适用SLR1200和SLR5100模块,使用前请确认您的机器已安装此模块</p>
 * <p>2.此API与Demo适用于95W4GUHF,95VV5UHF,95V4GUHF,50V2R2UHF,70UHF,50UHF,50PUHF设备</p>
 * <p>3.使用模块需要放置libModuleAPIJni.so,libserial_port_idata.so和libSimpleJni.so文件 ,以及ModuleAPI_J.jar文件</p>
 * <p>4.创建指定包名com.device.serialport并复制SerialPort类，创建指定包名com.example.scarx.idcardreader并复制SimpleInterface类</p>
 */
public class MyLib {

    public static String A5_Device = "A5VR2V100"; //A5就是50设备
    public static String V4G_95Device = "KBA2KV100";
    private String V5_95VDevice = "KB172V100";
    private String W4G_95Device = "95VR2V100";
    private String YF_70Device = "YFA7V100";
    private String VR2_70Device = "70VR2V100";
    private String NX2_A5V1Device = "KB3A5V100";
    public static final String A5P_Device = "50S-V01-R01";//A5P
    public static final String A5P_ComBaseLin_Device = "50V400R001";//A5P共基线版本

    private int cmd = 1;//jni使用的默认命令值
    private int ant[] = {1};//天线组，默认为天线只有1个
    private int option = 0;//默认为0，设置附加数据之后为32768
    private short maxPower = 3000; //最大功率值
    private Reader.READER_ERR operate_success = Reader.READER_ERR.MT_OK_ERR; //表示操作成功的状态
    private int defaultCmd = 1; //默认天线一
    private short defaultTime = 1000; //超时时间
    private Reader mReader;
    private JniModuleAPI jniModuleAPI;


    private MyLib() {
        mReader = MyApplication.getMyApp().getReader();
        jniModuleAPI = MyApplication.getMyApp().getJniModuleAPI();
    }

    /**
     * 获取当前MyLib类的单例对象
     *
     * @return MyLib的对象
     */
    public static MyLib getInstance() {
        // return new MyLib();
        return MySingleton.instance;
    }

    static class MySingleton {
        static final MyLib instance = new MyLib();
    }

    /**
     * 上电操作
     *
     * @return true成功，false失败
     */
    public boolean powerOn() {
     //   MLog.e(currentDeviceName);
        if (TextUtils.isEmpty(currentDeviceName))
            return false;
        String path = "";
        if (currentDeviceName.equals(A5_Device) || currentDeviceName.equals(NX2_A5V1Device)) { //50设备
            path = "/dev/ttyMT2";
            //上电
            enableUartComm_UHF(true);
            setPowerState_UHF(true);
        } else if (currentDeviceName.equals(V5_95VDevice) || currentDeviceName.equals(V4G_95Device)) {
            //  注意：需要先执行getDevPath()获取设备路径，才能使用上电操作
            path = SerialPort.getDevPath(2); //获取设备挂载路径
            int values = SerialPort.ioctlFromJNI(3);//IO口上电
            if (values != 0) {
                return false;
            }
        } else if (currentDeviceName.equals(W4G_95Device)) {
            if (SimpleInterface.IOCTL_UHF_POWER_ON()) {
                path = "/dev/ttyMT2";
            }
        } else if (currentDeviceName.equals(YF_70Device) || currentDeviceName.equals(VR2_70Device)) {
            UHFAndIDCardControl.openMainPower();
            UHFAndIDCardControl.UHFPowOn();
            path = "/dev/ttyMT1";
        } else if (currentDeviceName.equals(A5P_Device) || currentDeviceName.equals(A5P_ComBaseLin_Device)) {
            path = "/dev/ttyS1";
            enableUartComm_UHF(true);
            setPowerState_UHF(true);
        }
        //特别注意，此时初始化挂载了设备之后， mReader.hReader[0] =1,后续使用mReader的一些操作命令都会用到这个值
        return mReader.InitReader_Notype(path, 1) == operate_success; //设置挂载设备地址
    }

    /**
     * 下电
     *
     * @return true成功，false失败
     */
    public boolean powerOff() {
        if (TextUtils.isEmpty(currentDeviceName))
            return false;
        //   mReader.CloseReader();
        if (currentDeviceName.equals(A5_Device) || currentDeviceName.equals(A5P_Device) || currentDeviceName.equals(A5P_ComBaseLin_Device)) { //50设备
            enableUartComm_UHF(false);
            setPowerState_UHF(false);
            return true;
        } else if (currentDeviceName.equals(V5_95VDevice)) {
            return SerialPort.ioctlFromJNI(4) == 0;
        } else if (currentDeviceName.equals(W4G_95Device)) {
            return SimpleInterface.IOCTL_UHF_POWER_OFF();
        } else if (currentDeviceName.equals(currentDeviceName.equals(YF_70Device) || currentDeviceName.equals(VR2_70Device))) {
            UHFAndIDCardControl.UHFPowOff();
            UHFAndIDCardControl.closeMainPower();
            return true;
        }
        return false;
    }

    private void enableUartComm_UHF(boolean bEnable) {
        Intent intent = new Intent(EmshConstant.Action.INTENT_EMSH_REQUEST);
        intent.putExtra(EmshConstant.IntentExtra.EXTRA_COMMAND, EmshConstant.Command.CMD_REQUEST_ENABLE_UHF_COMM);
        intent.putExtra(EmshConstant.IntentExtra.EXTRA_PARAM_1, (bEnable ? 1 : 0));
        MyApplication.getMyApp().sendBroadcast(intent);
    }

    private void setPowerState_UHF(boolean bPowerOn) {
        Intent intent = new Intent(EmshConstant.Action.INTENT_EMSH_REQUEST);
        intent.putExtra(EmshConstant.IntentExtra.EXTRA_COMMAND, EmshConstant.Command.CMD_REQUEST_SET_POWER_MODE);
        intent.putExtra(EmshConstant.IntentExtra.EXTRA_PARAM_1, (bPowerOn ? EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_DSG_UHF : EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_STANDBY));
        MyApplication.getMyApp().sendBroadcast(intent);
    }


    /**
     * (仅1200模块支持，且仅适用于快速盘点)
     * 开启快速盘点
     *
     * @return true成功，false失败
     */
    public boolean asyncStartReading() {
        int val = jniModuleAPI.AsyncStartReading(cmd, ant, ant.length, option);
        return val == 0;
    }

    /**
     * (仅1200模块支持，且仅适用于快速盘点)
     * 获取此次快速盘点获取到的标签数量
     *
     * @param rcvData 传入数组并赋值，rcvData[0]为此次获取到的标签数量
     * @return true成功，false失败
     */
    public boolean asyncGetTagCount(int[] rcvData) {
        return mReader.AsyncGetTagCount(rcvData) == operate_success;
    }

    /**
     * (仅1200模块支持，且仅适用于快速盘点)
     * 获取快速盘点的标签数据，并赋值到实体类
     *
     * @param temp 用于接收具体数据实体类
     * @return true成功，false失败 <br/>
     * <pre id="codeUse">
     *     <b>快速盘点用法完整示例:</b>
     *     private Handler myHandler = new Handler();
     *     MyLib.getInstance().powerOn(); //上电
     *     MyLib.getInstance().setAdditionalData(1);//设置附加数据，0不返回附加数据，1返回TID，2返回RSSI(可选操作)
     *     MyLib.getInstance().asyncStartReading();
     *     myHandler.postDelayed(runnable_MainActivitys, 0);
     *     // 标签盘点操作
     *     private Runnable runnable_MainActivitys = new Runnable() {
     *     <code>@Override</code>
     *     public void run(){
     *         int[] rcvData = new int[] { 0 };
     *         boolean flag = false;
     *         flag = MyLib.getInstance().asyncGetTagCount(rcvData);
     *         if (flag) {
     *            if (rcvData[0] > 0) {
     *               for (int i = 0; i < rcvData[0]; i++) {
     *                   Reader.TAGINFO temp = MyApp.getMyApp().getReader().new TAGINFO();
     *                   flag = MyLib.getInstance().asyncGetNextTag(temp);
     *                   if (flag) {
     *                      String epc = Reader.bytes_Hexstr(temp.EpcId); // EPC的hex字符串
     *                      String embededData=Reader.bytes_Hexstr(temp.EmbededData);   // 附加数据，默认设置为TID
     *                      int rssi = temp.RSSI;//	RSSI数据
     *                      if (embededData.length() == 256) { // 数据过滤
     *                         embededData = embededData.substring(0, 24);
     *                      }
     *                      MLog.e("epc1111 = " + epc + " tid = " + embededData + " rssi = " + rssi);//输出日志的操作(测试使用，非必需)
     *                   }
     *               }
     *            }
     *         }
     *         myHandler.post(runnable_MainActivitys);
     *       }
     *     };
     * </pre>
     */
    public boolean asyncGetNextTag(Reader.TAGINFO temp) {
        return mReader.AsyncGetNextTag(temp) == operate_success;
    }

    /**
     * (仅1200模块支持，且仅适用于快速盘点)
     * 停止快速盘点
     *
     * @return true成功，false失败
     */
    public boolean asyncStopReading() {
        return jniModuleAPI.AsyncStopReading(cmd) == 0;
    }

    /**
     * 开启普通标签盘点，获取此次盘点的标签数量
     *
     * @param rcvData 传入数组，用于接收数据
     * @return true成功，false失败
     */
    public boolean tagInventory_Raw(int[] rcvData) {
        return mReader.TagInventory_Raw(ant, ant.length, (short) 50, rcvData) == operate_success;
    }

    /**
     * 获取普通盘点的标签数据，并赋值到实体类
     *
     * @param temp 用于接收具体数据实体类
     * @return true成功，false失败 <br/>
     * <pre id="codeUse">
     *     <b>普通盘点用法完整示例:</b>
     *     MyLib.getInstance().powerOn();//上电
     *     int[] rcvData = new int[] { 0 };
     *     if (MyLib.getInstance().tagInventory_Raw(rcvData)) {
     *          if (rcvData[0] > 0) {
     *              for (int i = 0; i < rcvData[0]; i++) {
     *                  Reader.TAGINFO temp = MyApp.getMyApp().getReader().new TAGINFO();
     *                  if (MyLib.getInstance().getNextTag(temp)) {
     *                      String epc = Reader.bytes_Hexstr(temp.EpcId); // EPC的hex字符串
     *                      String embededData = Reader.bytes_Hexstr(temp.EmbededData); // TID数据
     *                      int rssi = temp.RSSI;// RSSI数据
     *                      if(embededData.length() == 256){// 数据过滤
     *                          embededData = embededData.substring(0, 24);
     *                      }
     *                      MLog.e("epc1111 = " + epc + " tid = " + embededData + " rssi = " + rssi);//输出日志的操作(测试使用，非必需)
     *                  }
     *              }
     *          }
     *     }
     * </pre>
     */
    public boolean getNextTag(Reader.TAGINFO temp) {
        return mReader.GetNextTag(temp) == operate_success;
    }

    /**
     * 停止普通标签盘点
     *
     * @return true成功，false失败
     */
    public boolean stopTagReading() {
        return mReader.StopReading() == operate_success;
    }


    /**
     * 设置附加数据返回(仅1200模块支持，且仅适用于快速盘点)
     *
     * @param flag 设置0为不返回附加数据，1为返回TID数据，2为返回RSSI数据
     * @return true成功，false失败
     */
    public boolean setAdditionalData(int flag) {
        switch (flag) {
            case 0:
                option = 0;
                return true;
            case 1:
                option = 32768;
                Reader.EmbededData_ST edst = mReader.new EmbededData_ST();
                edst.accesspwd = null;
                edst.bank = 2;
                edst.startaddr = 0;
                edst.bytecnt = 12;
                Reader.READER_ERR er = mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, edst);
                return er == operate_success;
            case 2:
                int metaflag = 0;
                metaflag |= 0X0002;
                option = (metaflag << 8) | 0;
                return true;
        }
        return false;
    }

    /**
     * 获取UHF模块的读写功率
     *
     * @return short[]长度为2，下标0为读功率，下标1为写功率
     */
    public short[] getPower() {
        Reader.AntPowerConf apcf = mReader.new AntPowerConf();
        Reader.READER_ERR er = mReader.ParamGet(Reader.Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf);
        short st[] = null;
        if (er == operate_success) {
            st = new short[2];
            st[0] = apcf.Powers[0].readPower; //获取当前读功率
            st[1] = apcf.Powers[0].writePower; //获取当前写功率
        }
        return st;
    }

    /**
     * 设置UHF模块读写功率
     *
     * @param readPower  读功率
     * @param writePower 写功率
     * @return true成功，false失败
     */
    public boolean setPower(short readPower, short writePower) {
        Reader.AntPowerConf apcf = mReader.new AntPowerConf();
        apcf.antcnt = 1;
        Reader.AntPower jaap = mReader.new AntPower();
        jaap.antid = 1;
        jaap.readPower = readPower;
        jaap.writePower = writePower;
        apcf.Powers[0] = jaap;
        Reader.READER_ERR er = mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf);
        return er == operate_success;
    }

    /**
     * 设置区域频率
     *
     * @param frequency 对应区域, 0:中国,1:北美,2:韩国,3:欧洲,4:印度,5:加拿大
     * @return true成功，false失败
     */
    public boolean setFrequency(int frequency) {
        Reader.Region_Conf rre = null;
        switch (frequency) {
            case 0:
                rre = Reader.Region_Conf.RG_PRC;
                break;
            case 1:
                rre = Reader.Region_Conf.RG_NA;
                break;
            case 2:
                rre = Reader.Region_Conf.RG_EU3;
                break;

        }
        Reader.READER_ERR er = mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rre);
        return er == operate_success;
    }

    /**
     * 获取区域频率
     *
     * @return 对应区域，0:中国,1:北美,2:韩国,3:欧洲,4:印度,5:加拿大
     */
    public int getFrequency() {
        Reader.Region_Conf[] rcf2 = new Reader.Region_Conf[1];
        Reader.READER_ERR er = mReader.ParamGet(Reader.Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rcf2);
        int value = -1;
        if (rcf2[0].value() < 0) {
            return value;
        }
        if (er == Reader.READER_ERR.MT_OK_ERR) {
            switch (rcf2[0]) {
                case RG_PRC:
                    value = 0;
                    break;
                case RG_NA:
                    value = 1;
                    break;
                case RG_EU3:
                    value = 2;
                    break;
            }
        }
        return value;
    }

    /**
     * 设置指定频率频点(需要先设置区域)
     *
     * @param value 频点值
     * @return true成功，false失败
     */
    public boolean setFrequencyChannel(int value) {
        //设置指定频率
        int[] vls = new int[]{value};
        Reader.HoptableData_ST hdst = mReader.new HoptableData_ST();
        hdst.lenhtb = vls.length;
        hdst.htb = vls;
        Reader.READER_ERR er = mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst);
        return er == operate_success;
    }

    /**
     * (仅1200模块支持)
     * 设置快速盘点模式
     *
     * @param value 0为s0模式且为最大功率,1为s1模式且为最大功率
     */
    public boolean setFastMode(int value) {
        setPower(maxPower, maxPower);//设置读写最大功率
        Reader.READER_ERR er = mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, new int[]{value});
        return er == operate_success;
    }

    /**
     * 设置过滤
     * Reader.TagFilter_ST 结构同com.szyd.util.TagFilter_ST
     *
     * @param tfst 过滤用的实体类
     * @return true成功，false失败
     */
    public boolean setFilter(Reader.TagFilter_ST tfst) {
        Reader.READER_ERR er = mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_FILTER, tfst);
        return er == operate_success;
    }

    /**
     * 获取过滤条件
     * Reader.TagFilter_ST 结构同com.szyd.util.TagFilter_ST
     *
     * @return 返回TagFilter_ST实体对象，null则失败
     */
    public Reader.TagFilter_ST getFilter() {
        Reader.TagFilter_ST tfst2 = mReader.new TagFilter_ST();
        Reader.READER_ERR er = mReader.ParamGet(Reader.Mtr_Param.MTR_PARAM_TAG_FILTER, tfst2);
        return er == operate_success ? tfst2 : null;
    }

    /**
     * 读标签
     *
     * @param bank       操作区域，0:保留区,1:EPC区,2:TID区,3:USER区
     * @param startBlock 读取的起始块(word类型)
     * @param len        需要的块长度
     * @param rcvData    接收用的byte数据
     * @param pwd        密码
     * @return true成功，false失败 <br/>
     * <img src="image/epc.jpg" width="100%" height="40%" />
     * <pre id="codeUse">
     *  <b>读标签EPC区用法示例:</b>
     *  char bank =1;
     *  int startBlocks = 2;
     *  int len =6;
     *  byte [] rcvData = new byte[len*2];
     *  byte [] pwd = new byte[4] ;
     *  boolean result = readTag(bank,startBlocks,len,rcvData,pwd);
     * </pre>
     */
    public boolean readTag(char bank, int startBlock, int len, byte[] rcvData, byte[] pwd) {
        Reader.READER_ERR er = mReader.GetTagData(defaultCmd, bank, startBlock, len, rcvData, pwd, defaultTime); //获取指定区域数据
        return er == operate_success;
    }

    /**
     * 写标签
     *
     * @param bank       操作区域，0:保留区,1:EPC区,2:TID区,3:USER区
     * @param startBlock 读取的起始块(word类型)
     * @param data       过滤用的数据
     * @param pwd        密码
     * @return true成功，false失败<br/>
     * <img src="image/epc.jpg" width="100%" height="40%" />
     * <pre id="codeUse">
     *  <b>写标签EPC区用法示例:</b>
     *  char bank =1;
     *  int  startBlocks = 2;
     *  byte [] filterData = {...}; //需要写入的数据(hex转byte[])
     *  byte [] pwd = new byte[4] ;
     *  boolean result = writeTag(bank,startBlocks,data,pwd);
     * </pre>
     */
    public boolean writeTag(char bank, int startBlock, byte[] data, byte[] pwd) {
        Reader.READER_ERR er = mReader.WriteTagData(defaultCmd, bank, startBlock, data, data.length, pwd, defaultTime); //向指定区域写数据
        return er == operate_success;
    }

    /**
     * 锁定或者解锁标签
     *
     * @param bank       操作区域，0:访问密码,1:销毁密码,2:EPC区,3:TID区,4:USER区
     * @param opeateType 操作类型，0:解锁定,1:暂时锁定,2:永久锁定
     * @param pwd        密码
     * @return true成功，false失败 <br/>
     * <img src="image/reserved.jpg" width="100%" height="40%" />
     * <pre id="codeUse">
     * <b>锁标签示例:</b>
     * byte[] pwd = {0x66,0x66,0x66,0x66};
     * byte[] defalutPwd = new byte[4];
     * writeTag(0,2,pwd,defalutPwd); //为空白标签时，Reserved区写入AccessPassword进行初始化
     * lockTag(2,1,pwd); //暂时锁EPC区
     * </pre>
     */
    public boolean lockTag(int bank, int opeateType, byte[] pwd) {
        Reader.Lock_Obj lobj = null;
        Reader.Lock_Type ltyp = null;
        if (bank == 0) {
            lobj = Reader.Lock_Obj.LOCK_OBJECT_ACCESS_PASSWD;
            if (opeateType == 0)
                ltyp = Reader.Lock_Type.ACCESS_PASSWD_UNLOCK;
            else if (opeateType == 1)
                ltyp = Reader.Lock_Type.ACCESS_PASSWD_LOCK;
            else if (opeateType == 2)
                ltyp = Reader.Lock_Type.ACCESS_PASSWD_PERM_LOCK;

        } else if (bank == 1) {
            lobj = Reader.Lock_Obj.LOCK_OBJECT_KILL_PASSWORD;
            if (opeateType == 0)
                ltyp = Reader.Lock_Type.KILL_PASSWORD_UNLOCK;
            else if (opeateType == 1)
                ltyp = Reader.Lock_Type.KILL_PASSWORD_LOCK;
            else if (opeateType == 2)
                ltyp = Reader.Lock_Type.KILL_PASSWORD_PERM_LOCK;
        } else if (bank == 2) {
            lobj = Reader.Lock_Obj.LOCK_OBJECT_BANK1;
            if (opeateType == 0)
                ltyp = Reader.Lock_Type.BANK1_UNLOCK;
            else if (opeateType == 1)
                ltyp = Reader.Lock_Type.BANK1_LOCK;
            else if (opeateType == 2)
                ltyp = Reader.Lock_Type.BANK1_PERM_LOCK;
        } else if (bank == 3) {
            lobj = Reader.Lock_Obj.LOCK_OBJECT_BANK2;
            if (opeateType == 0)
                ltyp = Reader.Lock_Type.BANK2_UNLOCK;
            else if (opeateType == 1)
                ltyp = Reader.Lock_Type.BANK2_LOCK;
            else if (opeateType == 2)
                ltyp = Reader.Lock_Type.BANK2_PERM_LOCK;
        } else if (bank == 4) {
            lobj = Reader.Lock_Obj.LOCK_OBJECT_BANK3;
            if (opeateType == 0)
                ltyp = Reader.Lock_Type.BANK3_UNLOCK;
            else if (opeateType == 1)
                ltyp = Reader.Lock_Type.BANK3_LOCK;
            else if (opeateType == 2)
                ltyp = Reader.Lock_Type.BANK3_PERM_LOCK;
        }
        Reader.READER_ERR er = mReader.LockTag(defaultCmd, (byte) lobj.value(), (short) ltyp.value(), pwd, defaultTime);
        return er == operate_success;
    }

    /**
     * 销毁标签
     *
     * @param pwd 密码
     * @return true成功，false失败
     */
    public boolean killTag(byte[] pwd) {
        Reader.READER_ERR er = mReader.KillTag(defaultCmd, pwd, defaultTime);
        return er == operate_success;
    }
}
