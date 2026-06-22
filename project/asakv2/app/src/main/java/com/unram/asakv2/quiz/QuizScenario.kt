package com.unram.asakv2.quiz

import com.unram.asakv2.model.QuizPlan

object QuizScenario {

    private val ALL_HURUF = listOf(
        "ha", "na", "ca", "ra", "ka",
        "da", "ta", "sa", "wa", "la",
        "ma", "ga", "ba", "nga", "pa",
        "ja", "ya", "nya"
    )

    private val stage1Huruf = listOf("ha", "na", "ca", "ra")
    private val stage2Huruf = listOf("ka", "da", "ta", "sa")
    private val stage3Huruf = listOf("wa", "la", "ma", "ga")
    private val stage4Huruf = listOf("ba", "nga", "pa")
    private val stage5Huruf = listOf("ja", "ya", "nya")

    fun makeRandomPlans(
        tipes: List<Int>,
        countEach: Int,
        hurufPool: List<String>
    ): List<QuizPlan> {
        val result = mutableListOf<QuizPlan>()
        for (tipe in tipes) {
            val exp = expForTipe(tipe)
            if (tipe == 5) {
                
                val shuffled = hurufPool.shuffled()
                repeat(countEach) { i ->
                    val a = shuffled[i % shuffled.size]
                    val b = shuffled[(i + 1) % shuffled.size].let {
                        if (it == a) shuffled[(i + 2) % shuffled.size] else it
                    }
                    result.add(QuizPlan(tipe = 5, pasangan = Pair(a, b), expFull = exp, isStreakMode = true))
                }
            } else {
                val shuffled = hurufPool.shuffled()
                repeat(countEach) { i ->
                    result.add(QuizPlan(tipe = tipe, huruf = shuffled[i % shuffled.size], expFull = exp, isStreakMode = true))
                }
            }
        }
        return result.shuffled()
    }

    private fun expForTipe(tipe: Int): Int = when (tipe) {
        0 -> 5
        1 -> 10
        2 -> 10
        3 -> 40
        4 -> 30
        5 -> 20
        else -> 10
    }

    val stage1Bagian1 = stage1Huruf.map {
        QuizPlan(tipe = 0, huruf = it, expFull = 5, isStreakMode = true)
    }

    val stage1Bagian2 = listOf(
        QuizPlan(tipe = 1, huruf = "ha", expFull = 10, isStreakMode = true),
        QuizPlan(tipe = 2, huruf = "ha", expFull = 10, isStreakMode = true),
        QuizPlan(tipe = 1, huruf = "na", expFull = 10, isStreakMode = true),
        QuizPlan(tipe = 2, huruf = "na", expFull = 10, isStreakMode = true),
        QuizPlan(tipe = 1, huruf = "ca", expFull = 10, isStreakMode = true),
        QuizPlan(tipe = 2, huruf = "ca", expFull = 10, isStreakMode = true),
        QuizPlan(tipe = 1, huruf = "ra", expFull = 10, isStreakMode = true),
        QuizPlan(tipe = 2, huruf = "ra", expFull = 10, isStreakMode = true)
    ).shuffled()

    val stage1Bagian3 = listOf(
        QuizPlan(tipe = 3, huruf = "ha", expFull = 40, isStreakMode = true),
        QuizPlan(tipe = 4, huruf = "ha", expFull = 30, isStreakMode = true),
        QuizPlan(tipe = 3, huruf = "na", expFull = 40, isStreakMode = true),
        QuizPlan(tipe = 4, huruf = "na", expFull = 30, isStreakMode = true),
        QuizPlan(tipe = 3, huruf = "ca", expFull = 40, isStreakMode = true),
        QuizPlan(tipe = 4, huruf = "ca", expFull = 30, isStreakMode = true),
        QuizPlan(tipe = 3, huruf = "ra", expFull = 40, isStreakMode = true),
        QuizPlan(tipe = 4, huruf = "ra", expFull = 30, isStreakMode = true)
    ).shuffled()

    val stage1Bagian4 = listOf(
        QuizPlan(tipe = -1, huruf = "ar", expFull = 0, isStreakMode = false) 
    )

    val stage1Bagian5: List<QuizPlan> get() = buildList {
        val pool = stage1Huruf.shuffled()
        listOf(1, 2, 3, 4).forEach { tipe ->
            repeat(2) { i ->
                add(QuizPlan(tipe = tipe, huruf = pool[i % pool.size], expFull = expForTipe(tipe), isStreakMode = true))
            }
        }
        
        add(QuizPlan(tipe = 5, pasangan = Pair(pool[0], pool[1]), expFull = 20, isStreakMode = true))
        add(QuizPlan(tipe = 5, pasangan = Pair(pool[2], pool[3]), expFull = 20, isStreakMode = true))
    }.shuffled()

