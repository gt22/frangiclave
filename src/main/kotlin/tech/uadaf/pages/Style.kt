package tech.uadaf.pages

import tech.uadaf.config
import tech.uadaf.theme

class Sass {
    val imported: MutableSet<String> = mutableSetOf()
    val importMatcher = "^\\s*@import\\s+\"?(.+?)\"?\\n?\$".toRegex()
    private fun prepareSass(path: String): String {
        if(!imported.add(path)) return ""
        val dirPath = path.substringBeforeLast("/") + "/"
        val res = StringBuilder()
        javaClass.getResourceAsStream(path).use {
            it.bufferedReader().lines().map { line -> line.replace("{theme}", theme.id) }.forEach { line ->
                val match = importMatcher.matchEntire(line)
                if(match != null) {
                    var targetPath = dirPath + match.groupValues[1]
                    if(!targetPath.endsWith(".sass")) {
                        targetPath += ".sass"
                    }
                    res.append(prepareSass(targetPath))
                } else {
                    res.append(line)
                }
                res.append('\n')
            }
        }
        return res.toString()
    }

    fun convertSass(path: String): String {
        val sass = ProcessBuilder("sass", "--stdin", "--indented").start()
        try {
            val data = prepareSass(path)
            sass.outputStream.use { it.write(data.toByteArray()) }
            sass.inputStream.bufferedReader().use {
                return it.readText()
            }
        } finally {
            if (sass.isAlive) {
                sass.destroyForcibly()
            }
        }
    }
}

val stylesheet by lazy { Sass().convertSass("/style/index.sass") }
val lighttheme by lazy { Sass().convertSass("/style/themes/${theme.id}/theme_light.sass") }
val darktheme by lazy { Sass().convertSass("/style/themes/${theme.id}/theme_dark.sass") }