[![Travis Build Status](https://travis-ci.com/matsim-up/up-playgrounds.svg?branch=master)](https://travis-ci.com/matsim-up/up-playgrounds)
[![Packagecloud Repository](https://img.shields.io/badge/java-packagecloud.io-844fec.svg)](https://packagecloud.io/matsim-up/up-playgrounds/)


# MATSim UP Playgrounds

The playgrounds of the University of Pretoria's MATSim team. Unless changed explicitly
in the specific playground's `pom.xml` file, the playgrounds depend on the latest stable
MATSim release, currently `0.10.0`. Continuous integration (CI) is done on 
[Travis-CI](https://travis-ci.com/matsim-up/up-playgrounds) and (snapshot) jars are 
deployed to [packagecloud](https://packagecloud.io/matsim-up/up-playgrounds).

## Use in external projects

To use up-playgrounds as dependencies in an external maven project, update the external 
project's `pom.xml` in the following way:

1. Add the up-playgrounds packagecloud repository in the `repositories` section:

```
<repositories>
	<repository>
		<id>matsim-up-up-playgrounds</id>
		<url>https://packagecloud.io/matsim-up/up-playgrounds/maven2</url>
	</repository>
</repositories>
```

2. Add a dependency for each used playground in the `dependencies` section:

```
<dependencies>
	<dependency>
		<groupId>org.matsim.up.up-playgrounds</groupId>
		<artifactId>$playground_name$</artifactId>
		<version>0.10.0-SNAPSHOT</version>
	</dependency>
</dependencies>
```
