import ammonite.ops._, mill._

// sourceRoot -> allSources -> classFiles
//                                |
//                                v
//           resourceRoot ---->  jar

def sourceRoot = T.sources { pwd / 'src }

def resourceRoot = T.sources { pwd / 'resources }

def allSources = T { sourceRoot().flatMap(p => ls.rec(p.path)).map(PathRef(_)) }

def classFiles = T { 
  mkdir(T.ctx().dest)
  import ammonite.ops._
  %("javac", sources().map(_.path.toString()), "-d", T.ctx().dest)(wd = T.ctx().dest)
  PathRef(T.ctx().dest) 
}

def jar = T { Jvm.createJar(Loose.Agg(classFiles().path) ++ resourceRoot().map(_.path)) }

def run(mainClsName: String) = T.command {
  %%('java, "-cp", classFiles().path, mainClsName)
}
