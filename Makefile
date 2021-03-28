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
		-Dquarkus.args="~/cossmass/issues/006-2021-09-september/"

run:
	( \
		cd binder/target && \
		java -jar binder-runner.jar \
			~/cossmass/issues/006-2021-09-september/ \
	)

clean:
	mvn clean
	if [ -f .install ];then rm .install;fi