    val stage2Bagian1 = stage2Huruf.map {
        QuizPlan(tipe = 0, huruf = it, expFull = 5, isStreakMode = true)
    }

    val stage2Bagian2: List<QuizPlan> get() = buildList {
        stage2Huruf.forEach { h ->
            add(QuizPlan(tipe = 1, huruf = h, expFull = 10, isStreakMode = true))
            add(QuizPlan(tipe = 2, huruf = h, expFull = 10, isStreakMode = true))
        }
        
        val prev = stage1Huruf.shuffled().take(2)
        prev.forEach { h ->
            val t = listOf(1, 2).random()
            add(QuizPlan(tipe = t, huruf = h, expFull = expForTipe(t), isStreakMode = true))
        }
    }.shuffled()

    val stage2Bagian3: List<QuizPlan> get() = buildList {
        stage2Huruf.forEach { h ->
            add(QuizPlan(tipe = 3, huruf = h, expFull = 40, isStreakMode = true))
            add(QuizPlan(tipe = 4, huruf = h, expFull = 30, isStreakMode = true))
        }
        val prev = stage1Huruf.shuffled().take(2)
        prev.forEach { h ->
            val t = listOf(3, 4).random()
            add(QuizPlan(tipe = t, huruf = h, expFull = expForTipe(t), isStreakMode = true))
        }
    }.shuffled()

    val stage2Bagian4 = listOf(
        QuizPlan(tipe = -1, huruf = "ar", expFull = 0, isStreakMode = false)
    )

    val stage2Bagian5: List<QuizPlan> get() = buildList {
        val pool = stage2Huruf.shuffled()
        listOf(1, 2, 3, 4).forEach { tipe ->
            repeat(2) { i ->
                add(QuizPlan(tipe = tipe, huruf = pool[i % pool.size], expFull = expForTipe(tipe), isStreakMode = true))
            }
        }
        add(QuizPlan(tipe = 5, pasangan = Pair(pool[0], pool[1]), expFull = 20, isStreakMode = true))
        add(QuizPlan(tipe = 5, pasangan = Pair(pool[2], pool[3]), expFull = 20, isStreakMode = true))
    }.shuffled()

    val stage3Bagian1 = stage3Huruf.map {
        QuizPlan(tipe = 0, huruf = it, expFull = 5, isStreakMode = true)
    }

    val stage3Bagian2: List<QuizPlan> get() = buildList {
        stage3Huruf.forEach { h ->
            add(QuizPlan(tipe = 1, huruf = h, expFull = 10, isStreakMode = true))
            add(QuizPlan(tipe = 2, huruf = h, expFull = 10, isStreakMode = true))
        }
        val prevS1 = stage1Huruf.shuffled().take(2)
        val prevS2 = stage2Huruf.shuffled().take(2)
        (prevS1 + prevS2).forEach { h ->
            val t = listOf(1, 2).random()
            add(QuizPlan(tipe = t, huruf = h, expFull = expForTipe(t), isStreakMode = true))
        }
    }.shuffled()

    val stage3Bagian3: List<QuizPlan> get() = buildList {
        stage3Huruf.forEach { h ->
            add(QuizPlan(tipe = 3, huruf = h, expFull = 40, isStreakMode = true))
            add(QuizPlan(tipe = 4, huruf = h, expFull = 30, isStreakMode = true))
        }
        val prevS1 = stage1Huruf.shuffled().take(2)
        val prevS2 = stage2Huruf.shuffled().take(2)
        (prevS1 + prevS2).forEach { h ->
            val t = listOf(3, 4).random()
            add(QuizPlan(tipe = t, huruf = h, expFull = expForTipe(t), isStreakMode = true))
        }
    }.shuffled()

    val stage3Bagian4 = listOf(
        QuizPlan(tipe = -1, huruf = "ar", expFull = 0, isStreakMode = false)
    )

    val stage3Bagian5: List<QuizPlan> get() = buildList {
        val pool = stage3Huruf.shuffled()
        listOf(1, 2, 3, 4).forEach { tipe ->
            repeat(2) { i ->
                add(QuizPlan(tipe = tipe, huruf = pool[i % pool.size], expFull = expForTipe(tipe), isStreakMode = true))
            }
        }
        add(QuizPlan(tipe = 5, pasangan = Pair(pool[0], pool[1]), expFull = 20, isStreakMode = true))
        add(QuizPlan(tipe = 5, pasangan = Pair(pool[2], pool[3]), expFull = 20, isStreakMode = true))
    }.shuffled()

