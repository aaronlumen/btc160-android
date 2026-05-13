package com.surina.btc160.data

import java.math.BigInteger

object PuzzleData {

    // Keys confirmed solved (from btc_solve_hal3.py KNOWN_KEYS)
    private val SOLVED_KEYS = mapOf(
        63  to "7cce5efdaccf6808",
        64  to "f7051f27b09112d4",
        65  to "1a838b13505b26867",
        66  to "2832ed74f2b5e35ee",
        67  to "730fc235c1942c1ae",
        68  to "bebb3940cd0fc1491",
        69  to "101d83275fb2bc7e0c",
        70  to "349b84b6431a6c4ef1",
        75  to "4c5ce114686a1336e07",
        80  to "ea1a5c66dcc11b5ad180",
        85  to "11720c4f018d51b8cebba8",
        90  to "2ce00bb2136a445c71e85bf",
        95  to "527a792b183c7f64a0e8b1f4",
        100 to "af55fc59c335c8ec67ed24826",
        105 to "16f14fc2054cd87ee6396b33df3",
        110 to "35c0d7234df7deb0f20cf7062444",
        115 to "60f4d11574f5deee49961d9609ac6",
        120 to "b10f22572c497a836ea187f2e1fc23",
        125 to "1c533b6bb7f0804e09960225e44877ac",
        130 to "33e7665705359f04f28b88cf897c603c9",
    )

