// Credits: http://android-er.blogspot.in/2010/01/convert-exif-gps-info-to-degree-format.html

package com.app.spicit;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.util.Log;

public class GeoDecoder implements LogUtils {
	private boolean valid = false;
	private String TAG = "SpickIt> GeoDecoder";
	Float Latitude, Longitude;

	public GeoDecoder(ExifInterface exif) {
		String attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
		String attrLATITUDE_REF = exif
				.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
		String attrLONGITUDE = exif
				.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
		String attrLONGITUDE_REF = exif
				.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

		if ((attrLATITUDE != null) && (attrLATITUDE_REF != null)
				&& (attrLONGITUDE != null) && (attrLONGITUDE_REF != null)) {
			valid = true;

			if (attrLATITUDE_REF.equals("N")) {
				Latitude = convertToDegree(attrLATITUDE);
			} else {
				Latitude = 0 - convertToDegree(attrLATITUDE);
			}

			if (attrLONGITUDE_REF.equals("E")) {
				Longitude = convertToDegree(attrLONGITUDE);
			} else {
				Longitude = 0 - convertToDegree(attrLONGITUDE);
			}

		}
	};

	private Float convertToDegree(String stringDMS) {
		Float result = null;
		String[] DMS = stringDMS.split(",", 3);

		String[] stringD = DMS[0].split("/", 2);
		Double D0 = new Double(stringD[0]);
		Double D1 = new Double(stringD[1]);
		Double FloatD = D0 / D1;

		String[] stringM = DMS[1].split("/", 2);
		Double M0 = new Double(stringM[0]);
		Double M1 = new Double(stringM[1]);
		Double FloatM = M0 / M1;

		String[] stringS = DMS[2].split("/", 2);
		Double S0 = new Double(stringS[0]);
		Double S1 = new Double(stringS[1]);
		Double FloatS = S0 / S1;

		result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

		return result;

	};

	public boolean isValid() {
		return valid;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return (String.valueOf(Latitude) + ", " + String.valueOf(Longitude));
	}

	public int getLatitudeE6() {
		return (int) (Latitude * 1000000);
	}

	public int getLongitudeE6() {
		return (int) (Longitude * 1000000);
	}

	public double getLat() {
		return Latitude;
	}

	public double getLong() {
		return Longitude;
	}

	public List<Address> getAddress(Context context) throws IOException {
		Geocoder geocoder;
		List<Address> addresses = null;
		geocoder = new Geocoder(context, Locale.getDefault());
		try {
			addresses = geocoder.getFromLocation(getLat(), getLong(), 1);
			if (addresses.size() <= 0) {
				return addresses;
			}
			String address = addresses.get(0).getAddressLine(0);
			String city = addresses.get(0).getAddressLine(1);
			String country = addresses.get(0).getAddressLine(2);
			String local = addresses.get(0).getLocality();
			if (DEBUG)
				Log.i(TAG, address + " - " + city + " - " + country + " - "
						+ local);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}

		return addresses;
	}

	public String getCompleteAddress(Context context) {
		Geocoder geocoder;
		List<Address> addresses = null;
		String completeAdress = null;
		geocoder = new Geocoder(context, Locale.getDefault());
		try {
			addresses = geocoder.getFromLocation(getLat(), getLong(), 1);
			// TBD: Not efficient
			completeAdress += addresses.get(0).getAddressLine(0) + " -  ";
			completeAdress += addresses.get(0).getAddressLine(1) + " -  ";
			completeAdress += addresses.get(0).getAddressLine(2) + " -  ";
			completeAdress += addresses.get(0).getLocality();
			if (DEBUG)
				Log.i(TAG, completeAdress);
		} catch (IOException e) {
			if (DEBUG)
				Log.i(TAG, "Exception occured while getting address");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return completeAdress;
	}

}