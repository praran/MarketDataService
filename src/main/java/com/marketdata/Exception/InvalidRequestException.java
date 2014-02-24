package com.marketdata.Exception;

/**
 * @author Pradeep Muralidharan.
 */
public class InvalidRequestException extends  RuntimeException{

     public InvalidRequestException(){
          super();
      }

     public InvalidRequestException(String message){
         super(message);
     }
}
