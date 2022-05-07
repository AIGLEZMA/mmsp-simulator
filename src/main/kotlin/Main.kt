import com.github.doyaaaaaken.kotlincsv.client.KotlinCsvExperimental
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import kotlin.math.roundToLong

@OptIn(KotlinCsvExperimental::class)
fun main() {
    println("Launching mmsp-simulation software...")

    println("Please choose a region (default: Région Souss-Massa):")
    val region = readLineWithDefault("Région Souss-Massa")

    println("Please choose a locality (default: Préfecture d'Agadir Ida-Outanane):")
    val locality = readLineWithDefault("Préfecture d'Agadir Ida-Outanane")

    println("Ok. starting the real stuff...")

    val browserContext = Playwright.create().chromium()
        .launch(BrowserType.LaunchOptions().apply { headless = false; channel = "msedge" }).newContext()
    val page = browserContext.newPage()

    page.navigate("https://simulation.mmsp.gov.ma/salaire/pages/home.mmsp?localeCode=fr")
    page.pause()

    val categorySelectButton =
        page.locator("#categorieSelectId > div.ui-selectonemenu-trigger.ui-state-default.ui-corner-right > span")
    categorySelectButton.click()

    val categories = page.locator("#categorieSelectId_panel > div.ui-selectonemenu-items-wrapper > table > tbody > tr")
    val categoriesTable = page.locator("#categorieSelectId_panel > div.ui-selectonemenu-items-wrapper > table")
    categoriesTable.waitFor()

    page.pause()

    val salaries = mutableListOf<Salary>()
    for (i in 1 until categories.count()) {
        val category = page.locator("#categorieSelectId_$i")

        val categoryId = category.locator("td:nth-child(1)").innerHTML().toInt()
        val categoryName = category.locator("td:nth-child(2)").innerHTML()

        println("Loading bodies of ($categoryId) $categoryName")
        category.click()

        // bodies
        val bodySelectButton =
            page.locator("#corpsSelectId > div.ui-selectonemenu-trigger.ui-state-default.ui-corner-right > span")
        bodySelectButton.click()

        val bodies = page.locator("#corpsSelectId_panel > div.ui-selectonemenu-items-wrapper > table > tbody > tr")
        val bodiesTable = page.locator("#corpsSelectId_panel > div.ui-selectonemenu-items-wrapper > table")
        bodiesTable.waitFor()

        for (j in 1 until bodies.count()) {
            val body = bodies.nth(j)

            val bodyId = body.locator("td:nth-child(1)").innerHTML().toInt()
            val bodyName = body.locator("td:nth-child(2)").innerHTML()

            println(" - Loading work of ($bodyId) $bodyName")
            body.click()

            // work
            val workSelectButton =
                page.locator("#cadreSelectId > div.ui-selectonemenu-trigger.ui-state-default.ui-corner-right > span")
            workSelectButton.click()

            val works = page.locator("#cadreSelectId_panel > div.ui-selectonemenu-items-wrapper > table > tbody > tr")
            val worksTable = page.locator("#cadreSelectId_panel > div.ui-selectonemenu-items-wrapper > table")
            worksTable.waitFor()

            for (k in 1 until works.count()) {
                val work = works.nth(k)

                val workId = work.locator("td:nth-child(1)").innerHTML().toInt()
                val workName = work.locator("td:nth-child(2)").innerHTML()

                println("  - Loading ranks of ($workId) $workName")
                work.click()

                // rank
                val rankSelectButton =
                    page.locator("#gradeSelectId > div.ui-selectonemenu-trigger.ui-state-default.ui-corner-right > span")
                rankSelectButton.click()

                val ranks =
                    page.locator("#gradeSelectId_panel > div.ui-selectonemenu-items-wrapper > table > tbody > tr")
                val ranksTable = page.locator("#gradeSelectId_panel > div.ui-selectonemenu-items-wrapper > table")
                ranksTable.waitFor()

                for (l in 1 until ranks.count()) {
                    val rank = ranks.nth(l)

                    val rankId = rank.locator("td:nth-child(1)").innerHTML().toInt()
                    val rankName = rank.locator("td:nth-child(2)").innerHTML()

                    println("   - Loading step indexes of ($rankId) $rankName")
                    rank.click()

                    // step indexes
                    val stepIndexSelectButton =
                        page.locator("#echellonSelectId > div.ui-selectonemenu-trigger.ui-state-default.ui-corner-right > span")
                    stepIndexSelectButton.click()

                    val stepIndexes = page.locator("#echellonSelectId_items > li")
                    val stepIndexesTable = page.locator("#echellonSelectId_items")
                    stepIndexesTable.waitFor()

                    for (m in 1..1) {
                        val stepIndex = stepIndexes.nth(m)

                        val stepIndexId = stepIndex.innerHTML()

                        println("    - Getting salary of $stepIndexId")
                        stepIndex.click()

                        // salary

                        //  region
                        val regionSelectButton =
                            page.locator("#regionSelectId > div.ui-selectonemenu-trigger.ui-state-default.ui-corner-right > span")
                        regionSelectButton.click()

                        val regions = page.locator("#regionSelectId_items > li")
                        val regionsTable = page.locator("#regionSelectId_items")
                        regionsTable.waitFor()

                        for (n in 1 until regions.count()) {
                            val _region = regions.nth(n)
                            if (_region.innerHTML().equals(region)) {
                                _region.click()
                            }
                        }

                        //  location
                        val locationSelectButton =
                            page.locator("#localiteSelectId > div.ui-selectonemenu-trigger.ui-state-default.ui-corner-right > span")
                        locationSelectButton.click()

                        val localities =
                            page.locator("#localiteSelectId_panel > div.ui-selectonemenu-items-wrapper > table > tbody > tr")
                        val localitiesTable =
                            page.locator("#localiteSelectId_panel > div.ui-selectonemenu-items-wrapper > table")
                        localitiesTable.waitFor()

                        for (n in 1 until localities.count()) {
                            val _locality = localities.nth(n)
                            if (_locality.locator("td:nth-child(1)").innerHTML().equals(locality)) {
                                _locality.click()
                            }
                        }

                        val calculateButton = page.locator("#j_idt193")
                        calculateButton.click()

                        val netSalary = page.locator("#j_idt174_content > div > div:nth-child(2) > span")
                        val netSalaryLong = netSalary.innerHTML().removeSuffix(" dhs")
                            .toDouble().roundToLong()

                        salaries.add(Salary(categoryName, bodyName, workName, rankName, stepIndexId, netSalaryLong))

                        println("     - Net salary: $netSalaryLong MAD")

                        val precedentButton = page.locator("#j_idt190")
                        precedentButton.click()

                        // reset
                        if (m != stepIndexes.count()) {
                            stepIndexSelectButton.click()
                            stepIndexesTable.waitFor()
                        }
                    }

                    // reset
                    if (l != ranks.count()) {
                        rankSelectButton.click()
                        ranksTable.waitFor()
                    }
                }

                // reset
                if (k != works.count()) {
                    workSelectButton.click()
                    worksTable.waitFor()
                }
            }

            // reset
            if (j != bodies.count()) {
                bodySelectButton.click()
                bodiesTable.waitFor()
            }
        }

        // reset
        if (i != categories.count()) {
            categorySelectButton.click()
            categoriesTable.waitFor()
        }
    }

    println("-".repeat(20))

    salaries.sortBy { it.salary }
    for ((i, salary) in salaries.withIndex()) {
        println("$i - ${salary.category} - ${salary.body} - ${salary.work} - ${salary.rank} - ${salary.stepIndex} : ${salary.salary}")
    }
}

private fun readLineWithDefault(default: String): String {
    val line = readLine()
    return if (line != null && line.isNotBlank()) line else default
}

data class Salary(
    val category: String,
    val body: String,
    val work: String,
    val rank: String,
    val stepIndex: String,
    val salary: Long
)