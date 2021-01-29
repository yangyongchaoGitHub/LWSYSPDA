package com.dataexpo.lwsyspda.rfid;

import android.annotation.SuppressLint;

public class EmshConstant {

	public static class Action {
		public static final String INTENT_EMSH_REQUEST 		= "android.intent.extra.EMSH_REQUEST";
		public static final String INTENT_EMSH_BROADCAST 	= "android.intent.extra.EMSH_STATUS";
	};

	public static class IntentExtra {
		public static final String EXTRA_COMMAND	= "cmd";
		public static final String EXTRA_PARAM_1	= "arg1";
		public static final String EXTRA_PARAM_2	= "arg2";
	};

	public static class Command {
		public static final String CMD_REQUEST_ENABLE_EMSH_SVC		= "emsh.REQUEST_ENABLE_EMSH_SERVICE";
		public static final String CMD_REFRESH_EMSH_STATUS			= "emsh.REFRESH_BATTERY_STATUS";
		public static final String CMD_REQUEST_SET_POWER_MODE		= "emsh.REQUEST_SET_POWER_MODE";
		public static final String CMD_REQUEST_ENABLE_UHF_COMM		= "emsh.REQUEST_ENABLE_UHF_COMM";

		// When Emsh Service stopped but want to use ttyMT2 & ttyMT3
		public static final String CMD_REQUEST_ENABLE_UART2_COMM	= "emsh.REQUEST_ENABLE_UART2_COMM";
		public static final String CMD_REQUEST_ENABLE_UART3_COMM	= "emsh.REQUEST_ENABLE_UART3_COMM";
	};

	public static class EmshSessionStatus {
		public static final int EMSH_STATUS_HARDWARE_ATTACH			= (1<<0);
		public static final int EMSH_STATUS_PROTOCOL_VERSION		= (1<<1);
		public static final int EMSH_STATUS_DEVICE_MODEL_NUMBER		= (1<<2);
		public static final int EMSH_STATUS_POWER_STATUS			= (1<<3);
		public static final int EMSH_STATUS_BATTERY_STATUS			= (1<<4);
		public static final int EMSH_STATUS_DSG_STATUS				= (1<<5);
	};

	public static class EmshBatteryPowerMode {
		public static final int EMSH_PWR_MODE_STANDBY			= 0x00;	// Standby
		public static final int EMSH_PWR_MODE_DSG_PDA			= 0x01;	// Discharge to PDA
		public static final int EMSH_PWR_MODE_DSG_UHF			= 0x02;	// Discharge to UHF module
		public static final int EMSH_PWR_MODE_CHG_GENERAL		= 0x03;	// General charge mode
		public static final int EMSH_PWR_MODE_CHG_QUICK			= 0x04;	// Quick charge mode
		public static final int EMSH_PWR_MODE_CHG_FULL			= 0x05;	// EMSH battery charge full
		public static final int EMSH_PWR_MODE_ABN_TEMPERATURE	= 0x06;	// EMSH battery abnormal temperature
		public static final int EMSH_PWR_MODE_BATTERY_LOW		= 0x07;	// EMSH battery capacity very low
		public static final int EMSH_PWR_MODE_BATTERY_ERROR		= 0x11;	// Something error
	};

	public static class EmshBatteryStatus {
		public int 		SessionStatus;				// the communication session status
		public int		HardwareStatus;				// 0/1, power state of PIN 12
		public int		ProtocolVersion;			// Protocol version
		public String DeviceModelNumber;			// Device Part Number and version
		public int		BatteryPowerMode;			// class EmshBatteryPowerMode
		public int		BatteryCapacityLevel;		// 0 ~ 4
		public int		BatteryCapacityPercent;		// 0 ~ 100, <= 0 means invalid
		public int		BatteryTemperatureStatus;	// Battery temperature status
		public int		BatteryTerminalVoltage;		// mV
		public int		BatteryDischargeVoltage;	// mV
		public int		BatteryDischargeCurrent;	// mA
		public long		LatestUpdateUnixTime;		// The number of seconds since Jan. 1, 1970 GMT.
	};

	@SuppressLint("DefaultLocale")
	public static String getPowerModeDesc(EmshBatteryStatus status )
	{
		if ( (status.SessionStatus & EmshSessionStatus.EMSH_STATUS_POWER_STATUS) != 0 )
		{
			int  nPowerMode = status.BatteryPowerMode;

			switch ( nPowerMode )
			{
				case EmshBatteryPowerMode.EMSH_PWR_MODE_STANDBY:
					return String.format("[0x%02x]%s", nPowerMode, "STANDBY_MODE");
				case EmshBatteryPowerMode.EMSH_PWR_MODE_DSG_PDA:
					return String.format("[0x%02x]%s", nPowerMode, "DSG_PDA_MODE");
				case EmshBatteryPowerMode.EMSH_PWR_MODE_DSG_UHF:
					return String.format("[0x%02x]%s", nPowerMode, "DSG_UHF_MODE");
				case EmshBatteryPowerMode.EMSH_PWR_MODE_CHG_GENERAL:
					return String.format("[0x%02x]%s", nPowerMode, "CHG_GENERAL_MODE");
				case EmshBatteryPowerMode.EMSH_PWR_MODE_CHG_QUICK:
					return String.format("[0x%02x]%s", nPowerMode, "CHG_QUICK_MODE");
				case EmshBatteryPowerMode.EMSH_PWR_MODE_CHG_FULL:
					return String.format("[0x%02x]%s", nPowerMode, "CHG_FULL_MODE");
				case EmshBatteryPowerMode.EMSH_PWR_MODE_ABN_TEMPERATURE:
					return String.format("[0x%02x]%s", nPowerMode, "ABN_TEMPERATURE");
				case EmshBatteryPowerMode.EMSH_PWR_MODE_BATTERY_LOW:
					return String.format("[0x%02x]%s", nPowerMode, "BATTERY_TOO_LOW");
				case EmshBatteryPowerMode.EMSH_PWR_MODE_BATTERY_ERROR:
					return String.format("[0x%02x]%s", nPowerMode, "BATTERY_ERROR");
				default:
					return String.format("[0x%02x]%s", nPowerMode, "UNKNOWN");
			}
		}

		return "";
	}

}
