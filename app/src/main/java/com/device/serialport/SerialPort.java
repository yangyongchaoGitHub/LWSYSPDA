package com.device.serialport;

public final class SerialPort
{
    private static final int RETURNCODE_FAILURE = 1;
    private static final int RETURNCODE_SUCCESS = 0;
    public static final int IOCTRL_PMU_RFID_ON = 3;
    public static final int IOCTRL_PMU_RFID_OFF = 4;
    private static final int IOCTRL_PMU_BARCODE_ON = 5;
    private static final int IOCTRL_PMU_BARCODE_OFF = 6;
    static final int IOCTRL_PMU_RS232_ON = 23;
    static final int IOCTRL_PMU_RS232_OFF = 24;
    private static int device = 0;

    static
    {
        System.loadLibrary("serial_port_idata");
    }

    private static native int initFromJNI(int paramInt1, int paramInt2, int paramInt3);

    private static native int setTimeArg(int paramInt1, int paramInt2);

    public static native int ioctlFromJNI(int paramInt);

    public static native String getDevPath(int paramInt);

    private native int getReceiveArray(byte[] paramArrayOfByte, int paramInt);

    private native int writeByteArrFromJNI(byte[] paramArrayOfByte, int paramInt);

    private static native int exitFromJNI();

    public static int openSerial(int dev, int baud, int evenMode)
    {
        int hSerialRFID = -1;

        hSerialRFID = initFromJNI(dev, baud, evenMode);
        if (-1 == hSerialRFID) {
            return 1;
        }
        device = dev;

        setArg(1, 400);
        return 0;
    }

    public int read(byte[] caRecvData, int nByteSize)
    {
        return getReceiveArray(caRecvData, nByteSize);
    }

    public int read(byte[] caRecvData)
    {
        return getReceiveArray(caRecvData, 0);
    }

    public int write(byte[] baWriteData, int nByteSize)
    {
        return writeByteArrFromJNI(baWriteData, nByteSize);
    }

    public String getDevicePath(int device)
    {
        return getDevPath(device);
    }

    public static int setArg(int writeDelay, int firstReadWait)
    {
        return setTimeArg(writeDelay, firstReadWait);
    }

    public int SetPowerState(int controlcode)
    {
        return ioctlFromJNI(controlcode);
    }

    public static int closeSerial()
    {
        switch (device)
        {
            case 1:
                ioctlFromJNI(5);
                break;
            case 2:
                ioctlFromJNI(3);
                break;
            case 3:
                ioctlFromJNI(23);
                break;
        }
        exitFromJNI();
        return 0;
    }
}
