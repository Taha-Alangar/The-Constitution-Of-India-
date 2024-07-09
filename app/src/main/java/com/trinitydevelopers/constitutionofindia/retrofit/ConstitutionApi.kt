package com.trinitydevelopers.constitutionofindia.retrofit

import com.trinitydevelopers.constitutionofindia.model.AmendmentsItem
import com.trinitydevelopers.constitutionofindia.model.Article
import com.trinitydevelopers.constitutionofindia.model.ArticleItem
import com.trinitydevelopers.constitutionofindia.model.CaseStudyItem
import com.trinitydevelopers.constitutionofindia.model.NewsArticles
import com.trinitydevelopers.constitutionofindia.model.NewsArticlesItem
import com.trinitydevelopers.constitutionofindia.model.NotificationItem
import com.trinitydevelopers.constitutionofindia.model.Parts
import com.trinitydevelopers.constitutionofindia.model.PartsItem
import com.trinitydevelopers.constitutionofindia.model.Preamble
import com.trinitydevelopers.constitutionofindia.model.PreambleItem
import com.trinitydevelopers.constitutionofindia.model.ScheduleItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ConstitutionApi {

    @GET("constitution_of_india_news_article")
    fun getNewsArticles(): Call<List<NewsArticlesItem>>

    @GET("constitution_of_india_preamble")
    fun getPreamble(): Call<List<PreambleItem>>

    @GET("constitution_of_india_articles")
    fun getArticles(@Query("part_id") partId: String): Call<List<ArticleItem>>

    @GET("constitution_of_india_parts")
    fun getParts(): Call<List<PartsItem>>

    @GET("constitution_of_india_amendments")
    fun getAmendments(): Call<List<AmendmentsItem>>

    @GET("constitution_of_india_notification")
    fun getNotifications(): Call<List<NotificationItem>>

    @GET("constitution_of_india_case_study")
    fun getCaseStudies(): Call<List<CaseStudyItem>>

    @GET("constitution_of_india_schedule")
    fun getSchedules(): Call<List<ScheduleItem>>
}
