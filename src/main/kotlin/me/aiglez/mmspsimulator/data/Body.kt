package me.aiglez.mmspsimulator.data

import com.microsoft.playwright.Page

class Body(override val id: Int, override val name: String) : Identifiable() {

    private val work: MutableList<Work> = mutableListOf()

    fun loadWork(page: Page) {
        page.locator("#cadreSelectId > div:nth-child(4)").click()

        val elements =
            page.waitForSelector("#cadreSelectId_panel > div:nth-child(2) > table:nth-child(1) > tbody:nth-child(1)")

        for (element in elements.querySelectorAll("tr").drop(1)) {
            element.click()

            val work = Work(
                element.querySelector("td:nth-child(1)").innerHTML().toInt(),
                element.querySelector("td:nth-child(2)").innerHTML()
            )

            println("  * Loading ranks of ${work.id} - ${work.name}")
            work.loadRanks(page)

            this.work.add(work)

            //println("  * Pausing the page...")
            //page.pause()
            page.waitForSelector("#cadreSelectId > div:nth-child(4)").click()
        }
    }
}