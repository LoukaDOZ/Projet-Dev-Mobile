package com.example.projet_dm.data

import com.example.projet_dm.login.LoginInfo
import retrofit2.Response
import retrofit2.http.*

interface LoginWebService {
    @POST("oauth/access_token")
    fun getApiKey(
        @Body loginInfo: LoginInfo,
        @Query("client_id") clientId: String = "6ea37fd9bf6f4c47abd230729e5d5a25",
        @Query("client_secret") clientSecret: String = "c74e5e261be54a77b39877b0a123b6e4",
        @Query("grant_type") grantType: String = "password"
    ): Response<ApiKey>
}