    val ALL: List<PuzzleInfo> = buildList {
        // Puzzles 1-62: all solved, representative entries only
        for (b in 1..62) {
            add(PuzzleInfo(
                bits = b,
                rangeMin = BigInteger.TWO.pow(b - 1).toString(16),
                rangeMax = BigInteger.TWO.pow(b).subtract(BigInteger.ONE).toString(16),
                address = "",
                btcValue = b * 0.1,
                hash160 = "",
                publicKey = null,
                solved = true,
            ))
        }

        // Puzzles 63-70: solved, keys known
        addAll(solvedRange(63..70))

        // Puzzle 71: PRIMARY TARGET
        add(PuzzleInfo(
            bits       = 71,
            rangeMin   = "400000000000000000",
            rangeMax   = "7fffffffffffffffff",
            address    = "1PWo3JeB9jrGwfHDNpdGK54CRas7fsVzXU",
            btcValue   = 7.1,
            hash160    = "f6f5431d25bbf7b12e8add9af5e3475c44a0a5b8",
            publicKey  = null,
            solved     = false,
        ))

        // 72-74
        add(csv(72,"800000000000000000","ffffffffffffffffff","1JTK7s9YVYywfm5XUH7RNhHJH1LshCaRFR",7.2,"bf7413e8df4e7a34ce9dc13e2f2648783ec54adb"))
        add(csv(73,"1000000000000000000","1ffffffffffffffffff","12VVRNPi4SJqUTsp6FmqDqY5sGosDtysn4",7.3,"105b7f253f0ebd7843adaebbd805c944bfb863e4"))
        add(csv(74,"2000000000000000000","3ffffffffffffffffff","1FWGcVDK3JGzCC3WtkYetULPszMaK2Jksv",7.4,"9f1adb20baeacc38b3f49f3df6906a0e48f2df3d"))

        // 75 solved
        addAll(solvedRange(75..75))

        // 76-79
        add(csv(76,"8000000000000000000","fffffffffffffffffff","1DJh2eHFYQfACPmrvpyWc8MSTYKh7w9eRF",7.6,"86f9fea5cdecf033161dd2f8f8560768ae0a6d14"))
        add(csv(77,"10000000000000000000","1fffffffffffffffffff","1Bxk4CQdqL9p22JEtDfdXMsng1XacifUtE",7.7,"783c138ac81f6a52398564bb17455576e8525b29"))
        add(csv(78,"20000000000000000000","3fffffffffffffffffff","15qF6X51huDjqTmF9BJgxXdt1xcj46Jmhb",7.8,"35003c3ef8759c92092f8488fca59a042859018c"))
        add(csv(79,"40000000000000000000","7fffffffffffffffffff","1ARk8HWJMn8js8tQmGUJeQHjSE7KRkn2t8",7.9,"67671d5490c272e3ab7ddd34030d587738df33da"))

        // 80 solved
        addAll(solvedRange(80..80))

        // 81-84
        add(csv(81,"100000000000000000000","1ffffffffffffffffffff","15qsCm78whspNQFydGJQk5rexzxTQopnHZ",8.1,"351e605fac813965951ba433b7c2956bf8ad95ce"))
        add(csv(82,"200000000000000000000","3ffffffffffffffffffff","13zYrYhhJxp6Ui1VV7pqa5WDhNWM45ARAC",8.2,"20d28d4e87543947c7e4913bcdceaa16e2f8f061"))
        add(csv(83,"400000000000000000000","7ffffffffffffffffffff","14MdEb4eFcT3MVG5sPFG4jGLuHJSnt1Dk2",8.3,"24cef184714bbd030833904f5265c9c3e12a95a2"))
        add(csv(84,"800000000000000000000","fffffffffffffffffffff","1CMq3SvFcVEcpLMuuH8PUcNiqsK1oicG2D",8.4,"7c99ce73e19f9fbfcce4825ae88261e2b0b0b040"))

        // 85 solved
        addAll(solvedRange(85..85))

        // 86-89
        add(csv(86,"2000000000000000000000","3fffffffffffffffffffff","1K3x5L6G57Y494fDqBfrojD28UJv4s5JcK",8.6,"c60111ed3d63b49665747b0e31eb382da5193535"))
        add(csv(87,"4000000000000000000000","7fffffffffffffffffffff","1PxH3K1Shdjb7gSEoTX7UPDZ6SH4qGPrvq",8.7,"fbc708d671c03e26661b9c08f77598a529858b5e"))
        add(csv(88,"8000000000000000000000","ffffffffffffffffffffff","16AbnZjZZipwHMkYKBSfswGWKDmXHjEpSf",8.8,"38a968fdfb457654c51bcfc4f9174d6ee487bb41"))
        add(csv(89,"10000000000000000000000","1ffffffffffffffffffffff","19QciEHbGVNY4hrhfKXmcBBCrJSBZ6TaVt",8.9,"5c3862203d1e44ab3af441503e22db97b1c5097e"))

        // 90 solved; 91-94
        addAll(solvedRange(90..90))
        add(csv(91,"40000000000000000000000","7ffffffffffffffffffffff","1EzVHtmbN4fs4MiNk3ppEnKKhsmXYJ4s74",9.1,"9978f61b92d16c5f1a463a0995df70da1f7a7d2a"))
        add(csv(92,"80000000000000000000000","fffffffffffffffffffffff","1AE8NzzgKE7Yhz7BWtAcAAxiFMbPo82NB5",9.2,"6534b31208fe6e100d29f9c9c75aac8bf06fbb38"))
        add(csv(93,"100000000000000000000000","1fffffffffffffffffffffff","17Q7tuG2JwFFU9rXVj3uZqRtioH3mx2Jad",9.3,"463013cd41279f2fd0c31d0a16db3972bfffac8d"))
        add(csv(94,"200000000000000000000000","3fffffffffffffffffffffff","1K6xGMUbs6ZTXBnhw1pippqwK6wjBWtNpL",9.4,"c6927a00970d0165327d0a6db7950f05720c295c"))

        // 95 solved; 96-99
        addAll(solvedRange(95..95))
        add(csv(96,"800000000000000000000000","ffffffffffffffffffffffff","15ANYzzCp5BFHcCnVFzXqyibpzgPLWaD8b",9.6,"2da63cbd251d23c7b633cb287c09e6cf888b3fe4"))
        add(csv(97,"1000000000000000000000000","1ffffffffffffffffffffffff","18ywPwj39nGjqBrQJSzZVq2izR12MDpDr8",9.7,"578d94dc6f40fff35f91f6fba9b71c46b361dff2"))
        add(csv(98,"2000000000000000000000000","3ffffffffffffffffffffffff","1CaBVPrwUxbQYYswu32w7Mj4HR4maNoJSX",9.8,"7eefddd979a1d6bb6f29757a1f463579770ba566"))
        add(csv(99,"4000000000000000000000000","7ffffffffffffffffffffffff","1JWnE6p6UN7ZJBN7TtcbNDoRcjFtuDWoNL",9.9,"c01bf430a97cbcdaedddba87ef4ea21c456cebdb"))

        // 100 solved; 101-104
        addAll(solvedRange(100..100))
        add(csv(101,"10000000000000000000000000","1fffffffffffffffffffffffff","1CKCVdbDJasYmhswB6HKZHEAnNaDpK7W4n",10.1,"7c1a77205c03b9909663b2034faa0b544e6bc96b"))
        add(csv(102,"20000000000000000000000000","3fffffffffffffffffffffffff","1PXv28YxmYMaB8zxrKeZBW8dt2HK7RkRPX",10.2,"f72b812932f6d7102233971d65cec0a22b89e136"))
        add(csv(103,"40000000000000000000000000","7fffffffffffffffffffffffff","1AcAmB6jmtU6AiEcXkmiNE9TNVPsj9DULf",10.3,"695fd6dcf33f47166b25de968b2932b351b0afc4"))
        add(csv(104,"80000000000000000000000000","ffffffffffffffffffffffffff","1EQJvpsmhazYCcKX5Au6AZmZKRnzarMVZu",10.4,"93022af9a38f3ebb0c3f15dd1c83f8fadaf64e74"))

        // 105 solved; 106-109
        addAll(solvedRange(105..105))
        add(csv(106,"200000000000000000000000000","3ffffffffffffffffffffffffff","18KsfuHuzQaBTNLASyj15hy4LuqPUo1FNB",10.6,"505aaa63a5e209dfb90cee683a8e227a8c278e47"))
        add(csv(107,"400000000000000000000000000","7ffffffffffffffffffffffffff","15EJFC5ZTs9nhsdvSUeBXjLAuYq3SWaxTc",10.7,"2e644e46b042ffa86da35c54d7275f1abe6d4911"))
        add(csv(108,"800000000000000000000000000","fffffffffffffffffffffffffff","1HB1iKUqeffnVsvQsbpC6dNi1XKbyNuqao",10.8,"b166c44f12c7fc565f37ff6288ee64e0f0ec9a0b"))
        add(csv(109,"1000000000000000000000000000","1fffffffffffffffffffffffffff","1GvgAXVCbA8FBjXfWiAms4ytFeJcKsoyhL",10.9,"aeb0a0197442d4ade8ef41442d557b0e22b85ac0"))

        // 110 solved; 111-114
        addAll(solvedRange(110..110))
        add(csv(111,"4000000000000000000000000000","7fffffffffffffffffffffffffff","1824ZJQ7nKJ9QFTRBqn7z7dHV5EGpzUpH3",11.1,"4cfc43fe12a330c8164251e38c0c0c3c84cf86f6"))
        add(csv(112,"8000000000000000000000000000","ffffffffffffffffffffffffffff","18A7NA9FTsnJxWgkoFfPAFbQzuQxpRtCos",11.2,"4e81efec43c5195aeca0e3877664330418b8e48e"))
        add(csv(113,"10000000000000000000000000000","1ffffffffffffffffffffffffffff","1NeGn21dUDDeqFQ63xb2SpgUuXuBLA4WT4",11.3,"ed673389e4b12925316f9166d56d701829e53cf8"))
        add(csv(114,"20000000000000000000000000000","3ffffffffffffffffffffffffffff","174SNxfqpdMGYy5YQcfLbSTK3MRNZEePoy",11.4,"42773005f9594cd16b10985d428418acb7f352ec"))

        // 115 solved; 116-119
        addAll(solvedRange(115..115))
        add(csv(116,"80000000000000000000000000000","fffffffffffffffffffffffffffff","1MnJ6hdhvK37VLmqcdEwqC3iFxyWH2PHUV",11.6,"e3f381c34a20da049779b44cae0417c7fb2898d0"))
        add(csv(117,"100000000000000000000000000000","1fffffffffffffffffffffffffffff","1KNRfGWw7Q9Rmwsc6NT5zsdvEb9M2Wkj5Z",11.7,"c97f9591e28687be1c4d972e25be7c372a3221b4"))
        add(csv(118,"200000000000000000000000000000","3fffffffffffffffffffffffffffff","1PJZPzvGX19a7twf5HyD2VvNiPdHLzm9F6",11.8,"f4a4e1c11a5bbbd2fc139d221825407c66e0b8b4"))
        add(csv(119,"400000000000000000000000000000","7fffffffffffffffffffffffffffff","1GuBBhf61rnvRe4K8zu8vdQB3kHzwFqSy7",11.9,"ae6804b35c82f47f8b0a42d8c5e514fe5ef0a883"))

        // 120 solved; 121-124
        addAll(solvedRange(120..120))
        add(csv(121,"1000000000000000000000000000000","1ffffffffffffffffffffffffffffff","1GDSuiThEV64c166LUFC9uDcVdGjqkxKyh",12.1,"a6e4818537e42f7b3f021daa810367dad4dda16f"))
        add(csv(122,"2000000000000000000000000000000","3ffffffffffffffffffffffffffffff","1Me3ASYt5JCTAK2XaC32RMeH34PdprrfDx",12.2,"e263b62ea294b9650615a13b926e75944c823990"))
        add(csv(123,"4000000000000000000000000000000","7ffffffffffffffffffffffffffffff","1CdufMQL892A69KXgv6UNBD17ywWqYpKut",12.3,"7fa4515066ba6905f894b2078f9af7b1379169cf"))
        add(csv(124,"8000000000000000000000000000000","fffffffffffffffffffffffffffffff","1BkkGsX9ZM6iwL3zbqs7HWBV7SvosR6m8N",12.4,"75f74467ce7214f1767406d5ed12012aa523c48e"))

        // 125 solved; 126-129
        addAll(solvedRange(125..125))
        add(csv(126,"20000000000000000000000000000000","3fffffffffffffffffffffffffffffff","1AWCLZAjKbV1P7AHvaPNCKiB7ZWVDMxFiz",12.6,"683ea8a1ef06eada90556017d44323b5c04e00f1"))
        add(csv(127,"40000000000000000000000000000000","7fffffffffffffffffffffffffffffff","1G6EFyBRU86sThN3SSt3GrHu1sA7w7nzi4",12.7,"a58708aa98ad35c889bb36d8049bf9e9cacfd02a"))
        add(csv(128,"80000000000000000000000000000000","ffffffffffffffffffffffffffffffff","1MZ2L1gFrCtkkn6DnTT2e4PFUTHw9gNwaj",12.8,"e170ef514689d7230da362a0c121a07723550512"))
        add(csv(129,"100000000000000000000000000000000","1ffffffffffffffffffffffffffffffff","1Hz3uv3nNZzBVMXLGadCucgjiCs5W9vaGz",12.9,"ba4c2748360a6b66263e11d1dc8658463ca5ff18"))

        // 130 solved; 131-134
        addAll(solvedRange(130..130))
        add(csv(131,"400000000000000000000000000000000","7ffffffffffffffffffffffffffffffff","16zRPnT8znwq42q7XeMkZUhb1bKqgRogyy",13.1,"41b4b36a6c036568972380177eca2916cacd71de"))
        add(csv(132,"800000000000000000000000000000000","fffffffffffffffffffffffffffffffff","1KrU4dHE5WrW8rhWDsTRjR21r8t3dsrS3R",13.2,"cecd3ca4319651bd3afd1e23ab66e111ed38d16d"))
        add(csv(133,"1000000000000000000000000000000000","1fffffffffffffffffffffffffffffffff","17uDfp5r4n441xkgLFmhNoSW1KWp6xVLD",13.3,"014e15e4ea6da460cc7835e262676baa37988e4f"))
        add(csv(134,"2000000000000000000000000000000000","3fffffffffffffffffffffffffffffffff","13A3JrvXmvg5w9XGvyyR4JEJqiLz8ZySY3",13.4,"17a5ebfaf62e73f149e33ba674836801f13a80b9"))

        // 135-160: unsolved, some with public keys
        add(PuzzleInfo(135,"4000000000000000000000000000000000","7fffffffffffffffffffffffffffffffff","16RGFo6hjq9ym6Pj7N5H7L1NR1rVPJyw2v",13.5,"3b6f58a75a54bfd85d1bc6c51180fdc732992326","02145d2611c823a396ef6712ce0f712f09b9b4f3135e3e0aa3230fb9b6d08d1e16",false))
        add(csv(136,"8000000000000000000000000000000000","ffffffffffffffffffffffffffffffffff","1UDHPdovvR985NrWSkdWQDEQ1xuRiTALq",13.6,"05257be4b57ee43fc09762d5d3a9ad4a6e1a0364"))
        add(csv(137,"10000000000000000000000000000000000","1ffffffffffffffffffffffffffffffffff","15nf31J46iLuK1ZkTnqHo7WgN5cARFK3RA",13.7,"3482f8986e13c018692053a784481c63a3554c9c"))
        add(csv(138,"20000000000000000000000000000000000","3ffffffffffffffffffffffffffffffffff","1Ab4vzG6wEQBDNQM1B2bvUz4fqXXdFk2WT",13.8,"692a8e583866fc9056f5c61a45969fb9d868a08c"))
        add(csv(139,"40000000000000000000000000000000000","7ffffffffffffffffffffffffffffffffff","1Fz63c775VV9fNyj25d9Xfw3YHE6sKCxbt",13.9,"a45dae9cd5d3fde21e5aa9a95367d107267b3b8a"))
        add(PuzzleInfo(140,"80000000000000000000000000000000000","fffffffffffffffffffffffffffffffffff","1QKBaU6WAeycb3DbKbLBkX7vJiaS8r42Xo",14.0,"ffbb35a7bb9bbe16c1aa2534f7ff11d59c8e3d1a","031f6a332d3c5c4f2de2378c012f429cd109ba07d69690c6c701b6bb87860d6640",false))
        add(csv(141,"100000000000000000000000000000000000","1fffffffffffffffffffffffffffffffffff","1CD91Vm97mLQvXhrnoMChhJx4TP9MaQkJo",14.1,"7af50f73fd580f1713af3a6f9c5de49643ec6fc6"))
        add(csv(142,"200000000000000000000000000000000000","3fffffffffffffffffffffffffffffffffff","15MnK2jXPqTMURX4xC3h4mAZxyCcaWWEDD",14.2,"2fcea55e6d027a2ba7c7ebe95eedf47766730fe2"))
        add(csv(143,"400000000000000000000000000000000000","7fffffffffffffffffffffffffffffffffff","13N66gCzWWHEZBxhVxG18P8wyjEWF9Yoi1",14.3,"19ed3e03d19ddcedd5fa86543be820b3a7951650"))
        add(csv(144,"800000000000000000000000000000000000","ffffffffffffffffffffffffffffffffffff","1NevxKDYuDcCh1ZMMi6ftmWwGrZKC6j7Ux",14.4,"ed87120066e244ff5331d5f8625873d7a3acc39c"))
        add(PuzzleInfo(145,"1000000000000000000000000000000000000","1ffffffffffffffffffffffffffffffffffff","19GpszRNUej5yYqxXoLnbZWKew3KdVLkXg",14.5,"5abf369388deb8072741b4eb43ef10fa9388a729","03afdda497369e219a2c1c369954a930e4d3740968e5e4352475bcffce3140dae5",false))
        add(csv(146,"2000000000000000000000000000000000000","3ffffffffffffffffffffffffffffffffffff","1M7ipcdYHey2Y5RZM34MBbpugghmjaV89P",14.6,"dca7ebfb78ce21884300f133d89244bc4b1b756f"))
        add(csv(147,"4000000000000000000000000000000000000","7ffffffffffffffffffffffffffffffffffff","18aNhurEAJsw6BAgtANpexk5ob1aGTwSeL",14.7,"5318b9d7fcc93873f768725eb68ba2c924bb07ee"))
        add(csv(148,"8000000000000000000000000000000000000","fffffffffffffffffffffffffffffffffffff","1FwZXt6EpRT7Fkndzv6K4b4DFoT4trbMrV",14.8,"a3e3612e586fd206efb8eee6ccd58318e182829a"))
        add(csv(149,"10000000000000000000000000000000000000","1fffffffffffffffffffffffffffffffffffff","1CXvTzR6qv8wJ7eprzUKeWxyGcHwDYP1i2",14.9,"7e827e3b90da24c2a15f7b67e3bbece39955a5d0"))
        add(PuzzleInfo(150,"20000000000000000000000000000000000000","3fffffffffffffffffffffffffffffffffffff","1MUJSJYtGPVGkBCTqGspnxyHahpt5Te8jy",15.0,"e08c4d3bc9cf2b3e2cb88de2bfaa4fe8c7aa3f24","03137807790ea7dc6e97901c2bc87411f45ed74a5629315c4e4b03a0a102250c49",false))
        add(csv(151,"40000000000000000000000000000000000000","7fffffffffffffffffffffffffffffffffffff","13Q84TNNvgcL3HJiqQPvyBb9m4hxjS3jkV",15.1,"1a4fb632f0de0c53a0a31d57f840a19e56c645ee"))
        add(csv(152,"80000000000000000000000000000000000000","ffffffffffffffffffffffffffffffffffffff","1LuUHyrQr8PKSvbcY1v1PiuGuqFjWpDumN",15.2,"da56cd815fa2f0d6a4ce6d25ed7b1a01d9f9bc6b"))
        add(csv(153,"100000000000000000000000000000000000000","1ffffffffffffffffffffffffffffffffffffff","18192XpzzdDi2K11QVHR7td2HcPS6Qs5vg",15.3,"4ccf94a1b0efd63cddeee0ef5eee5ebe720cfcbf"))
        add(csv(154,"200000000000000000000000000000000000000","3ffffffffffffffffffffffffffffffffffffff","1NgVmsCCJaKLzGyKLFJfVequnFW9ZvnMLN",15.4,"edd2e206825fa8949d1304cd82c08d64b222f2eb"))
        add(PuzzleInfo(155,"400000000000000000000000000000000000000","7ffffffffffffffffffffffffffffffffffffff","1AoeP37TmHdFh8uN72fu9AqgtLrUwcv2wJ",15.5,"6b8b7830f73c5bf9e8beb9f161ad82b3bde992e4","035cd1854cae45391ca4ec428cc7e6c7d9984424b954209a8eea197b9e364c05f6",false))
        add(csv(156,"800000000000000000000000000000000000000","fffffffffffffffffffffffffffffffffffffff","1FTpAbQa4h8trvhQXjXnmNhqdiGBd1oraE",15.6,"9ea3f29aaedf7da10b1488934c50a39e271b0b64"))
        add(csv(157,"1000000000000000000000000000000000000000","1fffffffffffffffffffffffffffffffffffffff","14JHoRAdmJg3XR4RjMDh6Wed6ft6hzbQe9",15.7,"242d790e5a168043c76f0539fd894b73ee67b3b3"))
        add(csv(158,"2000000000000000000000000000000000000000","3fffffffffffffffffffffffffffffffffffffff","19z6waranEf8CcP8FqNgdwUe1QRxvUNKBG",15.8,"628dacebb0faa7f81670e174ca4c8a95a7e37029"))
        add(csv(159,"4000000000000000000000000000000000000000","7fffffffffffffffffffffffffffffffffffffff","14u4nA5sugaswb6SZgn5av2vuChdMnD9E5",15.9,"2ac1295b4e54b3f15bb0a99f84018d2082495645"))
        add(PuzzleInfo(160,"8000000000000000000000000000000000000000","ffffffffffffffffffffffffffffffffffffffff","1NBC8uXJy1GiJ6drkiZa1WuKn51ps7EPTv",16.0,"e84818e1bf7f699aa6e28ef9edfb582099099292","02e0a8b039282faf6fe0fd769cfbc4b6b4cf8758ba68220eac420e32b91ddfa673",false))
    }

