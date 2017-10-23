package org.android.wilis;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Koneksi {

	// Asli dari template
	public String isi_koneksi()
	{
		//String isi = "http://10.0.2.2/ar/"; over emulator
		String isi = "http://192.168.1.101/ar2/"; //over wifi
		//String isi = "http://ruangbelajar.net/arpku/"; //over online web
		return isi;
	}
	// end of asli


}




