package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Client {

	public static void main(String[] args) throws IOException {

		URL url = new URL("http://localhost:8080/lbs_server/print_session.jsp");// Java中自定义链表总结
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");

		conn.setDoOutput(true);

		// 第二次运行的时候，把上次读取的session的值设置上

		conn.setRequestProperty("Cookie",
				"JSESSIONID=320C57C083E7F678ED14B8974732225E");

		PrintWriter out = new PrintWriter(conn.getOutputStream());

		String str = "url = " + url;

		System.out.println("");

		out.println(str);

		out.flush();

		BufferedReader in = null;

		in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		String destStr = "";

		String inputLin = "";

		while ((inputLin = in.readLine()) != null) {

			destStr += inputLin;

		}

		System.out.println(destStr);

		// 第一次运行的时候，记录下来session的值

		String session_value = conn.getHeaderField("Set-Cookie");

		String[] sessionId = session_value.split(";");

		System.out.println(sessionId[0]);

		System.out.println("Session Value = " + session_value);

	}

}
