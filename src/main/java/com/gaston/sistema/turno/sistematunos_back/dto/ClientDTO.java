package com.gaston.sistema.turno.sistematunos_back.dto;

public class ClientDTO {
     private Long id;
     private String name;
     private String lastName;
     private String email;

     public Long getId() {
         return id;
     }
     public void setId(Long id) {
         this.id = id;
     }
     public String getName() {
         return name;
     }
     public void setName(String name) {
         this.name = name;
     }
     public String getLastName() {
         return lastName;
     }
     public void setLastName(String lastName) {
         this.lastName = lastName;
     }
     public String getEmail() {
         return email;
     }
     public void setEmail(String email) {
         this.email = email;
     }
}
