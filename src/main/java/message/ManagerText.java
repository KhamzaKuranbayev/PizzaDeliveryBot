package message;

import pizza_user.UserBot;

import static pizza_manager.ManagerBot.orders;

public class ManagerText {

    public static String chosetext(){
        String answer="";
        if(orders==null){
            answer="Tanlash";


        }
        return answer;
    }
}
