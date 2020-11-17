package message;

import pizza_user.*;


public class UserText {

    public static final String UZ = "\uD83C\uDDFA\uD83C\uDDFFUzbek";
    public static final String RU = "\uD83C\uDDF7\uD83C\uDDFAРусский";

    public static String create_order = "\uD83C\uDF55 Pizza tanlash";


    public static String userStartBeforeRegText() {

        return "Assalomu aleykum! Добро пожаловать в виртуальный помощник службы Uzbek Pizza Delivery \uD83C\uDF55";

    }

    public static String userStartAfterRegText() {
        String answer = "";
        switch (UserBot.LANGUAGE) {
            case RU:
            case UZ:
                answer = "Juda yaxshi! Birgalikda buyurtma beramizmi? \uD83D\uDE03";
                break;
        }
        return answer;
    }

    public static String userNameText() {
        String answer = "";
        switch (UserBot.LANGUAGE) {
            case RU:
            case UZ:
                answer = "Ismi-sharifingizni kiriting:";
                break;
        }
        return answer;
    }

    public static String userBalanceText() {
        String answer = "";
        switch (UserBot.LANGUAGE) {
            case RU:
            case UZ:
                answer = "Hisobingizga qancha pul kiritasiz?";
                break;
        }
        return answer;
    }

    public static String userAddressText() {
        String answer = "";
        switch (UserBot.LANGUAGE) {
            case RU:
            case UZ:
                answer = "Siz qaysi tumanda yashaysiz?";
                break;
        }
        return answer;
    }

    public static String userPhoneNumberText() {
        String answer = "";
        switch (UserBot.LANGUAGE) {
            case RU:
            case UZ:
                answer = "\uD83D\uDCF1 Telefon raqamingiz qanday?\n+998** *** ** **";
                break;
        }
        return answer;
    }

    public static String userMainMenuOrderText() {
        String answer = "";
        switch (UserBot.LANGUAGE) {
            case RU:
            case UZ:
                answer = "\uD83C\uDF55 Pizza tanlash";
                break;
        }
        return answer;
    }

    public static String userMainMenuInfoText() {
        String answer = "";
        switch (UserBot.LANGUAGE) {
            case RU:
            case UZ:
                answer = "\uD83D\uDCCB Informatsiya";
                break;
        }
        return answer;
    }

    public static String userMainMenuCardText() {
        String answer = "";
        switch (UserBot.LANGUAGE) {
            case RU:
            case UZ:
                answer = "\uD83D\uDECD Buyurtma savatchasi";
                break;
        }
        return answer;
    }

}
