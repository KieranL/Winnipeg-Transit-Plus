package com.kieran.winnipegbus.data

import org.jetbrains.anko.db.SqlType

fun COMPOSITE_PRIMARY_KEY(vararg columnNames: String): Pair<String, SqlType> {
    return "" to SqlType.create("PRIMARY KEY (${columnNames.joinToString(", ")}})")
}