    private fun solvedRange(range: IntRange): List<PuzzleInfo> = range.map { b ->
        val lo = BigInteger.TWO.pow(b - 1).toString(16)
        val hi = BigInteger.TWO.pow(b).subtract(BigInteger.ONE).toString(16)
        PuzzleInfo(b, lo, hi, "", b * 0.1, "", null, true, SOLVED_KEYS[b])
    }

    private fun csv(bits: Int, rMin: String, rMax: String, addr: String, btc: Double, h160: String) =
        PuzzleInfo(bits, rMin, rMax, addr, btc, h160, null, false)

    val unsolved get() = ALL.filter { !it.solved }
    val solved   get() = ALL.filter { it.solved }
    val puzzle71 get() = ALL.first { it.bits == 71 }

    // Puzzle 71 chunk math (BigInteger to handle 71-bit numbers)
    val PUZZLE_71_LO   = BigInteger("400000000000000000", 16)
    val PUZZLE_71_HI   = BigInteger("7fffffffffffffffff", 16)
    val N_CHUNKS       = 100_000
    val CHUNK_SIZE: BigInteger = (PUZZLE_71_HI - PUZZLE_71_LO + BigInteger.ONE) / BigInteger.valueOf(N_CHUNKS.toLong())
    val TARGET_START   = 51_000
    val TARGET_END     = 84_000

    fun chunkStart(idx: Int): BigInteger = PUZZLE_71_LO + CHUNK_SIZE * BigInteger.valueOf(idx.toLong())
    fun chunkEnd(idx: Int): BigInteger = if (idx == N_CHUNKS - 1) PUZZLE_71_HI
                                         else chunkStart(idx + 1) - BigInteger.ONE
}
