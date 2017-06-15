package bot

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot

class GrabStockQuoteBot : TelegramLongPollingBot() {

	override fun onUpdateReceived(update: Update?) {
		
		// We check if the update has a message and the message has text
		if (update!!.hasMessage() && update.getMessage().hasText()) {
			
			println("Message: " + update.message.getText());
			
			val message = SendMessage() // Create a SendMessage object with mandatory fields
					.setChatId(update.message.getChatId())
					.setText(update.message.getText());

			sendMessage(message); // Call method to send the message

		}
	}

	override fun getBotUsername(): String? {
		return "GrabStockQuoteBot"
	}

	override fun getBotToken(): String? {
		return "436951744:AAFfRktcl2OnCX6NvP4l3ibdEeVBt8LTaI8"
	}
}