import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import pizza_deliveryman.DeliverymanBot;
import pizza_manager.ManagerBot;
 import pizza_user.*;

public class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();

        ManagerBot.setManagers();
        DeliverymanBot.setDeliveryman();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new UserBot());
            botsApi.registerBot(new ManagerBot());
            botsApi.registerBot(new DeliverymanBot());

        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

    }
}
