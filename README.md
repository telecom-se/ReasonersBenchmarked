# ReasonersBenchmarked

[ReasonersBenchmark](https://github.com/telecom-se/ReasonersBenchmarked) is the part of [USE-RB](https://github.com/telecom-se/USE-RB).

You first need to clone and compile this project, as the absolute path to the package jar file will be needed as a parameter for USE-RB (see [Launch USE-RB](https://github.com/telecom-se/USE-RB/blob/master/README.md#launch))

This is a two-step process :

1. Clone it : `git clone https://github.com/telecom-se/ReasonersBenchmarked.git`
1. Few libraries are not online and are therefore packaged with `ReasonerBenchmark` project. We setup a local repository within the project. The repository is named `<id>in-project</id>` in the `pom.xml` file. You must modify the value of the location of this repository in the `pom.xml`file to where you actually clone the project. This step will be removed in next release, but until then, manual editing, sorry !
3. Create a uber JAR file :  `mvn clean install package`

You should now have a uber jar in the `target`folger which is named `ReasonersBenchmarked-0.0.1-SNAPSHOT.jar`.

<a name="licence"></a>
## License

Copyright © 2016 Christophe Gravier <christophe.gravier@univ-st-etienne.fr>
This work is free. You can redistribute it and/or modify it under the terms of the Do What The Fuck You Want To Public License, Version 2, as published by Sam Hocevar. See the [LICENCE.md](https://github.com/telecom-se/USE-RB/blob/master/LICENCE.md) file for more details.
![http://www.wtfpl.net](http://www.wtfpl.net/wp-content/uploads/2012/12/wtfpl-badge-4.png)
