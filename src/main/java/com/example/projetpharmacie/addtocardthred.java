package com.example.projetpharmacie;

import java.time.LocalDate;
import java.util.Date;

public class addtocardthred extends Thread {
private int idproduit ;
private int id ;
private int idclient ;
private UserSession userSession;
private int quantity ;
private int stock ;
private String statu ;
private Double total ;
private LocalDate today ;
private String role ;
private static CartThreadService service = new CartThreadService();
 public addtocardthred(int idproduit,int quantity,String role) {
     this.idproduit = idproduit;
     this.quantity = quantity;
     this.role = role;
 }
 public addtocardthred(int id ,int idproduit,int quantity,String role) {
    this.id = id;
     this.idproduit = idproduit;
     this.quantity = quantity;
     this.role = role;
 }
    public void run(){
        switch (role.toLowerCase()) {
            case "rupture":
                service.misestock(idproduit,quantity);
              break ;
            case "ajoute":
             service.ajouterlc(id,idproduit,quantity);
             break ;
            default:
                System.out.println("❌ Role inconnu");
        }
    }
    }

