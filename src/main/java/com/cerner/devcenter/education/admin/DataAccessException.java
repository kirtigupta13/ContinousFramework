package com.cerner.devcenter.education.admin;

 /**
  * This exception class is used to mask some of the various exceptions that are thrown when trying
  * to establish a connection to or perform queries on the database as to not give away
  * implementation details.
  *
  * @author CB034874
  *	
  */

 public class DataAccessException extends Exception {

     /**
      * Auto generated serializable class version number
      */
     private static final long serialVersionUID = -2448814936847102710L;

     private String message;
     private String i18nMessage;
     private Throwable cause;

     public DataAccessException() {
         super();
     }

     public DataAccessException(String message) {
         super(message);
         this.message = message;
     }

     public DataAccessException(Throwable cause) {
         super(cause);
         this.cause = cause;
     }

     public DataAccessException(String message, Throwable cause) {
         super(message, cause);
         this.message = message;
         this.cause = cause;
     }

     public DataAccessException(String message, String i18nMessage, Throwable cause) {
         super(message, cause);
         this.message = message;
         this.i18nMessage = i18nMessage;
         this.cause = cause;
     }

     @Override
     public String getMessage() {
         return this.message;
     }

     @Override
     public Throwable getCause() {
         return this.cause;
     }

     /**
      * Retrieves the i18n message if there is one, otherwise null.
      *
      * @return the i18n message.
      */
     public String getI18nMessage() {
         return this.i18nMessage;
     }
}
