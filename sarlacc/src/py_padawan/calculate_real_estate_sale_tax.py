def calculate_apartment_sale_tax(
        years_owned: int,
        sale_price: float,
        cadastral_value: float,
        donor_purchase_price: float
) -> dict:
    """Расчет налога при продаже подаренной квартиры в РФ.

    Все суммы передаются в рублях.
    """
    # 1. Проверяем минимальный срок владения (для близких родственников - 3 года)
    if years_owned >= 3:
        return {
            "Налогооблагаемая база": 0,
            "Итоговый налог к уплате": 0,
            "Причина": "Освобождено от налога (владение более 3 лет)",
        }

    # 2. Определяем доход для налоговой (с учетом правила 70% от кадастра)
    min_taxable_income = cadastral_value * 0.7
    real_income = max(sale_price, min_taxable_income)

    # 3. Считаем варианты уменьшения налога (вычеты)
    # Вариант А: Стандартный вычет 1 млн рублей
    tax_base_with_standard_deduction = max(0.0, real_income - 1000000)

    # Вариант Б: Учет расходов дарителя на покупку
    tax_base_with_donor_expenses = max(0.0, real_income - donor_purchase_price)

    # Выбираем самый выгодный вариант для налогоплательщика (где база меньше)
    if tax_base_with_donor_expenses <= tax_base_with_standard_deduction:
        tax_base = tax_base_with_donor_expenses
        method = "Уменьшение дохода на расходы дарителя"
    else:
        tax_base = tax_base_with_standard_deduction
        method = "Использование стандартного вычета (1 млн руб.)"

    # 4. Расчет налога по прогрессивной шкале (13% до 2.4 млн, 15% на превышение)
    tax_threshold = 2400000

    if tax_base <= tax_threshold:
        total_tax = tax_base * 0.13
    else:
        tax_13 = tax_threshold * 0.13
        tax_15 = (tax_base - tax_threshold) * 0.15
        total_tax = tax_13 + tax_15

    return {
        "Доход для расчета налогов": round(real_income, 2),
        "Выбранный метод снижения": method,
        "Налогооблагаемая база (чистая прибыль)": round(tax_base, 2),
        "Итоговый налог к уплате": round(total_tax, 2),
        "Причина": "Владение менее 3 лет, расчет по формулам",
    }


def calculate_apartment_sale_tax_and_print(
        # Сколько целых лет квартира была в собственности после дарения
        years_owned: int,
        # Цена предполагаемой продажи квартиры
        sale_price: float,
        # Кадастровая стоимость квартиры на текущий момент
        cadastral_value: float,
        # Цена приобретения квартиры
        donor_purchase_price: float
):
    result = calculate_apartment_sale_tax(
        years_owned=years_owned,
        sale_price=sale_price,
        cadastral_value=cadastral_value,
        donor_purchase_price=donor_purchase_price,
    )

    print("--- РЕЗУЛЬТАТ РАСЧЕТА НАЛОГА ---")
    for key, value in result.items():
        if isinstance(value, float):
            print(f"{key}: {value:,.2f} руб.")
        else:
            print(f"{key}: {value}")


calculate_apartment_sale_tax_and_print(
    years_owned=0,
    sale_price=0,
    cadastral_value=0,
    donor_purchase_price=0
)
