package com.cerner.devcenter.education.models;     
       
 /**       
  * This enum represents the Resource status that can be used for a        
  * {@link Resource}       
  * <li>{@link #Available}</li>        
  * <li>{@link #Pending}</li>      
  * <li>{@link #Deleted}</li>      
  *        
  * @author SV051339       
  *        
  */       
 public enum ResourceStatus {      
     /**       
      * {@link Resource} is available for users to access      
      */       
     Available,        
     /**       
      * {@link Resource} needs admin approval to make it {@link #Available}        
      */       
     Pending,      
     /**       
      * {@link Resource} has been removed/notapproved by admin        
      */       
     Deleted;      
 }