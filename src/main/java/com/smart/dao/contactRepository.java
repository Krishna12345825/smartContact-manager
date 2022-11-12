package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface contactRepository extends JpaRepository<Contact, Integer> {

	@Query("from Contact as c where c.user.id=:id")
	public Page<Contact> findContactbyUser(@Param("id")int  uid,Pageable pageable);
		
	public List<Contact> findByNameContainingAndUser(String name,User user);
	//pageable object has 2 values
	//current psge
	//contsct per psge
}
