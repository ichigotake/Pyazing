# For Groovy
-dontobfuscate
-keep class org.codehaus.groovy.vmplugin.**
-keep class org.codehaus.groovy.runtime.dgm*
-keepclassmembers class org.codehaus.groovy.runtime.dgm* {
  *;
}
-keepclassmembers class ** implements org.codehaus.groovy.runtime.GeneratedClosure {
  *;
}
-dontwarn org.codehaus.groovy.**
-dontwarn groovy**
-dontwarn me.champeau.gr8confagenda.app.SessionListFragment
