package de.k7bot;

import javax.security.auth.login.LoginException;

public class Main {

	public static void main(String[] args) {
		try {
			if (args.length <= 0) {
				new Klassenserver7bbot(false);
			} else {
				if (args[0].equals("--devmode") || args[0].equals("-d")) {
					new Klassenserver7bbot(true);
				} else {
					new Klassenserver7bbot(false);
				}

			}

		} catch (LoginException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
}

