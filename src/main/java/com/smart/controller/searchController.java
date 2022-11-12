package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.UserRepository;
import com.smart.dao.contactRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@RestController
public class searchController {

	//search handler
	@Autowired
	private UserRepository userrepository;
	@Autowired
	private contactRepository contactrepository;
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal)
	{

		User user = this.userrepository.getUserbyUserName(principal.getName());
		List<Contact> findByNameContainingAndUser = this.contactrepository.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(findByNameContainingAndUser);
	}
	}
