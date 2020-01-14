package com.notrace.architecturelib.data.remote

import androidx.lifecycle.LiveData
import com.notrace.architecturelib.data.model.RepoSearchResponse
import com.notrace.network.mvvm.base.ApiResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *create by chenyang on 2019/4/4
 **/
interface GithubService {
    /**
    @GET("users/{login}")
    fun getUser(@Path("login") login: String): LiveData<ApiResponse<User>>

    @GET("users/{login}/repos")
    fun getRepos(@Path("login") login: String): LiveData<ApiResponse<List<Repo>>>

    @GET("repos/{owner}/{name}")
    fun getRepo(
    @Path("owner") owner: String,
    @Path("name") name: String
    ): LiveData<ApiResponse<Repo>>

    @GET("repos/{owner}/{name}/contributors")
    fun getContributors(
    @Path("owner") owner: String,
    @Path("name") name: String
    ): LiveData<ApiResponse<List<Contributor>>>



    @GET("search/repositories")
    fun searchRepos(@Query("q") query: String): LiveData<ApiResponse<RepoSearchResponse>>

    @GET("search/repositories")
    fun searchRepos(@Query("q") query: String, @Query("page") page: Int): Call<RepoSearchResponse>


     **/

    @GET("search/repositories")
    fun searchRepos(@Query("q") query: String): Single<RepoSearchResponse>

    @GET("search/repositories")
    fun searchRepos(@Query("q") query: String, @Query("page") page: Int):Single<RepoSearchResponse>
    @GET("search/repositories")
    fun serchRepos(@Query("q") query: String, @Query("page") page: Int):LiveData<ApiResponse<RepoSearchResponse>>
}