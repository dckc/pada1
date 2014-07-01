The idea is to process tournament standings pages. (blah blah...)
Sample bracket: [ARLO][]

[ARLO]: http://challonge.com/ARLOPMS

Authors:
  - dckc
  - FlashingFire

## Dependencies / Acknowledgements

 - [Scala][]
 - [spray][]
 - [jsoup][]
 
[Scala]: http://www.scala-lang.org/documentation/getting-started.html
[spray]: http://spray.io/
[jsoup]: http://jsoup.org/

## Development Notes

Contents:

 - build.sbt -- project description and dependencies
 - project -- more sbt stuff
 - Procfile -- heroku deployment descriptor
 - system.properties -- also required for heroku deployment
 - src -- scala source code (and .html resources etc.)
          especially class `com.madmode.scgstats.Go`
          under `src/main/scala`.

See also:
  - [A Start in the Craft of Quality Software Development][1]

[1]: http://www.madmode.com/2014/06-pada1.html

/**
 * build.sbt -- project description and dependencies
 *
 * This is the gateway between our project and various bits of code
 * from the community that we build on.
 *
 * The way we use it is:
 *
 * 1. Make a basic build.sbt file in largely monkey-see-monkey-do fashion,
 * For example in IntelliJ, make sure you have the [scala plugin][1] installed, and then:
 * "choose File→ New Project on the main menu, or
 * click the New Project icon on the Welcome screen.
 * On the first page of the New Project wizard, in the selector pane,
 * choose SBT. On the right part of the page, enter the necessary information,
 * such as project's name, its location and click Finish"[2]
 * 2. Mix in the example build.sbt[3] from the spray introduction[4], Name, organization,
 * and version are up to us, but using a different scalaVersion or scalacOptions could
 * break thinks. I'm not sure what Revolver.settings does. There's also some mumbo jumbo
 * in project/plugins.sbt. But libraryDependencies is the main thing that integrates
 * spray into our project.
 * 3. As we discover other functionality that we want to get from the community,
 * do a little web searching and research. Then, once we know the name of a package
 * such as "jsoup", use [The Central Repository Search Engine][5] to find the relevant
 * "artifact" (finding the right version and such takes some practice) and then
 * under "Dependency Information" choose "Scala SBT" to get a line you can paste here.
 * **Notice the blank line between every item.** This isn’t just for show;
 * they’re actually required in order to separate each item.[6]
 *
 * In IntelliJ, we may need to Synchronize (in the right click menu of build.sbt)
 * when we make changes. This is roughly the equivalent of the sbt "update" command,
 * which resolves dependencies from the net.
 *
 * [1]: http://confluence.jetbrains.com/display/IntelliJIDEA/Scala
 * [2]: http://confluence.jetbrains.com/display/IntelliJIDEA/Getting+Started+with+SBT
 * [3]: https://github.com/spray/spray-template/blob/on_spray-can_1.1/build.sbt
 * [4]: http://spray.io/introduction/getting-started/
 * [5]: http://search.maven.org/
 * [6]: http://www.scala-sbt.org/0.13/tutorial/Hello.html
 *
 * See also [sbt][] and [maven][] in Wikipedia.
 *
 * [sbt]: http://en.wikipedia.org/wiki/Apache_Maven
 * [maven]: http://en.wikipedia.org/wiki/SBT_(software)
 */
