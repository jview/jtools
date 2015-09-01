/**
 * TODO
 */
package org.jview.jtool.thread;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;




/**
 * 
 * @author jview
 *
 */
public class LoadDbThread implements Runnable{
	private Logger log4 = Logger.getLogger(LoadDbThread.class);
	private String dsPath=null;
	private Map<String, Connection> connMap;
	public LoadDbThread(String dsPath, Map<String, Connection> connMap){
		this.dsPath=dsPath;
		this.connMap=connMap;
	}
	

	public void run(){
		try {
			this.loadDataSource();
		} catch (Exception e) {
			log4.warn("ERROR:load dataSource for dsPath="+dsPath, e);
		}
	}
	
	private void loadDataSource() throws Exception{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Connection conn=null;
		DocumentBuilder builder=factory.newDocumentBuilder();
		File file = new File(this.dsPath);
		Document doc = builder.parse(file);
		log4.info("dsPath="+dsPath);
		NodeList nl=doc.getElementsByTagName("Resource");
		Node node=null;
		NodeList childList=null;
		String name=null;
		String url=null;
		String driverClassName=null;
		String userName=null;
		String password=null;
		NamedNodeMap nnMap=null;
		for(int i=0; i<nl.getLength(); i++){
			node=nl.item(i);
			nnMap=node.getAttributes();
			name=nnMap.getNamedItem("name").getNodeValue();
			if(nnMap.getNamedItem("url")!=null){
				url=nnMap.getNamedItem("url").getNodeValue();
			}
			if(nnMap.getNamedItem("driverClassName")!=null){
				driverClassName=nnMap.getNamedItem("driverClassName").getNodeValue();
			}
			userName=nnMap.getNamedItem("username").getNodeValue();
			password=nnMap.getNamedItem("password").getNodeValue();
			try{
				Class.forName(driverClassName);
				conn=DriverManager.getConnection(url, userName, password);
				connMap.put(name, conn);
			}catch(Exception e){
				log4.warn("url="+url+", "+e.getMessage());
			}
		}
	}

}
