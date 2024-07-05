import com.ionspin.kotlin.bignum.decimal.times
import kotlinx.serialization.Serializable
import util.Gelds
import util.GeldsSerializer
import util.gelds
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class GameState(
    @Serializable(with = GeldsSerializer::class)
    internal val stashedMoney: Gelds,
    val workers: List<GameWorker>,
    val availableJobs: List<GameJob> = listOf(
        GameJob(1, Level(1, 50.gelds, 20.gelds, 1.seconds), "Einfaches Brot"),
        GameJob(2, Level(1, 500.gelds, 50.gelds, 1.seconds),"Brezel"),
        GameJob(3, Level(1, 1500.gelds, 225.gelds, 3.seconds),"Franzbrötchen"),
        GameJob(4, Level(1, 5000.gelds, 450.gelds, 6.seconds),"Berliner"),
        GameJob(5, Level(1, 10000.gelds, 5000.gelds, 12.seconds),"Laugenstange")
    ),
)

@Serializable
data class GameWorker(
    val jobId: Int,
    val createdAt: Long,
) {

    fun earnedWorker(job: GameJob, now: Long): Pair<Long, Gelds> {
        val collected = abs((now - createdAt) / job.level.duration.inWholeMilliseconds)
        return collected to collected * job.level.earn
    }
}

@Serializable
data class GameJob(
    val id: Int,
    val level: Level,
    val name: String
)

@Serializable
data class Level(
    val level: Int,
    @Serializable(with = GeldsSerializer::class)
    val cost: Gelds,
    @Serializable(with = GeldsSerializer::class)
    val earn: Gelds,
    val duration: Duration,
) {
    fun upgradeEfficiency() = copy(
        level = level + 1,
        earn = earn * 2,
    )
}