    val stage3Bagian6: List<QuizPlan> get() = buildList {
        val pool = (stage1Huruf + stage2Huruf + stage3Huruf).shuffled()
        listOf(1, 2, 3, 4).forEach { tipe ->
            repeat(4) { i ->
                add(QuizPlan(tipe = tipe, huruf = pool[i % pool.size], expFull = expForTipe(tipe), isStreakMode = true))
            }
        }
        
        repeat(4) { i ->
            val a = pool[i % pool.size]
            val b = pool[(i + 1) % pool.size].let { if (it == a) pool[(i + 2) % pool.size] else it }
            add(QuizPlan(tipe = 5, pasangan = Pair(a, b), expFull = 20, isStreakMode = true))
        }
    }.shuffled()

    val stage4Bagian1 = stage4Huruf.map {
        QuizPlan(tipe = 0, huruf = it, expFull = 5, isStreakMode = true)
    }

    val stage4Bagian2: List<QuizPlan> get() = buildList {
        stage4Huruf.forEach { h ->
            add(QuizPlan(tipe = 1, huruf = h, expFull = 10, isStreakMode = true))
            add(QuizPlan(tipe = 2, huruf = h, expFull = 10, isStreakMode = true))
        }
        
        val prev = (stage1Huruf + stage2Huruf + stage3Huruf).shuffled().take(2)
        prev.forEach { h ->
            val t = listOf(1, 2).random()
            add(QuizPlan(tipe = t, huruf = h, expFull = expForTipe(t), isStreakMode = true))
        }
    }.shuffled()

    val stage4Bagian3: List<QuizPlan> get() = buildList {
        stage4Huruf.forEach { h ->
            add(QuizPlan(tipe = 3, huruf = h, expFull = 40, isStreakMode = true))
            add(QuizPlan(tipe = 4, huruf = h, expFull = 30, isStreakMode = true))
        }
        val prev = (stage1Huruf + stage2Huruf + stage3Huruf).shuffled().take(2)
        prev.forEach { h ->
            val t = listOf(3, 4).random()
            add(QuizPlan(tipe = t, huruf = h, expFull = expForTipe(t), isStreakMode = true))
        }
    }.shuffled()

    val stage4Bagian4: List<QuizPlan> get() = buildList {
        val pool = (stage1Huruf + stage2Huruf + stage3Huruf).shuffled()
        listOf(1, 2, 3, 4).forEach { tipe ->
            repeat(2) { i ->
                add(QuizPlan(tipe = tipe, huruf = pool[i % pool.size], expFull = expForTipe(tipe), isStreakMode = true))
            }
        }
        add(QuizPlan(tipe = 5, pasangan = Pair(pool[0], pool[1]), expFull = 20, isStreakMode = true))
        add(QuizPlan(tipe = 5, pasangan = Pair(pool[2], pool[3]), expFull = 20, isStreakMode = true))
    }.shuffled()

    val stage4Bagian5 = listOf(
        QuizPlan(tipe = -1, huruf = "ar", expFull = 0, isStreakMode = false)
    )

    val stage4Bagian6: List<QuizPlan> get() = buildList {
        val pool = stage4Huruf.shuffled()
        listOf(1, 2, 3, 4).forEach { tipe ->
            repeat(2) { i ->
                add(QuizPlan(tipe = tipe, huruf = pool[i % pool.size], expFull = expForTipe(tipe), isStreakMode = true))
            }
        }
        add(QuizPlan(tipe = 5, pasangan = Pair(pool[0], pool[1]), expFull = 20, isStreakMode = true))
        add(QuizPlan(tipe = 5, pasangan = Pair(pool[0], pool[2]), expFull = 20, isStreakMode = true))
    }.shuffled()

    val stage5Bagian1 = stage5Huruf.map {
        QuizPlan(tipe = 0, huruf = it, expFull = 5, isStreakMode = true)
    }

    val stage5Bagian2: List<QuizPlan> get() = buildList {
        stage5Huruf.forEach { h ->
            add(QuizPlan(tipe = 1, huruf = h, expFull = 10, isStreakMode = true))
            add(QuizPlan(tipe = 2, huruf = h, expFull = 10, isStreakMode = true))
        }
        val prev = stage4Huruf.shuffled().take(2)
        prev.forEach { h ->
            val t = listOf(1, 2).random()
            add(QuizPlan(tipe = t, huruf = h, expFull = expForTipe(t), isStreakMode = true))
        }
    }.shuffled()

    val stage5Bagian3: List<QuizPlan> get() = buildList {
        stage5Huruf.forEach { h ->
            add(QuizPlan(tipe = 3, huruf = h, expFull = 40, isStreakMode = true))
            add(QuizPlan(tipe = 4, huruf = h, expFull = 30, isStreakMode = true))
        }
        val prev = stage4Huruf.shuffled().take(2)
        prev.forEach { h ->
            val t = listOf(3, 4).random()
            add(QuizPlan(tipe = t, huruf = h, expFull = expForTipe(t), isStreakMode = true))
        }
    }.shuffled()

