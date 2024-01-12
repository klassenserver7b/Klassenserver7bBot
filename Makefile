default:
	mvn clean
	mvn -B package
	cp target/k7bot-*-full.jar ~/Desktop/Bot/Bot_pvt.jar
	
clean:
	mvn clean
	
package:
	mvn -B package
	
copy:
	cp target/k7bot-*-full.jar ~/Desktop/Bot/Bot_pvt.jar
	
restart:
	sudo systemctl restart k7bot

start: default
	sudo systemctl start k7bot

stop:
	sudo systemctl stop k7bot