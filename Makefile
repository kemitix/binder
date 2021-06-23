graphs:
	mvn validate

install: .install

.install:
	mvn install
	touch .install

build:
	mvn package

test:
	mvn test

dev:
	mvn -pl binder quarkus:dev \
		-Dquarkus.args="~/cossmass/issues/006-2021-09-september/"

run:
	( \
		cd binder/target/quarkus-app && \
		java \
			--class-path "../../../*/target/" \
			-jar quarkus-run.jar \
			~/cossmass/issues/006-2021-09-september/ \
	)

clean:
	mvn clean
	if [ -f .install ];then rm .install;fi
