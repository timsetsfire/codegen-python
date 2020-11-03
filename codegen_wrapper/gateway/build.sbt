
name := "py4j-scoring"

libraryDependencies  ++= Seq(
    "com.opencsv" % "opencsv" % "5.3",
    "net.sf.py4j" % "py4j" % "0.10.9",
     "com.datarobot" % "datarobot-prediction" % "2.1.6" % "provided"
)

// export gateway=/Users/timothy.whittaker/Desktop/sbt-projects/CodegenGateway/target/scala-2.12/codegengateway-assembly-0.1.0-SNAPSHOT.jar
// export codegen=/Users/timothy.whittaker/Desktop/sbt-projects/CodegenGateway/ext_jars/5f46b3719c3324244fb7c447.jar
// java -cp $codegen:$gateway com.github.timsetsfire.gateway.Main 3421 3241 100 100 5f46b3719c3324244fb7c447
// java -cp $codegen -jar $gateway 3421 3241 100 100

// export sgateway=/Users/timothy.whittaker/Desktop/git/codegen-python/codegen_wrapper/gateway-scala/target/scala-2.12/py4j-scoring_2.12-0.1.0-SNAPSHOT.jar