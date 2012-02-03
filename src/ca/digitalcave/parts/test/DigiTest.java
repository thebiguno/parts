package ca.digitalcave.parts.test;
import java.net.HttpURLConnection;
import java.net.URL;

import org.restlet.engine.io.BioUtils;


public class DigiTest {

	public static void main(String[] args) throws Exception {
		HttpURLConnection c = (HttpURLConnection) new URL("http://search.digikey.com/scripts/dksearch/dksus.dll?vendor=0&keywords=338-1364-ND").openConnection();
		c.setDoInput(true);
		c.setDoOutput(true);
		c.setRequestProperty("Cookie", "TSd6f8f6_75=ca49834045ec2f4d1040acc24f4fc71d:jnmm:72fjxZYs:1635770085");
//		c.setRequestProperty("Set-Cookie", "sid=1378684833-23149; expires=Sat, 03-Mar-2012 00:00:00 GMT; path=/; domain=.digikey.com cur=USD; expires=Sat, 03-Mar-2012 00:00:00 GMT; path=/; domain=.digikey.com csscxt=1614063808.20480.0000; path=/");
//		c.setRequestProperty("Set-Cookie", "TSd6f8f6=2e549f97e0e929495f4e3553e4e8f973ba53856a8f5d431f4f2b175aca85ab039e07bf71a3c65dd840b6a4aece4430a77b378216; Path=/");
//		c.setRequestProperty("Host", "search.digikey.com");
//		c.setRequestProperty("Referer", "http://search.digikey.com/scripts/dksearch/dksus.dll?vendor=0&keywords=338-1364-ND");
//		c.setRequestProperty("P3P", "CP=\"PHY DEM ONL STA PUR NAV COM INT UNI OUR CUR ADM DEV TAI IDC COR BUS DSP\"");
		c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		c.setRequestMethod("POST");
		c.getOutputStream().write("TSd6f8f6_id=3&TSd6f8f6_md=1&TSd6f8f6_rf=0&TSd6f8f6_ct=0&TSd6f8f6_pd=0".getBytes());
//		c.connect();
		System.out.println(c.getResponseCode());
		System.out.println(c.getContentType());
		System.out.println(c.getContentLength());
		try {BioUtils.copy(c.getErrorStream(), System.err);} catch (Exception e) {}
		try {BioUtils.copy(c.getInputStream(), System.out);} catch (Exception e) {}
		
	}
}
