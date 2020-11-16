package models;

import pizza_user.UserBot;

public class Text {

    public static final String UZ = "Uzbek";
    public static final String RU = "Русский";

    private static StringBuilder result;

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
                answer = "\uD83D\uDECD Buyurtma berish";
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



}
