default: clean package copy
	
clean:
	mvn clean
	
package:
	mvn package

copy:
	sudo cp target/k7bot-*-full.jar /opt/k7bot/Bot.jar
	
restart:
	sudo systemctl restart k7bot

start:
	sudo systemctl start k7bot

stop:
	sudo systemctl stop k7bot
	
startb: default
	sudo systemctl start k7bot
	
restartb: default
	sudo systemctl restart k7bot