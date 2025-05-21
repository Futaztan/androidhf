package com.androidhf.data.enums

enum class SavingsType(val displayName: String) {
    INCOMEGOAL_BYTIME("Legyen több az adott összegnél, az idő végére"),         //egy értéket szeretnénk elérni, úgyhogy meg is tartsuk az adott ideig (Legyen legalább ennyi pénzünk az idő végén)
    INCOMEGOAL_BYAMOUNT("Bevételi cél"),                                        //csak annyi hogy egy értéket szeretnénk elérni, mielőtt eléri az időt (szerezzünk legalább ennyi pénzt mire lejár az idő)
    EXPENSEGOAL_BYAMOUNT("Költés limitálás")                                    //egy adott idő alatt ne költsünk ennyit (nem költhetünk összesen ennél többet a megadott idő alatt)
}