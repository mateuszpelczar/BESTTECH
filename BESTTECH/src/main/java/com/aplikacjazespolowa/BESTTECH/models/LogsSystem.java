package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class LogsSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String action;
    private String username;
    private LocalDateTime timestamp;
    private String level;

    public LogsSystem(){
//        this.timestamp=new Date();automatyczna data
    }

    public LogsSystem(String action, String username, String level){
        this.action=action;
        this.username=username;
        this.timestamp= LocalDateTime.now();
        this.level=level;
    }

//    public LogsSystem(String action, String username, Date timestamp){
//        this.action=action;
//        this.username=username;
//        this.timestamp=timestamp;
//    }



    public Integer getId(){
        return  id;
    }
    public void setId(Integer id){
        this.id=id;
    }

    public String getAction(){
        return  action;
    }

    public void setAction(String action){
        this.action=action;
    }

    public String  getUsername(){
        return  username;
    }

    public void setUsername(String username){
        this.username=username;
    }

    public LocalDateTime getTimestamp(){
        return  timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp){
        this.timestamp=timestamp;
    }

    public String getLevel(){
        return level;
    }

    public void setLevel(String level){
        this.level=level;
    }
}
