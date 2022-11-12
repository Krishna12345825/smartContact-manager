package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userrepository;

	@GetMapping("/")
	public String home(Model model)
	{
		model.addAttribute("title","Home-SMART CONTACT MANAGER");
		return "home";
	}
	@GetMapping("/about")
	public String about(Model model)
	{
		model.addAttribute("title","about-SMART CONTACT MANAGER");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model model)
	{
		model.addAttribute("title","Signup-SMART CONTACT MANAGER");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	@GetMapping("/signin")
	public String login()
	{
		
		return "login";
	}
	
	
	@PostMapping("/do_register")
	public String register(@Valid @ModelAttribute("user") User user,BindingResult result ,@RequestParam(value="accept",defaultValue = "false")boolean agreement, Model model,HttpSession session)
	{
		
try {
	
	if(!agreement)
	{
		throw new Exception("  You have not agreed the terms and condition");
	}
	if(result.hasErrors())
	{
		System.out.println(result.toString());
		model.addAttribute("user", user);
		return "signup";
	}
	user.setRole("ROLE_USER");
	user.setImageUrl("default.png");
	user.setEnabled(true);
	user.setPassword(passwordEncoder.encode(user.getPassword()));
	User result2=this.userrepository.save(user);
	model.addAttribute("user", new User());
	
	session.setAttribute("message",new Message("Successfully Registered","alert-success"));
	
} catch (Exception e) {
	// TODO: handle exception
	model.addAttribute("user", user);
	session.setAttribute("message",new Message("Something went wrong"+e.getMessage(),"alert-danger"));
	e.printStackTrace();
}
		return "signup";
	}
}
