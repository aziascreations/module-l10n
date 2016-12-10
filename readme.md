# L10N Module


## Installation
The module isn't on the Maven Central Repository, therefore a third-party repository is required.

You have to add the [JitPack](https://jitpack.io/) repository to you project's pom.xml file.
```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```

And this to your dependencies.
```xml
<dependency>
	<groupId>com.github.aziascreations</groupId>
	<artifactId>module-l10n</artifactId>
	<version>-SNAPSHOT</version>
</dependency>
```

If you are using another build automation tool or if want to download it, go check the JitPack page: https://jitpack.io/#aziascreations/module-versioning/

## Usage
Firstly, you have to import `com.azias.module.l10n.Localizer` in your code.


## License
[Public Domain](LICENSE)