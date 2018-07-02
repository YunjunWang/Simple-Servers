package simpleservers.simpleservers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Help Information on the UI
 * 
 * @author Yunjun Wang
 *
 */
public class HelpMenu {
	private DBManager dbManager;
	private ResourceBundle r = ResourceBundle.getBundle("simpleservers.simpleservers/i18n");

	public HelpMenu(DBManager dbManager) {
		this.dbManager = dbManager;
	}

	public DBManager getDBManager(){
		return this.dbManager;
	}

	public void getChoice() throws IOException {
		char ignore;
		char choice;

		String optQ = r.getString("OPTION_Q")/*"Choose one (q to quit):"*/; 
		System.out.print(optQ);

		choice = (char) (System.in.read());

		// ignore any further input, only take the first character
		do {
			ignore = (char) (System.in.read());
		} while (ignore != '\n');

		// quit when input is 'q'
		if (choice == 'q') {
			return;
		}
		if (choice != 'q' && (choice < '1' || choice > '5')) {
			String invOpt = r.getString("INVALID_OPTION")/*"Option not valid\n"*/;
			System.out.println(invOpt);
			getChoice();
		} else {
			String newLine = r.getString("NEW_LINE")/*"\n"*/;
			System.out.println(newLine);
			onGetChoice(choice);
		}
	}

	public void onGetChoice(char choice) throws java.io.IOException {
		Scanner input = null;
		String id;
		String name;
		String desc;
		char toCont = '0';

		switch (choice) {
		case '1':
			String opt1Help = r.getString("HELP_OPTION_1")/*"Input ID, name and/or description with space in between followed by enter:"*/;
			System.out.println(opt1Help);
			input = new Scanner(System.in);

			id = input.next();

			name = input.next().toString();
			desc = input.hasNext() ? input.next().toString() : null;

			if (desc != null) {
				dbManager.addServer(id, name, desc);
			} else {
				dbManager.addServer(id, name);
			}
			break;
		case '2':
			String opt2Help = r.getString("HELP_OPTION_2");/*"Input an existing server ID, a new name and/or description followed by enter:"*/;
			System.out.println(opt2Help);
			input = new Scanner(System.in);

			id = input.next();
			name = input.next().toString();
			desc = input.hasNext() ? input.next().toString() : null;
			if (desc != null) {
				dbManager.updateServer(id, name, desc);
			} else {
				dbManager.updateServer(id, name);
			}

			break;
		case '3':
			String opt3Help = r.getString("HELP_OPTION3")/*"Input an existing server ID followed by enter: "*/;
			System.out.println(opt3Help);
			input = new Scanner(System.in);

			id = input.next();
			dbManager.deleteServer(id);
			break;
		case '4':
			int count = dbManager.countServers();
			String opt4Result = r.getString("OPTION_4_RESULT")/*"Total number of servers in the system is: "*/;
			System.out.println(opt4Result + count);
			break;
		case '5':

			String[][] list = getList();
			String[][] subList = null;
			int page    = 0; // starts with 0, so we on the 2nd page
			int perPage = 2;
			int size = list.length;

			int to = 0;
			int from = 0; 

			while(to <= size && toCont != '\n'){
				from = page * perPage;
				to   = (page + 1) * perPage;
				to   = to < size ? to : size;

				subList = getSubList(list, from, to);
				showCurrPage(subList);	
				if(to < size){
					String str = r.getString("HELP_OPTION_5_CONT")/*"To load next page enter 'y'"*/;
					System.out.print(str);
					toCont = (char)System.in.read();
					char ignore;
					do{
						ignore = (char)System.in.read();
					}while(ignore != '\n');

					if(toCont == 'y'){
						page++;
					}
				}else{
					String str = r.getString("HELP_OPTION_5_COMPLETED")/*"All loaded"*/;
					System.out.println(str);
					break;
				}

			}

			break;
		}
		System.out.println();
		getChoice();
	}

	public void showCurrPage(String[][] subList){

		for (int i = 0; i < subList.length; i++) {
			String tabSpc = r.getString("TAB_SPC");/*"\t"*/
			System.out.println(subList[i][0] + tabSpc + subList[i][1]
					+ tabSpc + subList[i][2]);
		}
		System.out.println();
	}

	public String[][] getList(){
		ArrayList<Server> servers = dbManager.getServers();
		int length = servers.size();

		String[][] list    = new String[length][3];
		for(Server s : servers){
			list[servers.indexOf(s)][0] = s.getID();
			list[servers.indexOf(s)][1] = s.getName();
			list[servers.indexOf(s)][2] = s.getDesc();
		}

		return list;
	}

	public String[][] getSubList(String[][] list, int from, int to){
		String[][] subList = null;
		int size = list.length;


		if ( from < size ) {
			subList = Arrays.copyOfRange(list, from, to);
		}

		return subList;
	}
}
