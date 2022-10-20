package de.k7bot;

import javax.security.auth.login.LoginException;

public class Main {

	public static void main(String[] args) {
		try {
			if (args.length <= 0) {
				Klassenserver7bbot.getInstance(false);
			} else {
				if (args[0].equals("--devmode") || args[0].equals("-d")) {
					Klassenserver7bbot.getInstance(true);
				} else {
					Klassenserver7bbot.getInstance(false);
				}

			}

		} catch (LoginException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
}

