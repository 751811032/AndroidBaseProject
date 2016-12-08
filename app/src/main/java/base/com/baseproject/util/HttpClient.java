package base.com.baseproject.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpClient {

 //   private String IP = "http://192.168.100.22";
 //   private String IP = "http://10.10.30.82/jisuan/";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    private String url;
    private String cookie;
    private String sessionId;
    HttpURLConnection httpConn = null;
    private boolean connected = false;
    private Map<String, String> requestHeader = new HashMap<String, String>();
    private static final String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.3) Gecko/20100401 Firefox/3.6.3";
    public HttpClient(String url) {
        this(url, null);
    }

    public HttpClient() {
       /* try {
            InetAddress localhost = InetAddress.getLocalHost();
            if ("10.10.10.16".equals(localhost.getHostAddress())) {
                IP = IP + ":8084";
            } else {
                IP = IP + ":80";
            }
        } catch (UnknownHostException e) {
            System.err.println("Localhost not seeable. Something is odd. ");
        }*/
    }

    public HttpClient(String url, String cookie) {
        this.url = url;
        this.cookie = cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String setHttpClient(String url, String cookie) {
        String sReturn = "";
        this.url = url;
        this.cookie = cookie;
        connect("GET");
        try {
            sReturn = getBodyString();
        } catch (Exception e) {//Exception  MalformedURLException
            e.printStackTrace();
        }

        disconnect();
        return sReturn;
    }
    
    public String sendPost(String urlString, String sParams) {
        String sReturn = "";
        URL url = null;
        HttpURLConnection conn = null;
        BufferedReader in = null;
        try {
            url = new URL( urlString);

            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(30000);

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
//            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
            conn.setUseCaches(false);
            //     HttpURLConnection.setFollowRedirects(false);
            conn.setInstanceFollowRedirects(false);

            conn.setRequestProperty("Cookie", cookie);
////            Log.i(WebSiteUrl.Tag,"cookie="+cookie);
            Log.e("cookie------------------","cookie="+cookie);

            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.connect();
            conn.getOutputStream().write(sParams.getBytes());
            // 刷新对象输出流，将任何字节都写入潜在的流中（些处为ObjectOutputStream）
            conn.getOutputStream().flush();
            // 关闭流对象。此时，不能再向对象输出流写入任何数据，先前写入的数据存在于内存缓冲区中,
            // 在调用下边的getInputStream()函数时才把准备好的http请求正式发送到服务器
            conn.getOutputStream().close();

            // 调用HttpURLConnection连接对象的getInputStream()函数,
            // 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
            //  InputStream inStrm = httpConn.getInputStream(); // <===注意，实际发送请求的代码段就在这里
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            sReturn = sb.toString();
            sb = null;
        } catch (Exception e) {
        //	Log.i(WebSiteUrl.Tag,"111111");
        	e.printStackTrace();
        	sReturn = "netError";
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                	sReturn = "netError";
                	e.printStackTrace();
                //	Log.i(WebSiteUrl.Tag,"22222");
                }
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                	sReturn = "netError";
                	e.printStackTrace();
                	//Log.i(WebSiteUrl.Tag,"33333");
                }
            }
            url = null;
            conn = null;
            in = null;
        }
        return sReturn;
    }

    public static String sendGet(String urlString, String sParams) {
        String sReturn = "";
        URL url = null;
        HttpURLConnection conn = null;
        BufferedReader in = null;
        try {
            url = new URL( urlString);

            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(30000);

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
//            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
            conn.setUseCaches(false);
            //     HttpURLConnection.setFollowRedirects(false);
            conn.setInstanceFollowRedirects(false);

            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.connect();
            conn.getOutputStream().write(sParams.getBytes());
            // 刷新对象输出流，将任何字节都写入潜在的流中（些处为ObjectOutputStream）
            conn.getOutputStream().flush();
            // 关闭流对象。此时，不能再向对象输出流写入任何数据，先前写入的数据存在于内存缓冲区中,
            // 在调用下边的getInputStream()函数时才把准备好的http请求正式发送到服务器
            conn.getOutputStream().close();

            // 调用HttpURLConnection连接对象的getInputStream()函数,
            // 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
            //  InputStream inStrm = httpConn.getInputStream(); // <===注意，实际发送请求的代码段就在这里
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            sReturn = sb.toString();
            sb = null;
        } catch (Exception e) {
            //	Log.i(WebSiteUrl.Tag,"111111");
            e.printStackTrace();
            sReturn = "netError";
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    sReturn = "netError";
                    e.printStackTrace();
                    //	Log.i(WebSiteUrl.Tag,"22222");
                }
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    sReturn = "netError";
                    e.printStackTrace();
                    //Log.i(WebSiteUrl.Tag,"33333");
                }
            }
            url = null;
            conn = null;
            in = null;
        }
        return sReturn;
    }

    public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void addRequestHeader(String key, String value) {
        this.requestHeader.put(key, value);
    }

    public boolean connect(String method) {
       // System.out.println(method);
        boolean bReturn = true;
        try {
            Log.i("我的信息","url："+url);
            URLConnection con = new URL(url).openConnection();
            if (con instanceof HttpURLConnection) {
                httpConn = (HttpURLConnection) con;
            }
            httpConn.setConnectTimeout(10000);
            httpConn.setReadTimeout(10000);

            if ("POST".equals(method)) {
                httpConn.setDoInput(true);
                httpConn.setDoOutput(true);
                httpConn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
            }

            httpConn.setInstanceFollowRedirects(false);
            httpConn.setRequestMethod(method);
            if (cookie != null) {
            	//System.out.println(cookie);
                httpConn.addRequestProperty("Cookie", cookie);
            }
            httpConn.setRequestProperty("User-Agent", USER_AGENT);

            httpConn.setUseCaches(false);

            if (!this.requestHeader.isEmpty()) {
                for (String key : this.requestHeader.keySet()) {
                    httpConn.setRequestProperty(key, this.requestHeader.get(key));
                }
            }
            httpConn.connect();
            cookie = getCookie();
            Log.i("我的信息", "sessionId:" + cookie);
            sessionId = cookie;
            Log.i("我的信息", "sessionId:" + sessionId);
            connected = true;
        } catch (Exception e) {//Exception  MalformedURLException
            bReturn = false;
            e.printStackTrace();
        }
        return bReturn;
    }

    public boolean followRedirects() throws IOException {
        int resultCode = httpConn.getResponseCode();
        return (resultCode >= 300 && resultCode < 400);
    }

    public String getLocation() {
        String location = httpConn.getHeaderField("Location");
        if (location == null) {
            location = httpConn.getHeaderField("location");
        }
        return location;
    }

    public InputStream getInputStream() throws IOException {
        return httpConn.getInputStream();
    }

    public String getBodyString() throws IOException {
        return getBodyString(null);
    }

    public String getBodyString(String charset) throws IOException {
        BufferedReader reader;
        if (charset == null) {
            reader = new BufferedReader(new InputStreamReader(getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(getInputStream(),
                    charset));
        }
        StringBuffer sb = new StringBuffer();
        String inputLine = null;
        while ((inputLine = reader.readLine()) != null) {
            sb.append(inputLine);
        }
        return sb.toString();
    }

    public void post(List<String> dataList) throws IOException {
        PrintWriter out = new PrintWriter(httpConn.getOutputStream());
        StringBuffer postData = new StringBuffer();
        for (String data : dataList) {
            postData.append(data).append("&");
        }
        out.write(postData.toString());
        out.flush();
        out.close();
    }

    public String getCookie() {
        String key = null;
        String cookie = "";
        for (int i = 1; (key = httpConn.getHeaderFieldKey(i)) != null; i++) {
            if ("set-cookie".equalsIgnoreCase(key)) {
                String cookieVal = httpConn.getHeaderField(i);
                cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
                cookie = cookie + cookieVal + ";";
            //    cookie = cookie + cookieVal ;
            }
        }
     //   System.out.println(cookie);
        return cookie;
    }

    public void disconnect() {
        if (connected) {
            httpConn.disconnect();
            connected = false;
        }
    }

    public String getAccInfo() throws IOException {
        String str = "";
        connect(METHOD_GET);
        str = getBodyString();
        return str;
    }
}
