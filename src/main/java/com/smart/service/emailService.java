package com.smart.service;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class emailService {


	public static boolean sendEmail(String message, String subject, String to,  String from) {
		// TODO Auto-generated method stub
		//variable for gmail
		boolean f=false;
		String host="smtp.gmail.com";
		//get  System properties
		Properties pro=	System.getProperties();
		System.out.println("properties"+pro);
		//setting important info to prooperties object
		
		pro.put("mail.smtp.host",host);
		pro.put("mail.smtp.port","465");
		pro.put("mail.smtp.ssl.enable","true");
		pro.put("mail.smtp.auth","true");
		Session session=		Session.getInstance(pro,new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication("notvalid99999@gmail.com", "totallyinvalid");
			}
		});
		session.setDebug(true);
		MimeMessage mm=new MimeMessage(session);
		try {
			mm.setFrom(from);
			mm.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
			mm.setSubject(subject);
			mm.setText(message);
			Transport.send(mm);
			System.out.println("hvgvgvhggh");f=true;
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return f;
	}

}
