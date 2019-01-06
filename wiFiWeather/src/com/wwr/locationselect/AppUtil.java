package com.wwr.locationselect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.widget.Toast;

public class AppUtil {
	public static boolean Delfile(String file) {
		try {
			File f = new File(file);
			if (!f.exists()) {
				return false;
			} else {
				return f.delete();
			}

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	public static boolean fileIsExists(String fileurl) {
		try {
			File f = new File(fileurl);
			if (!f.exists()) {
				return false;
			}

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	public static byte[] read(InputStream in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = in.read(buffer)) != -1) {
			if (buffer == null) {
				break;
			}

			baos.write(buffer, 0, len);
			if (len != buffer.length) {
				break;
			}
		}
		baos.close();
		byte[] data = baos.toByteArray();
		return data;
	}

	public static ProgressDialog showProgress(Activity activity, String hintText) {
		Activity mActivity = null;
		if (activity.getParent() != null) {
			mActivity = activity.getParent();
			if (mActivity.getParent() != null) {
				mActivity = mActivity.getParent();
			}
		} else {
			mActivity = activity;
		}
		final Activity finalActivity = mActivity;
		ProgressDialog window = ProgressDialog
				.show(finalActivity, "", hintText);
		window.getWindow().setGravity(Gravity.CENTER);

		window.setCancelable(false);
		return window;
	}

	static public boolean isNetwork(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static JSONObject getJsonObject(JSONArray jsonArray, int index) {
		try {
			if (jsonArray != null && index >= 0 && index < jsonArray.length()) {
				return jsonArray.getJSONObject(index);
			}
		} catch (JSONException e) {
			return null;
		}
		return null;
	}
	public static String getJsonStringValue(JSONObject jsonObject, String key) {
		return getJsonStringValue(jsonObject, key, "");
	}

	public static String getJsonStringValue(JSONObject jsonObject, String key,
			String defaultValue) {
		try {
			if (jsonObject != null && jsonObject.has(key)) {
				String value = jsonObject.getString(key).trim();
				if (value.equals("null")) {
					value = "";
				}
				return value;
			}
		} catch (Exception e) {
			return defaultValue;
		}
		return defaultValue;
	}

	//
	public static int getJsonIntegerValue(JSONObject json, String key) {
		return getJsonIntegerValue(json, key, 0);
	}

	//
	public static int getJsonIntegerValue(JSONObject jsonObject, String key,
			int defaultValue) {
		try {
			if (jsonObject != null && jsonObject.has(key)) {
				return jsonObject.getInt(key);
			}
		} catch (Exception e) {
			return defaultValue;
		}
		return defaultValue;
	}

	//
	public static double getJsonDoubleValue(JSONObject json, String key) {
		return getJsonDoubleValue(json, key, 0);
	}

	//
	public static double getJsonDoubleValue(JSONObject jsonObject, String key,
			int defaultValue) {
		try {
			if (jsonObject != null && jsonObject.has(key)) {
				return jsonObject.getDouble(key);
			}
		} catch (Exception e) {
			return defaultValue;
		}
		return defaultValue;
	}

	public static Long getJsonLongValue(JSONObject json, String key) {
		return getJsonLongValue(json, key, 0L);
	}

	public static Long getJsonLongValue(JSONObject jsonObject, String key,
			Long defaultValue) {
		try {
			if (jsonObject != null && jsonObject.has(key)) {
				return jsonObject.getLong(key);
			}
		} catch (Exception e) {
			return defaultValue;
		}
		return defaultValue;
	}

	public static float getJsonFloatValue(JSONObject jsonObject, String key,
			float defaultValue) {
		try {
			if (jsonObject != null && jsonObject.has(key)) {
				return Float.valueOf(jsonObject.getString(key));
			}
		} catch (Exception e) {
			return defaultValue;
		}
		return defaultValue;
	}

	public static boolean getJsonBooleanValue(JSONObject jsonObject,
			String key, boolean defaultValue) {
		try {
			if (jsonObject != null && jsonObject.has(key)) {
				return jsonObject.getBoolean(key);
			}
		} catch (Exception e) {
			return defaultValue;
		}
		return defaultValue;
	}

	public static JSONObject getJsonObject(JSONObject jsonObject, String key) {
		try {
			if (jsonObject != null && jsonObject.has(key)) {
				return jsonObject.getJSONObject(key);
			}
		} catch (Exception e) {
			return new JSONObject();
		}
		return new JSONObject();
	}

	public static JSONArray getJsonArray(JSONObject jsonObject, String key) {
		try {
			if (jsonObject != null && jsonObject.has(key)) {
				return jsonObject.getJSONArray(key);
			}
		} catch (Exception e) {

			return new JSONArray();
		}
		return new JSONArray();
	}
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0
				|| str.trim().equals("null");
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static void showLongMessage(Context mContext, CharSequence text) {
		if (text != null && text.length() > 0) {
			Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
		}
	}
	public static void showShortMessage(Context mContext, CharSequence text) {
		if (text != null && text.length() > 0) {
			Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
		}
	}
	
}
