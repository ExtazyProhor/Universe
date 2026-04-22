def parse(config: dict[str, str], seq: str) -> list[str]:
    l = [(seq, '')]
    res = []
    while len(l) != 0:
        cur = l.pop(0)
        for k, v in config.items():
            if cur[0].startswith(k):
                cur = (cur[0][len(k):], cur[1] + v)
                if len(cur[0]) == 0:
                    res.append(cur[1])
                else:
                    l.append(cur)
    return list(set(res))


def parse_all(config: dict[str, str], seqs: list[str]):
    for seq in seqs:
        print(parse(config, seq))


"""
from py_padawan.exam.oge_cipher_fano_condition import parse_all

cfg = {
    '01': 'А',
    '011': 'В',
    '100': 'Д',
    '101': 'К',
    '111': 'О',
    '010': 'Р',
    '001': 'У',
}
parse_all(cfg, ['0110101001101010', '1000100110101', '100111011111100', '10000101001101'])
"""
