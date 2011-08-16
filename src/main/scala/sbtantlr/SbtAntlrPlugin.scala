package sbtantlr

import sbt._
import Process._
import Keys._
import org.antlr.Tool
import scala.collection.JavaConversions._

object SbtAntlrPlugin extends Plugin {
    
    final case class AntlrConfiguration (
    	report: Boolean,
    	printGrammar: Boolean,
    	debug: Boolean,
    	profile: Boolean,
    	nfa: Boolean,
    	dfa: Boolean,
    	trace: Boolean,
    	messageFormat: String,
    	verbose: Boolean,
    	maxSwitchCaseLabels: Int,
    	minSwitchAlts: Int
    )
    
    val antlrConfig				= config("antlr")
	val generate 				= TaskKey[Seq[File]]("generate")
	val antlrConfiguration		= TaskKey[AntlrConfiguration]("antlr-configuration")
	val report					= SettingKey[Boolean]("report")
	val printGrammar			= SettingKey[Boolean]("print-grammar")
	val debug					= SettingKey[Boolean]("debug")
	val profile					= SettingKey[Boolean]("profile")
	val nfa						= SettingKey[Boolean]("nfa")
	val dfa						= SettingKey[Boolean]("dfa")
	val trace					= SettingKey[Boolean]("trace")
	val messageFormat 			= SettingKey[String]("message-format")
	val verbose					= SettingKey[Boolean]("verbose")
	val maxSwitchCaseLabels		= SettingKey[Int]("max-switch-case-lables")
	val minSwitchAlts			= SettingKey[Int]("min-switch-alts")
    
    lazy val antlrSettings: Seq[Setting[_]] = inConfig(antlrConfig)(Seq[Setting[_]](
    	sourceDirectory 	<<= (sourceDirectory in Compile) { _ / "antlr3" },
    	javaSource 			<<= (sourceManaged in Compile) { _ / "antlr3" },
    	version 			:= "3.3",
    	report 				:= false,
    	printGrammar 		:= false,
    	debug 				:= false,
    	profile 			:= false,
    	nfa					:= false,
    	dfa					:= false,
    	trace				:= false,
    	verbose				:= false,
    	messageFormat		:= "antlr",
    	maxSwitchCaseLabels	:= 300,
    	minSwitchAlts		:= 3,
    	
    	managedClasspath 	<<= (classpathTypes in antlrConfig, update) map { (ct, report) =>
    	    Classpaths.managedJars(antlrConfig, ct, report)
    	},
    	antlrConfiguration	<<= antlrConfigurationTask,
    	generate 			<<= sourceGeneratorTask
    )) ++ Seq[Setting[_]](
        sourceGenerators in Compile <+= (generate in antlrConfig).identity,
        cleanFiles <+= (javaSource in antlrConfig).identity,
        libraryDependencies <+= (version in antlrConfig)("org.antlr" % "antlr" % _),
        ivyConfigurations += antlrConfig
    )
    
    private def antlrConfigurationTask = (report, printGrammar, debug, profile, nfa, dfa, trace, verbose, messageFormat) map {
        (report, printGrammar, debug, profile, nfa, dfa, trace, verbose, messageFormat) =>
            AntlrConfiguration(report, printGrammar, debug, profile, nfa, dfa, trace, messageFormat, verbose, 300, 3)
    }
    
    private def sourceGeneratorTask = (streams, sourceDirectory in antlrConfig, javaSource in antlrConfig, antlrConfiguration, cacheDirectory) map {
        (out, srcDir, targetDir, options, cache) =>
            val cachedCompile = FileFunction.cached(cache / "antlr3", inStyle = FilesInfo.lastModified, outStyle = FilesInfo.exists) { (in: Set[File]) =>
                generateWithAntlr(srcDir, targetDir, options, out.log)
            }
            cachedCompile((srcDir ** "*.g").get.toSet).toSeq
    }
	
	private def generateWithAntlr(srcDir: File, target: File, options: AntlrConfiguration, log: Logger) = {
	    printAntlrOptions(log, options)
	    
	    // prepare target
	    target.mkdirs()
	    
	    // configure antlr tool
	    val antlr = new Tool
	    log.info("Using ANTLR version %s to generate java source files.".format(antlr.VERSION))
	    antlr.setDebug(options.debug)
	    antlr.setGenerate_DFA_dot(options.dfa)
	    antlr.setGenerate_NFA_dot(options.nfa)
	    antlr.setProfile(options.profile)
	    antlr.setReport(options.report)
	    antlr.setPrintGrammar(options.printGrammar)
	    antlr.setTrace(options.trace)
	    antlr.setVerbose(options.verbose)
	    antlr.setMessageFormat(options.messageFormat)
	    antlr.setMaxSwitchCaseLabels(options.maxSwitchCaseLabels)
	    antlr.setMinSwitchAlts(options.minSwitchAlts)
	    
	    // propagate source and target path to antlr
	    antlr.setInputDirectory(srcDir.getPath)
	    antlr.setOutputDirectory(target.getPath)
	    
	    // tell antlr that we always want the output files to be produced in the output directory
	    // using the same relative path as the input file was to the input directory.
	    antlr.setForceRelativeOutput(true)
	    
	    // tell the antlr tool that we want sorted build mode
	    antlr.setMake(true)
	    
	    // process grammars
	    val grammars = (srcDir ** "*.g").get
	    log.info("Generating java source files for %d found ANTLR3 grammars.".format(grammars.size))
	    
	    // add each grammar file into the antlr tool's list of grammars to process
	    grammars foreach { g =>
	        val relPath = g relativeTo srcDir
	        log.info("Grammar file '%s' detected.".format(relPath.get.getPath))
	        antlr.addGrammarFile(relPath.get.getPath)
	    }
	    
	    // process all grammars
	    antlr.process
	    if (antlr.getNumErrors > 0) {
	        log.error("ANTLR caught %d build errors.".format(antlr.getNumErrors))
	    }
	    
	    (target ** "*.java").get.toSet
	}

    private def printAntlrOptions(log: Logger, options: AntlrConfiguration) {
        log.debug("ANTLR: report              : " + options.report);
        log.debug("ANTLR: printGrammar        : " + options.printGrammar);
        log.debug("ANTLR: debug               : " + options.debug);
        log.debug("ANTLR: profile             : " + options.profile);
        log.debug("ANTLR: nfa                 : " + options.nfa);
        log.debug("ANTLR: dfa                 : " + options.dfa);
        log.debug("ANTLR: trace               : " + options.trace);
        log.debug("ANTLR: messageFormat       : " + options.messageFormat);
        log.debug("ANTLR: maxSwitchCaseLabels : " + options.maxSwitchCaseLabels);
        log.debug("ANTLR: minSwitchAlts       : " + options.minSwitchAlts);
        log.debug("ANTLR: verbose             : " + options.verbose);
    }

}
