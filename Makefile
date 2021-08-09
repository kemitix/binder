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
	mvn -pl binder quarkus:dev

run:
	( \
		cd binder/target/quarkus-app && \
		java \
			--class-path "../../../*/target/" \
			-jar quarkus-run.jar \
	)

clean:
	mvn clean
	if [ -f .install ];then rm .install;fi
