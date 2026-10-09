package ru.prohor.universe.padawan.kotlin.scripts

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * Время выплаты - до аванса за текущий месяц или после
 */
private enum class PayoutTime { BEFORE, AFTER, TOGETHER }

/**
 * Информация об одной выплате - отпускном или премии
 */
private data class Payout(
    val gross: Double,
    val payoutTime: PayoutTime = PayoutTime.TOGETHER
)

/**
 * Информация об одном расчетном листе
 */
private data class Payslip(
    // Год
    val year: Int,
    // Месяц
    val month: Int,
    // Общий облагаемый доход
    val taxableIncomeToDate: Double,
    // Оклад
    val monthlySalary: Double,
    // Отработано дней
    val workedDays: Int,
    // Ставка НДФЛ
    val taxRate: Double = 0.13,
    // Доля аванса
    val advanceShareOfFullSalary: Double = 0.40,
    // Вознаграждение за служебные объекты
    val inventionRewardGross: Double = 1.00,
    // Премия
    val bonus: Payout = Payout(0.0),
    // Оплата труда по среднему заработку за время командировки
    val businessTripGross: Double = 0.0,
    // Отпуска
    val vacations: List<Payout> = emptyList(),
    // Компенсация за питание
    val mealCost: Double = 0.0,
    // Перерасход за питание
    val mealOverspend: Double = 0.0,
    // Суточные сверх нормы
    val excessDailyAllowance: Double = 0.0,
    // Проверять ли строго порядок и сумму отдельных выплат
    val strict: Boolean = true
)

fun main() {

}

private fun calculate(payslip: Payslip) {
    val workingDays = workingDaysCountMap[payslip.year]?.get(payslip.month)
        ?: throw IllegalArgumentException("Illegal: ${payslip.year}-${payslip.month}")
    require(workingDays > 0 && payslip.workedDays in 0..workingDays) { "Проверьте рабочие и отработанные дни" }
    require(payslip.taxRate in 0.0..1.0 && payslip.advanceShareOfFullSalary in 0.0..1.0)

    val salaryGross = rub(payslip.monthlySalary).multiply(BigDecimal(payslip.workedDays))
        .divide(BigDecimal(workingDays), 2, RoundingMode.HALF_UP)
    val bonusGross = rub(payslip.bonus.gross)
    val vacationGross = payslip.vacations.fold(BigDecimal.ZERO) { total, vacation -> total + rub(vacation.gross) }
    val accrued = sum(
        salaryGross, rub(payslip.inventionRewardGross), bonusGross, rub(payslip.businessTripGross), vacationGross
    )
    val nonCash = rub(payslip.mealCost) + rub(payslip.excessDailyAllowance)
    val currentTaxable = rub(payslip.taxableIncomeToDate)

    val priorTaxable = currentTaxable - accrued - nonCash
    require(priorTaxable.signum() >= 0) { "Общий облагаемый доход меньше дохода за месяц" }
    val rate = BigDecimal.valueOf(payslip.taxRate)
    val tax = taxOn(currentTaxable, rate) - taxOn(priorTaxable, rate)
    val deductions = tax + rub(payslip.mealOverspend)
    val totalToBank = accrued - deductions

    println("=== расчет ===")
    println("Отработано дней:            ${payslip.workedDays}/$workingDays")
    println("Оклад за отработанные дни:  ${money(salaryGross)}")
    println("Всего начислено:            ${money(accrued)}")
    println("Всего удержано:             ${money(deductions)}")
    println("  в том числе НДФЛ:         ${money(tax)}")
    println("Всего натуральных доходов:  ${money(nonCash)}")
    println("Общий облагаемый доход:     ${money(currentTaxable)}")
    println("Всего выплат (расчет):      ${money(totalToBank)}")

    var taxableAtPayment = priorTaxable
    var withheldAtPayment = taxOn(priorTaxable, rate)
    val separatePayments = mutableListOf<Pair<String, BigDecimal>>()

    fun addSeparatePayment(name: String, gross: BigDecimal) {
        if (gross.signum() == 0) return
        taxableAtPayment += gross
        val nextWithheld = taxOn(taxableAtPayment, rate)
        separatePayments += name to (gross - (nextWithheld - withheldAtPayment))
        withheldAtPayment = nextWithheld
    }

    if (payslip.bonus.payoutTime == PayoutTime.BEFORE) addSeparatePayment("Премия", bonusGross)
    payslip.vacations.forEachIndexed { index, vacation ->
        if (vacation.payoutTime == PayoutTime.BEFORE) {
            addSeparatePayment("Отпускные ${index + 1}", rub(vacation.gross))
        }
    }

    val advanceGross = rub(payslip.monthlySalary).multiply(BigDecimal.valueOf(payslip.advanceShareOfFullSalary))
        .setScale(2, RoundingMode.HALF_UP)
    addSeparatePayment("Аванс", advanceGross)

    if (payslip.bonus.payoutTime == PayoutTime.AFTER) addSeparatePayment("Премия", bonusGross)
    payslip.vacations.forEachIndexed { index, vacation ->
        if (vacation.payoutTime == PayoutTime.AFTER) {
            addSeparatePayment("Отпускные ${index + 1}", rub(vacation.gross))
        }
    }

    val alreadyPaid = separatePayments.fold(BigDecimal.ZERO) { total, payment -> total + payment.second }
    val finalSalaryPayment = totalToBank - alreadyPaid
    println("\n=== Расчетное распределение платежей ===")
    separatePayments.forEach { (name, amount) -> println("$name: ${money(amount)}") }
    println("Окончательный платеж: ${money(finalSalaryPayment)}")
    if (finalSalaryPayment.signum() < 0) {
        println("Проверьте параметры отдельных выплат: их сумма больше общего итога.")
    }
}

val workingDaysCountMap = mapOf(
    2025 to mapOf(
        1 to 17, 2 to 20, 3 to 21, 4 to 22, 5 to 18, 6 to 19,
        7 to 23, 8 to 21, 9 to 22, 10 to 23, 11 to 19, 12 to 22,
    ),
    2026 to mapOf(
        1 to 15, 2 to 19, 3 to 21, 4 to 22, 5 to 19, 6 to 21,
        7 to 23, 8 to 21, 9 to 22, 10 to 22, 11 to 20, 12 to 22,
    ),
)

private fun taxOn(taxableIncome: BigDecimal, rate: BigDecimal): BigDecimal {
    return taxableIncome.multiply(rate).setScale(0, RoundingMode.HALF_UP).setScale(2)
}

private fun sum(vararg amounts: BigDecimal) = amounts.fold(BigDecimal.ZERO) { total, amount -> total + amount }

private fun rub(amount: Double) = BigDecimal.valueOf(amount).setScale(2, RoundingMode.UNNECESSARY)

private fun money(amount: BigDecimal) = String.format(Locale.forLanguageTag("ru-RU"), "%,.2f ₽", amount)
