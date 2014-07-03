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

 - src -- scala source code (and .html resources etc.)
          especially class `com.madmode.scgstats.Go`
          under `src/main/scala`.
 - build.sbt -- project description and dependencies
 - project -- more sbt stuff
 - Procfile -- heroku deployment descriptor
 - system.properties -- also required for heroku deployment

See also:
  - [A Start in the Craft of Quality Software Development][1]

[1]: http://www.madmode.com/2014/06-pada1.html

<div>
<a href="https://travis-ci.org/dckc/pada1/builds">
 <img alt="build status" src="https://travis-ci.org/dckc/pada1.svg?branch=master"/>
</a>
</div>
