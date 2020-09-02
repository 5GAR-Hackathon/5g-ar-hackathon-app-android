package de.nanogiants.a5garapp.model.datastore

import de.nanogiants.a5garapp.model.entities.domain.Review
import io.github.serpro69.kfaker.Faker
import java.time.LocalDate
import java.util.concurrent.ThreadLocalRandom
import javax.inject.Inject
import kotlin.random.Random

class ReviewDatastoreImpl @Inject constructor() : ReviewDatastore {

  override suspend fun getReviewsForPOI(id: Int): List<Review> {
    val faker = Faker()
    val count = Random.nextInt(1, 9)

    return Array(count) {
      val username = getUsername()
      val content = faker.backToTheFuture.quotes()

      val createdAt = between(LocalDate.of(2020, 1, 1), LocalDate.now()).toString()
      val rating = Random.nextInt(1, 5) / 1.0f
      val likeCount = Random.nextInt(0, 10)

      Review(id, username, content, createdAt, rating, likeCount)
    }.toList().sortedByDescending { it.createdAt }
  }

  private fun between(startInclusive: LocalDate, endExclusive: LocalDate): LocalDate? {
    val startEpochDay: Long = startInclusive.toEpochDay()
    val endEpochDay: Long = endExclusive.toEpochDay()
    val randomDay: Long = ThreadLocalRandom
      .current()
      .nextLong(startEpochDay, endEpochDay)
    return LocalDate.ofEpochDay(randomDay)
  }

  private fun getUsername(): String {
    return listOf(
      "aniyah_collins",
      "henriette_mills",
      "arnulfo_hauck",
      "may_jacobi",
      "coby_bailey",
      "rahul_marvin",
      "johnnie_robel",
      "harmony_kautzer",
      "alvera_jones",
      "mossie_kemmer",
      "elyssa_runolfsdottir",
      "alvis_schoen",
      "albertha_schoen",
      "cheyanne_bogan",
      "loy_klein",
      "sim_schroeder",
      "adriel_ohara",
      "charity_ondricka",
      "ada_dach",
      "ricky_deckow",
      "verona_connelly",
      "karson_abshire",
      "kianna_padberg",
      "shany_spencer",
      "granville_graham",
      "peggie_hoppe",
      "jaylon_von",
      "haley_windler",
      "mauricio_weimann",
      "boyd_muller",
      "lyric_swift",
      "angel_jakubowski",
      "antonietta_spinka",
      "angelina_hilll",
      "timothy_morissette",
      "darian_marvin",
      "foster_casper",
      "elizabeth_farrell",
      "carey_rice",
      "stuart_legros",
      "mavis_mckenzie",
      "jameson_morar",
      "shaniya_heaney",
      "deja_harvey",
      "johnnie_crist",
      "itzel_davis",
      "benedict_franecki",
      "roma_kautzer",
      "christelle_lemke",
      "golda_yundt",
      "porter_langosh",
      "napoleon_considine",
      "agustin_cartwright",
      "florence_mraz",
      "mckenna_gleichner",
      "claudie_miller",
      "grant_braun",
      "myah_shanahan",
      "leann_bartoletti",
      "ronaldo_morar",
      "marilie_nicolas",
      "giovani_bashirian",
      "reginald_mraz",
      "lurline_littel",
      "jailyn_lesch",
      "scottie_collins",
      "aiden_reynolds",
      "daisha_mcdermott",
      "delores_hermiston",
      "bertram_cartwright",
      "adrianna_turcotte",
      "marilyne_kuhlman",
      "megane_hermann",
      "kirsten_kovacek",
      "lyric_zboncak",
      "halle_goyette",
      "landen_johnson",
      "celestino_spinka",
      "meghan_trantow",
      "noble_bartoletti",
      "valentina_greenholt",
      "jevon_deckow",
      "wiley_nienow",
      "robb_nitzsche",
      "berta_beatty",
      "lawrence_weissnat",
      "maxie_beier",
      "demario_bashirian",
      "jan_weimann",
      "dayne_casper",
      "dixie_smitham",
      "lea_johnston",
      "ari_reilly",
      "wilfred_zemlak",
      "magnus_bahringer",
      "madeline_terry",
      "maurice_kulas",
      "abelardo_lesch",
      "delta_shanahan",
      "beth_donnelly"
    ).shuffled().take(1)[0]
  }
}