import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.listDirectoryEntries

fun getRepos(): List<Path>
{
    val dirPath = Path(System.getProperty("user.home") + "/repos")
    return dirPath.listDirectoryEntries()
}

fun runFetch(list: List<Path>)
{
    for (repo in list) {
        ProcessBuilder("git", "-C", repo.toString(), "fetch")
            .start()
    }
}

fun runStatus(list: List<Path>)
{
    for (repo in list) {
        val proc = ProcessBuilder("git", "-C", repo.toString(), "status")
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .start()
        val status = proc.inputStream.bufferedReader().readText()
        sendNotif(status, repo)
    }
}

fun sendNotif(status: String, repo: Path)
{
    val currentRepo = repo.toString()
    val aheadString = "Your branch is ahead"
    val behindString = "Your branch is behind"
    val divergedString = "Have diverged"
    val unstagedString = "Changes not staged"

    when {
        status.contains(aheadString) -> ProcessBuilder(
            "notify-send", aheadString, currentRepo
            ).start()
        status.contains(behindString) -> ProcessBuilder(
            "notify-send", behindString, currentRepo
            ).start()
        status.contains(divergedString, ignoreCase = true) -> ProcessBuilder(
            "notify-send", divergedString, currentRepo
            ).start()
        status.contains(unstagedString) -> ProcessBuilder(
            "notify-send", unstagedString, currentRepo
            ).start()
    }
}

fun main()
{
    val repoList = getRepos()
    runFetch(repoList)
    runStatus(repoList)
}