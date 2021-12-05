package me.aiglez.mmspsimulator.data

import com.microsoft.playwright.Page

class Rank(override val id: Int, override val name: String) : Identifiable() {

    private val ranks: MutableMap<StepIndex, Salary> = mutableMapOf()

    fun loadStepIndexes(page: Page) {
        page.locator("#echellonSelectId > div:nth-child(4)").click()

        val elements = page.waitForSelector("#echellonSelectId_items").querySelectorAll("li")
        for (element in elements.drop(1)) {
            element.click()

            val stepIndex = StepIndex(element.innerHTML() ?: "TC null")

            println("    @ Getting salary of ${stepIndex.id}")
            this.ranks[stepIndex] = stepIndex.getSalary(page)

            page.waitForSelector("#echellonSelectId > div:nth-child(4)").click()
        }
    }
}