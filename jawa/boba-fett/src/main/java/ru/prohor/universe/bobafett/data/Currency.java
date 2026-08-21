package ru.prohor.universe.bobafett.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Currency {
    RUB(0, "Russian Ruble", "Российский рубль", "Российских рублей", "RUB", "🇷🇺"),
    USD(1, "United States Dollar", "Доллар США", "Долларов США", "USD", "🇺🇸"),
    EUR(2, "Euro", "Евро", "Евро", "EUR", "🇪🇺"),
    BTC(3, "Bitcoin", "Биткоин", "Биткоинов", "BTC", null),
    AED(4, "United Arab Emirates Dirham", "Дирхам ОАЭ", "Дирхамов ОАЭ", "AED", "🇦🇪"),
    TRY(5, "Turkish Lira", "Турецкая лира", "Турецких лир", "TRY", "🇹🇷"),
    CNY(6, "Chinese Yuan", "Китайский юань", "Китайских юаней", "CNY", "🇨🇳"),
    GBP(7, "British Pound Sterling", "Британский фунт стерлингов", "Британских фунтов стерлингов", "GBP", "🇬🇧"),
    GEL(8, "Georgian Lari", "Грузинский лари", "Грузинских лари", "GEL", "🇬🇪"),
    KZT(9, "Kazakhstani Tenge", "Казахстанский тенге", "Казахстанских тенге", "KZT", "🇰🇿"),
    AMD(10, "Armenian Dram", "Армянский драм", "Армянских драмов", "AMD", "🇦🇲"),
    BYN(11, "Belarusian Ruble", "Белорусский рубль", "Белорусских рублей", "BYN", "🇧🇾"),
    XAU(12, "Troy ounce of Gold", "Тройская унция Золота", "Тройских унций Золота", "XAU", "🥇"),
    XAG(13, "Troy ounce of Silver", "Тройская унция Серебра", "Тройских унций Серебра", "XAG", "🥈"),
    UZS(14, "Uzbekistan Som", "Узбекский сум", "Узбекских сумов", "UZS", "🇺🇿"),
    JPY(15, "Japanese Yen", "Японская иена", "Японских иен", "JPY", "🇯🇵"),
    THB(16, "Thai Baht", "Тайский бат", "Тайских батов", "THB", "🇹🇭"),
    CHF(17, "Swiss Franc", "Швейцарский франк", "Швейцарских франков", "CHF", "🇨🇭"),
    CAD(18, "Canadian Dollar", "Канадский доллар", "Канадских долларов", "CAD", "🇨🇦"),
    AUD(19, "Australian Dollar", "Австралийский доллар", "Австралийских долларов", "AUD", "🇦🇺"),
    INR(20, "Indian Rupee", "Индийская рупия", "Индийских рупий", "INR", "🇮🇳"),
    PLN(21, "Polish Zloty", "Польский злотый", "Польских злотых", "PLN", "🇵🇱"),
    SAR(22, "Saudi Riyal", "Саудовский риял", "Саудовских риялов", "SAR", "🇸🇦"),
    ILS(23, "Israeli New Sheqel", "Новый израильский шекель", "Новых израильских шекелей", "ILS", "🇮🇱"),
    KRW(24, "South Korean Won", "Южнокорейская вона", "Южнокорейских вон", "KRW", "🇰🇷"),
    HKD(25, "Hong Kong Dollar", "Гонконгский доллар", "Гонконгских долларов", "HKD", "🇭🇰"),
    TWD(26, "New Taiwan Dollar", "Новый тайваньский доллар", "Новых тайваньских долларов", "TWD", "🇹🇼"),
    SGD(27, "Singapore Dollar", "Сингапурский доллар", "Сингапурских долларов", "SGD", "🇸🇬"),
    MXN(28, "Mexican Peso", "Мексиканское песо", "Мексиканских песо", "MXN", "🇲🇽"),
    BRL(29, "Brazilian Real", "Бразильский реал", "Бразильских реалов", "BRL", "🇧🇷"),
    CZK(30, "Czech Republic Koruna", "Чешская крона", "Чешских крон", "CZK", "🇨🇿"),
    NOK(31, "Norwegian Krone", "Норвежская крона", "Норвежских крон", "NOK", "🇳🇴"),
    SEK(32, "Swedish Krona", "Шведская крона", "Шведских крон", "SEK", "🇸🇪"),
    DKK(33, "Danish Krone", "Датская крона", "Датских крон", "DKK", "🇩🇰"),
    HUF(34, "Hungarian Forint", "Венгерский форинт", "Венгерских форинтов", "HUF", "🇭🇺"),
    AZN(35, "Azerbaijani Manat", "Азербайджанский манат", "Азербайджанских манатов", "AZN", "🇦🇿"),
    TJS(36, "Tajikistani Somoni", "Таджикский сомони", "Таджикских сомони", "TJS", "🇹🇯"),
    TMT(37, "Turkmenistani Manat", "Туркменский манат", "Туркменских манатов", "TMT", "🇹🇲"),
    VND(38, "Vietnamese Dong", "Вьетнамский донг", "Вьетнамских донгов", "VND", "🇻🇳"),
    IDR(39, "Indonesian Rupiah", "Индонезийская рупия", "Индонезийских рупий", "IDR", "🇮🇩"),
    MYR(40, "Malaysian Ringgit", "Малайзийский ринггит", "Малайзийских ринггитов", "MYR", "🇲🇾"),
    ZAR(41, "South African Rand", "Южноафриканский рэнд", "Южноафриканских рэндов", "ZAR", "🇿🇦"),
    EGP(42, "Egyptian Pound", "Египетский фунт", "Египетских фунтов", "EGP", "🇪🇬"),
    QAR(43, "Qatari Rial", "Катарский риал", "Катарских риалов", "QAR", "🇶🇦"),
    OMR(44, "Omani Rial", "Оманский риал", "Оманских риалов", "OMR", "🇴🇲"),
    BHD(45, "Bahraini Dinar", "Бахрейнский динар", "Бахрейнских динаров", "BHD", "🇧🇭"),
    KWD(46, "Kuwaiti Dinar", "Кувейтский динар", "Кувейтских динаров", "KWD", "🇰🇼"),
    JOD(47, "Jordanian Dinar", "Иорданский динар", "Иорданских динаров", "JOD", "🇯🇴"),
    UAH(48, "Ukrainian Hryvnia", "Украинская гривна", "Украинских гривен", "UAH", "🇺🇦"),
    RON(49, "Romanian Leu", "Румынский лей", "Румынских леев", "RON", "🇷🇴"),
    BGN(50, "Bulgarian Lev", "Болгарский лев", "Болгарских левов", "BGN", "🇧🇬"),
    RSD(51, "Serbian Dinar", "Сербский динар", "Сербских динаров", "RSD", "🇷🇸"),
    HRK(52, "Croatian Kuna", "Хорватская куна", "Хорватских кун", "HRK", "🇭🇷"),
    ISK(53, "Icelandic Króna", "Исландская крона", "Исландских крон", "ISK", "🇮🇸"),
    CLP(54, "Chilean Peso", "Чилийское песо", "Чилийских песо", "CLP", "🇨🇱"),
    ARS(55, "Argentine Peso", "Аргентинское песо", "Аргентинских песо", "ARS", "🇦🇷"),
    COP(56, "Colombian Peso", "Колумбийское песо", "Колумбийских песо", "COP", "🇨🇴"),
    PEN(57, "Peruvian Nuevo Sol", "Перуанский соль", "Перуанских солей", "PEN", "🇵🇪"),
    UYU(58, "Uruguayan Peso", "Уругвайское песо", "Уругвайских песо", "UYU", "🇺🇾"),
    DOP(59, "Dominican Peso", "Доминиканское песо", "Доминиканских песо", "DOP", "🇩🇴"),
    CRC(60, "Costa Rican Colón", "Костариканский колон", "Костариканских колон", "CRC", "🇨🇷"),
    GTQ(61, "Guatemalan Quetzal", "Гватемальский кетсаль", "Гватемальских кетсалей", "GTQ", "🇬🇹"),
    PYG(62, "Paraguayan Guarani", "Парагвайский гуарани", "Парагвайских гуарани", "PYG", "🇵🇾"),
    BOB(63, "Bolivian Boliviano", "Боливийский боливиано", "Боливийских боливиано", "BOB", "🇧🇴"),
    MAD(64, "Moroccan Dirham", "Марокканский дирхам", "Марокканских дирхамов", "MAD", "🇲🇦"),
    TND(65, "Tunisian Dinar", "Тунисский динар", "Тунисских динаров", "TND", "🇹🇳"),
    DZD(66, "Algerian Dinar", "Алжирский динар", "Алжирских динаров", "DZD", "🇩🇿"),
    LYD(67, "Libyan Dinar", "Ливийский динар", "Ливийских динаров", "LYD", "🇱🇾"),
    NGN(68, "Nigerian Naira", "Нигерийская найра", "Нигерийских найр", "NGN", "🇳🇬"),
    GHS(69, "Ghanaian Cedi", "Ганский седи", "Ганских седи", "GHS", "🇬🇭"),
    KES(70, "Kenyan Shilling", "Кенийский шиллинг", "Кенийских шиллингов", "KES", "🇰🇪"),
    ETB(71, "Ethiopian Birr", "Эфиопский быр", "Эфиопских быров", "ETB", "🇪🇹"),
    MUR(72, "Mauritian Rupee", "Маврикийская рупия", "Маврикийских рупий", "MUR", "🇲🇺"),
    BDT(73, "Bangladeshi Taka", "Бангладешская така", "Бангладешских так", "BDT", "🇧🇩"),
    PKR(74, "Pakistani Rupee", "Пакистанская рупия", "Пакистанских рупий", "PKR", "🇵🇰"),
    LKR(75, "Sri Lankan Rupee", "Шри-ланкийская рупия", "Шри-ланкийских рупий", "LKR", "🇱🇰"),
    NPR(76, "Nepalese Rupee", "Непальская рупия", "Непальских рупий", "NPR", "🇳🇵"),
    KGS(77, "Kyrgystani Som", "Киргизский сом", "Киргизских сомов", "KGS", "🇰🇬"),
    MNT(78, "Mongolian Tugrik", "Монгольский тугрик", "Монгольских тугриков", "MNT", "🇲🇳"),
    KHR(79, "Cambodian Riel", "Камбоджийский риель", "Камбоджийских риелей", "KHR", "🇰🇭"),
    MMK(80, "Myanma Kyat", "Мьянманский кьят", "Мьянманских кьятов", "MMK", "🇲🇲"),
    LAK(81, "Laotian Kip", "Лаосский кип", "Лаосских кипов", "LAK", "🇱🇦"),
    PHP(82, "Philippine Peso", "Филиппинское песо", "Филиппинских песо", "PHP", "🇵🇭"),
    NZD(83, "New Zealand Dollar", "Новозеландский доллар", "Новозеландских долларов", "NZD", "🇳🇿"),
    FJD(84, "Fijian Dollar", "Фиджийский доллар", "Фиджийских долларов", "FJD", "🇫🇯"),
    MOP(85, "Macanese Pataca", "Макаосская патака", "Макаосских патак", "MOP", "🇲🇴"),
    MVR(86, "Maldivian Rufiyaa", "Мальдивская руфия", "Мальдивских руфий", "MVR", "🇲🇻"),
    SCR(87, "Seychellois Rupee", "Сейшельская рупия", "Сейшельских рупий", "SCR", "🇸🇨"),
    VES(
            88,
            "Venezuelan Bolívar Soberano",
            "Венесуэльский суверенный боливар",
            "Венесуэльских суверенных боливаров",
            "VES",
            "🇻🇪"
    ),
    IQD(89, "Iraqi Dinar", "Иракский динар", "Иракских динаров", "IQD", "🇮🇶"),
    IRR(90, "Iranian Rial", "Иранский риал", "Иранских риалов", "IRR", "🇮🇷"),
    LBP(91, "Lebanese Pound", "Ливанский фунт", "Ливанских фунтов", "LBP", "🇱🇧"),
    SYP(92, "Syrian Pound", "Сирийский фунт", "Сирийских фунтов", "SYP", "🇸🇾"),
    YER(93, "Yemeni Rial", "Йеменский риал", "Йеменских риалов", "YER", "🇾🇪"),
    SDG(94, "Sudanese Pound", "Суданский фунт", "Суданских фунтов", "SDG", "🇸🇩"),
    KPW(95, "North Korean Won", "Северокорейская вона", "Северокорейских вон", "KPW", "🇰🇵"),
    BWP(96, "Botswanan Pula", "Ботсванская пула", "Ботсванских пул", "BWP", "🇧🇼"),
    NAD(97, "Namibian Dollar", "Намибийский доллар", "Намибийских долларов", "NAD", "🇳🇦"),
    ZMW(98, "Zambian Kwacha", "Замбийская квача", "Замбийских квач", "ZMW", "🇿🇲"),
    MWK(99, "Malawian Kwacha", "Малавийская квача", "Малавийских квач", "MWK", "🇲🇼"),
    MZN(100, "Mozambican Metical", "Мозамбикский метикал", "Мозамбикских метикалов", "MZN", "🇲🇿"),
    BIF(101, "Burundian Franc", "Бурундийский франк", "Бурундийских франков", "BIF", "🇧🇮"),
    RWF(102, "Rwandan Franc", "Руандийский франк", "Руандийских франков", "RWF", "🇷🇼"),
    GNF(103, "Guinean Franc", "Гвинейский франк", "Гвинейских франков", "GNF", "🇬🇳"),
    DJF(104, "Djiboutian Franc", "Джибутийский франк", "Джибутийских франков", "DJF", "🇩🇯"),
    KMF(105, "Comorian Franc", "Коморский франк", "Коморских франков", "KMF", "🇰🇲"),
    XAF(106, "CFA Franc BEAC", "Франк КФА BEAC", "Франков КФА BEAC", "XAF", null),
    XOF(107, "CFA Franc BCEAO", "Франк КФА BCEAO", "Франков КФА BCEAO", "XOF", null),
    CVE(108, "Cape Verdean Escudo", "Эскудо Кабо-Верде", "Эскудо Кабо-Верде", "CVE", "🇨🇻"),
    ALL(109, "Albanian Lek", "Албанский лек", "Албанских леков", "ALL", "🇦🇱"),
    BAM(
            110,
            "Bosnia-Herzegovina Convertible Mark",
            "Конвертируемая марка Боснии и Герцеговины",
            "Конвертируемых марок Боснии и Герцеговины",
            "BAM",
            "🇧🇦"
    ),
    MKD(111, "Macedonian Denar", "Македонский денар", "Македонских денаров", "MKD", "🇲🇰"),
    MDL(112, "Moldovan Leu", "Молдавский лей", "Молдавских леев", "MDL", "🇲🇩"),
    ANG(
            113,
            "Netherlands Antillean Guilder",
            "Нидерландский антильский гульден",
            "Нидерландских антильских гульденов",
            "ANG",
            "🇨🇼"
    ),
    AWG(114, "Aruban Florin", "Арубанский флорин", "Арубанских флоринов", "AWG", "🇦🇼"),
    BSD(115, "Bahamian Dollar", "Багамский доллар", "Багамских долларов", "BSD", "🇧🇸"),
    BBD(116, "Barbadian Dollar", "Барбадосский доллар", "Барбадосских долларов", "BBD", "🇧🇧"),
    BZD(117, "Belize Dollar", "Белизский доллар", "Белизских долларов", "BZD", "🇧🇿"),
    BMD(118, "Bermudan Dollar", "Бермудский доллар", "Бермудских долларов", "BMD", "🇧🇲"),
    BND(119, "Brunei Dollar", "Брунейский доллар", "Брунейских долларов", "BND", "🇧🇳"),
    BTN(120, "Bhutanese Ngultrum", "Бутанский нгултрум", "Бутанских нгултрумов", "BTN", "🇧🇹"),
    AOA(121, "Angolan Kwanza", "Ангольская кванза", "Ангольских кванз", "AOA", "🇦🇴"),
    AFN(122, "Afghan Afghani", "Афганский афгани", "Афганских афгани", "AFN", "🇦🇫"),
    CDF(123, "Congolese Franc", "Конголезский франк", "Конголезских франков", "CDF", "🇨🇩"),
    GMD(124, "Gambian Dalasi", "Гамбийский даласи", "Гамбийских даласи", "GMD", "🇬🇲"),
    HTG(125, "Haitian Gourde", "Гаитянский гурд", "Гаитянских гурдов", "HTG", "🇭🇹"),
    HNL(126, "Honduran Lempira", "Гондурасская лемпира", "Гондурасских лемпир", "HNL", "🇭🇳"),
    NIO(127, "Nicaraguan Córdoba", "Никарагуанская кордоба", "Никарагуанских кордоб", "NIO", "🇳🇮"),
    PAB(128, "Panamanian Balboa", "Панамский бальбоа", "Панамских бальбоа", "PAB", "🇵🇦"),
    SVC(129, "Salvadoran Colón", "Сальвадорский колон", "Сальвадорских колон", "SVC", "🇸🇻"),
    PGK(130, "Papua New Guinean Kina", "Кина Папуа — Новой Гвинеи", "Кин Папуа — Новой Гвинеи", "PGK", "🇵🇬"),
    WST(131, "Samoan Tala", "Самоанская тала", "Самоанских тал", "WST", "🇼🇸"),
    TOP(132, "Tongan Paʻanga", "Тонганская паанга", "Тонганских паанг", "TOP", "🇹🇴"),
    VUV(133, "Vanuatu Vatu", "Вануатский вату", "Вануатских вату", "VUV", "🇻🇺"),
    XCD(134, "East Caribbean Dollar", "Восточнокарибский доллар", "Восточнокарибских долларов", "XCD", null),
    XPF(135, "CFP Franc", "Франк КФП", "Франков КФП", "XPF", null),
    SHP(136, "Saint Helena Pound", "Фунт острова Святой Елены", "Фунтов острова Святой Елены", "SHP", "🇸🇭"),
    FKP(137, "Falkland Islands Pound", "Фунт Фолклендских островов", "Фунтов Фолклендских островов", "FKP", "🇫🇰"),
    GIP(138, "Gibraltar Pound", "Гибралтарский фунт", "Гибралтарских фунтов", "GIP", "🇬🇮"),
    GGP(139, "Guernsey Pound", "Гернсийский фунт", "Гернсийских фунтов", "GGP", "🇬🇬"),
    IMP(140, "Manx pound", "Мэнский фунт", "Мэнских фунтов", "IMP", "🇮🇲"),
    JEP(141, "Jersey Pound", "Джерсийский фунт", "Джерсийских фунтов", "JEP", "🇯🇪"),
    JMD(142, "Jamaican Dollar", "Ямайский доллар", "Ямайских долларов", "JMD", "🇯🇲"),
    KYD(143, "Cayman Islands Dollar", "Доллар Каймановых островов", "Долларов Каймановых островов", "KYD", "🇰🇾"),
    LRD(144, "Liberian Dollar", "Либерийский доллар", "Либерийских долларов", "LRD", "🇱🇷"),
    SBD(145, "Solomon Islands Dollar", "Доллар Соломоновых Островов", "Долларов Соломоновых Островов", "SBD", "🇸🇧"),
    SRD(146, "Surinamese Dollar", "Суринамский доллар", "Суринамских долларов", "SRD", "🇸🇷"),
    TTD(147, "Trinidad and Tobago Dollar", "Доллар Тринидада и Тобаго", "Долларов Тринидада и Тобаго", "TTD", "🇹🇹"),
    GYD(148, "Guyanaese Dollar", "Гайанский доллар", "Гайанских долларов", "GYD", "🇬🇾"),
    LSL(149, "Lesotho Loti", "Лоти Лесото", "Лоти Лесото", "LSL", "🇱🇸"),
    SZL(150, "Swazi Lilangeni", "Свазилендский лилангени", "Свазилендских лилангени", "SZL", "🇸🇿"),
    SOS(151, "Somali Shilling", "Сомалийский шиллинг", "Сомалийских шиллингов", "SOS", "🇸🇴"),
    SLL(152, "Sierra Leonean Leone", "Сьерра-леонский леоне", "Сьерра-леонских леоне", "SLL", "🇸🇱"),
    ERN(153, "Eritrean Nakfa", "Эритрейская накфа", "Эритрейских накф", "ERN", "🇪🇷"),
    MGA(154, "Malagasy Ariary", "Малагасийский ариари", "Малагасийских ариари", "MGA", "🇲🇬"),
    STD(155, "São Tomé and Príncipe Dobra", "Добра Сан-Томе и Принсипи", "Добров Сан-Томе и Принсипи", "STD", "🇸🇹"),
    TZS(156, "Tanzanian Shilling", "Танзанийский шиллинг", "Танзанийских шиллингов", "TZS", "🇹🇿"),
    UGX(157, "Ugandan Shilling", "Угандийский шиллинг", "Угандийских шиллингов", "UGX", "🇺🇬"),
    ZWL(158, "Zimbabwean Dollar", "Доллар Зимбабве", "Долларов Зимбабве", "ZWL", "🇿🇼"),
    BYR(159, "Belarusian Ruble (pre-2016)", "Белорусский рубль (до 2016)", "Белорусских рублей (до 2016)", "BYR", "🇧🇾"),
    ZMK(160, "Zambian Kwacha (pre-2013)", "Замбийская квача (до 2013)", "Замбийских квач (до 2013)", "ZMK", "🇿🇲"),
    STN(
            161,
            "São Tomé and Príncipe Dobra",
            "Новая Добра Сан-Томе и Принсипи",
            "Новых Добров Сан-Томе и Принсипи",
            "STN",
            "🇸🇹"
    ),
    SLE(162, "Sierra Leonean Leone", "Леоне Сьерра-Леоне", "Леоне Сьерра-Леоне", "SLE", "🇸🇱"),
    CNH(163, "Offshore Chinese Yuan", "Офшорный китайский юань", "Офшорных китайских юаней", "CNH", "🇨🇳"),
    MRU(164, "Mauritanian Ouguiya", "Мавританская угия", "Мавританских угий", "MRU", "🇲🇷"),
    XCG(165, "Caribbean Guilder", "Карибский гульден", "Карибских гульденов", "XCG", "🇨🇼"),
    LTL(166, "Lithuanian Litas", "Литовский лит", "Литовских литов", "LTL", "🇱🇹"),
    LVL(167, "Latvian Lats", "Латвийский лат", "Латвийских латов", "LVL", "🇱🇻"),
    CUC(168, "Cuban Convertible Peso", "Кубинское конвертируемое песо", "Кубинских конвертируемых песо", "CUC", "🇨🇺"),
    CUP(169, "Cuban Peso", "Кубинское песо", "Кубинских песо", "CUP", "🇨🇺"),
    CLF(170, "Chilean Unit of Account (UF)", "Расчётная единица Чили (UF)", "Расчётных единиц Чили (UF)", "CLF", "🇨🇱"),
    XDR(171, "Special Drawing Rights", "Специальные права заимствования", "Специальных прав заимствования", "XDR", "🌐");

    // Индекс нужен, так как ordinal может измениться при добавлении новой валюты или удалении старой.
    // Сейчас ordinal служит для сортировки по релевантности
    public final int index;
    public final String name;
    public final String russianName;
    public final String russianNameForThousand;
    public final String code;
    public final String flag;

    Currency(
            int index,
            String name,
            String russianName,
            String russianNameForThousand,
            String code,
            String flag
    ) {
        this.index = index;
        this.name = name;
        this.russianName = russianName;
        this.russianNameForThousand = russianNameForThousand;
        this.code = code;
        this.flag = flag == null ? "🪙" : flag;
    }

    public static final List<Currency> CURRENCIES_FOR_RATES = Arrays.stream(values())
            .filter(currency -> currency != RUB)
            .toList();

    static {
        List<String> error = Arrays.stream(values())
                .collect(Collectors.groupingBy(c -> c.index, Collectors.toList()))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(e -> "Currencies " + e.getValue() + " have same index - " + e.getKey())
                .toList();
        if (!error.isEmpty()) {
            throw new IllegalStateException("\n" + String.join("\n", error));
        }
    }
}
