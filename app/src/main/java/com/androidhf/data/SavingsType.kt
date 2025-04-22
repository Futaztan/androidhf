package com.androidhf.data

enum class SavingsType(val displayName: String) {
    INCOMEGOAL_BYTIME("Legyen több az adott összegnél, az idő végére"),         //egy értéket szeretnénk elérni, úgyhogy meg is tartsuk az adott ideig (Legyen legalább ennyi pénzünk az idő végén)
    INCOMEGOAL_BYAMOUNT("Bevételi cél"),                                        //csak annyi hogy egy értéket szeretnénk elérni, mielőtt eléri az időt (szerezzünk legalább ennyi pénzt mire lejár az idő)
    EXPENSEGOAL_BYTIME("Ne legyen az adott összeg alatt, az idő végére"),       //adott idő végére legyen több pénzünk mint ez (költhetünk bármit ha az idő elértéig több pénzünk lesz mint amit megadtunk) (a megadott idő végén legyen legalább ennyi pénzünk)
    EXPENSEGOAL_BYAMOUNT("Költés limitálás")                                    //egy adott idő alatt ne költsünk ennyit (nem költhetünk összesen ennél többet a megadott idő alatt)
}