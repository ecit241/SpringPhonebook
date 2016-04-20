package com.banamir.phonebook.services;

import com.banamir.phonebook.manager.PhonebookManager;
import com.banamir.phonebook.model.PhonebookEntry;
import com.banamir.phonebook.model.PhonebookUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
public class PhonebookService {

    @Autowired
    public PhonebookManager manager;

    @Autowired
    UserService userService;

    public Collection<PhonebookEntry> list(Map<String,String> filters){

        return  manager.entries(getUser(),filters);
    }

    public PhonebookEntry create(PhonebookEntry entry){

        entry.setUser(getUser());

        return manager.addEntry(entry);
    }

    public PhonebookEntry update(PhonebookEntry entry){

        if(! isEntryOfUser(entry))
            throw new AccessDeniedException("The phonebook entry is not belong of current user");

        entry.setUser(getUser());

        return manager.updateEntry(entry);
    }

    public Long delete(PhonebookEntry entry){

        if(! isEntryOfUser(entry))
            throw new AccessDeniedException("The phonebook entry is not belong of current user");

        return manager.deleteEntry(entry);
    }

    protected PhonebookUser getUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null )
            throw new AccessDeniedException("The current user of must be authenticated");
        String username = auth.getName();
        try{
            return (PhonebookUser) userService.loadUserByUsername(username);
        } catch(UsernameNotFoundException e){
            throw new AccessDeniedException("The current user is not registered in the system");
        }

    }

    protected boolean isEntryOfUser(PhonebookEntry entry){
        return manager.getEntry(entry.getId()).getUser().equals(getUser());

    }

    public void setManager(PhonebookManager manager) {
        this.manager = manager;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

}
