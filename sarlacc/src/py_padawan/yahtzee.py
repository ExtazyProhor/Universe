import itertools

possibles = '123456'
variants = [6 ** i for i in range(0, 6)]


def roll_yahtzee(attempts: int) -> list[float]:
    previous_chances = [0.0] * 6
    previous_chances[0] = 1.0
    chances = [0.0] * 6

    for attempt in range(attempts):
        for dice in range(0, 5):
            counts = [0] * 6
            lst = [''.join(p) for p in itertools.product(possibles, repeat=(5 - dice))]
            for combination in lst:
                digits = []
                for i in range(1, 7):
                    digits.append(combination.count(str(i)))
                digits[1] += dice
                counts[max(digits)] += 1
            chances_tmp = [previous_chances[dice] * cnt / variants[5 - dice] for cnt in counts]
            for i in range(0, 6):
                chances[i] += chances_tmp[i]

        previous_chances = [i for i in chances]
        chances = [0.0] * len(previous_chances)
        chances[5] = previous_chances[5]

    return previous_chances


def yahtzee_more_than_99():
    i = 0
    while True:
        i += 1
        roll = roll_yahtzee(i)
        if roll[5] >= 0.99:
            print(i, ' бросок: ', roll)
            break


def first_three_rolls_chances():
    for i in range(1, 4):
        roll =  roll_yahtzee(i)
        print(i, ' бросок: ', roll)


def stats():
    print('Шанс на яцзы больше 99:')
    yahtzee_more_than_99()
    print()

    print('Шансы первых трех бросков:')
    first_three_rolls_chances()
    print()


stats()
