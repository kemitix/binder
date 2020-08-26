graphs:
	mvn validate

install: .install

.install:
	mvn install
	touch .install

test:
	mvn test ${RUN_PARAMS}

dev:
	mvn -pl runner quarkus:dev ${RUN_PARAMS}

clean:
	mvn clean
	if [ -f .install ];then rm .install;fi
