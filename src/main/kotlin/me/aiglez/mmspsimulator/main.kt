package me.aiglez.mmspsimulator

import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import me.aiglez.mmspsimulator.data.Category
import me.aiglez.mmspsimulator.data.StepIndex.Companion.locality
import me.aiglez.mmspsimulator.data.StepIndex.Companion.region
import kotlin.system.exitProcess

fun main() {
    println("Region (default: Région Souss-Massa)")
    region = readLineWithDefault("Région Souss-Massa")

    println("Locality (default: Préfecture d'Agadir Ida-Outanane)")
    locality = readLineWithDefault("Préfecture d'Agadir Ida-Outanane")

    println("Chosen region: $region")
    println("Chosen locality: $locality")

    println("Starting playwright instance...")
    val playwright = Playwright.create()
    val browserContext = playwright.firefox().launch(
        BrowserType.LaunchOptions().setHeadless(false)
    ).newContext()

    println("Loading the page...")
    val page = browserContext.newPage().apply {
        navigate("https://simulation.mmsp.gov.ma/salaire/pages/home.mmsp?localeCode=fr")
    }

    println("Pausing the page...")
    page.pause()

    page.waitForSelector("#categorieSelectId > div:nth-child(4)").click()

    val elements =
        page.waitForSelector("#categorieSelectId_panel > div:nth-child(2) > table:nth-child(1) > tbody:nth-child(1)")
    val categories = mutableListOf<Category>()

    for (element in elements.querySelectorAll("tr").drop(1)) {
        element.click()

        val category = Category(
            element.querySelector("td:nth-child(1)").innerHTML().toInt(),
            element.querySelector("td:nth-child(2)").innerHTML()
        )

        println("Loading bodies of ${category.id} - ${category.name}")
        category.loadBodies(page)
        categories.add(category)

        //println("Pausing the page...")
        //page.pause()
        page.waitForSelector("#categorieSelectId > div:nth-child(4)").click()
    }
    exitProcess(0)
}