    val stage5Bagian4: List<QuizPlan> get() = buildList {
        val pool = (stage1Huruf + stage2Huruf + stage3Huruf).shuffled()
        listOf(1, 2, 3, 4).forEach { tipe ->
            repeat(2) { i ->
                add(QuizPlan(tipe = tipe, huruf = pool[i % pool.size], expFull = expForTipe(tipe), isStreakMode = true))
            }
        }
        add(QuizPlan(tipe = 5, pasangan = Pair(pool[0], pool[1]), expFull = 20, isStreakMode = true))
        add(QuizPlan(tipe = 5, pasangan = Pair(pool[2], pool[3]), expFull = 20, isStreakMode = true))
    }.shuffled()

    val stage5Bagian5 = listOf(
        QuizPlan(tipe = -1, huruf = "ar", expFull = 0, isStreakMode = false)
    )

    val stage5Bagian6: List<QuizPlan> get() = buildList {
        val pool = stage5Huruf.shuffled()
        listOf(1, 2, 3, 4).forEach { tipe ->
            repeat(2) { i ->
                add(QuizPlan(tipe = tipe, huruf = pool[i % pool.size], expFull = expForTipe(tipe), isStreakMode = true))
            }
        }
        add(QuizPlan(tipe = 5, pasangan = Pair(pool[0], pool[1]), expFull = 20, isStreakMode = true))
        add(QuizPlan(tipe = 5, pasangan = Pair(pool[1], pool[2]), expFull = 20, isStreakMode = true))
    }.shuffled()

    private val allHuruf = stage1Huruf + stage2Huruf + stage3Huruf + stage4Huruf + stage5Huruf

    val stage6Bagian1: List<QuizPlan> get() = buildList {
        val pool = allHuruf.shuffled()
        listOf(1, 2).forEach { tipe ->
            repeat(10) { i ->
                add(QuizPlan(tipe = tipe, huruf = pool[i % pool.size], expFull = expForTipe(tipe), isStreakMode = true))
            }
        }
        
        val poolS = allHuruf.shuffled()
        repeat(10) { i ->
            val a = poolS[i % poolS.size]
            val b = poolS[(i + 3) % poolS.size].let { if (it == a) poolS[(i + 5) % poolS.size] else it }
            add(QuizPlan(tipe = 5, pasangan = Pair(a, b), expFull = 20, isStreakMode = true))
        }
    }.shuffled()

    val stage6Bagian2: List<QuizPlan> get() = buildList {
        listOf(3, 4).forEach { tipe ->
            allHuruf.shuffled().forEach { h ->
                add(QuizPlan(tipe = tipe, huruf = h, expFull = expForTipe(tipe), isStreakMode = true))
            }
        }
    }.shuffled()

    val stage6Bagian3: List<QuizPlan> get() = buildList {
        val pool = allHuruf.shuffled()
        listOf(1, 2, 3, 4).forEach { tipe ->
            repeat(10) { i ->
                add(QuizPlan(tipe = tipe, huruf = pool[i % pool.size], expFull = expForTipe(tipe), isStreakMode = true))
            }
        }
        val poolS = allHuruf.shuffled()
        repeat(10) { i ->
            val a = poolS[i % poolS.size]
            val b = poolS[(i + 4) % poolS.size].let { if (it == a) poolS[(i + 6) % poolS.size] else it }
            add(QuizPlan(tipe = 5, pasangan = Pair(a, b), expFull = 20, isStreakMode = true))
        }
    }.shuffled()

    val allStages: Map<Int, Map<Int, List<QuizPlan>>> get() = mapOf(
        1 to mapOf(
            1 to stage1Bagian1,
            2 to stage1Bagian2,
            3 to stage1Bagian3,
            4 to stage1Bagian4,
            5 to stage1Bagian5
        ),
        2 to mapOf(
            1 to stage2Bagian1,
            2 to stage2Bagian2,
            3 to stage2Bagian3,
            4 to stage2Bagian4,
            5 to stage2Bagian5
        ),
        3 to mapOf(
            1 to stage3Bagian1,
            2 to stage3Bagian2,
            3 to stage3Bagian3,
            4 to stage3Bagian4,
            5 to stage3Bagian5,
            6 to stage3Bagian6
        ),
        4 to mapOf(
            1 to stage4Bagian1,
            2 to stage4Bagian2,
            3 to stage4Bagian3,
            4 to stage4Bagian4,
            5 to stage4Bagian5,
            6 to stage4Bagian6
        ),
        5 to mapOf(
            1 to stage5Bagian1,
            2 to stage5Bagian2,
            3 to stage5Bagian3,
            4 to stage5Bagian4,
            5 to stage5Bagian5,
            6 to stage5Bagian6
        ),
        6 to mapOf(
            1 to stage6Bagian1,
            2 to stage6Bagian2,
            3 to stage6Bagian3
        )
    )
}