package me.aiglez.mmspsimulator.data

import com.microsoft.playwright.Page

class Work(override val id: Int, override val name: String) : Identifiable() {

    private val ranks: MutableList<Rank> = mutableListOf()

    fun loadRanks(page: Page) {
        page.locator("#gradeSelectId > div:nth-child(4)").click()

        val elements =
            page.waitForSelector("#gradeSelectId_panel > div:nth-child(2) > table:nth-child(1) > tbody:nth-child(1)")

        for (element in elements.querySelectorAll("tr").drop(1)) {
            element.click()

            val rank = Rank(
                element.querySelector("td:nth-child(1)").innerHTML().toInt(),
                element.querySelector("td:nth-child(2)").innerHTML()
            )

            println("   + Loading step indexes of ${rank.id} - ${rank.name}")
            rank.loadStepIndexes(page)

            this.ranks.add(rank)

            //println("  * Pausing the page...")
            //page.pause()
            page.waitForSelector("#gradeSelectId > div:nth-child(4)").click()
        }
    }

}