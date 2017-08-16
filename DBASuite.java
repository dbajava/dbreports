package dbasuite;

import dbasuite.Instance;
import dbasuite.Report;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class DBASuite {
	private static boolean argC=false;
	private static boolean argS=false;
	private static final char[] PASSWORD = "enfldsgbnlsngdlksdsgm".toCharArray();
	private static final byte[] SALT = {
			(byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
			(byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
	};
	public static void main(String[] args) throws JAXBException, SQLException, IOException, GeneralSecurityException {
		if(args.length>2){
			System.out.println("Invalid number of arguments Max is 2: c s");
			System.exit(0);
		}
		if(System.getProperty("passwd")!=null){
			try {
				System.out.println("Encrypted Password: "+encrypt(System.getProperty("passwd")));
				System.exit(0);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
		if(args.length!=0){
			for (int i =0;i<args.length;i++){
				if((!args[i].equals("s"))&&(!args[i].equals("c"))){
					System.out.println("Invalid argument: use s or c");
					System.exit(0);
				}else if(args[i].equals("s")){
					argS=true;
				}
				if(args[i].equals("c")){
					argC=true;
				}
			}
		}
		File[] files = new File[1];
		if (!argS){
			System.out.println("checking xml files...");
		}
		if (System.getProperty("xmlsa")!=null){
			files[0]=new File(System.getProperty("xmlsa"));
		}else{
			files=new File("xml").listFiles();
		}
		for (File file : files) {
			try{
				boolean toSend=false;
				if (file.isFile()) {
					if (!argS){
						System.out.println("Parsing xml file: "+file.getAbsoluteFile().getName());
					}
					if(file.getName().replaceAll("^.*\\.(.*)$", "$1").toLowerCase().equals("xml")){   	
						File fXmlFile = new File("xml"+File.separator+file.getAbsoluteFile().getName());
						JAXBContext jaxbContext;
						jaxbContext = JAXBContext.newInstance(Instance.class);
						Unmarshaller jaxbUnmarshaller;
						jaxbUnmarshaller = jaxbContext.createUnmarshaller();
						Instance instance = (Instance)jaxbUnmarshaller.unmarshal( fXmlFile );
						if (!argS){
							System.out.println("instance connection string: "+"jdbc:oracle:thin:@"+instance.getHostName()+":"+instance.getPort()+"/"+instance.getDbName());
						}
						Class.forName("oracle.jdbc.driver.OracleDriver");
						Connection connection  = DriverManager.getConnection("jdbc:oracle:thin:@"+instance.getHostName()+":"+instance.getPort()+"/"+instance.getDbName(),instance.getUserName(),decrypt(instance.getPassw()));
						Statement stmt = connection.createStatement();
						String content="<html>";
						content=content+"<body><title>Database Report instance: "+instance.getFantasyName()+"</title>";
						for (int i=0;i<instance.getReports().size();i++){
							Report report = (Report) instance.getReports().get(i);
							if(report.toRun()){
								if (!argS){
									System.out.println("Generating Report: "+report.getTitle());
									System.out.println("Using the query:\n"+report.getQuery());
								}
								List<String> repQuery=report.getQuery();
								for (int k=0;k<repQuery.size();k++){
									ArrayList<String> arrLis = report.getColname(k);

									if((!report.getTitle().equals(""))&&(k==0)){
										content=content+"<hr><font size=\"2\" face=\"arial\" color=\"black\"><br><center><header><h3>"+report.getTitle()+"</h3></header><center>";
									}
									content=content+"<center><table border=\"1\"><tr>";
									for(int n=0;n<arrLis.size();n++) content=content+"<th>"+arrLis.get(n).toString()+"</th>";
									content=content+" </tr>";
									ResultSet rs = report.runQuery(repQuery.get(k), stmt);
									content=content+report.addResults(rs);
									content=content+"</table></center></body></font></html>";
								}
							}
							if(!toSend)toSend=report.toRun();
						}
						connection.close();
						if(argC){
							if(toSend){
								SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss") ;
								Date date = new Date();
								String curDate =dateFormat.format(date);
								String htmlName="report_"+instance.getDbName()+"_"+curDate+".html";
								if (!argS){
									System.out.println("Writing file report.html to path:");
									System.out.println(System.getProperty("user.dir")+File.separator+htmlName);
								}
								File htmlFile = new File(System.getProperty("user.dir")+File.separator+htmlName);
								FileWriter fw =new FileWriter(htmlFile);
								fw.write(content);
								fw.close();
							}
						}else{
							if(toSend){
								if (!argS){
									System.out.println("Sending email to:"+instance.getToMail());
								}
								instance.sendMail(content);
								System.gc();
							}
						}
					}
				}
			}catch(Exception e){
				if (!argS){
					System.out.println(">>>>>>>>>>>ALERT<<<<<<<<<<<<<<");
					System.out.println("Error parsing XML file: "+file.getAbsoluteFile().getName());
					System.out.println(">>>>>>>>>>>ALERT<<<<<<<<<<<<<<");
				}
				File fXmlFile = new File("xml"+File.separator+"error_xml.conf");
				JAXBContext jaxbContext;
				jaxbContext = JAXBContext.newInstance(Instance.class);
				Unmarshaller jaxbUnmarshaller;
				jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				Instance instance = (Instance)jaxbUnmarshaller.unmarshal(fXmlFile);
				instance.setFantasyName("Error parsing XML file: "+file.getAbsoluteFile().getName());
				String content = e.getMessage();
				instance.setPort("666");
				instance.sendMail(content);
				System.gc();
			}
		}

	}
	private static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
	}
	private static String base64Encode(byte[] bytes) {
		return new BASE64Encoder().encode(bytes);
	}
	private static byte[] base64Decode(String property) throws IOException {
		return new BASE64Decoder().decodeBuffer(property);
	}
	private static String decrypt(String property) throws GeneralSecurityException, IOException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
	}
}