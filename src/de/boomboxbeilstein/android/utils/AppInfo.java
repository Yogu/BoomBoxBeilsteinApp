package de.boomboxbeilstein.android.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class AppInfo {
	private static Integer version = null;
	private static String fullVersion = null;
	
	public static int getVersion(Context context) {
		if (version == null) {
			PackageInfo pInfo;
			try {
				pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			} catch (NameNotFoundException e) {
				throw new RuntimeException(e);
			}
			version = pInfo.versionCode;
		}
		return version;
	}
	
	public static int getVersion() {
		return (version == null) ? -1 : version;
	}
	
	public static String getFullVersion(Context context) {
		if (fullVersion == null) {
			PackageInfo pInfo;
			try {
				pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			} catch (NameNotFoundException e) {
				throw new RuntimeException(e);
			}
			fullVersion = String.format("%d (%s)", pInfo.versionCode, pInfo.versionName);
		}
		return fullVersion;
	}
	
	public static String getFullVersion() {
		if (fullVersion != null)
			return fullVersion;
		else if (version != null)
			return version + "";
		else
			return "";
	}
}
