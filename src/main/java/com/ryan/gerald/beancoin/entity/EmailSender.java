//package com.ryan.gerald.beancoin.entity;
//
//import java.util.HashMap;
//import java.util.Properties;
//
//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.AddressException;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//
//import org.hibernate.mapping.Map;
//
////import com.ryan.gerald.beancoin.config.PrivateConfig;
//import com.ryan.gerald.beancoin.utilities.StringUtils;
//
//public class EmailSender {
//
//	/**
//	 *
//	 * Basic email sender. Takes sender email address and password (Env variables),
//	 * recipients and body. Body has "text" and "subject" keys.
//	 *
//	 * @param sender
//	 * @param password
//	 * @param recipient - string email address.
//	 * @param body      - hashmap with "text" and "subject" keys.
//	 */
//	public static void sendEmail(String sender, String password, String recipient, HashMap body) {
//		Properties properties = System.getProperties(); // just a <String, String> HashMap with extra features
//		properties.setProperty("mail.smtp.host", "smtp.gmail.com");
//		properties.setProperty("mail.smtp.port", "587"); // for google TLS
//		properties.setProperty("mail.smtp.auth", "true");
//		properties.setProperty("mail.smtp.starttls.enable", "true");
//
//		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication(sender, password);
//			}
//		});
//
//		try {
//			MimeMessage message = new MimeMessage(session); // email msg that understands MIME types and headers
//			message.setFrom(new InternetAddress(sender));
//			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
//			message.setSubject((String) body.get("subject"));
//			message.setText((String) body.get("text"));
//
//			Transport.send(message);
//			System.out.println("Message sent");
//		} catch (AddressException e) {
//			e.printStackTrace();
//		} catch (MessagingException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static void main(String[] args) {
//		HashMap<String, String> body = new HashMap<>();
//		body.put("subject", "Your private key");
//		body.put("text", "Send from java application just a simple text email");
////		if (!PrivateConfig.fromEmail.equals(null) && !PrivateConfig.emailPassword.equals(null)) {
////			EmailSender.sendEmail(PrivateConfig.fromEmail, PrivateConfig.emailPassword, "gerald.ryan40@yahoo.com",
////					body);
////		}
//	}
//
//}
