package me.aiglez.mmspsimulator.data

import com.microsoft.playwright.Page

class Category(override val id: Int, override val name: String) : Identifiable() {

    private val bodies: MutableList<Body> = mutableListOf()

    fun loadBodies(page: Page) {
        page.locator("#corpsSelectId > div:nth-child(4)").click()

        val elements =
            page.waitForSelector("#corpsSelectId_panel > div:nth-child(2) > table:nth-child(1) > tbody:nth-child(1)")

        for (element in elements.querySelectorAll("tr").drop(1)) {
            element.click()

            val body = Body(
                element.querySelector("td:nth-child(1)").innerHTML().toInt(),
                element.querySelector("td:nth-child(2)").innerHTML()
            )

            println("- Loading work of ${body.id} - ${body.name}")
            body.loadWork(page)
            bodies.add(body)

            //println("- Pausing the page...")
            //page.pause()
            page.waitForSelector("#corpsSelectId > div:nth-child(4)").click()
        }
    }
}