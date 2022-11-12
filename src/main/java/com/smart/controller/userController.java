package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.UserRepository;
import com.smart.dao.contactRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class userController {
	
	@Autowired
	private BCryptPasswordEncoder passw;
	
	@Autowired
	private UserRepository userrepository;
	
	@Autowired
	private contactRepository contactRepository;
	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal)
	{
		String name = principal.getName();
		System.out.println(name);
		User userbyUserName = this.userrepository.getUserbyUserName(name);
		model.addAttribute("user", userbyUserName);
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal)
	{
	
		return "normal/user_dashboard";
	}
	
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model)
	{
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
	}
	
	//processing contact controller
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,
			@RequestParam("pimage") MultipartFile img,
			Principal principal,
			HttpSession session)
	{
		
			
		try {
			String name=principal.getName();
			User user=this.userrepository.getUserbyUserName(name);
			if(img.isEmpty())
			{
				contact.setImage("contact.png");
				System.out.println("Empty file");
			}else
			{

				contact.setImage(img.getOriginalFilename());
				File file=new ClassPathResource("static/img").getFile();
				Path path=Paths.get(file.getAbsolutePath()+File.separator+img.getOriginalFilename());
				Files.copy(img.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				System.out.println("File uploaded successfully");
			}
			
			contact.setUser(user);
			user.getContact().add(contact);
			this.userrepository.save(user);
			session.setAttribute("message",new Message("Your contact is Successfully added","success") );
			
		} catch (Exception e) {
			e.printStackTrace();

			session.setAttribute("message",new Message("Something went wrong !!! Try again","danger") );
		}
		return "normal/add_contact_form";
		
	}
	
	//showing all contacts handler
	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page") Integer page,Principal principal,Model model)
	{
		String name=principal.getName();
		User u=this.userrepository.getUserbyUserName(name);
//		List<Contact> contact = u.getContact();
//		model.addAttribute("Contact", contact);
		PageRequest of = PageRequest.of(page, 5);
		Page<Contact> findContactbyUser = this.contactRepository.findContactbyUser(u.getId(),of);
		model.addAttribute("Contact", findContactbyUser);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",findContactbyUser.getTotalPages());
		System.out.println(findContactbyUser);
		return "normal/show-contact";
	}
	
	
	//showing contact details of individual
	@RequestMapping("/contact/{cid}")
	public String showContactDetails(@PathVariable("cid") int cid,Principal principal ,Model model)
	{
		Optional<Contact> findById = this.contactRepository.findById(cid);
		Contact contact = findById.get();
		String name=principal.getName();
		User u=this.userrepository.getUserbyUserName(name);
		if(u.getId()==contact.getUser().getId())
		model.addAttribute("contact", contact);
		return "normal/contact_detail";
	}
	
	//delete contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid")Integer cid,Principal principal,Model model,
	HttpSession session)
	{
		
		Optional<Contact> findById = this.contactRepository.findById(cid);
		Contact contact = findById.get();
		contact.setUser(null);
		String name=principal.getName();
		User u=this.userrepository.getUserbyUserName(name);
		//if(u.getId()==contact.getUser().getId())
	
		this.contactRepository.delete(contact);
		session.setAttribute("message",new Message("contact Deleted Successfully","success"));
		return "redirect:/user/show-contacts/0";
	}
	
	//update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,Model model)
	{
		System.out.println("Inside update form");
		model.addAttribute("title","Update Contact");
		Optional<Contact> findById = this.contactRepository.findById(cid);
		Contact contact = findById.get();
		model.addAttribute("contact", contact);
		return "normal/updateForm";
	}
	
	//update contact handler
	
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,Principal principal,@RequestParam("pimage") MultipartFile img,Model model,HttpSession session)
	{
		Contact oldinfo=this.contactRepository.findById(contact.getcId()).get();
		try {
			if(! img.isEmpty())
			{

				contact.setImage(img.getOriginalFilename());
				File file=new ClassPathResource("static/img").getFile();
				Path path=Paths.get(file.getAbsolutePath()+File.separator+img.getOriginalFilename());
				Files.copy(img.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				System.out.println("File uploaded successfully");
			}
			else
			{
				contact.setImage(oldinfo.getImage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String name=principal.getName();
		User u=this.userrepository.getUserbyUserName(name);
		
		contact.setUser(u);
		this.contactRepository.save(contact);
		session.setAttribute("message",new Message("Updated successfully","success"));
		return "redirect:/user/contact/"+contact.getcId();
	}
	
	//your profile handler
	@GetMapping("/profile")
	public String yourProfile()
	{
		
		return "normal/profile";
	}
	
	//settings
	@RequestMapping("/setting")
	public String setting()
	{
		return "normal/settings";
	}
	
	//change password handler
	@PostMapping("/change-password")
	public String changePassword(Principal principal,@RequestParam("old") String oldp, @RequestParam("new") String newp,HttpSession session)
	{
		String name = principal.getName();
		User userbyUserName = this.userrepository.getUserbyUserName(name);
		if(this.passw.matches(oldp, userbyUserName.getPassword()))
		{
			userbyUserName.setPassword(this.passw.encode(newp));
			this.userrepository.save(userbyUserName);
			session.setAttribute("message",new Message("Updated successfully","success"));
			
		}
		else
		{
			session.setAttribute("message",new Message("Password not Matched","danger"));
			return "redirect:/user/setting";
		}
		return "redirect:/user/index";
	}
	
}
