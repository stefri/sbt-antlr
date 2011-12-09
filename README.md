# sbt-antlr

A plugin for sbt-0.10.x that generates code based on an antlr grammar.


## Usage

Depend on the plugin: `./project/plugins/build.sbt`

    resolvers += "stefri" at "http://stefri.github.com/repo/snapshots"

    addSbtPlugin("com.github.stefri" % "sbt-antlr" % "0.2-SNAPSHOT")

Place your ANTLR3 grammar files in `src/main/antlr3` and they will be
included in your next build. Note, `sbt-antlr` generates the source code
only once as long as your grammar file didn't change it does not
re-generate the java source files.


## Include Plugin Settings

Include the settings from `sbtantlr.SbtAntlrPlugin.antlrSettings` in
your project build file. See the [SBT wiki page on plugins][1] for
further details.


## Problems and Feature Requests

Please use the issue tracker on github if you find a bug or want to
request a specific feature. Note, this plugin is in early alpha, there
are still lots of things todo - feel free to fork and send a pull
request to improve the codebase.


## License

`sbt-antlr` is licensed under the [Apache 2.0 License][2],
see the `LICENSE.md` file for further details.


## Credits

`sbt-antlr` is inspired by the `antlr3-maven-plugin` used to get a quick
start with the antlr generator tool. Moreover, since this was my first
SBT plugin, some of the already existing code generating SBT plugins
were used to understand the basics (namely the `sbt-protobuf` and
`sbt-xjc` plugins).
  
  [1]: https://github.com/harrah/xsbt/wiki/Plugins
  [2]: http://www.apache.org/licenses/LICENSE-2.0.html
