def plate(os, stay, diff, border):
    cmd_from = [0, 0, 0]
    cmd_to = [0, 0, 0]

    cmd_from[os] = border[0][os]
    cmd_from[stay] = border[0][stay]
    cmd_from[diff] = border[0][diff] + 3

    cmd_to[os] = border[1][os]
    cmd_to[stay] = border[1][stay]
    cmd_to[diff] = border[1][diff] - 3

    print_arr(cmd_from, cmd_to)


def print_arr(cmd_from, cmd_to):
    print(
        '/fill',
        cmd_from[0],
        cmd_from[1],
        cmd_from[2],

        cmd_to[0],
        cmd_to[1],
        cmd_to[2],
        'air'
    )


def main():
    lx = -10122
    ly = -48
    lz = 10358
    hx = -10142
    hy = -28
    hz = 10378

    step = {
        lx: lx - 1,
        ly: ly + 1,
        lz: lz + 1,

        hx: hx + 1,
        hy: hy - 1,
        hz: hz - 1
    }

    print(
        '/fill',
        lx,
        ly,
        lz,

        hx,
        hy,
        hz,
        'minecraft:cyan_concrete'
    )

    x = [lx, hx]
    y = [ly, hy]
    z = [lz, hz]

    dots = []

    for ix in x:
        for iy in y:
            for iz in z:
                dots.append((ix, iy, iz))

    borders = []
    for i in range(len(dots)):
        for j in range(i + 1, len(dots)):

            if int(dots[i][0] == dots[j][0]) + int(dots[i][1] == dots[j][1]) + int(dots[i][2] == dots[j][2]) == 2:
                borders.append((dots[i], dots[j]))

    for border in borders:
        os = 0
        for i in range(3):
            if border[0][i] != border[1][i]:
                os = i
                break
        stay = (os + 1) % 3
        diff = (os + 2) % 3

        plate(os, stay, diff, border)
        plate(os, diff, stay, border)

        cmd_from = [0, 0, 0]
        cmd_to = [0, 0, 0]

        cmd_from[os] = border[0][os]
        cmd_from[stay] = step[border[0][stay]]
        cmd_from[diff] = step[border[0][diff]]

        cmd_to[os] = border[1][os]
        cmd_to[stay] = step[border[1][stay]]
        cmd_to[diff] = step[border[1][diff]]

        print_arr(cmd_from, cmd_to)


main()
