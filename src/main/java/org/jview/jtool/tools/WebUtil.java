package org.jview.jtool.tools;

import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.htmlparser.Parser;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;
import org.jview.jtool.util.ErrorCode;





/**
 * 
 * @author chenjh
 *
 */
public class WebUtil {
	private Logger log4 = Logger.getLogger(WebUtil.class);

	private String username;
	private String password;
	private String login_post;
	private String login_url;
	private String base_url;
	private String url;

	public void login() throws Exception{
		log4.info("===========登录系统===========");
		if(this.base_url==null){
			log4.info("未指定base_url,不能登入");
			return;
		}
		String post = base_url +login_url;
		if(this.username!=null&&this.password!=null){
			post += "&username=" + username+ "&password=" + password;
		}
		log4.info("post=" + post);
		URL url = new URL(post);
		
		httpConn = (HttpURLConnection) url.openConnection();

		// setInstanceFollowRedirects can then be used to set if
		// redirects should be followed or not and this should be used
		// before the
		// connection is established (via getInputStream, getResponseCode,
		// and other
		// methods that result in the connection being established).

//		httpConn.setFollowRedirects(false);

		// inorder to disable the redirects
//		httpConn.setInstanceFollowRedirects(false);

		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		httpConn.setRequestProperty("User-Agent",
				"Mozilla/5.0 (compatible; MSIE 6.0; Windows NT)");
		httpConn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		
		

		// ok now, we can post it
		PrintStream send = new PrintStream(httpConn.getOutputStream());
		send.print("");		
		send.close();		
				
		this.cookies = getCookies(httpConn);
		this.sessionId=getSession(httpConn);
		

		log4.info("cookies="+cookies+" sessionId="+this.sessionId);
	}
	
	/**
	 * 更新最新的部门发文
	 * @param httpConn
	 * @throws Exception
	 */
	public List visit(boolean isByCookie) throws Exception{
		log4.info("======访问网站============cookie="+isByCookie);
		
		String newUrls = null;
		TableColumn[] arrColumns;
		LinkTag lt = null;
	
		String title, dates, codes, link;
		HtmlPage page = null;
		TableTag tableContent[] = null;
		int order_count = 0;
		List list = new LinkedList();
		
		if(this.getUrl().startsWith("http:")){
			newUrls = this.getUrl();
		}
		else{
			if(this.base_url.endsWith("/")){
				newUrls = this.base_url+"/"+this.getUrl();
			}
			else{
				newUrls = this.base_url+this.getUrl();
			}
			
		}
//		if(true){
//			page = move2Urls(this.getHttpURLConnection(), newUrls);
//			page.getBody().toHtml();
////			return page.getBody().toHtml();
//		}
		log4.info("newUrls="+newUrls);

		String str = this.move2UrlsHtml(this.getHttpURLConnection(), newUrls, isByCookie);
		
		String[] msgs = str.split("\n");
		List htmls = new LinkedList();
		for(String msg: msgs){
			msg = msg.trim();
			msg = msg.replaceAll("&nbsp;", "");
			msg = msg.replaceAll("&lt;", "");
			msg = msg.replaceAll("&gt;", "");
			msg = msg.replaceAll("&quot;", "");
			msg = msg.replaceAll("td", "");
			msg = msg.replaceAll("tr", "");
//			msg = msg.replaceAll("&", "&amp;");
//			msg = msg.replaceAll("<", "&lt;");
//			msg = msg.replaceAll(">", "&gt;");
//			msg = msg.replaceAll("\"", "&quot;");
//			msg = msg.replaceAll("'", "&apos;");
			if(!ErrorCode.isEmpty(msg)){
				htmls.add(msg);
			}
		}
	
		return htmls;
	}
	
	public void logout() throws Exception{
		String newUrls = this.base_url+"/logout.html";
		this.move2Urls(httpConn, newUrls);
	}
	private String cookies;
	private String sessionId;
	private HttpURLConnection httpConn;
	
