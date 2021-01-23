graphs:
	mvn validate

install: .install

.install:
	mvn install
	touch .install

test:
	mvn test ${RUN_PARAMS}

dev:
	mvn -pl binder quarkus:dev \
		-Dquarkus.args="~/cossmass/issues/005-2021-05-may/"

run:
	( \
		cd binder/target && \
		java -jar binder-runner.jar \
			~/cossmass/issues/005-2021-05-may/ \
	)

clean:
	mvn clean
	if [ -f .install ];then rm .install;fi
