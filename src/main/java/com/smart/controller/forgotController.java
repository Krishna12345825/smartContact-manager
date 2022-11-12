package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.emailService;

@Controller
public class forgotController {

	@Autowired
	private BCryptPasswordEncoder bpe;
	@Autowired
	private UserRepository userrepository;
	@Autowired
	private emailService emailservice;
	Random rand=new Random(1000);
	@GetMapping("/forget")
	public String openemailform()
	{
		
		return "forget_email_form";
	}
	
	@PostMapping("/sendOtp")
	public String sendOtp(@RequestParam("email") String email,HttpSession session)
	{
		int otp=rand.nextInt(9999);
		System.out.println(otp);
		String msg=" OTP="+otp+"";
		String sub="OTP FROM Smart Contact Manager";
		String to=email;
		String from="notvalid99999@gmail.com";
		boolean flag=this.emailservice.sendEmail(msg,sub,to,from);
		if(flag)
		{
			session.setAttribute("otp",otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}
		else
		{
			session.setAttribute("message", "Check your mail");
			
		
		
		}
		return "forget_email_form";
		
	}
	
	@PostMapping("/verify-otp")
	public String verify(@RequestParam("otp") String otp,HttpSession session)
	{
		int ot = (int) session.getAttribute("otp");
		String mail=(String) session.getAttribute("email");
		if(ot==Integer.parseInt(otp))
		{
			User user=this.userrepository.getUserbyUserName(mail);
			if(user==null)
			{
				session.setAttribute("message",new Message("You are not a registered user", "danger"));
				return "forget_email_form";
			}
			return "password_change_form";
		}
		else
		{
			session.setAttribute("message",new Message("You have entered wrong otp", "danger"));
			return "verify_otp"; 
		}
	}
	
	
	@GetMapping("/change-password")
	public String chngepass(@RequestParam("password") String password,HttpSession session)
	{
		User user=this.userrepository.getUserbyUserName((String)session.getAttribute("email"));
		user.setPassword(this.bpe.encode(password));
		this.userrepository.save(user);
		session.setAttribute("message",new Message("Password change successfully", "success"));
		
		return "redirect:/signin?change=changed successfully";
	}
}