	public String getCookies(){
		return cookies;
	}
	private HttpURLConnection getHttpURLConnection() throws Exception{
		if(this.httpConn==null){
			this.login();
		}
		return this.httpConn;
	}
	private  HtmlPage move2Urls(HttpURLConnection httpConn, String newUrls)
		throws Exception {
		httpConn.disconnect();
		String cookies = this.getCookies();
		// String
		// newUrls="http://eip.shenzhenair.com/shenzhenair/pub_bmwj.nsf/vwbydept?SearchView&count=20&Query=%E8%88%AA%E6%A0%A1&view=vwbydept";
		URL newURL = new URL(newUrls);
		log4.info("-----move2Urls the URL=" + newUrls);
		
		// OK, now we are ready to get the cookies out of the URLConnection
		
		// System.out.println("======cookies====="+cookies);
		httpConn = (HttpURLConnection) newURL.openConnection();
		httpConn.setRequestProperty("User-Agent",
				"Mozilla/5.0 (compatible; MSIE 6.0; Windows NT)");
		httpConn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		httpConn.setRequestProperty("Cookie", cookies);
		
		httpConn.setDoInput(true);
		
		Parser parser = new Parser(httpConn);		
		HtmlPage page = new HtmlPage(parser);
		try {
			parser.visitAllNodesWith(page);
		
		} catch (ParserException e1) {
			e1 = null;
		}
		
		return page;
	
	}
	
	private  String move2UrlsHtml(HttpURLConnection httpConn, String newUrls, boolean isByCookie)
		throws Exception {
		httpConn.disconnect();
		String cookies = this.getCookies();
//		System.out.println("----cookies="+cookies);
		// String
		// newUrls="http://eip.shenzhenair.com/shenzhenair/pub_bmwj.nsf/vwbydept?SearchView&count=20&Query=%E8%88%AA%E6%A0%A1&view=vwbydept";
		URL newURL = new URL(newUrls);
		log4.info("----move2UrlsHtml the URL=" + newUrls);
		
		// OK, now we are ready to get the cookies out of the URLConnection
		
		// System.out.println("======cookies====="+cookies);
		httpConn = (HttpURLConnection) newURL.openConnection();
		httpConn.setRequestProperty("User-Agent",
				"Mozilla/5.0 (compatible; MSIE 6.0; Windows NT)");
		httpConn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		if(isByCookie){
			httpConn.setRequestProperty("Cookie", cookies);
		}
		else{
			httpConn.setRequestProperty("Cookie", this.sessionId);
		}
		
		httpConn.setDoInput(true);
		
		httpConn.setDoOutput(true);
		httpConn.getOutputStream();
		byte[] bb = new byte[httpConn.getInputStream().available()];
		httpConn.getInputStream().read(bb);
		String str = new String(bb);
		
//		Parser parser = new Parser(httpConn);		
//		HtmlPage page = new HtmlPage(parser);
//		try {
//			parser.visitAllNodesWith(page);
//		
//		} catch (ParserException e1) {
//			e1 = null;
//		}
	
	return str;

}
	
	public static String getSession(HttpURLConnection conn){
		String session_value = conn.getHeaderField("Set-Cookie");
		String[] sessionId = session_value.split(";");
		return sessionId[0];
	}
	public static String getCookies(HttpURLConnection conn) {
		StringBuffer cookies = new StringBuffer();
		String headName;
		for (int i = 7; (headName = conn.getHeaderField(i)) != null; i++) {
			StringTokenizer st = new StringTokenizer(headName, "; ");
			while (st.hasMoreTokens()) {
				cookies.append(st.nextToken() + "; ");
			}
		}
		return cookies.toString();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLogin_url() {
		return login_url;
	}

	public void setLogin_url(String login_url) {
		this.login_url = login_url;
	}

	public String getBase_url() {
		return base_url;
	}

	public void setBase_url(String base_url) {
		this.base_url = base_url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLogin_post() {
		return login_post;
	}

	public void setLogin_post(String login_post) {
		this.login_post = login_post;
	}
	
	
	
}
