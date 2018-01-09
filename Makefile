all: compile
	@echo -e '[INFO] Done!\n' 
clean:
	@echo -e '\n[INFO] Cleaning Up..'
	@-mkdir temp
	@-cp bin/*.pgm temp 
	@-rm -rf bin

compile: clean
	@-mkdir bin
	@-cp temp/*.pgm bin
	@-rm -rf temp
	@-cp machine-list.txt bin
	@echo -e '[INFO] Compiling the Source..'
	@javac -d bin src/**/*.java


