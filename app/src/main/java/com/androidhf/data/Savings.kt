package com.androidhf.data

import java.time.LocalDate
import java.util.Date


//ez a class kezeli a megtakarításokat meg ilyeneket
class Savings(
    amount: Int, startdate: LocalDate, enddate: LocalDate, type: SavingsType, title: String, description: String, start: Int
) {
    var Amount: Int = amount                //a megtakarítási cél
    var StartDate: LocalDate = startdate         //mikor hoztuk létre
    var EndDate: LocalDate = enddate             //mikorra szeretnénk elérni
    var Type: SavingsType = type            //milyen típusú megtakarítást szeretnénk
                                            //incomegoal_bytime, incomegoal_byamount, expensegoal_bytime, expensegoal_byamount
    var Title: String = title               //a megtakarítás neve
    var Description: String = description   //rövid leírása, hogy mit szerettünk volna elérni
    var Start: Int = start                  //kezdő pénz mennyisége
}