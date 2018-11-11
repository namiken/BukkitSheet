package lbn.spread.sheet.command;

import java.util.List;

import lbn.spread.sheet.Authentication;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

public class TestSheetCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		try {
			SpreadsheetService service = Authentication.getService();
			List<SpreadsheetEntry> findAllSpreadsheets = Authentication.findAllSpreadsheets(service);
			for (SpreadsheetEntry spreadsheetEntry : findAllSpreadsheets) {
				sender.sendMessage("title:" + spreadsheetEntry.getTitle().getPlainText());
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return true;
	}
	
}
