package org.example;

public class JSONresponse {
        String reply;

        public JSONresponse() {
        }

        public void putField(String value){
            reply = "{\"message\":\""+value+"\"}";
        }

        public void putObject(String value){
            reply = "{\"object\":"+value+"}";
        }

        public String toString(){
            return reply;
        }

}
