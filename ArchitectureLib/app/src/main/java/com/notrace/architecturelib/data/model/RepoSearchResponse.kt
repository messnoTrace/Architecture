package com.notrace.architecturelib.data.model

import com.google.gson.annotations.SerializedName
import com.notrace.architecturelib.data.model.Repo

/**
 *create by chenyang on 2019/4/4
 **/
/**
 * Simple object to hold repo search responses. This is different from the Entity in the database
 * because we are keeping a search result in 1 row and denormalizing list of results into a single
 * column.
 */
data class RepoSearchResponse(
    @SerializedName("total_count")
    val total: Int = 0,
    @SerializedName("items")
    val items: List<Repo>
) {
    var nextPage: Int? = null
}