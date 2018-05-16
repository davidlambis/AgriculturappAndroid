package com.interedes.agriculturappv3.modules.models.ventas.resports

import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.QueryModel
import com.raizlabs.android.dbflow.annotation.Column

@QueryModel(database = DataSource::class) // (add database reference)
data class CountOfPost(  @Column(name = "count")
                         var count: Double? = 0.0 ) {

}