import com.cinchfinancial.neofact.dsl.model
import java.math.BigDecimal

model {
    // Facts
    val a_fact_name: String by facts
    val another_fact: List<BigDecimal> by facts
    val missing_fact: Boolean? by facts

    // Inputs
    val an_input by formula { a_fact_name }
    val another_input by formula { another_fact[0] }
    val yet_another by formula { another_fact.reduce { acc, bigDecimal -> acc + bigDecimal } }
    val fancy_one by formula {
        when (an_input) {
            null -> false
            else -> true
        }
    }
    val missing_fact_input by formula { missing_fact ?: false || a_fact_name.isEmpty() }

    // Rules
    rule {
        eval { another_input == another_fact[1] }
        recommend outcome "MYOUT" because "ITHURTS"
        recommend against "ANOTHER" because "WHYNOT"
    }
    rule {
        eval { another_fact.size > 2 }
        recommend outcome "MYOUT" because "DIFFERENT"
    }

    rule {
        eval { fancy_one && yet_another > BigDecimal.ZERO }
        recommend outcome "FOOEY" because "WOWY"
    }

    rule {
        eval { println(an_input); an_input?.length ?: 0 > 2 }
        recommend outcome "FOOEY" because "WOWY" attribute Pair("key", 3) options mapOf(
            "key1" to 3,
            "key2" to 34.5,
            "key3" to yet_another
        )

    }

}