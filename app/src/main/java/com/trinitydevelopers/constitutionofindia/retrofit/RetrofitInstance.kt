package com.trinitydevelopers.constitutionofindia.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://mapi.trycatchtech.com/v3/constitution_of_india/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ConstitutionApi by lazy {
        retrofit.create(ConstitutionApi::class.java)
    }
}