# jel-testcontainers

JEL testcontainers library for the testcontainers framework

[![License](https://img.shields.io/github/license/lorislab/jel-testcontainers?style=for-the-badge&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0)
[![CircleCI](https://img.shields.io/circleci/build/github/lorislab/jel-testcontainers?logo=circleci&style=for-the-badge)](https://circleci.com/gh/lorislab/jel-testcontainers)
[![Maven Central](https://img.shields.io/maven-central/v/org.lorislab.jel/jel-testcontainers?logo=java&style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/org.lorislab.jel/jel-testcontainers)
[![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/lorislab/jel-testcontainers?logo=github&style=for-the-badge)](https://github.com/lorislab/jel-testcontainers/releases/latest)

## Release process

Create new release run
```bash
mvn semver-release:release-create
```

Create new patch branch run
```bash
mvn semver-release:patch-create -DpatchVersion=X.X.0
```
