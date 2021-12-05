package me.aiglez.mmspsimulator.data

import com.microsoft.playwright.Page
import kotlin.math.roundToLong

class StepIndex(val id: String) {

    fun getSalary(page: Page): Salary {
        page.locator("#regionSelectId > div:nth-child(4)").click()

        val regionsElements = page.waitForSelector("#regionSelectId_items").querySelectorAll("li")
        for (regionElement in regionsElements) {
            if (regionElement.innerHTML().equals(region)) {
                regionElement.click()
            }
        }

        page.locator("#localiteSelectId > div:nth-child(4)").click()

        val localitiesElements =
            page.waitForSelector("#localiteSelectId_panel > div:nth-child(2) > table:nth-child(1) > tbody:nth-child(1)")

        for (localityElement in localitiesElements.querySelectorAll("tr").drop(1)) {
            if (localityElement.querySelector("td:nth-child(1)").innerHTML().equals(locality)) {
                localityElement.click()
            }
        }

        // real things start here
        page.waitForSelector("#j_idt193").click()

        // get salary
        val net = page.waitForSelector(".resultSalaire").innerHTML().removeSuffix(" dhs")
            .toDouble().roundToLong()
        val brut =
            page.waitForSelector("#j_idt167_content > div:nth-child(1) > div:nth-child(2) > span:nth-child(1)")
                .innerHTML().removeSuffix(" dhs").toDouble().roundToLong()
        val allDeductions =
            page.waitForSelector("#j_idt167_content > div:nth-child(3) > div:nth-child(2) > span:nth-child(1)")
                .innerHTML().removeSuffix(" dhs").toDouble().roundToLong()
        val incomeTax =
            page.waitForSelector("#j_idt153_content > div:nth-child(1) > div:nth-child(2) > span:nth-child(1)")
                .innerHTML().removeSuffix(" dhs").toDouble().roundToLong()
        val cmrContributions =
            page.waitForSelector("#j_idt159_content > div:nth-child(1) > div:nth-child(2) > span:nth-child(1)")
                .innerHTML().removeSuffix(" dhs").toDouble().roundToLong()

        println("     % Salary: $net MAD (deductions: $allDeductions MAD)")

        page.pause()
        page.waitForSelector("#j_idt190").click()

        return Salary(net, brut, allDeductions, incomeTax, cmrContributions)
    }

    companion object {
        lateinit var region: String
        lateinit var locality: String
    }

}